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
package org.pentaho.di.trans.steps.metainject;


import LogLevel.ERROR;
import ValueMetaInterface.TYPE_BIGNUMBER;
import ValueMetaInterface.TYPE_BOOLEAN;
import ValueMetaInterface.TYPE_DATE;
import ValueMetaInterface.TYPE_INTEGER;
import ValueMetaInterface.TYPE_NUMBER;
import ValueMetaInterface.TYPE_STRING;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.injection.Injection;
import org.pentaho.di.core.injection.InjectionSupported;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBigNumber;
import org.pentaho.di.core.row.value.ValueMetaBoolean;
import org.pentaho.di.core.row.value.ValueMetaDate;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaNumber;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectory;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInjectionMetaEntry;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInjectionInterface;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ MetaInject.class })
public class MetaInjectTest {
    private static final String INJECTOR_STEP_NAME = "TEST_STEP_FOR_INJECTION";

    private static final String TEST_VALUE = "TEST_VALUE";

    private static final String TEST_VARIABLE = "TEST_VARIABLE";

    private static final String TEST_PARAMETER = "TEST_PARAMETER";

    private static final String TEST_TARGET_STEP_NAME = "TEST_TARGET_STEP_NAME";

    private static final String TEST_SOURCE_STEP_NAME = "TEST_SOURCE_STEP_NAME";

    private static final String TEST_ATTR_VALUE = "TEST_ATTR_VALUE";

    private static final String TEST_FIELD = "TEST_FIELD";

    private static final String UNAVAILABLE_STEP = "UNAVAILABLE_STEP";

    private static final TargetStepAttribute UNAVAILABLE_TARGET_STEP = new TargetStepAttribute(MetaInjectTest.UNAVAILABLE_STEP, MetaInjectTest.TEST_ATTR_VALUE, false);

    private static final SourceStepField UNAVAILABLE_SOURCE_STEP = new SourceStepField(MetaInjectTest.UNAVAILABLE_STEP, MetaInjectTest.TEST_FIELD);

    private MetaInject metaInject;

    private Repository repository;

    private MetaInjectMeta meta;

    private MetaInjectData data;

    private TransMeta transMeta;

    private Trans trans;

    private StepMetaInjectionInterface metaInjectionInterface;

    private IMetaStore metaStore;

    @Test
    public void injectMetaFromMultipleInputSteps() throws KettleException {
        Map<TargetStepAttribute, SourceStepField> targetSourceMapping = new LinkedHashMap<TargetStepAttribute, SourceStepField>();
        targetSourceMapping.put(new TargetStepAttribute(MetaInjectTest.INJECTOR_STEP_NAME, "DATA_TYPE", true), new SourceStepField("TYPE_INPUT", "col_type"));
        targetSourceMapping.put(new TargetStepAttribute(MetaInjectTest.INJECTOR_STEP_NAME, "NAME", true), new SourceStepField("NAME_INPUT", "col_name"));
        meta.setTargetSourceMapping(targetSourceMapping);
        Mockito.doReturn(new String[]{ "NAME_INPUT", "TYPE_INPUT" }).when(transMeta).getPrevStepNames(ArgumentMatchers.any(StepMeta.class));
        RowSet nameInputRowSet = Mockito.mock(RowSet.class);
        RowMeta nameRowMeta = new RowMeta();
        nameRowMeta.addValueMeta(new ValueMetaString("col_name"));
        Mockito.doReturn(nameRowMeta).when(nameInputRowSet).getRowMeta();
        Mockito.doReturn(nameInputRowSet).when(metaInject).findInputRowSet("NAME_INPUT");
        RowSet typeInputRowSet = Mockito.mock(RowSet.class);
        RowMeta typeRowMeta = new RowMeta();
        typeRowMeta.addValueMeta(new ValueMetaString("col_type"));
        Mockito.doReturn(typeRowMeta).when(typeInputRowSet).getRowMeta();
        Mockito.doReturn(typeInputRowSet).when(metaInject).findInputRowSet("TYPE_INPUT");
        Mockito.doReturn(new Object[]{ "FIRST_NAME" }).doReturn(null).when(metaInject).getRowFrom(nameInputRowSet);
        Mockito.doReturn(new Object[]{ "String" }).doReturn(null).when(metaInject).getRowFrom(typeInputRowSet);
        List<StepInjectionMetaEntry> injectionMetaEntryList = new ArrayList<StepInjectionMetaEntry>();
        StepInjectionMetaEntry fields = new StepInjectionMetaEntry("FIELDS", ValueMetaInterface.TYPE_NONE, "");
        StepInjectionMetaEntry fieldEntry = new StepInjectionMetaEntry("FIELD", ValueMetaInterface.TYPE_NONE, "");
        fields.getDetails().add(fieldEntry);
        StepInjectionMetaEntry nameEntry = new StepInjectionMetaEntry("NAME", ValueMetaInterface.TYPE_STRING, "");
        fieldEntry.getDetails().add(nameEntry);
        StepInjectionMetaEntry dataEntry = new StepInjectionMetaEntry("DATA_TYPE", ValueMetaInterface.TYPE_STRING, "");
        fieldEntry.getDetails().add(dataEntry);
        injectionMetaEntryList.add(fields);
        Mockito.doReturn(injectionMetaEntryList).when(metaInjectionInterface).getStepInjectionMetadataEntries();
        meta.setNoExecution(true);
        Assert.assertTrue(metaInject.init(meta, data));
        metaInject.processRow(meta, data);
        StepInjectionMetaEntry expectedNameEntry = new StepInjectionMetaEntry("NAME", "FIRST_NAME", ValueMetaInterface.TYPE_STRING, "");
        StepInjectionMetaEntry expectedDataEntry = new StepInjectionMetaEntry("DATA_TYPE", "String", ValueMetaInterface.TYPE_STRING, "");
        Mockito.verify(metaInject, Mockito.atLeastOnce()).setEntryValueIfFieldExists(ArgumentMatchers.refEq(expectedNameEntry), ArgumentMatchers.any(RowMetaAndData.class), ArgumentMatchers.any(SourceStepField.class));
        Mockito.verify(metaInject, Mockito.atLeastOnce()).setEntryValueIfFieldExists(ArgumentMatchers.refEq(expectedDataEntry), ArgumentMatchers.any(RowMetaAndData.class), ArgumentMatchers.any(SourceStepField.class));
    }

