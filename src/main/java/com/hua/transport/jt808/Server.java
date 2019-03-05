package com.hua.transport.jt808;

import com.hua.transport.jt808.server.TCPServer;

public class Server {

	public static void main(String[] args) {
		TCPServer server = new TCPServer(20048);
		server.startServer();

		// Thread.sleep(3000);  检验码不一致
		// server.stopServer();
	}
}
