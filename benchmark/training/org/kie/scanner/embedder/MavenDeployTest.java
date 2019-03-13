/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.scanner.embedder;


import KieServices.Factory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.appformer.maven.integration.embedder.MavenSettings;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.eclipse.aether.repository.RemoteRepository;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.scanner.AbstractKieCiTest;
import org.kie.scanner.KieMavenRepository;


public class MavenDeployTest extends AbstractKieCiTest {
    @Test
    public void testDeploy() throws IOException {
        KieServices ks = Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "scanner-test-deploy", "1.0-SNAPSHOT");
        Path m2Folder = Files.createTempDirectory("temp-m2");
        Path settingsXmlPath = MavenDeployTest.generateSettingsXml(m2Folder);
        String oldSettingsXmlPath = System.getProperty(CUSTOM_SETTINGS_PROPERTY);
        try {
            System.setProperty(CUSTOM_SETTINGS_PROPERTY, settingsXmlPath.toString());
            MavenSettings.reinitSettings();
            InternalKieModule kJar1 = createKieJar(ks, releaseId, "rule1", "rule2");
            KieContainer kieContainer = ks.newKieContainer(releaseId);
            KieMavenRepository repository = KieMavenRepository.getKieMavenRepository();
            RemoteRepository remote = MavenDeployTest.createRemoteRepository(m2Folder);
            repository.deployArtifact(remote, releaseId, kJar1, createKPom(m2Folder, releaseId).toFile());
            // create a ksesion and check it works as expected
            KieSession ksession = kieContainer.newKieSession("KSession1");
            checkKSession(ksession, "rule1", "rule2");
            // create a new kjar
            InternalKieModule kJar2 = createKieJar(ks, releaseId, "rule2", "rule3");
            // deploy it on maven
            repository.deployArtifact(remote, releaseId, kJar2, createKPom(m2Folder, releaseId).toFile());
            // since I am not calling start() on the scanner it means it won't have automatic scheduled scanning
            KieScanner scanner = ks.newKieScanner(kieContainer);
            // scan the maven repo to get the new kjar version and deploy it on the kcontainer
            scanner.scanNow();
            // create a ksesion and check it works as expected
            KieSession ksession2 = kieContainer.newKieSession("KSession1");
            checkKSession(ksession2, "rule2", "rule3");
            ks.getRepository().removeKieModule(releaseId);
        } finally {
            if (oldSettingsXmlPath == null) {
                System.clearProperty(CUSTOM_SETTINGS_PROPERTY);
            } else {
                System.setProperty(CUSTOM_SETTINGS_PROPERTY, oldSettingsXmlPath);
            }
            MavenSettings.reinitSettings();
        }
    }

    @Test
    public void testKScannerWithDeployUsingDistributionManagement() throws IOException {
        KieServices ks = Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "scanner-test-deploy-dist", "1.0-SNAPSHOT");
        Path m2Folder = Files.createTempDirectory("temp-m2-dist");
        Path settingsXmlPath = MavenDeployTest.generateSettingsXml(m2Folder);
        String oldSettingsXmlPath = System.getProperty(CUSTOM_SETTINGS_PROPERTY);
        try {
            System.setProperty(CUSTOM_SETTINGS_PROPERTY, settingsXmlPath.toString());
            MavenSettings.reinitSettings();
            InternalKieModule kJar1 = createKieJar(ks, releaseId, "rule1", "rule2");
            KieContainer kieContainer = ks.newKieContainer(releaseId);
            KieMavenRepository repository = KieMavenRepository.getKieMavenRepository();
            repository.deployArtifact(releaseId, kJar1, createKPomWithDistributionManagement(m2Folder, releaseId).toFile());
            // create a ksesion and check it works as expected
            KieSession ksession = kieContainer.newKieSession("KSession1");
            checkKSession(ksession, "rule1", "rule2");
            // create a new kjar
            InternalKieModule kJar2 = createKieJar(ks, releaseId, "rule2", "rule3");
            // deploy it on maven
            repository.deployArtifact(releaseId, kJar2, createKPomWithDistributionManagement(m2Folder, releaseId).toFile());
            // since I am not calling start() on the scanner it means it won't have automatic scheduled scanning
            KieScanner scanner = ks.newKieScanner(kieContainer);
            // scan the maven repo to get the new kjar version and deploy it on the kcontainer
            scanner.scanNow();
            // create a ksesion and check it works as expected
            KieSession ksession2 = kieContainer.newKieSession("KSession1");
            checkKSession(ksession2, "rule2", "rule3");
            ks.getRepository().removeKieModule(releaseId);
        } finally {
            if (oldSettingsXmlPath == null) {
                System.clearProperty(CUSTOM_SETTINGS_PROPERTY);
            } else {
                System.setProperty(CUSTOM_SETTINGS_PROPERTY, oldSettingsXmlPath);
            }
            MavenSettings.reinitSettings();
        }
    }
}

