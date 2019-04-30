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
    public void testPerson_literalMutationString144192_literalMutationString144495_add153402() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person();
        p.name = "Jane Doe";
        Assert.assertEquals("Jane Doe", p.name);
        p.age = 25;
        p.smoker = true;
        ToStringBuilder o_testPerson_literalMutationString144192_literalMutationString144495_add153402__6 = new ToStringBuilder(p).append("name", p.name).append("Wn/8", p.age);
        Assert.assertEquals("{\"name\":\"Jane Doe\",\"Wn/8\":25,", ((StringBuffer) (((ToStringBuilder) (o_testPerson_literalMutationString144192_literalMutationString144495_add153402__6)).getStringBuffer())).toString());
        Assert.assertEquals("{\"name\":\"Jane Doe\",\"Wn/8\":25}", ((ToStringBuilder) (o_testPerson_literalMutationString144192_literalMutationString144495_add153402__6)).toString());
        new ToStringBuilder(p).append("name", p.name).append("Wn/8", p.age).append("smoker", p.smoker).toString();
        Assert.assertEquals("Jane Doe", p.name);
        Assert.assertEquals("{\"name\":\"Jane Doe\",\"Wn/8\":25}", ((StringBuffer) (((ToStringBuilder) (o_testPerson_literalMutationString144192_literalMutationString144495_add153402__6)).getStringBuffer())).toString());
        Assert.assertEquals("{\"name\":\"Jane Doe\",\"Wn/8\":25}}", ((ToStringBuilder) (o_testPerson_literalMutationString144192_literalMutationString144495_add153402__6)).toString());
    }

    static class NestingPerson {
        String pid;

        ToStringStyleTest.Person person;
    }
}

