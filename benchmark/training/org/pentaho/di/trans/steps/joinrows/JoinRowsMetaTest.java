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
package org.pentaho.di.trans.steps.joinrows;


import junit.framework.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;


public class JoinRowsMetaTest {
    LoadSaveTester loadSaveTester;

    Class<JoinRowsMeta> testMetaClass = JoinRowsMeta.class;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    @Test
    public void testCleanAfterHopToRemove_NullParameter() {
        JoinRowsMeta joinRowsMeta = new JoinRowsMeta();
        StepMeta stepMeta1 = new StepMeta("Step1", Mockito.mock(StepMetaInterface.class));
        joinRowsMeta.setMainStep(stepMeta1);
        joinRowsMeta.setMainStepname(stepMeta1.getName());
        // This call must not throw an exception
        joinRowsMeta.cleanAfterHopToRemove(null);
        // And no change to the step should be made
        Assert.assertEquals(stepMeta1, joinRowsMeta.getMainStep());
        Assert.assertEquals(stepMeta1.getName(), joinRowsMeta.getMainStepname());
    }

    @Test
    public void testCleanAfterHopToRemove_UnknownStep() {
        JoinRowsMeta joinRowsMeta = new JoinRowsMeta();
        StepMeta stepMeta1 = new StepMeta("Step1", Mockito.mock(StepMetaInterface.class));
        StepMeta stepMeta2 = new StepMeta("Step2", Mockito.mock(StepMetaInterface.class));
        joinRowsMeta.setMainStep(stepMeta1);
        joinRowsMeta.setMainStepname(stepMeta1.getName());
        joinRowsMeta.cleanAfterHopToRemove(stepMeta2);
        // No change to the step should be made
        Assert.assertEquals(stepMeta1, joinRowsMeta.getMainStep());
        Assert.assertEquals(stepMeta1.getName(), joinRowsMeta.getMainStepname());
    }

    @Test
    public void testCleanAfterHopToRemove_ReferredStep() {
        JoinRowsMeta joinRowsMeta = new JoinRowsMeta();
        StepMeta stepMeta1 = new StepMeta("Step1", Mockito.mock(StepMetaInterface.class));
        joinRowsMeta.setMainStep(stepMeta1);
        joinRowsMeta.setMainStepname(stepMeta1.getName());
        joinRowsMeta.cleanAfterHopToRemove(stepMeta1);
        // No change to the step should be made
        Assert.assertNull(joinRowsMeta.getMainStep());
        Assert.assertNull(joinRowsMeta.getMainStepname());
    }
}

