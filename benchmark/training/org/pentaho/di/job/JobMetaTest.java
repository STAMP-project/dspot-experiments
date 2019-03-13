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
package org.pentaho.di.job;


import Const.INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY;
import Const.INTERNAL_VARIABLE_JOB_FILENAME_DIRECTORY;
import Const.INTERNAL_VARIABLE_JOB_REPOSITORY_DIRECTORY;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.NotePadMeta;
import org.pentaho.di.core.exception.IdNotFoundException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.exception.LookupReferencesException;
import org.pentaho.di.core.gui.OverwritePrompter;
import org.pentaho.di.core.gui.Point;
import org.pentaho.di.core.listeners.ContentChangedListener;
import org.pentaho.di.core.listeners.CurrentDirectoryChangedListener;
import org.pentaho.di.job.entries.trans.JobEntryTrans;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.repository.ObjectRevision;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectory;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.resource.ResourceNamingInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static JobMeta.BORDER_INDENT;


public class JobMetaTest {
    private static final String JOB_META_NAME = "jobName";

    private JobMeta jobMeta;

    private RepositoryDirectoryInterface directoryJob;

    private ContentChangedListener listener;

    private ObjectRevision objectRevision;

    @Test
    public void testPathExist() throws IOException, URISyntaxException, KettleXMLException {
        Assert.assertTrue(testPath("je1-je4"));
    }

    @Test
    public void testPathNotExist() throws IOException, URISyntaxException, KettleXMLException {
        Assert.assertFalse(testPath("je2-je4"));
    }

