package com.jfinal.ext.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;

/**
 * request Parameter处理
 */
public class ParameterHandler extends Handler {
 
    @Override
    public void handle(String target, HttpServletRequest request,
            HttpServletResponse response, boolean[] isHandled) {
        next.handle(target, new ParameterRequestWrapper(request), response, isHandled);
    }
}