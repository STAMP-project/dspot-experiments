package com.querydsl.jpa.support;


import HSQLDBTemplates.DEFAULT;
import Ops.CONCAT;
import Ops.TRIM;
import com.querydsl.core.types.Template;
import org.junit.Assert;
import org.junit.Test;


public class DialectSupportTest {
    @Test
    public void convert() {
        Template trim = DEFAULT.getTemplate(TRIM);
        Assert.assertEquals("trim(both from ?1)", DialectSupport.convert(trim));
        Template concat = DEFAULT.getTemplate(CONCAT);
        Assert.assertEquals("?1 || ?2", DialectSupport.convert(concat));
    }
}

