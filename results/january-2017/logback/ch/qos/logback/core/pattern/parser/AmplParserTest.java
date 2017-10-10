

package ch.qos.logback.core.pattern.parser;


public class AmplParserTest {
    java.lang.String BARE = ch.qos.logback.core.pattern.parser.Token.BARE_COMPOSITE_KEYWORD_TOKEN.getValue().toString();

    ch.qos.logback.core.Context context = new ch.qos.logback.core.ContextBase();

    @org.junit.Test
    public void testBasic() throws java.lang.Exception {
        ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello");
        ch.qos.logback.core.pattern.parser.Node t = p.parse();
        org.junit.Assert.assertEquals(ch.qos.logback.core.pattern.parser.Node.LITERAL, t.getType());
        org.junit.Assert.assertEquals("hello", t.getValue());
    }

    @org.junit.Test
    public void testKeyword() throws java.lang.Exception {
        {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%xyz");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
            witness.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("xyz");
            org.junit.Assert.assertEquals(witness, t);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%xyz{x}");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
            ch.qos.logback.core.pattern.parser.SimpleKeywordNode n = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("xyz");
            java.util.List<java.lang.String> optionList = new java.util.ArrayList<java.lang.String>();
            optionList.add("x");
            n.setOptions(optionList);
            witness.next = n;
            org.junit.Assert.assertEquals(witness, t);
        }
    }

    @org.junit.Test
    public void testComposite() throws java.lang.Exception {
        {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child)");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
            ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
            ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
            composite.setChildNode(child);
            witness.next = composite;
            org.junit.Assert.assertEquals(witness, t);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child )");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
            ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
            ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
            composite.setChildNode(child);
            witness.next = composite;
            child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
            org.junit.Assert.assertEquals(witness, t);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child %h)");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
            ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
            ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
            composite.setChildNode(child);
            child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
            child.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
            witness.next = composite;
            org.junit.Assert.assertEquals(witness, t);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child %h) %m");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
            ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
            ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
            composite.setChildNode(child);
            child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
            child.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
            witness.next = composite;
            composite.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
            composite.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("m");
            org.junit.Assert.assertEquals(witness, t);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%( %child \\(%h\\) ) %m");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
            ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
            ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
            composite.setChildNode(child);
            ch.qos.logback.core.pattern.parser.Node c = child;
            c = c.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
            c = c.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " (");
            c = c.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
            c = c.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, ") ");
            witness.next = composite;
            composite.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
            composite.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("m");
            org.junit.Assert.assertEquals(witness, t);
        }
    }

    @org.junit.Test
    public void testNested() throws java.lang.Exception {
        {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%top %(%child%(%h))");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("top");
            ch.qos.logback.core.pattern.parser.Node w = witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
            ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
            w = w.next = composite;
            ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
            composite.setChildNode(child);
            composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
            child.next = composite;
            composite.setChildNode(new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h"));
            org.junit.Assert.assertEquals(witness, t);
        }
    }

    @org.junit.Test
    public void testFormattingInfo() throws java.lang.Exception {
        {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%45x");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
            witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(45, java.lang.Integer.MAX_VALUE));
            org.junit.Assert.assertEquals(witness, t);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%4.5x");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
            witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5));
            org.junit.Assert.assertEquals(witness, t);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%-4.5x");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
            witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5, false, true));
            org.junit.Assert.assertEquals(witness, t);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%-4.-5x");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
            witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5, false, false));
            org.junit.Assert.assertEquals(witness, t);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%-4.5x %12y");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
            witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5, false, true));
            ch.qos.logback.core.pattern.parser.Node n = witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
            n = n.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("y");
            ((ch.qos.logback.core.pattern.parser.FormattingNode) (n)).setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(12, java.lang.Integer.MAX_VALUE));
            org.junit.Assert.assertEquals(witness, t);
        }
    }

    @org.junit.Test
    public void testOptions0() throws java.lang.Exception {
        ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%45x{'test '}");
        ch.qos.logback.core.pattern.parser.Node t = p.parse();
        ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
        witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(45, java.lang.Integer.MAX_VALUE));
        java.util.List<java.lang.String> ol = new java.util.ArrayList<java.lang.String>();
        ol.add("test ");
        witness.setOptions(ol);
        org.junit.Assert.assertEquals(witness, t);
    }

    @org.junit.Test
    public void testOptions1() throws java.lang.Exception {
        ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%45x{a, b}");
        ch.qos.logback.core.pattern.parser.Node t = p.parse();
        ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
        witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(45, java.lang.Integer.MAX_VALUE));
        java.util.List<java.lang.String> ol = new java.util.ArrayList<java.lang.String>();
        ol.add("a");
        ol.add("b");
        witness.setOptions(ol);
        org.junit.Assert.assertEquals(witness, t);
    }

    @org.junit.Test
    public void keywordGluedToLitteral() throws java.lang.Exception {
        ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%x{}a");
        ch.qos.logback.core.pattern.parser.Node t = p.parse();
        ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
        witness.setOptions(new java.util.ArrayList<java.lang.String>());
        witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "a");
        org.junit.Assert.assertEquals(witness, t);
    }

    @org.junit.Test
    public void testCompositeFormatting() throws java.lang.Exception {
        ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%5(XYZ)");
        ch.qos.logback.core.pattern.parser.Node t = p.parse();
        ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
        ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
        composite.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(5, java.lang.Integer.MAX_VALUE));
        ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "XYZ");
        composite.setChildNode(child);
        witness.next = composite;
        org.junit.Assert.assertEquals(witness, t);
    }

    @org.junit.Test
    public void empty() {
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("");
            p.parse();
            org.junit.Assert.fail("");
        } catch (ch.qos.logback.core.spi e) {
        }
    }

    @org.junit.Test
    public void lbcore193() throws java.lang.Exception {
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(abc");
            p.setContext(context);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            org.junit.Assert.fail("where the is exception?");
        } catch (ch.qos.logback.core.spi ise) {
            org.junit.Assert.assertEquals("Expecting RIGHT_PARENTHESIS token but got null", ise.getMessage());
        }
        ch.qos.logback.core.status.StatusChecker sc = new ch.qos.logback.core.status.StatusChecker(context);
        sc.assertContainsMatch("Expecting RIGHT_PARENTHESIS");
        sc.assertContainsMatch(("See also " + (ch.qos.logback.core.pattern.parser.Parser.MISSING_RIGHT_PARENTHESIS)));
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#empty */
    @org.junit.Test(timeout = 10000)
    public void empty_cf40_literalMutation3703_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            try {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("-");
                p.parse();
                // StatementAdderMethod cloned existing statement
                p.E();
                org.junit.Assert.fail("");
            } catch (ch.qos.logback.core.spi.ScanException e) {
            }
            org.junit.Assert.fail("empty_cf40_literalMutation3703 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#empty */
    @org.junit.Test(timeout = 10000)
    public void empty_cf40_literalMutation3703_failAssert1_cf7220() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            try {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("-");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "-");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
                p.parse();
                // StatementAdderMethod cloned existing statement
                p.E();
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.Parser vc_2658 = (ch.qos.logback.core.pattern.parser.Parser)null;
                // StatementAdderMethod cloned existing statement
                vc_2658.C();
                org.junit.Assert.fail("");
            } catch (ch.qos.logback.core.spi.ScanException e) {
            }
            org.junit.Assert.fail("empty_cf40_literalMutation3703 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#empty */
    @org.junit.Test(timeout = 10000)
    public void empty_literalMutation2_cf221_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            try {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("toto");
                p.parse();
                // StatementAdderOnAssert create null value
                java.lang.String vc_100 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.Token vc_98 = (ch.qos.logback.core.pattern.parser.Token)null;
                // StatementAdderMethod cloned existing statement
                p.expectNotNull(vc_98, vc_100);
                org.junit.Assert.fail("");
            } catch (ch.qos.logback.core.spi.ScanException e) {
            }
            org.junit.Assert.fail("empty_literalMutation2_cf221 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    // see http://jira.qos.ch/browse/LBCORE-180
    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#keywordGluedToLitteral */
    @org.junit.Test(timeout = 10000)
    public void keywordGluedToLitteral_cf7423_failAssert13() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%x{}a");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
            witness.setOptions(new java.util.ArrayList<java.lang.String>());
            witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "a");
            // StatementAdderMethod cloned existing statement
            p.C();
            org.junit.Assert.fail("keywordGluedToLitteral_cf7423 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    // see http://jira.qos.ch/browse/LBCORE-180
    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#keywordGluedToLitteral */
    @org.junit.Test(timeout = 10000)
    public void keywordGluedToLitteral_cf7429_failAssert17_literalMutation7597_literalMutation8419() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("r(V!T");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "r(V!T");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "r(V!T");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
            witness.setOptions(new java.util.ArrayList<java.lang.String>());
            witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "*");
            // StatementAdderOnAssert create null value
            java.lang.String vc_2730 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            p.COMPOSITE(vc_2730);
            org.junit.Assert.fail("keywordGluedToLitteral_cf7429 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    // see http://jira.qos.ch/browse/LBCORE-180
    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#keywordGluedToLitteral */
    @org.junit.Test(timeout = 10000)
    public void keywordGluedToLitteral_cf7444_cf7758_failAssert55_literalMutation9709_failAssert61() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("70az%");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setOptions(new java.util.ArrayList<java.lang.String>());
                witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "a");
                // AssertGenerator replace invocation
                ch.qos.logback.core.pattern.parser.Node o_keywordGluedToLitteral_cf7444__11 = // StatementAdderMethod cloned existing statement
