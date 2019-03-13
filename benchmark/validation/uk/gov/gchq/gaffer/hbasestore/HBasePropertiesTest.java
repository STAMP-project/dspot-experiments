/**
 * Copyright 2016-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.hbasestore;


import com.fasterxml.jackson.databind.Module;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiserModules;
import uk.gov.gchq.gaffer.sketches.serialisation.json.SketchesJsonModules;
import uk.gov.gchq.gaffer.store.StoreException;


public class HBasePropertiesTest {
    @Test
    public void shouldGetAndSetProperties() throws IOException, StoreException {
        // Given
        final HBaseProperties properties = new HBaseProperties();
        // When
        properties.setDependencyJarsHdfsDirPath("pathTo/jars");
        properties.setWriteBufferSize(10);
        properties.setZookeepers("zookeeper1,zookeeper2");
        // Then
        Assert.assertEquals(new Path("pathTo/jars"), properties.getDependencyJarsHdfsDirPath());
        Assert.assertEquals(10, properties.getWriteBufferSize());
        Assert.assertEquals("zookeeper1,zookeeper2", properties.getZookeepers());
    }

    @Test
    public void shouldMergeHBaseJsonModules() {
        // Given
        final HBaseProperties props = new HBaseProperties();
        props.setJsonSerialiserModules((((HBasePropertiesTest.TestCustomJsonModules1.class.getName()) + ",") + (HBasePropertiesTest.TestCustomJsonModules2.class.getName())));
        // When
        final String modules = props.getJsonSerialiserModules();
        // Then
        Assert.assertEquals((((((SketchesJsonModules.class.getName()) + ",") + (HBasePropertiesTest.TestCustomJsonModules1.class.getName())) + ",") + (HBasePropertiesTest.TestCustomJsonModules2.class.getName())), modules);
    }

    @Test
    public void shouldMergeHBaseJsonModulesAndDeduplicate() {
        // Given
        final HBaseProperties props = new HBaseProperties();
        props.setJsonSerialiserModules((((HBasePropertiesTest.TestCustomJsonModules1.class.getName()) + ",") + (SketchesJsonModules.class.getName())));
        // When
        final String modules = props.getJsonSerialiserModules();
        // Then
        Assert.assertEquals((((SketchesJsonModules.class.getName()) + ",") + (HBasePropertiesTest.TestCustomJsonModules1.class.getName())), modules);
    }

    public static final class TestCustomJsonModules1 implements JSONSerialiserModules {
        public static List<Module> modules;

        @Override
        public List<Module> getModules() {
            return HBasePropertiesTest.TestCustomJsonModules1.modules;
        }
    }

    public static final class TestCustomJsonModules2 implements JSONSerialiserModules {
        public static List<Module> modules;

        @Override
        public List<Module> getModules() {
            return HBasePropertiesTest.TestCustomJsonModules2.modules;
        }
    }
}

