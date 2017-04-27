package com.jfinal.ext.sql;

import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.sql.NameSpaceDirective;
import com.jfinal.plugin.activerecord.sql.ParaDirective;
import com.jfinal.plugin.activerecord.sql.SqlDirective;
import com.jfinal.template.Engine;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hang on 2017/4/27 0027.
 */
@SuppressWarnings("unchecked")
class CndTemplate<M extends CndTemplate<M>> extends Cnd {
    static Engine engine = new Engine()
            .setDevMode(true)
            .addDirective("namespace", new NameSpaceDirective())
            .addDirective("sql", new SqlDirective())
            .addDirective("para", new ParaDirective())
            .addDirective("p", new ParaDirective());

    static final String SQL_PARA_KEY = "_SQL_PARA_";
    public M toCndByStr(String templateStr, Map<String,Object> map){
        SqlPara sqlPara = getSqlParaByStr(templateStr, map);
        StringBuilder sb = engine.getTemplateByString(templateStr).renderToStringBuilder(map);

        sql.append(sb.toString());
        if(sqlPara != null){
            for(Object object : sqlPara.getPara()){
                paramList.add(object);
            }
        }
        return (M)this;
    }

    SqlPara getSqlParaByStr(String sql, Map<String, Object> data){
        SqlPara sqlPara = new SqlPara();
        data.put(SQL_PARA_KEY, sqlPara);
        sqlPara.setSql(sql);
        return sqlPara;
    }

    public static void main(String[] args) {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("status", 0);
        map.put("qq", 1);

        String templateStr = "select * from table where qq = #p(qq) and status = #p(status)";
        Cnd cnd = Cnd.$template().toCndByStr(templateStr,map);
        System.out.println(cnd.getSql());
        for(Object o : cnd.getParas()){
            System.out.println(o);
        }
    }
}
