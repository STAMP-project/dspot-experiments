/**
 * Copyright 2005 JBoss Inc
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
package org.drools.compiler.builder.impl;


import KieServices.Factory;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;


public class KieFileSystemScannerTest {
    @Test
    public void testSnapshot() throws Exception {
        Path tempDir = Files.createTempDirectory(FileSystems.getDefault().getPath("./target"), null);
        File file = null;
        try {
            KieServices ks = Factory.get();
            ReleaseId releaseId = ks.newReleaseId("org.kie", "scanner-test", "1.0-SNAPSHOT");
            createKieJar(ks, releaseId, "R1");
            KieContainer kieContainer = ks.newKieContainer(releaseId);
            KieSession ksession = kieContainer.newKieSession();
            checkKSession(ksession, "R1");
            KieScanner scanner = ks.newKieScanner(kieContainer, tempDir.toString());
            scanner.scanNow();
            ksession = kieContainer.newKieSession();
            checkKSession(ksession, "R1");
            file = write(createKieJar(ks, releaseId, "R2"), tempDir, releaseId);
            ksession = kieContainer.newKieSession();
            checkKSession(ksession, "R1");
            scanner.scanNow();
            ksession = kieContainer.newKieSession();
            checkKSession(ksession, "R2");
        } finally {
            if (file != null) {
                file.delete();
            }
            Files.delete(tempDir);
        }
    }

    @Test
    public void testFixedVersion() throws Exception {
        Path tempDir = Files.createTempDirectory(FileSystems.getDefault().getPath("./target"), null);
        File file2 = null;
        File file3 = null;
        try {
            KieServices ks = Factory.get();
            ReleaseId releaseId1 = ks.newReleaseId("org.kie", "scanner-test", "1.0.0");
            ReleaseId releaseId2 = ks.newReleaseId("org.kie", "scanner-test", "1.0.1");
            ReleaseId releaseId3 = ks.newReleaseId("org.kie", "scanner-test", "1.1.0");
            createKieJar(ks, releaseId1, "R1");
            KieContainer kieContainer = ks.newKieContainer(releaseId1);
            KieSession ksession = kieContainer.newKieSession();
            checkKSession(ksession, "R1");
            KieScanner scanner = ks.newKieScanner(kieContainer, tempDir.toString());
            scanner.scanNow();
            ksession = kieContainer.newKieSession();
            checkKSession(ksession, "R1");
            file2 = write(createKieJar(ks, releaseId2, "R2"), tempDir, releaseId2);
            file3 = write(createKieJar(ks, releaseId2, "R3"), tempDir, releaseId3);
            ksession = kieContainer.newKieSession();
            checkKSession(ksession, "R1");
            scanner.scanNow();
            ksession = kieContainer.newKieSession();
            checkKSession(ksession, "R3");
        } finally {
            if (file2 != null) {
                file2.delete();
            }
            if (file3 != null) {
                file3.delete();
            }
            Files.delete(tempDir);
        }
    }

    @Test
    public void testDoNotUpgradeOnOlderVersion() throws Exception {
        Path tempDir = Files.createTempDirectory(FileSystems.getDefault().getPath("./target"), null);
        File file2 = null;
        try {
            KieServices ks = Factory.get();
            ReleaseId releaseIdNew = ks.newReleaseId("org.kie", "scanner-test", "1.1.0");
            ReleaseId releaseIdOld = ks.newReleaseId("org.kie", "scanner-test", "1.0.0");
            createKieJar(ks, releaseIdNew, "R1");
            KieContainer kieContainer = ks.newKieContainer(releaseIdNew);
            KieSession ksession = kieContainer.newKieSession();
            checkKSession(ksession, "R1");
            KieScanner scanner = ks.newKieScanner(kieContainer, tempDir.toString());
            file2 = write(createKieJar(ks, releaseIdOld, "R2"), tempDir, releaseIdOld);
            scanner.scanNow();
            ksession = kieContainer.newKieSession();
            checkKSession(ksession, "R1");
        } finally {
            if (file2 != null) {
                file2.delete();
            }
            Files.delete(tempDir);
        }
    }
}

