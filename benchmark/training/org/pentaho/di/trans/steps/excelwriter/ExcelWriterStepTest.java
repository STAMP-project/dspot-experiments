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
package org.pentaho.di.trans.steps.excelwriter;


import ValueMetaInterface.TYPE_INTEGER;
import com.google.common.io.Files;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.trans.step.StepInjectionMetaEntry;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


public class ExcelWriterStepTest {
    private static final String SHEET_NAME = "Sheet1";

    private HSSFWorkbook wb;

    private StepMockHelper<ExcelWriterStepMeta, ExcelWriterStepData> mockHelper;

    private ExcelWriterStep step;

    private ExcelWriterStepMeta stepMeta;

    private ExcelWriterStepMeta metaMock;

    private ExcelWriterStepData dataMock;

    @Test
    public void testProtectSheet() throws Exception {
        step.protectSheet(wb.getSheet(ExcelWriterStepTest.SHEET_NAME), "aa");
        Assert.assertTrue(wb.getSheet(ExcelWriterStepTest.SHEET_NAME).getProtect());
    }

    @Test
    public void testMaxSheetNameLength() throws Exception {
        PrintStream err = System.err;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            System.setErr(new PrintStream(baos));
            Mockito.when(mockHelper.initStepMetaInterface.getSheetname()).thenReturn("12345678901234567890123456789012");// 32 character

            step.init(mockHelper.initStepMetaInterface, mockHelper.initStepDataInterface);
            try {
                step.prepareNextOutputFile();
                Assert.fail();
            } catch (KettleException expected) {
                String content = expected.getMessage();
                if (!(content.contains("12345678901234567890123456789012"))) {
                    Assert.fail();
                    // CHECKSTYLE IGNORE EmptyBlock FOR NEXT 3 LINES
                } else {
                    // We expected this error message, the sheet name is too long for Excel
                }
            }
        } finally {
            System.setErr(err);
        }
    }

    @Test
    public void testTopLevelMetadataEntries() {
        try {
            List<StepInjectionMetaEntry> entries = stepMeta.getStepMetaInjectionInterface().getStepInjectionMetadataEntries();
            String masterKeys = "FIELDS";
            for (StepInjectionMetaEntry entry : entries) {
                String key = entry.getKey();
                Assert.assertTrue(masterKeys.contains(key));
                masterKeys = masterKeys.replace(key, "");
            }
            Assert.assertTrue(((masterKeys.trim().length()) == 0));
        } catch (KettleException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testChildLevelMetadataEntries() {
        try {
            List<StepInjectionMetaEntry> entries = stepMeta.getStepMetaInjectionInterface().getStepInjectionMetadataEntries();
            String childKeys = "NAME TYPE FORMAT STYLECELL FIELDTITLE TITLESTYLE " + "FORMULA HYPERLINKFIELD CELLCOMMENT COMMENTAUTHOR";
            StepInjectionMetaEntry mappingEntry = null;
            for (StepInjectionMetaEntry entry : entries) {
                String key = entry.getKey();
                if (key.equals("FIELDS")) {
                    mappingEntry = entry;
                    break;
                }
            }
            Assert.assertNotNull(mappingEntry);
            List<StepInjectionMetaEntry> fieldAttributes = mappingEntry.getDetails().get(0).getDetails();
            for (StepInjectionMetaEntry attribute : fieldAttributes) {
                String key = attribute.getKey();
                Assert.assertTrue(childKeys.contains(key));
                childKeys = childKeys.replace(key, "");
            }
            Assert.assertTrue(((childKeys.trim().length()) == 0));
        } catch (KettleException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testInjection() {
        ExcelWriterStepMeta meta = new ExcelWriterStepMeta();
        try {
            List<StepInjectionMetaEntry> entries = stepMeta.getStepMetaInjectionInterface().getStepInjectionMetadataEntries();
            for (StepInjectionMetaEntry entry : entries) {
                switch (entry.getValueType()) {
                    case ValueMetaInterface.TYPE_STRING :
                        entry.setValue("new_".concat(entry.getKey()));
                        break;
                    case ValueMetaInterface.TYPE_BOOLEAN :
                        entry.setValue(Boolean.TRUE);
                        break;
                    default :
                        break;
                }
                if (!(entry.getDetails().isEmpty())) {
                    List<StepInjectionMetaEntry> childEntries = entry.getDetails().get(0).getDetails();
                    for (StepInjectionMetaEntry childEntry : childEntries) {
                        switch (childEntry.getValueType()) {
                            case ValueMetaInterface.TYPE_STRING :
                                childEntry.setValue("new_".concat(childEntry.getKey()));
                                break;
                            case ValueMetaInterface.TYPE_BOOLEAN :
                                childEntry.setValue(Boolean.TRUE);
                                break;
                            default :
                                break;
                        }
                    }
                }
            }
            stepMeta.getStepMetaInjectionInterface().injectStepMetadataEntries(entries);
            Assert.assertEquals("Cell comment not properly injected... ", "new_CELLCOMMENT", stepMeta.getOutputFields()[0].getCommentField());
            Assert.assertEquals("Format not properly injected... ", "new_FORMAT", stepMeta.getOutputFields()[0].getFormat());
            Assert.assertEquals("Hyperlink not properly injected... ", "new_HYPERLINKFIELD", stepMeta.getOutputFields()[0].getHyperlinkField());
            Assert.assertEquals("Name not properly injected... ", "new_NAME", stepMeta.getOutputFields()[0].getName());
            Assert.assertEquals("Style cell not properly injected... ", "new_STYLECELL", stepMeta.getOutputFields()[0].getStyleCell());
            Assert.assertEquals("Title not properly injected... ", "new_FIELDTITLE", stepMeta.getOutputFields()[0].getTitle());
            Assert.assertEquals("Title style cell not properly injected... ", "new_TITLESTYLE", stepMeta.getOutputFields()[0].getTitleStyleCell());
            Assert.assertEquals("Type not properly injected... ", 0, stepMeta.getOutputFields()[0].getType());
            Assert.assertEquals("Comment author not properly injected... ", "new_COMMENTAUTHOR", stepMeta.getOutputFields()[0].getCommentAuthorField());
        } catch (KettleException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testPrepareNextOutputFile() throws Exception {
        Assert.assertTrue(step.init(metaMock, dataMock));
        File outDir = Files.createTempDir();
        String testFileOut = ((outDir.getAbsolutePath()) + (File.separator)) + "test.xlsx";
        Mockito.when(step.buildFilename(0)).thenReturn(testFileOut);
        Mockito.when(metaMock.isTemplateEnabled()).thenReturn(true);
        Mockito.when(metaMock.isStreamingData()).thenReturn(true);
        Mockito.when(metaMock.isHeaderEnabled()).thenReturn(true);
        Mockito.when(metaMock.getExtension()).thenReturn("xlsx");
        dataMock.createNewFile = true;
        dataMock.realTemplateFileName = getClass().getResource("template_test.xlsx").getFile();
        dataMock.realSheetname = "Sheet1";
        step.prepareNextOutputFile();
    }

    @Test
    public void testWriteUsingTemplateWithFormatting() throws Exception {
        Assert.assertTrue(step.init(metaMock, dataMock));
        String path = ((Files.createTempDir().getAbsolutePath()) + (File.separator)) + "formatted.xlsx";
        dataMock.fieldnrs = new int[]{ 0 };
        dataMock.linkfieldnrs = new int[]{ -1 };
        dataMock.commentfieldnrs = new int[]{ -1 };
        dataMock.createNewFile = true;
        dataMock.realTemplateFileName = getClass().getResource("template_with_formatting.xlsx").getFile();
        dataMock.realSheetname = "TicketData";
        dataMock.inputRowMeta = Mockito.mock(RowMetaInterface.class);
        ExcelWriterStepField field = new ExcelWriterStepField();
        ValueMetaInterface vmi = Mockito.mock(ValueMetaInteger.class);
        Mockito.doReturn(TYPE_INTEGER).when(vmi).getType();
        Mockito.doReturn("name").when(vmi).getName();
        Mockito.doReturn(12.0).when(vmi).getNumber(ArgumentMatchers.anyObject());
        Mockito.doReturn(path).when(step).buildFilename(0);
        Mockito.doReturn(true).when(metaMock).isTemplateEnabled();
        Mockito.doReturn(true).when(metaMock).isStreamingData();
        Mockito.doReturn(false).when(metaMock).isHeaderEnabled();
        Mockito.doReturn("xlsx").when(metaMock).getExtension();
        Mockito.doReturn(new ExcelWriterStepField[]{ field }).when(metaMock).getOutputFields();
        Mockito.doReturn(10).when(dataMock.inputRowMeta).size();
        Mockito.doReturn(vmi).when(dataMock.inputRowMeta).getValueMeta(ArgumentMatchers.anyInt());
        step.prepareNextOutputFile();
        dataMock.posY = 1;
        dataMock.sheet = Mockito.spy(dataMock.sheet);
        step.writeNextLine(new Object[]{ 12 });
        // without the fix for PDI-17146, createRow would generate an exception
        Mockito.verify(dataMock.sheet, Mockito.times(1)).createRow(ArgumentMatchers.anyInt());
    }
}

