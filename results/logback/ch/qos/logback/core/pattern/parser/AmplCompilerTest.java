

package ch.qos.logback.core.pattern.parser;


public class AmplCompilerTest {
    java.util.Map<java.lang.String, java.lang.String> converterMap = new java.util.HashMap<java.lang.String, java.lang.String>();

    ch.qos.logback.core.Context context = new ch.qos.logback.core.ContextBase();

    @org.junit.Before
    public void setUp() {
        converterMap.put("OTT", ch.qos.logback.core.pattern.Converter123.class.getName());
        converterMap.put("hello", ch.qos.logback.core.pattern.ConverterHello.class.getName());
        converterMap.putAll(ch.qos.logback.core.pattern.parser.Parser.DEFAULT_COMPOSITE_CONVERTER_MAP);
    }

    java.lang.String write(final ch.qos.logback.core.pattern.Converter<java.lang.Object> head, java.lang.Object event) {
        java.lang.StringBuilder buf = new java.lang.StringBuilder();
        ch.qos.logback.core.pattern.Converter<java.lang.Object> c = head;
        while (c != null) {
            c.write(buf, event);
            c = c.getNext();
        } 
        return buf.toString();
    }

    @org.junit.Test
    public void testLiteral() throws java.lang.Exception {
        ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("hello");
        ch.qos.logback.core.pattern.parser.Node t = p.parse();
        ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
        java.lang.String result = write(head, new java.lang.Object());
        org.junit.Assert.assertEquals("hello", result);
    }

    @org.junit.Test
    public void testBasic() throws java.lang.Exception {
        {
            ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %hello");
            p.setContext(context);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
            java.lang.String result = write(head, new java.lang.Object());
            org.junit.Assert.assertEquals("abc Hello", result);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %hello %OTT");
            p.setContext(context);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
            java.lang.String result = write(head, new java.lang.Object());
            org.junit.Assert.assertEquals("abc Hello 123", result);
        }
    }

    @org.junit.Test
    public void testFormat() throws java.lang.Exception {
        {
            ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %7hello");
            p.setContext(context);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
            java.lang.String result = write(head, new java.lang.Object());
            org.junit.Assert.assertEquals("abc   Hello", result);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %-7hello");
            p.setContext(context);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
            java.lang.String result = write(head, new java.lang.Object());
            org.junit.Assert.assertEquals("abc Hello  ", result);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %.3hello");
            p.setContext(context);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
            java.lang.String result = write(head, new java.lang.Object());
            org.junit.Assert.assertEquals("abc llo", result);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %.-3hello");
            p.setContext(context);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
            java.lang.String result = write(head, new java.lang.Object());
            org.junit.Assert.assertEquals("abc Hel", result);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %4.5OTT");
            p.setContext(context);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
            java.lang.String result = write(head, new java.lang.Object());
            org.junit.Assert.assertEquals("abc  123", result);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %-4.5OTT");
            p.setContext(context);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
            java.lang.String result = write(head, new java.lang.Object());
            org.junit.Assert.assertEquals("abc 123 ", result);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %3.4hello");
            p.setContext(context);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
            java.lang.String result = write(head, new java.lang.Object());
            org.junit.Assert.assertEquals("abc ello", result);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %-3.-4hello");
            p.setContext(context);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
            java.lang.String result = write(head, new java.lang.Object());
            org.junit.Assert.assertEquals("abc Hell", result);
        }
    }

    @org.junit.Test
    public void testComposite() throws java.lang.Exception {
        {
            ch.qos.logback.core.Context c = new ch.qos.logback.core.ContextBase();
            ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("%(ABC %hello)");
            p.setContext(c);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
            java.lang.String result = write(head, new java.lang.Object());
            ch.qos.logback.core.util.StatusPrinter.print(c);
            org.junit.Assert.assertEquals("ABC Hello", result);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("%(ABC %hello)");
            p.setContext(context);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
            java.lang.String result = write(head, new java.lang.Object());
            org.junit.Assert.assertEquals("ABC Hello", result);
        }
    }

