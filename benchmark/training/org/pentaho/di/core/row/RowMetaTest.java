/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.row;


import RowMeta.RowMetaCache;
import ValueMetaInterface.TYPE_STRING;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.core.row.value.ValueMetaDate;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.core.row.value.ValueMetaTimestamp;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;
import org.w3c.dom.Document;


public class RowMetaTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    RowMetaInterface rowMeta = new RowMeta();

    ValueMetaInterface string;

    ValueMetaInterface integer;

    ValueMetaInterface date;

    ValueMetaInterface charly;

    ValueMetaInterface dup;

    ValueMetaInterface bin;

    @Test
    public void testRowMetaInitializingFromXmlNode() throws Exception {
        String testXmlNode = null;
        try (InputStream in = RowMetaTest.class.getResourceAsStream("rowMetaNode.xml")) {
            testXmlNode = IOUtils.toString(in);
        }
        Document xmlDoc = XMLHandler.loadXMLString(testXmlNode);
        RowMeta rowMeta = Mockito.spy(new RowMeta(xmlDoc.getFirstChild()));
        Assert.assertEquals(2, rowMeta.getValueMetaList().size());
        ValueMetaInterface valueMeta = rowMeta.getValueMeta(0);
        Assert.assertTrue((valueMeta instanceof ValueMetaDate));
        Assert.assertEquals("testDate", valueMeta.getName());
        Assert.assertNull(valueMeta.getConversionMask());
        valueMeta = rowMeta.getValueMeta(1);
        Assert.assertTrue((valueMeta instanceof ValueMetaTimestamp));
        Assert.assertEquals("testTimestamp", valueMeta.getName());
        Assert.assertEquals("yyyy/MM/dd HH:mm:ss.000000000", valueMeta.getConversionMask());
    }

    @Test
    public void testGetValueMetaList() {
        List<ValueMetaInterface> list = rowMeta.getValueMetaList();
        Assert.assertTrue(list.contains(string));
        Assert.assertTrue(list.contains(integer));
        Assert.assertTrue(list.contains(date));
    }

    @Test
    public void testSetValueMetaList() throws KettlePluginException {
        List<ValueMetaInterface> setList = this.generateVList(new String[]{ "alpha", "bravo" }, new int[]{ 2, 2 });
        rowMeta.setValueMetaList(setList);
        Assert.assertTrue(setList.contains(rowMeta.searchValueMeta("alpha")));
        Assert.assertTrue(setList.contains(rowMeta.searchValueMeta("bravo")));
        // check that it is avalable by index:
        Assert.assertEquals(0, rowMeta.indexOfValue("alpha"));
        Assert.assertEquals(1, rowMeta.indexOfValue("bravo"));
    }

    @Test
    public void testSetValueMetaListNullName() throws KettlePluginException {
        List<ValueMetaInterface> setList = this.generateVList(new String[]{ "alpha", null }, new int[]{ 2, 2 });
        rowMeta.setValueMetaList(setList);
        Assert.assertTrue(setList.contains(rowMeta.searchValueMeta("alpha")));
        Assert.assertFalse(setList.contains(rowMeta.searchValueMeta(null)));
        // check that it is avalable by index:
        Assert.assertEquals(0, rowMeta.indexOfValue("alpha"));
        Assert.assertEquals((-1), rowMeta.indexOfValue(null));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDeSynchronizationModifyingOriginalList() {
        // remember 0-based arrays
        int size = rowMeta.size();
        // should be added at the end
        rowMeta.getValueMetaList().add(charly);
        Assert.assertEquals(size, rowMeta.indexOfValue("charly"));
    }

    @Test
    public void testExists() {
        Assert.assertTrue(rowMeta.exists(string));
        Assert.assertTrue(rowMeta.exists(date));
        Assert.assertTrue(rowMeta.exists(integer));
    }

    @Test
    public void testAddValueMetaValueMetaInterface() throws KettlePluginException {
        rowMeta.addValueMeta(charly);
        Assert.assertTrue(rowMeta.getValueMetaList().contains(charly));
    }

    @Test
    public void testAddValueMetaNullName() throws KettlePluginException {
        ValueMetaInterface vmi = new ValueMetaBase();
        rowMeta.addValueMeta(vmi);
        Assert.assertTrue(rowMeta.getValueMetaList().contains(vmi));
    }

    @Test
    public void testAddValueMetaIntValueMetaInterface() throws KettlePluginException {
        rowMeta.addValueMeta(1, charly);
        Assert.assertTrue(((rowMeta.getValueMetaList().indexOf(charly)) == 1));
    }

    @Test
    public void testGetValueMeta() {
        // see before method insertion order.
        Assert.assertTrue(rowMeta.getValueMeta(1).equals(integer));
    }

    @Test
    public void testSetValueMeta() throws KettlePluginException {
        rowMeta.setValueMeta(1, charly);
        Assert.assertEquals(1, rowMeta.getValueMetaList().indexOf(charly));
        Assert.assertEquals("There is still 3 elements:", 3, rowMeta.size());
        Assert.assertEquals((-1), rowMeta.indexOfValue("integer"));
    }

    @Test
    public void testSetValueMetaDup() throws KettlePluginException {
        rowMeta.setValueMeta(1, dup);
        Assert.assertEquals("There is still 3 elements:", 3, rowMeta.size());
        Assert.assertEquals((-1), rowMeta.indexOfValue("integer"));
        rowMeta.setValueMeta(1, dup);
        Assert.assertEquals("There is still 3 elements:", 3, rowMeta.size());
        Assert.assertEquals((-1), rowMeta.indexOfValue("integer"));
        rowMeta.setValueMeta(2, dup);
        Assert.assertEquals("There is still 3 elements:", 3, rowMeta.size());
        Assert.assertEquals("Original is still the same (object)", 1, rowMeta.getValueMetaList().indexOf(dup));
        Assert.assertEquals("Original is still the same (name)", 1, rowMeta.indexOfValue("dup"));
        Assert.assertEquals("Renaming happened", 2, rowMeta.indexOfValue("dup_1"));
    }

    @Test
    public void testInsertValueMetaDup() throws Exception {
        rowMeta.addValueMeta(1, new org.pentaho.di.core.row.value.ValueMetaInteger(integer.getName()));
        Assert.assertEquals("inserted", 4, rowMeta.size());
        Assert.assertEquals("rename new", "integer_1", rowMeta.getValueMeta(1).getName());
        rowMeta.addValueMeta(new org.pentaho.di.core.row.value.ValueMetaInteger(integer.getName()));
        Assert.assertEquals("rename after", "integer_2", rowMeta.getValueMeta(4).getName());
    }

    @Test
    public void testRemoveValueMetaDup() throws Exception {
        rowMeta.removeValueMeta(date.getName());
        Assert.assertEquals("removed", 2, rowMeta.size());
        rowMeta.addValueMeta(new org.pentaho.di.core.row.value.ValueMetaInteger(integer.getName()));
        Assert.assertEquals("rename after", "integer_1", rowMeta.getValueMeta(2).getName());
    }

    @Test
    public void testSetValueMetaNullName() throws KettlePluginException {
        ValueMetaInterface vmi = new ValueMetaBase();
        rowMeta.setValueMeta(1, vmi);
        Assert.assertEquals(1, rowMeta.getValueMetaList().indexOf(vmi));
        Assert.assertEquals("There is still 3 elements:", 3, rowMeta.size());
    }

    @Test
    public void testIndexOfValue() {
        List<ValueMetaInterface> list = rowMeta.getValueMetaList();
        Assert.assertEquals(0, list.indexOf(string));
        Assert.assertEquals(1, list.indexOf(integer));
        Assert.assertEquals(2, list.indexOf(date));
    }

    @Test
    public void testIndexOfNullValue() {
        Assert.assertEquals((-1), rowMeta.indexOfValue(null));
    }

    @Test
    public void testSearchValueMeta() {
        ValueMetaInterface vmi = rowMeta.searchValueMeta("integer");
        Assert.assertEquals(integer, vmi);
        vmi = rowMeta.searchValueMeta("string");
        Assert.assertEquals(string, vmi);
        vmi = rowMeta.searchValueMeta("date");
        Assert.assertEquals(date, vmi);
    }

    @Test
    public void testAddRowMeta() throws KettlePluginException {
        List<ValueMetaInterface> list = this.generateVList(new String[]{ "alfa", "bravo", "charly", "delta" }, new int[]{ 2, 2, 3, 4 });
        RowMeta added = new RowMeta();
        added.setValueMetaList(list);
        rowMeta.addRowMeta(added);
        Assert.assertEquals(7, rowMeta.getValueMetaList().size());
        Assert.assertEquals(5, rowMeta.indexOfValue("charly"));
    }

    @Test
    public void testMergeRowMeta() throws KettlePluginException {
        List<ValueMetaInterface> list = this.generateVList(new String[]{ "phobos", "demos", "mars" }, new int[]{ 6, 6, 6 });
        list.add(1, integer);
        RowMeta toMerge = new RowMeta();
        toMerge.setValueMetaList(list);
        rowMeta.mergeRowMeta(toMerge);
        Assert.assertEquals(7, rowMeta.size());
        list = rowMeta.getValueMetaList();
        Assert.assertTrue(list.contains(integer));
        ValueMetaInterface found = null;
        for (ValueMetaInterface vm : list) {
            if (vm.getName().equals("integer_1")) {
                found = vm;
                break;
            }
        }
        Assert.assertNotNull(found);
    }

    @Test
    public void testRemoveValueMetaString() throws KettleValueException {
        rowMeta.removeValueMeta("string");
        Assert.assertEquals(2, rowMeta.size());
        Assert.assertNotNull(rowMeta.searchValueMeta("integer"));
        Assert.assertTrue(rowMeta.searchValueMeta("integer").getName().equals("integer"));
        Assert.assertNull(rowMeta.searchValueMeta("string"));
    }

    @Test
    public void testRemoveValueMetaInt() {
        rowMeta.removeValueMeta(1);
        Assert.assertEquals(2, rowMeta.size());
        Assert.assertNotNull(rowMeta.searchValueMeta("date"));
        Assert.assertNotNull(rowMeta.searchValueMeta("string"));
        Assert.assertNull(rowMeta.searchValueMeta("notExists"));
        Assert.assertTrue(rowMeta.searchValueMeta("date").getName().equals("date"));
        Assert.assertNull(rowMeta.searchValueMeta("integer"));
    }

    @Test
    public void testLowerCaseNamesSearch() {
        Assert.assertNotNull(rowMeta.searchValueMeta("Integer"));
        Assert.assertNotNull(rowMeta.searchValueMeta("string".toUpperCase()));
    }

    @Test
    public void testMultipleSameNameInserts() {
        for (int i = 0; i < 13; i++) {
            rowMeta.addValueMeta(integer);
        }
        String resultName = "integer_13";
        Assert.assertTrue(rowMeta.searchValueMeta(resultName).getName().equals(resultName));
    }

    @Test
    public void testExternalValueMetaModification() {
        ValueMetaInterface vmi = rowMeta.searchValueMeta("string");
        vmi.setName("string2");
        Assert.assertNotNull(rowMeta.searchValueMeta(vmi.getName()));
    }

    @Test
    public void testSwapNames() throws KettlePluginException {
        ValueMetaInterface string2 = ValueMetaFactory.createValueMeta("string2", TYPE_STRING);
        rowMeta.addValueMeta(string2);
        Assert.assertSame(string, rowMeta.searchValueMeta("string"));
        Assert.assertSame(string2, rowMeta.searchValueMeta("string2"));
        string.setName("string2");
        string2.setName("string");
        Assert.assertSame(string2, rowMeta.searchValueMeta("string"));
        Assert.assertSame(string, rowMeta.searchValueMeta("string2"));
    }

    @Test
    public void testCopyRowMetaCacheConstructor() {
        Map<String, Integer> mapping = new HashMap<>();
        mapping.put("a", 1);
        RowMeta.RowMetaCache rowMetaCache = new RowMeta.RowMetaCache(mapping);
        RowMeta.RowMetaCache rowMetaCache2 = new RowMeta.RowMetaCache(rowMetaCache);
        Assert.assertEquals(rowMetaCache.mapping, rowMetaCache2.mapping);
        rowMetaCache = new RowMeta.RowMetaCache(mapping);
        rowMetaCache2 = new RowMeta.RowMetaCache(rowMetaCache);
        Assert.assertEquals(rowMetaCache.mapping, rowMetaCache2.mapping);
    }

    @Test
    public void testNeedRealClone() {
        RowMeta newRowMeta = new RowMeta();
        newRowMeta.addValueMeta(string);
        newRowMeta.addValueMeta(integer);
        newRowMeta.addValueMeta(date);
        newRowMeta.addValueMeta(charly);
        newRowMeta.addValueMeta(dup);
        newRowMeta.addValueMeta(bin);
        List<Integer> list = newRowMeta.getOrCreateValuesThatNeedRealClone(newRowMeta.valueMetaList);
        Assert.assertEquals(3, list.size());// Should be charly, dup and bin

        Assert.assertTrue(list.contains(3));// charly

        Assert.assertTrue(list.contains(4));// dup

        Assert.assertTrue(list.contains(5));// bin

        newRowMeta.addValueMeta(charly);// should have nulled the newRowMeta.needRealClone

        Assert.assertNull(newRowMeta.needRealClone);// null because of the new add

        list = newRowMeta.getOrCreateValuesThatNeedRealClone(newRowMeta.valueMetaList);
        Assert.assertNotNull(newRowMeta.needRealClone);
        Assert.assertEquals(4, list.size());// Should still be charly, dup, bin, charly_1

        newRowMeta.addValueMeta(bin);// add new binary, should null out needRealClone again

        Assert.assertNull(newRowMeta.needRealClone);// null because of the new add

        list = newRowMeta.getOrCreateValuesThatNeedRealClone(newRowMeta.valueMetaList);
        Assert.assertNotNull(newRowMeta.needRealClone);
        Assert.assertEquals(5, list.size());// Should be charly, dup and bin, charly_1, bin_1

        newRowMeta.addValueMeta(string);// add new string, should null out needRealClone again

        Assert.assertNull(newRowMeta.needRealClone);// null because of the new add

        list = newRowMeta.getOrCreateValuesThatNeedRealClone(newRowMeta.valueMetaList);
        Assert.assertNotNull(newRowMeta.needRealClone);
        Assert.assertEquals(5, list.size());// Should still only be charly, dup and bin, charly_1, bin_1 - adding a string doesn't change of result

    }

    @Test
    public void testMergeRowMetaWithOriginStep() throws Exception {
        List<ValueMetaInterface> list = this.generateVList(new String[]{ "phobos", "demos", "mars" }, new int[]{ 6, 6, 6 });
        list.add(1, integer);
        RowMeta toMerge = new RowMeta();
        toMerge.setValueMetaList(list);
        rowMeta.mergeRowMeta(toMerge, "newOriginStep");
        Assert.assertEquals(7, rowMeta.size());
        list = rowMeta.getValueMetaList();
        Assert.assertTrue(list.contains(integer));
        ValueMetaInterface found = null;
        ValueMetaInterface other = null;
        for (ValueMetaInterface vm : list) {
            if (vm.getName().equals("integer_1")) {
                found = vm;
                break;
            } else {
                other = vm;
            }
        }
        Assert.assertNotNull(found);
        Assert.assertEquals(found.getOrigin(), "newOriginStep");
        Assert.assertNotNull(other);
        Assert.assertEquals(other.getOrigin(), "originStep");
    }

    @Test
    public void testGetFieldNames() {
        rowMeta.clear();
        fillRowMeta();
        String[] names = rowMeta.getFieldNames();
        Assert.assertEquals(10, names.length);
        Assert.assertEquals("sample", names[0]);
        for (int i = 1; i < (names.length); i++) {
            Assert.assertEquals("", names[i]);
        }
    }

    @Test
    public void testHashCode() {
        rowMeta.clear();
        byte[] byteArray = new byte[]{ 49, 50, 51 };
        Object[] objArray = new Object[]{ byteArray };
        try {
            Assert.assertEquals(78512, rowMeta.hashCode(objArray));
        } catch (KettleValueException e) {
            e.printStackTrace();
        }
    }
}

