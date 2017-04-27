package com.jfinal.ext.sql;

import java.util.HashMap;
import java.util.Map;

import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.sql.SqlKit;
import com.jfinal.template.Engine;

/**
 * Created by hang on 2017/4/27 0027.
 */
@SuppressWarnings("unchecked")
class CndTemplate<M extends CndTemplate<M>> extends Cnd {
    static Engine engine = new SqlKit(CndTemplate.class.getSimpleName()).getEngine();
    
    StringBuilder selectSql = new StringBuilder();
    StringBuilder fromSql = new StringBuilder();
    
    public M toCndByStr(String sqlStr, Map<String,Object> map){
    	build(sqlStr, map);
    	return (M)this;
    }
	
    public Cnd.Select to$Select(String sqlStr, Map<String,Object> map){
    	 this.selectSql = build(sqlStr, map);
        return to$Select();
    }
    
    public Cnd.Select to$Select(String select, String sqlExceptSelect, Map<String,Object> map){
        this.selectSql = build(select, map);
        this.fromSql = build(sqlExceptSelect, map);
        return to$Select();
    }
    
    private Cnd.Select to$Select(){
    	Cnd.Select select = Cnd.$select();
    	select.paramList.addAll(paramList);
    	return select.select(selectSql.toString()).from(fromSql.toString());
    }
    
    private StringBuilder build(String sqlStr, Map<String,Object> map){
    	SqlPara sqlPara = getSqlParaByStr(sqlStr, map);
        StringBuilder sb = engine.getTemplateByString(sqlStr).renderToStringBuilder(map);

        sql.append(sb.toString());
        if(sqlPara != null){
            for(Object object : sqlPara.getPara()){
                paramList.add(object);
            }
        }
        return sb;
    }

    SqlPara getSqlParaByStr(String sql, Map<String, Object> data){
        SqlPara sqlPara = new SqlPara();
        data.put(SqlKit.SQL_PARA_KEY, sqlPara);
        sqlPara.setSql(sql);
        return sqlPara;
    }

    public static void main(String[] args) {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("status", 0);
        map.put("qq", 1);

        String select = "select *,#p(qq) ";
        String sqlExceptSelect = "from table where qq = #p(qq) and status = #p(status)";
        Cnd.Select cnd = Cnd.$template().to$Select(select+sqlExceptSelect,map).and("aa", 11).build();
        System.out.println(cnd.getSql());
        System.out.println(cnd.getSelectSql());
        System.out.println(cnd.getFromSql());
        for(Object o : cnd.getParas()){
            System.out.println(o);
        }
    }
}
