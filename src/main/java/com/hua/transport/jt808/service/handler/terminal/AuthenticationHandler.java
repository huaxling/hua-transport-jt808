package com.hua.transport.jt808.service.handler.terminal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.hua.transport.jt808.entity.DataPack;
import com.hua.transport.jt808.entity.Session;
import com.hua.transport.jt808.entity.DataPack.PackHead;
import com.hua.transport.jt808.entity.request.AuthenticationPack;
import com.hua.transport.jt808.entity.response.ServerBodyPack;
import com.hua.transport.jt808.service.handler.MessageHandler;

/**
 * 终端鉴权 ==> 平台通用应答
 * @author huaxl
 */
public class AuthenticationHandler extends MessageHandler {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	
	public AuthenticationHandler() {
		super();
	}

	@Override
	public void process(DataPack packageData) {
		// 
		
		PackHead header = packageData.getPackHead();
		logger.info("[终端鉴权],msgid={}, phone={},flowid={}", header.getId(), header.getTerminalPhone(), header.getFlowId());
		try {
			
			AuthenticationPack msg = new AuthenticationPack(packageData);
			//this.msgProcessService.processAuthMsg(authenticationMsg);
			log.debug("终端鉴权:{}", JSON.toJSONString(msg, true));

	        final String sessionId = Session.buildId(msg.getChannel());
	        Session session = sessionManager.findBySessionId(sessionId);
	        if (session == null) {
	            session = Session.buildSession(msg.getChannel(), msg.getPackHead().getTerminalPhone());
	        }
	        session.setAuthenticated(true);
	        session.setTerminalPhone(msg.getPackHead().getTerminalPhone());
	        sessionManager.put(session.getId(), session);

	        ServerBodyPack respMsgBody = new ServerBodyPack();
	        respMsgBody.setReplyCode(ServerBodyPack.success);
	        respMsgBody.setReplyFlowId(msg.getPackHead().getFlowId());
	        respMsgBody.setReplyId(msg.getPackHead().getId());
	        int flowId = super.getFlowId(msg.getChannel());
	        byte[] bs = this.msgEncoder.encode4ServerCommonRespMsg(msg, respMsgBody, flowId);
	        super.send2Client(msg.getChannel(), bs);
			
		} catch (Exception e) {
			logger.error("[终端鉴权]错误,err={}",  e.getMessage());
		}

	}

}
