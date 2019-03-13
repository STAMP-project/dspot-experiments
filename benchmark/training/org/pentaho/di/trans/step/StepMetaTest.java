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
package org.pentaho.di.trans.step;


import java.util.Random;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.utils.TestUtils;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class StepMetaTest {
    private static final Random rand = new Random();

    private static final String STEP_ID = "step_id";

    @Test
    public void cloning() throws Exception {
        StepMeta meta = StepMetaTest.createTestMeta();
        StepMeta clone = ((StepMeta) (meta.clone()));
        StepMetaTest.assertEquals(meta, clone);
    }

    @Test
    public void testEqualsHashCodeConsistency() throws Exception {
        StepMeta step = new StepMeta();
        step.setName("step");
        TestUtils.checkEqualsHashCodeConsistency(step, step);
        StepMeta stepSame = new StepMeta();
        stepSame.setName("step");
        Assert.assertTrue(step.equals(stepSame));
        TestUtils.checkEqualsHashCodeConsistency(step, stepSame);
        StepMeta stepCaps = new StepMeta();
        stepCaps.setName("STEP");
        TestUtils.checkEqualsHashCodeConsistency(step, stepCaps);
        StepMeta stepOther = new StepMeta();
        stepOther.setName("something else");
        TestUtils.checkEqualsHashCodeConsistency(step, stepOther);
    }

    @Test
    public void stepMetaXmlConsistency() throws Exception {
        StepMeta meta = new StepMeta("id", "name", null);
        StepMetaInterface smi = new org.pentaho.di.trans.steps.missing.MissingTrans(meta.getName(), meta.getStepID());
        meta.setStepMetaInterface(smi);
        StepMeta fromXml = StepMeta.fromXml(meta.getXML());
        Assert.assertThat(meta.getXML(), CoreMatchers.is(fromXml.getXML()));
    }
}

