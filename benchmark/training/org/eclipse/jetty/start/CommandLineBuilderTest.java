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
package org.eclipse.jetty.start;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class CommandLineBuilderTest {
    private CommandLineBuilder cmd = new CommandLineBuilder("java");

    @Test
    public void testSimpleCommandline() {
        MatcherAssert.assertThat(cmd.toString(), Matchers.is("java -Djava.io.tmpdir=/home/java/temp\\ dir/ --version"));
    }

    @Test
    public void testQuotingSimple() {
        assertQuoting("/opt/jetty", "/opt/jetty");
    }

    @Test
    public void testQuotingSpaceInPath() {
        assertQuoting("/opt/jetty 7/home", "/opt/jetty\\ 7/home");
    }

    @Test
    public void testQuotingSpaceAndQuotesInPath() {
        assertQuoting("/opt/jetty 7 \"special\"/home", "/opt/jetty\\ 7\\ \\\"special\\\"/home");
    }

    @Test
    public void testToStringIsQuotedEvenIfArgsAreNotQuotedForProcessBuilder() {
        System.out.println(cmd.toString());
    }

    @Test
    public void testQuoteQuotationMarks() {
        assertQuoting("-XX:OnOutOfMemoryError='kill -9 %p'", "-XX:OnOutOfMemoryError='kill -9 %p'");
    }
}

