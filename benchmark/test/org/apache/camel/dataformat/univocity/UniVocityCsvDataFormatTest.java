/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.dataformat.univocity;


import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests the options of {@link org.apache.camel.dataformat.univocity.UniVocityCsvDataFormat}.
 */
public final class UniVocityCsvDataFormatTest {
    @Test
    public void shouldConfigureNullValue() {
        UniVocityCsvDataFormat dataFormat = new UniVocityCsvDataFormat().setNullValue("N/A");
        Assert.assertEquals("N/A", dataFormat.getNullValue());
        Assert.assertEquals("N/A", dataFormat.createAndConfigureWriterSettings().getNullValue());
        Assert.assertEquals("N/A", dataFormat.createAndConfigureParserSettings().getNullValue());
    }

    @Test
    public void shouldConfigureSkipEmptyLines() {
        UniVocityCsvDataFormat dataFormat = new UniVocityCsvDataFormat().setSkipEmptyLines(true);
        Assert.assertTrue(dataFormat.getSkipEmptyLines());
        Assert.assertTrue(dataFormat.createAndConfigureWriterSettings().getSkipEmptyLines());
        Assert.assertTrue(dataFormat.createAndConfigureParserSettings().getSkipEmptyLines());
    }

    @Test
    public void shouldConfigureIgnoreTrailingWhitespaces() {
        UniVocityCsvDataFormat dataFormat = new UniVocityCsvDataFormat().setIgnoreTrailingWhitespaces(true);
        Assert.assertTrue(dataFormat.getIgnoreTrailingWhitespaces());
        Assert.assertTrue(dataFormat.createAndConfigureWriterSettings().getIgnoreTrailingWhitespaces());
        Assert.assertTrue(dataFormat.createAndConfigureParserSettings().getIgnoreTrailingWhitespaces());
    }

    @Test
    public void shouldConfigureIgnoreLeadingWhitespaces() {
        UniVocityCsvDataFormat dataFormat = new UniVocityCsvDataFormat().setIgnoreLeadingWhitespaces(true);
        Assert.assertTrue(dataFormat.getIgnoreLeadingWhitespaces());
        Assert.assertTrue(dataFormat.createAndConfigureWriterSettings().getIgnoreLeadingWhitespaces());
        Assert.assertTrue(dataFormat.createAndConfigureParserSettings().getIgnoreLeadingWhitespaces());
    }

    @Test
    public void shouldConfigureHeadersDisabled() {
        UniVocityCsvDataFormat dataFormat = new UniVocityCsvDataFormat().setHeadersDisabled(true);
        Assert.assertTrue(dataFormat.isHeadersDisabled());
        Assert.assertNull(dataFormat.createAndConfigureWriterSettings().getHeaders());
        Assert.assertNull(dataFormat.createAndConfigureParserSettings().getHeaders());
    }

    @Test
    public void shouldConfigureHeaders() {
        UniVocityCsvDataFormat dataFormat = new UniVocityCsvDataFormat().setHeaders(new String[]{ "A", "B", "C" });
        Assert.assertArrayEquals(new String[]{ "A", "B", "C" }, dataFormat.getHeaders());
        Assert.assertArrayEquals(new String[]{ "A", "B", "C" }, dataFormat.createAndConfigureWriterSettings().getHeaders());
        Assert.assertArrayEquals(new String[]{ "A", "B", "C" }, dataFormat.createAndConfigureParserSettings().getHeaders());
    }

    @Test
    public void shouldConfigureHeaderExtractionEnabled() {
        UniVocityCsvDataFormat dataFormat = new UniVocityCsvDataFormat().setHeaderExtractionEnabled(true);
        Assert.assertTrue(dataFormat.getHeaderExtractionEnabled());
        Assert.assertTrue(dataFormat.createAndConfigureParserSettings().isHeaderExtractionEnabled());
    }

    @Test
    public void shouldConfigureNumberOfRecordsToRead() {
        UniVocityCsvDataFormat dataFormat = new UniVocityCsvDataFormat().setNumberOfRecordsToRead(42);
        Assert.assertEquals(Integer.valueOf(42), dataFormat.getNumberOfRecordsToRead());
        Assert.assertEquals(42, dataFormat.createAndConfigureParserSettings().getNumberOfRecordsToRead());
    }

