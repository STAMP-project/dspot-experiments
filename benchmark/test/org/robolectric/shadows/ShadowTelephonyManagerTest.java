package org.robolectric.shadows;


import ServiceState.STATE_OUT_OF_SERVICE;
import SubscriptionManager.DEFAULT_SUBSCRIPTION_ID;
import TelephonyManager.NETWORK_TYPE_CDMA;
import TelephonyManager.PHONE_TYPE_CDMA;
import TelephonyManager.PHONE_TYPE_GSM;
import TelephonyManager.SIM_STATE_READY;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.PersistableBundle;
import android.telecom.PhoneAccountHandle;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.UiccSlotInfo;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadow.api.Shadow;


@RunWith(AndroidJUnit4.class)
public class ShadowTelephonyManagerTest {
    private TelephonyManager telephonyManager;

    @Test
    public void testListenInit() {
        PhoneStateListener listener = Mockito.mock(PhoneStateListener.class);
        telephonyManager.listen(listener, (((PhoneStateListener.LISTEN_CALL_STATE) | (PhoneStateListener.LISTEN_CELL_INFO)) | (PhoneStateListener.LISTEN_CELL_LOCATION)));
        Mockito.verify(listener).onCallStateChanged(TelephonyManager.CALL_STATE_IDLE, null);
        Mockito.verify(listener).onCellLocationChanged(null);
        if ((VERSION.SDK_INT) >= (VERSION_CODES.JELLY_BEAN_MR1)) {
            Mockito.verify(listener).onCellInfoChanged(Collections.emptyList());
        }
    }

