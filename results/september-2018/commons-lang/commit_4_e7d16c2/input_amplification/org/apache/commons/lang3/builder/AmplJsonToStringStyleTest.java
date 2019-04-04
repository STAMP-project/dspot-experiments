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
    public void testChar_add45986_literalMutationChar46277() throws Exception {
        try {
            new ToStringBuilder(this.base).append('A').toString();
        } catch (final UnsupportedOperationException e) {
        }
        new ToStringBuilder(this.base).append("a", 'A').toString();
        ToStringBuilder o_testChar_add45986__10 = new ToStringBuilder(this.base).append("a", '\n').append("b", 'B');
        Assert.assertEquals("{\"a\":\"\n\",\"b\":\"B\",", ((StringBuffer) (((ToStringBuilder) (o_testChar_add45986__10)).getStringBuffer())).toString());
        Assert.assertEquals("{\"a\":\"\n\",\"b\":\"B\"}", ((ToStringBuilder) (o_testChar_add45986__10)).toString());
        Assert.assertEquals(5, ((int) (((ToStringBuilder) (o_testChar_add45986__10)).getObject())));
        new ToStringBuilder(this.base).append("a", 'A').append("b", 'B').toString();
        Assert.assertEquals("{\"a\":\"\n\",\"b\":\"B\"}", ((StringBuffer) (((ToStringBuilder) (o_testChar_add45986__10)).getStringBuffer())).toString());
        Assert.assertEquals("{\"a\":\"\n\",\"b\":\"B\"}}", ((ToStringBuilder) (o_testChar_add45986__10)).toString());
        Assert.assertEquals(5, ((int) (((ToStringBuilder) (o_testChar_add45986__10)).getObject())));
    }

    @Test(timeout = 10000)
    public void testChar_add45986_literalMutationChar46290() throws Exception {
        try {
            new ToStringBuilder(this.base).append('A').toString();
        } catch (final UnsupportedOperationException e) {
        }
        new ToStringBuilder(this.base).append("a", 'A').toString();
        ToStringBuilder o_testChar_add45986__10 = new ToStringBuilder(this.base).append("a", 'A').append("b", '\u0000');
        Assert.assertEquals("{\"a\":\"A\",\"b\":\"\u0000\",", ((StringBuffer) (((ToStringBuilder) (o_testChar_add45986__10)).getStringBuffer())).toString());
        Assert.assertEquals("{\"a\":\"A\",\"b\":\"\u0000\"}", ((ToStringBuilder) (o_testChar_add45986__10)).toString());
        Assert.assertEquals(5, ((int) (((ToStringBuilder) (o_testChar_add45986__10)).getObject())));
        new ToStringBuilder(this.base).append("a", 'A').append("b", 'B').toString();
        Assert.assertEquals("{\"a\":\"A\",\"b\":\"\u0000\"}", ((StringBuffer) (((ToStringBuilder) (o_testChar_add45986__10)).getStringBuffer())).toString());
        Assert.assertEquals("{\"a\":\"A\",\"b\":\"\u0000\"}}", ((ToStringBuilder) (o_testChar_add45986__10)).toString());
        Assert.assertEquals(5, ((int) (((ToStringBuilder) (o_testChar_add45986__10)).getObject())));
    }

    @Test(timeout = 10000)
    public void testChar_add45986_literalMutationChar46317() throws Exception {
        try {
            new ToStringBuilder(this.base).append('A').toString();
        } catch (final UnsupportedOperationException e) {
        }
        new ToStringBuilder(this.base).append("a", 'A').toString();
        ToStringBuilder o_testChar_add45986__10 = new ToStringBuilder(this.base).append("a", 'A').append("b", '\n');
        Assert.assertEquals("{\"a\":\"A\",\"b\":\"\n\",", ((StringBuffer) (((ToStringBuilder) (o_testChar_add45986__10)).getStringBuffer())).toString());
        Assert.assertEquals("{\"a\":\"A\",\"b\":\"\n\"}", ((ToStringBuilder) (o_testChar_add45986__10)).toString());
        Assert.assertEquals(5, ((int) (((ToStringBuilder) (o_testChar_add45986__10)).getObject())));
        new ToStringBuilder(this.base).append("a", 'A').append("b", 'B').toString();
        Assert.assertEquals("{\"a\":\"A\",\"b\":\"\n\"}", ((StringBuffer) (((ToStringBuilder) (o_testChar_add45986__10)).getStringBuffer())).toString());
        Assert.assertEquals("{\"a\":\"A\",\"b\":\"\n\"}}", ((ToStringBuilder) (o_testChar_add45986__10)).toString());
        Assert.assertEquals(5, ((int) (((ToStringBuilder) (o_testChar_add45986__10)).getObject())));
    }

    @Test(timeout = 10000)
    public void testChar_add45986_literalMutationChar46249() throws Exception {
        try {
            new ToStringBuilder(this.base).append('A').toString();
        } catch (final UnsupportedOperationException e) {
        }
        new ToStringBuilder(this.base).append("a", 'A').toString();
        ToStringBuilder o_testChar_add45986__10 = new ToStringBuilder(this.base).append("a", '\u0000').append("b", 'B');
        Assert.assertEquals("{\"a\":\"\u0000\",\"b\":\"B\",", ((StringBuffer) (((ToStringBuilder) (o_testChar_add45986__10)).getStringBuffer())).toString());
        Assert.assertEquals("{\"a\":\"\u0000\",\"b\":\"B\"}", ((ToStringBuilder) (o_testChar_add45986__10)).toString());
        Assert.assertEquals(5, ((int) (((ToStringBuilder) (o_testChar_add45986__10)).getObject())));
        new ToStringBuilder(this.base).append("a", 'A').append("b", 'B').toString();
        Assert.assertEquals("{\"a\":\"\u0000\",\"b\":\"B\"}", ((StringBuffer) (((ToStringBuilder) (o_testChar_add45986__10)).getStringBuffer())).toString());
        Assert.assertEquals("{\"a\":\"\u0000\",\"b\":\"B\"}}", ((ToStringBuilder) (o_testChar_add45986__10)).toString());
        Assert.assertEquals(5, ((int) (((ToStringBuilder) (o_testChar_add45986__10)).getObject())));
    }

    static class NestingPerson {
        String pid;

        ToStringStyleTest.Person person;
    }
}

