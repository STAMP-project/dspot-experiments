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
    public void testPerson_literalMutationString144994_add146302_add153484() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person();
        p.name = "Jane Doe";
        Assert.assertEquals("Jane Doe", p.name);
        p.age = 25;
        p.smoker = true;
        ToStringBuilder o_testPerson_literalMutationString144994_add146302_add153484__6 = new ToStringBuilder(p).append("name", p.name).append("age", p.age).append("/lL((O", p.smoker);
        Assert.assertEquals("{\"name\":\"Jane Doe\",\"age\":25,\"/lL((O\":true}", ((ToStringBuilder) (o_testPerson_literalMutationString144994_add146302_add153484__6)).toString());
        Assert.assertEquals("{\"name\":\"Jane Doe\",\"age\":25,\"/lL((O\":true}", ((StringBuffer) (((ToStringBuilder) (o_testPerson_literalMutationString144994_add146302_add153484__6)).getStringBuffer())).toString());
        new ToStringBuilder(p).append("name", p.name).append("age", p.age).append("/lL((O", p.smoker).toString();
        new ToStringBuilder(p).append("name", p.name).append("age", p.age).append("/lL((O", p.smoker).toString();
        Assert.assertEquals("Jane Doe", p.name);
        Assert.assertEquals("{\"name\":\"Jane Doe\",\"age\":25,\"/lL((O\":true}}", ((ToStringBuilder) (o_testPerson_literalMutationString144994_add146302_add153484__6)).toString());
        Assert.assertEquals("{\"name\":\"Jane Doe\",\"age\":25,\"/lL((O\":true}}", ((StringBuffer) (((ToStringBuilder) (o_testPerson_literalMutationString144994_add146302_add153484__6)).getStringBuffer())).toString());
    }

    @Test(timeout = 10000)
    public void testNestingPerson_literalMutationString29593_add32859_literalMutationString36705() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person() {
            @Override
            public String toString() {
                return new ToStringBuilder(this).append("name", this.name).append("age", this.age).append("sm/oker", this.smoker).toString();
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
        ToStringBuilder o_testNestingPerson_literalMutationString29593_add32859__20 = new ToStringBuilder(nestP).append("pid", nestP.pid).append("person", nestP.person);
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"person\":{\"name\":\"Jane Doe\",\"age\":25,\"sm/oker\":true}}", ((ToStringBuilder) (o_testNestingPerson_literalMutationString29593_add32859__20)).toString());
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"person\":{\"name\":\"Jane Doe\",\"age\":25,\"sm/oker\":true}}", ((StringBuffer) (((ToStringBuilder) (o_testNestingPerson_literalMutationString29593_add32859__20)).getStringBuffer())).toString());
        new ToStringBuilder(nestP).append("(^h", nestP.pid).append("person", nestP.person).toString();
        Assert.assertEquals("Jane Doe", p.name);
        Assert.assertEquals("#1@Jane", nestP.pid);
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"person\":{\"name\":\"Jane Doe\",\"age\":25,\"sm/oker\":true}}}", ((ToStringBuilder) (o_testNestingPerson_literalMutationString29593_add32859__20)).toString());
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"person\":{\"name\":\"Jane Doe\",\"age\":25,\"sm/oker\":true}}}", ((StringBuffer) (((ToStringBuilder) (o_testNestingPerson_literalMutationString29593_add32859__20)).getStringBuffer())).toString());
    }

    @Test(timeout = 10000)
    public void testNestingPerson_literalMutationString29593_literalMutationString31056_add42541() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person() {
            @Override
            public String toString() {
                return new ToStringBuilder(this).append("name", this.name).append("age", this.age).append("sm/oker", this.smoker).toString();
            }
        };
        p.name = "Jane Doe";
        Assert.assertEquals("Jane Doe", p.name);
        p.age = 25;
        p.smoker = true;
        final AmplJsonToStringStyleTest.NestingPerson nestP = new AmplJsonToStringStyleTest.NestingPerson();
        nestP.pid = "#1@Jvane";
        Assert.assertEquals("#1@Jvane", nestP.pid);
        nestP.person = p;
        ToStringBuilder o_testNestingPerson_literalMutationString29593_literalMutationString31056_add42541__20 = new ToStringBuilder(nestP).append("pid", nestP.pid).append("person", nestP.person);
        Assert.assertEquals("{\"pid\":\"#1@Jvane\",\"person\":{\"name\":\"Jane Doe\",\"age\":25,\"sm/oker\":true}}", ((ToStringBuilder) (o_testNestingPerson_literalMutationString29593_literalMutationString31056_add42541__20)).toString());
        Assert.assertEquals("{\"pid\":\"#1@Jvane\",\"person\":{\"name\":\"Jane Doe\",\"age\":25,\"sm/oker\":true}}", ((StringBuffer) (((ToStringBuilder) (o_testNestingPerson_literalMutationString29593_literalMutationString31056_add42541__20)).getStringBuffer())).toString());
        new ToStringBuilder(nestP).append("pid", nestP.pid).append("person", nestP.person).toString();
        Assert.assertEquals("Jane Doe", p.name);
        Assert.assertEquals("#1@Jvane", nestP.pid);
        Assert.assertEquals("{\"pid\":\"#1@Jvane\",\"person\":{\"name\":\"Jane Doe\",\"age\":25,\"sm/oker\":true}}}", ((ToStringBuilder) (o_testNestingPerson_literalMutationString29593_literalMutationString31056_add42541__20)).toString());
        Assert.assertEquals("{\"pid\":\"#1@Jvane\",\"person\":{\"name\":\"Jane Doe\",\"age\":25,\"sm/oker\":true}}}", ((StringBuffer) (((ToStringBuilder) (o_testNestingPerson_literalMutationString29593_literalMutationString31056_add42541__20)).getStringBuffer())).toString());
    }

    @Test(timeout = 10000)
    public void testNestingPerson_literalMutationString29593_add32859_add42910() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person() {
            @Override
            public String toString() {
                return new ToStringBuilder(this).append("name", this.name).append("age", this.age).append("sm/oker", this.smoker).toString();
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
        ToStringBuilder o_testNestingPerson_literalMutationString29593_add32859__20 = new ToStringBuilder(nestP).append("pid", nestP.pid).append("person", nestP.person);
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"person\":{\"name\":\"Jane Doe\",\"age\":25,\"sm/oker\":true}}", ((ToStringBuilder) (o_testNestingPerson_literalMutationString29593_add32859__20)).toString());
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"person\":{\"name\":\"Jane Doe\",\"age\":25,\"sm/oker\":true}}", ((StringBuffer) (((ToStringBuilder) (o_testNestingPerson_literalMutationString29593_add32859__20)).getStringBuffer())).toString());
        new ToStringBuilder(nestP).append("pid", nestP.pid).append("person", nestP.person).toString();
        new ToStringBuilder(nestP).append("pid", nestP.pid).append("person", nestP.person).toString();
        Assert.assertEquals("Jane Doe", p.name);
        Assert.assertEquals("#1@Jane", nestP.pid);
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"person\":{\"name\":\"Jane Doe\",\"age\":25,\"sm/oker\":true}}}", ((ToStringBuilder) (o_testNestingPerson_literalMutationString29593_add32859__20)).toString());
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"person\":{\"name\":\"Jane Doe\",\"age\":25,\"sm/oker\":true}}}", ((StringBuffer) (((ToStringBuilder) (o_testNestingPerson_literalMutationString29593_add32859__20)).getStringBuffer())).toString());
    }

    @Test(timeout = 10000)
    public void testNestingPerson_literalMutationString29593_add32859_remove44053() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person() {
            @Override
            public String toString() {
                return new ToStringBuilder(this).append("name", this.name).append("age", this.age).append("sm/oker", this.smoker).toString();
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
        ToStringBuilder o_testNestingPerson_literalMutationString29593_add32859__20 = new ToStringBuilder(nestP).append("pid", nestP.pid).append("person", nestP.person);
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"person\":{\"name\":\"Jane Doe\",\"age\":25,\"sm/oker\":true}}", ((ToStringBuilder) (o_testNestingPerson_literalMutationString29593_add32859__20)).toString());
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"person\":{\"name\":\"Jane Doe\",\"age\":25,\"sm/oker\":true}}", ((StringBuffer) (((ToStringBuilder) (o_testNestingPerson_literalMutationString29593_add32859__20)).getStringBuffer())).toString());
        new ToStringBuilder(nestP).append("pid", nestP.pid).append("person", nestP.person).toString();
        Assert.assertEquals("Jane Doe", p.name);
        Assert.assertEquals("#1@Jane", nestP.pid);
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"person\":{\"name\":\"Jane Doe\",\"age\":25,\"sm/oker\":true}}}", ((ToStringBuilder) (o_testNestingPerson_literalMutationString29593_add32859__20)).toString());
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"person\":{\"name\":\"Jane Doe\",\"age\":25,\"sm/oker\":true}}}", ((StringBuffer) (((ToStringBuilder) (o_testNestingPerson_literalMutationString29593_add32859__20)).getStringBuffer())).toString());
    }

    @Test(timeout = 10000)
    public void testNestingPerson_literalMutationString29593_add32859() throws Exception {
        final ToStringStyleTest.Person p = new ToStringStyleTest.Person() {
            @Override
            public String toString() {
                return new ToStringBuilder(this).append("name", this.name).append("age", this.age).append("sm/oker", this.smoker).toString();
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
        ToStringBuilder o_testNestingPerson_literalMutationString29593_add32859__20 = new ToStringBuilder(nestP).append("pid", nestP.pid).append("person", nestP.person);
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"person\":{\"name\":\"Jane Doe\",\"age\":25,\"sm/oker\":true},", ((StringBuffer) (((ToStringBuilder) (o_testNestingPerson_literalMutationString29593_add32859__20)).getStringBuffer())).toString());
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"person\":{\"name\":\"Jane Doe\",\"age\":25,\"sm/oker\":true}}", ((ToStringBuilder) (o_testNestingPerson_literalMutationString29593_add32859__20)).toString());
        new ToStringBuilder(nestP).append("pid", nestP.pid).append("person", nestP.person).toString();
        Assert.assertEquals("Jane Doe", p.name);
        Assert.assertEquals("#1@Jane", nestP.pid);
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"person\":{\"name\":\"Jane Doe\",\"age\":25,\"sm/oker\":true}}", ((StringBuffer) (((ToStringBuilder) (o_testNestingPerson_literalMutationString29593_add32859__20)).getStringBuffer())).toString());
        Assert.assertEquals("{\"pid\":\"#1@Jane\",\"person\":{\"name\":\"Jane Doe\",\"age\":25,\"sm/oker\":true}}}", ((ToStringBuilder) (o_testNestingPerson_literalMutationString29593_add32859__20)).toString());
    }

    static class NestingPerson {
        String pid;

        ToStringStyleTest.Person person;
    }
}

