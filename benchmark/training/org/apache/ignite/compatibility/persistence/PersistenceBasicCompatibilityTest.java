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
package org.apache.ignite.compatibility.persistence;


import CacheAtomicityMode.TRANSACTIONAL;
import CacheWriteSynchronizationMode.FULL_SYNC;
import GridCacheAbstractFullApiSelfTest.LOCAL_IP_FINDER;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.configuration.BinaryConfiguration;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.configuration.PersistentStoreConfiguration;
import org.apache.ignite.lang.IgniteInClosure;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.junit.Test;


/**
 * Saves data using previous version of ignite and then load this data using actual version.
 */
public class PersistenceBasicCompatibilityTest extends IgnitePersistenceCompatibilityAbstractTest {
    /**
     *
     */
    protected static final String TEST_CACHE_NAME = PersistenceBasicCompatibilityTest.class.getSimpleName();

    /**
     *
     */
    protected volatile boolean compactFooter;

    /**
     * Tests opportunity to read data from previous Ignite DB version.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNodeStartByOldVersionPersistenceData_2_2() throws Exception {
        doTestStartupWithOldVersion("2.2.0");
    }

    /**
     * Tests opportunity to read data from previous Ignite DB version.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNodeStartByOldVersionPersistenceData_2_1() throws Exception {
        doTestStartupWithOldVersion("2.1.0");
    }

    /**
     * Tests opportunity to read data from previous Ignite DB version.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNodeStartByOldVersionPersistenceData_2_3() throws Exception {
        doTestStartupWithOldVersion("2.3.0");
    }

    /**
     * Tests opportunity to read data from previous Ignite DB version.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNodeStartByOldVersionPersistenceData_2_4() throws Exception {
        doTestStartupWithOldVersion("2.4.0");
    }

    /**
     * Tests opportunity to read data from previous Ignite DB version.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNodeStartByOldVersionPersistenceData_2_5() throws Exception {
        doTestStartupWithOldVersion("2.5.0");
    }

    /**
     * Tests opportunity to read data from previous Ignite DB version.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testNodeStartByOldVersionPersistenceData_2_6() throws Exception {
        doTestStartupWithOldVersion("2.6.0");
    }

    /**
     *
     */
    public static class PostStartupClosure implements IgniteInClosure<Ignite> {
        /**
         * {@inheritDoc }
         */
        @Override
        public void apply(Ignite ignite) {
            ignite.active(true);
            CacheConfiguration<Object, Object> cacheCfg = new CacheConfiguration();
            cacheCfg.setName(PersistenceBasicCompatibilityTest.TEST_CACHE_NAME);
            cacheCfg.setAtomicityMode(TRANSACTIONAL);
            cacheCfg.setBackups(1);
            cacheCfg.setWriteSynchronizationMode(FULL_SYNC);
            IgniteCache<Object, Object> cache = ignite.createCache(cacheCfg);
            PersistenceBasicCompatibilityTest.saveCacheData(cache);
        }
    }

    /**
     *
     */
    public static class ConfigurationClosure implements IgniteInClosure<IgniteConfiguration> {
        /**
         * Compact footer.
         */
        private boolean compactFooter;

        /**
         *
         *
         * @param compactFooter
         * 		Compact footer.
         */
        public ConfigurationClosure(boolean compactFooter) {
            this.compactFooter = compactFooter;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void apply(IgniteConfiguration cfg) {
            cfg.setLocalHost("127.0.0.1");
            TcpDiscoverySpi disco = new TcpDiscoverySpi();
            disco.setIpFinder(LOCAL_IP_FINDER);
            cfg.setDiscoverySpi(disco);
            cfg.setPeerClassLoadingEnabled(false);
            cfg.setPersistentStoreConfiguration(new PersistentStoreConfiguration());
            cfg.setBinaryConfiguration(new BinaryConfiguration().setCompactFooter(compactFooter));
        }
    }

    /**
     * Enum for cover binaryObject enum save/load.
     */
    public enum TestEnum {

        /**
         *
         */
        A,
        /**
         *
         */
        B,
        /**
         *
         */
        C;}

    /**
     * Special class to test WAL reader resistance to Serializable interface.
     */
    static class TestSerializable implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 0L;

        /**
         * I value.
         */
        private int iVal;

        /**
         * Creates test object
         *
         * @param iVal
         * 		I value.
         */
        TestSerializable(int iVal) {
            this.iVal = iVal;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return (("TestSerializable{" + "iVal=") + (iVal)) + '}';
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

            PersistenceBasicCompatibilityTest.TestSerializable that = ((PersistenceBasicCompatibilityTest.TestSerializable) (o));
            return (iVal) == (that.iVal);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return iVal;
        }
    }

    /**
     * Special class to test WAL reader resistance to Serializable interface.
     */
    static class TestExternalizable implements Externalizable {
        /**
         *
         */
        private static final long serialVersionUID = 0L;

        /**
         * I value.
         */
        private int iVal;

        /**
         * Noop ctor for unmarshalling
         */
        public TestExternalizable() {
        }

        /**
         * Creates test object with provided value.
         *
         * @param iVal
         * 		I value.
         */
        public TestExternalizable(int iVal) {
            this.iVal = iVal;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return (("TestExternalizable{" + "iVal=") + (iVal)) + '}';
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(iVal);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            iVal = in.readInt();
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

            PersistenceBasicCompatibilityTest.TestExternalizable that = ((PersistenceBasicCompatibilityTest.TestExternalizable) (o));
            return (iVal) == (that.iVal);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return iVal;
        }
    }

    /**
     * Container class to test toString of data records.
     */
    static class TestStringContainerToBePrinted {
        /**
         *
         */
        String data;

        /**
         * Creates container.
         *
         * @param data
         * 		value to be searched in to String.
         */
        public TestStringContainerToBePrinted(String data) {
            this.data = data;
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

            PersistenceBasicCompatibilityTest.TestStringContainerToBePrinted printed = ((PersistenceBasicCompatibilityTest.TestStringContainerToBePrinted) (o));
            return (data) != null ? data.equals(printed.data) : (printed.data) == null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return (data) != null ? data.hashCode() : 0;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return ((("TestStringContainerToBePrinted{" + "data='") + (data)) + '\'') + '}';
        }
    }
}