p.Eopt();
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(o_keywordGluedToLitteral_cf7444__11);
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_2787 = new java.lang.String();
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.Token vc_2784 = (ch.qos.logback.core.pattern.parser.Token)null;
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.Parser vc_2782 = (ch.qos.logback.core.pattern.parser.Parser)null;
                // StatementAdderMethod cloned existing statement
                vc_2782.expectNotNull(vc_2784, vc_2787);
                org.junit.Assert.fail("keywordGluedToLitteral_cf7444_cf7758 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("keywordGluedToLitteral_cf7444_cf7758_failAssert55_literalMutation9709 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    // see http://jira.qos.ch/browse/LBCORE-180
    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#keywordGluedToLitteral */
    @org.junit.Test(timeout = 10000)
    public void keywordGluedToLitteral_cf7447_failAssert26() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%x{}a");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
            witness.setOptions(new java.util.ArrayList<java.lang.String>());
            witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "a");
            // StatementAdderMethod cloned existing statement
            p.T();
            org.junit.Assert.fail("keywordGluedToLitteral_cf7447 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    // see http://jira.qos.ch/browse/LBCORE-180
    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#keywordGluedToLitteral */
    @org.junit.Test(timeout = 10000)
    public void keywordGluedToLitteral_cf7447_failAssert26_literalMutation7798_literalMutation8627_failAssert59() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%x{a");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("-");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)witness).getNext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)witness).getType(), 1);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)witness).getValue(), "-");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)witness).getOptions());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)witness).getFormatInfo());
                witness.setOptions(new java.util.ArrayList<java.lang.String>());
                witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "a");
                // StatementAdderMethod cloned existing statement
                p.T();
                org.junit.Assert.fail("keywordGluedToLitteral_cf7447 should have thrown IllegalStateException");
            } catch (java.lang.IllegalStateException eee) {
            }
            org.junit.Assert.fail("keywordGluedToLitteral_cf7447_failAssert26_literalMutation7798_literalMutation8627 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    // see http://jira.qos.ch/browse/LBCORE-180
    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#keywordGluedToLitteral */
    @org.junit.Test(timeout = 10000)
    public void keywordGluedToLitteral_cf7450_failAssert28_literalMutation7817() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("{7GYU");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "{7GYU");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
            witness.setOptions(new java.util.ArrayList<java.lang.String>());
            witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "a");
            // StatementAdderMethod cloned existing statement
            p.parse();
            org.junit.Assert.fail("keywordGluedToLitteral_cf7450 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    // see http://jira.qos.ch/browse/LBCORE-180
    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#keywordGluedToLitteral */
    @org.junit.Test(timeout = 10000)
    public void keywordGluedToLitteral_cf7453_cf7869_failAssert44() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%x{}a");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
            witness.setOptions(new java.util.ArrayList<java.lang.String>());
            witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "a");
            // AssertGenerator replace invocation
            ch.qos.logback.core.pattern.parser.Token o_keywordGluedToLitteral_cf7453__11 = // StatementAdderMethod cloned existing statement
p.getCurentToken();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_keywordGluedToLitteral_cf7453__11);
            // StatementAdderMethod cloned existing statement
            p.C();
            org.junit.Assert.fail("keywordGluedToLitteral_cf7453_cf7869 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    // see http://jira.qos.ch/browse/LBCORE-180
    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#keywordGluedToLitteral */
    @org.junit.Test(timeout = 10000)
    public void keywordGluedToLitteral_cf7456_cf7992_failAssert45_literalMutation9321_failAssert16() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("\\S#;L");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setOptions(new java.util.ArrayList<java.lang.String>());
                witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "a");
                // AssertGenerator replace invocation
                ch.qos.logback.core.pattern.parser.Token o_keywordGluedToLitteral_cf7456__11 = // StatementAdderMethod cloned existing statement
p.getNextToken();
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(o_keywordGluedToLitteral_cf7456__11);
                // StatementAdderMethod cloned existing statement
                p.SINGLE();
                org.junit.Assert.fail("keywordGluedToLitteral_cf7456_cf7992 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("keywordGluedToLitteral_cf7456_cf7992_failAssert45_literalMutation9321 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    // see http://jira.qos.ch/browse/LBCORE-180
    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#keywordGluedToLitteral */
    @org.junit.Test(timeout = 10000)
    public void keywordGluedToLitteral_cf7459() throws java.lang.Exception {
        ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%x{}a");
        ch.qos.logback.core.pattern.parser.Node t = p.parse();
        ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
        witness.setOptions(new java.util.ArrayList<java.lang.String>());
        witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "a");
        // StatementAdderMethod cloned existing statement
        p.advanceTokenPointer();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
        org.junit.Assert.assertEquals(witness, t);
    }

    // see http://jira.qos.ch/browse/LBCORE-180
    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#keywordGluedToLitteral */
    @org.junit.Test(timeout = 10000)
    public void keywordGluedToLitteral_cf7469_failAssert35_literalMutation8188_failAssert50() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%x{a");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setOptions(new java.util.ArrayList<java.lang.String>());
                witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "a");
                // StatementAdderOnAssert create null value
                java.lang.String vc_2752 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.Token vc_2750 = (ch.qos.logback.core.pattern.parser.Token)null;
                // StatementAdderMethod cloned existing statement
                p.expectNotNull(vc_2750, vc_2752);
                org.junit.Assert.fail("keywordGluedToLitteral_cf7469 should have thrown IllegalStateException");
            } catch (java.lang.IllegalStateException eee) {
            }
            org.junit.Assert.fail("keywordGluedToLitteral_cf7469_failAssert35_literalMutation8188 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#lbcore193 */
    @org.junit.Test(timeout = 10000)
    public void lbcore193_cf9851_failAssert23_literalMutation11045() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            try {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("IX(w]wREJ%");
                p.setContext(context);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                org.junit.Assert.fail("where the is exception?");
            } catch (ch.qos.logback.core.spi.ScanException ise) {
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.Parser vc_3050 = (ch.qos.logback.core.pattern.parser.Parser)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_3050);
                // StatementAdderMethod cloned existing statement
                vc_3050.getNextToken();
                // MethodAssertGenerator build local variable
                Object o_14_0 = ise.getMessage();
            }
            ch.qos.logback.core.status.StatusChecker sc = new ch.qos.logback.core.status.StatusChecker(context);
            sc.assertContainsMatch("Expecting RIGHT_PARENTHESIS");
            sc.assertContainsMatch(("See also " + (ch.qos.logback.core.pattern.parser.Parser.MISSING_RIGHT_PARENTHESIS)));
            org.junit.Assert.fail("lbcore193_cf9851 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testBasic */
    @org.junit.Test(timeout = 10000)
    public void testBasic_cf22516_failAssert13() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            // MethodAssertGenerator build local variable
            Object o_5_0 = t.getType();
            // StatementAdderMethod cloned existing statement
            p.C();
            // MethodAssertGenerator build local variable
            Object o_9_0 = t.getValue();
            org.junit.Assert.fail("testBasic_cf22516 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testBasic */
    @org.junit.Test(timeout = 10000)
    public void testBasic_cf22522_failAssert17() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            // MethodAssertGenerator build local variable
            Object o_5_0 = t.getType();
            // StatementAdderOnAssert create null value
            java.lang.String vc_6878 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            p.COMPOSITE(vc_6878);
            // MethodAssertGenerator build local variable
            Object o_11_0 = t.getValue();
            org.junit.Assert.fail("testBasic_cf22522 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testBasic */
    @org.junit.Test(timeout = 10000)
    public void testBasic_cf22537_cf22715_failAssert33() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            // MethodAssertGenerator build local variable
            Object o_5_0 = t.getType();
            // AssertGenerator replace invocation
            ch.qos.logback.core.pattern.parser.Node o_testBasic_cf22537__7 = // StatementAdderMethod cloned existing statement
p.Eopt();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_testBasic_cf22537__7);
            // StatementAdderMethod cloned existing statement
            p.C();
            // MethodAssertGenerator build local variable
            Object o_13_0 = t.getValue();
            org.junit.Assert.fail("testBasic_cf22537_cf22715 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testBasic */
    @org.junit.Test(timeout = 10000)
    public void testBasic_cf22537_cf22764_failAssert9() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            // MethodAssertGenerator build local variable
            Object o_5_0 = t.getType();
            // AssertGenerator replace invocation
            ch.qos.logback.core.pattern.parser.Node o_testBasic_cf22537__7 = // StatementAdderMethod cloned existing statement
p.Eopt();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_testBasic_cf22537__7);
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_6935 = new java.lang.String();
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.pattern.parser.Token vc_6932 = (ch.qos.logback.core.pattern.parser.Token)null;
            // StatementAdderMethod cloned existing statement
            p.expectNotNull(vc_6932, vc_6935);
            // MethodAssertGenerator build local variable
            Object o_17_0 = t.getValue();
            org.junit.Assert.fail("testBasic_cf22537_cf22764 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testBasic */
    @org.junit.Test(timeout = 10000)
    public void testBasic_cf22540_failAssert26_literalMutation22788() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("!M1hl");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "!M1hl");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            // MethodAssertGenerator build local variable
            Object o_5_0 = t.getType();
            // StatementAdderMethod cloned existing statement
            p.T();
            // MethodAssertGenerator build local variable
            Object o_9_0 = t.getValue();
            org.junit.Assert.fail("testBasic_cf22540 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testBasic */
    @org.junit.Test(timeout = 10000)
    public void testBasic_cf22546_cf22863_cf23214_failAssert67() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            // MethodAssertGenerator build local variable
            Object o_5_0 = t.getType();
            // AssertGenerator replace invocation
            ch.qos.logback.core.pattern.parser.Token o_testBasic_cf22546__7 = // StatementAdderMethod cloned existing statement
p.getCurentToken();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_testBasic_cf22546__7);
            // AssertGenerator replace invocation
            ch.qos.logback.core.pattern.parser.Node o_testBasic_cf22546_cf22863__11 = // StatementAdderMethod cloned existing statement
p.Eopt();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_testBasic_cf22546_cf22863__11);
            // StatementAdderMethod cloned existing statement
            p.C();
            // MethodAssertGenerator build local variable
            Object o_17_0 = t.getValue();
            org.junit.Assert.fail("testBasic_cf22546_cf22863_cf23214 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testBasic */
    @org.junit.Test(timeout = 10000)
    public void testBasic_cf22546_cf22872_cf24351_failAssert21() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            // MethodAssertGenerator build local variable
            Object o_5_0 = t.getType();
            // AssertGenerator replace invocation
            ch.qos.logback.core.pattern.parser.Token o_testBasic_cf22546__7 = // StatementAdderMethod cloned existing statement
p.getCurentToken();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_testBasic_cf22546__7);
            // AssertGenerator replace invocation
            ch.qos.logback.core.pattern.parser.Token o_testBasic_cf22546_cf22872__11 = // StatementAdderMethod cloned existing statement
p.getCurentToken();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_testBasic_cf22546_cf22872__11);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_428 = "hello";
            // StatementAdderMethod cloned existing statement
            p.COMPOSITE(String_vc_428);
            // MethodAssertGenerator build local variable
            Object o_19_0 = t.getValue();
            org.junit.Assert.fail("testBasic_cf22546_cf22872_cf24351 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testBasic */
    @org.junit.Test(timeout = 10000)
    public void testBasic_cf22549_cf22972_failAssert36_literalMutation23735() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("helo");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "helo");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            // MethodAssertGenerator build local variable
            Object o_5_0 = t.getType();
            // AssertGenerator replace invocation
            ch.qos.logback.core.pattern.parser.Token o_testBasic_cf22549__7 = // StatementAdderMethod cloned existing statement
