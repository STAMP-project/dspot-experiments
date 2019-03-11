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


import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.joran.action.Action;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.Attributes;


/**
 * Test SimpleRuleStore for various explicit rule combinations.
 *
 * We also test that explicit patterns are case sensitive.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class SimpleRuleStoreTest {
    SimpleRuleStore srs = new SimpleRuleStore(new ContextBase());

    CaseCombinator cc = new CaseCombinator();

    @Test
    public void smoke() throws Exception {
        srs.addRule(new ElementSelector("a/b"), new SimpleRuleStoreTest.XAction());
        // test for all possible case combinations of "a/b"
        for (String s : cc.combinations("a/b")) {
            System.out.println(("s=" + s));
            List<Action> r = srs.matchActions(new ElementPath(s));
            Assert.assertNotNull(r);
            Assert.assertEquals(1, r.size());
            if (!((r.get(0)) instanceof SimpleRuleStoreTest.XAction)) {
                Assert.fail("Wrong type");
            }
        }
    }

    @Test
    public void smokeII() throws Exception {
        srs.addRule(new ElementSelector("a/b"), new SimpleRuleStoreTest.XAction());
        srs.addRule(new ElementSelector("a/b"), new SimpleRuleStoreTest.YAction());
        for (String s : cc.combinations("a/b")) {
            List<Action> r = srs.matchActions(new ElementPath(s));
            Assert.assertNotNull(r);
            Assert.assertEquals(2, r.size());
            if (!((r.get(0)) instanceof SimpleRuleStoreTest.XAction)) {
                Assert.fail("Wrong type");
            }
            if (!((r.get(1)) instanceof SimpleRuleStoreTest.YAction)) {
                Assert.fail("Wrong type");
            }
        }
    }

    @Test
    public void testSlashSuffix() throws Exception {
        ElementSelector pa = new ElementSelector("a/");
        srs.addRule(pa, new SimpleRuleStoreTest.XAction());
        for (String s : cc.combinations("a")) {
            List<Action> r = srs.matchActions(new ElementPath(s));
            Assert.assertNotNull(r);
            Assert.assertEquals(1, r.size());
            if (!((r.get(0)) instanceof SimpleRuleStoreTest.XAction)) {
                Assert.fail("Wrong type");
            }
        }
    }

    @Test
    public void testTail1() throws Exception {
        srs.addRule(new ElementSelector("*/b"), new SimpleRuleStoreTest.XAction());
        for (String s : cc.combinations("a/b")) {
            List<Action> r = srs.matchActions(new ElementPath(s));
            Assert.assertNotNull(r);
            Assert.assertEquals(1, r.size());
            if (!((r.get(0)) instanceof SimpleRuleStoreTest.XAction)) {
                Assert.fail("Wrong type");
            }
        }
    }

    @Test
    public void testTail2() throws Exception {
        SimpleRuleStore srs = new SimpleRuleStore(new ContextBase());
        srs.addRule(new ElementSelector("*/c"), new SimpleRuleStoreTest.XAction());
        for (String s : cc.combinations("a/b/c")) {
            List<Action> r = srs.matchActions(new ElementPath(s));
            Assert.assertNotNull(r);
            Assert.assertEquals(1, r.size());
            if (!((r.get(0)) instanceof SimpleRuleStoreTest.XAction)) {
                Assert.fail("Wrong type");
            }
        }
    }

    @Test
    public void testTail3() throws Exception {
        srs.addRule(new ElementSelector("*/b"), new SimpleRuleStoreTest.XAction());
        srs.addRule(new ElementSelector("*/a/b"), new SimpleRuleStoreTest.YAction());
        for (String s : cc.combinations("a/b")) {
            List<Action> r = srs.matchActions(new ElementPath(s));
            Assert.assertNotNull(r);
            Assert.assertEquals(1, r.size());
            if (!((r.get(0)) instanceof SimpleRuleStoreTest.YAction)) {
                Assert.fail("Wrong type");
            }
        }
    }

    @Test
    public void testTail4() throws Exception {
        srs.addRule(new ElementSelector("*/b"), new SimpleRuleStoreTest.XAction());
        srs.addRule(new ElementSelector("*/a/b"), new SimpleRuleStoreTest.YAction());
        srs.addRule(new ElementSelector("a/b"), new SimpleRuleStoreTest.ZAction());
        for (String s : cc.combinations("a/b")) {
            List<Action> r = srs.matchActions(new ElementPath(s));
            Assert.assertNotNull(r);
            Assert.assertEquals(1, r.size());
            if (!((r.get(0)) instanceof SimpleRuleStoreTest.ZAction)) {
                Assert.fail("Wrong type");
            }
        }
    }

    @Test
    public void testSuffix() throws Exception {
        srs.addRule(new ElementSelector("a"), new SimpleRuleStoreTest.XAction());
        srs.addRule(new ElementSelector("a/*"), new SimpleRuleStoreTest.YAction());
        for (String s : cc.combinations("a/b")) {
            List<Action> r = srs.matchActions(new ElementPath(s));
            Assert.assertNotNull(r);
            Assert.assertEquals(1, r.size());
            Assert.assertTrue(((r.get(0)) instanceof SimpleRuleStoreTest.YAction));
        }
    }

    @Test
    public void testDeepSuffix() throws Exception {
        srs.addRule(new ElementSelector("a"), new SimpleRuleStoreTest.XAction(1));
        srs.addRule(new ElementSelector("a/b/*"), new SimpleRuleStoreTest.XAction(2));
        for (String s : cc.combinations("a/other")) {
            List<Action> r = srs.matchActions(new ElementPath(s));
            Assert.assertNull(r);
        }
    }

    @Test
    public void testPrefixSuffixInteraction1() throws Exception {
        srs.addRule(new ElementSelector("a"), new SimpleRuleStoreTest.ZAction());
        srs.addRule(new ElementSelector("a/*"), new SimpleRuleStoreTest.YAction());
        srs.addRule(new ElementSelector("*/a/b"), new SimpleRuleStoreTest.XAction(3));
        for (String s : cc.combinations("a/b")) {
            List<Action> r = srs.matchActions(new ElementPath(s));
            Assert.assertNotNull(r);
            Assert.assertEquals(1, r.size());
            Assert.assertTrue(((r.get(0)) instanceof SimpleRuleStoreTest.XAction));
            SimpleRuleStoreTest.XAction xaction = ((SimpleRuleStoreTest.XAction) (r.get(0)));
            Assert.assertEquals(3, xaction.id);
        }
    }

    @Test
    public void testPrefixSuffixInteraction2() throws Exception {
        srs.addRule(new ElementSelector("tG"), new SimpleRuleStoreTest.XAction());
        srs.addRule(new ElementSelector("tG/tS"), new SimpleRuleStoreTest.YAction());
        srs.addRule(new ElementSelector("tG/tS/test"), new SimpleRuleStoreTest.ZAction());
        srs.addRule(new ElementSelector("tG/tS/test/*"), new SimpleRuleStoreTest.XAction(9));
        for (String s : cc.combinations("tG/tS/toto")) {
            List<Action> r = srs.matchActions(new ElementPath(s));
            Assert.assertNull(r);
        }
    }

    class XAction extends Action {
        int id = 0;

        XAction() {
        }

        XAction(int id) {
            this.id = id;
        }

        public void begin(InterpretationContext ec, String name, Attributes attributes) {
        }

        public void end(InterpretationContext ec, String name) {
        }

        public void finish(InterpretationContext ec) {
        }

        public String toString() {
            return ("XAction(" + (id)) + ")";
        }
    }

    class YAction extends Action {
        public void begin(InterpretationContext ec, String name, Attributes attributes) {
        }

        public void end(InterpretationContext ec, String name) {
        }

        public void finish(InterpretationContext ec) {
        }
    }

    class ZAction extends Action {
        public void begin(InterpretationContext ec, String name, Attributes attributes) {
        }

        public void end(InterpretationContext ec, String name) {
        }

        public void finish(InterpretationContext ec) {
        }
    }
}

