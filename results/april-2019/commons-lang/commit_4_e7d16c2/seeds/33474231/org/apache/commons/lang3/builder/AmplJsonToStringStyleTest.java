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
    public void testNestingPerson_literalMutationString448() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person() {
            @Override
            public String toString() {
                return new ToStringBuilder(this).append("name", this.name).append("age", this.age).append("smoker", this.smoker).toString();
            }
        };
        p.name = "Jane Doe";
        Assert.assertEquals("Jane Doe", p.name);
        p.age = 25;
        p.smoker = true;
        final AmplJsonToStringStyleTest.NestingPerson nestP = new AmplJsonToStringStyleTest.NestingPerson();
        nestP.pid = "Z2/k+%m";
        Assert.assertEquals("Z2/k+%m", nestP.pid);
        nestP.person = p;
        String o_testNestingPerson_literalMutationString448__20 = new ToStringBuilder(nestP).append("pid", nestP.pid).append("person", nestP.person).toString();
        Assert.assertEquals("{\"pid\":\"Z2/k+%m\",\"person\":{\"name\":\"Jane Doe\",\"age\":25,\"smoker\":true}}", o_testNestingPerson_literalMutationString448__20);
        Assert.assertEquals("Jane Doe", p.name);
        Assert.assertEquals("Z2/k+%m", nestP.pid);
    }

    static class NestingPerson {
        String pid;

        ToStringStyleTest.Person person;
    }
}

