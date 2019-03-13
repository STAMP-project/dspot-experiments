/**
 * This file is part of dependency-check-core.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) 2012 Jeremy Long. All Rights Reserved.
 */
package org.owasp.dependencycheck.analyzer;


import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseDBTestCase;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.Engine;
import org.owasp.dependencycheck.data.cpe.IndexEntry;
import org.owasp.dependencycheck.dependency.Dependency;
import org.owasp.dependencycheck.dependency.naming.Identifier;


/**
 *
 *
 * @author Jeremy Long
 */
public class CPEAnalyzerIT extends BaseDBTestCase {
    /**
     * Tests of buildSearch of class CPEAnalyzer.
     *
     * @throws Exception
     * 		is thrown when an IO Exception occurs.
     */
    @Test
    public void testBuildSearch() throws Exception {
        Set<String> productWeightings = new HashSet<>();// Collections.singleton("struts2");

        Set<String> vendorWeightings = new HashSet<>();// Collections.singleton("apache");

        Map<String, MutableInt> vendor = new HashMap<>();
        Map<String, MutableInt> product = new HashMap<>();
        vendor.put("apache software foundation", new MutableInt(1));
        product.put("struts 2 core", new MutableInt(1));
        CPEAnalyzer instance = new CPEAnalyzer();
        instance.initialize(getSettings());
        String queryText = instance.buildSearch(vendor, product, vendorWeightings, productWeightings);
        String expResult = "product:(struts 2 core) AND vendor:(apache software foundation)";
        Assert.assertTrue(expResult.equals(queryText));
        vendorWeightings.add("apache");
        productWeightings.add("struts2");
        queryText = instance.buildSearch(vendor, product, vendorWeightings, productWeightings);
        expResult = "product:(struts^2 2 core struts2^2) AND vendor:(apache^2 software foundation)";
        Assert.assertTrue(expResult.equals(queryText));
        instance.close();
    }

    /**
     * Test of determineCPE method, of class CPEAnalyzer.
     *
     * @throws Exception
     * 		is thrown when an exception occurs
     */
    @Test
    public void testDetermineCPE_full() throws Exception {
        CPEAnalyzer cpeAnalyzer = new CPEAnalyzer();
        try (Engine e = new Engine(getSettings())) {
            // update needs to be performed so that xtream can be tested
            e.doUpdates(true);
            cpeAnalyzer.initialize(getSettings());
            cpeAnalyzer.prepare(e);
            FileNameAnalyzer fnAnalyzer = new FileNameAnalyzer();
            fnAnalyzer.initialize(getSettings());
            fnAnalyzer.prepare(e);
            JarAnalyzer jarAnalyzer = new JarAnalyzer();
            jarAnalyzer.initialize(getSettings());
            jarAnalyzer.accept(new File("test.jar"));// trick analyzer into "thinking it is active"

            jarAnalyzer.prepare(e);
            HintAnalyzer hAnalyzer = new HintAnalyzer();
            hAnalyzer.initialize(getSettings());
            hAnalyzer.prepare(e);
            FalsePositiveAnalyzer fp = new FalsePositiveAnalyzer();
            fp.initialize(getSettings());
            fp.prepare(e);
            CpeSuppressionAnalyzer cpeSuppression = new CpeSuppressionAnalyzer();
            cpeSuppression.initialize(getSettings());
            cpeSuppression.prepare(e);
            // callDetermineCPE_full("hazelcast-2.5.jar", null, cpeAnalyzer, fnAnalyzer, jarAnalyzer, hAnalyzer, fp, cpeSuppression);
            // callDetermineCPE_full("spring-context-support-2.5.5.jar", "cpe:2.3:a:springsource:spring_framework:2.5.5:*:*:*:*:*:*:*", cpeAnalyzer, fnAnalyzer, jarAnalyzer, hAnalyzer, fp, cpeSuppression);
            callDetermineCPE_full("spring-core-3.0.0.RELEASE.jar", "cpe:2.3:a:pivotal_software:spring_framework:3.0.0:*:*:*:*:*:*:*", cpeAnalyzer, fnAnalyzer, jarAnalyzer, hAnalyzer, fp, cpeSuppression);
            // callDetermineCPE_full("spring-core-3.0.0.RELEASE.jar", "cpe:2.3:a:pivotal:spring_framework:3.0.0:*:*:*:*:*:*:*", cpeAnalyzer, fnAnalyzer, jarAnalyzer, hAnalyzer, fp, cpeSuppression);
            // callDetermineCPE_full("spring-core-3.0.0.RELEASE.jar", "cpe:2.3:a:springsource:spring_framework:3.0.0:*:*:*:*:*:*:*", cpeAnalyzer, fnAnalyzer, jarAnalyzer, hAnalyzer, fp, cpeSuppression);
            // 
            // callDetermineCPE_full("jaxb-xercesImpl-1.5.jar", null, cpeAnalyzer, fnAnalyzer, jarAnalyzer, hAnalyzer, fp, cpeSuppression);
            // callDetermineCPE_full("ehcache-core-2.2.0.jar", null, cpeAnalyzer, fnAnalyzer, jarAnalyzer, hAnalyzer, fp, cpeSuppression);
            // //updated test data set using more recent years no longer has mortbay_jetty - although it is still in the full NVD data set
            // //callDetermineCPE_full("org.mortbay.jetty.jar", "cpe:2.3:a:mortbay_jetty:jetty:4.2.27:*:*:*:*:*:*:*", cpeAnalyzer, fnAnalyzer, jarAnalyzer, hAnalyzer, fp, cpeSuppression);
            // callDetermineCPE_full("xstream-1.4.8.jar", "cpe:2.3:a:xstream_project:xstream:1.4.8:*:*:*:*:*:*:*", cpeAnalyzer, fnAnalyzer, jarAnalyzer, hAnalyzer, fp, cpeSuppression);
        } finally {
            cpeAnalyzer.close();
        }
    }

