package com.jfinal.json.serializer;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.jfinal.plugin.activerecord.Model;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

public class JfinalModelSerializer implements ObjectSerializer {
    public static final JfinalModelSerializer instance = new JfinalModelSerializer();
    /**
     */
    public JfinalModelSerializer() {
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

        Model model = (Model) object;

        Map<String, Object> map = model.getColumns();
        serializer.write(map);
    }
}
