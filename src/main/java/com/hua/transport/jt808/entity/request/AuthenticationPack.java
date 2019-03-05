package com.hua.transport.jt808.entity.request;

import java.util.Arrays;

import com.hua.transport.jt808.common.Consts;
import com.hua.transport.jt808.entity.DataPack;

/**
 * 终端鉴权消息
 * 
 * @author huaxl
 *
 */
public class AuthenticationPack extends DataPack {
	
	private String authCode;

	public AuthenticationPack() {
	}

	public AuthenticationPack(DataPack packageData) {
		this();
		this.channel = packageData.getChannel();
		this.checkSum = packageData.getCheckSum();
		this.bodyBytes = packageData.getBodyBytes();
		this.packHead = packageData.getPackHead();
		this.authCode = new String(packageData.getBodyBytes(), Consts.DEFAULT_CHARSET);
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getAuthCode() {
		return authCode;
	}

	@Override
	public String toString() {
		return "TerminalAuthenticationMsg [authCode=" + authCode + ", msgHeader=" + packHead + ", msgBodyBytes="
				+ Arrays.toString(bodyBytes) + ", checkSum=" + checkSum + ", channel=" + channel + "]";
	}

}
