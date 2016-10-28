package com.jfinal.ext.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;

/**
 * 统一XSS处理
 */
public class XssHandler extends Handler {
 
    @Override
    public void handle(String target, HttpServletRequest request,
            HttpServletResponse response, boolean[] isHandled) {
        next.handle(target, new XSSRequestWrapper(request), response, isHandled);
    }
}