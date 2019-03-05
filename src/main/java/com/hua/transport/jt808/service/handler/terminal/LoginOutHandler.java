package com.hua.transport.jt808.service.handler.terminal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.hua.transport.jt808.entity.DataPack;
import com.hua.transport.jt808.entity.DataPack.PackHead;
import com.hua.transport.jt808.entity.response.ServerBodyPack;
import com.hua.transport.jt808.service.handler.MessageHandler;

/**
 * 终端注销(终端注销数据消息体为空) ==> 平台通用应答
 * @author huaxl
 */
public class LoginOutHandler extends MessageHandler {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public LoginOutHandler() {
		super();
	}

	@Override
	public void process(DataPack packageData) {
		
		PackHead header = packageData.getPackHead();
		logger.info("[终端注销],msgid={}, phone={},flowid={}", header.getId(), header.getTerminalPhone(), header.getFlowId());
		try {
			log.info("终端注销:{}", JSON.toJSONString(packageData, true));
	        final PackHead reqHeader = packageData.getPackHead();
	        
	        int flowId = super.getFlowId(packageData.getChannel());
	        ServerBodyPack respMsgBody = new ServerBodyPack(reqHeader.getFlowId(), reqHeader.getId(), ServerBodyPack.success);
	        byte[] bs = this.msgEncoder.encode4ServerCommonRespMsg(packageData, respMsgBody, flowId);
	        
	        super.send2Client(packageData.getChannel(), bs);
		} catch (Exception e) {
			logger.error("[终端注销]错误, err={}", e.getMessage());
		}

	}

}
