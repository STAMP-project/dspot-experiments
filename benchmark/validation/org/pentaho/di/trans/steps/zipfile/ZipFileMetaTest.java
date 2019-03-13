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
package org.pentaho.di.trans.steps.zipfile;


import java.util.ArrayList;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.StringObjectId;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.named.cluster.NamedClusterEmbedManager;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;


/**
 * Created by bgroves on 11/10/15.
 */
public class ZipFileMetaTest {
    private static final String SOURCE_FILENAME = "Files";

    private static final String TARGET_FILENAME = "ZipFile";

    private static final String BASE_FOLDER = "BaseFolder";

    private static final String OPERATION_TYPE = "move";

    private static final boolean ADD_RESULT_FILENAME = true;

    private static final boolean OVERWRITE_ZIP_ENTRY = true;

    private static final boolean CREATE_PARENT_FOLDER = true;

    private static final boolean KEEP_SOURCE_FOLDER = true;

    private static final String MOVE_TO_FOLDER_FIELD = "movetothisfolder";

    @Test
    public void testGettersSetters() {
        ZipFileMeta zipFileMeta = new ZipFileMeta();
        zipFileMeta.setDynamicSourceFileNameField(ZipFileMetaTest.SOURCE_FILENAME);
        zipFileMeta.setDynamicTargetFileNameField(ZipFileMetaTest.TARGET_FILENAME);
        zipFileMeta.setBaseFolderField(ZipFileMetaTest.BASE_FOLDER);
        zipFileMeta.setOperationType(ZipFileMeta.getOperationTypeByDesc(ZipFileMetaTest.OPERATION_TYPE));
        zipFileMeta.setaddTargetFileNametoResult(ZipFileMetaTest.ADD_RESULT_FILENAME);
        zipFileMeta.setOverwriteZipEntry(ZipFileMetaTest.OVERWRITE_ZIP_ENTRY);
        zipFileMeta.setCreateParentFolder(ZipFileMetaTest.CREATE_PARENT_FOLDER);
        zipFileMeta.setKeepSouceFolder(ZipFileMetaTest.KEEP_SOURCE_FOLDER);
        zipFileMeta.setMoveToFolderField(ZipFileMetaTest.MOVE_TO_FOLDER_FIELD);
        Assert.assertEquals(ZipFileMetaTest.SOURCE_FILENAME, zipFileMeta.getDynamicSourceFileNameField());
        Assert.assertEquals(ZipFileMetaTest.TARGET_FILENAME, zipFileMeta.getDynamicTargetFileNameField());
        Assert.assertEquals(ZipFileMetaTest.BASE_FOLDER, zipFileMeta.getBaseFolderField());
        Assert.assertEquals(ZipFileMeta.getOperationTypeByDesc(ZipFileMetaTest.OPERATION_TYPE), zipFileMeta.getOperationType());
        Assert.assertEquals(ZipFileMetaTest.MOVE_TO_FOLDER_FIELD, zipFileMeta.getMoveToFolderField());
        Assert.assertTrue(zipFileMeta.isaddTargetFileNametoResult());
        Assert.assertTrue(zipFileMeta.isOverwriteZipEntry());
        Assert.assertTrue(zipFileMeta.isKeepSouceFolder());
        Assert.assertTrue(zipFileMeta.isCreateParentFolder());
        Assert.assertEquals(ZipFileMetaTest.MOVE_TO_FOLDER_FIELD, zipFileMeta.getMoveToFolderField());
        Assert.assertNotNull(zipFileMeta.getStepData());
        Assert.assertTrue(zipFileMeta.supportsErrorHandling());
    }

    @Test
    public void testLoadAndGetXml() throws Exception {
        ZipFileMeta zipFileMeta = new ZipFileMeta();
        Node stepnode = getTestNode();
        DatabaseMeta dbMeta = Mockito.mock(DatabaseMeta.class);
        IMetaStore metaStore = Mockito.mock(IMetaStore.class);
        StepMeta mockParentStepMeta = Mockito.mock(StepMeta.class);
        zipFileMeta.setParentStepMeta(mockParentStepMeta);
        TransMeta mockTransMeta = Mockito.mock(TransMeta.class);
        NamedClusterEmbedManager embedManager = Mockito.mock(NamedClusterEmbedManager.class);
        Mockito.when(mockParentStepMeta.getParentTransMeta()).thenReturn(mockTransMeta);
        Mockito.when(mockTransMeta.getNamedClusterEmbedManager()).thenReturn(embedManager);
        zipFileMeta.loadXML(stepnode, Collections.singletonList(dbMeta), metaStore);
        assertXmlOutputMeta(zipFileMeta);
    }

