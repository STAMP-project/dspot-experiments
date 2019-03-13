package com.wix.reactnativenotifications.core.notification;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.facebook.react.bridge.ReactContext;
import com.wix.reactnativenotifications.core.AppLaunchHelper;
import com.wix.reactnativenotifications.core.AppLifecycleFacade;
import com.wix.reactnativenotifications.core.AppLifecycleFacade.AppVisibilityListener;
import com.wix.reactnativenotifications.core.InitialNotificationHolder;
import com.wix.reactnativenotifications.core.JsIOHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class PushNotificationTest {
    private static final String NOTIFICATION_OPENED_EVENT_NAME = "notificationOpened";

    private static final String NOTIFICATION_RECEIVED_EVENT_NAME = "notificationReceived";

    private static final String DEFAULT_NOTIFICATION_TITLE = "Notification-title";

    private static final String DEFAULT_NOTIFICATION_BODY = "Notification-body";

    @Mock
    private ReactContext mReactContext;

    @Mock
    private Context mContext;

    @Mock
    private NotificationManager mNotificationManager;

    @Mock
    private Bundle mDefaultBundle;

    @Mock
    private Intent mLaunchIntent;

    @Mock
    private AppLifecycleFacade mAppLifecycleFacade;

    @Mock
    private AppLaunchHelper mAppLaunchHelper;

    @Mock
    private JsIOHelper mJsIOHelper;

    @Test
    public void onOpened_noReactContext_launchApp() throws Exception {
        Mockito.when(mAppLifecycleFacade.isReactInitialized()).thenReturn(false);
        final PushNotification uut = createUUT();
        uut.onOpened();
        Mockito.verify(mContext).startActivity(ArgumentMatchers.eq(mLaunchIntent));
        // The unit shouldn't wait for visibility in this case cause we dont make the extra effort of
        // notifying the notification upon app launch completion (simply cause we dont know when in completes).
        // Instead, the user is expected to use getInitialNotification().
        Mockito.verify(mAppLifecycleFacade, Mockito.never()).addVisibilityListener(ArgumentMatchers.any(AppVisibilityListener.class));
    }

    @Test
    public void onOpened_noReactContext_setAsInitialNotification() throws Exception {
        Mockito.when(mAppLifecycleFacade.isReactInitialized()).thenReturn(false);
        Activity currentActivity = Mockito.mock(Activity.class);
        Mockito.when(mReactContext.getCurrentActivity()).thenReturn(currentActivity);
        final PushNotification uut = createUUT();
        uut.onOpened();
        Mockito.verify(InitialNotificationHolder.getInstance()).set(ArgumentMatchers.any(PushNotificationProps.class));
    }

    @Test
    public void onOpened_appInvisible_resumeAppWaitForVisibility() throws Exception {
        setUpBackgroundApp();
        final PushNotification uut = createUUT();
        uut.onOpened();
        Mockito.verify(mContext).startActivity(ArgumentMatchers.any(Intent.class));
        Mockito.verify(mAppLifecycleFacade).addVisibilityListener(ArgumentMatchers.any(AppVisibilityListener.class));
    }

    @Test
    public void onOpened_appInvisible_dontSetInitialNotification() throws Exception {
        setUpBackgroundApp();
        Activity currentActivity = Mockito.mock(Activity.class);
        Mockito.when(mReactContext.getCurrentActivity()).thenReturn(currentActivity);
        final PushNotification uut = createUUT();
        uut.onOpened();
        Mockito.verify(InitialNotificationHolder.getInstance(), Mockito.never()).set(ArgumentMatchers.any(PushNotificationProps.class));
    }

    @Test
    public void onOpened_appGoesVisible_resumeAppAndNotifyJs() throws Exception {
        // Arrange
        setUpBackgroundApp();
        // Act
        final PushNotification uut = createUUT();
        uut.onOpened();
        // Hijack and invoke visibility listener
        ArgumentCaptor<AppVisibilityListener> listenerCaptor = ArgumentCaptor.forClass(AppVisibilityListener.class);
        Mockito.verify(mAppLifecycleFacade).addVisibilityListener(listenerCaptor.capture());
        AppVisibilityListener listener = listenerCaptor.getValue();
        listener.onAppVisible();
        // Assert
        Mockito.verify(mJsIOHelper).sendEventToJS(ArgumentMatchers.eq(PushNotificationTest.NOTIFICATION_OPENED_EVENT_NAME), ArgumentMatchers.eq(mDefaultBundle), ArgumentMatchers.eq(mReactContext));
    }

    @Test
    public void onOpened_appVisible_notifyJS() throws Exception {
        setUpForegroundApp();
        final PushNotification uut = createUUT();
        uut.onOpened();
        Mockito.verify(mContext, Mockito.never()).startActivity(ArgumentMatchers.any(Intent.class));
        Mockito.verify(mJsIOHelper).sendEventToJS(ArgumentMatchers.eq(PushNotificationTest.NOTIFICATION_OPENED_EVENT_NAME), ArgumentMatchers.eq(mDefaultBundle), ArgumentMatchers.eq(mReactContext));
    }

    @Test
    public void onOpened_appVisible_clearNotificationsDrawer() throws Exception {
        Mockito.verify(mNotificationManager, Mockito.never()).cancelAll();
        setUpForegroundApp();
        final PushNotification uut = createUUT();
        uut.onOpened();
        Mockito.verify(mNotificationManager).cancelAll();
    }

    @Test
    public void onOpened_appVisible_dontSetInitialNotification() throws Exception {
        setUpForegroundApp();
        Activity currentActivity = Mockito.mock(Activity.class);
        Mockito.when(mReactContext.getCurrentActivity()).thenReturn(currentActivity);
        final PushNotification uut = createUUT();
        uut.onOpened();
        Mockito.verify(InitialNotificationHolder.getInstance(), Mockito.never()).set(ArgumentMatchers.any(PushNotificationProps.class));
    }

    @Test
    public void onOpened_reactInitializedWithNoActivities_setAsInitialNotification() throws Exception {
        setUpBackgroundApp();
        Mockito.when(mReactContext.getCurrentActivity()).thenReturn(null);// Just for clarity

        final PushNotification uut = createUUT();
        uut.onOpened();
        Mockito.verify(InitialNotificationHolder.getInstance()).set(ArgumentMatchers.any(PushNotificationProps.class));
    }

    @Test
    public void onReceived_validData_postNotificationAndNotifyJS() throws Exception {
        // Arrange
        setUpForegroundApp();
        // Act
        final PushNotification uut = createUUT();
        uut.onReceived();
        // Assert
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        Mockito.verify(mNotificationManager).notify(ArgumentMatchers.anyInt(), notificationCaptor.capture());
        verifyNotification(notificationCaptor.getValue());
        Mockito.verify(mJsIOHelper).sendEventToJS(ArgumentMatchers.eq(PushNotificationTest.NOTIFICATION_RECEIVED_EVENT_NAME), ArgumentMatchers.eq(mDefaultBundle), ArgumentMatchers.eq(mReactContext));
    }

    @Test
    public void onReceived_validDataForBackgroundApp_postNotificationAndNotifyJs() throws Exception {
        // Arrange
        setUpForegroundApp();
        // Act
        final PushNotification uut = createUUT();
        uut.onReceived();
        // Assert
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        Mockito.verify(mNotificationManager).notify(ArgumentMatchers.anyInt(), notificationCaptor.capture());
        verifyNotification(notificationCaptor.getValue());
        Mockito.verify(mJsIOHelper).sendEventToJS(ArgumentMatchers.eq(PushNotificationTest.NOTIFICATION_RECEIVED_EVENT_NAME), ArgumentMatchers.eq(mDefaultBundle), ArgumentMatchers.eq(mReactContext));
    }

    @Test
    public void onReceived_validDataForDeadApp_postNotificationDontNotifyJS() throws Exception {
        final PushNotification uut = createUUT();
        uut.onReceived();
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        Mockito.verify(mNotificationManager).notify(ArgumentMatchers.anyInt(), notificationCaptor.capture());
        verifyNotification(notificationCaptor.getValue());
        Mockito.verify(mJsIOHelper, Mockito.never()).sendEventToJS(ArgumentMatchers.eq(PushNotificationTest.NOTIFICATION_RECEIVED_EVENT_NAME), ArgumentMatchers.any(Bundle.class), ArgumentMatchers.any(ReactContext.class));
    }

    @Test
    public void onPostRequest_withValidDataButNoId_postNotifications() throws Exception {
        // Arrange
        setUpForegroundApp();
        // Act
        final PushNotification uut = createUUT();
        uut.onPostRequest(null);
        // Assert
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        Mockito.verify(mNotificationManager).notify(ArgumentMatchers.anyInt(), notificationCaptor.capture());
        verifyNotification(notificationCaptor.getValue());
        // Shouldn't notify an event on an explicit call to notification posting
        Mockito.verify(mJsIOHelper, Mockito.never()).sendEventToJS(ArgumentMatchers.eq(PushNotificationTest.NOTIFICATION_RECEIVED_EVENT_NAME), ArgumentMatchers.any(Bundle.class), ArgumentMatchers.any(ReactContext.class));
    }

    @Test
    public void onPostRequest_withValidDataButNoId_idsShouldBeUnique() throws Exception {
        createUUT().onPostRequest(null);
        createUUT().onPostRequest(null);
        ArgumentCaptor<Integer> idsCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(mNotificationManager, Mockito.times(2)).notify(idsCaptor.capture(), ArgumentMatchers.any(Notification.class));
        Assert.assertNotEquals(idsCaptor.getAllValues().get(0), idsCaptor.getAllValues().get(1));
    }

    @Test
    public void onPostRequest_withValidDataAndExplicitId_postNotification() throws Exception {
        final int id = 666;
        final PushNotification uut = createUUT();
        uut.onPostRequest(id);
        Mockito.verify(mNotificationManager).notify(ArgumentMatchers.eq(id), ArgumentMatchers.any(Notification.class));
    }

    @Test
    public void onPostRequest_emptyData_postNotification() throws Exception {
        PushNotification uut = createUUT(new Bundle());
        uut.onPostRequest(null);
        Mockito.verify(mNotificationManager).notify(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Notification.class));
    }
}

