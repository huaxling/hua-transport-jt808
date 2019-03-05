package com.hua.transport.jt808.entity;

import java.util.Arrays;

import com.alibaba.fastjson.annotation.JSONField;

import io.netty.channel.Channel;

/**
 * 通用数据包
 * @author huaxl
 *
 */
public class DataPack {

	/**
	 * 16byte 消息头
	 */
	protected PackHead packHead;

	// 消息体字节数组
	@JSONField(serialize=false)
	protected byte[] bodyBytes;

	/**
	 * 校验码 1byte
	 */
	protected int checkSum;

	@JSONField(serialize=false)
	protected Channel channel;

	public PackHead getPackHead() {
		return packHead;
	}

	public void setPackHead(PackHead packHead) {
		this.packHead = packHead;
	}

	public byte[] getBodyBytes() {
		return bodyBytes;
	}

	public void setBodyBytes(byte[] bodyBytes) {
		this.bodyBytes = bodyBytes;
	}

	public int getCheckSum() {
		return checkSum;
	}

	public void setCheckSum(int checkSum) {
		this.checkSum = checkSum;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	@Override
	public String toString() {
		return "PackageData [packHead=" + packHead + ", bodyBytes=" + Arrays.toString(bodyBytes) + ", checkSum=" + checkSum + ", address=" + channel + "]";
	}

	
	public static class PackHead {
		// 消息ID
		protected int id;

		
		/////// ========消息体属性
		// byte[2-3]
		protected int bodyPropsField;
		// 消息体长度
		protected int bodyLength;
		// 数据加密方式
		protected int encryptionType;
		// 是否分包,true==>有消息包封装项
		protected boolean hasSubPackage;
		// 保留位[14-15]
		protected String reservedBit;
		/////// ========消息体属性

		
		// 终端手机号
		protected String terminalPhone;
		
		
		// 流水号
		protected int flowId;
		

		//////// =====消息包封装项
		// byte[12-15]
		protected int infoField;
		// 消息包总数(word(16))
		protected long subPackage;
		// 包序号(word(16))这次发送的这个消息包是分包中的第几个消息包, 从 1 开始
		protected long subPackageSequeue;
		//////// =====消息包封装项

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getMsgBodyLength() {
			return bodyLength;
		}

		public void setMsgBodyLength(int msgBodyLength) {
			this.bodyLength = msgBodyLength;
		}

		public int getEncryptionType() {
			return encryptionType;
		}

		public void setEncryptionType(int encryptionType) {
			this.encryptionType = encryptionType;
		}

		public String getTerminalPhone() {
			return terminalPhone;
		}

		public void setTerminalPhone(String terminalPhone) {
			this.terminalPhone = terminalPhone;
		}

		public int getFlowId() {
			return flowId;
		}

		public void setFlowId(int flowId) {
			this.flowId = flowId;
		}

		public boolean isHasSubPackage() {
			return hasSubPackage;
		}

		public void setHasSubPackage(boolean hasSubPackage) {
			this.hasSubPackage = hasSubPackage;
		}

		public String getReservedBit() {
			return reservedBit;
		}

		public void setReservedBit(String reservedBit) {
			this.reservedBit = reservedBit;
		}

		public long getSubPackage() {
			return subPackage;
		}

		public void setSubPackage(long totalPackage) {
			this.subPackage = totalPackage;
		}

		public long getSubPackageSequeue() {
			return subPackageSequeue;
		}

		public void setSubPackageSequeue(long packageSequeue) {
			this.subPackageSequeue = packageSequeue;
		}

		public int getBodyPropsField() {
			return bodyPropsField;
		}

		public void setBodyPropsField(int bodyPropsField) {
			this.bodyPropsField = bodyPropsField;
		}

		public void setInfoField(int infoField) {
			this.infoField = infoField;
		}

		public int getInfoField() {
			return infoField;
		}

		@Override
		public String toString() {
			return "PackHead [id=" + id + ", bodyPropsField=" + bodyPropsField + ", bodyLength=" + bodyLength
					+ ", encryptionType=" + encryptionType + ", hasSubPackage=" + hasSubPackage + ", reservedBit="
					+ reservedBit + ", terminalPhone=" + terminalPhone + ", flowId=" + flowId + ", infoField="
					+ infoField + ", subPackage=" + subPackage + ", subPackageSequeue=" + subPackageSequeue + "]";
		}

	}

}