    @Test
    public void testReadRep() throws Exception {
        ZipFileMeta zipFileMeta = new ZipFileMeta();
        Repository rep = Mockito.mock(Repository.class);
        IMetaStore metastore = Mockito.mock(IMetaStore.class);
        DatabaseMeta dbMeta = Mockito.mock(DatabaseMeta.class);
        StringObjectId oid = new StringObjectId("oid");
        Mockito.when(rep.getStepAttributeString(oid, "sourcefilenamefield")).thenReturn(ZipFileMetaTest.SOURCE_FILENAME);
        Mockito.when(rep.getStepAttributeString(oid, "targetfilenamefield")).thenReturn(ZipFileMetaTest.TARGET_FILENAME);
        Mockito.when(rep.getStepAttributeString(oid, "baseFolderField")).thenReturn(ZipFileMetaTest.BASE_FOLDER);
        Mockito.when(rep.getStepAttributeString(oid, "operation_type")).thenReturn(ZipFileMetaTest.OPERATION_TYPE);
        Mockito.when(rep.getStepAttributeBoolean(oid, "addresultfilenames")).thenReturn(ZipFileMetaTest.ADD_RESULT_FILENAME);
        Mockito.when(rep.getStepAttributeBoolean(oid, "overwritezipentry")).thenReturn(ZipFileMetaTest.OVERWRITE_ZIP_ENTRY);
        Mockito.when(rep.getStepAttributeBoolean(oid, "createparentfolder")).thenReturn(ZipFileMetaTest.CREATE_PARENT_FOLDER);
        Mockito.when(rep.getStepAttributeBoolean(oid, "keepsourcefolder")).thenReturn(ZipFileMetaTest.KEEP_SOURCE_FOLDER);
        Mockito.when(rep.getStepAttributeString(oid, "movetofolderfield")).thenReturn(ZipFileMetaTest.MOVE_TO_FOLDER_FIELD);
        zipFileMeta.readRep(rep, metastore, oid, Collections.singletonList(dbMeta));
        Assert.assertEquals(ZipFileMetaTest.SOURCE_FILENAME, zipFileMeta.getDynamicSourceFileNameField());
        Assert.assertEquals(ZipFileMetaTest.TARGET_FILENAME, zipFileMeta.getDynamicTargetFileNameField());
        Assert.assertEquals(ZipFileMetaTest.BASE_FOLDER, zipFileMeta.getBaseFolderField());
        Assert.assertEquals(ZipFileMeta.getOperationTypeByDesc(ZipFileMetaTest.OPERATION_TYPE), zipFileMeta.getOperationType());
        Assert.assertEquals(ZipFileMetaTest.MOVE_TO_FOLDER_FIELD, zipFileMeta.getMoveToFolderField());
        Assert.assertTrue(zipFileMeta.isaddTargetFileNametoResult());
        Assert.assertTrue(zipFileMeta.isOverwriteZipEntry());
        Assert.assertTrue(zipFileMeta.isKeepSouceFolder());
        Assert.assertTrue(zipFileMeta.isCreateParentFolder());
        Mockito.reset(rep, metastore);
        StringObjectId transid = new StringObjectId("transid");
        zipFileMeta.saveRep(rep, metastore, transid, oid);
        Mockito.verify(rep).saveStepAttribute(transid, oid, "sourcefilenamefield", ZipFileMetaTest.SOURCE_FILENAME);
        Mockito.verify(rep).saveStepAttribute(transid, oid, "targetfilenamefield", ZipFileMetaTest.TARGET_FILENAME);
        Mockito.verify(rep).saveStepAttribute(transid, oid, "baseFolderField", ZipFileMetaTest.BASE_FOLDER);
        Mockito.verify(rep).saveStepAttribute(transid, oid, "operation_type", ZipFileMetaTest.OPERATION_TYPE);
        Mockito.verify(rep).saveStepAttribute(transid, oid, "addresultfilenames", ZipFileMetaTest.ADD_RESULT_FILENAME);
        Mockito.verify(rep).saveStepAttribute(transid, oid, "overwritezipentry", ZipFileMetaTest.OVERWRITE_ZIP_ENTRY);
        Mockito.verify(rep).saveStepAttribute(transid, oid, "createparentfolder", ZipFileMetaTest.CREATE_PARENT_FOLDER);
        Mockito.verify(rep).saveStepAttribute(transid, oid, "keepsourcefolder", ZipFileMetaTest.KEEP_SOURCE_FOLDER);
        Mockito.verify(rep).saveStepAttribute(transid, oid, "movetofolderfield", ZipFileMetaTest.MOVE_TO_FOLDER_FIELD);
        Mockito.verifyNoMoreInteractions(rep, metastore);
    }

