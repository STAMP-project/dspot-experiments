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
    public void testArray_literalMutationString33997_literalMutationString35324_add41750() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person();
        p.name = "%FU8un|*";
        Assert.assertEquals("%FU8un|*", p.name);
        p.age = 25;
        p.smoker = true;
        ToStringBuilder o_testArray_literalMutationString33997_literalMutationString35324_add41750__6 = new ToStringBuilder(p).append("name", p.name).append("age", p.age).append("smoker", p.smoker).append("groups", new Object() {
            @Override
            public String toString() {
                return "D:ItZbl2O_3GjAH0oLai[UL^z,E/";
            }
        });
        Assert.assertEquals("{\"name\":\"%FU8un|*\",\"age\":25,\"smoker\":true,\"groups\":\"D:ItZbl2O_3GjAH0oLai[UL^z,E/\",", ((StringBuffer) (((ToStringBuilder) (o_testArray_literalMutationString33997_literalMutationString35324_add41750__6)).getStringBuffer())).toString());
        Assert.assertEquals("{\"name\":\"%FU8un|*\",\"age\":25,\"smoker\":true,\"groups\":\"D:ItZbl2O_3GjAH0oLai[UL^z,E/\"}", ((ToStringBuilder) (o_testArray_literalMutationString33997_literalMutationString35324_add41750__6)).toString());
        new ToStringBuilder(p).append("name", p.name).append("age", p.age).append("smoker", p.smoker).append("groups", new Object() {
            @Override
            public String toString() {
                return "D:ItZbl2O_3GjAH0oLai[UL^z,E/";
            }
        }).toString();
        Assert.assertEquals("%FU8un|*", p.name);
        Assert.assertEquals("{\"name\":\"%FU8un|*\",\"age\":25,\"smoker\":true,\"groups\":\"D:ItZbl2O_3GjAH0oLai[UL^z,E/\"}", ((StringBuffer) (((ToStringBuilder) (o_testArray_literalMutationString33997_literalMutationString35324_add41750__6)).getStringBuffer())).toString());
        Assert.assertEquals("{\"name\":\"%FU8un|*\",\"age\":25,\"smoker\":true,\"groups\":\"D:ItZbl2O_3GjAH0oLai[UL^z,E/\"}}", ((ToStringBuilder) (o_testArray_literalMutationString33997_literalMutationString35324_add41750__6)).toString());
    }

    static class NestingPerson {
        String pid;

        ToStringStyleTest.Person person;
    }
}

