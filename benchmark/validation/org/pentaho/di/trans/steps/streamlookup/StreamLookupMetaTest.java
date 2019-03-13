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
package org.pentaho.di.trans.steps.streamlookup;


import java.util.UUID;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.initializer.InitializerInterface;


public class StreamLookupMetaTest implements InitializerInterface<StepMetaInterface> {
    LoadSaveTester loadSaveTester;

    Class<StreamLookupMeta> testMetaClass = StreamLookupMeta.class;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    @Test
    public void testCloneInfoSteps() {
        StreamLookupMeta meta = new StreamLookupMeta();
        meta.setDefault();
        final String stepName = UUID.randomUUID().toString();
        StepMeta infoStep = Mockito.mock(StepMeta.class);
        Mockito.when(infoStep.getName()).thenReturn(stepName);
        meta.getStepIOMeta().getInfoStreams().get(0).setStepMeta(infoStep);
        StreamLookupMeta cloned = ((StreamLookupMeta) (meta.clone()));
        Assert.assertEquals(stepName, cloned.getStepIOMeta().getInfoStreams().get(0).getStepname());
        Assert.assertNotSame(meta.getStepIOMeta().getInfoStreams().get(0), cloned.getStepIOMeta().getInfoStreams().get(0));
    }

    // PDI-16110
    @Test
    public void testGetXML() {
        StreamLookupMeta streamLookupMeta = new StreamLookupMeta();
        streamLookupMeta.setKeystream(new String[]{ "testKeyStreamValue" });
        streamLookupMeta.setKeylookup(new String[]{ "testKeyLookupValue" });
        streamLookupMeta.setValue(new String[]{ "testValue" });
        streamLookupMeta.setValueName(new String[]{  });
        streamLookupMeta.setValueDefault(new String[]{  });
        streamLookupMeta.setValueDefaultType(new int[]{  });
        // run without exception
        streamLookupMeta.afterInjectionSynchronization();
        streamLookupMeta.getXML();
        Assert.assertEquals(streamLookupMeta.getKeystream().length, streamLookupMeta.getValueName().length);
        Assert.assertEquals(streamLookupMeta.getKeystream().length, streamLookupMeta.getValueDefault().length);
        Assert.assertEquals(streamLookupMeta.getKeystream().length, streamLookupMeta.getValueDefaultType().length);
    }
}

