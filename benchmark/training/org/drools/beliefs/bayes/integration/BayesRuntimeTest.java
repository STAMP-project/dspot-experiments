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
package org.drools.beliefs.bayes.integration;


import ResourceType.BAYES;
import org.drools.beliefs.bayes.BayesInstance;
import org.drools.beliefs.bayes.runtime.BayesRuntime;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.junit.Assert;
import org.junit.Test;
import org.kie.internal.io.ResourceFactory;


public class BayesRuntimeTest {
    @Test
    public void testBayesRuntimeManager() throws Exception {
        KnowledgeBuilderImpl kbuilder = new KnowledgeBuilderImpl();
        kbuilder.add(ResourceFactory.newClassPathResource("Garden.xmlbif", AssemblerTest.class), BAYES);
        InternalKnowledgeBase kbase = getKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSessionImpl ksession = ((StatefulKnowledgeSessionImpl) (kbase.newKieSession()));
        BayesRuntime bayesRuntime = ksession.getKieRuntime(BayesRuntime.class);
        BayesInstance<Garden> instance = bayesRuntime.createInstance(Garden.class);
        Assert.assertNotNull(instance);
    }
}

