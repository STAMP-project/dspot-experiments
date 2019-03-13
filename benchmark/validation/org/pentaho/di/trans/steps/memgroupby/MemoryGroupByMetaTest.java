/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.steps.memgroupby;


import Const.KETTLE_COMPATIBILITY_MEMORY_GROUP_BY_SUM_AVERAGE_RETURN_NUMBER_TYPE;
import ValueMetaInterface.TYPE_BIGNUMBER;
import ValueMetaInterface.TYPE_BINARY;
import ValueMetaInterface.TYPE_BOOLEAN;
import ValueMetaInterface.TYPE_DATE;
import ValueMetaInterface.TYPE_INET;
import ValueMetaInterface.TYPE_INTEGER;
import ValueMetaInterface.TYPE_NUMBER;
import ValueMetaInterface.TYPE_STRING;
import ValueMetaInterface.TYPE_TIMESTAMP;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.initializer.InitializerInterface;

import static MemoryGroupByMeta.TYPE_GROUP_AVERAGE;
import static MemoryGroupByMeta.TYPE_GROUP_CONCAT_COMMA;
import static MemoryGroupByMeta.TYPE_GROUP_CONCAT_STRING;
import static MemoryGroupByMeta.TYPE_GROUP_COUNT_ALL;
import static MemoryGroupByMeta.TYPE_GROUP_COUNT_ANY;
import static MemoryGroupByMeta.TYPE_GROUP_COUNT_DISTINCT;
import static MemoryGroupByMeta.TYPE_GROUP_FIRST;
import static MemoryGroupByMeta.TYPE_GROUP_FIRST_INCL_NULL;
import static MemoryGroupByMeta.TYPE_GROUP_LAST;
import static MemoryGroupByMeta.TYPE_GROUP_LAST_INCL_NULL;
import static MemoryGroupByMeta.TYPE_GROUP_MAX;
import static MemoryGroupByMeta.TYPE_GROUP_MEDIAN;
import static MemoryGroupByMeta.TYPE_GROUP_MIN;
import static MemoryGroupByMeta.TYPE_GROUP_PERCENTILE;
import static MemoryGroupByMeta.TYPE_GROUP_STANDARD_DEVIATION;
import static MemoryGroupByMeta.TYPE_GROUP_SUM;


public class MemoryGroupByMetaTest implements InitializerInterface<MemoryGroupByMeta> {
    LoadSaveTester<MemoryGroupByMeta> loadSaveTester;

