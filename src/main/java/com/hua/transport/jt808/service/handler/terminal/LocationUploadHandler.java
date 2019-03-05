package com.hua.transport.jt808.service.handler.terminal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.hua.transport.jt808.entity.DataPack;
import com.hua.transport.jt808.entity.DataPack.PackHead;
import com.hua.transport.jt808.entity.request.LocationPack;
import com.hua.transport.jt808.entity.response.ServerBodyPack;
import com.hua.transport.jt808.service.handler.MessageHandler;

/**
 * 处理模板
 * 
 * @author huaxl
 */
public class LocationUploadHandler extends MessageHandler {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public LocationUploadHandler() {
		super();
	}

	@Override
	public void process(DataPack packageData) {
		//
		PackHead header = packageData.getPackHead();
		logger.info("[位置信息],msgid={}, phone={},flowid={}", header.getId(), header.getTerminalPhone(), header.getFlowId());
		try {

			LocationPack msg = this.decoder.toLocationInfoUploadMsg(packageData);
			log.debug("位置 信息:{}", JSON.toJSONString(msg, true));
			
			ServerBodyPack respMsgBody = new ServerBodyPack(header.getFlowId(), header.getId(), ServerBodyPack.success);
			int flowId = super.getFlowId(msg.getChannel());
			byte[] bs = this.msgEncoder.encode4ServerCommonRespMsg(msg, respMsgBody, flowId);
			super.send2Client(msg.getChannel(), bs);

		} catch (Exception e) {
			logger.error("[位置信息]错误,err={}", e.getMessage());
		}

	}

}
