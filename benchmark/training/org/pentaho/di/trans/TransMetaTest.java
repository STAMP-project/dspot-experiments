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
package org.pentaho.di.trans;


import CheckResultInterface.TYPE_RESULT_OK;
import Const.INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY;
import Const.INTERNAL_VARIABLE_TRANSFORMATION_FILENAME_DIRECTORY;
import Const.INTERNAL_VARIABLE_TRANSFORMATION_REPOSITORY_DIRECTORY;
import TransMeta.XML_TAG_INFO;
import UserDefinedJavaClassDef.ClassType;
import ValueMetaInterface.TYPE_INTEGER;
import ValueMetaInterface.TYPE_STRING;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.ProgressMonitorListener;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.gui.OverwritePrompter;
import org.pentaho.di.core.gui.Point;
import org.pentaho.di.core.listeners.ContentChangedListener;
import org.pentaho.di.core.logging.TransLogTable;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.metastore.DatabaseMetaStoreUtil;
import org.pentaho.di.repository.ObjectRevision;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectory;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.trans.step.StepIOMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaChangeListenerInterface;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.step.StepPartitioningMeta;
import org.pentaho.di.trans.steps.datagrid.DataGridMeta;
import org.pentaho.di.trans.steps.dummytrans.DummyTransMeta;
import org.pentaho.di.trans.steps.textfileoutput.TextFileOutputMeta;
import org.pentaho.di.trans.steps.userdefinedjavaclass.UserDefinedJavaClassMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.stores.memory.MemoryMetaStore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static TransMeta.BORDER_INDENT;


@RunWith(PowerMockRunner.class)
public class TransMetaTest {
    public static final String STEP_NAME = "Any step name";

    private TransMeta transMeta;

    @Test
    public void testGetMinimum() {
        final Point minimalCanvasPoint = new Point(0, 0);
        // for test goal should content coordinate more than NotePadMetaPoint
        final Point stepPoint = new Point(500, 500);
        // empty Trans return 0 coordinate point
        Point point = transMeta.getMinimum();
        Assert.assertEquals(minimalCanvasPoint.x, point.x);
        Assert.assertEquals(minimalCanvasPoint.y, point.y);
        // when Trans  content Step  than  trans should return minimal coordinate of step
        StepMeta stepMeta = Mockito.mock(StepMeta.class);
        Mockito.when(stepMeta.getLocation()).thenReturn(stepPoint);
        transMeta.addStep(stepMeta);
        Point actualStepPoint = transMeta.getMinimum();
        Assert.assertEquals(((stepPoint.x) - (BORDER_INDENT)), actualStepPoint.x);
        Assert.assertEquals(((stepPoint.y) - (BORDER_INDENT)), actualStepPoint.y);
    }

