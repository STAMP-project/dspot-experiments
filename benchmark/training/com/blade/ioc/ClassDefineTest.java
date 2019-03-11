package com.blade.ioc;


import com.blade.ioc.annotation.Bean;
import com.blade.ioc.bean.ClassDefine;
import com.blade.types.BladeClassDefineType;
import com.blade.types.BladeWebHookType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author biezhi
 * @unknown 2017/9/19
 */
public class ClassDefineTest {
    @Test
    public void testClassDefine() {
        ClassDefine classDefine = ClassDefine.create(BladeClassDefineType.class);
        int modifires = classDefine.getModifiers();
        Field[] fields = classDefine.getDeclaredFields();
        Bean bean = classDefine.getAnnotation(Bean.class);
        Annotation[] annotations = classDefine.getAnnotations();
        List<ClassDefine> interfaces = classDefine.getInterfaces();
        String name = classDefine.getName();
        String simpleName = classDefine.getSimpleName();
        ClassDefine superKlass = classDefine.getSuperKlass();
        Class<?> type = classDefine.getType();
        Assert.assertEquals(Modifier.PUBLIC, modifires);
        Assert.assertEquals(2, fields.length);
        Assert.assertNotNull(bean);
        Assert.assertEquals(1, annotations.length);
        Assert.assertEquals(0, interfaces.size());
        Assert.assertEquals("com.blade.types.BladeClassDefineType", name);
        Assert.assertEquals("BladeClassDefineType", simpleName);
        Assert.assertEquals(Object.class, superKlass.getType());
        Assert.assertEquals(BladeClassDefineType.class, type);
        Assert.assertEquals(false, classDefine.isAbstract());
        Assert.assertEquals(false, classDefine.isInterface());
        Assert.assertEquals(false, classDefine.isStatic());
        Assert.assertEquals(true, classDefine.isPublic());
        Assert.assertEquals(false, classDefine.isPrivate());
        Assert.assertEquals(false, classDefine.isProtected());
        Assert.assertEquals(1, ClassDefine.create(BladeWebHookType.class).getInterfaces().size());
        Assert.assertEquals(1, ClassDefine.create(BladeWebHookType.class).getInterfaces().size());
    }
}

