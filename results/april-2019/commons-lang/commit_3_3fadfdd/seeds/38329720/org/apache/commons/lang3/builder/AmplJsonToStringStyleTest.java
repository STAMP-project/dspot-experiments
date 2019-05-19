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
    public void testPerson_literalMutationString1406() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person();
        p.name = "Jane Doe";
        Assert.assertEquals("Jane Doe", p.name);
        p.age = 25;
        p.smoker = true;
        String o_testPerson_literalMutationString1406__6 = new ToStringBuilder(p).append("name", p.name).append("age", p.age).append("%/-;=h", p.smoker).toString();
        Assert.assertEquals("{\"name\":\"Jane Doe\",\"age\":25,\"%/-;=h\":true}", o_testPerson_literalMutationString1406__6);
        Assert.assertEquals("Jane Doe", p.name);
    }

    @Test(timeout = 10000)
    public void testNestingPerson_literalMutationString425() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person() {
            @Override
            public String toString() {
                return new ToStringBuilder(this).append("name", this.name).append("/ge", this.age).append("smoker", this.smoker).toString();
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
        String o_testNestingPerson_literalMutationString425__20 = new ToStringBuilder(nestP).append("pid", nestP.pid).append("person", nestP.person).toString();
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"person\":{\"name\":\"Jane Doe\",\"/ge\":25,\"smoker\":true}}", o_testNestingPerson_literalMutationString425__20);
        Assert.assertEquals("Jane Doe", p.name);
        Assert.assertEquals("#1@Jane", nestP.pid);
    }

    @Test(timeout = 10000)
    public void testLANG1395_literalMutationString1773() throws Exception {
        String o_testLANG1395_literalMutationString1773__1 = new ToStringBuilder(base).append("giz/", "value").toString();
        Assert.assertEquals("{\"giz/\":\"value\"}", o_testLANG1395_literalMutationString1773__1);
        String o_testLANG1395_literalMutationString1773__4 = new ToStringBuilder(base).append("name", "").toString();
        Assert.assertEquals("{\"name\":\"\"}", o_testLANG1395_literalMutationString1773__4);
        String o_testLANG1395_literalMutationString1773__7 = new ToStringBuilder(base).append("name", '"').toString();
        Assert.assertEquals("{\"name\":\"\\\"\"}", o_testLANG1395_literalMutationString1773__7);
        String o_testLANG1395_literalMutationString1773__10 = new ToStringBuilder(base).append("name", '\\').toString();
        Assert.assertEquals("{\"name\":\"\\\\\"}", o_testLANG1395_literalMutationString1773__10);
        String o_testLANG1395_literalMutationString1773__13 = new ToStringBuilder(this.base).append("name", "Let\'s \"quote\" this").toString();
        Assert.assertEquals("{\"name\":\"Let\'s \\\"quote\\\" this\"}", o_testLANG1395_literalMutationString1773__13);
        Assert.assertEquals("{\"giz/\":\"value\"}", o_testLANG1395_literalMutationString1773__1);
        Assert.assertEquals("{\"name\":\"\"}", o_testLANG1395_literalMutationString1773__4);
        Assert.assertEquals("{\"name\":\"\\\"\"}", o_testLANG1395_literalMutationString1773__7);
        Assert.assertEquals("{\"name\":\"\\\\\"}", o_testLANG1395_literalMutationString1773__10);
    }

    static class NestingPerson {
        String pid;

        ToStringStyleTest.Person person;
    }
}

