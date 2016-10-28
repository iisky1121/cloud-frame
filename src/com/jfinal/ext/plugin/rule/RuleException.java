package com.jfinal.ext.plugin.rule;

public class RuleException extends RuntimeException {
	static final long serialVersionUID = -7034897190745766939L;

    public RuleException() {
        super();
    }

    public RuleException(String message) {
    	super(message);
    }
    public RuleException(String message, Object... objs) {
        super(format(message, objs));
    }
    
    private static String format(String msg, Object... objs){
		for(Object obj : objs){
			msg = msg.replaceFirst("[?]", obj.toString());
		}
		return msg;
	}
}
