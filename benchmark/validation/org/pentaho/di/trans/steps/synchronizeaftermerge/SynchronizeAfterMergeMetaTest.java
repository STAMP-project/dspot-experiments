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
package org.pentaho.di.trans.steps.synchronizeaftermerge;


import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.initializer.InitializerInterface;


public class SynchronizeAfterMergeMetaTest implements InitializerInterface<StepMetaInterface> {
    LoadSaveTester loadSaveTester;

    Class<SynchronizeAfterMergeMeta> testMetaClass = SynchronizeAfterMergeMeta.class;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    @Test
    public void testPDI16559() throws Exception {
        SynchronizeAfterMergeMeta synchronizeAfterMerge = new SynchronizeAfterMergeMeta();
        synchronizeAfterMerge.setKeyStream(new String[]{ "field1", "field2", "field3", "field4", "field5" });
        synchronizeAfterMerge.setKeyLookup(new String[]{ "lookup1", "lookup2" });
        synchronizeAfterMerge.setKeyCondition(new String[]{ "cond1", "cond2", "cond3" });
        synchronizeAfterMerge.setKeyStream2(new String[]{ "stream2-a", "stream2-b", "stream2-x", "stream2-d" });
        synchronizeAfterMerge.setUpdateLookup(new String[]{ "updlook1", "updlook2", "updlook3", "updlook4", "updlook5" });
        synchronizeAfterMerge.setUpdateStream(new String[]{ "updstr1", "updstr2", "updstr3" });
        synchronizeAfterMerge.setUpdate(new Boolean[]{ false, true });
        synchronizeAfterMerge.afterInjectionSynchronization();
        String ktrXml = synchronizeAfterMerge.getXML();
        int targetSz = synchronizeAfterMerge.getKeyStream().length;
        Assert.assertEquals(targetSz, synchronizeAfterMerge.getKeyLookup().length);
        Assert.assertEquals(targetSz, synchronizeAfterMerge.getKeyCondition().length);
        Assert.assertEquals(targetSz, synchronizeAfterMerge.getKeyStream2().length);
        targetSz = synchronizeAfterMerge.getUpdateLookup().length;
        Assert.assertEquals(targetSz, synchronizeAfterMerge.getUpdateStream().length);
        Assert.assertEquals(targetSz, synchronizeAfterMerge.getUpdate().length);
    }
}