    @Test
    public void shouldConfigureEmptyValue() {
        UniVocityCsvDataFormat dataFormat = new UniVocityCsvDataFormat().setEmptyValue("empty");
        Assert.assertEquals("empty", dataFormat.getEmptyValue());
        Assert.assertEquals("empty", dataFormat.createAndConfigureWriterSettings().getEmptyValue());
        Assert.assertEquals("empty", dataFormat.createAndConfigureParserSettings().getEmptyValue());
    }

    @Test
    public void shouldConfigureLineSeparator() {
        UniVocityCsvDataFormat dataFormat = new UniVocityCsvDataFormat().setLineSeparator("ls");
        Assert.assertEquals("ls", dataFormat.getLineSeparator());
        Assert.assertEquals("ls", dataFormat.createAndConfigureWriterSettings().getFormat().getLineSeparatorString());
        Assert.assertEquals("ls", dataFormat.createAndConfigureParserSettings().getFormat().getLineSeparatorString());
    }

    @Test
    public void shouldConfigureNormalizedLineSeparator() {
        UniVocityCsvDataFormat dataFormat = new UniVocityCsvDataFormat().setNormalizedLineSeparator('n');
        Assert.assertEquals(Character.valueOf('n'), dataFormat.getNormalizedLineSeparator());
        Assert.assertEquals('n', dataFormat.createAndConfigureWriterSettings().getFormat().getNormalizedNewline());
        Assert.assertEquals('n', dataFormat.createAndConfigureParserSettings().getFormat().getNormalizedNewline());
    }

    @Test
    public void shouldConfigureComment() {
        UniVocityCsvDataFormat dataFormat = new UniVocityCsvDataFormat().setComment('c');
        Assert.assertEquals(Character.valueOf('c'), dataFormat.getComment());
        Assert.assertEquals('c', dataFormat.createAndConfigureWriterSettings().getFormat().getComment());
        Assert.assertEquals('c', dataFormat.createAndConfigureParserSettings().getFormat().getComment());
    }

    @Test
    public void shouldConfigureLazyLoad() {
        UniVocityCsvDataFormat dataFormat = new UniVocityCsvDataFormat().setLazyLoad(true);
        Assert.assertTrue(dataFormat.isLazyLoad());
    }

    @Test
    public void shouldConfigureAsMap() {
        UniVocityCsvDataFormat dataFormat = new UniVocityCsvDataFormat().setAsMap(true);
        Assert.assertTrue(dataFormat.isAsMap());
    }

    @Test
    public void shouldConfigureQuoteAllFields() {
        UniVocityCsvDataFormat dataFormat = new UniVocityCsvDataFormat().setQuoteAllFields(true);
        Assert.assertTrue(dataFormat.getQuoteAllFields());
        Assert.assertTrue(dataFormat.createAndConfigureWriterSettings().getQuoteAllFields());
    }

    @Test
    public void shouldConfigureQuote() {
        UniVocityCsvDataFormat dataFormat = new UniVocityCsvDataFormat().setQuote('q');
        Assert.assertEquals(Character.valueOf('q'), dataFormat.getQuote());
        Assert.assertEquals('q', dataFormat.createAndConfigureWriterSettings().getFormat().getQuote());
        Assert.assertEquals('q', dataFormat.createAndConfigureParserSettings().getFormat().getQuote());
    }

    @Test
    public void shouldConfigureQuoteEscape() {
        UniVocityCsvDataFormat dataFormat = new UniVocityCsvDataFormat().setQuoteEscape('e');
        Assert.assertEquals(Character.valueOf('e'), dataFormat.getQuoteEscape());
        Assert.assertEquals('e', dataFormat.createAndConfigureWriterSettings().getFormat().getQuoteEscape());
        Assert.assertEquals('e', dataFormat.createAndConfigureParserSettings().getFormat().getQuoteEscape());
    }

    @Test
    public void shouldConfigureDelimiter() {
        UniVocityCsvDataFormat dataFormat = new UniVocityCsvDataFormat().setDelimiter('d');
        Assert.assertEquals(Character.valueOf('d'), dataFormat.getDelimiter());
        Assert.assertEquals('d', dataFormat.createAndConfigureWriterSettings().getFormat().getDelimiter());
        Assert.assertEquals('d', dataFormat.createAndConfigureParserSettings().getFormat().getDelimiter());
    }
}

