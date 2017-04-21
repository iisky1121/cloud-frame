package com.jfinal.ext.kit;

import com.jfinal.interfaces.ICallback;
import com.jfinal.interfaces.IDataFilter;
import com.jfinal.interfaces.IDataGetter;
import com.jfinal.interfaces.IDataSelector;
import com.jfinal.kit.StrKit;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * ====================================================================
 * 　　==       ==         ==         ===      ==       ==== == ==
 * 　　==       ==       ==  ==       == ==    ==      ==
 * 　　== == == ==      == == ==      ==  ==   ==      ==     = ==
 * 　　==       ==     ==      ==     ==    == ==      ==       ==
 * 　　==       ==    ==        ==    ==      ===       ==== == ==
 * ====================================================================
 * Created by hang on 2017/3/24 0024.
 * ====================================================================
 */
public class DataKit {
    public static <T> void filter(Collection<T> object, IDataFilter<T> filter){
        Iterator<T> iterator = object.iterator();
        while (iterator.hasNext()){
            T t = iterator.next();
            if(filter.filter(t)){
                iterator.remove();
            }
        }
    }

    public static <T> void selector(Collection<T> object, IDataSelector<T> selector){
        Iterator<T> iterator = object.iterator();
        while (iterator.hasNext()){
            T t = iterator.next();
            if(!selector.selector(t)){
                iterator.remove();
            }
        }
    }

    public static <T,M> Set<M> getValues(Collection<T> object, IDataGetter<T,M> getter){
        Set<M> set = new HashSet<M>();
        Iterator<T> iterator = object.iterator();
        while (iterator.hasNext()){
            T t = iterator.next();
            if(getter.selector(t)){
                set.add(getter.getter(t));
            }
        }
        return set;
    }

    public static <T> T getOrDefault(T t, T def){
        if(t == null){
            return def;
        }
        return t;
    }

    public static void nullCall(Object object, ICallback call){
        if(object == null && call != null){
            call.callback();
        }
    }

    public static void notNullCall(Object object, ICallback call){
        if(object != null && call != null){
            call.callback();
        }
    }

    public static void notBlankCall(String str, ICallback call){
        if(StrKit.notBlank(str) && call != null){
            call.callback();
        }
    }

    public static void blankCall(String str, ICallback call){
        if(StrKit.isBlank(str) && call != null){
            call.callback();
        }
    }

    public static void succCall(Boolean b, ICallback call){
        if(b != null && b && call != null){
            call.callback();
        }
    }

    public static <T> T ifNull(T t1, T t2){
        if(t1 == null){
            return t2;
        }
        return t1;
    }
}
