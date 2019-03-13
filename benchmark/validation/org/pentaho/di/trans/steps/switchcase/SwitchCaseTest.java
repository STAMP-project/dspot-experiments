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
package org.pentaho.di.trans.steps.switchcase;


import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.di.core.QueueRowSet;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.dummytrans.DummyTransMeta;
import org.pentaho.di.trans.steps.mock.StepMockHelper;
import org.pentaho.metastore.api.IMetaStore;
import org.xml.sax.SAXException;


public class SwitchCaseTest {
    private StepMockHelper<SwitchCaseMeta, SwitchCaseData> mockHelper;

    private static Boolean EMPTY_STRING_AND_NULL_ARE_DIFFERENT = false;

    /**
     * PDI 6900. Test that process row works correctly. Simulate step workload when input and output row sets already
     * created and mapped to specified case values.
     *
     * @throws KettleException
     * 		
     */
    @Test
    public void testProcessRow() throws KettleException {
        SwitchCaseTest.SwitchCaseCustom krasavez = new SwitchCaseTest.SwitchCaseCustom(mockHelper);
        krasavez.first = false;
        // create two output row sets
        RowSet rowSetOne = new QueueRowSet();
        RowSet rowSetTwo = new QueueRowSet();
        // this row set should contain only '3'.
        krasavez.data.outputMap.put(3, rowSetOne);
        krasavez.data.outputMap.put(3, rowSetTwo);
        // this row set contains nulls only
        RowSet rowSetNullOne = new QueueRowSet();
        RowSet rowSetNullTwo = new QueueRowSet();
        krasavez.data.nullRowSetSet.add(rowSetNullOne);
        krasavez.data.nullRowSetSet.add(rowSetNullTwo);
        // this row set contains all expect null or '3'
        RowSet def = new QueueRowSet();
        krasavez.data.defaultRowSetSet.add(def);
        // generate some data (see method implementation)
        // expected: 5 times null,
        // expected 1*2 = 2 times 3
        // expected 5*2 + 5 = 15 rows generated
        // expected 15 - 5 - 2 = 8 rows go to default.
        // expected one empty string at the end
        // System.out.println( krasavez.getInputDataOverview() );
        // 1, 1, null, 2, 2, null, 3, 3, null, 4, 4, null, 5, 5, null,""
        krasavez.generateData(1, 5, 2);
        // call method under test
        krasavez.processRow();
        Assert.assertEquals("First row set collects 2 rows", 2, rowSetOne.size());
        Assert.assertEquals("Second row set collects 2 rows", 2, rowSetTwo.size());
        Assert.assertEquals("First null row set collects 5 rows", 6, rowSetNullOne.size());
        Assert.assertEquals("Second null row set collects 5 rows", 6, rowSetNullTwo.size());
        Assert.assertEquals("Default row set collects the rest of rows", 8, def.size());
        // now - check the data is correct in every row set:
        Assert.assertEquals("First row set contains only 3: ", true, isRowSetContainsValue(rowSetOne, new Object[]{ 3 }, new Object[]{  }));
        Assert.assertEquals("Second row set contains only 3: ", true, isRowSetContainsValue(rowSetTwo, new Object[]{ 3 }, new Object[]{  }));
        Assert.assertEquals("First null row set contains only null: ", true, isRowSetContainsValue(rowSetNullOne, new Object[]{ null }, new Object[]{  }));
        Assert.assertEquals("Second null row set contains only null: ", true, isRowSetContainsValue(rowSetNullTwo, new Object[]{ null }, new Object[]{  }));
        Assert.assertEquals("Default row set do not contains null or 3, but other", true, isRowSetContainsValue(def, new Object[]{ 1, 2, 4, 5 }, new Object[]{ 3, null }));
    }

