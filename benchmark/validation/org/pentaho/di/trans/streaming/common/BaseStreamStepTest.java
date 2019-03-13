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


import ObjectLocationSpecificationMethod.FILENAME;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.logging.LogChannelInterfaceFactory;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.streaming.api.StreamSource;
import org.pentaho.di.trans.streaming.api.StreamWindow;


@RunWith(MockitoJUnitRunner.class)
public class BaseStreamStepTest {
    private BaseStreamStep baseStreamStep;

    @Mock
    BaseStreamStepMeta meta;

    @Mock
    BaseStreamStepMeta metaWithVariables;

    @Mock
    StepDataInterface stepData;

    @Mock
    StreamSource<List<Object>> streamSource;

    @Mock
    StreamWindow<List<Object>, Result> streamWindow;

    @Mock
    LogChannelInterfaceFactory logChannelFactory;

    @Mock
    LogChannelInterface logChannel;

    @Mock
    private StepMeta parentStepMeta;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testInitMissingFilename() {
        Mockito.when(meta.getSpecificationMethod()).thenReturn(FILENAME);
        Assert.assertFalse(baseStreamStep.init(meta, stepData));
        Mockito.verify(logChannel).logError(ArgumentMatchers.contains("Unable to load transformation "), ArgumentMatchers.any(KettleException.class));
    }

    @Test
    public void testInitFilenameSubstitution() throws IOException, KettleException {
        // verifies that filename resolution uses the parents ${Internal.Entry.Current.Directory}.
        // Necessary since the Current.Directory may change when running non-locally.
        // Variables should all be set in variableizedStepMeta after init, with the caveat that
        // the substrans location must be set using the parents Current.Directory.
        KettleEnvironment.init();
        File testFile = File.createTempFile("testInitFilenameSubstitution", ".ktr", folder.getRoot());
        try (PrintWriter pw = new PrintWriter(testFile)) {
            // empty subtrans definition
            pw.write(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<transformation/>"));
        }
        Mockito.when(meta.getParentStepMeta()).thenReturn(parentStepMeta);
        Mockito.when(metaWithVariables.getFileName()).thenReturn("noSuchFilename.ktr");
        Mockito.when(meta.withVariables(baseStreamStep)).thenReturn(metaWithVariables);
        baseStreamStep.getParentVariableSpace().setVariable("Internal.Entry.Current.Directory", testFile.getParentFile().getAbsolutePath());
        Mockito.when(metaWithVariables.getSpecificationMethod()).thenReturn(FILENAME);
        Mockito.when(meta.getSpecificationMethod()).thenReturn(FILENAME);
        Mockito.when(meta.getFileName()).thenReturn(("${Internal.Entry.Current.Directory}/" + (testFile.getName())));
        Assert.assertTrue(baseStreamStep.init(meta, stepData));
        Assert.assertThat(baseStreamStep.variablizedStepMeta, IsEqual.equalTo(metaWithVariables));
    }

    @Test
    public void testStop() throws KettleException {
        Result result = new Result();
        result.setSafeStop(false);
        result.setRows(Collections.emptyList());
        Mockito.when(streamWindow.buffer(ArgumentMatchers.any())).thenReturn(Collections.singletonList(result));
        baseStreamStep.processRow(meta, stepData);
        Assert.assertFalse(baseStreamStep.isSafeStopped());
        Mockito.verify(streamSource).close();
    }

    @Test
    public void testSafeStop() throws KettleException {
        Result result = new Result();
        result.setSafeStop(true);
        Mockito.when(streamWindow.buffer(ArgumentMatchers.any())).thenReturn(Collections.singletonList(result));
        baseStreamStep.processRow(meta, stepData);
        Assert.assertTrue(baseStreamStep.isSafeStopped());
        Mockito.verify(streamSource, Mockito.times(2)).close();
    }

    @Test
    public void testAlwaysCloses() throws KettleException {
        Mockito.when(streamWindow.buffer(ArgumentMatchers.any())).thenThrow(new IllegalStateException("run for your life!!!"));
        try {
            baseStreamStep.processRow(meta, stepData);
        } catch (IllegalStateException ignored) {
        }
        Mockito.verify(streamSource).close();
    }
}

