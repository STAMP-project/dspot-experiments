package org.apache.commons.lang3.builder;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class AmplJsonToStringStyleTest {
    private final Integer base = Integer.valueOf(5);

    @Before
    public void setUp() throws Exception {
        ToStringBuilder.setDefaultStyle(ToStringStyle.JSON_STYLE);
    }

    @After
    public void tearDown() throws Exception {
        ToStringBuilder.setDefaultStyle(ToStringStyle.DEFAULT_STYLE);
    }

    @Test(timeout = 10000)
    public void testAppendSuper_literalMutationString41() throws Exception {
        String o_testAppendSuper_literalMutationString41__1 = new ToStringBuilder(base).appendSuper((("Integer@8888[" + (System.lineSeparator())) + "]")).toString();
        Assert.assertEquals("{}", o_testAppendSuper_literalMutationString41__1);
        String o_testAppendSuper_literalMutationString41__5 = new ToStringBuilder(base).appendSuper((((("Integer@8888[" + (System.lineSeparator())) + "  null") + (System.lineSeparator())) + "]")).toString();
        Assert.assertEquals("{}", o_testAppendSuper_literalMutationString41__5);
        String o_testAppendSuper_literalMutationString41__10 = new ToStringBuilder(base).appendSuper((("Integer@8888[" + (System.lineSeparator())) + "]")).append("a", "zn ./").toString();
        Assert.assertEquals("{\"a\":\"zn ./\"}", o_testAppendSuper_literalMutationString41__10);
        String o_testAppendSuper_literalMutationString41__15 = new ToStringBuilder(base).appendSuper((((("Integer@8888[" + (System.lineSeparator())) + "  null") + (System.lineSeparator())) + "]")).append("a", "hello").toString();
        Assert.assertEquals("{\"a\":\"hello\"}", o_testAppendSuper_literalMutationString41__15);
        String o_testAppendSuper_literalMutationString41__21 = new ToStringBuilder(base).appendSuper(null).append("a", "hello").toString();
        Assert.assertEquals("{\"a\":\"hello\"}", o_testAppendSuper_literalMutationString41__21);
        String o_testAppendSuper_literalMutationString41__25 = new ToStringBuilder(this.base).appendSuper("{\"a\":\"hello\"}").append("b", "world").toString();
        Assert.assertEquals("{\"a\":\"hello\",\"b\":\"world\"}", o_testAppendSuper_literalMutationString41__25);
        Assert.assertEquals("{}", o_testAppendSuper_literalMutationString41__1);
        Assert.assertEquals("{}", o_testAppendSuper_literalMutationString41__5);
        Assert.assertEquals("{\"a\":\"zn ./\"}", o_testAppendSuper_literalMutationString41__10);
        Assert.assertEquals("{\"a\":\"hello\"}", o_testAppendSuper_literalMutationString41__15);
        Assert.assertEquals("{\"a\":\"hello\"}", o_testAppendSuper_literalMutationString41__21);
    }

    static class NestingPerson {
        String pid;

        ToStringStyleTest.Person person;
    }
}

