/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.realm;


import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * The test will only be executed if the environment variables PAM_USER and PAM_PASS are present.
 * They should contain username and password of an valid system user to make the test pass. The
 * service needs to be configured under /etc/pam.d/sshd to resolve and authenticate the system user.
 *
 * Contains main() function so the test can be executed manually.
 *
 * Set in MacOS to run in IDE(A):
 * $ launchctl setenv PAM_USER user
 * $ launchctl setenv PAM_PASS xxxxx
 */
public class PamRealmTest {
    @Test
    public void testDoGetAuthenticationInfo() {
        PamRealm realm = new PamRealm();
        realm.setService("sshd");
        String pamUser = System.getenv("PAM_USER");
        String pamPass = System.getenv("PAM_PASS");
        Assume.assumeTrue((pamUser != null));
        Assume.assumeTrue((pamPass != null));
        // mock shiro auth token
        UsernamePasswordToken authToken = Mockito.mock(UsernamePasswordToken.class);
        Mockito.when(authToken.getUsername()).thenReturn(pamUser);
        Mockito.when(authToken.getPassword()).thenReturn(pamPass.toCharArray());
        Mockito.when(authToken.getCredentials()).thenReturn(pamPass);
        AuthenticationInfo authInfo = realm.doGetAuthenticationInfo(authToken);
        Assert.assertTrue(((authInfo.getCredentials()) != null));
    }
}

