/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.repository.kdr;


import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.UserInfo;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class KettleDatabaseRepositorySecurityProviderTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private KettleDatabaseRepository repository;

    private KettleDatabaseRepositorySecurityProvider provider;

    @Test(expected = KettleException.class)
    public void saveUserInfo_NormalizesInfo_FailsIfStillBreaches() throws Exception {
        provider.saveUserInfo(new UserInfo("    "));
    }

    @Test(expected = KettleException.class)
    public void saveUserInfo_CheckDuplication_FailsIfFoundSame() throws Exception {
        testSaveUserInfo_Passes("login", "login", "login");
    }

    @Test
    public void saveUserInfo_CheckDuplication_PassesIfFoundDifferenceInCase() throws Exception {
        testSaveUserInfo_Passes("login", "login", "LOGIN");
    }

    @Test
    public void saveUserInfo_NormalizesInfo_PassesIfNoViolations() throws Exception {
        testSaveUserInfo_Passes("login    ", "login");
    }

    @Test
    public void saveUserInfo_CheckDuplication_PassesIfFoundNothing() throws Exception {
        testSaveUserInfo_Passes("login", "login");
    }
}

