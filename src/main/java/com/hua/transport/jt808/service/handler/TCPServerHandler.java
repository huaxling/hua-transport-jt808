package com.hua.transport.jt808.service.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hua.transport.jt808.entity.DataPack;
import com.hua.transport.jt808.entity.Session;
import com.hua.transport.jt808.entity.DataPack.PackHead;
import com.hua.transport.jt808.server.SessionManager;
import com.hua.transport.jt808.service.codec.DataDecoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

public class TCPServerHandler extends ChannelInboundHandlerAdapter { // (1)

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final DataDecoder decoder;
	private final SessionManager sessionManager;
	

	public TCPServerHandler() {
		this.decoder = new DataDecoder();
		this.sessionManager = SessionManager.getInstance();
	}
	
	/**
	 * 
	 * 处理业务逻辑
	 * 
	 * @param packageData
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * 
	 */
	private void processPackageData(DataPack packageData) throws InstantiationException, IllegalAccessException {
		
		PackHead header = packageData.getPackHead();
		Integer msgId = header.getId();
		
		logger.info("消息头部：msgid={}, phone={}, flowid={}", msgId, header.getTerminalPhone(), header.getFlowId());
		
		MessageHandler handler = MessageHandlerFactory.getInstance(msgId);
		if(handler != null){
			handler.process(packageData);
		}else {	// 其他情况
			logger.error("[未知消息类型],msgId={},phone={},package={}", header.getId(), header.getTerminalPhone(), packageData);
		}
	}

	

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws InterruptedException { // (2)
		try {
			ByteBuf buf = (ByteBuf) msg;
			if (buf.readableBytes() <= 0) {
				// ReferenceCountUtil.safeRelease(msg);
				return;
			}

			byte[] bs = new byte[buf.readableBytes()];
			buf.readBytes(bs);

			// 字节数据转换为针对于808消息结构的实体类
			DataPack pkg = this.decoder.bytes2PackageData(bs);
			// 引用channel,以便回送数据给硬件
			pkg.setChannel(ctx.channel());
			processPackageData(pkg);
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("消息处理异常", e);
		} finally {
			release(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
		logger.error("发生异常:{}", cause);
		//cause.printStackTrace();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Session session = Session.buildSession(ctx.channel());
		sessionManager.put(session.getId(), session);
		logger.debug("终端连接:{}", session);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		final String sessionId = ctx.channel().id().asLongText();
		Session session = sessionManager.findBySessionId(sessionId);
		this.sessionManager.removeBySessionId(sessionId);
		logger.debug("终端断开连接:{}", session);
		ctx.channel().close();
		// ctx.close();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.READER_IDLE) {
				Session session = this.sessionManager.removeBySessionId(Session.buildId(ctx.channel()));
				logger.error("服务器主动断开连接:{}", session);
				ctx.close();
			}
		}
	}

	private void release(Object msg) {
		try {
			ReferenceCountUtil.release(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}