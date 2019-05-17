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
    public void testAppendSuper_literalMutationString64() throws Exception {
        String o_testAppendSuper_literalMutationString64__1 = new ToStringBuilder(base).appendSuper((("Integer@8888[" + (System.lineSeparator())) + "]")).toString();
        Assert.assertEquals("{}", o_testAppendSuper_literalMutationString64__1);
        String o_testAppendSuper_literalMutationString64__5 = new ToStringBuilder(base).appendSuper((((("Integer@8888[" + (System.lineSeparator())) + "  null") + (System.lineSeparator())) + "]")).toString();
        Assert.assertEquals("{}", o_testAppendSuper_literalMutationString64__5);
        String o_testAppendSuper_literalMutationString64__10 = new ToStringBuilder(base).appendSuper((("Integer@8888[" + (System.lineSeparator())) + "]")).append("a", "hello").toString();
        Assert.assertEquals("{\"a\":\"hello\"}", o_testAppendSuper_literalMutationString64__10);
        String o_testAppendSuper_literalMutationString64__15 = new ToStringBuilder(base).appendSuper((((("Integer@8888[" + (System.lineSeparator())) + "  null") + (System.lineSeparator())) + "]")).append("a", "b0/|]").toString();
        Assert.assertEquals("{\"a\":\"b0/|]\"}", o_testAppendSuper_literalMutationString64__15);
        String o_testAppendSuper_literalMutationString64__21 = new ToStringBuilder(base).appendSuper(null).append("a", "hello").toString();
        Assert.assertEquals("{\"a\":\"hello\"}", o_testAppendSuper_literalMutationString64__21);
        String o_testAppendSuper_literalMutationString64__25 = new ToStringBuilder(this.base).appendSuper("{\"a\":\"hello\"}").append("b", "world").toString();
        Assert.assertEquals("{\"a\":\"hello\",\"b\":\"world\"}", o_testAppendSuper_literalMutationString64__25);
        Assert.assertEquals("{}", o_testAppendSuper_literalMutationString64__1);
        Assert.assertEquals("{}", o_testAppendSuper_literalMutationString64__5);
        Assert.assertEquals("{\"a\":\"hello\"}", o_testAppendSuper_literalMutationString64__10);
        Assert.assertEquals("{\"a\":\"b0/|]\"}", o_testAppendSuper_literalMutationString64__15);
        Assert.assertEquals("{\"a\":\"hello\"}", o_testAppendSuper_literalMutationString64__21);
    }

    @Test(timeout = 10000)
    public void testArray_literalMutationString41261() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person();
        p.name = "vmz|//{`";
        Assert.assertEquals("vmz|//{`", p.name);
        p.age = 25;
        p.smoker = true;
        String o_testArray_literalMutationString41261__6 = new ToStringBuilder(p).append("name", p.name).append("age", p.age).append("smoker", p.smoker).append("groups", new Object() {
            @Override
            public String toString() {
                return "['admin', 'manager', 'user']";
            }
        }).toString();
        Assert.assertEquals("{\"name\":\"vmz|//{`\",\"age\":25,\"smoker\":true,\"groups\":[\'admin\', \'manager\', \'user\']}", o_testArray_literalMutationString41261__6);
        Assert.assertEquals("vmz|//{`", p.name);
    }

    static class NestingPerson {
        String pid;

        ToStringStyleTest.Person person;
    }
}

