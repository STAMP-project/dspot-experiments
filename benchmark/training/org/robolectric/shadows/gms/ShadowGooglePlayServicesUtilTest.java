package org.robolectric.shadows.gms;


import ConnectionResult.INTERNAL_ERROR;
import ConnectionResult.SERVICE_MISSING;
import ConnectionResult.SUCCESS;
import RuntimeEnvironment.application;
import android.app.Activity;
import android.content.Context;
import com.google.android.gms.common.GooglePlayServicesUtil;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.gms.ShadowGooglePlayServicesUtil.GooglePlayServicesUtilImpl;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, shadows = { ShadowGooglePlayServicesUtil.class })
public class ShadowGooglePlayServicesUtilTest {
    @Mock
    private GooglePlayServicesUtilImpl mockGooglePlayServicesUtil;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getImplementation_defaultNotNull() {
        Assert.assertNotNull(ShadowGooglePlayServicesUtil.getImpl());
    }

    @Test
    public void provideImplementation_nullValueNotAllowed() {
        thrown.expect(NullPointerException.class);
        ShadowGooglePlayServicesUtil.provideImpl(null);
    }

    @Test
    public void getImplementation_shouldGetSetted() {
        ShadowGooglePlayServicesUtil.provideImpl(mockGooglePlayServicesUtil);
        ShadowGooglePlayServicesUtil.GooglePlayServicesUtilImpl googlePlayServicesUtil = ShadowGooglePlayServicesUtil.getImpl();
        Assert.assertSame(googlePlayServicesUtil, mockGooglePlayServicesUtil);
    }

    @Test
    public void canRedirectStaticMethodToImplementation() {
        ShadowGooglePlayServicesUtil.provideImpl(mockGooglePlayServicesUtil);
        Mockito.when(mockGooglePlayServicesUtil.isGooglePlayServicesAvailable(ArgumentMatchers.any(Context.class))).thenReturn(INTERNAL_ERROR);
        Assert.assertEquals(INTERNAL_ERROR, GooglePlayServicesUtil.isGooglePlayServicesAvailable(application));
    }

    @Test
    public void getErrorString_goesToRealImpl() {
        Assert.assertEquals("SUCCESS", GooglePlayServicesUtil.getErrorString(SUCCESS));
        Assert.assertEquals("SERVICE_MISSING", GooglePlayServicesUtil.getErrorString(SERVICE_MISSING));
    }

    @Test
    public void getRemoteContext_defaultNotNull() {
        Assert.assertNotNull(GooglePlayServicesUtil.getRemoteContext(application));
    }

    @Test
    public void getRemoteResource_defaultNotNull() {
        Assert.assertNotNull(GooglePlayServicesUtil.getRemoteResource(application));
    }

    @Test
    public void getErrorDialog() {
        Assert.assertNotNull(GooglePlayServicesUtil.getErrorDialog(SERVICE_MISSING, new Activity(), 0));
        Assert.assertNull(GooglePlayServicesUtil.getErrorDialog(SUCCESS, new Activity(), 0));
        Assert.assertNotNull(GooglePlayServicesUtil.getErrorDialog(SERVICE_MISSING, new Activity(), 0, null));
        Assert.assertNull(GooglePlayServicesUtil.getErrorDialog(SUCCESS, new Activity(), 0, null));
    }

    @Test
    public void getErrorPendingIntent() {
        Assert.assertNotNull(GooglePlayServicesUtil.getErrorPendingIntent(SERVICE_MISSING, application, 0));
        Assert.assertNull(GooglePlayServicesUtil.getErrorPendingIntent(SUCCESS, application, 0));
    }

    @Test
    public void getOpenSourceSoftwareLicenseInfo_defaultNotNull() {
        Assert.assertNotNull(GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(application));
    }

    @Test
    public void isGooglePlayServicesAvailable_defaultServiceMissing() {
        Assert.assertEquals(SERVICE_MISSING, GooglePlayServicesUtil.isGooglePlayServicesAvailable(application));
    }
}

