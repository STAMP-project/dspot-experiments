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
package com.liferay.portal.search.elasticsearch6.internal.index;


import java.util.Date;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;


/**
 *
 *
 * @author Bryan Engler
 */
public class LiferayTypeMappingsModifiedDateFieldTest {
    @Test
    public void testDate() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Invalid format: \"1970-01-18T12:08:26.556Z\" is malformed at " + "\"-01-18T12:08:26.556Z\""));
        index(new Date(1512506556L));
    }

    @Test
    public void testLong() throws Exception {
        index(20171115050402L);
        _liferayIndexFixture.assertType("modified", "date");
    }

    @Test
    public void testLongMalformed() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid format: \"1512506556\" is too short");
        index(1512506556L);
    }

    @Test
    public void testString() throws Exception {
        index("20171115050402");
        _liferayIndexFixture.assertType("modified", "date");
    }

    @Test
    public void testStringMalformed() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Invalid format: \"2017-11-15 05:04:02\" is malformed at " + "\"-11-15 05:04:02\""));
        index("2017-11-15 05:04:02");
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public TestName testName = new TestName();

    private LiferayIndexFixture _liferayIndexFixture;
}

