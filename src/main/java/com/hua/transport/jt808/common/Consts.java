package com.hua.transport.jt808.common;

import java.nio.charset.Charset;

public class Consts {

	public static final String DEFAULT_ENCODE = "GBK";
	public static final Charset DEFAULT_CHARSET = Charset.forName(DEFAULT_ENCODE);
	
	
	/** 标识位  **/
	public static final int PKG_DELIMITER = 0x7e;
	/** 客户端发呆15分钟后,服务器主动断开连接  **/
	public static int TCP_CLIENT_IDLE = 15;

	
	/** 终端通用应答  **/
	public static final Integer MSGID_COMMON_RESP = 0x0001;
	/** 终端心跳  **/
	public static final Integer MSGID_HEART_BEAT = 0x0002;
	/** 终端注册  **/
	public static final Integer MSGID_REGISTER = 0x0100;
	/** 终端注销  **/
	public static final Integer MSGID_LOG_OUT = 0x0003;
	/** 终端鉴权  **/
	public static final Integer MSGID_AUTHENTICATION = 0x0102;
	/** 位置信息汇报  **/
	public static final Integer MSGID_LOCATION_UPLOAD = 0x0200;
	/** 胎压数据透传  **/
	public static final Integer MSGID_TRANSMISSION_TYPE_PRESSURE = 0x0600;
	/** 查询终端参数应答  **/
	public static final Integer MSGID_PARAM_QUERY_RESP = 0x0104;

	
	/** 平台通用应答  **/
	public static final int CMD_COMMON_RESP = 0x8001;
	/** 终端注册应答  **/
	public static final int CMD_REGISTER_RESP = 0x8100;
	
	
	/** 设置终端参数  **/
	public static final int CMD_PARAM_SETTINGS = 0X8103;
	/** 查询终端参数  **/
	public static final int CMD_PARAM_QUERY = 0x8104;

}