p.getNextToken();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_testBasic_cf22549__7);
            // StatementAdderMethod cloned existing statement
            p.parse();
            // MethodAssertGenerator build local variable
            Object o_13_0 = t.getValue();
            org.junit.Assert.fail("testBasic_cf22549_cf22972 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testBasic */
    @org.junit.Test(timeout = 10000)
    public void testBasic_cf22551_failAssert31_add23007_literalMutation23433_failAssert24() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hel\\lo");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                // MethodAssertGenerator build local variable
                Object o_5_0 = t.getType();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_5_0, 0);
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.Parser vc_6894 = (ch.qos.logback.core.pattern.parser.Parser)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_6894);
                // StatementAdderMethod cloned existing statement
                // MethodCallAdder
                vc_6894.advanceTokenPointer();
                // StatementAdderMethod cloned existing statement
                vc_6894.advanceTokenPointer();
                // MethodAssertGenerator build local variable
                Object o_11_0 = t.getValue();
                org.junit.Assert.fail("testBasic_cf22551 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testBasic_cf22551_failAssert31_add23007_literalMutation23433 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testBasic */
    @org.junit.Test(timeout = 10000)
    public void testBasic_cf22552() throws java.lang.Exception {
        ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello");
        ch.qos.logback.core.pattern.parser.Node t = p.parse();
        org.junit.Assert.assertEquals(ch.qos.logback.core.pattern.parser.Node.LITERAL, t.getType());
        // StatementAdderMethod cloned existing statement
        p.advanceTokenPointer();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
        org.junit.Assert.assertEquals("hello", t.getValue());
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testBasic */
    @org.junit.Test(timeout = 10000)
    public void testBasic_cf22562_failAssert35() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            // MethodAssertGenerator build local variable
            Object o_5_0 = t.getType();
            // StatementAdderOnAssert create null value
            java.lang.String vc_6900 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.pattern.parser.Token vc_6898 = (ch.qos.logback.core.pattern.parser.Token)null;
            // StatementAdderMethod cloned existing statement
            p.expectNotNull(vc_6898, vc_6900);
            // MethodAssertGenerator build local variable
            Object o_13_0 = t.getValue();
            org.junit.Assert.fail("testBasic_cf22562 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testCompositeFormatting */
    @org.junit.Test(timeout = 10000)
    public void testCompositeFormatting_cf44080_failAssert21_literalMutation44238() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%5(XYZ)");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
            ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
            composite.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, java.lang.Integer.MAX_VALUE));
            ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "XYZ");
            composite.setChildNode(child);
            witness.next = composite;
            // StatementAdderOnAssert create null value
            java.util.Map vc_7484 = (java.util.Map)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_7484);
            // StatementAddOnAssert local variable replacement
            ch.qos.logback.core.pattern.parser.Node c = child;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(c.equals(child));
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)c).getNext());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)c).getValue(), "XYZ");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)c).getType(), 0);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.pattern.parser.Parser vc_7480 = (ch.qos.logback.core.pattern.parser.Parser)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_7480);
            // StatementAdderMethod cloned existing statement
            vc_7480.compile(c, vc_7484);
            org.junit.Assert.fail("testCompositeFormatting_cf44080 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testCompositeFormatting */
    @org.junit.Test(timeout = 10000)
    public void testCompositeFormatting_cf44101_failAssert24() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%5(XYZ)");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
            ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
            composite.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(5, java.lang.Integer.MAX_VALUE));
            ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "XYZ");
            composite.setChildNode(child);
            witness.next = composite;
            // StatementAdderMethod cloned existing statement
            p.C();
            org.junit.Assert.fail("testCompositeFormatting_cf44101 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testCompositeFormatting */
    @org.junit.Test(timeout = 10000)
    public void testCompositeFormatting_cf44109_failAssert29_literalMutation44421_literalMutation47518() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("bH6cBeMD,6`&");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "bH6cBeMD,6`&");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
            ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
            composite.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(10, java.lang.Integer.MAX_VALUE));
            ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "XYZ");
            composite.setChildNode(child);
            witness.next = composite;
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_440 = "hello";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_440, "hello");
            // StatementAdderMethod cloned existing statement
            p.COMPOSITE(String_vc_440);
            org.junit.Assert.fail("testCompositeFormatting_cf44109 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testCompositeFormatting */
    @org.junit.Test(timeout = 10000)
    public void testCompositeFormatting_cf44116_failAssert32_literalMutation44489_literalMutation47611_failAssert20() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("GF^1L:(bd <%");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "C,u/.");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)witness).getValue(), "C,u/.");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)witness).getType(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)witness).getNext());
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                composite.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(5, java.lang.Integer.MAX_VALUE));
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "XYZ");
                composite.setChildNode(child);
                witness.next = composite;
                // StatementAdderMethod cloned existing statement
                p.SINGLE();
                org.junit.Assert.fail("testCompositeFormatting_cf44116 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testCompositeFormatting_cf44116_failAssert32_literalMutation44489_literalMutation47611 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testCompositeFormatting */
    @org.junit.Test(timeout = 10000)
    public void testCompositeFormatting_cf44119_failAssert34_literalMutation44526() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("*4w}wEG;$irs");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "*4w}wEG;$irs");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
            ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
            composite.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(5, java.lang.Integer.MAX_VALUE));
            ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "XYZ");
            composite.setChildNode(child);
            witness.next = composite;
            // StatementAdderMethod cloned existing statement
            p.E();
            org.junit.Assert.fail("testCompositeFormatting_cf44119 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testCompositeFormatting */
    @org.junit.Test(timeout = 10000)
    public void testCompositeFormatting_cf44125_failAssert37() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%5(XYZ)");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
            ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
            composite.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(5, java.lang.Integer.MAX_VALUE));
            ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "XYZ");
            composite.setChildNode(child);
            witness.next = composite;
            // StatementAdderMethod cloned existing statement
            p.T();
            org.junit.Assert.fail("testCompositeFormatting_cf44125 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testCompositeFormatting */
    @org.junit.Test(timeout = 10000)
    public void testCompositeFormatting_cf44137() throws java.lang.Exception {
        ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%5(XYZ)");
        ch.qos.logback.core.pattern.parser.Node t = p.parse();
        ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
        ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
        composite.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(5, java.lang.Integer.MAX_VALUE));
        ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "XYZ");
        composite.setChildNode(child);
        witness.next = composite;
        // StatementAdderMethod cloned existing statement
        p.advanceTokenPointer();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
        org.junit.Assert.assertEquals(witness, t);
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testCompositeFormatting */
    @org.junit.Test(timeout = 10000)
    public void testCompositeFormatting_cf44147_failAssert46() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%5(XYZ)");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
            ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
            composite.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(5, java.lang.Integer.MAX_VALUE));
            ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "XYZ");
            composite.setChildNode(child);
            witness.next = composite;
            // StatementAdderOnAssert create null value
            java.lang.String vc_7512 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.pattern.parser.Token vc_7510 = (ch.qos.logback.core.pattern.parser.Token)null;
            // StatementAdderMethod cloned existing statement
            p.expectNotNull(vc_7510, vc_7512);
            org.junit.Assert.fail("testCompositeFormatting_cf44147 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testComposite */
    @org.junit.Test(timeout = 10000)
    public void testComposite_cf24536_failAssert103() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child)");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                composite.setChildNode(child);
                witness.next = composite;
            }
            // System.out.println("testRecursive part 2");
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child )");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                composite.setChildNode(child);
                witness.next = composite;
                child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child %h)");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                composite.setChildNode(child);
                child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                child.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
                witness.next = composite;
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child %h) %m");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                composite.setChildNode(child);
                child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                child.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
                witness.next = composite;
                composite.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                composite.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("m");
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%( %child \\(%h\\) ) %m");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                composite.setChildNode(child);
                ch.qos.logback.core.pattern.parser.Node c = child;
                c = c.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                c = c.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " (");
                c = c.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
                c = c.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, ") ");
                witness.next = composite;
                composite.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                composite.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("m");
                // StatementAdderMethod cloned existing statement
                p.C();
            }
            org.junit.Assert.fail("testComposite_cf24536 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testComposite */
    @org.junit.Test(timeout = 10000)
    public void testComposite_cf24542_failAssert107_literalMutation26253_literalMutation40810_failAssert19() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child)");
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                    ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                    ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                    composite.setChildNode(child);
                    witness.next = composite;
                }
                // System.out.println("testRecursive part 2");
                {
                    ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child )");
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                    ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                    ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                    composite.setChildNode(child);
                    witness.next = composite;
                    child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child %h)");
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                    ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                    ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                    composite.setChildNode(child);
                    child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                    child.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
                    witness.next = composite;
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child %h) %m");
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                    ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                    ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("chi1ld");
                    // AssertGenerator add assertion
                    org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)child).getValue(), "chi1ld");
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)child).getFormatInfo());
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)child).getOptions());
                    // AssertGenerator add assertion
                    org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)child).getType(), 1);
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)child).getNext());
                    composite.setChildNode(child);
                    child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                    child.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
                    witness.next = composite;
                    composite.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                    composite.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("m");
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%( %child \\(%h\\ ) %m");
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                    ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                    ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                    composite.setChildNode(child);
                    ch.qos.logback.core.pattern.parser.Node c = child;
                    c = c.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                    c = c.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " (");
                    c = c.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
                    c = c.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, ") ");
                    witness.next = composite;
                    composite.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                    composite.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("m");
                    // StatementAdderOnAssert create null value
                    java.lang.String vc_7320 = (java.lang.String)null;
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(vc_7320);
                    // StatementAdderMethod cloned existing statement
                    p.COMPOSITE(vc_7320);
                }
                org.junit.Assert.fail("testComposite_cf24542 should have thrown IllegalStateException");
            } catch (java.lang.IllegalStateException eee) {
            }
            org.junit.Assert.fail("testComposite_cf24542_failAssert107_literalMutation26253_literalMutation40810 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testComposite */
    @org.junit.Test(timeout = 10000)
    public void testComposite_cf24554_failAssert113() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child)");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                composite.setChildNode(child);
                witness.next = composite;
            }
            // System.out.println("testRecursive part 2");
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child )");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                composite.setChildNode(child);
                witness.next = composite;
                child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child %h)");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                composite.setChildNode(child);
                child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                child.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
                witness.next = composite;
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child %h) %m");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                composite.setChildNode(child);
                child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                child.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
                witness.next = composite;
                composite.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                composite.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("m");
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%( %child \\(%h\\) ) %m");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                composite.setChildNode(child);
                ch.qos.logback.core.pattern.parser.Node c = child;
                c = c.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                c = c.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " (");
                c = c.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
                c = c.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, ") ");
                witness.next = composite;
                composite.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                composite.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("m");
                // StatementAdderMethod cloned existing statement
                p.E();
            }
            org.junit.Assert.fail("testComposite_cf24554 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testComposite */
    @org.junit.Test(timeout = 10000)
    public void testComposite_cf24554_failAssert113_literalMutation26835() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("ezvrGEreU<<1A*");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "ezvrGEreU<<1A*");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                composite.setChildNode(child);
                witness.next = composite;
            }
            // System.out.println("testRecursive part 2");
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child )");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                composite.setChildNode(child);
                witness.next = composite;
                child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child %h)");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                composite.setChildNode(child);
                child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                child.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
                witness.next = composite;
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child %h) %m");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                composite.setChildNode(child);
                child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                child.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
                witness.next = composite;
                composite.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                composite.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("m");
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%( %child \\(%h\\) ) %m");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                composite.setChildNode(child);
                ch.qos.logback.core.pattern.parser.Node c = child;
                c = c.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                c = c.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " (");
                c = c.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
                c = c.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, ") ");
                witness.next = composite;
                composite.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                composite.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("m");
                // StatementAdderMethod cloned existing statement
                p.E();
            }
            org.junit.Assert.fail("testComposite_cf24554 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testComposite */
    @org.junit.Test(timeout = 10000)
    public void testComposite_cf24563_failAssert118_literalMutation27599_literalMutation39579_failAssert13() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child)");
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                    ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                    ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                    composite.setChildNode(child);
                    witness.next = composite;
                }
                // System.out.println("testRecursive part 2");
                {
                    ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%c{ild )");
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                    ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                    ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                    composite.setChildNode(child);
                    witness.next = composite;
                    child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child %h)");
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                    ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                    ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("ch%ild");
                    // AssertGenerator add assertion
                    org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)child).getValue(), "ch%ild");
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)child).getFormatInfo());
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)child).getOptions());
                    // AssertGenerator add assertion
                    org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)child).getType(), 1);
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)child).getNext());
                    composite.setChildNode(child);
                    child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                    child.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
                    witness.next = composite;
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child %h) %m");
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                    ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                    ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                    composite.setChildNode(child);
                    child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                    child.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
                    witness.next = composite;
                    composite.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                    composite.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("m");
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%( %child \\(%h\\) ) %m");
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                    ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                    ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                    composite.setChildNode(child);
                    ch.qos.logback.core.pattern.parser.Node c = child;
                    c = c.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                    c = c.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " (");
                    c = c.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
                    c = c.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, ") ");
                    witness.next = composite;
                    composite.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                    composite.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("m");
                    // StatementAdderMethod cloned existing statement
                    p.parse();
                }
                org.junit.Assert.fail("testComposite_cf24563 should have thrown IllegalStateException");
            } catch (java.lang.IllegalStateException eee) {
            }
            org.junit.Assert.fail("testComposite_cf24563_failAssert118_literalMutation27599_literalMutation39579 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testComposite */
    @org.junit.Test(timeout = 10000)
    public void testComposite_cf24572() throws java.lang.Exception {
        {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child)");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
            ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
            ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
            composite.setChildNode(child);
            witness.next = composite;
            // System.out.println("w:" + witness);
            // System.out.println(t);
            org.junit.Assert.assertEquals(witness, t);
        }
        // System.out.println("testRecursive part 2");
        {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child )");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
            ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
            ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
            composite.setChildNode(child);
            witness.next = composite;
            child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
            org.junit.Assert.assertEquals(witness, t);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child %h)");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
            ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
            ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
            composite.setChildNode(child);
            child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
            child.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
            witness.next = composite;
            org.junit.Assert.assertEquals(witness, t);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child %h) %m");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
            ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
            ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
            composite.setChildNode(child);
            child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
            child.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
            witness.next = composite;
            composite.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
            composite.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("m");
            org.junit.Assert.assertEquals(witness, t);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%( %child \\(%h\\) ) %m");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
            ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
            ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
            composite.setChildNode(child);
            ch.qos.logback.core.pattern.parser.Node c = child;
            c = c.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
            c = c.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " (");
            c = c.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
            c = c.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, ") ");
            witness.next = composite;
            composite.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
            composite.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("m");
            // StatementAdderMethod cloned existing statement
            p.advanceTokenPointer();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
            org.junit.Assert.assertEquals(witness, t);
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testComposite */
    @org.junit.Test(timeout = 10000)
    public void testComposite_cf24577_failAssert124_literalMutation28796_failAssert17() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child)");
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                    ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                    ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                    composite.setChildNode(child);
                    witness.next = composite;
                }
                // System.out.println("testRecursive part 2");
                {
                    ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child )");
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                    ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                    ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                    composite.setChildNode(child);
                    witness.next = composite;
                    child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("h4%;w=`uO#ew?n+\\q");
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                    ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                    ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                    composite.setChildNode(child);
                    child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                    child.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
                    witness.next = composite;
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child %h) %m");
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                    ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                    ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                    composite.setChildNode(child);
                    child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                    child.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
                    witness.next = composite;
                    composite.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                    composite.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("m");
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%( %child \\(%h\\) ) %m");
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                    ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                    ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                    composite.setChildNode(child);
                    ch.qos.logback.core.pattern.parser.Node c = child;
                    c = c.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                    c = c.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " (");
                    c = c.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
                    c = c.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, ") ");
                    witness.next = composite;
                    composite.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                    composite.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("m");
                    // StatementAdderOnAssert create random local variable
                    java.lang.String vc_7343 = new java.lang.String();
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.Token vc_7340 = (ch.qos.logback.core.pattern.parser.Token)null;
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.Parser vc_7338 = (ch.qos.logback.core.pattern.parser.Parser)null;
                    // StatementAdderMethod cloned existing statement
                    vc_7338.expectNotNull(vc_7340, vc_7343);
                }
                org.junit.Assert.fail("testComposite_cf24577 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testComposite_cf24577_failAssert124_literalMutation28796 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testComposite */
    @org.junit.Test(timeout = 10000)
    public void testComposite_cf24584_failAssert126_literalMutation29026_literalMutation33800() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("4RACWgV_uQi>/q");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "4RACWgV_uQi>/q");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                composite.setChildNode(child);
                witness.next = composite;
            }
            // System.out.println("testRecursive part 2");
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child )");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                composite.setChildNode(child);
                witness.next = composite;
                child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child %h)");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                composite.setChildNode(child);
                child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                child.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)child.next.next).getNext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)child.next.next).getType(), 1);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)child.next.next).getFormatInfo());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)child.next.next).getOptions());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)child.next.next).getValue(), "");
                witness.next = composite;
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child %h) %m");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                composite.setChildNode(child);
                child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                child.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
                witness.next = composite;
                composite.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                composite.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("m");
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%( %child \\(%h\\) ) %m");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                composite.setChildNode(child);
                ch.qos.logback.core.pattern.parser.Node c = child;
                c = c.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                c = c.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " (");
                c = c.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
                c = c.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, ") ");
                witness.next = composite;
                composite.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                composite.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("m");
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_431 = "hello";
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(String_vc_431, "hello");
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.Token vc_7340 = (ch.qos.logback.core.pattern.parser.Token)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_7340);
                // StatementAdderMethod cloned existing statement
                p.expectNotNull(vc_7340, String_vc_431);
            }
            org.junit.Assert.fail("testComposite_cf24584 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testComposite */
    @org.junit.Test
    public void testComposite_literalMutation24452_failAssert36() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child)");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                composite.setChildNode(child);
                witness.next = composite;
            }
            // System.out.println("testRecursive part 2");
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child )");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                composite.setChildNode(child);
                witness.next = composite;
                child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child %)");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                composite.setChildNode(child);
                child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                child.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
                witness.next = composite;
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%(%child %h) %m");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                composite.setChildNode(child);
                child.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                child.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
                witness.next = composite;
                composite.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                composite.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("m");
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%( %child \\(%h\\) ) %m");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                composite.setChildNode(child);
                ch.qos.logback.core.pattern.parser.Node c = child;
                c = c.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                c = c.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " (");
                c = c.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h");
                c = c.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, ") ");
                witness.next = composite;
                composite.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                composite.next.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("m");
            }
            org.junit.Assert.fail("testComposite_literalMutation24452 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testFormattingInfo */
    @org.junit.Test(timeout = 10000)
    public void testFormattingInfo_cf48360_failAssert88() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%45x");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(45, java.lang.Integer.MAX_VALUE));
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%4.5x");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5));
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%-4.5x");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5, false, true));
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%-4.-5x");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5, false, false));
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%-4.5x %12y");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5, false, true));
                ch.qos.logback.core.pattern.parser.Node n = witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                n = n.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("y");
                ((ch.qos.logback.core.pattern.parser.FormattingNode) (n)).setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(12, java.lang.Integer.MAX_VALUE));
                // StatementAdderMethod cloned existing statement
                p.C();
            }
            org.junit.Assert.fail("testFormattingInfo_cf48360 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testFormattingInfo */
    @org.junit.Test(timeout = 10000)
    public void testFormattingInfo_cf48368_failAssert93() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%45x");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(45, java.lang.Integer.MAX_VALUE));
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%4.5x");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5));
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%-4.5x");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5, false, true));
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%-4.-5x");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5, false, false));
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%-4.5x %12y");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5, false, true));
                ch.qos.logback.core.pattern.parser.Node n = witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                n = n.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("y");
                ((ch.qos.logback.core.pattern.parser.FormattingNode) (n)).setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(12, java.lang.Integer.MAX_VALUE));
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_454 = "%-4.5x %12y";
                // StatementAdderMethod cloned existing statement
                p.COMPOSITE(String_vc_454);
            }
            org.junit.Assert.fail("testFormattingInfo_cf48368 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testFormattingInfo */
    @org.junit.Test(timeout = 10000)
    public void testFormattingInfo_cf48368_failAssert93_literalMutation50534() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%45x");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(45, java.lang.Integer.MAX_VALUE));
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("(+zf,");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "(+zf,");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5));
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%-4.5x");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5, false, true));
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%-4.-5x");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5, false, false));
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%-4.5x %12y");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5, false, true));
                ch.qos.logback.core.pattern.parser.Node n = witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                n = n.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("y");
                ((ch.qos.logback.core.pattern.parser.FormattingNode) (n)).setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(12, java.lang.Integer.MAX_VALUE));
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_454 = "%-4.5x %12y";
                // StatementAdderMethod cloned existing statement
                p.COMPOSITE(String_vc_454);
            }
            org.junit.Assert.fail("testFormattingInfo_cf48368 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testFormattingInfo */
    @org.junit.Test(timeout = 10000)
    public void testFormattingInfo_cf48396() throws java.lang.Exception {
        {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%45x");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
            witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(45, java.lang.Integer.MAX_VALUE));
            org.junit.Assert.assertEquals(witness, t);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%4.5x");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
            witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5));
            org.junit.Assert.assertEquals(witness, t);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%-4.5x");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
            witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5, false, true));
            org.junit.Assert.assertEquals(witness, t);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%-4.-5x");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
            witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5, false, false));
            org.junit.Assert.assertEquals(witness, t);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%-4.5x %12y");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
            witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5, false, true));
            ch.qos.logback.core.pattern.parser.Node n = witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
            n = n.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("y");
            ((ch.qos.logback.core.pattern.parser.FormattingNode) (n)).setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(12, java.lang.Integer.MAX_VALUE));
            // StatementAdderMethod cloned existing statement
            p.advanceTokenPointer();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
            org.junit.Assert.assertEquals(witness, t);
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testFormattingInfo */
    @org.junit.Test(timeout = 10000)
    public void testFormattingInfo_cf48406_failAssert110() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%45x");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(45, java.lang.Integer.MAX_VALUE));
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%4.5x");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5));
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%-4.5x");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5, false, true));
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%-4.-5x");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5, false, false));
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%-4.5x %12y");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5, false, true));
                ch.qos.logback.core.pattern.parser.Node n = witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                n = n.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("y");
                ((ch.qos.logback.core.pattern.parser.FormattingNode) (n)).setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(12, java.lang.Integer.MAX_VALUE));
                // StatementAdderOnAssert create null value
                java.lang.String vc_7750 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.Token vc_7748 = (ch.qos.logback.core.pattern.parser.Token)null;
                // StatementAdderMethod cloned existing statement
                p.expectNotNull(vc_7748, vc_7750);
            }
            org.junit.Assert.fail("testFormattingInfo_cf48406 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testFormattingInfo */
    @org.junit.Test(timeout = 10000)
    public void testFormattingInfo_cf48408_failAssert111_literalMutation52911() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%45x");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(45, java.lang.Integer.MAX_VALUE));
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%.5x");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "x");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.pattern.FormatInfo)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getFormatInfo()).isLeftTruncate());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.FormatInfo)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getFormatInfo()).getMax(), 5);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 1);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getOptions());
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.pattern.FormatInfo)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getFormatInfo()).isLeftPad());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.FormatInfo)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getFormatInfo()).getMin(), -2147483648);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5));
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%-4.5x");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5, false, true));
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%-4.-5x");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5, false, false));
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%-4.5x %12y");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5, false, true));
                ch.qos.logback.core.pattern.parser.Node n = witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                n = n.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("y");
                ((ch.qos.logback.core.pattern.parser.FormattingNode) (n)).setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(12, java.lang.Integer.MAX_VALUE));
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_455 = "y";
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.Token vc_7748 = (ch.qos.logback.core.pattern.parser.Token)null;
                // StatementAdderMethod cloned existing statement
                p.expectNotNull(vc_7748, String_vc_455);
            }
            org.junit.Assert.fail("testFormattingInfo_cf48408 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testFormattingInfo */
    @org.junit.Test(timeout = 10000)
    public void testFormattingInfo_cf48409_failAssert112_literalMutation53064_literalMutation55942() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("yp[[");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "yp[[");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(45, java.lang.Integer.MAX_VALUE));
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%4.5x");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5));
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%-4.5x");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5, false, true));
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%-4.-5x");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 5, false, false));
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%-4.5x %12y");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.FormattingNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(4, 10, false, true));
                ch.qos.logback.core.pattern.parser.Node n = witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                n = n.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("y");
                ((ch.qos.logback.core.pattern.parser.FormattingNode) (n)).setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(12, java.lang.Integer.MAX_VALUE));
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_7751 = new java.lang.String();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(vc_7751, "");
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.Token vc_7748 = (ch.qos.logback.core.pattern.parser.Token)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_7748);
                // StatementAdderMethod cloned existing statement
                p.expectNotNull(vc_7748, vc_7751);
            }
            org.junit.Assert.fail("testFormattingInfo_cf48409 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testKeyword */
    @org.junit.Test(timeout = 10000)
    public void testKeyword_cf62186_failAssert35() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%xyz");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                witness.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("xyz");
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%xyz{x}");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.SimpleKeywordNode n = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("xyz");
                java.util.List<java.lang.String> optionList = new java.util.ArrayList<java.lang.String>();
                optionList.add("x");
                n.setOptions(optionList);
                witness.next = n;
                // StatementAdderMethod cloned existing statement
                p.C();
            }
            org.junit.Assert.fail("testKeyword_cf62186 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testKeyword */
    @org.junit.Test(timeout = 10000)
    public void testKeyword_cf62191_failAssert38_literalMutation62580_failAssert27() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("\\uo)|yV<4");
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                    witness.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("xyz");
                }
                {
                    ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%xyz{x}");
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                    ch.qos.logback.core.pattern.parser.SimpleKeywordNode n = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("xyz");
                    java.util.List<java.lang.String> optionList = new java.util.ArrayList<java.lang.String>();
                    optionList.add("x");
                    n.setOptions(optionList);
                    witness.next = n;
                    // StatementAdderOnAssert create random local variable
                    java.lang.String vc_7899 = new java.lang.String();
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.Parser vc_7896 = (ch.qos.logback.core.pattern.parser.Parser)null;
                    // StatementAdderMethod cloned existing statement
                    vc_7896.COMPOSITE(vc_7899);
                }
                org.junit.Assert.fail("testKeyword_cf62191 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testKeyword_cf62191_failAssert38_literalMutation62580 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testKeyword */
    @org.junit.Test(timeout = 10000)
    public void testKeyword_cf62210_failAssert48() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%xyz");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                witness.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("xyz");
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%xyz{x}");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.SimpleKeywordNode n = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("xyz");
                java.util.List<java.lang.String> optionList = new java.util.ArrayList<java.lang.String>();
                optionList.add("x");
                n.setOptions(optionList);
                witness.next = n;
                // StatementAdderMethod cloned existing statement
                p.T();
            }
            org.junit.Assert.fail("testKeyword_cf62210 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testKeyword */
    @org.junit.Test(timeout = 10000)
    public void testKeyword_cf62216_cf63241_failAssert30_literalMutation66927() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("Mn_ mX6{,");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "Mn_ mX6{,");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                witness.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("xyz");
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%xyz{x}");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.SimpleKeywordNode n = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("xyz");
                java.util.List<java.lang.String> optionList = new java.util.ArrayList<java.lang.String>();
                optionList.add("x");
                n.setOptions(optionList);
                witness.next = n;
                // AssertGenerator replace invocation
                ch.qos.logback.core.pattern.parser.Token o_testKeyword_cf62216__25 = // StatementAdderMethod cloned existing statement
