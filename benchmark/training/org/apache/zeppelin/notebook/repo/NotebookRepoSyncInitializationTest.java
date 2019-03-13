/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.notebook.repo;


import ConfVars.ZEPPELIN_HOME;
import ConfVars.ZEPPELIN_NOTEBOOK_DIR;
import ConfVars.ZEPPELIN_NOTEBOOK_STORAGE;
import java.io.File;
import java.io.IOException;
import org.apache.zeppelin.conf.ZeppelinConfiguration;
import org.apache.zeppelin.notebook.repo.mock.VFSNotebookRepoMock;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO(zjffdu) move it to zeppelin-zengine
public class NotebookRepoSyncInitializationTest {
    private static final Logger LOG = LoggerFactory.getLogger(NotebookRepoSyncInitializationTest.class);

    private String validFirstStorageClass = "org.apache.zeppelin.notebook.repo.VFSNotebookRepo";

    private String validSecondStorageClass = "org.apache.zeppelin.notebook.repo.mock.VFSNotebookRepoMock";

    private String invalidStorageClass = "org.apache.zeppelin.notebook.repo.DummyNotebookRepo";

    private String validOneStorageConf = validFirstStorageClass;

    private String validTwoStorageConf = ((validFirstStorageClass) + ",") + (validSecondStorageClass);

    private String invalidTwoStorageConf = ((validFirstStorageClass) + ",") + (invalidStorageClass);

    private String unsupportedStorageConf = ((((validFirstStorageClass) + ",") + (validSecondStorageClass)) + ",") + (validSecondStorageClass);

    private String emptyStorageConf = "";

    @Test
    public void validInitOneStorageTest() throws IOException {
        // no need to initialize folder due to one storage
        // set confs
        System.setProperty(ZEPPELIN_NOTEBOOK_STORAGE.getVarName(), validOneStorageConf);
        ZeppelinConfiguration conf = ZeppelinConfiguration.create();
        // create repo
        NotebookRepoSync notebookRepoSync = new NotebookRepoSync(conf);
        // check proper initialization of one storage
        Assert.assertEquals(notebookRepoSync.getRepoCount(), 1);
        Assert.assertTrue(((notebookRepoSync.getRepo(0)) instanceof VFSNotebookRepo));
    }

    @Test
    public void validInitTwoStorageTest() throws IOException {
        // initialize folders for each storage
        String zpath = ((System.getProperty("java.io.tmpdir")) + "/ZeppelinLTest_") + (System.currentTimeMillis());
        File mainZepDir = new File(zpath);
        mainZepDir.mkdirs();
        new File(mainZepDir, "conf").mkdirs();
        String mainNotePath = zpath + "/notebook";
        String secNotePath = mainNotePath + "_secondary";
        File mainNotebookDir = new File(mainNotePath);
        File secNotebookDir = new File(secNotePath);
        mainNotebookDir.mkdirs();
        secNotebookDir.mkdirs();
        // set confs
        System.setProperty(ZEPPELIN_HOME.getVarName(), mainZepDir.getAbsolutePath());
        System.setProperty(ZEPPELIN_NOTEBOOK_DIR.getVarName(), mainNotebookDir.getAbsolutePath());
        System.setProperty(ZEPPELIN_NOTEBOOK_STORAGE.getVarName(), validTwoStorageConf);
        ZeppelinConfiguration conf = ZeppelinConfiguration.create();
        // create repo
        NotebookRepoSync notebookRepoSync = new NotebookRepoSync(conf);
        // check that both initialized
        Assert.assertEquals(notebookRepoSync.getRepoCount(), 2);
        Assert.assertTrue(((notebookRepoSync.getRepo(0)) instanceof VFSNotebookRepo));
        Assert.assertTrue(((notebookRepoSync.getRepo(1)) instanceof VFSNotebookRepoMock));
    }

    @Test
    public void invalidInitTwoStorageTest() throws IOException {
        // set confs
        System.setProperty(ZEPPELIN_NOTEBOOK_STORAGE.getVarName(), invalidTwoStorageConf);
        ZeppelinConfiguration conf = ZeppelinConfiguration.create();
        // create repo
        NotebookRepoSync notebookRepoSync = new NotebookRepoSync(conf);
        // check that second didn't initialize
        NotebookRepoSyncInitializationTest.LOG.info((" " + (notebookRepoSync.getRepoCount())));
        Assert.assertEquals(notebookRepoSync.getRepoCount(), 1);
        Assert.assertTrue(((notebookRepoSync.getRepo(0)) instanceof VFSNotebookRepo));
    }

    @Test
    public void initUnsupportedNumberStoragesTest() throws IOException {
        // initialize folders for each storage, currently for 2 only
        String zpath = ((System.getProperty("java.io.tmpdir")) + "/ZeppelinLTest_") + (System.currentTimeMillis());
        File mainZepDir = new File(zpath);
        mainZepDir.mkdirs();
        new File(mainZepDir, "conf").mkdirs();
        String mainNotePath = zpath + "/notebook";
        String secNotePath = mainNotePath + "_secondary";
        File mainNotebookDir = new File(mainNotePath);
        File secNotebookDir = new File(secNotePath);
        mainNotebookDir.mkdirs();
        secNotebookDir.mkdirs();
        // set confs
        System.setProperty(ZEPPELIN_HOME.getVarName(), mainZepDir.getAbsolutePath());
        System.setProperty(ZEPPELIN_NOTEBOOK_DIR.getVarName(), mainNotebookDir.getAbsolutePath());
        System.setProperty(ZEPPELIN_NOTEBOOK_STORAGE.getVarName(), unsupportedStorageConf);
        ZeppelinConfiguration conf = ZeppelinConfiguration.create();
        // create repo
        NotebookRepoSync notebookRepoSync = new NotebookRepoSync(conf);
        // check that first two storages initialized instead of three
        Assert.assertEquals(notebookRepoSync.getRepoCount(), 2);
        Assert.assertTrue(((notebookRepoSync.getRepo(0)) instanceof VFSNotebookRepo));
        Assert.assertTrue(((notebookRepoSync.getRepo(1)) instanceof VFSNotebookRepoMock));
    }

    @Test
    public void initEmptyStorageTest() throws IOException {
        // set confs
        System.setProperty(ZEPPELIN_NOTEBOOK_STORAGE.getVarName(), emptyStorageConf);
        ZeppelinConfiguration conf = ZeppelinConfiguration.create();
        // create repo
        NotebookRepoSync notebookRepoSync = new NotebookRepoSync(conf);
        // check initialization of one default storage
        Assert.assertEquals(notebookRepoSync.getRepoCount(), 1);
        Assert.assertTrue(((notebookRepoSync.getRepo(0)) instanceof NotebookRepoWithVersionControl));
    }

    @Test
    public void initOneDummyStorageTest() throws IOException {
        // set confs
        System.setProperty(ZEPPELIN_NOTEBOOK_STORAGE.getVarName(), invalidStorageClass);
        ZeppelinConfiguration conf = ZeppelinConfiguration.create();
        // create repo
        NotebookRepoSync notebookRepoSync = new NotebookRepoSync(conf);
        // check initialization of one default storage instead of invalid one
        Assert.assertEquals(notebookRepoSync.getRepoCount(), 1);
        Assert.assertTrue(((notebookRepoSync.getRepo(0)) instanceof NotebookRepo));
    }
}

