package org.hamcrest;


import org.junit.Assert;
import org.junit.Test;


public final class BaseDescriptionTest {
    private final StringBuilder result = new StringBuilder();

    private final BaseDescription baseDescription = new BaseDescription() {
        @Override
        protected void append(char c) {
            result.append(c);
        }
    };

    @Test
    public void describesAppendedNullValue() {
        baseDescription.appendValue(null);
        Assert.assertEquals("null", result.toString());
    }

    @Test
    public void quotesAppendedStringValue() {
        baseDescription.appendValue("foo");
        Assert.assertEquals("\"foo\"", result.toString());
    }

    @Test
    public void quotesAppendedCharacterValue() {
        baseDescription.appendValue('f');
        Assert.assertEquals("\"f\"", result.toString());
    }

    @Test
    public void quotesAppendedTab() {
        baseDescription.appendValue('\t');
        Assert.assertEquals("\"\\t\"", result.toString());
    }

    @Test
    public void quotesAppendedNewLine() {
        baseDescription.appendValue('\n');
        Assert.assertEquals("\"\\n\"", result.toString());
    }

    @Test
    public void quotesAppendedLineReturn() {
        baseDescription.appendValue('\r');
        Assert.assertEquals("\"\\r\"", result.toString());
    }

    @Test
    public void quotesAppendedBackslash() {
        baseDescription.appendValue('\\');
        Assert.assertEquals("\"\\\\\"", result.toString());
    }

    @Test
    public void quotesAppendedDoubleQuotes() {
        baseDescription.appendValue('"');
        Assert.assertEquals("\"\\\"\"", result.toString());
    }

    @Test
    public void bracketsAppendedShortValue() {
        baseDescription.appendValue(Short.valueOf("2"));
        Assert.assertEquals("<2s>", result.toString());
    }

    @Test
    public void bracketsAppendedLongValue() {
        baseDescription.appendValue(Long.valueOf("2"));
        Assert.assertEquals("<2L>", result.toString());
    }

    @Test
    public void bracketsAppendedFloatValue() {
        baseDescription.appendValue(Float.valueOf("1.2"));
        Assert.assertEquals("<1.2F>", result.toString());
    }

    @Test
    public void describesAppendedArrayValue() {
        baseDescription.appendValue(new String[]{ "2", "3" });
        Assert.assertEquals("[\"2\", \"3\"]", result.toString());
    }

    @Test
    public void bracketsAppendedObjectValue() {
        final Object value = new Object();
        baseDescription.appendValue(value);
        Assert.assertEquals((("<" + (value.toString())) + ">"), result.toString());
    }

    @Test
    public void safelyDescribesAppendedValueOfObjectWhoseToStringThrowsAnException() {
        final Object value = new Object() {
            @Override
            public String toString() {
                throw new UnsupportedOperationException();
            }
        };
        final String expected = ((value.getClass().getName()) + "@") + (Integer.toHexString(value.hashCode()));
        baseDescription.appendValue(value);
        Assert.assertEquals((("<" + expected) + ">"), result.toString());
    }
}

