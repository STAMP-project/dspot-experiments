/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.text.localizer.taglib.internal.address;


import StringPool.COMMA_AND_SPACE;
import StringPool.NEW_LINE;
import StringPool.SPACE;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Html;
import com.liferay.portal.util.HtmlImpl;
import com.liferay.text.localizer.address.AddressTextLocalizer;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Drew Brokke
 */
public class USAddressTextLocalizerTest {
    @Test
    public void testAllFields() {
        _setCity(USAddressTextLocalizerTest._CITY);
        _setCountry(USAddressTextLocalizerTest._COUNTRY_NAME);
        _setRegion(USAddressTextLocalizerTest._REGION_NAME);
        _setStreets(USAddressTextLocalizerTest._STREET_1, USAddressTextLocalizerTest._STREET_2, USAddressTextLocalizerTest._STREET_3);
        _setZip(USAddressTextLocalizerTest._ZIP);
        Assert.assertEquals(StringBundler.concat(USAddressTextLocalizerTest._STREET_1, NEW_LINE, USAddressTextLocalizerTest._STREET_2, NEW_LINE, USAddressTextLocalizerTest._STREET_3, NEW_LINE, USAddressTextLocalizerTest._CITY, COMMA_AND_SPACE, USAddressTextLocalizerTest._REGION_NAME, SPACE, USAddressTextLocalizerTest._ZIP, NEW_LINE, USAddressTextLocalizerTest._COUNTRY_NAME), _addressTextLocalizer.format(_address));
    }

    @Test
    public void testCountryLine() {
        _setCountry(USAddressTextLocalizerTest._COUNTRY_NAME);
        Assert.assertEquals(USAddressTextLocalizerTest._COUNTRY_NAME, _addressTextLocalizer.format(_address));
    }

    @Test
    public void testRegionLineWithCity() {
        _setCity(USAddressTextLocalizerTest._CITY);
        Assert.assertEquals(USAddressTextLocalizerTest._CITY, _addressTextLocalizer.format(_address));
    }

    @Test
    public void testRegionLineWithCityAndRegionName() {
        _setCity(USAddressTextLocalizerTest._CITY);
        _setRegion(USAddressTextLocalizerTest._REGION_NAME);
        Assert.assertEquals(StringBundler.concat(USAddressTextLocalizerTest._CITY, COMMA_AND_SPACE, USAddressTextLocalizerTest._REGION_NAME), _addressTextLocalizer.format(_address));
    }

    @Test
    public void testRegionLineWithCityAndRegionNameAndZip() {
        _setCity(USAddressTextLocalizerTest._CITY);
        _setRegion(USAddressTextLocalizerTest._REGION_NAME);
        _setZip(USAddressTextLocalizerTest._ZIP);
        Assert.assertEquals(StringBundler.concat(USAddressTextLocalizerTest._CITY, COMMA_AND_SPACE, USAddressTextLocalizerTest._REGION_NAME, SPACE, USAddressTextLocalizerTest._ZIP), _addressTextLocalizer.format(_address));
    }

    @Test
    public void testRegionLineWithCityAndZip() {
        _setCity(USAddressTextLocalizerTest._CITY);
        _setZip(USAddressTextLocalizerTest._ZIP);
        Assert.assertEquals(StringBundler.concat(USAddressTextLocalizerTest._CITY, COMMA_AND_SPACE, USAddressTextLocalizerTest._ZIP), _addressTextLocalizer.format(_address));
    }

    @Test
    public void testRegionLineWithRegionName() {
        _setRegion(USAddressTextLocalizerTest._REGION_NAME);
        Assert.assertEquals(USAddressTextLocalizerTest._REGION_NAME, _addressTextLocalizer.format(_address));
    }

    @Test
    public void testRegionLineWithRegionNameAndZip() {
        _setRegion(USAddressTextLocalizerTest._REGION_NAME);
        _setZip(USAddressTextLocalizerTest._ZIP);
        Assert.assertEquals(StringBundler.concat(USAddressTextLocalizerTest._REGION_NAME, SPACE, USAddressTextLocalizerTest._ZIP), _addressTextLocalizer.format(_address));
    }

    @Test
    public void testRegionLineWithZip() {
        _setZip(USAddressTextLocalizerTest._ZIP);
        Assert.assertEquals(USAddressTextLocalizerTest._ZIP, _addressTextLocalizer.format(_address));
    }

    @Test
    public void testStreetAndCountryLines() {
        _setCountry(USAddressTextLocalizerTest._COUNTRY_NAME);
        _setStreets(USAddressTextLocalizerTest._STREET_1, USAddressTextLocalizerTest._STREET_2, USAddressTextLocalizerTest._STREET_3);
        Assert.assertEquals(StringBundler.concat(USAddressTextLocalizerTest._STREET_1, NEW_LINE, USAddressTextLocalizerTest._STREET_2, NEW_LINE, USAddressTextLocalizerTest._STREET_3, NEW_LINE, USAddressTextLocalizerTest._COUNTRY_NAME), _addressTextLocalizer.format(_address));
    }

    @Test
    public void testStreetLines() {
        _setStreets(USAddressTextLocalizerTest._STREET_1, USAddressTextLocalizerTest._STREET_2, USAddressTextLocalizerTest._STREET_3);
        Assert.assertEquals(StringBundler.concat(USAddressTextLocalizerTest._STREET_1, NEW_LINE, USAddressTextLocalizerTest._STREET_2, NEW_LINE, USAddressTextLocalizerTest._STREET_3), _addressTextLocalizer.format(_address));
    }

    @Test
    public void testWillEscapeFields() {
        String unescapedValue = "<script type=\"text/javascript\">alert(\"Hello World\");</script>";
        _setCity(unescapedValue);
        _setStreets(unescapedValue, unescapedValue, unescapedValue);
        _setZip(unescapedValue);
        _setCountry(unescapedValue);
        _setRegion(unescapedValue);
        Html html = new HtmlImpl();
        String escapedValue = html.escape(unescapedValue);
        Assert.assertEquals(StringBundler.concat(escapedValue, NEW_LINE, escapedValue, NEW_LINE, escapedValue, NEW_LINE, escapedValue, COMMA_AND_SPACE, escapedValue, SPACE, escapedValue, NEW_LINE, escapedValue), _addressTextLocalizer.format(_address));
    }

    private static final String _CITY = RandomTestUtil.randomString();

    private static final String _COUNTRY_NAME = RandomTestUtil.randomString();

    private static final String _REGION_NAME = RandomTestUtil.randomString();

    private static final String _STREET_1 = RandomTestUtil.randomString();

    private static final String _STREET_2 = RandomTestUtil.randomString();

    private static final String _STREET_3 = RandomTestUtil.randomString();

    private static final String _ZIP = RandomTestUtil.randomString();

    private Address _address;

    private final AddressTextLocalizer _addressTextLocalizer = _createAddressTextLocalizer();

    private String _city;

    private Country _country;

    private Region _region;

    private String _street1;

    private String _street2;

    private String _street3;

    private String _zip;
}

