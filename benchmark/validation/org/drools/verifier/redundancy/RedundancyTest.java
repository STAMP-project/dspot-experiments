/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.drools.verifier.redundancy;


import ResourceType.DRL;
import java.util.Collection;
import org.drools.verifier.Verifier;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.report.components.Redundancy;
import org.drools.verifier.report.components.Subsumption;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.internal.io.ResourceFactory;


public class RedundancyTest {
    @Test
    public void testVerifierLiteralRestrictionRedundancy() throws Exception {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();
        Verifier verifier = vBuilder.newVerifier();
        verifier.addResourcesToVerify(ResourceFactory.newClassPathResource("RedundantRestrictions.drl", getClass()), DRL);
        Assert.assertFalse(verifier.hasErrors());
        boolean noProblems = verifier.fireAnalysis();
        Assert.assertTrue(noProblems);
        Collection<? extends Object> subsumptionList = getKnowledgeSession().getObjects(new ClassObjectFilter(Subsumption.class));
        Collection<? extends Object> redundancyList = getKnowledgeSession().getObjects(new ClassObjectFilter(Redundancy.class));
        Assert.assertEquals(2, subsumptionList.size());
        Assert.assertEquals(1, redundancyList.size());
        verifier.dispose();
    }
}

