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
 * This class tests the options of {@link org.apache.camel.dataformat.univocity.UniVocityFixedWidthDataFormat}.
 */
public final class UniVocityFixedWidthDataFormatTest {
    @Test
    public void shouldConfigureNullValue() {
        UniVocityFixedWidthDataFormat dataFormat = new UniVocityFixedWidthDataFormat().setFieldLengths(new int[]{ 1, 2, 3 }).setNullValue("N/A");
        Assert.assertEquals("N/A", dataFormat.getNullValue());
        Assert.assertEquals("N/A", dataFormat.createAndConfigureWriterSettings().getNullValue());
        Assert.assertEquals("N/A", dataFormat.createAndConfigureParserSettings().getNullValue());
    }

    @Test
    public void shouldConfigureSkipEmptyLines() {
        UniVocityFixedWidthDataFormat dataFormat = new UniVocityFixedWidthDataFormat().setFieldLengths(new int[]{ 1, 2, 3 }).setSkipEmptyLines(true);
        Assert.assertTrue(dataFormat.getSkipEmptyLines());
        Assert.assertTrue(dataFormat.createAndConfigureWriterSettings().getSkipEmptyLines());
        Assert.assertTrue(dataFormat.createAndConfigureParserSettings().getSkipEmptyLines());
    }

    @Test
    public void shouldConfigureIgnoreTrailingWhitespaces() {
        UniVocityFixedWidthDataFormat dataFormat = new UniVocityFixedWidthDataFormat().setFieldLengths(new int[]{ 1, 2, 3 }).setIgnoreTrailingWhitespaces(true);
        Assert.assertTrue(dataFormat.getIgnoreTrailingWhitespaces());
        Assert.assertTrue(dataFormat.createAndConfigureWriterSettings().getIgnoreTrailingWhitespaces());
        Assert.assertTrue(dataFormat.createAndConfigureParserSettings().getIgnoreTrailingWhitespaces());
    }

    @Test
    public void shouldConfigureIgnoreLeadingWhitespaces() {
        UniVocityFixedWidthDataFormat dataFormat = new UniVocityFixedWidthDataFormat().setFieldLengths(new int[]{ 1, 2, 3 }).setIgnoreLeadingWhitespaces(true);
        Assert.assertTrue(dataFormat.getIgnoreLeadingWhitespaces());
        Assert.assertTrue(dataFormat.createAndConfigureWriterSettings().getIgnoreLeadingWhitespaces());
        Assert.assertTrue(dataFormat.createAndConfigureParserSettings().getIgnoreLeadingWhitespaces());
    }

    @Test
    public void shouldConfigureHeadersDisabled() {
        UniVocityFixedWidthDataFormat dataFormat = new UniVocityFixedWidthDataFormat().setFieldLengths(new int[]{ 1, 2, 3 }).setHeadersDisabled(true);
        Assert.assertTrue(dataFormat.isHeadersDisabled());
        Assert.assertNull(dataFormat.createAndConfigureWriterSettings().getHeaders());
        Assert.assertNull(dataFormat.createAndConfigureParserSettings().getHeaders());
    }

    @Test
    public void shouldConfigureHeaders() {
        UniVocityFixedWidthDataFormat dataFormat = new UniVocityFixedWidthDataFormat().setFieldLengths(new int[]{ 1, 2, 3 }).setHeaders(new String[]{ "A", "B", "C" });
        Assert.assertArrayEquals(new String[]{ "A", "B", "C" }, dataFormat.getHeaders());
        Assert.assertArrayEquals(new String[]{ "A", "B", "C" }, dataFormat.createAndConfigureWriterSettings().getHeaders());
        Assert.assertArrayEquals(new String[]{ "A", "B", "C" }, dataFormat.createAndConfigureParserSettings().getHeaders());
    }

    @Test
    public void shouldConfigureHeaderExtractionEnabled() {
        UniVocityFixedWidthDataFormat dataFormat = new UniVocityFixedWidthDataFormat().setFieldLengths(new int[]{ 1, 2, 3 }).setHeaderExtractionEnabled(true);
        Assert.assertTrue(dataFormat.getHeaderExtractionEnabled());
        Assert.assertTrue(dataFormat.createAndConfigureParserSettings().isHeaderExtractionEnabled());
    }

    @Test
    public void shouldConfigureNumberOfRecordsToRead() {
        UniVocityFixedWidthDataFormat dataFormat = new UniVocityFixedWidthDataFormat().setFieldLengths(new int[]{ 1, 2, 3 }).setNumberOfRecordsToRead(42);
        Assert.assertEquals(Integer.valueOf(42), dataFormat.getNumberOfRecordsToRead());
        Assert.assertEquals(42, dataFormat.createAndConfigureParserSettings().getNumberOfRecordsToRead());
    }

    @Test
    public void shouldConfigureEmptyValue() {
        UniVocityFixedWidthDataFormat dataFormat = new UniVocityFixedWidthDataFormat().setFieldLengths(new int[]{ 1, 2, 3 }).setEmptyValue("empty");
        Assert.assertEquals("empty", dataFormat.getEmptyValue());
        Assert.assertEquals("empty", dataFormat.createAndConfigureWriterSettings().getEmptyValue());
    }

    @Test
    public void shouldConfigureLineSeparator() {
        UniVocityFixedWidthDataFormat dataFormat = new UniVocityFixedWidthDataFormat().setFieldLengths(new int[]{ 1, 2, 3 }).setLineSeparator("ls");
        Assert.assertEquals("ls", dataFormat.getLineSeparator());
        Assert.assertEquals("ls", dataFormat.createAndConfigureWriterSettings().getFormat().getLineSeparatorString());
        Assert.assertEquals("ls", dataFormat.createAndConfigureParserSettings().getFormat().getLineSeparatorString());
    }