    @Test
    public void testCheck() {
        ZipFileMeta zipFileMeta = new ZipFileMeta();
        zipFileMeta.setDefault();
        TransMeta transMeta = Mockito.mock(TransMeta.class);
        StepMeta stepInfo = Mockito.mock(StepMeta.class);
        RowMetaInterface prev = Mockito.mock(RowMetaInterface.class);
        Repository repos = Mockito.mock(Repository.class);
        IMetaStore metastore = Mockito.mock(IMetaStore.class);
        RowMetaInterface info = Mockito.mock(RowMetaInterface.class);
        ArrayList<CheckResultInterface> remarks = new ArrayList<>();
        zipFileMeta.check(remarks, transMeta, stepInfo, prev, new String[]{ "input" }, new String[]{ "output" }, info, new Variables(), repos, metastore);
        Assert.assertEquals(2, remarks.size());
        Assert.assertEquals("Source Filename field is missing!", remarks.get(0).getText());
        Assert.assertEquals("Step is receiving info from other steps.", remarks.get(1).getText());
        remarks = new ArrayList();
        zipFileMeta = new ZipFileMeta();
        zipFileMeta.setDynamicSourceFileNameField("sourceFileField");
        zipFileMeta.check(remarks, transMeta, stepInfo, prev, new String[0], new String[]{ "output" }, info, new Variables(), repos, metastore);
        Assert.assertEquals(2, remarks.size());
        Assert.assertEquals("Target Filename field was specified", remarks.get(0).getText());
        Assert.assertEquals("No input received from other steps!", remarks.get(1).getText());
    }

    @Test
    public void testGetStep() throws Exception {
        StepMeta stepInfo = Mockito.mock(StepMeta.class);
        Mockito.when(stepInfo.getName()).thenReturn("Zip Step Name");
        StepDataInterface stepData = Mockito.mock(StepDataInterface.class);
        TransMeta transMeta = Mockito.mock(TransMeta.class);
        Mockito.when(transMeta.findStep("Zip Step Name")).thenReturn(stepInfo);
        Trans trans = Mockito.mock(Trans.class);
        ZipFileMeta zipFileMeta = new ZipFileMeta();
        ZipFile zipFile = ((ZipFile) (zipFileMeta.getStep(stepInfo, stepData, 0, transMeta, trans)));
        Assert.assertEquals(stepInfo, zipFile.getStepMeta());
        Assert.assertEquals(stepData, zipFile.getStepDataInterface());
        Assert.assertEquals(transMeta, zipFile.getTransMeta());
        Assert.assertEquals(trans, zipFile.getTrans());
        Assert.assertEquals(0, zipFile.getCopy());
    }

    @Test
    public void testOperationType() {
        Assert.assertEquals(0, ZipFileMeta.getOperationTypeByDesc(null));
        Assert.assertEquals(1, ZipFileMeta.getOperationTypeByDesc("Move source file"));
        Assert.assertEquals(1, ZipFileMeta.getOperationTypeByDesc("move"));
        Assert.assertEquals(0, ZipFileMeta.getOperationTypeByDesc("doesn't exist"));
        Assert.assertEquals("Move source file", ZipFileMeta.getOperationTypeDesc(1));
        Assert.assertEquals("Do nothing", ZipFileMeta.getOperationTypeDesc(100));
        Assert.assertEquals("Do nothing", ZipFileMeta.getOperationTypeDesc((-1)));
    }
}

