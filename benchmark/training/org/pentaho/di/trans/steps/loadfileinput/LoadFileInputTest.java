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
package org.pentaho.di.trans.steps.loadfileinput;


import ValueMetaInterface.TRIM_TYPE_BOTH;
import ValueMetaInterface.TRIM_TYPE_LEFT;
import ValueMetaInterface.TRIM_TYPE_NONE;
import ValueMetaInterface.TRIM_TYPE_RIGHT;
import ValueMetaInterface.TYPE_BINARY;
import java.io.UnsupportedEncodingException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.fileinput.FileInputList;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBinary;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;


public class LoadFileInputTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private FileSystemManager fs;

    private String filesPath;

    private String transName;

    private TransMeta transMeta;

    private Trans trans;

    private LoadFileInputMeta stepMetaInterface;

    private StepDataInterface stepDataInterface;

    private StepMeta stepMeta;

    private FileInputList stepInputFiles;

    private int stepCopyNr;

    private LoadFileInput stepLoadFileInput;

    private StepMetaInterface runtimeSMI;

    private StepDataInterface runtimeSDI;

    private LoadFileInputField inputField;

    private static String wasEncoding;

    @Test
    public void testOpenNextFile_noFiles() {
        Assert.assertFalse(stepMetaInterface.isIgnoreEmptyFile());// ensure default value

        Assert.assertFalse(stepLoadFileInput.openNextFile());
    }

    @Test
    public void testOpenNextFile_noFiles_ignoreEmpty() {
        stepMetaInterface.setIgnoreEmptyFile(true);
        Assert.assertFalse(stepLoadFileInput.openNextFile());
    }

    @Test
    public void testOpenNextFile_0() {
        Assert.assertFalse(stepMetaInterface.isIgnoreEmptyFile());// ensure default value

        stepInputFiles.addFile(getFile("input0.txt"));
        Assert.assertTrue(stepLoadFileInput.openNextFile());
        Assert.assertFalse(stepLoadFileInput.openNextFile());
    }

    @Test
    public void testOpenNextFile_0_ignoreEmpty() {
        stepMetaInterface.setIgnoreEmptyFile(true);
        stepInputFiles.addFile(getFile("input0.txt"));
        Assert.assertFalse(stepLoadFileInput.openNextFile());
    }

    @Test
    public void testOpenNextFile_000() {
        Assert.assertFalse(stepMetaInterface.isIgnoreEmptyFile());// ensure default value

        stepInputFiles.addFile(getFile("input0.txt"));
        stepInputFiles.addFile(getFile("input0.txt"));
        stepInputFiles.addFile(getFile("input0.txt"));
        Assert.assertTrue(stepLoadFileInput.openNextFile());
        Assert.assertTrue(stepLoadFileInput.openNextFile());
        Assert.assertTrue(stepLoadFileInput.openNextFile());
        Assert.assertFalse(stepLoadFileInput.openNextFile());
    }

    @Test
    public void testOpenNextFile_000_ignoreEmpty() {
        stepMetaInterface.setIgnoreEmptyFile(true);
        stepInputFiles.addFile(getFile("input0.txt"));
        stepInputFiles.addFile(getFile("input0.txt"));
        stepInputFiles.addFile(getFile("input0.txt"));
        Assert.assertFalse(stepLoadFileInput.openNextFile());
    }

    @Test
    public void testOpenNextFile_10() {
        Assert.assertFalse(stepMetaInterface.isIgnoreEmptyFile());// ensure default value

        stepInputFiles.addFile(getFile("input1.txt"));
        stepInputFiles.addFile(getFile("input0.txt"));
        Assert.assertTrue(stepLoadFileInput.openNextFile());
        Assert.assertTrue(stepLoadFileInput.openNextFile());
        Assert.assertFalse(stepLoadFileInput.openNextFile());
    }

    @Test
    public void testOpenNextFile_10_ignoreEmpty() {
        stepMetaInterface.setIgnoreEmptyFile(true);
        stepInputFiles.addFile(getFile("input1.txt"));
        stepInputFiles.addFile(getFile("input0.txt"));
        Assert.assertTrue(stepLoadFileInput.openNextFile());
        Assert.assertFalse(stepLoadFileInput.openNextFile());
    }

    @Test
    public void testOpenNextFile_01() {
        Assert.assertFalse(stepMetaInterface.isIgnoreEmptyFile());// ensure default value

        stepInputFiles.addFile(getFile("input0.txt"));
        stepInputFiles.addFile(getFile("input1.txt"));
        Assert.assertTrue(stepLoadFileInput.openNextFile());
        Assert.assertTrue(stepLoadFileInput.openNextFile());
        Assert.assertFalse(stepLoadFileInput.openNextFile());
    }

    @Test
    public void testOpenNextFile_01_ignoreEmpty() {
        stepMetaInterface.setIgnoreEmptyFile(true);
        stepInputFiles.addFile(getFile("input0.txt"));
        stepInputFiles.addFile(getFile("input1.txt"));
        Assert.assertTrue(stepLoadFileInput.openNextFile());
        Assert.assertFalse(stepLoadFileInput.openNextFile());
    }

    @Test
    public void testOpenNextFile_010() {
        Assert.assertFalse(stepMetaInterface.isIgnoreEmptyFile());// ensure default value

        stepInputFiles.addFile(getFile("input0.txt"));
        stepInputFiles.addFile(getFile("input1.txt"));
        stepInputFiles.addFile(getFile("input0.txt"));
        Assert.assertTrue(stepLoadFileInput.openNextFile());
        Assert.assertTrue(stepLoadFileInput.openNextFile());
        Assert.assertTrue(stepLoadFileInput.openNextFile());
        Assert.assertFalse(stepLoadFileInput.openNextFile());
    }

    @Test
    public void testOpenNextFile_010_ignoreEmpty() {
        stepMetaInterface.setIgnoreEmptyFile(true);
        stepInputFiles.addFile(getFile("input0.txt"));
        stepInputFiles.addFile(getFile("input1.txt"));
        stepInputFiles.addFile(getFile("input0.txt"));
        Assert.assertTrue(stepLoadFileInput.openNextFile());
        Assert.assertFalse(stepLoadFileInput.openNextFile());
    }

    @Test
    public void testGetOneRow() throws Exception {
        // string without specified encoding
        stepInputFiles.addFile(getFile("input1.txt"));
        Assert.assertNotNull(stepLoadFileInput.getOneRow());
        Assert.assertEquals("input1 - not empty", new String(stepLoadFileInput.data.filecontent));
    }

    @Test
    public void testUTF8Encoding() throws FileSystemException, KettleException {
        stepMetaInterface.setIncludeFilename(true);
        stepMetaInterface.setFilenameField("filename");
        stepMetaInterface.setIncludeRowNumber(true);
        stepMetaInterface.setRowNumberField("rownumber");
        stepMetaInterface.setShortFileNameField("shortname");
        stepMetaInterface.setExtensionField("extension");
        stepMetaInterface.setPathField("path");
        stepMetaInterface.setIsHiddenField("hidden");
        stepMetaInterface.setLastModificationDateField("lastmodified");
        stepMetaInterface.setUriField("uri");
        stepMetaInterface.setRootUriField("root uri");
        // string with UTF-8 encoding
        setEncoding("UTF-8");
        stepInputFiles.addFile(getFile("UTF-8.txt"));
        Object[] result = stepLoadFileInput.getOneRow();
        Assert.assertEquals(" UTF-8 string ???? ", result[0]);
        Assert.assertEquals(1L, result[2]);
        Assert.assertEquals("UTF-8.txt", result[3]);
        Assert.assertEquals("txt", result[4]);
        Assert.assertEquals(false, result[6]);
        Assert.assertEquals(getFile("UTF-8.txt").getURL().toString(), result[8]);
        Assert.assertEquals(getFile("UTF-8.txt").getName().getRootURI(), result[9]);
    }

    @Test
    public void testUTF8TrimLeft() throws KettleException {
        setEncoding("UTF-8");
        inputField.setTrimType(TRIM_TYPE_LEFT);
        stepInputFiles.addFile(getFile("UTF-8.txt"));
        Assert.assertEquals("UTF-8 string ???? ", stepLoadFileInput.getOneRow()[0]);
    }

    @Test
    public void testUTF8TrimRight() throws KettleException {
        setEncoding("UTF-8");
        inputField.setTrimType(TRIM_TYPE_RIGHT);
        stepInputFiles.addFile(getFile("UTF-8.txt"));
        Assert.assertEquals(" UTF-8 string ????", stepLoadFileInput.getOneRow()[0]);
    }

    @Test
    public void testUTF8Trim() throws KettleException {
        setEncoding("UTF-8");
        inputField.setTrimType(TRIM_TYPE_BOTH);
        stepInputFiles.addFile(getFile("UTF-8.txt"));
        Assert.assertEquals("UTF-8 string ????", stepLoadFileInput.getOneRow()[0]);
    }

    @Test
    public void testWindowsEncoding() throws KettleException {
        setEncoding("Windows-1252");
        inputField.setTrimType(TRIM_TYPE_NONE);
        stepInputFiles.addFile(getFile("Windows-1252.txt"));
        Assert.assertEquals(" Windows-1252 string ???? ", stepLoadFileInput.getOneRow()[0]);
    }

    @Test
    public void testWithNoEncoding() throws UnsupportedEncodingException, KettleException {
        // string with Windows-1252 encoding but with no encoding set
        ((LoadFileInputMeta) (runtimeSMI)).setEncoding(null);
        stepInputFiles.addFile(getFile("Windows-1252.txt"));
        Assert.assertNotEquals(" Windows-1252 string ???? ", stepLoadFileInput.getOneRow()[0]);
        Assert.assertEquals(" Windows-1252 string ???? ", new String(stepLoadFileInput.data.filecontent, "Windows-1252"));
    }

    @Test
    public void testByteArray() throws Exception {
        RowMetaInterface mockedRowMetaInterface = Mockito.mock(RowMetaInterface.class);
        stepLoadFileInput.data.outputRowMeta = mockedRowMetaInterface;
        stepLoadFileInput.data.convertRowMeta = mockedRowMetaInterface;
        Mockito.doReturn(new ValueMetaString()).when(mockedRowMetaInterface).getValueMeta(ArgumentMatchers.anyInt());
        // byte array
        Mockito.doReturn(new ValueMetaBinary()).when(mockedRowMetaInterface).getValueMeta(ArgumentMatchers.anyInt());
        setEncoding("UTF-8");
        stepInputFiles.addFile(getFile("pentaho_splash.png"));
        inputField = new LoadFileInputField();
        inputField.setType(TYPE_BINARY);
        ((LoadFileInputMeta) (runtimeSMI)).setInputFields(new LoadFileInputField[]{ inputField });
        Assert.assertNotNull(stepLoadFileInput.getOneRow());
        Assert.assertArrayEquals(IOUtils.toByteArray(getFile("pentaho_splash.png").getContent().getInputStream()), stepLoadFileInput.data.filecontent);
    }

    @Test
    public void testCopyOrCloneArrayFromLoadFileWithSmallerSizedReadRowArray() {
        int size = 5;
        Object[] rowData = new Object[size];
        Object[] readrow = new Object[size - 1];
        LoadFileInput loadFileInput = Mockito.mock(LoadFileInput.class);
        Mockito.when(loadFileInput.copyOrCloneArrayFromLoadFile(rowData, readrow)).thenCallRealMethod();
        Assert.assertEquals(5, loadFileInput.copyOrCloneArrayFromLoadFile(rowData, readrow).length);
    }

    @Test
    public void testCopyOrCloneArrayFromLoadFileWithBiggerSizedReadRowArray() {
        int size = 5;
        Object[] rowData = new Object[size];
        Object[] readrow = new Object[size + 1];
        LoadFileInput loadFileInput = Mockito.mock(LoadFileInput.class);
        Mockito.when(loadFileInput.copyOrCloneArrayFromLoadFile(rowData, readrow)).thenCallRealMethod();
        Assert.assertEquals(6, loadFileInput.copyOrCloneArrayFromLoadFile(rowData, readrow).length);
    }

    @Test
    public void testCopyOrCloneArrayFromLoadFileWithSameSizedReadRowArray() {
        int size = 5;
        Object[] rowData = new Object[size];
        Object[] readrow = new Object[size];
        LoadFileInput loadFileInput = Mockito.mock(LoadFileInput.class);
        Mockito.when(loadFileInput.copyOrCloneArrayFromLoadFile(rowData, readrow)).thenCallRealMethod();
        Assert.assertEquals(5, loadFileInput.copyOrCloneArrayFromLoadFile(rowData, readrow).length);
    }
}

