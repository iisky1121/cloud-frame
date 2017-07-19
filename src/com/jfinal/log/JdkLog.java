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

package com.jfinal.log;

import java.util.logging.Level;

/**
 * JdkLog.
 */
public class JdkLog extends Log {

	private java.util.logging.Logger log;
	private String clazzName;
	
	JdkLog(Class<?> clazz) {
		log = java.util.logging.Logger.getLogger(clazz.getName());
		clazzName = clazz.getName();
	}
	
	JdkLog(String name) {
		log = java.util.logging.Logger.getLogger(name);
		clazzName = name;
	}
	
	public static JdkLog getLog(Class<?> clazz) {
		return new JdkLog(clazz);
	}
	
	public static JdkLog getLog(String name) {
		return new JdkLog(name);
	}
	
	public void debug(Object message) {
		log.logp(Level.FINE, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message.toString());
	}
	
	public void debug(Object message,  Throwable t) {
		log.logp(Level.FINE, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message.toString(), t);
	}
	
	public void info(Object message) {
		log.logp(Level.INFO, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message.toString());
	}
	
	public void info(Object message, Throwable t) {
		log.logp(Level.INFO, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message.toString(), t);
	}
	
	public void warn(Object message) {
		log.logp(Level.WARNING, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message.toString());
	}
	
	public void warn(Object message, Throwable t) {
		log.logp(Level.WARNING, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message.toString(), t);
	}
	
	public void error(Object message) {
		log.logp(Level.SEVERE, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message.toString());
	}
	
	public void error(Object message, Throwable t) {
		log.logp(Level.SEVERE, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message.toString(), t);
	}
	
	/**
	 * JdkLog fatal is the same as the error.
	 */
	public void fatal(Object message) {
		log.logp(Level.SEVERE, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message.toString());
	}
	
	/**
	 * JdkLog fatal is the same as the error.
	 */
	public void fatal(Object message, Throwable t) {
		log.logp(Level.SEVERE, clazzName, Thread.currentThread().getStackTrace()[1].getMethodName(), message.toString(), t);
	}
	
	public boolean isDebugEnabled() {
		return log.isLoggable(Level.FINE);
	}
	
	public boolean isInfoEnabled() {
		return log.isLoggable(Level.INFO);
	}
	
	public boolean isWarnEnabled() {
		return log.isLoggable(Level.WARNING);
	}
	
	public boolean isErrorEnabled() {
		return log.isLoggable(Level.SEVERE);
	}
	
	public boolean isFatalEnabled() {
		return log.isLoggable(Level.SEVERE);
	}
}



