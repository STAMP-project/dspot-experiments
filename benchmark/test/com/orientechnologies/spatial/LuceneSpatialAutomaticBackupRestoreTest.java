/**
 * * Copyright 2014 Orient Technologies.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 */
package com.orientechnologies.spatial;


import OClass.INDEX_TYPE.SPATIAL;
import com.orientechnologies.common.io.OIOUtils;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.config.OServerParameterConfiguration;
import com.orientechnologies.orient.server.handler.OAutomaticBackup;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.zip.GZIPInputStream;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


/**
 * Created by Enrico Risa on 07/07/15.
 */
public class LuceneSpatialAutomaticBackupRestoreTest {
    private static final String DBNAME = "OLuceneAutomaticBackupRestoreTest";

    public File tempFolder;

    private OrientDB orientDB;

    private String URL = null;

    private String BACKUPDIR = null;

    private String BACKUFILE = null;

    private OServer server;

    private ODatabaseDocumentInternal db;

    @Rule
    public TestName name = new TestName();

    @Test
    public void shouldBackupAndRestore() throws IOException, InterruptedException {
        String query = "select * from City where  ST_WITHIN(location,'POLYGON ((12.314015 41.8262816, 12.314015 41.963125, 12.6605063 41.963125, 12.6605063 41.8262816, 12.314015 41.8262816))')" + " = true";
        List<?> docs = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>(query));
        Assert.assertEquals(docs.size(), 1);
        String jsonConfig = OIOUtils.readStreamAsString(getClass().getClassLoader().getResourceAsStream("automatic-backup.json"));
        ODocument doc = new ODocument().fromJSON(jsonConfig).field("enabled", true).field("targetFileName", "${DBNAME}.zip").field("targetDirectory", BACKUPDIR).field("dbInclude", new String[]{ LuceneSpatialAutomaticBackupRestoreTest.DBNAME }).field("firstTime", new SimpleDateFormat("HH:mm:ss").format(new Date(((System.currentTimeMillis()) + 2000))));
        OIOUtils.writeFile(new File(((tempFolder.getAbsolutePath()) + "/config/automatic-backup.json")), doc.toJSON());
        final OAutomaticBackup aBackup = new OAutomaticBackup();
        final OServerParameterConfiguration[] config = new OServerParameterConfiguration[]{  };
        aBackup.config(server, config);
        final CountDownLatch latch = new CountDownLatch(1);
        aBackup.registerListener(new OAutomaticBackup.OAutomaticBackupListener() {
            @Override
            public void onBackupCompleted(String database) {
                System.out.println("complete ");
                latch.countDown();
            }

            @Override
            public void onBackupError(String database, Exception e) {
                System.out.println(("e.getMessage() = " + (e.getMessage())));
            }
        });
        latch.await();
        aBackup.sendShutdown();
        // RESTORE
        dropIfExists();
        db = createAndOpen();
        FileInputStream stream = new FileInputStream(new File(((BACKUFILE) + ".zip")));
        db.restore(stream, null, null, null);
        db.close();
        // VERIFY
        db = open();
        assertThat(db.countClass("City")).isEqualTo(1);
        OIndex<?> index = db.getMetadata().getIndexManager().getIndex("City.location");
        assertThat(index).isNotNull();
        assertThat(index.getType()).isEqualTo(SPATIAL.name());
        assertThat(db.<List>query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<Object>(query))).hasSize(1);
    }

    @Test
    public void shouldExportImport() throws IOException, InterruptedException {
        String query = "select * from City where  ST_WITHIN(location,'POLYGON ((12.314015 41.8262816, 12.314015 41.963125, 12.6605063 41.963125, 12.6605063 41.8262816, 12.314015 41.8262816))')" + " = true";
        List<?> docs = db.query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<ODocument>(query));
        Assert.assertEquals(docs.size(), 1);
        String jsonConfig = OIOUtils.readStreamAsString(getClass().getClassLoader().getResourceAsStream("automatic-backup.json"));
        ODocument doc = new ODocument().fromJSON(jsonConfig).field("enabled", true).field("targetFileName", "${DBNAME}.json").field("targetDirectory", BACKUPDIR).field("mode", "EXPORT").field("dbInclude", new String[]{ LuceneSpatialAutomaticBackupRestoreTest.DBNAME }).field("firstTime", new SimpleDateFormat("HH:mm:ss").format(new Date(((System.currentTimeMillis()) + 2000))));
        OIOUtils.writeFile(new File(((tempFolder.getAbsolutePath()) + "/config/automatic-backup.json")), doc.toJSON());
        final OAutomaticBackup aBackup = new OAutomaticBackup();
        final OServerParameterConfiguration[] config = new OServerParameterConfiguration[]{  };
        aBackup.config(server, config);
        final CountDownLatch latch = new CountDownLatch(1);
        aBackup.registerListener(new OAutomaticBackup.OAutomaticBackupListener() {
            @Override
            public void onBackupCompleted(String database) {
                latch.countDown();
            }

            @Override
            public void onBackupError(String database, Exception e) {
                latch.countDown();
            }
        });
        latch.await();
        aBackup.sendShutdown();
        db.close();
        dropIfExists();
        // RESTORE
        db = createAndOpen();
        GZIPInputStream stream = new GZIPInputStream(new FileInputStream(((BACKUFILE) + ".json.gz")));
        importDatabase();
        db.close();
        // VERIFY
        db = open();
        assertThat(db.countClass("City")).isEqualTo(1);
        OIndex<?> index = db.getMetadata().getIndexManager().getIndex("City.location");
        assertThat(index).isNotNull();
        assertThat(index.getType()).isEqualTo(SPATIAL.name());
        assertThat(db.<List>query(new com.orientechnologies.orient.core.sql.query.OSQLSynchQuery<Object>(query))).hasSize(1);
    }
}

