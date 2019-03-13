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
package org.springframework.boot.cli.compiler.grape;


import java.io.File;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.internal.impl.SimpleLocalRepositoryManagerFactory;
import org.eclipse.aether.repository.LocalRepository;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.boot.test.util.TestPropertyValues;


/**
 * Tests for {@link SettingsXmlRepositorySystemSessionAutoConfiguration}.
 *
 * @author Andy Wilkinson
 */
public class SettingsXmlRepositorySystemSessionAutoConfigurationTests {
    @Mock
    private RepositorySystem repositorySystem;

    @Test
    public void basicSessionCustomization() {
        assertSessionCustomization("src/test/resources/maven-settings/basic");
    }

    @Test
    public void encryptedSettingsSessionCustomization() {
        assertSessionCustomization("src/test/resources/maven-settings/encrypted");
    }

    @Test
    public void propertyInterpolation() {
        final DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        BDDMockito.given(this.repositorySystem.newLocalRepositoryManager(ArgumentMatchers.eq(session), ArgumentMatchers.any(LocalRepository.class))).willAnswer(( invocation) -> {
            LocalRepository localRepository = invocation.getArgument(1);
            return new SimpleLocalRepositoryManagerFactory().newInstance(session, localRepository);
        });
        TestPropertyValues.of("user.home:src/test/resources/maven-settings/property-interpolation", "foo:bar").applyToSystemProperties(() -> {
            new SettingsXmlRepositorySystemSessionAutoConfiguration().apply(session, this.repositorySystem);
            return null;
        });
        assertThat(session.getLocalRepository().getBasedir().getAbsolutePath()).endsWith(((((File.separatorChar) + "bar") + (File.separatorChar)) + "repository"));
    }
}

