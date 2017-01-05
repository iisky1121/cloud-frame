package com.jfinal.base;

public class ReturnResult {
	private String code;//系统默认标识code
	private String msg;//错误提示语
	private String error_code;//自定义错误code
	private Object result;//返回结果对象
	private Exception exception;//异常信息

	public String getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getError_code() {
		return error_code;
	}

	public void setError_code(String error_code) {
		this.error_code = error_code;
	}

	@SuppressWarnings("unchecked")
	public <T> T getResult() {
		return (T) result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public Exception getException() {
		return exception;
	}
	
	ReturnResult(String code, String msg, String error_code, Object result, Exception exception) {
		this.code = code;
		this.msg = msg;
		this.error_code = error_code;
		this.result = result;
		this.exception = exception;
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
	 * 成功
	 */
	public static ReturnResult success() {
		return new ReturnResult(BaseConfig.success_code, BaseConfig.success_msg, null, null, null);
	}
	public static ReturnResult success(String msg) {
		return new ReturnResult(BaseConfig.success_code, msg, null, null, null);
	}
	public static ReturnResult success(Object result) {
		return new ReturnResult(BaseConfig.success_code, BaseConfig.success_msg, null, result, null);
	}
	public static ReturnResult success(String msg, Object result) {
		return new ReturnResult(BaseConfig.success_code, msg, null, result, null);
	}
	
	/**
	 * 失败
	 */
	public static ReturnResult failure() {
		return new ReturnResult(BaseConfig.failure_code, BaseConfig.failure_msg, null, null, null);
	}
	public static ReturnResult failure(String msg) {
		return new ReturnResult(BaseConfig.failure_code, msg, null, null, null);
	}
	public static ReturnResult failure(Exception exception) {
		return new ReturnResult(BaseConfig.failure_code, BaseConfig.failure_msg, null, null, exception);
	}
	public static ReturnResult failure(String msg, Exception exception) {
		return new ReturnResult(BaseConfig.failure_code, msg, null, null, exception);
	}
	public static ReturnResult failure(String msg, String error_code) {
		return new ReturnResult(BaseConfig.failure_code, msg, error_code, null, null);
	}
	public static ReturnResult failure(String msg, Object result) {
		return new ReturnResult(BaseConfig.failure_code, msg, null, result, null);
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
}
