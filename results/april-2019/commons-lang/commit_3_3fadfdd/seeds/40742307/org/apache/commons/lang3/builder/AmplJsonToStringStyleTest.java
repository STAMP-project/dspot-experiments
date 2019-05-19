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
    public void testNestingPerson_literalMutationString430() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person() {
            @Override
            public String toString() {
                return new ToStringBuilder(this).append("name", this.name).append("age", this.age).append("d/=*Yd", this.smoker).toString();
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
        String o_testNestingPerson_literalMutationString430__20 = new ToStringBuilder(nestP).append("pid", nestP.pid).append("person", nestP.person).toString();
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"person\":{\"name\":\"Jane Doe\",\"age\":25,\"d/=*Yd\":true}}", o_testNestingPerson_literalMutationString430__20);
        Assert.assertEquals("Jane Doe", p.name);
        Assert.assertEquals("#1@Jane", nestP.pid);
    }

    @Test(timeout = 10000)
    public void testLANG1395_literalMutationString1808() throws Exception {
        String o_testLANG1395_literalMutationString1808__1 = new ToStringBuilder(base).append("name", "value").toString();
        Assert.assertEquals("{\"name\":\"value\"}", o_testLANG1395_literalMutationString1808__1);
        String o_testLANG1395_literalMutationString1808__4 = new ToStringBuilder(base).append("name", "").toString();
        Assert.assertEquals("{\"name\":\"\"}", o_testLANG1395_literalMutationString1808__4);
        String o_testLANG1395_literalMutationString1808__7 = new ToStringBuilder(base).append("name", '"').toString();
        Assert.assertEquals("{\"name\":\"\\\"\"}", o_testLANG1395_literalMutationString1808__7);
        String o_testLANG1395_literalMutationString1808__10 = new ToStringBuilder(base).append("na/me", '\\').toString();
        Assert.assertEquals("{\"na/me\":\"\\\\\"}", o_testLANG1395_literalMutationString1808__10);
        String o_testLANG1395_literalMutationString1808__13 = new ToStringBuilder(this.base).append("name", "Let\'s \"quote\" this").toString();
        Assert.assertEquals("{\"name\":\"Let\'s \\\"quote\\\" this\"}", o_testLANG1395_literalMutationString1808__13);
        Assert.assertEquals("{\"name\":\"value\"}", o_testLANG1395_literalMutationString1808__1);
        Assert.assertEquals("{\"name\":\"\"}", o_testLANG1395_literalMutationString1808__4);
        Assert.assertEquals("{\"name\":\"\\\"\"}", o_testLANG1395_literalMutationString1808__7);
        Assert.assertEquals("{\"na/me\":\"\\\\\"}", o_testLANG1395_literalMutationString1808__10);
    }

    static class NestingPerson {
        String pid;

        ToStringStyleTest.Person person;
    }
}

