/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.security.krb;


import java.io.File;
import java.security.Principal;
import java.security.PrivilegedExceptionAction;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.LoginException;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.ProcessContext;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;


public class KerberosUserIT {
    @ClassRule
    public static TemporaryFolder tmpDir = new TemporaryFolder();

    private static KDCServer kdc;

    private static KerberosPrincipal principal1;

    private static File principal1KeytabFile;

    private static KerberosPrincipal principal2;

    private static File principal2KeytabFile;

    private static KerberosPrincipal principal3;

    private static final String principal3Password = "changeme";

    @Test
    public void testKeytabUserSuccessfulLoginAndLogout() throws LoginException {
        // perform login for user1
        final KerberosUser user1 = new KerberosKeytabUser(KerberosUserIT.principal1.getName(), KerberosUserIT.principal1KeytabFile.getAbsolutePath());
        user1.login();
        // perform login for user2
        final KerberosUser user2 = new KerberosKeytabUser(KerberosUserIT.principal2.getName(), KerberosUserIT.principal2KeytabFile.getAbsolutePath());
        user2.login();
        // verify user1 Subject only has user1 principal
        final Subject user1Subject = getSubject();
        final Set<Principal> user1SubjectPrincipals = user1Subject.getPrincipals();
        Assert.assertEquals(1, user1SubjectPrincipals.size());
        Assert.assertEquals(KerberosUserIT.principal1.getName(), user1SubjectPrincipals.iterator().next().getName());
        // verify user2 Subject only has user2 principal
        final Subject user2Subject = getSubject();
        final Set<Principal> user2SubjectPrincipals = user2Subject.getPrincipals();
        Assert.assertEquals(1, user2SubjectPrincipals.size());
        Assert.assertEquals(KerberosUserIT.principal2.getName(), user2SubjectPrincipals.iterator().next().getName());
        // call check/relogin and verify neither user performed a relogin
        Assert.assertFalse(user1.checkTGTAndRelogin());
        Assert.assertFalse(user2.checkTGTAndRelogin());
        // perform logout for both users
        user1.logout();
        user2.logout();
        // verify subjects have no more principals
        Assert.assertEquals(0, user1Subject.getPrincipals().size());
        Assert.assertEquals(0, user2Subject.getPrincipals().size());
    }

    @Test
    public void testKeytabLoginWithUnknownPrincipal() throws LoginException {
        final String unknownPrincipal = "doesnotexist@" + (KerberosUserIT.kdc.getRealm());
        final KerberosUser user1 = new KerberosKeytabUser(unknownPrincipal, KerberosUserIT.principal1KeytabFile.getAbsolutePath());
        try {
            user1.login();
            Assert.fail("Login should have failed");
        } catch (Exception e) {
            // exception is expected here
            // e.printStackTrace();
        }
    }

    @Test
    public void testPasswordUserSuccessfulLoginAndLogout() throws LoginException {
        // perform login for user
        final KerberosUser user = new KerberosPasswordUser(KerberosUserIT.principal3.getName(), KerberosUserIT.principal3Password);
        user.login();
        // verify user Subject only has user principal
        final Subject userSubject = getSubject();
        final Set<Principal> userSubjectPrincipals = userSubject.getPrincipals();
        Assert.assertEquals(1, userSubjectPrincipals.size());
        Assert.assertEquals(KerberosUserIT.principal3.getName(), userSubjectPrincipals.iterator().next().getName());
        // call check/relogin and verify neither user performed a relogin
        Assert.assertFalse(user.checkTGTAndRelogin());
        // perform logout for both users
        user.logout();
        // verify subjects have no more principals
        Assert.assertEquals(0, userSubject.getPrincipals().size());
    }

    @Test(expected = LoginException.class)
    public void testPasswordUserLoginWithInvalidPassword() throws LoginException {
        // perform login for user
        final KerberosUser user = new KerberosPasswordUser("user3", "NOT THE PASSWORD");
        user.login();
    }

    @Test
    public void testCheckTGTAndRelogin() throws InterruptedException, LoginException {
        final KerberosUser user1 = new KerberosKeytabUser(KerberosUserIT.principal1.getName(), KerberosUserIT.principal1KeytabFile.getAbsolutePath());
        user1.login();
        // Since we set the lifetime to 15 seconds we should hit a relogin before 15 attempts
        boolean performedRelogin = false;
        for (int i = 0; i < 30; i++) {
            Thread.sleep(1000);
            System.out.println(("checkTGTAndRelogin #" + i));
            performedRelogin = user1.checkTGTAndRelogin();
            if (performedRelogin) {
                System.out.println("Performed relogin!");
                break;
            }
        }
        Assert.assertEquals(true, performedRelogin);
    }

    @Test
    public void testKeytabAction() {
        final KerberosUser user1 = new KerberosKeytabUser(KerberosUserIT.principal1.getName(), KerberosUserIT.principal1KeytabFile.getAbsolutePath());
        final AtomicReference<String> resultHolder = new AtomicReference<>(null);
        final PrivilegedExceptionAction<Void> privilegedAction = () -> {
            resultHolder.set("SUCCESS");
            return null;
        };
        final ProcessContext context = Mockito.mock(ProcessContext.class);
        final ComponentLog logger = Mockito.mock(ComponentLog.class);
        // create the action to test and execute it
        final KerberosAction kerberosAction = new KerberosAction(user1, privilegedAction, logger);
        kerberosAction.execute();
        // if the result holder has the string success then we know the action executed
        Assert.assertEquals("SUCCESS", resultHolder.get());
    }
}