    @Test
    public void shouldGiveDeviceId() {
        String testId = "TESTING123";
        Shadows.shadowOf(telephonyManager).setDeviceId(testId);
        Assert.assertEquals(testId, telephonyManager.getDeviceId());
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void shouldGiveDeviceIdForSlot() {
        Shadows.shadowOf(telephonyManager).setDeviceId(1, "device in slot 1");
        Shadows.shadowOf(telephonyManager).setDeviceId(2, "device in slot 2");
        Assert.assertEquals("device in slot 1", telephonyManager.getDeviceId(1));
        Assert.assertEquals("device in slot 2", telephonyManager.getDeviceId(2));
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void getImei() {
        String testImei = "4test imei";
        Shadows.shadowOf(telephonyManager).setImei(testImei);
        Assert.assertEquals(testImei, telephonyManager.getImei());
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void getImeiForSlot() {
        Shadows.shadowOf(telephonyManager).setImei("defaultImei");
        Shadows.shadowOf(telephonyManager).setImei(0, "imei0");
        Shadows.shadowOf(telephonyManager).setImei(1, "imei1");
        Assert.assertEquals("imei0", telephonyManager.getImei(0));
        Assert.assertEquals("imei1", telephonyManager.getImei(1));
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void getMeid() {
        String testMeid = "4test meid";
        Shadows.shadowOf(telephonyManager).setMeid(testMeid);
        Assert.assertEquals(testMeid, telephonyManager.getMeid());
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void getMeidForSlot() {
        Shadows.shadowOf(telephonyManager).setMeid("defaultMeid");
        Shadows.shadowOf(telephonyManager).setMeid(0, "meid0");
        Shadows.shadowOf(telephonyManager).setMeid(1, "meid1");
        Assert.assertEquals("meid0", telephonyManager.getMeid(0));
        Assert.assertEquals("meid1", telephonyManager.getMeid(1));
    }

    @Test
    public void shouldGiveNetworkOperatorName() {
        Shadows.shadowOf(telephonyManager).setNetworkOperatorName("SomeOperatorName");
        Assert.assertEquals("SomeOperatorName", telephonyManager.getNetworkOperatorName());
    }

    @Test
    public void shouldGiveSimOperatorName() {
        Shadows.shadowOf(telephonyManager).setSimOperatorName("SomeSimOperatorName");
        Assert.assertEquals("SomeSimOperatorName", telephonyManager.getSimOperatorName());
    }

    @Test(expected = SecurityException.class)
    public void getSimSerialNumber_shouldThrowSecurityExceptionWhenReadPhoneStatePermissionNotGranted() throws Exception {
        Shadows.shadowOf(telephonyManager).setReadPhoneStatePermission(false);
        telephonyManager.getSimSerialNumber();
    }

    @Test
    public void shouldGetSimSerialNumber() {
        Shadows.shadowOf(telephonyManager).setSimSerialNumber("SomeSerialNumber");
        Assert.assertEquals("SomeSerialNumber", telephonyManager.getSimSerialNumber());
    }

    @Test
    public void shouldGiveNetworkType() {
        Shadows.shadowOf(telephonyManager).setNetworkType(NETWORK_TYPE_CDMA);
        Assert.assertEquals(NETWORK_TYPE_CDMA, telephonyManager.getNetworkType());
    }

    @Test
    @Config(minSdk = VERSION_CODES.N)
    public void shouldGiveVoiceNetworkType() {
        Shadows.shadowOf(telephonyManager).setVoiceNetworkType(NETWORK_TYPE_CDMA);
        assertThat(telephonyManager.getVoiceNetworkType()).isEqualTo(NETWORK_TYPE_CDMA);
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR1)
    public void shouldGiveAllCellInfo() {
        PhoneStateListener listener = Mockito.mock(PhoneStateListener.class);
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_CELL_INFO);
        List<CellInfo> allCellInfo = Collections.singletonList(Mockito.mock(CellInfo.class));
        Shadows.shadowOf(telephonyManager).setAllCellInfo(allCellInfo);
        Assert.assertEquals(allCellInfo, telephonyManager.getAllCellInfo());
        Mockito.verify(listener).onCellInfoChanged(allCellInfo);
    }

    @Test
    public void shouldGiveNetworkCountryIso() {
        Shadows.shadowOf(telephonyManager).setNetworkCountryIso("SomeIso");
        Assert.assertEquals("SomeIso", telephonyManager.getNetworkCountryIso());
    }

    @Test
    public void shouldGiveNetworkOperator() {
        Shadows.shadowOf(telephonyManager).setNetworkOperator("SomeOperator");
        Assert.assertEquals("SomeOperator", telephonyManager.getNetworkOperator());
    }

    @Test
    public void shouldGiveLine1Number() {
        Shadows.shadowOf(telephonyManager).setLine1Number("123-244-2222");
        Assert.assertEquals("123-244-2222", telephonyManager.getLine1Number());
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR2)
    public void shouldGiveGroupIdLevel1() {
        Shadows.shadowOf(telephonyManager).setGroupIdLevel1("SomeGroupId");
        Assert.assertEquals("SomeGroupId", telephonyManager.getGroupIdLevel1());
    }

    @Test(expected = SecurityException.class)
    public void getDeviceId_shouldThrowSecurityExceptionWhenReadPhoneStatePermissionNotGranted() throws Exception {
        Shadows.shadowOf(telephonyManager).setReadPhoneStatePermission(false);
        telephonyManager.getDeviceId();
    }

    @Test
    public void shouldGivePhoneType() {
        Shadows.shadowOf(telephonyManager).setPhoneType(PHONE_TYPE_CDMA);
        Assert.assertEquals(PHONE_TYPE_CDMA, telephonyManager.getPhoneType());
        Shadows.shadowOf(telephonyManager).setPhoneType(PHONE_TYPE_GSM);
        Assert.assertEquals(PHONE_TYPE_GSM, telephonyManager.getPhoneType());
    }

    @Test
    public void shouldGiveCellLocation() {
        PhoneStateListener listener = Mockito.mock(PhoneStateListener.class);
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_CELL_LOCATION);
        CellLocation mockCellLocation = Mockito.mock(CellLocation.class);
        Shadows.shadowOf(telephonyManager).setCellLocation(mockCellLocation);
        Assert.assertEquals(mockCellLocation, telephonyManager.getCellLocation());
        Mockito.verify(listener).onCellLocationChanged(mockCellLocation);
    }

    @Test
    public void shouldGiveCallState() {
        PhoneStateListener listener = Mockito.mock(PhoneStateListener.class);
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        Shadows.shadowOf(telephonyManager).setCallState(TelephonyManager.CALL_STATE_RINGING, "911");
        Assert.assertEquals(TelephonyManager.CALL_STATE_RINGING, telephonyManager.getCallState());
        Mockito.verify(listener).onCallStateChanged(TelephonyManager.CALL_STATE_RINGING, "911");
        Shadows.shadowOf(telephonyManager).setCallState(TelephonyManager.CALL_STATE_OFFHOOK, "911");
        Assert.assertEquals(TelephonyManager.CALL_STATE_OFFHOOK, telephonyManager.getCallState());
        Mockito.verify(listener).onCallStateChanged(TelephonyManager.CALL_STATE_OFFHOOK, null);
    }

    @Test
    public void isSmsCapable() {
        assertThat(telephonyManager.isSmsCapable()).isTrue();
        Shadows.shadowOf(telephonyManager).setIsSmsCapable(false);
        assertThat(telephonyManager.isSmsCapable()).isFalse();
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void shouldGiveCarrierConfigIfSet() {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putInt("foo", 42);
        Shadows.shadowOf(telephonyManager).setCarrierConfig(bundle);
        Assert.assertEquals(bundle, telephonyManager.getCarrierConfig());
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void shouldGiveNonNullCarrierConfigIfNotSet() {
        Assert.assertNotNull(telephonyManager.getCarrierConfig());
    }

    @Test
    public void shouldGiveVoiceMailNumber() {
        Shadows.shadowOf(telephonyManager).setVoiceMailNumber("123");
        Assert.assertEquals("123", telephonyManager.getVoiceMailNumber());
    }

    @Test
    public void shouldGiveVoiceMailAlphaTag() {
        Shadows.shadowOf(telephonyManager).setVoiceMailAlphaTag("tag");
        Assert.assertEquals("tag", telephonyManager.getVoiceMailAlphaTag());
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void shouldGivePhoneCount() {
        Shadows.shadowOf(telephonyManager).setPhoneCount(42);
        Assert.assertEquals(42, telephonyManager.getPhoneCount());
    }

    @Test
    @Config(minSdk = VERSION_CODES.N)
    public void shouldGiveVoiceVibrationEnabled() {
        PhoneAccountHandle phoneAccountHandle = new PhoneAccountHandle(new android.content.ComponentName(ApplicationProvider.getApplicationContext(), Object.class), "handle");
        Shadows.shadowOf(telephonyManager).setVoicemailVibrationEnabled(phoneAccountHandle, true);
        Assert.assertTrue(telephonyManager.isVoicemailVibrationEnabled(phoneAccountHandle));
    }

    @Test
    @Config(minSdk = VERSION_CODES.N)
    public void shouldGiveVoicemailRingtoneUri() {
        PhoneAccountHandle phoneAccountHandle = new PhoneAccountHandle(new android.content.ComponentName(ApplicationProvider.getApplicationContext(), Object.class), "handle");
        Uri ringtoneUri = /* fragment = */
        Uri.fromParts("file", "ringtone.mp3", null);
        Shadows.shadowOf(telephonyManager).setVoicemailRingtoneUri(phoneAccountHandle, ringtoneUri);
        Assert.assertEquals(ringtoneUri, telephonyManager.getVoicemailRingtoneUri(phoneAccountHandle));
    }

    // The setter on the real manager was added in O
    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void shouldSetVoicemailRingtoneUri() {
        PhoneAccountHandle phoneAccountHandle = new PhoneAccountHandle(new android.content.ComponentName(ApplicationProvider.getApplicationContext(), Object.class), "handle");
        Uri ringtoneUri = /* fragment = */
        Uri.fromParts("file", "ringtone.mp3", null);
        // Note: Using the real manager to set, instead of the shadow.
        telephonyManager.setVoicemailRingtoneUri(phoneAccountHandle, ringtoneUri);
        Assert.assertEquals(ringtoneUri, telephonyManager.getVoicemailRingtoneUri(phoneAccountHandle));
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void shouldCreateForPhoneAccountHandle() {
        PhoneAccountHandle phoneAccountHandle = new PhoneAccountHandle(new android.content.ComponentName(ApplicationProvider.getApplicationContext(), Object.class), "handle");
        TelephonyManager mockTelephonyManager = Mockito.mock(TelephonyManager.class);
        Shadows.shadowOf(telephonyManager).setTelephonyManagerForHandle(phoneAccountHandle, mockTelephonyManager);
        Assert.assertEquals(mockTelephonyManager, telephonyManager.createForPhoneAccountHandle(phoneAccountHandle));
    }

    @Test
    @Config(minSdk = VERSION_CODES.N)
    public void shouldCreateForSubscriptionId() {
        int subscriptionId = 42;
        TelephonyManager mockTelephonyManager = Mockito.mock(TelephonyManager.class);
        Shadows.shadowOf(telephonyManager).setTelephonyManagerForSubscriptionId(subscriptionId, mockTelephonyManager);
        Assert.assertEquals(mockTelephonyManager, telephonyManager.createForSubscriptionId(subscriptionId));
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void shouldSetServiceState() {
        ServiceState serviceState = new ServiceState();
        serviceState.setState(STATE_OUT_OF_SERVICE);
        Shadows.shadowOf(telephonyManager).setServiceState(serviceState);
        Assert.assertEquals(serviceState, telephonyManager.getServiceState());
    }

    @Test
    public void shouldSetIsNetworkRoaming() {
        Shadows.shadowOf(telephonyManager).setIsNetworkRoaming(true);
        Assert.assertTrue(telephonyManager.isNetworkRoaming());
    }

    @Test
    public void shouldGetSimState() {
        assertThat(telephonyManager.getSimState()).isEqualTo(SIM_STATE_READY);
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void shouldGetSimStateUsingSlotNumber() {
        int expectedSimState = TelephonyManager.SIM_STATE_ABSENT;
        int slotNumber = 3;
        Shadows.shadowOf(telephonyManager).setSimState(slotNumber, expectedSimState);
        assertThat(telephonyManager.getSimState(slotNumber)).isEqualTo(expectedSimState);
    }

    @Test
    public void shouldGetSimIso() {
        assertThat(telephonyManager.getSimCountryIso()).isEmpty();
    }

    @Test
    @Config(minSdk = VERSION_CODES.N)
    public void shouldGetSimIosWhenSetUsingSlotNumber() {
        String expectedSimIso = "usa";
        int subId = 2;
        Shadows.shadowOf(telephonyManager).setSimCountryIso(subId, expectedSimIso);
        assertThat(telephonyManager.getSimCountryIso(subId)).isEqualTo(expectedSimIso);
    }

    @Test
    @Config(minSdk = VERSION_CODES.P)
    public void shouldGetSimCarrierId() {
        int expectedCarrierId = 132;
        Shadows.shadowOf(telephonyManager).setSimCarrierId(expectedCarrierId);
        assertThat(telephonyManager.getSimCarrierId()).isEqualTo(expectedCarrierId);
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void shouldGetCurrentPhoneTypeGivenSubId() {
        int subId = 1;
        int expectedPhoneType = TelephonyManager.PHONE_TYPE_GSM;
        Shadows.shadowOf(telephonyManager).setCurrentPhoneType(subId, expectedPhoneType);
        assertThat(telephonyManager.getCurrentPhoneType(subId)).isEqualTo(expectedPhoneType);
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void shouldGetCarrierPackageNamesForIntentAndPhone() {
        List<String> packages = Collections.singletonList("package1");
        int phoneId = 123;
        Shadows.shadowOf(telephonyManager).setCarrierPackageNamesForPhone(phoneId, packages);
        assertThat(telephonyManager.getCarrierPackageNamesForIntentAndPhone(new Intent(), phoneId)).isEqualTo(packages);
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void shouldGetCarrierPackageNamesForIntent() {
        List<String> packages = Collections.singletonList("package1");
        Shadows.shadowOf(telephonyManager).setCarrierPackageNamesForPhone(DEFAULT_SUBSCRIPTION_ID, packages);
        assertThat(telephonyManager.getCarrierPackageNamesForIntent(new Intent())).isEqualTo(packages);
    }

    @Test
    public void resetSimStates_shouldRetainDefaultState() {
        Shadows.shadowOf(telephonyManager).resetSimStates();
        assertThat(telephonyManager.getSimState()).isEqualTo(SIM_STATE_READY);
    }

    @Test
    @Config(minSdk = VERSION_CODES.N)
    public void resetSimCountryIsos_shouldRetainDefaultState() {
        Shadows.shadowOf(telephonyManager).resetSimCountryIsos();
        assertThat(telephonyManager.getSimCountryIso()).isEmpty();
    }

    @Test
    public void shouldSetSubscriberId() {
        String subscriberId = "123451234512345";
        Shadows.shadowOf(telephonyManager).setSubscriberId(subscriberId);
        assertThat(telephonyManager.getSubscriberId()).isEqualTo(subscriberId);
    }

    @Test
    @Config(minSdk = VERSION_CODES.P)
    public void getUiccSlotsInfo() {
        UiccSlotInfo slotInfo1 = new UiccSlotInfo(true, true, null, 0, 0, true);
        UiccSlotInfo slotInfo2 = new UiccSlotInfo(true, true, null, 0, 1, true);
        UiccSlotInfo[] slotInfos = new UiccSlotInfo[]{ slotInfo1, slotInfo2 };
        Shadows.shadowOf(telephonyManager).setUiccSlotsInfo(slotInfos);
        assertThat(Shadows.shadowOf(telephonyManager).getUiccSlotsInfo()).isEqualTo(slotInfos);
    }

    @Test
    @Config(minSdk = VERSION_CODES.O)
    public void shouldSetVisualVoicemailPackage() {
        Shadows.shadowOf(telephonyManager).setVisualVoicemailPackageName("org.foo");
        assertThat(telephonyManager.getVisualVoicemailPackageName()).isEqualTo("org.foo");
    }

    @Test
    @Config(minSdk = VERSION_CODES.P)
    public void canSetAndGetSignalStrength() {
        SignalStrength ss = Shadow.newInstanceOf(SignalStrength.class);
        Shadows.shadowOf(telephonyManager).setSignalStrength(ss);
        assertThat(telephonyManager.getSignalStrength()).isEqualTo(ss);
    }

    @Test
    @Config(minSdk = VERSION_CODES.P)
    public void shouldGiveSignalStrength() {
        PhoneStateListener listener = Mockito.mock(PhoneStateListener.class);
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        SignalStrength ss = Shadow.newInstanceOf(SignalStrength.class);
        Shadows.shadowOf(telephonyManager).setSignalStrength(ss);
        Mockito.verify(listener).onSignalStrengthsChanged(ss);
    }
}

