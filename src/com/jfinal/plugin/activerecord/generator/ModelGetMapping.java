package com.jfinal.plugin.activerecord.generator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hang on 2017/8/23 0023.
 */
class ModelGetMapping {
    static Map<String, String> mapping = new HashMap<String,String>(){{
        put("java.util.Date", "getDate");
        put("java.sql.Time", "getTime");
        put("java.sql.Timestamp", "getTimestamp");

        put("java.lang.String", "getStr");

        // int, integer, tinyint, smallint, mediumint
        put("java.lang.Integer", "getInt");

        // bigint
        put("java.lang.Long", "getLong");

        // real, double
        put("java.lang.Double", "getDouble");

        // float
        put("java.lang.Float", "getFloat");

        // bit
        put("java.lang.Boolean", "getBoolean");

        // decimal, numeric
        put("java.math.BigDecimal", "getBigDecimal");

        // unsigned bigint
        put("java.math.BigInteger", "getBigInteger");

        // short
        put("java.lang.Short", "getShort");
    }};
}