    @Test
    public void testContentChangeListener() throws Exception {
        jobMeta.setChanged();
        jobMeta.setChanged(true);
        Mockito.verify(listener, Mockito.times(2)).contentChanged(ArgumentMatchers.same(jobMeta));
        jobMeta.clearChanged();
        jobMeta.setChanged(false);
        Mockito.verify(listener, Mockito.times(2)).contentSafe(ArgumentMatchers.same(jobMeta));
        jobMeta.removeContentChangedListener(listener);
        jobMeta.setChanged();
        jobMeta.setChanged(true);
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void testLookupRepositoryReferences() throws Exception {
        jobMeta.clear();
        JobEntryTrans jobEntryMock = Mockito.mock(JobEntryTrans.class);
        Mockito.when(jobEntryMock.hasRepositoryReferences()).thenReturn(true);
        JobEntryTrans brokenJobEntryMock = Mockito.mock(JobEntryTrans.class);
        Mockito.when(brokenJobEntryMock.hasRepositoryReferences()).thenReturn(true);
        Mockito.doThrow(Mockito.mock(IdNotFoundException.class)).when(brokenJobEntryMock).lookupRepositoryReferences(ArgumentMatchers.any(Repository.class));
        JobEntryCopy jobEntryCopy1 = Mockito.mock(JobEntryCopy.class);
        Mockito.when(jobEntryCopy1.getEntry()).thenReturn(jobEntryMock);
        jobMeta.addJobEntry(0, jobEntryCopy1);
        JobEntryCopy jobEntryCopy2 = Mockito.mock(JobEntryCopy.class);
        Mockito.when(jobEntryCopy2.getEntry()).thenReturn(brokenJobEntryMock);
        jobMeta.addJobEntry(1, jobEntryCopy2);
        JobEntryCopy jobEntryCopy3 = Mockito.mock(JobEntryCopy.class);
        Mockito.when(jobEntryCopy3.getEntry()).thenReturn(jobEntryMock);
        jobMeta.addJobEntry(2, jobEntryCopy3);
        try {
            jobMeta.lookupRepositoryReferences(Mockito.mock(Repository.class));
            Assert.fail("no exception for broken entry");
        } catch (LookupReferencesException e) {
            // ok
        }
        Mockito.verify(jobEntryMock, Mockito.times(2)).lookupRepositoryReferences(ArgumentMatchers.any(Repository.class));
    }

    /**
     * Given job meta object. <br/>
     * When the job is called to export resources, then the existing current directory should be used as a context to
     * locate resources.
     */
    @Test
    public void shouldUseExistingRepositoryDirectoryWhenExporting() throws KettleException {
        final JobMeta jobMetaSpy = Mockito.spy(jobMeta);
        JobMeta jobMeta = new JobMeta() {
            @Override
            public Object realClone(boolean doClear) {
                return jobMetaSpy;
            }
        };
        jobMeta.setRepositoryDirectory(directoryJob);
        jobMeta.setName(JobMetaTest.JOB_META_NAME);
        jobMeta.exportResources(null, new HashMap<String, org.pentaho.di.resource.ResourceDefinition>(4), Mockito.mock(ResourceNamingInterface.class), null, null);
        // assert
        Mockito.verify(jobMetaSpy).setRepositoryDirectory(directoryJob);
    }

    @Test
    public void shouldUseCoordinatesOfItsStepsAndNotesWhenCalculatingMinimumPoint() {
        Point jobEntryPoint = new Point(500, 500);
        Point notePadMetaPoint = new Point(400, 400);
        JobEntryCopy jobEntryCopy = Mockito.mock(JobEntryCopy.class);
        Mockito.when(jobEntryCopy.getLocation()).thenReturn(jobEntryPoint);
        NotePadMeta notePadMeta = Mockito.mock(NotePadMeta.class);
        Mockito.when(notePadMeta.getLocation()).thenReturn(notePadMetaPoint);
        // empty Job return 0 coordinate point
        Point point = jobMeta.getMinimum();
        Assert.assertEquals(0, point.x);
        Assert.assertEquals(0, point.y);
        // when Job contains a single step or note, then jobMeta should return coordinates of it, subtracting borders
        jobMeta.addJobEntry(0, jobEntryCopy);
        Point actualStepPoint = jobMeta.getMinimum();
        Assert.assertEquals(((jobEntryPoint.x) - (BORDER_INDENT)), actualStepPoint.x);
        Assert.assertEquals(((jobEntryPoint.y) - (BORDER_INDENT)), actualStepPoint.y);
        // when Job contains step or notes, then jobMeta should return minimal coordinates of them, subtracting borders
        jobMeta.addNote(notePadMeta);
        Point stepPoint = jobMeta.getMinimum();
        Assert.assertEquals(((notePadMetaPoint.x) - (BORDER_INDENT)), stepPoint.x);
        Assert.assertEquals(((notePadMetaPoint.y) - (BORDER_INDENT)), stepPoint.y);
    }

    @Test
    public void testEquals_oneNameNull() {
        Assert.assertFalse(testEquals(null, null, null, null));
    }

    @Test
    public void testEquals_secondNameNull() {
        jobMeta.setName(null);
        Assert.assertFalse(testEquals(JobMetaTest.JOB_META_NAME, null, null, null));
    }

    @Test
    public void testEquals_sameNameOtherDir() {
        RepositoryDirectoryInterface otherDirectory = Mockito.mock(RepositoryDirectoryInterface.class);
        Mockito.when(otherDirectory.getPath()).thenReturn("otherDirectoryPath");
        Assert.assertFalse(testEquals(JobMetaTest.JOB_META_NAME, otherDirectory, null, null));
    }

    @Test
    public void testEquals_sameNameSameDirNullRev() {
        Assert.assertFalse(testEquals(JobMetaTest.JOB_META_NAME, directoryJob, null, null));
    }

    @Test
    public void testEquals_sameNameSameDirDiffRev() {
        ObjectRevision otherRevision = Mockito.mock(ObjectRevision.class);
        Mockito.when(otherRevision.getName()).thenReturn("otherRevision");
        Assert.assertFalse(testEquals(JobMetaTest.JOB_META_NAME, directoryJob, otherRevision, null));
    }

    @Test
    public void testEquals_sameNameSameDirSameRev() {
        Assert.assertTrue(testEquals(JobMetaTest.JOB_META_NAME, directoryJob, objectRevision, null));
    }

    @Test
    public void testEquals_sameNameSameDirSameRevFilename() {
        Assert.assertFalse(testEquals(JobMetaTest.JOB_META_NAME, directoryJob, objectRevision, "Filename"));
    }

    @Test
    public void testEquals_sameFilename() {
        String newFilename = "Filename";
        jobMeta.setFilename(newFilename);
        Assert.assertFalse(testEquals(null, null, null, newFilename));
    }

    @Test
    public void testEquals_difFilenameSameName() {
        jobMeta.setFilename("Filename");
        Assert.assertFalse(testEquals(JobMetaTest.JOB_META_NAME, null, null, "OtherFileName"));
    }

    @Test
    public void testEquals_sameFilenameSameName() {
        String newFilename = "Filename";
        jobMeta.setFilename(newFilename);
        Assert.assertTrue(testEquals(JobMetaTest.JOB_META_NAME, null, null, newFilename));
    }

    @Test
    public void testEquals_sameFilenameDifName() {
        String newFilename = "Filename";
        jobMeta.setFilename(newFilename);
        Assert.assertFalse(testEquals("OtherName", null, null, newFilename));
    }

    @Test
    public void testLoadXml() throws KettleException {
        String directory = "/home/admin";
        Node jobNode = Mockito.mock(Node.class);
        NodeList nodeList = new NodeList() {
            Node node = Mockito.mock(Node.class);

            {
                Mockito.when(node.getNodeName()).thenReturn("directory");
                Node child = Mockito.mock(Node.class);
                Mockito.when(node.getFirstChild()).thenReturn(child);
                Mockito.when(child.getNodeValue()).thenReturn(directory);
            }

            @Override
            public Node item(int index) {
                return node;
            }

            @Override
            public int getLength() {
                return 1;
            }
        };
        Mockito.when(jobNode.getChildNodes()).thenReturn(nodeList);
        Repository rep = Mockito.mock(Repository.class);
        RepositoryDirectory repDirectory = new RepositoryDirectory(new RepositoryDirectory(new RepositoryDirectory(), "home"), "admin");
        Mockito.when(rep.findDirectory(Mockito.eq(directory))).thenReturn(repDirectory);
        JobMeta meta = new JobMeta();
        meta.loadXML(jobNode, null, rep, Mockito.mock(IMetaStore.class), false, Mockito.mock(OverwritePrompter.class));
        Job job = new Job(rep, meta);
        job.setInternalKettleVariables(null);
        Assert.assertEquals(repDirectory.getPath(), job.getVariable(INTERNAL_VARIABLE_JOB_REPOSITORY_DIRECTORY));
    }

    @Test
    public void testAddRemoveJobEntryCopySetUnsetParent() throws Exception {
        JobEntryCopy jobEntryCopy = Mockito.mock(JobEntryCopy.class);
        jobMeta.addJobEntry(jobEntryCopy);
        jobMeta.removeJobEntry(0);
        Mockito.verify(jobEntryCopy, Mockito.times(1)).setParentJobMeta(jobMeta);
        Mockito.verify(jobEntryCopy, Mockito.times(1)).setParentJobMeta(null);
    }

    @Test
    public void testFireCurrentDirChanged() throws Exception {
        String pathBefore = "/path/before";
        String pathAfter = "path/after";
        RepositoryDirectoryInterface repoDirOrig = Mockito.mock(RepositoryDirectoryInterface.class);
        Mockito.when(repoDirOrig.getPath()).thenReturn(pathBefore);
        RepositoryDirectoryInterface repoDir = Mockito.mock(RepositoryDirectoryInterface.class);
        Mockito.when(repoDir.getPath()).thenReturn(pathAfter);
        jobMeta.setRepository(Mockito.mock(Repository.class));
        jobMeta.setRepositoryDirectory(repoDirOrig);
        CurrentDirectoryChangedListener listener = Mockito.mock(CurrentDirectoryChangedListener.class);
        jobMeta.addCurrentDirectoryChangedListener(listener);
        jobMeta.setRepositoryDirectory(repoDir);
        Mockito.verify(listener, Mockito.times(1)).directoryChanged(jobMeta, pathBefore, pathAfter);
    }

    @Test
    public void testHasLoop_simpleLoop() throws Exception {
        // main->2->3->main
        JobMeta jobMetaSpy = Mockito.spy(jobMeta);
        JobEntryCopy jobEntryCopyMain = createJobEntryCopy("mainStep");
        JobEntryCopy jobEntryCopy2 = createJobEntryCopy("step2");
        JobEntryCopy jobEntryCopy3 = createJobEntryCopy("step3");
        Mockito.when(jobMetaSpy.findNrPrevJobEntries(jobEntryCopyMain)).thenReturn(1);
        Mockito.when(jobMetaSpy.findPrevJobEntry(jobEntryCopyMain, 0)).thenReturn(jobEntryCopy2);
        Mockito.when(jobMetaSpy.findNrPrevJobEntries(jobEntryCopy2)).thenReturn(1);
        Mockito.when(jobMetaSpy.findPrevJobEntry(jobEntryCopy2, 0)).thenReturn(jobEntryCopy3);
        Mockito.when(jobMetaSpy.findNrPrevJobEntries(jobEntryCopy3)).thenReturn(1);
        Mockito.when(jobMetaSpy.findPrevJobEntry(jobEntryCopy3, 0)).thenReturn(jobEntryCopyMain);
        Assert.assertTrue(jobMetaSpy.hasLoop(jobEntryCopyMain));
    }

    @Test
    public void testHasLoop_loopInPrevSteps() throws Exception {
        // main->2->3->4->3
        JobMeta jobMetaSpy = Mockito.spy(jobMeta);
        JobEntryCopy jobEntryCopyMain = createJobEntryCopy("mainStep");
        JobEntryCopy jobEntryCopy2 = createJobEntryCopy("step2");
        JobEntryCopy jobEntryCopy3 = createJobEntryCopy("step3");
        JobEntryCopy jobEntryCopy4 = createJobEntryCopy("step4");
        Mockito.when(jobMetaSpy.findNrPrevJobEntries(jobEntryCopyMain)).thenReturn(1);
        Mockito.when(jobMetaSpy.findPrevJobEntry(jobEntryCopyMain, 0)).thenReturn(jobEntryCopy2);
        Mockito.when(jobMetaSpy.findNrPrevJobEntries(jobEntryCopy2)).thenReturn(1);
        Mockito.when(jobMetaSpy.findPrevJobEntry(jobEntryCopy2, 0)).thenReturn(jobEntryCopy3);
        Mockito.when(jobMetaSpy.findNrPrevJobEntries(jobEntryCopy3)).thenReturn(1);
        Mockito.when(jobMetaSpy.findPrevJobEntry(jobEntryCopy3, 0)).thenReturn(jobEntryCopy4);
        Mockito.when(jobMetaSpy.findNrPrevJobEntries(jobEntryCopy4)).thenReturn(1);
        Mockito.when(jobMetaSpy.findPrevJobEntry(jobEntryCopy4, 0)).thenReturn(jobEntryCopy3);
        // check no StackOverflow error
        Assert.assertFalse(jobMetaSpy.hasLoop(jobEntryCopyMain));
    }

    @Test
    public void testSetInternalEntryCurrentDirectoryWithFilename() {
        JobMeta jobMetaTest = new JobMeta();
        jobMetaTest.setFilename("hasFilename");
        jobMetaTest.setVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY, "Original value defined at run execution");
        jobMetaTest.setVariable(INTERNAL_VARIABLE_JOB_FILENAME_DIRECTORY, "file:///C:/SomeFilenameDirectory");
        jobMetaTest.setVariable(INTERNAL_VARIABLE_JOB_REPOSITORY_DIRECTORY, "/SomeRepDirectory");
        jobMetaTest.setInternalEntryCurrentDirectory();
        Assert.assertEquals("file:///C:/SomeFilenameDirectory", jobMetaTest.getVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY));
    }

    @Test
    public void testSetInternalEntryCurrentDirectoryWithRepository() {
        JobMeta jobMetaTest = new JobMeta();
        RepositoryDirectoryInterface path = Mockito.mock(RepositoryDirectoryInterface.class);
        Mockito.when(path.getPath()).thenReturn("aPath");
        jobMetaTest.setRepository(Mockito.mock(Repository.class));
        jobMetaTest.setRepositoryDirectory(path);
        jobMetaTest.setVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY, "Original value defined at run execution");
        jobMetaTest.setVariable(INTERNAL_VARIABLE_JOB_FILENAME_DIRECTORY, "file:///C:/SomeFilenameDirectory");
        jobMetaTest.setVariable(INTERNAL_VARIABLE_JOB_REPOSITORY_DIRECTORY, "/SomeRepDirectory");
        jobMetaTest.setInternalEntryCurrentDirectory();
        Assert.assertEquals("/SomeRepDirectory", jobMetaTest.getVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY));
    }

    @Test
    public void testSetInternalEntryCurrentDirectoryWithoutFilenameOrRepository() {
        JobMeta jobMetaTest = new JobMeta();
        jobMetaTest.setVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY, "Original value defined at run execution");
        jobMetaTest.setVariable(INTERNAL_VARIABLE_JOB_FILENAME_DIRECTORY, "file:///C:/SomeFilenameDirectory");
        jobMetaTest.setVariable(INTERNAL_VARIABLE_JOB_REPOSITORY_DIRECTORY, "/SomeRepDirectory");
        jobMetaTest.setInternalEntryCurrentDirectory();
        Assert.assertEquals("Original value defined at run execution", jobMetaTest.getVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY));
    }

    @Test
    public void testUpdateCurrentDirWithFilename() {
        JobMeta jobMetaTest = new JobMeta();
        jobMetaTest.setFilename("hasFilename");
        jobMetaTest.setVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY, "Original value defined at run execution");
        jobMetaTest.setVariable(INTERNAL_VARIABLE_JOB_FILENAME_DIRECTORY, "file:///C:/SomeFilenameDirectory");
        jobMetaTest.setVariable(INTERNAL_VARIABLE_JOB_REPOSITORY_DIRECTORY, "/SomeRepDirectory");
        jobMetaTest.updateCurrentDir();
        Assert.assertEquals("file:///C:/SomeFilenameDirectory", jobMetaTest.getVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY));
    }

    @Test
    public void testUpdateCurrentDirWithRepository() {
        JobMeta jobMetaTest = new JobMeta();
        RepositoryDirectoryInterface path = Mockito.mock(RepositoryDirectoryInterface.class);
        Mockito.when(path.getPath()).thenReturn("aPath");
        jobMetaTest.setRepository(Mockito.mock(Repository.class));
        jobMetaTest.setRepositoryDirectory(path);
        jobMetaTest.setVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY, "Original value defined at run execution");
        jobMetaTest.setVariable(INTERNAL_VARIABLE_JOB_FILENAME_DIRECTORY, "file:///C:/SomeFilenameDirectory");
        jobMetaTest.setVariable(INTERNAL_VARIABLE_JOB_REPOSITORY_DIRECTORY, "/SomeRepDirectory");
        jobMetaTest.updateCurrentDir();
        Assert.assertEquals("/SomeRepDirectory", jobMetaTest.getVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY));
    }

    @Test
    public void testUpdateCurrentDirWithoutFilenameOrRepository() {
        JobMeta jobMetaTest = new JobMeta();
        jobMetaTest.setVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY, "Original value defined at run execution");
        jobMetaTest.setVariable(INTERNAL_VARIABLE_JOB_FILENAME_DIRECTORY, "file:///C:/SomeFilenameDirectory");
        jobMetaTest.setVariable(INTERNAL_VARIABLE_JOB_REPOSITORY_DIRECTORY, "/SomeRepDirectory");
        jobMetaTest.updateCurrentDir();
        Assert.assertEquals("Original value defined at run execution", jobMetaTest.getVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY));
    }
}

