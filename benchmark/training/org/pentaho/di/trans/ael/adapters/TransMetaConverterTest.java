/**
 * ! ******************************************************************************
 *
 *  Pentaho Data Integration
 *
 *  Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * *****************************************************************************
 */
package org.pentaho.di.trans.ael.adapters;


import Hop.TYPE_ERROR;
import Hop.TYPE_NORMAL;
import TransMetaConverter.SUB_TRANSFORMATIONS_KEY;
import TransMetaConverter.TRANS_DEFAULT_NAME;
import TransMetaConverter.TRANS_META_CONF_KEY;
import TransMetaConverter.TRANS_META_NAME_CONF_KEY;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleMissingPluginsException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.engine.api.model.Hop;
import org.pentaho.di.engine.api.model.Transformation;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectory;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.csvinput.CsvInputMeta;
import org.pentaho.di.trans.steps.dummytrans.DummyTransMeta;
import org.pentaho.di.trans.steps.tableinput.TableInputMeta;
import org.pentaho.di.workarounds.ResolvableResource;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


@RunWith(MockitoJUnitRunner.class)
public class TransMetaConverterTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Spy
    StepMetaInterface stepMetaInterface = new DummyTransMeta();

    final String XML = "<xml></xml>";

    @Test
    public void simpleConvert() {
        TransMeta meta = new TransMeta();
        meta.setFilename("fileName");
        meta.addStep(new StepMeta("stepName", stepMetaInterface));
        Transformation trans = TransMetaConverter.convert(meta);
        MatcherAssert.assertThat(trans.getId(), CoreMatchers.is(meta.getFilename()));
        MatcherAssert.assertThat(trans.getOperations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(trans.getOperations().get(0).getId(), CoreMatchers.is("stepName"));
    }

    @Test
    public void transWithHops() {
        TransMeta meta = new TransMeta();
        meta.setFilename("fileName");
        StepMeta from = new StepMeta("step1", stepMetaInterface);
        meta.addStep(from);
        StepMeta to = new StepMeta("step2", stepMetaInterface);
        meta.addStep(to);
        meta.addTransHop(new TransHopMeta(from, to));
        Transformation trans = TransMetaConverter.convert(meta);
        MatcherAssert.assertThat(trans.getId(), CoreMatchers.is(meta.getFilename()));
        MatcherAssert.assertThat(trans.getOperations().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(trans.getHops().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(trans.getHops().get(0).getFrom().getId(), CoreMatchers.is(from.getName()));
        MatcherAssert.assertThat(trans.getHops().get(0).getTo().getId(), CoreMatchers.is(to.getName()));
        MatcherAssert.assertThat(trans.getHops().stream().map(Hop::getType).collect(Collectors.toList()), CoreMatchers.everyItem(CoreMatchers.is(TYPE_NORMAL)));
    }

    @Test
    public void transIdFromRepo() throws Exception {
        TransMeta meta = new TransMeta();
        meta.setName("transName");
        Transformation trans = TransMetaConverter.convert(meta);
        MatcherAssert.assertThat(trans.getId(), CoreMatchers.is("/transName"));
    }

    @Test
    public void transConfigItems() throws Exception {
        TransMeta meta = new TransMeta();
        meta.setName("foo");
        Transformation trans = TransMetaConverter.convert(meta);
        MatcherAssert.assertThat(trans.getConfig().get(TRANS_META_NAME_CONF_KEY), CoreMatchers.is("foo"));
        MatcherAssert.assertThat(((String) (trans.getConfig().get(TRANS_META_CONF_KEY))), CoreMatchers.startsWith("<transformation>"));
    }

    @Test
    public void transConfigItemsNoNameSpecified() throws Exception {
        TransMeta meta = new TransMeta();
        Transformation trans = TransMetaConverter.convert(meta);
        MatcherAssert.assertThat(trans.getConfig().get(TRANS_META_NAME_CONF_KEY), CoreMatchers.is(TRANS_DEFAULT_NAME));
        MatcherAssert.assertThat(((String) (trans.getConfig().get(TRANS_META_CONF_KEY))), CoreMatchers.startsWith("<transformation>"));
    }

    @Test
    public void testDisabledHops() {
        TransMeta trans = new TransMeta();
        StepMeta start = new StepMeta("Start", stepMetaInterface);
        trans.addStep(start);
        StepMeta withEnabledHop = new StepMeta("WithEnabledHop", stepMetaInterface);
        trans.addStep(withEnabledHop);
        StepMeta withDisabledHop = new StepMeta("WithDisabledHop", stepMetaInterface);
        trans.addStep(withDisabledHop);
        StepMeta shouldStay = new StepMeta("ShouldStay", stepMetaInterface);
        trans.addStep(shouldStay);
        StepMeta shouldNotStay = new StepMeta("ShouldNotStay", stepMetaInterface);
        trans.addStep(shouldNotStay);
        StepMeta withEnabledAndDisabledHops = new StepMeta("WithEnabledAndDisabledHops", stepMetaInterface);
        trans.addStep(withEnabledAndDisabledHops);
        StepMeta afterEnabledDisabled = new StepMeta("AfterEnabledDisabled", stepMetaInterface);
        trans.addStep(afterEnabledDisabled);
        trans.addTransHop(new TransHopMeta(start, withEnabledHop));
        trans.addTransHop(new TransHopMeta(start, withDisabledHop, false));
        trans.addTransHop(new TransHopMeta(withEnabledHop, shouldStay));
        trans.addTransHop(new TransHopMeta(withDisabledHop, shouldStay));
        trans.addTransHop(new TransHopMeta(withDisabledHop, shouldNotStay));
        trans.addTransHop(new TransHopMeta(start, withEnabledAndDisabledHops));
        trans.addTransHop(new TransHopMeta(withEnabledHop, withEnabledAndDisabledHops, false));
        trans.addTransHop(new TransHopMeta(withEnabledAndDisabledHops, afterEnabledDisabled));
        Transformation transformation = TransMetaConverter.convert(trans);
        List<String> steps = transformation.getOperations().stream().map(( op) -> op.getId()).collect(Collectors.toList());
        MatcherAssert.assertThat("Only 5 ops should exist", steps.size(), CoreMatchers.is(5));
        MatcherAssert.assertThat(steps, CoreMatchers.hasItems("Start", "WithEnabledHop", "ShouldStay", "WithEnabledAndDisabledHops", "AfterEnabledDisabled"));
        List<String> hops = transformation.getHops().stream().map(( hop) -> hop.getId()).collect(Collectors.toList());
        MatcherAssert.assertThat("Only 4 hops should exist", hops.size(), CoreMatchers.is(4));
        MatcherAssert.assertThat(hops, CoreMatchers.hasItems("Start -> WithEnabledHop", "WithEnabledHop -> ShouldStay", "Start -> WithEnabledAndDisabledHops", "WithEnabledAndDisabledHops -> AfterEnabledDisabled"));
    }

    @Test
    public void testRemovingDisabledInputSteps() {
        TransMeta trans = new TransMeta();
        StepMeta inputToBeRemoved = new StepMeta("InputToBeRemoved", stepMetaInterface);
        trans.addStep(inputToBeRemoved);
        StepMeta inputToStay = new StepMeta("InputToStay", stepMetaInterface);
        trans.addStep(inputToStay);
        StepMeta inputReceiver1 = new StepMeta("InputReceiver1", stepMetaInterface);
        trans.addStep(inputReceiver1);
        StepMeta inputReceiver2 = new StepMeta("InputReceiver2", stepMetaInterface);
        trans.addStep(inputReceiver2);
        TransHopMeta hop1 = new TransHopMeta(inputToBeRemoved, inputReceiver1, false);
        TransHopMeta hop2 = new TransHopMeta(inputToStay, inputReceiver1);
        TransHopMeta hop3 = new TransHopMeta(inputToBeRemoved, inputReceiver2, false);
        trans.addTransHop(hop1);
        trans.addTransHop(hop2);
        trans.addTransHop(hop3);
        Transformation transformation = TransMetaConverter.convert(trans);
        List<String> steps = transformation.getOperations().stream().map(( op) -> op.getId()).collect(Collectors.toList());
        MatcherAssert.assertThat("Only 2 ops should exist", steps.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(steps, CoreMatchers.hasItems("InputToStay", "InputReceiver1"));
        List<String> hops = transformation.getHops().stream().map(( hop) -> hop.getId()).collect(Collectors.toList());
        MatcherAssert.assertThat("Only 1 hop should exist", hops.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(hops, CoreMatchers.hasItems("InputToStay -> InputReceiver1"));
    }

    @Test
    public void testMultipleDisabledHops() {
        TransMeta trans = new TransMeta();
        StepMeta input = new StepMeta("Input", stepMetaInterface);
        trans.addStep(input);
        StepMeta step1 = new StepMeta("Step1", stepMetaInterface);
        trans.addStep(step1);
        StepMeta step2 = new StepMeta("Step2", stepMetaInterface);
        trans.addStep(step2);
        StepMeta step3 = new StepMeta("Step3", stepMetaInterface);
        trans.addStep(step3);
        TransHopMeta hop1 = new TransHopMeta(input, step1, false);
        TransHopMeta hop2 = new TransHopMeta(step1, step2, false);
        TransHopMeta hop3 = new TransHopMeta(step2, step3, false);
        trans.addTransHop(hop1);
        trans.addTransHop(hop2);
        trans.addTransHop(hop3);
        Transformation transformation = TransMetaConverter.convert(trans);
        MatcherAssert.assertThat("Trans has steps though all of them should be removed", transformation.getOperations().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat("Trans has hops though all of them should be removed", transformation.getHops().size(), CoreMatchers.is(0));
    }

    @Test
    public void errorHops() throws Exception {
        TransMeta meta = new TransMeta();
        meta.setFilename("fileName");
        StepMeta from = new StepMeta("step1", stepMetaInterface);
        meta.addStep(from);
        StepMeta to = new StepMeta("step2", stepMetaInterface);
        meta.addStep(to);
        meta.addTransHop(new TransHopMeta(from, to));
        StepMeta error = new StepMeta("errorHandler", stepMetaInterface);
        meta.addStep(error);
        TransHopMeta errorHop = new TransHopMeta(from, error);
        errorHop.setErrorHop(true);
        meta.addTransHop(errorHop);
        Transformation trans = TransMetaConverter.convert(meta);
        Map<String, List<Hop>> hops = trans.getHops().stream().collect(Collectors.groupingBy(Hop::getType));
        List<Hop> normalHops = hops.get(TYPE_NORMAL);
        MatcherAssert.assertThat(normalHops.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(normalHops.get(0).getTo().getId(), CoreMatchers.is("step2"));
        List<Hop> errorHops = hops.get(TYPE_ERROR);
        MatcherAssert.assertThat(errorHops.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(errorHops.get(0).getTo().getId(), CoreMatchers.is("errorHandler"));
        MatcherAssert.assertThat(hops.values().stream().flatMap(List::stream).map(Hop::getFrom).map(Operation::getId).collect(Collectors.toList()), CoreMatchers.everyItem(CoreMatchers.equalTo("step1")));
    }

    @Test
    public void lazyConversionTurnedOff() throws KettleException {
        KettleEnvironment.init();
        TransMeta transMeta = new TransMeta();
        CsvInputMeta csvInputMeta = new CsvInputMeta();
        csvInputMeta.setLazyConversionActive(true);
        StepMeta csvInput = new StepMeta("Csv", csvInputMeta);
        transMeta.addStep(csvInput);
        TableInputMeta tableInputMeta = new TableInputMeta();
        tableInputMeta.setLazyConversionActive(true);
        StepMeta tableInput = new StepMeta("Table", tableInputMeta);
        transMeta.addStep(tableInput);
        Transformation trans = TransMetaConverter.convert(transMeta);
        TransMeta cloneMeta;
        String transMetaXml = ((String) (trans.getConfig().get(TRANS_META_CONF_KEY)));
        Document doc;
        try {
            doc = XMLHandler.loadXMLString(transMetaXml);
            Node stepNode = XMLHandler.getSubNode(doc, "transformation");
            cloneMeta = new TransMeta(stepNode, null);
        } catch (KettleXMLException | KettleMissingPluginsException e) {
            throw new RuntimeException(e);
        }
        MatcherAssert.assertThat(isLazyConversionActive(), CoreMatchers.is(false));
        MatcherAssert.assertThat(isLazyConversionActive(), CoreMatchers.is(false));
    }

    @Test
    public void testIncludesSubTransformations() throws Exception {
        TransMeta parentTransMeta = new TransMeta(getClass().getResource("trans-meta-converter-parent.ktr").getPath());
        Transformation transformation = TransMetaConverter.convert(parentTransMeta);
        @SuppressWarnings({ "unchecked", "ConstantConditions" })
        HashMap<String, Transformation> config = ((HashMap<String, Transformation>) (transformation.getConfig(SUB_TRANSFORMATIONS_KEY).get()));
        Assert.assertEquals(1, config.size());
        Assert.assertNotNull(config.get(("file://" + (getClass().getResource("trans-meta-converter-sub.ktr").getPath()))));
    }

    @Test
    public void testIncludesSubTransformationsFromRepository() throws Exception {
        TransMeta parentTransMeta = new TransMeta(getClass().getResource("trans-meta-converter-parent.ktr").getPath());
        Repository repository = Mockito.mock(Repository.class);
        TransMeta transMeta = new TransMeta();
        RepositoryDirectoryInterface repositoryDirectory = new RepositoryDirectory(null, "public");
        String directory = getClass().getResource("").toString().replace(File.separator, "/");
        Mockito.when(repository.findDirectory("public")).thenReturn(repositoryDirectory);
        Mockito.when(repository.loadTransformation("trans-meta-converter-sub.ktr", repositoryDirectory, null, true, null)).thenReturn(transMeta);
        parentTransMeta.setRepository(repository);
        parentTransMeta.setRepositoryDirectory(repositoryDirectory);
        parentTransMeta.setVariable("Internal.Entry.Current.Directory", "public");
        Transformation transformation = TransMetaConverter.convert(parentTransMeta);
        @SuppressWarnings({ "unchecked", "ConstantConditions" })
        HashMap<String, Transformation> config = ((HashMap<String, Transformation>) (transformation.getConfig(SUB_TRANSFORMATIONS_KEY).get()));
        Assert.assertEquals(1, config.size());
        Assert.assertNotNull(config.get("public/trans-meta-converter-sub.ktr"));
    }

    @Test
    public void testClonesTransMeta() throws KettleException {
        class ResultCaptor implements Answer<Object> {
            private Object result;

            public Object getResult() {
                return result;
            }

            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                result = invocationOnMock.callRealMethod();
                return result;
            }
        }
        TransMeta originalTransMeta = Mockito.spy(new TransMeta());
        ResultCaptor cloneTransMetaCaptor = new ResultCaptor();
        Mockito.doAnswer(cloneTransMetaCaptor).when(originalTransMeta).realClone(ArgumentMatchers.eq(false));
        originalTransMeta.setName("TransName");
        TransMetaConverter.convert(originalTransMeta);
        TransMeta cloneTransMeta = ((TransMeta) (cloneTransMetaCaptor.getResult()));
        Mockito.verify(originalTransMeta).realClone(ArgumentMatchers.eq(false));
        MatcherAssert.assertThat(cloneTransMeta.getName(), CoreMatchers.is(originalTransMeta.getName()));
        Mockito.verify(originalTransMeta, Mockito.never()).getXML();
        Mockito.verify(cloneTransMeta).getXML();
    }

    @Test
    public void testResolveStepMetaResources() throws KettleException, MetaStoreException {
        Variables variables = new Variables();
        TransMeta transMeta = Mockito.spy(new TransMeta());
        transMeta.setParentVariableSpace(variables);
        Mockito.doReturn(transMeta).when(transMeta).realClone(false);
        TransMetaConverterTest.TestMetaResolvableResource testMetaResolvableResource = Mockito.spy(new TransMetaConverterTest.TestMetaResolvableResource());
        TransMetaConverterTest.TestMetaResolvableResource testMetaResolvableResourceTwo = Mockito.spy(new TransMetaConverterTest.TestMetaResolvableResource());
        StepMeta testMeta = new StepMeta("TestMeta", testMetaResolvableResource);
        StepMeta testMetaTwo = new StepMeta("TestMeta2", testMetaResolvableResourceTwo);
        transMeta.addStep(testMeta);
        transMeta.addStep(testMetaTwo);
        transMeta.addTransHop(new TransHopMeta(testMeta, testMetaTwo));
        TransMetaConverter.convert(transMeta);
        Mockito.verify(testMetaResolvableResource).resolve();
        Mockito.verify(testMetaResolvableResourceTwo).resolve();
    }

    private static class TestMetaResolvableResource extends BaseStepMeta implements StepMetaInterface , ResolvableResource {
        @Override
        public void resolve() {
        }

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
}

