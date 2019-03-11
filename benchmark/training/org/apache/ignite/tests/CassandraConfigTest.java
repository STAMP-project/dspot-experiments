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
package org.apache.ignite.tests;


import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.cache.store.cassandra.persistence.KeyPersistenceSettings;
import org.junit.Assert;
import org.junit.Test;


/**
 * Simple test for DDL generator.
 */
public class CassandraConfigTest {
    /**
     * Check if same DDL generated for similar keys and same KeyPersistenceConfiguration.
     */
    @Test
    public void testDDLGeneration() {
        KeyPersistenceSettings keyPersistenceSettingsA = getKeyPersistenceSettings(CassandraConfigTest.KeyA.class);
        KeyPersistenceSettings keyPersistenceSettingsB = getKeyPersistenceSettings(CassandraConfigTest.KeyB.class);
        Assert.assertEquals(keyPersistenceSettingsB.getPrimaryKeyDDL(), keyPersistenceSettingsA.getPrimaryKeyDDL());
        Assert.assertEquals(keyPersistenceSettingsB.getClusteringDDL(), keyPersistenceSettingsA.getClusteringDDL());
    }

    /**
     *
     */
    public static class BaseKey {
        /**
         *
         */
        // Looks like next annotation is ignored when generating DDL,
        // but Ignite supports this annotation in parent classes.
        // @AffinityKeyMapped
        @QuerySqlField
        private Integer contextId;

        /**
         *
         */
        public Integer getContextId() {
            return contextId;
        }

        /**
         *
         */
        public void setContextId(Integer contextId) {
            this.contextId = contextId;
        }
    }

    /**
     *
     */
    public static class KeyA extends CassandraConfigTest.BaseKey {
        /**
         *
         */
        @QuerySqlField(index = true)
        private String timestamp;

        /**
         *
         */
        @QuerySqlField(index = true)
        private String name;

        /**
         *
         */
        @QuerySqlField
        private String creationDate;

        /**
         * Constructor.
         */
        public KeyA() {
        }
    }

    /**
     *
     */
    public static class KeyB {
        /**
         *
         */
        @QuerySqlField(index = true)
        private String timestamp;

        /**
         *
         */
        @QuerySqlField(index = true)
        private String name;

        /**
         *
         */
        @QuerySqlField
        private String creationDate;

        /**
         *
         */
        // @AffinityKeyMapped
        @QuerySqlField
        private Integer contextId;

        /**
         * Constructor.
         */
        public KeyB() {
        }
    }
}

