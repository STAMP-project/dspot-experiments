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


import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaChangeListenerInterface;
import org.pentaho.di.trans.steps.metainject.MetaInjectMeta;
import org.pentaho.di.trans.steps.metainject.SourceStepField;
import org.pentaho.di.trans.steps.metainject.TargetStepAttribute;


public class TransMetaTest {
    private TransMeta transMeta;

    @Test
    public void testAddOrReplaceStep() throws Exception {
        StepMeta stepMeta = TransMetaTest.mockStepMeta("ETL metadata injection");
        MetaInjectMeta stepMetaInterfaceMock = Mockito.mock(MetaInjectMeta.class);
        Mockito.when(stepMeta.getStepMetaInterface()).thenReturn(stepMetaInterfaceMock);
        transMeta.addOrReplaceStep(stepMeta);
        Mockito.verify(stepMeta).setParentTransMeta(ArgumentMatchers.any(TransMeta.class));
        // to make sure that method comes through positive scenario
        assert (transMeta.steps.size()) == 1;
        assert transMeta.changed_steps;
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

    @Test
    public void testTransListeners() {
        TransMeta TransMeta = new TransMeta();
        StepMeta oldFormStep = new StepMeta();
        oldFormStep.setName("Generate_1");
        StepMeta newFormStep = new StepMeta();
        newFormStep.setName("Generate_2");
        StepMeta toStep = new StepMeta();
        toStep.setStepMetaInterface(new MetaInjectMeta());
        toStep.setName("ETL Inject Metadata");
        StepMeta deletedStep = new StepMeta();
        deletedStep.setStepMetaInterface(new MetaInjectMeta());
        deletedStep.setName("ETL Inject Metadata for delete");
        // Verify add & remove listeners
        TransMeta.addStep(oldFormStep);
        TransMeta.addStep(toStep);
        TransMeta.addStep(deletedStep);
        Assert.assertEquals(TransMeta.nrStepChangeListeners(), 2);
        TransMeta.removeStepChangeListener(((StepMetaChangeListenerInterface) (deletedStep.getStepMetaInterface())));
        Assert.assertEquals(TransMeta.nrStepChangeListeners(), 1);
        TransMeta.removeStep(2);
        TransHopMeta hi = new TransHopMeta(oldFormStep, toStep);
        TransMeta.addTransHop(hi);
        // Verify MetaInjectMeta.onStepChange()
        // add new TargetStepAttribute
        MetaInjectMeta toMeta = ((MetaInjectMeta) (toStep.getStepMetaInterface()));
        Map<TargetStepAttribute, SourceStepField> sourceMapping = new HashMap<TargetStepAttribute, SourceStepField>();
        TargetStepAttribute keyTest = new TargetStepAttribute("File", "key", true);
        SourceStepField valueTest = new SourceStepField(oldFormStep.getName(), oldFormStep.getName());
        sourceMapping.put(keyTest, valueTest);
        toMeta.setTargetSourceMapping(sourceMapping);
        // Run all listeners
        TransMeta.notifyAllListeners(oldFormStep, newFormStep);
        // Verify changes, which listeners makes
        sourceMapping = toMeta.getTargetSourceMapping();
        for (Map.Entry<TargetStepAttribute, SourceStepField> entry : sourceMapping.entrySet()) {
            SourceStepField value = entry.getValue();
            if (!(value.getStepname().equals(newFormStep.getName()))) {
                Assert.fail();
            }
        }
        // verify another functions
        TransMeta.addStep(deletedStep);
        Assert.assertEquals(TransMeta.nrSteps(), 3);
        Assert.assertEquals(TransMeta.nrStepChangeListeners(), 2);
        TransMeta.removeStep(0);
        Assert.assertEquals(TransMeta.nrSteps(), 2);
    }
}

