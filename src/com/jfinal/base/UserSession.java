package com.jfinal.base;

public class UserSession {  
    private static final ThreadLocal<Object> SESSION_USER = new ThreadLocal<Object>();  
    
    @SuppressWarnings("unchecked")
	public static <T> T get() {  
        return (T) SESSION_USER.get();  
    }  
      
    public static void set(Object user) {  
        SESSION_USER.set(user);
    }  
} 
