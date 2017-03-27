package com.jfinal.base;

import com.jfinal.interfaces.ISuccCallback;
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
		delete(null);
	}

	public void delete(ISuccCallback<ReturnResult> call){
		String id = getPara();
		if(StrKit.isBlank(id)){
			renderResult(BaseConfig.attrValueEmpty("id"));
			return;
		}
		ReturnResult result = ReturnResult.create(getM().deletes(id)).call(new ISuccCallback<ReturnResult>() {
			@Override
			public ReturnResult callback(ReturnResult returnResult) {
				if(call != null){
					returnResult.setResult(id);
					call.callback(returnResult);
				}
				return returnResult;
			}
		});
		renderResult(result.render());
	}

	/**
	 * 通用批量删除
	 * 
	 */
	public void deletes() {
		deletes(null);
	}
	public void deletes(ISuccCallback<ReturnResult> call){
		ReturnResult result = checkNotNull("ids").call(new ISuccCallback<ReturnResult>() {
			@Override
			public ReturnResult callback(ReturnResult returnResult) {
				String ids[] = getPara("ids").split(",");
				return ReturnResult.create(getM().deletes(ids)).call(new ISuccCallback<ReturnResult>() {
					@Override
					public ReturnResult callback(ReturnResult returnResult) {
						if(call != null){
							returnResult.setResult(ids);
							call.callback(returnResult);
						}
						return returnResult;
					}
				});
			}
		});
		renderResult(result.render());
	}

	/**
	 * 通用新增
	 * 
	 */
	public void save(){
		renderResult(save(getData(), null).render());
	}
	
	public void save(ISuccCallback<ReturnResult> call){
		renderResult(save(getData(), call).render());
	}

	public ReturnResult save(M data){
		return save(data, null);
	}

	public ReturnResult save(M data, ISuccCallback<ReturnResult> call){
		return checkSaveOrUpdate(data).call(new ISuccCallback<ReturnResult>() {
			@Override
			public ReturnResult callback(ReturnResult returnResult) {
				return ReturnResult.create(data.save()).call(new ISuccCallback<ReturnResult>() {
					@Override
					public ReturnResult callback(ReturnResult returnResult) {
						if(call != null){
							returnResult.setResult(data);
							call.callback(returnResult);
						}
						return returnResult;
					}
				});
			}
		});
	}

	/**
	 * 通用修改
	 * 
	 */
	public void update(){
		renderResult(update(getData(), null).render());
	}

	public void update(ISuccCallback<ReturnResult> call){
		renderResult(update(getData(), call).render());
	}

	public ReturnResult update(M data){
		return update(data, null);
	}

	public ReturnResult update(M data, ISuccCallback<ReturnResult> call){
		return checkSaveOrUpdate(data).call(new ISuccCallback<ReturnResult>() {
			@Override
			public ReturnResult callback(ReturnResult returnResult) {
				return ReturnResult.create(data.update()).call(new ISuccCallback<ReturnResult>() {
					@Override
					public ReturnResult callback(ReturnResult returnResult) {
						if(call != null){
							returnResult.setResult(data);
							call.callback(returnResult);
						}
						return returnResult;
					}
				});
			}
		});
	}

	private ReturnResult checkSaveOrUpdate(M data){
		if(data == null || data._getAttrNames() == null || data._getAttrNames().length == 0){
			return BaseConfig.dataError();
		}
		return ReturnResult.success();
	}
}
