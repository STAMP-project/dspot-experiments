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
    public void testPerson_literalMutationString85602() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person();
        p.name = "Jane Doe";
        Assert.assertEquals("Jane Doe", p.name);
        p.age = 25;
        p.smoker = true;
        String o_testPerson_literalMutationString85602__6 = new ToStringBuilder(p).append("n/me", p.name).append("age", p.age).append("smoker", p.smoker).toString();
        Assert.assertEquals("{\"n/me\":\"Jane Doe\",\"age\":25,\"smoker\":true}", o_testPerson_literalMutationString85602__6);
        Assert.assertEquals("Jane Doe", p.name);
    }

    @Test(timeout = 10000)
    public void testPerson_literalMutationString85602_add86929() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person();
        p.name = "Jane Doe";
        Assert.assertEquals("Jane Doe", p.name);
        p.age = 25;
        p.smoker = true;
        ToStringBuilder o_testPerson_literalMutationString85602_add86929__6 = new ToStringBuilder(p).append("n/me", p.name).append("age", p.age);
        Assert.assertEquals("{\"n/me\":\"Jane Doe\",\"age\":25,", ((StringBuffer) (((ToStringBuilder) (o_testPerson_literalMutationString85602_add86929__6)).getStringBuffer())).toString());
        Assert.assertEquals("{\"n/me\":\"Jane Doe\",\"age\":25}", ((ToStringBuilder) (o_testPerson_literalMutationString85602_add86929__6)).toString());
        String o_testPerson_literalMutationString85602__6 = new ToStringBuilder(p).append("n/me", p.name).append("age", p.age).append("smoker", p.smoker).toString();
        Assert.assertEquals("{\"n/me\":\"Jane Doe\",\"age\":25,\"smoker\":true}", o_testPerson_literalMutationString85602__6);
        Assert.assertEquals("Jane Doe", p.name);
        Assert.assertEquals("{\"n/me\":\"Jane Doe\",\"age\":25}", ((StringBuffer) (((ToStringBuilder) (o_testPerson_literalMutationString85602_add86929__6)).getStringBuffer())).toString());
        Assert.assertEquals("{\"n/me\":\"Jane Doe\",\"age\":25}}", ((ToStringBuilder) (o_testPerson_literalMutationString85602_add86929__6)).toString());
    }

    @Test(timeout = 10000)
    public void testPerson_literalMutationString85602_literalMutationString85816() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person();
        p.name = "Jane Doe";
        Assert.assertEquals("Jane Doe", p.name);
        p.age = 25;
        p.smoker = true;
        String o_testPerson_literalMutationString85602__6 = new ToStringBuilder(p).append("n/me", p.name).append("!ge", p.age).append("smoker", p.smoker).toString();
        Assert.assertEquals("{\"n/me\":\"Jane Doe\",\"!ge\":25,\"smoker\":true}", o_testPerson_literalMutationString85602__6);
        Assert.assertEquals("Jane Doe", p.name);
    }

    @Test(timeout = 10000)
    public void testNestingPerson_literalMutationNumber24486_remove28020_literalMutationString30583() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person() {
            @Override
            public String toString() {
                return new ToStringBuilder(this).append("name", this.name).append("age", this.age).append("sm/oker", this.smoker).toString();
            }
        };
        p.name = "Jane Doe";
        Assert.assertEquals("Jane Doe", p.name);
        p.age = 24;
        p.smoker = true;
        final AmplJsonToStringStyleTest.NestingPerson nestP = new AmplJsonToStringStyleTest.NestingPerson();
        nestP.pid = "#1@Jane";
        Assert.assertEquals("#1@Jane", nestP.pid);
        nestP.person = p;
        String o_testNestingPerson_literalMutationNumber24486__21 = new ToStringBuilder(nestP).append("pid", nestP.pid).append("person", nestP.person).toString();
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"person\":{\"name\":\"Jane Doe\",\"age\":24,\"sm/oker\":true}}", o_testNestingPerson_literalMutationNumber24486__21);
        Assert.assertEquals("Jane Doe", p.name);
        Assert.assertEquals("#1@Jane", nestP.pid);
    }

    static class NestingPerson {
        String pid;

        ToStringStyleTest.Person person;
    }
}

