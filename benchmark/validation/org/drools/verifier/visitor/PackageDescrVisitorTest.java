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
package org.drools.verifier.visitor;


import VerifierComponentType.SUB_PATTERN;
import VerifierComponentType.SUB_RULE;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.verifier.Verifier;
import org.drools.verifier.components.SubPattern;
import org.drools.verifier.components.SubRule;
import org.drools.verifier.data.VerifierComponent;
import org.drools.verifier.data.VerifierData;
import org.junit.Assert;
import org.junit.Test;


public class PackageDescrVisitorTest {
    private VerifierData verifierData;

    private PackageDescrVisitor packageDescrVisitor;

    @Test
    public void testVisit() throws Exception {
        PackageDescr packageDescr = getPackageDescr(Verifier.class.getResourceAsStream("Misc3.drl"));
        Assert.assertNotNull(packageDescr);
        packageDescrVisitor.visitPackageDescr(packageDescr);
        Collection<VerifierComponent> all = verifierData.getAll();
        Set<String> names = new HashSet<String>();
        for (VerifierComponent verifierComponent : all) {
            String path = verifierComponent.getPath();
            System.out.println(("-" + verifierComponent));
            if ((verifierComponent.getDescr()) != null) {
                System.out.println((((((" \n\t\t => " + (verifierComponent.getDescr().getLine())) + ":") + (+(verifierComponent.getDescr().getEndLine()))) + " ") + (verifierComponent.getDescr().getText())));
            } else {
                System.out.println((" \n\t\t => null for " + (verifierComponent.getClass().getSimpleName())));
            }
            if (names.contains(path)) {
                Assert.fail(("Dublicate path " + path));
            } else {
                names.add(path);
            }
        }
        Assert.assertNotNull(all);
        Assert.assertEquals(45, all.size());
    }

    @Test
    public void testSubPatterns() throws Exception {
        PackageDescr packageDescr = getPackageDescr(getClass().getResourceAsStream("SubPattern.drl"));
        Assert.assertNotNull(packageDescr);
        packageDescrVisitor.visitPackageDescr(packageDescr);
        Collection<VerifierComponent> all = verifierData.getAll();
        Assert.assertNotNull(all);
        SubPattern test1SubPattern = null;
        SubPattern test2SubPattern = null;
        SubRule test1SubRule = null;
        SubRule test2SubRule = null;
        for (VerifierComponent verifierComponent : all) {
            // System.out.println( verifierComponent );
            System.out.println(("-" + verifierComponent));
            if ((verifierComponent.getDescr()) != null) {
                System.out.println((((((" \n\t\t => " + (verifierComponent.getDescr().getLine())) + ":") + (+(verifierComponent.getDescr().getEndLine()))) + " ") + (verifierComponent.getDescr().getText())));
            } else {
                System.out.println((" \n\t\t => null for " + (verifierComponent.getClass().getSimpleName())));
            }
            if (verifierComponent.getVerifierComponentType().equals(SUB_PATTERN)) {
                SubPattern subPattern = ((SubPattern) (verifierComponent));
                if ("Test 1".equals(subPattern.getRuleName())) {
                    Assert.assertNull(test1SubPattern);
                    test1SubPattern = subPattern;
                } else
                    if ("Test 2".equals(subPattern.getRuleName())) {
                        Assert.assertNull(test2SubPattern);
                        test2SubPattern = subPattern;
                    }

            }
            if (verifierComponent.getVerifierComponentType().equals(SUB_RULE)) {
                SubRule subRule = ((SubRule) (verifierComponent));
                if ("Test 1".equals(subRule.getRuleName())) {
                    Assert.assertNull(test1SubRule);
                    test1SubRule = subRule;
                } else
                    if ("Test 2".equals(subRule.getRuleName())) {
                        Assert.assertNull(test2SubRule);
                        test2SubRule = subRule;
                    }

            }
        }
        Assert.assertNotNull(test1SubPattern);
        Assert.assertEquals(3, test1SubPattern.getItems().size());
        Assert.assertNotNull(test2SubPattern);
        Assert.assertEquals(3, test2SubPattern.getItems().size());
        Assert.assertNotNull(test1SubRule);
        Assert.assertEquals(1, test1SubRule.getItems().size());
        Assert.assertNotNull(test2SubRule);
        Assert.assertEquals(1, test2SubRule.getItems().size());
    }
}