    /**
     * Test of determineCPE method, of class CPEAnalyzer.
     *
     * @throws Exception
     * 		is thrown when an exception occurs
     */
    @Test
    public void testDetermineCPE() throws Exception {
        // File file = new File(this.getClass().getClassLoader().getResource("struts2-core-2.1.2.jar").getPath());
        File file = BaseTest.getResourceAsFile(this, "struts2-core-2.1.2.jar");
        // File file = new File(this.getClass().getClassLoader().getResource("axis2-adb-1.4.1.jar").getPath());
        Dependency struts = new Dependency(file);
        CpeSuppressionAnalyzer suppressionAnalyzer = new CpeSuppressionAnalyzer();
        suppressionAnalyzer.initialize(getSettings());
        suppressionAnalyzer.prepare(null);
        FileNameAnalyzer fnAnalyzer = new FileNameAnalyzer();
        fnAnalyzer.analyze(struts, null);
        HintAnalyzer hintAnalyzer = new HintAnalyzer();
        hintAnalyzer.initialize(getSettings());
        hintAnalyzer.prepare(null);
        JarAnalyzer jarAnalyzer = new JarAnalyzer();
        jarAnalyzer.initialize(getSettings());
        jarAnalyzer.accept(new File("test.jar"));// trick analyzer into "thinking it is active"

        jarAnalyzer.prepare(null);
        jarAnalyzer.analyze(struts, null);
        hintAnalyzer.analyze(struts, null);
        // File fileCommonValidator = new File(this.getClass().getClassLoader().getResource("commons-validator-1.4.0.jar").getPath());
        File fileCommonValidator = BaseTest.getResourceAsFile(this, "commons-validator-1.4.0.jar");
        Dependency commonValidator = new Dependency(fileCommonValidator);
        jarAnalyzer.analyze(commonValidator, null);
        hintAnalyzer.analyze(commonValidator, null);
        // File fileSpring = new File(this.getClass().getClassLoader().getResource("spring-core-2.5.5.jar").getPath());
        File fileSpring = BaseTest.getResourceAsFile(this, "spring-core-2.5.5.jar");
        Dependency spring = new Dependency(fileSpring);
        jarAnalyzer.analyze(spring, null);
        hintAnalyzer.analyze(spring, null);
        // File fileSpring3 = new File(this.getClass().getClassLoader().getResource("spring-core-3.0.0.RELEASE.jar").getPath());
        File fileSpring3 = BaseTest.getResourceAsFile(this, "spring-core-3.0.0.RELEASE.jar");
        Dependency spring3 = new Dependency(fileSpring3);
        jarAnalyzer.analyze(spring3, null);
        hintAnalyzer.analyze(spring3, null);
        CPEAnalyzer instance = new CPEAnalyzer();
        try (Engine engine = new Engine(getSettings())) {
            engine.openDatabase(true, true);
            instance.initialize(getSettings());
            instance.prepare(engine);
            instance.determineCPE(commonValidator);
            instance.determineCPE(struts);
            instance.determineCPE(spring);
            instance.determineCPE(spring3);
            instance.close();
            suppressionAnalyzer.analyze(commonValidator, engine);
            commonValidator.getVulnerableSoftwareIdentifiers().forEach(( i) -> {
                fail(("Apache Common Validator found an unexpected CPE identifier - " + (i.getValue())));
            });
            String expResult = "cpe:2.3:a:apache:struts:2.1.2:*:*:*:*:*:*:*";
            Assert.assertTrue("Incorrect match size - struts", ((struts.getVulnerableSoftwareIdentifiers().size()) >= 1));
            boolean found = false;
            for (Identifier i : struts.getVulnerableSoftwareIdentifiers()) {
                if (expResult.equals(i.getValue())) {
                    found = true;
                    break;
                }
            }
            Assert.assertTrue("Incorrect match - struts", found);
            Assert.assertTrue(("Incorrect match size - spring3 - " + (spring3.getVulnerableSoftwareIdentifiers().size())), ((spring3.getVulnerableSoftwareIdentifiers().size()) >= 1));
            jarAnalyzer.close();
            suppressionAnalyzer.close();
        }
    }

