/**
 * Copyright 2014 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx.fileloader;


import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.Test;


public class CSVTest {
    private static final boolean IS_WINDOWS = System.getProperty("os.name").contains("indow");

    public static final String TABLE_ROWS_TEST_CSV = "tableRowsTest.csv";

    public static final String INT_ROWS_TEST_CSV = "intTableTest.csv";

    public static final String BIG_INT_ROWS_TEST_CSV = "bigIntTableTest.csv";

    public static final String DOUBLE_ROWS_TEST_CSV = "doubleTableTest.csv";

    @Test
    public void shouldReturnDataAsListForPlot() throws Exception {
        // when
        List<Map<String, Object>> values = new CSV().read(CSVTest.getOsAppropriatePath(getClass().getClassLoader(), CSVTest.TABLE_ROWS_TEST_CSV));
        // then
        assertThat(values.get(2).get("m3")).isEqualTo(8);
        assertThat(values.get(2).get("time")).isEqualTo(new SimpleDateFormat("yyyy-MM-dd").parse("1990-03-31"));
    }

    @Test
    public void shouldReturnInt() throws Exception {
        // when
        List<Map<String, Object>> values = new CSV().read(CSVTest.getOsAppropriatePath(getClass().getClassLoader(), CSVTest.INT_ROWS_TEST_CSV));
        // then
        assertThat(values.get(0).get("a")).isEqualTo(1);
    }

    @Test
    public void shouldReturnBigInt() throws Exception {
        // when
        List<Map<String, Object>> values = new CSV().read(CSVTest.getOsAppropriatePath(getClass().getClassLoader(), CSVTest.BIG_INT_ROWS_TEST_CSV));
        // then
        assertThat(values.get(0).get("a")).isEqualTo(new BigInteger("123456789123456789"));
    }

    @Test
    public void shouldReturnDouble() throws Exception {
        // when
        List<Map<String, Object>> values = new CSV().read(CSVTest.getOsAppropriatePath(getClass().getClassLoader(), CSVTest.DOUBLE_ROWS_TEST_CSV));
        // then
        assertThat(values.get(0).get("a")).isEqualTo(7.8981);
    }

    @Test
    public void dateFormat() throws Exception {
        // when
        List<Map<String, Object>> values = new CSV().read(CSVTest.getOsAppropriatePath(getClass().getClassLoader(), "interest-rates.csv"));
        // then
        Date time = ((Date) (values.get(0).get("time")));
        assertThat(time.getTime()).isEqualTo(633744000000L);
    }
}

