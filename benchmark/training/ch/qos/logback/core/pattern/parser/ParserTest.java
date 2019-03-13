/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.core.pattern.parser;


import Node.LITERAL;
import Token.BARE_COMPOSITE_KEYWORD_TOKEN;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.pattern.FormatInfo;
import ch.qos.logback.core.spi.ScanException;
import ch.qos.logback.core.testUtil.StatusChecker;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static Node.LITERAL;
import static Parser.MISSING_RIGHT_PARENTHESIS;


public class ParserTest {
    String BARE = BARE_COMPOSITE_KEYWORD_TOKEN.getValue().toString();

    Context context = new ContextBase();

    @Test
    public void testBasic() throws Exception {
        Parser<Object> p = new Parser("hello");
        Node t = p.parse();
        Assert.assertEquals(LITERAL, t.getType());
        Assert.assertEquals("hello", t.getValue());
    }

    @Test
    public void testKeyword() throws Exception {
        {
            Parser<Object> p = new Parser("hello%xyz");
            Node t = p.parse();
            Node witness = new Node(LITERAL, "hello");
            witness.next = new SimpleKeywordNode("xyz");
            Assert.assertEquals(witness, t);
        }
        {
            Parser<Object> p = new Parser("hello%xyz{x}");
            Node t = p.parse();
            Node witness = new Node(LITERAL, "hello");
            SimpleKeywordNode n = new SimpleKeywordNode("xyz");
            List<String> optionList = new ArrayList<String>();
            optionList.add("x");
            n.setOptions(optionList);
            witness.next = n;
            Assert.assertEquals(witness, t);
        }
    }

    @Test
    public void testComposite() throws Exception {
        {
            Parser<Object> p = new Parser("hello%(%child)");
            Node t = p.parse();
            Node witness = new Node(LITERAL, "hello");
            CompositeNode composite = new CompositeNode(BARE);
            Node child = new SimpleKeywordNode("child");
            composite.setChildNode(child);
            witness.next = composite;
            // System.out.println("w:" + witness);
            // System.out.println(t);
            Assert.assertEquals(witness, t);
        }
        // System.out.println("testRecursive part 2");
        {
            Parser<Object> p = new Parser("hello%(%child )");
            Node t = p.parse();
            Node witness = new Node(LITERAL, "hello");
            CompositeNode composite = new CompositeNode(BARE);
            Node child = new SimpleKeywordNode("child");
            composite.setChildNode(child);
            witness.next = composite;
            child.next = new Node(LITERAL, " ");
            Assert.assertEquals(witness, t);
        }
        {
            Parser<Object> p = new Parser("hello%(%child %h)");
            Node t = p.parse();
            Node witness = new Node(LITERAL, "hello");
            CompositeNode composite = new CompositeNode(BARE);
            Node child = new SimpleKeywordNode("child");
            composite.setChildNode(child);
            child.next = new Node(LITERAL, " ");
            child.next.next = new SimpleKeywordNode("h");
            witness.next = composite;
            Assert.assertEquals(witness, t);
        }
        {
            Parser<Object> p = new Parser("hello%(%child %h) %m");
            Node t = p.parse();
            Node witness = new Node(LITERAL, "hello");
            CompositeNode composite = new CompositeNode(BARE);
            Node child = new SimpleKeywordNode("child");
            composite.setChildNode(child);
            child.next = new Node(LITERAL, " ");
            child.next.next = new SimpleKeywordNode("h");
            witness.next = composite;
            composite.next = new Node(LITERAL, " ");
            composite.next.next = new SimpleKeywordNode("m");
            Assert.assertEquals(witness, t);
        }
        {
            Parser<Object> p = new Parser("hello%( %child \\(%h\\) ) %m");
            Node t = p.parse();
            Node witness = new Node(LITERAL, "hello");
            CompositeNode composite = new CompositeNode(BARE);
            Node child = new Node(LITERAL, " ");
            composite.setChildNode(child);
            Node c = child;
            c = c.next = new SimpleKeywordNode("child");
            c = c.next = new Node(LITERAL, " (");
            c = c.next = new SimpleKeywordNode("h");
            c = c.next = new Node(LITERAL, ") ");
            witness.next = composite;
            composite.next = new Node(LITERAL, " ");
            composite.next.next = new SimpleKeywordNode("m");
            Assert.assertEquals(witness, t);
        }
    }

    @Test
    public void testNested() throws Exception {
        {
            Parser<Object> p = new Parser("%top %(%child%(%h))");
            Node t = p.parse();
            Node witness = new SimpleKeywordNode("top");
            Node w = witness.next = new Node(LITERAL, " ");
            CompositeNode composite = new CompositeNode(BARE);
            w = w.next = composite;
            Node child = new SimpleKeywordNode("child");
            composite.setChildNode(child);
            composite = new CompositeNode(BARE);
            child.next = composite;
            composite.setChildNode(new SimpleKeywordNode("h"));
            Assert.assertEquals(witness, t);
        }
    }

