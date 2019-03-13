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
package org.apache.camel.dataformat.csv;


import CSVFormat.DEFAULT;
import CSVFormat.EXCEL;
import CSVFormat.MYSQL;
import QuoteMode.ALL;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests the creation of the proper {@link org.apache.commons.csv.CSVFormat} based on the properties of
 * {@link org.apache.camel.dataformat.csv.CsvDataFormat}.
 * It doesn't test the marshalling and unmarshalling based on the CSV format.
 */
public class CsvDataFormatTest {
    @Test
    public void shouldUseDefaultFormat() {
        CsvDataFormat dataFormat = new CsvDataFormat();
        // Properly initialized
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        // Properly used
        Assert.assertEquals(DEFAULT, dataFormat.getActiveFormat());
    }

    @Test
    public void shouldUseFormatFromConstructor() {
        CsvDataFormat dataFormat = new CsvDataFormat(CSVFormat.EXCEL);
        // Properly initialized
        Assert.assertSame(EXCEL, dataFormat.getFormat());
        // Properly used
        Assert.assertEquals(EXCEL, dataFormat.getActiveFormat());
    }

    @Test
    public void shouldUseSpecifiedFormat() {
        CsvDataFormat dataFormat = new CsvDataFormat().setFormat(MYSQL);
        // Properly saved
        Assert.assertSame(MYSQL, dataFormat.getFormat());
        // Properly used
        Assert.assertEquals(MYSQL, dataFormat.getActiveFormat());
    }

    @Test
    public void shouldFallbackToDefaultFormat() {
        CsvDataFormat dataFormat = setFormat(null);
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        // Properly used
        Assert.assertEquals(DEFAULT, dataFormat.getActiveFormat());
    }

    @Test
    public void shouldDefineFormatByName() {
        CsvDataFormat dataFormat = new CsvDataFormat().setFormatName("EXCEL");
        // Properly saved
        Assert.assertSame(EXCEL, dataFormat.getFormat());
        // Properly used
        Assert.assertEquals(EXCEL, dataFormat.getActiveFormat());
    }

    @Test
    public void shouldDisableCommentMarker() {
        CsvDataFormat dataFormat = new CsvDataFormat().setCommentMarkerDisabled(true).setCommentMarker('c');
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertTrue(dataFormat.isCommentMarkerDisabled());
        Assert.assertEquals(Character.valueOf('c'), dataFormat.getCommentMarker());
        // Properly used
        Assert.assertNull(dataFormat.getActiveFormat().getCommentMarker());
    }

    @Test
    public void shouldOverrideCommentMarker() {
        CsvDataFormat dataFormat = new CsvDataFormat().setCommentMarker('c');
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertEquals(Character.valueOf('c'), dataFormat.getCommentMarker());
        // Properly used
        Assert.assertEquals(Character.valueOf('c'), dataFormat.getActiveFormat().getCommentMarker());
    }

    @Test
    public void shouldOverrideDelimiter() {
        CsvDataFormat dataFormat = new CsvDataFormat().setDelimiter('d');
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertEquals(Character.valueOf('d'), dataFormat.getDelimiter());
        // Properly used
        Assert.assertEquals('d', dataFormat.getActiveFormat().getDelimiter());
    }

    @Test
    public void shouldDisableEscape() {
        CsvDataFormat dataFormat = new CsvDataFormat().setEscapeDisabled(true).setEscape('e');
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertTrue(dataFormat.isEscapeDisabled());
        Assert.assertEquals(Character.valueOf('e'), dataFormat.getEscape());
        // Properly used
        Assert.assertNull(dataFormat.getActiveFormat().getEscapeCharacter());
    }

    @Test
    public void shouldOverrideEscape() {
        CsvDataFormat dataFormat = new CsvDataFormat().setEscape('e');
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertEquals(Character.valueOf('e'), dataFormat.getEscape());
        // Properly used
        Assert.assertEquals(Character.valueOf('e'), dataFormat.getActiveFormat().getEscapeCharacter());
    }

    @Test
    public void shouldDisableHeader() {
        CsvDataFormat dataFormat = new CsvDataFormat().setHeaderDisabled(true).setHeader(new String[]{ "a", "b", "c" });
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertTrue(dataFormat.isHeaderDisabled());
        Assert.assertArrayEquals(new String[]{ "a", "b", "c" }, dataFormat.getHeader());
        // Properly used
        Assert.assertNull(dataFormat.getActiveFormat().getHeader());
    }

    @Test
    public void shouldOverrideHeader() {
        CsvDataFormat dataFormat = new CsvDataFormat().setHeader(new String[]{ "a", "b", "c" });
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertArrayEquals(new String[]{ "a", "b", "c" }, dataFormat.getHeader());
        // Properly used
        Assert.assertArrayEquals(new String[]{ "a", "b", "c" }, dataFormat.getActiveFormat().getHeader());
    }

