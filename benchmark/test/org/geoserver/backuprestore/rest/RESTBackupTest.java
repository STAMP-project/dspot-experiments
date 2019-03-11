/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.backuprestore.rest;


import java.io.File;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.sf.json.JSONObject;
import org.geoserver.backuprestore.BackupRestoreTestSupport;
import org.geoserver.backuprestore.utils.BackupUtils;
import org.geoserver.platform.resource.Paths;
import org.geoserver.platform.resource.Resource;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Alessio Fabiani, GeoSolutions
 */
public class RESTBackupTest extends BackupRestoreTestSupport {
    @Test
    public void testNewBackup() throws Exception {
        Resource tmpDir = BackupUtils.tmpDir();
        String archiveFilePath = Paths.path(tmpDir.path(), "geoserver-backup.zip");
        String json = (((((("{\"backup\": {" + "   \"archiveFile\": \"") + archiveFilePath) + "\", ") + "   \"overwrite\": true,") + "   \"options\": { \"option\": [\"BK_BEST_EFFORT=true\"] }") + "  }") + "}";
        JSONObject backup = postNewBackup(json);
        Assert.assertNotNull(backup);
        JSONObject execution = readExecutionStatus(backup.getJSONObject("execution").getLong("id"));
        Assert.assertTrue((("STARTED".equals(execution.getString("status"))) || ("STARTING".equals(execution.getString("status")))));
        int cnt = 0;
        while ((cnt < 100) && (("STARTED".equals(execution.getString("status"))) || ("STARTING".equals(execution.getString("status"))))) {
            execution = readExecutionStatus(execution.getLong("id"));
            Thread.sleep(100);
            cnt++;
        } 
        if (cnt < 100) {
            Assert.assertTrue("COMPLETED".equals(execution.getString("status")));
        }
    }

    @Test
    public void testParameterizedBackup() throws Exception {
        Resource tmpDir = BackupUtils.tmpDir();
        String archiveFilePath = Paths.path(tmpDir.path(), "geoserver-backup.zip");
        String json = (((((("{\"backup\": {" + "   \"archiveFile\": \"") + archiveFilePath) + "\", ") + "   \"overwrite\": true,") + "   \"options\": { \"option\": [\"BK_PARAM_PASSWORDS=true\"] }") + "  }") + "}";
        JSONObject backup = postNewBackup(json);
        Assert.assertNotNull(backup);
        JSONObject execution = readExecutionStatus(backup.getJSONObject("execution").getLong("id"));
        Assert.assertTrue((("STARTED".equals(execution.getString("status"))) || ("STARTING".equals(execution.getString("status")))));
        int cnt = 0;
        while ((cnt < 100) && (("STARTED".equals(execution.getString("status"))) || ("STARTING".equals(execution.getString("status"))))) {
            execution = readExecutionStatus(execution.getLong("id"));
            Thread.sleep(100);
            cnt++;
        } 
        if (cnt < 100) {
            Assert.assertTrue("COMPLETED".equals(execution.getString("status")));
            ZipFile backupZip = new ZipFile(new File(archiveFilePath));
            ZipEntry entry = backupZip.getEntry("store.dat.1");
            Scanner scanner = new Scanner(backupZip.getInputStream(entry), "UTF-8");
            boolean hasExpectedValue = false;
            while ((scanner.hasNextLine()) && (!hasExpectedValue)) {
                String line = scanner.next();
                hasExpectedValue = line.contains("encryptedValue");
            } 
            Assert.assertTrue("Expected the store output to contain tokenized password", hasExpectedValue);
        }
    }

    @Test
    public void testFilteredBackup() throws Exception {
        Resource tmpDir = BackupUtils.tmpDir();
        String archiveFilePath = Paths.path(tmpDir.path(), "geoserver-backup.zip");
        String json = ((((((("{\"backup\": {" + "   \"archiveFile\": \"") + archiveFilePath) + "\", ") + "   \"overwrite\": true,") + "   \"options\": { \"option\": [\"BK_BEST_EFFORT=false\"] },") + "   \"wsFilter\": \"name IN (\'topp\',\'geosolutions-it\')\"") + "  }") + "}";
        JSONObject backup = postNewBackup(json);
        Assert.assertNotNull(backup);
        JSONObject execution = readExecutionStatus(backup.getJSONObject("execution").getLong("id"));
        Assert.assertTrue(execution.getJSONObject("stepExecutions").getJSONArray("step").getJSONObject(0).getJSONObject("parameters").get("wsFilter").equals("name IN ('topp','geosolutions-it')"));
        Assert.assertTrue((("STARTED".equals(execution.getString("status"))) || ("STARTING".equals(execution.getString("status")))));
        int cnt = 0;
        while ((cnt < 100) && (("STARTED".equals(execution.getString("status"))) || ("STARTING".equals(execution.getString("status"))))) {
            execution = readExecutionStatus(execution.getLong("id"));
            Thread.sleep(100);
            cnt++;
        } 
        if (cnt < 100) {
            Assert.assertTrue("COMPLETED".equals(execution.getString("status")));
        }
    }
}

