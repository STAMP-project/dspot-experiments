/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.jdbc;


import ColumnMetaData.Rep;
import java.util.Iterator;
import org.apache.calcite.avatica.ColumnMetaData;
import org.apache.drill.categories.JdbcTest;
import org.apache.drill.jdbc.impl.DrillColumnMetaDataList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(JdbcTest.class)
public class DrillColumnMetaDataListTest {
    private DrillColumnMetaDataList emptyList;

    private DrillColumnMetaDataList oneElementList;

    private DrillColumnMetaDataList twoElementList;

    private ColumnMetaData exampleIntColumn = new ColumnMetaData(0, false, false, false, false, 0, true, 10, "intLabel", "intColName", "schemaName", 0, 1, ",myTable", "myCategory", new ColumnMetaData.ScalarType(1, "myIntType", Rep.INTEGER), true, false, false, Integer.class.getName());

    private ColumnMetaData exampleStringColumn = new ColumnMetaData(0, false, false, false, false, 0, true, 10, "stringLabel", "stringColName", "schemaName", 0, 1, ",myTable", "myCategory", new ColumnMetaData.ScalarType(1, "myStringType", Rep.STRING), false, true, true, String.class.getName());

    @Test
    public void testSize() throws Exception {
        Assert.assertEquals("Default constructor should give empty list", 0, emptyList.size());
        Assert.assertEquals(1, oneElementList.size());
        Assert.assertEquals(2, twoElementList.size());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetFromEmptyList() throws Exception {
        emptyList.get(0);
    }

    @Test
    public void testGetFromNonEmptyList() throws Exception {
        Assert.assertEquals(oneElementList.get(0).columnName, "/path/to/testInt");
        Assert.assertEquals(twoElementList.get(0).columnName, "/path/to/testInt");
        Assert.assertEquals(twoElementList.get(1).columnName, "/path/to/testString");
    }

    @Test
    public void testIsEmpty() throws Exception {
        Assert.assertTrue("Default constructor should give empty list", emptyList.isEmpty());
        Assert.assertFalse("One-element List should not be empty", oneElementList.isEmpty());
        Assert.assertFalse("Two-element List should not be empty", twoElementList.isEmpty());
    }

    @Test
    public void testContains() throws Exception {
        Assert.assertFalse(emptyList.contains(exampleIntColumn));
        Assert.assertFalse(emptyList.contains(exampleStringColumn));
        Assert.assertTrue(oneElementList.contains(oneElementList.get(0)));
        Assert.assertFalse(oneElementList.contains(exampleStringColumn));
        Assert.assertTrue(twoElementList.contains(twoElementList.get(0)));
        Assert.assertTrue(twoElementList.contains(twoElementList.get(1)));
        Assert.assertFalse(twoElementList.contains(exampleStringColumn));
    }

    @Test
    public void testIterator() throws Exception {
        Assert.assertFalse(emptyList.iterator().hasNext());
        Iterator<ColumnMetaData> iterator1 = oneElementList.iterator();
        Assert.assertNotNull(iterator1);
        Assert.assertTrue(iterator1.hasNext());
        Assert.assertEquals(iterator1.next(), oneElementList.get(0));
        Assert.assertFalse(iterator1.hasNext());
        Iterator<ColumnMetaData> iterator2 = twoElementList.iterator();
        Assert.assertNotNull(iterator2);
        Assert.assertTrue(iterator2.hasNext());
        Assert.assertEquals(iterator2.next(), twoElementList.get(0));
        Assert.assertTrue(iterator2.hasNext());
        Assert.assertEquals(iterator2.next(), twoElementList.get(1));
        Assert.assertFalse(iterator2.hasNext());
    }

    @Test
    public void testToArray() throws Exception {
        Assert.assertEquals(0, emptyList.toArray().length);
        Assert.assertEquals(1, oneElementList.toArray().length);
        Assert.assertEquals(2, twoElementList.toArray().length);
    }

    @Test
    public void testToArrayWithParam() throws Exception {
        ColumnMetaData[] colArray0 = emptyList.toArray(new ColumnMetaData[]{  });
        Assert.assertEquals(0, colArray0.length);
        ColumnMetaData[] colArray1 = oneElementList.toArray(new ColumnMetaData[]{  });
        Assert.assertEquals(1, colArray1.length);
        ColumnMetaData[] colArray2 = twoElementList.toArray(new ColumnMetaData[]{  });
        Assert.assertEquals(2, colArray2.length);
    }

    @Test
    public void testIndexOf() throws Exception {
        Assert.assertEquals((-1), emptyList.indexOf(exampleIntColumn));
        Assert.assertEquals((-1), oneElementList.indexOf(exampleIntColumn));
        Assert.assertEquals((-1), twoElementList.indexOf(exampleIntColumn));
        Assert.assertEquals(0, oneElementList.indexOf(oneElementList.get(0)));
        Assert.assertEquals(0, twoElementList.indexOf(twoElementList.get(0)));
        Assert.assertEquals(1, twoElementList.indexOf(twoElementList.get(1)));
    }

    @Test
    public void testLastIndexOf() throws Exception {
        Assert.assertEquals((-1), emptyList.lastIndexOf(exampleIntColumn));
        Assert.assertEquals((-1), oneElementList.lastIndexOf(exampleIntColumn));
        Assert.assertEquals((-1), twoElementList.lastIndexOf(exampleIntColumn));
        Assert.assertEquals(0, oneElementList.lastIndexOf(oneElementList.get(0)));
        Assert.assertEquals(0, twoElementList.lastIndexOf(twoElementList.get(0)));
        Assert.assertEquals(1, twoElementList.lastIndexOf(twoElementList.get(1)));
    }
}

