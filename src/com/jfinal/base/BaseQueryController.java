package com.jfinal.base;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.jfinal.ext.sql.Cnd;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
/**
 *BaseQueryController
 */
public abstract class BaseQueryController<M extends Model<M>> extends CommonController {
	
	protected M getM(){
		try {
			return getClazz().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public M getData(){
		M m = getM();
		if(m != null){
			if(m instanceof IBean){
				return getBean();
			}
			else{
				return getModel();
			}
		}
		return null;
	}

	/**
	 * 获取M的class
	 * 
	 * @return M
	 */
	@SuppressWarnings("unchecked")
	private Class<M> getClazz() {
		Type t = getClass().getGenericSuperclass();
		Type[] params = ((ParameterizedType) t).getActualTypeArguments();
		return (Class<M>) params[0];
	}
	
	public M getModel() {
		String alias = getAlias();
		if(alias == null){
			return getModel(getClazz(), true);
		}
		return getModel(getClazz(), alias, true);
	}
	
	public M getBean() {
		String alias = getAlias();
		if(alias == null){
			return getBean(getClazz(), true);
		}
		return getBean(getClazz(), alias, true);
	}
	/**
	 * 通过传入参数获取M的对象
	 * 
	 * @return M
	 */
	public Cnd.Select getQuery(Object ...modelClassAndAlias) {
		return getQuery(getParaMap(), modelClassAndAlias);
	}
	public Cnd.Select getQuery(Map<String, String[]> params) {
		String alias = getAlias();
		alias = (alias == null? "" : alias);
		return getQuery(params, getClazz(), alias);
	}
	public Cnd.Select getQuery(Map<String, String[]> params, Object ...modelClassAndAlias) {
		return Cnd.select().queryToCnd(params, modelClassAndAlias);
	}

	/**
	 * 获取Model的表名
	 * 
	 * @return String
	 */
	public String getTableName() {
		return getM().getTableName();
	}
	
	/**
	 * 获取Model的别名
	 * 
	 * @return String
	 */
	public String getAlias() {
		return getM().getAlias();
	}

	/**
	 * 通用分页查找
	 */
	public void getByPage() {
		renderSucc(getPage());
	}
	public Page<M> getPage() {
		return getPage(getParaMap());
	}
	public Page<M> getPage(Map<String, String[]> params) {
		Cnd.Select cnd = getQuery(params).where().build();
		
		Page<M> page = getM().paginate(getParaToInt("pageNumber", 1),
						getParaToInt("pageSize", 10),
						Cnd.$SELECT_,
						String.format(Cnd.$_FROM, getTableName()).concat(getAlias()==null?"":" "+getAlias()).concat(cnd.getSql()),
						cnd.getParas()
						);
		return page;
	}

	
	/**
	 * 通用查找全部
	 */
	public void getAll() {
		renderSucc(getList());
	}
	public List<M> getList() {
		return getList(getParaMap());
	}
	public List<M> getList(Map<String, String[]> params) {
		Cnd.Select cnd =getQuery(params).where().build();
		List<M> list = getM().find(String.format(Cnd.$SELECT_FROM, getTableName()).concat(getAlias()==null?"":" "+getAlias()).concat(cnd.getSql()), cnd.getParas());
		return list;
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
		M data = getM().findById(id);
		if(data == null){
			renderResult(BaseConfig.dataNotExist());
			return;
		}
		renderSucc(data);
	}
}
