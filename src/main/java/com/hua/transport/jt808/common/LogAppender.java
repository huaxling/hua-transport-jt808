package com.hua.transport.jt808.common;

import org.apache.log4j.Priority;
import org.apache.log4j.DailyRollingFileAppender;

public class LogAppender extends DailyRollingFileAppender {

	@Override
	public boolean isAsSevereAsThreshold(Priority priority) {
		return this.getThreshold().equals(priority);
	}
	
}
