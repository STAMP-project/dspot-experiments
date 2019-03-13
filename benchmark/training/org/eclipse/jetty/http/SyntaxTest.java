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
package org.eclipse.jetty.http;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class SyntaxTest {
    @Test
    public void testRequireValidRFC2616Token_Good() {
        String[] tokens = new String[]{ "name", "", null, "n.a.m.e", "na-me", "+name", "na*me", "na$me", "#name" };
        for (String token : tokens) {
            Syntax.requireValidRFC2616Token(token, "Test Based");
            // No exception should occur here
        }
    }

    @Test
    public void testRequireValidRFC2616Token_Bad() {
        String[] tokens = new String[]{ "\"name\"", "name\t", "na me", "name\u0082", "na\tme", "na;me", "{name}", "[name]", "\"" };
        for (String token : tokens) {
            try {
                Syntax.requireValidRFC2616Token(token, "Test Based");
                Assertions.fail(((("RFC2616 Token [" + token) + "] Should have thrown ") + (IllegalArgumentException.class.getName())));
            } catch (IllegalArgumentException e) {
                MatcherAssert.assertThat((("Testing Bad RFC2616 Token [" + token) + "]"), e.getMessage(), CoreMatchers.allOf(CoreMatchers.containsString("Test Based"), CoreMatchers.containsString("RFC2616")));
            }
        }
    }

    @Test
    public void testRequireValidRFC6265CookieValue_Good() {
        String[] values = new String[]{ "value", "", null, "val=ue", "val-ue", "\"value\"", "val/ue", "v.a.l.u.e" };
        for (String value : values) {
            Syntax.requireValidRFC6265CookieValue(value);
            // No exception should occur here
        }
    }

    @Test
    public void testRequireValidRFC6265CookieValue_Bad() {
        String[] values = new String[]{ "va\tlue", "\t", "value\u0000", "val\u0082ue", "va lue", "va;lue", "\"value", "value\"", "val\\ue", "val\"ue", "\"" };
        for (String value : values) {
            try {
                Syntax.requireValidRFC6265CookieValue(value);
                Assertions.fail(((("RFC6265 Cookie Value [" + value) + "] Should have thrown ") + (IllegalArgumentException.class.getName())));
            } catch (IllegalArgumentException e) {
                MatcherAssert.assertThat((("Testing Bad RFC6265 Cookie Value [" + value) + "]"), e.getMessage(), CoreMatchers.containsString("RFC6265"));
            }
        }
    }
}