    @Test
    public void getThisStepFieldsPassesCloneRowMeta() throws Exception {
        final String overriddenValue = "overridden";
        StepMeta nextStep = TransMetaTest.mockStepMeta("nextStep");
        StepMetaInterface smi = Mockito.mock(StepMetaInterface.class);
        StepIOMeta ioMeta = Mockito.mock(StepIOMeta.class);
        Mockito.when(smi.getStepIOMeta()).thenReturn(ioMeta);
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                RowMetaInterface rmi = ((RowMetaInterface) (invocation.getArguments()[0]));
                rmi.clear();
                rmi.addValueMeta(new ValueMetaString(overriddenValue));
                return null;
            }
        }).when(smi).getFields(ArgumentMatchers.any(RowMetaInterface.class), ArgumentMatchers.anyString(), ArgumentMatchers.any(RowMetaInterface[].class), ArgumentMatchers.eq(nextStep), ArgumentMatchers.any(VariableSpace.class), ArgumentMatchers.any(Repository.class), ArgumentMatchers.any(IMetaStore.class));
        StepMeta thisStep = TransMetaTest.mockStepMeta("thisStep");
        Mockito.when(thisStep.getStepMetaInterface()).thenReturn(smi);
        RowMeta rowMeta = new RowMeta();
        rowMeta.addValueMeta(new ValueMetaString("value"));
        RowMetaInterface thisStepsFields = transMeta.getThisStepFields(thisStep, nextStep, rowMeta);
        Assert.assertEquals(1, thisStepsFields.size());
        Assert.assertEquals(overriddenValue, thisStepsFields.getValueMeta(0).getName());
    }

    @Test
    public void getThisStepFieldsPassesClonedInfoRowMeta() throws Exception {
        // given
        StepMetaInterface smi = Mockito.mock(StepMetaInterface.class);
        StepIOMeta ioMeta = Mockito.mock(StepIOMeta.class);
        Mockito.when(smi.getStepIOMeta()).thenReturn(ioMeta);
        StepMeta thisStep = TransMetaTest.mockStepMeta("thisStep");
        StepMeta nextStep = TransMetaTest.mockStepMeta("nextStep");
        Mockito.when(thisStep.getStepMetaInterface()).thenReturn(smi);
        RowMeta row = new RowMeta();
        Mockito.when(smi.getTableFields()).thenReturn(row);
        // when
        transMeta.getThisStepFields(thisStep, nextStep, row);
        // then
        Mockito.verify(smi, Mockito.never()).getFields(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq(new RowMetaInterface[]{ row }), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testDatabaseNotOverridden() throws Exception {
        final String name = "db meta";
        DatabaseMeta dbMetaShared = new DatabaseMeta();
        dbMetaShared.setName(name);
        dbMetaShared.setHostname("host");
        DatabaseMeta dbMetaStore = new DatabaseMeta();
        dbMetaStore.setName(name);
        dbMetaStore.setHostname("anotherhost");
        IMetaStore mstore = new MemoryMetaStore();
        DatabaseMetaStoreUtil.createDatabaseElement(mstore, dbMetaStore);
        TransMeta trans = new TransMeta();
        trans.addOrReplaceDatabase(dbMetaShared);
        trans.setMetaStore(mstore);
        trans.importFromMetaStore();
        DatabaseMeta dbMeta = trans.findDatabase(name);
        Assert.assertEquals(dbMetaShared.getHostname(), dbMeta.getHostname());
    }

    @Test
    public void testContentChangeListener() throws Exception {
        ContentChangedListener listener = Mockito.mock(ContentChangedListener.class);
        transMeta.addContentChangedListener(listener);
        transMeta.setChanged();
        transMeta.setChanged(true);
        Mockito.verify(listener, Mockito.times(2)).contentChanged(ArgumentMatchers.same(transMeta));
        transMeta.clearChanged();
        transMeta.setChanged(false);
        Mockito.verify(listener, Mockito.times(2)).contentSafe(ArgumentMatchers.same(transMeta));
        transMeta.removeContentChangedListener(listener);
        transMeta.setChanged();
        transMeta.setChanged(true);
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void testCompare() throws Exception {
        TransMeta transMeta = new TransMeta("aFile", "aName");
        TransMeta transMeta2 = new TransMeta("aFile", "aName");
        Assert.assertEquals(0, transMeta.compare(transMeta, transMeta2));
        transMeta2.setVariable("myVariable", "myValue");
        Assert.assertEquals(0, transMeta.compare(transMeta, transMeta2));
        transMeta2.setFilename(null);
        Assert.assertEquals(1, transMeta.compare(transMeta, transMeta2));
        Assert.assertEquals((-1), transMeta.compare(transMeta2, transMeta));
        transMeta2.setFilename("aFile");
        transMeta2.setName(null);
        Assert.assertEquals(1, transMeta.compare(transMeta, transMeta2));
        Assert.assertEquals((-1), transMeta.compare(transMeta2, transMeta));
        transMeta2.setFilename("aFile2");
        transMeta2.setName("aName");
        Assert.assertEquals((-1), transMeta.compare(transMeta, transMeta2));
        Assert.assertEquals(1, transMeta.compare(transMeta2, transMeta));
        transMeta2.setFilename("aFile");
        transMeta2.setName("aName2");
        Assert.assertEquals((-1), transMeta.compare(transMeta, transMeta2));
        Assert.assertEquals(1, transMeta.compare(transMeta2, transMeta));
        transMeta.setFilename(null);
        transMeta2.setFilename(null);
        transMeta2.setName("aName");
        Assert.assertEquals(0, transMeta.compare(transMeta, transMeta2));
        RepositoryDirectoryInterface path1 = Mockito.mock(RepositoryDirectoryInterface.class);
        transMeta.setRepositoryDirectory(path1);
        Mockito.when(path1.getPath()).thenReturn("aPath2");
        RepositoryDirectoryInterface path2 = Mockito.mock(RepositoryDirectoryInterface.class);
        Mockito.when(path2.getPath()).thenReturn("aPath");
        transMeta2.setRepositoryDirectory(path2);
        Assert.assertEquals(1, transMeta.compare(transMeta, transMeta2));
        Assert.assertEquals((-1), transMeta.compare(transMeta2, transMeta));
        Mockito.when(path1.getPath()).thenReturn("aPath");
        Assert.assertEquals(0, transMeta.compare(transMeta, transMeta2));
        ObjectRevision revision2 = Mockito.mock(ObjectRevision.class);
        transMeta2.setObjectRevision(revision2);
        Assert.assertEquals((-1), transMeta.compare(transMeta, transMeta2));
        Assert.assertEquals(1, transMeta.compare(transMeta2, transMeta));
        ObjectRevision revision1 = Mockito.mock(ObjectRevision.class);
        transMeta.setObjectRevision(revision1);
        Mockito.when(revision1.getName()).thenReturn("aRevision");
        Mockito.when(revision2.getName()).thenReturn("aRevision");
        Assert.assertEquals(0, transMeta.compare(transMeta, transMeta2));
        Mockito.when(revision2.getName()).thenReturn("aRevision2");
        Assert.assertEquals((-1), transMeta.compare(transMeta, transMeta2));
        Assert.assertEquals(1, transMeta.compare(transMeta2, transMeta));
    }

    @Test
    public void testEquals() throws Exception {
        TransMeta transMeta = new TransMeta("1", "2");
        Assert.assertFalse(transMeta.equals("somethingelse"));
        Assert.assertTrue(transMeta.equals(new TransMeta("1", "2")));
    }

    @Test
    public void testTransHops() throws Exception {
        TransMeta transMeta = new TransMeta("transFile", "myTrans");
        StepMeta step1 = new StepMeta("name1", null);
        StepMeta step2 = new StepMeta("name2", null);
        StepMeta step3 = new StepMeta("name3", null);
        StepMeta step4 = new StepMeta("name4", null);
        TransHopMeta hopMeta1 = new TransHopMeta(step1, step2, true);
        TransHopMeta hopMeta2 = new TransHopMeta(step2, step3, true);
        TransHopMeta hopMeta3 = new TransHopMeta(step3, step4, false);
        transMeta.addTransHop(0, hopMeta1);
        transMeta.addTransHop(1, hopMeta2);
        transMeta.addTransHop(2, hopMeta3);
        List<StepMeta> hops = transMeta.getTransHopSteps(true);
        Assert.assertSame(step1, hops.get(0));
        Assert.assertSame(step2, hops.get(1));
        Assert.assertSame(step3, hops.get(2));
        Assert.assertSame(step4, hops.get(3));
        Assert.assertEquals(hopMeta2, transMeta.findTransHop("name2 --> name3 (enabled)"));
        Assert.assertEquals(hopMeta3, transMeta.findTransHopFrom(step3));
        Assert.assertEquals(hopMeta2, transMeta.findTransHop(hopMeta2));
        Assert.assertEquals(hopMeta1, transMeta.findTransHop(step1, step2));
        Assert.assertEquals(null, transMeta.findTransHop(step3, step4, false));
        Assert.assertEquals(hopMeta3, transMeta.findTransHop(step3, step4, true));
        Assert.assertEquals(hopMeta2, transMeta.findTransHopTo(step3));
        transMeta.removeTransHop(0);
        hops = transMeta.getTransHopSteps(true);
        Assert.assertSame(step2, hops.get(0));
        Assert.assertSame(step3, hops.get(1));
        Assert.assertSame(step4, hops.get(2));
        transMeta.removeTransHop(hopMeta2);
        hops = transMeta.getTransHopSteps(true);
        Assert.assertSame(step3, hops.get(0));
        Assert.assertSame(step4, hops.get(1));
    }

    @Test
    public void testGetAllTransHops() throws Exception {
        TransMeta transMeta = new TransMeta("transFile", "myTrans");
        StepMeta step1 = new StepMeta("name1", null);
        StepMeta step2 = new StepMeta("name2", null);
        StepMeta step3 = new StepMeta("name3", null);
        StepMeta step4 = new StepMeta("name4", null);
        TransHopMeta hopMeta1 = new TransHopMeta(step1, step2, true);
        TransHopMeta hopMeta2 = new TransHopMeta(step2, step3, true);
        TransHopMeta hopMeta3 = new TransHopMeta(step2, step4, true);
        transMeta.addTransHop(0, hopMeta1);
        transMeta.addTransHop(1, hopMeta2);
        transMeta.addTransHop(2, hopMeta3);
        List<TransHopMeta> allTransHopFrom = transMeta.findAllTransHopFrom(step2);
        Assert.assertEquals(step3, allTransHopFrom.get(0).getToStep());
        Assert.assertEquals(step4, allTransHopFrom.get(1).getToStep());
    }

    @Test
    public void testGetPrevInfoFields() throws KettleStepException {
        DataGridMeta dgm1 = new DataGridMeta();
        dgm1.setFieldName(new String[]{ "id", "colA" });
        dgm1.allocate(2);
        dgm1.setFieldType(new String[]{ ValueMetaFactory.getValueMetaName(TYPE_INTEGER), ValueMetaFactory.getValueMetaName(TYPE_STRING) });
        List<List<String>> dgm1Data = new ArrayList<>();
        dgm1Data.add(Arrays.asList("1", "A"));
        dgm1Data.add(Arrays.asList("2", "B"));
        dgm1.setDataLines(dgm1Data);
        DataGridMeta dgm2 = new DataGridMeta();
        dgm2.allocate(1);
        dgm2.setFieldName(new String[]{ "moreData" });
        dgm2.setFieldType(new String[]{ ValueMetaFactory.getValueMetaName(TYPE_STRING) });
        List<List<String>> dgm2Data = new ArrayList<>();
        dgm2Data.add(Arrays.asList("Some Informational Data"));
        dgm2.setDataLines(dgm2Data);
        StepMeta dg1 = new StepMeta("input1", dgm1);
        StepMeta dg2 = new StepMeta("input2", dgm2);
        final String UDJC_METHOD = "public boolean processRow( StepMetaInterface smi, StepDataInterface sdi ) throws KettleException { return " + "false; }";
        UserDefinedJavaClassMeta udjcMeta = new UserDefinedJavaClassMeta();
        udjcMeta.getInfoStepDefinitions().add(new org.pentaho.di.trans.steps.userdefinedjavaclass.InfoStepDefinition(dg2.getName(), dg2.getName(), dg2, "info_data"));
        udjcMeta.replaceDefinitions(Collections.singletonList(new org.pentaho.di.trans.steps.userdefinedjavaclass.UserDefinedJavaClassDef(ClassType.TRANSFORM_CLASS, "MainClass", UDJC_METHOD)));
        StepMeta udjc = new StepMeta("PDI-14910", udjcMeta);
        TransHopMeta hop1 = new TransHopMeta(dg1, udjc, true);
        TransHopMeta hop2 = new TransHopMeta(dg2, udjc, true);
        transMeta.addStep(dg1);
        transMeta.addStep(dg2);
        transMeta.addStep(udjc);
        transMeta.addTransHop(hop1);
        transMeta.addTransHop(hop2);
        RowMetaInterface row = null;
        row = transMeta.getPrevInfoFields(udjc);
        Assert.assertNotNull(row);
        Assert.assertEquals(1, row.size());
        Assert.assertEquals("moreData", row.getValueMeta(0).getName());
        Assert.assertEquals(TYPE_STRING, row.getValueMeta(0).getType());
    }

    @Test
    public void testAddStepWithChangeListenerInterface() {
        StepMeta stepMeta = Mockito.mock(StepMeta.class);
        TransMetaTest.StepMetaChangeListenerInterfaceMock metaInterface = Mockito.mock(TransMetaTest.StepMetaChangeListenerInterfaceMock.class);
        Mockito.when(stepMeta.getStepMetaInterface()).thenReturn(metaInterface);
        Assert.assertEquals(0, transMeta.steps.size());
        Assert.assertEquals(0, transMeta.stepChangeListeners.size());
        // should not throw exception if there are no steps in step meta
        transMeta.addStep(0, stepMeta);
        Assert.assertEquals(1, transMeta.steps.size());
        Assert.assertEquals(1, transMeta.stepChangeListeners.size());
        transMeta.addStep(0, stepMeta);
        Assert.assertEquals(2, transMeta.steps.size());
        Assert.assertEquals(2, transMeta.stepChangeListeners.size());
    }

    @Test
    public void testIsAnySelectedStepUsedInTransHopsNothingSelectedCase() {
        List<StepMeta> selectedSteps = Arrays.asList(new StepMeta(), new StepMeta(), new StepMeta());
        transMeta.getSteps().addAll(selectedSteps);
        Assert.assertFalse(transMeta.isAnySelectedStepUsedInTransHops());
    }

    @Test
    public void testIsAnySelectedStepUsedInTransHopsAnySelectedCase() {
        StepMeta stepMeta = new StepMeta();
        stepMeta.setName(TransMetaTest.STEP_NAME);
        TransHopMeta transHopMeta = new TransHopMeta();
        stepMeta.setSelected(true);
        List<StepMeta> selectedSteps = Arrays.asList(new StepMeta(), stepMeta, new StepMeta());
        transHopMeta.setToStep(stepMeta);
        transHopMeta.setFromStep(stepMeta);
        transMeta.getSteps().addAll(selectedSteps);
        transMeta.addTransHop(transHopMeta);
        Assert.assertTrue(transMeta.isAnySelectedStepUsedInTransHops());
    }

    @Test
    public void testCloneWithParam() throws Exception {
        TransMeta transMeta = new TransMeta("transFile", "myTrans");
        transMeta.addParameterDefinition("key", "defValue", "description");
        Object clone = transMeta.realClone(true);
        Assert.assertNotNull(clone);
    }

    public abstract static class StepMetaChangeListenerInterfaceMock implements StepMetaChangeListenerInterface , StepMetaInterface {
        @Override
        public abstract Object clone();
    }

    @Test
    public void testLoadXml() throws KettleException {
        String directory = "/home/admin";
        Node jobNode = Mockito.mock(Node.class);
        NodeList nodeList = new NodeList() {
            ArrayList<Node> nodes = new ArrayList<>();

            {
                Node nodeInfo = Mockito.mock(Node.class);
                Mockito.when(nodeInfo.getNodeName()).thenReturn(XML_TAG_INFO);
                Mockito.when(nodeInfo.getChildNodes()).thenReturn(this);
                Node nodeDirectory = Mockito.mock(Node.class);
                Mockito.when(nodeDirectory.getNodeName()).thenReturn("directory");
                Node child = Mockito.mock(Node.class);
                Mockito.when(nodeDirectory.getFirstChild()).thenReturn(child);
                Mockito.when(child.getNodeValue()).thenReturn(directory);
                nodes.add(nodeDirectory);
                nodes.add(nodeInfo);
            }

            @Override
            public Node item(int index) {
                return nodes.get(index);
            }

            @Override
            public int getLength() {
                return nodes.size();
            }
        };
        Mockito.when(jobNode.getChildNodes()).thenReturn(nodeList);
        Repository rep = Mockito.mock(Repository.class);
        RepositoryDirectory repDirectory = new RepositoryDirectory(new RepositoryDirectory(new RepositoryDirectory(), "home"), "admin");
        Mockito.when(rep.findDirectory(Mockito.eq(directory))).thenReturn(repDirectory);
        TransMeta meta = new TransMeta();
        VariableSpace variableSpace = Mockito.mock(VariableSpace.class);
        Mockito.when(variableSpace.listVariables()).thenReturn(new String[0]);
        meta.loadXML(jobNode, null, Mockito.mock(IMetaStore.class), rep, false, variableSpace, Mockito.mock(OverwritePrompter.class));
        meta.setInternalKettleVariables(null);
        Assert.assertEquals(repDirectory.getPath(), meta.getVariable(INTERNAL_VARIABLE_TRANSFORMATION_REPOSITORY_DIRECTORY));
    }

    @Test
    public void testTransWithOneStepIsConsideredUsed() throws Exception {
        TransMeta transMeta = new TransMeta(getClass().getResource("one-step-trans.ktr").getPath());
        Assert.assertEquals(1, transMeta.getUsedSteps().size());
        Repository rep = Mockito.mock(Repository.class);
        ProgressMonitorListener monitor = Mockito.mock(ProgressMonitorListener.class);
        List<CheckResultInterface> remarks = new ArrayList<>();
        IMetaStore metaStore = Mockito.mock(IMetaStore.class);
        transMeta.checkSteps(remarks, false, monitor, new Variables(), rep, metaStore);
        Assert.assertEquals(4, remarks.size());
        for (CheckResultInterface remark : remarks) {
            Assert.assertEquals(TYPE_RESULT_OK, remark.getType());
        }
    }

    @Test
    public void testGetCacheVersion() throws Exception {
        TransMeta transMeta = new TransMeta(getClass().getResource("one-step-trans.ktr").getPath());
        int oldCacheVersion = transMeta.getCacheVersion();
        transMeta.setSizeRowset(10);
        int currCacheVersion = transMeta.getCacheVersion();
        Assert.assertNotEquals(oldCacheVersion, currCacheVersion);
    }

    @Test
    public void testGetCacheVersionWithIrrelevantParameters() throws Exception {
        TransMeta transMeta = new TransMeta(getClass().getResource("one-step-trans.ktr").getPath());
        int oldCacheVersion = transMeta.getCacheVersion();
        int currCacheVersion;
        transMeta.setSizeRowset(1000);
        currCacheVersion = transMeta.getCacheVersion();
        Assert.assertNotEquals(oldCacheVersion, currCacheVersion);
        oldCacheVersion = currCacheVersion;
        // scenarios that should not impact the cache version
        // transformation description
        transMeta.setDescription("transformation description");
        // transformation status
        transMeta.setTransstatus(100);
        // transformation log table
        transMeta.setTransLogTable(Mockito.mock(TransLogTable.class));
        // transformation created user
        transMeta.setCreatedUser("user");
        // transformation modified user
        transMeta.setModifiedUser("user");
        // transformation created date
        transMeta.setCreatedDate(new Date());
        // transformation modified date
        transMeta.setModifiedDate(new Date());
        // transformation is key private flag
        transMeta.setPrivateKey(false);
        // transformation attributes
        Map<String, String> attributes = new HashMap<>();
        attributes.put("key", "value");
        transMeta.setAttributes("group", attributes);
        // step description
        StepMeta stepMeta = transMeta.getStep(0);
        stepMeta.setDescription("stepDescription");
        // step position
        stepMeta.setLocation(10, 20);
        stepMeta.setLocation(new Point(30, 40));
        // step type id
        stepMeta.setStepID("Dummy");
        // step is distributed flag
        stepMeta.setDistributes(false);
        // step copies
        stepMeta.setCopies(5);
        // step partitioning meta
        stepMeta.setStepPartitioningMeta(Mockito.mock(StepPartitioningMeta.class));
        // assert that nothing impacted the cache version
        Assert.assertEquals(oldCacheVersion, transMeta.getCacheVersion());
    }

    @Test
    public void testGetPrevStepFields() throws KettleStepException {
        DataGridMeta dgm = new DataGridMeta();
        dgm.allocate(2);
        dgm.setFieldName(new String[]{ "id" });
        dgm.setFieldType(new String[]{ ValueMetaFactory.getValueMetaName(TYPE_INTEGER) });
        List<List<String>> dgm1Data = new ArrayList<>();
        dgm1Data.add(Collections.singletonList("1"));
        dgm1Data.add(Collections.singletonList("2"));
        dgm.setDataLines(dgm1Data);
        DataGridMeta dgm2 = new DataGridMeta();
        dgm2.allocate(2);
        dgm2.setFieldName(new String[]{ "foo" });
        dgm2.setFieldType(new String[]{ ValueMetaFactory.getValueMetaName(TYPE_STRING) });
        List<List<String>> dgm1Data2 = new ArrayList<>();
        dgm1Data2.add(Collections.singletonList("3"));
        dgm1Data2.add(Collections.singletonList("4"));
        dgm2.setDataLines(dgm1Data2);
        StepMeta dg = new StepMeta("input1", dgm);
        StepMeta dg2 = new StepMeta("input2", dgm2);
        TextFileOutputMeta textFileOutputMeta = new TextFileOutputMeta();
        StepMeta textFileOutputStep = new StepMeta("BACKLOG-21039", textFileOutputMeta);
        TransHopMeta hop = new TransHopMeta(dg, textFileOutputStep, true);
        TransHopMeta hop2 = new TransHopMeta(dg2, textFileOutputStep, true);
        transMeta.addStep(dg);
        transMeta.addStep(dg2);
        transMeta.addStep(textFileOutputStep);
        transMeta.addTransHop(hop);
        transMeta.addTransHop(hop2);
        RowMetaInterface allRows = transMeta.getPrevStepFields(textFileOutputStep, null, null);
        Assert.assertNotNull(allRows);
        Assert.assertEquals(2, allRows.size());
        Assert.assertEquals("id", allRows.getValueMeta(0).getName());
        Assert.assertEquals("foo", allRows.getValueMeta(1).getName());
        Assert.assertEquals(TYPE_INTEGER, allRows.getValueMeta(0).getType());
        Assert.assertEquals(TYPE_STRING, allRows.getValueMeta(1).getType());
        RowMetaInterface rows1 = transMeta.getPrevStepFields(textFileOutputStep, "input1", null);
        Assert.assertNotNull(rows1);
        Assert.assertEquals(1, rows1.size());
        Assert.assertEquals("id", rows1.getValueMeta(0).getName());
        Assert.assertEquals(TYPE_INTEGER, rows1.getValueMeta(0).getType());
        RowMetaInterface rows2 = transMeta.getPrevStepFields(textFileOutputStep, "input2", null);
        Assert.assertNotNull(rows2);
        Assert.assertEquals(1, rows2.size());
        Assert.assertEquals("foo", rows2.getValueMeta(0).getName());
        Assert.assertEquals(TYPE_STRING, rows2.getValueMeta(0).getType());
        dgm.setFieldName(new String[]{ "id", "name" });
        dgm.setFieldType(new String[]{ ValueMetaFactory.getValueMetaName(TYPE_INTEGER), ValueMetaFactory.getValueMetaName(TYPE_STRING) });
        allRows = transMeta.getPrevStepFields(textFileOutputStep, null, null);
        Assert.assertNotNull(allRows);
        Assert.assertEquals(3, allRows.size());
        Assert.assertEquals("id", allRows.getValueMeta(0).getName());
        Assert.assertEquals("name", allRows.getValueMeta(1).getName());
        Assert.assertEquals("foo", allRows.getValueMeta(2).getName());
        Assert.assertEquals(TYPE_INTEGER, allRows.getValueMeta(0).getType());
        Assert.assertEquals(TYPE_STRING, allRows.getValueMeta(1).getType());
        Assert.assertEquals(TYPE_STRING, allRows.getValueMeta(2).getType());
        rows1 = transMeta.getPrevStepFields(textFileOutputStep, "input1", null);
        Assert.assertNotNull(rows1);
        Assert.assertEquals(2, rows1.size());
        Assert.assertEquals("id", rows1.getValueMeta(0).getName());
        Assert.assertEquals("name", rows1.getValueMeta(1).getName());
        Assert.assertEquals(TYPE_INTEGER, rows1.getValueMeta(0).getType());
        Assert.assertEquals(TYPE_STRING, rows1.getValueMeta(1).getType());
    }

    @Test
    public void testHasLoop_simpleLoop() throws Exception {
        // main->2->3->main
        TransMeta transMetaSpy = Mockito.spy(transMeta);
        StepMeta stepMetaMain = createStepMeta("mainStep");
        StepMeta stepMeta2 = createStepMeta("step2");
        StepMeta stepMeta3 = createStepMeta("step3");
        List<StepMeta> mainPrevSteps = new ArrayList<>();
        mainPrevSteps.add(stepMeta2);
        Mockito.doReturn(mainPrevSteps).when(transMetaSpy).findPreviousSteps(stepMetaMain, true);
        Mockito.when(transMetaSpy.findNrPrevSteps(stepMetaMain)).thenReturn(1);
        Mockito.when(transMetaSpy.findPrevStep(stepMetaMain, 0)).thenReturn(stepMeta2);
        List<StepMeta> stepmeta2PrevSteps = new ArrayList<>();
        stepmeta2PrevSteps.add(stepMeta3);
        Mockito.doReturn(stepmeta2PrevSteps).when(transMetaSpy).findPreviousSteps(stepMeta2, true);
        Mockito.when(transMetaSpy.findNrPrevSteps(stepMeta2)).thenReturn(1);
        Mockito.when(transMetaSpy.findPrevStep(stepMeta2, 0)).thenReturn(stepMeta3);
        List<StepMeta> stepmeta3PrevSteps = new ArrayList<>();
        stepmeta3PrevSteps.add(stepMetaMain);
        Mockito.doReturn(stepmeta3PrevSteps).when(transMetaSpy).findPreviousSteps(stepMeta3, true);
        Mockito.when(transMetaSpy.findNrPrevSteps(stepMeta3)).thenReturn(1);
        Mockito.when(transMetaSpy.findPrevStep(stepMeta3, 0)).thenReturn(stepMetaMain);
        Assert.assertTrue(transMetaSpy.hasLoop(stepMetaMain));
    }

    @Test
    public void testHasLoop_loopInPrevSteps() throws Exception {
        // main->2->3->4->3
        TransMeta transMetaSpy = Mockito.spy(transMeta);
        StepMeta stepMetaMain = createStepMeta("mainStep");
        StepMeta stepMeta2 = createStepMeta("step2");
        StepMeta stepMeta3 = createStepMeta("step3");
        StepMeta stepMeta4 = createStepMeta("step4");
        Mockito.when(transMetaSpy.findNrPrevSteps(stepMetaMain)).thenReturn(1);
        Mockito.when(transMetaSpy.findPrevStep(stepMetaMain, 0)).thenReturn(stepMeta2);
        Mockito.when(transMetaSpy.findNrPrevSteps(stepMeta2)).thenReturn(1);
        Mockito.when(transMetaSpy.findPrevStep(stepMeta2, 0)).thenReturn(stepMeta3);
        Mockito.when(transMetaSpy.findNrPrevSteps(stepMeta3)).thenReturn(1);
        Mockito.when(transMetaSpy.findPrevStep(stepMeta3, 0)).thenReturn(stepMeta4);
        Mockito.when(transMetaSpy.findNrPrevSteps(stepMeta4)).thenReturn(1);
        Mockito.when(transMetaSpy.findPrevStep(stepMeta4, 0)).thenReturn(stepMeta3);
        // check no StackOverflow error
        Assert.assertFalse(transMetaSpy.hasLoop(stepMetaMain));
    }

    @Test
    public void infoStepFieldsAreNotIncludedInGetStepFields() throws KettleStepException {
        // validates that the fields from info steps are not included in the resulting step fields for a stepMeta.
        // This is important with steps like StreamLookup and Append, where the previous steps may or may not
        // have their fields included in the current step.
        TransMeta transMeta = new TransMeta(new Variables());
        StepMeta toBeAppended1 = // no info steps
        // names of fields from this step
        testStep("toBeAppended1", Collections.emptyList(), Arrays.asList("field1", "field2"));
        StepMeta toBeAppended2 = testStep("toBeAppended2", Collections.emptyList(), Arrays.asList("field1", "field2"));
        StepMeta append = // info step names
        // output field of this step
        testStep("append", Arrays.asList("toBeAppended1", "toBeAppended2"), Collections.singletonList("outputField"));
        StepMeta after = new StepMeta("after", new DummyTransMeta());
        wireUpTestTransMeta(transMeta, toBeAppended1, toBeAppended2, append, after);
        RowMetaInterface results = transMeta.getStepFields(append, after, Mockito.mock(ProgressMonitorListener.class));
        Assert.assertThat(1, CoreMatchers.equalTo(results.size()));
        Assert.assertThat("outputField", CoreMatchers.equalTo(results.getFieldNames()[0]));
    }

    @Test
    public void prevStepFieldsAreIncludedInGetStepFields() throws KettleStepException {
        TransMeta transMeta = new TransMeta(new Variables());
        StepMeta prevStep1 = testStep("prevStep1", Collections.emptyList(), Arrays.asList("field1", "field2"));
        StepMeta prevStep2 = testStep("prevStep2", Collections.emptyList(), Arrays.asList("field3", "field4", "field5"));
        StepMeta someStep = testStep("step", Arrays.asList("prevStep1"), Arrays.asList("outputField"));
        StepMeta after = new StepMeta("after", new DummyTransMeta());
        wireUpTestTransMeta(transMeta, prevStep1, prevStep2, someStep, after);
        RowMetaInterface results = transMeta.getStepFields(someStep, after, Mockito.mock(ProgressMonitorListener.class));
        Assert.assertThat(4, CoreMatchers.equalTo(results.size()));
        Assert.assertThat(new String[]{ "field3", "field4", "field5", "outputField" }, CoreMatchers.equalTo(results.getFieldNames()));
    }

    @Test
    public void findPreviousStepsNullMeta() {
        TransMeta transMeta = new TransMeta(new Variables());
        List<StepMeta> result = transMeta.findPreviousSteps(null, false);
        Assert.assertThat(0, CoreMatchers.equalTo(result.size()));
        Assert.assertThat(result, CoreMatchers.equalTo(new ArrayList()));
    }

    @Test
    public void testSetInternalEntryCurrentDirectoryWithFilename() {
        TransMeta transMetaTest = new TransMeta();
        transMetaTest.setFilename("hasFilename");
        transMetaTest.setVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY, "Original value defined at run execution");
        transMetaTest.setVariable(INTERNAL_VARIABLE_TRANSFORMATION_FILENAME_DIRECTORY, "file:///C:/SomeFilenameDirectory");
        transMetaTest.setVariable(INTERNAL_VARIABLE_TRANSFORMATION_REPOSITORY_DIRECTORY, "/SomeRepDirectory");
        transMetaTest.setInternalEntryCurrentDirectory();
        Assert.assertEquals("file:///C:/SomeFilenameDirectory", transMetaTest.getVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY));
    }

    @Test
    public void testSetInternalEntryCurrentDirectoryWithRepository() {
        TransMeta transMetaTest = new TransMeta();
        RepositoryDirectoryInterface path = Mockito.mock(RepositoryDirectoryInterface.class);
        Mockito.when(path.getPath()).thenReturn("aPath");
        transMetaTest.setRepository(Mockito.mock(Repository.class));
        transMetaTest.setRepositoryDirectory(path);
        transMetaTest.setVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY, "Original value defined at run execution");
        transMetaTest.setVariable(INTERNAL_VARIABLE_TRANSFORMATION_FILENAME_DIRECTORY, "file:///C:/SomeFilenameDirectory");
        transMetaTest.setVariable(INTERNAL_VARIABLE_TRANSFORMATION_REPOSITORY_DIRECTORY, "/SomeRepDirectory");
        transMetaTest.setInternalEntryCurrentDirectory();
        Assert.assertEquals("/SomeRepDirectory", transMetaTest.getVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY));
    }

    @Test
    public void testSetInternalEntryCurrentDirectoryWithoutFilenameOrRepository() {
        TransMeta transMetaTest = new TransMeta();
        transMetaTest.setVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY, "Original value defined at run execution");
        transMetaTest.setVariable(INTERNAL_VARIABLE_TRANSFORMATION_FILENAME_DIRECTORY, "file:///C:/SomeFilenameDirectory");
        transMetaTest.setVariable(INTERNAL_VARIABLE_TRANSFORMATION_REPOSITORY_DIRECTORY, "/SomeRepDirectory");
        transMetaTest.setInternalEntryCurrentDirectory();
        Assert.assertEquals("Original value defined at run execution", transMetaTest.getVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY));
    }
}