    /**
     * PDI-6900 Check that SwichCase step can correctly set up input values to output rowsets.
     *
     * @throws KettleException
     * 		
     * @throws URISyntaxException
     * 		
     * @throws ParserConfigurationException
     * 		
     * @throws SAXException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testCreateOutputValueMapping() throws IOException, URISyntaxException, ParserConfigurationException, KettleException, SAXException {
        SwitchCaseTest.SwitchCaseCustom krasavez = new SwitchCaseTest.SwitchCaseCustom(mockHelper);
        // load step info value-case mapping from xml.
        List<DatabaseMeta> emptyList = new ArrayList<DatabaseMeta>();
        krasavez.meta.loadXML(SwitchCaseTest.loadStepXmlMetadata("SwitchCaseTest.xml"), emptyList, Mockito.mock(IMetaStore.class));
        KeyToRowSetMap expectedNN = new KeyToRowSetMap();
        Set<RowSet> nulls = new HashSet<RowSet>();
        // create real steps for all targets
        List<SwitchCaseTarget> list = krasavez.meta.getCaseTargets();
        for (SwitchCaseTarget item : list) {
            StepMetaInterface smInt = new DummyTransMeta();
            StepMeta stepMeta = new StepMeta(item.caseTargetStepname, smInt);
            item.caseTargetStep = stepMeta;
            // create and put row set for this
            RowSet rw = new QueueRowSet();
            krasavez.map.put(item.caseTargetStepname, rw);
            // null values goes to null rowset
            if ((item.caseValue) != null) {
                expectedNN.put(item.caseValue, rw);
            } else {
                nulls.add(rw);
            }
        }
        // create default step
        StepMetaInterface smInt = new DummyTransMeta();
        StepMeta stepMeta = new StepMeta(krasavez.meta.getDefaultTargetStepname(), smInt);
        krasavez.meta.setDefaultTargetStep(stepMeta);
        RowSet rw = new QueueRowSet();
        krasavez.map.put(krasavez.meta.getDefaultTargetStepname(), rw);
        createOutputValueMapping();
        // inspect step output data:
        Set<RowSet> ones = krasavez.data.outputMap.get("1");
        Assert.assertEquals("Output map for 1 values contains 2 row sets", 2, ones.size());
        Set<RowSet> twos = krasavez.data.outputMap.get("2");
        Assert.assertEquals("Output map for 2 values contains 1 row sets", 1, twos.size());
        Assert.assertEquals("Null row set contains 2 items: ", 2, krasavez.data.nullRowSetSet.size());
        Assert.assertEquals("We have at least one default rowset", 1, krasavez.data.defaultRowSetSet.size());
        // check that rowsets data is correct:
        Set<RowSet> rowsets = expectedNN.get("1");
        for (RowSet rowset : rowsets) {
            Assert.assertTrue("Output map for 1 values contains expected row set", ones.contains(rowset));
        }
        rowsets = expectedNN.get("2");
        for (RowSet rowset : rowsets) {
            Assert.assertTrue("Output map for 2 values contains expected row set", twos.contains(rowset));
        }
        for (RowSet rowset : krasavez.data.nullRowSetSet) {
            Assert.assertTrue("Output map for null values contains expected row set", nulls.contains(rowset));
        }
        // we have already check that there is only one item.
        for (RowSet rowset : krasavez.data.defaultRowSetSet) {
            Assert.assertTrue("Output map for default case contains expected row set", rowset.equals(rw));
        }
    }

    @Test
    public void testCreateOutputValueMappingWithBinaryType() throws IOException, URISyntaxException, ParserConfigurationException, KettleException, SAXException {
        SwitchCaseTest.SwitchCaseCustom krasavez = new SwitchCaseTest.SwitchCaseCustom(mockHelper);
        // load step info value-case mapping from xml.
        List<DatabaseMeta> emptyList = new ArrayList<DatabaseMeta>();
        krasavez.meta.loadXML(SwitchCaseTest.loadStepXmlMetadata("SwitchCaseBinaryTest.xml"), emptyList, Mockito.mock(IMetaStore.class));
        KeyToRowSetMap expectedNN = new KeyToRowSetMap();
        Set<RowSet> nulls = new HashSet<RowSet>();
        // create real steps for all targets
        List<SwitchCaseTarget> list = krasavez.meta.getCaseTargets();
        for (SwitchCaseTarget item : list) {
            StepMetaInterface smInt = new DummyTransMeta();
            StepMeta stepMeta = new StepMeta(item.caseTargetStepname, smInt);
            item.caseTargetStep = stepMeta;
            // create and put row set for this
            RowSet rw = new QueueRowSet();
            krasavez.map.put(item.caseTargetStepname, rw);
            // null values goes to null rowset
            if ((item.caseValue) != null) {
                expectedNN.put(item.caseValue, rw);
            } else {
                nulls.add(rw);
            }
        }
        // create default step
        StepMetaInterface smInt = new DummyTransMeta();
        StepMeta stepMeta = new StepMeta(krasavez.meta.getDefaultTargetStepname(), smInt);
        krasavez.meta.setDefaultTargetStep(stepMeta);
        RowSet rw = new QueueRowSet();
        krasavez.map.put(krasavez.meta.getDefaultTargetStepname(), rw);
        createOutputValueMapping();
        // inspect step output data:
        Set<RowSet> ones = krasavez.data.outputMap.get("1");
        Assert.assertEquals("Output map for 1 values contains 2 row sets", 2, ones.size());
        Set<RowSet> zeros = krasavez.data.outputMap.get("0");
        Assert.assertEquals("Output map for 0 values contains 1 row sets", 1, zeros.size());
        Assert.assertEquals("Null row set contains 0 items: ", 2, krasavez.data.nullRowSetSet.size());
        Assert.assertEquals("We have at least one default rowset", 1, krasavez.data.defaultRowSetSet.size());
        // check that rowsets data is correct:
        Set<RowSet> rowsets = expectedNN.get("1");
        for (RowSet rowset : rowsets) {
            Assert.assertTrue("Output map for 1 values contains expected row set", ones.contains(rowset));
        }
        rowsets = expectedNN.get("0");
        for (RowSet rowset : rowsets) {
            Assert.assertTrue("Output map for 0 values contains expected row set", zeros.contains(rowset));
        }
        for (RowSet rowset : krasavez.data.nullRowSetSet) {
            Assert.assertTrue("Output map for null values contains expected row set", nulls.contains(rowset));
        }
        // we have already check that there is only one item.
        for (RowSet rowset : krasavez.data.defaultRowSetSet) {
            Assert.assertTrue("Output map for default case contains expected row set", rowset.equals(rw));
        }
    }

    @Test
    public void processRow_NullsArePutIntoDefaultWhenNotSpecified() throws Exception {
        SwitchCaseTest.SwitchCaseCustom step = new SwitchCaseTest.SwitchCaseCustom(mockHelper);
        step.meta.loadXML(SwitchCaseTest.loadStepXmlMetadata("SwitchCaseTest_PDI-12671.xml"), Collections.<DatabaseMeta>emptyList(), Mockito.mock(IMetaStore.class));
        List<RowSet> outputRowSets = new LinkedList<RowSet>();
        for (SwitchCaseTarget item : step.meta.getCaseTargets()) {
            StepMetaInterface smInt = new DummyTransMeta();
            item.caseTargetStep = new StepMeta(item.caseTargetStepname, smInt);
            RowSet rw = new QueueRowSet();
            step.map.put(item.caseTargetStepname, rw);
            outputRowSets.add(rw);
        }
        // create a default step
        StepMetaInterface smInt = new DummyTransMeta();
        StepMeta stepMeta = new StepMeta(step.meta.getDefaultTargetStepname(), smInt);
        step.meta.setDefaultTargetStep(stepMeta);
        RowSet defaultRowSet = new QueueRowSet();
        step.map.put(step.meta.getDefaultTargetStepname(), defaultRowSet);
        step.input.add(new Object[]{ null });
        step.processRow();
        Assert.assertEquals(1, defaultRowSet.size());
        for (RowSet rowSet : outputRowSets) {
            Assert.assertEquals(0, rowSet.size());
        }
        Assert.assertNull(defaultRowSet.getRow()[0]);
    }

    @Test
    public void prepareObjectTypeBinaryTest_Equals() throws Exception {
        Assert.assertEquals(Arrays.hashCode(new byte[]{ 1, 2, 3 }), SwitchCase.prepareObjectType(new byte[]{ 1, 2, 3 }));
    }

    @Test
    public void prepareObjectTypeBinaryTest_NotEquals() throws Exception {
        Assert.assertNotEquals(Arrays.hashCode(new byte[]{ 1, 2, 4 }), SwitchCase.prepareObjectType(new byte[]{ 1, 2, 3 }));
    }

    @Test
    public void prepareObjectTypeBinaryTest_Null() throws Exception {
        byte[] given = null;
        byte[] expected = null;
        Assert.assertEquals(expected, SwitchCase.prepareObjectType(given));
    }

    @Test
    public void prepareObjectTypeTest_Equals() throws Exception {
        Assert.assertEquals("2", SwitchCase.prepareObjectType("2"));
    }

    @Test
    public void prepareObjectTypeTest_NotEquals() throws Exception {
        Assert.assertNotEquals("2", SwitchCase.prepareObjectType("1"));
    }

    @Test
    public void prepareObjectTypeTest_Null() throws Exception {
        Assert.assertEquals(null, SwitchCase.prepareObjectType(null));
    }

    /**
     * Switch case step ancestor with overridden methods to have ability to simulate normal transformation execution.
     */
    private static class SwitchCaseCustom extends SwitchCase {
        Queue<Object[]> input = new LinkedList<Object[]>();

