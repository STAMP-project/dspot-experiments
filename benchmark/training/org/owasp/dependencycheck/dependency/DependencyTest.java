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
package org.owasp.dependencycheck.dependency;


import Confidence.HIGH;
import EvidenceType.PRODUCT;
import EvidenceType.VENDOR;
import EvidenceType.VERSION;
import Part.APPLICATION;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;
import org.owasp.dependencycheck.data.nexus.MavenArtifact;
import org.owasp.dependencycheck.dependency.naming.CpeIdentifier;
import org.owasp.dependencycheck.dependency.naming.Identifier;
import us.springett.parsers.cpe.Cpe;
import us.springett.parsers.cpe.CpeBuilder;

import static Confidence.HIGHEST;


/**
 *
 *
 * @author Jeremy Long
 */
public class DependencyTest extends BaseTest {
    /**
     * Test of getFileName method, of class Dependency.
     */
    @Test
    public void testGetFileName() {
        Dependency instance = new Dependency();
        String expResult = "filename";
        instance.setFileName(expResult);
        String result = instance.getFileName();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of setFileName method, of class Dependency.
     */
    @Test
    public void testSetFileName() {
        String fileName = "file.tar";
        Dependency instance = new Dependency();
        instance.setFileName(fileName);
        Assert.assertEquals(fileName, instance.getFileName());
    }

    /**
     * Test of setActualFilePath method, of class Dependency.
     */
    @Test
    public void testSetActualFilePath() {
        String expectedPath = "file.tar";
        String actualPath = "file.tar";
        Dependency instance = new Dependency();
        instance.setSha1sum("non-null value");
        instance.setActualFilePath(actualPath);
        Assert.assertEquals(expectedPath, instance.getActualFilePath());
    }

    /**
     * Test of getActualFilePath method, of class Dependency.
     */
    @Test
    public void testGetActualFilePath() {
        Dependency instance = new Dependency();
        String expResult = "file.tar";
        instance.setSha1sum("non-null value");
        instance.setActualFilePath(expResult);
        String result = instance.getActualFilePath();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of setFilePath method, of class Dependency.
     */
    @Test
    public void testSetFilePath() {
        String filePath = "file.tar";
        Dependency instance = new Dependency();
        instance.setFilePath(filePath);
        Assert.assertEquals(filePath, instance.getFilePath());
    }

    /**
     * Test of getFilePath method, of class Dependency.
     */
    @Test
    public void testGetFilePath() {
        Dependency instance = new Dependency();
        String expResult = "file.tar";
        instance.setFilePath(expResult);
        String result = instance.getFilePath();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getMd5sum method, of class Dependency.
     */
    @Test
    public void testGetMd5sum() {
        // File file = new File(this.getClass().getClassLoader().getResource("struts2-core-2.1.2.jar").getPath());
        File file = BaseTest.getResourceAsFile(this, "struts2-core-2.1.2.jar");
        Dependency instance = new Dependency(file);
        // assertEquals("89CE9E36AA9A9E03F1450936D2F4F8DD0F961F8B", result.getSha1sum());
        // String expResult = "C30B57142E1CCBC1EFD5CD15F307358F";
        String expResult = "c30b57142e1ccbc1efd5cd15f307358f";
        String result = instance.getMd5sum();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of setMd5sum method, of class Dependency.
     */
    @Test
    public void testSetMd5sum() {
        String md5sum = "test";
        Dependency instance = new Dependency();
        instance.setMd5sum(md5sum);
        Assert.assertEquals(md5sum, instance.getMd5sum());
    }

    /**
     * Test of getSha1sum method, of class Dependency.
     */
    @Test
    public void testGetSha1sum() {
        // File file = new File(this.getClass().getClassLoader().getResource("struts2-core-2.1.2.jar").getPath());
        File file = BaseTest.getResourceAsFile(this, "struts2-core-2.1.2.jar");
        Dependency instance = new Dependency(file);
        // String expResult = "89CE9E36AA9A9E03F1450936D2F4F8DD0F961F8B";
        String expResult = "89ce9e36aa9a9e03f1450936d2f4f8dd0f961f8b";
        String result = instance.getSha1sum();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getSha1sum method, of class Dependency.
     */
    @Test
    public void testGetSha256sum() {
        File file = BaseTest.getResourceAsFile(this, "struts2-core-2.1.2.jar");
        Dependency instance = new Dependency(file);
        String expResult = "5c1847a10800027254fcd0073385cceb46b1dacee061f3cd465e314bec592e81";
        String result = instance.getSha256sum();
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of setSha1sum method, of class Dependency.
     */
    @Test
    public void testSetSha1sum() {
        String sha1sum = "test";
        Dependency instance = new Dependency();
        instance.setSha1sum(sha1sum);
        Assert.assertEquals(sha1sum, instance.getSha1sum());
    }

    /**
     * Test of setSha1sum method, of class Dependency.
     */
    @Test
    public void testSetSha256um() {
        String sha256sum = "test";
        Dependency instance = new Dependency();
        instance.setSha256sum(sha256sum);
        Assert.assertEquals(sha256sum, instance.getSha256sum());
    }

    /**
     * Test of getIdentifiers method, of class Dependency.
     */
    @Test
    public void testGetSoftwareIdentifiers() {
        Dependency instance = new Dependency();
        Set<Identifier> result = instance.getSoftwareIdentifiers();
        Assert.assertNotNull(result);
    }

    /**
     * Test of setIdentifiers method, of class Dependency.
     */
    @Test
    public void testAddSoftwareIdentifiers() {
        Set<Identifier> identifiers = new HashSet<>();
        Dependency instance = new Dependency();
        instance.addSoftwareIdentifiers(identifiers);
        Assert.assertNotNull(instance.getSoftwareIdentifiers());
    }

    /**
     * Test of addIdentifier method, of class Dependency.
     */
    @Test
    public void testAddVulnerableSoftwareIIdentifier() throws Exception {
        CpeBuilder builder = new CpeBuilder();
        Cpe cpe = builder.part(APPLICATION).vendor("apache").product("struts").version("2.1.2").build();
        CpeIdentifier id = new CpeIdentifier(cpe, HIGHEST);
        cpe = builder.part(APPLICATION).vendor("apache").product("struts").version("2.1.2").build();
        CpeIdentifier expResult = new CpeIdentifier(cpe, HIGHEST);
        Dependency instance = new Dependency();
        instance.addVulnerableSoftwareIdentifier(id);
        Assert.assertEquals(1, instance.getVulnerableSoftwareIdentifiers().size());
        Assert.assertTrue("Identifier doesn't contain expected result.", instance.getVulnerableSoftwareIdentifiers().contains(expResult));
    }

    /**
     * Test of getEvidence method, of class Dependency.
     */
    @Test
    public void testGetEvidence() {
        Dependency instance = new Dependency();
        Set<Evidence> result = instance.getEvidence(VENDOR);
        Assert.assertNotNull(result);
        result = instance.getEvidence(PRODUCT);
        Assert.assertNotNull(result);
        result = instance.getEvidence(VERSION);
        Assert.assertNotNull(result);
    }

    /**
     * Test of addAsEvidence method, of class Dependency.
     */
    @Test
    public void testAddAsEvidence() {
        Dependency instance = new Dependency();
        MavenArtifact mavenArtifact = new MavenArtifact("group", "artifact", "version", "url");
        instance.addAsEvidence("pom", mavenArtifact, HIGH);
        Assert.assertTrue(instance.contains(VENDOR, HIGH));
        Assert.assertEquals(3, instance.size());
        Assert.assertFalse(instance.getSoftwareIdentifiers().isEmpty());
    }

    /**
     * Test of addAsEvidence method, of class Dependency.
     */
    @Test
    public void testAddAsEvidenceWithEmptyArtifact() {
        Dependency instance = new Dependency();
        MavenArtifact mavenArtifact = new MavenArtifact(null, null, null, null);
        instance.addAsEvidence("pom", mavenArtifact, HIGH);
        Assert.assertFalse(instance.getEvidence(VENDOR).stream().anyMatch(( e) -> (e.getConfidence()) == Confidence.HIGH));
        Assert.assertTrue(((instance.size()) == 0));
        Assert.assertTrue(instance.getSoftwareIdentifiers().isEmpty());
    }

    /**
     * Test of addAsEvidence method, of class Dependency.
     */
    @Test
    public void testAddAsEvidenceWithExisting() {
        Dependency instance = new Dependency();
        MavenArtifact mavenArtifact = new MavenArtifact("group", "artifact", "version", null);
        instance.addAsEvidence("pom", mavenArtifact, HIGH);
        Assert.assertTrue(instance.getEvidence(VENDOR).stream().anyMatch(( e) -> (e.getConfidence()) == Confidence.HIGH));
        Assert.assertTrue(((instance.size()) == 3));
        Assert.assertFalse(instance.getSoftwareIdentifiers().isEmpty());
        instance.getSoftwareIdentifiers().forEach(( i) -> {
            assertNull(i.getUrl());
        });
        mavenArtifact = new MavenArtifact("group", "artifact", "version", "url");
        instance.addAsEvidence("pom", mavenArtifact, HIGH);
        Assert.assertTrue(instance.getEvidence(VENDOR).stream().anyMatch(( e) -> (e.getConfidence()) == Confidence.HIGH));
        Assert.assertTrue(((instance.size()) == 3));
        Assert.assertFalse(instance.getSoftwareIdentifiers().isEmpty());
        instance.getSoftwareIdentifiers().forEach(( i) -> {
            assertNotNull(i.getUrl());
        });
    }
}

