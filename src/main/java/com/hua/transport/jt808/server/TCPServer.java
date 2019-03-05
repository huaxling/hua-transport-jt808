package com.hua.transport.jt808.server;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;
import com.hua.transport.jt808.common.Consts;
import com.hua.transport.jt808.service.codec.LogDecoder;
import com.hua.transport.jt808.service.handler.TCPServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;

public class TCPServer {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	private int port;
	private EventLoopGroup bossGroup = null;
	private EventLoopGroup workerGroup = null;
	private volatile boolean isRunning = false;

	public TCPServer() {
	}

	public TCPServer(int port) {
		this();
		this.port = port;
	}

	private void bind() throws Exception {
		this.bossGroup = new NioEventLoopGroup();
		this.workerGroup = new NioEventLoopGroup();
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(bossGroup, workerGroup)//
				.channel(NioServerSocketChannel.class) //
				.childHandler(new ChannelInitializer<SocketChannel>() { //
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						
						ch.pipeline().addLast("idleStateHandler",
								new IdleStateHandler(Consts.TCP_CLIENT_IDLE, 0, 0, TimeUnit.MINUTES));
						
						ch.pipeline().addLast(new LogDecoder());
						
						// 1024表示单条消息的最大长度，解码器在查找分隔符的时候，达到该长度还没找到的话会抛异常
						ch.pipeline().addLast(
								new DelimiterBasedFrameDecoder(1024, Unpooled.copiedBuffer(new byte[] { 0x7e }),
										Unpooled.copiedBuffer(new byte[] { 0x7e, 0x7e })));
						//ch.pipeline().addLast(new PackageDataDecoder());
						
						ch.pipeline().addLast(new TCPServerHandler());
					}
				}).option(ChannelOption.SO_BACKLOG, 128) //
				.childOption(ChannelOption.SO_KEEPALIVE, true);

		log.info("TCP服务启动完毕,port={}", this.port);
		ChannelFuture channelFuture = serverBootstrap.bind(port).sync();

		channelFuture.channel().closeFuture().sync();
	}

	public synchronized void startServer() {
		if (this.isRunning) {
			throw new IllegalStateException(this.getName() + " is already started .");
		}
		this.isRunning = true;

		new Thread(() -> {
			try {
				this.bind();
			} catch (Exception e) {
				this.log.info("TCP服务启动出错:{}", e.getMessage());
				e.printStackTrace();
			}
		}, this.getName()).start();
	}

	public synchronized void stopServer() {
		if (!this.isRunning) {
			throw new IllegalStateException(this.getName() + " is not yet started .");
		}
		this.isRunning = false;

		try {
			Future<?> future = this.workerGroup.shutdownGracefully().await();
			if (!future.isSuccess()) {
				log.error("workerGroup 无法正常停止:{}", future.cause());
			}

			future = this.bossGroup.shutdownGracefully().await();
			if (!future.isSuccess()) {
				log.error("bossGroup 无法正常停止:{}", future.cause());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.log.info("TCP服务已经停止...");
	}

	private String getName() {
		return "TCP-Server";
	}
}