    @org.junit.Test
    public void testCompositeFormatting() throws java.lang.Exception {
        {
            ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %4.10(ABC)");
            p.setContext(context);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
            java.lang.String result = write(head, new java.lang.Object());
            org.junit.Assert.assertEquals("xyz  ABC", result);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %-4.10(ABC)");
            p.setContext(context);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
            java.lang.String result = write(head, new java.lang.Object());
            org.junit.Assert.assertEquals("xyz ABC ", result);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %.2(ABC %hello)");
            p.setContext(context);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
            java.lang.String result = write(head, new java.lang.Object());
            org.junit.Assert.assertEquals("xyz lo", result);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %.-2(ABC)");
            p.setContext(context);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
            java.lang.String result = write(head, new java.lang.Object());
            org.junit.Assert.assertEquals("xyz AB", result);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %30.30(ABC %20hello)");
            p.setContext(context);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
            java.lang.String result = write(head, new java.lang.Object());
            org.junit.Assert.assertEquals("xyz       ABC                Hello", result);
        }
    }

    @org.junit.Test
    public void testWithNopEscape() throws java.lang.Exception {
        {
            ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %hello\\_world");
            p.setContext(context);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
            java.lang.String result = write(head, new java.lang.Object());
            org.junit.Assert.assertEquals("xyz Helloworld", result);
        }
    }

