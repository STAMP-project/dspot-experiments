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
package org.pentaho.di.trans.steps.textfileinput;


import TextFileInputMeta.FILE_FORMAT_DOS;
import TextFileInputMeta.FILE_FORMAT_MIXED;
import TextFileInputMeta.FILE_FORMAT_UNIX;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.fileinput.FileInputList;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.playlist.FilePlayListAll;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.TransTestingUtil;
import org.pentaho.di.trans.step.errorhandling.FileErrorHandler;
import org.pentaho.di.trans.steps.StepMockUtil;

import static TextFileInputMeta.FILE_FORMAT_UNIX;


/**
 *
 *
 * @deprecated replaced by implementation in the ...steps.fileinput.text package
 */
public class TextFileInputTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testGetLineDOS() throws UnsupportedEncodingException, KettleFileException {
        String input = "col1\tcol2\tcol3\r\ndata1\tdata2\tdata3\r\n";
        String expected = "col1\tcol2\tcol3";
        String output = TextFileInput.getLine(null, TextFileInputTest.getInputStreamReader(input), FILE_FORMAT_DOS, new StringBuilder(1000));
        Assert.assertEquals(expected, output);
    }

    @Test
    public void testGetLineUnix() throws UnsupportedEncodingException, KettleFileException {
        String input = "col1\tcol2\tcol3\ndata1\tdata2\tdata3\n";
        String expected = "col1\tcol2\tcol3";
        String output = TextFileInput.getLine(null, TextFileInputTest.getInputStreamReader(input), FILE_FORMAT_UNIX, new StringBuilder(1000));
        Assert.assertEquals(expected, output);
    }

    @Test
    public void testGetLineOSX() throws UnsupportedEncodingException, KettleFileException {
        String input = "col1\tcol2\tcol3\rdata1\tdata2\tdata3\r";
        String expected = "col1\tcol2\tcol3";
        String output = TextFileInput.getLine(null, TextFileInputTest.getInputStreamReader(input), FILE_FORMAT_UNIX, new StringBuilder(1000));
        Assert.assertEquals(expected, output);
    }

    @Test
    public void testGetLineMixed() throws UnsupportedEncodingException, KettleFileException {
        String input = "col1\tcol2\tcol3\r\ndata1\tdata2\tdata3\r";
        String expected = "col1\tcol2\tcol3";
        String output = TextFileInput.getLine(null, TextFileInputTest.getInputStreamReader(input), FILE_FORMAT_MIXED, new StringBuilder(1000));
        Assert.assertEquals(expected, output);
    }

    @Test(timeout = 100)
    public void test_PDI695() throws UnsupportedEncodingException, KettleFileException {
        String inputDOS = "col1\tcol2\tcol3\r\ndata1\tdata2\tdata3\r\n";
        String inputUnix = "col1\tcol2\tcol3\ndata1\tdata2\tdata3\n";
        String inputOSX = "col1\tcol2\tcol3\rdata1\tdata2\tdata3\r";
        String expected = "col1\tcol2\tcol3";
        Assert.assertEquals(expected, TextFileInput.getLine(null, TextFileInputTest.getInputStreamReader(inputDOS), FILE_FORMAT_UNIX, new StringBuilder(1000)));
        Assert.assertEquals(expected, TextFileInput.getLine(null, TextFileInputTest.getInputStreamReader(inputUnix), FILE_FORMAT_UNIX, new StringBuilder(1000)));
        Assert.assertEquals(expected, TextFileInput.getLine(null, TextFileInputTest.getInputStreamReader(inputOSX), FILE_FORMAT_UNIX, new StringBuilder(1000)));
    }

    @Test
    public void readWrappedInputWithoutHeaders() throws Exception {
        final String content = new StringBuilder().append("r1c1").append('\n').append(";r1c2\n").append("r2c1").append('\n').append(";r2c2").toString();
        final String virtualFile = TextFileInputTest.createVirtualFile("pdi-2607.txt", content);
        TextFileInputMeta meta = new TextFileInputMeta();
        meta.setLineWrapped(true);
        meta.setNrWraps(1);
        meta.setInputFields(new TextFileInputField[]{ TextFileInputTest.field("col1"), TextFileInputTest.field("col2") });
        meta.setFileCompression("None");
        meta.setFileType("CSV");
        meta.setHeader(false);
        meta.setNrHeaderLines((-1));
        meta.setFooter(false);
        meta.setNrFooterLines((-1));
        TextFileInputData data = new TextFileInputData();
        data.setFiles(new FileInputList());
        data.getFiles().addFile(KettleVFS.getFileObject(virtualFile));
        data.outputRowMeta = new RowMeta();
        data.outputRowMeta.addValueMeta(new ValueMetaString("col1"));
        data.outputRowMeta.addValueMeta(new ValueMetaString("col2"));
        data.dataErrorLineHandler = Mockito.mock(FileErrorHandler.class);
        data.fileFormatType = FILE_FORMAT_UNIX;
        data.separator = ";";
        data.filterProcessor = new TextFileFilterProcessor(new TextFileFilter[0]);
        data.filePlayList = new FilePlayListAll();
        TextFileInput input = StepMockUtil.getStep(TextFileInput.class, TextFileInputMeta.class, "test");
        List<Object[]> output = TransTestingUtil.execute(input, meta, data, 2, false);
        TransTestingUtil.assertResult(new Object[]{ "r1c1", "r1c2" }, output.get(0));
        TransTestingUtil.assertResult(new Object[]{ "r2c1", "r2c2" }, output.get(1));
        TextFileInputTest.deleteVfsFile(virtualFile);
    }

    @Test
    public void readInputWithMissedValues() throws Exception {
        final String virtualFile = TextFileInputTest.createVirtualFile("pdi-14172.txt", "1,1,1\n", "2,,2\n");
        TextFileInputMeta meta = new TextFileInputMeta();
        TextFileInputField field2 = TextFileInputTest.field("col2");
        field2.setRepeated(true);
        meta.setInputFields(new TextFileInputField[]{ TextFileInputTest.field("col1"), field2, TextFileInputTest.field("col3") });
        meta.setFileCompression("None");
        meta.setFileType("CSV");
        meta.setHeader(false);
        meta.setNrHeaderLines((-1));
        meta.setFooter(false);
        meta.setNrFooterLines((-1));
        TextFileInputData data = new TextFileInputData();
        data.setFiles(new FileInputList());
        data.getFiles().addFile(KettleVFS.getFileObject(virtualFile));
        data.outputRowMeta = new RowMeta();
        data.outputRowMeta.addValueMeta(new ValueMetaString("col1"));
        data.outputRowMeta.addValueMeta(new ValueMetaString("col2"));
        data.outputRowMeta.addValueMeta(new ValueMetaString("col3"));
        data.dataErrorLineHandler = Mockito.mock(FileErrorHandler.class);
        data.fileFormatType = FILE_FORMAT_UNIX;
        data.separator = ",";
        data.filterProcessor = new TextFileFilterProcessor(new TextFileFilter[0]);
        data.filePlayList = new FilePlayListAll();
        TextFileInput input = StepMockUtil.getStep(TextFileInput.class, TextFileInputMeta.class, "test");
        List<Object[]> output = TransTestingUtil.execute(input, meta, data, 2, false);
        TransTestingUtil.assertResult(new Object[]{ "1", "1", "1" }, output.get(0));
        TransTestingUtil.assertResult(new Object[]{ "2", "1", "2" }, output.get(1));
        TextFileInputTest.deleteVfsFile(virtualFile);
    }

    @Test
    public void readInputWithDefaultValues() throws Exception {
        final String virtualFile = TextFileInputTest.createVirtualFile("pdi-14832.txt", "1,\n");
        TextFileInputMeta meta = new TextFileInputMeta();
        TextFileInputField field2 = TextFileInputTest.field("col2");
        field2.setIfNullValue("DEFAULT");
        meta.setInputFields(new TextFileInputField[]{ TextFileInputTest.field("col1"), field2 });
        meta.setFileCompression("None");
        meta.setFileType("CSV");
        meta.setHeader(false);
        meta.setNrHeaderLines((-1));
        meta.setFooter(false);
        meta.setNrFooterLines((-1));
        TextFileInputData data = new TextFileInputData();
        data.setFiles(new FileInputList());
        data.getFiles().addFile(KettleVFS.getFileObject(virtualFile));
        data.outputRowMeta = new RowMeta();
        data.outputRowMeta.addValueMeta(new ValueMetaString("col1"));
        data.outputRowMeta.addValueMeta(new ValueMetaString("col2"));
        data.dataErrorLineHandler = Mockito.mock(FileErrorHandler.class);
        data.fileFormatType = FILE_FORMAT_UNIX;
        data.separator = ",";
        data.filterProcessor = new TextFileFilterProcessor(new TextFileFilter[0]);
        data.filePlayList = new FilePlayListAll();
        TextFileInput input = StepMockUtil.getStep(TextFileInput.class, TextFileInputMeta.class, "test");
        List<Object[]> output = TransTestingUtil.execute(input, meta, data, 1, false);
        TransTestingUtil.assertResult(new Object[]{ "1", "DEFAULT" }, output.get(0));
        TextFileInputTest.deleteVfsFile(virtualFile);
    }

    /**
     * PDI-14390 Text file input throws NPE if skipping error rows and passing through incoming fieds
     *
     * @throws Exception
     * 		
     */
    @Test
    public void convertLineToRowTest() throws Exception {
        LogChannelInterface log = Mockito.mock(LogChannelInterface.class);
        TextFileLine textFileLine = Mockito.mock(TextFileLine.class);
        textFileLine.line = "testData1;testData2;testData3";
        InputFileMetaInterface info = Mockito.mock(InputFileMetaInterface.class);
        TextFileInputField[] textFileInputFields = new TextFileInputField[]{ new TextFileInputField(), new TextFileInputField(), new TextFileInputField() };
        Mockito.doReturn(textFileInputFields).when(info).getInputFields();
        Mockito.doReturn("CSV").when(info).getFileType();
        Mockito.doReturn("/").when(info).getEscapeCharacter();
        Mockito.doReturn(true).when(info).isErrorIgnored();
        Mockito.doReturn(true).when(info).isErrorLineSkipped();
        RowMetaInterface outputRowMeta = Mockito.mock(RowMetaInterface.class);
        Mockito.doReturn(15).when(outputRowMeta).size();
        ValueMetaInterface valueMetaWithError = Mockito.mock(ValueMetaInterface.class);
        Mockito.doThrow(new KettleValueException("Error converting")).when(valueMetaWithError).convertDataFromString(Mockito.anyString(), Mockito.any(ValueMetaInterface.class), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt());
        Mockito.doReturn(valueMetaWithError).when(outputRowMeta).getValueMeta(Mockito.anyInt());
        // it should run without NPE
        TextFileInput.convertLineToRow(log, textFileLine, info, new Object[3], 1, outputRowMeta, Mockito.mock(RowMetaInterface.class), null, 1L, ";", null, "/", Mockito.mock(FileErrorHandler.class), false, false, false, false, false, false, false, false, null, null, false, new Date(), null, null, null, 1L);
    }
}

