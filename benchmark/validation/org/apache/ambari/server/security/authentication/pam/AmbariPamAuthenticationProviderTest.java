/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.security.authentication.pam;


import ClientSecurityType.LOCAL;
import com.google.inject.Injector;
import junit.framework.Assert;
import org.apache.ambari.server.configuration.Configuration;
import org.apache.ambari.server.orm.entities.UserEntity;
import org.apache.ambari.server.security.authentication.AccountDisabledException;
import org.apache.ambari.server.security.authentication.AmbariUserAuthentication;
import org.apache.ambari.server.security.authentication.TooManyLoginFailuresException;
import org.apache.ambari.server.security.authorization.Users;
import org.easymock.EasyMockSupport;
import org.junit.Test;
import org.jvnet.libpam.PAM;
import org.jvnet.libpam.PAMException;
import org.jvnet.libpam.UnixUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;


public class AmbariPamAuthenticationProviderTest extends EasyMockSupport {
    private static final String TEST_USER_NAME = "userName";

    private static final String TEST_USER_PASS = "userPass";

    private static final String TEST_USER_INCORRECT_PASS = "userIncorrectPass";

    private Injector injector;

    @Test(expected = AuthenticationException.class)
    public void testBadCredential() throws Exception {
        PAM pam = createMock(PAM.class);
        expect(pam.authenticate(eq(AmbariPamAuthenticationProviderTest.TEST_USER_NAME), eq(AmbariPamAuthenticationProviderTest.TEST_USER_INCORRECT_PASS))).andThrow(new PAMException()).once();
        pam.dispose();
        expectLastCall().once();
        PamAuthenticationFactory pamAuthenticationFactory = injector.getInstance(PamAuthenticationFactory.class);
        expect(pamAuthenticationFactory.createInstance(injector.getInstance(Configuration.class))).andReturn(pam).once();
        Users users = injector.getInstance(Users.class);
        expect(users.getUserEntity(AmbariPamAuthenticationProviderTest.TEST_USER_NAME)).andReturn(null).once();
        replayAll();
        Authentication authentication = new UsernamePasswordAuthenticationToken(AmbariPamAuthenticationProviderTest.TEST_USER_NAME, AmbariPamAuthenticationProviderTest.TEST_USER_INCORRECT_PASS);
        AmbariPamAuthenticationProvider authenticationProvider = injector.getInstance(AmbariPamAuthenticationProvider.class);
        authenticationProvider.authenticate(authentication);
        verifyAll();
    }

    @Test
    public void testAuthenticateExistingUser() throws Exception {
        UnixUser unixUser = createNiceMock(UnixUser.class);
        PAM pam = createMock(PAM.class);
        expect(pam.authenticate(eq(AmbariPamAuthenticationProviderTest.TEST_USER_NAME), eq(AmbariPamAuthenticationProviderTest.TEST_USER_PASS))).andReturn(unixUser).once();
        pam.dispose();
        expectLastCall().once();
        PamAuthenticationFactory pamAuthenticationFactory = injector.getInstance(PamAuthenticationFactory.class);
        expect(pamAuthenticationFactory.createInstance(injector.getInstance(Configuration.class))).andReturn(pam).once();
        UserEntity userEntity = combineUserEntity(true, true, 0);
        Users users = injector.getInstance(Users.class);
        expect(users.getUserEntity(AmbariPamAuthenticationProviderTest.TEST_USER_NAME)).andReturn(userEntity).once();
        expect(users.getUser(userEntity)).andReturn(new org.apache.ambari.server.security.authorization.User(userEntity)).once();
        expect(users.getUserAuthorities(userEntity)).andReturn(null).once();
        replayAll();
        Authentication authentication = new UsernamePasswordAuthenticationToken(AmbariPamAuthenticationProviderTest.TEST_USER_NAME, AmbariPamAuthenticationProviderTest.TEST_USER_PASS);
        AmbariPamAuthenticationProvider authenticationProvider = injector.getInstance(AmbariPamAuthenticationProvider.class);
        Authentication result = authenticationProvider.authenticate(authentication);
        Assert.assertNotNull(result);
        Assert.assertEquals(true, result.isAuthenticated());
        Assert.assertTrue((result instanceof AmbariUserAuthentication));
        verifyAll();
    }

    @Test(expected = AccountDisabledException.class)
    public void testAuthenticateDisabledUser() throws Exception {
        UnixUser unixUser = createNiceMock(UnixUser.class);
        PAM pam = createMock(PAM.class);
        expect(pam.authenticate(eq(AmbariPamAuthenticationProviderTest.TEST_USER_NAME), eq(AmbariPamAuthenticationProviderTest.TEST_USER_PASS))).andReturn(unixUser).once();
        pam.dispose();
        expectLastCall().once();
        PamAuthenticationFactory pamAuthenticationFactory = injector.getInstance(PamAuthenticationFactory.class);
        expect(pamAuthenticationFactory.createInstance(injector.getInstance(Configuration.class))).andReturn(pam).once();
        UserEntity userEntity = combineUserEntity(true, false, 0);
        Users users = injector.getInstance(Users.class);
        expect(users.getUserEntity(AmbariPamAuthenticationProviderTest.TEST_USER_NAME)).andReturn(userEntity).once();
        replayAll();
        Authentication authentication = new UsernamePasswordAuthenticationToken(AmbariPamAuthenticationProviderTest.TEST_USER_NAME, AmbariPamAuthenticationProviderTest.TEST_USER_PASS);
        AmbariPamAuthenticationProvider authenticationProvider = injector.getInstance(AmbariPamAuthenticationProvider.class);
        authenticationProvider.authenticate(authentication);
        verifyAll();
    }

