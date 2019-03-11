package com.querydsl.apt.domain;


import QQuerySuperTypeTest_JdoEntity.jdoEntity.references;
import com.querydsl.core.annotations.QuerySupertype;
import java.util.Set;
import javax.jdo.annotations.PersistenceCapable;
import org.junit.Assert;
import org.junit.Test;


public class QuerySuperTypeTest {
    @QuerySupertype
    public static class Supertype {}

    @PersistenceCapable
    public static class JdoEntity {
        Set<QuerySuperTypeTest.Supertype> references;
    }

    @Test
    public void jdoEntity() {
        Assert.assertEquals(QQuerySuperTypeTest_Supertype.class, references.any().getClass());
    }
}

