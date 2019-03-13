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
package org.eclipse.jetty.websocket.api.util;


import java.util.Iterator;
import java.util.NoSuchElementException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Test QuoteUtil
 */
public class QuoteUtilTest {
    @Test
    public void testSplitAt_PreserveQuoting() {
        Iterator<String> iter = QuoteUtil.splitAt("permessage-compress; method=\"foo, bar\"", ";");
        assertSplitAt(iter, "permessage-compress", "method=\"foo, bar\"");
    }

    @Test
    public void testSplitAt_PreserveQuotingWithNestedDelim() {
        Iterator<String> iter = QuoteUtil.splitAt("permessage-compress; method=\"foo; x=10\"", ";");
        assertSplitAt(iter, "permessage-compress", "method=\"foo; x=10\"");
    }

    @Test
    public void testSplitAtAllWhitespace() {
        Iterator<String> iter = QuoteUtil.splitAt("   ", "=");
        MatcherAssert.assertThat("Has Next", iter.hasNext(), Matchers.is(false));
        Assertions.assertThrows(NoSuchElementException.class, () -> iter.next());
    }

    @Test
    public void testSplitAtEmpty() {
        Iterator<String> iter = QuoteUtil.splitAt("", "=");
        MatcherAssert.assertThat("Has Next", iter.hasNext(), Matchers.is(false));
        Assertions.assertThrows(NoSuchElementException.class, () -> iter.next());
    }

    @Test
    public void testSplitAtHelloWorld() {
        Iterator<String> iter = QuoteUtil.splitAt("Hello World", " =");
        assertSplitAt(iter, "Hello", "World");
    }

    @Test
    public void testSplitAtKeyValue_Message() {
        Iterator<String> iter = QuoteUtil.splitAt("method=\"foo, bar\"", "=");
        assertSplitAt(iter, "method", "foo, bar");
    }

    @Test
    public void testSplitAtQuotedDelim() {
        // test that split ignores delimiters that occur within a quoted
        // part of the sequence.
        Iterator<String> iter = QuoteUtil.splitAt("A,\"B,C\",D", ",");
        assertSplitAt(iter, "A", "B,C", "D");
    }

    @Test
    public void testSplitAtSimple() {
        Iterator<String> iter = QuoteUtil.splitAt("Hi", "=");
        assertSplitAt(iter, "Hi");
    }

    @Test
    public void testSplitKeyValue_Quoted() {
        Iterator<String> iter = QuoteUtil.splitAt("Key = \"Value\"", "=");
        assertSplitAt(iter, "Key", "Value");
    }

    @Test
    public void testSplitKeyValue_QuotedValueList() {
        Iterator<String> iter = QuoteUtil.splitAt("Fruit = \"Apple, Banana, Cherry\"", "=");
        assertSplitAt(iter, "Fruit", "Apple, Banana, Cherry");
    }

    @Test
    public void testSplitKeyValue_QuotedWithDelim() {
        Iterator<String> iter = QuoteUtil.splitAt("Key = \"Option=Value\"", "=");
        assertSplitAt(iter, "Key", "Option=Value");
    }

    @Test
    public void testSplitKeyValue_Simple() {
        Iterator<String> iter = QuoteUtil.splitAt("Key=Value", "=");
        assertSplitAt(iter, "Key", "Value");
    }

    @Test
    public void testSplitKeyValue_WithWhitespace() {
        Iterator<String> iter = QuoteUtil.splitAt("Key = Value", "=");
        assertSplitAt(iter, "Key", "Value");
    }

    @Test
    public void testQuoteIfNeeded() {
        StringBuilder buf = new StringBuilder();
        QuoteUtil.quoteIfNeeded(buf, "key", ",");
        MatcherAssert.assertThat("key", buf.toString(), Matchers.is("key"));
    }

    @Test
    public void testQuoteIfNeeded_null() {
        StringBuilder buf = new StringBuilder();
        QuoteUtil.quoteIfNeeded(buf, null, ";=");
        MatcherAssert.assertThat("<null>", buf.toString(), Matchers.is(""));
    }
}

