package com.jfinal.ext.kit;

import com.jfinal.base.BaseConfig;
import com.jfinal.base.ReturnResult;
import com.jfinal.core.Controller;
import com.jfinal.ext.plugin.validate.ValidateKit;
import com.jfinal.kit.StrKit;
import com.jfinal.route.ControllerBind;

import java.util.Enumeration;

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

    public static String controlerKey(Controller controller){
        ControllerBind cb = controller.getClass().getAnnotation(ControllerBind.class);
        return cb == null?null:cb.controllerKey();
    }

    public static String controlerUrl(Controller controller){
        String controllerKey = controlerKey(controller);
        if(!StrKit.isBlank(controllerKey)){
            if(controllerKey.startsWith("/")){
                controllerKey = controllerKey.replaceFirst("/", "");
            }
            if(controllerKey.endsWith("/")){
                controllerKey = controllerKey.substring(0, controllerKey.length()-1);
            }
            return controllerKey;
        }
        return null;
    }

    public static String showPage(Controller controller){
        setAttrs(controller);
        return replacePageUrl(controller.getPara())+".html";
    }
    /**
     * 5个(_)向上3级，从最前面开始匹配第一个
     * 4个(_)向上2级，从最前面开始匹配第一个
     * 3个(_)向上1级，从最前面开始匹配第一个
     * 2个(_)向下1级
     * @param url
     * @return
     */
    public static String replacePageUrl(String url){
        if(StrKit.isBlank(url)){
            return url;
        }
        if(url.startsWith("_____")){
            url =  url.replaceFirst("_____", "../../../");
        }
        else if(url.startsWith("____")){
            url =  url.replaceFirst("____", "../../");
        }
        else if(url.startsWith("___")){
            url = url.replaceFirst("___", "../");
        }
        else if(url.startsWith("__")){
            url = url.replaceFirst("__", "");
        }
        return url.replaceAll("__", "/");
    }

    public static String appendUrlParas(Controller controller, String url){
        StringBuilder builder = new StringBuilder();
        Enumeration<String> paras = controller.getParaNames();
        while(paras.hasMoreElements()){
            String paraName=paras.nextElement();
            builder.append("&"+paraName+"="+controller.getPara(paraName));
        }
        if(url.indexOf("?") == -1 && builder.length() > 0){
            return url+"?"+builder.substring(1);
        }
        return url+builder.toString();
    }

    public static void setAttrs(Controller controller){
        Enumeration<String> paras = controller.getParaNames();
        while(paras.hasMoreElements()){
            String paraName=paras.nextElement();
            controller.setAttr(paraName, controller.getPara(paraName));
        }
    }
}
