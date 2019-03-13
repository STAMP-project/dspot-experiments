package org.geoserver.backuprestore;


import Backup.PARAM_BEST_EFFORT_MODE;
import Backup.PARAM_SKIP_SETTINGS;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.geoserver.platform.resource.Files;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;


public class BackupWithoutSettingsTest extends BackupRestoreTestSupport {
    protected static Backup backupFacade;

    @Test
    public void testRunSpringBatchBackupJob() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_BEST_EFFORT_MODE, "true");
        params.put(PARAM_SKIP_SETTINGS, "true");
        File backupFile = File.createTempFile("testRunSpringBatchBackupJob", ".zip");
        BackupExecutionAdapter backupExecution = BackupWithoutSettingsTest.backupFacade.runBackupAsync(Files.asResource(backupFile), true, null, null, null, params);
        // Wait a bit
        Thread.sleep(100);
        Assert.assertNotNull(BackupWithoutSettingsTest.backupFacade.getBackupExecutions());
        Assert.assertTrue((!(BackupWithoutSettingsTest.backupFacade.getBackupExecutions().isEmpty())));
        Assert.assertNotNull(backupExecution);
        int cnt = 0;
        while ((cnt < 100) && (((backupExecution.getStatus()) != (BatchStatus.COMPLETED)) || (!(backupExecution.isRunning())))) {
            Thread.sleep(100);
            cnt++;
            if ((((backupExecution.getStatus()) == (BatchStatus.ABANDONED)) || ((backupExecution.getStatus()) == (BatchStatus.FAILED))) || ((backupExecution.getStatus()) == (BatchStatus.UNKNOWN))) {
                for (Throwable exception : backupExecution.getAllFailureExceptions()) {
                    LOGGER.log(Level.INFO, ("ERROR: " + (exception.getLocalizedMessage())), exception);
                    exception.printStackTrace();
                }
                break;
            }
        } 
        if (((backupExecution.getStatus()) != (BatchStatus.COMPLETED)) && (backupExecution.isRunning())) {
            BackupWithoutSettingsTest.backupFacade.stopExecution(backupExecution.getId());
        }
        if ((backupExecution.getStatus()) == (BatchStatus.COMPLETED)) {
            ZipFile backupZip = new ZipFile(backupFile);
            ZipEntry zipEntry = backupZip.getEntry("global.xml");
            Assert.assertNull(zipEntry);
            Assert.assertNull(backupZip.getEntry("logging.xml"));
            Assert.assertNull(backupZip.getEntry("services.xml"));
            Assert.assertNull(backupZip.getEntry("security"));
        }
    }
}