    @Test
    public void shouldConfigureNormalizedLineSeparator() {
        UniVocityFixedWidthDataFormat dataFormat = new UniVocityFixedWidthDataFormat().setFieldLengths(new int[]{ 1, 2, 3 }).setNormalizedLineSeparator('n');
        Assert.assertEquals(Character.valueOf('n'), dataFormat.getNormalizedLineSeparator());
        Assert.assertEquals('n', dataFormat.createAndConfigureWriterSettings().getFormat().getNormalizedNewline());
        Assert.assertEquals('n', dataFormat.createAndConfigureParserSettings().getFormat().getNormalizedNewline());
    }

    @Test
    public void shouldConfigureComment() {
        UniVocityFixedWidthDataFormat dataFormat = new UniVocityFixedWidthDataFormat().setFieldLengths(new int[]{ 1, 2, 3 }).setComment('c');
        Assert.assertEquals(Character.valueOf('c'), dataFormat.getComment());
        Assert.assertEquals('c', dataFormat.createAndConfigureWriterSettings().getFormat().getComment());
        Assert.assertEquals('c', dataFormat.createAndConfigureParserSettings().getFormat().getComment());
    }

    @Test
    public void shouldConfigureLazyLoad() {
        UniVocityFixedWidthDataFormat dataFormat = new UniVocityFixedWidthDataFormat().setFieldLengths(new int[]{ 1, 2, 3 }).setLazyLoad(true);
        Assert.assertTrue(dataFormat.isLazyLoad());
    }

    @Test
    public void shouldConfigureAsMap() {
        UniVocityFixedWidthDataFormat dataFormat = new UniVocityFixedWidthDataFormat().setFieldLengths(new int[]{ 1, 2, 3 }).setAsMap(true);
        Assert.assertTrue(dataFormat.isAsMap());
    }

    @Test
    public void shouldConfigurePadding() {
        UniVocityFixedWidthDataFormat dataFormat = new UniVocityFixedWidthDataFormat().setFieldLengths(new int[]{ 1, 2, 3 }).setPadding('p');
        Assert.assertEquals(Character.valueOf('p'), dataFormat.getPadding());
        Assert.assertEquals('p', dataFormat.createAndConfigureWriterSettings().getFormat().getPadding());
        Assert.assertEquals('p', dataFormat.createAndConfigureParserSettings().getFormat().getPadding());
    }

    @Test
    public void shouldConfigureSkipTrailingCharsUntilNewline() {
        UniVocityFixedWidthDataFormat dataFormat = new UniVocityFixedWidthDataFormat().setFieldLengths(new int[]{ 1, 2, 3 }).setSkipTrailingCharsUntilNewline(true);
        Assert.assertTrue(dataFormat.getSkipTrailingCharsUntilNewline());
        Assert.assertTrue(dataFormat.createAndConfigureParserSettings().getSkipTrailingCharsUntilNewline());
    }

    @Test
    public void shouldConfigureRecordEndsOnNewline() {
        UniVocityFixedWidthDataFormat dataFormat = new UniVocityFixedWidthDataFormat().setFieldLengths(new int[]{ 1, 2, 3 }).setRecordEndsOnNewline(true);
        Assert.assertTrue(dataFormat.getRecordEndsOnNewline());
        Assert.assertTrue(dataFormat.createAndConfigureParserSettings().getRecordEndsOnNewline());
    }

    @Test
    public void shouldConfigureFieldLengthWithLengthsOnly() {
        UniVocityFixedWidthDataFormat dataFormat = new UniVocityFixedWidthDataFormat().setFieldLengths(new int[]{ 1, 2, 3 });
        Assert.assertArrayEquals(new int[]{ 1, 2, 3 }, dataFormat.getFieldLengths());
        dataFormat.createAndConfigureWriterSettings();
    }

    @Test
    public void shouldConfigureFieldLengthWithHeadersAndLengths() {
        UniVocityFixedWidthDataFormat dataFormat = new UniVocityFixedWidthDataFormat().setFieldLengths(new int[]{ 1, 2, 3 }).setHeaders(new String[]{ "A", "B", "C" });
        Assert.assertArrayEquals(new int[]{ 1, 2, 3 }, dataFormat.getFieldLengths());
        Assert.assertArrayEquals(new String[]{ "A", "B", "C" }, dataFormat.getHeaders());
        dataFormat.createAndConfigureWriterSettings();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowNoFieldLengths() {
        UniVocityFixedWidthDataFormat dataFormat = new UniVocityFixedWidthDataFormat();
        dataFormat.createAndConfigureWriterSettings();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowHeadersAndLengthsOfDifferentSize() {
        UniVocityFixedWidthDataFormat dataFormat = new UniVocityFixedWidthDataFormat().setFieldLengths(new int[]{ 1, 2, 3, 4 }).setHeaders(new String[]{ "A", "B", "C" });
        Assert.assertArrayEquals(new int[]{ 1, 2, 3, 4 }, dataFormat.getFieldLengths());
        Assert.assertArrayEquals(new String[]{ "A", "B", "C" }, dataFormat.getHeaders());
        dataFormat.createAndConfigureWriterSettings();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowHeadersWithSameName() {
        UniVocityFixedWidthDataFormat dataFormat = new UniVocityFixedWidthDataFormat().setFieldLengths(new int[]{ 1, 2, 3 }).setHeaders(new String[]{ "A", "B", "A" });
        Assert.assertArrayEquals(new int[]{ 1, 2, 3 }, dataFormat.getFieldLengths());
        Assert.assertArrayEquals(new String[]{ "A", "B", "A" }, dataFormat.getHeaders());
        dataFormat.createAndConfigureWriterSettings();
    }
}