    @Test
    public void testMetastoreIsSet() throws Exception {
        Mockito.doReturn(new String[]{  }).when(transMeta).getPrevStepNames(ArgumentMatchers.any(StepMeta.class));
        data.stepInjectionMetasMap = new HashMap();
        data.stepInjectionMap = new HashMap();
        data.transMeta = new TransMeta();
        meta.setNoExecution(false);
        Mockito.doReturn(ERROR).when(metaInject).getLogLevel();
        // don't need to actually run anything to verify this. force it to "stopped"
        Mockito.doReturn(true).when(metaInject).isStopped();
        Mockito.doNothing().when(metaInject).waitUntilFinished(ArgumentMatchers.any(Trans.class));
        // make sure the injected tranformation doesn't have a metastore first
        Assert.assertNull(data.transMeta.getMetaStore());
        metaInject.processRow(meta, data);
        // now it should be set
        Assert.assertEquals(metaStore, data.transMeta.getMetaStore());
    }

    @Test
    public void testTransWaitsForListenersToFinish() throws Exception {
        Mockito.doReturn(new String[]{  }).when(transMeta).getPrevStepNames(ArgumentMatchers.any(StepMeta.class));
        data.stepInjectionMetasMap = new HashMap();
        data.stepInjectionMap = new HashMap();
        data.transMeta = new TransMeta();
        meta.setNoExecution(false);
        Trans injectTrans = Mockito.mock(Trans.class);
        Mockito.doReturn(injectTrans).when(metaInject).createInjectTrans();
        Mockito.when(injectTrans.isFinished()).thenReturn(true);
        Result result = Mockito.mock(Result.class);
        Mockito.when(injectTrans.getResult()).thenReturn(result);
        metaInject.processRow(meta, data);
        Mockito.verify(injectTrans).waitUntilFinished();
    }

    @Test
    public void transVariablesPassedToChildTransformation() throws KettleException {
        Mockito.doReturn(new String[]{ MetaInjectTest.TEST_VARIABLE }).when(metaInject).listVariables();
        Mockito.doReturn(MetaInjectTest.TEST_VALUE).when(metaInject).getVariable(MetaInjectTest.TEST_VARIABLE);
        TransMeta transMeta = new TransMeta();
        Mockito.doReturn(transMeta).when(metaInject).getTransMeta();
        TransMeta internalTransMeta = new TransMeta();
        Mockito.doReturn(internalTransMeta).when(metaInject).loadTransformationMeta();
        Assert.assertTrue(metaInject.init(meta, data));
        Assert.assertEquals(MetaInjectTest.TEST_VALUE, internalTransMeta.getVariable(MetaInjectTest.TEST_VARIABLE));
    }

