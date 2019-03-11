package org.robolectric.shadows;


import ActivityManager.LOCK_TASK_MODE_LOCKED;
import ActivityManager.LOCK_TASK_MODE_NONE;
import ActivityManager.MemoryInfo;
import ActivityManager.RunningAppProcessInfo;
import ActivityManager.RunningAppProcessInfo.REASON_PROVIDER_IN_USE;
import ActivityManager.RunningServiceInfo;
import ActivityManager.RunningTaskInfo;
import Context.USER_SERVICE;
import android.app.ActivityManager;
import android.app.ActivityManager.AppTask;
import android.app.Application;
import android.content.ComponentName;
import android.os.Build.VERSION_CODES;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.android.collect.Lists;
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
public class ShadowActivityManagerTest {
    private ActivityManager activityManager;

    @Test
    public void getMemoryInfo_canGetMemoryInfoForOurProcess() {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        memoryInfo.availMem = 12345;
        memoryInfo.lowMemory = true;
        memoryInfo.threshold = 10000;
        memoryInfo.totalMem = 55555;
        Shadows.shadowOf(activityManager).setMemoryInfo(memoryInfo);
        ActivityManager.MemoryInfo fetchedMemoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(fetchedMemoryInfo);
        assertThat(fetchedMemoryInfo.availMem).isEqualTo(12345);
        assertThat(fetchedMemoryInfo.lowMemory).isTrue();
        assertThat(fetchedMemoryInfo.threshold).isEqualTo(10000);
        assertThat(fetchedMemoryInfo.totalMem).isEqualTo(55555);
    }

    @Test
    public void getMemoryInfo_canGetMemoryInfoEvenWhenWeDidNotSetIt() {
        ActivityManager.MemoryInfo fetchedMemoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(fetchedMemoryInfo);
        assertThat(fetchedMemoryInfo.lowMemory).isFalse();
    }

