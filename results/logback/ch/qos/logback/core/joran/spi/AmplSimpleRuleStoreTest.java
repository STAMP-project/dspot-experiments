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


package ch.qos.logback.core.joran.spi;


/**
 * Test SimpleRuleStore for various explicit rule combinations.
 *
 * We also test that explicit patterns are case sensitive.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class AmplSimpleRuleStoreTest {
    ch.qos.logback.core.joran.spi.SimpleRuleStore srs = new ch.qos.logback.core.joran.spi.SimpleRuleStore(new ch.qos.logback.core.ContextBase());

    ch.qos.logback.core.joran.spi.CaseCombinator cc = new ch.qos.logback.core.joran.spi.CaseCombinator();

    @org.junit.Test
    public void smoke() throws java.lang.Exception {
        srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a/b"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction());
        // test for all possible case combinations of "a/b"
        for (java.lang.String s : cc.combinations("a/b")) {
            java.lang.System.out.println(("s=" + s));
            java.util.List<ch.qos.logback.core.joran.action.Action> r = srs.matchActions(new ch.qos.logback.core.joran.spi.ElementPath(s));
            org.junit.Assert.assertNotNull(r);
            org.junit.Assert.assertEquals(1, r.size());
            if (!((r.get(0)) instanceof ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction)) {
                org.junit.Assert.fail("Wrong type");
            }
        }
    }

    @org.junit.Test
    public void smokeII() throws java.lang.Exception {
        srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a/b"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction());
        srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a/b"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.YAction());
        for (java.lang.String s : cc.combinations("a/b")) {
            java.util.List<ch.qos.logback.core.joran.action.Action> r = srs.matchActions(new ch.qos.logback.core.joran.spi.ElementPath(s));
            org.junit.Assert.assertNotNull(r);
            org.junit.Assert.assertEquals(2, r.size());
            if (!((r.get(0)) instanceof ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction)) {
                org.junit.Assert.fail("Wrong type");
            }
            if (!((r.get(1)) instanceof ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.YAction)) {
                org.junit.Assert.fail("Wrong type");
            }
        }
    }

    @org.junit.Test
    public void testSlashSuffix() throws java.lang.Exception {
        ch.qos.logback.core.joran.spi.ElementSelector pa = new ch.qos.logback.core.joran.spi.ElementSelector("a/");
        srs.addRule(pa, new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction());
        for (java.lang.String s : cc.combinations("a")) {
            java.util.List<ch.qos.logback.core.joran.action.Action> r = srs.matchActions(new ch.qos.logback.core.joran.spi.ElementPath(s));
            org.junit.Assert.assertNotNull(r);
            org.junit.Assert.assertEquals(1, r.size());
            if (!((r.get(0)) instanceof ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction)) {
                org.junit.Assert.fail("Wrong type");
            }
        }
    }

    @org.junit.Test
    public void testTail1() throws java.lang.Exception {
        srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("*/b"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction());
        for (java.lang.String s : cc.combinations("a/b")) {
            java.util.List<ch.qos.logback.core.joran.action.Action> r = srs.matchActions(new ch.qos.logback.core.joran.spi.ElementPath(s));
            org.junit.Assert.assertNotNull(r);
            org.junit.Assert.assertEquals(1, r.size());
            if (!((r.get(0)) instanceof ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction)) {
                org.junit.Assert.fail("Wrong type");
            }
        }
    }

    @org.junit.Test
    public void testTail2() throws java.lang.Exception {
        ch.qos.logback.core.joran.spi.SimpleRuleStore srs = new ch.qos.logback.core.joran.spi.SimpleRuleStore(new ch.qos.logback.core.ContextBase());
        srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("*/c"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction());
        for (java.lang.String s : cc.combinations("a/b/c")) {
            java.util.List<ch.qos.logback.core.joran.action.Action> r = srs.matchActions(new ch.qos.logback.core.joran.spi.ElementPath(s));
            org.junit.Assert.assertNotNull(r);
            org.junit.Assert.assertEquals(1, r.size());
            if (!((r.get(0)) instanceof ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction)) {
                org.junit.Assert.fail("Wrong type");
            }
        }
    }

    @org.junit.Test
    public void testTail3() throws java.lang.Exception {
        srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("*/b"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction());
        srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("*/a/b"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.YAction());
        for (java.lang.String s : cc.combinations("a/b")) {
            java.util.List<ch.qos.logback.core.joran.action.Action> r = srs.matchActions(new ch.qos.logback.core.joran.spi.ElementPath(s));
            org.junit.Assert.assertNotNull(r);
            org.junit.Assert.assertEquals(1, r.size());
            if (!((r.get(0)) instanceof ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.YAction)) {
                org.junit.Assert.fail("Wrong type");
            }
        }
    }

    @org.junit.Test
    public void testTail4() throws java.lang.Exception {
        srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("*/b"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction());
        srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("*/a/b"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.YAction());
        srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a/b"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.ZAction());
        for (java.lang.String s : cc.combinations("a/b")) {
            java.util.List<ch.qos.logback.core.joran.action.Action> r = srs.matchActions(new ch.qos.logback.core.joran.spi.ElementPath(s));
            org.junit.Assert.assertNotNull(r);
            org.junit.Assert.assertEquals(1, r.size());
            if (!((r.get(0)) instanceof ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.ZAction)) {
                org.junit.Assert.fail("Wrong type");
            }
        }
    }

    @org.junit.Test
    public void testSuffix() throws java.lang.Exception {
        srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction());
        srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a/*"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.YAction());
        for (java.lang.String s : cc.combinations("a/b")) {
            java.util.List<ch.qos.logback.core.joran.action.Action> r = srs.matchActions(new ch.qos.logback.core.joran.spi.ElementPath(s));
            org.junit.Assert.assertNotNull(r);
            org.junit.Assert.assertEquals(1, r.size());
            org.junit.Assert.assertTrue(((r.get(0)) instanceof ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.YAction));
        }
    }

    @org.junit.Test
    public void testDeepSuffix() throws java.lang.Exception {
        srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction(1));
        srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a/b/*"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction(2));
        for (java.lang.String s : cc.combinations("a/other")) {
            java.util.List<ch.qos.logback.core.joran.action.Action> r = srs.matchActions(new ch.qos.logback.core.joran.spi.ElementPath(s));
            org.junit.Assert.assertNull(r);
        }
    }

    @org.junit.Test
    public void testPrefixSuffixInteraction1() throws java.lang.Exception {
        srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.ZAction());
        srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a/*"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.YAction());
        srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("*/a/b"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction(3));
        for (java.lang.String s : cc.combinations("a/b")) {
            java.util.List<ch.qos.logback.core.joran.action.Action> r = srs.matchActions(new ch.qos.logback.core.joran.spi.ElementPath(s));
            org.junit.Assert.assertNotNull(r);
            org.junit.Assert.assertEquals(1, r.size());
            org.junit.Assert.assertTrue(((r.get(0)) instanceof ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction));
            ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction xaction = ((ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction) (r.get(0)));
            org.junit.Assert.assertEquals(3, xaction.id);
        }
    }

    @org.junit.Test
    public void testPrefixSuffixInteraction2() throws java.lang.Exception {
        srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("tG"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction());
        srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("tG/tS"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.YAction());
        srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("tG/tS/test"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.ZAction());
        srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("tG/tS/test/*"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction(9));
        for (java.lang.String s : cc.combinations("tG/tS/toto")) {
            java.util.List<ch.qos.logback.core.joran.action.Action> r = srs.matchActions(new ch.qos.logback.core.joran.spi.ElementPath(s));
            org.junit.Assert.assertNull(r);
        }
    }

    class XAction extends ch.qos.logback.core.joran.action.Action {
        int id = 0;

        XAction() {
        }

        XAction(int id) {
            this.id = id;
        }

        public void begin(ch.qos.logback.core.joran.spi.InterpretationContext ec, java.lang.String name, org.xml.sax.Attributes attributes) {
        }

        public void end(ch.qos.logback.core.joran.spi.InterpretationContext ec, java.lang.String name) {
        }

        public void finish(ch.qos.logback.core.joran.spi.InterpretationContext ec) {
        }

        public java.lang.String toString() {
            return ("XAction(" + (id)) + ")";
        }
    }

    class YAction extends ch.qos.logback.core.joran.action.Action {
        public void begin(ch.qos.logback.core.joran.spi.InterpretationContext ec, java.lang.String name, org.xml.sax.Attributes attributes) {
        }

        public void end(ch.qos.logback.core.joran.spi.InterpretationContext ec, java.lang.String name) {
        }

        public void finish(ch.qos.logback.core.joran.spi.InterpretationContext ec) {
        }
    }

    class ZAction extends ch.qos.logback.core.joran.action.Action {
        public void begin(ch.qos.logback.core.joran.spi.InterpretationContext ec, java.lang.String name, org.xml.sax.Attributes attributes) {
        }

        public void end(ch.qos.logback.core.joran.spi.InterpretationContext ec, java.lang.String name) {
        }

        public void finish(ch.qos.logback.core.joran.spi.InterpretationContext ec) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.SimpleRuleStoreTest#testDeepSuffix */
    @org.junit.Test(timeout = 10000)
    public void testDeepSuffix_cf32959_failAssert2_cf33079() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction(1));
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a/b/*"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction(2));
            for (java.lang.String s : cc.combinations("a/other")) {
                java.util.List<ch.qos.logback.core.joran.action.Action> r = srs.matchActions(new ch.qos.logback.core.joran.spi.ElementPath(s));
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementPath vc_7171 = new ch.qos.logback.core.joran.spi.ElementPath();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).duplicate()).duplicate()).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_1651431891 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1651431891, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).duplicate()).duplicate().equals(vc_7171));
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).duplicate()).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).duplicate()).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_583316408 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_583316408, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_1489463662 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1489463662, ((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).duplicate()).duplicate()).duplicate().equals(vc_7171));
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).duplicate().equals(vc_7171));
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).peekLast());
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_7168 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_7168);
                // StatementAdderMethod cloned existing statement
                vc_7168.matchActions(vc_7171);
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementPath vc_7203 = new ch.qos.logback.core.joran.spi.ElementPath();
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_7200 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // StatementAdderMethod cloned existing statement
                vc_7200.matchActions(vc_7203);
                org.junit.Assert.assertNull(r);
            }
            org.junit.Assert.fail("testDeepSuffix_cf32959 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.SimpleRuleStoreTest#testDeepSuffix */
    @org.junit.Test(timeout = 10000)
    public void testDeepSuffix_cf32964_failAssert4_cf33242() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction(1));
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a/b/*"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction(2));
            for (java.lang.String s : cc.combinations("a/other")) {
                java.util.List<ch.qos.logback.core.joran.action.Action> r = srs.matchActions(new ch.qos.logback.core.joran.spi.ElementPath(s));
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.action.Action vc_7176 = (ch.qos.logback.core.joran.action.Action)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_7176);
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementSelector vc_7175 = new ch.qos.logback.core.joran.spi.ElementSelector();
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7175).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7175).duplicate()).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_575606619 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_575606619, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7175).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                java.util.ArrayList collection_1201437111 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1201437111, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7175).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                java.util.ArrayList collection_479491732 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_479491732, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_7175).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7175).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7175).duplicate()).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7175).duplicate()).duplicate()).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7175).size(), 0);
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_7172 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_7172);
                // StatementAdderMethod cloned existing statement
                vc_7172.addRule(vc_7175, vc_7176);
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementPath vc_7235 = new ch.qos.logback.core.joran.spi.ElementPath();
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_7232 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // StatementAdderMethod cloned existing statement
                vc_7232.matchActions(vc_7235);
                org.junit.Assert.assertNull(r);
            }
            org.junit.Assert.fail("testDeepSuffix_cf32964 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.SimpleRuleStoreTest#testDeepSuffix */
    @org.junit.Test(timeout = 10000)
    public void testDeepSuffix_cf32973_failAssert8_cf33623_cf36246() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction(1));
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a/b/*"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction(2));
            for (java.lang.String s : cc.combinations("a/other")) {
                java.util.List<ch.qos.logback.core.joran.action.Action> r = srs.matchActions(new ch.qos.logback.core.joran.spi.ElementPath(s));
                // StatementAdderOnAssert create null value
                java.lang.String vc_7182 = (java.lang.String)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_7182);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_7182);
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementSelector vc_7181 = new ch.qos.logback.core.joran.spi.ElementSelector();
                // AssertGenerator add assertion
                java.util.ArrayList collection_112821567 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_112821567, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7181).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7181).duplicate()).duplicate()).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7181).duplicate()).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_122588513 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_122588513, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_7181).getCopyOfPartList());;
                // AssertGenerator add assertion
                java.util.ArrayList collection_1176913212 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1176913212, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7181).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7181).duplicate()).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7181).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7181).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7181).peekLast());
                // AssertGenerator add assertion
                java.util.ArrayList collection_611835573 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_611835573, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7181).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7181).duplicate()).duplicate()).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7181).duplicate()).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_1862271166 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1862271166, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_7181).getCopyOfPartList());;
                // AssertGenerator add assertion
                java.util.ArrayList collection_1002692897 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1002692897, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7181).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7181).duplicate()).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_7181).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7181).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_7181).peekLast());
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_7178 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_7178);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_7178);
                // StatementAdderMethod cloned existing statement
                vc_7178.addRule(vc_7181, vc_7182);
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_456 = "a/other";
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementSelector vc_7309 = new ch.qos.logback.core.joran.spi.ElementSelector();
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_7306 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // StatementAdderMethod cloned existing statement
                vc_7306.addRule(vc_7309, String_vc_456);
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.ElementSelector vc_7772 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
                // StatementAdderMethod cloned existing statement
                vc_7306.addRule(vc_7772, String_vc_456);
                org.junit.Assert.assertNull(r);
            }
            org.junit.Assert.fail("testDeepSuffix_cf32973 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.SimpleRuleStoreTest#testDeepSuffix */
    @org.junit.Test(timeout = 10000)
    public void testDeepSuffix_cf32959_failAssert2_cf33078_cf39784() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction(1));
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a/b/*"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction(2));
            for (java.lang.String s : cc.combinations("a/other")) {
                java.util.List<ch.qos.logback.core.joran.action.Action> r = srs.matchActions(new ch.qos.logback.core.joran.spi.ElementPath(s));
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementPath vc_7171 = new ch.qos.logback.core.joran.spi.ElementPath();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).duplicate()).duplicate()).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_794112342 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_794112342, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).duplicate()).duplicate().equals(vc_7171));
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).duplicate()).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).duplicate()).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_1410611035 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1410611035, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_1632582430 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1632582430, ((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).duplicate()).duplicate()).duplicate().equals(vc_7171));
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).duplicate().equals(vc_7171));
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).duplicate()).duplicate()).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_141536944 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_141536944, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).duplicate()).duplicate().equals(vc_7171));
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).duplicate()).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).duplicate()).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_1518852225 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1518852225, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_248477220 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_248477220, ((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).duplicate()).duplicate()).duplicate().equals(vc_7171));
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).duplicate().equals(vc_7171));
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_7171).peekLast());
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_7168 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_7168);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_7168);
                // StatementAdderMethod cloned existing statement
                vc_7168.matchActions(vc_7171);
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_7200 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // StatementAdderMethod cloned existing statement
                vc_7200.matchActions(vc_7171);
                // StatementAdderOnAssert create null value
                java.lang.String vc_8398 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.ElementSelector vc_8396 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
                // StatementAdderMethod cloned existing statement
                vc_7200.addRule(vc_8396, vc_8398);
                org.junit.Assert.assertNull(r);
            }
            org.junit.Assert.fail("testDeepSuffix_cf32959 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.SimpleRuleStoreTest#testPrefixSuffixInteraction1 */
    @org.junit.Test
    public void testPrefixSuffixInteraction1_literalMutation41066_failAssert2() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.ZAction());
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a/*"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.YAction());
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("*/ab"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction(3));
            for (java.lang.String s : cc.combinations("a/b")) {
                java.util.List<ch.qos.logback.core.joran.action.Action> r = srs.matchActions(new ch.qos.logback.core.joran.spi.ElementPath(s));
                org.junit.Assert.assertNotNull(r);
                // MethodAssertGenerator build local variable
                Object o_18_0 = r.size();
                // MethodAssertGenerator build local variable
                Object o_20_0 = (r.get(0)) instanceof ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction;
                ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction xaction = ((ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction) (r.get(0)));
            }
            org.junit.Assert.fail("testPrefixSuffixInteraction1_literalMutation41066 should have thrown Exception");
        } catch (java.lang.Exception eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.SimpleRuleStoreTest#testPrefixSuffixInteraction1 */
    @org.junit.Test(timeout = 10000)
    public void testPrefixSuffixInteraction1_cf41094_failAssert22_cf41576() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.ZAction());
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a/*"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.YAction());
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("*/a/b"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction(3));
            for (java.lang.String s : cc.combinations("a/b")) {
                java.util.List<ch.qos.logback.core.joran.action.Action> r = srs.matchActions(new ch.qos.logback.core.joran.spi.ElementPath(s));
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_8815 = new java.lang.String();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(vc_8815, "");
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementSelector vc_8813 = new ch.qos.logback.core.joran.spi.ElementSelector();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8813).duplicate()).duplicate()).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8813).duplicate()).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8813).duplicate()).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8813).peekLast());
                // AssertGenerator add assertion
                java.util.ArrayList collection_1759462972 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1759462972, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_8813).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8813).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_1821889479 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1821889479, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8813).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                java.util.ArrayList collection_1074739477 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1074739477, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8813).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8813).duplicate()).peekLast());
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_8810 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_8810);
                // StatementAdderMethod cloned existing statement
                vc_8810.addRule(vc_8813, vc_8815);
                org.junit.Assert.assertNotNull(r);
                // MethodAssertGenerator build local variable
                Object o_18_0 = r.size();
                // MethodAssertGenerator build local variable
                Object o_20_0 = (r.get(0)) instanceof ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction;
                ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction xaction = ((ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction) (r.get(0)));
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_539 = "a";
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.ElementSelector vc_8636 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_8634 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // StatementAdderMethod cloned existing statement
                vc_8634.addRule(vc_8636, String_vc_539);
            }
            org.junit.Assert.fail("testPrefixSuffixInteraction1_cf41094 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.SimpleRuleStoreTest#testPrefixSuffixInteraction1 */
    @org.junit.Test(timeout = 10000)
    public void testPrefixSuffixInteraction1_cf41096_failAssert24_literalMutation41641_failAssert6() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.ZAction());
                srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a/*"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.YAction());
                srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("*/ab"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction(3));
                for (java.lang.String s : cc.combinations("a/b")) {
                    java.util.List<ch.qos.logback.core.joran.action.Action> r = srs.matchActions(new ch.qos.logback.core.joran.spi.ElementPath(s));
                    org.junit.Assert.assertNotNull(r);
                    // MethodAssertGenerator build local variable
                    Object o_18_0 = r.size();
                    // MethodAssertGenerator build local variable
                    Object o_20_0 = (r.get(0)) instanceof ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction;
                    ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction xaction = ((ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction) (r.get(0)));
                    // StatementAdderOnAssert create null value
                    java.lang.String vc_8638 = (java.lang.String)null;
                    // StatementAdderOnAssert create random local variable
                    ch.qos.logback.core.joran.spi.ElementSelector vc_8637 = new ch.qos.logback.core.joran.spi.ElementSelector();
                    // StatementAdderOnAssert create null value
                    ch.qos.logback.core.joran.spi.RuleStore vc_8634 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                    // StatementAdderMethod cloned existing statement
                    vc_8634.addRule(vc_8637, vc_8638);
                }
                org.junit.Assert.fail("testPrefixSuffixInteraction1_cf41096 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testPrefixSuffixInteraction1_cf41096_failAssert24_literalMutation41641 should have thrown ClassCastException");
        } catch (java.lang.ClassCastException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.SimpleRuleStoreTest#testPrefixSuffixInteraction1 */
    @org.junit.Test(timeout = 10000)
    public void testPrefixSuffixInteraction1_cf41094_failAssert22_cf41560() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.ZAction());
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a/*"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.YAction());
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("*/a/b"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction(3));
            for (java.lang.String s : cc.combinations("a/b")) {
                java.util.List<ch.qos.logback.core.joran.action.Action> r = srs.matchActions(new ch.qos.logback.core.joran.spi.ElementPath(s));
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementPath vc_8803 = new ch.qos.logback.core.joran.spi.ElementPath();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8803).duplicate()).duplicate()).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8803).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8803).duplicate()).duplicate()).duplicate().equals(vc_8803));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_8803).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8803).duplicate()).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_8803).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_8803).duplicate().equals(vc_8803));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8803).duplicate()).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_211327587 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_211327587, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8803).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                java.util.ArrayList collection_357726415 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_357726415, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8803).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                java.util.ArrayList collection_932646387 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_932646387, ((ch.qos.logback.core.joran.spi.ElementPath)vc_8803).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_8803).duplicate()).duplicate().equals(vc_8803));
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_8800 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_8800);
                // StatementAdderMethod cloned existing statement
                vc_8800.matchActions(vc_8803);
                org.junit.Assert.assertNotNull(r);
                // MethodAssertGenerator build local variable
                Object o_18_0 = r.size();
                // MethodAssertGenerator build local variable
                Object o_20_0 = (r.get(0)) instanceof ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction;
                ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction xaction = ((ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction) (r.get(0)));
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_539 = "a";
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.ElementSelector vc_8636 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_8634 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // StatementAdderMethod cloned existing statement
                vc_8634.addRule(vc_8636, String_vc_539);
            }
            org.junit.Assert.fail("testPrefixSuffixInteraction1_cf41094 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.SimpleRuleStoreTest#testPrefixSuffixInteraction1 */
    @org.junit.Test(timeout = 10000)
    public void testPrefixSuffixInteraction1_cf41094_failAssert22_cf41575_cf42413() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.ZAction());
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a/*"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.YAction());
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("*/a/b"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction(3));
            for (java.lang.String s : cc.combinations("a/b")) {
                java.util.List<ch.qos.logback.core.joran.action.Action> r = srs.matchActions(new ch.qos.logback.core.joran.spi.ElementPath(s));
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_550 = "*/a/b";
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(String_vc_550, "*/a/b");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(String_vc_550, "*/a/b");
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementSelector vc_8813 = new ch.qos.logback.core.joran.spi.ElementSelector();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8813).duplicate()).duplicate()).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8813).duplicate()).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8813).duplicate()).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8813).peekLast());
                // AssertGenerator add assertion
                java.util.ArrayList collection_1611981639 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1611981639, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_8813).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8813).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_1151005480 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1151005480, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8813).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                java.util.ArrayList collection_519289909 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_519289909, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8813).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8813).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8813).duplicate()).duplicate()).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8813).duplicate()).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8813).duplicate()).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8813).peekLast());
                // AssertGenerator add assertion
                java.util.ArrayList collection_2042341603 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2042341603, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_8813).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_8813).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_1941424735 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1941424735, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8813).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                java.util.ArrayList collection_1068146882 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1068146882, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8813).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_8813).duplicate()).peekLast());
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_8810 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_8810);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_8810);
                // StatementAdderMethod cloned existing statement
                vc_8810.addRule(vc_8813, String_vc_550);
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_562 = "*/a/b";
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementSelector vc_9005 = new ch.qos.logback.core.joran.spi.ElementSelector();
                // StatementAdderMethod cloned existing statement
                vc_8810.addRule(vc_9005, String_vc_562);
                org.junit.Assert.assertNotNull(r);
                // MethodAssertGenerator build local variable
                Object o_18_0 = r.size();
                // MethodAssertGenerator build local variable
                Object o_20_0 = (r.get(0)) instanceof ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction;
                ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction xaction = ((ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction) (r.get(0)));
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_539 = "a";
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.ElementSelector vc_8636 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_8634 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // StatementAdderMethod cloned existing statement
                vc_8634.addRule(vc_8636, String_vc_539);
            }
            org.junit.Assert.fail("testPrefixSuffixInteraction1_cf41094 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.SimpleRuleStoreTest#testPrefixSuffixInteraction2 */
    @org.junit.Test(timeout = 10000)
    public void testPrefixSuffixInteraction2_cf44866_failAssert3_cf45222() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("tG"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction());
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("tG/tS"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.YAction());
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("tG/tS/test"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.ZAction());
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("tG/tS/test/*"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction(9));
            for (java.lang.String s : cc.combinations("tG/tS/toto")) {
                java.util.List<ch.qos.logback.core.joran.action.Action> r = srs.matchActions(new ch.qos.logback.core.joran.spi.ElementPath(s));
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.action.Action vc_9560 = (ch.qos.logback.core.joran.action.Action)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_9560);
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementSelector vc_9559 = new ch.qos.logback.core.joran.spi.ElementSelector();
                // AssertGenerator add assertion
                java.util.ArrayList collection_1105352774 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1105352774, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_9559).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9559).duplicate()).duplicate()).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9559).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_9559).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_1081245924 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1081245924, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9559).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                java.util.ArrayList collection_1593281285 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1593281285, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9559).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_9559).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9559).duplicate()).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9559).duplicate()).size(), 0);
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_9556 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_9556);
                // StatementAdderMethod cloned existing statement
                vc_9556.addRule(vc_9559, vc_9560);
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_601 = "tG/tS/test/*";
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_9626 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // StatementAdderMethod cloned existing statement
                vc_9626.addRule(vc_9559, String_vc_601);
                org.junit.Assert.assertNull(r);
            }
            org.junit.Assert.fail("testPrefixSuffixInteraction2_cf44866 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.SimpleRuleStoreTest#testPrefixSuffixInteraction2 */
    @org.junit.Test(timeout = 10000)
    public void testPrefixSuffixInteraction2_cf44861_failAssert1_cf45002() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("tG"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction());
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("tG/tS"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.YAction());
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("tG/tS/test"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.ZAction());
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("tG/tS/test/*"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction(9));
            for (java.lang.String s : cc.combinations("tG/tS/toto")) {
                java.util.List<ch.qos.logback.core.joran.action.Action> r = srs.matchActions(new ch.qos.logback.core.joran.spi.ElementPath(s));
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementPath vc_9555 = new ch.qos.logback.core.joran.spi.ElementPath();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).duplicate()).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_1458779511 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1458779511, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).duplicate()).duplicate()).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_548984347 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_548984347, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).duplicate()).duplicate()).peekLast());
                // AssertGenerator add assertion
                java.util.ArrayList collection_1195205418 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1195205418, ((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).duplicate()).duplicate()).duplicate().equals(vc_9555));
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).duplicate()).duplicate().equals(vc_9555));
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).duplicate().equals(vc_9555));
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_9552 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_9552);
                // StatementAdderMethod cloned existing statement
                vc_9552.matchActions(vc_9555);
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.ElementPath vc_9586 = (ch.qos.logback.core.joran.spi.ElementPath)null;
                // StatementAdderMethod cloned existing statement
                vc_9552.matchActions(vc_9586);
                org.junit.Assert.assertNull(r);
            }
            org.junit.Assert.fail("testPrefixSuffixInteraction2_cf44861 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.SimpleRuleStoreTest#testPrefixSuffixInteraction2 */
    @org.junit.Test(timeout = 10000)
    public void testPrefixSuffixInteraction2_cf44861_failAssert1_cf45049_cf48904() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("tG"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction());
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("tG/tS"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.YAction());
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("tG/tS/test"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.ZAction());
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("tG/tS/test/*"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction(9));
            for (java.lang.String s : cc.combinations("tG/tS/toto")) {
                java.util.List<ch.qos.logback.core.joran.action.Action> r = srs.matchActions(new ch.qos.logback.core.joran.spi.ElementPath(s));
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementPath vc_9555 = new ch.qos.logback.core.joran.spi.ElementPath();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).duplicate()).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_1197410227 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1197410227, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).duplicate()).duplicate()).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_958901692 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_958901692, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).duplicate()).duplicate()).peekLast());
                // AssertGenerator add assertion
                java.util.ArrayList collection_168254417 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_168254417, ((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).duplicate()).duplicate()).duplicate().equals(vc_9555));
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).duplicate()).duplicate().equals(vc_9555));
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).duplicate().equals(vc_9555));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).duplicate()).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_784499144 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_784499144, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).duplicate()).duplicate()).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_1964205322 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1964205322, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).duplicate()).duplicate()).peekLast());
                // AssertGenerator add assertion
                java.util.ArrayList collection_508225396 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_508225396, ((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).duplicate()).duplicate()).duplicate().equals(vc_9555));
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).duplicate()).duplicate().equals(vc_9555));
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_9555).duplicate().equals(vc_9555));
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_9552 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_9552);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_9552);
                // StatementAdderMethod cloned existing statement
                vc_9552.matchActions(vc_9555);
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_9599 = new java.lang.String();
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementSelector vc_9597 = new ch.qos.logback.core.joran.spi.ElementSelector();
                // StatementAdderMethod cloned existing statement
                vc_9552.addRule(vc_9597, vc_9599);
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_10208 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // StatementAdderMethod cloned existing statement
                vc_10208.matchActions(vc_9555);
                org.junit.Assert.assertNull(r);
            }
            org.junit.Assert.fail("testPrefixSuffixInteraction2_cf44861 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.SimpleRuleStoreTest#testPrefixSuffixInteraction2 */
    @org.junit.Test(timeout = 10000)
    public void testPrefixSuffixInteraction2_cf44866_failAssert3_cf45232_cf46090() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("tG"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction());
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("tG/tS"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.YAction());
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("tG/tS/test"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.ZAction());
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("tG/tS/test/*"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction(9));
            for (java.lang.String s : cc.combinations("tG/tS/toto")) {
                java.util.List<ch.qos.logback.core.joran.action.Action> r = srs.matchActions(new ch.qos.logback.core.joran.spi.ElementPath(s));
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.action.Action vc_9560 = (ch.qos.logback.core.joran.action.Action)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_9560);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_9560);
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementSelector vc_9559 = new ch.qos.logback.core.joran.spi.ElementSelector();
                // AssertGenerator add assertion
                java.util.ArrayList collection_1215015358 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1215015358, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_9559).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9559).duplicate()).duplicate()).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9559).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_9559).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_180566384 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_180566384, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9559).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                java.util.ArrayList collection_890978205 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_890978205, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9559).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_9559).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9559).duplicate()).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9559).duplicate()).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_817152596 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_817152596, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_9559).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9559).duplicate()).duplicate()).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9559).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_9559).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_1364530018 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1364530018, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9559).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                java.util.ArrayList collection_1841576644 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1841576644, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9559).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_9559).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9559).duplicate()).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_9559).duplicate()).size(), 0);
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_9556 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_9556);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_9556);
                // StatementAdderMethod cloned existing statement
                vc_9556.addRule(vc_9559, vc_9560);
                // StatementAdderOnAssert create null value
                java.lang.String vc_9630 = (java.lang.String)null;
                // StatementAdderMethod cloned existing statement
                vc_9556.addRule(vc_9559, vc_9630);
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.ElementSelector vc_9772 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_9770 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // StatementAdderMethod cloned existing statement
                vc_9770.addRule(vc_9772, vc_9630);
                org.junit.Assert.assertNull(r);
            }
            org.junit.Assert.fail("testPrefixSuffixInteraction2_cf44866 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.SimpleRuleStoreTest#testSuffix */
    @org.junit.Test(timeout = 10000)
    public void testSuffix_cf57484_failAssert12_cf57615() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction());
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a/*"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.YAction());
            for (java.lang.String s : cc.combinations("a/b")) {
                java.util.List<ch.qos.logback.core.joran.action.Action> r = srs.matchActions(new ch.qos.logback.core.joran.spi.ElementPath(s));
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementPath vc_11891 = new ch.qos.logback.core.joran.spi.ElementPath();
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_11891).duplicate().equals(vc_11891));
                // AssertGenerator add assertion
                java.util.ArrayList collection_463070860 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_463070860, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11891).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11891).duplicate()).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_11891).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11891).duplicate()).duplicate()).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_361192878 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_361192878, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11891).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11891).duplicate()).duplicate().equals(vc_11891));
                // AssertGenerator add assertion
                java.util.ArrayList collection_444768395 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_444768395, ((ch.qos.logback.core.joran.spi.ElementPath)vc_11891).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11891).duplicate()).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11891).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_11891).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11891).duplicate()).duplicate()).duplicate().equals(vc_11891));
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_11888 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_11888);
                // StatementAdderMethod cloned existing statement
                vc_11888.matchActions(vc_11891);
                org.junit.Assert.assertNotNull(r);
                // MethodAssertGenerator build local variable
                Object o_15_0 = r.size();
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.action.Action vc_11832 = (ch.qos.logback.core.joran.action.Action)null;
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementSelector vc_11831 = new ch.qos.logback.core.joran.spi.ElementSelector();
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_11828 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // StatementAdderMethod cloned existing statement
                vc_11828.addRule(vc_11831, vc_11832);
                // MethodAssertGenerator build local variable
                Object o_25_0 = (r.get(0)) instanceof ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.YAction;
            }
            org.junit.Assert.fail("testSuffix_cf57484 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.SimpleRuleStoreTest#testSuffix */
    @org.junit.Test(timeout = 10000)
    public void testSuffix_cf57482_failAssert11_cf57581_cf59524() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction());
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a/*"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.YAction());
            for (java.lang.String s : cc.combinations("a/b")) {
                java.util.List<ch.qos.logback.core.joran.action.Action> r = srs.matchActions(new ch.qos.logback.core.joran.spi.ElementPath(s));
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementPath vc_11875 = new ch.qos.logback.core.joran.spi.ElementPath();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11875).duplicate()).duplicate()).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11875).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11875).duplicate()).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_346485951 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_346485951, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11875).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_11875).duplicate().equals(vc_11875));
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_11875).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11875).duplicate()).duplicate()).duplicate().equals(vc_11875));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_11875).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_748298533 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_748298533, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11875).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                java.util.ArrayList collection_1267407456 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1267407456, ((ch.qos.logback.core.joran.spi.ElementPath)vc_11875).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11875).duplicate()).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11875).duplicate()).duplicate().equals(vc_11875));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11875).duplicate()).duplicate()).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11875).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11875).duplicate()).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_535694318 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_535694318, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11875).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)vc_11875).duplicate().equals(vc_11875));
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)vc_11875).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11875).duplicate()).duplicate()).duplicate().equals(vc_11875));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)vc_11875).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_2086903008 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2086903008, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11875).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                java.util.ArrayList collection_1477135107 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1477135107, ((ch.qos.logback.core.joran.spi.ElementPath)vc_11875).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11875).duplicate()).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)vc_11875).duplicate()).duplicate().equals(vc_11875));
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_11872 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_11872);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_11872);
                // StatementAdderMethod cloned existing statement
                vc_11872.matchActions(vc_11875);
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_12367 = new java.lang.String();
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.ElementSelector vc_12364 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_12362 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // StatementAdderMethod cloned existing statement
                vc_12362.addRule(vc_12364, vc_12367);
                org.junit.Assert.assertNotNull(r);
                // MethodAssertGenerator build local variable
                Object o_15_0 = r.size();
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.action.Action vc_11832 = (ch.qos.logback.core.joran.action.Action)null;
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.ElementSelector vc_11830 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_11828 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // StatementAdderMethod cloned existing statement
                vc_11828.addRule(vc_11830, vc_11832);
                // MethodAssertGenerator build local variable
                Object o_25_0 = (r.get(0)) instanceof ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.YAction;
            }
            org.junit.Assert.fail("testSuffix_cf57482 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.joran.spi.SimpleRuleStoreTest#testSuffix */
    @org.junit.Test(timeout = 10000)
    public void testSuffix_cf57491_failAssert14_cf57702_cf63056() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.XAction());
            srs.addRule(new ch.qos.logback.core.joran.spi.ElementSelector("a/*"), new ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.YAction());
            for (java.lang.String s : cc.combinations("a/b")) {
                java.util.List<ch.qos.logback.core.joran.action.Action> r = srs.matchActions(new ch.qos.logback.core.joran.spi.ElementPath(s));
                // StatementAdderOnAssert create null value
                java.lang.String vc_11934 = (java.lang.String)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_11934);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_11934);
                // StatementAdderOnAssert create random local variable
                ch.qos.logback.core.joran.spi.ElementSelector vc_11933 = new ch.qos.logback.core.joran.spi.ElementSelector();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_11933).duplicate()).duplicate()).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_917521525 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_917521525, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_11933).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_11933).duplicate()).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_11933).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_11933).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_11933).duplicate()).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_91335746 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_91335746, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_11933).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                java.util.ArrayList collection_1487023300 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1487023300, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_11933).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_11933).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_11933).duplicate()).duplicate()).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_250639356 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_250639356, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_11933).duplicate()).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_11933).duplicate()).duplicate()).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementSelector)vc_11933).peekLast());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementSelector)vc_11933).size(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_11933).duplicate()).size(), 0);
                // AssertGenerator add assertion
                java.util.ArrayList collection_1724518449 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1724518449, ((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_11933).duplicate()).getCopyOfPartList());;
                // AssertGenerator add assertion
                java.util.ArrayList collection_1932127297 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1932127297, ((ch.qos.logback.core.joran.spi.ElementSelector)vc_11933).getCopyOfPartList());;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ElementPath)((ch.qos.logback.core.joran.spi.ElementSelector)vc_11933).duplicate()).peekLast());
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_11930 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_11930);
                // AssertGenerator add assertion
                org.junit.Assert.assertNull(vc_11930);
                // StatementAdderMethod cloned existing statement
                vc_11930.addRule(vc_11933, vc_11934);
                // StatementAdderOnAssert create random local variable
                java.lang.String vc_13135 = new java.lang.String();
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.ElementSelector vc_13132 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_13130 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // StatementAdderMethod cloned existing statement
                vc_13130.addRule(vc_13132, vc_13135);
                org.junit.Assert.assertNotNull(r);
                // MethodAssertGenerator build local variable
                Object o_15_0 = r.size();
                // StatementAdderOnAssert create literal from method
                java.lang.String String_vc_739 = "a/*";
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.ElementSelector vc_11836 = (ch.qos.logback.core.joran.spi.ElementSelector)null;
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.joran.spi.RuleStore vc_11834 = (ch.qos.logback.core.joran.spi.RuleStore)null;
                // StatementAdderMethod cloned existing statement
                vc_11834.addRule(vc_11836, String_vc_739);
                // MethodAssertGenerator build local variable
                Object o_25_0 = (r.get(0)) instanceof ch.qos.logback.core.joran.spi.AmplSimpleRuleStoreTest.YAction;
            }
            org.junit.Assert.fail("testSuffix_cf57491 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

