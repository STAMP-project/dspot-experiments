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
    public void testPerson_literalMutationString1381() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person();
        p.name = "5i/E4)r>";
        Assert.assertEquals("5i/E4)r>", p.name);
        p.age = 25;
        p.smoker = true;
        String o_testPerson_literalMutationString1381__6 = new ToStringBuilder(p).append("name", p.name).append("age", p.age).append("smoker", p.smoker).toString();
        Assert.assertEquals("{\"name\":\"5i/E4)r>\",\"age\":25,\"smoker\":true}", o_testPerson_literalMutationString1381__6);
        Assert.assertEquals("5i/E4)r>", p.name);
    }

    static class NestingPerson {
        String pid;

        ToStringStyleTest.Person person;
    }
}

