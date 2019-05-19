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
    public void testLANG1395_literalMutationString1786() throws Exception {
        String o_testLANG1395_literalMutationString1786__1 = new ToStringBuilder(base).append("name", "value").toString();
        Assert.assertEquals("{\"name\":\"value\"}", o_testLANG1395_literalMutationString1786__1);
        String o_testLANG1395_literalMutationString1786__4 = new ToStringBuilder(base).append("k=k/", "").toString();
        Assert.assertEquals("{\"k=k/\":\"\"}", o_testLANG1395_literalMutationString1786__4);
        String o_testLANG1395_literalMutationString1786__7 = new ToStringBuilder(base).append("name", '"').toString();
        Assert.assertEquals("{\"name\":\"\\\"\"}", o_testLANG1395_literalMutationString1786__7);
        String o_testLANG1395_literalMutationString1786__10 = new ToStringBuilder(base).append("name", '\\').toString();
        Assert.assertEquals("{\"name\":\"\\\\\"}", o_testLANG1395_literalMutationString1786__10);
        String o_testLANG1395_literalMutationString1786__13 = new ToStringBuilder(this.base).append("name", "Let\'s \"quote\" this").toString();
        Assert.assertEquals("{\"name\":\"Let\'s \\\"quote\\\" this\"}", o_testLANG1395_literalMutationString1786__13);
        Assert.assertEquals("{\"name\":\"value\"}", o_testLANG1395_literalMutationString1786__1);
        Assert.assertEquals("{\"k=k/\":\"\"}", o_testLANG1395_literalMutationString1786__4);
        Assert.assertEquals("{\"name\":\"\\\"\"}", o_testLANG1395_literalMutationString1786__7);
        Assert.assertEquals("{\"name\":\"\\\\\"}", o_testLANG1395_literalMutationString1786__10);
    }

    static class NestingPerson {
        String pid;

        ToStringStyleTest.Person person;
    }
}

