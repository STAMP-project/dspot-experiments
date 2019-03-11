package com.querydsl.collections;


import com.google.common.collect.Lists;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimplePath;
import java.util.List;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class MockTest {
    @Test
    public void test() {
        List<MockTest> tests = Lists.newArrayList(new MockTest(), new MockTest(), new MockTest());
        SimplePath<MockTest> path = Expressions.path(MockTest.class, "obj");
        MockTest mock = EasyMock.createMock(MockTest.class);
        Assert.assertTrue(CollQueryFactory.from(path, tests).where(path.eq(mock)).fetch().isEmpty());
    }
}

