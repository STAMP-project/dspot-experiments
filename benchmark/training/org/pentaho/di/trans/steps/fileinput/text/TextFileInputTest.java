/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2019 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.steps.fileinput.text;


import AbstractFileErrorHandler.NO_PARTS;
import TextFileInputMeta.FILE_FORMAT_DOS;
import TextFileInputMeta.FILE_FORMAT_MIXED;
import TextFileInputMeta.FILE_FORMAT_UNIX;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.TransTestingUtil;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.errorhandling.FileErrorHandler;
import org.pentaho.di.trans.steps.StepMockUtil;
import org.pentaho.di.trans.steps.file.BaseFileField;
import org.pentaho.di.trans.steps.file.IBaseFileInputReader;
import org.pentaho.di.trans.steps.file.IBaseFileInputStepControl;


public class TextFileInputTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testGetLineDOS() throws UnsupportedEncodingException, KettleFileException {
        String input = "col1\tcol2\tcol3\r\ndata1\tdata2\tdata3\r\n";
        String expected = "col1\tcol2\tcol3";
        String output = TextFileInputUtils.getLine(null, TextFileInputTest.getInputStreamReader(input), FILE_FORMAT_DOS, new StringBuilder(1000));
        Assert.assertEquals(expected, output);
    }

    @Test
    public void testGetLineUnix() throws UnsupportedEncodingException, KettleFileException {
        String input = "col1\tcol2\tcol3\ndata1\tdata2\tdata3\n";
        String expected = "col1\tcol2\tcol3";
        String output = TextFileInputUtils.getLine(null, TextFileInputTest.getInputStreamReader(input), FILE_FORMAT_UNIX, new StringBuilder(1000));
        Assert.assertEquals(expected, output);
    }

    @Test
    public void testGetLineOSX() throws UnsupportedEncodingException, KettleFileException {
        String input = "col1\tcol2\tcol3\rdata1\tdata2\tdata3\r";
        String expected = "col1\tcol2\tcol3";
        String output = TextFileInputUtils.getLine(null, TextFileInputTest.getInputStreamReader(input), FILE_FORMAT_UNIX, new StringBuilder(1000));
        Assert.assertEquals(expected, output);
    }

    @Test
    public void testGetLineMixed() throws UnsupportedEncodingException, KettleFileException {
        String input = "col1\tcol2\tcol3\r\ndata1\tdata2\tdata3\r";
        String expected = "col1\tcol2\tcol3";
        String output = TextFileInputUtils.getLine(null, TextFileInputTest.getInputStreamReader(input), FILE_FORMAT_MIXED, new StringBuilder(1000));
        Assert.assertEquals(expected, output);
    }

    @Test(timeout = 100)
    public void test_PDI695() throws UnsupportedEncodingException, KettleFileException {
        String inputDOS = "col1\tcol2\tcol3\r\ndata1\tdata2\tdata3\r\n";
        String inputUnix = "col1\tcol2\tcol3\ndata1\tdata2\tdata3\n";
        String inputOSX = "col1\tcol2\tcol3\rdata1\tdata2\tdata3\r";
        String expected = "col1\tcol2\tcol3";
        Assert.assertEquals(expected, TextFileInputUtils.getLine(null, TextFileInputTest.getInputStreamReader(inputDOS), FILE_FORMAT_UNIX, new StringBuilder(1000)));
        Assert.assertEquals(expected, TextFileInputUtils.getLine(null, TextFileInputTest.getInputStreamReader(inputUnix), FILE_FORMAT_UNIX, new StringBuilder(1000)));
        Assert.assertEquals(expected, TextFileInputUtils.getLine(null, TextFileInputTest.getInputStreamReader(inputOSX), FILE_FORMAT_UNIX, new StringBuilder(1000)));
    }

    @Test
    public void readWrappedInputWithoutHeaders() throws Exception {
        final String content = new StringBuilder().append("r1c1").append('\n').append(";r1c2\n").append("r2c1").append('\n').append(";r2c2").toString();
        final String virtualFile = TextFileInputTest.createVirtualFile("pdi-2607.txt", content);
        TextFileInputMeta meta = createMetaObject(TextFileInputTest.field("col1"), TextFileInputTest.field("col2"));
        meta.content.lineWrapped = true;
        meta.content.nrWraps = 1;
        TextFileInputData data = createDataObject(virtualFile, ";", "col1", "col2");
        TextFileInput input = StepMockUtil.getStep(TextFileInput.class, TextFileInputMeta.class, "test");
        List<Object[]> output = TransTestingUtil.execute(input, meta, data, 2, false);
        TransTestingUtil.assertResult(new Object[]{ "r1c1", "r1c2" }, output.get(0));
        TransTestingUtil.assertResult(new Object[]{ "r2c1", "r2c2" }, output.get(1));
        TextFileInputTest.deleteVfsFile(virtualFile);
    }

    @Test
    public void readInputWithMissedValues() throws Exception {
        final String virtualFile = TextFileInputTest.createVirtualFile("pdi-14172.txt", "1,1,1\n", "2,,2\n");
        BaseFileField field2 = TextFileInputTest.field("col2");
        field2.setRepeated(true);
        TextFileInputMeta meta = createMetaObject(TextFileInputTest.field("col1"), field2, TextFileInputTest.field("col3"));
        TextFileInputData data = createDataObject(virtualFile, ",", "col1", "col2", "col3");
        TextFileInput input = StepMockUtil.getStep(TextFileInput.class, TextFileInputMeta.class, "test");
        List<Object[]> output = TransTestingUtil.execute(input, meta, data, 2, false);
        TransTestingUtil.assertResult(new Object[]{ "1", "1", "1" }, output.get(0));
        TransTestingUtil.assertResult(new Object[]{ "2", "1", "2" }, output.get(1));
        TextFileInputTest.deleteVfsFile(virtualFile);
    }

    @Test
    public void readInputWithNonEmptyNullif() throws Exception {
        final String virtualFile = TextFileInputTest.createVirtualFile("pdi-14358.txt", "-,-\n");
        BaseFileField col2 = TextFileInputTest.field("col2");
        col2.setNullString("-");
        TextFileInputMeta meta = createMetaObject(TextFileInputTest.field("col1"), col2);
        TextFileInputData data = createDataObject(virtualFile, ",", "col1", "col2");
        TextFileInput input = StepMockUtil.getStep(TextFileInput.class, TextFileInputMeta.class, "test");
        List<Object[]> output = TransTestingUtil.execute(input, meta, data, 1, false);
        TransTestingUtil.assertResult(new Object[]{ "-" }, output.get(0));
        TextFileInputTest.deleteVfsFile(virtualFile);
    }

    @Test
    public void readInputWithDefaultValues() throws Exception {
        final String virtualFile = TextFileInputTest.createVirtualFile("pdi-14832.txt", "1,\n");
        BaseFileField col2 = TextFileInputTest.field("col2");
        col2.setIfNullValue("DEFAULT");
        TextFileInputMeta meta = createMetaObject(TextFileInputTest.field("col1"), col2);
        TextFileInputData data = createDataObject(virtualFile, ",", "col1", "col2");
        TextFileInput input = StepMockUtil.getStep(TextFileInput.class, TextFileInputMeta.class, "test");
        List<Object[]> output = TransTestingUtil.execute(input, meta, data, 1, false);
        TransTestingUtil.assertResult(new Object[]{ "1", "DEFAULT" }, output.get(0));
        TextFileInputTest.deleteVfsFile(virtualFile);
    }

    @Test
    public void testErrorHandlerLineNumber() throws Exception {
        final String content = new StringBuilder().append("123").append('\n').append("333\n").append("345").append('\n').append("773\n").append("aaa").append('\n').append("444").toString();
        final String virtualFile = TextFileInputTest.createVirtualFile("pdi-2607.txt", content);
        TextFileInputMeta meta = createMetaObject(TextFileInputTest.field("col1"));
        meta.inputFields[0].setType(1);
        meta.content.lineWrapped = false;
        meta.content.nrWraps = 1;
        meta.errorHandling.errorIgnored = true;
        TextFileInputData data = createDataObject(virtualFile, ";", "col1");
        data.dataErrorLineHandler = Mockito.mock(FileErrorHandler.class);
        TextFileInput input = StepMockUtil.getStep(TextFileInput.class, TextFileInputMeta.class, "test");
        List<Object[]> output = TransTestingUtil.execute(input, meta, data, 4, false);
        Mockito.verify(data.dataErrorLineHandler).handleLineError(4, NO_PARTS);
        TextFileInputTest.deleteVfsFile(virtualFile);
    }

    @Test
    public void testHandleOpenFileException() throws Exception {
        final String content = new StringBuilder().append("123").append('\n').append("333\n").toString();
        final String virtualFile = TextFileInputTest.createVirtualFile("pdi-16697.txt", content);
        TextFileInputMeta meta = createMetaObject(TextFileInputTest.field("col1"));
        meta.inputFields[0].setType(1);
        meta.errorHandling.errorIgnored = true;
        meta.errorHandling.skipBadFiles = true;
        TextFileInputData data = createDataObject(virtualFile, ";", "col1");
        data.dataErrorLineHandler = Mockito.mock(FileErrorHandler.class);
        TextFileInputTest.TestTextFileInput textFileInput = StepMockUtil.getStep(TextFileInputTest.TestTextFileInput.class, TextFileInputMeta.class, "test");
        StepMeta stepMeta = getStepMeta();
        Mockito.doReturn(true).when(stepMeta).isDoingErrorHandling();
        List<Object[]> output = TransTestingUtil.execute(textFileInput, meta, data, 0, false);
        TextFileInputTest.deleteVfsFile(virtualFile);
        Assert.assertEquals(1, data.rejectedFiles.size());
        Assert.assertEquals(0, getErrors());
    }

    @Test
    public void test_PDI17117() throws Exception {
        final String virtualFile = TextFileInputTest.createVirtualFile("pdi-14832.txt", "1,\n");
        BaseFileField col2 = TextFileInputTest.field("col2");
        col2.setIfNullValue("DEFAULT");
        TextFileInputMeta meta = createMetaObject(TextFileInputTest.field("col1"), col2);
        meta.inputFiles.passingThruFields = true;
        meta.inputFiles.acceptingFilenames = true;
        TextFileInputData data = createDataObject(virtualFile, ",", "col1", "col2");
        TextFileInput input = Mockito.spy(StepMockUtil.getStep(TextFileInput.class, TextFileInputMeta.class, "test"));
        RowSet rowset = Mockito.mock(RowSet.class);
        RowMetaInterface rwi = Mockito.mock(RowMetaInterface.class);
        Object[] obj1 = new Object[2];
        Object[] obj2 = new Object[2];
        Mockito.doReturn(rowset).when(input).findInputRowSet(null);
        Mockito.doReturn(null).when(input).getRowFrom(rowset);
        Mockito.when(input.getRowFrom(rowset)).thenReturn(obj1, obj2, null);
        Mockito.doReturn(rwi).when(rowset).getRowMeta();
        Mockito.when(rwi.getString(obj2, 0)).thenReturn("filename1", "filename2");
        List<Object[]> output = TransTestingUtil.execute(input, meta, data, 0, false);
        List<String> passThroughKeys = new ArrayList(data.passThruFields.keySet());
        org.pentaho.di.core.util.Assert.assertNotNull(passThroughKeys);
        // set order is not guaranteed - order alphabetically
        passThroughKeys.sort(String.CASE_INSENSITIVE_ORDER);
        Assert.assertEquals(2, passThroughKeys.size());
        org.pentaho.di.core.util.Assert.assertNotNull(passThroughKeys.get(0));
        org.pentaho.di.core.util.Assert.assertTrue(passThroughKeys.get(0).startsWith("0_file"));
        org.pentaho.di.core.util.Assert.assertTrue(passThroughKeys.get(0).endsWith("filename1"));
        org.pentaho.di.core.util.Assert.assertNotNull(passThroughKeys.get(1));
        org.pentaho.di.core.util.Assert.assertTrue(passThroughKeys.get(1).startsWith("1_file"));
        org.pentaho.di.core.util.Assert.assertTrue(passThroughKeys.get(1).endsWith("filename2"));
        TextFileInputTest.deleteVfsFile(virtualFile);
    }

    @Test
    public void testClose() throws Exception {
        TextFileInputMeta mockTFIM = createMetaObject(null);
        String virtualFile = TextFileInputTest.createVirtualFile("pdi-17267.txt", null);
        TextFileInputData mockTFID = createDataObject(virtualFile, ";", null);
        mockTFID.lineBuffer = new ArrayList();
        mockTFID.lineBuffer.add(new TextFileLine(null, 0L, null));
        mockTFID.lineBuffer.add(new TextFileLine(null, 0L, null));
        mockTFID.lineBuffer.add(new TextFileLine(null, 0L, null));
        mockTFID.filename = "";
        FileContent mockFileContent = Mockito.mock(FileContent.class);
        InputStream mockInputStream = Mockito.mock(InputStream.class);
        Mockito.when(mockFileContent.getInputStream()).thenReturn(mockInputStream);
        FileObject mockFO = Mockito.mock(FileObject.class);
        Mockito.when(mockFO.getContent()).thenReturn(mockFileContent);
        TextFileInputReader tFIR = new TextFileInputReader(Mockito.mock(IBaseFileInputStepControl.class), mockTFIM, mockTFID, mockFO, Mockito.mock(LogChannelInterface.class));
        Assert.assertEquals(3, mockTFID.lineBuffer.size());
        tFIR.close();
        // After closing the file, the buffer must be empty!
        Assert.assertEquals(0, mockTFID.lineBuffer.size());
    }

    public static class TestTextFileInput extends TextFileInput {
        public TestTextFileInput(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
            super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
        }

        @Override
        protected IBaseFileInputReader createReader(TextFileInputMeta meta, TextFileInputData data, FileObject file) throws Exception {
            throw new Exception(("Can not create reader for the file object " + file));
        }
    }
}

