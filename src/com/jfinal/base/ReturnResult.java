package com.jfinal.base;

import com.jfinal.interfaces.ISuccCallback;
import com.jfinal.kit.StrKit;

import java.util.HashMap;
import java.util.Map;

public class ReturnResult {
	private String code;//系统默认标识code
	private String msg;//错误提示语
	private String error_code;//自定义错误code
	private Object result;//返回结果对象
	private String cause;//错误原因

	public String getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public ReturnResult setMsg(String msg) {
		this.msg = msg;
		return this;
	}

	public String getError_code() {
		return error_code;
	}

	public ReturnResult setError_code(String error_code) {
		this.error_code = error_code;
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> T getResult() {
		return (T) result;
	}

	public ReturnResult setResult(Object result) {
		this.result = result;
		return this;
	}

	public String getCause() {
		return cause;
	}

	public ReturnResult setCause(String cause) {
		this.cause = cause;
		return this;
	}

	ReturnResult(String code, String msg, String error_code, Object result, String cause) {
		this.code = code;
		this.msg = msg;
		this.error_code = error_code;
		this.result = result;
		this.cause = cause;
	}

	/**
	 * 自定义
	 */
	public static ReturnResult create(Boolean isTrue) {
		return create(isTrue, BaseConfig.failure_msg);
	}
	public static ReturnResult create(Boolean isTrue, String errorStr) {
		return create(isTrue, errorStr, BaseConfig.success_msg);
	}
	public static ReturnResult create(Boolean isTrue, String errorStr, String succStr) {
		if(isTrue != null && isTrue){
			return success(succStr);
		}
		return failure(errorStr);
	}


	/**
	 * 智能判断各种返回结果,默认为失败
	 *
	 */
	public static ReturnResult toResult(Object object) {
		if(object == null){
			return ReturnResult.create(false);
		}
		else if(object instanceof Boolean){
			return ReturnResult.create((Boolean)object);
		}
		else if(object instanceof ReturnResult){
			return ((ReturnResult)object);
		}
		else if(object instanceof String){
			return ReturnResult.failure((String)object);
		}
		else{
			return ReturnResult.success(object);
		}
	}
	
	/**
	 * 成功
	 */
	public static ReturnResult success() {
		return success(BaseConfig.success_msg);
	}
	public static ReturnResult success(String msg) {
		return success(msg, null);
	}
	public static ReturnResult success(Object result) {
		return success(BaseConfig.success_msg, result);
	}
	public static ReturnResult success(String msg, Object result) {
		return new ReturnResult(BaseConfig.success_code, msg, null, result, null);
	}
	
	/**
	 * 失败
	 */
	public static ReturnResult failure() {
		return failure(BaseConfig.failure_msg);
	}
	public static ReturnResult failure(String msg) {
		return failure(msg, null);
	}
	public static ReturnResult failure(String msg, Object result) {
		return failure(msg, null, result);
	}
    public static ReturnResult failure(String msg, String error_code, Object result) {
    	return new ReturnResult(BaseConfig.failure_code, msg, error_code, result, null);
    }
    
    /**
	 * 是否成功
	 */
    public boolean isSucceed() {
        return getCode() != null && getCode().equals(BaseConfig.success_code);
    }
    
    public Map<String,Object> render(){
    	Map<String,Object> map = new HashMap<String,Object>();
		if(!StrKit.isBlank(code)){
			map.put("code", code);
		}
		if(!StrKit.isBlank(msg)){
			map.put("msg", msg);
		}
		if(!StrKit.isBlank(error_code)){
			map.put("error_code", error_code);
		}
		if(result != null){
			map.put("result", result);
		}
		if(!StrKit.isBlank(cause)){
			map.put("cause", cause);
		}
		return map;
    }
    
    /**
     * 成功回调，用户多个ReturnResult直接调用
     * @param call
     * @return
     */
    public ReturnResult call(ISuccCallback<ReturnResult> call){
    	if(call == null || !this.isSucceed()){
    		return this;
    	}

		ReturnResult returnResult = call.callback(this);
		if(!returnResult.isSucceed()){
			return returnResult;
		}
		return this;
    }
}
