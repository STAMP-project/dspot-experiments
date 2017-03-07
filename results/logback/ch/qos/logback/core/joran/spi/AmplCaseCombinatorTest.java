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


public class AmplCaseCombinatorTest {
    ch.qos.logback.core.joran.spi.CaseCombinator p = new ch.qos.logback.core.joran.spi.CaseCombinator();

    @org.junit.Test
    public void smoke() {
        ch.qos.logback.core.joran.spi.CaseCombinator p = new ch.qos.logback.core.joran.spi.CaseCombinator();
        java.util.List<java.lang.String> result = p.combinations("a-B=");
        java.util.List<java.lang.String> witness = new java.util.ArrayList<java.lang.String>();
        witness.add("a-b=");
        witness.add("A-b=");
        witness.add("a-B=");
        witness.add("A-B=");
        org.junit.Assert.assertEquals(witness, result);
    }

    @org.junit.Test
    public void other() {
        java.util.List<java.lang.String> result = p.combinations("aBCd");
        org.junit.Assert.assertEquals(16, result.size());
        java.util.Set<java.lang.String> witness = new java.util.HashSet<java.lang.String>(result);
        // check that there are no duplicates
        org.junit.Assert.assertEquals(16, witness.size());
    }
}

