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
package org.kie.dmn.core;


import KieServices.Factory;
import RuntimeTypeCheckOption.PROPERTY_NAME;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.compiler.RuntimeTypeCheckOption;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.dmn.core.util.KieHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DMNRuntimeTypeCheckTest extends BaseInterpretedVsCompiledTest {
    public static final Logger LOG = LoggerFactory.getLogger(DMNRuntimeTypeCheckTest.class);

    private static final KieServices ks = Factory.get();

    public DMNRuntimeTypeCheckTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    @Test
    public void testDefaultNoTypeCheck() {
        // do NOT use the DMNRuntimeUtil as that enables typeSafe check override for runtime.
        final KieContainer kieContainer = KieHelper.getKieContainer(DMNRuntimeTypeCheckTest.ks.newReleaseId("org.kie", ("dmn-test-" + (UUID.randomUUID())), "1.0"), DMNRuntimeTypeCheckTest.ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        assertNoTypeCheck(runtime);
    }

    @Test
    public void testAskTypeCheckInKModule() {
        final DMNRuntime runtime = getRuntimeWithTypeCheckOption("true", DMNRuntimeTypeCheckTest.ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        assertPerformTypeCheck(runtime);
    }

    @Test
    public void testAskTypeCheckWithGlobalEnvVariable() {
        System.setProperty(PROPERTY_NAME, "true");
        // do NOT use the DMNRuntimeUtil as that enables typeSafe check override for runtime.
        final KieContainer kieContainer = KieHelper.getKieContainer(DMNRuntimeTypeCheckTest.ks.newReleaseId("org.kie", ("dmn-test-" + (UUID.randomUUID())), "1.0"), DMNRuntimeTypeCheckTest.ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        assertPerformTypeCheck(runtime);
        System.clearProperty(PROPERTY_NAME);
    }

    @Test
    public void testExplicitDisableTypeCheckInKModule() {
        final DMNRuntime runtime = getRuntimeWithTypeCheckOption("false", DMNRuntimeTypeCheckTest.ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        assertNoTypeCheck(runtime);
    }

    @Test
    public void testUnreckonOptionTypeCheckInKModuleDefaultsToNoTypeCheck() {
        final DMNRuntime runtime = getRuntimeWithTypeCheckOption("boh", DMNRuntimeTypeCheckTest.ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        assertNoTypeCheck(runtime);
    }

    @Test
    public void testEmptyOptionTypeCheckInKModuleDefaultsToNoTypeCheck() {
        final DMNRuntime runtime = getRuntimeWithTypeCheckOption("", DMNRuntimeTypeCheckTest.ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        assertNoTypeCheck(runtime);
    }

    @Test
    public void testDefaultNoTypeCheckButOverrideRuntime() {
        // do NOT use the DMNRuntimeUtil as that enables typeSafe check override for runtime.
        final KieContainer kieContainer = KieHelper.getKieContainer(DMNRuntimeTypeCheckTest.ks.newReleaseId("org.kie", ("dmn-test-" + (UUID.randomUUID())), "1.0"), DMNRuntimeTypeCheckTest.ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        ((DMNRuntimeImpl) (runtime)).setOption(new RuntimeTypeCheckOption(true));
        assertPerformTypeCheck(runtime);
    }

    @Test
    public void testExplicitDisableTypeCheckInKModuleButOverrideRuntime() {
        final DMNRuntime runtime = getRuntimeWithTypeCheckOption("false", DMNRuntimeTypeCheckTest.ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        ((DMNRuntimeImpl) (runtime)).setOption(new RuntimeTypeCheckOption(true));
        assertPerformTypeCheck(runtime);
    }

    @Test
    public void testAskTypeCheckInKModuleButOverrideRuntime() {
        final DMNRuntime runtime = getRuntimeWithTypeCheckOption("true", DMNRuntimeTypeCheckTest.ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        ((DMNRuntimeImpl) (runtime)).setOption(new RuntimeTypeCheckOption(false));
        assertPerformTypeCheck(runtime);
    }

    @Test
    public void testMisleadingNPEbyAPIusage() {
        // do NOT use the DMNRuntimeUtil as that enables typeSafe check override for runtime.
        final KieContainer kieContainer = KieHelper.getKieContainer(DMNRuntimeTypeCheckTest.ks.newReleaseId("org.kie", ("dmn-test-" + (UUID.randomUUID())), "1.0"), DMNRuntimeTypeCheckTest.ks.getResources().newClassPathResource("simple-item-def.dmn", this.getClass()));
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        final DMNModel dmnModel = runtime.getModel("<wrong>", "<wrong>");
        // please notice an end-user of the API might not having checked the result of the previous call is not a null.
        final DMNContext emptyContext = DMNFactory.newContext();
        try {
            final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
            DMNRuntimeTypeCheckTest.LOG.debug("{}", dmnResult);
            Assert.fail("");
        } catch (final Exception e) {
            Assert.assertTrue(e.getMessage().contains("'model'"));
            /* java.lang.NullPointerException: Kie DMN API parameter 'model' cannot be null.
            at java.util.Objects.requireNonNull(Objects.java:290)
            at org.kie.dmn.core.impl.DMNRuntimeImpl.evaluateAll(DMNRuntimeImpl.java:123)
            at org.kie.dmn.core.DMNRuntimeTypeCheckTest.testMisleadingNPEbyAPIusage(DMNRuntimeTypeCheckTest.java:199)
             */
        }
    }
}

