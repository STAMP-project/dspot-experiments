package com.wix.reactnativenotifications.core;


import com.wix.reactnativenotifications.core.notification.PushNotificationProps;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class InitialNotificationHolderTest {
    @Test
    public void initialState() throws Exception {
        final InitialNotificationHolder uut = createUUT();
        Assert.assertNull(uut.get());
    }

    @Test
    public void setsInitialNotification() throws Exception {
        PushNotificationProps props = Mockito.mock(PushNotificationProps.class);
        final InitialNotificationHolder uut = createUUT();
        uut.set(props);
        Assert.assertEquals(props, uut.get());
    }

    @Test
    public void clearsInitialNotification() throws Exception {
        PushNotificationProps props = Mockito.mock(PushNotificationProps.class);
        final InitialNotificationHolder uut = createUUT();
        uut.set(props);
        uut.clear();
        Assert.assertNull(uut.get());
    }

    @Test
    public void replacesInitialNotification() throws Exception {
        PushNotificationProps props1 = Mockito.mock(PushNotificationProps.class);
        PushNotificationProps props2 = Mockito.mock(PushNotificationProps.class);
        final InitialNotificationHolder uut = createUUT();
        uut.set(props1);
        uut.set(props2);
        Assert.assertNotEquals(props1, props2);
        Assert.assertEquals(props2, uut.get());
    }

    @Test
    public void isALazySingleton() throws Exception {
        final InitialNotificationHolder instance = InitialNotificationHolder.getInstance();
        Assert.assertNotNull(instance);
        Assert.assertEquals(instance, InitialNotificationHolder.getInstance());
    }
}

