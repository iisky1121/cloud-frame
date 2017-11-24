package com.jfinal.base;

import com.jfinal.ext.kit.ControllerKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;

public class TemplateController<M extends Model<M>> extends BaseController<M> {
	/**
	 * 模块首页
	 */
	public void index(){
		ControllerKit.setAttrs(this);
		setAttr("_dataUrl", ControllerKit.controlerUrl(this));
		render("_index.html");
	}
	
	/**
	 * 分页列表数据
	 */
	public void getByPage(){
		setAttr("_page", kit.getPage(getParaMap()));
		render("_page.html");
	}
	
	/**
	 * 模块详情
	 */
	public void info(){
		view();
		render("_form.html");
	}

	/**
	 * 模块详情（只读）
	 */
	public void view(){
		showPage();
		setAttr("_id", getPara());
		render("_view.html");
	}

	/**
	 * 页面跳转
	 */
	public void showPage(){
		setAttr("_dataUrl", ControllerKit.controlerUrl(this));
		if(!StrKit.isBlank(getPara("id"))){
			setAttr("_id", getPara("id"));
		}
		render(ControllerKit.showPage(this));
	}
}
