package com.test.fastjson;


import com.jarvis.cache.serializer.FastjsonSerializer;
import com.jarvis.cache.to.CacheWrapper;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Test;


/**
 *
 *
 * @unknown jiayu.qiu
 */
public class FastJsonDeepCloneCacheWrapperTest {
    @Test
    public void testCacheWrapper() throws Exception {
        CacheWrapper<Object> cache = new CacheWrapper<Object>();
        List<User> list = new ArrayList<User>();
        User user = new User();
        setName("ttt");
        setBirthday(new Date());
        setId(111);
        list.add(user);
        cache.setCacheObject(list);
        Field[] fields = CacheWrapper.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Type genericFieldType = field.getGenericType();
            System.out.println((((field.getName()) + "--->") + (genericFieldType.getClass().getName())));
            if (genericFieldType instanceof ParameterizedType) {
                System.out.println("----ParameterizedType----------------");
            } else
                if (genericFieldType instanceof TypeVariable) {
                    System.out.println("----TypeVariable----------------");
                    TypeVariable tv = ((TypeVariable) (genericFieldType));
                    Type[] types = tv.getBounds();
                    for (Type type : types) {
                        System.out.println(("-----" + type));
                    }
                }

        }
        Type superType = CacheWrapper.class.getGenericSuperclass();
        System.out.println(superType.getClass().getName());
        FastjsonSerializer fjson = new FastjsonSerializer();
        CacheWrapper<List<User>> obj = ((CacheWrapper<List<User>>) (fjson.deepClone(cache, null)));
        System.out.println(obj.getCacheObject());
    }
}

