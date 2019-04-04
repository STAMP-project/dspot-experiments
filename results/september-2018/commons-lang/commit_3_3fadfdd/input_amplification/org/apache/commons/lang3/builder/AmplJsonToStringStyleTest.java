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
    public void testNestingPerson_add33752_add35496_literalMutationString50895() throws Exception {
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
        nestP.pid = "#1@Jane";
        Assert.assertEquals("#1@Jane", nestP.pid);
        nestP.person = p;
        ToStringBuilder o_testNestingPerson_add33752_add35496__20 = new ToStringBuilder(nestP).append("pid", nestP.pid).append("person", nestP.person);
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"person\":{\"name\":\"Jane Doe\",\"age\":25,\"smoker\":true},", ((StringBuffer) (((ToStringBuilder) (o_testNestingPerson_add33752_add35496__20)).getStringBuffer())).toString());
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"person\":{\"name\":\"Jane Doe\",\"age\":25,\"smoker\":true}}", ((ToStringBuilder) (o_testNestingPerson_add33752_add35496__20)).toString());
        ToStringBuilder o_testNestingPerson_add33752__20 = 
            new ToStringBuilder(nestP).append("pid", nestP.pid).append("per/on", nestP.person);
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"per/on\":{\"name\":\"Jane Doe\",\"age\":25,\"smoker\":true},", ((StringBuffer) (((ToStringBuilder) (o_testNestingPerson_add33752__20)).getStringBuffer())).toString());
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"per/on\":{\"name\":\"Jane Doe\",\"age\":25,\"smoker\":true}}", ((ToStringBuilder) (o_testNestingPerson_add33752__20)).toString());
        new ToStringBuilder(nestP).append("pid", nestP.pid).append("person", nestP.person).toString();
        Assert.assertEquals("Jane Doe", p.name);
        Assert.assertEquals("#1@Jane", nestP.pid);
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"person\":{\"name\":\"Jane Doe\",\"age\":25,\"smoker\":true}}", ((StringBuffer) (((ToStringBuilder) (o_testNestingPerson_add33752_add35496__20)).getStringBuffer())).toString());
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"person\":{\"name\":\"Jane Doe\",\"age\":25,\"smoker\":true}}}", ((ToStringBuilder) (o_testNestingPerson_add33752_add35496__20)).toString());
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"per/on\":{\"name\":\"Jane Doe\",\"age\":25,\"smoker\":true}}", ((StringBuffer) (((ToStringBuilder) (o_testNestingPerson_add33752__20)).getStringBuffer())).toString());
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"per/on\":{\"name\":\"Jane Doe\",\"age\":25,\"smoker\":true}}}", ((ToStringBuilder) (o_testNestingPerson_add33752__20)).toString());
    }

    @Test(timeout = 10000)
    public void testArray_add52734_literalMutationString55629() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person();
        p.name = "Jane Doe";
        Assert.assertEquals("Jane Doe", p.name);
        p.age = 25;
        p.smoker = true;
        ToStringBuilder o_testArray_add52734__6 = new ToStringBuilder(p).append("&P/T", p.name).append("age", p.age).append("smoker", p.smoker).append("groups", new Object() {
            @Override
            public String toString() {
                return "['admin', 'manager', 'user']";
            }
        });
        Assert.assertEquals("{\"&P/T\":\"Jane Doe\",\"age\":25,\"smoker\":true,\"groups\":[\'admin\', \'manager\', \'user\'],", ((StringBuffer) (((ToStringBuilder) (o_testArray_add52734__6)).getStringBuffer())).toString());
        Assert.assertEquals("{\"&P/T\":\"Jane Doe\",\"age\":25,\"smoker\":true,\"groups\":[\'admin\', \'manager\', \'user\']}", ((ToStringBuilder) (o_testArray_add52734__6)).toString());
        new ToStringBuilder(p).append("name", p.name).append("age", p.age).append("smoker", p.smoker).append("groups", new Object() {
            @Override
            public String toString() {
                return "['admin', 'manager', 'user']";
            }
        }).toString();
        Assert.assertEquals("Jane Doe", p.name);
        Assert.assertEquals("{\"&P/T\":\"Jane Doe\",\"age\":25,\"smoker\":true,\"groups\":[\'admin\', \'manager\', \'user\']}", ((StringBuffer) (((ToStringBuilder) (o_testArray_add52734__6)).getStringBuffer())).toString());
        Assert.assertEquals("{\"&P/T\":\"Jane Doe\",\"age\":25,\"smoker\":true,\"groups\":[\'admin\', \'manager\', \'user\']}}", ((ToStringBuilder) (o_testArray_add52734__6)).toString());
    }

    @Test(timeout = 10000)
    public void testArray_add52734_add55808_literalMutationString72975() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person();
        p.name = "Jane Doe";
        Assert.assertEquals("Jane Doe", p.name);
        p.age = 25;
        p.smoker = true;
        ToStringBuilder o_testArray_add52734__6 = new ToStringBuilder(p).append("8Pv/", p.name).append("age", p.age).append("smoker", p.smoker).append("groups", new Object() {
            @Override
            public String toString() {
                return "['admin', 'manager', 'user']";
            }
        });
        Assert.assertEquals("{\"8Pv/\":\"Jane Doe\",\"age\":25,\"smoker\":true,\"groups\":[\'admin\', \'manager\', \'user\'],", ((StringBuffer) (((ToStringBuilder) (o_testArray_add52734__6)).getStringBuffer())).toString());
        Assert.assertEquals("{\"8Pv/\":\"Jane Doe\",\"age\":25,\"smoker\":true,\"groups\":[\'admin\', \'manager\', \'user\']}", ((ToStringBuilder) (o_testArray_add52734__6)).toString());
        ToStringBuilder o_testArray_add52734_add55808__19 = new ToStringBuilder(p).append("name", p.name).append("age", p.age).append("smoker", p.smoker).append("groups", new Object() {
            @Override
            public String toString() {
                return "['admin', 'manager', 'user']";
            }
        });
        Assert.assertEquals("{\"name\":\"Jane Doe\",\"age\":25,\"smoker\":true,\"groups\":[\'admin\', \'manager\', \'user\'],", ((StringBuffer) (((ToStringBuilder) (o_testArray_add52734_add55808__19)).getStringBuffer())).toString());
        Assert.assertEquals("{\"name\":\"Jane Doe\",\"age\":25,\"smoker\":true,\"groups\":[\'admin\', \'manager\', \'user\']}", ((ToStringBuilder) (o_testArray_add52734_add55808__19)).toString());
        new ToStringBuilder(p).append("name", p.name).append("age", p.age).append("smoker", p.smoker).append("groups", new Object() {
            @Override
            public String toString() {
                return "['admin', 'manager', 'user']";
            }
        }).toString();
        Assert.assertEquals("Jane Doe", p.name);
        Assert.assertEquals("{\"8Pv/\":\"Jane Doe\",\"age\":25,\"smoker\":true,\"groups\":[\'admin\', \'manager\', \'user\']}", ((StringBuffer) (((ToStringBuilder) (o_testArray_add52734__6)).getStringBuffer())).toString());
        Assert.assertEquals("{\"8Pv/\":\"Jane Doe\",\"age\":25,\"smoker\":true,\"groups\":[\'admin\', \'manager\', \'user\']}}", ((ToStringBuilder) (o_testArray_add52734__6)).toString());
        Assert.assertEquals("{\"name\":\"Jane Doe\",\"age\":25,\"smoker\":true,\"groups\":[\'admin\', \'manager\', \'user\']}", ((StringBuffer) (((ToStringBuilder) (o_testArray_add52734_add55808__19)).getStringBuffer())).toString());
        Assert.assertEquals("{\"name\":\"Jane Doe\",\"age\":25,\"smoker\":true,\"groups\":[\'admin\', \'manager\', \'user\']}}", ((ToStringBuilder) (o_testArray_add52734_add55808__19)).toString());
    }

    static class NestingPerson {
        String pid;

        ToStringStyleTest.Person person;
    }
}

