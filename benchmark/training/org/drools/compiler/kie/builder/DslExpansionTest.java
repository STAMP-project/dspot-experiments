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
package org.drools.compiler.kie.builder;


import KieServices.Factory;
import java.util.List;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;


/**
 * Test for DSL expansion with KieBuilder
 */
public class DslExpansionTest {
    @Test
    public void testDSLExpansion_MessageImplNPE() throws Exception {
        final KieServices ks = Factory.get();
        final ReleaseId releaseId = ks.newReleaseId("org.kie", "dsl-test", "1.0");
        final KieModuleModel kproj = ks.newKieModuleModel();
        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML()).writePomXML(KieBuilderImpl.generatePomXml(releaseId)).write("src/main/resources/KBase1/test-dsl.dsl", createDSL()).write("src/main/resources/KBase1/test-rule.dslr", createDRL());
        final KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        final List<Message> messages = kieBuilder.buildAll().getResults().getMessages();
        if (!(messages.isEmpty())) {
            for (final Message m : messages) {
                System.out.println(m.getText());
            }
        }
        Assert.assertTrue(messages.isEmpty());
    }

    @Test
    public void testDSLExpansion_NoExpansion() throws Exception {
        final KieServices ks = Factory.get();
        final ReleaseId releaseId = ks.newReleaseId("org.kie", "dsl-test", "1.0");
        final KieModuleModel kproj = ks.newKieModuleModel();
        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML()).writePomXML(KieBuilderImpl.generatePomXml(releaseId)).write("src/main/resources/KBase1/test-dsl.dsl", createDSL()).write("src/main/resources/KBase1/test-rule.drl", createDRL());
        final KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        final List<Message> messages = kieBuilder.buildAll().getResults().getMessages();
        if (!(messages.isEmpty())) {
            for (final Message m : messages) {
                System.out.println(m.getText());
            }
        }
        Assert.assertFalse(messages.isEmpty());
    }
}

