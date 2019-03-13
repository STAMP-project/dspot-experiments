/**
 * -
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 - 2017 Spotify AB
 * --
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
 * -/-/-
 */
/**
 * Copyright (c) 2017
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.spotify.docker.client;


import com.spotify.docker.client.DockerCredentialHelper.CredentialHelperDelegate;
import com.spotify.docker.client.messages.DockerCredentialHelperAuth;
import com.spotify.docker.client.messages.RegistryAuth;
import com.spotify.docker.client.messages.RegistryConfigs;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.lang.RandomStringUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


@SuppressWarnings("deprecated")
public class DockerConfigReaderTest {
    private static final RegistryAuth DOCKER_AUTH_CONFIG = RegistryAuth.builder().serverAddress("https://index.docker.io/v1/").username("dockerman").password("sw4gy0lo").email("dockerman@hub.com").build();

    private static final RegistryAuth MY_AUTH_CONFIG = RegistryAuth.builder().serverAddress("https://narnia.mydock.io/v1/").username("megaman").password("riffraff").email("megaman@mydock.com").build();

    private static final RegistryAuth IDENTITY_TOKEN_AUTH_CONFIG = RegistryAuth.builder().email("dockerman@hub.com").serverAddress("docker.customdomain.com").identityToken("52ce5fd5-eb60-42bf-931f-5eeec128211a").build();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final DockerConfigReader reader = new DockerConfigReader();

    private CredentialHelperDelegate credentialHelperDelegate;

    @Test
    public void testFromDockerConfig_FullConfig() throws Exception {
        final RegistryAuth registryAuth = reader.anyRegistryAuth(DockerConfigReaderTest.getTestFilePath("dockerConfig/fullConfig.json"));
        MatcherAssert.assertThat(registryAuth, CoreMatchers.equalTo(DockerConfigReaderTest.DOCKER_AUTH_CONFIG));
    }

    @Test
    public void testFromDockerConfig_FullDockerCfg() throws Exception {
        final RegistryAuth registryAuth = reader.anyRegistryAuth(DockerConfigReaderTest.getTestFilePath("dockerConfig/fullDockerCfg"));
        MatcherAssert.assertThat(registryAuth, CoreMatchers.equalTo(DockerConfigReaderTest.DOCKER_AUTH_CONFIG));
    }

    @Test
    public void testFromDockerConfig_IdentityToken() throws Exception {
        final RegistryAuth authConfig = reader.anyRegistryAuth(DockerConfigReaderTest.getTestFilePath("dockerConfig/identityTokenConfig.json"));
        MatcherAssert.assertThat(authConfig, CoreMatchers.equalTo(DockerConfigReaderTest.IDENTITY_TOKEN_AUTH_CONFIG));
    }

    @Test
    public void testFromDockerConfig_IncompleteConfig() throws Exception {
        final RegistryAuth registryAuth = reader.anyRegistryAuth(DockerConfigReaderTest.getTestFilePath("dockerConfig/incompleteConfig.json"));
        final RegistryAuth expected = RegistryAuth.builder().email("dockerman@hub.com").serverAddress("https://different.docker.io/v1/").build();
        MatcherAssert.assertThat(registryAuth, Matchers.is(expected));
    }

    @Test
    public void testFromDockerConfig_WrongConfigs() throws Exception {
        final RegistryAuth registryAuth1 = reader.anyRegistryAuth(DockerConfigReaderTest.getTestFilePath("dockerConfig/wrongConfig1.json"));
        MatcherAssert.assertThat(registryAuth1, Matchers.is(DockerConfigReaderTest.emptyRegistryAuth()));
        final RegistryAuth registryAuth2 = reader.anyRegistryAuth(DockerConfigReaderTest.getTestFilePath("dockerConfig/wrongConfig2.json"));
        MatcherAssert.assertThat(registryAuth2, Matchers.is(DockerConfigReaderTest.emptyRegistryAuth()));
    }

    @Test
    public void testFromDockerConfig_MissingConfigFile() throws Exception {
        final Path randomPath = Paths.get(((RandomStringUtils.randomAlphanumeric(16)) + ".json"));
        expectedException.expect(FileNotFoundException.class);
        reader.anyRegistryAuth(randomPath);
    }

    @Test
    public void testFromDockerConfig_MultiConfig() throws Exception {
        final Path path = DockerConfigReaderTest.getTestFilePath("dockerConfig/multiConfig.json");
        final RegistryAuth myDockParsed = reader.authForRegistry(path, "https://narnia.mydock.io/v1/");
        MatcherAssert.assertThat(myDockParsed, CoreMatchers.equalTo(DockerConfigReaderTest.MY_AUTH_CONFIG));
        final RegistryAuth dockerIoParsed = reader.authForRegistry(path, "https://index.docker.io/v1/");
        MatcherAssert.assertThat(dockerIoParsed, CoreMatchers.equalTo(DockerConfigReaderTest.DOCKER_AUTH_CONFIG));
    }

    @Test
    public void testFromDockerConfig_AddressProtocol() throws IOException {
        final Path path = DockerConfigReaderTest.getTestFilePath("dockerConfig/protocolMissing.json");
        // Server address matches exactly what's in the config file
        final RegistryAuth noProto = reader.authForRegistry(path, "docker.example.com");
        MatcherAssert.assertThat(noProto.serverAddress(), CoreMatchers.equalTo("docker.example.com"));
        // Server address doesn't have a protocol but the entry in the config file does (https)
        final RegistryAuth httpsProto = reader.authForRegistry(path, "repo.example.com");
        MatcherAssert.assertThat(httpsProto.serverAddress(), CoreMatchers.equalTo("https://repo.example.com"));
        // Server address doesn't have a protocol but the entry in the config file does (http)
        final RegistryAuth httpProto = reader.authForRegistry(path, "local.example.com");
        MatcherAssert.assertThat(httpProto.serverAddress(), CoreMatchers.equalTo("http://local.example.com"));
    }

    @Test
    public void testParseRegistryConfigs() throws Exception {
        final Path path = DockerConfigReaderTest.getTestFilePath("dockerConfig/multiConfig.json");
        final RegistryConfigs configs = reader.authForAllRegistries(path);
        MatcherAssert.assertThat(configs.configs(), Matchers.allOf(Matchers.hasEntry("https://index.docker.io/v1/", DockerConfigReaderTest.DOCKER_AUTH_CONFIG), Matchers.hasEntry("https://narnia.mydock.io/v1/", DockerConfigReaderTest.MY_AUTH_CONFIG)));
    }

    @Test
    public void testParseNoAuths() throws Exception {
        final Path path = DockerConfigReaderTest.getTestFilePath("dockerConfig/noAuths.json");
        final RegistryConfigs configs = reader.authForAllRegistries(path);
        MatcherAssert.assertThat(configs, CoreMatchers.equalTo(RegistryConfigs.empty()));
    }

    @Test
    public void testCredHelpers() throws Exception {
        final Path path = DockerConfigReaderTest.getTestFilePath("dockerConfig/credHelpers.json");
        final String registry1 = "https://foo.io";
        final String registry2 = "https://adventure.zone";
        final String registry3 = "https://beyond.zone";
        final DockerCredentialHelperAuth testAuth1 = DockerCredentialHelperAuth.create("cool user", "cool password", registry1);
        final DockerCredentialHelperAuth testAuth2 = DockerCredentialHelperAuth.create("taako", "lupe", registry2);
        Mockito.when(credentialHelperDelegate.get("a-cred-helper", registry1)).thenReturn(testAuth1);
        Mockito.when(credentialHelperDelegate.get("magic-missile", registry2)).thenReturn(testAuth2);
        Mockito.when(credentialHelperDelegate.get("elusive-helper", registry3)).thenReturn(null);
        final RegistryConfigs expected = RegistryConfigs.builder().addConfig(registry1, testAuth1.toRegistryAuth()).addConfig(registry2, testAuth2.toRegistryAuth()).build();
        final RegistryConfigs configs = reader.authForAllRegistries(path);
        MatcherAssert.assertThat(configs, Matchers.is(expected));
    }

    @Test
    public void testCredsStoreAndCredHelpersAndAuth() throws Exception {
        final Path path = DockerConfigReaderTest.getTestFilePath("dockerConfig/credsStoreAndCredHelpersAndAuth.json");
        // This registry is in the file, in the "auths" sections
        final String registry1 = DockerConfigReaderTest.DOCKER_AUTH_CONFIG.serverAddress();
        MatcherAssert.assertThat(reader.authForRegistry(path, registry1), Matchers.is(DockerConfigReaderTest.DOCKER_AUTH_CONFIG));
        // This registry is in the "credHelpers" section. It will give us a
        // credsStore value which will trigger our mock and give us testAuth2.
        final String registry2 = "https://adventure.zone";
        final DockerCredentialHelperAuth testAuth2 = DockerCredentialHelperAuth.create("taako", "lupe", registry2);
        Mockito.when(credentialHelperDelegate.get("magic-missile", registry2)).thenReturn(testAuth2);
        MatcherAssert.assertThat(reader.authForRegistry(path, registry2), Matchers.is(testAuth2.toRegistryAuth()));
        // This registry is not in the "auths" or anywhere else. It should default
        // to using the credsStore value, and our mock will return testAuth3.
        final String registry3 = "https://rush.in";
        final DockerCredentialHelperAuth testAuth3 = DockerCredentialHelperAuth.create("magnus", "julia", registry3);
        Mockito.when(credentialHelperDelegate.get("starblaster", registry3)).thenReturn(testAuth3);
        MatcherAssert.assertThat(reader.authForRegistry(path, registry3), Matchers.is(testAuth3.toRegistryAuth()));
        // Finally, when we get auths for *all* registries in the file, we only expect
        // auths for the two registries that are explicitly mentioned.
        // Since registry1 is in the "auths" and registry2 is in the "credHelpers",
        // we will see auths for them.
        final RegistryConfigs registryConfigs = RegistryConfigs.builder().addConfig(registry2, testAuth2.toRegistryAuth()).addConfig(registry1, DockerConfigReaderTest.DOCKER_AUTH_CONFIG).build();
        MatcherAssert.assertThat(reader.authForAllRegistries(path), Matchers.is(registryConfigs));
    }

    @Test
    public void testDuplicateServerInAuthsAndCredHelpers() throws Exception {
        final Path path = DockerConfigReaderTest.getTestFilePath("dockerConfig/duplicateServerInAuthsAndCredHelpers.json");
        final String registry = "https://index.docker.io/v1/";
        final DockerCredentialHelperAuth testAuth = DockerCredentialHelperAuth.create("dockerman", "sw4gy0lo", registry);
        Mockito.when(credentialHelperDelegate.get("magic-missile", registry)).thenReturn(testAuth);
        // Test that for duplicate registries, credHelpers is preferred over auths.
        final RegistryConfigs registryConfigs = RegistryConfigs.builder().addConfig(registry, testAuth.toRegistryAuth()).build();
        MatcherAssert.assertThat(reader.authForAllRegistries(path), Matchers.is(registryConfigs));
    }
}

