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
package org.pentaho.di.trans.steps.blockuntilstepsfinish;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.initializer.InitializerInterface;


public class BlockUntilStepsFinishMetaTest implements InitializerInterface<BlockUntilStepsFinishMeta> {
    LoadSaveTester<BlockUntilStepsFinishMeta> loadSaveTester;

    Class<BlockUntilStepsFinishMeta> testMetaClass = BlockUntilStepsFinishMeta.class;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    @Test
    public void cloneTest() throws Exception {
        BlockUntilStepsFinishMeta meta = new BlockUntilStepsFinishMeta();
        meta.allocate(2);
        meta.setStepName(new String[]{ "step1", "step2" });
        meta.setStepCopyNr(new String[]{ "copy1", "copy2" });
        BlockUntilStepsFinishMeta aClone = ((BlockUntilStepsFinishMeta) (meta.clone()));
        Assert.assertFalse((aClone == meta));
        Assert.assertTrue(Arrays.equals(meta.getStepName(), aClone.getStepName()));
        Assert.assertTrue(Arrays.equals(meta.getStepCopyNr(), aClone.getStepCopyNr()));
        Assert.assertEquals(meta.getXML(), aClone.getXML());
    }
}

