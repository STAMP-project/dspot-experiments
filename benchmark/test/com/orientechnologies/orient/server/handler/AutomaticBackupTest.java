package com.orientechnologies.orient.server.handler;


import ODatabaseType.PLOCAL;
import com.orientechnologies.common.io.OIOUtils;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.exception.OConfigurationException;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.config.OServerParameterConfiguration;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Luca Garulli (l.garulli--(at)--orientdb.com)
 */
public class AutomaticBackupTest {
    private static final String DBNAME = "testautobackup";

    private static final String DBNAME2 = (AutomaticBackupTest.DBNAME) + "2";

    private static final String DBNAME3 = (AutomaticBackupTest.DBNAME) + "3";

    private static String BACKUPDIR;

    private static String URL;

    private static String URL2;

    private final String tempDirectory;

    private ODatabaseDocument database;

    private final OServer server;

    public AutomaticBackupTest() throws IOException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InstantiationException, NoSuchMethodException, SecurityException, InvocationTargetException, InstanceAlreadyExistsException, MBeanRegistrationException, MalformedObjectNameException, NotCompliantMBeanException {
        // SET THE ORIENTDB_HOME DIRECTORY TO CHECK JSON FILE CREATION
        tempDirectory = new File("target/testhome").getAbsolutePath();
        System.setProperty("ORIENTDB_HOME", tempDirectory);
        server = new OServer(false) {
            @Override
            public Map<String, String> getAvailableStorageNames() {
                HashMap<String, String> result = new HashMap<String, String>();
                result.put(AutomaticBackupTest.DBNAME, AutomaticBackupTest.URL);
                return result;
            }
        };
    }

    @Test
    public void testFullBackupWithJsonConfigInclude() throws Exception {
        if (new File(((AutomaticBackupTest.BACKUPDIR) + "/testautobackup.zip")).exists())
            new File(((AutomaticBackupTest.BACKUPDIR) + "/testautobackup.zip")).delete();

        Assert.assertFalse(new File(((tempDirectory) + "/config/automatic-backup.json")).exists());
        String jsonConfig = OIOUtils.readStreamAsString(getClass().getResourceAsStream("automatic-backup.json"));
        ODocument doc = new ODocument().fromJSON(jsonConfig);
        doc.field("enabled", true);
        doc.field("targetFileName", "${DBNAME}.zip");
        doc.field("dbInclude", new String[]{ "testautobackup" });
        doc.field("firstTime", new SimpleDateFormat("HH:mm:ss").format(new Date(((System.currentTimeMillis()) + 5000))));
        OIOUtils.writeFile(new File(((tempDirectory) + "/config/automatic-backup.json")), doc.toJSON());
        final OAutomaticBackup aBackup = new OAutomaticBackup();
        final OServerParameterConfiguration[] config = new OServerParameterConfiguration[]{  };
        aBackup.config(server, config);
        waitForFile(Paths.get(AutomaticBackupTest.BACKUPDIR).resolve("testautobackup.zip"));
        aBackup.sendShutdown();
        if (server.existsDatabase(AutomaticBackupTest.DBNAME2))
            server.dropDatabase(AutomaticBackupTest.DBNAME2);

        server.createDatabase(AutomaticBackupTest.DBNAME2, PLOCAL, null);
        ODatabaseDocument database2 = server.openDatabase(AutomaticBackupTest.DBNAME2, null, null, null, true);
        database2.restore(new FileInputStream(((AutomaticBackupTest.BACKUPDIR) + "/testautobackup.zip")), null, null, null);
        Assert.assertEquals(database2.countClass("TestBackup"), 1);
        database2.close();
    }

    @Test
    public void testFullBackupWithJsonConfigExclude() throws Exception {
        if (new File(((AutomaticBackupTest.BACKUPDIR) + "/testautobackup.zip")).exists())
            new File(((AutomaticBackupTest.BACKUPDIR) + "/testautobackup.zip")).delete();

        Assert.assertFalse(new File(((tempDirectory) + "/config/automatic-backup.json")).exists());
        String jsonConfig = OIOUtils.readStreamAsString(getClass().getResourceAsStream("automatic-backup.json"));
        ODocument doc = new ODocument().fromJSON(jsonConfig);
        doc.field("enabled", true);
        doc.field("targetFileName", "${DBNAME}.zip");
        doc.field("dbExclude", new String[]{ "testautobackup" });
        doc.field("firstTime", new SimpleDateFormat("HH:mm:ss").format(new Date(((System.currentTimeMillis()) + 2000))));
        OIOUtils.writeFile(new File(((tempDirectory) + "/config/automatic-backup.json")), doc.toJSON());
        final OAutomaticBackup aBackup = new OAutomaticBackup();
        final OServerParameterConfiguration[] config = new OServerParameterConfiguration[]{  };
        aBackup.config(server, config);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        aBackup.sendShutdown();
        Assert.assertFalse(new File(((AutomaticBackupTest.BACKUPDIR) + "/testautobackup.zip")).exists());
    }

