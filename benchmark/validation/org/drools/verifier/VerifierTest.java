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
import VerifierComponentType.FIELD;
import VerifierComponentType.OBJECT_TYPE;
import java.io.IOException;
import java.util.Collection;
import java.util.jar.JarInputStream;
import org.drools.core.io.impl.ClassPathResource;
import org.drools.verifier.builder.VerifierBuilder;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.junit.Assert;
import org.junit.Test;


public class VerifierTest {
    @Test
    public void testVerifier() {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();
        // Check that the builder works.
        Assert.assertFalse(vBuilder.hasErrors());
        Assert.assertEquals(0, vBuilder.getErrors().size());
        Verifier verifier = vBuilder.newVerifier();
        verifier.addResourcesToVerify(new ClassPathResource("Misc3.drl", Verifier.class), DRL);
        Assert.assertFalse(verifier.hasErrors());
        Assert.assertEquals(0, verifier.getErrors().size());
        boolean works = verifier.fireAnalysis();
        Assert.assertTrue(works);
        VerifierReport result = verifier.getResult();
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.getBySeverity(ERROR).size());
        Assert.assertEquals(6, result.getBySeverity(WARNING).size());
        Assert.assertEquals(1, result.getBySeverity(NOTE).size());
    }

    @Test
    public void testFactTypesFromJar() {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();
        // Check that the builder works.
        Assert.assertFalse(vBuilder.hasErrors());
        Assert.assertEquals(0, vBuilder.getErrors().size());
        Verifier verifier = vBuilder.newVerifier();
        try {
            JarInputStream jar = new JarInputStream(this.getClass().getResourceAsStream("model.jar"));
            verifier.addObjectModel(jar);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
        verifier.addResourcesToVerify(new ClassPathResource("imports.drl", Verifier.class), DRL);
        Assert.assertFalse(verifier.hasErrors());
        Assert.assertEquals(0, verifier.getErrors().size());
        boolean works = verifier.fireAnalysis();
        Assert.assertTrue(works);
        VerifierReport result = verifier.getResult();
        Collection<ObjectType> objectTypes = result.getVerifierData().getAll(OBJECT_TYPE);
        Assert.assertNotNull(objectTypes);
        Assert.assertEquals(3, objectTypes.size());
        Collection<Field> fields = result.getVerifierData().getAll(FIELD);
        Assert.assertNotNull(fields);
        Assert.assertEquals(10, fields.size());
    }

    @Test
    public void testFactTypesFromJarAndDeclarations() {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();
        // Check that the builder works.
        Assert.assertFalse(vBuilder.hasErrors());
        Assert.assertEquals(0, vBuilder.getErrors().size());
        Verifier verifier = vBuilder.newVerifier();
        try {
            JarInputStream jar = new JarInputStream(this.getClass().getResourceAsStream("model.jar"));
            verifier.addObjectModel(jar);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
        verifier.addResourcesToVerify(new ClassPathResource("importsAndDeclarations.drl", Verifier.class), DRL);
        Assert.assertFalse(verifier.hasErrors());
        Assert.assertEquals(0, verifier.getErrors().size());
        boolean works = verifier.fireAnalysis();
        Assert.assertTrue(works);
        VerifierReport result = verifier.getResult();
        Collection<ObjectType> objectTypes = result.getVerifierData().getAll(OBJECT_TYPE);
        for (ObjectType objectType : objectTypes) {
            if (objectType.getName().equals("VoiceCall")) {
                Assert.assertEquals(4, objectType.getMetadata().keySet().size());
            }
        }
        Assert.assertNotNull(objectTypes);
        Assert.assertEquals(4, objectTypes.size());
        Collection<Field> fields = result.getVerifierData().getAll(FIELD);
        Assert.assertNotNull(fields);
        Assert.assertEquals(11, fields.size());
    }

    @Test
    public void testCustomRule() {
        VerifierBuilder vBuilder = VerifierBuilderFactory.newVerifierBuilder();
        VerifierConfiguration vConfiguration = vBuilder.newVerifierConfiguration();
        // Check that the builder works.
        Assert.assertFalse(vBuilder.hasErrors());
        Assert.assertEquals(0, vBuilder.getErrors().size());
        vConfiguration.getVerifyingResources().put(new ClassPathResource("FindPatterns.drl", Verifier.class), DRL);
        Verifier verifier = vBuilder.newVerifier(vConfiguration);
        verifier.addResourcesToVerify(new ClassPathResource("Misc3.drl", Verifier.class), DRL);
        Assert.assertFalse(verifier.hasErrors());
        Assert.assertEquals(0, verifier.getErrors().size());
        boolean works = verifier.fireAnalysis();
        if (!works) {
            for (VerifierError error : verifier.getErrors()) {
                System.out.println(error.getMessage());
            }
            Assert.fail("Could not run verifier");
        }
        Assert.assertTrue(works);
        VerifierReport result = verifier.getResult();
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.getBySeverity(ERROR).size());
        Assert.assertEquals(0, result.getBySeverity(WARNING).size());
        Assert.assertEquals(6, result.getBySeverity(NOTE).size());
        for (VerifierMessageBase m : result.getBySeverity(NOTE)) {
            Assert.assertEquals("This pattern was found.", m.getMessage());
        }
    }
}

