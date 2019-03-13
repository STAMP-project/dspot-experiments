/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.scanner;


import KieServices.Factory;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.core.util.FileManager;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/* Test to verify handling of SNAPSHORT artifacts with a remote Nexus repository

Prerequisites for this test:

1. Download and extract https://sonatype-download.global.ssl.fastly.net/nexus/3/nexus-3.6.0-02-unix.tar.gz
2. Start the Nexus server
$ ./bin/nexus start
3. Note that this test uses 'http://localhost:8081' as nexus target, with the default nexus user name 'admin' and password 'admin123'
 */
@RunWith(Parameterized.class)
@Ignore("ignored because it needs a running nexus server")
public class KieRepositoryScannerNexusTest extends AbstractKieCiTest {
    private static final Logger LOG = LoggerFactory.getLogger(KieRepositoryScannerNexusTest.class);

    private final boolean useWiredComponentProvider;

    private FileManager fileManager;

    public KieRepositoryScannerNexusTest(boolean useWiredComponentProvider) {
        this.useWiredComponentProvider = useWiredComponentProvider;
    }

    @Test
    public void testKScannerNewContainer() throws Exception {
        KieServices ks = Factory.get();
        ReleaseId releaseId = ks.newReleaseId("com.redhat.gss", "scanner-test", "1.0-SNAPSHOT");
        InternalKieModule kJar1 = createKieJar(ks, releaseId, getPomWithDistributionManagement(releaseId), true, "rule1", "rule2");
        KieMavenRepository repository = KieMavenRepository.getKieMavenRepository();
        repository.deployArtifact(releaseId, kJar1, createKPomWithDistributionManagement(fileManager, releaseId));
        // remove kjar from KieRepo
        ks.getRepository().removeKieModule(releaseId);
        KieContainer kieContainer = ks.newKieContainer(releaseId);
        // create a ksesion and check it works as expected
        KieSession ksession = kieContainer.newKieSession("KSession1");
        checkKSession(ksession, "rule1", "rule2");
        // Store the module originally being loaded from Nexus to re-insert it, after generating a new version
        KieModule firstModuleLoadedFromNexus = ks.getRepository().removeKieModule(releaseId);
        // create a new kjar
        InternalKieModule kJar2 = createKieJar(ks, releaseId, getPomWithDistributionManagement(releaseId), true, "rule2", "rule3");
        // deploy it on maven
        repository.deployArtifact(releaseId, kJar2, createKPomWithDistributionManagement(fileManager, releaseId));
        // remove kjar from KieRepo
        ks.getRepository().removeKieModule(releaseId);
        // Insert the module once again - should be an older snapshot than the second one and therefore be replaced when getting the second one
        ks.getRepository().addKieModule(firstModuleLoadedFromNexus);
        // create new KieContainer
        KieContainer kieContainer2 = ks.newKieContainer(releaseId);
        // create a ksession for the new container and check it works as expected
        KieSession ksession2 = kieContainer2.newKieSession("KSession1");
        checkKSession(ksession2, "rule2", "rule3");
        ks.getRepository().removeKieModule(releaseId);
    }
}

