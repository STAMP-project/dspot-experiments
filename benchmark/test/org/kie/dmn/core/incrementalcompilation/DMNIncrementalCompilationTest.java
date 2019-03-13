/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.dmn.core.incrementalcompilation;


import KieServices.Factory;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseInterpretedVsCompiledTestCanonicalKieModule;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.core.util.KieHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DMNIncrementalCompilationTest extends BaseInterpretedVsCompiledTestCanonicalKieModule {
    public static final Logger LOG = LoggerFactory.getLogger(DMNIncrementalCompilationTest.class);

    public DMNIncrementalCompilationTest(final boolean useExecModelCompiler, boolean canonicalKieModule) {
        super(useExecModelCompiler, canonicalKieModule);
    }

    @Test
    public void testUpgrade() throws Exception {
        final KieServices ks = Factory.get();
        final ReleaseId releaseId_v10 = ks.newReleaseId("org.kie", "dmn-test-PR1997", "1.0");
        KieHelper.createAndDeployJar(ks, releaseId_v10, wrapWithDroolsModelResource(ks, ks.getResources().newClassPathResource("/org/kie/dmn/core/incrementalcompilation/v1/20180731-pr1997.dmn", this.getClass()).setTargetPath("20180731-pr1997.dmn"), ks.getResources().newClassPathResource("/org/kie/dmn/core/incrementalcompilation/v1/Person.java", this.getClass()).setTargetPath("acme/Person.java")));
        final KieContainer kieContainer = ks.newKieContainer(releaseId_v10);
        final DMNRuntime runtime = DMNRuntimeUtil.typeSafeGetKieRuntime(kieContainer);
        checkTestUpgrade(kieContainer, runtime, "setFirstName", "setLastName", "Hello John Doe, your age is: 47");
        final ReleaseId releaseId_v11 = ks.newReleaseId("org.kie", "dmn-test-PR1997", "1.1");
        KieHelper.createAndDeployJar(ks, releaseId_v11, wrapWithDroolsModelResource(ks, ks.getResources().newClassPathResource("/org/kie/dmn/core/incrementalcompilation/v2/20180731-pr1997.dmn", this.getClass()).setTargetPath("20180731-pr1997.dmn"), ks.getResources().newClassPathResource("/org/kie/dmn/core/incrementalcompilation/v2/Person.java", this.getClass()).setTargetPath("acme/Person.java")));
        kieContainer.updateToVersion(releaseId_v11);
        checkTestUpgrade(kieContainer, runtime, "setFN", "setLN", "UPGRADED Hello John Doe, your age is: 47");
    }
}

