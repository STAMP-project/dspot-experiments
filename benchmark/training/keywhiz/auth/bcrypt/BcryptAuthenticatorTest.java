/**
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package keywhiz.auth.bcrypt;


import io.dropwizard.auth.basic.BasicCredentials;
import java.util.Optional;
import keywhiz.auth.User;
import keywhiz.service.daos.UserDAO;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class BcryptAuthenticatorTest {
    BcryptAuthenticator bcryptAuthenticator;

    String hashedPass;

    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    @Mock
    UserDAO userDAO;

    @Test
    public void bcryptAuthenticatorCreatesUserOnSuccess() throws Exception {
        Mockito.when(userDAO.getHashedPassword("sysadmin")).thenReturn(Optional.of(hashedPass));
        User user = bcryptAuthenticator.authenticate(new BasicCredentials("sysadmin", "validpass")).orElseThrow(RuntimeException::new);
        assertThat(user).isEqualTo(User.named("sysadmin"));
    }

    @Test
    public void bcryptAuthenticatorFailsForBadPassword() throws Exception {
        Mockito.when(userDAO.getHashedPassword("sysadmin")).thenReturn(Optional.of(hashedPass));
        Optional<User> missingUser = bcryptAuthenticator.authenticate(new BasicCredentials("sysadmin", "badpass"));
        assertThat(missingUser.isPresent()).isFalse();
    }

    @Test
    public void bcryptAuthenticatorFailsForBadUser() throws Exception {
        Mockito.when(userDAO.getHashedPassword("invaliduser")).thenReturn(Optional.empty());
        Optional<User> missingUser = bcryptAuthenticator.authenticate(new BasicCredentials("invaliduser", "validpass"));
        assertThat(missingUser.isPresent()).isFalse();
    }

    @Test
    public void bcryptAuthenticatorRejectsInvalidUsername() throws Exception {
        String crazyUsername = "sysadmin)`~!@#$%^&*()+=[]{}\\|;:\'\",<>?/\r\n\t";
        Optional<User> missingUser = bcryptAuthenticator.authenticate(new BasicCredentials(crazyUsername, "validpass"));
        assertThat(missingUser.isPresent()).isFalse();
    }
}

