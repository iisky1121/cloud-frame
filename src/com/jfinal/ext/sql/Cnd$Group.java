package com.jfinal.ext.sql;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hang on 2017/4/26 0026.
 */
public class Cnd$Group {
    private List<Param> params = new ArrayList<Param>();

    public Cnd$Group and(Param p){
        p.setSymbol(Cnd.Symbol.and);
        return add(p);
    }

    public Cnd$Group or(Param p){
        p.setSymbol(Cnd.Symbol.or);
        return add(p);
    }

    private Cnd$Group add(Param p){
        params.add(p);
        return this;
    }
}
