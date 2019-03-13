/**
 * Copyright 2017. Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.accumulostore;


import com.fasterxml.jackson.databind.Module;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiserModules;
import uk.gov.gchq.gaffer.sketches.serialisation.json.SketchesJsonModules;


public class AccumuloPropertiesTest {
    @Test
    public void shouldMergeAccumuloJsonModules() {
        // Given
        final AccumuloProperties props = new AccumuloProperties();
        props.setJsonSerialiserModules((((AccumuloPropertiesTest.TestCustomJsonModules1.class.getName()) + ",") + (AccumuloPropertiesTest.TestCustomJsonModules2.class.getName())));
        // When
        final String modules = props.getJsonSerialiserModules();
        // Then
        Assert.assertEquals((((((SketchesJsonModules.class.getName()) + ",") + (AccumuloPropertiesTest.TestCustomJsonModules1.class.getName())) + ",") + (AccumuloPropertiesTest.TestCustomJsonModules2.class.getName())), modules);
    }

    @Test
    public void shouldMergeAccumuloJsonModulesAndDeduplicate() {
        // Given
        final AccumuloProperties props = new AccumuloProperties();
        props.setJsonSerialiserModules((((AccumuloPropertiesTest.TestCustomJsonModules1.class.getName()) + ",") + (SketchesJsonModules.class.getName())));
        // When
        final String modules = props.getJsonSerialiserModules();
        // Then
        Assert.assertEquals((((SketchesJsonModules.class.getName()) + ",") + (AccumuloPropertiesTest.TestCustomJsonModules1.class.getName())), modules);
    }

    @Test
    public void shouldSetProperties() {
        // Given
        final AccumuloProperties props = new AccumuloProperties();
        final String NUM_THREADS_WRITER = "5";
        final String MAX_TIME_OUT = "500";
        final String MAX_BUFFER = "200000000";
        final String ZOOKEEPERS = "accumulo.zookeepers";
        final String INSTANCE = "accumulo.instance";
        final String NUM_THREADS_SCANNER = "8";
        final String CLIENT_SIDE_BLOOM = "786432000";
        final String FALSE_POSITIVE_RATE = "0.0003";
        final String MAX_BLOOM_FILTER = "7864320";
        final String KEY_PACKAGE_CLASS = "gaffer.store.accumulo.keypackage.class";
        final String REPLICATION_FACTOR = "accumulo.file.replication";
        // When
        props.setNumThreadsForBatchWriter(NUM_THREADS_WRITER);
        props.setMaxTimeOutForBatchWriterInMilliseconds(MAX_TIME_OUT);
        props.setMaxBufferSizeForBatchWriterInBytes(MAX_BUFFER);
        props.setZookeepers(ZOOKEEPERS);
        props.setInstance(INSTANCE);
        props.setThreadsForBatchScanner(NUM_THREADS_SCANNER);
        props.setClientSideBloomFilterSize(CLIENT_SIDE_BLOOM);
        props.setFalsePositiveRate(FALSE_POSITIVE_RATE);
        props.setMaxBloomFilterToPassToAnIterator(MAX_BLOOM_FILTER);
        props.setKeyPackageClass(KEY_PACKAGE_CLASS);
        props.setTableFileReplicationFactor(REPLICATION_FACTOR);
        props.setEnableValidatorIterator(true);
        // Then
        Assert.assertEquals(Integer.parseInt(NUM_THREADS_WRITER), props.getNumThreadsForBatchWriter());
        Assert.assertEquals(Long.parseLong(MAX_TIME_OUT), props.getMaxTimeOutForBatchWriterInMilliseconds().longValue());
        Assert.assertEquals(Long.parseLong(MAX_BUFFER), props.getMaxBufferSizeForBatchWriterInBytes().longValue());
        Assert.assertEquals(ZOOKEEPERS, props.getZookeepers());
        Assert.assertEquals(INSTANCE, props.getInstance());
        Assert.assertEquals(Integer.parseInt(NUM_THREADS_SCANNER), props.getThreadsForBatchScanner());
        Assert.assertEquals(Integer.parseInt(CLIENT_SIDE_BLOOM), props.getClientSideBloomFilterSize());
        Assert.assertEquals(Double.parseDouble(FALSE_POSITIVE_RATE), props.getFalsePositiveRate(), 1.0E-4);
        Assert.assertEquals(Integer.parseInt(MAX_BLOOM_FILTER), props.getMaxBloomFilterToPassToAnIterator());
        Assert.assertEquals(KEY_PACKAGE_CLASS, props.getKeyPackageClass());
        Assert.assertEquals(REPLICATION_FACTOR, props.getTableFileReplicationFactor());
        Assert.assertTrue(props.getEnableValidatorIterator());
    }

    public static final class TestCustomJsonModules1 implements JSONSerialiserModules {
        public static List<Module> modules;

        @Override
        public List<Module> getModules() {
            return AccumuloPropertiesTest.TestCustomJsonModules1.modules;
        }
    }

    public static final class TestCustomJsonModules2 implements JSONSerialiserModules {
        public static List<Module> modules;

        @Override
        public List<Module> getModules() {
            return AccumuloPropertiesTest.TestCustomJsonModules2.modules;
        }
    }
}

