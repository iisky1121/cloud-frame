package com.jfinal.ext.kit;

import com.jfinal.base.BaseConfig;
import com.jfinal.base.ReturnResult;
import com.jfinal.core.Controller;
import com.jfinal.ext.plugin.validate.ValidateKit;

/**
 * Created by hang on 2017/7/26 0026.
 */
public class ControllerKit{
    /**
     * 设置登录用户信息
     *
     * @return T
     */
    public static void setLoginUser(Controller controller, Object obj){
        controller.setSessionAttr(BaseConfig.loginUserSessionAttr, obj);
    }

    /**
     * 清除登录用户信息
     *
     * @return T
     */
    public static void clearLoginUser(Controller controller){
        controller.removeSessionAttr(BaseConfig.loginUserSessionAttr);
    }

    /**
     * 获取用户登录信息
     *
     * @return T
     */
    public static <T> T loginUser(Controller controller){
        return controller.getSessionAttr(BaseConfig.loginUserSessionAttr);
    }

    /**
     * 检查必填属性
     *
     */
    public static ReturnResult checkNotNull(Controller controller, String... attrs){
        return ValidateKit.checkNotNull(controller, attrs);
    }

    /**
     * 检查属性值
     *
     */
    public static ReturnResult checkAttrValue(Controller controller, String attr, Object... values){
        return ValidateKit.checkAttrValue(controller, attr, values);
    }

    /**
     * 检查属性值
     *
     */
    public static ReturnResult checkAttrValue(Controller controller, String attr, Class<Enum<?>> enumClass){
        return ValidateKit.checkAttrValue(controller, attr, enumClass);
    }
}
