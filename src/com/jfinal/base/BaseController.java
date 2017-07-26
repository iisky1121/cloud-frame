package com.jfinal.base;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;

/**
 *BaseController
 */
public abstract class BaseController<M extends Model<M>> extends CommonController {
	protected BaseControllerKit<M> kit = new BaseControllerKit<M>(this);
	/**
	 * 通用删除
	 * 
	 */
	public void delete() {
		String id = getPara();
		if(StrKit.isBlank(id)){
			renderResult(BaseConfig.attrValueEmpty("id"));
			return;
		}
		renderResult(kit.delete(id, null));
	}

	/**
	 * 通用批量删除
	 * 
	 */
	public void deletes(){
		ReturnResult result = checkNotNull("ids");
		if(!result.isSucceed()){
			renderResult(result);
			return;
		}
		renderResult(kit.deletes(getPara("ids").split(","), null));
	}

	/**
	 * 通用新增
	 * 
	 */
	public void save(){
		renderResult(kit.save(kit.getData(), null));
	}

	/**
	 * 通用修改
	 * 
	 */
	public void update(){
		renderResult(kit.update(kit.getData(), null));
	}

	/**
	 * 通用分页查找
	 */
	public void getByPage() {
		renderSucc(kit.getPage(getParaMap()));
	}

	/**
	 * 通用查找全部
	 */
	public void getAll() {
		renderSucc(kit.getList(getParaMap()));
	}

	/**
	 * 通用根据id查找
	 */
	public void getById() {
		String id = getPara();
		if(StrKit.isBlank(id)){
			renderResult(BaseConfig.attrValueEmpty("id"));
			return;
		}
		renderSucc(kit.getById(id));
	}
}
