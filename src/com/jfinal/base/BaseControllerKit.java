package com.jfinal.base;

import com.jfinal.core.Controller;
import com.jfinal.ext.plugin.sql.Cnd;
import com.jfinal.interfaces.ISuccCallback;
import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by hang on 2017/7/26 0026.
 */
public class BaseControllerKit<M extends Model<M>> {
    private Controller controller;

    public BaseControllerKit(Controller controller){
        this.controller = controller;
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

    protected M getM(){
        try {
            return getClazz().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Class<M> getClazz() {
        Type t = controller.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) t).getActualTypeArguments();
        return (Class<M>) params[0];
    }

    public M getModel() {
        String alias = getM().getAlias();
        if(alias == null){
            return controller.getModel(getClazz(), true);
        }
        return controller.getModel(getClazz(), alias, true);
    }

    public M getBean() {
        String alias = getM().getAlias();
        if(alias == null){
            return controller.getBean(getClazz(), true);
        }
        return controller.getBean(getClazz(), alias, true);
    }

    public ReturnResult checkSaveOrUpdate(M data){
        if(data == null || data._getAttrNames() == null || data._getAttrNames().length == 0){
            return BaseConfig.dataError();
        }
        return ReturnResult.success();
    }

    /**
     * 通过传入参数获取M的对象
     *
     * @return M
     */
    public Cnd.Query getQuery(Map<String, String[]> params) {
        String alias = getM().getAlias();
        alias = (alias == null? "" : alias);
        return Cnd.$query().setParaMap(params).setCnd(getClazz(), alias);
    }

    /**
     * 通用分页查找
     */
    public Page<M> getPage(Map<String, String[]> params) {
        Cnd.Query cnd = getQuery(params).where().build();

        Page<M> page = getM().paginate(controller.getParaToInt("pageNumber", 1),
                controller.getParaToInt("pageSize", 10),
                Cnd.$SELECT_,
                String.format(Cnd.$_FROM_TABLE, getM().getTableName()).concat(getM().getAlias()==null?"":" "+getM().getAlias()).concat(cnd.getSql()),
                cnd.getParas()
        );
        return page;
    }

    /**
     * 通用查找全部
     */
    public List<M> getList(Map<String, String[]> params) {
        Cnd.Query cnd =getQuery(params).where().build();
        List<M> list = getM().find(String.format(Cnd.$SELECT_FROM_TABLE, getM().getTableName()).concat(getM().getAlias()==null?"":" "+getM().getAlias()).concat(cnd.getSql()), cnd.getParas());
        return list;
    }

    /**
     * 通用根据id查找
     */
    public M getById(Object id) {
        return getM().findById(id);
    }

    /**
     * 通用删除
     *
     */
    public ReturnResult delete(Object id, ISuccCallback<ReturnResult> call){
        ReturnResult result = ReturnResult.create(getM().deleteById(id)).call(new ISuccCallback<ReturnResult>() {
            @Override
            public ReturnResult callback(ReturnResult returnResult) {
                if(call != null){
                    returnResult.setResult(id);
                    call.callback(returnResult);
                }
                return returnResult;
            }
        });
        return result;
    }

    /**
     * 通用批量删除
     *
     */
    public ReturnResult deletes(Object[] ids, ISuccCallback<ReturnResult> call){
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

    /**
     * 通用新增
     *
     */
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
}