    Class<MemoryGroupByMeta> testMetaClass = MemoryGroupByMeta.class;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    @Test
    public void testGetFields() {
        final String stepName = "this step name";
        MemoryGroupByMeta meta = new MemoryGroupByMeta();
        meta.setDefault();
        meta.allocate(1, 17);
        // Declare input fields
        RowMetaInterface rm = getInputRowMeta();
        String[] groupFields = new String[2];
        groupFields[0] = "myGroupField1";
        groupFields[1] = "myGroupField2";
        String[] aggregateFields = new String[24];
        String[] subjectFields = new String[24];
        int[] aggregateTypes = new int[24];
        String[] valueFields = new String[24];
        subjectFields[0] = "myString";
        aggregateTypes[0] = TYPE_GROUP_CONCAT_COMMA;
        aggregateFields[0] = "ConcatComma";
        valueFields[0] = null;
        subjectFields[1] = "myString";
        aggregateTypes[1] = TYPE_GROUP_CONCAT_STRING;
        aggregateFields[1] = "ConcatString";
        valueFields[1] = "|";
        subjectFields[2] = "myString";
        aggregateTypes[2] = TYPE_GROUP_COUNT_ALL;
        aggregateFields[2] = "CountAll";
        valueFields[2] = null;
        subjectFields[3] = "myString";
        aggregateTypes[3] = TYPE_GROUP_COUNT_ANY;
        aggregateFields[3] = "CountAny";
        valueFields[3] = null;
        subjectFields[4] = "myString";
        aggregateTypes[4] = TYPE_GROUP_COUNT_DISTINCT;
        aggregateFields[4] = "CountDistinct";
        valueFields[4] = null;
        subjectFields[5] = "myString";
        aggregateTypes[5] = TYPE_GROUP_FIRST;
        aggregateFields[5] = "First(String)";
        valueFields[5] = null;
        subjectFields[6] = "myInteger";
        aggregateTypes[6] = TYPE_GROUP_FIRST;
        aggregateFields[6] = "First(Integer)";
        valueFields[6] = null;
        subjectFields[7] = "myNumber";
        aggregateTypes[7] = TYPE_GROUP_FIRST_INCL_NULL;
        aggregateFields[7] = "FirstInclNull(Number)";
        valueFields[7] = null;
        subjectFields[8] = "myBigNumber";
        aggregateTypes[8] = TYPE_GROUP_FIRST_INCL_NULL;
        aggregateFields[8] = "FirstInclNull(BigNumber)";
        valueFields[8] = null;
        subjectFields[9] = "myBinary";
        aggregateTypes[9] = TYPE_GROUP_LAST;
        aggregateFields[9] = "Last(Binary)";
        valueFields[9] = null;
        subjectFields[10] = "myBoolean";
        aggregateTypes[10] = TYPE_GROUP_LAST;
        aggregateFields[10] = "Last(Boolean)";
        valueFields[10] = null;
        subjectFields[11] = "myDate";
        aggregateTypes[11] = TYPE_GROUP_LAST_INCL_NULL;
        aggregateFields[11] = "LastInclNull(Date)";
        valueFields[11] = null;
        subjectFields[12] = "myTimestamp";
        aggregateTypes[12] = TYPE_GROUP_LAST_INCL_NULL;
        aggregateFields[12] = "LastInclNull(Timestamp)";
        valueFields[12] = null;
        subjectFields[13] = "myInternetAddress";
        aggregateTypes[13] = TYPE_GROUP_MAX;
        aggregateFields[13] = "Max(InternetAddress)";
        valueFields[13] = null;
        subjectFields[14] = "myString";
        aggregateTypes[14] = TYPE_GROUP_MAX;
        aggregateFields[14] = "Max(String)";
        valueFields[14] = null;
        subjectFields[15] = "myInteger";
        aggregateTypes[15] = TYPE_GROUP_MEDIAN;// Always returns Number

        aggregateFields[15] = "Median(Integer)";
        valueFields[15] = null;
        subjectFields[16] = "myNumber";
        aggregateTypes[16] = TYPE_GROUP_MIN;
        aggregateFields[16] = "Min(Number)";
        valueFields[16] = null;
        subjectFields[17] = "myBigNumber";
        aggregateTypes[17] = TYPE_GROUP_MIN;
        aggregateFields[17] = "Min(BigNumber)";
        valueFields[17] = null;
        subjectFields[18] = "myBinary";
        aggregateTypes[18] = TYPE_GROUP_PERCENTILE;
        aggregateFields[18] = "Percentile(Binary)";
        valueFields[18] = "0.5";
        subjectFields[19] = "myBoolean";
        aggregateTypes[19] = TYPE_GROUP_STANDARD_DEVIATION;
        aggregateFields[19] = "StandardDeviation(Boolean)";
        valueFields[19] = null;
        subjectFields[20] = "myDate";
        aggregateTypes[20] = TYPE_GROUP_SUM;
        aggregateFields[20] = "Sum(Date)";
        valueFields[20] = null;
        subjectFields[21] = "myInteger";
        aggregateTypes[21] = TYPE_GROUP_SUM;
        aggregateFields[21] = "Sum(Integer)";
        valueFields[21] = null;
        subjectFields[22] = "myInteger";
        aggregateTypes[22] = TYPE_GROUP_AVERAGE;
        aggregateFields[22] = "Average(Integer)";
        valueFields[22] = null;
        subjectFields[23] = "myDate";
        aggregateTypes[23] = TYPE_GROUP_AVERAGE;
        aggregateFields[23] = "Average(Date)";
        valueFields[23] = null;
        meta.setGroupField(groupFields);
        meta.setSubjectField(subjectFields);
        meta.setAggregateType(aggregateTypes);
        meta.setAggregateField(aggregateFields);
        meta.setValueField(valueFields);
        Variables vars = new Variables();
        meta.getFields(rm, stepName, null, null, vars, null, null);
        Assert.assertNotNull(rm);
        Assert.assertEquals(26, rm.size());
        Assert.assertTrue(((rm.indexOfValue("myGroupField1")) >= 0));
        Assert.assertEquals(TYPE_STRING, rm.getValueMeta(rm.indexOfValue("myGroupField1")).getType());
        Assert.assertTrue(((rm.indexOfValue("myGroupField2")) >= 0));
        Assert.assertEquals(TYPE_STRING, rm.getValueMeta(rm.indexOfValue("myGroupField2")).getType());
        Assert.assertTrue(((rm.indexOfValue("myGroupField2")) > (rm.indexOfValue("myGroupField1"))));
        Assert.assertTrue(((rm.indexOfValue("ConcatComma")) >= 0));
        Assert.assertEquals(TYPE_STRING, rm.getValueMeta(rm.indexOfValue("ConcatComma")).getType());
        Assert.assertTrue(((rm.indexOfValue("ConcatString")) >= 0));
        Assert.assertEquals(TYPE_STRING, rm.getValueMeta(rm.indexOfValue("ConcatString")).getType());
        Assert.assertTrue(((rm.indexOfValue("CountAll")) >= 0));
        Assert.assertEquals(TYPE_INTEGER, rm.getValueMeta(rm.indexOfValue("CountAll")).getType());
        Assert.assertTrue(((rm.indexOfValue("CountAny")) >= 0));
        Assert.assertEquals(TYPE_INTEGER, rm.getValueMeta(rm.indexOfValue("CountAny")).getType());
        Assert.assertTrue(((rm.indexOfValue("CountDistinct")) >= 0));
        Assert.assertEquals(TYPE_INTEGER, rm.getValueMeta(rm.indexOfValue("CountDistinct")).getType());
        Assert.assertTrue(((rm.indexOfValue("First(String)")) >= 0));
        Assert.assertEquals(TYPE_STRING, rm.getValueMeta(rm.indexOfValue("First(String)")).getType());
        Assert.assertTrue(((rm.indexOfValue("First(Integer)")) >= 0));
        Assert.assertEquals(TYPE_INTEGER, rm.getValueMeta(rm.indexOfValue("First(Integer)")).getType());
        Assert.assertTrue(((rm.indexOfValue("FirstInclNull(Number)")) >= 0));
        Assert.assertEquals(TYPE_NUMBER, rm.getValueMeta(rm.indexOfValue("FirstInclNull(Number)")).getType());
        Assert.assertTrue(((rm.indexOfValue("FirstInclNull(BigNumber)")) >= 0));
        Assert.assertEquals(TYPE_BIGNUMBER, rm.getValueMeta(rm.indexOfValue("FirstInclNull(BigNumber)")).getType());
        Assert.assertTrue(((rm.indexOfValue("Last(Binary)")) >= 0));
        Assert.assertEquals(TYPE_BINARY, rm.getValueMeta(rm.indexOfValue("Last(Binary)")).getType());
        Assert.assertTrue(((rm.indexOfValue("Last(Boolean)")) >= 0));
        Assert.assertEquals(TYPE_BOOLEAN, rm.getValueMeta(rm.indexOfValue("Last(Boolean)")).getType());
        Assert.assertTrue(((rm.indexOfValue("LastInclNull(Date)")) >= 0));
        Assert.assertEquals(TYPE_DATE, rm.getValueMeta(rm.indexOfValue("LastInclNull(Date)")).getType());
        Assert.assertTrue(((rm.indexOfValue("LastInclNull(Timestamp)")) >= 0));
        Assert.assertEquals(TYPE_TIMESTAMP, rm.getValueMeta(rm.indexOfValue("LastInclNull(Timestamp)")).getType());
        Assert.assertTrue(((rm.indexOfValue("Max(InternetAddress)")) >= 0));
        Assert.assertEquals(TYPE_INET, rm.getValueMeta(rm.indexOfValue("Max(InternetAddress)")).getType());
        Assert.assertTrue(((rm.indexOfValue("Max(String)")) >= 0));
        Assert.assertEquals(TYPE_STRING, rm.getValueMeta(rm.indexOfValue("Max(String)")).getType());
        Assert.assertTrue(((rm.indexOfValue("Median(Integer)")) >= 0));
        Assert.assertEquals(TYPE_NUMBER, rm.getValueMeta(rm.indexOfValue("Median(Integer)")).getType());
        Assert.assertTrue(((rm.indexOfValue("Min(Number)")) >= 0));
        Assert.assertEquals(TYPE_NUMBER, rm.getValueMeta(rm.indexOfValue("Min(Number)")).getType());
        Assert.assertTrue(((rm.indexOfValue("Min(BigNumber)")) >= 0));
        Assert.assertEquals(TYPE_BIGNUMBER, rm.getValueMeta(rm.indexOfValue("Min(BigNumber)")).getType());
        Assert.assertTrue(((rm.indexOfValue("Percentile(Binary)")) >= 0));
        Assert.assertEquals(TYPE_NUMBER, rm.getValueMeta(rm.indexOfValue("Percentile(Binary)")).getType());
        Assert.assertTrue(((rm.indexOfValue("StandardDeviation(Boolean)")) >= 0));
        Assert.assertEquals(TYPE_NUMBER, rm.getValueMeta(rm.indexOfValue("StandardDeviation(Boolean)")).getType());
        Assert.assertTrue(((rm.indexOfValue("Sum(Date)")) >= 0));
        Assert.assertEquals(TYPE_NUMBER, rm.getValueMeta(rm.indexOfValue("Sum(Date)")).getType());// Force changed to Numeric

        Assert.assertTrue(((rm.indexOfValue("Sum(Integer)")) >= 0));
        Assert.assertEquals(TYPE_INTEGER, rm.getValueMeta(rm.indexOfValue("Sum(Integer)")).getType());
        Assert.assertTrue(((rm.indexOfValue("Average(Integer)")) >= 0));
        Assert.assertEquals(TYPE_INTEGER, rm.getValueMeta(rm.indexOfValue("Average(Integer)")).getType());
        Assert.assertTrue(((rm.indexOfValue("Average(Date)")) >= 0));
        Assert.assertEquals(TYPE_NUMBER, rm.getValueMeta(rm.indexOfValue("Average(Date)")).getType());
        // Test Compatibility
        rm = getInputRowMeta();
        vars.setVariable(KETTLE_COMPATIBILITY_MEMORY_GROUP_BY_SUM_AVERAGE_RETURN_NUMBER_TYPE, "Y");
        meta.getFields(rm, stepName, null, null, vars, null, null);
        Assert.assertNotNull(rm);
        Assert.assertEquals(26, rm.size());
        Assert.assertTrue(((rm.indexOfValue("Average(Integer)")) >= 0));
        Assert.assertEquals(TYPE_NUMBER, rm.getValueMeta(rm.indexOfValue("Average(Integer)")).getType());
    }

