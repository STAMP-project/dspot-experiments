package org.robolectric.shadows;


import android.os.Build.VERSION_CODES;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;


@RunWith(AndroidJUnit4.class)
@Config(minSdk = VERSION_CODES.LOLLIPOP)
public class ShadowTelecomManagerTest {
    private TelecomManager telecomService;

    @Test
    public void getSimCallManager() {
        PhoneAccountHandle handle = ShadowTelecomManagerTest.createHandle("id");
        Shadows.shadowOf(telecomService).setSimCallManager(handle);
        assertThat(telecomService.getConnectionManager().getId()).isEqualTo("id");
    }

    @Test
    public void registerAndUnRegister() {
        assertThat(Shadows.shadowOf(telecomService).getAllPhoneAccountsCount()).isEqualTo(0);
        assertThat(Shadows.shadowOf(telecomService).getAllPhoneAccounts()).hasSize(0);
        PhoneAccountHandle handler = ShadowTelecomManagerTest.createHandle("id");
        PhoneAccount phoneAccount = PhoneAccount.builder(handler, "main_account").build();
        telecomService.registerPhoneAccount(phoneAccount);
        assertThat(Shadows.shadowOf(telecomService).getAllPhoneAccountsCount()).isEqualTo(1);
        assertThat(Shadows.shadowOf(telecomService).getAllPhoneAccounts()).hasSize(1);
        assertThat(telecomService.getAllPhoneAccountHandles()).hasSize(1);
        assertThat(telecomService.getAllPhoneAccountHandles()).contains(handler);
        assertThat(telecomService.getPhoneAccount(handler).getLabel()).isEqualTo(phoneAccount.getLabel());
        telecomService.unregisterPhoneAccount(handler);
        assertThat(Shadows.shadowOf(telecomService).getAllPhoneAccountsCount()).isEqualTo(0);
        assertThat(Shadows.shadowOf(telecomService).getAllPhoneAccounts()).hasSize(0);
        assertThat(telecomService.getAllPhoneAccountHandles()).hasSize(0);
    }

    @Test
    public void clearAccounts() {
        PhoneAccountHandle anotherPackageHandle = ShadowTelecomManagerTest.createHandle("some.other.package", "id");
        telecomService.registerPhoneAccount(PhoneAccount.builder(anotherPackageHandle, "another_package").build());
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP_MR1)
    public void clearAccountsForPackage() {
        PhoneAccountHandle accountHandle1 = ShadowTelecomManagerTest.createHandle("a.package", "id1");
        telecomService.registerPhoneAccount(PhoneAccount.builder(accountHandle1, "another_package").build());
        PhoneAccountHandle accountHandle2 = ShadowTelecomManagerTest.createHandle("some.other.package", "id2");
        telecomService.registerPhoneAccount(PhoneAccount.builder(accountHandle2, "another_package").build());
        telecomService.clearAccountsForPackage(accountHandle1.getComponentName().getPackageName());
        assertThat(telecomService.getPhoneAccount(accountHandle1)).isNull();
        assertThat(telecomService.getPhoneAccount(accountHandle2)).isNotNull();
    }

