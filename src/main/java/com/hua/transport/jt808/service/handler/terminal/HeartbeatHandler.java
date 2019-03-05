package com.hua.transport.jt808.service.handler.terminal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.hua.transport.jt808.entity.DataPack;
import com.hua.transport.jt808.entity.DataPack.PackHead;
import com.hua.transport.jt808.entity.response.ServerBodyPack;
import com.hua.transport.jt808.service.handler.MessageHandler;

/**
 * 终端心跳-消息体为空 ==> 平台通用应答
 * @author huaxl
 */
public class HeartbeatHandler extends MessageHandler {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public HeartbeatHandler() {
		super();
	}

	@Override
	public void process(DataPack packageData) {
		PackHead header = packageData.getPackHead();
		logger.info("[终端心跳],msgid={}, phone={},flowid={}", header.getId(), header.getTerminalPhone(), header.getFlowId());
		try {
			logger.debug("心跳信息:{}", JSON.toJSONString(packageData, true));
			
	        ServerBodyPack respMsgBody = new ServerBodyPack(header.getFlowId(), header.getId(), ServerBodyPack.success);
	        int flowId = super.getFlowId(packageData.getChannel());
	        byte[] bs = this.msgEncoder.encode4ServerCommonRespMsg(packageData, respMsgBody, flowId);
	        super.send2Client(packageData.getChannel(), bs);
	        
	        
		} catch (Exception e) {
			logger.error("[终端心跳]错误,err={}", e.getMessage());
		}

	}

}
