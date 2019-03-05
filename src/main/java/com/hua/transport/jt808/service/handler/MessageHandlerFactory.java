package com.hua.transport.jt808.service.handler;

import java.util.HashMap;
import java.util.Map;
import com.hua.transport.jt808.common.Consts;
import com.hua.transport.jt808.service.handler.terminal.AuthenticationHandler;
import com.hua.transport.jt808.service.handler.terminal.HeartbeatHandler;
import com.hua.transport.jt808.service.handler.terminal.LocationUploadHandler;
import com.hua.transport.jt808.service.handler.terminal.LoginOutHandler;
import com.hua.transport.jt808.service.handler.terminal.RegisterHandler;

public class MessageHandlerFactory {
	
	/**
	 * 消息和处理类映射表
	 */
	public static Map<Integer, Class<?>> handlerMap = new HashMap<Integer, Class<?>>();
	static{
		handlerMap.put(Consts.MSGID_HEART_BEAT, HeartbeatHandler.class);  // 终端心跳
		handlerMap.put(Consts.MSGID_REGISTER, RegisterHandler.class);  // 终端注册
		handlerMap.put(Consts.MSGID_LOG_OUT, LoginOutHandler.class);  // 终端注销
		handlerMap.put(Consts.MSGID_AUTHENTICATION, AuthenticationHandler.class); // 终端鉴权
		handlerMap.put(Consts.MSGID_LOCATION_UPLOAD, LocationUploadHandler.class); // 位置信息汇报
	}
	
	
	public static MessageHandler getInstance(Integer msgId) throws InstantiationException, IllegalAccessException{
		Class clazz = handlerMap.get(msgId);
		if(clazz == null){
			return null;
		}
		
		return (MessageHandler)clazz.newInstance();
	}
	
}
