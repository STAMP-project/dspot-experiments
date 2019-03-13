package com.querydsl.apt.domain;


import QQueryEmbedded7Test_Entity.entity.productRoles;
import QQueryEmbedded7Test_Entity.entity.users;
import com.querydsl.core.annotations.QueryEmbedded;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class QueryEmbedded7Test {
    @QueryEntity
    public static class Entity {
        @QueryEmbedded
        Collection<String> users;

        @QueryEmbedded
        Set<Long> productRoles;

        // misuse, but shouldn't cause problems
        @QueryEmbedded
        Locale locale;

        // misuse, but shouldn't cause problems
        @QueryEmbedded
        String string;
    }

    @Test
    public void test() {
        Assert.assertEquals(StringPath.class, users.any().getClass());
        Assert.assertEquals(NumberPath.class, productRoles.any().getClass());
    }
}

