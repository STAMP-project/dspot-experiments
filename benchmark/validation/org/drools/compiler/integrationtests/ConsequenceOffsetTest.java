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


import ResourceType.DRL;
import org.drools.compiler.lang.descr.RuleDescr;
import org.junit.Assert;
import org.junit.Test;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;


public class ConsequenceOffsetTest {
    @Test
    public void testConsequenceOffset() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newInputStreamResource(ConsequenceOffsetTest.class.getResourceAsStream("test_consequenceOffset.drl")), DRL);
        Assert.assertFalse(kbuilder.hasErrors());
        int offset = -1;
        Assert.assertFalse(kbuilder.hasErrors());
        for (final RuleDescr rule : getPackageDescrs("com.sample").get(0).getRules()) {
            if (rule.getName().equals("test")) {
                offset = rule.getConsequenceOffset();
            }
        }
        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newInputStreamResource(ConsequenceOffsetTest.class.getResourceAsStream("test_consequenceOffset2.drl")), DRL);
        kbuilder.add(ResourceFactory.newInputStreamResource(ConsequenceOffsetTest.class.getResourceAsStream("test_consequenceOffset.drl")), DRL);
        Assert.assertFalse(kbuilder.hasErrors());
        for (final RuleDescr rule : getPackageDescrs("com.sample").get(0).getRules()) {
            if (rule.getName().equals("test")) {
                Assert.assertEquals(offset, rule.getConsequenceOffset());
                return;
            }
        }
        Assert.fail();
    }

    @Test
    public void testLargeSetOfImports() {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newInputStreamResource(ConsequenceOffsetTest.class.getResourceAsStream("test_consequenceOffsetImports.drl")), DRL);
        Assert.assertFalse(kbuilder.hasErrors());
    }
}

