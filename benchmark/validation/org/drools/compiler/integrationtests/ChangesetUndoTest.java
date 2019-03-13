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
package org.drools.compiler.integrationtests;


import ResourceType.CHANGE_SET;
import ResourceType.DRL;
import java.io.File;
import org.drools.core.io.impl.FileSystemResource;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.kie.api.definition.KiePackage;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;


public class ChangesetUndoTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private static final File[] resources = new File[5];

    private static final String drl1 = "package org.drools.test1; " + ("declare Foo id : int end \n" + "rule A when then end \n");

    private static final String drl2 = "package org.drools.test2; " + ("declare Foo2 id : Missing end \n" + "rule A when then end \n");

    private static final String drl3 = "package org.drools.test3; " + ("declare Bar id : int end \n" + "rule A when end \n");

    @Test
    public void testCompilationUndo() {
        final KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add(new FileSystemResource(ChangesetUndoTest.resources[0]), CHANGE_SET);
        Assert.assertTrue(knowledgeBuilder.hasErrors());
        knowledgeBuilder.undo();
        Assert.assertFalse(knowledgeBuilder.hasErrors());
        for (final KiePackage kp : knowledgeBuilder.getKnowledgePackages()) {
            Assert.assertTrue(kp.getRules().isEmpty());
            Assert.assertTrue(kp.getFactTypes().isEmpty());
        }
    }

    @Test
    public void testCompilationUndoAfterGoodResults() {
        final KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add(new FileSystemResource(ChangesetUndoTest.resources[1]), DRL);
        knowledgeBuilder.add(new FileSystemResource(ChangesetUndoTest.resources[4]), CHANGE_SET);
        Assert.assertTrue(knowledgeBuilder.hasErrors());
        knowledgeBuilder.undo();
        Assert.assertFalse(knowledgeBuilder.hasErrors());
        for (final KiePackage kp : knowledgeBuilder.getKnowledgePackages()) {
            if ("org.drools.test1".equals(kp.getName())) {
                Assert.assertEquals(1, kp.getRules().size());
                Assert.assertEquals(1, kp.getFactTypes().size());
            } else {
                Assert.assertTrue(kp.getRules().isEmpty());
                Assert.assertTrue(kp.getFactTypes().isEmpty());
            }
        }
    }
}

