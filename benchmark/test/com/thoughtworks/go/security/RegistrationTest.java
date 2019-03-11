/**
 * Copyright 2017 ThoughtWorks, Inc.
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
package com.thoughtworks.go.security;


import java.io.IOException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class RegistrationTest {
    @Rule
    public final TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void decodeFromJson() throws IOException {
        Registration origin = createRegistration();
        Registration reg = RegistrationJSONizer.fromJson(RegistrationJSONizer.toJson(origin));
        Assert.assertThat(reg.getPrivateKey(), Matchers.is(origin.getPrivateKey()));
        Assert.assertThat(reg.getPublicKey(), Matchers.is(origin.getPublicKey()));
        Assert.assertThat(reg.getChain(), Matchers.is(origin.getChain()));
        Assert.assertThat(reg.getCertificateNotBeforeDate(), Matchers.is(origin.getCertificateNotBeforeDate()));
        Assert.assertThat(reg.getFirstCertificate(), Matchers.is(origin.getFirstCertificate()));
        Assert.assertThat(reg.getChain().length, Matchers.is(3));
    }

    @Test
    public void shouldBeValidWhenPrivateKeyAndChainIsPresent() throws Exception {
        Assert.assertFalse(Registration.createNullPrivateKeyEntry().isValid());
        Assert.assertFalse(new Registration(null, null).isValid());
        Assert.assertFalse(new Registration(null).isValid());
        Assert.assertTrue(createRegistration().isValid());
    }

    @Test
    public void registrationFromEmptyJSONShouldBeInvalid() throws Exception {
        Assert.assertFalse(RegistrationJSONizer.fromJson("{}").isValid());
    }

    @Test
    public void shouldEncodeDecodeEmptyRegistration() throws Exception {
        Registration toSerialize = Registration.createNullPrivateKeyEntry();
        Registration deserialized = RegistrationJSONizer.fromJson(RegistrationJSONizer.toJson(toSerialize));
        Assert.assertTrue(EqualsBuilder.reflectionEquals(toSerialize, deserialized));
    }
}