p.getCurentToken();
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(o_testKeyword_cf62216__25);
                // StatementAdderMethod cloned existing statement
                p.T();
            }
            org.junit.Assert.fail("testKeyword_cf62216_cf63241 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testKeyword */
    @org.junit.Test(timeout = 10000)
    public void testKeyword_cf62222() throws java.lang.Exception {
        {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%xyz");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
            witness.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("xyz");
            org.junit.Assert.assertEquals(witness, t);
        }
        {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%xyz{x}");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
            ch.qos.logback.core.pattern.parser.SimpleKeywordNode n = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("xyz");
            java.util.List<java.lang.String> optionList = new java.util.ArrayList<java.lang.String>();
            optionList.add("x");
            n.setOptions(optionList);
            witness.next = n;
            // StatementAdderMethod cloned existing statement
            p.advanceTokenPointer();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
            org.junit.Assert.assertEquals(witness, t);
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testKeyword */
    @org.junit.Test(timeout = 10000)
    public void testKeyword_cf62235_failAssert59_literalMutation63765() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("`T  [ _|^");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "`T  [ _|^");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                witness.next = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("xyz");
            }
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("hello%xyz{x}");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "hello");
                ch.qos.logback.core.pattern.parser.SimpleKeywordNode n = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("xyz");
                java.util.List<java.lang.String> optionList = new java.util.ArrayList<java.lang.String>();
                optionList.add("x");
                n.setOptions(optionList);
                witness.next = n;
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_7921 = new java.lang.String();
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.Token vc_7918 = (ch.qos.logback.core.pattern.parser.Token)null;
                // StatementAdderMethod cloned existing statement
                p.expectNotNull(vc_7918, vc_7921);
            }
            org.junit.Assert.fail("testKeyword_cf62235 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testNested */
    @org.junit.Test(timeout = 10000)
    public void testNested_cf68132_failAssert23_literalMutation68346() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%top %(%child%(%h))");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("tp");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)witness).getNext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)witness).getType(), 1);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)witness).getValue(), "tp");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)witness).getOptions());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)witness).getFormatInfo());
                ch.qos.logback.core.pattern.parser.Node w = witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                w = w.next = composite;
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                composite.setChildNode(child);
                composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                child.next = composite;
                composite.setChildNode(new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h"));
                // StatementAdderOnAssert create null value
                java.util.Map vc_8130 = (java.util.Map)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_8130);
                // StatementAddOnAssert local variable replacement
                ch.qos.logback.core.pattern.parser.Node c = child;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getValue(), "child");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.CompositeNode)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getNext()).getChildNode()).getFormatInfo());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.CompositeNode)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getNext()).getOptions());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.CompositeNode)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getNext()).getType(), 2);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.CompositeNode)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getNext()).getChildNode()).getType(), 1);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.CompositeNode)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getNext()).getValue(), "BARE");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.CompositeNode)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getNext()).getFormatInfo());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getFormatInfo());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.CompositeNode)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getNext()).getChildNode()).getOptions());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.CompositeNode)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getNext()).getChildNode()).getValue(), "h");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getOptions());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.CompositeNode)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getNext()).getNext());
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(c.equals(child));
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getNext().equals(child.next));
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getNext().equals(composite));
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.CompositeNode)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getNext()).getChildNode()).getNext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getType(), 1);
                // StatementAdderMethod cloned existing statement
                p.compile(c, vc_8130);
            }
            org.junit.Assert.fail("testNested_cf68132 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testNested */
    @org.junit.Test(timeout = 10000)
    public void testNested_cf68145_failAssert25() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%top %(%child%(%h))");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("top");
                ch.qos.logback.core.pattern.parser.Node w = witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                w = w.next = composite;
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                composite.setChildNode(child);
                composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                child.next = composite;
                composite.setChildNode(new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h"));
                // StatementAdderMethod cloned existing statement
                p.C();
            }
            org.junit.Assert.fail("testNested_cf68145 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testNested */
    @org.junit.Test(timeout = 10000)
    public void testNested_cf68151_failAssert29_literalMutation68488_literalMutation69624() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser(" !+Qrr3k4GkkI`p_&<M");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), " !+Qrr3k4GkkI`p_&<M");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("top");
                ch.qos.logback.core.pattern.parser.Node w = witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                w = w.next = composite;
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("chld");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)child).getValue(), "chld");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)child).getFormatInfo());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)child).getOptions());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)child).getType(), 1);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)child).getNext());
                composite.setChildNode(child);
                composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                child.next = composite;
                composite.setChildNode(new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h"));
                // StatementAdderOnAssert create null value
                java.lang.String vc_8136 = (java.lang.String)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_8136);
                // StatementAdderMethod cloned existing statement
                p.COMPOSITE(vc_8136);
            }
            org.junit.Assert.fail("testNested_cf68151 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testNested */
    @org.junit.Test(timeout = 10000)
    public void testNested_cf68153_failAssert30() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%top %(%child%(%h))");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("top");
                ch.qos.logback.core.pattern.parser.Node w = witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                w = w.next = composite;
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                composite.setChildNode(child);
                composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                child.next = composite;
                composite.setChildNode(new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h"));
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_478 = "%top %(%child%(%h))";
                // StatementAdderMethod cloned existing statement
                p.COMPOSITE(String_vc_478);
            }
            org.junit.Assert.fail("testNested_cf68153 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testNested */
    @org.junit.Test(timeout = 10000)
    public void testNested_cf68153_failAssert30_literalMutation68499_failAssert29() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("S]:<v[8M@P9-Oxm)\\Uf");
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("top");
                    ch.qos.logback.core.pattern.parser.Node w = witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                    ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                    w = w.next = composite;
                    ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                    composite.setChildNode(child);
                    composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                    child.next = composite;
                    composite.setChildNode(new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h"));
                    // StatementAdderOnAssert create literal from method
                    java.lang.String String_vc_478 = "%top %(%child%(%h))";
                    // StatementAdderMethod cloned existing statement
                    p.COMPOSITE(String_vc_478);
                }
                org.junit.Assert.fail("testNested_cf68153 should have thrown IllegalStateException");
            } catch (java.lang.IllegalStateException eee) {
            }
            org.junit.Assert.fail("testNested_cf68153_failAssert30_literalMutation68499 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testNested */
    @org.junit.Test(timeout = 10000)
    public void testNested_cf68168_failAssert37_literalMutation68770_literalMutation72469_failAssert30() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("\\Mmc,#cCJr$O<R<T$^J");
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("eop");
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)witness).getNext());
                    // AssertGenerator add assertion
                    org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)witness).getType(), 1);
                    // AssertGenerator add assertion
                    org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)witness).getValue(), "eop");
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)witness).getOptions());
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)witness).getFormatInfo());
                    ch.qos.logback.core.pattern.parser.Node w = witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                    ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                    w = w.next = composite;
                    ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                    composite.setChildNode(child);
                    composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                    child.next = composite;
                    composite.setChildNode(new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h"));
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.Parser vc_8144 = (ch.qos.logback.core.pattern.parser.Parser)null;
                    // AssertGenerator add assertion
                    org.junit.Assert.assertNull(vc_8144);
                    // StatementAdderMethod cloned existing statement
                    vc_8144.T();
                }
                org.junit.Assert.fail("testNested_cf68168 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testNested_cf68168_failAssert37_literalMutation68770_literalMutation72469 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testNested */
    @org.junit.Test(timeout = 10000)
    public void testNested_cf68177_failAssert42_literalMutation68982_failAssert31() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%top %(%child%(%^))");
                    ch.qos.logback.core.pattern.parser.Node t = p.parse();
                    ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("top");
                    ch.qos.logback.core.pattern.parser.Node w = witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                    ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                    w = w.next = composite;
                    ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                    composite.setChildNode(child);
                    composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                    child.next = composite;
                    composite.setChildNode(new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h"));
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.pattern.parser.Parser vc_8150 = (ch.qos.logback.core.pattern.parser.Parser)null;
                    // StatementAdderMethod cloned existing statement
                    vc_8150.getNextToken();
                }
                org.junit.Assert.fail("testNested_cf68177 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testNested_cf68177_failAssert42_literalMutation68982 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testNested */
    @org.junit.Test(timeout = 10000)
    public void testNested_cf68177_failAssert42_literalMutation68984() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("-h#WiY*!),3T#tJH`b7");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "-h#WiY*!");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("top");
                ch.qos.logback.core.pattern.parser.Node w = witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                w = w.next = composite;
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                composite.setChildNode(child);
                composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                child.next = composite;
                composite.setChildNode(new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h"));
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.Parser vc_8150 = (ch.qos.logback.core.pattern.parser.Parser)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_8150);
                // StatementAdderMethod cloned existing statement
                vc_8150.getNextToken();
            }
            org.junit.Assert.fail("testNested_cf68177 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testNested */
    @org.junit.Test(timeout = 10000)
    public void testNested_cf68178_cf69033_failAssert44_literalMutation72673() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%top %(%child%(%h))");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("top");
                ch.qos.logback.core.pattern.parser.Node w = witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, "");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)w).getType(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)w).getValue(), "");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)w).getNext());
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                w = w.next = composite;
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                composite.setChildNode(child);
                composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                child.next = composite;
                composite.setChildNode(new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h"));
                // AssertGenerator replace invocation
                ch.qos.logback.core.pattern.parser.Token o_testNested_cf68178__23 = // StatementAdderMethod cloned existing statement
p.getNextToken();
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(o_testNested_cf68178__23);
                // StatementAdderOnAssert create null value
                java.util.Map vc_8232 = (java.util.Map)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_8232);
                // StatementAddOnAssert local variable replacement
                ch.qos.logback.core.pattern.parser.Node c = child;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getValue(), "child");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.CompositeNode)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getNext()).getChildNode()).getFormatInfo());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.CompositeNode)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getNext()).getOptions());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.CompositeNode)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getNext()).getType(), 2);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.CompositeNode)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getNext()).getChildNode()).getType(), 1);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.CompositeNode)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getNext()).getValue(), "BARE");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.CompositeNode)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getNext()).getFormatInfo());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getFormatInfo());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.CompositeNode)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getNext()).getChildNode()).getOptions());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.CompositeNode)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getNext()).getChildNode()).getValue(), "h");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getOptions());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.CompositeNode)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getNext()).getNext());
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(c.equals(child));
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getNext().equals(child.next));
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getNext().equals(composite));
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.CompositeNode)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getNext()).getChildNode()).getNext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)c).getType(), 1);
                // StatementAdderMethod cloned existing statement
                p.compile(c, vc_8232);
            }
            org.junit.Assert.fail("testNested_cf68178_cf69033 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testNested */
    @org.junit.Test(timeout = 10000)
    public void testNested_cf68181() throws java.lang.Exception {
        {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%top %(%child%(%h))");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("top");
            ch.qos.logback.core.pattern.parser.Node w = witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
            ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
            w = w.next = composite;
            ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
            composite.setChildNode(child);
            composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
            child.next = composite;
            composite.setChildNode(new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h"));
            // StatementAdderMethod cloned existing statement
            p.advanceTokenPointer();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
            org.junit.Assert.assertEquals(witness, t);
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testNested */
    @org.junit.Test(timeout = 10000)
    public void testNested_cf68193_failAssert48() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%top %(%child%(%h))");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("top");
                ch.qos.logback.core.pattern.parser.Node w = witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                w = w.next = composite;
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                composite.setChildNode(child);
                composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                child.next = composite;
                composite.setChildNode(new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h"));
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_479 = "%top %(%child%(%h))";
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.Token vc_8156 = (ch.qos.logback.core.pattern.parser.Token)null;
                // StatementAdderMethod cloned existing statement
                p.expectNotNull(vc_8156, String_vc_479);
            }
            org.junit.Assert.fail("testNested_cf68193 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testNested */
    @org.junit.Test(timeout = 10000)
    public void testNested_cf68193_failAssert48_literalMutation69338() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%top %(%child%(&h))");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "top");
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getFormatInfo());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 1);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext()).getValue(), " ");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext()).getType(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getOptions());
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.Node witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("top");
                ch.qos.logback.core.pattern.parser.Node w = witness.next = new ch.qos.logback.core.pattern.parser.Node(ch.qos.logback.core.pattern.parser.Node.LITERAL, " ");
                ch.qos.logback.core.pattern.parser.CompositeNode composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                w = w.next = composite;
                ch.qos.logback.core.pattern.parser.Node child = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("child");
                composite.setChildNode(child);
                composite = new ch.qos.logback.core.pattern.parser.CompositeNode(BARE);
                child.next = composite;
                composite.setChildNode(new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("h"));
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_479 = "%top %(%child%(%h))";
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.pattern.parser.Token vc_8156 = (ch.qos.logback.core.pattern.parser.Token)null;
                // StatementAdderMethod cloned existing statement
                p.expectNotNull(vc_8156, String_vc_479);
            }
            org.junit.Assert.fail("testNested_cf68193 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testOptions0 */
    @org.junit.Test(timeout = 10000)
    public void testOptions0_cf72813_failAssert16() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%45x{'test '}");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
            witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(45, java.lang.Integer.MAX_VALUE));
            java.util.List<java.lang.String> ol = new java.util.ArrayList<java.lang.String>();
            ol.add("test ");
            witness.setOptions(ol);
            // StatementAdderMethod cloned existing statement
            p.C();
            org.junit.Assert.fail("testOptions0_cf72813 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testOptions0 */
    @org.junit.Test(timeout = 10000)
    public void testOptions0_cf72819_failAssert20_literalMutation73014() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%45x{\'tst \'}");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "x");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.pattern.FormatInfo)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getFormatInfo()).isLeftTruncate());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.FormatInfo)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getFormatInfo()).getMax(), 2147483647);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
            // AssertGenerator add assertion
            java.util.ArrayList collection_831430367 = new java.util.ArrayList<Object>();
	collection_831430367.add("tst ");
	org.junit.Assert.assertEquals(collection_831430367, ((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getOptions());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.pattern.FormatInfo)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getFormatInfo()).isLeftPad());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.FormatInfo)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getFormatInfo()).getMin(), 45);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
            witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(45, java.lang.Integer.MAX_VALUE));
            java.util.List<java.lang.String> ol = new java.util.ArrayList<java.lang.String>();
            ol.add("test ");
            witness.setOptions(ol);
            // StatementAdderOnAssert create null value
            java.lang.String vc_8408 = (java.lang.String)null;
            // StatementAdderMethod cloned existing statement
            p.COMPOSITE(vc_8408);
            org.junit.Assert.fail("testOptions0_cf72819 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testOptions0 */
    @org.junit.Test(timeout = 10000)
    public void testOptions0_cf72821_failAssert21_literalMutation73029_failAssert3() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%45x{\'test }");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(45, java.lang.Integer.MAX_VALUE));
                java.util.List<java.lang.String> ol = new java.util.ArrayList<java.lang.String>();
                ol.add("test ");
                witness.setOptions(ol);
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_494 = "x";
                // StatementAdderMethod cloned existing statement
                p.COMPOSITE(String_vc_494);
                org.junit.Assert.fail("testOptions0_cf72821 should have thrown IllegalStateException");
            } catch (java.lang.IllegalStateException eee) {
            }
            org.junit.Assert.fail("testOptions0_cf72821_failAssert21_literalMutation73029 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testOptions0 */
    @org.junit.Test(timeout = 10000)
    public void testOptions0_cf72821_failAssert21_literalMutation73036_literalMutation74506() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("1<7CZFJ#xH:=Z");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "1<7CZFJ#xH:=Z");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
            witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(90, java.lang.Integer.MAX_VALUE));
            java.util.List<java.lang.String> ol = new java.util.ArrayList<java.lang.String>();
            ol.add("test ");
            witness.setOptions(ol);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_494 = "x";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_494, "x");
            // StatementAdderMethod cloned existing statement
            p.COMPOSITE(String_vc_494);
            org.junit.Assert.fail("testOptions0_cf72821 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testOptions0 */
    @org.junit.Test(timeout = 10000)
    public void testOptions0_cf72822_failAssert22_add73042_literalMutation74158_failAssert23() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("$l :,0G\\2ipy%");
                ch.qos.logback.core.pattern.parser.Node t = p.parse();
                ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
                witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(45, java.lang.Integer.MAX_VALUE));
                java.util.List<java.lang.String> ol = new java.util.ArrayList<java.lang.String>();
                ol.add("test ");
                // MethodCallAdder
                witness.setOptions(ol);
                witness.setOptions(ol);
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_8409 = new java.lang.String();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(vc_8409, "");
                // StatementAdderMethod cloned existing statement
                p.COMPOSITE(vc_8409);
                org.junit.Assert.fail("testOptions0_cf72822 should have thrown IllegalStateException");
            } catch (java.lang.IllegalStateException eee) {
            }
            org.junit.Assert.fail("testOptions0_cf72822_failAssert22_add73042_literalMutation74158 should have thrown ScanException");
        } catch (ch.qos.logback.core.spi.ScanException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testOptions0 */
    @org.junit.Test(timeout = 10000)
    public void testOptions0_cf72827_failAssert23_literalMutation73062() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("@#)Q!c3-K6Ez#");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "@#");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
            witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(45, java.lang.Integer.MAX_VALUE));
            java.util.List<java.lang.String> ol = new java.util.ArrayList<java.lang.String>();
            ol.add("test ");
            witness.setOptions(ol);
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.pattern.parser.Parser vc_8410 = (ch.qos.logback.core.pattern.parser.Parser)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_8410);
            // StatementAdderMethod cloned existing statement
            vc_8410.SINGLE();
            org.junit.Assert.fail("testOptions0_cf72827 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testOptions0 */
    @org.junit.Test(timeout = 10000)
    public void testOptions0_cf72831_failAssert26() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%45x{'test '}");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
            witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(45, java.lang.Integer.MAX_VALUE));
            java.util.List<java.lang.String> ol = new java.util.ArrayList<java.lang.String>();
            ol.add("test ");
            witness.setOptions(ol);
            // StatementAdderMethod cloned existing statement
            p.E();
            org.junit.Assert.fail("testOptions0_cf72831 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testOptions0 */
    @org.junit.Test(timeout = 10000)
    public void testOptions0_cf72846_cf73507_failAssert29() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%45x{'test '}");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
            witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(45, java.lang.Integer.MAX_VALUE));
            java.util.List<java.lang.String> ol = new java.util.ArrayList<java.lang.String>();
            ol.add("test ");
            witness.setOptions(ol);
            // AssertGenerator replace invocation
            ch.qos.logback.core.pattern.parser.Token o_testOptions0_cf72846__13 = // StatementAdderMethod cloned existing statement
