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
    public void testArray_literalMutationString57926_add60550() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person();
        p.name = "<;o/V.8S";
        Assert.assertEquals("<;o/V.8S", p.name);
        p.age = 25;
        p.smoker = true;
        ToStringBuilder o_testArray_literalMutationString57926_add60550__6 = new ToStringBuilder(p).append("name", p.name).append("age", p.age).append("smoker", p.smoker).append("groups", new Object() {
            @Override
            public String toString() {
                return "['admin', 'manager', 'user']";
            }
        });
        Assert.assertEquals("{\"name\":\"<;o/V.8S\",\"age\":25,\"smoker\":true,\"groups\":[\'admin\', \'manager\', \'user\'],", ((StringBuffer) (((ToStringBuilder) (o_testArray_literalMutationString57926_add60550__6)).getStringBuffer())).toString());
        Assert.assertEquals("{\"name\":\"<;o/V.8S\",\"age\":25,\"smoker\":true,\"groups\":[\'admin\', \'manager\', \'user\']}", ((ToStringBuilder) (o_testArray_literalMutationString57926_add60550__6)).toString());
        new ToStringBuilder(p).append("name", p.name).append("age", p.age).append("smoker", p.smoker).append("groups", new Object() {
            @Override
            public String toString() {
                return "['admin', 'manager', 'user']";
            }
        }).toString();
        Assert.assertEquals("<;o/V.8S", p.name);
        Assert.assertEquals("{\"name\":\"<;o/V.8S\",\"age\":25,\"smoker\":true,\"groups\":[\'admin\', \'manager\', \'user\']}", ((StringBuffer) (((ToStringBuilder) (o_testArray_literalMutationString57926_add60550__6)).getStringBuffer())).toString());
        Assert.assertEquals("{\"name\":\"<;o/V.8S\",\"age\":25,\"smoker\":true,\"groups\":[\'admin\', \'manager\', \'user\']}}", ((ToStringBuilder) (o_testArray_literalMutationString57926_add60550__6)).toString());
    }

    static class NestingPerson {
        String pid;

        ToStringStyleTest.Person person;
    }
}

