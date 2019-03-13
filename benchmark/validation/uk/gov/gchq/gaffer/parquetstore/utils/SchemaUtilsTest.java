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
package uk.gov.gchq.gaffer.parquetstore.utils;


import ParquetStore.DESTINATION;
import ParquetStore.DIRECTED;
import ParquetStore.SOURCE;
import TestGroups.EDGE;
import TestGroups.EDGE_2;
import TestGroups.ENTITY;
import TestGroups.ENTITY_2;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class SchemaUtilsTest {
    private SchemaUtils utils;

    @Test
    public void getColumnToSerialiserTest() {
        final Map<String, String> columnToSerialiser = utils.getColumnToSerialiser(EDGE);
        Assert.assertEquals("uk.gov.gchq.gaffer.parquetstore.serialisation.impl.StringParquetSerialiser", columnToSerialiser.get(SOURCE));
        Assert.assertEquals("uk.gov.gchq.gaffer.parquetstore.serialisation.impl.StringParquetSerialiser", columnToSerialiser.get(DESTINATION));
        Assert.assertEquals("uk.gov.gchq.gaffer.parquetstore.serialisation.impl.BooleanParquetSerialiser", columnToSerialiser.get(DIRECTED));
        Assert.assertEquals("uk.gov.gchq.gaffer.parquetstore.serialisation.impl.ByteParquetSerialiser", columnToSerialiser.get("byte"));
        Assert.assertEquals("uk.gov.gchq.gaffer.parquetstore.serialisation.impl.DoubleParquetSerialiser", columnToSerialiser.get("double"));
        Assert.assertEquals("uk.gov.gchq.gaffer.parquetstore.serialisation.impl.FloatParquetSerialiser", columnToSerialiser.get("float"));
        Assert.assertEquals("uk.gov.gchq.gaffer.parquetstore.serialisation.impl.TreeSetStringParquetSerialiser", columnToSerialiser.get("treeSet"));
        Assert.assertEquals("uk.gov.gchq.gaffer.parquetstore.serialisation.impl.LongParquetSerialiser", columnToSerialiser.get("long"));
        Assert.assertEquals("uk.gov.gchq.gaffer.parquetstore.serialisation.impl.ShortParquetSerialiser", columnToSerialiser.get("short"));
        Assert.assertEquals("uk.gov.gchq.gaffer.parquetstore.serialisation.impl.DateParquetSerialiser", columnToSerialiser.get("date"));
        Assert.assertEquals("uk.gov.gchq.gaffer.parquetstore.serialisation.impl.FreqMapParquetSerialiser", columnToSerialiser.get("freqMap"));
        Assert.assertEquals("uk.gov.gchq.gaffer.parquetstore.serialisation.impl.IntegerParquetSerialiser", columnToSerialiser.get("count"));
    }

    @Test
    public void getEntityGroupsTest() {
        final Set<String> entityGroups = utils.getEntityGroups();
        final LinkedHashSet<String> expected = new LinkedHashSet<>(2);
        expected.add(ENTITY);
        expected.add(ENTITY_2);
        Assert.assertEquals(expected, entityGroups);
    }

    @Test
    public void getEdgeGroupsTest() {
        final Set<String> edgeGroups = utils.getEdgeGroups();
        final LinkedHashSet<String> expected = new LinkedHashSet<>(2);
        expected.add(EDGE);
        expected.add(EDGE_2);
        Assert.assertEquals(expected, edgeGroups);
    }
}

