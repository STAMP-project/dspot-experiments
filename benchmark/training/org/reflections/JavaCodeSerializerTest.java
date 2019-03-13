package org.reflections;


import org.junit.Assert;
import org.junit.Test;
import org.reflections.serializers.JavaCodeSerializer;


/**
 *
 */
public class JavaCodeSerializerTest {
    @Test
    public void resolve() throws NoSuchFieldException, NoSuchMethodException {
        // class
        Assert.assertEquals(TestModel.C1.class, JavaCodeSerializer.resolveClass(MyTestModelStore.org.reflections.TestModel$C1.class));
        // method
        Assert.assertEquals(TestModel.C4.class.getDeclaredMethod("m1"), JavaCodeSerializer.resolveMethod(MyTestModelStore.org.reflections.TestModel$C4.methods.m1.class));
        // overloaded method with parameters
        Assert.assertEquals(TestModel.C4.class.getDeclaredMethod("m1", int.class, String[].class), JavaCodeSerializer.resolveMethod(MyTestModelStore.org.reflections.TestModel$C4.methods.m1_int__java_lang_String$$.class));
        // overloaded method with parameters and multi dimensional array
        Assert.assertEquals(TestModel.C4.class.getDeclaredMethod("m1", int[][].class, String[][].class), JavaCodeSerializer.resolveMethod(MyTestModelStore.org.reflections.TestModel$C4.methods.m1_int$$$$__java_lang_String$$$$.class));
        // field
        Assert.assertEquals(TestModel.C4.class.getDeclaredField("f1"), JavaCodeSerializer.resolveField(MyTestModelStore.org.reflections.TestModel$C4.fields.f1.class));
        // annotation
        Assert.assertEquals(TestModel.C2.class.getAnnotation(TestModel.AC2.class), JavaCodeSerializer.resolveAnnotation(MyTestModelStore.org.reflections.TestModel$C2.annotations.org_reflections_TestModel$AC2.class));
    }
}

