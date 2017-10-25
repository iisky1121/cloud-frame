package com.jfinal.ext.kit;

import com.jfinal.kit.StrKit;

import java.util.Collection;

public class ArrayKit {

    /**
     * 兼容原始类型数组无法强转为Object[]的问题,
     * java内原始类型数组转化为对象数组会特别麻烦,所以本方法穷举出所有的原始类型数组进行强转
     */
    public static Object[] toObjectArray(Object array) {
        if (!array.getClass().isArray()) {
            throw new IllegalArgumentException("入参必须是数组");
        }
        if (int[].class.isInstance(array)) {
            int[] intArr = int[].class.cast(array);
            Integer[] objArr = new Integer[intArr.length];
            for (int i = 0; i < intArr.length; i++) {
                objArr[i] = intArr[i];
            }
            return objArr;
        }
        if (long[].class.isInstance(array)) {
            long[] longArr = long[].class.cast(array);
            Long[] objArr = new Long[longArr.length];
            for (int i = 0; i < longArr.length; i++) {
                objArr[i] = longArr[i];
            }
            return objArr;
        }
        if (short[].class.isInstance(array)) {
            short[] arr = short[].class.cast(array);
            Short[] objArr = new Short[arr.length];
            for (int i = 0; i < arr.length; i++) {
                objArr[i] = arr[i];
            }
            return objArr;
        }
        if (float[].class.isInstance(array)) {
            float[] arr = float[].class.cast(array);
            Float[] objArr = new Float[arr.length];
            for (int i = 0; i < arr.length; i++) {
                objArr[i] = arr[i];
            }
            return objArr;
        }
        if (double[].class.isInstance(array)) {
            double[] arr = double[].class.cast(array);
            Double[] objArr = new Double[arr.length];
            for (int i = 0; i < arr.length; i++) {
                objArr[i] = arr[i];
            }
            return objArr;
        }
        if (char[].class.isInstance(array)) {
            char[] arr = char[].class.cast(array);
            Character[] objArr = new Character[arr.length];
            for (int i = 0; i < arr.length; i++) {
                objArr[i] = arr[i];
            }
            return objArr;
        }
        if (byte[].class.isInstance(array)) {
            byte[] arr = byte[].class.cast(array);
            Byte[] objArr = new Byte[arr.length];
            for (int i = 0; i < arr.length; i++) {
                objArr[i] = arr[i];
            }
            return objArr;
        }
        if (boolean[].class.isInstance(array)) {
            boolean[] arr = boolean[].class.cast(array);
            Boolean[] objArr = new Boolean[arr.length];
            for (int i = 0; i < arr.length; i++) {
                objArr[i] = arr[i];
            }
            return objArr;
        }
        return Object[].class.cast(array);
    }

    public static String join(Collection collection) {
        return join(collection, ",");
    }

    public static String join(Collection collection, String separator) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for(Object object : collection){
            if (i > 0 && StrKit.notBlank(separator)) {
                sb.append(separator);
            }
            sb.append(object);
            i++;
        }
        return sb.toString();
    }
}