    @Test
    public void shouldAllowMissingColumnNames() {
        CsvDataFormat dataFormat = new CsvDataFormat().setAllowMissingColumnNames(true);
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertEquals(Boolean.TRUE, dataFormat.getAllowMissingColumnNames());
        // Properly used
        Assert.assertTrue(dataFormat.getActiveFormat().getAllowMissingColumnNames());
    }

    @Test
    public void shouldNotAllowMissingColumnNames() {
        CsvDataFormat dataFormat = new CsvDataFormat().setAllowMissingColumnNames(false);
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertEquals(Boolean.FALSE, dataFormat.getAllowMissingColumnNames());
        // Properly used
        Assert.assertFalse(dataFormat.getActiveFormat().getAllowMissingColumnNames());
    }

    @Test
    public void shouldIgnoreEmptyLines() {
        CsvDataFormat dataFormat = new CsvDataFormat().setIgnoreEmptyLines(true);
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertEquals(Boolean.TRUE, dataFormat.getIgnoreEmptyLines());
        // Properly used
        Assert.assertTrue(dataFormat.getActiveFormat().getIgnoreEmptyLines());
    }

    @Test
    public void shouldNotIgnoreEmptyLines() {
        CsvDataFormat dataFormat = new CsvDataFormat().setIgnoreEmptyLines(false);
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertEquals(Boolean.FALSE, dataFormat.getIgnoreEmptyLines());
        // Properly used
        Assert.assertFalse(dataFormat.getActiveFormat().getIgnoreEmptyLines());
    }

    @Test
    public void shouldIgnoreSurroundingSpaces() {
        CsvDataFormat dataFormat = new CsvDataFormat().setIgnoreSurroundingSpaces(true);
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertEquals(Boolean.TRUE, dataFormat.getIgnoreSurroundingSpaces());
        // Properly used
        Assert.assertTrue(dataFormat.getActiveFormat().getIgnoreSurroundingSpaces());
    }

    @Test
    public void shouldNotIgnoreSurroundingSpaces() {
        CsvDataFormat dataFormat = new CsvDataFormat().setIgnoreSurroundingSpaces(false);
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertEquals(Boolean.FALSE, dataFormat.getIgnoreSurroundingSpaces());
        // Properly used
        Assert.assertFalse(dataFormat.getActiveFormat().getIgnoreSurroundingSpaces());
    }

    @Test
    public void shouldDisableNullString() {
        CsvDataFormat dataFormat = new CsvDataFormat().setNullStringDisabled(true).setNullString("****");
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertTrue(dataFormat.isNullStringDisabled());
        Assert.assertEquals("****", dataFormat.getNullString());
        // Properly used
        Assert.assertNull(dataFormat.getActiveFormat().getNullString());
    }

    @Test
    public void shouldOverrideNullString() {
        CsvDataFormat dataFormat = new CsvDataFormat().setNullString("****");
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertEquals("****", dataFormat.getNullString());
        // Properly used
        Assert.assertEquals("****", dataFormat.getActiveFormat().getNullString());
    }

    @Test
    public void shouldDisableQuote() {
        CsvDataFormat dataFormat = new CsvDataFormat().setQuoteDisabled(true).setQuote('q');
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertTrue(dataFormat.isQuoteDisabled());
        Assert.assertEquals(Character.valueOf('q'), dataFormat.getQuote());
        // Properly used
        Assert.assertNull(dataFormat.getActiveFormat().getQuoteCharacter());
    }

    @Test
    public void shouldOverrideQuote() {
        CsvDataFormat dataFormat = new CsvDataFormat().setQuote('q');
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertEquals(Character.valueOf('q'), dataFormat.getQuote());
        // Properly used
        Assert.assertEquals(Character.valueOf('q'), dataFormat.getActiveFormat().getQuoteCharacter());
    }

    @Test
    public void shouldOverrideQuoteMode() {
        CsvDataFormat dataFormat = new CsvDataFormat().setQuoteMode(ALL);
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertEquals(ALL, dataFormat.getQuoteMode());
        // Properly used
        Assert.assertEquals(ALL, dataFormat.getActiveFormat().getQuoteMode());
    }

    @Test
    public void shouldDisableRecordSeparator() {
        CsvDataFormat dataFormat = new CsvDataFormat().setRecordSeparatorDisabled(true).setRecordSeparator("separator");
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertTrue(dataFormat.isRecordSeparatorDisabled());
        Assert.assertEquals("separator", dataFormat.getRecordSeparator());
        // Properly used
        Assert.assertNull(dataFormat.getActiveFormat().getRecordSeparator());
    }

    @Test
    public void shouldOverrideRecordSeparator() {
        CsvDataFormat dataFormat = new CsvDataFormat().setRecordSeparator("separator");
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertEquals("separator", dataFormat.getRecordSeparator());
        // Properly used
        Assert.assertEquals("separator", dataFormat.getActiveFormat().getRecordSeparator());
    }

