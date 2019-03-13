package org.robolectric.shadows;


import ActivityInfo.CONFIG_MCC;
import ActivityInfo.CONFIG_MNC;
import ActivityInfo.CONFIG_ORIENTATION;
import ActivityInfo.CONFIG_SCREEN_LAYOUT;
import Color.WHITE;
import Context.DEVICE_POLICY_SERVICE;
import Intent.ACTION_VIEW;
import Intent.CATEGORY_APP_BROWSER;
import Intent.CATEGORY_APP_CALENDAR;
import Intent.CATEGORY_DEFAULT;
import Intent.CATEGORY_HOME;
import Intent.CATEGORY_LAUNCHER;
import PackageInstaller.Session;
import PackageInstaller.SessionInfo;
import PackageManager.COMPONENT_ENABLED_STATE_DEFAULT;
import PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
import PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
import PackageManager.DELETE_FAILED_INTERNAL_ERROR;
import PackageManager.DELETE_SUCCEEDED;
import PackageManager.DONT_KILL_APP;
import PackageManager.FEATURE_CAMERA;
import PackageManager.GET_ACTIVITIES;
import PackageManager.GET_INTENT_FILTERS;
import PackageManager.GET_META_DATA;
import PackageManager.GET_PERMISSIONS;
import PackageManager.GET_PROVIDERS;
import PackageManager.GET_RECEIVERS;
import PackageManager.GET_RESOLVED_FILTER;
import PackageManager.GET_SERVICES;
import PackageManager.GET_SHARED_LIBRARY_FILES;
import PackageManager.MATCH_ALL;
import PackageManager.MATCH_DISABLED_COMPONENTS;
import PackageManager.MATCH_SYSTEM_ONLY;
import PathPermission.PATTERN_SIMPLE_GLOB;
import PermissionInfo.PROTECTION_DANGEROUS;
import PermissionInfo.PROTECTION_NORMAL;
import android.Manifest.permission;
import android.Manifest.permission_group;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ChangedPackages;
import android.content.pm.FeatureInfo;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageParser.PermissionGroup;
import android.content.pm.PackageStats;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.DocumentsContract;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.R;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowPackageManager.PackageSetting;
import org.robolectric.shadows.ShadowPackageManager.ResolveInfoComparator;
import org.robolectric.util.ReflectionHelpers;
import org.robolectric.util.ReflectionHelpers.ClassParameter;
import permission_group.CAMERA;

import static org.robolectric.R.bool.false_bool_value;
import static org.robolectric.R.color.clear;
import static org.robolectric.R.drawable.an_image;
import static org.robolectric.R.integer.test_integer1;
import static org.robolectric.R.string.app_name;
import static org.robolectric.R.string.str_int;
import static org.robolectric.R.string.test_permission_description;
import static org.robolectric.R.string.test_permission_label;
import static org.robolectric.R.xml.dialog_preferences;


@RunWith(AndroidJUnit4.class)
public class ShadowPackageManagerTest {
    private static final String TEST_PACKAGE_NAME = "com.some.other.package";

    private static final String TEST_PACKAGE_LABEL = "My Little App";

    private static final String TEST_APP_PATH = "/values/app/application.apk";

    private static final String TEST_PACKAGE2_NAME = "com.a.second.package";

    private static final String TEST_PACKAGE2_LABEL = "A Second App";

    private static final String TEST_APP2_PATH = "/values/app/application2.apk";

    private static final Object USER_ID = 1;

    protected ShadowPackageManager shadowPackageManager;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private PackageManager packageManager;