    @Test
    public void transParametersPassedToChildTransformation() throws KettleException {
        Trans trans = new Trans();
        trans.addParameterDefinition(MetaInjectTest.TEST_PARAMETER, "TEST_DEF_VALUE", "");
        trans.setParameterValue(MetaInjectTest.TEST_PARAMETER, MetaInjectTest.TEST_VALUE);
        Mockito.doReturn(trans).when(metaInject).getTrans();
        TransMeta internalTransMeta = new TransMeta();
        Mockito.doReturn(internalTransMeta).when(metaInject).loadTransformationMeta();
        Assert.assertTrue(metaInject.init(meta, data));
        Assert.assertEquals(MetaInjectTest.TEST_VALUE, internalTransMeta.getParameterValue(MetaInjectTest.TEST_PARAMETER));
    }

    @Test
    public void getUnavailableSourceSteps() {
        TargetStepAttribute targetStep = new TargetStepAttribute(MetaInjectTest.TEST_TARGET_STEP_NAME, MetaInjectTest.TEST_ATTR_VALUE, false);
        SourceStepField unavailableSourceStep = new SourceStepField(MetaInjectTest.UNAVAILABLE_STEP, MetaInjectTest.TEST_FIELD);
        Map<TargetStepAttribute, SourceStepField> targetMap = Collections.singletonMap(targetStep, unavailableSourceStep);
        TransMeta sourceTransMeta = Mockito.mock(TransMeta.class);
        Mockito.doReturn(new String[0]).when(sourceTransMeta).getPrevStepNames(ArgumentMatchers.any(StepMeta.class));
        Set<SourceStepField> actualSet = MetaInject.getUnavailableSourceSteps(targetMap, sourceTransMeta, Mockito.mock(StepMeta.class));
        Assert.assertTrue(actualSet.contains(unavailableSourceStep));
    }

    @Test
    public void getUnavailableTargetSteps() {
        TargetStepAttribute unavailableTargetStep = new TargetStepAttribute(MetaInjectTest.UNAVAILABLE_STEP, MetaInjectTest.TEST_ATTR_VALUE, false);
        SourceStepField sourceStep = new SourceStepField(MetaInjectTest.TEST_SOURCE_STEP_NAME, MetaInjectTest.TEST_FIELD);
        Map<TargetStepAttribute, SourceStepField> targetMap = Collections.singletonMap(unavailableTargetStep, sourceStep);
        TransMeta injectedTransMeta = Mockito.mock(TransMeta.class);
        Mockito.doReturn(Collections.emptyList()).when(injectedTransMeta).getUsedSteps();
        Set<TargetStepAttribute> actualSet = MetaInject.getUnavailableTargetSteps(targetMap, injectedTransMeta);
        Assert.assertTrue(actualSet.contains(unavailableTargetStep));
    }

    @Test
    public void removeUnavailableStepsFromMapping_unavailable_source_step() {
        TargetStepAttribute unavailableTargetStep = new TargetStepAttribute(MetaInjectTest.UNAVAILABLE_STEP, MetaInjectTest.TEST_ATTR_VALUE, false);
        SourceStepField unavailableSourceStep = new SourceStepField(MetaInjectTest.UNAVAILABLE_STEP, MetaInjectTest.TEST_FIELD);
        Map<TargetStepAttribute, SourceStepField> targetMap = new HashMap<TargetStepAttribute, SourceStepField>();
        targetMap.put(unavailableTargetStep, unavailableSourceStep);
        Set<SourceStepField> unavailableSourceSteps = Collections.singleton(MetaInjectTest.UNAVAILABLE_SOURCE_STEP);
        MetaInject.removeUnavailableStepsFromMapping(targetMap, unavailableSourceSteps, Collections.<TargetStepAttribute>emptySet());
        Assert.assertTrue(targetMap.isEmpty());
    }