    @Test
    public void shouldSkipHeaderRecord() {
        CsvDataFormat dataFormat = new CsvDataFormat().setSkipHeaderRecord(true);
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertEquals(Boolean.TRUE, dataFormat.getSkipHeaderRecord());
        // Properly used
        Assert.assertTrue(dataFormat.getActiveFormat().getSkipHeaderRecord());
    }

    @Test
    public void shouldNotSkipHeaderRecord() {
        CsvDataFormat dataFormat = new CsvDataFormat().setSkipHeaderRecord(false);
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertEquals(Boolean.FALSE, dataFormat.getSkipHeaderRecord());
        // Properly used
        Assert.assertFalse(dataFormat.getActiveFormat().getSkipHeaderRecord());
    }

    @Test
    public void shouldHandleLazyLoad() {
        CsvDataFormat dataFormat = new CsvDataFormat().setLazyLoad(true);
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertTrue(dataFormat.isLazyLoad());
        // Properly used (it doesn't modify the format)
        Assert.assertEquals(DEFAULT, dataFormat.getActiveFormat());
    }

    @Test
    public void shouldHandleUseMaps() {
        CsvDataFormat dataFormat = new CsvDataFormat().setUseMaps(true);
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertTrue(dataFormat.isUseMaps());
        // Properly used (it doesn't modify the format)
        Assert.assertEquals(DEFAULT, dataFormat.getActiveFormat());
    }

    @Test
    public void shouldHandleRecordConverter() {
        CsvRecordConverter<String> converter = new CsvRecordConverter<String>() {
            @Override
            public String convertRecord(CSVRecord record) {
                return record.toString();
            }
        };
        CsvDataFormat dataFormat = new CsvDataFormat().setRecordConverter(converter);
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertSame(converter, dataFormat.getRecordConverter());
        // Properly used (it doesn't modify the format)
        Assert.assertEquals(DEFAULT, dataFormat.getActiveFormat());
    }

    @Test
    public void testTrim() {
        // Set to TRUE
        CsvDataFormat dataFormat = new CsvDataFormat().setTrim(true);
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertEquals(Boolean.TRUE, dataFormat.getTrim());
        // Properly used
        Assert.assertTrue(dataFormat.getActiveFormat().getTrim());
        // NOT set
        dataFormat = new CsvDataFormat();
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertEquals(null, dataFormat.getTrim());
        // Properly used
        Assert.assertFalse(dataFormat.getActiveFormat().getTrim());
        // Set to false
        dataFormat = new CsvDataFormat().setTrim(false);
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertEquals(Boolean.FALSE, dataFormat.getTrim());
        // Properly used
        Assert.assertFalse(dataFormat.getActiveFormat().getTrim());
    }

    @Test
    public void testIgnoreHeaderCase() {
        // Set to TRUE
        CsvDataFormat dataFormat = new CsvDataFormat().setIgnoreHeaderCase(true);
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertEquals(Boolean.TRUE, dataFormat.getIgnoreHeaderCase());
        // Properly used
        Assert.assertTrue(dataFormat.getActiveFormat().getIgnoreHeaderCase());
        // NOT set
        dataFormat = new CsvDataFormat();
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertEquals(null, dataFormat.getIgnoreHeaderCase());
        // Properly used
        Assert.assertFalse(dataFormat.getActiveFormat().getIgnoreHeaderCase());
        // Set to false
        dataFormat = new CsvDataFormat().setIgnoreHeaderCase(false);
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertEquals(Boolean.FALSE, dataFormat.getIgnoreHeaderCase());
        // Properly used
        Assert.assertFalse(dataFormat.getActiveFormat().getIgnoreHeaderCase());
    }

    @Test
    public void testTrailingDelimiter() {
        // Set to TRUE
        CsvDataFormat dataFormat = new CsvDataFormat().setTrailingDelimiter(true);
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertEquals(Boolean.TRUE, dataFormat.getTrailingDelimiter());
        // Properly used
        Assert.assertTrue(dataFormat.getActiveFormat().getTrailingDelimiter());
        // NOT set
        dataFormat = new CsvDataFormat();
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertEquals(null, dataFormat.getTrailingDelimiter());
        // Properly used
        Assert.assertFalse(dataFormat.getActiveFormat().getTrailingDelimiter());
        // Set to false
        dataFormat = new CsvDataFormat().setTrailingDelimiter(false);
        // Properly saved
        Assert.assertSame(DEFAULT, dataFormat.getFormat());
        Assert.assertEquals(Boolean.FALSE, dataFormat.getTrailingDelimiter());
        // Properly used
        Assert.assertFalse(dataFormat.getActiveFormat().getTrailingDelimiter());
    }
}

