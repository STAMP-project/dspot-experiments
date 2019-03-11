package org.robolectric.shadows.gms;


import RuntimeEnvironment.application;
import android.accounts.Account;
import android.content.Intent;
import com.google.android.gms.auth.AccountChangeEvent;
import com.google.android.gms.auth.GoogleAuthUtil;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.gms.ShadowGoogleAuthUtil.GoogleAuthUtilImpl;


/**
 * Unit test for {@link ShadowGoogleAuthUtil}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, shadows = { ShadowGoogleAuthUtil.class })
public class ShadowGoogleAuthUtilTest {
    @Mock
    private GoogleAuthUtilImpl mockGoogleAuthUtil;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getImplementation_defaultNotNull() {
        Assert.assertNotNull(ShadowGoogleAuthUtil.getImpl());
    }

    @Test
    public void provideImplementation_nullValueNotAllowed() {
        thrown.expect(NullPointerException.class);
        ShadowGoogleAuthUtil.provideImpl(null);
    }

    @Test
    public void getImplementation_shouldGetSetted() {
        ShadowGoogleAuthUtil.provideImpl(mockGoogleAuthUtil);
        GoogleAuthUtilImpl googleAuthUtil = ShadowGoogleAuthUtil.getImpl();
        Assert.assertSame(googleAuthUtil, mockGoogleAuthUtil);
    }

    @Test
    public void canRedirectStaticMethodToImplementation() throws Exception {
        ShadowGoogleAuthUtil.provideImpl(mockGoogleAuthUtil);
        GoogleAuthUtil.clearToken(application, "token");
        Mockito.verify(mockGoogleAuthUtil, Mockito.times(1)).clearToken(application, "token");
    }

    @Test
    public void getAccountChangeEvents_defaultReturnEmptyList() throws Exception {
        List<AccountChangeEvent> list = GoogleAuthUtil.getAccountChangeEvents(application, 0, "name");
        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void getAccountId_defaultNotNull() throws Exception {
        Assert.assertNotNull(GoogleAuthUtil.getAccountId(application, "name"));
    }

    @Test
    public void getToken_defaultNotNull() throws Exception {
        Assert.assertNotNull(GoogleAuthUtil.getToken(application, "name", "scope"));
        Assert.assertNotNull(GoogleAuthUtil.getToken(application, "name", "scope", null));
        Assert.assertNotNull(GoogleAuthUtil.getToken(application, new Account("name", "robo"), "scope"));
        Assert.assertNotNull(GoogleAuthUtil.getToken(application, new Account("name", "robo"), "scope", null));
        Assert.assertNotNull(GoogleAuthUtil.getTokenWithNotification(application, "name", "scope", null));
        Assert.assertNotNull(GoogleAuthUtil.getTokenWithNotification(application, "name", "scope", null, new Intent()));
        Assert.assertNotNull(GoogleAuthUtil.getTokenWithNotification(application, "name", "scope", null, "authority", null));
        Assert.assertNotNull(GoogleAuthUtil.getTokenWithNotification(application, new Account("name", "robo"), "scope", null));
        Assert.assertNotNull(GoogleAuthUtil.getTokenWithNotification(application, new Account("name", "robo"), "scope", null, new Intent()));
        Assert.assertNotNull(GoogleAuthUtil.getTokenWithNotification(application, new Account("name", "robo"), "scope", null, "authority", null));
    }

    @Test
    public void getTokenWithNotification_nullCallBackThrowIllegalArgumentException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        GoogleAuthUtil.getTokenWithNotification(application, "name", "scope", null, null);
    }

    @Test
    public void getTokenWithNotification_nullAuthorityThrowIllegalArgumentException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        Assert.assertNotNull(GoogleAuthUtil.getTokenWithNotification(application, "name", "scope", null, null, null));
    }
}

