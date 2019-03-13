/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.drools.testcoverage.memory;


import KieServices.Factory;
import java.io.IOException;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.core.util.FileManager;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.MavenUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.scanner.KieMavenRepository;
import org.kie.test.testcategory.TurtleTestCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Ignore("DROOLS-3628 Ignoring, because it can be useful when run locally, however due to unpredictable GC behaviour, unstable in automation.")
@Category(TurtleTestCategory.class)
public class KieScannerMemoryTest {
    private static final Logger logger = LoggerFactory.getLogger(KieScannerMemoryTest.class);

    private FileManager fileManager;

    @Test
    public void testScannerMemoryFootprint() throws IOException {
        final KieServices kieServices = Factory.get();
        final KieMavenRepository repository = KieMavenRepository.getKieMavenRepository();
        final KieModule kieModule = KieUtil.getKieModuleFromDrls(TestConstants.PACKAGE_FUNCTIONAL, KieBaseTestConfiguration.CLOUD_IDENTITY, "rule R when then end");
        final ReleaseId releaseId = kieModule.getReleaseId();
        repository.installArtifact(releaseId, ((InternalKieModule) (kieModule)), MavenUtil.createPomXml(fileManager, releaseId));
        final KieContainer kieContainer = kieServices.newKieContainer(releaseId);
        final KieScanner kieScanner = kieServices.newKieScanner(kieContainer);
        kieScanner.start(20);
        try {
            measureMemoryFootprint(1000, 100, 6, 30);
        } finally {
            kieScanner.stop();
        }
    }
}

