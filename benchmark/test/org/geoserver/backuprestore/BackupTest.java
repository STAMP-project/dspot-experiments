/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.backuprestore;


import BatchStatus.COMPLETED;
import BatchStatus.STOPPED;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.platform.resource.Files;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.util.factory.Hints;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.springframework.batch.core.BatchStatus;

import static Backup.PARAM_BEST_EFFORT_MODE;
import static Backup.PARAM_PARAMETERIZE_PASSWDS;
import static Backup.PARAM_PASSWORD_TOKENS;


/**
 *
 *
 * @author Alessio Fabiani, GeoSolutions
 */
public class BackupTest extends BackupRestoreTestSupport {
    @Test
    public void testRunSpringBatchBackupJob() throws Exception {
        Hints hints = new Hints(new HashMap(2));
        hints.add(new Hints(new Hints.OptionKey(PARAM_BEST_EFFORT_MODE), PARAM_BEST_EFFORT_MODE));
        BackupExecutionAdapter backupExecution = BackupRestoreTestSupport.backupFacade.runBackupAsync(Files.asResource(File.createTempFile("testRunSpringBatchBackupJob", ".zip")), true, null, null, null, hints);
        // Wait a bit
        Thread.sleep(100);
        Assert.assertNotNull(BackupRestoreTestSupport.backupFacade.getBackupExecutions());
        Assert.assertTrue((!(BackupRestoreTestSupport.backupFacade.getBackupExecutions().isEmpty())));
        Assert.assertNotNull(backupExecution);
        int cnt = 0;
        while ((cnt < 100) && (((backupExecution.getStatus()) != (BatchStatus.COMPLETED)) || (backupExecution.isRunning()))) {
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
        Assert.assertEquals(backupExecution.getStatus(), COMPLETED);
        Assert.assertThat(((ContinuableHandler.getInvocationsCount()) > 2), CoreMatchers.is(true));
        // check that generic listener was invoked for the backup job
        Assert.assertThat(GenericListener.getBackupAfterInvocations(), CoreMatchers.is(3));
        Assert.assertThat(GenericListener.getBackupBeforeInvocations(), CoreMatchers.is(3));
        Assert.assertThat(GenericListener.getRestoreAfterInvocations(), CoreMatchers.is(3));
        Assert.assertThat(GenericListener.getRestoreBeforeInvocations(), CoreMatchers.is(3));
    }

    @Test
    public void testTryToRunMultipleSpringBatchBackupJobs() throws Exception {
        Hints hints = new Hints(new HashMap(2));
        hints.add(new Hints(new Hints.OptionKey(PARAM_BEST_EFFORT_MODE), PARAM_BEST_EFFORT_MODE));
        BackupRestoreTestSupport.backupFacade.runBackupAsync(Files.asResource(File.createTempFile("testRunSpringBatchBackupJob", ".zip")), true, null, null, null, hints);
        try {
            BackupRestoreTestSupport.backupFacade.runBackupAsync(Files.asResource(File.createTempFile("testRunSpringBatchBackupJob", ".zip")), true, null, null, null, hints);
        } catch (IOException e) {
            Assert.assertEquals(e.getMessage(), "Could not start a new Backup Job Execution since there are currently Running jobs.");
        }
        // Wait a bit
        Thread.sleep(100);
        Assert.assertNotNull(BackupRestoreTestSupport.backupFacade.getBackupExecutions());
        Assert.assertTrue((!(BackupRestoreTestSupport.backupFacade.getBackupExecutions().isEmpty())));
        Assert.assertEquals(BackupRestoreTestSupport.backupFacade.getBackupRunningExecutions().size(), 1);
        BackupExecutionAdapter backupExecution = null;
        final Iterator<BackupExecutionAdapter> iterator = BackupRestoreTestSupport.backupFacade.getBackupExecutions().values().iterator();
        while (iterator.hasNext()) {
            backupExecution = iterator.next();
        } 
        Assert.assertNotNull(backupExecution);
        int cnt = 0;
        while ((cnt < 100) && (((backupExecution.getStatus()) != (BatchStatus.COMPLETED)) || (!(backupExecution.isRunning())))) {
            Thread.sleep(100);
            cnt++;
            if ((((backupExecution.getStatus()) == (BatchStatus.ABANDONED)) || ((backupExecution.getStatus()) == (BatchStatus.FAILED))) || ((backupExecution.getStatus()) == (BatchStatus.UNKNOWN))) {
                LOGGER.severe(("backupExecution.getStatus() == " + (backupExecution.getStatus())));
                for (Throwable exception : backupExecution.getAllFailureExceptions()) {
                    LOGGER.log(Level.INFO, ("ERROR: " + (exception.getLocalizedMessage())), exception);
                    exception.printStackTrace();
                }
                break;
            }
        } 
        Assert.assertEquals(backupExecution.getStatus(), COMPLETED);
        Assert.assertThat(((ContinuableHandler.getInvocationsCount()) > 2), CoreMatchers.is(true));
    }

    @Test
    public void testRunSpringBatchRestoreJob() throws Exception {
        Hints hints = new Hints(new HashMap(2));
        hints.add(new Hints(new Hints.OptionKey(PARAM_BEST_EFFORT_MODE), PARAM_BEST_EFFORT_MODE));
        RestoreExecutionAdapter restoreExecution = BackupRestoreTestSupport.backupFacade.runRestoreAsync(BackupRestoreTestSupport.file("geoserver-full-backup.zip"), null, null, null, hints);
        // Wait a bit
        Thread.sleep(100);
        Assert.assertNotNull(BackupRestoreTestSupport.backupFacade.getRestoreExecutions());
        Assert.assertTrue((!(BackupRestoreTestSupport.backupFacade.getRestoreExecutions().isEmpty())));
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
        if (((restoreExecution.getStatus()) != (BatchStatus.COMPLETED)) && (restoreExecution.isRunning())) {
            BackupRestoreTestSupport.backupFacade.stopExecution(restoreExecution.getId());
        }
        if ((restoreCatalog.getWorkspaces().size()) > 0) {
            Assert.assertEquals(restoreCatalog.getWorkspaces().size(), restoreCatalog.getNamespaces().size());
            Assert.assertEquals(9, restoreCatalog.getDataStores().size(), 9);
            Assert.assertEquals(28, restoreCatalog.getResources(FeatureTypeInfo.class).size());
            Assert.assertEquals(4, restoreCatalog.getResources(CoverageInfo.class).size());
            Assert.assertEquals(23, restoreCatalog.getStyles().size());
            Assert.assertEquals(4, restoreCatalog.getLayers().size());
            Assert.assertEquals(1, restoreCatalog.getLayerGroups().size());
        }
        checkExtraPropertiesExists();
        if ((restoreExecution.getStatus()) == (BatchStatus.COMPLETED)) {
            Assert.assertThat(((ContinuableHandler.getInvocationsCount()) > 2), CoreMatchers.is(true));
            // check that generic listener was invoked for the backup job
            Assert.assertThat(GenericListener.getBackupAfterInvocations(), CoreMatchers.is(2));
            Assert.assertThat(GenericListener.getBackupBeforeInvocations(), CoreMatchers.is(2));
            Assert.assertThat(GenericListener.getRestoreAfterInvocations(), CoreMatchers.is(3));
            Assert.assertThat(GenericListener.getRestoreBeforeInvocations(), CoreMatchers.is(3));
        }
    }

    @Test
    public void testParameterizedRestore() throws Exception {
        Hints hints = new Hints(new HashMap(2));
        hints.add(new Hints(new Hints.OptionKey(PARAM_BEST_EFFORT_MODE), PARAM_BEST_EFFORT_MODE));
        hints.add(new Hints(new Hints.OptionKey(PARAM_PARAMETERIZE_PASSWDS), PARAM_PARAMETERIZE_PASSWDS));
        hints.add(new Hints(new Hints.OptionKey(PARAM_PASSWORD_TOKENS, "*"), "${sf:sf.passwd.encryptedValue}=foo"));
        RestoreExecutionAdapter restoreExecution = BackupRestoreTestSupport.backupFacade.runRestoreAsync(BackupRestoreTestSupport.file("parameterized-restore.zip"), null, null, null, hints);
        // Wait a bit
        Thread.sleep(100);
        Assert.assertNotNull(BackupRestoreTestSupport.backupFacade.getRestoreExecutions());
        Assert.assertTrue((!(BackupRestoreTestSupport.backupFacade.getRestoreExecutions().isEmpty())));
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
        if (((restoreExecution.getStatus()) != (BatchStatus.COMPLETED)) && (restoreExecution.isRunning())) {
            BackupRestoreTestSupport.backupFacade.stopExecution(restoreExecution.getId());
        }
        if ((restoreCatalog.getWorkspaces().size()) > 0) {
            Assert.assertEquals(restoreCatalog.getWorkspaces().size(), restoreCatalog.getNamespaces().size());
            Assert.assertEquals(9, restoreCatalog.getDataStores().size());
            Assert.assertEquals(47, restoreCatalog.getResources(FeatureTypeInfo.class).size());
            Assert.assertEquals(4, restoreCatalog.getResources(CoverageInfo.class).size());
            Assert.assertEquals(23, restoreCatalog.getStyles().size());
            Assert.assertEquals(4, restoreCatalog.getLayers().size());
            Assert.assertEquals(1, restoreCatalog.getLayerGroups().size());
        }
        checkExtraPropertiesExists();
        if ((restoreExecution.getStatus()) == (BatchStatus.COMPLETED)) {
            Assert.assertThat(((ContinuableHandler.getInvocationsCount()) > 2), CoreMatchers.is(true));
            // check that generic listener was invoked for the backup job
            Assert.assertThat(GenericListener.getBackupAfterInvocations(), CoreMatchers.is(0));
            Assert.assertThat(GenericListener.getBackupBeforeInvocations(), CoreMatchers.is(0));
            Assert.assertThat(GenericListener.getRestoreAfterInvocations(), CoreMatchers.is(1));
            Assert.assertThat(GenericListener.getRestoreBeforeInvocations(), CoreMatchers.is(1));
            DataStoreInfo restoredDataStore = restoreCatalog.getStoreByName("sf", "sf", DataStoreInfo.class);
            Serializable passwd = restoredDataStore.getConnectionParameters().get("passwd");
            Assert.assertEquals("foo", passwd);
        }
    }

    @Test
    public void testRunSpringBatchFilteredRestoreJob() throws Exception {
        Hints hints = new Hints(new HashMap(2));
        hints.add(new Hints(new Hints.OptionKey(PARAM_BEST_EFFORT_MODE), PARAM_BEST_EFFORT_MODE));
        Filter filter = ECQL.toFilter("name = 'topp'");
        RestoreExecutionAdapter restoreExecution = BackupRestoreTestSupport.backupFacade.runRestoreAsync(BackupRestoreTestSupport.file("geoserver-full-backup.zip"), filter, null, null, hints);
        // Wait a bit
        Thread.sleep(100);
        Assert.assertNotNull(BackupRestoreTestSupport.backupFacade.getRestoreExecutions());
        Assert.assertTrue((!(BackupRestoreTestSupport.backupFacade.getRestoreExecutions().isEmpty())));
        Assert.assertNotNull(restoreExecution);
        Thread.sleep(100);
        final Catalog restoreCatalog = restoreExecution.getRestoreCatalog();
        Assert.assertNotNull(restoreCatalog);
        int cnt = 0;
        while ((cnt < 100) && ((restoreExecution.getStatus()) != (BatchStatus.COMPLETED))) {
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
        Assert.assertEquals(restoreExecution.getStatus(), COMPLETED);
        if ((restoreCatalog.getWorkspaces().size()) > 0) {
            Assert.assertEquals(9, restoreCatalog.getDataStores().size());
            Assert.assertEquals(23, restoreCatalog.getStyles().size());
        }
        checkExtraPropertiesExists();
        Assert.assertThat(((ContinuableHandler.getInvocationsCount()) > 2), CoreMatchers.is(true));
    }

    @Test
    public void testStopSpringBatchBackupJob() throws Exception {
        Hints hints = new Hints(new HashMap(2));
        hints.add(new Hints(new Hints.OptionKey(PARAM_BEST_EFFORT_MODE), PARAM_BEST_EFFORT_MODE));
        BackupExecutionAdapter backupExecution = BackupRestoreTestSupport.backupFacade.runBackupAsync(Files.asResource(File.createTempFile("testRunSpringBatchBackupJob", ".zip")), true, null, null, null, hints);
        int cnt = 0;
        while ((cnt < 100) && ((backupExecution.getStatus()) != (BatchStatus.STARTED))) {
            // Wait a bit
            Thread.sleep(10);
            cnt++;
            if ((((backupExecution.getStatus()) == (BatchStatus.ABANDONED)) || ((backupExecution.getStatus()) == (BatchStatus.FAILED))) || ((backupExecution.getStatus()) == (BatchStatus.UNKNOWN))) {
                for (Throwable exception : backupExecution.getAllFailureExceptions()) {
                    LOGGER.log(Level.INFO, ("ERROR: " + (exception.getLocalizedMessage())), exception);
                    exception.printStackTrace();
                }
                break;
            }
        } 
        if ((backupExecution.getStatus()) != (BatchStatus.COMPLETED)) {
            BackupRestoreTestSupport.backupFacade.stopExecution(backupExecution.getId());
            // Wait a bit
            Thread.sleep(100);
            Assert.assertNotNull(backupExecution);
            cnt = 0;
            while ((cnt < 100) && ((backupExecution.getStatus()) != (BatchStatus.STOPPED))) {
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
            Assert.assertEquals(backupExecution.getStatus(), STOPPED);
        }
    }
}

