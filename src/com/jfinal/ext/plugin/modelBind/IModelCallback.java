package com.jfinal.ext.plugin.modelBind;

import com.jfinal.plugin.activerecord.Model;

/**
 * IModelCallback 保存和修改时，回调model对象
 */
public interface IModelCallback {
	void save(Model<?> model);
	void update(Model<?> model);
}