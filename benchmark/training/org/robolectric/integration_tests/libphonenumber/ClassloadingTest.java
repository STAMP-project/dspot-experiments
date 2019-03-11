package org.robolectric.integration_tests.libphonenumber;


import Phonenumber.PhoneNumber;
import com.google.i18n.phonenumbers.Phonenumber;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = Config.ALL_SDKS)
public class ClassloadingTest {
    /**
     * <a href="https://github.com/robolectric/robolectric/issues/2773">Issue</a>
     */
    @Test
    public void getResourceAsStream() throws Exception {
        Phonenumber.PhoneNumber phoneNumber = new Phonenumber.PhoneNumber();
        phoneNumber.setCountryCode(7);
        phoneNumber.setNationalNumber(4956360636L);
        String format = com.google.i18n.phonenumbers.PhoneNumberUtil.getInstance().format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        assertThat(format).isNotNull();
    }
}

