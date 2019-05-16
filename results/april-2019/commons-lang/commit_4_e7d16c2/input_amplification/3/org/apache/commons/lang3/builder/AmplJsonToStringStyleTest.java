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
    public void testNestingPerson_literalMutationString24737_remove28298_literalMutationString30572() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person() {
            @Override
            public String toString() {
                return new ToStringBuilder(this).append("nae", this.name).append("age", this.age).append("smoker", this.smoker).toString();
            }
        };
        p.name = "FWB/Z!76";
        Assert.assertEquals("FWB/Z!76", p.name);
        p.age = 25;
        p.smoker = true;
        final AmplJsonToStringStyleTest.NestingPerson nestP = new AmplJsonToStringStyleTest.NestingPerson();
        nestP.pid = "#1@Jane";
        Assert.assertEquals("#1@Jane", nestP.pid);
        nestP.person = p;
        String o_testNestingPerson_literalMutationString24737__20 = new ToStringBuilder(nestP).append("pid", nestP.pid).append("person", nestP.person).toString();
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"person\":{\"nae\":\"FWB/Z!76\",\"age\":25,\"smoker\":true}}", o_testNestingPerson_literalMutationString24737__20);
        Assert.assertEquals("FWB/Z!76", p.name);
        Assert.assertEquals("#1@Jane", nestP.pid);
    }

    @Test(timeout = 10000)
    public void testArray_literalMutationString41799() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person();
        p.name = "Jane Doe";
        Assert.assertEquals("Jane Doe", p.name);
        p.age = 25;
        p.smoker = true;
        String o_testArray_literalMutationString41799__6 = new ToStringBuilder(p).append("name", p.name).append("age", p.age).append("smoker", p.smoker).append("groups", new Object() {
            @Override
            public String toString() {
                return "3qv!4>w)|%r6/76<oD)^hwEt|E`v";
            }
        }).toString();
        Assert.assertEquals("{\"name\":\"Jane Doe\",\"age\":25,\"smoker\":true,\"groups\":\"3qv!4>w)|%r6/76<oD)^hwEt|E`v\"}", o_testArray_literalMutationString41799__6);
        Assert.assertEquals("Jane Doe", p.name);
    }

    @Test(timeout = 10000)
    public void testArray_literalMutationString41799_literalMutationString42398() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person();
        p.name = "Jane Doe";
        Assert.assertEquals("Jane Doe", p.name);
        p.age = 25;
        p.smoker = true;
        String o_testArray_literalMutationString41799__6 = new ToStringBuilder(p).append("name", p.name).append("age", p.age).append("smoker", p.smoker).append("$ucRyk", new Object() {
            @Override
            public String toString() {
                return "3qv!4>w)|%r6/76<oD)^hwEt|E`v";
            }
        }).toString();
        Assert.assertEquals("{\"name\":\"Jane Doe\",\"age\":25,\"smoker\":true,\"$ucRyk\":\"3qv!4>w)|%r6/76<oD)^hwEt|E`v\"}", o_testArray_literalMutationString41799__6);
        Assert.assertEquals("Jane Doe", p.name);
    }

    @Test(timeout = 10000)
    public void testArray_literalMutationString41799_add44349() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person();
        p.name = "Jane Doe";
        Assert.assertEquals("Jane Doe", p.name);
        p.age = 25;
        p.smoker = true;
        String o_testArray_literalMutationString41799_add44349__6 = new ToStringBuilder(p).append("name", p.name).append("age", p.age).append("smoker", p.smoker).append("groups", new Object() {
            @Override
            public String toString() {
                return "3qv!4>w)|%r6/76<oD)^hwEt|E`v";
            }
        }).toString();
        Assert.assertEquals("{\"name\":\"Jane Doe\",\"age\":25,\"smoker\":true,\"groups\":\"3qv!4>w)|%r6/76<oD)^hwEt|E`v\"}", o_testArray_literalMutationString41799_add44349__6);
        String o_testArray_literalMutationString41799__6 = new ToStringBuilder(p).append("name", p.name).append("age", p.age).append("smoker", p.smoker).append("groups", new Object() {
            @Override
            public String toString() {
                return "3qv!4>w)|%r6/76<oD)^hwEt|E`v";
            }
        }).toString();
        Assert.assertEquals("{\"name\":\"Jane Doe\",\"age\":25,\"smoker\":true,\"groups\":\"3qv!4>w)|%r6/76<oD)^hwEt|E`v\"}", o_testArray_literalMutationString41799__6);
        Assert.assertEquals("Jane Doe", p.name);
        Assert.assertEquals("{\"name\":\"Jane Doe\",\"age\":25,\"smoker\":true,\"groups\":\"3qv!4>w)|%r6/76<oD)^hwEt|E`v\"}", o_testArray_literalMutationString41799_add44349__6);
    }

    @Test(timeout = 10000)
    public void testArray_literalMutationString41799_remove44593() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person();
        p.name = "Jane Doe";
        Assert.assertEquals("Jane Doe", p.name);
        p.age = 25;
        p.smoker = true;
        String o_testArray_literalMutationString41799__6 = new ToStringBuilder(p).append("name", p.name).append("age", p.age).append("smoker", p.smoker).append("groups", new Object() {
            @Override
            public String toString() {
                return "3qv!4>w)|%r6/76<oD)^hwEt|E`v";
            }
        }).toString();
        Assert.assertEquals("{\"name\":\"Jane Doe\",\"age\":25,\"smoker\":true,\"groups\":\"3qv!4>w)|%r6/76<oD)^hwEt|E`v\"}", o_testArray_literalMutationString41799__6);
        Assert.assertEquals("Jane Doe", p.name);
    }

    @Test(timeout = 10000)
    public void testArray_literalMutationString41798_add44339_literalMutationString45452() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person();
        p.name = "Jane Doe";
        Assert.assertEquals("Jane Doe", p.name);
        p.age = 25;
        p.smoker = true;
        String o_testArray_literalMutationString41798_add44339__6 = new ToStringBuilder(p).append("name", p.name).append("age", p.age).append("smoker", p.smoker).append("groups", new Object() {
            @Override
            public String toString() {
                return "[\'admin\', \'manager\'r \'user\']";
            }
        }).toString();
        Assert.assertEquals("{\"name\":\"Jane Doe\",\"age\":25,\"smoker\":true,\"groups\":[\'admin\', \'manager\'r \'user\']}", o_testArray_literalMutationString41798_add44339__6);
        String o_testArray_literalMutationString41798__6 = new ToStringBuilder(p).append("name", p.name).append("age", p.age).append("smoker", p.smoker).append("groups", new Object() {
            @Override
            public String toString() {
                return "S&J/,y9#GX&rd0JHG42o{z_s)5$b";
            }
        }).toString();
        Assert.assertEquals("{\"name\":\"Jane Doe\",\"age\":25,\"smoker\":true,\"groups\":\"S&J/,y9#GX&rd0JHG42o{z_s)5$b\"}", o_testArray_literalMutationString41798__6);
        Assert.assertEquals("Jane Doe", p.name);
        Assert.assertEquals("{\"name\":\"Jane Doe\",\"age\":25,\"smoker\":true,\"groups\":[\'admin\', \'manager\'r \'user\']}", o_testArray_literalMutationString41798_add44339__6);
    }

    static class NestingPerson {
        String pid;

        ToStringStyleTest.Person person;
    }
}

