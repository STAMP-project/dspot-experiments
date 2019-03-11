/**
 * Copyright 2017-2018. Crown Copyright
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
package uk.gov.gchq.gaffer.parquetstore;


import CompressionCodecName.GZIP;
import CompressionCodecName.LZO;
import CompressionCodecName.SNAPPY;
import CompressionCodecName.UNCOMPRESSED;
import com.fasterxml.jackson.databind.Module;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.gov.gchq.gaffer.commonutil.CommonTestConstants;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiserModules;
import uk.gov.gchq.gaffer.sketches.serialisation.json.SketchesJsonModules;


public class ParquetStorePropertiesTest {
    @Rule
    public final TemporaryFolder testFolder = new TemporaryFolder(CommonTestConstants.TMP_DIRECTORY);

    private ParquetStoreProperties props;

    @Test
    public void threadsAvailableTest() {
        Assert.assertEquals(((Integer) (3)), props.getThreadsAvailable());
        props.setThreadsAvailable(9);
        Assert.assertEquals(((Integer) (9)), props.getThreadsAvailable());
    }

    @Test
    public void dataDirTest() {
        Assert.assertEquals(null, props.getDataDir());
        props.setDataDir("Test");
        Assert.assertEquals("Test", props.getDataDir());
    }

    @Test
    public void tempFilesDirTest() {
        Assert.assertEquals(null, props.getTempFilesDir());
        props.setTempFilesDir("Test");
        Assert.assertEquals("Test", props.getTempFilesDir());
    }

    @Test
    public void rowGroupSizeTest() {
        Assert.assertEquals(((Integer) (4194304)), props.getRowGroupSize());
        props.setRowGroupSize(100000);
        Assert.assertEquals(((Integer) (100000)), props.getRowGroupSize());
    }

    @Test
    public void pageSizeTest() {
        Assert.assertEquals(((Integer) (1048576)), props.getPageSize());
        props.setPageSize(100000);
        Assert.assertEquals(((Integer) (100000)), props.getPageSize());
    }

    @Test
    public void sampleRateTest() {
        Assert.assertEquals(((Integer) (10)), props.getSampleRate());
        props.setSampleRate(100000);
        Assert.assertEquals(((Integer) (100000)), props.getSampleRate());
    }

    @Test
    public void addElementsOutputFilesPerGroupTest() {
        Assert.assertEquals(10, props.getAddElementsOutputFilesPerGroup());
        props.setAddElementsOutputFilesPerGroup(10000);
        Assert.assertEquals(10000, props.getAddElementsOutputFilesPerGroup());
    }

    @Test
    public void aggregateTest() {
        Assert.assertEquals(true, props.getAggregateOnIngest());
        props.setAggregateOnIngest(false);
        Assert.assertEquals(false, props.getAggregateOnIngest());
    }

    @Test
    public void sortBySplitsTest() {
        Assert.assertEquals(false, props.getSortBySplitsOnIngest());
        props.setSortBySplitsOnIngest(true);
        Assert.assertEquals(true, props.getSortBySplitsOnIngest());
    }

    @Test
    public void sparkMasterTest() {
        // might fail if Spark is properly installed
        Assert.assertEquals("local[*]", props.getSparkMaster());
        props.setSparkMaster("Test");
        Assert.assertEquals("Test", props.getSparkMaster());
    }

    @Test
    public void compressionTest() {
        Assert.assertEquals(GZIP, props.getCompressionCodecName());
        props.setCompressionCodecName(SNAPPY.name());
        Assert.assertEquals(SNAPPY, props.getCompressionCodecName());
        props.setCompressionCodecName(LZO.name());
        Assert.assertEquals(LZO, props.getCompressionCodecName());
        props.setCompressionCodecName(UNCOMPRESSED.name());
        Assert.assertEquals(UNCOMPRESSED, props.getCompressionCodecName());
    }

    @Test
    public void shouldMergeParquetJsonModules() {
        // Given
        props.setJsonSerialiserModules((((ParquetStorePropertiesTest.TestCustomJsonModules1.class.getName()) + ",") + (ParquetStorePropertiesTest.TestCustomJsonModules2.class.getName())));
        // When
        final String modules = props.getJsonSerialiserModules();
        // Then
        Assert.assertEquals((((((SketchesJsonModules.class.getName()) + ",") + (ParquetStorePropertiesTest.TestCustomJsonModules1.class.getName())) + ",") + (ParquetStorePropertiesTest.TestCustomJsonModules2.class.getName())), modules);
    }

    @Test
    public void shouldMergeParquetJsonModulesAndDeduplicate() {
        // Given
        props.setJsonSerialiserModules((((ParquetStorePropertiesTest.TestCustomJsonModules1.class.getName()) + ",") + (SketchesJsonModules.class.getName())));
        // When
        final String modules = props.getJsonSerialiserModules();
        // Then
        Assert.assertEquals((((SketchesJsonModules.class.getName()) + ",") + (ParquetStorePropertiesTest.TestCustomJsonModules1.class.getName())), modules);
    }

    public static final class TestCustomJsonModules1 implements JSONSerialiserModules {
        public static List<Module> modules;

        @Override
        public List<Module> getModules() {
            return ParquetStorePropertiesTest.TestCustomJsonModules1.modules;
        }
    }

    public static final class TestCustomJsonModules2 implements JSONSerialiserModules {
        public static List<Module> modules;

        @Override
        public List<Module> getModules() {
            return ParquetStorePropertiesTest.TestCustomJsonModules2.modules;
        }
    }
}

