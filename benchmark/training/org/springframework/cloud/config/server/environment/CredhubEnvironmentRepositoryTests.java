/**
 * Copyright 2018-2019 the original author or authors.
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
package org.springframework.cloud.config.server.environment;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.credhub.core.credential.CredHubCredentialOperations;
import org.springframework.credhub.support.CredentialType;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.json.JsonCredential;


/**
 *
 *
 * @author Alberto C. R?os
 */
public class CredhubEnvironmentRepositoryTests {
    private CredhubEnvironmentRepository credhubEnvironmentRepository;

    private CredHubCredentialOperations credhubCredentialOperations;

    @Test
    public void shouldDisplayEmptyPropertiesWhenNoPathFound() {
        Mockito.when(this.credhubCredentialOperations.findByPath("/my-application/production/mylabel")).thenReturn(Collections.emptyList());
        Environment environment = this.credhubEnvironmentRepository.findOne("my-application", "production", "mylabel");
        assertThat(environment.getLabel()).isEqualTo("mylabel");
        assertThat(environment.getProfiles()).containsExactly("production");
        assertThat(environment.getName()).isEqualTo("my-application");
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo("credhub-my-application");
        assertThat(environment.getPropertySources().get(0).getSource()).isEmpty();
    }

    @Test
    public void shouldRetrieveDefaultsWhenNoLabelNorProfileProvided() {
        stubCredentials("/my-application/default/master", "toggles", "key1", "value1");
        Environment environment = this.credhubEnvironmentRepository.findOne("my-application", null, null);
        assertThat(environment.getLabel()).isEqualTo("master");
        assertThat(environment.getProfiles()).containsExactly("default");
        assertThat(environment.getName()).isEqualTo("my-application");
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo("credhub-my-application");
        assertThat(environment.getPropertySources().get(0).getSource()).isEqualTo(Collections.singletonMap("key1", "value1"));
    }

    @Test
    public void shouldRetrieveGivenProfileAndLabel() {
        stubCredentials("/my-application/production/mylabel", "toggles", "key1", "value1");
        Environment environment = this.credhubEnvironmentRepository.findOne("my-application", "production", "mylabel");
        assertThat(environment.getLabel()).isEqualTo("mylabel");
        assertThat(environment.getProfiles()).containsExactly("production");
        assertThat(environment.getName()).isEqualTo("my-application");
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo("credhub-my-application");
        assertThat(environment.getPropertySources().get(0).getSource()).isEqualTo(Collections.singletonMap("key1", "value1"));
    }

    @Test
    public void shouldRetrieveGivenMultipleProfiles() {
        stubCredentials("/my-application/production/mylabel", "toggles", "key1", "value1");
        stubCredentials("/my-application/cloud/mylabel", "abs", "key2", "value2");
        Environment environment = this.credhubEnvironmentRepository.findOne("my-application", "production,cloud", "mylabel");
        assertThat(environment.getLabel()).isEqualTo("mylabel");
        assertThat(environment.getProfiles()).containsExactly("production", "cloud");
        assertThat(environment.getName()).isEqualTo("my-application");
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo("credhub-my-application");
        HashMap<Object, Object> expectedValues = new HashMap<>();
        expectedValues.put("key1", "value1");
        expectedValues.put("key2", "value2");
        assertThat(environment.getPropertySources().get(0).getSource()).isEqualTo(expectedValues);
    }

    @Test
    public void shouldMergeWhenMoreThanOneCredentialsFound() {
        String expectedPath = "/my-application/production/mylabel";
        SimpleCredentialName togglesCredentialName = new SimpleCredentialName((expectedPath + "/toggles"));
        SimpleCredentialName absCredentialName = new SimpleCredentialName((expectedPath + "/abs"));
        Mockito.when(this.credhubCredentialOperations.findByPath(expectedPath)).thenReturn(Arrays.asList(new org.springframework.credhub.support.CredentialSummary(togglesCredentialName), new org.springframework.credhub.support.CredentialSummary(absCredentialName)));
        JsonCredential credentials = new JsonCredential();
        credentials.put("key1", "value1");
        Mockito.when(this.credhubCredentialOperations.getByName(togglesCredentialName, JsonCredential.class)).thenReturn(new org.springframework.credhub.support.CredentialDetails("id1", togglesCredentialName, CredentialType.JSON, credentials));
        JsonCredential otherCredentials = new JsonCredential();
        otherCredentials.put("key2", "value2");
        Mockito.when(this.credhubCredentialOperations.getByName(absCredentialName, JsonCredential.class)).thenReturn(new org.springframework.credhub.support.CredentialDetails("id2", absCredentialName, CredentialType.JSON, otherCredentials));
        Environment environment = this.credhubEnvironmentRepository.findOne("my-application", "production", "mylabel");
        assertThat(environment.getLabel()).isEqualTo("mylabel");
        assertThat(environment.getProfiles()).containsExactly("production");
        assertThat(environment.getName()).isEqualTo("my-application");
        assertThat(environment.getPropertySources().get(0).getName()).isEqualTo("credhub-my-application");
        HashMap<Object, Object> expectedValues = new HashMap<>();
        expectedValues.put("key1", "value1");
        expectedValues.put("key2", "value2");
        assertThat(environment.getPropertySources().get(0).getSource()).isEqualTo(expectedValues);
    }
}

