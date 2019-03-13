package com.navercorp.pinpoint.web.dao.hbase;


import HbaseApiMetaDataDao.SPEL_KEY;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;


public class HbaseApiMetaDataDaoTest {
    @Test
    public void getApiMetaDataCachable() {
        // cacheable key - spring expression language
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("agentId", "foo");
        context.setVariable("time", ((long) (1)));
        context.setVariable("apiId", ((int) (2)));
        String key = ((String) (parser.parseExpression(SPEL_KEY).getValue(context)));
        Assert.assertEquals("foo.1.2", key);
    }
}

