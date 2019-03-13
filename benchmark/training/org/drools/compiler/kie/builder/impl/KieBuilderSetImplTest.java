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
package org.drools.compiler.kie.builder.impl;


import KieServices.Factory;
import org.drools.compiler.CommonTestMethodBase;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.io.Resource;
import org.kie.internal.builder.IncrementalResults;


public class KieBuilderSetImplTest extends CommonTestMethodBase {
    @Test
    public void testBuild() throws Exception {
        final KieServices ks = Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write("src/main/resources/rule%201.drl", ruleContent());
        final KieBuilderSetImpl kieBuilderSet = new KieBuilderSetImpl(kieBuilder(ks, kfs));
        kieBuilderSet.setFiles(new String[]{ "src/main/resources/rule%201.drl" });
        final IncrementalResults build = kieBuilderSet.build();
        Assert.assertEquals(0, build.getAddedMessages().size());
        Assert.assertEquals(0, build.getRemovedMessages().size());
    }

    @Test
    public void testDummyResourceWithAnEncodedFileName() {
        final Resource dummyResource = new KieBuilderSetImpl.DummyResource("Dummy%20Resource");
        final Resource testResource = new KieBuilderSetImpl.DummyResource("Dummy Resource");
        Assert.assertEquals(testResource, dummyResource);
    }

    @Test
    public void testDummyResourceWithWrongEncodedFileName() {
        final Resource dummyResource = new KieBuilderSetImpl.DummyResource("Dummy 100%");
        Assert.assertEquals(dummyResource.getSourcePath(), "Dummy 100%");
    }
}