    @Test
    public void removeUnavailableStepsFromMapping_unavailable_target_step() {
        TargetStepAttribute unavailableTargetStep = new TargetStepAttribute(MetaInjectTest.UNAVAILABLE_STEP, MetaInjectTest.TEST_ATTR_VALUE, false);
        SourceStepField unavailableSourceStep = new SourceStepField(MetaInjectTest.UNAVAILABLE_STEP, MetaInjectTest.TEST_FIELD);
        Map<TargetStepAttribute, SourceStepField> targetMap = new HashMap<TargetStepAttribute, SourceStepField>();
        targetMap.put(unavailableTargetStep, unavailableSourceStep);
        Set<TargetStepAttribute> unavailableTargetSteps = Collections.singleton(MetaInjectTest.UNAVAILABLE_TARGET_STEP);
        MetaInject.removeUnavailableStepsFromMapping(targetMap, Collections.<SourceStepField>emptySet(), unavailableTargetSteps);
        Assert.assertTrue(targetMap.isEmpty());
    }

    @Test
    public void removeUnavailableStepsFromMapping_unavailable_source_target_step() {
        TargetStepAttribute unavailableTargetStep = new TargetStepAttribute(MetaInjectTest.UNAVAILABLE_STEP, MetaInjectTest.TEST_ATTR_VALUE, false);
        SourceStepField unavailableSourceStep = new SourceStepField(MetaInjectTest.UNAVAILABLE_STEP, MetaInjectTest.TEST_FIELD);
        Map<TargetStepAttribute, SourceStepField> targetMap = new HashMap<TargetStepAttribute, SourceStepField>();
        targetMap.put(unavailableTargetStep, unavailableSourceStep);
        Set<TargetStepAttribute> unavailableTargetSteps = Collections.singleton(MetaInjectTest.UNAVAILABLE_TARGET_STEP);
        Set<SourceStepField> unavailableSourceSteps = Collections.singleton(MetaInjectTest.UNAVAILABLE_SOURCE_STEP);
        MetaInject.removeUnavailableStepsFromMapping(targetMap, unavailableSourceSteps, unavailableTargetSteps);
        Assert.assertTrue(targetMap.isEmpty());
    }

    @Test
    public void setEntryValue_string() throws KettleValueException {
        StepInjectionMetaEntry entry = Mockito.mock(StepInjectionMetaEntry.class);
        Mockito.doReturn(TYPE_STRING).when(entry).getValueType();
        RowMetaAndData row = MetaInjectTest.createRowMetaAndData(new ValueMetaString(MetaInjectTest.TEST_FIELD), MetaInjectTest.TEST_VALUE);
        SourceStepField sourceField = new SourceStepField(MetaInjectTest.TEST_SOURCE_STEP_NAME, MetaInjectTest.TEST_FIELD);
        MetaInject.setEntryValue(entry, row, sourceField);
        Mockito.verify(entry).setValue(MetaInjectTest.TEST_VALUE);
    }

    @Test
    public void setEntryValue_boolean() throws KettleValueException {
        StepInjectionMetaEntry entry = Mockito.mock(StepInjectionMetaEntry.class);
        Mockito.doReturn(TYPE_BOOLEAN).when(entry).getValueType();
        RowMetaAndData row = MetaInjectTest.createRowMetaAndData(new ValueMetaBoolean(MetaInjectTest.TEST_FIELD), true);
        SourceStepField sourceField = new SourceStepField(MetaInjectTest.TEST_SOURCE_STEP_NAME, MetaInjectTest.TEST_FIELD);
        MetaInject.setEntryValue(entry, row, sourceField);
        Mockito.verify(entry).setValue(true);
    }

    @Test
    public void setEntryValue_integer() throws KettleValueException {
        StepInjectionMetaEntry entry = Mockito.mock(StepInjectionMetaEntry.class);
        Mockito.doReturn(TYPE_INTEGER).when(entry).getValueType();
        RowMetaAndData row = MetaInjectTest.createRowMetaAndData(new ValueMetaInteger(MetaInjectTest.TEST_FIELD), new Long(1));
        SourceStepField sourceField = new SourceStepField(MetaInjectTest.TEST_SOURCE_STEP_NAME, MetaInjectTest.TEST_FIELD);
        MetaInject.setEntryValue(entry, row, sourceField);
        Mockito.verify(entry).setValue(1L);
    }

    @Test
    public void setEntryValue_number() throws KettleValueException {
        StepInjectionMetaEntry entry = Mockito.mock(StepInjectionMetaEntry.class);
        Mockito.doReturn(TYPE_NUMBER).when(entry).getValueType();
        RowMetaAndData row = MetaInjectTest.createRowMetaAndData(new ValueMetaNumber(MetaInjectTest.TEST_FIELD), new Double(1));
        SourceStepField sourceField = new SourceStepField(MetaInjectTest.TEST_SOURCE_STEP_NAME, MetaInjectTest.TEST_FIELD);
        MetaInject.setEntryValue(entry, row, sourceField);
        Mockito.verify(entry).setValue(1.0);
    }

