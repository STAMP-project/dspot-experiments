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
package org.drools.compiler.common;


import ResourceType.DRL;
import java.util.ArrayList;
import java.util.List;
import org.drools.core.common.TerminalNodeIterator;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.util.Iterator;
import org.junit.Assert;
import org.junit.Test;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;


public class TerminalNodeIteratorTest {
    @Test
    public void testTerminalNodeListener() {
        String str = "package org.kie.test \n" + ((((((((((((((((((("\n" + "rule rule1 when\n") + "then\n") + "end\n") + "rule rule2 when\n") + "then\n") + "end\n") + "rule rule3 when\n") + "    Object()") + "then\n") + "end\n") + "rule rule4 when\n") + "    Object()") + "then\n") + "end\n") + "rule rule5 when\n")// this will result in two terminal nodes
         + "    Object() or\n") + "    Object()\n") + "then\n") + "end\n");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), DRL);
        if (kbuilder.hasErrors()) {
            Assert.fail(kbuilder.getErrors().toString());
        }
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        List<String> nodes = new ArrayList<String>();
        Iterator it = TerminalNodeIterator.iterator(kbase);
        for (TerminalNode node = ((TerminalNode) (it.next())); node != null; node = ((TerminalNode) (it.next()))) {
            nodes.add(getRule().getName());
        }
        Assert.assertEquals(6, nodes.size());
        Assert.assertTrue(nodes.contains("rule1"));
        Assert.assertTrue(nodes.contains("rule2"));
        Assert.assertTrue(nodes.contains("rule3"));
        Assert.assertTrue(nodes.contains("rule4"));
        Assert.assertTrue(nodes.contains("rule5"));
        int first = nodes.indexOf("rule5");
        int second = nodes.lastIndexOf("rule5");
        Assert.assertTrue((first != second));
    }
}

