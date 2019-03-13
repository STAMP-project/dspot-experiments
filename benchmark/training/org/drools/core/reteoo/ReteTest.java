/**
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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
package org.drools.core.reteoo;


import PropagationContext.Type.INSERTION;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.test.model.Cheese;
import org.drools.core.test.model.DroolsTestCase;
import org.junit.Assert;
import org.junit.Test;


public class ReteTest extends DroolsTestCase {
    private PropagationContextFactory pctxFactory;

    private InternalKnowledgeBase kBase;

    private BuildContext buildContext;

    private EntryPointNode entryPoint;

    /**
     * Tests ObjectTypeNodes are correctly added to the Rete object
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testObjectTypeNodes() throws Exception {
        final Rete rete = kBase.getRete();
        final ObjectTypeNode objectTypeNode = new ObjectTypeNode(1, this.entryPoint, new ClassObjectType(Object.class), buildContext);
        objectTypeNode.attach(buildContext);
        final ObjectTypeNode stringTypeNode = new ObjectTypeNode(2, this.entryPoint, new ClassObjectType(String.class), buildContext);
        stringTypeNode.attach(buildContext);
        final List<ObjectTypeNode> list = rete.getObjectTypeNodes();
        // Check the ObjectTypeNodes are correctly added to Rete
        Assert.assertEquals(3, list.size());
        Assert.assertTrue(list.contains(objectTypeNode));
        Assert.assertTrue(list.contains(stringTypeNode));
    }

    /**
     * Tests that interfaces and parent classes for an asserted  class are  cached, for  quick future iterations
     */
    @Test
    public void testCache() {
        StatefulKnowledgeSessionImpl ksession = ((StatefulKnowledgeSessionImpl) (kBase.newKieSession()));
        // Create a Rete network with ObjectTypeNodes for List, Collection and ArrayList
        final Rete rete = kBase.getRete();
        ObjectTypeNode objectTypeNode = new ObjectTypeNode(1, this.entryPoint, new ClassObjectType(List.class), buildContext);
        objectTypeNode.attach(buildContext);
        MockObjectSink sink = new MockObjectSink();
        objectTypeNode.addObjectSink(sink);
        objectTypeNode = new ObjectTypeNode(1, this.entryPoint, new ClassObjectType(Collection.class), buildContext);
        objectTypeNode.attach(buildContext);
        sink = new MockObjectSink();
        objectTypeNode.addObjectSink(sink);
        objectTypeNode = new ObjectTypeNode(1, this.entryPoint, new ClassObjectType(ArrayList.class), buildContext);
        objectTypeNode.attach(buildContext);
        sink = new MockObjectSink();
        objectTypeNode.addObjectSink(sink);
        // ArrayList matches all three ObjectTypeNodes
        final DefaultFactHandle h1 = new DefaultFactHandle(1, new ArrayList());
        rete.assertObject(h1, pctxFactory.createPropagationContext(0, INSERTION, null, null, null), ksession);
        // LinkedList matches two ObjectTypeNodes
        h1.setObject(new LinkedList());
        rete.assertObject(h1, pctxFactory.createPropagationContext(0, INSERTION, null, null, null), ksession);
        ClassObjectTypeConf conf = ((ClassObjectTypeConf) (ksession.getObjectTypeConfigurationRegistry().getObjectTypeConf(this.entryPoint.getEntryPoint(), new ArrayList())));
        assertLength(3, conf.getObjectTypeNodes());
        conf = ((ClassObjectTypeConf) (ksession.getObjectTypeConfigurationRegistry().getObjectTypeConf(this.entryPoint.getEntryPoint(), new ArrayList())));
        assertLength(3, conf.getObjectTypeNodes());
    }

    /**
     * Test asserts correctly propagate
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAssertObject() throws Exception {
        StatefulKnowledgeSessionImpl ksession = ((StatefulKnowledgeSessionImpl) (kBase.newKieSession()));
        // Create a Rete network with ObjectTypeNodes for List, Collection and ArrayList
        final Rete rete = kBase.getRete();
        final ObjectTypeNode objectTypeNode = new ObjectTypeNode(1, this.entryPoint, new ClassObjectType(List.class), buildContext);
        objectTypeNode.attach(buildContext);
        final MockObjectSink sink1 = new MockObjectSink();
        objectTypeNode.addObjectSink(sink1);
        // There are no String ObjectTypeNodes, make sure its not propagated
        final String string = "String";
        final DefaultFactHandle h1 = new DefaultFactHandle(1, string);
        rete.assertObject(h1, pctxFactory.createPropagationContext(0, INSERTION, null, null, null), ksession);
        assertLength(0, sink1.getAsserted());
        // There is a List ObjectTypeNode, make sure it was propagated
        final List list = new ArrayList();
        final DefaultFactHandle h2 = new DefaultFactHandle(1, list);
        rete.assertObject(h2, pctxFactory.createPropagationContext(0, INSERTION, null, null, null), ksession);
        ksession.fireAllRules();
        final List asserted = sink1.getAsserted();
        assertLength(1, asserted);
        final Object[] results = ((Object[]) (asserted.get(0)));
        Assert.assertSame(list, getObject());
    }

    @Test
    public void testAssertObjectWithNoMatchingObjectTypeNode() {
        StatefulKnowledgeSessionImpl ksession = ((StatefulKnowledgeSessionImpl) (kBase.newKieSession()));
        final Rete rete = kBase.getRete();
        Assert.assertEquals(1, rete.getObjectTypeNodes().size());
        List list = new ArrayList();
        ksession.insert(list);
        ksession.fireAllRules();
        Assert.assertEquals(1, rete.getObjectTypeNodes().size());
    }

    /**
     * All objects deleted from a RootNode must be propagated to all children
     * ObjectTypeNodes.
     */
    @Test
    public void testRetractObject() throws Exception {
        StatefulKnowledgeSessionImpl ksession = ((StatefulKnowledgeSessionImpl) (kBase.newKieSession()));
        // Create a Rete network with ObjectTypeNodes for List, Collection and ArrayList
        final Rete rete = kBase.getRete();
        final ObjectTypeNode objectTypeNode = new ObjectTypeNode(1, this.entryPoint, new ClassObjectType(List.class), buildContext);
        objectTypeNode.attach(buildContext);
        final MockObjectSink sink1 = new MockObjectSink();
        objectTypeNode.addObjectSink(sink1);
        // There are no String ObjectTypeNodes, make sure its not propagated
        final String string = "String";
        final DefaultFactHandle h1 = new DefaultFactHandle(1, string);
        rete.assertObject(h1, pctxFactory.createPropagationContext(0, INSERTION, null, null, null), ksession);
        assertLength(0, sink1.getAsserted());
        assertLength(0, sink1.getRetracted());
        // There is a List ObjectTypeNode, make sure it was propagated
        final List list = new ArrayList();
        final DefaultFactHandle h2 = new DefaultFactHandle(1, list);
        // need  to assert first, to force it to build  up the cache
        rete.assertObject(h2, pctxFactory.createPropagationContext(0, INSERTION, null, null, null), ksession);
        rete.retractObject(h2, pctxFactory.createPropagationContext(0, INSERTION, null, null, null), ksession);
        ksession.fireAllRules();
        final List retracted = sink1.getRetracted();
        assertLength(1, retracted);
        final Object[] results = ((Object[]) (retracted.get(0)));
        Assert.assertSame(list, getObject());
    }

    @Test
    public void testIsShadowed() {
        StatefulKnowledgeSessionImpl ksession = ((StatefulKnowledgeSessionImpl) (kBase.newKieSession()));
        // Create a Rete network with ObjectTypeNodes for List, Collection and ArrayList
        final Rete rete = kBase.getRete();
        final ObjectTypeNode objectTypeNode = new ObjectTypeNode(1, this.entryPoint, new ClassObjectType(Cheese.class), buildContext);
        objectTypeNode.attach(buildContext);
        final MockObjectSink sink1 = new MockObjectSink();
        objectTypeNode.addObjectSink(sink1);
        // There are no String ObjectTypeNodes, make sure its not propagated
        final Cheese cheese = new Cheese("brie", 15);
        final DefaultFactHandle h1 = new DefaultFactHandle(1, cheese);
        rete.assertObject(h1, pctxFactory.createPropagationContext(0, INSERTION, null, null, null), ksession);
        ksession.fireAllRules();
        final Object[] results = ((Object[]) (sink1.getAsserted().get(0)));
    }

    public static class TestBuildContext extends BuildContext {
        InternalKnowledgeBase kBase;

        TestBuildContext(InternalKnowledgeBase kBase) {
            super(kBase);
            this.kBase = kBase;
        }
    }
}