    @Test
    public void testFormattingInfo() throws Exception {
        {
            Parser<Object> p = new Parser("%45x");
            Node t = p.parse();
            FormattingNode witness = new SimpleKeywordNode("x");
            witness.setFormatInfo(new FormatInfo(45, Integer.MAX_VALUE));
            Assert.assertEquals(witness, t);
        }
        {
            Parser<Object> p = new Parser("%4.5x");
            Node t = p.parse();
            FormattingNode witness = new SimpleKeywordNode("x");
            witness.setFormatInfo(new FormatInfo(4, 5));
            Assert.assertEquals(witness, t);
        }
        {
            Parser<Object> p = new Parser("%-4.5x");
            Node t = p.parse();
            FormattingNode witness = new SimpleKeywordNode("x");
            witness.setFormatInfo(new FormatInfo(4, 5, false, true));
            Assert.assertEquals(witness, t);
        }
        {
            Parser<Object> p = new Parser("%-4.-5x");
            Node t = p.parse();
            FormattingNode witness = new SimpleKeywordNode("x");
            witness.setFormatInfo(new FormatInfo(4, 5, false, false));
            Assert.assertEquals(witness, t);
        }
        {
            Parser<Object> p = new Parser("%-4.5x %12y");
            Node t = p.parse();
            FormattingNode witness = new SimpleKeywordNode("x");
            witness.setFormatInfo(new FormatInfo(4, 5, false, true));
            Node n = witness.next = new Node(LITERAL, " ");
            n = n.next = new SimpleKeywordNode("y");
            ((FormattingNode) (n)).setFormatInfo(new FormatInfo(12, Integer.MAX_VALUE));
            Assert.assertEquals(witness, t);
        }
    }

    @Test
    public void testOptions0() throws Exception {
        Parser<Object> p = new Parser("%45x{'test '}");
        Node t = p.parse();
        SimpleKeywordNode witness = new SimpleKeywordNode("x");
        witness.setFormatInfo(new FormatInfo(45, Integer.MAX_VALUE));
        List<String> ol = new ArrayList<String>();
        ol.add("test ");
        witness.setOptions(ol);
        Assert.assertEquals(witness, t);
    }

    @Test
    public void testOptions1() throws Exception {
        Parser<Object> p = new Parser("%45x{a, b}");
        Node t = p.parse();
        SimpleKeywordNode witness = new SimpleKeywordNode("x");
        witness.setFormatInfo(new FormatInfo(45, Integer.MAX_VALUE));
        List<String> ol = new ArrayList<String>();
        ol.add("a");
        ol.add("b");
        witness.setOptions(ol);
        Assert.assertEquals(witness, t);
    }

    // see http://jira.qos.ch/browse/LBCORE-180
    @Test
    public void keywordGluedToLitteral() throws Exception {
        Parser<Object> p = new Parser("%x{}a");
        Node t = p.parse();
        SimpleKeywordNode witness = new SimpleKeywordNode("x");
        witness.setOptions(new ArrayList<String>());
        witness.next = new Node(LITERAL, "a");
        Assert.assertEquals(witness, t);
    }

    @Test
    public void testCompositeFormatting() throws Exception {
        Parser<Object> p = new Parser("hello%5(XYZ)");
        Node t = p.parse();
        Node witness = new Node(LITERAL, "hello");
        CompositeNode composite = new CompositeNode(BARE);
        composite.setFormatInfo(new FormatInfo(5, Integer.MAX_VALUE));
        Node child = new Node(LITERAL, "XYZ");
        composite.setChildNode(child);
        witness.next = composite;
        Assert.assertEquals(witness, t);
    }

    @Test
    public void empty() {
        try {
            Parser<Object> p = new Parser("");
            p.parse();
            Assert.fail("");
        } catch (ScanException e) {
        }
    }

    @Test
    public void lbcore193() throws Exception {
        try {
            Parser<Object> p = new Parser("hello%(abc");
            p.setContext(context);
            p.parse();
            Assert.fail("where the is exception?");
        } catch (ScanException ise) {
            Assert.assertEquals("Expecting RIGHT_PARENTHESIS token but got null", ise.getMessage());
        }
        StatusChecker sc = new StatusChecker(context);
        sc.assertContainsMatch("Expecting RIGHT_PARENTHESIS");
        sc.assertContainsMatch(("See also " + (MISSING_RIGHT_PARENTHESIS)));
    }
}

