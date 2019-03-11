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
package com.liferay.users.admin.internal.user.initials.generator;


import StringPool.BLANK;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.users.admin.kernel.util.UserInitialsGenerator;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Drew Brokke
 */
public class UserInitialsGeneratorImplTest {
    @Test
    public void testFirstLast() throws Exception {
        _setUpLanguageUtil("first-name,last-name");
        Assert.assertEquals("FL", _userInitialsGenerator.getInitials(UserInitialsGeneratorImplTest._LOCALE, UserInitialsGeneratorImplTest._FIRST_NAME, UserInitialsGeneratorImplTest._MIDDLE_NAME, UserInitialsGeneratorImplTest._LAST_NAME));
        Assert.assertEquals("F", _userInitialsGenerator.getInitials(UserInitialsGeneratorImplTest._LOCALE, UserInitialsGeneratorImplTest._FIRST_NAME, UserInitialsGeneratorImplTest._MIDDLE_NAME, null));
        Assert.assertEquals("L", _userInitialsGenerator.getInitials(UserInitialsGeneratorImplTest._LOCALE, null, UserInitialsGeneratorImplTest._MIDDLE_NAME, UserInitialsGeneratorImplTest._LAST_NAME));
        Assert.assertEquals(BLANK, _userInitialsGenerator.getInitials(UserInitialsGeneratorImplTest._LOCALE, null, UserInitialsGeneratorImplTest._MIDDLE_NAME, null));
    }

    @Test
    public void testFirstMiddleLast() throws Exception {
        _setUpLanguageUtil("first-name,middle-name,last-name");
        Assert.assertEquals("FM", _userInitialsGenerator.getInitials(UserInitialsGeneratorImplTest._LOCALE, UserInitialsGeneratorImplTest._FIRST_NAME, UserInitialsGeneratorImplTest._MIDDLE_NAME, UserInitialsGeneratorImplTest._LAST_NAME));
        Assert.assertEquals("FM", _userInitialsGenerator.getInitials(UserInitialsGeneratorImplTest._LOCALE, UserInitialsGeneratorImplTest._FIRST_NAME, UserInitialsGeneratorImplTest._MIDDLE_NAME, null));
        Assert.assertEquals("ML", _userInitialsGenerator.getInitials(UserInitialsGeneratorImplTest._LOCALE, null, UserInitialsGeneratorImplTest._MIDDLE_NAME, UserInitialsGeneratorImplTest._LAST_NAME));
        Assert.assertEquals("FL", _userInitialsGenerator.getInitials(UserInitialsGeneratorImplTest._LOCALE, UserInitialsGeneratorImplTest._FIRST_NAME, null, UserInitialsGeneratorImplTest._LAST_NAME));
        Assert.assertEquals("F", _userInitialsGenerator.getInitials(UserInitialsGeneratorImplTest._LOCALE, UserInitialsGeneratorImplTest._FIRST_NAME, null, null));
        Assert.assertEquals("M", _userInitialsGenerator.getInitials(UserInitialsGeneratorImplTest._LOCALE, null, UserInitialsGeneratorImplTest._MIDDLE_NAME, null));
        Assert.assertEquals("L", _userInitialsGenerator.getInitials(UserInitialsGeneratorImplTest._LOCALE, null, null, UserInitialsGeneratorImplTest._LAST_NAME));
    }

    @Test
    public void testLastFirst() throws Exception {
        _setUpLanguageUtil("last-name,first-name");
        Assert.assertEquals("LF", _userInitialsGenerator.getInitials(UserInitialsGeneratorImplTest._LOCALE, UserInitialsGeneratorImplTest._FIRST_NAME, UserInitialsGeneratorImplTest._MIDDLE_NAME, UserInitialsGeneratorImplTest._LAST_NAME));
        Assert.assertEquals("F", _userInitialsGenerator.getInitials(UserInitialsGeneratorImplTest._LOCALE, UserInitialsGeneratorImplTest._FIRST_NAME, UserInitialsGeneratorImplTest._MIDDLE_NAME, null));
        Assert.assertEquals("L", _userInitialsGenerator.getInitials(UserInitialsGeneratorImplTest._LOCALE, null, UserInitialsGeneratorImplTest._MIDDLE_NAME, UserInitialsGeneratorImplTest._LAST_NAME));
        Assert.assertEquals(BLANK, _userInitialsGenerator.getInitials(UserInitialsGeneratorImplTest._LOCALE, null, UserInitialsGeneratorImplTest._MIDDLE_NAME, null));
    }

