package com.hua.transport.jt808.service.handler.terminal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.hua.transport.jt808.entity.DataPack;
import com.hua.transport.jt808.entity.Session;
import com.hua.transport.jt808.entity.DataPack.PackHead;
import com.hua.transport.jt808.entity.request.RegisterPack;
import com.hua.transport.jt808.entity.response.RegisterBodyPack;
import com.hua.transport.jt808.service.handler.MessageHandler;

/**
 * 终端注册 ==> 终端注册应答
 * @author huaxl
 */
public class RegisterHandler extends MessageHandler {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public RegisterHandler() {
		super();
	}

	@Override
	public void process(DataPack packageData) {
		
		PackHead header = packageData.getPackHead();
		logger.info("[终端注册],msgid={}, phone={},flowid={}", header.getId(), header.getTerminalPhone(), header.getFlowId());
		try {
			
			RegisterPack msg = this.decoder.toTerminalRegisterMsg(packageData);
			log.debug("终端注册:{}", JSON.toJSONString(msg, true));
			
	        final String sessionId = Session.buildId(msg.getChannel());
	        Session session = sessionManager.findBySessionId(sessionId);
	        if (session == null) {
	            session = Session.buildSession(msg.getChannel(), msg.getPackHead().getTerminalPhone());
	        }
	        session.setAuthenticated(true);
	        session.setTerminalPhone(msg.getPackHead().getTerminalPhone());
	        sessionManager.put(session.getId(), session);

	        RegisterBodyPack respMsgBody = new RegisterBodyPack();
	        respMsgBody.setReplyCode(RegisterBodyPack.success);
	        respMsgBody.setReplyFlowId(msg.getPackHead().getFlowId());
	        
	        // TODO 鉴权码暂时写死
	        respMsgBody.setReplyToken("123");
	        int flowId = super.getFlowId(msg.getChannel());
	        byte[] bs = this.msgEncoder.encode4TerminalRegisterResp(msg, respMsgBody, flowId);

	        super.send2Client(msg.getChannel(), bs);
		} catch (Exception e) {
			logger.error("<<<<<err={}", e.getMessage());
		}

	}

}
