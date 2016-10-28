package com.jfinal.base;

import com.apis.wechat.Wechat;
import com.apis.wechat.msg.InMsgParser;
import com.apis.wechat.msg.OutMsgXmlBuilder;
import com.apis.wechat.msg.in.InImageMsg;
import com.apis.wechat.msg.in.InLinkMsg;
import com.apis.wechat.msg.in.InLocationMsg;
import com.apis.wechat.msg.in.InMsg;
import com.apis.wechat.msg.in.InShortVideoMsg;
import com.apis.wechat.msg.in.InTextMsg;
import com.apis.wechat.msg.in.InVideoMsg;
import com.apis.wechat.msg.in.InVoiceMsg;
import com.apis.wechat.msg.in.event.InCustomEvent;
import com.apis.wechat.msg.in.event.InFollowEvent;
import com.apis.wechat.msg.in.event.InLocationEvent;
import com.apis.wechat.msg.in.event.InMassEvent;
import com.apis.wechat.msg.in.event.InMenuEvent;
import com.apis.wechat.msg.in.event.InPoiCheckNotifyEvent;
import com.apis.wechat.msg.in.event.InQrCodeEvent;
import com.apis.wechat.msg.in.event.InShakearoundUserShakeEvent;
import com.apis.wechat.msg.in.event.InTemplateMsgEvent;
import com.apis.wechat.msg.in.event.InVerifyFailEvent;
import com.apis.wechat.msg.in.event.InVerifySuccessEvent;
import com.apis.wechat.msg.in.speech_recognition.InSpeechRecognitionResults;
import com.apis.wechat.msg.out.OutMsg;
import com.apis.wechat.msg.out.OutTextMsg;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;

/**
 * 接收微信服务器消息，自动解析成 InMsg 并分发到相应的处理方法
 */
public abstract class BaseWechatController extends Controller {
	
	private static final Log log =  Log.getLog(BaseWechatController.class);
	private String inMsgXml = null;		// 本次请求 xml数据
	private InMsg inMsg = null;			// 本次请求 xml 解析后的 InMsg 对象
	private Wechat.Cfg cfg = Wechat.getCfg();

	public void setCfg(Wechat.Cfg cfg){
		this.cfg = cfg;
	}
	
	/**
	 * weixin 公众号服务器调用唯一入口，即在开发者中心输入的 URL 必须要指向此 action
	 */
	public void index() {
		// 如果是服务器配置请求，则配置服务器并返回
		if (isConfigServerRequest()) {
			configServer();
			return;
		}

		// 开发模式输出微信服务发送过来的 xml 消息
		if (PropKit.getBoolean("devMode", false)) {
			System.out.println("接收消息:\n" + getInMsgXml());
		} else {
			// 签名检测
			if (!checkSignature()) {
				this.renderText("签名验证失败，请确定是微信服务器在发送消息过来");
				return;
			}
		}
		
		// 解析消息并根据消息类型分发到相应的处理方法
		InMsg msg = getInMsg();
		if (msg instanceof InTextMsg)
			processInTextMsg((InTextMsg) msg);
		else if (msg instanceof InImageMsg)
			processInImageMsg((InImageMsg) msg);
		else if (msg instanceof InVoiceMsg)
			processInVoiceMsg((InVoiceMsg) msg);
		else if (msg instanceof InVideoMsg)
			processInVideoMsg((InVideoMsg) msg);
		else if (msg instanceof InShortVideoMsg)   //支持小视频
			processInShortVideoMsg((InShortVideoMsg) msg);
		else if (msg instanceof InLocationMsg)
			processInLocationMsg((InLocationMsg) msg);
		else if (msg instanceof InLinkMsg)
			processInLinkMsg((InLinkMsg) msg);
		else if (msg instanceof InCustomEvent)
			processInCustomEvent((InCustomEvent) msg);
		else if (msg instanceof InFollowEvent)
			processInFollowEvent((InFollowEvent) msg);
		else if (msg instanceof InQrCodeEvent)
			processInQrCodeEvent((InQrCodeEvent) msg);
		else if (msg instanceof InLocationEvent)
			processInLocationEvent((InLocationEvent) msg);
		else if (msg instanceof InMassEvent)
			processInMassEvent((InMassEvent) msg);
		else if (msg instanceof InMenuEvent)
			processInMenuEvent((InMenuEvent) msg);
		else if (msg instanceof InSpeechRecognitionResults)
			processInSpeechRecognitionResults((InSpeechRecognitionResults) msg);
		else if (msg instanceof InTemplateMsgEvent)
			processInTemplateMsgEvent((InTemplateMsgEvent) msg);
		else if (msg instanceof InShakearoundUserShakeEvent)
			processInShakearoundUserShakeEvent((InShakearoundUserShakeEvent) msg);
		else if (msg instanceof InVerifySuccessEvent)
			processInVerifySuccessEvent((InVerifySuccessEvent) msg);
		else if (msg instanceof InVerifyFailEvent)
			processInVerifyFailEvent((InVerifyFailEvent) msg);
		else if (msg instanceof InPoiCheckNotifyEvent)
			processInPoiCheckNotifyEvent((InPoiCheckNotifyEvent) msg);
		else
			log.error("未能识别的消息类型。 消息 xml 内容为：\n" + getInMsgXml());
	}
	
