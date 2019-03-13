/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.autoconfigure.security.oauth2.client;


import OAuth2ClientProperties.Registration;
import org.junit.Test;


/**
 * Tests for {@link OAuth2ClientProperties}.
 *
 * @author Madhura Bhave
 * @author Artsiom Yudovin
 */
public class OAuth2ClientPropertiesTests {
    private OAuth2ClientProperties properties = new OAuth2ClientProperties();

    @Test
    public void clientIdAbsentThrowsException() {
        OAuth2ClientProperties.Registration registration = new OAuth2ClientProperties.Registration();
        registration.setClientSecret("secret");
        registration.setProvider("google");
        this.properties.getRegistration().put("foo", registration);
        assertThatIllegalStateException().isThrownBy(this.properties::validate).withMessageContaining("Client id must not be empty.");
    }

    @Test
    public void clientSecretAbsentShouldNotThrowException() {
        OAuth2ClientProperties.Registration registration = new OAuth2ClientProperties.Registration();
        registration.setClientId("foo");
        registration.setProvider("google");
        this.properties.getRegistration().put("foo", registration);
        this.properties.validate();
    }
}