    @Test
    public void testPDI16559() throws Exception {
        MemoryGroupByMeta memoryGroupBy = new MemoryGroupByMeta();
        memoryGroupBy.setGroupField(new String[]{ "group1", "group 2" });
        memoryGroupBy.setSubjectField(new String[]{ "field1", "field2", "field3", "field4", "field5", "field6", "field7", "field8", "field9", "field10", "field11", "field12" });
        memoryGroupBy.setAggregateField(new String[]{ "fieldID1", "fieldID2", "fieldID3", "fieldID4", "fieldID5", "fieldID6", "fieldID7", "fieldID8", "fieldID9", "fieldID10", "fieldID11" });
        memoryGroupBy.setValueField(new String[]{ "asdf", "asdf", "qwer", "qwer", "QErasdf", "zxvv", "fasdf", "qwerqwr" });
        memoryGroupBy.setAggregateType(new int[]{ 12, 6, 15, 14, 23, 177, 13, 21 });
        try {
            String badXml = memoryGroupBy.getXML();
            Assert.fail("Before calling afterInjectionSynchronization, should have thrown an ArrayIndexOOB");
        } catch (Exception expected) {
            // Do Nothing
        }
        memoryGroupBy.afterInjectionSynchronization();
        // run without a exception
        String ktrXml = memoryGroupBy.getXML();
        int targetSz = memoryGroupBy.getSubjectField().length;
        Assert.assertEquals(targetSz, memoryGroupBy.getAggregateField().length);
        Assert.assertEquals(targetSz, memoryGroupBy.getAggregateType().length);
        Assert.assertEquals(targetSz, memoryGroupBy.getValueField().length);
        // Check for null arrays being handled
        memoryGroupBy.setValueField(null);// null string array

        memoryGroupBy.afterInjectionSynchronization();
        Assert.assertEquals(targetSz, memoryGroupBy.getValueField().length);
    }
}