    private final ArgumentCaptor<PackageStats> packageStatsCaptor = ArgumentCaptor.forClass(PackageStats.class);

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void packageInstallerCreateSession() throws Exception {
        PackageInstaller packageInstaller = ApplicationProvider.getApplicationContext().getPackageManager().getPackageInstaller();
        int sessionId = packageInstaller.createSession(ShadowPackageManagerTest.createSessionParams("packageName"));
        PackageInstaller.SessionInfo sessionInfo = packageInstaller.getSessionInfo(sessionId);
        assertThat(sessionInfo.isActive()).isTrue();
        assertThat(sessionInfo.appPackageName).isEqualTo("packageName");
        packageInstaller.abandonSession(sessionId);
        assertThat(packageInstaller.getSessionInfo(sessionId)).isNull();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void packageInstallerOpenSession() throws Exception {
        PackageInstaller packageInstaller = ApplicationProvider.getApplicationContext().getPackageManager().getPackageInstaller();
        int sessionId = packageInstaller.createSession(ShadowPackageManagerTest.createSessionParams("packageName"));
        PackageInstaller.Session session = packageInstaller.openSession(sessionId);
        assertThat(session).isNotNull();
    }

    @Test
    public void packageInstallerAndGetPackageArchiveInfo() {
        ApplicationInfo appInfo = new ApplicationInfo();
        appInfo.flags = ApplicationInfo.FLAG_INSTALLED;
        appInfo.packageName = ShadowPackageManagerTest.TEST_PACKAGE_NAME;
        appInfo.sourceDir = ShadowPackageManagerTest.TEST_APP_PATH;
        appInfo.name = ShadowPackageManagerTest.TEST_PACKAGE_LABEL;
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = ShadowPackageManagerTest.TEST_PACKAGE_NAME;
        packageInfo.applicationInfo = appInfo;
        shadowPackageManager.installPackage(packageInfo);
        PackageInfo packageInfoResult = shadowPackageManager.getPackageArchiveInfo(ShadowPackageManagerTest.TEST_APP_PATH, 0);
        assertThat(packageInfoResult).isNotNull();
        ApplicationInfo applicationInfo = packageInfoResult.applicationInfo;
        assertThat(applicationInfo).isInstanceOf(ApplicationInfo.class);
        assertThat(applicationInfo.packageName).isEqualTo(ShadowPackageManagerTest.TEST_PACKAGE_NAME);
        assertThat(applicationInfo.sourceDir).isEqualTo(ShadowPackageManagerTest.TEST_APP_PATH);
    }

    @Test
    public void applicationFlags() throws Exception {
        int flags = packageManager.getApplicationInfo("org.robolectric", 0).flags;
        assertThat((flags & (FLAG_ALLOW_BACKUP))).isEqualTo(FLAG_ALLOW_BACKUP);
        assertThat((flags & (FLAG_ALLOW_CLEAR_USER_DATA))).isEqualTo(FLAG_ALLOW_CLEAR_USER_DATA);
        assertThat((flags & (FLAG_ALLOW_TASK_REPARENTING))).isEqualTo(FLAG_ALLOW_TASK_REPARENTING);
        assertThat((flags & (FLAG_DEBUGGABLE))).isEqualTo(FLAG_DEBUGGABLE);
        assertThat((flags & (FLAG_HAS_CODE))).isEqualTo(FLAG_HAS_CODE);
        assertThat((flags & (FLAG_RESIZEABLE_FOR_SCREENS))).isEqualTo(FLAG_RESIZEABLE_FOR_SCREENS);
        assertThat((flags & (FLAG_SUPPORTS_LARGE_SCREENS))).isEqualTo(FLAG_SUPPORTS_LARGE_SCREENS);
        assertThat((flags & (FLAG_SUPPORTS_NORMAL_SCREENS))).isEqualTo(FLAG_SUPPORTS_NORMAL_SCREENS);
        assertThat((flags & (FLAG_SUPPORTS_SCREEN_DENSITIES))).isEqualTo(FLAG_SUPPORTS_SCREEN_DENSITIES);
        assertThat((flags & (FLAG_SUPPORTS_SMALL_SCREENS))).isEqualTo(FLAG_SUPPORTS_SMALL_SCREENS);
        assertThat((flags & (FLAG_VM_SAFE_MODE))).isEqualTo(FLAG_VM_SAFE_MODE);
    }

    /**
     * Tests the permission grants of this test package.
     *
     * <p>These grants are defined in the test package's AndroidManifest.xml.
     */
    @Test
    public void testCheckPermission_thisPackage() throws Exception {
        String thisPackage = ApplicationProvider.getApplicationContext().getPackageName();
        Assert.assertEquals(PERMISSION_GRANTED, packageManager.checkPermission("android.permission.INTERNET", thisPackage));
        Assert.assertEquals(PERMISSION_GRANTED, packageManager.checkPermission("android.permission.SYSTEM_ALERT_WINDOW", thisPackage));
        Assert.assertEquals(PERMISSION_GRANTED, packageManager.checkPermission("android.permission.GET_TASKS", thisPackage));
        Assert.assertEquals(PERMISSION_DENIED, packageManager.checkPermission("android.permission.ACCESS_FINE_LOCATION", thisPackage));
        Assert.assertEquals(PERMISSION_DENIED, packageManager.checkPermission("android.permission.ACCESS_FINE_LOCATION", "random-package"));
    }

    /**
     * Tests the permission grants of other packages. These packages are added to the PackageManager
     * by calling {@link ShadowPackageManager#addPackage}.
     */
    @Test
    public void testCheckPermission_otherPackages() throws Exception {
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = ShadowPackageManagerTest.TEST_PACKAGE_NAME;
        packageInfo.requestedPermissions = new String[]{ "android.permission.INTERNET", "android.permission.SEND_SMS" };
        // Grant one of the permissions.
        packageInfo.requestedPermissionsFlags = new int[]{ REQUESTED_PERMISSION_GRANTED, 0/* this permission isn't granted */
         };
        shadowPackageManager.installPackage(packageInfo);
        Assert.assertEquals(PERMISSION_GRANTED, packageManager.checkPermission("android.permission.INTERNET", ShadowPackageManagerTest.TEST_PACKAGE_NAME));
        Assert.assertEquals(PERMISSION_DENIED, packageManager.checkPermission("android.permission.SEND_SMS", ShadowPackageManagerTest.TEST_PACKAGE_NAME));
        Assert.assertEquals(PERMISSION_DENIED, packageManager.checkPermission("android.permission.READ_SMS", ShadowPackageManagerTest.TEST_PACKAGE_NAME));
    }

    /**
     * Tests the permission grants of other packages. These packages are added to the PackageManager
     * by calling {@link ShadowPackageManager#addPackage}.
     */
    @Test
    public void testCheckPermission_otherPackages_grantedByDefault() throws Exception {
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = ShadowPackageManagerTest.TEST_PACKAGE_NAME;
        packageInfo.requestedPermissions = new String[]{ "android.permission.INTERNET", "android.permission.SEND_SMS" };
        shadowPackageManager.installPackage(packageInfo);
        // Because we didn't specify permission grant state in the PackageInfo object, all requested
        // permissions are automatically granted. See ShadowPackageManager.grantPermissionsByDefault()
        // for the explanation.
        Assert.assertEquals(PERMISSION_GRANTED, packageManager.checkPermission("android.permission.INTERNET", ShadowPackageManagerTest.TEST_PACKAGE_NAME));
        Assert.assertEquals(PERMISSION_GRANTED, packageManager.checkPermission("android.permission.SEND_SMS", ShadowPackageManagerTest.TEST_PACKAGE_NAME));
        Assert.assertEquals(PERMISSION_DENIED, packageManager.checkPermission("android.permission.READ_SMS", ShadowPackageManagerTest.TEST_PACKAGE_NAME));
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void testGrantRuntimePermission() throws Exception {
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = ShadowPackageManagerTest.TEST_PACKAGE_NAME;
        packageInfo.requestedPermissions = new String[]{ "android.permission.SEND_SMS", "android.permission.READ_SMS" };
        packageInfo.requestedPermissionsFlags = new int[]{ 0, 0 };// Not granted by default

        shadowPackageManager.installPackage(packageInfo);
        packageManager.grantRuntimePermission(ShadowPackageManagerTest.TEST_PACKAGE_NAME, "android.permission.SEND_SMS", myUserHandle());
        assertThat(packageInfo.requestedPermissionsFlags[0]).isEqualTo(REQUESTED_PERMISSION_GRANTED);
        assertThat(packageInfo.requestedPermissionsFlags[1]).isEqualTo(0);
        packageManager.grantRuntimePermission(ShadowPackageManagerTest.TEST_PACKAGE_NAME, "android.permission.READ_SMS", myUserHandle());
        assertThat(packageInfo.requestedPermissionsFlags[0]).isEqualTo(REQUESTED_PERMISSION_GRANTED);
        assertThat(packageInfo.requestedPermissionsFlags[1]).isEqualTo(REQUESTED_PERMISSION_GRANTED);
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void testGrantRuntimePermission_packageNotFound() throws Exception {
        try {
            packageManager.grantRuntimePermission("com.unknown.package", "android.permission.SEND_SMS", myUserHandle());
            Assert.fail("Exception expected");
        } catch (SecurityException expected) {
        }
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void testGrantRuntimePermission_doesntRequestPermission() throws Exception {
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = ShadowPackageManagerTest.TEST_PACKAGE_NAME;
        packageInfo.requestedPermissions = new String[]{ "android.permission.SEND_SMS", "android.permission.READ_SMS" };
        packageInfo.requestedPermissionsFlags = new int[]{ 0, 0 };// Not granted by default

        shadowPackageManager.installPackage(packageInfo);
        try {
            // This permission is not granted to the package.
            packageManager.grantRuntimePermission(ShadowPackageManagerTest.TEST_PACKAGE_NAME, "android.permission.RECEIVE_SMS", myUserHandle());
            Assert.fail("Exception expected");
        } catch (SecurityException expected) {
        }
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void testRevokeRuntimePermission() throws Exception {
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = ShadowPackageManagerTest.TEST_PACKAGE_NAME;
        packageInfo.requestedPermissions = new String[]{ "android.permission.SEND_SMS", "android.permission.READ_SMS" };
        packageInfo.requestedPermissionsFlags = new int[]{ REQUESTED_PERMISSION_GRANTED, REQUESTED_PERMISSION_GRANTED };
        shadowPackageManager.installPackage(packageInfo);
        packageManager.revokeRuntimePermission(ShadowPackageManagerTest.TEST_PACKAGE_NAME, "android.permission.SEND_SMS", myUserHandle());
        assertThat(packageInfo.requestedPermissionsFlags[0]).isEqualTo(0);
        assertThat(packageInfo.requestedPermissionsFlags[1]).isEqualTo(REQUESTED_PERMISSION_GRANTED);
        packageManager.revokeRuntimePermission(ShadowPackageManagerTest.TEST_PACKAGE_NAME, "android.permission.READ_SMS", myUserHandle());
        assertThat(packageInfo.requestedPermissionsFlags[0]).isEqualTo(0);
        assertThat(packageInfo.requestedPermissionsFlags[1]).isEqualTo(0);
    }

    @Test
    public void testQueryBroadcastReceiverSucceeds() {
        Intent intent = new Intent("org.robolectric.ACTION_RECEIVER_PERMISSION_PACKAGE");
        intent.setPackage(ApplicationProvider.getApplicationContext().getPackageName());
        List<ResolveInfo> receiverInfos = packageManager.queryBroadcastReceivers(intent, GET_RESOLVED_FILTER);
        assertThat(receiverInfos).isNotEmpty();
        assertThat(receiverInfos.get(0).activityInfo.name).isEqualTo("org.robolectric.ConfigTestReceiverPermissionsAndActions");
        assertThat(receiverInfos.get(0).activityInfo.permission).isEqualTo("org.robolectric.CUSTOM_PERM");
        assertThat(receiverInfos.get(0).filter.getAction(0)).isEqualTo("org.robolectric.ACTION_RECEIVER_PERMISSION_PACKAGE");
    }

    @Test
    public void testQueryBroadcastReceiverFailsForMissingPackageName() {
        Intent intent = new Intent("org.robolectric.ACTION_ONE_MORE_PACKAGE");
        List<ResolveInfo> receiverInfos = packageManager.queryBroadcastReceivers(intent, GET_RESOLVED_FILTER);
        assertThat(receiverInfos).isEmpty();
    }

    @Test
    public void testQueryBroadcastReceiver_matchAllWithoutIntentFilter() {
        Intent intent = new Intent();
        intent.setPackage(ApplicationProvider.getApplicationContext().getPackageName());
        List<ResolveInfo> receiverInfos = packageManager.queryBroadcastReceivers(intent, GET_INTENT_FILTERS);
        assertThat(receiverInfos).hasSize(7);
        for (ResolveInfo receiverInfo : receiverInfos) {
            assertThat(receiverInfo.activityInfo.name).isNotEqualTo("com.bar.ReceiverWithoutIntentFilter");
        }
    }

    @Test
    public void testGetPackageInfo_ForReceiversSucceeds() throws Exception {
        PackageInfo receiverInfos = packageManager.getPackageInfo(ApplicationProvider.getApplicationContext().getPackageName(), GET_RECEIVERS);
        assertThat(receiverInfos.receivers).isNotEmpty();
        assertThat(receiverInfos.receivers[0].name).isEqualTo("org.robolectric.ConfigTestReceiver.InnerReceiver");
        assertThat(receiverInfos.receivers[0].permission).isEqualTo("com.ignored.PERM");
    }

    private static class ActivityWithConfigChanges extends Activity {}

    @Test
    public void getActivityMetaData_configChanges() throws Exception {
        Activity activity = Robolectric.setupActivity(ShadowPackageManagerTest.ActivityWithConfigChanges.class);
        ActivityInfo activityInfo = activity.getPackageManager().getActivityInfo(activity.getComponentName(), 0);
        int configChanges = activityInfo.configChanges;
        assertThat((configChanges & (ActivityInfo.CONFIG_SCREEN_LAYOUT))).isEqualTo(CONFIG_SCREEN_LAYOUT);
        assertThat((configChanges & (ActivityInfo.CONFIG_ORIENTATION))).isEqualTo(CONFIG_ORIENTATION);
        // Spot check a few other possible values that shouldn't be in the flags.
        assertThat((configChanges & (ActivityInfo.CONFIG_FONT_SCALE))).isEqualTo(0);
        assertThat((configChanges & (ActivityInfo.CONFIG_SCREEN_SIZE))).isEqualTo(0);
    }

    /**
     * MCC + MNC are always present in config changes since Oreo.
     */
    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void getActivityMetaData_configChangesAlwaysIncludesMccAndMnc() throws Exception {
        Activity activity = Robolectric.setupActivity(ShadowPackageManagerTest.ActivityWithConfigChanges.class);
        ActivityInfo activityInfo = activity.getPackageManager().getActivityInfo(activity.getComponentName(), 0);
        int configChanges = activityInfo.configChanges;
        assertThat((configChanges & (ActivityInfo.CONFIG_MCC))).isEqualTo(CONFIG_MCC);
        assertThat((configChanges & (ActivityInfo.CONFIG_MNC))).isEqualTo(CONFIG_MNC);
    }

    @Test
    public void getPermissionInfo_withMinimalFields() throws Exception {
        PermissionInfo permission = packageManager.getPermissionInfo("org.robolectric.permission_with_minimal_fields", 0);
        assertThat(permission.labelRes).isEqualTo(0);
        assertThat(permission.descriptionRes).isEqualTo(0);
        assertThat(permission.protectionLevel).isEqualTo(PROTECTION_NORMAL);
    }

    @Test
    public void getPermissionInfo_addedPermissions() throws Exception {
        PermissionInfo permissionInfo = new PermissionInfo();
        permissionInfo.name = "manually_added_permission";
        shadowPackageManager.addPermissionInfo(permissionInfo);
        PermissionInfo permission = packageManager.getPermissionInfo("manually_added_permission", 0);
        assertThat(permission.name).isEqualTo("manually_added_permission");
    }

    @Test
    public void getPermissionGroupInfo_fromManifest() throws Exception {
        PermissionGroupInfo permissionGroupInfo = ApplicationProvider.getApplicationContext().getPackageManager().getPermissionGroupInfo("org.robolectric.package_permission_group", 0);
        assertThat(permissionGroupInfo.name).isEqualTo("org.robolectric.package_permission_group");
    }

    @Test
    public void getPermissionGroupInfo_extraPermissionGroup() throws Exception {
        PermissionGroupInfo newCameraPermission = new PermissionGroupInfo();
        newCameraPermission.name = permission_group.CAMERA;
        shadowPackageManager.addPermissionGroupInfo(newCameraPermission);
        assertThat(packageManager.getPermissionGroupInfo(CAMERA, 0).name).isEqualTo(newCameraPermission.name);
    }

    @Test
    public void getAllPermissionGroups_fromManifest() throws Exception {
        List<PermissionGroupInfo> allPermissionGroups = packageManager.getAllPermissionGroups(0);
        assertThat(allPermissionGroups).hasSize(1);
        assertThat(allPermissionGroups.get(0).name).isEqualTo("org.robolectric.package_permission_group");
    }

    @Test
    public void getAllPermissionGroups_duplicateInExtraPermissions() throws Exception {
        assertThat(packageManager.getAllPermissionGroups(0)).hasSize(1);
        PermissionGroupInfo overriddenPermission = new PermissionGroupInfo();
        overriddenPermission.name = "org.robolectric.package_permission_group";
        shadowPackageManager.addPermissionGroupInfo(overriddenPermission);
        PermissionGroupInfo newCameraPermission = new PermissionGroupInfo();
        newCameraPermission.name = permission_group.CAMERA;
        shadowPackageManager.addPermissionGroupInfo(newCameraPermission);
        List<PermissionGroupInfo> allPermissionGroups = packageManager.getAllPermissionGroups(0);
        assertThat(allPermissionGroups).hasSize(2);
    }

    @Test
    public void getAllPermissionGroups_duplicatePermission() throws Exception {
        assertThat(packageManager.getAllPermissionGroups(0)).hasSize(1);
        // Package 1
        Package pkg = new Package(ShadowPackageManagerTest.TEST_PACKAGE_NAME);
        ApplicationInfo appInfo = pkg.applicationInfo;
        appInfo.flags = ApplicationInfo.FLAG_INSTALLED;
        appInfo.packageName = ShadowPackageManagerTest.TEST_PACKAGE_NAME;
        appInfo.sourceDir = ShadowPackageManagerTest.TEST_APP_PATH;
        appInfo.name = ShadowPackageManagerTest.TEST_PACKAGE_LABEL;
        PermissionGroupInfo contactsPermissionGroupInfoApp1 = new PermissionGroupInfo();
        contactsPermissionGroupInfoApp1.name = permission_group.CONTACTS;
        PermissionGroup contactsPermissionGroupApp1 = new PermissionGroup(pkg, contactsPermissionGroupInfoApp1);
        pkg.permissionGroups.add(contactsPermissionGroupApp1);
        PermissionGroupInfo storagePermissionGroupInfoApp1 = new PermissionGroupInfo();
        storagePermissionGroupInfoApp1.name = permission_group.STORAGE;
        PermissionGroup storagePermissionGroupApp1 = new PermissionGroup(pkg, storagePermissionGroupInfoApp1);
        pkg.permissionGroups.add(storagePermissionGroupApp1);
        shadowPackageManager.addPackageInternal(pkg);
        // Package 2, contains one permission group that is the same
        Package pkg2 = new Package(ShadowPackageManagerTest.TEST_PACKAGE2_NAME);
        ApplicationInfo appInfo2 = pkg2.applicationInfo;
        appInfo2.flags = ApplicationInfo.FLAG_INSTALLED;
        appInfo2.packageName = ShadowPackageManagerTest.TEST_PACKAGE2_NAME;
        appInfo2.sourceDir = ShadowPackageManagerTest.TEST_APP2_PATH;
        appInfo2.name = ShadowPackageManagerTest.TEST_PACKAGE2_LABEL;
        PermissionGroupInfo contactsPermissionGroupInfoApp2 = new PermissionGroupInfo();
        contactsPermissionGroupInfoApp2.name = permission_group.CONTACTS;
        PermissionGroup contactsPermissionGroupApp2 = new PermissionGroup(pkg2, contactsPermissionGroupInfoApp2);
        pkg2.permissionGroups.add(contactsPermissionGroupApp2);
        PermissionGroupInfo calendarPermissionGroupInfoApp2 = new PermissionGroupInfo();
        calendarPermissionGroupInfoApp2.name = permission_group.CALENDAR;
        PermissionGroup calendarPermissionGroupApp2 = new PermissionGroup(pkg2, calendarPermissionGroupInfoApp2);
        pkg2.permissionGroups.add(calendarPermissionGroupApp2);
        shadowPackageManager.addPackageInternal(pkg2);
        // Make sure that the duplicate permission group does not show up in the list
        // Total list should be: contacts, storage, calendar, "org.robolectric.package_permission_group"
        List<PermissionGroupInfo> allPermissionGroups = packageManager.getAllPermissionGroups(0);
        assertThat(allPermissionGroups).hasSize(4);
    }

    @Test
    public void getPackageArchiveInfo() {
        ApplicationInfo appInfo = new ApplicationInfo();
        appInfo.flags = ApplicationInfo.FLAG_INSTALLED;
        appInfo.packageName = ShadowPackageManagerTest.TEST_PACKAGE_NAME;
        appInfo.sourceDir = ShadowPackageManagerTest.TEST_APP_PATH;
        appInfo.name = ShadowPackageManagerTest.TEST_PACKAGE_LABEL;
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = ShadowPackageManagerTest.TEST_PACKAGE_NAME;
        packageInfo.applicationInfo = appInfo;
        shadowPackageManager.installPackage(packageInfo);
        PackageInfo packageInfoResult = shadowPackageManager.getPackageArchiveInfo(ShadowPackageManagerTest.TEST_APP_PATH, 0);
        assertThat(packageInfoResult).isNotNull();
        ApplicationInfo applicationInfo = packageInfoResult.applicationInfo;
        assertThat(applicationInfo).isInstanceOf(ApplicationInfo.class);
        assertThat(applicationInfo.packageName).isEqualTo(ShadowPackageManagerTest.TEST_PACKAGE_NAME);
        assertThat(applicationInfo.sourceDir).isEqualTo(ShadowPackageManagerTest.TEST_APP_PATH);
    }

    @Test
    public void getApplicationInfo_ThisApplication() throws Exception {
        ApplicationInfo info = packageManager.getApplicationInfo(ApplicationProvider.getApplicationContext().getPackageName(), 0);
        assertThat(info).isNotNull();
        assertThat(info.packageName).isEqualTo(ApplicationProvider.getApplicationContext().getPackageName());
        assertThat(info.processName).isEqualTo(info.packageName);
    }

    @Test
    public void getApplicationInfo_uninstalledApplication_includeUninstalled() throws Exception {
        shadowPackageManager.deletePackage(ApplicationProvider.getApplicationContext().getPackageName());
        ApplicationInfo info = packageManager.getApplicationInfo(ApplicationProvider.getApplicationContext().getPackageName(), MATCH_UNINSTALLED_PACKAGES);
        assertThat(info).isNotNull();
        assertThat(info.packageName).isEqualTo(ApplicationProvider.getApplicationContext().getPackageName());
    }

    @Test
    public void getApplicationInfo_uninstalledApplication_dontIncludeUninstalled() throws Exception {
        shadowPackageManager.deletePackage(ApplicationProvider.getApplicationContext().getPackageName());
        try {
            packageManager.getApplicationInfo(ApplicationProvider.getApplicationContext().getPackageName(), 0);
            Assert.fail("PackageManager.NameNotFoundException not thrown");
        } catch (PackageManager e) {
            // expected
        }
    }

    @Test(expected = NameNotFoundException.class)
    public void getApplicationInfo_whenUnknown_shouldThrowNameNotFoundException() throws Exception {
        try {
            packageManager.getApplicationInfo("unknown_package", 0);
            Assert.fail("should have thrown NameNotFoundException");
        } catch (PackageManager e) {
            assertThat(e.getMessage()).contains("unknown_package");
            throw e;
        }
    }

    @Test
    public void getApplicationInfo_OtherApplication() throws Exception {
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = ShadowPackageManagerTest.TEST_PACKAGE_NAME;
        packageInfo.applicationInfo = new ApplicationInfo();
        packageInfo.applicationInfo.packageName = ShadowPackageManagerTest.TEST_PACKAGE_NAME;
        packageInfo.applicationInfo.name = ShadowPackageManagerTest.TEST_PACKAGE_LABEL;
        shadowPackageManager.installPackage(packageInfo);
        ApplicationInfo info = packageManager.getApplicationInfo(ShadowPackageManagerTest.TEST_PACKAGE_NAME, 0);
        assertThat(info).isNotNull();
        assertThat(info.packageName).isEqualTo(ShadowPackageManagerTest.TEST_PACKAGE_NAME);
        assertThat(packageManager.getApplicationLabel(info).toString()).isEqualTo(ShadowPackageManagerTest.TEST_PACKAGE_LABEL);
    }

    @Test
    public void removePackage_shouldHideItFromGetApplicationInfo() {
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = ShadowPackageManagerTest.TEST_PACKAGE_NAME;
        packageInfo.applicationInfo = new ApplicationInfo();
        packageInfo.applicationInfo.packageName = ShadowPackageManagerTest.TEST_PACKAGE_NAME;
        packageInfo.applicationInfo.name = ShadowPackageManagerTest.TEST_PACKAGE_LABEL;
        shadowPackageManager.installPackage(packageInfo);
        shadowPackageManager.removePackage(ShadowPackageManagerTest.TEST_PACKAGE_NAME);
        try {
            packageManager.getApplicationInfo(ShadowPackageManagerTest.TEST_PACKAGE_NAME, 0);
            Assert.fail("NameNotFoundException not thrown");
        } catch (NameNotFoundException e) {
            // expected
        }
    }

    @Test
    public void queryIntentActivities_EmptyResult() throws Exception {
        Intent i = new Intent(Intent.ACTION_APP_ERROR, null);
        i.addCategory(CATEGORY_APP_BROWSER);
        List<ResolveInfo> activities = packageManager.queryIntentActivities(i, 0);
        assertThat(activities).isEmpty();
    }

    @Test
    public void queryIntentActivities_Match() throws Exception {
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(CATEGORY_LAUNCHER);
        ResolveInfo info = new ResolveInfo();
        info.nonLocalizedLabel = ShadowPackageManagerTest.TEST_PACKAGE_LABEL;
        info.activityInfo = new ActivityInfo();
        info.activityInfo.name = "name";
        info.activityInfo.packageName = ShadowPackageManagerTest.TEST_PACKAGE_NAME;
        shadowPackageManager.addResolveInfoForIntent(i, info);
        List<ResolveInfo> activities = packageManager.queryIntentActivities(i, 0);
        assertThat(activities).isNotNull();
        assertThat(activities).hasSize(2);
        assertThat(activities.get(0).nonLocalizedLabel.toString()).isEqualTo(ShadowPackageManagerTest.TEST_PACKAGE_LABEL);
    }

    @Test
    public void queryIntentActivities_ServiceMatch() throws Exception {
        Intent i = new Intent("SomeStrangeAction");
        ResolveInfo info = new ResolveInfo();
        info.nonLocalizedLabel = ShadowPackageManagerTest.TEST_PACKAGE_LABEL;
        info.serviceInfo = new ServiceInfo();
        info.serviceInfo.name = "name";
        info.serviceInfo.packageName = ShadowPackageManagerTest.TEST_PACKAGE_NAME;
        shadowPackageManager.addResolveInfoForIntent(i, info);
        List<ResolveInfo> activities = packageManager.queryIntentActivities(i, 0);
        assertThat(activities).isNotNull();
        assertThat(activities).isEmpty();
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void queryIntentActivitiesAsUser_EmptyResult() throws Exception {
        Intent i = new Intent(Intent.ACTION_APP_ERROR, null);
        i.addCategory(CATEGORY_APP_BROWSER);
        List<ResolveInfo> activities = packageManager.queryIntentActivitiesAsUser(i, 0, 0);
        assertThat(activities).isEmpty();
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void queryIntentActivitiesAsUser_Match() throws Exception {
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(CATEGORY_LAUNCHER);
        ResolveInfo info = new ResolveInfo();
        info.nonLocalizedLabel = ShadowPackageManagerTest.TEST_PACKAGE_LABEL;
        shadowPackageManager.addResolveInfoForIntent(i, info);
        List<ResolveInfo> activities = packageManager.queryIntentActivitiesAsUser(i, 0, 0);
        assertThat(activities).isNotNull();
        assertThat(activities).hasSize(2);
        assertThat(activities.get(0).nonLocalizedLabel.toString()).isEqualTo(ShadowPackageManagerTest.TEST_PACKAGE_LABEL);
    }

    @Test
    public void queryIntentActivities_launcher() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, MATCH_ALL);
        assertThat(resolveInfos).hasSize(1);
        assertThat(resolveInfos.get(0).activityInfo.name).isEqualTo("org.robolectric.shadows.TestActivityAlias");
        assertThat(resolveInfos.get(0).activityInfo.targetActivity).isEqualTo("org.robolectric.shadows.TestActivity");
    }

    @Test
    public void queryIntentActivities_MatchSystemOnly() throws Exception {
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(CATEGORY_LAUNCHER);
        ResolveInfo info1 = ShadowResolveInfo.newResolveInfo(ShadowPackageManagerTest.TEST_PACKAGE_LABEL, ShadowPackageManagerTest.TEST_PACKAGE_NAME);
        ResolveInfo info2 = ShadowResolveInfo.newResolveInfo("System App", "system.launcher");
        info2.activityInfo.applicationInfo.flags |= ApplicationInfo.FLAG_SYSTEM;
        info2.nonLocalizedLabel = "System App";
        shadowPackageManager.addResolveInfoForIntent(i, info1);
        shadowPackageManager.addResolveInfoForIntent(i, info2);
        List<ResolveInfo> activities = packageManager.queryIntentActivities(i, MATCH_SYSTEM_ONLY);
        assertThat(activities).isNotNull();
        assertThat(activities).hasSize(1);
        assertThat(activities.get(0).nonLocalizedLabel.toString()).isEqualTo("System App");
    }

    @Test
    public void queryIntentActivities_EmptyResultWithNoMatchingImplicitIntents() throws Exception {
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(CATEGORY_LAUNCHER);
        i.setDataAndType(Uri.parse("content://testhost1.com:1/testPath/test.jpeg"), "image/jpeg");
        List<ResolveInfo> activities = packageManager.queryIntentActivities(i, 0);
        assertThat(activities).isEmpty();
    }

    @Test
    public void queryIntentActivities_MatchWithExplicitIntent() throws Exception {
        Intent i = new Intent();
        i.setClassName(ApplicationProvider.getApplicationContext(), "org.robolectric.shadows.TestActivity");
        List<ResolveInfo> activities = packageManager.queryIntentActivities(i, 0);
        assertThat(activities).isNotNull();
        assertThat(activities).hasSize(1);
        assertThat(activities.get(0).resolvePackageName).isEqualTo("org.robolectric");
        assertThat(activities.get(0).activityInfo.name).isEqualTo("org.robolectric.shadows.TestActivity");
    }

    @Test
    public void queryIntentActivities_MatchWithImplicitIntents() throws Exception {
        Uri uri = Uri.parse("content://testhost1.com:1/testPath/test.jpeg");
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addCategory(CATEGORY_DEFAULT);
        i.setDataAndType(uri, "image/jpeg");
        List<ResolveInfo> activities = packageManager.queryIntentActivities(i, 0);
        assertThat(activities).isNotNull();
        assertThat(activities).hasSize(1);
        assertThat(activities.get(0).resolvePackageName).isEqualTo("org.robolectric");
        assertThat(activities.get(0).activityInfo.name).isEqualTo("org.robolectric.shadows.TestActivity");
    }

    @Test
    public void queryIntentActivities_MatchWithAliasIntents() throws Exception {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(CATEGORY_LAUNCHER);
        List<ResolveInfo> activities = packageManager.queryIntentActivities(i, 0);
        assertThat(activities).isNotNull();
        assertThat(activities).hasSize(1);
        assertThat(activities.get(0).resolvePackageName).isEqualTo("org.robolectric");
        assertThat(activities.get(0).activityInfo.targetActivity).isEqualTo("org.robolectric.shadows.TestActivity");
        assertThat(activities.get(0).activityInfo.name).isEqualTo("org.robolectric.shadows.TestActivityAlias");
    }

    @Test
    public void queryIntentActivities_DisabledComponentExplicitIntent() throws Exception {
        Intent i = new Intent();
        i.setClassName(ApplicationProvider.getApplicationContext(), "org.robolectric.shadows.DisabledActivity");
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(i, 0);
        assertThat(resolveInfos).isEmpty();
    }

    @Test
    public void queryIntentActivities_MatchDisabledComponents() throws Exception {
        Intent i = new Intent();
        i.setClassName(ApplicationProvider.getApplicationContext(), "org.robolectric.shadows.DisabledActivity");
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(i, MATCH_DISABLED_COMPONENTS);
        assertThat(resolveInfos).isNotNull();
        assertThat(resolveInfos).hasSize(1);
        assertThat(resolveInfos.get(0).activityInfo.enabled).isFalse();
    }

    @Test
    public void queryIntentActivities_DisabledComponentViaPmExplicitIntent() throws Exception {
        Intent i = new Intent();
        i.setClassName(ApplicationProvider.getApplicationContext(), "org.robolectric.shadows.TestActivity");
        ComponentName componentToDisable = new ComponentName(ApplicationProvider.getApplicationContext(), "org.robolectric.shadows.TestActivity");
        packageManager.setComponentEnabledSetting(componentToDisable, COMPONENT_ENABLED_STATE_DISABLED, DONT_KILL_APP);
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(i, 0);
        assertThat(resolveInfos).isEmpty();
    }

    @Test
    public void queryIntentActivities_DisabledComponentEnabledViaPmExplicitIntent() throws Exception {
        Intent i = new Intent();
        i.setClassName(ApplicationProvider.getApplicationContext(), "org.robolectric.shadows.DisabledActivity");
        ComponentName componentToDisable = new ComponentName(ApplicationProvider.getApplicationContext(), "org.robolectric.shadows.DisabledActivity");
        packageManager.setComponentEnabledSetting(componentToDisable, COMPONENT_ENABLED_STATE_ENABLED, DONT_KILL_APP);
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(i, 0);
        assertThat(resolveInfos).hasSize(1);
        assertThat(resolveInfos.get(0).activityInfo.enabled).isFalse();
    }

    @Test
    public void queryIntentActivities_DisabledComponentViaPmImplicitIntent() throws Exception {
        Uri uri = Uri.parse("content://testhost1.com:1/testPath/test.jpeg");
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addCategory(CATEGORY_DEFAULT);
        i.setDataAndType(uri, "image/jpeg");
        ComponentName componentToDisable = new ComponentName(ApplicationProvider.getApplicationContext(), "org.robolectric.shadows.TestActivity");
        packageManager.setComponentEnabledSetting(componentToDisable, COMPONENT_ENABLED_STATE_DISABLED, DONT_KILL_APP);
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(i, 0);
        assertThat(resolveInfos).isEmpty();
    }

    @Test
    public void queryIntentActivities_MatchDisabledViaPmComponents() throws Exception {
        Uri uri = Uri.parse("content://testhost1.com:1/testPath/test.jpeg");
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addCategory(CATEGORY_DEFAULT);
        i.setDataAndType(uri, "image/jpeg");
        ComponentName componentToDisable = new ComponentName(ApplicationProvider.getApplicationContext(), "org.robolectric.shadows.TestActivity");
        packageManager.setComponentEnabledSetting(componentToDisable, COMPONENT_ENABLED_STATE_DISABLED, DONT_KILL_APP);
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(i, MATCH_DISABLED_COMPONENTS);
        assertThat(resolveInfos).isNotNull();
        assertThat(resolveInfos).hasSize(1);
        assertThat(resolveInfos.get(0).activityInfo.enabled).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void queryIntentActivities_appHidden_includeUninstalled() {
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        /* hidden= */
        /* user= */
        packageManager.setApplicationHiddenSettingAsUser(packageName, true, null);
        Intent i = new Intent();
        i.setClassName(ApplicationProvider.getApplicationContext(), "org.robolectric.shadows.TestActivity");
        List<ResolveInfo> activities = packageManager.queryIntentActivities(i, MATCH_UNINSTALLED_PACKAGES);
        assertThat(activities).hasSize(1);
        assertThat(activities.get(0).resolvePackageName).isEqualTo(packageName);
        assertThat(activities.get(0).activityInfo.name).isEqualTo("org.robolectric.shadows.TestActivity");
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void queryIntentActivities_appHidden_dontIncludeUninstalled() {
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        /* hidden= */
        /* user= */
        packageManager.setApplicationHiddenSettingAsUser(packageName, true, null);
        Intent i = new Intent();
        i.setClassName(ApplicationProvider.getApplicationContext(), "org.robolectric.shadows.TestActivity");
        assertThat(/* flags= */
        packageManager.queryIntentActivities(i, 0)).isEmpty();
    }

    @Test
    public void resolveActivity_Match() throws Exception {
        Intent i = new Intent(Intent.ACTION_MAIN, null).addCategory(CATEGORY_LAUNCHER);
        ResolveInfo info = new ResolveInfo();
        info.nonLocalizedLabel = ShadowPackageManagerTest.TEST_PACKAGE_LABEL;
        info.activityInfo = new ActivityInfo();
        info.activityInfo.name = "name";
        info.activityInfo.packageName = ShadowPackageManagerTest.TEST_PACKAGE_NAME;
        shadowPackageManager.addResolveInfoForIntent(i, info);
        assertThat(packageManager.resolveActivity(i, 0)).isNotNull();
        assertThat(packageManager.resolveActivity(i, 0).activityInfo.name).isEqualTo("name");
        assertThat(packageManager.resolveActivity(i, 0).activityInfo.packageName).isEqualTo(ShadowPackageManagerTest.TEST_PACKAGE_NAME);
    }

    @Test
    public void addIntentFilterForComponent() throws Exception {
        ComponentName testComponent = new ComponentName("package", "name");
        IntentFilter intentFilter = new IntentFilter("ACTION");
        intentFilter.addCategory(CATEGORY_DEFAULT);
        intentFilter.addCategory(CATEGORY_APP_CALENDAR);
        shadowPackageManager.addActivityIfNotPresent(testComponent);
        shadowPackageManager.addIntentFilterForActivity(testComponent, intentFilter);
        Intent intent = new Intent();
        intent.setAction("ACTION");
        assertThat(intent.resolveActivity(packageManager)).isEqualTo(testComponent);
        intent.setPackage("package");
        assertThat(intent.resolveActivity(packageManager)).isEqualTo(testComponent);
        intent.addCategory(CATEGORY_APP_CALENDAR);
        assertThat(intent.resolveActivity(packageManager)).isEqualTo(testComponent);
        intent.putExtra("key", "value");
        assertThat(intent.resolveActivity(packageManager)).isEqualTo(testComponent);
        intent.setData(Uri.parse("content://boo"));// data matches only if it is in the filter

        assertThat(intent.resolveActivity(packageManager)).isNull();
        intent.setData(null).setAction("BOO");// different action

        assertThat(intent.resolveActivity(packageManager)).isNull();
    }

    @Test
    public void resolveActivity_NoMatch() throws Exception {
        Intent i = new Intent();
        i.setComponent(new ComponentName("foo.bar", "No Activity"));
        assertThat(packageManager.resolveActivity(i, 0)).isNull();
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void resolveActivityAsUser_Match() throws Exception {
        Intent i = new Intent(Intent.ACTION_MAIN, null).addCategory(CATEGORY_LAUNCHER);
        ResolveInfo info = new ResolveInfo();
        info.nonLocalizedLabel = ShadowPackageManagerTest.TEST_PACKAGE_LABEL;
        info.activityInfo = new ActivityInfo();
        info.activityInfo.name = "name";
        info.activityInfo.packageName = ShadowPackageManagerTest.TEST_PACKAGE_NAME;
        shadowPackageManager.addResolveInfoForIntent(i, info);
        ResolveInfo resolvedActivity = ReflectionHelpers.callInstanceMethod(packageManager, "resolveActivityAsUser", ClassParameter.from(Intent.class, i), ClassParameter.from(int.class, 0), ClassParameter.from(int.class, ShadowPackageManagerTest.USER_ID));
        assertThat(resolvedActivity).isNotNull();
        assertThat(resolvedActivity.activityInfo.name).isEqualTo("name");
        assertThat(resolvedActivity.activityInfo.packageName).isEqualTo(ShadowPackageManagerTest.TEST_PACKAGE_NAME);
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void resolveActivityAsUser_NoMatch() throws Exception {
        Intent i = new Intent();
        i.setComponent(new ComponentName("foo.bar", "No Activity"));
        ResolveInfo resolvedActivity = ReflectionHelpers.callInstanceMethod(packageManager, "resolveActivityAsUser", ClassParameter.from(Intent.class, i), ClassParameter.from(int.class, 0), ClassParameter.from(int.class, ShadowPackageManagerTest.USER_ID));
        assertThat(resolvedActivity).isNull();
    }

    @Test
    public void queryIntentServices_EmptyResult() throws Exception {
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(CATEGORY_LAUNCHER);
        List<ResolveInfo> activities = packageManager.queryIntentServices(i, 0);
        assertThat(activities).isEmpty();
    }

    @Test
    public void queryIntentServices_MatchWithExplicitIntent() throws Exception {
        Intent i = new Intent();
        i.setClassName(ApplicationProvider.getApplicationContext(), "com.foo.Service");
        List<ResolveInfo> services = packageManager.queryIntentServices(i, 0);
        assertThat(services).isNotNull();
        assertThat(services).hasSize(1);
        assertThat(services.get(0).resolvePackageName).isEqualTo("org.robolectric");
        assertThat(services.get(0).serviceInfo.name).isEqualTo("com.foo.Service");
    }

    @Test
    public void queryIntentServices_Match() throws Exception {
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        ResolveInfo info = new ResolveInfo();
        info.serviceInfo = new ServiceInfo();
        info.nonLocalizedLabel = ShadowPackageManagerTest.TEST_PACKAGE_LABEL;
        shadowPackageManager.addResolveInfoForIntent(i, info);
        List<ResolveInfo> services = packageManager.queryIntentServices(i, 0);
        assertThat(services).hasSize(1);
        assertThat(services.get(0).nonLocalizedLabel.toString()).isEqualTo(ShadowPackageManagerTest.TEST_PACKAGE_LABEL);
    }

    @Test
    public void queryIntentServices_fromManifest() {
        Intent i = new Intent("org.robolectric.ACTION_DIFFERENT_PACKAGE");
        i.addCategory(CATEGORY_LAUNCHER);
        i.setType("image/jpeg");
        List<ResolveInfo> services = packageManager.queryIntentServices(i, 0);
        assertThat(services).isNotEmpty();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void queryIntentServices_appHidden_includeUninstalled() {
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        /* hidden= */
        /* user= */
        packageManager.setApplicationHiddenSettingAsUser(packageName, true, null);
        Intent i = new Intent();
        i.setClassName(ApplicationProvider.getApplicationContext(), "com.foo.Service");
        List<ResolveInfo> services = packageManager.queryIntentServices(i, MATCH_UNINSTALLED_PACKAGES);
        assertThat(services).hasSize(1);
        assertThat(services.get(0).resolvePackageName).isEqualTo(packageName);
        assertThat(services.get(0).serviceInfo.name).isEqualTo("com.foo.Service");
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void queryIntentServices_appHidden_dontIncludeUninstalled() {
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        /* hidden= */
        /* user= */
        packageManager.setApplicationHiddenSettingAsUser(packageName, true, null);
        Intent i = new Intent();
        i.setClassName(ApplicationProvider.getApplicationContext(), "com.foo.Service");
        assertThat(/* flags= */
        packageManager.queryIntentServices(i, 0)).isEmpty();
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void queryIntentServicesAsUser() {
        Intent i = new Intent("org.robolectric.ACTION_DIFFERENT_PACKAGE");
        i.addCategory(CATEGORY_LAUNCHER);
        i.setType("image/jpeg");
        List<ResolveInfo> services = packageManager.queryIntentServicesAsUser(i, 0, 0);
        assertThat(services).isNotEmpty();
    }

    @Test
    public void queryBroadcastReceivers_EmptyResult() throws Exception {
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(CATEGORY_LAUNCHER);
        List<ResolveInfo> broadCastReceivers = packageManager.queryBroadcastReceivers(i, 0);
        assertThat(broadCastReceivers).isEmpty();
    }

    @Test
    public void queryBroadcastReceivers_Match() throws Exception {
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        ResolveInfo info = new ResolveInfo();
        info.nonLocalizedLabel = ShadowPackageManagerTest.TEST_PACKAGE_LABEL;
        shadowPackageManager.addResolveInfoForIntent(i, info);
        List<ResolveInfo> broadCastReceivers = packageManager.queryBroadcastReceivers(i, 0);
        assertThat(broadCastReceivers).hasSize(1);
        assertThat(broadCastReceivers.get(0).nonLocalizedLabel.toString()).isEqualTo(ShadowPackageManagerTest.TEST_PACKAGE_LABEL);
    }

    @Test
    public void queryBroadcastReceivers_MatchWithExplicitIntent() throws Exception {
        Intent i = new Intent();
        i.setClassName(ApplicationProvider.getApplicationContext(), "org.robolectric.fakes.ConfigTestReceiver");
        List<ResolveInfo> receivers = packageManager.queryBroadcastReceivers(i, 0);
        assertThat(receivers).isNotNull();
        assertThat(receivers).hasSize(1);
        assertThat(receivers.get(0).resolvePackageName).isEqualTo("org.robolectric");
        assertThat(receivers.get(0).activityInfo.name).isEqualTo("org.robolectric.fakes.ConfigTestReceiver");
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void queryBroadcastReceivers_appHidden_includeUninstalled() {
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        /* hidden= */
        /* user= */
        packageManager.setApplicationHiddenSettingAsUser(packageName, true, null);
        Intent i = new Intent();
        i.setClassName(ApplicationProvider.getApplicationContext(), "org.robolectric.fakes.ConfigTestReceiver");
        List<ResolveInfo> activities = packageManager.queryBroadcastReceivers(i, MATCH_UNINSTALLED_PACKAGES);
        assertThat(activities).hasSize(1);
        assertThat(activities.get(0).resolvePackageName).isEqualTo(packageName);
        assertThat(activities.get(0).activityInfo.name).isEqualTo("org.robolectric.fakes.ConfigTestReceiver");
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void queryBroadcastReceivers_appHidden_dontIncludeUninstalled() {
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        /* hidden= */
        /* user= */
        packageManager.setApplicationHiddenSettingAsUser(packageName, true, null);
        Intent i = new Intent();
        i.setClassName(ApplicationProvider.getApplicationContext(), "org.robolectric.fakes.ConfigTestReceiver");
        assertThat(/* flags= */
        packageManager.queryBroadcastReceivers(i, 0)).isEmpty();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void queryIntentContentProviders_EmptyResult() throws Exception {
        Intent i = new Intent(DocumentsContract.PROVIDER_INTERFACE);
        List<ResolveInfo> broadCastReceivers = packageManager.queryIntentContentProviders(i, 0);
        assertThat(broadCastReceivers).isEmpty();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void queryIntentContentProviders_Match() throws Exception {
        Intent i = new Intent(DocumentsContract.PROVIDER_INTERFACE);
        ResolveInfo resolveInfo = new ResolveInfo();
        ProviderInfo providerInfo = new ProviderInfo();
        providerInfo.authority = "com.robolectric";
        resolveInfo.providerInfo = providerInfo;
        shadowPackageManager.addResolveInfoForIntent(i, resolveInfo);
        List<ResolveInfo> contentProviders = packageManager.queryIntentContentProviders(i, 0);
        assertThat(contentProviders).hasSize(1);
        assertThat(contentProviders.get(0).providerInfo.authority).isEqualTo(providerInfo.authority);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void queryIntentContentProviders_MatchSystemOnly() throws Exception {
        Intent i = new Intent(DocumentsContract.PROVIDER_INTERFACE);
        ResolveInfo info1 = new ResolveInfo();
        info1.providerInfo = new ProviderInfo();
        info1.providerInfo.applicationInfo = new ApplicationInfo();
        ResolveInfo info2 = new ResolveInfo();
        info2.providerInfo = new ProviderInfo();
        info2.providerInfo.applicationInfo = new ApplicationInfo();
        info2.providerInfo.applicationInfo.flags |= ApplicationInfo.FLAG_SYSTEM;
        info2.nonLocalizedLabel = "System App";
        shadowPackageManager.addResolveInfoForIntent(i, info1);
        shadowPackageManager.addResolveInfoForIntent(i, info2);
        List<ResolveInfo> activities = packageManager.queryIntentContentProviders(i, MATCH_SYSTEM_ONLY);
        assertThat(activities).isNotNull();
        assertThat(activities).hasSize(1);
        assertThat(activities.get(0).nonLocalizedLabel.toString()).isEqualTo("System App");
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void queryIntentContentProviders_MatchDisabledComponents() throws Exception {
        Intent i = new Intent(DocumentsContract.PROVIDER_INTERFACE);
        ResolveInfo resolveInfo = new ResolveInfo();
        resolveInfo.providerInfo = new ProviderInfo();
        resolveInfo.providerInfo.applicationInfo = new ApplicationInfo();
        resolveInfo.providerInfo.applicationInfo.packageName = "org.robolectric.shadows.TestPackageName";
        resolveInfo.providerInfo.name = "org.robolectric.shadows.TestProvider";
        resolveInfo.providerInfo.enabled = false;
        shadowPackageManager.addResolveInfoForIntent(i, resolveInfo);
        List<ResolveInfo> resolveInfos = packageManager.queryIntentContentProviders(i, 0);
        assertThat(resolveInfos).isEmpty();
        resolveInfos = packageManager.queryIntentContentProviders(i, MATCH_DISABLED_COMPONENTS);
        assertThat(resolveInfos).isNotNull();
        assertThat(resolveInfos).hasSize(1);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void queryIntentContentProviders_appHidden_includeUninstalled() {
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        /* hidden= */
        /* user= */
        packageManager.setApplicationHiddenSettingAsUser(packageName, true, null);
        Intent i = new Intent(DocumentsContract.PROVIDER_INTERFACE);
        i.setClassName(ApplicationProvider.getApplicationContext(), "org.robolectric.shadows.testing.TestContentProvider1");
        List<ResolveInfo> resolveInfos = packageManager.queryIntentContentProviders(i, 0);
        assertThat(resolveInfos).isEmpty();
        resolveInfos = packageManager.queryIntentContentProviders(i, MATCH_UNINSTALLED_PACKAGES);
        assertThat(resolveInfos).hasSize(1);
        assertThat(resolveInfos.get(0).providerInfo.applicationInfo.packageName).isEqualTo(packageName);
        assertThat(resolveInfos.get(0).providerInfo.name).isEqualTo("org.robolectric.shadows.testing.TestContentProvider1");
    }

    @Test
    public void resolveService_Match() throws Exception {
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        ResolveInfo info = new ResolveInfo();
        info.serviceInfo = new ServiceInfo();
        info.serviceInfo.name = "name";
        shadowPackageManager.addResolveInfoForIntent(i, info);
        assertThat(packageManager.resolveService(i, 0)).isNotNull();
        assertThat(packageManager.resolveService(i, 0).serviceInfo.name).isEqualTo("name");
    }

    @Test
    public void removeResolveInfosForIntent_shouldCauseResolveActivityToReturnNull() throws Exception {
        Intent intent = new Intent(Intent.ACTION_APP_ERROR, null).addCategory(CATEGORY_APP_BROWSER);
        ResolveInfo info = new ResolveInfo();
        info.nonLocalizedLabel = ShadowPackageManagerTest.TEST_PACKAGE_LABEL;
        info.activityInfo = new ActivityInfo();
        info.activityInfo.packageName = "com.org";
        shadowPackageManager.addResolveInfoForIntent(intent, info);
        shadowPackageManager.removeResolveInfosForIntent(intent, "com.org");
        assertThat(packageManager.resolveActivity(intent, 0)).isNull();
    }

    @Test
    public void removeResolveInfosForIntent_forService() throws Exception {
        Intent intent = new Intent(Intent.ACTION_APP_ERROR, null).addCategory(CATEGORY_APP_BROWSER);
        ResolveInfo info = new ResolveInfo();
        info.nonLocalizedLabel = ShadowPackageManagerTest.TEST_PACKAGE_LABEL;
        info.serviceInfo = new ServiceInfo();
        info.serviceInfo.packageName = "com.org";
        shadowPackageManager.addResolveInfoForIntent(intent, info);
        shadowPackageManager.removeResolveInfosForIntent(intent, "com.org");
        assertThat(packageManager.resolveService(intent, 0)).isNull();
    }

    @Test
    public void resolveService_NoMatch() throws Exception {
        Intent i = new Intent();
        i.setComponent(new ComponentName("foo.bar", "No Activity"));
        assertThat(packageManager.resolveService(i, 0)).isNull();
    }

    @Test
    public void queryActivityIcons_Match() throws Exception {
        Intent i = new Intent();
        i.setComponent(new ComponentName(ShadowPackageManagerTest.TEST_PACKAGE_NAME, ""));
        Drawable d = new BitmapDrawable();
        shadowPackageManager.addActivityIcon(i, d);
        assertThat(packageManager.getActivityIcon(i)).isSameAs(d);
        assertThat(packageManager.getActivityIcon(i.getComponent())).isSameAs(d);
    }

    @Test
    public void hasSystemFeature() throws Exception {
        // uninitialized
        assertThat(packageManager.hasSystemFeature(FEATURE_CAMERA)).isFalse();
        // positive
        shadowPackageManager.setSystemFeature(FEATURE_CAMERA, true);
        assertThat(packageManager.hasSystemFeature(FEATURE_CAMERA)).isTrue();
        // negative
        shadowPackageManager.setSystemFeature(FEATURE_CAMERA, false);
        assertThat(packageManager.hasSystemFeature(FEATURE_CAMERA)).isFalse();
    }

    @Test
    public void addSystemSharedLibraryName() {
        shadowPackageManager.addSystemSharedLibraryName("com.foo.system_library_1");
        shadowPackageManager.addSystemSharedLibraryName("com.foo.system_library_2");
        assertThat(packageManager.getSystemSharedLibraryNames()).asList().containsExactly("com.foo.system_library_1", "com.foo.system_library_2");
    }

    @Test
    public void clearSystemSharedLibraryName() {
        shadowPackageManager.addSystemSharedLibraryName("com.foo.system_library_1");
        shadowPackageManager.clearSystemSharedLibraryNames();
        assertThat(packageManager.getSystemSharedLibraryNames()).isEmpty();
    }

    @Test
    public void getPackageInfo_shouldReturnActivityInfos() throws Exception {
        PackageInfo packageInfo = packageManager.getPackageInfo(ApplicationProvider.getApplicationContext().getPackageName(), GET_ACTIVITIES);
        ActivityInfo activityInfoWithFilters = ShadowPackageManagerTest.findActivity(packageInfo.activities, ShadowPackageManagerTest.ActivityWithFilters.class.getName());
        assertThat(activityInfoWithFilters.packageName).isEqualTo("org.robolectric");
        assertThat(activityInfoWithFilters.exported).isEqualTo(true);
        assertThat(activityInfoWithFilters.permission).isEqualTo("com.foo.MY_PERMISSION");
    }

    @Test
    public void getPackageInfo_getProvidersShouldReturnProviderInfos() throws Exception {
        PackageInfo packageInfo = packageManager.getPackageInfo(ApplicationProvider.getApplicationContext().getPackageName(), GET_PROVIDERS);
        ProviderInfo[] providers = packageInfo.providers;
        assertThat(providers).isNotEmpty();
        assertThat(providers.length).isEqualTo(2);
        assertThat(providers[0].packageName).isEqualTo("org.robolectric");
        assertThat(providers[1].packageName).isEqualTo("org.robolectric");
    }

    @Test
    public void getProviderInfo_shouldReturnProviderInfos() throws Exception {
        ProviderInfo providerInfo1 = packageManager.getProviderInfo(new ComponentName(ApplicationProvider.getApplicationContext(), "org.robolectric.shadows.testing.TestContentProvider1"), 0);
        assertThat(providerInfo1.packageName).isEqualTo("org.robolectric");
        assertThat(providerInfo1.authority).isEqualTo("org.robolectric.authority1");
        ProviderInfo providerInfo2 = packageManager.getProviderInfo(new ComponentName(ApplicationProvider.getApplicationContext(), "org.robolectric.shadows.testing.TestContentProvider2"), 0);
        assertThat(providerInfo2.packageName).isEqualTo("org.robolectric");
        assertThat(providerInfo2.authority).isEqualTo("org.robolectric.authority2");
    }

    @Test
    public void getProviderInfo_packageNotFoundShouldThrowException() {
        try {
            packageManager.getProviderInfo(new ComponentName("non.existent.package", ".tester.DoesntExist"), 0);
            Assert.fail("should have thrown NameNotFoundException");
        } catch (NameNotFoundException e) {
            // expected
        }
    }

    @Test
    public void getProviderInfo_shouldPopulatePermissionsInProviderInfos() throws Exception {
        ProviderInfo providerInfo = packageManager.getProviderInfo(new ComponentName(ApplicationProvider.getApplicationContext(), "org.robolectric.shadows.testing.TestContentProvider1"), 0);
        assertThat(providerInfo.authority).isEqualTo("org.robolectric.authority1");
        assertThat(providerInfo.readPermission).isEqualTo("READ_PERMISSION");
        assertThat(providerInfo.writePermission).isEqualTo("WRITE_PERMISSION");
        assertThat(providerInfo.pathPermissions).asList().hasSize(1);
        assertThat(providerInfo.pathPermissions[0].getType()).isEqualTo(PATTERN_SIMPLE_GLOB);
        assertThat(providerInfo.pathPermissions[0].getPath()).isEqualTo("/path/*");
        assertThat(providerInfo.pathPermissions[0].getReadPermission()).isEqualTo("PATH_READ_PERMISSION");
        assertThat(providerInfo.pathPermissions[0].getWritePermission()).isEqualTo("PATH_WRITE_PERMISSION");
    }

    @Test
    public void getProviderInfo_shouldMetaDataInProviderInfos() throws Exception {
        ProviderInfo providerInfo = packageManager.getProviderInfo(new ComponentName(ApplicationProvider.getApplicationContext(), "org.robolectric.shadows.testing.TestContentProvider1"), GET_META_DATA);
        assertThat(providerInfo.authority).isEqualTo("org.robolectric.authority1");
        assertThat(providerInfo.metaData.getString("greeting")).isEqualTo("Hello");
    }

    @Test
    public void resolveContentProvider_shouldResolveByPackageName() throws Exception {
        ProviderInfo providerInfo = packageManager.resolveContentProvider("org.robolectric.authority1", 0);
        assertThat(providerInfo.packageName).isEqualTo("org.robolectric");
        assertThat(providerInfo.authority).isEqualTo("org.robolectric.authority1");
    }

    @Test
    public void testReceiverInfo() throws Exception {
        ActivityInfo info = packageManager.getReceiverInfo(new ComponentName(ApplicationProvider.getApplicationContext(), "org.robolectric.test.ConfigTestReceiver"), GET_META_DATA);
        assertThat(info.metaData.getInt("numberOfSheep")).isEqualTo(42);
    }

    @Test
    public void testGetPackageInfo_ForReceiversIncorrectPackage() {
        try {
            packageManager.getPackageInfo("unknown_package", GET_RECEIVERS);
            Assert.fail("should have thrown NameNotFoundException");
        } catch (PackageManager e) {
            assertThat(e.getMessage()).contains("unknown_package");
        }
    }

    @Test
    public void getPackageInfo_shouldReturnRequestedPermissions() throws Exception {
        PackageInfo packageInfo = packageManager.getPackageInfo(ApplicationProvider.getApplicationContext().getPackageName(), GET_PERMISSIONS);
        String[] permissions = packageInfo.requestedPermissions;
        assertThat(permissions).isNotNull();
        assertThat(permissions.length).isEqualTo(4);
    }

    @Test
    public void getPackageInfo_uninstalledPackage_includeUninstalled() throws Exception {
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        shadowPackageManager.deletePackage(packageName);
        PackageInfo info = packageManager.getPackageInfo(packageName, MATCH_UNINSTALLED_PACKAGES);
        assertThat(info).isNotNull();
        assertThat(info.packageName).isEqualTo(packageName);
    }

    @Test
    public void getPackageInfo_uninstalledPackage_dontIncludeUninstalled() throws Exception {
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        shadowPackageManager.deletePackage(packageName);
        try {
            PackageInfo info = packageManager.getPackageInfo(packageName, 0);
            Assert.fail(("should have thrown NameNotFoundException:" + (info.applicationInfo.flags)));
        } catch (NameNotFoundException e) {
            // expected
        }
    }

    @Test
    public void getPackageInfo_disabledPackage_includeDisabled() throws Exception {
        packageManager.setApplicationEnabledSetting(ApplicationProvider.getApplicationContext().getPackageName(), COMPONENT_ENABLED_STATE_DISABLED, 0);
        PackageInfo info = packageManager.getPackageInfo(ApplicationProvider.getApplicationContext().getPackageName(), MATCH_DISABLED_COMPONENTS);
        assertThat(info).isNotNull();
        assertThat(info.packageName).isEqualTo(ApplicationProvider.getApplicationContext().getPackageName());
    }

    @Test
    public void getInstalledPackages_uninstalledPackage_dontIncludeUninstalled() throws Exception {
        shadowPackageManager.deletePackage(ApplicationProvider.getApplicationContext().getPackageName());
        assertThat(packageManager.getInstalledPackages(0)).isEmpty();
    }

    @Test
    public void getInstalledPackages_disabledPackage_includeDisabled() throws Exception {
        packageManager.setApplicationEnabledSetting(ApplicationProvider.getApplicationContext().getPackageName(), COMPONENT_ENABLED_STATE_DISABLED, 0);
        assertThat(packageManager.getInstalledPackages(MATCH_DISABLED_COMPONENTS)).isNotEmpty();
        assertThat(packageManager.getInstalledPackages(MATCH_DISABLED_COMPONENTS).get(0).packageName).isEqualTo(ApplicationProvider.getApplicationContext().getPackageName());
    }

    @Test
    public void testGetPreferredActivities() throws Exception {
        final String packageName = "com.example.dummy";
        ComponentName name = new ComponentName(packageName, "LauncherActivity");
        // Setup an intentfilter and add to packagemanager
        IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(CATEGORY_HOME);
        packageManager.addPreferredActivity(filter, 0, null, name);
        // Test match
        List<IntentFilter> filters = new ArrayList<>();
        List<ComponentName> activities = new ArrayList<>();
        int filterCount = packageManager.getPreferredActivities(filters, activities, null);
        assertThat(filterCount).isEqualTo(1);
        assertThat(activities.size()).isEqualTo(1);
        assertThat(activities.get(0).getPackageName()).isEqualTo(packageName);
        assertThat(filters.size()).isEqualTo(1);
        filterCount = packageManager.getPreferredActivities(filters, activities, "other");
        assertThat(filterCount).isEqualTo(0);
    }

    @Test
    public void resolveActivity_preferred() {
        ComponentName preferredName = new ComponentName("preferred", "LauncherActivity");
        ComponentName otherName = new ComponentName("other", "LauncherActivity");
        Intent homeIntent = new Intent(Intent.ACTION_MAIN).addCategory(CATEGORY_HOME);
        shadowPackageManager.setResolveInfosForIntent(homeIntent, ImmutableList.of(ShadowResolveInfo.newResolveInfo("label1", otherName.getPackageName(), otherName.getClassName()), ShadowResolveInfo.newResolveInfo("label2", preferredName.getPackageName(), preferredName.getClassName())));
        ResolveInfo resolveInfo = packageManager.resolveActivity(homeIntent, 0);
        assertThat(resolveInfo.activityInfo.packageName).isEqualTo(otherName.getPackageName());
        // Setup an intentfilter and add to packagemanager
        IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(CATEGORY_HOME);
        packageManager.addPreferredActivity(filter, 0, null, preferredName);
        resolveInfo = packageManager.resolveActivity(homeIntent, 0);
        assertThat(resolveInfo.activityInfo.packageName).isEqualTo(preferredName.getPackageName());
    }

    @Test
    public void canResolveDrawableGivenPackageAndResourceId() throws Exception {
        Drawable drawable = ShadowDrawable.createFromStream(new ByteArrayInputStream(new byte[0]), "my_source");
        shadowPackageManager.addDrawableResolution("com.example.foo", 4334, drawable);
        Drawable actual = packageManager.getDrawable("com.example.foo", 4334, null);
        assertThat(actual).isSameAs(drawable);
    }

    @Test
    public void shouldAssignTheApplicationClassNameFromTheManifest() throws Exception {
        ApplicationInfo applicationInfo = packageManager.getApplicationInfo("org.robolectric", 0);
        assertThat(applicationInfo.className).isEqualTo("org.robolectric.shadows.testing.TestApplication");
    }

    @Test
    @Config(minSdk = VERSION_CODES.N_MR1)
    public void shouldAssignTheApplicationNameFromTheManifest() throws Exception {
        ApplicationInfo applicationInfo = packageManager.getApplicationInfo("org.robolectric", 0);
        assertThat(applicationInfo.name).isEqualTo("org.robolectric.shadows.testing.TestApplication");
    }

    @Test
    public void testLaunchIntentForPackage() {
        Intent intent = packageManager.getLaunchIntentForPackage(ShadowPackageManagerTest.TEST_PACKAGE_LABEL);
        assertThat(intent).isNull();
        Intent launchIntent = new Intent(Intent.ACTION_MAIN);
        launchIntent.setPackage(ShadowPackageManagerTest.TEST_PACKAGE_LABEL);
        launchIntent.addCategory(CATEGORY_LAUNCHER);
        ResolveInfo resolveInfo = new ResolveInfo();
        resolveInfo.activityInfo = new ActivityInfo();
        resolveInfo.activityInfo.packageName = ShadowPackageManagerTest.TEST_PACKAGE_LABEL;
        resolveInfo.activityInfo.name = "LauncherActivity";
        shadowPackageManager.addResolveInfoForIntent(launchIntent, resolveInfo);
        intent = packageManager.getLaunchIntentForPackage(ShadowPackageManagerTest.TEST_PACKAGE_LABEL);
        assertThat(intent.getComponent().getClassName()).isEqualTo("LauncherActivity");
    }

    @Test
    public void shouldAssignTheAppMetaDataFromTheManifest() throws Exception {
        ApplicationInfo info = packageManager.getApplicationInfo(ApplicationProvider.getApplicationContext().getPackageName(), 0);
        Bundle meta = info.metaData;
        assertThat(meta.getString("org.robolectric.metaName1")).isEqualTo("metaValue1");
        assertThat(meta.getString("org.robolectric.metaName2")).isEqualTo("metaValue2");
        assertThat(meta.getBoolean("org.robolectric.metaFalseLiteral")).isEqualTo(false);
        assertThat(meta.getBoolean("org.robolectric.metaTrueLiteral")).isEqualTo(true);
        assertThat(meta.getInt("org.robolectric.metaInt")).isEqualTo(123);
        assertThat(meta.getFloat("org.robolectric.metaFloat")).isEqualTo(1.23F);
        assertThat(meta.getInt("org.robolectric.metaColor")).isEqualTo(WHITE);
        assertThat(meta.getBoolean("org.robolectric.metaBooleanFromRes")).isEqualTo(ApplicationProvider.getApplicationContext().getResources().getBoolean(false_bool_value));
        assertThat(meta.getInt("org.robolectric.metaIntFromRes")).isEqualTo(ApplicationProvider.getApplicationContext().getResources().getInteger(test_integer1));
        assertThat(meta.getInt("org.robolectric.metaColorFromRes")).isEqualTo(ApplicationProvider.getApplicationContext().getResources().getColor(clear));
        assertThat(meta.getString("org.robolectric.metaStringFromRes")).isEqualTo(ApplicationProvider.getApplicationContext().getString(app_name));
        assertThat(meta.getString("org.robolectric.metaStringOfIntFromRes")).isEqualTo(ApplicationProvider.getApplicationContext().getString(str_int));
        assertThat(meta.getInt("org.robolectric.metaStringRes")).isEqualTo(app_name);
    }

    @Test
    public void testResolveDifferentIntentObjects() {
        Intent intent1 = new Intent(Intent.ACTION_MAIN);
        intent1.setPackage(ShadowPackageManagerTest.TEST_PACKAGE_LABEL);
        intent1.addCategory(CATEGORY_APP_BROWSER);
        assertThat(packageManager.resolveActivity(intent1, 0)).isNull();
        ResolveInfo resolveInfo = new ResolveInfo();
        resolveInfo.activityInfo = new ActivityInfo();
        resolveInfo.activityInfo.packageName = ShadowPackageManagerTest.TEST_PACKAGE_LABEL;
        resolveInfo.activityInfo.name = "BrowserActivity";
        shadowPackageManager.addResolveInfoForIntent(intent1, resolveInfo);
        // the original intent object should yield a result
        ResolveInfo result = packageManager.resolveActivity(intent1, 0);
        assertThat(result.activityInfo.name).isEqualTo("BrowserActivity");
        // AND a new, functionally equivalent intent should also yield a result
        Intent intent2 = new Intent(Intent.ACTION_MAIN);
        intent2.setPackage(ShadowPackageManagerTest.TEST_PACKAGE_LABEL);
        intent2.addCategory(CATEGORY_APP_BROWSER);
        result = packageManager.resolveActivity(intent2, 0);
        assertThat(result.activityInfo.name).isEqualTo("BrowserActivity");
    }

    @Test
    public void testResolvePartiallySimilarIntents() {
        Intent intent1 = new Intent(Intent.ACTION_APP_ERROR);
        intent1.setPackage(ShadowPackageManagerTest.TEST_PACKAGE_LABEL);
        intent1.addCategory(CATEGORY_APP_BROWSER);
        assertThat(packageManager.resolveActivity(intent1, 0)).isNull();
        ResolveInfo resolveInfo = new ResolveInfo();
        resolveInfo.activityInfo = new ActivityInfo();
        resolveInfo.activityInfo.packageName = ShadowPackageManagerTest.TEST_PACKAGE_LABEL;
        resolveInfo.activityInfo.name = "BrowserActivity";
        shadowPackageManager.addResolveInfoForIntent(intent1, resolveInfo);
        // the original intent object should yield a result
        ResolveInfo result = packageManager.resolveActivity(intent1, 0);
        assertThat(result.activityInfo.name).isEqualTo("BrowserActivity");
        // an intent with just the same action should not be considered the same
        Intent intent2 = new Intent(Intent.ACTION_APP_ERROR);
        result = packageManager.resolveActivity(intent2, 0);
        assertThat(result).isNull();
        // an intent with just the same category should not be considered the same
        Intent intent3 = new Intent();
        intent3.addCategory(CATEGORY_APP_BROWSER);
        result = packageManager.resolveActivity(intent3, 0);
        assertThat(result).isNull();
        // an intent without the correct package restriction should not be the same
        Intent intent4 = new Intent(Intent.ACTION_APP_ERROR);
        intent4.addCategory(CATEGORY_APP_BROWSER);
        result = packageManager.resolveActivity(intent4, 0);
        assertThat(result).isNull();
    }

    @Test
    public void testSetApplicationEnabledSetting() {
        assertThat(packageManager.getApplicationEnabledSetting("org.robolectric")).isEqualTo(COMPONENT_ENABLED_STATE_DEFAULT);
        packageManager.setApplicationEnabledSetting("org.robolectric", COMPONENT_ENABLED_STATE_DISABLED, 0);
        assertThat(packageManager.getApplicationEnabledSetting("org.robolectric")).isEqualTo(COMPONENT_ENABLED_STATE_DISABLED);
    }

    private static class ActivityWithMetadata extends Activity {}

    @Test
    public void getActivityMetaData() throws Exception {
        Activity activity = Robolectric.setupActivity(ShadowPackageManagerTest.ActivityWithMetadata.class);
        ActivityInfo activityInfo = packageManager.getActivityInfo(activity.getComponentName(), ((PackageManager.GET_ACTIVITIES) | (PackageManager.GET_META_DATA)));
        assertThat(activityInfo.metaData.get("someName")).isEqualTo("someValue");
    }

    @Test
    public void shouldAssignLabelResFromTheManifest() throws Exception {
        ApplicationInfo applicationInfo = packageManager.getApplicationInfo("org.robolectric", 0);
        assertThat(applicationInfo.labelRes).isEqualTo(app_name);
        assertThat(applicationInfo.nonLocalizedLabel).isNull();
    }

    @Test
    public void getServiceInfo_shouldReturnServiceInfoIfExists() throws Exception {
        ServiceInfo serviceInfo = packageManager.getServiceInfo(new ComponentName("org.robolectric", "com.foo.Service"), 0);
        assertThat(serviceInfo.packageName).isEqualTo("org.robolectric");
        assertThat(serviceInfo.name).isEqualTo("com.foo.Service");
        assertThat(serviceInfo.permission).isEqualTo("com.foo.MY_PERMISSION");
        assertThat(serviceInfo.applicationInfo).isNotNull();
    }

    @Test
    public void getServiceInfo_shouldReturnServiceInfoWithMetaDataWhenFlagsSet() throws Exception {
        ServiceInfo serviceInfo = packageManager.getServiceInfo(new ComponentName("org.robolectric", "com.foo.Service"), GET_META_DATA);
        assertThat(serviceInfo.metaData).isNotNull();
    }

    @Test
    public void getServiceInfo_shouldReturnServiceInfoWithoutMetaDataWhenFlagsNotSet() throws Exception {
        ComponentName component = new ComponentName("org.robolectric", "com.foo.Service");
        ServiceInfo serviceInfo = packageManager.getServiceInfo(component, 0);
        assertThat(serviceInfo.metaData).isNull();
    }

    @Test
    public void getServiceInfo_shouldThrowNameNotFoundExceptionIfNotExist() {
        ComponentName nonExistComponent = new ComponentName("org.robolectric", "com.foo.NonExistService");
        try {
            packageManager.getServiceInfo(nonExistComponent, GET_SERVICES);
            Assert.fail("should have thrown NameNotFoundException");
        } catch (PackageManager e) {
            assertThat(e.getMessage()).contains("com.foo.NonExistService");
        }
    }

    @Test
    public void getServiceInfo_shouldFindServiceIfAddedInResolveInfo() throws Exception {
        ComponentName componentName = new ComponentName("com.test", "com.test.ServiceName");
        ResolveInfo resolveInfo = new ResolveInfo();
        resolveInfo.serviceInfo = new ServiceInfo();
        resolveInfo.serviceInfo.name = componentName.getClassName();
        resolveInfo.serviceInfo.applicationInfo = new ApplicationInfo();
        resolveInfo.serviceInfo.applicationInfo.packageName = componentName.getPackageName();
        shadowPackageManager.addResolveInfoForIntent(new Intent("RANDOM_ACTION"), resolveInfo);
        ServiceInfo serviceInfo = packageManager.getServiceInfo(componentName, 0);
        assertThat(serviceInfo).isNotNull();
    }

    @Test
    public void getNameForUid() {
        assertThat(packageManager.getNameForUid(10)).isNull();
        shadowPackageManager.setNameForUid(10, "a_name");
        assertThat(packageManager.getNameForUid(10)).isEqualTo("a_name");
    }

    @Test
    public void getPackagesForUid() {
        assertThat(packageManager.getPackagesForUid(10)).isNull();
        shadowPackageManager.setPackagesForUid(10, new String[]{ "a_name" });
        assertThat(packageManager.getPackagesForUid(10)).asList().containsExactly("a_name");
    }

    @Test
    @Config(minSdk = VERSION_CODES.N)
    public void getPackageUid() throws NameNotFoundException {
        shadowPackageManager.setPackagesForUid(10, new String[]{ "a_name" });
        assertThat(packageManager.getPackageUid("a_name", 0)).isEqualTo(10);
    }

    @Test
    @Config(minSdk = VERSION_CODES.N)
    public void getPackageUid_shouldThrowNameNotFoundExceptionIfNotExist() {
        try {
            packageManager.getPackageUid("a_name", 0);
            Assert.fail("should have thrown NameNotFoundException");
        } catch (PackageManager e) {
            assertThat(e.getMessage()).contains("a_name");
        }
    }

    @Test
    public void getPackagesForUid_shouldReturnSetPackageName() {
        shadowPackageManager.setPackagesForUid(10, new String[]{ "a_name" });
        assertThat(packageManager.getPackagesForUid(10)).asList().containsExactly("a_name");
    }

    @Test
    public void getResourcesForApplication_currentApplication() throws Exception {
        assertThat(packageManager.getResourcesForApplication("org.robolectric").getString(app_name)).isEqualTo(ApplicationProvider.getApplicationContext().getString(app_name));
    }

    @Test
    public void getResourcesForApplication_unknownPackage() {
        try {
            packageManager.getResourcesForApplication("non.existent.package");
            Assert.fail("should have thrown NameNotFoundException");
        } catch (NameNotFoundException e) {
            // expected
        }
    }

    @Test
    public void getResourcesForApplication_anotherPackage() throws Exception {
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = "another.package";
        ApplicationInfo applicationInfo = new ApplicationInfo();
        applicationInfo.packageName = "another.package";
        packageInfo.applicationInfo = applicationInfo;
        shadowPackageManager.installPackage(packageInfo);
        assertThat(packageManager.getResourcesForApplication("another.package")).isNotNull();
        assertThat(packageManager.getResourcesForApplication("another.package")).isNotEqualTo(ApplicationProvider.getApplicationContext().getResources());
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void shouldShowRequestPermissionRationale() {
        assertThat(packageManager.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)).isFalse();
        shadowPackageManager.setShouldShowRequestPermissionRationale(Manifest.permission.CAMERA, true);
        assertThat(packageManager.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)).isTrue();
    }

    @Test
    public void getSystemAvailableFeatures() {
        assertThat(packageManager.getSystemAvailableFeatures()).isNull();
        FeatureInfo feature = new FeatureInfo();
        feature.reqGlEsVersion = 131072;
        feature.flags = FeatureInfo.FLAG_REQUIRED;
        shadowPackageManager.addSystemAvailableFeature(feature);
        assertThat(packageManager.getSystemAvailableFeatures()).asList().contains(feature);
        shadowPackageManager.clearSystemAvailableFeatures();
        assertThat(packageManager.getSystemAvailableFeatures()).isNull();
    }

    @Test
    public void verifyPendingInstall() {
        packageManager.verifyPendingInstall(1234, VERIFICATION_ALLOW);
        assertThat(shadowPackageManager.getVerificationResult(1234)).isEqualTo(VERIFICATION_ALLOW);
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void extendPendingInstallTimeout() {
        packageManager.extendVerificationTimeout(1234, 0, 1000);
        assertThat(shadowPackageManager.getVerificationExtendedTimeout(1234)).isEqualTo(1000);
    }

    // Functionality removed in O
    @Test
    @Config(minSdk = VERSION_CODES.N, maxSdk = VERSION_CODES.N_MR1)
    public void whenPackageNotPresent_getPackageSizeInfo_callsBackWithFailure() throws Exception {
        IPackageStatsObserver packageStatsObserver = Mockito.mock(IPackageStatsObserver.class);
        packageManager.getPackageSizeInfo("nonexistant.package", packageStatsObserver);
        Mockito.verify(packageStatsObserver).onGetStatsCompleted(packageStatsCaptor.capture(), ArgumentMatchers.eq(false));
        assertThat(packageStatsCaptor.getValue()).isNull();
    }

    // Functionality removed in O
    @Test
    @Config(minSdk = VERSION_CODES.N, maxSdk = VERSION_CODES.N_MR1)
    public void whenPackageNotPresentAndPaused_getPackageSizeInfo_callsBackWithFailure() throws Exception {
        Robolectric.getForegroundThreadScheduler().pause();
        IPackageStatsObserver packageStatsObserver = Mockito.mock(IPackageStatsObserver.class);
        packageManager.getPackageSizeInfo("nonexistant.package", packageStatsObserver);
        Mockito.verifyZeroInteractions(packageStatsObserver);
        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();
        Mockito.verify(packageStatsObserver).onGetStatsCompleted(packageStatsCaptor.capture(), ArgumentMatchers.eq(false));
        assertThat(packageStatsCaptor.getValue()).isNull();
    }

    // Functionality removed in O
    @Test
    @Config(minSdk = VERSION_CODES.N, maxSdk = VERSION_CODES.N_MR1)
    public void whenNotPreconfigured_getPackageSizeInfo_callsBackWithDefaults() throws Exception {
        IPackageStatsObserver packageStatsObserver = Mockito.mock(IPackageStatsObserver.class);
        packageManager.getPackageSizeInfo("org.robolectric", packageStatsObserver);
        Mockito.verify(packageStatsObserver).onGetStatsCompleted(packageStatsCaptor.capture(), ArgumentMatchers.eq(true));
        assertThat(packageStatsCaptor.getValue().packageName).isEqualTo("org.robolectric");
    }

    // Functionality removed in O
    @Test
    @Config(minSdk = VERSION_CODES.N, maxSdk = VERSION_CODES.N_MR1)
    public void whenPreconfigured_getPackageSizeInfo_callsBackWithConfiguredValues() throws Exception {
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = "org.robolectric";
        PackageStats packageStats = new PackageStats("org.robolectric");
        shadowPackageManager.addPackage(packageInfo, packageStats);
        IPackageStatsObserver packageStatsObserver = Mockito.mock(IPackageStatsObserver.class);
        packageManager.getPackageSizeInfo("org.robolectric", packageStatsObserver);
        Mockito.verify(packageStatsObserver).onGetStatsCompleted(packageStatsCaptor.capture(), ArgumentMatchers.eq(true));
        assertThat(packageStatsCaptor.getValue().packageName).isEqualTo("org.robolectric");
        assertThat(packageStatsCaptor.getValue().toString()).isEqualTo(packageStats.toString());
    }

    // Functionality removed in O
    @Test
    @Config(minSdk = VERSION_CODES.N, maxSdk = VERSION_CODES.N_MR1)
    public void whenPreconfiguredForAnotherPackage_getPackageSizeInfo_callsBackWithConfiguredValues() throws Exception {
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = "org.other";
        PackageStats packageStats = new PackageStats("org.other");
        shadowPackageManager.addPackage(packageInfo, packageStats);
        IPackageStatsObserver packageStatsObserver = Mockito.mock(IPackageStatsObserver.class);
        packageManager.getPackageSizeInfo("org.other", packageStatsObserver);
        Mockito.verify(packageStatsObserver).onGetStatsCompleted(packageStatsCaptor.capture(), ArgumentMatchers.eq(true));
        assertThat(packageStatsCaptor.getValue().packageName).isEqualTo("org.other");
        assertThat(packageStatsCaptor.getValue().toString()).isEqualTo(packageStats.toString());
    }

    // Functionality removed in O
    @Test
    @Config(minSdk = VERSION_CODES.N, maxSdk = VERSION_CODES.N_MR1)
    public void whenPaused_getPackageSizeInfo_callsBackWithConfiguredValuesAfterIdle() throws Exception {
        Robolectric.getForegroundThreadScheduler().pause();
        IPackageStatsObserver packageStatsObserver = Mockito.mock(IPackageStatsObserver.class);
        packageManager.getPackageSizeInfo("org.robolectric", packageStatsObserver);
        Mockito.verifyZeroInteractions(packageStatsObserver);
        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();
        Mockito.verify(packageStatsObserver).onGetStatsCompleted(packageStatsCaptor.capture(), ArgumentMatchers.eq(true));
        assertThat(packageStatsCaptor.getValue().packageName).isEqualTo("org.robolectric");
    }

    @Test
    public void currentToCanonicalPackageNames() {
        shadowPackageManager.addCurrentToCannonicalName("current_name_1", "cannonical_name_1");
        shadowPackageManager.addCurrentToCannonicalName("current_name_2", "cannonical_name_2");
        packageManager.currentToCanonicalPackageNames(new String[]{ "current_name_1", "current_name_2" });
    }

    @Test
    public void getInstalledApplications() {
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(0);
        // Default should include the application under test
        assertThat(installedApplications).hasSize(1);
        assertThat(installedApplications.get(0).packageName).isEqualTo("org.robolectric");
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = "org.other";
        packageInfo.applicationInfo = new ApplicationInfo();
        packageInfo.applicationInfo.packageName = "org.other";
        shadowPackageManager.installPackage(packageInfo);
        installedApplications = packageManager.getInstalledApplications(0);
        assertThat(installedApplications).hasSize(2);
        assertThat(installedApplications.get(1).packageName).isEqualTo("org.other");
    }

    @Test
    public void getPermissionInfo() throws Exception {
        PermissionInfo permission = ApplicationProvider.getApplicationContext().getPackageManager().getPermissionInfo("org.robolectric.some_permission", 0);
        assertThat(permission.labelRes).isEqualTo(test_permission_label);
        assertThat(permission.descriptionRes).isEqualTo(test_permission_description);
        assertThat(permission.name).isEqualTo("org.robolectric.some_permission");
    }

    @Test
    public void checkSignatures_same() throws Exception {
        shadowPackageManager.installPackage(ShadowPackageManagerTest.newPackageInfo("first.package", new Signature("00000000")));
        shadowPackageManager.installPackage(ShadowPackageManagerTest.newPackageInfo("second.package", new Signature("00000000")));
        assertThat(packageManager.checkSignatures("first.package", "second.package")).isEqualTo(SIGNATURE_MATCH);
    }

    @Test
    public void checkSignatures_firstNotSigned() throws Exception {
        shadowPackageManager.installPackage(ShadowPackageManagerTest.newPackageInfo("first.package", ((Signature[]) (null))));
        shadowPackageManager.installPackage(ShadowPackageManagerTest.newPackageInfo("second.package", new Signature("00000000")));
        assertThat(packageManager.checkSignatures("first.package", "second.package")).isEqualTo(SIGNATURE_FIRST_NOT_SIGNED);
    }

    @Test
    public void checkSignatures_secondNotSigned() throws Exception {
        shadowPackageManager.installPackage(ShadowPackageManagerTest.newPackageInfo("first.package", new Signature("00000000")));
        shadowPackageManager.installPackage(ShadowPackageManagerTest.newPackageInfo("second.package", ((Signature[]) (null))));
        assertThat(packageManager.checkSignatures("first.package", "second.package")).isEqualTo(SIGNATURE_SECOND_NOT_SIGNED);
    }

    @Test
    public void checkSignatures_neitherSigned() throws Exception {
        shadowPackageManager.installPackage(ShadowPackageManagerTest.newPackageInfo("first.package", ((Signature[]) (null))));
        shadowPackageManager.installPackage(ShadowPackageManagerTest.newPackageInfo("second.package", ((Signature[]) (null))));
        assertThat(packageManager.checkSignatures("first.package", "second.package")).isEqualTo(SIGNATURE_NEITHER_SIGNED);
    }

    @Test
    public void checkSignatures_noMatch() throws Exception {
        shadowPackageManager.installPackage(ShadowPackageManagerTest.newPackageInfo("first.package", new Signature("00000000")));
        shadowPackageManager.installPackage(ShadowPackageManagerTest.newPackageInfo("second.package", new Signature("FFFFFFFF")));
        assertThat(packageManager.checkSignatures("first.package", "second.package")).isEqualTo(SIGNATURE_NO_MATCH);
    }

    @Test
    public void checkSignatures_noMatch_mustBeExact() throws Exception {
        shadowPackageManager.installPackage(ShadowPackageManagerTest.newPackageInfo("first.package", new Signature("00000000")));
        shadowPackageManager.installPackage(ShadowPackageManagerTest.newPackageInfo("second.package", new Signature("00000000"), new Signature("FFFFFFFF")));
        assertThat(packageManager.checkSignatures("first.package", "second.package")).isEqualTo(SIGNATURE_NO_MATCH);
    }

    @Test
    public void checkSignatures_unknownPackage() throws Exception {
        assertThat(packageManager.checkSignatures("first.package", "second.package")).isEqualTo(SIGNATURE_UNKNOWN_PACKAGE);
    }

    @Test
    public void getPermissionInfo_notFound() {
        try {
            packageManager.getPermissionInfo("non_existant_permission", 0);
            Assert.fail("should have thrown NameNotFoundException");
        } catch (NameNotFoundException e) {
            // expected
        }
    }

    @Test
    public void getPermissionInfo_noMetaData() throws Exception {
        PermissionInfo permission = packageManager.getPermissionInfo("org.robolectric.some_permission", 0);
        assertThat(permission.metaData).isNull();
        assertThat(permission.name).isEqualTo("org.robolectric.some_permission");
        assertThat(permission.descriptionRes).isEqualTo(test_permission_description);
        assertThat(permission.labelRes).isEqualTo(test_permission_label);
        assertThat(permission.nonLocalizedLabel).isNull();
        assertThat(permission.group).isEqualTo("my_permission_group");
        assertThat(permission.protectionLevel).isEqualTo(PROTECTION_DANGEROUS);
    }

    @Test
    public void getPermissionInfo_withMetaData() throws Exception {
        PermissionInfo permission = packageManager.getPermissionInfo("org.robolectric.some_permission", GET_META_DATA);
        assertThat(permission.metaData).isNotNull();
        assertThat(permission.metaData.getString("meta_data_name")).isEqualTo("meta_data_value");
    }

    @Test
    public void getPermissionInfo_withLiteralLabel() throws Exception {
        PermissionInfo permission = packageManager.getPermissionInfo("org.robolectric.permission_with_literal_label", 0);
        assertThat(permission.labelRes).isEqualTo(0);
        assertThat(permission.nonLocalizedLabel).isEqualTo("Literal label");
        assertThat(permission.protectionLevel).isEqualTo(PROTECTION_NORMAL);
    }

    @Test
    public void queryPermissionsByGroup_groupNotFound() throws Exception {
        try {
            packageManager.queryPermissionsByGroup("nonexistent_permission_group", 0);
            Assert.fail("Exception expected");
        } catch (NameNotFoundException expected) {
        }
    }

    @Test
    public void queryPermissionsByGroup_noMetaData() throws Exception {
        List<PermissionInfo> permissions = packageManager.queryPermissionsByGroup("my_permission_group", 0);
        assertThat(permissions).hasSize(1);
        PermissionInfo permission = permissions.get(0);
        assertThat(permission.group).isEqualTo("my_permission_group");
        assertThat(permission.name).isEqualTo("org.robolectric.some_permission");
        assertThat(permission.metaData).isNull();
    }

    @Test
    public void queryPermissionsByGroup_withMetaData() throws Exception {
        List<PermissionInfo> permissions = packageManager.queryPermissionsByGroup("my_permission_group", GET_META_DATA);
        assertThat(permissions).hasSize(1);
        PermissionInfo permission = permissions.get(0);
        assertThat(permission.group).isEqualTo("my_permission_group");
        assertThat(permission.name).isEqualTo("org.robolectric.some_permission");
        assertThat(permission.metaData).isNotNull();
        assertThat(permission.metaData.getString("meta_data_name")).isEqualTo("meta_data_value");
    }

    @Test
    public void queryPermissionsByGroup_nullMatchesPermissionsNotAssociatedWithGroup() throws Exception {
        List<PermissionInfo> permissions = packageManager.queryPermissionsByGroup(null, 0);
        assertThat(Iterables.transform(permissions, ShadowPackageManagerTest.getPermissionNames())).containsExactly("org.robolectric.permission_with_minimal_fields", "org.robolectric.permission_with_literal_label");
    }

    @Test
    public void queryPermissionsByGroup_nullMatchesPermissionsNotAssociatedWithGroup_with_addPermissionInfo() throws Exception {
        PermissionInfo permissionInfo = new PermissionInfo();
        permissionInfo.name = "some_name";
        shadowPackageManager.addPermissionInfo(permissionInfo);
        List<PermissionInfo> permissions = packageManager.queryPermissionsByGroup(null, 0);
        assertThat(permissions).isNotEmpty();
        assertThat(permissions.get(0).name).isEqualTo(permissionInfo.name);
    }

    @Test
    public void queryPermissionsByGroup_with_addPermissionInfo() throws Exception {
        PermissionInfo permissionInfo = new PermissionInfo();
        permissionInfo.name = "some_name";
        permissionInfo.group = "some_group";
        shadowPackageManager.addPermissionInfo(permissionInfo);
        List<PermissionInfo> permissions = packageManager.queryPermissionsByGroup(permissionInfo.group, 0);
        assertThat(permissions).hasSize(1);
        assertThat(permissions.get(0).name).isEqualTo(permissionInfo.name);
        assertThat(permissions.get(0).group).isEqualTo(permissionInfo.group);
    }

    @Test
    public void getDefaultActivityIcon() {
        assertThat(packageManager.getDefaultActivityIcon()).isNotNull();
    }

    @Test
    public void addPackageShouldUseUidToProvidePackageName() throws Exception {
        PackageInfo packageInfoOne = new PackageInfo();
        packageInfoOne.packageName = "package.one";
        packageInfoOne.applicationInfo = new ApplicationInfo();
        packageInfoOne.applicationInfo.uid = 1234;
        packageInfoOne.applicationInfo.packageName = packageInfoOne.packageName;
        shadowPackageManager.installPackage(packageInfoOne);
        PackageInfo packageInfoTwo = new PackageInfo();
        packageInfoTwo.packageName = "package.two";
        packageInfoTwo.applicationInfo = new ApplicationInfo();
        packageInfoTwo.applicationInfo.uid = 1234;
        packageInfoTwo.applicationInfo.packageName = packageInfoTwo.packageName;
        shadowPackageManager.installPackage(packageInfoTwo);
        assertThat(packageManager.getPackagesForUid(1234)).asList().containsExactly("package.one", "package.two");
    }

    @Test
    public void installerPackageName() throws Exception {
        packageManager.setInstallerPackageName("target.package", "installer.package");
        assertThat(packageManager.getInstallerPackageName("target.package")).isEqualTo("installer.package");
    }

    @Test
    public void getXml() throws Exception {
        XmlResourceParser in = packageManager.getXml(ApplicationProvider.getApplicationContext().getPackageName(), dialog_preferences, ApplicationProvider.getApplicationContext().getApplicationInfo());
        assertThat(in).isNotNull();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void addPackageShouldNotCreateSessions() {
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = "test.package";
        shadowPackageManager.installPackage(packageInfo);
        assertThat(packageManager.getPackageInstaller().getAllSessions()).isEmpty();
    }

    @Test
    public void installPackage_defaults() {
        PackageInfo info = new PackageInfo();
        info.packageName = "name";
        info.activities = new ActivityInfo[]{ new ActivityInfo() };
        shadowPackageManager.installPackage(info);
        PackageInfo installed = shadowPackageManager.getInternalMutablePackageInfo("name");
        ActivityInfo activity = installed.activities[0];
        assertThat(installed.applicationInfo).isNotNull();
        assertThat(installed.applicationInfo.packageName).isEqualTo("name");
        assertThat((((installed.applicationInfo.flags) & (ApplicationInfo.FLAG_INSTALLED)) != 0)).named("%s is installed", installed.applicationInfo).isTrue();
        assertThat(activity.packageName).isEqualTo("name");
        // this should be really equal in parcel sense as ApplicationInfo doesn't implement equals().
        assertThat(activity.applicationInfo).isEqualTo(installed.applicationInfo);
        assertThat(installed.applicationInfo.processName).isEqualTo("name");
        assertThat(activity.name).isNotEmpty();
    }

    @Test
    public void addPackageMultipleTimesShouldWork() throws Exception {
        shadowPackageManager.addPackage("test.package");
        // Shouldn't throw exception
        shadowPackageManager.addPackage("test.package");
    }

    @Test
    public void addPackageSetsStorage() throws Exception {
        shadowPackageManager.addPackage("test.package");
        PackageInfo packageInfo = packageManager.getPackageInfo("test.package", 0);
        assertThat(packageInfo.applicationInfo.sourceDir).isNotNull();
        assertThat(new File(packageInfo.applicationInfo.sourceDir).exists()).isTrue();
        assertThat(packageInfo.applicationInfo.publicSourceDir).isEqualTo(packageInfo.applicationInfo.sourceDir);
    }

    @Test
    public void addComponent_noData() {
        try {
            shadowPackageManager.addOrUpdateActivity(new ActivityInfo());
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // should throw
        }
    }

    @Test
    public void addActivity() throws Exception {
        ActivityInfo activityInfo = new ActivityInfo();
        activityInfo.name = "name";
        activityInfo.packageName = "package";
        shadowPackageManager.addOrUpdateActivity(activityInfo);
        assertThat(packageManager.getActivityInfo(new ComponentName("package", "name"), 0)).isNotNull();
    }

    @Test
    public void addService() throws Exception {
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.name = "name";
        serviceInfo.packageName = "package";
        shadowPackageManager.addOrUpdateService(serviceInfo);
        assertThat(packageManager.getServiceInfo(new ComponentName("package", "name"), 0)).isNotNull();
    }

    @Test
    public void addProvider() throws Exception {
        ProviderInfo providerInfo = new ProviderInfo();
        providerInfo.name = "name";
        providerInfo.packageName = "package";
        shadowPackageManager.addOrUpdateProvider(providerInfo);
        assertThat(packageManager.getProviderInfo(new ComponentName("package", "name"), 0)).isNotNull();
    }

    @Test
    public void addReceiver() throws Exception {
        ActivityInfo receiverInfo = new ActivityInfo();
        receiverInfo.name = "name";
        receiverInfo.packageName = "package";
        shadowPackageManager.addOrUpdateReceiver(receiverInfo);
        assertThat(packageManager.getReceiverInfo(new ComponentName("package", "name"), 0)).isNotNull();
    }

    @Test
    public void addActivity_addsNewPackage() throws Exception {
        ActivityInfo activityInfo = new ActivityInfo();
        activityInfo.name = "name";
        activityInfo.packageName = "package";
        shadowPackageManager.addOrUpdateActivity(activityInfo);
        PackageInfo packageInfo = packageManager.getPackageInfo("package", GET_ACTIVITIES);
        assertThat(packageInfo.packageName).isEqualTo("package");
        assertThat(packageInfo.applicationInfo.packageName).isEqualTo("package");
        assertThat((((packageInfo.applicationInfo.flags) & (ApplicationInfo.FLAG_INSTALLED)) != 0)).named("applicationInfo is installed").isTrue();
        assertThat(packageInfo.activities).hasLength(1);
        ActivityInfo addedInfo = packageInfo.activities[0];
        assertThat(addedInfo.name).isEqualTo("name");
        assertThat(addedInfo.applicationInfo).isNotNull();
        assertThat(addedInfo.applicationInfo.packageName).isEqualTo("package");
    }

    @Test
    public void addActivity_usesExistingPackage() throws Exception {
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        int originalActivitiesCount = packageManager.getPackageInfo(packageName, GET_ACTIVITIES).activities.length;
        ActivityInfo activityInfo = new ActivityInfo();
        activityInfo.name = "name";
        activityInfo.packageName = packageName;
        shadowPackageManager.addOrUpdateActivity(activityInfo);
        PackageInfo packageInfo = packageManager.getPackageInfo(packageName, GET_ACTIVITIES);
        assertThat(packageInfo.activities).hasLength((originalActivitiesCount + 1));
        ActivityInfo addedInfo = packageInfo.activities[originalActivitiesCount];
        assertThat(addedInfo.name).isEqualTo("name");
        assertThat(addedInfo.applicationInfo).isNotNull();
        assertThat(addedInfo.applicationInfo.packageName).isEqualTo(packageName);
    }

    @Test
    public void removeActivity() throws Exception {
        ComponentName componentName = new ComponentName(ApplicationProvider.getApplicationContext(), "org.robolectric.shadows.TestActivity");
        ActivityInfo removed = shadowPackageManager.removeActivity(componentName);
        assertThat(removed).isNotNull();
        try {
            packageManager.getActivityInfo(componentName, 0);
            // for now it goes here because package manager autocreates activities...
            // fail();
        } catch (NameNotFoundException e) {
            // expected
        }
    }

    @Test
    public void removeService() throws Exception {
        ComponentName componentName = new ComponentName(ApplicationProvider.getApplicationContext(), "com.foo.Service");
        ServiceInfo removed = shadowPackageManager.removeService(componentName);
        assertThat(removed).isNotNull();
        try {
            packageManager.getServiceInfo(componentName, 0);
            Assert.fail();
        } catch (NameNotFoundException e) {
            // expected
        }
    }

    @Test
    public void removeProvider() throws Exception {
        ComponentName componentName = new ComponentName(ApplicationProvider.getApplicationContext(), "org.robolectric.shadows.testing.TestContentProvider1");
        ProviderInfo removed = shadowPackageManager.removeProvider(componentName);
        assertThat(removed).isNotNull();
        try {
            packageManager.getProviderInfo(componentName, 0);
            Assert.fail();
        } catch (NameNotFoundException e) {
            // expected
        }
    }

    @Test
    public void removeReceiver() throws Exception {
        ComponentName componentName = new ComponentName(ApplicationProvider.getApplicationContext(), "org.robolectric.fakes.ConfigTestReceiver");
        ActivityInfo removed = shadowPackageManager.removeReceiver(componentName);
        assertThat(removed).isNotNull();
        try {
            packageManager.getReceiverInfo(componentName, 0);
            Assert.fail();
        } catch (NameNotFoundException e) {
            // expected
        }
    }

    @Test
    public void removeNonExistingComponent() throws Exception {
        ComponentName componentName = new ComponentName(ApplicationProvider.getApplicationContext(), "org.robolectric.DoesnExist");
        ActivityInfo removed = shadowPackageManager.removeReceiver(componentName);
        assertThat(removed).isNull();
    }

    @Test
    public void deletePackage() throws Exception {
        // Apps must have the android.permission.DELETE_PACKAGES set to delete packages.
        PackageManager packageManager = ApplicationProvider.getApplicationContext().getPackageManager();
        shadowPackageManager.getInternalMutablePackageInfo(ApplicationProvider.getApplicationContext().getPackageName()).requestedPermissions = new String[]{ permission.DELETE_PACKAGES };
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = "test.package";
        shadowPackageManager.installPackage(packageInfo);
        IPackageDeleteObserver mockObserver = Mockito.mock(IPackageDeleteObserver.class);
        packageManager.deletePackage(packageInfo.packageName, mockObserver, 0);
        shadowPackageManager.doPendingUninstallCallbacks();
        assertThat(shadowPackageManager.getDeletedPackages()).contains(packageInfo.packageName);
        Mockito.verify(mockObserver).packageDeleted(packageInfo.packageName, DELETE_SUCCEEDED);
    }

    @Test
    public void deletePackage_missingRequiredPermission() throws Exception {
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = "test.package";
        shadowPackageManager.installPackage(packageInfo);
        IPackageDeleteObserver mockObserver = Mockito.mock(IPackageDeleteObserver.class);
        packageManager.deletePackage(packageInfo.packageName, mockObserver, 0);
        shadowPackageManager.doPendingUninstallCallbacks();
        assertThat(shadowPackageManager.getDeletedPackages()).hasSize(0);
        Mockito.verify(mockObserver).packageDeleted(packageInfo.packageName, DELETE_FAILED_INTERNAL_ERROR);
    }

    private static class ActivityWithFilters extends Activity {}

    @Test
    public void getIntentFiltersForComponent() throws Exception {
        List<IntentFilter> intentFilters = shadowPackageManager.getIntentFiltersForActivity(new ComponentName(ApplicationProvider.getApplicationContext(), ShadowPackageManagerTest.ActivityWithFilters.class));
        assertThat(intentFilters).hasSize(1);
        IntentFilter intentFilter = intentFilters.get(0);
        assertThat(intentFilter.getCategory(0)).isEqualTo(CATEGORY_DEFAULT);
        assertThat(intentFilter.getAction(0)).isEqualTo(ACTION_VIEW);
        assertThat(intentFilter.getDataPath(0).getPath()).isEqualTo("/testPath/test.jpeg");
    }

    @Test
    public void getPackageInfo_shouldHaveWritableDataDirs() throws Exception {
        PackageInfo packageInfo = packageManager.getPackageInfo(ApplicationProvider.getApplicationContext().getPackageName(), 0);
        File dataDir = new File(packageInfo.applicationInfo.dataDir);
        assertThat(dataDir.isDirectory()).isTrue();
        assertThat(dataDir.exists()).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void getApplicationHiddenSettingAsUser_hidden() throws Exception {
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        /* hidden= */
        /* user= */
        packageManager.setApplicationHiddenSettingAsUser(packageName, true, null);
        assertThat(/* user= */
        packageManager.getApplicationHiddenSettingAsUser(packageName, null)).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void getApplicationHiddenSettingAsUser_notHidden() throws Exception {
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        assertThat(/* user= */
        packageManager.getApplicationHiddenSettingAsUser(packageName, null)).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void getApplicationHiddenSettingAsUser_unknownPackage() throws Exception {
        assertThat(/* user= */
        packageManager.getApplicationHiddenSettingAsUser("not.a.package", null)).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void setApplicationHiddenSettingAsUser_includeUninstalled() throws Exception {
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        /* hidden= */
        /* user= */
        packageManager.setApplicationHiddenSettingAsUser(packageName, true, null);
        assertThat(packageManager.getPackageInfo(packageName, MATCH_UNINSTALLED_PACKAGES)).isNotNull();
        assertThat(packageManager.getApplicationInfo(packageName, MATCH_UNINSTALLED_PACKAGES)).isNotNull();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(MATCH_UNINSTALLED_PACKAGES);
        assertThat(installedPackages).hasSize(1);
        assertThat(installedPackages.get(0).packageName).isEqualTo(packageName);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void setApplicationHiddenSettingAsUser_dontIncludeUninstalled() throws Exception {
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        boolean success = /* hidden= */
        /* user= */
        packageManager.setApplicationHiddenSettingAsUser(packageName, true, null);
        assertThat(success).isTrue();
        try {
            PackageInfo info = /* flags= */
            packageManager.getPackageInfo(packageName, 0);
            Assert.fail(("PackageManager.NameNotFoundException not thrown. Returned app with flags: " + (info.applicationInfo.flags)));
        } catch (NameNotFoundException e) {
            // Expected
        }
        try {
            /* flags= */
            packageManager.getApplicationInfo(packageName, 0);
            Assert.fail("PackageManager.NameNotFoundException not thrown");
        } catch (NameNotFoundException e) {
            // Expected
        }
        assertThat(/* flags= */
        packageManager.getInstalledPackages(0)).isEmpty();
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP_MR1)
    public void setUnbadgedApplicationIcon() throws Exception {
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        Drawable d = new BitmapDrawable();
        shadowPackageManager.setUnbadgedApplicationIcon(packageName, d);
        assertThat(packageManager.getApplicationInfo(packageName, GET_SHARED_LIBRARY_FILES).loadUnbadgedIcon(packageManager)).isSameAs(d);
    }

    @Test
    @Config(minSdk = VERSION_CODES.P)
    public void isPackageSuspended_nonExistentPackage_shouldThrow() {
        try {
            packageManager.isPackageSuspended(ShadowPackageManagerTest.TEST_PACKAGE_NAME);
            Assert.fail("Should have thrown NameNotFoundException");
        } catch (Exception expected) {
            // The compiler thinks that isPackageSuspended doesn't throw NameNotFoundException because the
            // test is compiled against the publicly released SDK.
            assertThat(expected).isInstanceOf(NameNotFoundException.class);
        }
    }

    @Test
    @Config(minSdk = VERSION_CODES.P)
    public void isPackageSuspended_callersPackage_shouldReturnFalse() throws NameNotFoundException {
        assertThat(packageManager.isPackageSuspended(ApplicationProvider.getApplicationContext().getPackageName())).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.P)
    public void isPackageSuspended_installedNeverSuspendedPackage_shouldReturnFalse() throws NameNotFoundException {
        shadowPackageManager.installPackage(ShadowPackageManagerTest.createPackageInfoWithPackageName(ShadowPackageManagerTest.TEST_PACKAGE_NAME));
        assertThat(packageManager.isPackageSuspended(ShadowPackageManagerTest.TEST_PACKAGE_NAME)).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.P)
    public void isPackageSuspended_installedSuspendedPackage_shouldReturnTrue() throws NameNotFoundException {
        shadowPackageManager.installPackage(ShadowPackageManagerTest.createPackageInfoWithPackageName(ShadowPackageManagerTest.TEST_PACKAGE_NAME));
        /* suspended= */
        /* appExtras= */
        /* launcherExtras= */
        /* dialogMessage= */
        setPackagesSuspended(new String[]{ ShadowPackageManagerTest.TEST_PACKAGE_NAME }, true, null, null, null);
        assertThat(packageManager.isPackageSuspended(ShadowPackageManagerTest.TEST_PACKAGE_NAME)).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.P)
    public void isPackageSuspended_installedUnsuspendedPackage_shouldReturnFalse() throws NameNotFoundException {
        shadowPackageManager.installPackage(ShadowPackageManagerTest.createPackageInfoWithPackageName(ShadowPackageManagerTest.TEST_PACKAGE_NAME));
        /* suspended= */
        /* appExtras= */
        /* launcherExtras= */
        /* dialogMessage= */
        setPackagesSuspended(new String[]{ ShadowPackageManagerTest.TEST_PACKAGE_NAME }, true, null, null, null);
        /* suspended= */
        /* appExtras= */
        /* launcherExtras= */
        /* dialogMessage= */
        setPackagesSuspended(new String[]{ ShadowPackageManagerTest.TEST_PACKAGE_NAME }, false, null, null, null);
        assertThat(packageManager.isPackageSuspended(ShadowPackageManagerTest.TEST_PACKAGE_NAME)).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.P)
    public void setPackagesSuspended_withProfileOwner_shouldThrow() {
        DevicePolicyManager devicePolicyManager = ((DevicePolicyManager) (ApplicationProvider.getApplicationContext().getSystemService(DEVICE_POLICY_SERVICE)));
        Shadows.shadowOf(devicePolicyManager).setProfileOwner(new ComponentName("com.profile.owner", "ProfileOwnerClass"));
        try {
            /* suspended= */
            /* appExtras= */
            /* launcherExtras= */
            /* dialogMessage= */
            setPackagesSuspended(new String[]{ ShadowPackageManagerTest.TEST_PACKAGE_NAME }, true, null, null, null);
            Assert.fail("Should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
        }
    }

    @Test
    @Config(minSdk = VERSION_CODES.P)
    public void setPackagesSuspended_withDeviceOwner_shouldThrow() {
        DevicePolicyManager devicePolicyManager = ((DevicePolicyManager) (ApplicationProvider.getApplicationContext().getSystemService(DEVICE_POLICY_SERVICE)));
        Shadows.shadowOf(devicePolicyManager).setDeviceOwner(new ComponentName("com.device.owner", "DeviceOwnerClass"));
        // Robolectric uses a random UID (see ShadowProcess#getRandomApplicationUid) that falls within
        // the range of the system user, so the device owner is on the current user and hence apps
        // cannot be suspended.
        try {
            /* suspended= */
            /* appExtras= */
            /* launcherExtras= */
            /* dialogMessage= */
            setPackagesSuspended(new String[]{ ShadowPackageManagerTest.TEST_PACKAGE_NAME }, true, null, null, null);
            Assert.fail("Should have thrown UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
        }
    }

    @Test
    @Config(minSdk = VERSION_CODES.P)
    public void setPackagesSuspended_shouldSuspendSuspendablePackagesAndReturnTheRest() throws NameNotFoundException {
        shadowPackageManager.installPackage(ShadowPackageManagerTest.createPackageInfoWithPackageName("android"));
        shadowPackageManager.installPackage(ShadowPackageManagerTest.createPackageInfoWithPackageName("com.suspendable.package1"));
        shadowPackageManager.installPackage(ShadowPackageManagerTest.createPackageInfoWithPackageName("com.suspendable.package2"));
        assertThat(/* suspended= */
        /* appExtras= */
        /* launcherExtras= */
        /* dialogMessage= */
        setPackagesSuspended(new String[]{ "com.nonexistent.package"// Unsuspenable (app doesn't exist).
        , "com.suspendable.package1", "android"// Unsuspendable (platform package).
        , "com.suspendable.package2", ApplicationProvider.getApplicationContext().getPackageName()// Unsuspendable (caller's package).
         }, true, null, null, null)).asList().containsExactly("com.nonexistent.package", "android", ApplicationProvider.getApplicationContext().getPackageName());
        assertThat(packageManager.isPackageSuspended("com.suspendable.package1")).isTrue();
        assertThat(packageManager.isPackageSuspended("android")).isFalse();
        assertThat(packageManager.isPackageSuspended("com.suspendable.package2")).isTrue();
        assertThat(packageManager.isPackageSuspended(ApplicationProvider.getApplicationContext().getPackageName())).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void getChangedPackages_negativeSequenceNumber_returnsNull() {
        shadowPackageManager.addChangedPackage((-5), ShadowPackageManagerTest.TEST_PACKAGE_NAME);
        assertThat(packageManager.getChangedPackages((-5))).isNull();
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void getChangedPackages_validSequenceNumber_withChangedPackages() {
        shadowPackageManager.addChangedPackage(0, ShadowPackageManagerTest.TEST_PACKAGE_NAME);
        shadowPackageManager.addChangedPackage(0, ShadowPackageManagerTest.TEST_PACKAGE2_NAME);
        shadowPackageManager.addChangedPackage(1, "appPackageName");
        ChangedPackages changedPackages = packageManager.getChangedPackages(0);
        assertThat(changedPackages.getSequenceNumber()).isEqualTo(1);
        assertThat(changedPackages.getPackageNames()).containsExactly(ShadowPackageManagerTest.TEST_PACKAGE_NAME, ShadowPackageManagerTest.TEST_PACKAGE2_NAME);
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void getChangedPackages_validSequenceNumber_noChangedPackages() {
        assertThat(packageManager.getChangedPackages(0)).isNull();
    }

    @Test
    @Config(minSdk = VERSION_CODES.P)
    public void setPackagesSuspended_shouldUnsuspendSuspendablePackagesAndReturnTheRest() throws NameNotFoundException {
        shadowPackageManager.installPackage(ShadowPackageManagerTest.createPackageInfoWithPackageName("android"));
        shadowPackageManager.installPackage(ShadowPackageManagerTest.createPackageInfoWithPackageName("com.suspendable.package1"));
        shadowPackageManager.installPackage(ShadowPackageManagerTest.createPackageInfoWithPackageName("com.suspendable.package2"));
        /* suspended= */
        /* appExtras= */
        /* launcherExtras= */
        /* dialogMessage= */
        setPackagesSuspended(new String[]{ "com.suspendable.package1", "com.suspendable.package2" }, false, null, null, null);
        assertThat(/* suspended= */
        /* appExtras= */
        /* launcherExtras= */
        /* dialogMessage= */
        setPackagesSuspended(new String[]{ "com.nonexistent.package"// Unsuspenable (app doesn't exist).
        , "com.suspendable.package1", "android"// Unsuspendable (platform package).
        , "com.suspendable.package2", ApplicationProvider.getApplicationContext().getPackageName()// Unsuspendable (caller's package).
         }, false, null, null, null)).asList().containsExactly("com.nonexistent.package", "android", ApplicationProvider.getApplicationContext().getPackageName());
        assertThat(packageManager.isPackageSuspended("com.suspendable.package1")).isFalse();
        assertThat(packageManager.isPackageSuspended("android")).isFalse();
        assertThat(packageManager.isPackageSuspended("com.suspendable.package2")).isFalse();
        assertThat(packageManager.isPackageSuspended(ApplicationProvider.getApplicationContext().getPackageName())).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.P)
    public void getPackageSetting_nonExistentPackage_shouldReturnNull() {
        assertThat(shadowPackageManager.getPackageSetting(ShadowPackageManagerTest.TEST_PACKAGE_NAME)).isNull();
    }

    @Test
    @Config(minSdk = VERSION_CODES.P)
    public void getPackageSetting_removedPackage_shouldReturnNull() {
        shadowPackageManager.installPackage(ShadowPackageManagerTest.createPackageInfoWithPackageName(ShadowPackageManagerTest.TEST_PACKAGE_NAME));
        shadowPackageManager.removePackage(ShadowPackageManagerTest.TEST_PACKAGE_NAME);
        assertThat(shadowPackageManager.getPackageSetting(ShadowPackageManagerTest.TEST_PACKAGE_NAME)).isNull();
    }

    @Test
    @Config(minSdk = VERSION_CODES.P)
    public void getPackageSetting_installedNeverSuspendedPackage_shouldReturnUnsuspendedSetting() {
        shadowPackageManager.installPackage(ShadowPackageManagerTest.createPackageInfoWithPackageName(ShadowPackageManagerTest.TEST_PACKAGE_NAME));
        PackageSetting setting = shadowPackageManager.getPackageSetting(ShadowPackageManagerTest.TEST_PACKAGE_NAME);
        assertThat(setting.isSuspended()).isFalse();
        assertThat(setting.getDialogMessage()).isNull();
        assertThat(setting.getSuspendedAppExtras()).isNull();
        assertThat(setting.getSuspendedLauncherExtras()).isNull();
    }

    @Test
    @Config(minSdk = VERSION_CODES.P)
    public void getPackageSetting_installedSuspendedPackage_shouldReturnSuspendedSetting() {
        shadowPackageManager.installPackage(ShadowPackageManagerTest.createPackageInfoWithPackageName(ShadowPackageManagerTest.TEST_PACKAGE_NAME));
        PersistableBundle appExtras = new PersistableBundle();
        appExtras.putString("key", "value");
        PersistableBundle launcherExtras = new PersistableBundle();
        launcherExtras.putInt("number", 7);
        setPackagesSuspended(new String[]{ ShadowPackageManagerTest.TEST_PACKAGE_NAME }, true, appExtras, launcherExtras, "Dialog message");
        PackageSetting setting = shadowPackageManager.getPackageSetting(ShadowPackageManagerTest.TEST_PACKAGE_NAME);
        assertThat(setting.isSuspended()).isTrue();
        assertThat(setting.getDialogMessage()).isEqualTo("Dialog message");
        assertThat(setting.getSuspendedAppExtras().getString("key")).isEqualTo("value");
        assertThat(setting.getSuspendedLauncherExtras().getInt("number")).isEqualTo(7);
    }

    @Test
    @Config(minSdk = VERSION_CODES.P)
    public void getPackageSetting_installedUnsuspendedPackage_shouldReturnUnsuspendedSetting() {
        shadowPackageManager.installPackage(ShadowPackageManagerTest.createPackageInfoWithPackageName(ShadowPackageManagerTest.TEST_PACKAGE_NAME));
        PersistableBundle appExtras = new PersistableBundle();
        appExtras.putString("key", "value");
        PersistableBundle launcherExtras = new PersistableBundle();
        launcherExtras.putInt("number", 7);
        setPackagesSuspended(new String[]{ ShadowPackageManagerTest.TEST_PACKAGE_NAME }, true, appExtras, launcherExtras, "Dialog message");
        setPackagesSuspended(new String[]{ ShadowPackageManagerTest.TEST_PACKAGE_NAME }, false, appExtras, launcherExtras, "Dialog message");
        PackageSetting setting = shadowPackageManager.getPackageSetting(ShadowPackageManagerTest.TEST_PACKAGE_NAME);
        assertThat(setting.isSuspended()).isFalse();
        assertThat(setting.getDialogMessage()).isNull();
        assertThat(setting.getSuspendedAppExtras()).isNull();
        assertThat(setting.getSuspendedLauncherExtras()).isNull();
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void canRequestPackageInstalls_shouldReturnFalseByDefault() throws Exception {
        assertThat(packageManager.canRequestPackageInstalls()).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void canRequestPackageInstalls_shouldReturnTrue_whenSetToTrue() throws Exception {
        shadowPackageManager.setCanRequestPackageInstalls(true);
        assertThat(packageManager.canRequestPackageInstalls()).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void canRequestPackageInstalls_shouldReturnFalse_whenSetToFalse() throws Exception {
        shadowPackageManager.setCanRequestPackageInstalls(false);
        assertThat(packageManager.canRequestPackageInstalls()).isFalse();
    }

    @Test
    public void loadIcon_default() {
        ActivityInfo info = new ActivityInfo();
        info.applicationInfo = new ApplicationInfo();
        info.packageName = "testPackage";
        info.name = "testName";
        Drawable icon = info.loadIcon(packageManager);
        assertThat(icon).isNotNull();
    }

    @Test
    public void loadIcon_specified() {
        ActivityInfo info = new ActivityInfo();
        info.applicationInfo = new ApplicationInfo();
        info.packageName = "testPackage";
        info.name = "testName";
        info.icon = an_image;
        Drawable icon = info.loadIcon(packageManager);
        assertThat(icon).isNotNull();
    }

    @Test
    public void resolveInfoComparator() {
        ResolveInfo priority = new ResolveInfo();
        priority.priority = 100;
        ResolveInfo preferredOrder = new ResolveInfo();
        preferredOrder.preferredOrder = 100;
        ResolveInfo defaultResolveInfo = new ResolveInfo();
        ResolveInfo[] array = new ResolveInfo[]{ priority, preferredOrder, defaultResolveInfo };
        Arrays.sort(array, new ResolveInfoComparator());
        assertThat(array).asList().containsExactly(preferredOrder, priority, defaultResolveInfo).inOrder();
    }

    @Test
    public void addActicityIfNotPresent_newPackage() throws Exception {
        ComponentName componentName = new ComponentName("test.package", "Activity");
        shadowPackageManager.addActivityIfNotPresent(componentName);
        ActivityInfo activityInfo = packageManager.getActivityInfo(componentName, 0);
        assertThat(activityInfo).isNotNull();
        assertThat(activityInfo.packageName).isEqualTo("test.package");
        assertThat(activityInfo.name).isEqualTo("Activity");
    }

    @Test
    public void addActicityIfNotPresent_existing() throws Exception {
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        ComponentName componentName = new ComponentName(packageName, ShadowPackageManagerTest.ActivityWithFilters.class.getName());
        shadowPackageManager.addActivityIfNotPresent(componentName);
        ActivityInfo activityInfo = packageManager.getActivityInfo(componentName, 0);
        assertThat(activityInfo).isNotNull();
        assertThat(activityInfo.packageName).isEqualTo(packageName);
        assertThat(activityInfo.name).isEqualTo(ShadowPackageManagerTest.ActivityWithFilters.class.getName());
    }

    @Test
    public void addActicityIfNotPresent_newActivity() throws Exception {
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        ComponentName componentName = new ComponentName(packageName, "NewActivity");
        shadowPackageManager.addActivityIfNotPresent(componentName);
        ActivityInfo activityInfo = packageManager.getActivityInfo(componentName, 0);
        assertThat(activityInfo).isNotNull();
        assertThat(activityInfo.packageName).isEqualTo(packageName);
        assertThat(activityInfo.name).isEqualTo("NewActivity");
    }

    @Test
    public void setSafeMode() {
        assertThat(packageManager.isSafeMode()).isFalse();
        shadowPackageManager.setSafeMode(true);
        assertThat(packageManager.isSafeMode()).isTrue();
    }
}