    @Test
    public void testFullBackup() throws Exception {
        if (new File(((AutomaticBackupTest.BACKUPDIR) + "/fullBackup.zip")).exists())
            new File(((AutomaticBackupTest.BACKUPDIR) + "/fullBackup.zip")).delete();

        final OAutomaticBackup aBackup = new OAutomaticBackup();
        final OServerParameterConfiguration[] config = new OServerParameterConfiguration[]{ new OServerParameterConfiguration("enabled", "true"), new OServerParameterConfiguration("firstTime", new SimpleDateFormat("HH:mm:ss").format(new Date(((System.currentTimeMillis()) + 5000)))), new OServerParameterConfiguration("delay", "1d"), new OServerParameterConfiguration("mode", "FULL_BACKUP"), new OServerParameterConfiguration("target.directory", AutomaticBackupTest.BACKUPDIR), new OServerParameterConfiguration("target.fileName", "fullBackup.zip") };
        aBackup.config(server, config);
        waitForFile(Paths.get(AutomaticBackupTest.BACKUPDIR).resolve("fullBackup.zip"));
        aBackup.sendShutdown();
        final ODatabaseDocumentTx database2 = new ODatabaseDocumentTx(AutomaticBackupTest.URL2);
        if (database2.exists())
            database2.open("admin", "admin").drop();

        database2.create();
        database2.restore(new FileInputStream(((AutomaticBackupTest.BACKUPDIR) + "/fullBackup.zip")), null, null, null);
        Assert.assertEquals(database2.countClass("TestBackup"), 1);
        database2.close();
    }

    @Test
    public void testAutomaticBackupDisable() throws IOException, ClassNotFoundException, InstanceAlreadyExistsException, MBeanRegistrationException, MalformedObjectNameException, NotCompliantMBeanException {
        String jsonConfig = OIOUtils.readStreamAsString(getClass().getResourceAsStream("automatic-backup.json"));
        ODocument doc = new ODocument().fromJSON(jsonConfig);
        doc.field("enabled", false);
        doc.field("targetFileName", "${DBNAME}.zip");
        doc.field("dbExclude", new String[]{ "testautobackup" });
        doc.field("firstTime", new SimpleDateFormat("HH:mm:ss").format(new Date(((System.currentTimeMillis()) + 2000))));
        OIOUtils.writeFile(new File(((tempDirectory) + "/config/automatic-backup.json")), doc.toJSON());
        final OAutomaticBackup aBackup = new OAutomaticBackup();
        final OServerParameterConfiguration[] config = new OServerParameterConfiguration[]{  };
        try {
            aBackup.config(server, config);
        } catch (OConfigurationException e) {
            Assert.fail("It should not get an configuration exception when disabled");
        }
    }

    // // @Test
    // //TODO: move to EE test suite
    // public void testIncrementalBackup() throws IOException, ClassNotFoundException, MalformedObjectNameException,
    // InstanceAlreadyExistsException, NotCompliantMBeanException, MBeanRegistrationException {
    // if (new File(BACKUPDIR + "/" + DBNAME).exists())
    // OFileUtils.deleteRecursively(new File(BACKUPDIR + "/" + DBNAME));
    // 
    // final OAutomaticBackup aBackup = new OAutomaticBackup();
    // 
    // final OServerParameterConfiguration[] config = new OServerParameterConfiguration[] {
    // new OServerParameterConfiguration("firstTime",
    // new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis() + 2000))),
    // new OServerParameterConfiguration("delay", "1d"), new OServerParameterConfiguration("mode", "INCREMENTAL_BACKUP"),
    // new OServerParameterConfiguration("target.directory", BACKUPDIR) };
    // 
    // aBackup.config(server, config);
    // 
    // try {
    // Thread.sleep(5000);
    // } catch (InterruptedException e) {
    // e.printStackTrace();
    // }
    // 
    // aBackup.sendShutdown();
    // 
    // final ODatabaseDocumentTx database2 = new ODatabaseDocumentTx(URL2);
    // if (database2.exists())
    // database2.open("admin", "admin").drop();
    // database2.create(BACKUPDIR + "/" + DBNAME);
    // 
    // Assert.assertEquals(database2.countClass("TestBackup"), 1);
    // }
    @Test
    public void testExport() throws Exception {
        if (new File(((AutomaticBackupTest.BACKUPDIR) + "/fullExport.json.gz")).exists())
            new File(((AutomaticBackupTest.BACKUPDIR) + "/fullExport.json.gz")).delete();

        final OAutomaticBackup aBackup = new OAutomaticBackup();
        final OServerParameterConfiguration[] config = new OServerParameterConfiguration[]{ new OServerParameterConfiguration("enabled", "true"), new OServerParameterConfiguration("firstTime", new SimpleDateFormat("HH:mm:ss").format(new Date(((System.currentTimeMillis()) + 5000)))), new OServerParameterConfiguration("delay", "1d"), new OServerParameterConfiguration("mode", "EXPORT"), new OServerParameterConfiguration("target.directory", AutomaticBackupTest.BACKUPDIR), new OServerParameterConfiguration("target.fileName", "fullExport.json.gz") };
        aBackup.config(server, config);
        waitForFile(Paths.get(AutomaticBackupTest.BACKUPDIR).resolve("fullExport.json.gz"));
        aBackup.sendShutdown();
        if (server.existsDatabase(AutomaticBackupTest.DBNAME3))
            server.dropDatabase(AutomaticBackupTest.DBNAME3);

        server.createDatabase(AutomaticBackupTest.DBNAME3, PLOCAL, null);
        ODatabaseDocumentInternal database2 = server.openDatabase(AutomaticBackupTest.DBNAME3, null, null, null, true);
        importDatabase();
        Assert.assertEquals(database2.countClass("TestBackup"), 1);
        database2.close();
    }
}

