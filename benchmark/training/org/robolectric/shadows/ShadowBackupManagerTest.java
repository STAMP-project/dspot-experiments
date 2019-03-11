package org.robolectric.shadows;


import BackupManager.SUCCESS;
import android.Manifest.permission.BACKUP;
import android.app.Application;
import android.app.backup.BackupManager;
import android.app.backup.RestoreObserver;
import android.app.backup.RestoreSession;
import android.app.backup.RestoreSet;
import android.os.Build.VERSION_CODES;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


/**
 * Unit tests for {@link ShadowBackupManager}.
 */
@RunWith(AndroidJUnit4.class)
public class ShadowBackupManagerTest {
    private BackupManager backupManager;

    @Mock
    private ShadowBackupManagerTest.TestRestoreObserver restoreObserver;

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void setBackupEnabled_setToTrue_shouldEnableBackup() {
        backupManager.setBackupEnabled(true);
        assertThat(backupManager.isBackupEnabled()).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void setBackupEnabled_multipleInstances_shouldBeEnabled() {
        // BackupManager is used by creating new instances, but all of them talk to the same
        // BackupManagerService in Android, so methods that route through the service will share states.
        backupManager.setBackupEnabled(true);
        assertThat(isBackupEnabled()).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void setBackupEnabled_setToFalse_shouldDisableBackup() {
        backupManager.setBackupEnabled(false);
        assertThat(backupManager.isBackupEnabled()).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void isBackupEnabled_noPermission_shouldThrowSecurityException() {
        Shadows.shadowOf(((Application) (ApplicationProvider.getApplicationContext()))).denyPermissions(BACKUP);
        try {
            backupManager.isBackupEnabled();
            Assert.fail("SecurityException should be thrown");
        } catch (SecurityException e) {
            // pass
        }
    }

    @Test
    public void getAvailableRestoreSets_shouldCallbackToRestoreSetsAvailable() {
        RestoreSession restoreSession = backupManager.beginRestoreSession();
        int result = restoreSession.getAvailableRestoreSets(restoreObserver);
        assertThat(result).isEqualTo(SUCCESS);
        Robolectric.flushForegroundThreadScheduler();
        ArgumentCaptor<RestoreSet[]> restoreSetArg = ArgumentCaptor.forClass(RestoreSet[].class);
        Mockito.verify(restoreObserver).restoreSetsAvailable(restoreSetArg.capture());
        RestoreSet[] restoreSets = restoreSetArg.getValue();
        assertThat(restoreSets).hasLength(2);
        assertThat(restoreSets).asList().comparingElementsUsing(ShadowBackupManagerTest.fieldCorrespondence("token")).containsExactly(123L, 456L);
    }

    @Test
    public void restoreAll_shouldRestoreData() {
        RestoreSession restoreSession = backupManager.beginRestoreSession();
        int result = restoreSession.restoreAll(123L, restoreObserver);
        assertThat(result).isEqualTo(SUCCESS);
        Robolectric.flushForegroundThreadScheduler();
        restoreStarting(ArgumentMatchers.eq(2));
        Mockito.verify(restoreObserver).restoreFinished(ArgumentMatchers.eq(SUCCESS));
        assertThat(Shadows.shadowOf(backupManager).getPackageRestoreToken("foo.bar")).isEqualTo(123L);
        assertThat(Shadows.shadowOf(backupManager).getPackageRestoreToken("bar.baz")).isEqualTo(123L);
        assertThat(Shadows.shadowOf(backupManager).getPackageRestoreToken("hello.world")).isEqualTo(0L);
    }

    @Test
    public void restoreSome_shouldRestoreSpecifiedPackages() {
        RestoreSession restoreSession = backupManager.beginRestoreSession();
        int result = restoreSession.restoreSome(123L, restoreObserver, new String[]{ "bar.baz" });
        assertThat(result).isEqualTo(SUCCESS);
        Robolectric.flushForegroundThreadScheduler();
        restoreStarting(ArgumentMatchers.eq(1));
        Mockito.verify(restoreObserver).restoreFinished(ArgumentMatchers.eq(SUCCESS));
        assertThat(Shadows.shadowOf(backupManager).getPackageRestoreToken("foo.bar")).isEqualTo(0L);
        assertThat(Shadows.shadowOf(backupManager).getPackageRestoreToken("bar.baz")).isEqualTo(123L);
    }

    @Test
    public void restorePackage_shouldRestoreSpecifiedPackage() {
        RestoreSession restoreSession = backupManager.beginRestoreSession();
        restoreSession.restoreSome(123L, restoreObserver, new String[0]);
        assertThat(Shadows.shadowOf(backupManager).getPackageRestoreToken("bar.baz")).isEqualTo(0L);
        restoreSession.endRestoreSession();
        Robolectric.flushForegroundThreadScheduler();
        Mockito.reset(restoreObserver);
        restoreSession = backupManager.beginRestoreSession();
        int result = restoreSession.restorePackage("bar.baz", restoreObserver);
        assertThat(result).isEqualTo(SUCCESS);
        Robolectric.flushForegroundThreadScheduler();
        restoreStarting(ArgumentMatchers.eq(1));
        Mockito.verify(restoreObserver).restoreFinished(ArgumentMatchers.eq(SUCCESS));
        assertThat(Shadows.shadowOf(backupManager).getPackageRestoreToken("bar.baz")).isEqualTo(123L);
    }

    @Test
    public void restorePackage_noRestoreToken_shouldReturnFailure() {
        RestoreSession restoreSession = backupManager.beginRestoreSession();
        int result = restoreSession.restorePackage("bar.baz", restoreObserver);
        assertThat(result).isEqualTo((-1));
    }

    private static class TestRestoreObserver extends RestoreObserver {}
}