    @Test
    public void setEntryValue_date() throws KettleValueException {
        StepInjectionMetaEntry entry = Mockito.mock(StepInjectionMetaEntry.class);
        Mockito.doReturn(TYPE_DATE).when(entry).getValueType();
        RowMetaAndData row = MetaInjectTest.createRowMetaAndData(new ValueMetaDate(MetaInjectTest.TEST_FIELD), null);
        SourceStepField sourceField = new SourceStepField(MetaInjectTest.TEST_SOURCE_STEP_NAME, MetaInjectTest.TEST_FIELD);
        MetaInject.setEntryValue(entry, row, sourceField);
        Mockito.verify(entry).setValue(null);
    }

    @Test
    public void setEntryValue_bignumber() throws KettleValueException {
        StepInjectionMetaEntry entry = Mockito.mock(StepInjectionMetaEntry.class);
        Mockito.doReturn(TYPE_BIGNUMBER).when(entry).getValueType();
        RowMetaAndData row = MetaInjectTest.createRowMetaAndData(new ValueMetaBigNumber(MetaInjectTest.TEST_FIELD), new BigDecimal(1));
        SourceStepField sourceField = new SourceStepField(MetaInjectTest.TEST_SOURCE_STEP_NAME, MetaInjectTest.TEST_FIELD);
        MetaInject.setEntryValue(entry, row, sourceField);
        Mockito.verify(entry).setValue(new BigDecimal(1));
    }

    @Test
    public void convertToUpperCaseSet_null_array() {
        Set<String> actualResult = MetaInject.convertToUpperCaseSet(null);
        Assert.assertNotNull(actualResult);
        Assert.assertTrue(actualResult.isEmpty());
    }

    @Test
    public void convertToUpperCaseSet() {
        String[] input = new String[]{ "Test_Step", "test_step1" };
        Set<String> actualResult = MetaInject.convertToUpperCaseSet(input);
        Set<String> expectedResult = new HashSet<>();
        expectedResult.add("TEST_STEP");
        expectedResult.add("TEST_STEP1");
        Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testGetUnavailableTargetKeys() throws Exception {
        final String targetStepName = "injectable step name";
        TargetStepAttribute unavailableTargetAttr = new TargetStepAttribute(targetStepName, "NOT_THERE", false);
        TargetStepAttribute availableTargetAttr = new TargetStepAttribute(targetStepName, "THERE", false);
        SourceStepField sourceStep = new SourceStepField(MetaInjectTest.TEST_SOURCE_STEP_NAME, MetaInjectTest.TEST_FIELD);
        Map<TargetStepAttribute, SourceStepField> targetMap = new HashMap<>(2);
        targetMap.put(unavailableTargetAttr, sourceStep);
        targetMap.put(availableTargetAttr, sourceStep);
        StepMetaInterface smi = new MetaInjectTest.InjectableTestStepMeta();
        TransMeta transMeta = mockSingleStepTransMeta(targetStepName, smi);
        Set<TargetStepAttribute> unavailable = MetaInject.getUnavailableTargetKeys(targetMap, transMeta, Collections.<TargetStepAttribute>emptySet());
        Assert.assertEquals(1, unavailable.size());
        Assert.assertTrue(unavailable.contains(unavailableTargetAttr));
    }

    @Test
    public void testStepChangeListener() throws Exception {
        MetaInjectMeta mim = new MetaInjectMeta();
        StepMeta sm = new StepMeta("testStep", mim);
        try {
            transMeta.addOrReplaceStep(sm);
        } catch (Exception ex) {
            Assert.fail();
        }
    }

    @InjectionSupported(localizationPrefix = "", groups = "groups")
    private static class InjectableTestStepMeta extends BaseStepMeta implements StepMetaInterface {
        @Injection(name = "THERE")
        private String there;

        @Override
        public void setDefault() {
        }

        @Override
        public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
            return null;
        }

        @Override
        public StepDataInterface getStepData() {
            return null;
        }
    }

    @Test
    public void testWriteInjectedKtrNoRepo() throws Exception {
        PowerMockito.doNothing().when(metaInject, "writeInjectedKtrToRepo", "/home/admin/injected_trans.ktr");
        PowerMockito.doNothing().when(metaInject, "writeInjectedKtrToFs", "/home/admin/injected_trans.ktr");
        metaInject.setRepository(null);
        Whitebox.<String>invokeMethod(metaInject, "writeInjectedKtr", "/home/admin/injected_trans.ktr");
        PowerMockito.verifyPrivate(metaInject, Mockito.times(0)).invoke("writeInjectedKtrToRepo", "/home/admin/injected_trans.ktr");
        PowerMockito.verifyPrivate(metaInject, Mockito.times(1)).invoke("writeInjectedKtrToFs", ("/home/admin/injected_trans" + ".ktr"));
    }

