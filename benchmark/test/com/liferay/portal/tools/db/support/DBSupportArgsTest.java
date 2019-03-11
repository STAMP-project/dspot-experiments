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
package com.liferay.portal.tools.db.support;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Di Giorgi
 */
public class DBSupportArgsTest {
    @Test
    public void testGet() throws Exception {
        DBSupportArgs dbSupportArgs = new DBSupportArgs();
        dbSupportArgs.setPassword(DBSupportArgsTest._CUSTOM_PASSWORD);
        dbSupportArgs.setUrl(DBSupportArgsTest._CUSTOM_URL);
        dbSupportArgs.setUserName(DBSupportArgsTest._CUSTOM_USER_NAME);
        Assert.assertEquals(DBSupportArgsTest._CUSTOM_PASSWORD, dbSupportArgs.getPassword());
        Assert.assertEquals(DBSupportArgsTest._CUSTOM_URL, dbSupportArgs.getUrl());
        Assert.assertEquals(DBSupportArgsTest._CUSTOM_USER_NAME, dbSupportArgs.getUserName());
        dbSupportArgs.setPropertiesFile(DBSupportArgsTest._getFile("portal-ext-one.properties"));
        Assert.assertEquals("password", dbSupportArgs.getPassword());
        Assert.assertEquals("url", dbSupportArgs.getUrl());
        Assert.assertEquals("user_name", dbSupportArgs.getUserName());
        dbSupportArgs.setPropertiesFile(DBSupportArgsTest._getFile("portal-ext-two.properties"));
        Assert.assertEquals(DBSupportArgsTest._CUSTOM_PASSWORD, dbSupportArgs.getPassword());
        Assert.assertEquals("url", dbSupportArgs.getUrl());
        Assert.assertEquals(DBSupportArgsTest._CUSTOM_USER_NAME, dbSupportArgs.getUserName());
        dbSupportArgs.setPropertiesFile(null);
        Assert.assertEquals(DBSupportArgsTest._CUSTOM_PASSWORD, dbSupportArgs.getPassword());
        Assert.assertEquals(DBSupportArgsTest._CUSTOM_URL, dbSupportArgs.getUrl());
        Assert.assertEquals(DBSupportArgsTest._CUSTOM_USER_NAME, dbSupportArgs.getUserName());
    }

    private static final String _CUSTOM_PASSWORD = "custom_password";

    private static final String _CUSTOM_URL = "custom_url";

    private static final String _CUSTOM_USER_NAME = "custom_user_name";
}

