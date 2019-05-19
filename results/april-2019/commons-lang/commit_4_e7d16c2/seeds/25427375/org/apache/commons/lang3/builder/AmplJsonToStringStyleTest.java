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
    public void testArray_literalMutationString790() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person();
        p.name = "o/ fb)Xp";
        Assert.assertEquals("o/ fb)Xp", p.name);
        p.age = 25;
        p.smoker = true;
        String o_testArray_literalMutationString790__6 = new ToStringBuilder(p).append("name", p.name).append("age", p.age).append("smoker", p.smoker).append("groups", new Object() {
            @Override
            public String toString() {
                return "['admin', 'manager', 'user']";
            }
        }).toString();
        Assert.assertEquals("{\"name\":\"o/ fb)Xp\",\"age\":25,\"smoker\":true,\"groups\":[\'admin\', \'manager\', \'user\']}", o_testArray_literalMutationString790__6);
        Assert.assertEquals("o/ fb)Xp", p.name);
    }

    static class NestingPerson {
        String pid;

        ToStringStyleTest.Person person;
    }
}

