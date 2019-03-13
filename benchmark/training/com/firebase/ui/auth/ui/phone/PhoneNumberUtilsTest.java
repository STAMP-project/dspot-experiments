/**
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modifications copyright (C) 2017 Google Inc
 */
package com.firebase.ui.auth.ui.phone;


import Context.TELEPHONY_SERVICE;
import android.content.Context;
import android.telephony.TelephonyManager;
import com.firebase.ui.auth.data.model.CountryInfo;
import com.firebase.ui.auth.data.model.PhoneNumber;
import com.firebase.ui.auth.util.data.PhoneNumberUtils;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
public class PhoneNumberUtilsTest {
    @Test
    public void testGetPhoneNumber() {
        final PhoneNumber number = getPhoneNumber(PhoneTestConstants.RAW_PHONE);
        Assert.assertEquals(PhoneTestConstants.PHONE_NO_COUNTRY_CODE, number.getPhoneNumber());
        Assert.assertEquals(PhoneTestConstants.US_COUNTRY_CODE, number.getCountryCode());
        Assert.assertEquals(PhoneTestConstants.US_ISO2, number.getCountryIso());
    }

    @Test
    public void testGetPhoneNumber_withLongestCountryCode() {
        final PhoneNumber phoneNumber = getPhoneNumber(PhoneTestConstants.YE_RAW_PHONE);
        Assert.assertEquals(PhoneTestConstants.PHONE_NO_COUNTRY_CODE, phoneNumber.getPhoneNumber());
        Assert.assertEquals(PhoneTestConstants.YE_COUNTRY_CODE, phoneNumber.getCountryCode());
        Assert.assertEquals(PhoneTestConstants.YE_ISO2, phoneNumber.getCountryIso());
    }

    @Test
    public void testGetPhoneNumber_withPhoneWithoutPlusSign() {
        final PhoneNumber phoneNumber = getPhoneNumber(PhoneTestConstants.PHONE);
        Assert.assertEquals(PhoneTestConstants.PHONE, phoneNumber.getPhoneNumber());
        Assert.assertEquals(PhoneTestConstants.US_COUNTRY_CODE, phoneNumber.getCountryCode());
        Assert.assertEquals(PhoneTestConstants.US_ISO2, phoneNumber.getCountryIso());
    }

    @Test
    public void testGetPhoneNumber_noCountryCode() {
        final PhoneNumber number = getPhoneNumber(("0" + (PhoneTestConstants.PHONE_NO_COUNTRY_CODE)));
        Assert.assertEquals(("0" + (PhoneTestConstants.PHONE_NO_COUNTRY_CODE)), number.getPhoneNumber());
        Assert.assertEquals(PhoneTestConstants.US_COUNTRY_CODE, number.getCountryCode());
        Assert.assertEquals(PhoneTestConstants.US_ISO2, number.getCountryIso());
    }

    @Test
    public void testGetCountryCode() {
        Assert.assertEquals(Integer.valueOf(86), getCountryCode(Locale.CHINA.getCountry()));
        Assert.assertEquals(null, getCountryCode(null));
        Assert.assertEquals(null, getCountryCode(new Locale("", "DJJZ").getCountry()));
    }

    @Test
    @Config(sdk = 16)
    public void testFormatNumberToE164_belowApi21() {
        String validPhoneNumber = "+919994947354";
        CountryInfo indiaCountryInfo = new CountryInfo(new Locale("", "IN"), 91);
        // no leading plus
        Assert.assertEquals(validPhoneNumber, format("9994947354", indiaCountryInfo));
        // fully formatted
        Assert.assertEquals(validPhoneNumber, format("+919994947354", indiaCountryInfo));
        // parantheses and hyphens
        Assert.assertEquals(validPhoneNumber, format("(99949) 47-354", indiaCountryInfo));
        // The following cases would fail for lower api versions.
        // Leaving tests in place to formally identify cases
        // no leading +
        // assertEquals(validPhoneNumber, format("919994947354", indiaCountryInfo));
        // with hyphens
        // assertEquals(validPhoneNumber, format("+91-(999)-(49)-(47354)",
        // indiaCountryInfo));
        // with spaces leading plus
        // assertEquals(validPhoneNumber, format("+91 99949 47354", indiaCountryInfo));
        // space formatting
        // assertEquals(validPhoneNumber, format("91 99949 47354", indiaCountryInfo));
        // invalid phone number
        // assertNull(format("999474735", indiaCountryInfo));
    }

    @Test
    public void testGetCurrentCountryInfo_fromSim() {
        Context context = Mockito.mock(Context.class);
        TelephonyManager telephonyManager = Mockito.mock(TelephonyManager.class);
        Mockito.when(context.getSystemService(TELEPHONY_SERVICE)).thenReturn(telephonyManager);
        Mockito.when(telephonyManager.getSimCountryIso()).thenReturn("IN");
        Assert.assertEquals(new CountryInfo(new Locale("", "IN"), 91), getCurrentCountryInfo(context));
    }

    @Test
    public void testGetCurrentCountryInfo_noTelephonyReturnsDefaultLocale() {
        Context context = Mockito.mock(Context.class);
        Assert.assertEquals(new CountryInfo(Locale.getDefault(), PhoneNumberUtils.getCountryCode(Locale.getDefault().getCountry())), getCurrentCountryInfo(context));
    }
}

