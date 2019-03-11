package com.querydsl.codegen;


import com.mysema.codegen.model.Type;
import com.querydsl.core.annotations.QueryEntity;
import java.io.File;
import java.lang.reflect.Field;
import org.junit.Assert;
import org.junit.Test;


public class Inheritance2Test {
    @QueryEntity
    public abstract class Base<T extends Inheritance2Test.Base<T>> {
        @SuppressWarnings("unchecked")
        Inheritance2Test.Base2 base;

        Inheritance2Test.Base2<?, ?> base2;
    }

    @QueryEntity
    public abstract class Base2<T extends Inheritance2Test.Base2<T, U>, U extends Inheritance2Test.IFace> {}

    @QueryEntity
    public abstract class BaseSub extends Inheritance2Test.Base<Inheritance2Test.BaseSub> {}

    @QueryEntity
    public abstract class BaseSub2<T extends Inheritance2Test.BaseSub2<T>> extends Inheritance2Test.Base<T> {}

    @QueryEntity
    public abstract class Base2Sub<T extends Inheritance2Test.IFace> extends Inheritance2Test.Base2<Inheritance2Test.Base2Sub<T>, T> {}

    public interface IFace {}

    @Test
    public void base_base() throws NoSuchFieldException, SecurityException {
        TypeFactory typeFactory = new TypeFactory();
        Field field = Inheritance2Test.Base.class.getDeclaredField("base");
        Type type = typeFactory.get(field.getType(), field.getGenericType());
        Assert.assertEquals(0, type.getParameters().size());
    }

    @Test
    public void base_base2() throws NoSuchFieldException, SecurityException {
        TypeFactory typeFactory = new TypeFactory();
        Field field = Inheritance2Test.Base.class.getDeclaredField("base2");
        Type type = typeFactory.get(field.getType(), field.getGenericType());
        Assert.assertEquals(2, type.getParameters().size());
        Assert.assertNull(getVarName());
        Assert.assertNull(getVarName());
    }

    @Test
    public void test() {
        GenericExporter exporter = new GenericExporter();
        exporter.setTargetFolder(new File(("target/" + (getClass().getSimpleName()))));
        exporter.export(getClass().getClasses());
    }
}

