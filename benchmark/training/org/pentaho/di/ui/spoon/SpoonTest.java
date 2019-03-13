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
package org.pentaho.di.ui.spoon;


import FileDialogOperation.ORIGIN_SPOON;
import FileDialogOperation.SAVE;
import MainSpoonPerspective.ID;
import RepositoryObjectType.TRANSFORMATION;
import SWT.CANCEL;
import SWT.NO;
import SWT.YES;
import java.util.Collections;
import junit.framework.Assert;
import org.eclipse.swt.widgets.Shell;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.di.core.EngineMetaInterface;
import org.pentaho.di.core.NotePadMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.gui.Point;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryObject;
import org.pentaho.di.repository.RepositorySecurityProvider;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepErrorMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.csvinput.CsvInputMeta;
import org.pentaho.di.trans.steps.dummytrans.DummyTransMeta;
import org.pentaho.di.ui.core.FileDialogOperation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Spoon tests
 *
 * @author Pavel Sakun
 * @see Spoon
 */
public class SpoonTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private final Spoon spoon = Mockito.mock(Spoon.class);

    private final LogChannelInterface log = Mockito.mock(LogChannelInterface.class);

    private static SpoonPerspective mockSpoonPerspective = Mockito.mock(SpoonPerspective.class);

    private static SpoonPerspectiveManager perspective = SpoonPerspectiveManager.getInstance();

    /**
     * test two steps
     *
     * @see http://jira.pentaho.com/browse/PDI-689
     * @throws KettleException
     * 		
     */
    @Test
    public void testCopyPasteStepsErrorHandling() throws KettleException {
        final TransMeta transMeta = new TransMeta();
        // for check copy both step and hop
        StepMeta sourceStep = new StepMeta("CsvInput", "Step1", new CsvInputMeta());
        StepMeta targetStep = new StepMeta("Dummy", "Dummy Step1", new DummyTransMeta());
        sourceStep.setSelected(true);
        targetStep.setSelected(true);
        transMeta.addStep(sourceStep);
        transMeta.addStep(targetStep);
        StepErrorMeta errorMeta = new StepErrorMeta(transMeta, sourceStep, targetStep);
        sourceStep.setStepErrorMeta(errorMeta);
        errorMeta.setSourceStep(sourceStep);
        errorMeta.setTargetStep(targetStep);
        final int stepsSizeBefore = transMeta.getSteps().size();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                spoon.pasteXML(transMeta, ((String) (invocation.getArguments()[0])), Mockito.mock(Point.class));
                Assert.assertTrue("Steps was not copied", (stepsSizeBefore < (transMeta.getSteps().size())));
                // selected copied step
                for (StepMeta s : transMeta.getSelectedSteps()) {
                    if ((s.getStepMetaInterface()) instanceof CsvInputMeta) {
                        // check that stepError was copied
                        Assert.assertNotNull("Error hop was not copied", s.getStepErrorMeta());
                    }
                }
                return null;
            }
        }).when(spoon).toClipboard(ArgumentMatchers.anyString());
        spoon.copySelected(transMeta, transMeta.getSelectedSteps(), Collections.<NotePadMeta>emptyList());
    }

    /**
     * test copy one step with error handling
     *
     * @see http://jira.pentaho.com/browse/PDI-13358
     * @throws KettleException
     * 		
     */
    @Test
    public void testCopyPasteOneStepWithErrorHandling() throws KettleException {
        final TransMeta transMeta = new TransMeta();
        StepMeta sourceStep = new StepMeta("CsvInput", "Step1", new CsvInputMeta());
        StepMeta targetStep = new StepMeta("Dummy", "Dummy Step1", new DummyTransMeta());
        sourceStep.setSelected(true);
        transMeta.addStep(sourceStep);
        transMeta.addStep(targetStep);
        StepErrorMeta errorMeta = new StepErrorMeta(transMeta, sourceStep, targetStep);
        sourceStep.setStepErrorMeta(errorMeta);
        errorMeta.setSourceStep(sourceStep);
        errorMeta.setTargetStep(targetStep);
        final int stepsSizeBefore = transMeta.getSteps().size();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                spoon.pasteXML(transMeta, ((String) (invocation.getArguments()[0])), Mockito.mock(Point.class));
                Assert.assertTrue("Steps was not copied", (stepsSizeBefore < (transMeta.getSteps().size())));
                // selected copied step
                for (StepMeta s : transMeta.getSelectedSteps()) {
                    if ((s.getStepMetaInterface()) instanceof CsvInputMeta) {
                        // check that stepError was empty, because we copy only one step from pair
                        Assert.assertNull("Error hop was not copied", s.getStepErrorMeta());
                    }
                }
                return null;
            }
        }).when(spoon).toClipboard(ArgumentMatchers.anyString());
        spoon.copySelected(transMeta, transMeta.getSelectedSteps(), Collections.<NotePadMeta>emptyList());
    }

    /**
     * Testing displayed test in case versioning enabled
     *
     * @see http://jira.pentaho.com/browse/BACKLOG-11607
     * @throws KettleException
     * 		
     */
    @Test
    public void testSetShellTextForTransformationWVersionEnabled() {
        TransMeta mockTransMeta = Mockito.mock(TransMeta.class);
        Shell mockShell = SpoonTest.prepareSetShellTextTests(spoon, mockTransMeta, true, true, false, false, false, false, false, false);
        Mockito.verify(mockShell).setText("Spoon - [RepositoryName] transformationName v1.0");
    }

    @Test
    public void testSetShellTextForTransformationWVersionEnabledRepIsNull() {
        TransMeta mockTransMeta = Mockito.mock(TransMeta.class);
        Shell mockShell = SpoonTest.prepareSetShellTextTests(spoon, mockTransMeta, true, true, true, false, false, false, false, false);
        Mockito.verify(mockShell).setText("Spoon - transformationName v1.0");
    }

    @Test
    public void testSetShellTextForTransformationWVersionEnabledRevIsNull() {
        TransMeta mockTransMeta = Mockito.mock(TransMeta.class);
        Shell mockShell = SpoonTest.prepareSetShellTextTests(spoon, mockTransMeta, true, true, true, true, false, false, false, false);
        Mockito.verify(mockShell).setText("Spoon - transformationName");
    }

    @Test
    public void testSetShellTextForTransformationWVersionEnabledChanged() {
        TransMeta mockTransMeta = Mockito.mock(TransMeta.class);
        Shell mockShell = SpoonTest.prepareSetShellTextTests(spoon, mockTransMeta, true, true, false, false, true, false, false, false);
        Mockito.verify(mockShell).setText(("Spoon - [RepositoryName] transformationName v1.0 " + (BaseMessages.getString(Spoon.class, "Spoon.Various.Changed"))));
    }

    @Test
    public void testSetShellTextForTransformationWVersionEnabledNameIsNull() {
        TransMeta mockTransMeta = Mockito.mock(TransMeta.class);
        Shell mockShell = SpoonTest.prepareSetShellTextTests(spoon, mockTransMeta, true, true, false, false, false, true, false, false);
        Mockito.verify(mockShell).setText("Spoon - [RepositoryName] transformationFilename v1.0");
    }

    @Test
    public void testSetShellTextForTransformationWVersionEnabledNameFileNameNull() {
        TransMeta mockTransMeta = Mockito.mock(TransMeta.class);
        Shell mockShell = SpoonTest.prepareSetShellTextTests(spoon, mockTransMeta, true, true, false, false, false, true, true, false);
        Mockito.verify(mockShell).setText("Spoon - [RepositoryName] tabName v1.0");
    }

    @Test
    public void testSetShellTextForTransformationWVersionEnabledNameFileNameTabNull() {
        TransMeta mockTransMeta = Mockito.mock(TransMeta.class);
        Shell mockShell = SpoonTest.prepareSetShellTextTests(spoon, mockTransMeta, true, true, false, false, false, true, true, true);
        Mockito.verify(mockShell).setText((("Spoon - [RepositoryName] " + (BaseMessages.getString(Spoon.class, "Spoon.Various.NoName"))) + " v1.0"));
    }

    @Test
    public void testSetShellTextForTransformationWVersionDisabled() {
        TransMeta mockTransMeta = Mockito.mock(TransMeta.class);
        Shell mockShell = SpoonTest.prepareSetShellTextTests(spoon, mockTransMeta, false, true, false, false, false, false, false, false);
        Mockito.verify(mockShell).setText("Spoon - [RepositoryName] transformationName");
    }

    @Test
    public void testSetShellTextForJobWVersionEnabled() {
        JobMeta mockJobMeta = Mockito.mock(JobMeta.class);
        Shell mockShell = SpoonTest.prepareSetShellTextTests(spoon, mockJobMeta, true, false, false, false, false, false, false, false);
        Mockito.verify(mockShell).setText("Spoon - [RepositoryName] jobName v1.0");
    }

    @Test
    public void testSetShellTextForJobWVersionEnabledRepIsNull() {
        JobMeta mockJobMeta = Mockito.mock(JobMeta.class);
        Shell mockShell = SpoonTest.prepareSetShellTextTests(spoon, mockJobMeta, true, false, true, false, false, false, false, false);
        Mockito.verify(mockShell).setText("Spoon - jobName v1.0");
    }

    @Test
    public void testSetShellTextForJobWVersionEnabledRevIsNull() {
        JobMeta mockJobMeta = Mockito.mock(JobMeta.class);
        Shell mockShell = SpoonTest.prepareSetShellTextTests(spoon, mockJobMeta, true, false, true, true, false, false, false, false);
        Mockito.verify(mockShell).setText("Spoon - jobName");
    }

    @Test
    public void testSetShellTextForJobWVersionDisabled() {
        JobMeta mockJobMeta = Mockito.mock(JobMeta.class);
        Shell mockShell = SpoonTest.prepareSetShellTextTests(spoon, mockJobMeta, false, false, false, false, false, false, false, false);
        Mockito.verify(mockShell).setText("Spoon - [RepositoryName] jobName");
    }

    @Test
    public void testDelHop() throws Exception {
        StepMetaInterface fromStepMetaInterface = Mockito.mock(StepMetaInterface.class);
        StepMeta fromStep = new StepMeta();
        fromStep.setStepMetaInterface(fromStepMetaInterface);
        StepMetaInterface toStepMetaInterface = Mockito.mock(StepMetaInterface.class);
        StepMeta toStep = new StepMeta();
        toStep.setStepMetaInterface(toStepMetaInterface);
        TransHopMeta transHopMeta = new TransHopMeta();
        transHopMeta.setFromStep(fromStep);
        transHopMeta.setToStep(toStep);
        TransMeta transMeta = Mockito.mock(TransMeta.class);
        spoon.delHop(transMeta, transHopMeta);
        Mockito.verify(fromStepMetaInterface, Mockito.times(1)).cleanAfterHopFromRemove(toStep);
        Mockito.verify(toStepMetaInterface, Mockito.times(1)).cleanAfterHopToRemove(fromStep);
    }

    @Test
    public void testNullParamSaveToFile() throws Exception {
        Mockito.doCallRealMethod().when(spoon).saveToFile(ArgumentMatchers.any());
        Assert.assertFalse(spoon.saveToFile(null));
    }

    @Test
    public void testJobToRepSaveToFile() throws Exception {
        JobMeta mockJobMeta = Mockito.mock(JobMeta.class);
        SpoonTest.prepareSetSaveTests(spoon, log, SpoonTest.mockSpoonPerspective, mockJobMeta, false, false, ID, true, true, null, null, true, true);
        Mockito.doCallRealMethod().when(spoon).saveToFile(mockJobMeta);
        Assert.assertTrue(spoon.saveToFile(mockJobMeta));
        Mockito.verify(mockJobMeta).setRepository(spoon.rep);
        Mockito.verify(mockJobMeta).setMetaStore(spoon.metaStore);
        Mockito.verify(mockJobMeta).setFilename(null);
        Mockito.verify(spoon.delegates.tabs).renameTabs();
        Mockito.verify(spoon).enableMenus();
    }

    @Test
    public void testJobToFileSaveToFile() throws Exception {
        JobMeta mockJobMeta = Mockito.mock(JobMeta.class);
        SpoonTest.prepareSetSaveTests(spoon, log, SpoonTest.mockSpoonPerspective, mockJobMeta, true, false, "NotMainSpoonPerspective", true, true, null, "filename", true, true);
        Mockito.doCallRealMethod().when(spoon).saveToFile(mockJobMeta);
        Assert.assertTrue(spoon.saveToFile(mockJobMeta));
        Mockito.verify(mockJobMeta).setRepository(spoon.rep);
        Mockito.verify(mockJobMeta).setMetaStore(spoon.metaStore);
        Mockito.verify(spoon.delegates.tabs).renameTabs();
        Mockito.verify(spoon).enableMenus();
    }

    @Test
    public void testJobToFileWithoutNameSaveToFile() throws Exception {
        JobMeta mockJobMeta = Mockito.mock(JobMeta.class);
        SpoonTest.prepareSetSaveTests(spoon, log, SpoonTest.mockSpoonPerspective, mockJobMeta, true, false, "NotMainSpoonPerspective", true, true, null, null, true, true);
        Mockito.doCallRealMethod().when(spoon).saveToFile(mockJobMeta);
        Mockito.doReturn(true).when(spoon).saveFileAs(mockJobMeta);
        Assert.assertTrue(spoon.saveToFile(mockJobMeta));
        Mockito.verify(mockJobMeta).setRepository(spoon.rep);
        Mockito.verify(mockJobMeta).setMetaStore(spoon.metaStore);
        Mockito.verify(spoon.delegates.tabs).renameTabs();
        Mockito.verify(spoon).enableMenus();
    }

    @Test
    public void testJobToFileCantSaveToFile() throws Exception {
        JobMeta mockJobMeta = Mockito.mock(JobMeta.class);
        SpoonTest.prepareSetSaveTests(spoon, log, SpoonTest.mockSpoonPerspective, mockJobMeta, true, false, "NotMainSpoonPerspective", true, true, null, null, true, false);
        Mockito.doCallRealMethod().when(spoon).saveToFile(mockJobMeta);
        Mockito.doReturn(true).when(spoon).saveFileAs(mockJobMeta);
        Assert.assertFalse(spoon.saveToFile(mockJobMeta));
        Mockito.verify(mockJobMeta).setRepository(spoon.rep);
        Mockito.verify(mockJobMeta).setMetaStore(spoon.metaStore);
        // repo is null, meta filename is null and meta.canSave() returns false, therefore none of the save methods are
        // called on the meta and the meta isn't actually saved - tabs should not be renamed
        Mockito.verify(spoon.delegates.tabs, Mockito.never()).renameTabs();
        Mockito.verify(spoon).enableMenus();
        // now mock mockJobMeta.canSave() to return true, such that saveFileAs is called (also mocked to return true)
        Mockito.doReturn(true).when(mockJobMeta).canSave();
        spoon.saveToFile(mockJobMeta);
        // and verify that renameTabs is called
        Mockito.verify(spoon.delegates.tabs).renameTabs();
    }

    @Test
    public void testTransToRepSaveToFile() throws Exception {
        TransMeta mockTransMeta = Mockito.mock(TransMeta.class);
        SpoonTest.prepareSetSaveTests(spoon, log, SpoonTest.mockSpoonPerspective, mockTransMeta, false, false, ID, true, true, null, null, true, true);
        Mockito.doCallRealMethod().when(spoon).saveToFile(mockTransMeta);
        Assert.assertTrue(spoon.saveToFile(mockTransMeta));
        Mockito.verify(mockTransMeta).setRepository(spoon.rep);
        Mockito.verify(mockTransMeta).setMetaStore(spoon.metaStore);
        Mockito.verify(mockTransMeta).setFilename(null);
        Mockito.verify(spoon.delegates.tabs).renameTabs();
        Mockito.verify(spoon).enableMenus();
    }

    @Test
    public void testJobToRepSaveFileAs() throws Exception {
        JobMeta mockJobMeta = Mockito.mock(JobMeta.class);
        // passing a invalid type so not running GUIResource class
        SpoonTest.prepareSetSaveTests(spoon, log, SpoonTest.mockSpoonPerspective, mockJobMeta, false, false, ID, true, true, "Invalid TYPE", null, true, true);
        Mockito.doCallRealMethod().when(spoon).saveFileAs(mockJobMeta);
        Assert.assertTrue(spoon.saveFileAs(mockJobMeta));
        Mockito.verify(mockJobMeta).setRepository(spoon.rep);
        Mockito.verify(mockJobMeta).setMetaStore(spoon.metaStore);
        Mockito.verify(mockJobMeta).setObjectId(null);
        Mockito.verify(mockJobMeta).setFilename(null);
        Mockito.verify(spoon.delegates.tabs).findTabMapEntry(mockJobMeta);
        Mockito.verify(spoon).enableMenus();
    }

    @Test
    public void testJobToRepSaveFileAsFailed() throws Exception {
        JobMeta mockJobMeta = Mockito.mock(JobMeta.class);
        // passing a invalid type so not running GUIResource class
        SpoonTest.prepareSetSaveTests(spoon, log, SpoonTest.mockSpoonPerspective, mockJobMeta, false, false, ID, false, true, "Invalid TYPE", null, true, true);
        Mockito.doCallRealMethod().when(spoon).saveFileAs(mockJobMeta);
        Assert.assertFalse(spoon.saveFileAs(mockJobMeta));
        Mockito.verify(mockJobMeta).setRepository(spoon.rep);
        Mockito.verify(mockJobMeta).setMetaStore(spoon.metaStore);
        Mockito.verify(mockJobMeta).setObjectId(null);
        Mockito.verify(mockJobMeta).setFilename(null);
        Mockito.verify(spoon).enableMenus();
    }

    @Test
    public void testJobToXMLFileSaveFileAs() throws Exception {
        JobMeta mockJobMeta = Mockito.mock(JobMeta.class);
        // passing a invalid type so not running GUIResource class
        SpoonTest.prepareSetSaveTests(spoon, log, SpoonTest.mockSpoonPerspective, mockJobMeta, true, true, "NotMainSpoonPerspective", true, true, "Invalid TYPE", null, true, true);
        Mockito.doCallRealMethod().when(spoon).saveFileAs(mockJobMeta);
        Assert.assertTrue(spoon.saveFileAs(mockJobMeta));
        Mockito.verify(mockJobMeta).setRepository(spoon.rep);
        Mockito.verify(mockJobMeta).setMetaStore(spoon.metaStore);
        Mockito.verify(spoon.delegates.tabs).findTabMapEntry(mockJobMeta);
        Mockito.verify(spoon).enableMenus();
    }

    @Test
    public void testJobToXMLFileSaveFileAsFailed() throws Exception {
        JobMeta mockJobMeta = Mockito.mock(JobMeta.class);
        // passing a invalid type so not running GUIResource class
        SpoonTest.prepareSetSaveTests(spoon, log, SpoonTest.mockSpoonPerspective, mockJobMeta, true, true, "NotMainSpoonPerspective", true, false, "Invalid TYPE", null, true, true);
        Mockito.doCallRealMethod().when(spoon).saveFileAs(mockJobMeta);
        Assert.assertFalse(spoon.saveFileAs(mockJobMeta));
        Mockito.verify(mockJobMeta).setRepository(spoon.rep);
        Mockito.verify(mockJobMeta).setMetaStore(spoon.metaStore);
        Mockito.verify(spoon).enableMenus();
    }

    @Test
    public void testTransToRepSaveFileAs() throws Exception {
        TransMeta mockTransMeta = Mockito.mock(TransMeta.class);
        // passing a invalid type so not running GUIResource class
        SpoonTest.prepareSetSaveTests(spoon, log, SpoonTest.mockSpoonPerspective, mockTransMeta, false, false, ID, true, true, "Invalid TYPE", null, true, true);
        Mockito.doCallRealMethod().when(spoon).saveFileAs(mockTransMeta);
        Assert.assertTrue(spoon.saveFileAs(mockTransMeta));
        Mockito.verify(mockTransMeta).setRepository(spoon.rep);
        Mockito.verify(mockTransMeta).setMetaStore(spoon.metaStore);
        Mockito.verify(mockTransMeta).setObjectId(null);
        Mockito.verify(mockTransMeta).setFilename(null);
        Mockito.verify(spoon.delegates.tabs).findTabMapEntry(mockTransMeta);
        Mockito.verify(spoon).enableMenus();
    }

    @Test
    public void testTransToRepSaveFileAsFailed() throws Exception {
        TransMeta mockTransMeta = Mockito.mock(TransMeta.class);
        // passing a invalid type so not running GUIResource class
        SpoonTest.prepareSetSaveTests(spoon, log, SpoonTest.mockSpoonPerspective, mockTransMeta, false, false, ID, false, true, "Invalid TYPE", null, true, true);
        Mockito.doCallRealMethod().when(spoon).saveFileAs(mockTransMeta);
        Assert.assertFalse(spoon.saveFileAs(mockTransMeta));
        Mockito.verify(mockTransMeta).setRepository(spoon.rep);
        Mockito.verify(mockTransMeta).setMetaStore(spoon.metaStore);
        Mockito.verify(mockTransMeta).setObjectId(null);
        Mockito.verify(mockTransMeta).setFilename(null);
        Mockito.verify(spoon).enableMenus();
    }

    @Test
    public void testTransToXMLFileSaveFileAs() throws Exception {
        TransMeta mockTransMeta = Mockito.mock(TransMeta.class);
        // passing a invalid type so not running GUIResource class
        SpoonTest.prepareSetSaveTests(spoon, log, SpoonTest.mockSpoonPerspective, mockTransMeta, true, true, "NotMainSpoonPerspective", true, true, "Invalid TYPE", null, true, true);
        Mockito.doCallRealMethod().when(spoon).saveFileAs(mockTransMeta);
        Assert.assertTrue(spoon.saveFileAs(mockTransMeta));
        Mockito.verify(mockTransMeta).setRepository(spoon.rep);
        Mockito.verify(mockTransMeta).setMetaStore(spoon.metaStore);
        Mockito.verify(spoon.delegates.tabs).findTabMapEntry(mockTransMeta);
        Mockito.verify(spoon).enableMenus();
    }

    @Test
    public void testTransToXMLFileSaveFileAsFailed() throws Exception {
        TransMeta mockTransMeta = Mockito.mock(TransMeta.class);
        // passing a invalid type so not running GUIResource class
        SpoonTest.prepareSetSaveTests(spoon, log, SpoonTest.mockSpoonPerspective, mockTransMeta, true, true, "NotMainSpoonPerspective", true, false, "Invalid TYPE", null, true, true);
        Mockito.doCallRealMethod().when(spoon).saveFileAs(mockTransMeta);
        Assert.assertFalse(spoon.saveFileAs(mockTransMeta));
        Mockito.verify(mockTransMeta).setRepository(spoon.rep);
        Mockito.verify(mockTransMeta).setMetaStore(spoon.metaStore);
        Mockito.verify(spoon).enableMenus();
    }

    @Test
    public void testTransToRepSaveObjectIdNotNullToFile() throws Exception {
        TransMeta mockTransMeta = Mockito.mock(TransMeta.class);
        SpoonTest.prepareSetSaveTests(spoon, log, SpoonTest.mockSpoonPerspective, mockTransMeta, false, false, ID, true, true, null, null, false, true);
        Mockito.doCallRealMethod().when(spoon).saveToFile(mockTransMeta);
        Assert.assertTrue(spoon.saveToFile(mockTransMeta));
        Mockito.verify(mockTransMeta).setRepository(spoon.rep);
        Mockito.verify(mockTransMeta).setMetaStore(spoon.metaStore);
        Mockito.verify(mockTransMeta, Mockito.never()).setFilename(null);
        Mockito.verify(spoon.delegates.tabs).renameTabs();
        Mockito.verify(spoon).enableMenus();
    }

    @Test
    public void saveToRepository() throws Exception {
        JobMeta mockJobMeta = Mockito.mock(JobMeta.class);
        SpoonTest.prepareSetSaveTests(spoon, log, SpoonTest.mockSpoonPerspective, mockJobMeta, false, false, "NotMainSpoonPerspective", true, true, "filename", null, true, false);
        RepositoryDirectoryInterface dirMock = Mockito.mock(RepositoryDirectoryInterface.class);
        Mockito.doReturn("my/path").when(dirMock).getPath();
        Mockito.doReturn(dirMock).when(mockJobMeta).getRepositoryDirectory();
        Mockito.doReturn("trans").when(mockJobMeta).getName();
        RepositoryDirectoryInterface newDirMock = Mockito.mock(RepositoryDirectoryInterface.class);
        Mockito.doReturn("my/new/path").when(newDirMock).getPath();
        RepositoryObject repositoryObject = Mockito.mock(RepositoryObject.class);
        Mockito.doReturn(newDirMock).when(repositoryObject).getRepositoryDirectory();
        FileDialogOperation fileDlgOp = Mockito.mock(FileDialogOperation.class);
        Mockito.doReturn(repositoryObject).when(fileDlgOp).getRepositoryObject();
        Mockito.doReturn(fileDlgOp).when(spoon).getFileDialogOperation(SAVE, ORIGIN_SPOON);
        Mockito.doReturn("newTrans").when(repositoryObject).getName();
        Mockito.doCallRealMethod().when(spoon).saveToRepository(mockJobMeta, true);
        // mock a successful save
        Mockito.doReturn(true).when(spoon).saveToRepositoryConfirmed(mockJobMeta);
        spoon.saveToRepository(mockJobMeta, true);
        // verify that the meta name and directory have been updated and renameTabs is called
        Mockito.verify(spoon.delegates.tabs, Mockito.times(1)).renameTabs();
        Mockito.verify(mockJobMeta, Mockito.times(1)).setRepositoryDirectory(newDirMock);
        Mockito.verify(mockJobMeta, Mockito.never()).setRepositoryDirectory(dirMock);// verify that the dir is never set back

        Mockito.verify(mockJobMeta, Mockito.times(1)).setName("newTrans");
        Mockito.verify(mockJobMeta, Mockito.never()).setName("trans");// verify that the name is never set back

        // mock a failed save
        Mockito.doReturn(false).when(spoon).saveToRepositoryConfirmed(mockJobMeta);
        spoon.saveToRepository(mockJobMeta, true);
        // verify that the meta name and directory have not changed and renameTabs is not called (only once form the
        // previous test)
        Mockito.verify(spoon.delegates.tabs, Mockito.times(1)).renameTabs();
        Mockito.verify(mockJobMeta, Mockito.times(2)).setRepositoryDirectory(newDirMock);
        Mockito.verify(mockJobMeta, Mockito.times(1)).setRepositoryDirectory(dirMock);// verify that the dir is set back

        Mockito.verify(mockJobMeta, Mockito.times(2)).setName("newTrans");
        Mockito.verify(mockJobMeta, Mockito.times(1)).setName("trans");// verify that the name is set back

    }

    @Test
    public void testLoadLastUsedTransLocalWithRepository() throws Exception {
        String repositoryName = "repositoryName";
        String fileName = "fileName";
        setLoadLastUsedJobLocalWithRepository(false, repositoryName, null, fileName, true);
        Mockito.verify(spoon).openFile(fileName, true);
    }

    @Test
    public void testLoadLastUsedTransLocalNoRepository() throws Exception {
        String repositoryName = null;
        String fileName = "fileName";
        setLoadLastUsedJobLocalWithRepository(false, repositoryName, null, fileName, true);
        Mockito.verify(spoon).openFile(fileName, false);
    }

    @Test
    public void testLoadLastUsedTransLocalNoFilename() throws Exception {
        String repositoryName = null;
        String fileName = null;
        setLoadLastUsedJobLocalWithRepository(false, repositoryName, null, fileName, true);
        Mockito.verify(spoon, Mockito.never()).openFile(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testLoadLastUsedJobLocalWithRepository() throws Exception {
        String repositoryName = null;
        String fileName = "fileName";
        setLoadLastUsedJobLocalWithRepository(false, repositoryName, null, fileName, false);
        Mockito.verify(spoon).openFile(fileName, false);
    }

    @Test
    public void testLoadLastUsedRepTransNoRepository() throws Exception {
        String repositoryName = null;
        String fileName = "fileName";
        setLoadLastUsedJobLocalWithRepository(true, repositoryName, null, fileName, false);
        Mockito.verify(spoon, Mockito.never()).openFile(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testLoadLastUsedTransLocalWithRepositoryAtStartup() throws Exception {
        String repositoryName = "repositoryName";
        String fileName = "fileName";
        setLoadLastUsedJobLocalWithRepository(false, repositoryName, null, fileName, true, true);
        Mockito.verify(spoon).openFile(fileName, true);
    }

    @Test
    public void testLoadLastUsedTransLocalNoRepositoryAtStartup() throws Exception {
        String repositoryName = null;
        String fileName = "fileName";
        setLoadLastUsedJobLocalWithRepository(false, repositoryName, null, fileName, true, true);
        Mockito.verify(spoon).openFile(fileName, false);
    }

    @Test
    public void testLoadLastUsedTransLocalNoFilenameAtStartup() throws Exception {
        String repositoryName = null;
        String fileName = null;
        setLoadLastUsedJobLocalWithRepository(false, repositoryName, null, fileName, true, true);
        Mockito.verify(spoon, Mockito.never()).openFile(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testLoadLastUsedJobLocalWithRepositoryAtStartup() throws Exception {
        String repositoryName = null;
        String fileName = "fileName";
        setLoadLastUsedJobLocalWithRepository(false, repositoryName, null, fileName, false, true);
        Mockito.verify(spoon).openFile(fileName, false);
    }

    @Test
    public void testLoadLastUsedRepTransNoRepositoryAtStartup() throws Exception {
        String repositoryName = null;
        String fileName = "fileName";
        setLoadLastUsedJobLocalWithRepository(true, repositoryName, null, fileName, false, true);
        Mockito.verify(spoon, Mockito.never()).openFile(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testCancelPromptToSave() throws Exception {
        setPromptToSave(CANCEL, false);
        Assert.assertFalse(spoon.promptForSave());
    }

    @Test
    public void testNoPromptToSave() throws Exception {
        SpoonBrowser mockBrowser = setPromptToSave(NO, false);
        Assert.assertTrue(spoon.promptForSave());
        Mockito.verify(mockBrowser, Mockito.never()).applyChanges();
    }

    @Test
    public void testYesPromptToSave() throws Exception {
        SpoonBrowser mockBrowser = setPromptToSave(YES, false);
        Assert.assertTrue(spoon.promptForSave());
        Mockito.verify(mockBrowser).applyChanges();
    }

    @Test
    public void testCanClosePromptToSave() throws Exception {
        setPromptToSave(YES, true);
        Assert.assertTrue(spoon.promptForSave());
    }

    @Test
    public void testVersioningEnabled() throws Exception {
        Repository repository = Mockito.mock(Repository.class);
        RepositorySecurityProvider securityProvider = Mockito.mock(RepositorySecurityProvider.class);
        Mockito.doReturn(securityProvider).when(repository).getSecurityProvider();
        EngineMetaInterface jobTransMeta = Mockito.spy(new TransMeta());
        RepositoryDirectoryInterface repositoryDirectoryInterface = Mockito.mock(RepositoryDirectoryInterface.class);
        Mockito.doReturn("/home").when(repositoryDirectoryInterface).toString();
        Mockito.doReturn("trans").when(jobTransMeta).getName();
        Mockito.doReturn(TRANSFORMATION).when(jobTransMeta).getRepositoryElementType();
        Mockito.doReturn(true).when(jobTransMeta).getVersioningEnabled();
        boolean result = Spoon.isVersionEnabled(repository, jobTransMeta);
        assertTrue(result);
        Mockito.verify(securityProvider, Mockito.never()).isVersioningEnabled(Mockito.anyString());
    }

    @Test
    public void testVersioningDisabled() throws Exception {
        Repository repository = Mockito.mock(Repository.class);
        RepositorySecurityProvider securityProvider = Mockito.mock(RepositorySecurityProvider.class);
        Mockito.doReturn(securityProvider).when(repository).getSecurityProvider();
        EngineMetaInterface jobTransMeta = Mockito.spy(new TransMeta());
        RepositoryDirectoryInterface repositoryDirectoryInterface = Mockito.mock(RepositoryDirectoryInterface.class);
        Mockito.doReturn("/home").when(repositoryDirectoryInterface).toString();
        Mockito.doReturn("trans").when(jobTransMeta).getName();
        Mockito.doReturn(TRANSFORMATION).when(jobTransMeta).getRepositoryElementType();
        Mockito.doReturn(false).when(jobTransMeta).getVersioningEnabled();
        boolean result = Spoon.isVersionEnabled(repository, jobTransMeta);
        assertFalse(result);
        Mockito.verify(securityProvider, Mockito.never()).isVersioningEnabled(Mockito.anyString());
    }

    @Test
    public void testVersioningCheckingOnServer() throws Exception {
        Repository repository = Mockito.mock(Repository.class);
        RepositorySecurityProvider securityProvider = Mockito.mock(RepositorySecurityProvider.class);
        Mockito.doReturn(securityProvider).when(repository).getSecurityProvider();
        EngineMetaInterface jobTransMeta = Mockito.spy(new TransMeta());
        RepositoryDirectoryInterface repositoryDirectoryInterface = Mockito.mock(RepositoryDirectoryInterface.class);
        Mockito.doReturn("/home").when(repositoryDirectoryInterface).toString();
        Mockito.doReturn("trans").when(jobTransMeta).getName();
        Mockito.doReturn(TRANSFORMATION).when(jobTransMeta).getRepositoryElementType();
        Mockito.doReturn(true).when(securityProvider).isVersioningEnabled(Mockito.anyString());
        boolean result = Spoon.isVersionEnabled(repository, jobTransMeta);
        assertTrue(result);
    }

    @Test
    public void textGetFileType() {
        Assert.assertEquals("File", Spoon.getFileType(null));
        Assert.assertEquals("File", Spoon.getFileType(""));
        Assert.assertEquals("File", Spoon.getFileType(" "));
        Assert.assertEquals("File", Spoon.getFileType("foo"));
        Assert.assertEquals("File", Spoon.getFileType("foo/foe"));
        Assert.assertEquals("File", Spoon.getFileType("ktr"));
        Assert.assertEquals("File", Spoon.getFileType("ktr"));
        Assert.assertEquals("Transformation", Spoon.getFileType("foo/foe.ktr"));
        Assert.assertEquals("Transformation", Spoon.getFileType("foe.ktr"));
        Assert.assertEquals("Transformation", Spoon.getFileType(".ktr"));
        Assert.assertEquals("Job", Spoon.getFileType("foo/foe.kjb"));
        Assert.assertEquals("Job", Spoon.getFileType("foe.kjb"));
        Assert.assertEquals("Job", Spoon.getFileType(".kjb"));
    }
}

