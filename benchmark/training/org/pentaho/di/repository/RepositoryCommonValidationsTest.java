/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.repository;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class RepositoryCommonValidationsTest {
    @Test(expected = NullPointerException.class)
    public void checkUserInfo_Null() {
        RepositoryCommonValidations.checkUserInfo(null);
    }

    @Test
    public void checkUserInfo_LoginIsNull() {
        Assert.assertFalse(RepositoryCommonValidations.checkUserInfo(RepositoryCommonValidationsTest.user(null, "name")));
    }

    @Test
    public void checkUserInfo_LoginIsBlank() {
        Assert.assertFalse(RepositoryCommonValidations.checkUserInfo(RepositoryCommonValidationsTest.user("", "name")));
    }

    @Test
    public void checkUserInfo_LoginContainsSpaces() {
        Assert.assertFalse(RepositoryCommonValidations.checkUserInfo(RepositoryCommonValidationsTest.user("  \t\n ", "name")));
    }

    @Test
    public void checkUserInfo_BothAreMeaningful() {
        Assert.assertTrue(RepositoryCommonValidations.checkUserInfo(RepositoryCommonValidationsTest.user("login", "name")));
    }

    @Test(expected = NullPointerException.class)
    public void normalizeUserInfo_Null() {
        RepositoryCommonValidations.normalizeUserInfo(null);
    }

    @Test
    public void normalizeUserInfo_Valid() {
        IUser normalized = RepositoryCommonValidations.normalizeUserInfo(RepositoryCommonValidationsTest.user("login", "name"));
        Assert.assertEquals("login", normalized.getLogin());
        Assert.assertEquals("login", normalized.getName());
    }

    @Test
    public void normalizeUserInfo_WithSpaces() {
        IUser normalized = RepositoryCommonValidations.normalizeUserInfo(RepositoryCommonValidationsTest.user("  login \t\n ", "name"));
        Assert.assertEquals("login", normalized.getLogin());
        Assert.assertEquals("login", normalized.getName());
    }
}

