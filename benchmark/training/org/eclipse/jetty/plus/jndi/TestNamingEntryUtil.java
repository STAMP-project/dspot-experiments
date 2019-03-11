/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.plus.jndi;


import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static NamingEntry.__contextName;


public class TestNamingEntryUtil {
    public class MyNamingEntry extends NamingEntry {
        public MyNamingEntry(Object scope, String name, Object value) throws NamingException {
            super(scope, name);
            save(value);
        }
    }

    public class ScopeA {
        @Override
        public String toString() {
            return ((this.getClass().getName()) + "@") + (Long.toHexString(super.hashCode()));
        }
    }

    @Test
    public void testGetNameForScope() throws Exception {
        TestNamingEntryUtil.ScopeA scope = new TestNamingEntryUtil.ScopeA();
        Name name = NamingEntryUtil.getNameForScope(scope);
        Assertions.assertNotNull(name);
        Assertions.assertEquals(scope.toString(), name.toString());
    }

    @Test
    public void testGetContextForScope() throws Exception {
        TestNamingEntryUtil.ScopeA scope = new TestNamingEntryUtil.ScopeA();
        try {
            Context c = NamingEntryUtil.getContextForScope(scope);
            Assertions.fail("context should not exist");
        } catch (NameNotFoundException e) {
            // expected
        }
        InitialContext ic = new InitialContext();
        Context scopeContext = ic.createSubcontext(NamingEntryUtil.getNameForScope(scope));
        Assertions.assertNotNull(scopeContext);
        try {
            Context c = NamingEntryUtil.getContextForScope(scope);
            Assertions.assertNotNull(c);
        } catch (NameNotFoundException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testMakeNamingEntryName() throws Exception {
        Name name = NamingEntryUtil.makeNamingEntryName(null, "fee/fi/fo/fum");
        Assertions.assertNotNull(name);
        Assertions.assertEquals(((__contextName) + "/fee/fi/fo/fum"), name.toString());
    }

    @Test
    public void testLookupNamingEntry() throws Exception {
        TestNamingEntryUtil.ScopeA scope = new TestNamingEntryUtil.ScopeA();
        NamingEntry ne = NamingEntryUtil.lookupNamingEntry(scope, "foo");
        Assertions.assertNull(ne);
        TestNamingEntryUtil.MyNamingEntry mne = new TestNamingEntryUtil.MyNamingEntry(scope, "foo", 9);
        ne = NamingEntryUtil.lookupNamingEntry(scope, "foo");
        Assertions.assertNotNull(ne);
        Assertions.assertEquals(ne, mne);
    }

    @Test
    public void testLookupNamingEntries() throws Exception {
        TestNamingEntryUtil.ScopeA scope = new TestNamingEntryUtil.ScopeA();
        List<?> list = NamingEntryUtil.lookupNamingEntries(scope, TestNamingEntryUtil.MyNamingEntry.class);
        MatcherAssert.assertThat(list, Matchers.is(Matchers.empty()));
        TestNamingEntryUtil.MyNamingEntry mne1 = new TestNamingEntryUtil.MyNamingEntry(scope, "a/b", 1);
        TestNamingEntryUtil.MyNamingEntry mne2 = new TestNamingEntryUtil.MyNamingEntry(scope, "a/c", 2);
        TestNamingEntryUtil.ScopeA scope2 = new TestNamingEntryUtil.ScopeA();
        TestNamingEntryUtil.MyNamingEntry mne3 = new TestNamingEntryUtil.MyNamingEntry(scope2, "a/b", 3);
        list = NamingEntryUtil.lookupNamingEntries(scope, TestNamingEntryUtil.MyNamingEntry.class);
        MatcherAssert.assertThat(list, Matchers.containsInAnyOrder(mne1, mne2));
        list = NamingEntryUtil.lookupNamingEntries(scope2, TestNamingEntryUtil.MyNamingEntry.class);
        MatcherAssert.assertThat(list, Matchers.containsInAnyOrder(mne3));
    }
}

