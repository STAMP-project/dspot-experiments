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
 * Copyright (c) 2013 Jeremy Long. All Rights Reserved.
 */
package org.owasp.dependencycheck.xml.suppression;


import com.github.packageurl.MalformedPackageURLException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.dependency.Confidence;
import org.owasp.dependencycheck.dependency.Dependency;
import org.owasp.dependencycheck.dependency.Vulnerability;
import org.owasp.dependencycheck.dependency.naming.CpeIdentifier;
import org.owasp.dependencycheck.dependency.naming.PurlIdentifier;
import us.springett.parsers.cpe.exceptions.CpeValidationException;


/**
 * Test of the suppression rule.
 *
 * @author Jeremy Long
 */
public class SuppressionRuleTest extends BaseTest {
    // <editor-fold defaultstate="collapsed" desc="Stupid tests of properties">
    /**
     * Test of FilePath property, of class SuppressionRule.
     */
    @Test
    public void testFilePath() {
        SuppressionRule instance = new SuppressionRule();
        PropertyType expResult = new PropertyType();
        expResult.setValue("test");
        instance.setFilePath(expResult);
        PropertyType result = instance.getFilePath();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of Sha1 property, of class SuppressionRule.
     */
    @Test
    public void testSha1() {
        SuppressionRule instance = new SuppressionRule();
        String expResult = "384FAA82E193D4E4B0546059CA09572654BC3970";
        instance.setSha1(expResult);
        String result = instance.getSha1();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of Cpe property, of class SuppressionRule.
     */
    @Test
    public void testCpe() {
        SuppressionRule instance = new SuppressionRule();
        List<PropertyType> cpe = new ArrayList<PropertyType>();
        instance.setCpe(cpe);
        Assert.assertFalse(instance.hasCpe());
        PropertyType pt = new PropertyType();
        pt.setValue("one");
        instance.addCpe(pt);
        Assert.assertTrue(instance.hasCpe());
        List<PropertyType> result = instance.getCpe();
        Assert.assertEquals(cpe, result);
    }

    /**
     * Test of CvssBelow property, of class SuppressionRule.
     */
    @Test
    public void testGetCvssBelow() {
        SuppressionRule instance = new SuppressionRule();
        List<Float> cvss = new ArrayList<>();
        instance.setCvssBelow(cvss);
        Assert.assertFalse(instance.hasCvssBelow());
        instance.addCvssBelow(0.7F);
        Assert.assertTrue(instance.hasCvssBelow());
        List<Float> result = instance.getCvssBelow();
        Assert.assertEquals(cvss, result);
    }

    /**
     * Test of Cwe property, of class SuppressionRule.
     */
    @Test
    public void testCwe() {
        SuppressionRule instance = new SuppressionRule();
        List<String> cwe = new ArrayList<>();
        instance.setCwe(cwe);
        Assert.assertFalse(instance.hasCwe());
        instance.addCwe("2");
        Assert.assertTrue(instance.hasCwe());
        List<String> result = instance.getCwe();
        Assert.assertEquals(cwe, result);
    }

    /**
     * Test of Cve property, of class SuppressionRule.
     */
    @Test
    public void testCve() {
        SuppressionRule instance = new SuppressionRule();
        List<String> cve = new ArrayList<>();
        instance.setCve(cve);
        Assert.assertFalse(instance.hasCve());
        instance.addCve("CVE-2013-1337");
        Assert.assertTrue(instance.hasCve());
        List<String> result = instance.getCve();
        Assert.assertEquals(cve, result);
    }

    /**
     * Test of base property, of class SuppressionRule.
     */
    @Test
    public void testBase() {
        SuppressionRule instance = new SuppressionRule();
        Assert.assertFalse(instance.isBase());
        instance.setBase(true);
        Assert.assertTrue(instance.isBase());
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Ignored duplicate tests, left in, as empty tests, so IDE doesn't re-generate them">
    /**
     * Test of getFilePath method, of class SuppressionRule.
     */
    @Test
    public void testGetFilePath() {
        // already tested, this is just left so the IDE doesn't recreate it.
    }

    /**
     * Test of setFilePath method, of class SuppressionRule.
     */
    @Test
    public void testSetFilePath() {
        // already tested, this is just left so the IDE doesn't recreate it.
    }

    /**
     * Test of getSha1 method, of class SuppressionRule.
     */
    @Test
    public void testGetSha1() {
        // already tested, this is just left so the IDE doesn't recreate it.
    }

    /**
     * Test of setSha1 method, of class SuppressionRule.
     */
    @Test
    public void testSetSha1() {
        // already tested, this is just left so the IDE doesn't recreate it.
    }

    /**
     * Test of getCpe method, of class SuppressionRule.
     */
    @Test
    public void testGetCpe() {
        // already tested, this is just left so the IDE doesn't recreate it.
    }

    /**
     * Test of setCpe method, of class SuppressionRule.
     */
    @Test
    public void testSetCpe() {
        // already tested, this is just left so the IDE doesn't recreate it.
    }

    /**
     * Test of addCpe method, of class SuppressionRule.
     */
    @Test
    public void testAddCpe() {
        // already tested, this is just left so the IDE doesn't recreate it.
    }

    /**
     * Test of hasCpe method, of class SuppressionRule.
     */
    @Test
    public void testHasCpe() {
        // already tested, this is just left so the IDE doesn't recreate it.
    }

    /**
     * Test of setCvssBelow method, of class SuppressionRule.
     */
    @Test
    public void testSetCvssBelow() {
        // already tested, this is just left so the IDE doesn't recreate it.
    }

    /**
     * Test of addCvssBelow method, of class SuppressionRule.
     */
    @Test
    public void testAddCvssBelow() {
        // already tested, this is just left so the IDE doesn't recreate it.
    }

    /**
     * Test of hasCvssBelow method, of class SuppressionRule.
     */
    @Test
    public void testHasCvssBelow() {
        // already tested, this is just left so the IDE doesn't recreate it.
    }

    /**
     * Test of getCwe method, of class SuppressionRule.
     */
    @Test
    public void testGetCwe() {
        // already tested, this is just left so the IDE doesn't recreate it.
    }

    /**
     * Test of setCwe method, of class SuppressionRule.
     */
    @Test
    public void testSetCwe() {
        // already tested, this is just left so the IDE doesn't recreate it.
    }

    /**
     * Test of addCwe method, of class SuppressionRule.
     */
    @Test
    public void testAddCwe() {
        // already tested, this is just left so the IDE doesn't recreate it.
    }

    /**
     * Test of hasCwe method, of class SuppressionRule.
     */
    @Test
    public void testHasCwe() {
        // already tested, this is just left so the IDE doesn't recreate it.
    }

    /**
     * Test of getCve method, of class SuppressionRule.
     */
    @Test
    public void testGetCve() {
        // already tested, this is just left so the IDE doesn't recreate it.
    }

    /**
     * Test of setCve method, of class SuppressionRule.
     */
    @Test
    public void testSetCve() {
        // already tested, this is just left so the IDE doesn't recreate it.
    }

    /**
     * Test of addCve method, of class SuppressionRule.
     */
    @Test
    public void testAddCve() {
        // already tested, this is just left so the IDE doesn't recreate it.
    }

    /**
     * Test of hasCve method, of class SuppressionRule.
     */
    @Test
    public void testHasCve() {
        // already tested, this is just left so the IDE doesn't recreate it.
    }

    // </editor-fold>
    /**
     * Test of cpeHasNoVersion method, of class SuppressionRule.
     */
    @Test
    public void testCpeHasNoVersion() {
        PropertyType c = new PropertyType();
        c.setValue("cpe:/a:microsoft:.net_framework:4.5");
        SuppressionRule instance = new SuppressionRule();
        Assert.assertFalse(instance.cpeHasNoVersion(c));
        c.setValue("cpe:/a:microsoft:.net_framework:");
        Assert.assertFalse(instance.cpeHasNoVersion(c));
        c.setValue("cpe:/a:microsoft:.net_framework");
        Assert.assertTrue(instance.cpeHasNoVersion(c));
    }

    /**
     * Test of identifierMatches method, of class SuppressionRule.
     */
    @Test
    public void testCpeMatches() throws MalformedPackageURLException, CpeValidationException {
        CpeIdentifier identifier = new CpeIdentifier("microsoft", ".net_framework", "4.5", Confidence.HIGHEST);
        PropertyType cpe = new PropertyType();
        cpe.setValue("cpe:/a:microsoft:.net_framework:4.5");
        SuppressionRule instance = new SuppressionRule();
        boolean expResult = true;
        boolean result = instance.identifierMatches(cpe, identifier);
        Assert.assertEquals(expResult, result);
        cpe.setValue("cpe:/a:microsoft:.net_framework:4.0");
        expResult = false;
        result = instance.identifierMatches(cpe, identifier);
        Assert.assertEquals(expResult, result);
        cpe.setValue("CPE:/a:microsoft:.net_framework:4.5");
        cpe.setCaseSensitive(true);
        expResult = false;
        result = instance.identifierMatches(cpe, identifier);
        Assert.assertEquals(expResult, result);
        cpe.setValue("cpe:/a:microsoft:.net_framework");
        cpe.setCaseSensitive(false);
        expResult = true;
        result = instance.identifierMatches(cpe, identifier);
        Assert.assertEquals(expResult, result);
        cpe.setValue("cpe:/a:microsoft:.*");
        cpe.setRegex(true);
        expResult = true;
        result = instance.identifierMatches(cpe, identifier);
        Assert.assertEquals(expResult, result);
        cpe.setValue("CPE:/a:microsoft:.*");
        cpe.setRegex(true);
        cpe.setCaseSensitive(true);
        expResult = false;
        result = instance.identifierMatches(cpe, identifier);
        Assert.assertEquals(expResult, result);
        cpe.setValue("cpe:/a:apache:.*");
        cpe.setRegex(true);
        cpe.setCaseSensitive(false);
        expResult = false;
        result = instance.identifierMatches(cpe, identifier);
        Assert.assertEquals(expResult, result);
        identifier = new CpeIdentifier("apache", "tomcat", "7.0", Confidence.HIGH);
        cpe.setValue("cpe:/a:apache:tomcat");
        cpe.setRegex(false);
        cpe.setCaseSensitive(false);
        expResult = true;
        result = instance.identifierMatches(cpe, identifier);
        Assert.assertEquals(expResult, result);
        PurlIdentifier pid = new PurlIdentifier("maven", "org.springframework", "spring-core", "2.5.5", Confidence.HIGH);
        cpe.setValue("org.springframework:spring-core:2.5.5");
        cpe.setRegex(false);
        cpe.setCaseSensitive(false);
        expResult = true;
        result = instance.identifierMatches(cpe, pid);
        Assert.assertEquals(expResult, result);
        cpe.setValue("org\\.springframework\\.security:spring.*");
        cpe.setRegex(true);
        cpe.setCaseSensitive(false);
        expResult = false;
        result = instance.identifierMatches(cpe, pid);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of process method, of class SuppressionRule.
     */
    @Test
    public void testProcess() throws CpeValidationException {
        // File struts = new File(this.getClass().getClassLoader().getResource("struts2-core-2.1.2.jar").getPath());
        File struts = BaseTest.getResourceAsFile(this, "struts2-core-2.1.2.jar");
        Dependency dependency = new Dependency(struts);
        CpeIdentifier cpeId = new CpeIdentifier("microsoft", ".net_framework", "4.5", Confidence.HIGH);
        dependency.addVulnerableSoftwareIdentifier(cpeId);
        String sha1 = dependency.getSha1sum();
        dependency.setSha1sum("384FAA82E193D4E4B0546059CA09572654BC3970");
        Vulnerability v = createVulnerability();
        dependency.addVulnerability(v);
        // cwe
        SuppressionRule instance = new SuppressionRule();
        instance.setSha1(sha1);
        instance.addCwe("287");
        instance.process(dependency);
        Assert.assertEquals(1, dependency.getVulnerabilities().size());
        dependency.setSha1sum(sha1);
        instance.process(dependency);
        Assert.assertTrue(dependency.getVulnerabilities().isEmpty());
        Assert.assertEquals(1, dependency.getSuppressedVulnerabilities().size());
        // cvss
        dependency.addVulnerability(v);
        instance = new SuppressionRule();
        instance.addCvssBelow(5.0F);
        instance.process(dependency);
        Assert.assertEquals(1, dependency.getVulnerabilities().size());
        instance.addCvssBelow(8.0F);
        instance.process(dependency);
        Assert.assertTrue(dependency.getVulnerabilities().isEmpty());
        Assert.assertEquals(1, dependency.getSuppressedVulnerabilities().size());
        // cve
        dependency.addVulnerability(v);
        instance = new SuppressionRule();
        instance.addCve("CVE-2012-1337");
        instance.process(dependency);
        Assert.assertEquals(1, dependency.getVulnerabilities().size());
        instance.addCve("CVE-2013-1337");
        instance.process(dependency);
        Assert.assertTrue(dependency.getVulnerabilities().isEmpty());
        Assert.assertEquals(1, dependency.getSuppressedVulnerabilities().size());
        // cpe
        instance = new SuppressionRule();
        PropertyType pt = new PropertyType();
        pt.setValue("cpe:/a:microsoft:.net_framework:4.0");
        instance.addCpe(pt);
        instance.process(dependency);
        Assert.assertTrue(((dependency.getVulnerableSoftwareIdentifiers().size()) == 1));
        pt = new PropertyType();
        pt.setValue("cpe:/a:microsoft:.net_framework:4.5");
        instance.addCpe(pt);
        pt = new PropertyType();
        pt.setValue(".*");
        pt.setRegex(true);
        instance.setFilePath(pt);
        instance.process(dependency);
        Assert.assertTrue(dependency.getVulnerableSoftwareIdentifiers().isEmpty());
        Assert.assertEquals(1, dependency.getSuppressedIdentifiers().size());
        instance = new SuppressionRule();
        dependency.addVulnerableSoftwareIdentifier(new CpeIdentifier("microsoft", ".net_framework", "4.0", Confidence.HIGH));
        dependency.addVulnerableSoftwareIdentifier(new CpeIdentifier("microsoft", ".net_framework", "4.5", Confidence.HIGH));
        dependency.addVulnerableSoftwareIdentifier(new CpeIdentifier("microsoft", ".net_framework", "5.0", Confidence.HIGH));
        pt = new PropertyType();
        pt.setValue("cpe:/a:microsoft:.net_framework");
        instance.addCpe(pt);
        instance.setBase(true);
        Assert.assertEquals(3, dependency.getVulnerableSoftwareIdentifiers().size());
        Assert.assertEquals(1, dependency.getSuppressedIdentifiers().size());
        instance.process(dependency);
        Assert.assertTrue(dependency.getVulnerableSoftwareIdentifiers().isEmpty());
        Assert.assertEquals(1, dependency.getSuppressedIdentifiers().size());
    }

    /**
     * Test of process method, of class SuppressionRule.
     */
    @Test
    public void testProcessGAV() throws MalformedPackageURLException, CpeValidationException {
        // File spring = new File(this.getClass().getClassLoader().getResource("spring-security-web-3.0.0.RELEASE.jar").getPath());
        File spring = BaseTest.getResourceAsFile(this, "spring-security-web-3.0.0.RELEASE.jar");
        Dependency dependency = new Dependency(spring);
        dependency.addVulnerableSoftwareIdentifier(new CpeIdentifier("vmware", "springsource_spring_framework", "3.0.0", Confidence.HIGH));
        dependency.addVulnerableSoftwareIdentifier(new CpeIdentifier("springsource", "spring_framework", "3.0.0", Confidence.HIGH));
        dependency.addVulnerableSoftwareIdentifier(new CpeIdentifier("mod_security", "mod_security", "3.0.0", Confidence.HIGH));
        dependency.addVulnerableSoftwareIdentifier(new CpeIdentifier("vmware", "springsource_spring_security", "3.0.0", Confidence.HIGH));
        dependency.addSoftwareIdentifier(new PurlIdentifier("maven", "org.springframework.security", "spring-security-web", "3.0.0.RELEASE", Confidence.HIGH));
        // cpe
        SuppressionRule instance = new SuppressionRule();
        PropertyType pt = new PropertyType();
        pt.setValue("org\\.springframework\\.security:spring.*");
        pt.setRegex(true);
        pt.setCaseSensitive(false);
        instance.setGav(pt);
        pt = new PropertyType();
        pt.setValue("cpe:/a:mod_security:mod_security");
        instance.addCpe(pt);
        pt = new PropertyType();
        pt.setValue("cpe:/a:springsource:spring_framework");
        instance.addCpe(pt);
        pt = new PropertyType();
        pt.setValue("cpe:/a:vmware:springsource_spring_framework");
        instance.addCpe(pt);
        instance.process(dependency);
        Assert.assertEquals(1, dependency.getVulnerableSoftwareIdentifiers().size());
    }
}

