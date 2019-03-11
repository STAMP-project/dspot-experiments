package com.querydsl.jpa;


import EclipseLinkTemplates.DEFAULT;
import org.junit.Assert;
import org.junit.Test;


public class QueryHandlerTest {
    @Test
    public void types() {
        Assert.assertEquals(EclipseLinkHandler.class, DEFAULT.getQueryHandler().getClass());
        Assert.assertEquals(HibernateHandler.class, HQLTemplates.DEFAULT.getQueryHandler().getClass());
        Assert.assertEquals(DefaultQueryHandler.class, JPQLTemplates.DEFAULT.getQueryHandler().getClass());
    }
}

