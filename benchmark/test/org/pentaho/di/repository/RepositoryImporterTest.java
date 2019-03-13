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
package org.pentaho.di.repository;


import ObjectLocationSpecificationMethod.REPOSITORY_BY_NAME;
import java.util.Collections;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.metainject.MetaInjectMeta;
import org.pentaho.di.trans.steps.metainject.RepositoryImporterExtension;
import org.w3c.dom.Node;


public class RepositoryImporterTest {
    private static final String ROOT_PATH = "/test_root";

    @Mock
    private RepositoryImportFeedbackInterface feedback;

    private RepositoryDirectoryInterface baseDirectory;

    private Node entityNode;

    @Test
    public void testPatchTransSteps_with_meta_inject_step() throws Exception {
        Repository repository = Mockito.mock(Repository.class);
        LogChannelInterface log = Mockito.mock(LogChannelInterface.class);
        RepositoryImporter importer = Mockito.spy(new RepositoryImporter(repository, log));
        importer.setBaseDirectory(Mockito.mock(RepositoryDirectoryInterface.class));
        Mockito.doReturn("TEST_PATH").when(importer).resolvePath(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        MetaInjectMeta metaInjectMeta = Mockito.mock(MetaInjectMeta.class);
        Mockito.doReturn(REPOSITORY_BY_NAME).when(metaInjectMeta).getSpecificationMethod();
        StepMeta stepMeta = Mockito.mock(StepMeta.class);
        Mockito.doReturn(metaInjectMeta).when(stepMeta).getStepMetaInterface();
        Mockito.doReturn(true).when(stepMeta).isEtlMetaInject();
        TransMeta transMeta = Mockito.mock(TransMeta.class);
        Mockito.doReturn(Collections.singletonList(stepMeta)).when(transMeta).getSteps();
        Object[] object = new Object[4];
        object[0] = "TEST_PATH";
        object[1] = Mockito.mock(RepositoryDirectoryInterface.class);
        object[2] = stepMeta;
        object[3] = true;
        RepositoryImporterExtension repositoryImporterExtension = new RepositoryImporterExtension();
        repositoryImporterExtension.callExtensionPoint(log, object);
        Mockito.verify(metaInjectMeta).setDirectoryPath("TEST_PATH");
    }
}

