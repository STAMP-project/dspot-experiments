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
 * Copyright (c) 2016 Jeremy Long. All Rights Reserved.
 */
package org.owasp.dependencycheck.xml.hints;


import java.io.File;
import java.io.InputStream;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.owasp.dependencycheck.BaseTest;


/**
 *
 *
 * @author Jeremy Long
 */
public class HintParserTest extends BaseTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Test of parseHints method, of class HintParser.
     */
    @Test
    public void testParseHints_File() throws Exception {
        File file = BaseTest.getResourceAsFile(this, "hints.xml");
        HintParser instance = new HintParser();
        instance.parseHints(file);
        List<HintRule> hintRules = instance.getHintRules();
        List<VendorDuplicatingHintRule> vendorRules = instance.getVendorDuplicatingHintRules();
        Assert.assertEquals("Two duplicating hints should have been read", 2, vendorRules.size());
        Assert.assertEquals("Two hint rules should have been read", 2, hintRules.size());
        Assert.assertEquals("One add product should have been read", 1, hintRules.get(0).getAddProduct().size());
        Assert.assertEquals("One add vendor should have been read", 1, hintRules.get(0).getAddVendor().size());
        Assert.assertEquals("Two file name should have been read", 2, hintRules.get(1).getFilenames().size());
        Assert.assertEquals("add product name not found", "add product name", hintRules.get(0).getAddProduct().get(0).getName());
        Assert.assertEquals("add vendor name not found", "add vendor name", hintRules.get(0).getAddVendor().get(0).getName());
        Assert.assertEquals("given product name not found", "given product name", hintRules.get(0).getGivenProduct().get(0).getName());
        Assert.assertEquals("given vendor name not found", "given vendor name", hintRules.get(0).getGivenVendor().get(0).getName());
        Assert.assertEquals("spring file name not found", "spring", hintRules.get(1).getFilenames().get(0).getValue());
        Assert.assertEquals("file name 1 should not be case sensitive", false, hintRules.get(1).getFilenames().get(0).isCaseSensitive());
        Assert.assertEquals("file name 1 should not be a regex", false, hintRules.get(1).getFilenames().get(0).isRegex());
        Assert.assertEquals("file name 2 should be case sensitive", true, hintRules.get(1).getFilenames().get(1).isCaseSensitive());
        Assert.assertEquals("file name 2 should be a regex", true, hintRules.get(1).getFilenames().get(1).isRegex());
        Assert.assertEquals("sun duplicating vendor", "sun", vendorRules.get(0).getValue());
        Assert.assertEquals("sun duplicates vendor oracle", "oracle", vendorRules.get(0).getDuplicate());
    }

    /**
     * Test the application of the correct XSD by the parser by using a
     * hints-file with namespace
     * {@code https://jeremylong.github.io/DependencyCheck/dependency-hint.1.1.xsd}
     * that is using the version evidence for {@code<given> } that was introduced
     * with namespace
     * {@code https://jeremylong.github.io/DependencyCheck/dependency-hint.1.2.xsd}.
     * This should yield a specific SAXParseException that gets wrapped into a
     * HintParseException. We check for the correct error by searching for the
     * error-message of the SAXParser in the exception's message.
     */
    @Test
    public void testParseHintsXSDSelection() throws Exception {
        thrown.expect(HintParseException.class);
        thrown.expectMessage("Line=7, Column=133: cvc-enumeration-valid: Value 'version' is not facet-valid with respect to enumeration '[vendor, product]'. It must be a value from the enumeration.");
        File file = BaseTest.getResourceAsFile(this, "hints_invalid.xml");
        HintParser instance = new HintParser();
        instance.parseHints(file);
        Assert.fail("A parser exception for an XML-schema violation should have been thrown");
    }

    /**
     * Test of parseHints method, of class HintParser.
     */
    @Test
    public void testParseHints_InputStream() throws Exception {
        InputStream ins = BaseTest.getResourceAsStream(this, "hints_12.xml");
        HintParser instance = new HintParser();
        instance.parseHints(ins);
        List<HintRule> hintRules = instance.getHintRules();
        List<VendorDuplicatingHintRule> vendorRules = instance.getVendorDuplicatingHintRules();
        Assert.assertEquals("Zero duplicating hints should have been read", 0, vendorRules.size());
        Assert.assertEquals("Two hint rules should have been read", 2, hintRules.size());
        Assert.assertEquals("One given product should have been read in hint 0", 1, hintRules.get(0).getGivenProduct().size());
        Assert.assertEquals("One given vendor should have been read in hint 0", 1, hintRules.get(0).getGivenVendor().size());
        Assert.assertEquals("One given version should have been read in hint 0", 1, hintRules.get(0).getGivenVersion().size());
        Assert.assertEquals("One add product should have been read in hint 0", 1, hintRules.get(0).getAddProduct().size());
        Assert.assertEquals("One add vendor should have been read in hint 0", 1, hintRules.get(0).getAddVendor().size());
        Assert.assertEquals("One add version should have been read in hint 0", 1, hintRules.get(0).getAddVersion().size());
        Assert.assertEquals("Zero remove product should have been read in hint 0", 0, hintRules.get(0).getRemoveProduct().size());
        Assert.assertEquals("Zero remove vendor should have been read in hint 0", 0, hintRules.get(0).getRemoveVendor().size());
        Assert.assertEquals("Zero remove version should have been read in hint 0", 0, hintRules.get(0).getRemoveVersion().size());
        Assert.assertEquals("Zero given product should have been read in hint 1", 0, hintRules.get(1).getGivenProduct().size());
        Assert.assertEquals("Zero given vendor should have been read in hint 1", 0, hintRules.get(1).getGivenVendor().size());
        Assert.assertEquals("One given version should have been read in hint 1", 1, hintRules.get(1).getGivenVersion().size());
        Assert.assertEquals("One remove product should have been read in hint 1", 1, hintRules.get(1).getRemoveProduct().size());
        Assert.assertEquals("One remove vendor should have been read in hint 1", 1, hintRules.get(1).getRemoveVendor().size());
        Assert.assertEquals("One remove version should have been read in hint 1", 1, hintRules.get(1).getRemoveVersion().size());
        Assert.assertEquals("Zero add product should have been read in hint 1", 0, hintRules.get(1).getAddProduct().size());
        Assert.assertEquals("Zero add vendor should have been read in hint 1", 0, hintRules.get(1).getAddVendor().size());
        Assert.assertEquals("Zero add version should have been read in hint 1", 0, hintRules.get(1).getAddVersion().size());
        Assert.assertEquals("add product name not found in hint 0", "add product name", hintRules.get(0).getAddProduct().get(0).getName());
        Assert.assertEquals("add vendor name not found in hint 0", "add vendor name", hintRules.get(0).getAddVendor().get(0).getName());
        Assert.assertEquals("add version name not found in hint 0", "add version name", hintRules.get(0).getAddVersion().get(0).getName());
        Assert.assertEquals("given product name not found in hint 0", "given product name", hintRules.get(0).getGivenProduct().get(0).getName());
        Assert.assertEquals("given vendor name not found in hint 0", "given vendor name", hintRules.get(0).getGivenVendor().get(0).getName());
        Assert.assertEquals("given version name not found in hint 0", "given version name", hintRules.get(0).getGivenVersion().get(0).getName());
        Assert.assertEquals("given version name not found in hint 1", "given version name", hintRules.get(1).getGivenVersion().get(0).getName());
        Assert.assertEquals("add product name not found in hint 1", "remove product name", hintRules.get(1).getRemoveProduct().get(0).getName());
        Assert.assertEquals("add vendor name not found in hint 1", "remove vendor name", hintRules.get(1).getRemoveVendor().get(0).getName());
        Assert.assertEquals("add version name not found in hint 1", "remove version name", hintRules.get(1).getRemoveVersion().get(0).getName());
    }

    /**
     * Test of parseHints method, of class HintParser.
     */
    @Test
    public void testParseHintsWithRegex() throws Exception {
        InputStream ins = BaseTest.getResourceAsStream(this, "hints_13.xml");
        HintParser instance = new HintParser();
        instance.parseHints(ins);
        List<VendorDuplicatingHintRule> vendor = instance.getVendorDuplicatingHintRules();
        List<HintRule> rules = instance.getHintRules();
        Assert.assertEquals("Zero duplicating hints should have been read", 0, vendor.size());
        Assert.assertEquals("Two hint rules should have been read", 2, rules.size());
        Assert.assertEquals("One given product should have been read in hint 0", 1, rules.get(0).getGivenProduct().size());
        Assert.assertEquals("One given vendor should have been read in hint 0", 1, rules.get(0).getGivenVendor().size());
        Assert.assertEquals("One given version should have been read in hint 0", 1, rules.get(0).getGivenVersion().size());
        Assert.assertEquals("One add product should have been read in hint 0", 1, rules.get(0).getAddProduct().size());
        Assert.assertEquals("One add vendor should have been read in hint 0", 1, rules.get(0).getAddVendor().size());
        Assert.assertEquals("One add version should have been read in hint 0", 1, rules.get(0).getAddVersion().size());
        Assert.assertEquals("Zero remove product should have been read in hint 0", 0, rules.get(0).getRemoveProduct().size());
        Assert.assertEquals("Zero remove vendor should have been read in hint 0", 0, rules.get(0).getRemoveVendor().size());
        Assert.assertEquals("Zero remove version should have been read in hint 0", 0, rules.get(0).getRemoveVersion().size());
        Assert.assertEquals("Zero given product should have been read in hint 1", 0, rules.get(1).getGivenProduct().size());
        Assert.assertEquals("Zero given vendor should have been read in hint 1", 0, rules.get(1).getGivenVendor().size());
        Assert.assertEquals("One given version should have been read in hint 1", 1, rules.get(1).getGivenVersion().size());
        Assert.assertEquals("One remove product should have been read in hint 1", 1, rules.get(1).getRemoveProduct().size());
        Assert.assertEquals("One remove vendor should have been read in hint 1", 1, rules.get(1).getRemoveVendor().size());
        Assert.assertEquals("One remove version should have been read in hint 1", 1, rules.get(1).getRemoveVersion().size());
        Assert.assertEquals("Zero add product should have been read in hint 1", 0, rules.get(1).getAddProduct().size());
        Assert.assertEquals("Zero add vendor should have been read in hint 1", 0, rules.get(1).getAddVendor().size());
        Assert.assertEquals("Zero add version should have been read in hint 1", 0, rules.get(1).getAddVersion().size());
        Assert.assertEquals("add product name not found in hint 0", "add product name", rules.get(0).getAddProduct().get(0).getName());
        Assert.assertEquals("add vendor name not found in hint 0", "add vendor name", rules.get(0).getAddVendor().get(0).getName());
        Assert.assertEquals("add version name not found in hint 0", "add version name", rules.get(0).getAddVersion().get(0).getName());
        Assert.assertEquals("given product name not found in hint 0", "given product name", rules.get(0).getGivenProduct().get(0).getName());
        Assert.assertEquals("value not registered to be a regex for given product in hint 0", true, rules.get(0).getGivenProduct().get(0).isRegex());
        Assert.assertEquals("given vendor name not found in hint 0", "given vendor name", rules.get(0).getGivenVendor().get(0).getName());
        Assert.assertEquals("value not registered to be a regex for given vendor in hint 0", true, rules.get(0).getGivenVendor().get(0).isRegex());
        Assert.assertEquals("given version name not found in hint 0", "given version name", rules.get(0).getGivenVersion().get(0).getName());
        Assert.assertEquals("value not registered to not be a regex for given version in hint 0", false, rules.get(0).getGivenVersion().get(0).isRegex());
        Assert.assertEquals("given version name not found in hint 1", "given version name", rules.get(1).getGivenVersion().get(0).getName());
        Assert.assertEquals("value not registered to not be a regex by default for given version in hint 1", false, rules.get(1).getRemoveProduct().get(0).isRegex());
        Assert.assertEquals("remove product name not found in hint 1", "remove product name", rules.get(1).getRemoveProduct().get(0).getName());
        Assert.assertEquals("value not registered to not be a regex for product removal in hint 1", false, rules.get(1).getRemoveProduct().get(0).isRegex());
        Assert.assertEquals("remove vendor name not found in hint 1", "remove vendor name", rules.get(1).getRemoveVendor().get(0).getName());
        Assert.assertEquals("value not registered to not be a regex for vendor removal in hint 1", false, rules.get(1).getRemoveVendor().get(0).isRegex());
        Assert.assertEquals("remove version name not found in hint 1", "remove version name", rules.get(1).getRemoveVersion().get(0).getName());
        Assert.assertEquals("value not defaulted to not be a regex for vendor removal in hint 1", false, rules.get(1).getRemoveVersion().get(0).isRegex());
    }
}

