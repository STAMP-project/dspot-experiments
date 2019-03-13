/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.matchers.text;


import java.util.LinkedHashMap;
import org.junit.Assert;
import org.junit.Test;


public class ValuePrinterTest {
    @Test
    public void prints_values() {
        assertThat(ValuePrinter.print(null)).isEqualTo("null");
        assertThat(ValuePrinter.print("str")).isEqualTo("\"str\"");
        assertThat(ValuePrinter.print("x\ny")).isEqualTo("\"x\ny\"");
        assertThat(ValuePrinter.print(3)).isEqualTo("3");
        assertThat(ValuePrinter.print(3L)).isEqualTo("3L");
        assertThat(ValuePrinter.print(3.14)).isEqualTo("3.14d");
        assertThat(ValuePrinter.print(3.14F)).isEqualTo("3.14f");
        assertThat(ValuePrinter.print(new int[]{ 1, 2 })).isEqualTo("[1, 2]");
        assertThat(ValuePrinter.print(new LinkedHashMap<String, Object>() {
            {
                put("foo", 2L);
            }
        })).isEqualTo("{\"foo\" = 2L}");
        assertThat(ValuePrinter.print(new LinkedHashMap<String, Object>() {
            {
                put("int passed as hex", 1);
                put("byte", ((byte) (1)));
                put("short", ((short) (2)));
                put("int", 3);
                put("long", 4L);
                put("float", 2.71F);
                put("double", 3.14);
            }
        })).isEqualTo("{\"int passed as hex\" = 1, \"byte\" = (byte) 0x01, \"short\" = (short) 2, \"int\" = 3, \"long\" = 4L, \"float\" = 2.71f, \"double\" = 3.14d}");
        Assert.assertTrue(ValuePrinter.print(new ValuePrinterTest.UnsafeToString()).contains("UnsafeToString"));
        assertThat(ValuePrinter.print(new ValuePrinterTest.ToString())).isEqualTo("ToString");
        assertThat(ValuePrinter.print(new FormattedText("formatted"))).isEqualTo("formatted");
    }

    @Test
    public void prints_chars() {
        assertThat(ValuePrinter.print('a')).isEqualTo("'a'");
        assertThat(ValuePrinter.print('\n')).isEqualTo("\'\\n\'");
        assertThat(ValuePrinter.print('\t')).isEqualTo("\'\\t\'");
        assertThat(ValuePrinter.print('\r')).isEqualTo("\'\\r\'");
    }

    static class ToString {
        public String toString() {
            return "ToString";
        }
    }

    static class UnsafeToString {
        public String toString() {
            throw new RuntimeException("ka-boom!");
        }
    }
}

