/**
 * This file is part of dependency-check-maven.
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
 *
 * Copyright (c) 2014 Jeremy Long. All Rights Reserved.
 */
package org.owasp.dependencycheck.maven;


import Settings.KEYS.AUTO_UPDATE;
import java.io.File;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import mockit.Mock;
import mockit.Tested;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.stubs.ArtifactStub;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.owasp.dependencycheck.Engine;
import org.owasp.dependencycheck.data.nvdcve.DatabaseException;
import org.owasp.dependencycheck.exception.ExceptionCollection;
import org.owasp.dependencycheck.utils.InvalidSettingException;


/**
 *
 *
 * @author Jeremy Long
 */
public class BaseDependencyCheckMojoTest extends BaseTest {
    @Tested
    MavenProject project;

    /**
     * Test of scanArtifacts method, of class BaseDependencyCheckMojo.
     */
    @Test
    public void testScanArtifacts() throws DatabaseException, InvalidSettingException {
        new mockit.MockUp<MavenProject>() {
            @Mock
            public Set<Artifact> getArtifacts() {
                Set<Artifact> artifacts = new HashSet<>();
                Artifact a = new ArtifactStub();
                try {
                    File file = new File(Test.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                    a.setFile(file);
                    artifacts.add(a);
                } catch (URISyntaxException ex) {
                    Logger.getLogger(BaseDependencyCheckMojoTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                // File file = new File(this.getClass().getClassLoader().getResource("daytrader-ear-2.1.7.ear").getPath());
                return artifacts;
            }

            @Mock
            public String getName() {
                return "test-project";
            }
        };
        if (canRun()) {
            boolean autoUpdate = getSettings().getBoolean(AUTO_UPDATE);
            getSettings().setBoolean(AUTO_UPDATE, false);
            try (Engine engine = new Engine(getSettings())) {
                getSettings().setBoolean(AUTO_UPDATE, autoUpdate);
                Assert.assertTrue(((engine.getDependencies().length) == 0));
                BaseDependencyCheckMojoTest.BaseDependencyCheckMojoImpl instance = new BaseDependencyCheckMojoTest.BaseDependencyCheckMojoImpl();
                ExceptionCollection exCol = null;
                try {
                    // the mock above fails under some JDKs
                    exCol = instance.scanArtifacts(project, engine);
                } catch (NullPointerException ex) {
                    Assume.assumeNoException(ex);
                }
                Assert.assertNull(exCol);
                Assert.assertFalse(((engine.getDependencies().length) == 0));
            }
        }
    }

    /**
     * Implementation of ODC Mojo for testing.
     */
    public static class BaseDependencyCheckMojoImpl extends BaseDependencyCheckMojo {
        @Override
        protected void runCheck() throws MojoExecutionException, MojoFailureException {
            throw new UnsupportedOperationException("Operation not supported");
        }

        @Override
        public String getName(Locale locale) {
            return "test implementation";
        }

        @Override
        public String getDescription(Locale locale) {
            return "test implementation";
        }

        @Override
        public boolean canGenerateReport() {
            throw new UnsupportedOperationException("Operation not supported");
        }

        @Override
        protected ExceptionCollection scanDependencies(Engine engine) throws MojoExecutionException {
            throw new UnsupportedOperationException("Operation not supported");
        }
    }
}

