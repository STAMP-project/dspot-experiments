/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.testcoverage.functional;


import org.drools.testcoverage.common.util.TestConstants;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;


/**
 * Tests correct behavior of KieRepository, such as adding/removing KieModules.
 */
public class KieRepositoryTest {
    private static final String TEST_ARTIFACT_ID = "kie-repo-test";

    private static final String TEST_ARTIFACT_VERSION_RELEASE = "1.0.0";

    private static final String TEST_ARTIFACT_VERSION_SNAPSHOT = "1.0.0";

    private KieServices kieServices;

    private static final String DRL = "package defaultKBase;\n rule testRule when then end\n";

    /**
     * Tests adding a KieModule with a non-SNAPSHOT ReleaseId to KieRepository.
     */
    @Test
    public void testAddKieModuleRelease() {
        final ReleaseId releaseId = kieServices.newReleaseId(TestConstants.PACKAGE_TESTCOVERAGE, KieRepositoryTest.TEST_ARTIFACT_ID, KieRepositoryTest.TEST_ARTIFACT_VERSION_RELEASE);
        this.testKieModuleAddition(releaseId);
    }

    /**
     * Tests adding a KieModule with a SNAPSHOT ReleaseId to KieRepository.
     */
    @Test
    public void testAddKieModuleSnapshot() {
        final ReleaseId releaseId = kieServices.newReleaseId(TestConstants.PACKAGE_TESTCOVERAGE, KieRepositoryTest.TEST_ARTIFACT_ID, KieRepositoryTest.TEST_ARTIFACT_VERSION_SNAPSHOT);
        this.testKieModuleAddition(releaseId);
    }

    /**
     * Tests removing a KieModule with a non-SNAPSHOT ReleaseId from KieRepository.
     */
    @Test
    public void testRemoveKieModuleRelease() {
        final ReleaseId releaseId = kieServices.newReleaseId(TestConstants.PACKAGE_TESTCOVERAGE, KieRepositoryTest.TEST_ARTIFACT_ID, KieRepositoryTest.TEST_ARTIFACT_VERSION_RELEASE);
        this.testKieModuleRemoval(releaseId);
    }

    /**
     * Tests removing a KieModule with a SNAPSHOT ReleaseId from KieRepository.
     */
    @Test
    public void testRemoveKieModuleSnapshot() {
        final ReleaseId releaseId = kieServices.newReleaseId(TestConstants.PACKAGE_TESTCOVERAGE, KieRepositoryTest.TEST_ARTIFACT_ID, KieRepositoryTest.TEST_ARTIFACT_VERSION_SNAPSHOT);
        this.testKieModuleRemoval(releaseId);
    }
}

