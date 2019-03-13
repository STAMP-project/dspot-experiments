package com.querydsl.core.types;


import com.querydsl.core.types.dsl.Expressions;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class QListTest {
    @Test
    public void newInstance() {
        QList qList = new QList(Expressions.stringPath("a"), Expressions.stringPath("b"));
        List<?> list = qList.newInstance("a", null);
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("a", list.get(0));
        Assert.assertNull(list.get(1));
    }
}

