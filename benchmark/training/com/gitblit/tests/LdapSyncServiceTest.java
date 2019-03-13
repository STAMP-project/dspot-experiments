/**
 * Copyright 2014 gitblit.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gitblit.tests;


import Keys.realm.ldap;
import Keys.realm.ldap.synchronize;
import com.gitblit.service.LdapSyncService;
import com.gitblit.tests.mock.MemorySettings;
import org.junit.Assert;
import org.junit.Test;


/**
 * A behavior driven test for the LdapSyncService with in-memory Settings.
 *
 * @author Alfred Schmid
 */
public class LdapSyncServiceTest {
    private MemorySettings settings;

    @Test
    public void defaultOfUnAvailableLdapSynchronizeKeyIsLdapServiceNotReady() {
        LdapSyncService ldapSyncService = new LdapSyncService(settings, null);
        Assert.assertFalse((("When key " + (ldap.synchronize)) + " is not configured ldap sync is not ready."), ldapSyncService.isReady());
    }

    @Test
    public void whenLdapSynchronizeKeyIsFalseLdapServiceNotReady() {
        LdapSyncService ldapSyncService = new LdapSyncService(settings, null);
        settings.put(synchronize, "false");
        Assert.assertFalse((("When key " + (ldap.synchronize)) + " is configured with value false ldap sync is not ready."), ldapSyncService.isReady());
    }

    @Test
    public void whenLdapSynchronizeKeyIsTrueLdapServiceIsReady() {
        LdapSyncService ldapSyncService = new LdapSyncService(settings, null);
        settings.put(synchronize, "true");
        Assert.assertTrue((("When key " + (ldap.synchronize)) + " is configured with value true ldap sync is not ready."), ldapSyncService.isReady());
    }
}

