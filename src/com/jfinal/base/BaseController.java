package com.jfinal.base;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.NotAction;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;
/**
 *BaseController
 */
public class BaseController<M extends Model<M>> extends BaseQueryController<M> {
	/**
	 * 通用删除
	 * 
	 * @throws Exception
	 */
	public void delete() {
		String id = getPara();
		if(StrKit.isBlank(id)){
			renderError("id不能为空");
			return;
		}
		M data = getM().findById(id);
		if(data == null){
			renderError("数据不存在");
			return;
		}
		renderJson(data.delete());
	}

	/**
	 * 通用批量删除
	 * 
	 * @throws Exception
	 */
	public void deletes() {
		checkNotNull("ids");
		
		String ids[] = getPara("ids").split(",");
		renderJson(getM().deletes(ids));
	}	

	/**
	 * 通用新增
	 * 
	 */
	public void save(){
		M data = getData();
		checkSaveOrUpdate(data);
		renderJson(save(data));
	}
	
	@Before(NotAction.class)
	public boolean save(M data){
		checkSaveOrUpdate(data);
		return data.save();
	}

	/**
	 * 通用修改
	 * 
	 */
	public void update(){
		M data = getData();
		checkSaveOrUpdate(data);
		renderJson(update(data));
	}
	
	@Before(NotAction.class)
	public boolean update(M data){
		checkSaveOrUpdate(data);
		return data.update();
	}
	
	private void checkSaveOrUpdate(M data){
		if(data == null || data._getAttrNames() == null || data._getAttrNames().length == 0){
			renderError("数据错误");
			throw new IllegalArgumentException("数据错误");
		}
	}
}