    @Test
    public void testWriteInjectedKtrWithRepo() throws Exception {
        PowerMockito.doNothing().when(metaInject, "writeInjectedKtrToRepo", "/home/admin/injected_trans.ktr");
        PowerMockito.doNothing().when(metaInject, "writeInjectedKtrToFs", "/home/admin/injected_trans.ktr");
        metaInject.setRepository(repository);
        Whitebox.<String>invokeMethod(metaInject, "writeInjectedKtr", "/home/admin/injected_trans.ktr");
        PowerMockito.verifyPrivate(metaInject, Mockito.times(1)).invoke("writeInjectedKtrToRepo", "/home/admin/injected_trans.ktr");
        PowerMockito.verifyPrivate(metaInject, Mockito.times(0)).invoke("writeInjectedKtrToFs", "/home/admin/injected_trans.ktr");
    }

    @Test
    public void testWriteInjectedKtrToRepoSameDir() throws Exception {
        RepositoryDirectory rootDir = PowerMockito.spy(new RepositoryDirectory(null, "/"));
        RepositoryDirectory adminDir = PowerMockito.spy(new RepositoryDirectory(new RepositoryDirectory(new RepositoryDirectory(null, "/"), "home"), "admin"));
        TransMeta cloneMeta = PowerMockito.spy(((TransMeta) (data.transMeta.clone())));
        PowerMockito.doReturn(cloneMeta).when(data.transMeta).clone();
        PowerMockito.doReturn(adminDir).when(repository).createRepositoryDirectory(rootDir, "home/admin");
        PowerMockito.doReturn(adminDir).when(data.transMeta).getRepositoryDirectory();
        PowerMockito.whenNew(RepositoryDirectory.class).withArguments(null, "/").thenReturn(rootDir);
        metaInject.setRepository(repository);
        Whitebox.<String>invokeMethod(metaInject, "writeInjectedKtrToRepo", "/home/admin/injected_trans.ktr");
        Mockito.verify(repository, Mockito.times(1)).findDirectory("home/admin");
        Mockito.verify(repository, Mockito.times(1)).createRepositoryDirectory(rootDir, "home/admin");
        Mockito.verify(cloneMeta, Mockito.times(1)).setRepositoryDirectory(adminDir);
        Mockito.verify(cloneMeta, Mockito.times(1)).setObjectId(ArgumentMatchers.any(ObjectId.class));
        Mockito.verify(repository, Mockito.times(1)).save(cloneMeta, null, null, true);
    }

    @Test
    public void testWriteInjectedKtrToRepoDifferentDir() throws Exception {
        RepositoryDirectory rootDir = PowerMockito.spy(new RepositoryDirectory(null, "/"));
        RepositoryDirectory adminDir = PowerMockito.spy(new RepositoryDirectory(new RepositoryDirectory(new RepositoryDirectory(null, "/"), "home"), "admin"));
        TransMeta cloneMeta = PowerMockito.spy(((TransMeta) (data.transMeta.clone())));
        PowerMockito.doReturn(cloneMeta).when(data.transMeta).clone();
        PowerMockito.doReturn(adminDir).when(repository).createRepositoryDirectory(rootDir, "/home/admin");
        PowerMockito.doReturn(adminDir).when(data.transMeta).getRepositoryDirectory();
        PowerMockito.whenNew(RepositoryDirectory.class).withArguments(null, "/").thenReturn(rootDir);
        metaInject.setRepository(repository);
        Whitebox.<String>invokeMethod(metaInject, "writeInjectedKtrToRepo", "injected_trans");
        Mockito.verify(repository, Mockito.times(0)).findDirectory(ArgumentMatchers.anyString());
        Mockito.verify(repository, Mockito.times(0)).createRepositoryDirectory(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(cloneMeta, Mockito.times(1)).setRepositoryDirectory(adminDir);
        Mockito.verify(cloneMeta, Mockito.times(1)).setObjectId(ArgumentMatchers.any(ObjectId.class));
        Mockito.verify(repository, Mockito.times(1)).save(cloneMeta, null, null, true);
    }
}

