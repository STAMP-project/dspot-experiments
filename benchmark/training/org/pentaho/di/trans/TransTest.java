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
package org.pentaho.di.trans;


import Const.INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY;
import Const.INTERNAL_VARIABLE_TRANSFORMATION_FILENAME_DIRECTORY;
import Const.INTERNAL_VARIABLE_TRANSFORMATION_REPOSITORY_DIRECTORY;
import LogChannel.GENERAL;
import Trans.STRING_FINISHED;
import com.google.common.collect.ImmutableList;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.ProgressMonitorListener;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.StepLogTable;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;


@RunWith(MockitoJUnitRunner.class)
public class TransTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Mock
    private StepInterface stepMock;

    @Mock
    private StepInterface stepMock2;

    @Mock
    private StepDataInterface data;

    @Mock
    private StepDataInterface data2;

    @Mock
    private StepMeta stepMeta;

    @Mock
    private StepMeta stepMeta2;

    @Mock
    private TransMeta transMeta;

    int count = 10000;

    Trans trans;

    TransMeta meta;

    /**
     * PDI-14948 - Execution of trans with no steps never ends
     */
    @Test(timeout = 1000)
    public void transWithNoStepsIsNotEndless() throws Exception {
        Trans transWithNoSteps = new Trans(new TransMeta());
        transWithNoSteps = Mockito.spy(transWithNoSteps);
        transWithNoSteps.prepareExecution(new String[]{  });
        transWithNoSteps.startThreads();
        // check trans lifecycle is not corrupted
        Mockito.verify(transWithNoSteps).fireTransStartedListeners();
        Mockito.verify(transWithNoSteps).fireTransFinishedListeners();
    }

    @Test
    public void testFindDatabaseWithEncodedConnectionName() {
        DatabaseMeta dbMeta1 = new DatabaseMeta("encoded_DBConnection", "Oracle", "localhost", "access", "test", "111", "test", "test");
        dbMeta1.setDisplayName("encoded.DBConnection");
        meta.addDatabase(dbMeta1);
        DatabaseMeta dbMeta2 = new DatabaseMeta("normalDBConnection", "Oracle", "localhost", "access", "test", "111", "test", "test");
        dbMeta2.setDisplayName("normalDBConnection");
        meta.addDatabase(dbMeta2);
        DatabaseMeta databaseMeta = meta.findDatabase(dbMeta1.getDisplayName());
        Assert.assertNotNull(databaseMeta);
        Assert.assertEquals(databaseMeta.getName(), "encoded_DBConnection");
        Assert.assertEquals(databaseMeta.getDisplayName(), "encoded.DBConnection");
    }

    /**
     * PDI-10762 - Trans and TransMeta leak
     */
    @Test
    public void testLoggingObjectIsNotLeakInMeta() {
        String expected = meta.log.getLogChannelId();
        meta.clear();
        String actual = meta.log.getLogChannelId();
        Assert.assertEquals("Use same logChannel for empty constructors, or assign General level for clear() calls", expected, actual);
    }

    /**
     * PDI-10762 - Trans and TransMeta leak
     */
    @Test
    public void testLoggingObjectIsNotLeakInTrans() throws KettleException {
        Repository rep = Mockito.mock(Repository.class);
        RepositoryDirectoryInterface repInt = Mockito.mock(RepositoryDirectoryInterface.class);
        Mockito.when(rep.loadTransformation(Mockito.anyString(), Mockito.any(RepositoryDirectoryInterface.class), Mockito.any(ProgressMonitorListener.class), Mockito.anyBoolean(), Mockito.anyString())).thenReturn(meta);
        Mockito.when(rep.findDirectory(Mockito.anyString())).thenReturn(repInt);
        Trans trans = new Trans(meta, rep, "junit", "junitDir", "fileName");
        Assert.assertEquals("Log channel General assigned", GENERAL.getLogChannelId(), trans.log.getLogChannelId());
    }

    /**
     * PDI-5229 - ConcurrentModificationException when restarting transformation Test that listeners can be accessed
     * concurrently during transformation finish
     *
     * @throws KettleException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testTransFinishListenersConcurrentModification() throws InterruptedException, KettleException {
        CountDownLatch start = new CountDownLatch(1);
        TransTest.TransFinishListenerAdder add = new TransTest.TransFinishListenerAdder(trans, start);
        TransTest.TransFinishListenerFirer firer = new TransTest.TransFinishListenerFirer(trans, start);
        startThreads(add, firer, start);
        Assert.assertEquals("All listeners are added: no ConcurrentModificationException", count, add.c);
        Assert.assertEquals("All Finish listeners are iterated over: no ConcurrentModificationException", count, firer.c);
    }

    /**
     * Test that listeners can be accessed concurrently during transformation start
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testTransStartListenersConcurrentModification() throws InterruptedException {
        CountDownLatch start = new CountDownLatch(1);
        TransTest.TransFinishListenerAdder add = new TransTest.TransFinishListenerAdder(trans, start);
        TransTest.TransStartListenerFirer starter = new TransTest.TransStartListenerFirer(trans, start);
        startThreads(add, starter, start);
        Assert.assertEquals("All listeners are added: no ConcurrentModificationException", count, add.c);
        Assert.assertEquals("All Start listeners are iterated over: no ConcurrentModificationException", count, starter.c);
    }

    /**
     * Test that transformation stop listeners can be accessed concurrently
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testTransStoppedListenersConcurrentModification() throws InterruptedException {
        CountDownLatch start = new CountDownLatch(1);
        TransTest.TransStoppedCaller stopper = new TransTest.TransStoppedCaller(trans, start);
        TransTest.TransStopListenerAdder adder = new TransTest.TransStopListenerAdder(trans, start);
        startThreads(stopper, adder, start);
        Assert.assertEquals("All transformation stop listeners is added", count, adder.c);
        Assert.assertEquals("All stop call success", count, stopper.c);
    }

    @Test
    public void testPDI12424ParametersFromMetaAreCopiedToTrans() throws IOException, URISyntaxException, KettleException {
        String testParam = "testParam";
        String testParamValue = "testParamValue";
        TransMeta mockTransMeta = Mockito.mock(TransMeta.class);
        Mockito.when(mockTransMeta.listVariables()).thenReturn(new String[]{  });
        Mockito.when(mockTransMeta.listParameters()).thenReturn(new String[]{ testParam });
        Mockito.when(mockTransMeta.getParameterValue(testParam)).thenReturn(testParamValue);
        FileObject ktr = KettleVFS.createTempFile("parameters", ".ktr", "ram://");
        try (OutputStream outputStream = ktr.getContent().getOutputStream(true)) {
            InputStream inputStream = new ByteArrayInputStream("<transformation></transformation>".getBytes());
            IOUtils.copy(inputStream, outputStream);
        }
        Trans trans = new Trans(mockTransMeta, null, null, null, ktr.getURL().toURI().toString());
        Assert.assertEquals(testParamValue, trans.getParameterValue(testParam));
    }

    @Test
    public void testRecordsCleanUpMethodIsCalled() throws Exception {
        Database mockedDataBase = Mockito.mock(Database.class);
        Trans trans = Mockito.mock(Trans.class);
        StepLogTable stepLogTable = StepLogTable.getDefault(Mockito.mock(VariableSpace.class), Mockito.mock(HasDatabasesInterface.class));
        stepLogTable.setConnectionName("connection");
        TransMeta transMeta = new TransMeta();
        transMeta.setStepLogTable(stepLogTable);
        Mockito.when(trans.getTransMeta()).thenReturn(transMeta);
        Mockito.when(trans.createDataBase(ArgumentMatchers.any(DatabaseMeta.class))).thenReturn(mockedDataBase);
        Mockito.when(trans.getSteps()).thenReturn(new ArrayList());
        Mockito.doCallRealMethod().when(trans).writeStepLogInformation();
        trans.writeStepLogInformation();
        Mockito.verify(mockedDataBase).cleanupLogRecords(stepLogTable);
    }

    @Test
    public void testFireTransFinishedListeners() throws Exception {
        Trans trans = new Trans();
        TransListener mockListener = Mockito.mock(TransListener.class);
        trans.setTransListeners(Collections.singletonList(mockListener));
        trans.fireTransFinishedListeners();
        Mockito.verify(mockListener).transFinished(trans);
    }

    @Test(expected = KettleException.class)
    public void testFireTransFinishedListenersExceptionOnTransFinished() throws Exception {
        Trans trans = new Trans();
        TransListener mockListener = Mockito.mock(TransListener.class);
        Mockito.doThrow(KettleException.class).when(mockListener).transFinished(trans);
        trans.setTransListeners(Collections.singletonList(mockListener));
        trans.fireTransFinishedListeners();
    }

    @Test
    public void testFinishStatus() throws Exception {
        while (trans.isRunning()) {
            Thread.sleep(1);
        } 
        Assert.assertEquals(STRING_FINISHED, trans.getStatus());
    }

    @Test
    public void testSafeStop() {
        Mockito.when(stepMock.isSafeStopped()).thenReturn(false);
        Mockito.when(stepMock.getStepname()).thenReturn("stepName");
        trans.setSteps(ImmutableList.of(combi(stepMock, data, stepMeta)));
        Result result = trans.getResult();
        Assert.assertFalse(result.isSafeStop());
        Mockito.when(stepMock.isSafeStopped()).thenReturn(true);
        result = trans.getResult();
        Assert.assertTrue(result.isSafeStop());
    }

    @Test
    public void safeStopStopsInputStepsRightAway() throws KettleException {
        trans.setSteps(ImmutableList.of(combi(stepMock, data, stepMeta)));
        Mockito.when(transMeta.findPreviousSteps(stepMeta, true)).thenReturn(Collections.emptyList());
        trans.transMeta = transMeta;
        trans.safeStop();
        verifyStopped(stepMock, 1);
    }

    @Test
    public void safeLetsNonInputStepsKeepRunning() throws KettleException {
        trans.setSteps(ImmutableList.of(combi(stepMock, data, stepMeta), combi(stepMock2, data2, stepMeta2)));
        Mockito.when(transMeta.findPreviousSteps(stepMeta, true)).thenReturn(Collections.emptyList());
        // stepMeta2 will have stepMeta as previous, so is not an input step
        Mockito.when(transMeta.findPreviousSteps(stepMeta2, true)).thenReturn(ImmutableList.of(stepMeta));
        trans.transMeta = transMeta;
        trans.safeStop();
        verifyStopped(stepMock, 1);
        // non input step shouldn't have stop called
        verifyStopped(stepMock2, 0);
    }

    private abstract class TransKicker implements Runnable {
        protected Trans tr;

        protected int c = 0;

        protected CountDownLatch start;

        protected int max = count;

        TransKicker(Trans tr, CountDownLatch start) {
            this.tr = tr;
            this.start = start;
        }

        protected boolean isStopped() {
            (c)++;
            return (c) >= (max);
        }
    }

    private class TransStoppedCaller extends TransTest.TransKicker {
        TransStoppedCaller(Trans tr, CountDownLatch start) {
            super(tr, start);
        }

        @Override
        public void run() {
            try {
                start.await();
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
            while (!(isStopped())) {
                trans.stopAll();
            } 
        }
    }

    private class TransStopListenerAdder extends TransTest.TransKicker {
        TransStopListenerAdder(Trans tr, CountDownLatch start) {
            super(tr, start);
        }

        @Override
        public void run() {
            try {
                start.await();
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
            while (!(isStopped())) {
                trans.addTransStoppedListener(transStoppedListener);
            } 
        }
    }

    private class TransFinishListenerAdder extends TransTest.TransKicker {
        TransFinishListenerAdder(Trans tr, CountDownLatch start) {
            super(tr, start);
        }

        @Override
        public void run() {
            try {
                start.await();
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
            // run
            while (!(isStopped())) {
                tr.addTransListener(listener);
            } 
        }
    }

    private class TransFinishListenerFirer extends TransTest.TransKicker {
        TransFinishListenerFirer(Trans tr, CountDownLatch start) {
            super(tr, start);
        }

        @Override
        public void run() {
            try {
                start.await();
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
            // run
            while (!(isStopped())) {
                try {
                    tr.fireTransFinishedListeners();
                    // clean array blocking queue
                    tr.waitUntilFinished();
                } catch (KettleException e) {
                    throw new RuntimeException();
                }
            } 
        }
    }

    private class TransStartListenerFirer extends TransTest.TransKicker {
        TransStartListenerFirer(Trans tr, CountDownLatch start) {
            super(tr, start);
        }

        @Override
        public void run() {
            try {
                start.await();
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
            // run
            while (!(isStopped())) {
                try {
                    tr.fireTransStartedListeners();
                } catch (KettleException e) {
                    throw new RuntimeException();
                }
            } 
        }
    }

    private final TransListener listener = new TransListener() {
        @Override
        public void transStarted(Trans trans) throws KettleException {
        }

        @Override
        public void transActive(Trans trans) {
        }

        @Override
        public void transFinished(Trans trans) throws KettleException {
        }
    };

    private final TransStoppedListener transStoppedListener = new TransStoppedListener() {
        @Override
        public void transStopped(Trans trans) {
        }
    };

    @Test
    public void testNewTransformationsWithContainerObjectId() throws Exception {
        TransMeta meta = Mockito.mock(TransMeta.class);
        Mockito.doReturn(new String[]{ "X", "Y", "Z" }).when(meta).listVariables();
        Mockito.doReturn(new String[]{ "A", "B", "C" }).when(meta).listParameters();
        Mockito.doReturn("XYZ").when(meta).getVariable(ArgumentMatchers.anyString());
        Mockito.doReturn("").when(meta).getParameterDescription(ArgumentMatchers.anyString());
        Mockito.doReturn("").when(meta).getParameterDefault(ArgumentMatchers.anyString());
        Mockito.doReturn("ABC").when(meta).getParameterValue(ArgumentMatchers.anyString());
        String carteId = UUID.randomUUID().toString();
        Mockito.doReturn(carteId).when(meta).getContainerObjectId();
        Trans trans = new Trans(meta);
        Assert.assertEquals(carteId, trans.getContainerObjectId());
    }

    /**
     * This test demonstrates the issue fixed in PDI-17436.
     * When a job is scheduled twice, it gets the same log channel Id and both logs get merged
     */
    @Test
    public void testTwoTransformationsGetSameLogChannelId() throws Exception {
        TransMeta meta = Mockito.mock(TransMeta.class);
        Mockito.doReturn(new String[]{ "X", "Y", "Z" }).when(meta).listVariables();
        Mockito.doReturn(new String[]{ "A", "B", "C" }).when(meta).listParameters();
        Mockito.doReturn("XYZ").when(meta).getVariable(ArgumentMatchers.anyString());
        Mockito.doReturn("").when(meta).getParameterDescription(ArgumentMatchers.anyString());
        Mockito.doReturn("").when(meta).getParameterDefault(ArgumentMatchers.anyString());
        Mockito.doReturn("ABC").when(meta).getParameterValue(ArgumentMatchers.anyString());
        Trans trans1 = new Trans(meta);
        Trans trans2 = new Trans(meta);
        Assert.assertEquals(trans1.getLogChannelId(), trans2.getLogChannelId());
    }

    /**
     * This test demonstrates the fix for PDI-17436.
     * Two schedules -> two Carte object Ids -> two log channel Ids
     */
    @Test
    public void testTwoTransformationsGetDifferentLogChannelIdWithDifferentCarteId() throws Exception {
        TransMeta meta1 = Mockito.mock(TransMeta.class);
        Mockito.doReturn(new String[]{ "X", "Y", "Z" }).when(meta1).listVariables();
        Mockito.doReturn(new String[]{ "A", "B", "C" }).when(meta1).listParameters();
        Mockito.doReturn("XYZ").when(meta1).getVariable(ArgumentMatchers.anyString());
        Mockito.doReturn("").when(meta1).getParameterDescription(ArgumentMatchers.anyString());
        Mockito.doReturn("").when(meta1).getParameterDefault(ArgumentMatchers.anyString());
        Mockito.doReturn("ABC").when(meta1).getParameterValue(ArgumentMatchers.anyString());
        TransMeta meta2 = Mockito.mock(TransMeta.class);
        Mockito.doReturn(new String[]{ "X", "Y", "Z" }).when(meta2).listVariables();
        Mockito.doReturn(new String[]{ "A", "B", "C" }).when(meta2).listParameters();
        Mockito.doReturn("XYZ").when(meta2).getVariable(ArgumentMatchers.anyString());
        Mockito.doReturn("").when(meta2).getParameterDescription(ArgumentMatchers.anyString());
        Mockito.doReturn("").when(meta2).getParameterDefault(ArgumentMatchers.anyString());
        Mockito.doReturn("ABC").when(meta2).getParameterValue(ArgumentMatchers.anyString());
        String carteId1 = UUID.randomUUID().toString();
        String carteId2 = UUID.randomUUID().toString();
        Mockito.doReturn(carteId1).when(meta1).getContainerObjectId();
        Mockito.doReturn(carteId2).when(meta2).getContainerObjectId();
        Trans trans1 = new Trans(meta1);
        Trans trans2 = new Trans(meta2);
        Assert.assertNotEquals(trans1.getContainerObjectId(), trans2.getContainerObjectId());
        Assert.assertNotEquals(trans1.getLogChannelId(), trans2.getLogChannelId());
    }

    @Test
    public void testSetInternalEntryCurrentDirectoryWithFilename() {
        Trans transTest = new Trans();
        boolean hasFilename = true;
        boolean hasRepoDir = false;
        transTest.copyVariablesFrom(null);
        transTest.setVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY, "Original value defined at run execution");
        transTest.setVariable(INTERNAL_VARIABLE_TRANSFORMATION_FILENAME_DIRECTORY, "file:///C:/SomeFilenameDirectory");
        transTest.setVariable(INTERNAL_VARIABLE_TRANSFORMATION_REPOSITORY_DIRECTORY, "/SomeRepDirectory");
        transTest.setInternalEntryCurrentDirectory(hasFilename, hasRepoDir);
        Assert.assertEquals("file:///C:/SomeFilenameDirectory", transTest.getVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY));
    }

    @Test
    public void testSetInternalEntryCurrentDirectoryWithRepository() {
        Trans transTest = new Trans();
        boolean hasFilename = false;
        boolean hasRepoDir = true;
        transTest.copyVariablesFrom(null);
        transTest.setVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY, "Original value defined at run execution");
        transTest.setVariable(INTERNAL_VARIABLE_TRANSFORMATION_FILENAME_DIRECTORY, "file:///C:/SomeFilenameDirectory");
        transTest.setVariable(INTERNAL_VARIABLE_TRANSFORMATION_REPOSITORY_DIRECTORY, "/SomeRepDirectory");
        transTest.setInternalEntryCurrentDirectory(hasFilename, hasRepoDir);
        Assert.assertEquals("/SomeRepDirectory", transTest.getVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY));
    }

    @Test
    public void testSetInternalEntryCurrentDirectoryWithoutFilenameOrRepository() {
        Trans transTest = new Trans();
        transTest.copyVariablesFrom(null);
        boolean hasFilename = false;
        boolean hasRepoDir = false;
        transTest.setVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY, "Original value defined at run execution");
        transTest.setVariable(INTERNAL_VARIABLE_TRANSFORMATION_FILENAME_DIRECTORY, "file:///C:/SomeFilenameDirectory");
        transTest.setVariable(INTERNAL_VARIABLE_TRANSFORMATION_REPOSITORY_DIRECTORY, "/SomeRepDirectory");
        transTest.setInternalEntryCurrentDirectory(hasFilename, hasRepoDir);
        Assert.assertEquals("Original value defined at run execution", transTest.getVariable(INTERNAL_VARIABLE_ENTRY_CURRENT_DIRECTORY));
    }
}