	/**
	 * 在接收到微信服务器的 InMsg 消息后后响应 OutMsg 消息
	 */
	protected void render(OutMsg outMsg) {
		String outMsgXml = OutMsgXmlBuilder.build(outMsg);
		// 开发模式向控制台输出即将发送的 OutMsg 消息的 xml 内容
		if (PropKit.getBoolean("devMode", false)) {
			System.out.println("发送消息:\n"+outMsgXml);
		}
		
		// 是否需要加密消息
		if (cfg.isMessageEncrypt()) {
			outMsgXml = Wechat.common(cfg).encrypt(outMsgXml, getPara("timestamp"), getPara("nonce"));
		}
		
		renderText(outMsgXml, "text/xml");
	}
	
	protected void renderOutTextMsg(String content) {
		OutTextMsg outMsg= new OutTextMsg(getInMsg());
		outMsg.setContent(content);
		render(outMsg);
	}
	
	protected String getInMsgXml() {
		if (inMsgXml == null) {
			inMsgXml = HttpKit.readData(getRequest());
			
			// 是否需要解密消息
			if (cfg.isMessageEncrypt()) {
				inMsgXml = Wechat.common(cfg).decrypt(inMsgXml, getPara("timestamp"), getPara("nonce"), getPara("msg_signature"));
			}
		}
		return inMsgXml;
	}
	
	protected InMsg getInMsg() {
		if (inMsg == null)
			inMsg = InMsgParser.parse(getInMsgXml()); 
		return inMsg;
	}
	
	/**
	 * 配置开发者中心微信服务器所需的 url 与 token
	 * @return true 为config server 请求，false 正式消息交互请求
	 */
	private void configServer() {
		Controller c = this;
		// 通过 echostr 判断请求是否为配置微信服务器回调所需的 url 与 token
		String echostr = c.getPara("echostr");
		String signature = c.getPara("signature");
		String timestamp = c.getPara("timestamp");
		String nonce = c.getPara("nonce");
		boolean isOk = Wechat.common().checkSignature(signature, timestamp, nonce);
		if (isOk)
			c.renderText(echostr);
		else
			log.error("验证失败：configServer");
	}
	
	// 处理接收到的文本消息
	protected abstract void processInTextMsg(InTextMsg inTextMsg);
	
	// 处理接收到的图片消息
	protected abstract void processInImageMsg(InImageMsg inImageMsg);
	
	// 处理接收到的语音消息
	protected abstract void processInVoiceMsg(InVoiceMsg inVoiceMsg);
	
	// 处理接收到的视频消息
	protected abstract void processInVideoMsg(InVideoMsg inVideoMsg);

	// 处理接收到的视频消息
	protected abstract void processInShortVideoMsg(InShortVideoMsg inShortVideoMsg);
	
	// 处理接收到的地址位置消息
	protected abstract void processInLocationMsg(InLocationMsg inLocationMsg);

	// 处理接收到的链接消息
	protected abstract void processInLinkMsg(InLinkMsg inLinkMsg);

    // 处理接收到的多客服管理事件
    protected abstract void processInCustomEvent(InCustomEvent inCustomEvent);

	// 处理接收到的关注/取消关注事件
	protected abstract void processInFollowEvent(InFollowEvent inFollowEvent);
	
	// 处理接收到的扫描带参数二维码事件
	protected abstract void processInQrCodeEvent(InQrCodeEvent inQrCodeEvent);
	
	// 处理接收到的上报地理位置事件
	protected abstract void processInLocationEvent(InLocationEvent inLocationEvent);

    // 处理接收到的群发任务结束时通知事件
    protected abstract void processInMassEvent(InMassEvent inMassEvent);

	// 处理接收到的自定义菜单事件
	protected abstract void processInMenuEvent(InMenuEvent inMenuEvent);
	
	// 处理接收到的语音识别结果
	protected abstract void processInSpeechRecognitionResults(InSpeechRecognitionResults inSpeechRecognitionResults);
	
	// 处理接收到的模板消息是否送达成功通知事件
	protected abstract void processInTemplateMsgEvent(InTemplateMsgEvent inTemplateMsgEvent);

	// 处理微信摇一摇事件
	protected abstract void processInShakearoundUserShakeEvent(InShakearoundUserShakeEvent inShakearoundUserShakeEvent);

	// 资质认证成功 || 名称认证成功 || 年审通知 || 认证过期失效通知
	protected abstract void processInVerifySuccessEvent(InVerifySuccessEvent inVerifySuccessEvent);

	// 资质认证失败 || 名称认证失败
	protected abstract void processInVerifyFailEvent(InVerifyFailEvent inVerifyFailEvent);
	
	// 门店在审核事件消息
	protected abstract void processInPoiCheckNotifyEvent(InPoiCheckNotifyEvent inPoiCheckNotifyEvent);
	
	/**
	 * 检测签名
	 */
	private boolean checkSignature() {
		Controller controller = this;
		String signature = controller.getPara("signature");
		String timestamp = controller.getPara("timestamp");
		String nonce = controller.getPara("nonce");
		if (StrKit.isBlank(signature) || StrKit.isBlank(timestamp) || StrKit.isBlank(nonce)) {
			controller.renderText("check signature failure");
			return false;
		}
		
		if (Wechat.common().checkSignature(signature, timestamp, nonce)) {
			return true;
		}
		else {
			log.error("check signature failure: " +
					" signature = " + controller.getPara("signature") +
					" timestamp = " + controller.getPara("timestamp") +
					" nonce = " + controller.getPara("nonce"));
			
			return false;
		}
	}
	
	/**
	 * 是否为开发者中心保存服务器配置的请求
	 */
	private boolean isConfigServerRequest() {
		Controller controller = this;
		return StrKit.notBlank(controller.getPara("echostr"));
	}
}