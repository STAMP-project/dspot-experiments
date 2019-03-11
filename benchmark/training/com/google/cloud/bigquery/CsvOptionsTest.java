/**
 * Copyright 2015 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigquery;


import FormatOptions.CSV;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;


public class CsvOptionsTest {
    private static final Boolean ALLOW_JAGGED_ROWS = true;

    private static final Boolean ALLOW_QUOTED_NEWLINE = true;

    private static final Charset ENCODING = StandardCharsets.UTF_8;

    private static final String FIELD_DELIMITER = ",";

    private static final String QUOTE = "\"";

    private static final long SKIP_LEADING_ROWS = 42L;

    private static final CsvOptions CSV_OPTIONS = CsvOptions.newBuilder().setAllowJaggedRows(CsvOptionsTest.ALLOW_JAGGED_ROWS).setAllowQuotedNewLines(CsvOptionsTest.ALLOW_QUOTED_NEWLINE).setEncoding(CsvOptionsTest.ENCODING).setFieldDelimiter(CsvOptionsTest.FIELD_DELIMITER).setQuote(CsvOptionsTest.QUOTE).setSkipLeadingRows(CsvOptionsTest.SKIP_LEADING_ROWS).build();

    @Test
    public void testToBuilder() {
        compareCsvOptions(CsvOptionsTest.CSV_OPTIONS, CsvOptionsTest.CSV_OPTIONS.toBuilder().build());
        CsvOptions csvOptions = CsvOptionsTest.CSV_OPTIONS.toBuilder().setFieldDelimiter(";").build();
        Assert.assertEquals(";", csvOptions.getFieldDelimiter());
        csvOptions = csvOptions.toBuilder().setFieldDelimiter(",").build();
        compareCsvOptions(CsvOptionsTest.CSV_OPTIONS, csvOptions);
    }

    @Test
    public void testToBuilderIncomplete() {
        CsvOptions csvOptions = CsvOptions.newBuilder().setFieldDelimiter("|").build();
        Assert.assertEquals(csvOptions, csvOptions.toBuilder().build());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(CSV, CsvOptionsTest.CSV_OPTIONS.getType());
        Assert.assertEquals(CsvOptionsTest.ALLOW_JAGGED_ROWS, CsvOptionsTest.CSV_OPTIONS.allowJaggedRows());
        Assert.assertEquals(CsvOptionsTest.ALLOW_QUOTED_NEWLINE, CsvOptionsTest.CSV_OPTIONS.allowQuotedNewLines());
        Assert.assertEquals(CsvOptionsTest.ENCODING.name(), CsvOptionsTest.CSV_OPTIONS.getEncoding());
        Assert.assertEquals(CsvOptionsTest.FIELD_DELIMITER, CsvOptionsTest.CSV_OPTIONS.getFieldDelimiter());
        Assert.assertEquals(CsvOptionsTest.QUOTE, CsvOptionsTest.CSV_OPTIONS.getQuote());
        Assert.assertEquals(CsvOptionsTest.SKIP_LEADING_ROWS, ((long) (CsvOptionsTest.CSV_OPTIONS.getSkipLeadingRows())));
    }

    @Test
    public void testToAndFromPb() {
        compareCsvOptions(CsvOptionsTest.CSV_OPTIONS, CsvOptions.fromPb(CsvOptionsTest.CSV_OPTIONS.toPb()));
        CsvOptions csvOptions = CsvOptions.newBuilder().setAllowJaggedRows(CsvOptionsTest.ALLOW_JAGGED_ROWS).build();
        compareCsvOptions(csvOptions, CsvOptions.fromPb(csvOptions.toPb()));
    }
}

