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
package org.drools.verifier;


import ResourceType.DRL;
import Severity.ERROR;
import Severity.NOTE;
import Severity.WARNING;
import java.util.Collections;
import org.drools.core.io.impl.ClassPathResource;
import org.drools.verifier.builder.ScopesAgendaFilter;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.data.VerifierReport;
import org.junit.Assert;
import org.junit.Test;


public class VerifyingScopeTest {
    @Test
    public void testSingleRule() {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();
        VerifierConfiguration vConfiguration = vBuilder.newVerifierConfiguration();
        // Check that the builder works.
        Assert.assertFalse(vBuilder.hasErrors());
        Assert.assertEquals(0, vBuilder.getErrors().size());
        vConfiguration.getVerifyingResources().put(new ClassPathResource("VerifyingScope.drl", Verifier.class), DRL);
        Verifier verifier = vBuilder.newVerifier(vConfiguration);
        verifier.addResourcesToVerify(new ClassPathResource("Misc3.drl", Verifier.class), DRL);
        Assert.assertFalse(verifier.hasErrors());
        Assert.assertEquals(0, verifier.getErrors().size());
        boolean works = verifier.fireAnalysis(new ScopesAgendaFilter(true, ScopesAgendaFilter.VERIFYING_SCOPE_SINGLE_RULE));
        if (!works) {
            for (VerifierError error : verifier.getErrors()) {
                System.out.println(error.getMessage());
            }
            Assert.fail("Error when building in verifier");
        }
        VerifierReport result = verifier.getResult();
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.getBySeverity(ERROR).size());
        Assert.assertEquals(0, result.getBySeverity(WARNING).size());
        Assert.assertEquals(6, result.getBySeverity(NOTE).size());
    }

    @Test
    public void testNothing() {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();
        VerifierConfiguration vConfiguration = vBuilder.newVerifierConfiguration();
        // Check that the builder works.
        Assert.assertFalse(vBuilder.hasErrors());
        Assert.assertEquals(0, vBuilder.getErrors().size());
        vConfiguration.getVerifyingResources().put(new ClassPathResource("VerifyingScope.drl", Verifier.class), DRL);
        Verifier verifier = vBuilder.newVerifier(vConfiguration);
        verifier.addResourcesToVerify(new ClassPathResource("Misc3.drl", Verifier.class), DRL);
        Assert.assertFalse(verifier.hasErrors());
        Assert.assertEquals(0, verifier.getErrors().size());
        boolean works = verifier.fireAnalysis(new ScopesAgendaFilter(true, Collections.EMPTY_LIST));
        Assert.assertTrue(works);
        VerifierReport result = verifier.getResult();
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.getBySeverity(ERROR).size());
        Assert.assertEquals(0, result.getBySeverity(WARNING).size());
        Assert.assertEquals(2, result.getBySeverity(NOTE).size());
    }

    @Test
    public void testDecisionTable() {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();
        VerifierConfiguration vConfiguration = vBuilder.newVerifierConfiguration();
        // Check that the builder works.
        Assert.assertFalse(vBuilder.hasErrors());
        Assert.assertEquals(0, vBuilder.getErrors().size());
        vConfiguration.getVerifyingResources().put(new ClassPathResource("VerifyingScope.drl", Verifier.class), DRL);
        Verifier verifier = vBuilder.newVerifier(vConfiguration);
        verifier.addResourcesToVerify(new ClassPathResource("Misc3.drl", Verifier.class), DRL);
        Assert.assertFalse(verifier.hasErrors());
        Assert.assertEquals(0, verifier.getErrors().size());
        boolean works = verifier.fireAnalysis(new ScopesAgendaFilter(false, ScopesAgendaFilter.VERIFYING_SCOPE_DECISION_TABLE));
        Assert.assertTrue(works);
        VerifierReport result = verifier.getResult();
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.getBySeverity(ERROR).size());
        Assert.assertEquals(0, result.getBySeverity(WARNING).size());
        Assert.assertEquals(2, result.getBySeverity(NOTE).size());
    }
}