p.getNextToken();
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_testOptions0_cf72846__13);
            // StatementAdderOnAssert create null value
            java.lang.String vc_8532 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.pattern.parser.Token vc_8530 = (ch.qos.logback.core.pattern.parser.Token)null;
            // StatementAdderMethod cloned existing statement
            p.expectNotNull(vc_8530, vc_8532);
            org.junit.Assert.fail("testOptions0_cf72846_cf73507 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testOptions0 */
    @org.junit.Test(timeout = 10000)
    public void testOptions0_cf72849() throws java.lang.Exception {
        ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%45x{'test '}");
        ch.qos.logback.core.pattern.parser.Node t = p.parse();
        ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
        witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(45, java.lang.Integer.MAX_VALUE));
        java.util.List<java.lang.String> ol = new java.util.ArrayList<java.lang.String>();
        ol.add("test ");
        witness.setOptions(ol);
        // StatementAdderMethod cloned existing statement
        p.advanceTokenPointer();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
        org.junit.Assert.assertEquals(witness, t);
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testOptions1 */
    @org.junit.Test(timeout = 10000)
    public void testOptions1_cf75987_failAssert17() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%45x{a, b}");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
            witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(45, java.lang.Integer.MAX_VALUE));
            java.util.List<java.lang.String> ol = new java.util.ArrayList<java.lang.String>();
            ol.add("a");
            ol.add("b");
            witness.setOptions(ol);
            // StatementAdderMethod cloned existing statement
            p.C();
            org.junit.Assert.fail("testOptions1_cf75987 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testOptions1 */
    @org.junit.Test(timeout = 10000)
    public void testOptions1_cf75995_failAssert22_literalMutation76218() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%45x{a, b}");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("&");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)witness).getNext());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)witness).getType(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)witness).getValue(), "&");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)witness).getOptions());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)witness).getFormatInfo());
            witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(45, java.lang.Integer.MAX_VALUE));
            java.util.List<java.lang.String> ol = new java.util.ArrayList<java.lang.String>();
            ol.add("a");
            ol.add("b");
            witness.setOptions(ol);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_508 = "x";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_508, "x");
            // StatementAdderMethod cloned existing statement
            p.COMPOSITE(String_vc_508);
            org.junit.Assert.fail("testOptions1_cf75995 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testOptions1 */
    @org.junit.Test(timeout = 10000)
    public void testOptions1_cf75995_failAssert22_literalMutation76220_literalMutation78875() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("nU9z07!:wO");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "nU9z07!:wO");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.Node)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 0);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
            witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(22, java.lang.Integer.MAX_VALUE));
            java.util.List<java.lang.String> ol = new java.util.ArrayList<java.lang.String>();
            ol.add("a");
            ol.add("b");
            witness.setOptions(ol);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_508 = "x";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(String_vc_508, "x");
            // StatementAdderMethod cloned existing statement
            p.COMPOSITE(String_vc_508);
            org.junit.Assert.fail("testOptions1_cf75995 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testOptions1 */
    @org.junit.Test(timeout = 10000)
    public void testOptions1_cf76005_failAssert27() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%45x{a, b}");
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
            witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(45, java.lang.Integer.MAX_VALUE));
            java.util.List<java.lang.String> ol = new java.util.ArrayList<java.lang.String>();
            ol.add("a");
            ol.add("b");
            witness.setOptions(ol);
            // StatementAdderMethod cloned existing statement
            p.E();
            org.junit.Assert.fail("testOptions1_cf76005 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testOptions1 */
    @org.junit.Test(timeout = 10000)
    public void testOptions1_cf76020_cf76697() throws java.lang.Exception {
        ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%45x{a, b}");
        ch.qos.logback.core.pattern.parser.Node t = p.parse();
        ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
        witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(45, java.lang.Integer.MAX_VALUE));
        java.util.List<java.lang.String> ol = new java.util.ArrayList<java.lang.String>();
        ol.add("a");
        ol.add("b");
        witness.setOptions(ol);
        // AssertGenerator replace invocation
        ch.qos.logback.core.pattern.parser.Token o_testOptions1_cf76020__14 = // StatementAdderMethod cloned existing statement
p.getNextToken();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_testOptions1_cf76020__14);
        // StatementAdderMethod cloned existing statement
        p.advanceTokenPointer();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
        org.junit.Assert.assertEquals(witness, t);
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testOptions1 */
    @org.junit.Test(timeout = 10000)
    public void testOptions1_cf76023() throws java.lang.Exception {
        ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%45x{a, b}");
        ch.qos.logback.core.pattern.parser.Node t = p.parse();
        ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
        witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(45, java.lang.Integer.MAX_VALUE));
        java.util.List<java.lang.String> ol = new java.util.ArrayList<java.lang.String>();
        ol.add("a");
        ol.add("b");
        witness.setOptions(ol);
        // StatementAdderMethod cloned existing statement
        p.advanceTokenPointer();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
        org.junit.Assert.assertEquals(witness, t);
    }

    /* amplification of ch.qos.logback.core.pattern.parser.ParserTest#testOptions1 */
    @org.junit.Test(timeout = 10000)
    public void testOptions1_cf76035_failAssert40_literalMutation76919() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.pattern.parser.Parser p = new ch.qos.logback.core.pattern.parser.Parser("%45x{a; b}");
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getValue(), "x");
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getNext());
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getStatusManager());
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.pattern.FormatInfo)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getFormatInfo()).isLeftTruncate());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.FormatInfo)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getFormatInfo()).getMax(), 2147483647);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getType(), 1);
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(((ch.qos.logback.core.pattern.parser.Parser)p).getContext());
            // AssertGenerator add assertion
            java.util.ArrayList collection_1922264409 = new java.util.ArrayList<Object>();
	collection_1922264409.add("a; b");
	org.junit.Assert.assertEquals(collection_1922264409, ((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getOptions());;
            // AssertGenerator add assertion
            org.junit.Assert.assertTrue(((ch.qos.logback.core.pattern.FormatInfo)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getFormatInfo()).isLeftPad());
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(((ch.qos.logback.core.pattern.FormatInfo)((ch.qos.logback.core.pattern.parser.SimpleKeywordNode)((ch.qos.logback.core.pattern.parser.Parser)p).parse()).getFormatInfo()).getMin(), 45);
            ch.qos.logback.core.pattern.parser.Node t = p.parse();
            ch.qos.logback.core.pattern.parser.SimpleKeywordNode witness = new ch.qos.logback.core.pattern.parser.SimpleKeywordNode("x");
            witness.setFormatInfo(new ch.qos.logback.core.pattern.FormatInfo(45, java.lang.Integer.MAX_VALUE));
            java.util.List<java.lang.String> ol = new java.util.ArrayList<java.lang.String>();
            ol.add("a");
            ol.add("b");
            witness.setOptions(ol);
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_509 = "%45x{a, b}";
            // StatementAdderOnAssert create null value
            ch.qos.logback.core.pattern.parser.Token vc_8666 = (ch.qos.logback.core.pattern.parser.Token)null;
            // StatementAdderMethod cloned existing statement
            p.expectNotNull(vc_8666, String_vc_509);
            org.junit.Assert.fail("testOptions1_cf76035 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }
}

