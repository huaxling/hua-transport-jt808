package com.hua.transport.jt808.service.codec;

import java.util.Arrays;

import com.hua.transport.jt808.common.Consts;
import com.hua.transport.jt808.entity.DataPack;
import com.hua.transport.jt808.entity.Session;
import com.hua.transport.jt808.entity.request.RegisterPack;
import com.hua.transport.jt808.entity.response.ServerBodyPack;
import com.hua.transport.jt808.entity.response.RegisterBodyPack;
import com.hua.transport.jt808.util.BitUtil;
import com.hua.transport.jt808.util.JT808Util;

/**
 * 数据包编码器
 * @author huaxl
 *
 */
public class DataEncoder {
	private BitUtil bitUtil;
	private JT808Util jt808Util;

	public DataEncoder() {
		this.bitUtil = new BitUtil();
		this.jt808Util = new JT808Util();
	}

	public byte[] encode4TerminalRegisterResp(RegisterPack req, RegisterBodyPack respMsgBody,
			int flowId) throws Exception {
		// 消息体字节数组
		byte[] msgBody = null;
		// 鉴权码(STRING) 只有在成功后才有该字段
		if (respMsgBody.getReplyCode() == RegisterBodyPack.success) {
			msgBody = this.bitUtil.concatAll(Arrays.asList(//
					bitUtil.integerTo2Bytes(respMsgBody.getReplyFlowId()), // 流水号(2)
					new byte[] { respMsgBody.getReplyCode() }, // 结果
					respMsgBody.getReplyToken().getBytes(Consts.DEFAULT_CHARSET)// 鉴权码(STRING)
			));
		} else {
			msgBody = this.bitUtil.concatAll(Arrays.asList(//
					bitUtil.integerTo2Bytes(respMsgBody.getReplyFlowId()), // 流水号(2)
					new byte[] { respMsgBody.getReplyCode() }// 错误代码
			));
		}

		// 消息头
		int msgBodyProps = this.jt808Util.generateMsgBodyProps(msgBody.length, 0b000, false, 0);
		byte[] msgHeader = this.jt808Util.generateMsgHeader(req.getPackHead().getTerminalPhone(),
				Consts.CMD_REGISTER_RESP, msgBody, msgBodyProps, flowId);
		byte[] headerAndBody = this.bitUtil.concatAll(msgHeader, msgBody);

		// 校验码
		int checkSum = this.bitUtil.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
		// 连接并且转义
		return this.doEncode(headerAndBody, checkSum);
	}

	// public byte[] encode4ServerCommonRespMsg(TerminalAuthenticationMsg req,
	// ServerCommonRespMsgBody respMsgBody, int flowId) throws Exception {
	public byte[] encode4ServerCommonRespMsg(DataPack req, ServerBodyPack respMsgBody, int flowId)
			throws Exception {
		byte[] msgBody = this.bitUtil.concatAll(Arrays.asList(//
				bitUtil.integerTo2Bytes(respMsgBody.getReplyFlowId()), // 应答流水号
				bitUtil.integerTo2Bytes(respMsgBody.getReplyId()), // 应答ID,对应的终端消息的ID
				new byte[] { respMsgBody.getReplyCode() }// 结果
		));

		// 消息头
		int msgBodyProps = this.jt808Util.generateMsgBodyProps(msgBody.length, 0b000, false, 0);
		byte[] msgHeader = this.jt808Util.generateMsgHeader(req.getPackHead().getTerminalPhone(),
				Consts.CMD_COMMON_RESP, msgBody, msgBodyProps, flowId);
		byte[] headerAndBody = this.bitUtil.concatAll(msgHeader, msgBody);
		// 校验码
		int checkSum = this.bitUtil.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
		// 连接并且转义
		return this.doEncode(headerAndBody, checkSum);
	}

	public byte[] encode4ParamSetting(byte[] msgBodyBytes, Session session) throws Exception {
		// 消息头
		int msgBodyProps = this.jt808Util.generateMsgBodyProps(msgBodyBytes.length, 0b000, false, 0);
		byte[] msgHeader = this.jt808Util.generateMsgHeader(session.getTerminalPhone(),
				Consts.CMD_PARAM_SETTINGS, msgBodyBytes, msgBodyProps, session.currentFlowId());
		// 连接消息头和消息体
		byte[] headerAndBody = this.bitUtil.concatAll(msgHeader, msgBodyBytes);
		// 校验码
		int checkSum = this.bitUtil.getCheckSum4JT808(headerAndBody, 0, headerAndBody.length - 1);
		// 连接并且转义
		return this.doEncode(headerAndBody, checkSum);
	}

	private byte[] doEncode(byte[] headerAndBody, int checkSum) throws Exception {
		byte[] noEscapedBytes = this.bitUtil.concatAll(Arrays.asList(//
				new byte[] { Consts.PKG_DELIMITER }, // 0x7e
				headerAndBody, // 消息头+ 消息体
				bitUtil.integerTo1Bytes(checkSum), // 校验码
				new byte[] { Consts.PKG_DELIMITER }// 0x7e
		));
		// 转义
		return jt808Util.doEscape4Send(noEscapedBytes, 1, noEscapedBytes.length - 2);
	}
}
