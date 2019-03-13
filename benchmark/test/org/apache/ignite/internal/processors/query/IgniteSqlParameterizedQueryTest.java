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
package org.apache.ignite.internal.processors.query;


import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.processors.cache.index.AbstractIndexingCommonTest;
import org.junit.Test;


/**
 * Test sql queries with parameters for all types.
 * The test is fix  for issue 'IGNITE-6286'
 *
 * @author Sergey Chernolyas &amp;sergey_chernolyas@gmail.com&amp;
 * @see <a href="https://issues.apache.org/jira/browse/IGNITE-6286">IGNITE-6286</a>
 */
public class IgniteSqlParameterizedQueryTest extends AbstractIndexingCommonTest {
    /**
     *
     */
    private static final String CACHE_BOOKMARK = "Bookmark";

    /**
     *
     */
    private static final String NODE_CLIENT = "client";

    /**
     * testing parametrized query by field with supported type
     *
     * @throws Exception
     * 		if any error occurs
     */
    @Test
    public void testSupportedTypes() throws Exception {
        IgniteCache<String, IgniteSqlParameterizedQueryTest.Bookmark> cache = grid(IgniteSqlParameterizedQueryTest.NODE_CLIENT).cache(IgniteSqlParameterizedQueryTest.CACHE_BOOKMARK);
        IgniteSqlParameterizedQueryTest.Bookmark bookmark = new IgniteSqlParameterizedQueryTest.Bookmark();
        bookmark.setId(UUID.randomUUID().toString());
        bookmark.setStockCount(Integer.MAX_VALUE);
        bookmark.setUrlPort(Short.MAX_VALUE);
        bookmark.setUserId(Long.MAX_VALUE);
        bookmark.setVisitRatio(Float.MAX_VALUE);
        bookmark.setTaxPercentage(Double.MAX_VALUE);
        bookmark.setFavourite(true);
        bookmark.setDisplayMask(Byte.MAX_VALUE);
        bookmark.setSerialNumber(UUID.randomUUID());
        bookmark.setVisitCount(new BigInteger("1000000000000000"));
        bookmark.setSiteWeight(new BigDecimal("1000000000000000.001"));
        bookmark.setCreated(new Date());
        cache.put(bookmark.id, bookmark);
        assertEquals(bookmark.getId(), columnValue("id", bookmark.getId()));
        assertEquals(bookmark.getStockCount(), columnValue("stockcount", bookmark.getStockCount()));
        assertEquals(bookmark.getUrlPort(), columnValue("urlport", bookmark.getUrlPort()));
        assertEquals(bookmark.getUserId(), columnValue("userid", bookmark.getUserId()));
        assertEquals(bookmark.getVisitRatio(), columnValue("visitratio", bookmark.getVisitRatio()));
        assertEquals(bookmark.getTaxPercentage(), columnValue("taxpercentage", bookmark.getTaxPercentage()));
        assertEquals(bookmark.getFavourite(), columnValue("favourite", bookmark.getFavourite()));
        assertEquals(bookmark.getDisplayMask(), columnValue("displaymask", bookmark.getDisplayMask()));
        assertEquals(bookmark.getSerialNumber(), columnValue("serialnumber", bookmark.getSerialNumber()));
        assertEquals(bookmark.getVisitCount(), columnValue("visitcount", bookmark.getVisitCount()));
        assertEquals(bookmark.getSiteWeight(), columnValue("siteweight", bookmark.getSiteWeight()));
        assertEquals(bookmark.getCreated(), columnValue("created", bookmark.getCreated()));
    }

    /**
     * Object with all predefined SQL Data Types
     *
     * @see <a href="https://apacheignite.readme.io/docs/dml#section-advanced-configuration">SQL Data Types</a>
     */
    private static class Bookmark implements Serializable {
        /**
         *
         */
        @QuerySqlField
        private String id;

        /**
         *
         */
        @QuerySqlField
        private Integer stockCount;

        /**
         *
         */
        @QuerySqlField
        private Short urlPort;

        /**
         *
         */
        @QuerySqlField
        private Long userId;

        /**
         *
         */
        @QuerySqlField
        private Float visitRatio;

        /**
         *
         */
        @QuerySqlField
        private Double taxPercentage;

        /**
         *
         */
        @QuerySqlField
        private Boolean favourite;

        /**
         *
         */
        @QuerySqlField
        private Byte displayMask;

        /**
         *
         */
        @QuerySqlField
        private UUID serialNumber;

        /**
         *
         */
        @QuerySqlField
        private BigDecimal siteWeight;

        /**
         *
         */
        @QuerySqlField
        private BigInteger visitCount;

        /**
         *
         */
        @QuerySqlField
        private Date created;

        /**
         *
         */
        public String getId() {
            return id;
        }

        /**
         *
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         *
         */
        public Integer getStockCount() {
            return stockCount;
        }

        /**
         *
         */
        public void setStockCount(Integer stockCount) {
            this.stockCount = stockCount;
        }

        /**
         *
         */
        public Short getUrlPort() {
            return urlPort;
        }

        /**
         *
         */
        public void setUrlPort(Short urlPort) {
            this.urlPort = urlPort;
        }

        /**
         *
         */
        public Long getUserId() {
            return userId;
        }

        /**
         *
         */
        public void setUserId(Long userId) {
            this.userId = userId;
        }

        /**
         *
         */
        public Float getVisitRatio() {
            return visitRatio;
        }

        /**
         *
         */
        public void setVisitRatio(Float visitRatio) {
            this.visitRatio = visitRatio;
        }

        /**
         *
         */
        public Double getTaxPercentage() {
            return taxPercentage;
        }

        /**
         *
         */
        public void setTaxPercentage(Double taxPercentage) {
            this.taxPercentage = taxPercentage;
        }

        /**
         *
         */
        public Boolean getFavourite() {
            return favourite;
        }

        /**
         *
         */
        public void setFavourite(Boolean favourite) {
            this.favourite = favourite;
        }

        /**
         *
         */
        public Byte getDisplayMask() {
            return displayMask;
        }

        /**
         *
         */
        public void setDisplayMask(Byte displayMask) {
            this.displayMask = displayMask;
        }

        /**
         *
         */
        public UUID getSerialNumber() {
            return serialNumber;
        }

        /**
         *
         */
        public void setSerialNumber(UUID serialNumber) {
            this.serialNumber = serialNumber;
        }

        /**
         *
         */
        public BigDecimal getSiteWeight() {
            return siteWeight;
        }

        /**
         *
         */
        public void setSiteWeight(BigDecimal siteWeight) {
            this.siteWeight = siteWeight;
        }

        /**
         *
         */
        public BigInteger getVisitCount() {
            return visitCount;
        }

        /**
         *
         */
        public void setVisitCount(BigInteger visitCount) {
            this.visitCount = visitCount;
        }

        /**
         *
         */
        public Date getCreated() {
            return created;
        }

        /**
         *
         */
        public void setCreated(Date created) {
            this.created = created;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            IgniteSqlParameterizedQueryTest.Bookmark bookmark = ((IgniteSqlParameterizedQueryTest.Bookmark) (o));
            return Objects.equals(id, bookmark.id);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}

