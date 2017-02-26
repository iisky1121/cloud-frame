package com.jfinal.base;

import com.apis.wechat.Wechat;
import com.apis.wechat.msg.InMsgParser;
import com.apis.wechat.msg.in.InImageMsg;
import com.apis.wechat.msg.in.InLinkMsg;
import com.apis.wechat.msg.in.InLocationMsg;
import com.apis.wechat.msg.in.InMsg;
import com.apis.wechat.msg.in.InNotDefinedMsg;
import com.apis.wechat.msg.in.InShortVideoMsg;
import com.apis.wechat.msg.in.InTextMsg;
import com.apis.wechat.msg.in.InVideoMsg;
import com.apis.wechat.msg.in.InVoiceMsg;
import com.apis.wechat.msg.in.event.InCustomEvent;
import com.apis.wechat.msg.in.event.InFollowEvent;
import com.apis.wechat.msg.in.event.InLocationEvent;
import com.apis.wechat.msg.in.event.InMassEvent;
import com.apis.wechat.msg.in.event.InMenuEvent;
import com.apis.wechat.msg.in.event.InMerChantOrderEvent;
import com.apis.wechat.msg.in.event.InNotDefinedEvent;
import com.apis.wechat.msg.in.event.InPoiCheckNotifyEvent;
import com.apis.wechat.msg.in.event.InQrCodeEvent;
import com.apis.wechat.msg.in.event.InShakearoundUserShakeEvent;
import com.apis.wechat.msg.in.event.InSubmitMemberCardEvent;
import com.apis.wechat.msg.in.event.InTemplateMsgEvent;
import com.apis.wechat.msg.in.event.InUpdateMemberCardEvent;
import com.apis.wechat.msg.in.event.InUserPayFromCardEvent;
import com.apis.wechat.msg.in.event.InUserViewCardEvent;
import com.apis.wechat.msg.in.event.InVerifyFailEvent;
import com.apis.wechat.msg.in.event.InVerifySuccessEvent;
import com.apis.wechat.msg.in.event.InWifiEvent;
import com.apis.wechat.msg.in.speech_recognition.InSpeechRecognitionResults;
import com.apis.wechat.msg.out.OutMsg;
import com.apis.wechat.msg.out.OutTextMsg;
import com.jfinal.core.Controller;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.LogKit;
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
	
	protected Wechat.Cfg getCfg() {
		return cfg;
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
		else if (msg instanceof InSpeechRecognitionResults)  //update by unas at 2016-1-29, 由于继承InVoiceMsg，需要在InVoiceMsg前判断类型
			processInSpeechRecognitionResults((InSpeechRecognitionResults) msg);
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
		else if (msg instanceof InWifiEvent)
			processInWifiEvent((InWifiEvent) msg);
		else if (msg instanceof InUserViewCardEvent)
			processInUserViewCardEvent((InUserViewCardEvent) msg);
		else if (msg instanceof InSubmitMemberCardEvent)
			processInSubmitMemberCardEvent((InSubmitMemberCardEvent) msg);
		else if (msg instanceof InUpdateMemberCardEvent)
			processInUpdateMemberCardEvent((InUpdateMemberCardEvent) msg);
		else if (msg instanceof InUserPayFromCardEvent)
			processInUserPayFromCardEvent((InUserPayFromCardEvent) msg);
		else if (msg instanceof InMerChantOrderEvent)
			processInMerChantOrderEvent((InMerChantOrderEvent) msg);
		else if (msg instanceof InNotDefinedEvent) {
			log.error("未能识别的事件类型。 消息 xml 内容为：\n" + getInMsgXml());
			processIsNotDefinedEvent((InNotDefinedEvent) msg);
		} else if (msg instanceof InNotDefinedMsg) {
			log.error("未能识别的消息类型。 消息 xml 内容为：\n" + getInMsgXml());
			processIsNotDefinedMsg((InNotDefinedMsg) msg);
		}
	}
	
	/**
	 * 在接收到微信服务器的 InMsg 消息后后响应 OutMsg 消息
	 */
	protected void render(OutMsg outMsg) {
		String outMsgXml = outMsg.toXml();
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
			LogKit.info("inMsgXml:"+inMsgXml);
			
			if(StrKit.isBlank(inMsgXml)){
				return null;
			}
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
	
	//  关注/取消关注事件
	protected abstract void processInFollowEvent(InFollowEvent inFollowEvent);
	
	// 接收文本消息事件
	protected abstract void processInTextMsg(InTextMsg inTextMsg);
	
	// 自定义菜单事件
	protected abstract void processInMenuEvent(InMenuEvent inMenuEvent);
	
	// 接收图片消息事件
	protected void processInImageMsg(InImageMsg inImageMsg) {}
	
	// 接收语音消息事件
	protected void processInVoiceMsg(InVoiceMsg inVoiceMsg) {}
	
	// 接收视频消息事件
	protected void processInVideoMsg(InVideoMsg inVideoMsg) {}
	
	// 接收地理位置消息事件
	protected void processInLocationMsg(InLocationMsg inLocationMsg) {}
	
	// 接收链接消息事件
	protected void processInLinkMsg(InLinkMsg inLinkMsg) {}
	
	// 扫描带参数二维码事件
	protected void processInQrCodeEvent(InQrCodeEvent inQrCodeEvent) {}
	
	// 上报地理位置事件
	protected void processInLocationEvent(InLocationEvent inLocationEvent) {}
	
	// 接收语音识别结果，与 InVoiceMsg 唯一的不同是多了一个 Recognition 标记
	protected void processInSpeechRecognitionResults(InSpeechRecognitionResults inSpeechRecognitionResults) {}
	
	// 在模版消息发送任务完成后事件
	protected void processInTemplateMsgEvent(InTemplateMsgEvent inTemplateMsgEvent) {}

	// 群发完成事件
	protected void processInMassEvent(InMassEvent inMassEvent) {}

	// 接收小视频消息
	protected void processInShortVideoMsg(InShortVideoMsg inShortVideoMsg) {}

	// 接客服入会话事件
	protected void processInCustomEvent(InCustomEvent inCustomEvent) {}

	// 用户进入摇一摇界面，在“周边”页卡下摇一摇时事件
	protected void processInShakearoundUserShakeEvent(InShakearoundUserShakeEvent inShakearoundUserShakeEvent) {}

	// 资质认证事件
	protected void processInVerifySuccessEvent(InVerifySuccessEvent inVerifySuccessEvent) {}

	// 资质认证失败事件
	protected void processInVerifyFailEvent(InVerifyFailEvent inVerifyFailEvent){}

	// 门店在审核通过后下发消息事件
	protected void processInPoiCheckNotifyEvent(InPoiCheckNotifyEvent inPoiCheckNotifyEvent) {}

	// WIFI连网后下发消息 by unas at 2016-1-29
	protected void processInWifiEvent(InWifiEvent inWifiEvent) {}

	// 微信会员卡二维码扫描领取事件
	protected void processInUserViewCardEvent(InUserViewCardEvent msg) {}

	// 微信会员卡激活事件
	protected void processInSubmitMemberCardEvent(InSubmitMemberCardEvent msg) {}

	// 微信会员卡积分变更事件
	protected void processInUpdateMemberCardEvent(InUpdateMemberCardEvent msg) {}

	// 微信会员卡快速买单事件
	protected void processInUserPayFromCardEvent(InUserPayFromCardEvent msg) {}

	// 微信小店订单支付成功接口事件
	protected void processInMerChantOrderEvent(InMerChantOrderEvent inMerChantOrderEvent) {}

	// 没有找到对应的事件消息
	protected void processIsNotDefinedEvent(InNotDefinedEvent inNotDefinedEvent) {}

	// 没有找到对应的消息
	protected void processIsNotDefinedMsg(InNotDefinedMsg inNotDefinedMsg) {}
	
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