    @Test(expected = TooManyLoginFailuresException.class)
    public void testAuthenticateLockedUser() throws Exception {
        UnixUser unixUser = createNiceMock(UnixUser.class);
        PAM pam = createMock(PAM.class);
        expect(pam.authenticate(eq(AmbariPamAuthenticationProviderTest.TEST_USER_NAME), eq(AmbariPamAuthenticationProviderTest.TEST_USER_PASS))).andReturn(unixUser).once();
        pam.dispose();
        expectLastCall().once();
        PamAuthenticationFactory pamAuthenticationFactory = injector.getInstance(PamAuthenticationFactory.class);
        expect(pamAuthenticationFactory.createInstance(injector.getInstance(Configuration.class))).andReturn(pam).once();
        UserEntity userEntity = combineUserEntity(true, true, 11);
        Users users = injector.getInstance(Users.class);
        expect(users.getUserEntity(AmbariPamAuthenticationProviderTest.TEST_USER_NAME)).andReturn(userEntity).once();
        replayAll();
        Authentication authentication = new UsernamePasswordAuthenticationToken(AmbariPamAuthenticationProviderTest.TEST_USER_NAME, AmbariPamAuthenticationProviderTest.TEST_USER_PASS);
        AmbariPamAuthenticationProvider authenticationProvider = injector.getInstance(AmbariPamAuthenticationProvider.class);
        authenticationProvider.authenticate(authentication);
        verifyAll();
    }

    @Test
    public void testAuthenticateNewUser() throws Exception {
        UnixUser unixUser = createNiceMock(UnixUser.class);
        expect(unixUser.getUserName()).andReturn(AmbariPamAuthenticationProviderTest.TEST_USER_NAME.toLowerCase()).atLeastOnce();
        PAM pam = createMock(PAM.class);
        expect(pam.authenticate(eq(AmbariPamAuthenticationProviderTest.TEST_USER_NAME), eq(AmbariPamAuthenticationProviderTest.TEST_USER_PASS))).andReturn(unixUser).once();
        pam.dispose();
        expectLastCall().once();
        PamAuthenticationFactory pamAuthenticationFactory = injector.getInstance(PamAuthenticationFactory.class);
        expect(pamAuthenticationFactory.createInstance(injector.getInstance(Configuration.class))).andReturn(pam).once();
        UserEntity userEntity = combineUserEntity(false, true, 0);
        Users users = injector.getInstance(Users.class);
        expect(users.getUserEntity(AmbariPamAuthenticationProviderTest.TEST_USER_NAME)).andReturn(null).once();
        expect(users.createUser(AmbariPamAuthenticationProviderTest.TEST_USER_NAME, AmbariPamAuthenticationProviderTest.TEST_USER_NAME.toLowerCase(), AmbariPamAuthenticationProviderTest.TEST_USER_NAME, true)).andReturn(userEntity).once();
        users.addPamAuthentication(userEntity, AmbariPamAuthenticationProviderTest.TEST_USER_NAME.toLowerCase());
        expectLastCall().once();
        expect(users.getUser(userEntity)).andReturn(new org.apache.ambari.server.security.authorization.User(userEntity)).once();
        expect(users.getUserAuthorities(userEntity)).andReturn(null).once();
        replayAll();
        Authentication authentication = new UsernamePasswordAuthenticationToken(AmbariPamAuthenticationProviderTest.TEST_USER_NAME, AmbariPamAuthenticationProviderTest.TEST_USER_PASS);
        AmbariPamAuthenticationProvider authenticationProvider = injector.getInstance(AmbariPamAuthenticationProvider.class);
        Authentication result = authenticationProvider.authenticate(authentication);
        Assert.assertNotNull(result);
        Assert.assertEquals(true, result.isAuthenticated());
        Assert.assertTrue((result instanceof AmbariUserAuthentication));
        verifyAll();
    }

    @Test
    public void testDisabled() throws Exception {
        Configuration configuration = injector.getInstance(Configuration.class);
        configuration.setClientSecurityType(LOCAL);
        Authentication authentication = new UsernamePasswordAuthenticationToken(AmbariPamAuthenticationProviderTest.TEST_USER_NAME, AmbariPamAuthenticationProviderTest.TEST_USER_PASS);
        AmbariPamAuthenticationProvider authenticationProvider = injector.getInstance(AmbariPamAuthenticationProvider.class);
        Authentication auth = authenticationProvider.authenticate(authentication);
        Assert.assertTrue((auth == null));
    }
}

