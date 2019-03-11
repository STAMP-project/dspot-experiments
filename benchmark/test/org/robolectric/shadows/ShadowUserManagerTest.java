package org.robolectric.shadows;


import UserManager.DISALLOW_CAMERA;
import UserManager.ENSURE_VERIFY_APPS;
import UserState.STATE_BOOTING;
import UserState.STATE_RUNNING_LOCKED;
import UserState.STATE_RUNNING_UNLOCKED;
import UserState.STATE_RUNNING_UNLOCKING;
import UserState.STATE_SHUTDOWN;
import UserState.STATE_STOPPING;
import android.Manifest.permission;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
public class ShadowUserManagerTest {
    private UserManager userManager;

    private Context context;

    private static final int TEST_USER_HANDLE = 0;

    private static final int PROFILE_USER_HANDLE = 2;

    private static final String PROFILE_USER_NAME = "profile";

    private static final String SEED_ACCOUNT_TYPE = "seed_account_type";

    private static final int PROFILE_USER_FLAGS = 0;

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void shouldGetUserProfiles() {
        assertThat(userManager.getUserProfiles()).contains(myUserHandle());
        UserHandle anotherProfile = ShadowUserManagerTest.newUserHandle(2);
        Shadows.shadowOf(userManager).addUserProfile(anotherProfile);
        assertThat(userManager.getUserProfiles()).containsExactly(myUserHandle(), anotherProfile);
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR2)
    public void testGetApplicationRestrictions() {
        String packageName = context.getPackageName();
        assertThat(userManager.getApplicationRestrictions(packageName).size()).isEqualTo(0);
        Bundle restrictions = new Bundle();
        restrictions.putCharSequence("test_key", "test_value");
        Shadows.shadowOf(userManager).setApplicationRestrictions(packageName, restrictions);
        assertThat(userManager.getApplicationRestrictions(packageName).getCharSequence("test_key").toString()).isEqualTo("test_value");
    }

