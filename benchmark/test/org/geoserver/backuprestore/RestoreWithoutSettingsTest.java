package org.geoserver.backuprestore;


import Backup.PARAM_BEST_EFFORT_MODE;
import Backup.PARAM_PURGE_RESOURCES;
import Backup.PARAM_SKIP_SETTINGS;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.geoserver.catalog.Catalog;
import org.geoserver.config.GeoServer;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;


public class RestoreWithoutSettingsTest extends BackupRestoreTestSupport {
    protected static Backup backupFacade;

    @Test
    public void testRestoreWithoutSettings() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(PARAM_BEST_EFFORT_MODE, "true");
        params.put(PARAM_SKIP_SETTINGS, "true");
        params.put(PARAM_PURGE_RESOURCES, "false");
        Assert.assertNotNull(getCatalog().getWorkspaceByName("shouldNotBeDeleted"));
        RestoreExecutionAdapter restoreExecution = RestoreWithoutSettingsTest.backupFacade.runRestoreAsync(BackupRestoreTestSupport.file("settings-modified-restore.zip"), null, null, null, params);
        // Wait a bit
        Thread.sleep(100);
        Assert.assertNotNull(RestoreWithoutSettingsTest.backupFacade.getRestoreExecutions());
        Assert.assertTrue((!(RestoreWithoutSettingsTest.backupFacade.getRestoreExecutions().isEmpty())));
        Assert.assertNotNull(restoreExecution);
        Thread.sleep(100);
        final Catalog restoreCatalog = restoreExecution.getRestoreCatalog();
        Assert.assertNotNull(restoreCatalog);
        int cnt = 0;
        while ((cnt < 100) && (((restoreExecution.getStatus()) != (BatchStatus.COMPLETED)) || (!(restoreExecution.isRunning())))) {
            Thread.sleep(100);
            cnt++;
            if ((((restoreExecution.getStatus()) == (BatchStatus.ABANDONED)) || ((restoreExecution.getStatus()) == (BatchStatus.FAILED))) || ((restoreExecution.getStatus()) == (BatchStatus.UNKNOWN))) {
                for (Throwable exception : restoreExecution.getAllFailureExceptions()) {
                    LOGGER.log(Level.INFO, ("ERROR: " + (exception.getLocalizedMessage())), exception);
                    exception.printStackTrace();
                }
                break;
            }
        } 
        if (((restoreExecution.getStatus()) != (BatchStatus.COMPLETED)) && (!(restoreExecution.isRunning()))) {
            RestoreWithoutSettingsTest.backupFacade.stopExecution(restoreExecution.getId());
        }
        if ((restoreExecution.getStatus()) == (BatchStatus.COMPLETED)) {
            GeoServer geoServer = getGeoServer();
            Assert.assertEquals(null, geoServer.getLogging().getLocation());
            Assert.assertEquals("Andrea Aime", geoServer.getGlobal().getSettings().getContact().getContactPerson());
            String configPasswordEncrypterName = getSecurityManager().getSecurityConfig().getConfigPasswordEncrypterName();
            Assert.assertEquals("pbePasswordEncoder", configPasswordEncrypterName);
            Catalog catalog = geoServer.getCatalog();
            Assert.assertNotNull(catalog.getWorkspaceByName("shouldNotBeDeleted"));
        }
    }
}

