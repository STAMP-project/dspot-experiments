/**
 * Copyright (c) 2012-2018 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package io.moquette.broker.config;


import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;


public class ConfigurationParserTest {
    ConfigurationParser m_parser;

    @Test
    public void checkDefaultOptions() {
        Properties props = m_parser.getProperties();
        // verifyDefaults(props);
        Assert.assertTrue(props.isEmpty());
    }

    @Test
    public void parseEmpty() throws ParseException {
        Reader conf = new StringReader("  ");
        m_parser.parse(conf);
        // Verify
        // verifyDefaults(m_parser.getProperties());
        Assert.assertTrue(m_parser.getProperties().isEmpty());
    }

    @Test
    public void parseValidComment() throws ParseException {
        Reader conf = new StringReader("#simple comment");
        m_parser.parse(conf);
        // Verify
        // verifyDefaults(m_parser.getProperties());
        Assert.assertTrue(m_parser.getProperties().isEmpty());
    }

    @Test(expected = ParseException.class)
    public void parseInvalidComment() throws ParseException {
        Reader conf = new StringReader(" #simple comment");
        m_parser.parse(conf);
    }

    @Test
    public void parseSingleVariable() throws ParseException {
        Reader conf = new StringReader("port 1234");
        m_parser.parse(conf);
        // Verify
        Assert.assertEquals("1234", m_parser.getProperties().getProperty("port"));
    }

    @Test
    public void parseCompleteFile() throws ParseException {
        String content = "# This is initial m_config format \r\n" + ((("  \r\n" + "port 1234 \r\n") + "host   localhost \r\n") + "fake  multi word string property\r\n");
        Reader conf = new StringReader(content);
        m_parser.parse(conf);
        // Verify
        Properties props = m_parser.getProperties();
        Assert.assertEquals("1234", props.getProperty("port"));
        Assert.assertEquals("localhost", props.getProperty("host"));
        Assert.assertEquals("multi word string property", props.getProperty("fake"));
    }
}