    @org.junit.Test
    public void testUnknownWord() throws java.lang.Exception {
        ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("%unknown");
        p.setContext(context);
        ch.qos.logback.core.pattern.parser.Node t = p.parse();
        ch.qos.logback.core.pattern.Converter<java.lang.Object> o_testUnknownWord__6 = p.compile(t, converterMap);
        org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.LiteralConverter) (o_testUnknownWord__6)).getNext());
        ch.qos.logback.core.status.StatusChecker checker = new ch.qos.logback.core.status.StatusChecker(context.getStatusManager());
        checker.assertContainsMatch("\\[unknown] is not a valid conversion word");
    }

    @org.junit.Test(timeout = 10000)
    public void testUnknownWord_add80989() throws java.lang.Exception {
        ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("%unknown");
        p.setContext(context);
        ch.qos.logback.core.pattern.parser.Node t = p.parse();
        ch.qos.logback.core.pattern.Converter<java.lang.Object> o_testUnknownWord_add80989__6 = p.compile(t, converterMap);
        org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.LiteralConverter) (o_testUnknownWord_add80989__6)).getNext());
        p.compile(t, converterMap);
        ch.qos.logback.core.status.StatusChecker checker = new ch.qos.logback.core.status.StatusChecker(context.getStatusManager());
        checker.assertContainsMatch("\\[unknown] is not a valid conversion word");
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testBasic */
    @org.junit.Test(timeout = 10000)
    public void testBasic_cf23_failAssert20_add69_literalMutation121_failAssert8() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %h{ello");
                    // MethodCallAdder
                    p.setContext(context);
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %hello %OTT");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.CompositeNode vc_2 = (ch.qos.logback.core.pattern.parser.CompositeNode)null;
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(vc_2);
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.Compiler vc_0 = (ch.qos.logback.core.pattern.parser.Compiler)null;
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(vc_0);
                    // StatementAdderMethod cloned existing statement
                    vc_0.createCompositeConverter(vc_2);
                }
                org.junit.Assert.fail("testBasic_cf23 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testBasic_cf23_failAssert20_add69_literalMutation121 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testBasic */
    @org.junit.Test(timeout = 10000)
    public void testBasic_cf31_failAssert21_add83_literalMutation201_failAssert2() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("J!\\=[,J^uy");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %hello %OTT");
                    // MethodCallAdder
                    p.setContext(context);
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.Compiler vc_4 = (ch.qos.logback.core.pattern.parser.Compiler)null;
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(vc_4);
                    // StatementAdderMethod cloned existing statement
                    vc_4.compile();
                }
                org.junit.Assert.fail("testBasic_cf31 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testBasic_cf31_failAssert21_add83_literalMutation201 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testBasic */
    @org.junit.Test(timeout = 10000)
    public void testBasic_cf31_failAssert21_literalMutation85_failAssert8_literalMutation225_failAssert3() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // AssertGenerator generate try/catch block with fail statement
                try {
                    {
                        ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("\\");
                        p.setContext(context);
                        ch.qos.logback.core.pattern.parser.Node t = p.parse();
                        ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                        java.lang.String result = write(head, new java.lang.Object());
                    }
                    {
                        ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %hello %OTT");
                        p.setContext(context);
                        ch.qos.logback.core.pattern.parser.Node t = p.parse();
                        ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                        java.lang.String result = write(head, new java.lang.Object());
                        // StatementAdderOnAssert create null value
                        ch.qos.logback.core.pattern.parser.Compiler vc_4 = (ch.qos.logback.core.pattern.parser.Compiler)null;
                        // StatementAdderMethod cloned existing statement
                        vc_4.compile();
                    }
                    org.junit.Assert.fail("testBasic_cf31 should have thrown NullPointerException");
                } catch (java.lang.NullPointerException eee) {
                }
                org.junit.Assert.fail("testBasic_cf31_failAssert21_literalMutation85 should have thrown ScanException");
            } catch (ch.qos.logback.core.spi.ScanException eee) {
            }
            org.junit.Assert.fail("testBasic_cf31_failAssert21_literalMutation85_failAssert8_literalMutation225 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testBasic */
    @org.junit.Test(timeout = 10000)
    public void testBasic_cf31_failAssert21_literalMutation94_failAssert10() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %hello");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("ToX)D7x>\\ZBob5_");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.Compiler vc_4 = (ch.qos.logback.core.pattern.parser.Compiler)null;
                    // StatementAdderMethod cloned existing statement
                    vc_4.compile();
                }
                org.junit.Assert.fail("testBasic_cf31 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testBasic_cf31_failAssert21_literalMutation94 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testBasic */
    @org.junit.Test(timeout = 10000)
    public void testBasic_cf33_failAssert22_add96_literalMutation273() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("y}W`_*s>).");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "y}W`_*s>");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %hello %OTT");
                // MethodCallAdder
                p.setContext(context);
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.SimpleKeywordNode vc_8 = (ch.qos.logback.core.pattern.parser.SimpleKeywordNode)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_8);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_8);
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.Compiler vc_6 = (ch.qos.logback.core.pattern.parser.Compiler)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_6);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_6);
                // StatementAdderMethod cloned existing statement
                vc_6.createConverter(vc_8);
            }
            org.junit.Assert.fail("testBasic_cf33 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testBasic */
    @org.junit.Test(timeout = 10000)
    public void testBasic_cf33_failAssert22_literalMutation103_failAssert12_literalMutation313() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>(")bc %hello");
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).parse());
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.SimpleKeywordNode vc_8 = (ch.qos.logback.core.pattern.parser.SimpleKeywordNode)null;
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.Compiler vc_6 = (ch.qos.logback.core.pattern.parser.Compiler)null;
                    // StatementAdderMethod cloned existing statement
                    vc_6.createConverter(vc_8);
                }
                org.junit.Assert.fail("testBasic_cf33 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testBasic_cf33_failAssert22_literalMutation103 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testCompositeFormatting */
    @org.junit.Test(timeout = 10000)
    public void testCompositeFormatting_cf825_failAssert49_literalMutation1056_literalMutation1323_failAssert28() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %4.10(ABC)");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("iw/qa6pE0]A\\RCi");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %.2nABC %hello)");
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext()).getOptions());
                    // AssertGenerator add assertion
                    org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext()).getValue(), "nABC");
                    // AssertGenerator add assertion
                    org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "xyz ");
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
                    // AssertGenerator add assertion
                    org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
                    // AssertGenerator add assertion
                    org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext()).getType(), 1);
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %.-2(ABC)");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %30.30(ABC %20hello)");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.CompositeNode vc_22 = (ch.qos.logback.core.pattern.parser.CompositeNode)null;
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(vc_22);
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.Compiler vc_20 = (ch.qos.logback.core.pattern.parser.Compiler)null;
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(vc_20);
                    // StatementAdderMethod cloned existing statement
                    vc_20.createCompositeConverter(vc_22);
                }
                org.junit.Assert.fail("testCompositeFormatting_cf825 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testCompositeFormatting_cf825_failAssert49_literalMutation1056_literalMutation1323 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testCompositeFormatting */
    @org.junit.Test(timeout = 10000)
    public void testCompositeFormatting_cf833_failAssert50_literalMutation1074() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %4A.10(ABC)");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext()).getOptions());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext()).getValue(), "A");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "xyz ");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext()).getType(), 1);
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %-4.10(ABC)");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %.2(ABC %hello)");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %.-2(ABC)");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %30.30(ABC %20hello)");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.Compiler vc_24 = (ch.qos.logback.core.pattern.parser.Compiler)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_24);
                // StatementAdderMethod cloned existing statement
                vc_24.compile();
            }
            org.junit.Assert.fail("testCompositeFormatting_cf833 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testCompositeFormatting */
    @org.junit.Test(timeout = 10000)
    public void testCompositeFormatting_cf833_failAssert50_literalMutation1097_failAssert29() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %4.10(ABC)");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %-4.10(ABC)");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %.2(ABC %hello)");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %.-2(ABC)");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("UZo@XT$QA3=&:J/N_g`3C8t%");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.Compiler vc_24 = (ch.qos.logback.core.pattern.parser.Compiler)null;
                    // StatementAdderMethod cloned existing statement
                    vc_24.compile();
                }
                org.junit.Assert.fail("testCompositeFormatting_cf833 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testCompositeFormatting_cf833_failAssert50_literalMutation1097 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testCompositeFormatting */
    @org.junit.Test(timeout = 10000)
    public void testCompositeFormatting_cf835_failAssert51_literalMutation1107_failAssert18() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("x\\yz %4.10(ABC)");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %-4.10(ABC)");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %.2(ABC %hello)");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %.-2(ABC)");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %30.30(ABC %20hello)");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.SimpleKeywordNode vc_28 = (ch.qos.logback.core.pattern.parser.SimpleKeywordNode)null;
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.Compiler vc_26 = (ch.qos.logback.core.pattern.parser.Compiler)null;
                    // StatementAdderMethod cloned existing statement
                    vc_26.createConverter(vc_28);
                }
                org.junit.Assert.fail("testCompositeFormatting_cf835 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testCompositeFormatting_cf835_failAssert51_literalMutation1107 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testCompositeFormatting */
    @org.junit.Test(timeout = 10000)
    public void testCompositeFormatting_cf835_failAssert51_literalMutation1122() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %4.10(ABC)");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %-4.10(ABC)");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %.2(ABC %hello)");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz 2.-2(ABC)");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "xyz 2.-2(ABC");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %30.30(ABC %20hello)");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.SimpleKeywordNode vc_28 = (ch.qos.logback.core.pattern.parser.SimpleKeywordNode)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_28);
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.Compiler vc_26 = (ch.qos.logback.core.pattern.parser.Compiler)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_26);
                // StatementAdderMethod cloned existing statement
                vc_26.createConverter(vc_28);
            }
            org.junit.Assert.fail("testCompositeFormatting_cf835 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testCompositeFormatting */
    @org.junit.Test
    public void testCompositeFormatting_literalMutation775_failAssert0_literalMutation847_failAssert11() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("\\");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %-4.10(ABC)");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %.2(ABC %hello)");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %.-2(ABC)");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %30.30(ABC %20hello)");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                org.junit.Assert.fail("testCompositeFormatting_literalMutation775 should have thrown ScanException");
            } catch (ch.qos.logback.core.spi.ScanException eee) {
            }
            org.junit.Assert.fail("testCompositeFormatting_literalMutation775_failAssert0_literalMutation847 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testCompositeFormatting */
    @org.junit.Test
    public void testCompositeFormatting_literalMutation775_failAssert0_literalMutation847_failAssert11_literalMutation1594() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("\\");
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %-4.10(ABC)");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %.2(ABC %hello)");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %30.30(ABC %20hello)");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                org.junit.Assert.fail("testCompositeFormatting_literalMutation775 should have thrown ScanException");
            } catch (ch.qos.logback.core.spi.ScanException eee) {
            }
            org.junit.Assert.fail("testCompositeFormatting_literalMutation775_failAssert0_literalMutation847 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testCompositeFormatting */
    @org.junit.Test
    public void testCompositeFormatting_literalMutation785_failAssert10_literalMutation879_failAssert22_literalMutation1946() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("I0}2Z*:D}l!3tx");
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
                    // AssertGenerator add assertion
                    org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "I0}2Z*:D}l!3tx");
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
                    // AssertGenerator add assertion
                    org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("\\");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %.2(ABC %hello)");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %.-2(ABC)");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %30.30(ABC %20hello)");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                org.junit.Assert.fail("testCompositeFormatting_literalMutation785 should have thrown ScanException");
            } catch (ch.qos.logback.core.spi.ScanException eee) {
            }
            org.junit.Assert.fail("testCompositeFormatting_literalMutation785_failAssert10_literalMutation879 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testCompositeFormatting */
    @org.junit.Test
    public void testCompositeFormatting_literalMutation805_failAssert29_literalMutation936() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %4.10(ABC)");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>(")aJlLP]M{.a6Z;:");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).parse());
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %.2(ABC %hello)");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %30.30(ABC %20hello)");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            org.junit.Assert.fail("testCompositeFormatting_literalMutation805 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testCompositeFormatting */
    @org.junit.Test
    public void testCompositeFormatting_literalMutation808_failAssert32() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %4.10(ABC)");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %-4.10(ABC)");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %.2(ABC %hello)");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("^!%Y\\i_E\\[i.}");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %30.30(ABC %20hello)");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            org.junit.Assert.fail("testCompositeFormatting_literalMutation808 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testComposite */
    @org.junit.Test(timeout = 10000)
    public void testComposite_cf339_failAssert20_literalMutation393() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // {
            // Parser<Object> p = new Parser<Object>("%(ABC)");
            // p.setContext(context);
            // Node t = p.parse();
            // Converter<Object> head = p.compile(t, converterMap);
            // String result = write(head, new Object());
            // assertEquals("ABC", result);
            // }
            {
                ch.qos.logback.core.Context c = new ch.qos.logback.core.ContextBase();
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("I)uhDiCMs-NCP");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "I");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
                p.setContext(c);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
                ch.qos.logback.core.util.StatusPrinter.print(c);
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("%(ABC %hello)");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.CompositeNode vc_12 = (ch.qos.logback.core.pattern.parser.CompositeNode)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_12);
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.Compiler vc_10 = (ch.qos.logback.core.pattern.parser.Compiler)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_10);
                // StatementAdderMethod cloned existing statement
                vc_10.createCompositeConverter(vc_12);
            }
            org.junit.Assert.fail("testComposite_cf339 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testComposite */
    @org.junit.Test(timeout = 10000)
    public void testComposite_cf347_failAssert21_literalMutation413_failAssert11() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // {
                // Parser<Object> p = new Parser<Object>("%(ABC)");
                // p.setContext(context);
                // Node t = p.parse();
                // Converter<Object> head = p.compile(t, converterMap);
                // String result = write(head, new Object());
                // assertEquals("ABC", result);
                // }
                {
                    ch.qos.logback.core.Context c = new ch.qos.logback.core.ContextBase();
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("%(ABC %hello)");
                    p.setContext(c);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                    ch.qos.logback.core.util.StatusPrinter.print(c);
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("wL%G{#C.D%L]D");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.Compiler vc_14 = (ch.qos.logback.core.pattern.parser.Compiler)null;
                    // StatementAdderMethod cloned existing statement
                    vc_14.compile();
                }
                org.junit.Assert.fail("testComposite_cf347 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testComposite_cf347_failAssert21_literalMutation413 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testComposite */
    @org.junit.Test(timeout = 10000)
    public void testComposite_cf349_failAssert22_add419_literalMutation718_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // {
                // Parser<Object> p = new Parser<Object>("%(ABC)");
                // p.setContext(context);
                // Node t = p.parse();
                // Converter<Object> head = p.compile(t, converterMap);
                // String result = write(head, new Object());
                // assertEquals("ABC", result);
                // }
                {
                    ch.qos.logback.core.Context c = new ch.qos.logback.core.ContextBase();
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("%(ABC %hello)");
                    p.setContext(c);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                    ch.qos.logback.core.util.StatusPrinter.print(c);
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("\\AB@BafG1>V9s");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.SimpleKeywordNode vc_18 = (ch.qos.logback.core.pattern.parser.SimpleKeywordNode)null;
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(vc_18);
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.Compiler vc_16 = (ch.qos.logback.core.pattern.parser.Compiler)null;
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(vc_16);
                    // StatementAdderMethod cloned existing statement
                    // MethodCallAdder
                    vc_16.createConverter(vc_18);
                    // StatementAdderMethod cloned existing statement
                    vc_16.createConverter(vc_18);
                }
                org.junit.Assert.fail("testComposite_cf349 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testComposite_cf349_failAssert22_add419_literalMutation718 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testFormat */
    @org.junit.Test(timeout = 10000)
    public void testFormat_cf2333_failAssert81_literalMutation3006() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %7hello");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("_Ch)v;-EOShL");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "_Ch");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %.3hello");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %.-3hello");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %4.5OTT");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %-4.5OTT");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %3.4hello");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %-3.-4hello");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.Compiler vc_34 = (ch.qos.logback.core.pattern.parser.Compiler)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_34);
                // StatementAdderMethod cloned existing statement
                vc_34.compile();
            }
            org.junit.Assert.fail("testFormat_cf2333 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testFormat */
    @org.junit.Test(timeout = 10000)
    public void testFormat_cf2333_failAssert81_literalMutation3006_literalMutation3764_failAssert8() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %7hello");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("tb|zZLU]V5<A");
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
                    // AssertGenerator add assertion
                    org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "_Ch");
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
                    // AssertGenerator add assertion
                    org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %.3hello");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %.-3hello");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %4.5OTT");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %-4.5OTT");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %3.4hello");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %-3.-4hello");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.Compiler vc_34 = (ch.qos.logback.core.pattern.parser.Compiler)null;
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(vc_34);
                    // StatementAdderMethod cloned existing statement
                    vc_34.compile();
                }
                org.junit.Assert.fail("testFormat_cf2333 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testFormat_cf2333_failAssert81_literalMutation3006_literalMutation3764 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testFormat */
    @org.junit.Test(timeout = 10000)
    public void testFormat_cf2333_failAssert81_literalMutation3037_failAssert25_literalMutation4438_failAssert19() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // AssertGenerator generate try/catch block with fail statement
                try {
                    {
                        ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %7hello");
                        p.setContext(context);
                        ch.qos.logback.core.pattern.parser.Node t = p.parse();
                        ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                        java.lang.String result = write(head, new java.lang.Object());
                    }
                    {
                        ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %-7hello");
                        p.setContext(context);
                        ch.qos.logback.core.pattern.parser.Node t = p.parse();
                        ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                        java.lang.String result = write(head, new java.lang.Object());
                    }
                    {
                        ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %.3hello");
                        p.setContext(context);
                        ch.qos.logback.core.pattern.parser.Node t = p.parse();
                        ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                        java.lang.String result = write(head, new java.lang.Object());
                    }
                    {
                        ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("t(-b`l}A)v%,5");
                        p.setContext(context);
                        ch.qos.logback.core.pattern.parser.Node t = p.parse();
                        ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                        java.lang.String result = write(head, new java.lang.Object());
                    }
                    {
                        ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %4.5OTT");
                        p.setContext(context);
                        ch.qos.logback.core.pattern.parser.Node t = p.parse();
                        ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                        java.lang.String result = write(head, new java.lang.Object());
                    }
                    {
                        ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %-4.5OTT");
                        p.setContext(context);
                        ch.qos.logback.core.pattern.parser.Node t = p.parse();
                        ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                        java.lang.String result = write(head, new java.lang.Object());
                    }
                    {
                        ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %3.4hello");
                        p.setContext(context);
                        ch.qos.logback.core.pattern.parser.Node t = p.parse();
                        ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                        java.lang.String result = write(head, new java.lang.Object());
                    }
                    {
                        ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %-3`.-4hello");
                        p.setContext(context);
                        ch.qos.logback.core.pattern.parser.Node t = p.parse();
                        ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                        java.lang.String result = write(head, new java.lang.Object());
                        // StatementAdderOnAssert create null value
                        ch.qos.logback.core.pattern.parser.Compiler vc_34 = (ch.qos.logback.core.pattern.parser.Compiler)null;
                        // StatementAdderMethod cloned existing statement
                        vc_34.compile();
                    }
                    org.junit.Assert.fail("testFormat_cf2333 should have thrown NullPointerException");
                } catch (java.lang.NullPointerException eee) {
                }
                org.junit.Assert.fail("testFormat_cf2333_failAssert81_literalMutation3037 should have thrown NumberFormatException");
            } catch (java.lang.NumberFormatException eee) {
            }
            org.junit.Assert.fail("testFormat_cf2333_failAssert81_literalMutation3037_failAssert25_literalMutation4438 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testFormat */
    @org.junit.Test(timeout = 10000)
    public void testFormat_cf2335_failAssert82_add3043_literalMutation3496_failAssert16() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %7hello");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %-7hello");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %.3hello");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("#wqH\\4fi7v[B.");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %4.5OTT");
                    // MethodCallAdder
                    p.setContext(context);
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %-4.5OTT");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %3.4hello");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %-3.-4hello");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.SimpleKeywordNode vc_38 = (ch.qos.logback.core.pattern.parser.SimpleKeywordNode)null;
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(vc_38);
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.Compiler vc_36 = (ch.qos.logback.core.pattern.parser.Compiler)null;
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(vc_36);
                    // StatementAdderMethod cloned existing statement
                    vc_36.createConverter(vc_38);
                }
                org.junit.Assert.fail("testFormat_cf2335 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testFormat_cf2335_failAssert82_add3043_literalMutation3496 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testFormat */
    @org.junit.Test
    public void testFormat_literalMutation2288_failAssert43() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %7hello");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %-7hello");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %.3hello");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %.-3hello");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("2sc\\-c&=M-z");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %-4.5OTT");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %3.4hello");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("abc %-3.-4hello");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
            }
            org.junit.Assert.fail("testFormat_literalMutation2288 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testLiteral */
    @org.junit.Test(timeout = 10000)
    public void testLiteral_cf4665_failAssert12_literalMutation4690_failAssert5() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("\\ello");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.SimpleKeywordNode vc_48 = (ch.qos.logback.core.pattern.parser.SimpleKeywordNode)null;
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.Compiler vc_46 = (ch.qos.logback.core.pattern.parser.Compiler)null;
                // StatementAdderMethod cloned existing statement
                vc_46.createConverter(vc_48);
                org.junit.Assert.fail("testLiteral_cf4665 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testLiteral_cf4665_failAssert12_literalMutation4690 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testUnknownWord */
    @org.junit.Test(timeout = 10000)
    public void testUnknownWord_add4728() throws java.lang.Exception {
        ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("%unknown");
        p.setContext(context);
        ch.qos.logback.core.pattern.parser.Node t = p.parse();
        // AssertGenerator replace invocation
        ch.qos.logback.core.pattern.Converter<java.lang.Object> o_testUnknownWord_add4728__6 = // MethodCallAdder
p.compile(t, converterMap);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.LiteralConverter)o_testUnknownWord_add4728__6).getNext());
        p.compile(t, converterMap);
        ch.qos.logback.core.status.StatusChecker checker = new ch.qos.logback.core.status.StatusChecker(context.getStatusManager());
        checker.assertContainsMatch("\\[unknown] is not a valid conversion word");
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testWithNopEscape */
    @org.junit.Test(timeout = 10000)
    public void testWithNopEscape_cf4780_failAssert10_literalMutation4805_failAssert3() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("d0o\\s,`RKzjDmh`1{");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.CompositeNode vc_52 = (ch.qos.logback.core.pattern.parser.CompositeNode)null;
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.Compiler vc_50 = (ch.qos.logback.core.pattern.parser.Compiler)null;
                    // StatementAdderMethod cloned existing statement
                    vc_50.createCompositeConverter(vc_52);
                }
                org.junit.Assert.fail("testWithNopEscape_cf4780 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testWithNopEscape_cf4780_failAssert10_literalMutation4805 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testWithNopEscape */
    @org.junit.Test(timeout = 10000)
    public void testWithNopEscape_cf4788_failAssert11_add4807_literalMutation4862_failAssert7() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("xyz %hello\\world");
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.Compiler vc_54 = (ch.qos.logback.core.pattern.parser.Compiler)null;
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(vc_54);
                    // StatementAdderMethod cloned existing statement
                    // MethodCallAdder
                    vc_54.compile();
                    // StatementAdderMethod cloned existing statement
                    vc_54.compile();
                }
                org.junit.Assert.fail("testWithNopEscape_cf4788 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testWithNopEscape_cf4788_failAssert11_add4807_literalMutation4862 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testWithNopEscape */
    @org.junit.Test(timeout = 10000)
    public void testWithNopEscape_cf4788_failAssert11_literalMutation4812() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("uAtCG&AaxJ7XHV&!)");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "uAtCG&AaxJ7XHV&!");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                java.lang.String result = write(head, new java.lang.Object());
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.Compiler vc_54 = (ch.qos.logback.core.pattern.parser.Compiler)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_54);
                // StatementAdderMethod cloned existing statement
                vc_54.compile();
            }
            org.junit.Assert.fail("testWithNopEscape_cf4788 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.CompilerTest#testWithNopEscape */
    @org.junit.Test(timeout = 10000)
    public void testWithNopEscape_cf4788_failAssert11_literalMutation4812_literalMutation4873_failAssert11() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    ch.qos.logback.core.pattern.parser.Parser<java.lang.Object> p = new ch.qos.logback.core.pattern.parser.Parser<java.lang.Object>("Q7gU=FXgds1EH39 c");
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
                    // AssertGenerator add assertion
                    org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "uAtCG&AaxJ7XHV&!");
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
                    // AssertGenerator add assertion
                    org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
                    p.setContext(context);
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.Converter<java.lang.Object> head = p.compile(t, converterMap);
                    java.lang.String result = write(head, new java.lang.Object());
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.Compiler vc_54 = (ch.qos.logback.core.pattern.parser.Compiler)null;
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(vc_54);
                    // StatementAdderMethod cloned existing statement
                    vc_54.compile();
                }
                org.junit.Assert.fail("testWithNopEscape_cf4788 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testWithNopEscape_cf4788_failAssert11_literalMutation4812_literalMutation4873 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }
}

