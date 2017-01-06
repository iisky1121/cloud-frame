package com.jfinal.base;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.NotAction;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;
/**
 *BaseController
 */
public abstract class BaseController<M extends Model<M>> extends BaseQueryController<M> {
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
		M data = getM().findById(id);
		if(data == null){
			renderResult(BaseConfig.dataNotExist());
			return;
		}
		renderJson(data.delete());
	}

	/**
	 * 通用批量删除
	 * 
	 */
	public void deletes() {
		ReturnResult result = checkNotNull("ids");;
		if(!result.isSucceed()){
			renderResult(result);
			return;
		}
		
		String ids[] = getPara("ids").split(",");
		renderJson(getM().deletes(ids));
	}	

	/**
	 * 通用新增
	 * 
	 */
	public void save(){
		M data = getData();
		ReturnResult result = checkSaveOrUpdate(data);
		if(!result.isSucceed()){
			renderResult(result);
			return;
		}
		renderJson(save(data));
	}
	
	@Before(NotAction.class)
	public ReturnResult save(M data){
		ReturnResult result = checkSaveOrUpdate(data);
		if(!result.isSucceed()){
			return result;
		}
		return ReturnResult.create(data.save());
	}

	/**
	 * 通用修改
	 * 
	 */
	public void update(){
		M data = getData();
		ReturnResult result = checkSaveOrUpdate(data);
		if(!result.isSucceed()){
			renderResult(result);
			return;
		}
		renderJson(update(data));
	}
	
	@Before(NotAction.class)
	public ReturnResult update(M data){
		ReturnResult result = checkSaveOrUpdate(data);
		if(!result.isSucceed()){
			return result;
		}
		return ReturnResult.create(data.update());
	}
	
	private ReturnResult checkSaveOrUpdate(M data){
		if(data == null || data._getAttrNames() == null || data._getAttrNames().length == 0){
			return BaseConfig.dataError();
		}
		return ReturnResult.success();
	}
}
