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
    public void testArray_literalMutationString810() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person();
        p.name = "Jane Doe";
        Assert.assertEquals("Jane Doe", p.name);
        p.age = 25;
        p.smoker = true;
        String o_testArray_literalMutationString810__6 = new ToStringBuilder(p).append("name", p.name).append("r%/", p.age).append("smoker", p.smoker).append("groups", new Object() {
            @Override
            public String toString() {
                return "['admin', 'manager', 'user']";
            }
        }).toString();
        Assert.assertEquals("{\"name\":\"Jane Doe\",\"r%/\":25,\"smoker\":true,\"groups\":[\'admin\', \'manager\', \'user\']}", o_testArray_literalMutationString810__6);
        Assert.assertEquals("Jane Doe", p.name);
    }

    @Test(timeout = 10000)
    public void testLANG1395_literalMutationString1774() throws Exception {
        String o_testLANG1395_literalMutationString1774__1 = new ToStringBuilder(base).append("/ame", "value").toString();
        Assert.assertEquals("{\"/ame\":\"value\"}", o_testLANG1395_literalMutationString1774__1);
        String o_testLANG1395_literalMutationString1774__4 = new ToStringBuilder(base).append("name", "").toString();
        Assert.assertEquals("{\"name\":\"\"}", o_testLANG1395_literalMutationString1774__4);
        String o_testLANG1395_literalMutationString1774__7 = new ToStringBuilder(base).append("name", '"').toString();
        Assert.assertEquals("{\"name\":\"\\\"\"}", o_testLANG1395_literalMutationString1774__7);
        String o_testLANG1395_literalMutationString1774__10 = new ToStringBuilder(base).append("name", '\\').toString();
        Assert.assertEquals("{\"name\":\"\\\\\"}", o_testLANG1395_literalMutationString1774__10);
        String o_testLANG1395_literalMutationString1774__13 = new ToStringBuilder(this.base).append("name", "Let\'s \"quote\" this").toString();
        Assert.assertEquals("{\"name\":\"Let\'s \\\"quote\\\" this\"}", o_testLANG1395_literalMutationString1774__13);
        Assert.assertEquals("{\"/ame\":\"value\"}", o_testLANG1395_literalMutationString1774__1);
        Assert.assertEquals("{\"name\":\"\"}", o_testLANG1395_literalMutationString1774__4);
        Assert.assertEquals("{\"name\":\"\\\"\"}", o_testLANG1395_literalMutationString1774__7);
        Assert.assertEquals("{\"name\":\"\\\\\"}", o_testLANG1395_literalMutationString1774__10);
    }

    static class NestingPerson {
        String pid;

        ToStringStyleTest.Person person;
    }
}

