/**
 * This file is part of dependency-check-core.
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
 * Copyright (c) 2018 Steve Springett. All Rights Reserved.
 */
package org.owasp.dependencycheck.agent;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseDBTestCase;
import org.owasp.dependencycheck.dependency.Dependency;


public class DependencyCheckScanAgentIT extends BaseDBTestCase {
    private static final File REPORT_DIR = new File("target/test-scan-agent/report");

    @Test
    public void testComponentMetadata() throws Exception {
        List<Dependency> dependencies = new ArrayList<>();
        dependencies.add(createDependency("apache", "tomcat", "5.0.5"));
        DependencyCheckScanAgent scanAgent = createScanAgent();
        scanAgent.setDependencies(dependencies);
        scanAgent.execute();
        Dependency tomcat = scanAgent.getDependencies().get(0);
        Assert.assertTrue(((tomcat.getVulnerableSoftwareIdentifiers().size()) >= 1));
        // This will change over time
        Assert.assertTrue(((tomcat.getVulnerabilities().size()) > 5));
    }
}