        RowMetaInterface rowMetaInterface;

        // we will use real data and meta.
        SwitchCaseData data = new SwitchCaseData();

        SwitchCaseMeta meta = new SwitchCaseMeta();

        Map<String, RowSet> map = new HashMap<String, RowSet>();

        SwitchCaseCustom(StepMockHelper<SwitchCaseMeta, SwitchCaseData> mockHelper) throws KettleValueException {
            super(mockHelper.stepMeta, mockHelper.stepDataInterface, 0, mockHelper.transMeta, mockHelper.trans);
            // this.mockHelper = mockHelper;
            init(meta, data);
            // call to convert value will returns same value.
            data.valueMeta = Mockito.mock(ValueMetaInterface.class);
            Mockito.when(data.valueMeta.convertData(ArgumentMatchers.any(ValueMetaInterface.class), ArgumentMatchers.any())).thenAnswer(new Answer<Object>() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    Object[] objArr = invocation.getArguments();
                    return (objArr != null) && ((objArr.length) > 1) ? objArr[1] : null;
                }
            });
            // same when call to convertDataFromString
            // CHECKSTYLE:Indentation:OFF
            Mockito.when(data.valueMeta.convertDataFromString(Mockito.anyString(), ArgumentMatchers.any(ValueMetaInterface.class), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt())).thenAnswer(new Answer<Object>() {
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    Object[] objArr = invocation.getArguments();
                    return (objArr != null) && ((objArr.length) > 1) ? objArr[0] : null;
                }
            });
            // null-check
            Mockito.when(data.valueMeta.isNull(ArgumentMatchers.any())).thenAnswer(new Answer<Object>() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    Object[] objArr = invocation.getArguments();
                    Object obj = objArr[0];
                    if (obj == null) {
                        return true;
                    }
                    if (SwitchCaseTest.EMPTY_STRING_AND_NULL_ARE_DIFFERENT) {
                        return false;
                    }
                    // If it's a string and the string is empty, it's a null value as well
                    // 
                    if (obj instanceof String) {
                        if ((((String) (obj)).length()) == 0) {
                            return true;
                        }
                    }
                    return false;
                }
            });
        }

        /**
         * used for input row generation
         *
         * @param start
         * 		
         * @param finish
         * 		
         * @param copy
         * 		
         */
        void generateData(int start, int finish, int copy) {
            input.clear();
            for (int i = start; i <= finish; i++) {
                for (int j = 0; j < copy; j++) {
                    input.add(new Object[]{ i });
                }
                input.add(new Object[]{ null });
            }
            input.add(new Object[]{ "" });
        }

        /**
         * useful to see generated data as String
         *
         * @return 
         */
        @SuppressWarnings("unused")
        public String getInputDataOverview() {
            StringBuilder sb = new StringBuilder();
            for (Object[] row : input) {
                sb.append(((row[0]) + ", "));
            }
            return sb.toString();
        }

        /**
         * mock step data processing
         */
        @Override
        public Object[] getRow() throws KettleException {
            return input.poll();
        }

        /**
         * simulate concurrent execution
         *
         * @throws KettleException
         * 		
         */
        public void processRow() throws KettleException {
            boolean run = false;
            do {
                run = processRow(meta, data);
            } while (run );
        }

        @Override
        public RowSet findOutputRowSet(String targetStep) throws KettleStepException {
            return map.get(targetStep);
        }

        @Override
        public RowMetaInterface getInputRowMeta() {
            if ((rowMetaInterface) == null) {
                rowMetaInterface = getDynamicRowMetaInterface();
            }
            return rowMetaInterface;
        }

        private RowMetaInterface getDynamicRowMetaInterface() {
            RowMetaInterface inputRowMeta = Mockito.mock(RowMetaInterface.class);
            return inputRowMeta;
        }
    }
}

