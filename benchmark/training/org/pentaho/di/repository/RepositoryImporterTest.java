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
package org.pentaho.di.repository;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Node;


@RunWith(MockitoJUnitRunner.class)
public class RepositoryImporterTest {
    private static final String ROOT_PATH = "/test_root";

    private static final String USER_NAME_PATH = "/userName";

    @Mock
    private RepositoryImportFeedbackInterface feedback;

    private RepositoryDirectoryInterface baseDirectory;

    private Node entityNode;

    @Test
    public void testImportJob_patchJobEntries_without_variables() throws KettleException {
        JobEntryInterface jobEntry = RepositoryImporterTest.createJobEntry("/userName");
        StepMetaInterface stepMeta = RepositoryImporterTest.createStepMeta("");
        RepositoryImporter importer = RepositoryImporterTest.createRepositoryImporter(jobEntry, stepMeta, true);
        importer.setBaseDirectory(baseDirectory);
        importer.importJob(entityNode, feedback);
        Mockito.verify(((HasRepositoryDirectories) (jobEntry))).setDirectories(new String[]{ (RepositoryImporterTest.ROOT_PATH) + (RepositoryImporterTest.USER_NAME_PATH) });
    }

    @Test
    public void testImportJob_patchJobEntries_with_variable() throws KettleException {
        JobEntryInterface jobEntryInterface = RepositoryImporterTest.createJobEntry("${USER_VARIABLE}");
        StepMetaInterface stepMeta = RepositoryImporterTest.createStepMeta("");
        RepositoryImporter importer = RepositoryImporterTest.createRepositoryImporter(jobEntryInterface, stepMeta, true);
        importer.setBaseDirectory(baseDirectory);
        importer.importJob(entityNode, feedback);
        Mockito.verify(((HasRepositoryDirectories) (jobEntryInterface))).setDirectories(new String[]{ "${USER_VARIABLE}" });
    }

    @Test
    public void testImportJob_patchJobEntries_when_directory_path_starts_with_variable() throws KettleException {
        JobEntryInterface jobEntryInterface = RepositoryImporterTest.createJobEntry("${USER_VARIABLE}/myDir");
        StepMetaInterface stepMeta = RepositoryImporterTest.createStepMeta("");
        RepositoryImporter importer = RepositoryImporterTest.createRepositoryImporter(jobEntryInterface, stepMeta, true);
        importer.setBaseDirectory(baseDirectory);
        importer.importJob(entityNode, feedback);
        Mockito.verify(((HasRepositoryDirectories) (jobEntryInterface))).setDirectories(new String[]{ "${USER_VARIABLE}/myDir" });
        JobEntryInterface jobEntryInterface2 = RepositoryImporterTest.createJobEntry("${USER_VARIABLE}/myDir");
        RepositoryImporter importerWithCompatibilityImportPath = RepositoryImporterTest.createRepositoryImporter(jobEntryInterface2, stepMeta, false);
        importerWithCompatibilityImportPath.setBaseDirectory(baseDirectory);
        importerWithCompatibilityImportPath.importJob(entityNode, feedback);
        Mockito.verify(((HasRepositoryDirectories) (jobEntryInterface2))).setDirectories(new String[]{ (RepositoryImporterTest.ROOT_PATH) + "/${USER_VARIABLE}/myDir" });
    }

    @Test
    public void testImportJob_patchJobEntries_when_directory_path_ends_with_variable() throws KettleException {
        JobEntryInterface jobEntryInterface = RepositoryImporterTest.createJobEntry("/myDir/${USER_VARIABLE}");
        StepMetaInterface stepMeta = RepositoryImporterTest.createStepMeta("");
        RepositoryImporter importer = RepositoryImporterTest.createRepositoryImporter(jobEntryInterface, stepMeta, true);
        importer.setBaseDirectory(baseDirectory);
        importer.importJob(entityNode, feedback);
        Mockito.verify(((HasRepositoryDirectories) (jobEntryInterface))).setDirectories(new String[]{ "/myDir/${USER_VARIABLE}" });
        JobEntryInterface jobEntryInterface2 = RepositoryImporterTest.createJobEntry("/myDir/${USER_VARIABLE}");
        RepositoryImporter importerWithCompatibilityImportPath = RepositoryImporterTest.createRepositoryImporter(jobEntryInterface2, stepMeta, false);
        importerWithCompatibilityImportPath.setBaseDirectory(baseDirectory);
        importerWithCompatibilityImportPath.importJob(entityNode, feedback);
        Mockito.verify(((HasRepositoryDirectories) (jobEntryInterface2))).setDirectories(new String[]{ (RepositoryImporterTest.ROOT_PATH) + "/myDir/${USER_VARIABLE}" });
    }

