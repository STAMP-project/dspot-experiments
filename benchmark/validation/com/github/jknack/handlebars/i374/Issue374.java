package com.github.jknack.handlebars.i374;


import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.Options;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings({ "rawtypes", "unchecked" })
public class Issue374 extends AbstractTest {
    // This fails
    @Test
    public void test_helper__IF_EQ__with_int() throws IOException {
        HashMap map = new HashMap();
        map.put("num1", 5);
        map.put("num2", 5);
        Assert.assertEquals("True", inlineTemplate().apply(map));
    }

    // This passes
    @Test
    public void test_helper__IF_EQ__with_Integer() throws IOException {
        HashMap map = new HashMap();
        map.put("num1", new Integer(5));// netbeans says: unnecessary boxing to integer

        map.put("num2", new Integer(5));
        Assert.assertEquals("True", inlineTemplate().apply(map));
    }

    // This passes
    @Test
    public void test_helper__IF_EQ__with_int_Integer() throws IOException {
        HashMap map = new HashMap();
        map.put("num1", 5);
        map.put("num2", new Integer(5));
        Assert.assertEquals("True", inlineTemplate().apply(map));
    }

    // This fails
    @Test
    public void test_helper__IF_EQ__with_getValue_int() throws IOException {
        HashMap map = new HashMap();
        map.put("val1", new Issue374.Value(5));
        map.put("val2", new Issue374.Value(5));
        Assert.assertEquals("True", inlineTemplate_getValue().apply(map));
    }

    // This passes
    @Test
    public void test_helper__IF_EQ__with_getValue_Integer() throws IOException {
        HashMap map = new HashMap();
        map.put("val1", new Issue374.Value(new Integer(5)));
        map.put("val2", new Issue374.Value(new Integer(5)));
        Assert.assertEquals("True", inlineTemplate_getValue().apply(map));
    }

    // //////////////////////////////////////////////////////////////////////////////
    public class Value {
        private final Integer value;

        public Value(final Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
    }

    public class MyHelper {
        private double epsilon = 1.0E-6;

        public MyHelper() {
        }

        public CharSequence if_eq(final Object number1, final Object number2, final Options options) throws IOException {
            Double val1 = toDouble(number1);
            Double val2 = toDouble(number2);
            boolean cmp = ((val1 != null) && (val2 != null)) && ((compare(val1, val2)) == 0);
            return options.isFalsy(cmp) ? options.inverse() : options.fn();
        }

        protected int compare(final Double val1, final Double val2) {
            return (Math.abs(((val1 / val2) - 1))) < (epsilon) ? 0 : val1.compareTo(val2);
        }

        protected Double toDouble(final Object obj) {
            Double dbl = null;
            if (obj instanceof Double) {
                dbl = ((Double) (obj));
            }
            if (obj instanceof Integer) {
                dbl = new Double(((Integer) (obj)));
            }
            if (obj instanceof Long) {
                dbl = new Double(((Long) (obj)));
            }
            if (obj instanceof BigDecimal) {
                dbl = ((BigDecimal) (obj)).doubleValue();
            }
            if (obj instanceof Float) {
                dbl = new Double(((Float) (obj)));
            }
            if (obj instanceof String) {
                String str = ((String) (obj));
                if (str.matches("[0-9]*\\.?[0-9]+")) {
                    dbl = new Double(str);
                }
            }
            System.out.println(("Object: " + obj));
            if (obj != null) {
                System.out.println(((("Double value for " + (obj.getClass().getName())) + " : ") + dbl));
            }
            return dbl;
        }
    }
}

