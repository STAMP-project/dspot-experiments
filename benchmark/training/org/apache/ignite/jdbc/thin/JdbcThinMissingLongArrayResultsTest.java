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
package org.apache.ignite.jdbc.thin;


import QuerySqlField.Group;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Random;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.junit.Test;


/**
 *
 */
public class JdbcThinMissingLongArrayResultsTest extends JdbcThinAbstractSelfTest {
    /**
     * First cache name.
     */
    private static final String CACHE_NAME = "test";

    /**
     * URL.
     */
    private static final String URL = "jdbc:ignite:thin://127.0.0.1";

    /**
     * Grid count.
     */
    private static final int GRID_CNT = 2;

    /**
     * Rand.
     */
    private static final Random RAND = new Random(123);

    /**
     * Sample size.
     */
    private static final int SAMPLE_SIZE = 1000000;

    /**
     * Block size.
     */
    private static final int BLOCK_SIZE = 5000;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings({ "unused" })
    @Test
    public void testDefaults() throws Exception {
        try (Connection conn = DriverManager.getConnection(JdbcThinMissingLongArrayResultsTest.URL)) {
            conn.setSchema((('"' + (JdbcThinMissingLongArrayResultsTest.CACHE_NAME)) + '"'));
            try (PreparedStatement st = conn.prepareStatement("SELECT * FROM VALUE")) {
                ResultSet rs = st.executeQuery();
                int cols = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(rs.getObject(1).toString());
                    for (int i = 1; i < cols; ++i)
                        sb.append(", ").append(rs.getObject((i + 1)).toString());

                    System.out.println(sb.toString());
                } 
            }
        }
    }

    /**
     *
     */
    @SuppressWarnings("unused")
    public static class Key implements Serializable {
        @QuerySqlField(orderedGroups = { @Group(name = "date_sec_idx", order = 0, descending = true) })
        private long date;

        @QuerySqlField(index = true, orderedGroups = { @Group(name = "date_sec_idx", order = 3) })
        private int securityId;

        /**
         *
         */
        public Key() {
            // Required for default binary serialization
        }

        /**
         *
         *
         * @return Date.
         */
        public long getDate() {
            return date;
        }

        /**
         *
         *
         * @param securityId
         * 		Security ID.
         */
        public void setSecurityId(int securityId) {
            this.securityId = securityId;
        }

        /**
         *
         *
         * @param date
         * 		Date.
         */
        public void setDate(long date) {
            this.date = date;
        }
    }

    /**
     *
     */
    public static class Value implements Serializable {
        /**
         * Time.
         */
        @QuerySqlField
        private long[] time;

        /**
         * Open.
         */
        @QuerySqlField
        private double[] open;

        /**
         * High.
         */
        @QuerySqlField
        private double[] high;

        /**
         * Low.
         */
        @QuerySqlField
        private double[] low;

        /**
         * Close.
         */
        @QuerySqlField
        private double[] close;

        /**
         * Market vwap.
         */
        @QuerySqlField
        private double[] marketVWAP;

        /**
         *
         */
        public Value() {
            // Required for default binary serialization
        }

        /**
         *
         *
         * @return close.
         */
        public double[] getClose() {
            return close;
        }

        /**
         *
         *
         * @return time.
         */
        public long[] getTime() {
            return time;
        }

        /**
         *
         *
         * @param time
         * 		time.
         */
        public void setTime(long[] time) {
            this.time = time;
        }

        /**
         *
         *
         * @param open
         * 		open.
         */
        public void setOpen(double[] open) {
            this.open = open;
        }

        /**
         *
         *
         * @param high
         * 		high.
         */
        public void setHigh(double[] high) {
            this.high = high;
        }

        /**
         *
         *
         * @param low
         * 		low.
         */
        public void setLow(double[] low) {
            this.low = low;
        }

        /**
         *
         *
         * @param close
         * 		close.
         */
        public void setClose(double[] close) {
            this.close = close;
        }

        /**
         *
         *
         * @param marketVWAP
         * 		marketVWAP.
         */
        public void setMarketVWAP(double[] marketVWAP) {
            this.marketVWAP = marketVWAP;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return (((((((((((("OHLC{" + ", time=") + (Arrays.toString(time))) + ", open=") + (Arrays.toString(open))) + ", high=") + (Arrays.toString(high))) + ", low=") + (Arrays.toString(low))) + ", close=") + (Arrays.toString(close))) + ", marketVWAP=") + (Arrays.toString(marketVWAP))) + '}';
        }
    }
}