    @Test
    public void testLastFirstMiddle() throws Exception {
        _setUpLanguageUtil("last-name,first-name,middle-name");
        Assert.assertEquals("LF", _userInitialsGenerator.getInitials(UserInitialsGeneratorImplTest._LOCALE, UserInitialsGeneratorImplTest._FIRST_NAME, UserInitialsGeneratorImplTest._MIDDLE_NAME, UserInitialsGeneratorImplTest._LAST_NAME));
        Assert.assertEquals("FM", _userInitialsGenerator.getInitials(UserInitialsGeneratorImplTest._LOCALE, UserInitialsGeneratorImplTest._FIRST_NAME, UserInitialsGeneratorImplTest._MIDDLE_NAME, null));
        Assert.assertEquals("LM", _userInitialsGenerator.getInitials(UserInitialsGeneratorImplTest._LOCALE, null, UserInitialsGeneratorImplTest._MIDDLE_NAME, UserInitialsGeneratorImplTest._LAST_NAME));
        Assert.assertEquals("LF", _userInitialsGenerator.getInitials(UserInitialsGeneratorImplTest._LOCALE, UserInitialsGeneratorImplTest._FIRST_NAME, null, UserInitialsGeneratorImplTest._LAST_NAME));
        Assert.assertEquals("F", _userInitialsGenerator.getInitials(UserInitialsGeneratorImplTest._LOCALE, UserInitialsGeneratorImplTest._FIRST_NAME, null, null));
        Assert.assertEquals("M", _userInitialsGenerator.getInitials(UserInitialsGeneratorImplTest._LOCALE, null, UserInitialsGeneratorImplTest._MIDDLE_NAME, null));
        Assert.assertEquals("L", _userInitialsGenerator.getInitials(UserInitialsGeneratorImplTest._LOCALE, null, null, UserInitialsGeneratorImplTest._LAST_NAME));
    }

    @Test
    public void testNoPropertiesReturnedUsesDefaultValues() throws Exception {
        _setUpLanguageUtil(BLANK);
        Assert.assertEquals("FL", _userInitialsGenerator.getInitials(UserInitialsGeneratorImplTest._LOCALE, UserInitialsGeneratorImplTest._FIRST_NAME, UserInitialsGeneratorImplTest._MIDDLE_NAME, UserInitialsGeneratorImplTest._LAST_NAME));
        Assert.assertEquals("F", _userInitialsGenerator.getInitials(UserInitialsGeneratorImplTest._LOCALE, UserInitialsGeneratorImplTest._FIRST_NAME, UserInitialsGeneratorImplTest._MIDDLE_NAME, null));
        Assert.assertEquals("L", _userInitialsGenerator.getInitials(UserInitialsGeneratorImplTest._LOCALE, null, UserInitialsGeneratorImplTest._MIDDLE_NAME, UserInitialsGeneratorImplTest._LAST_NAME));
        Assert.assertEquals(BLANK, _userInitialsGenerator.getInitials(UserInitialsGeneratorImplTest._LOCALE, null, UserInitialsGeneratorImplTest._MIDDLE_NAME, null));
    }

    private static final String _FIRST_NAME = "First";

    private static final String _LAST_NAME = "Last";

    private static final Locale _LOCALE = LocaleUtil.ENGLISH;

    private static final String _MIDDLE_NAME = "Middle";

    private final UserInitialsGenerator _userInitialsGenerator = new UserInitialsGeneratorImpl();
}

