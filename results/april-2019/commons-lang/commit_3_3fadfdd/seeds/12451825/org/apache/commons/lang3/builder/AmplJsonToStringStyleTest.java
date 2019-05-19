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
    public void testNestingPerson_literalMutationString414() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person() {
            @Override
            public String toString() {
                return new ToStringBuilder(this).append("O/CG", this.name).append("age", this.age).append("smoker", this.smoker).toString();
            }
        };
        p.name = "Jane Doe";
        Assert.assertEquals("Jane Doe", p.name);
        p.age = 25;
        p.smoker = true;
        final AmplJsonToStringStyleTest.NestingPerson nestP = new AmplJsonToStringStyleTest.NestingPerson();
        nestP.pid = "#1@Jane";
        Assert.assertEquals("#1@Jane", nestP.pid);
        nestP.person = p;
        String o_testNestingPerson_literalMutationString414__20 = new ToStringBuilder(nestP).append("pid", nestP.pid).append("person", nestP.person).toString();
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"person\":{\"O/CG\":\"Jane Doe\",\"age\":25,\"smoker\":true}}", o_testNestingPerson_literalMutationString414__20);
        Assert.assertEquals("Jane Doe", p.name);
        Assert.assertEquals("#1@Jane", nestP.pid);
    }

    @Test(timeout = 10000)
    public void testLANG1395_literalMutationString1819() throws Exception {
        String o_testLANG1395_literalMutationString1819__1 = new ToStringBuilder(base).append("name", "value").toString();
        Assert.assertEquals("{\"name\":\"value\"}", o_testLANG1395_literalMutationString1819__1);
        String o_testLANG1395_literalMutationString1819__4 = new ToStringBuilder(base).append("name", "").toString();
        Assert.assertEquals("{\"name\":\"\"}", o_testLANG1395_literalMutationString1819__4);
        String o_testLANG1395_literalMutationString1819__7 = new ToStringBuilder(base).append("name", '"').toString();
        Assert.assertEquals("{\"name\":\"\\\"\"}", o_testLANG1395_literalMutationString1819__7);
        String o_testLANG1395_literalMutationString1819__10 = new ToStringBuilder(base).append("name", '\\').toString();
        Assert.assertEquals("{\"name\":\"\\\\\"}", o_testLANG1395_literalMutationString1819__10);
        String o_testLANG1395_literalMutationString1819__13 = new ToStringBuilder(this.base).append("/ame", "Let\'s \"quote\" this").toString();
        Assert.assertEquals("{\"/ame\":\"Let\'s \\\"quote\\\" this\"}", o_testLANG1395_literalMutationString1819__13);
        Assert.assertEquals("{\"name\":\"value\"}", o_testLANG1395_literalMutationString1819__1);
        Assert.assertEquals("{\"name\":\"\"}", o_testLANG1395_literalMutationString1819__4);
        Assert.assertEquals("{\"name\":\"\\\"\"}", o_testLANG1395_literalMutationString1819__7);
        Assert.assertEquals("{\"name\":\"\\\\\"}", o_testLANG1395_literalMutationString1819__10);
    }

    @Test(timeout = 10000)
    public void testLANG1395_literalMutationString1786() throws Exception {
        String o_testLANG1395_literalMutationString1786__1 = new ToStringBuilder(base).append("name", "value").toString();
        Assert.assertEquals("{\"name\":\"value\"}", o_testLANG1395_literalMutationString1786__1);
        String o_testLANG1395_literalMutationString1786__4 = new ToStringBuilder(base).append("na/me", "").toString();
        Assert.assertEquals("{\"na/me\":\"\"}", o_testLANG1395_literalMutationString1786__4);
        String o_testLANG1395_literalMutationString1786__7 = new ToStringBuilder(base).append("name", '"').toString();
        Assert.assertEquals("{\"name\":\"\\\"\"}", o_testLANG1395_literalMutationString1786__7);
        String o_testLANG1395_literalMutationString1786__10 = new ToStringBuilder(base).append("name", '\\').toString();
        Assert.assertEquals("{\"name\":\"\\\\\"}", o_testLANG1395_literalMutationString1786__10);
        String o_testLANG1395_literalMutationString1786__13 = new ToStringBuilder(this.base).append("name", "Let\'s \"quote\" this").toString();
        Assert.assertEquals("{\"name\":\"Let\'s \\\"quote\\\" this\"}", o_testLANG1395_literalMutationString1786__13);
        Assert.assertEquals("{\"name\":\"value\"}", o_testLANG1395_literalMutationString1786__1);
        Assert.assertEquals("{\"na/me\":\"\"}", o_testLANG1395_literalMutationString1786__4);
        Assert.assertEquals("{\"name\":\"\\\"\"}", o_testLANG1395_literalMutationString1786__7);
        Assert.assertEquals("{\"name\":\"\\\\\"}", o_testLANG1395_literalMutationString1786__10);
    }

    static class NestingPerson {
        String pid;

        ToStringStyleTest.Person person;
    }
}