    @Test
    public void getRunningTasks_shouldReturnTaskList() {
        final ActivityManager.RunningTaskInfo task1 = buildTaskInfo(new ComponentName("org.robolectric", "Task 1"));
        final ActivityManager.RunningTaskInfo task2 = buildTaskInfo(new ComponentName("org.robolectric", "Task 2"));
        assertThat(activityManager.getRunningTasks(Integer.MAX_VALUE)).isEmpty();
        Shadows.shadowOf(activityManager).setTasks(Lists.newArrayList(task1, task2));
        assertThat(activityManager.getRunningTasks(Integer.MAX_VALUE)).containsExactly(task1, task2);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void getAppTasks_shouldReturnAppTaskList() {
        final AppTask task1 = ShadowAppTask.newInstance();
        final AppTask task2 = ShadowAppTask.newInstance();
        assertThat(activityManager.getAppTasks()).isEmpty();
        Shadows.shadowOf(activityManager).setAppTasks(Lists.newArrayList(task1, task2));
        assertThat(activityManager.getAppTasks()).containsExactly(task1, task2);
    }

    @Test
    public void getRunningAppProcesses_shouldReturnProcessList() {
        final ActivityManager.RunningAppProcessInfo process1 = buildProcessInfo(new ComponentName("org.robolectric", "Process 1"));
        final ActivityManager.RunningAppProcessInfo process2 = buildProcessInfo(new ComponentName("org.robolectric", "Process 2"));
        assertThat(activityManager.getRunningAppProcesses().size()).isEqualTo(1);
        ActivityManager.RunningAppProcessInfo myInfo = activityManager.getRunningAppProcesses().get(0);
        assertThat(myInfo.pid).isEqualTo(Process.myPid());
        assertThat(myInfo.uid).isEqualTo(Process.myUid());
        assertThat(myInfo.processName).isEqualTo(getBaseContext().getPackageName());
        Shadows.shadowOf(activityManager).setProcesses(Lists.newArrayList(process1, process2));
        assertThat(activityManager.getRunningAppProcesses()).containsExactly(process1, process2);
    }

    @Test
    public void getRunningServices_shouldReturnServiceList() {
        final ActivityManager.RunningServiceInfo service1 = buildServiceInfo(new ComponentName("org.robolectric", "Service 1"));
        final ActivityManager.RunningServiceInfo service2 = buildServiceInfo(new ComponentName("org.robolectric", "Service 2"));
        assertThat(activityManager.getRunningServices(Integer.MAX_VALUE)).isEmpty();
        Shadows.shadowOf(activityManager).setServices(Lists.newArrayList(service1, service2));
        assertThat(activityManager.getRunningServices(Integer.MAX_VALUE)).containsExactly(service1, service2);
    }

    @Test
    public void getMemoryClass_shouldWork() {
        assertThat(activityManager.getMemoryClass()).isEqualTo(16);
        Shadows.shadowOf(activityManager).setMemoryClass(42);
        assertThat(activityManager.getMemoryClass()).isEqualTo(42);
    }

    @Test
    public void killBackgroundProcesses_shouldWork() {
        assertThat(Shadows.shadowOf(activityManager).getBackgroundPackage()).isNull();
        activityManager.killBackgroundProcesses("org.robolectric");
        assertThat(Shadows.shadowOf(activityManager).getBackgroundPackage()).isEqualTo("org.robolectric");
    }

    @Test
    public void getLauncherLargeIconDensity_shouldWork() {
        assertThat(activityManager.getLauncherLargeIconDensity()).isGreaterThan(0);
    }

    @Test
    public void isUserAMonkey_shouldReturnFalse() {
        assertThat(ActivityManager.isUserAMonkey()).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.KITKAT)
    public void setIsLowRamDevice() {
        Shadows.shadowOf(activityManager).setIsLowRamDevice(true);
        assertThat(activityManager.isLowRamDevice()).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void getLockTaskModeState() throws Exception {
        assertThat(activityManager.getLockTaskModeState()).isEqualTo(LOCK_TASK_MODE_NONE);
        Shadows.shadowOf(activityManager).setLockTaskModeState(LOCK_TASK_MODE_LOCKED);
        assertThat(activityManager.getLockTaskModeState()).isEqualTo(LOCK_TASK_MODE_LOCKED);
        assertThat(activityManager.isInLockTaskMode()).isTrue();
    }

    @Test
    public void getMyMemoryState() throws Exception {
        ActivityManager.RunningAppProcessInfo inState = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(inState);
        assertThat(inState.uid).isEqualTo(myUid());
        assertThat(inState.pid).isEqualTo(myPid());
        assertThat(inState.importanceReasonCode).isEqualTo(0);
        ActivityManager.RunningAppProcessInfo setState = new ActivityManager.RunningAppProcessInfo();
        setState.uid = myUid();
        setState.pid = myPid();
        setState.importanceReasonCode = RunningAppProcessInfo.REASON_PROVIDER_IN_USE;
        Shadows.shadowOf(activityManager).setProcesses(ImmutableList.of(setState));
        inState = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(inState);
        assertThat(inState.importanceReasonCode).isEqualTo(REASON_PROVIDER_IN_USE);
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void switchUser() {
        UserManager userManager = ((UserManager) (ApplicationProvider.getApplicationContext().getSystemService(USER_SERVICE)));
        Shadows.shadowOf(((Application) (ApplicationProvider.getApplicationContext()))).setSystemService(USER_SERVICE, userManager);
        Shadows.shadowOf(userManager).addUser(10, "secondary_user", 0);
        activityManager.switchUser(10);
        assertThat(UserHandle.myUserId()).isEqualTo(10);
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void getCurrentUser_default_returnZero() {
        assertThat(ActivityManager.getCurrentUser()).isEqualTo(0);
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void getCurrentUser_nonDefault_returnValueSet() {
        UserManager userManager = ((UserManager) (ApplicationProvider.getApplicationContext().getSystemService(USER_SERVICE)));
        Shadows.shadowOf(((Application) (ApplicationProvider.getApplicationContext()))).setSystemService(USER_SERVICE, userManager);
        Shadows.shadowOf(userManager).addUser(10, "secondary_user", 0);
        activityManager.switchUser(10);
        assertThat(ActivityManager.getCurrentUser()).isEqualTo(10);
    }
}