    @Test
    public void getPhoneAccountsSupportingScheme() {
        PhoneAccountHandle handleMatchingScheme = ShadowTelecomManagerTest.createHandle("id1");
        telecomService.registerPhoneAccount(PhoneAccount.builder(handleMatchingScheme, "some_scheme").addSupportedUriScheme("some_scheme").build());
        PhoneAccountHandle handleNotMatchingScheme = ShadowTelecomManagerTest.createHandle("id2");
        telecomService.registerPhoneAccount(PhoneAccount.builder(handleNotMatchingScheme, "another_scheme").addSupportedUriScheme("another_scheme").build());
        List<PhoneAccountHandle> actual = telecomService.getPhoneAccountsSupportingScheme("some_scheme");
        assertThat(actual).contains(handleMatchingScheme);
        assertThat(actual).doesNotContain(handleNotMatchingScheme);
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void getCallCapablePhoneAccounts() {
        PhoneAccountHandle callCapableHandle = ShadowTelecomManagerTest.createHandle("id1");
        telecomService.registerPhoneAccount(PhoneAccount.builder(callCapableHandle, "enabled").setIsEnabled(true).build());
        PhoneAccountHandle notCallCapableHandler = ShadowTelecomManagerTest.createHandle("id2");
        telecomService.registerPhoneAccount(PhoneAccount.builder(notCallCapableHandler, "disabled").setIsEnabled(false).build());
        List<PhoneAccountHandle> callCapablePhoneAccounts = telecomService.getCallCapablePhoneAccounts();
        assertThat(callCapablePhoneAccounts).contains(callCapableHandle);
        assertThat(callCapablePhoneAccounts).doesNotContain(notCallCapableHandler);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP_MR1)
    public void getPhoneAccountsForPackage() {
        PhoneAccountHandle handleInThisApplicationsPackage = ShadowTelecomManagerTest.createHandle("id1");
        telecomService.registerPhoneAccount(PhoneAccount.builder(handleInThisApplicationsPackage, "this_package").build());
        PhoneAccountHandle anotherPackageHandle = ShadowTelecomManagerTest.createHandle("some.other.package", "id2");
        telecomService.registerPhoneAccount(PhoneAccount.builder(anotherPackageHandle, "another_package").build());
        List<PhoneAccountHandle> phoneAccountsForPackage = telecomService.getPhoneAccountsForPackage();
        assertThat(phoneAccountsForPackage).contains(handleInThisApplicationsPackage);
        assertThat(phoneAccountsForPackage).doesNotContain(anotherPackageHandle);
    }

    @Test
    public void testAddNewIncomingCall() {
        telecomService.addNewIncomingCall(ShadowTelecomManagerTest.createHandle("id"), null);
        assertThat(Shadows.shadowOf(telecomService).getAllIncomingCalls()).hasSize(1);
    }

    @Test
    public void testAddUnknownCall() {
        telecomService.addNewUnknownCall(ShadowTelecomManagerTest.createHandle("id"), null);
        assertThat(Shadows.shadowOf(telecomService).getAllUnknownCalls()).hasSize(1);
    }

    @Test
    public void testIsRinging_noIncomingOrUnknownCallsAdded_shouldBeFalse() {
        assertThat(Shadows.shadowOf(telecomService).isRinging()).isFalse();
    }

    @Test
    public void testIsRinging_incomingCallAdded_shouldBeTrue() {
        telecomService.addNewIncomingCall(ShadowTelecomManagerTest.createHandle("id"), null);
        assertThat(Shadows.shadowOf(telecomService).isRinging()).isTrue();
    }

    @Test
    public void testIsRinging_unknownCallAdded_shouldBeTrue() {
        Shadows.shadowOf(telecomService).addNewUnknownCall(ShadowTelecomManagerTest.createHandle("id"), null);
        assertThat(Shadows.shadowOf(telecomService).isRinging()).isTrue();
    }

    @Test
    public void testIsRinging_incomingCallAdded_thenRingerSilenced_shouldBeFalse() {
        telecomService.addNewIncomingCall(ShadowTelecomManagerTest.createHandle("id"), null);
        telecomService.silenceRinger();
        assertThat(Shadows.shadowOf(telecomService).isRinging()).isFalse();
    }

    @Test
    public void testIsRinging_unknownCallAdded_thenRingerSilenced_shouldBeFalse() {
        Shadows.shadowOf(telecomService).addNewUnknownCall(ShadowTelecomManagerTest.createHandle("id"), null);
        telecomService.silenceRinger();
        assertThat(Shadows.shadowOf(telecomService).isRinging()).isFalse();
    }

    @Test
    public void testIsRinging_ringerSilenced_thenIncomingCallAdded_shouldBeTrue() {
        telecomService.silenceRinger();
        telecomService.addNewIncomingCall(ShadowTelecomManagerTest.createHandle("id"), null);
        assertThat(Shadows.shadowOf(telecomService).isRinging()).isTrue();
    }

    @Test
    public void testIsRinging_ringerSilenced_thenUnknownCallAdded_shouldBeTrue() {
        telecomService.silenceRinger();
        Shadows.shadowOf(telecomService).addNewUnknownCall(ShadowTelecomManagerTest.createHandle("id"), null);
        assertThat(Shadows.shadowOf(telecomService).isRinging()).isTrue();
    }

    @Test
    @Config(minSdk = VERSION_CODES.M)
    public void setDefaultDialerPackage() {
        Shadows.shadowOf(telecomService).setDefaultDialer("some.package");
        assertThat(telecomService.getDefaultDialerPackage()).isEqualTo("some.package");
    }

    @Test
    public void canSetAndGetIsInCall() throws Exception {
        Shadows.shadowOf(telecomService).setIsInCall(true);
        assertThat(telecomService.isInCall()).isTrue();
    }

    @Test
    public void isInCall_setIsInCallNotCalled_shouldReturnFalse() throws Exception {
        assertThat(telecomService.isInCall()).isFalse();
    }
}

