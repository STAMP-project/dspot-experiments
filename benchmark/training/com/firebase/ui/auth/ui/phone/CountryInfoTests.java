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


import com.firebase.ui.auth.data.model.CountryInfo;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class CountryInfoTests {
    private static final Locale COUNTRY_NAME_US = new Locale("", "US");

    private static final int COUNTRY_CODE_US = 1;

    private static final Locale COUNTRY_NAME_BS = new Locale("", "BS");

    private static final int COUNTRY_CODE_JP = 81;

    @Test
    public void testEquals_differentObject() {
        final CountryInfo countryInfo1 = new CountryInfo(CountryInfoTests.COUNTRY_NAME_US, CountryInfoTests.COUNTRY_CODE_US);
        final CountryInfo countryInfo2 = new CountryInfo(CountryInfoTests.COUNTRY_NAME_US, CountryInfoTests.COUNTRY_CODE_US);
        Assert.assertEquals(countryInfo1, countryInfo2);
        Assert.assertEquals(countryInfo2, countryInfo1);
        Assert.assertEquals(countryInfo1, countryInfo1);
        Assert.assertEquals(countryInfo2, countryInfo2);
        Assert.assertNotSame(countryInfo2, countryInfo1);
    }

    @Test
    public void testEquals_null() {
        final CountryInfo countryInfo = new CountryInfo(CountryInfoTests.COUNTRY_NAME_US, CountryInfoTests.COUNTRY_CODE_US);
        Assert.assertFalse(countryInfo.equals(null));
    }

    @Test
    public void testEquals_differentClass() {
        final CountryInfo countryInfo = new CountryInfo(CountryInfoTests.COUNTRY_NAME_US, CountryInfoTests.COUNTRY_CODE_US);
        Assert.assertFalse(countryInfo.equals(0));
    }

    @Test
    public void testEquals_differentCountryName() {
        final CountryInfo usCountryInfo = new CountryInfo(CountryInfoTests.COUNTRY_NAME_US, CountryInfoTests.COUNTRY_CODE_US);
        final CountryInfo bsCountryInfo = new CountryInfo(CountryInfoTests.COUNTRY_NAME_BS, CountryInfoTests.COUNTRY_CODE_US);
        Assert.assertFalse(usCountryInfo.equals(bsCountryInfo));
    }

    @Test
    public void testEquals_nullCountryName() {
        final CountryInfo usCountryInfo = new CountryInfo(CountryInfoTests.COUNTRY_NAME_US, CountryInfoTests.COUNTRY_CODE_US);
        final CountryInfo bsCountryInfo = new CountryInfo(null, CountryInfoTests.COUNTRY_CODE_US);
        Assert.assertFalse(usCountryInfo.equals(bsCountryInfo));
        Assert.assertFalse(bsCountryInfo.equals(usCountryInfo));
    }

    @Test
    public void testEquals_differentCountryCode() {
        final CountryInfo usCountryInfo = new CountryInfo(CountryInfoTests.COUNTRY_NAME_US, CountryInfoTests.COUNTRY_CODE_US);
        final CountryInfo jpCountryInfo = new CountryInfo(CountryInfoTests.COUNTRY_NAME_US, CountryInfoTests.COUNTRY_CODE_JP);
        Assert.assertFalse(usCountryInfo.equals(jpCountryInfo));
    }

    @Test
    public void testHashCode() {
        final CountryInfo usCountryInfo = new CountryInfo(CountryInfoTests.COUNTRY_NAME_US, CountryInfoTests.COUNTRY_CODE_US);
        final CountryInfo bsCountryInfo = new CountryInfo(null, CountryInfoTests.COUNTRY_CODE_US);
        Assert.assertEquals(2611999, usCountryInfo.hashCode());
        Assert.assertEquals(1, bsCountryInfo.hashCode());
    }

    @Test
    public void testToString() {
        final CountryInfo usCountryInfo = new CountryInfo(CountryInfoTests.COUNTRY_NAME_US, CountryInfoTests.COUNTRY_CODE_US);
        int firstLetter = ('U' - 65) + 127462;
        int secondLetter = ('S' - 65) + 127462;
        String expected = (((((new String(Character.toChars(firstLetter))) + (new String(Character.toChars(secondLetter)))) + " ") + (usCountryInfo.getLocale().getDisplayCountry())) + " +") + (usCountryInfo.getCountryCode());
        Assert.assertEquals(expected, usCountryInfo.toString());
    }
}