    @Test
    @Config(minSdk = VERSION_CODES.N)
    public void isUserUnlocked() {
        assertThat(userManager.isUserUnlocked()).isTrue();
        Shadows.shadowOf(userManager).setUserUnlocked(false);
        assertThat(userManager.isUserUnlocked()).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void hasUserRestriction() {
        assertThat(userManager.hasUserRestriction(ENSURE_VERIFY_APPS)).isFalse();
        UserHandle userHandle = Process.myUserHandle();
        Shadows.shadowOf(userManager).setUserRestriction(userHandle, ENSURE_VERIFY_APPS, true);
        assertThat(userManager.hasUserRestriction(ENSURE_VERIFY_APPS)).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR2)
    public void getUserRestrictions() {
        assertThat(userManager.getUserRestrictions().size()).isEqualTo(0);
        UserHandle userHandle = Process.myUserHandle();
        Shadows.shadowOf(userManager).setUserRestriction(userHandle, ENSURE_VERIFY_APPS, true);
        Bundle restrictions = userManager.getUserRestrictions();
        assertThat(restrictions.size()).isEqualTo(1);
        assertThat(restrictions.getBoolean(ENSURE_VERIFY_APPS)).isTrue();
        // make sure that the bundle is not an internal state
        restrictions.putBoolean("something", true);
        restrictions = userManager.getUserRestrictions();
        assertThat(restrictions.size()).isEqualTo(1);
        Shadows.shadowOf(userManager).setUserRestriction(ShadowUserManagerTest.newUserHandle(10), DISALLOW_CAMERA, true);
        assertThat(userManager.hasUserRestriction(DISALLOW_CAMERA)).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR2)
    public void clearUserRestrictions() {
        assertThat(userManager.getUserRestrictions().size()).isEqualTo(0);
        Shadows.shadowOf(userManager).setUserRestriction(myUserHandle(), ENSURE_VERIFY_APPS, true);
        assertThat(userManager.getUserRestrictions().size()).isEqualTo(1);
        Shadows.shadowOf(userManager).clearUserRestrictions(myUserHandle());
        assertThat(userManager.getUserRestrictions().size()).isEqualTo(0);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void isManagedProfile() {
        assertThat(userManager.isManagedProfile()).isFalse();
        Shadows.shadowOf(userManager).setManagedProfile(true);
        assertThat(userManager.isManagedProfile()).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void enforcePermissionChecks() throws Exception {
        Shadows.shadowOf(userManager).enforcePermissionChecks(true);
        try {
            userManager.isManagedProfile();
            Assert.fail("Expected exception");
        } catch (SecurityException expected) {
        }
        Application context = ApplicationProvider.getApplicationContext();
        PackageInfo packageInfo = Shadows.shadowOf(context.getPackageManager()).getInternalMutablePackageInfo(context.getPackageName());
        packageInfo.requestedPermissions = new String[]{ permission.MANAGE_USERS };
        Shadows.shadowOf(userManager).setManagedProfile(true);
        assertThat(userManager.isManagedProfile()).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void shouldGetSerialNumberForUser() {
        long serialNumberInvalid = -1L;
        UserHandle userHandle = ShadowUserManagerTest.newUserHandle(10);
        assertThat(userManager.getSerialNumberForUser(userHandle)).isEqualTo(serialNumberInvalid);
        assertThat(userManager.getUserSerialNumber(userHandle.getIdentifier())).isEqualTo(serialNumberInvalid);
        Shadows.shadowOf(userManager).addUserProfile(userHandle);
        assertThat(userManager.getSerialNumberForUser(userHandle)).isNotEqualTo(serialNumberInvalid);
        assertThat(userManager.getUserSerialNumber(userHandle.getIdentifier())).isNotEqualTo(serialNumberInvalid);
    }

    @Test
    @Config(minSdk = VERSION_CODES.N_MR1)
    public void isDemoUser() {
        // All methods are based on the current user, so no need to pass a UserHandle.
        assertThat(userManager.isDemoUser()).isFalse();
        Shadows.shadowOf(userManager).setIsDemoUser(true);
        assertThat(userManager.isDemoUser()).isTrue();
        Shadows.shadowOf(userManager).setIsDemoUser(false);
        assertThat(userManager.isDemoUser()).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void isSystemUser() {
        assertThat(userManager.isSystemUser()).isTrue();
        Shadows.shadowOf(userManager).setIsSystemUser(false);
        assertThat(userManager.isSystemUser()).isFalse();
        Shadows.shadowOf(userManager).setIsSystemUser(true);
        assertThat(userManager.isSystemUser()).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.N)
    public void isPrimaryUser() {
        assertThat(userManager.isPrimaryUser()).isTrue();
        Shadows.shadowOf(userManager).setIsPrimaryUser(false);
        assertThat(userManager.isPrimaryUser()).isFalse();
        Shadows.shadowOf(userManager).setIsPrimaryUser(true);
        assertThat(userManager.isPrimaryUser()).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR2)
    public void isLinkedUser() {
        assertThat(userManager.isLinkedUser()).isFalse();
        Shadows.shadowOf(userManager).setIsLinkedUser(true);
        assertThat(userManager.isLinkedUser()).isTrue();
        Shadows.shadowOf(userManager).setIsLinkedUser(false);
        assertThat(userManager.isLinkedUser()).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.KITKAT_WATCH)
    public void isGuestUser() {
        assertThat(userManager.isGuestUser()).isFalse();
        Shadows.shadowOf(userManager).setIsGuestUser(true);
        assertThat(userManager.isGuestUser()).isTrue();
        Shadows.shadowOf(userManager).setIsGuestUser(false);
        assertThat(userManager.isGuestUser()).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void isUserRunning() {
        UserHandle userHandle = ShadowUserManagerTest.newUserHandle(0);
        assertThat(userManager.isUserRunning(userHandle)).isFalse();
        Shadows.shadowOf(userManager).setUserState(userHandle, STATE_RUNNING_UNLOCKED);
        assertThat(userManager.isUserRunning(userHandle)).isTrue();
        Shadows.shadowOf(userManager).setUserState(userHandle, STATE_RUNNING_LOCKED);
        assertThat(userManager.isUserRunning(userHandle)).isTrue();
        Shadows.shadowOf(userManager).setUserState(userHandle, STATE_RUNNING_UNLOCKING);
        assertThat(userManager.isUserRunning(userHandle)).isTrue();
        Shadows.shadowOf(userManager).setUserState(userHandle, STATE_STOPPING);
        assertThat(userManager.isUserRunning(userHandle)).isFalse();
        Shadows.shadowOf(userManager).setUserState(userHandle, STATE_BOOTING);
        assertThat(userManager.isUserRunning(userHandle)).isFalse();
        Shadows.shadowOf(userManager).setUserState(userHandle, STATE_SHUTDOWN);
        assertThat(userManager.isUserRunning(userHandle)).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void isUserRunningOrStopping() {
        UserHandle userHandle = ShadowUserManagerTest.newUserHandle(0);
        assertThat(userManager.isUserRunningOrStopping(userHandle)).isFalse();
        Shadows.shadowOf(userManager).setUserState(userHandle, STATE_RUNNING_UNLOCKED);
        assertThat(userManager.isUserRunningOrStopping(userHandle)).isTrue();
        Shadows.shadowOf(userManager).setUserState(userHandle, STATE_RUNNING_LOCKED);
        assertThat(userManager.isUserRunningOrStopping(userHandle)).isTrue();
        Shadows.shadowOf(userManager).setUserState(userHandle, STATE_RUNNING_UNLOCKING);
        assertThat(userManager.isUserRunningOrStopping(userHandle)).isTrue();
        Shadows.shadowOf(userManager).setUserState(userHandle, STATE_STOPPING);
        assertThat(userManager.isUserRunningOrStopping(userHandle)).isTrue();
        Shadows.shadowOf(userManager).setUserState(userHandle, STATE_BOOTING);
        assertThat(userManager.isUserRunningOrStopping(userHandle)).isFalse();
        Shadows.shadowOf(userManager).setUserState(userHandle, STATE_SHUTDOWN);
        assertThat(userManager.isUserRunningOrStopping(userHandle)).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void addSecondaryUser() {
        assertThat(userManager.getUserCount()).isEqualTo(1);
        UserHandle userHandle = Shadows.shadowOf(userManager).addUser(10, "secondary_user", 0);
        assertThat(userHandle.getIdentifier()).isEqualTo(10);
        assertThat(userManager.getUserCount()).isEqualTo(2);
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void removeSecondaryUser() {
        Shadows.shadowOf(userManager).addUser(10, "secondary_user", 0);
        assertThat(Shadows.shadowOf(userManager).removeUser(10)).isTrue();
        assertThat(userManager.getUserCount()).isEqualTo(1);
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void switchToSecondaryUser() {
        Shadows.shadowOf(userManager).addUser(10, "secondary_user", 0);
        Shadows.shadowOf(userManager).switchUser(10);
        assertThat(UserHandle.myUserId()).isEqualTo(10);
    }

    @Test
    @Config(minSdk = VERSION_CODES.N)
    public void canSwitchUser() {
        assertThat(Shadows.shadowOf(userManager).canSwitchUsers()).isFalse();
        Shadows.shadowOf(userManager).setCanSwitchUser(true);
        assertThat(Shadows.shadowOf(userManager).canSwitchUsers()).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void getUsers() {
        assertThat(userManager.getUsers()).hasSize(1);
        Shadows.shadowOf(userManager).addUser(10, "secondary_user", 0);
        assertThat(userManager.getUsers()).hasSize(2);
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void getUserInfo() {
        Shadows.shadowOf(userManager).addUser(10, "secondary_user", 0);
        assertThat(userManager.getUserInfo(10)).isNotNull();
        assertThat(userManager.getUserInfo(10).name).isEqualTo("secondary_user");
    }

    @Test
    @Config(minSdk = VERSION_CODES.N)
    public void switchToUserNotAddedShouldThrowException() {
        try {
            Shadows.shadowOf(userManager).switchUser(10);
            Assert.fail("Switching to the user that was never added should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            assertThat(e).hasMessageThat().isEqualTo("Must add user before switching to it");
        }
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void getProfiles_addedProfile_containsProfile() {
        Shadows.shadowOf(userManager).addProfile(ShadowUserManagerTest.TEST_USER_HANDLE, ShadowUserManagerTest.PROFILE_USER_HANDLE, ShadowUserManagerTest.PROFILE_USER_NAME, ShadowUserManagerTest.PROFILE_USER_FLAGS);
        assertThat(userManager.getProfiles(ShadowUserManagerTest.TEST_USER_HANDLE).get(0).id).isEqualTo(ShadowUserManagerTest.PROFILE_USER_HANDLE);
    }

    @Test
    @Config(minSdk = VERSION_CODES.N)
    public void supportsMultipleUsers() {
        assertThat(UserManager.supportsMultipleUsers()).isFalse();
        Shadows.shadowOf(userManager).setSupportsMultipleUsers(true);
        assertThat(UserManager.supportsMultipleUsers()).isTrue();
    }
}

