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
package org.pentaho.di.trans.steps.textfileoutput;


import TextFileOutputData.FileStream;
import ValueMetaInterface.STORAGE_TYPE_BINARY_STRING;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.compress.CompressionOutputStream;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


/**
 * User: Dzmitry Stsiapanau Date: 10/18/13 Time: 2:23 PM
 */
public class TextFileOutputTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    /**
     *
     */
    private static final String EMPTY_FILE_NAME = "Empty File";

    /**
     *
     */
    private static final String EMPTY_STRING = "";

    public class TextFileOutputTestHandler extends TextFileOutput {
        public List<Throwable> errors = new ArrayList<Throwable>();

        private Object[] row;

        public TextFileOutputTestHandler(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
            super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
        }

        public void setRow(Object[] row) {
            this.row = row;
        }

        @Override
        public String buildFilename(String filename, boolean ziparchive) {
            return filename;
        }

        @Override
        public Object[] getRow() throws KettleException {
            return row;
        }

        @Override
        public void putRow(RowMetaInterface rowMeta, Object[] row) throws KettleStepException {
        }

        @Override
        public void logError(String message) {
            errors.add(new KettleException(message));
        }

        @Override
        public void logError(String message, Throwable thr) {
            errors.add(thr);
        }

        @Override
        public void logError(String message, Object... arguments) {
            errors.add(new KettleException(message));
        }
    }

    private static final String TEXT_FILE_OUTPUT_PREFIX = "textFileOutput";

    private static final String TEXT_FILE_OUTPUT_EXTENSION = ".txt";

    private static final String END_LINE = " endLine ";

    private static final String RESULT_ROWS = "\"some data\" \"another data\"\n" + "\"some data2\" \"another data2\"\n";

    private static final String TEST_PREVIOUS_DATA = "testPreviousData\n";

    private StepMockHelper<TextFileOutputMeta, TextFileOutputData> stepMockHelper;

    private TextFileField textFileField = new TextFileField("Name", 2, TextFileOutputTest.EMPTY_STRING, 10, 20, TextFileOutputTest.EMPTY_STRING, TextFileOutputTest.EMPTY_STRING, TextFileOutputTest.EMPTY_STRING, TextFileOutputTest.EMPTY_STRING);

    private TextFileField textFileField2 = new TextFileField("Surname", 2, TextFileOutputTest.EMPTY_STRING, 10, 20, TextFileOutputTest.EMPTY_STRING, TextFileOutputTest.EMPTY_STRING, TextFileOutputTest.EMPTY_STRING, TextFileOutputTest.EMPTY_STRING);

    private TextFileField[] textFileFields = new TextFileField[]{ textFileField, textFileField2 };

    private Object[] row = new Object[]{ "some data", "another data" };

    private Object[] row2 = new Object[]{ "some data2", "another data2" };

    private List<Object[]> emptyRows = new ArrayList<Object[]>();

    private List<Object[]> rows = new ArrayList<Object[]>();

    private List<String> contents = new ArrayList<String>();

    private TextFileOutput textFileOutput;

    {
        rows.add(row);
        rows.add(row2);
        contents.add(TextFileOutputTest.EMPTY_STRING);
        contents.add(TextFileOutputTest.EMPTY_STRING);
        contents.add(TextFileOutputTest.END_LINE);
        contents.add(TextFileOutputTest.END_LINE);
        contents.add(null);
        contents.add(null);
        contents.add(TextFileOutputTest.END_LINE);
        contents.add(TextFileOutputTest.END_LINE);
        contents.add(TextFileOutputTest.RESULT_ROWS);
        contents.add(TextFileOutputTest.RESULT_ROWS);
        contents.add(((TextFileOutputTest.RESULT_ROWS) + (TextFileOutputTest.END_LINE)));
        contents.add(((TextFileOutputTest.RESULT_ROWS) + (TextFileOutputTest.END_LINE)));
        contents.add(TextFileOutputTest.RESULT_ROWS);
        contents.add(TextFileOutputTest.RESULT_ROWS);
        contents.add(((TextFileOutputTest.RESULT_ROWS) + (TextFileOutputTest.END_LINE)));
        contents.add(((TextFileOutputTest.RESULT_ROWS) + (TextFileOutputTest.END_LINE)));
        contents.add(TextFileOutputTest.EMPTY_STRING);
        contents.add(TextFileOutputTest.TEST_PREVIOUS_DATA);
        contents.add(TextFileOutputTest.END_LINE);
        contents.add(((TextFileOutputTest.TEST_PREVIOUS_DATA) + (TextFileOutputTest.END_LINE)));
        contents.add(TextFileOutputTest.TEST_PREVIOUS_DATA);
        contents.add(TextFileOutputTest.TEST_PREVIOUS_DATA);
        contents.add(TextFileOutputTest.END_LINE);
        contents.add(((TextFileOutputTest.TEST_PREVIOUS_DATA) + (TextFileOutputTest.END_LINE)));
        contents.add(TextFileOutputTest.RESULT_ROWS);
        contents.add(((TextFileOutputTest.TEST_PREVIOUS_DATA) + (TextFileOutputTest.RESULT_ROWS)));
        contents.add(((TextFileOutputTest.RESULT_ROWS) + (TextFileOutputTest.END_LINE)));
        contents.add((((TextFileOutputTest.TEST_PREVIOUS_DATA) + (TextFileOutputTest.RESULT_ROWS)) + (TextFileOutputTest.END_LINE)));
        contents.add(TextFileOutputTest.RESULT_ROWS);
        contents.add(((TextFileOutputTest.TEST_PREVIOUS_DATA) + (TextFileOutputTest.RESULT_ROWS)));
        contents.add(((TextFileOutputTest.RESULT_ROWS) + (TextFileOutputTest.END_LINE)));
        contents.add((((TextFileOutputTest.TEST_PREVIOUS_DATA) + (TextFileOutputTest.RESULT_ROWS)) + (TextFileOutputTest.END_LINE)));
    }

    @Test
    public void testCloseFileDataOutIsNullCase() {
        textFileOutput = new TextFileOutput(stepMockHelper.stepMeta, stepMockHelper.stepDataInterface, 0, stepMockHelper.transMeta, stepMockHelper.trans);
        textFileOutput.data = Mockito.mock(TextFileOutputData.class);
        Assert.assertNull(textFileOutput.data.out);
        textFileOutput.closeFile();
    }

    @Test
    public void testCloseFileDataOutIsNotNullCase() throws IOException {
        textFileOutput = new TextFileOutput(stepMockHelper.stepMeta, stepMockHelper.stepDataInterface, 0, stepMockHelper.transMeta, stepMockHelper.trans);
        textFileOutput.data = Mockito.mock(TextFileOutputData.class);
        textFileOutput.data.out = Mockito.mock(CompressionOutputStream.class);
        textFileOutput.closeFile();
        Assert.assertNull(textFileOutput.data.out);
    }

    @Test
    public void testsIterate() {
        FileObject resultFile = null;
        FileObject contentFile;
        String content = null;
        Boolean[] bool = new Boolean[]{ false, true };
        int i = 0;
        for (Boolean fileExists : bool) {
            for (Boolean dataReceived : bool) {
                for (Boolean isDoNotOpenNewFileInit : bool) {
                    for (Boolean endLineExists : bool) {
                        for (Boolean append : bool) {
                            try {
                                resultFile = helpTestInit(fileExists, dataReceived, isDoNotOpenNewFileInit, endLineExists, append);
                                content = ((String) (contents.toArray()[(i++)]));
                                contentFile = createTemplateFile(content);
                                if (resultFile.exists()) {
                                    Assert.assertTrue(IOUtils.contentEquals(resultFile.getContent().getInputStream(), contentFile.getContent().getInputStream()));
                                } else {
                                    Assert.assertFalse(contentFile.exists());
                                }
                            } catch (Exception e) {
                                Assert.fail((((((((((((((((e.getMessage()) + "\n FileExists = ") + fileExists) + "\n DataReceived = ") + dataReceived) + "\n isDoNotOpenNewFileInit = ") + isDoNotOpenNewFileInit) + "\n EndLineExists = ") + endLineExists) + "\n Append = ") + append) + "\n Content = ") + content) + "\n resultFile = ") + resultFile));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Tests the RULE#1: If 'Do not create file at start' checkbox is cheked AND 'Add landing line of file' is NOT set AND
     * transformation does not pass any rows to the file input step, then NO output file should be created.
     *
     * @throws KettleException
     * 		
     */
    @Test
    public void testNoOpenFileCall_IfRule_1() throws KettleException {
        TextFileField tfFieldMock = Mockito.mock(TextFileField.class);
        TextFileField[] textFileFields = new TextFileField[]{ tfFieldMock };
        Mockito.when(stepMockHelper.initStepMetaInterface.getEndedLine()).thenReturn(TextFileOutputTest.EMPTY_STRING);
        Mockito.when(stepMockHelper.initStepMetaInterface.getOutputFields()).thenReturn(textFileFields);
        Mockito.when(stepMockHelper.initStepMetaInterface.isDoNotOpenNewFileInit()).thenReturn(true);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.getEndedLine()).thenReturn(TextFileOutputTest.EMPTY_STRING);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.getFileName()).thenReturn(TextFileOutputTest.EMPTY_FILE_NAME);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.isDoNotOpenNewFileInit()).thenReturn(true);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.getOutputFields()).thenReturn(textFileFields);
        textFileOutput = new TextFileOutput(stepMockHelper.stepMeta, stepMockHelper.stepDataInterface, 0, stepMockHelper.transMeta, stepMockHelper.trans);
        TextFileOutput textFileoutputSpy = Mockito.spy(textFileOutput);
        Mockito.doReturn(false).when(textFileoutputSpy).isWriteHeader(((TextFileOutputTest.TEXT_FILE_OUTPUT_PREFIX) + (TextFileOutputTest.TEXT_FILE_OUTPUT_EXTENSION)));
        Mockito.doNothing().when(textFileoutputSpy).initFileStreamWriter(TextFileOutputTest.EMPTY_FILE_NAME);
        try {
            Mockito.doNothing().when(textFileoutputSpy).flushOpenFiles(true);
        } catch (IOException e) {
        }
        textFileoutputSpy.init(stepMockHelper.initStepMetaInterface, stepMockHelper.initStepDataInterface);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.buildFilename(Mockito.anyString(), Mockito.anyString(), Mockito.anyObject(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyBoolean(), Mockito.anyObject())).thenReturn(((TextFileOutputTest.TEXT_FILE_OUTPUT_PREFIX) + (TextFileOutputTest.TEXT_FILE_OUTPUT_EXTENSION)));
        textFileoutputSpy.processRow(stepMockHelper.processRowsStepMetaInterface, stepMockHelper.initStepDataInterface);
        Mockito.verify(textFileoutputSpy, Mockito.never()).initFileStreamWriter(TextFileOutputTest.EMPTY_FILE_NAME);
        Mockito.verify(textFileoutputSpy, Mockito.never()).writeEndedLine();
        Mockito.verify(textFileoutputSpy).setOutputDone();
    }

    @Test
    public void containsSeparatorOrEnclosureIsNotUnnecessaryInvoked_SomeFieldsFromMeta() {
        TextFileField field = new TextFileField();
        field.setName("name");
        assertNotInvokedTwice(field);
    }

    @Test
    public void containsSeparatorOrEnclosureIsNotUnnecessaryInvoked_AllFieldsFromMeta() {
        assertNotInvokedTwice(null);
    }

    @Test
    public void testEndedLineVar() throws Exception {
        TextFileOutputData data = new TextFileOutputData();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        data.writer = baos;
        TextFileOutputMeta meta = new TextFileOutputMeta();
        meta.setEndedLine("${endvar}");
        meta.setDefault();
        meta.setEncoding("UTF-8");
        stepMockHelper.stepMeta.setStepMetaInterface(meta);
        TextFileOutput textFileOutput = new TextFileOutputTest.TextFileOutputTestHandler(stepMockHelper.stepMeta, data, 0, stepMockHelper.transMeta, stepMockHelper.trans);
        textFileOutput.meta = meta;
        textFileOutput.data = data;
        textFileOutput.setVariable("endvar", "this is the end");
        textFileOutput.writeEndedLine();
        Assert.assertEquals("this is the end", baos.toString("UTF-8"));
    }

    /**
     * PDI-15650
     * File Exists=N Flag Set=N Add Header=Y Append=Y
     * Result = File is created, header is written at top of file (this changed by the fix)
     *
     * @throws KettleException
     * 		
     */
    @Test
    public void testProcessRule_2() throws Exception {
        TextFileField tfFieldMock = Mockito.mock(TextFileField.class);
        TextFileField[] textFileFields = new TextFileField[]{ tfFieldMock };
        Mockito.when(stepMockHelper.initStepMetaInterface.getEndedLine()).thenReturn(TextFileOutputTest.EMPTY_STRING);
        Mockito.when(stepMockHelper.initStepMetaInterface.getOutputFields()).thenReturn(textFileFields);
        Mockito.when(stepMockHelper.initStepMetaInterface.isDoNotOpenNewFileInit()).thenReturn(true);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.getEndedLine()).thenReturn(TextFileOutputTest.EMPTY_STRING);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.getFileName()).thenReturn(((TextFileOutputTest.TEXT_FILE_OUTPUT_PREFIX) + (TextFileOutputTest.TEXT_FILE_OUTPUT_EXTENSION)));
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.isFileAppended()).thenReturn(true);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.isHeaderEnabled()).thenReturn(true);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.getOutputFields()).thenReturn(textFileFields);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.isDoNotOpenNewFileInit()).thenReturn(true);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.isFileNameInField()).thenReturn(false);
        Object[] rowData = new Object[]{ "data text" };
        textFileOutput = new TextFileOutputTest.TextFileOutputTestHandler(stepMockHelper.stepMeta, stepMockHelper.stepDataInterface, 0, stepMockHelper.transMeta, stepMockHelper.trans);
        ((TextFileOutputTest.TextFileOutputTestHandler) (textFileOutput)).setRow(rowData);
        RowMetaInterface inputRowMeta = Mockito.mock(RowMetaInterface.class);
        ValueMetaInterface valueMetaInterface = Mockito.mock(ValueMetaInterface.class);
        Mockito.when(valueMetaInterface.getString(Mockito.anyObject())).thenReturn(((TextFileOutputTest.TEXT_FILE_OUTPUT_PREFIX) + (TextFileOutputTest.TEXT_FILE_OUTPUT_EXTENSION)));
        Mockito.when(inputRowMeta.getValueMeta(Mockito.anyInt())).thenReturn(valueMetaInterface);
        Mockito.when(inputRowMeta.clone()).thenReturn(inputRowMeta);
        textFileOutput.setInputRowMeta(inputRowMeta);
        TextFileOutputData.FileStream streams = Mockito.mock(FileStream.class);
        stepMockHelper.initStepDataInterface.fileStreamsCollection = stepMockHelper.initStepDataInterface.new FileStreamsMap();
        stepMockHelper.initStepDataInterface.fileName = (TextFileOutputTest.TEXT_FILE_OUTPUT_PREFIX) + (TextFileOutputTest.TEXT_FILE_OUTPUT_EXTENSION);
        stepMockHelper.initStepDataInterface.fileStreamsCollection.add(((TextFileOutputTest.TEXT_FILE_OUTPUT_PREFIX) + (TextFileOutputTest.TEXT_FILE_OUTPUT_EXTENSION)), streams);
        TextFileOutput textFileOutputSpy = Mockito.spy(textFileOutput);
        Mockito.doNothing().when(textFileOutputSpy).initFileStreamWriter(((TextFileOutputTest.TEXT_FILE_OUTPUT_PREFIX) + (TextFileOutputTest.TEXT_FILE_OUTPUT_EXTENSION)));
        Mockito.doNothing().when(textFileOutputSpy).writeRow(inputRowMeta, rowData);
        Mockito.doReturn(false).when(textFileOutputSpy).isFileExists(((TextFileOutputTest.TEXT_FILE_OUTPUT_PREFIX) + (TextFileOutputTest.TEXT_FILE_OUTPUT_EXTENSION)));
        Mockito.doReturn(true).when(textFileOutputSpy).isWriteHeader(((TextFileOutputTest.TEXT_FILE_OUTPUT_PREFIX) + (TextFileOutputTest.TEXT_FILE_OUTPUT_EXTENSION)));
        textFileOutputSpy.init(stepMockHelper.processRowsStepMetaInterface, stepMockHelper.initStepDataInterface);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.buildFilename(((TextFileOutputTest.TEXT_FILE_OUTPUT_PREFIX) + (TextFileOutputTest.TEXT_FILE_OUTPUT_EXTENSION)), null, textFileOutputSpy, 0, null, 0, true, stepMockHelper.processRowsStepMetaInterface)).thenReturn(((TextFileOutputTest.TEXT_FILE_OUTPUT_PREFIX) + (TextFileOutputTest.TEXT_FILE_OUTPUT_EXTENSION)));
        textFileOutputSpy.processRow(stepMockHelper.processRowsStepMetaInterface, stepMockHelper.initStepDataInterface);
        Mockito.verify(textFileOutputSpy, Mockito.times(1)).writeHeader();
    }

    /**
     * PDI-15650
     * File Exists=N Flag Set=N Add Header=Y Append=Y
     * Result = File is created, header is written at top of file (this changed by the fix)
     * with file name in stream
     *
     * @throws KettleException
     * 		
     */
    @Test
    public void testProcessRule_2FileNameInField() throws KettleException {
        TextFileField tfFieldMock = Mockito.mock(TextFileField.class);
        TextFileField[] textFileFields = new TextFileField[]{ tfFieldMock };
        Mockito.when(stepMockHelper.initStepMetaInterface.getEndedLine()).thenReturn(TextFileOutputTest.EMPTY_STRING);
        Mockito.when(stepMockHelper.initStepMetaInterface.getOutputFields()).thenReturn(textFileFields);
        Mockito.when(stepMockHelper.initStepMetaInterface.isDoNotOpenNewFileInit()).thenReturn(true);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.getEndedLine()).thenReturn(TextFileOutputTest.EMPTY_STRING);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.getFileName()).thenReturn(((TextFileOutputTest.TEXT_FILE_OUTPUT_PREFIX) + (TextFileOutputTest.TEXT_FILE_OUTPUT_EXTENSION)));
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.isFileAppended()).thenReturn(true);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.isHeaderEnabled()).thenReturn(true);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.getOutputFields()).thenReturn(textFileFields);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.isDoNotOpenNewFileInit()).thenReturn(true);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.isFileNameInField()).thenReturn(true);
        Object[] rowData = new Object[]{ "data text" };
        textFileOutput = new TextFileOutputTest.TextFileOutputTestHandler(stepMockHelper.stepMeta, stepMockHelper.stepDataInterface, 0, stepMockHelper.transMeta, stepMockHelper.trans);
        ((TextFileOutputTest.TextFileOutputTestHandler) (textFileOutput)).setRow(rowData);
        RowMetaInterface inputRowMeta = Mockito.mock(RowMetaInterface.class);
        ValueMetaInterface valueMetaInterface = Mockito.mock(ValueMetaInterface.class);
        Mockito.when(valueMetaInterface.getString(Mockito.anyObject())).thenReturn(((TextFileOutputTest.TEXT_FILE_OUTPUT_PREFIX) + (TextFileOutputTest.TEXT_FILE_OUTPUT_EXTENSION)));
        Mockito.when(inputRowMeta.getValueMeta(Mockito.anyInt())).thenReturn(valueMetaInterface);
        Mockito.when(inputRowMeta.clone()).thenReturn(inputRowMeta);
        textFileOutput.setInputRowMeta(inputRowMeta);
        TextFileOutputData.FileStream streams = Mockito.mock(FileStream.class);
        stepMockHelper.initStepDataInterface.fileStreamsCollection = stepMockHelper.initStepDataInterface.new FileStreamsMap();
        stepMockHelper.initStepDataInterface.fileName = (TextFileOutputTest.TEXT_FILE_OUTPUT_PREFIX) + (TextFileOutputTest.TEXT_FILE_OUTPUT_EXTENSION);
        stepMockHelper.initStepDataInterface.fileStreamsCollection.add(((TextFileOutputTest.TEXT_FILE_OUTPUT_PREFIX) + (TextFileOutputTest.TEXT_FILE_OUTPUT_EXTENSION)), streams);
        TextFileOutput textFileOutputSpy = Mockito.spy(textFileOutput);
        Mockito.doNothing().when(textFileOutputSpy).initFileStreamWriter(((TextFileOutputTest.TEXT_FILE_OUTPUT_PREFIX) + (TextFileOutputTest.TEXT_FILE_OUTPUT_EXTENSION)));
        Mockito.doReturn(false).when(textFileOutputSpy).isFileExists(((TextFileOutputTest.TEXT_FILE_OUTPUT_PREFIX) + (TextFileOutputTest.TEXT_FILE_OUTPUT_EXTENSION)));
        Mockito.doReturn(true).when(textFileOutputSpy).isWriteHeader(((TextFileOutputTest.TEXT_FILE_OUTPUT_PREFIX) + (TextFileOutputTest.TEXT_FILE_OUTPUT_EXTENSION)));
        Mockito.doNothing().when(textFileOutputSpy).writeRow(inputRowMeta, rowData);
        textFileOutputSpy.init(stepMockHelper.processRowsStepMetaInterface, stepMockHelper.initStepDataInterface);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.buildFilename(((TextFileOutputTest.TEXT_FILE_OUTPUT_PREFIX) + (TextFileOutputTest.TEXT_FILE_OUTPUT_EXTENSION)), null, textFileOutputSpy, 0, null, 0, true, stepMockHelper.processRowsStepMetaInterface)).thenReturn(((TextFileOutputTest.TEXT_FILE_OUTPUT_PREFIX) + (TextFileOutputTest.TEXT_FILE_OUTPUT_EXTENSION)));
        textFileOutputSpy.processRow(stepMockHelper.processRowsStepMetaInterface, stepMockHelper.initStepDataInterface);
        Mockito.verify(textFileOutputSpy, Mockito.times(1)).writeHeader();
    }

    /**
     * Test for PDI-13987
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFastDumpDisableStreamEncodeTest() throws Exception {
        textFileOutput = new TextFileOutputTest.TextFileOutputTestHandler(stepMockHelper.stepMeta, stepMockHelper.stepDataInterface, 0, stepMockHelper.transMeta, stepMockHelper.trans);
        textFileOutput.meta = stepMockHelper.processRowsStepMetaInterface;
        String testString = "???";
        String inputEncode = "UTF-8";
        String outputEncode = "Windows-1252";
        Object[] rows = new Object[]{ testString.getBytes(inputEncode) };
        ValueMetaBase valueMetaInterface = new ValueMetaBase("test", ValueMetaInterface.TYPE_STRING);
        valueMetaInterface.setStringEncoding(inputEncode);
        valueMetaInterface.setStorageType(STORAGE_TYPE_BINARY_STRING);
        valueMetaInterface.setStorageMetadata(new ValueMetaString());
        TextFileOutputData data = new TextFileOutputData();
        data.binarySeparator = " ".getBytes();
        data.binaryEnclosure = "\"".getBytes();
        data.binaryNewline = "\n".getBytes();
        textFileOutput.data = data;
        RowMeta rowMeta = new RowMeta();
        rowMeta.addValueMeta(valueMetaInterface);
        Mockito.doReturn(outputEncode).when(stepMockHelper.processRowsStepMetaInterface).getEncoding();
        textFileOutput.data.writer = Mockito.mock(BufferedOutputStream.class);
        textFileOutput.writeRow(rowMeta, rows);
        Mockito.verify(textFileOutput.data.writer, Mockito.times(1)).write(testString.getBytes(outputEncode));
    }
}

