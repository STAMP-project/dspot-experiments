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
package org.pentaho.di.trans.streaming.common;


import BaseStreamStepMeta.MappingMetaRetriever;
import CheckResultInterface.TYPE_RESULT_ERROR;
import ObjectLocationSpecificationMethod.FILENAME;
import ResourceEntry.ResourceType.ACTIONFILE;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.injection.Injection;
import org.pentaho.di.core.injection.InjectionSupported;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.logging.LogChannelInterfaceFactory;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.resource.ResourceReference;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import static org.pentaho.di.core.util.Assert.assertTrue;


@RunWith(MockitoJUnitRunner.class)
public class BaseStreamStepMetaTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private BaseStreamStepMeta meta;

    @Mock
    private LogChannelInterfaceFactory logChannelFactory;

    @Mock
    private LogChannelInterface logChannel;

    @Mock
    private RowMetaInterface rowMeta;

    @Mock
    private RowMetaInterface prevRowMeta;

    @Mock
    private StepMeta subTransStepMeta;

    @Mock
    private StepMeta nextStepMeta;

    @Mock
    private StepMetaInterface stepMetaInterface;

    @Mock
    private VariableSpace space;

    @Mock
    private Repository repo;

    @Mock
    private MappingMetaRetriever mappingMetaRetriever;

    @Mock
    private TransMeta subTransMeta;

    @Mock
    private TransMeta transMeta;

    @Step(id = "StuffStream", name = "Stuff Stream")
    @InjectionSupported(localizationPrefix = "stuff", groups = { "stuffGroup" })
    private static class StuffStreamMeta extends BaseStreamStepMeta {
        @Injection(name = "stuff", group = "stuffGroup")
        List<String> stuff = new ArrayList<>();

        // stuff needs to be mutable to support .add() for metadatainjection.
        // otherwise would use Arrays.asList();
        {
            stuff.add("one");
            stuff.add("two");
        }

        @Injection(name = "AUTH_PASSWORD")
        String password = "test";

        @Override
        public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
            return null;
        }

        @Override
        public StepDataInterface getStepData() {
            return null;
        }

        @Override
        public RowMeta getRowMeta(String origin, VariableSpace space) {
            return null;
        }
    }

    @Test
    public void testCheckErrorsOnZeroSizeAndDuration() {
        meta.setBatchDuration("0");
        meta.setBatchSize("0");
        ArrayList<CheckResultInterface> remarks = new ArrayList<>();
        meta.check(remarks, null, null, null, null, null, null, new Variables(), null, null);
        Assert.assertEquals(1, remarks.size());
        Assert.assertEquals(("The \"Number of records\" and \"Duration\" fields can\u2019t both be set to 0. Please set a value of 1 or higher " + "for one of the fields."), remarks.get(0).getText());
    }

    @Test
    public void testCheckErrorsOnNaN() {
        List<CheckResultInterface> remarks = new ArrayList<>();
        meta.setBatchDuration("blah");
        meta.setBatchSize("blah");
        meta.check(remarks, null, null, null, null, null, null, new Variables(), null, null);
        Assert.assertEquals(2, remarks.size());
        Assert.assertEquals(TYPE_RESULT_ERROR, remarks.get(0).getType());
        Assert.assertEquals("The \"Duration\" field is using a non-numeric value. Please set a numeric value.", remarks.get(0).getText());
        Assert.assertEquals(TYPE_RESULT_ERROR, remarks.get(1).getType());
        Assert.assertEquals("The \"Number of records\" field is using a non-numeric value. Please set a numeric value.", remarks.get(1).getText());
    }

    @Test
    public void testCheckErrorsOnVariables() {
        List<CheckResultInterface> remarks = new ArrayList<>();
        Variables space = new Variables();
        space.setVariable("something", "1000");
        meta.setBatchSize("${something}");
        meta.setBatchDuration("0");
        meta.check(remarks, null, null, null, null, null, null, space, null, null);
        Assert.assertEquals(0, remarks.size());
    }

    @Test
    public void testCheckErrorsOnSubStepName() {
        List<CheckResultInterface> remarks = new ArrayList<>();
        Variables space = new Variables();
        meta.setBatchSize("10");
        meta.setBatchDuration("10");
        meta.setSubStep("MissingStep");
        meta.check(remarks, null, null, null, null, null, null, space, null, null);
        Assert.assertEquals(1, remarks.size());
        Assert.assertEquals("Unable to complete \"null\".  Cannot return fields from \"MissingStep\" because it does not exist in the sub-transformation.", remarks.get(0).getText());
    }

    @Test
    public void testCheckErrorsOnVariablesSubstituteError() {
        List<CheckResultInterface> remarks = new ArrayList<>();
        Variables space = new Variables();
        space.setVariable("something", "0");
        meta.setBatchSize("${something}");
        meta.setBatchDuration("${something}");
        meta.check(remarks, null, null, null, null, null, null, space, null, null);
        Assert.assertEquals(1, remarks.size());
        Assert.assertEquals(("The \"Number of records\" and \"Duration\" fields can\u2019t both be set to 0. Please set a value of 1 " + "or higher for one of the fields."), remarks.get(0).getText());
        testRoundTrip(meta);
    }

    @Test
    public void testBasicRoundTrip() {
        meta.setBatchDuration("1000");
        meta.setBatchSize("100");
        meta.setTransformationPath("aPath");
        testRoundTrip(meta);
    }

    @Test
    public void testRoundTripInjectionList() {
        BaseStreamStepMetaTest.StuffStreamMeta startingMeta = new BaseStreamStepMetaTest.StuffStreamMeta();
        startingMeta.stuff = new ArrayList<>();
        startingMeta.stuff.add("foo");
        startingMeta.stuff.add("bar");
        startingMeta.stuff.add("baz");
        setBatchDuration("1000");
        setBatchSize("100");
        setTransformationPath("aPath");
        setParallelism("4");
        testRoundTrip(startingMeta);
    }

    @Test
    public void testSaveDefaultEmptyConnection() {
        BaseStreamStepMetaTest.StuffStreamMeta meta = new BaseStreamStepMetaTest.StuffStreamMeta();
        testRoundTrip(meta);
    }

    @Test
    public void testGetResourceDependencies() {
        String stepId = "KafkConsumerInput";
        String path = "/home/bgroves/fake.ktr";
        StepMeta stepMeta = new StepMeta();
        stepMeta.setStepID(stepId);
        BaseStreamStepMetaTest.StuffStreamMeta inputMeta = new BaseStreamStepMetaTest.StuffStreamMeta();
        List<ResourceReference> resourceDependencies = inputMeta.getResourceDependencies(new TransMeta(), stepMeta);
        Assert.assertEquals(0, resourceDependencies.get(0).getEntries().size());
        setTransformationPath(path);
        resourceDependencies = inputMeta.getResourceDependencies(new TransMeta(), stepMeta);
        Assert.assertEquals(1, resourceDependencies.get(0).getEntries().size());
        Assert.assertEquals(path, resourceDependencies.get(0).getEntries().get(0).getResource());
        Assert.assertEquals(ACTIONFILE, resourceDependencies.get(0).getEntries().get(0).getResourcetype());
        testRoundTrip(inputMeta);
    }

    @Test
    public void testReferencedObjectHasDescription() {
        BaseStreamStepMeta meta = new BaseStreamStepMetaTest.StuffStreamMeta();
        Assert.assertEquals(1, meta.getReferencedObjectDescriptions().length);
        assertTrue(((meta.getReferencedObjectDescriptions()[0]) != null));
        testRoundTrip(meta);
    }

    @Test
    public void testIsReferencedObjectEnabled() {
        BaseStreamStepMeta meta = new BaseStreamStepMetaTest.StuffStreamMeta();
        Assert.assertEquals(1, meta.isReferencedObjectEnabled().length);
        Assert.assertFalse(meta.isReferencedObjectEnabled()[0]);
        meta.setTransformationPath("/some/path");
        assertTrue(meta.isReferencedObjectEnabled()[0]);
        testRoundTrip(meta);
    }

    @Test
    public void testLoadReferencedObject() {
        BaseStreamStepMeta meta = new BaseStreamStepMetaTest.StuffStreamMeta();
        meta.setFileName(getClass().getResource("/org/pentaho/di/trans/subtrans-executor-sub.ktr").getPath());
        meta.setSpecificationMethod(FILENAME);
        try {
            TransMeta subTrans = ((TransMeta) (meta.loadReferencedObject(0, null, null, new Variables())));
            Assert.assertEquals("subtrans-executor-sub", subTrans.getName());
        } catch (KettleException e) {
            Assert.fail();
        }
        testRoundTrip(meta);
    }

    @Test
    public void testGetFieldsDoesEnvSubstitutionForSubStepName() throws KettleStepException {
        // https://jira.pentaho.com/browse/BACKLOG-22575
        BaseStreamStepMeta meta = new BaseStreamStepMetaTest.StuffStreamMeta();
        meta.setSubStep("${parameter}");
        when(space.environmentSubstitute("${parameter}")).thenReturn("realSubStepName");
        when(subTransStepMeta.getName()).thenReturn("realSubStepName");
        meta.mappingMetaRetriever = mappingMetaRetriever;
        meta.getFields(rowMeta, "origin", null, nextStepMeta, space, repo, null);
        Mockito.verify(space).environmentSubstitute("${parameter}");
        Mockito.verify(subTransMeta).getPrevStepFields("realSubStepName");
        Mockito.verify(stepMetaInterface).getFields(rowMeta, "origin", null, nextStepMeta, space, repo, null);
    }

    @Test
    public void replacingFileNameAlsoSetsTransformationPath() {
        BaseStreamStepMetaTest.StuffStreamMeta stuffStreamMeta = new BaseStreamStepMetaTest.StuffStreamMeta();
        replaceFileName("someName");
        Assert.assertEquals("someName", getTransformationPath());
    }

    @Test
    public void testGetFileName() {
        meta = new BaseStreamStepMetaTest.StuffStreamMeta();
        String testPathName = "transformationPathName";
        String testFileName = "testFileName";
        // verify that when the fileName is not set, we get the transformation path
        meta.setTransformationPath(testPathName);
        MatcherAssert.assertThat(meta.getFileName(), CoreMatchers.equalTo(testPathName));
        // verify that when the fileName is set, we get it
        meta.setFileName(testFileName);
        MatcherAssert.assertThat(meta.getFileName(), CoreMatchers.equalTo(testFileName));
    }
}

