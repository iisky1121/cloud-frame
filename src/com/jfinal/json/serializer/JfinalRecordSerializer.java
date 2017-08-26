package com.jfinal.json.serializer;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.jfinal.plugin.activerecord.Record;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

public class JfinalRecordSerializer implements ObjectSerializer {
    public static final JfinalRecordSerializer instance = new JfinalRecordSerializer();
    /**
     */
    public JfinalRecordSerializer() {
    }

    /**
     * @param serializer
     * @param object
     * @param fieldName
     * @param fieldType
     * @param features
     * @throws IOException
     */
    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        if (object == null) {
            serializer.out.writeNull();
            return;
        }

        Record record = (Record) object;

        Map<String, Object> map = record.getColumns();
        serializer.write(map);
    }
}
