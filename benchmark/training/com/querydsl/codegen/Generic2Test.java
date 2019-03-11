package com.querydsl.codegen;


import com.mysema.codegen.model.Type;
import com.querydsl.core.annotations.QueryEntity;
import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class Generic2Test {
    @QueryEntity
    public static class AbstractCollectionAttribute<T extends Collection<?>> {
        T value;
    }

    @QueryEntity
    public static class ListAttribute<T> extends Generic2Test.AbstractCollectionAttribute<List<T>> {
        String name;
    }

    @QueryEntity
    public static class Product {
        Generic2Test.ListAttribute<Integer> integerAttributes;

        Generic2Test.ListAttribute<String> stringAttributes;
    }

    @Test
    public void resolve() {
        TypeFactory factory = new TypeFactory(Collections.<Class<? extends Annotation>>emptyList());
        Type type = factory.get(Generic2Test.AbstractCollectionAttribute.class);
        Assert.assertEquals("com.querydsl.codegen.Generic2Test.AbstractCollectionAttribute", type.getGenericName(false));
        Assert.assertEquals("com.querydsl.codegen.Generic2Test.AbstractCollectionAttribute", type.getGenericName(true));
    }

    @Test
    public void resolve2() {
        TypeFactory factory = new TypeFactory(Collections.<Class<? extends Annotation>>emptyList());
        Type type = factory.getEntityType(Generic2Test.AbstractCollectionAttribute.class);
        Assert.assertEquals("com.querydsl.codegen.Generic2Test.AbstractCollectionAttribute<? extends java.util.Collection<?>>", type.getGenericName(false));
        Assert.assertEquals("com.querydsl.codegen.Generic2Test.AbstractCollectionAttribute<? extends java.util.Collection<?>>", type.getGenericName(true));
    }

    @Test
    public void export() {
        GenericExporter exporter = new GenericExporter();
        exporter.setTargetFolder(new File("target/Generic2Test"));
        exporter.export(Generic2Test.class.getClasses());
    }
}

