package com.jfinal.ext.sql;

import com.jfinal.kit.StrKit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hang on 2017/4/27 0027.
 */
class CndDelete<M extends CndDelete> extends CndBaseSelect<M> {
    //操作的表名
    private String tableName;

    public M table(String tableName){
        this.tableName = tableName;
        return (M)this;
    }

    public M build(){
        sql.append(String.format(Cnd.$DELETE_FROM_TABLE, tableName));

        StringBuilder  sb = new StringBuilder();
        List<Object> paramArrayList = new ArrayList<Object>();

        CndWhere where = getWhere();
        CndBuilder.build$CndWhere(where, sb, paramArrayList);

        CndBuilder.build$Symbol(where, sb, sql);

        paramList.addAll(paramArrayList);
        return (M)this;
    }

    public static void main(String[] args) {
        Cnd cnd = Cnd.$delete()
                .table("mmm")
                .where()
                .and("ccc", "1,2,3")
                .or("a", 1)
                .build();
        System.out.println(cnd.getSql());
        System.out.println(cnd.getParas());
    }
}
