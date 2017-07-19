/**
 * Copyright (c) 2011-2017, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jfinal.kit;

import com.jfinal.log.Log;

/**
 * LogKit.
 */
public class LogKit {
	
	private static class Holder {
		private static Log log = Log.getLog(LogKit.class);
	}
	
	/**
	 * 当通过 Constants.setLogFactory(...) 或者 
	 * LogManager.me().setDefaultLogFacotyr(...)
	 * 指定默认日志工厂以后，重置一下内部 Log 对象，以便使内部日志实现与系统保持一致
	 */
	public static void synchronizeLog() {
		Holder.log = Log.getLog(LogKit.class);
	}
	
	/**
	 * Do nothing.
	 */
	public static void logNothing(Throwable t) {
		
	}
	
	public static void debug(Object message) {
		Holder.log.debug(message);
	}
	
	public static void debug(Object message, Throwable t) {
		Holder.log.debug(message, t);
	}
	
	public static void info(Object message) {
		Holder.log.info(message);
	}
	
	public static void info(Object message, Throwable t) {
		Holder.log.info(message, t);
	}
	
	public static void warn(Object message) {
		Holder.log.warn(message);
	}
	
	public static void warn(Object message, Throwable t) {
		Holder.log.warn(message, t);
	}
	
	public static void error(Object message) {
		Holder.log.error(message);
	}

	
	public static void error(Object message, Throwable t) {
		Holder.log.error(message, t);
	}
	
	public static void fatal(Object message) {
		Holder.log.fatal(message);
	}
	
	public static void fatal(Object message, Throwable t) {
		Holder.log.fatal(message, t);
	}
	
	public static boolean isDebugEnabled() {
		return Holder.log.isDebugEnabled();
	}
	
	public static boolean isInfoEnabled() {
		return Holder.log.isInfoEnabled();
	}
	
	public static boolean isWarnEnabled() {
		return Holder.log.isWarnEnabled();
	}
	
	public static boolean isErrorEnabled() {
		return Holder.log.isErrorEnabled();
	}
	
	public static boolean isFatalEnabled() {
		return Holder.log.isFatalEnabled();
	}
}