    @Test
    public void testImportTrans_patchTransEntries_without_variables() throws KettleException {
        JobEntryInterface jobEntryInterface = RepositoryImporterTest.createJobEntry("");
        StepMetaInterface stepMeta = RepositoryImporterTest.createStepMeta("/userName");
        RepositoryImporter importer = RepositoryImporterTest.createRepositoryImporter(jobEntryInterface, stepMeta, true);
        importer.setBaseDirectory(baseDirectory);
        importer.importTransformation(entityNode, feedback);
        Mockito.verify(((HasRepositoryDirectories) (stepMeta))).setDirectories(new String[]{ (RepositoryImporterTest.ROOT_PATH) + (RepositoryImporterTest.USER_NAME_PATH) });
    }

    @Test
    public void testImportTrans_patchTransEntries_with_variable() throws KettleException {
        JobEntryInterface jobEntryInterface = RepositoryImporterTest.createJobEntry("");
        StepMetaInterface stepMeta = RepositoryImporterTest.createStepMeta("${USER_VARIABLE}");
        RepositoryImporter importer = RepositoryImporterTest.createRepositoryImporter(jobEntryInterface, stepMeta, true);
        importer.setBaseDirectory(baseDirectory);
        importer.importTransformation(entityNode, feedback);
        Mockito.verify(((HasRepositoryDirectories) (stepMeta))).setDirectories(new String[]{ "${USER_VARIABLE}" });
    }

    @Test
    public void testImportTrans_patchTransEntries_when_directory_path_starts_with_variable() throws KettleException {
        JobEntryInterface jobEntryInterface = RepositoryImporterTest.createJobEntry("");
        StepMetaInterface stepMeta = RepositoryImporterTest.createStepMeta("${USER_VARIABLE}/myDir");
        RepositoryImporter importer = RepositoryImporterTest.createRepositoryImporter(jobEntryInterface, stepMeta, true);
        importer.setBaseDirectory(baseDirectory);
        importer.importTransformation(entityNode, feedback);
        Mockito.verify(((HasRepositoryDirectories) (stepMeta))).setDirectories(new String[]{ "${USER_VARIABLE}/myDir" });
        StepMetaInterface stepMeta2 = RepositoryImporterTest.createStepMeta("${USER_VARIABLE}/myDir");
        RepositoryImporter importerWithCompatibilityImportPath = RepositoryImporterTest.createRepositoryImporter(jobEntryInterface, stepMeta2, false);
        importerWithCompatibilityImportPath.setBaseDirectory(baseDirectory);
        importerWithCompatibilityImportPath.importTransformation(entityNode, feedback);
        Mockito.verify(((HasRepositoryDirectories) (stepMeta2))).setDirectories(new String[]{ (RepositoryImporterTest.ROOT_PATH) + "/${USER_VARIABLE}/myDir" });
    }

    @Test
    public void testImportTrans_patchTransEntries_when_directory_path_ends_with_variable() throws KettleException {
        JobEntryInterface jobEntryInterface = RepositoryImporterTest.createJobEntry("");
        StepMetaInterface stepMeta = RepositoryImporterTest.createStepMeta("/myDir/${USER_VARIABLE}");
        RepositoryImporter importer = RepositoryImporterTest.createRepositoryImporter(jobEntryInterface, stepMeta, true);
        importer.setBaseDirectory(baseDirectory);
        importer.importTransformation(entityNode, feedback);
        Mockito.verify(((HasRepositoryDirectories) (stepMeta))).setDirectories(new String[]{ "/myDir/${USER_VARIABLE}" });
        StepMetaInterface stepMeta2 = RepositoryImporterTest.createStepMeta("/myDir/${USER_VARIABLE}");
        RepositoryImporter importerWithCompatibilityImportPath = RepositoryImporterTest.createRepositoryImporter(jobEntryInterface, stepMeta2, false);
        importerWithCompatibilityImportPath.setBaseDirectory(baseDirectory);
        importerWithCompatibilityImportPath.importTransformation(entityNode, feedback);
        Mockito.verify(((HasRepositoryDirectories) (stepMeta2))).setDirectories(new String[]{ (RepositoryImporterTest.ROOT_PATH) + "/myDir/${USER_VARIABLE}" });
    }
}

