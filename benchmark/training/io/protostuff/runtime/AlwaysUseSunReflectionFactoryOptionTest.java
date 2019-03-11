package io.protostuff.runtime;


import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Kostiantyn Shchepanovskyi
 */
public class AlwaysUseSunReflectionFactoryOptionTest {
    @Test
    public void forceUseSunReflectionFactory() throws Exception {
        System.setProperty("protostuff.runtime.always_use_sun_reflection_factory", "true");
        Schema<AlwaysUseSunReflectionFactoryOptionTest.MyClass> schema = RuntimeSchema.getSchema(AlwaysUseSunReflectionFactoryOptionTest.MyClass.class);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        AlwaysUseSunReflectionFactoryOptionTest.MyClass myClass = new AlwaysUseSunReflectionFactoryOptionTest.MyClass();// constructor initializes list with one element

        ProtostuffIOUtil.writeTo(output, myClass, schema, LinkedBuffer.allocate());
        byte[] bytes = output.toByteArray();
        Assert.assertEquals(1, myClass.getList().size());
        AlwaysUseSunReflectionFactoryOptionTest.MyClass myClassNew = schema.newMessage();// default constructor should not be used

        ProtostuffIOUtil.mergeFrom(bytes, myClassNew, schema);
        Assert.assertEquals(1, myClassNew.getList().size());
    }

    static final class MyClass {
        private List<String> list;

        public MyClass() {
            list = new ArrayList<String>();
            list.add("hello");
        }

        public List<String> getList() {
            return list;
        }
    }
}