    /**
     * Test of determineIdentifiers method, of class CPEAnalyzer.
     *
     * @throws Exception
     * 		is thrown when an exception occurs
     */
    @Test
    public void testDetermineIdentifiers() throws Exception {
        callDetermieIdentifiers("eclipse", "jetty", "9.4.8.v20171121", "cpe:2.3:a:eclipse:jetty:9.4.8:20171121:*:*:*:*:*:*");
        callDetermieIdentifiers("openssl", "openssl", "1.0.1c", "cpe:2.3:a:openssl:openssl:1.0.1c:*:*:*:*:*:*:*");
    }

    /**
     * Test of searchCPE method, of class CPEAnalyzer.
     *
     * @throws Exception
     * 		is thrown when an exception occurs
     */
    @Test
    public void testSearchCPE() throws Exception {
        Map<String, MutableInt> vendor = new HashMap<>();
        Map<String, MutableInt> product = new HashMap<>();
        vendor.put("apache software foundation", new MutableInt(1));
        product.put("struts 2 core", new MutableInt(1));
        String expVendor = "apache";
        String expProduct = "struts";
        CPEAnalyzer instance = new CPEAnalyzer();
        try (Engine engine = new Engine(getSettings())) {
            engine.openDatabase(true, true);
            instance.initialize(getSettings());
            instance.prepare(engine);
            Set<String> productWeightings = Collections.singleton("struts2");
            Set<String> vendorWeightings = Collections.singleton("apache");
            List<IndexEntry> result = instance.searchCPE(vendor, product, vendorWeightings, productWeightings);
            boolean found = false;
            for (IndexEntry entry : result) {
                if ((expVendor.equals(entry.getVendor())) && (expProduct.equals(entry.getProduct()))) {
                    found = true;
                    break;
                }
            }
            Assert.assertTrue("apache:struts was not identified", found);
        }
        instance.close();
    }
}

