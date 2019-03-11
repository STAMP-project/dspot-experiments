/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIESOR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.orientechnologies.orient.object.jpa.parsing;


import com.orientechnologies.orient.object.jpa.OJPAPersistenceUnitInfo;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.persistence.PersistenceException;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import org.junit.Assert;
import org.junit.Test;


public class PersistenceXMLParsingTest {
    /**
     * Test parsing a persistence descriptor with several entries
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFile1() throws Exception {
        URL locationUrl = getClass().getClassLoader().getResource("apache-aries/file1/META-INF/persistence.xml");
        Collection<? extends PersistenceUnitInfo> parsedUnits = PersistenceXmlUtil.parse(locationUrl);
        Assert.assertNotNull("Persistence units shouldn't be null.", parsedUnits);
        Assert.assertEquals("An incorrect number of persistence units has been returned.", 4, parsedUnits.size());
        Iterator<? extends PersistenceUnitInfo> iterator = parsedUnits.iterator();
        PersistenceXMLParsingTest.assertPersistenceUnit(iterator.next(), new PersistenceXMLParsingTest.Rule[]{ // 
        new PersistenceXMLParsingTest.Rule("The schema version was incorrect", "1.0", "getPersistenceXMLSchemaVersion"), new PersistenceXMLParsingTest.Rule("The unit name was incorrect", "alpha", "getPersistenceUnitName"), new PersistenceXMLParsingTest.Rule("The transaction type was incorrect", null, "getTransactionType"), new PersistenceXMLParsingTest.Rule("The provider class name was incorrect", null, "getPersistenceProviderClassName"), new PersistenceXMLParsingTest.Rule("One or more mapping files were specified", Collections.EMPTY_LIST, "getMappingFileNames"), new PersistenceXMLParsingTest.Rule("One or more jar files were specified", Collections.EMPTY_LIST, "getJarFileUrls"), new PersistenceXMLParsingTest.Rule("One or more managed classes were specified", Collections.EMPTY_LIST, "getManagedClassNames"), new PersistenceXMLParsingTest.Rule("We should not exclude any classes", false, "excludeUnlistedClasses") });
        PersistenceXMLParsingTest.assertPersistenceUnit(iterator.next(), new PersistenceXMLParsingTest.Rule[]{ // 
        new PersistenceXMLParsingTest.Rule("The schema version was incorrect", "1.0", "getPersistenceXMLSchemaVersion"), new PersistenceXMLParsingTest.Rule("The unit name was incorrect", "bravo", "getPersistenceUnitName"), new PersistenceXMLParsingTest.Rule("The transaction type was incorrect", JTA, "getTransactionType"), new PersistenceXMLParsingTest.Rule("The provider class name was incorrect", "bravo.persistence.provider", "getPersistenceProviderClassName"), new PersistenceXMLParsingTest.Rule("Incorrect mapping files were listed", Arrays.asList("bravoMappingFile1.xml", "bravoMappingFile2.xml"), "getMappingFileNames"), new PersistenceXMLParsingTest.Rule("Incorrect jar URLs were listed", PersistenceXMLParsingTest.asURLList("bravoJarFile1.jar", "bravoJarFile2.jar"), "getJarFileUrls"), new PersistenceXMLParsingTest.Rule("Incorrect managed classes were listed", Arrays.asList("bravoClass1", "bravoClass2"), "getManagedClassNames"), new PersistenceXMLParsingTest.Rule("We should not exclude any classes", true, "excludeUnlistedClasses"), new PersistenceXMLParsingTest.Rule("The properties should never be null", PersistenceXMLParsingTest.asProperty("some.prop", "prop.value", "some.other.prop", "another.prop.value"), "getProperties") });
        PersistenceXMLParsingTest.assertPersistenceUnit(iterator.next(), new PersistenceXMLParsingTest.Rule[]{ // 
        new PersistenceXMLParsingTest.Rule("The schema version was incorrect", "1.0", "getPersistenceXMLSchemaVersion"), new PersistenceXMLParsingTest.Rule("The unit name was incorrect", "charlie", "getPersistenceUnitName"), new PersistenceXMLParsingTest.Rule("The transaction type was incorrect", PersistenceUnitTransactionType.RESOURCE_LOCAL, "getTransactionType"), new PersistenceXMLParsingTest.Rule("The provider class name was incorrect", "charlie.persistence.provider", "getPersistenceProviderClassName"), new PersistenceXMLParsingTest.Rule("One or more mapping files were specified", Collections.EMPTY_LIST, "getMappingFileNames"), new PersistenceXMLParsingTest.Rule("One or more jar files were specified", Collections.EMPTY_LIST, "getJarFileUrls"), new PersistenceXMLParsingTest.Rule("One or more managed classes were specified", Collections.EMPTY_LIST, "getManagedClassNames"), new PersistenceXMLParsingTest.Rule("We should not exclude any classes", true, "excludeUnlistedClasses") });
        PersistenceXMLParsingTest.assertPersistenceUnit(iterator.next(), new PersistenceXMLParsingTest.Rule[]{ // 
        new PersistenceXMLParsingTest.Rule("The schema version was incorrect", "1.0", "getPersistenceXMLSchemaVersion"), new PersistenceXMLParsingTest.Rule("The unit name was incorrect", "delta", "getPersistenceUnitName"), new PersistenceXMLParsingTest.Rule("The transaction type was incorrect", PersistenceUnitTransactionType.RESOURCE_LOCAL, "getTransactionType"), new PersistenceXMLParsingTest.Rule("The provider class name was incorrect", "delta.persistence.provider", "getPersistenceProviderClassName"), new PersistenceXMLParsingTest.Rule("One or more mapping files were specified", Collections.EMPTY_LIST, "getMappingFileNames"), new PersistenceXMLParsingTest.Rule("One or more jar files were specified", Collections.EMPTY_LIST, "getJarFileUrls"), new PersistenceXMLParsingTest.Rule("One or more managed classes were specified", Collections.EMPTY_LIST, "getManagedClassNames"), new PersistenceXMLParsingTest.Rule("We should not exclude any classes", false, "excludeUnlistedClasses") });
    }

    @Test
    public void testFile2() throws Exception {
        URL locationUrl = getClass().getClassLoader().getResource("apache-aries/file2/META-INF/persistence.xml");
        Collection<? extends PersistenceUnitInfo> parsedUnits = PersistenceXmlUtil.parse(locationUrl);
        Assert.assertNotNull("Persistence units shouldn't be null.", parsedUnits);
        Assert.assertEquals("An incorrect number of persistence units has been returned.", 0, parsedUnits.size());
    }

    @Test(expected = PersistenceException.class)
    public void testFile3() throws Exception {
        URL locationUrl = getClass().getClassLoader().getResource("apache-aries/file3/META-INF/persistence.xml");
        PersistenceXmlUtil.parse(locationUrl);
        Assert.fail("Parsing should not succeed");
    }

    @Test
    public void testJPA2() throws Exception {
        URL locationUrl = getClass().getClassLoader().getResource("apache-aries/file22/META-INF/persistence.xml");
        Collection<? extends PersistenceUnitInfo> parsedUnits = PersistenceXmlUtil.parse(locationUrl);
        Assert.assertNotNull("Persistence units shouldn't be null.", parsedUnits);
        Assert.assertEquals("An incorrect number of persistence units has been returned.", 2, parsedUnits.size());
        Iterator<? extends PersistenceUnitInfo> iterator = parsedUnits.iterator();
        // test defaults
        PersistenceXMLParsingTest.assertPersistenceUnit(iterator.next(), new PersistenceXMLParsingTest.Rule[]{ // 
        new PersistenceXMLParsingTest.Rule("The schema version was incorrect", "2.0", "getPersistenceXMLSchemaVersion"), new PersistenceXMLParsingTest.Rule("The unit name was incorrect", "default", "getPersistenceUnitName"), new PersistenceXMLParsingTest.Rule("Unexpected SharedCacheMode", SharedCacheMode.UNSPECIFIED, "getSharedCacheMode"), new PersistenceXMLParsingTest.Rule("Unexpected ValidationMode", ValidationMode.AUTO, "getValidationMode") });
        PersistenceXMLParsingTest.assertPersistenceUnit(iterator.next(), new PersistenceXMLParsingTest.Rule[]{ // 
        new PersistenceXMLParsingTest.Rule("The schema version was incorrect", "2.0", "getPersistenceXMLSchemaVersion"), new PersistenceXMLParsingTest.Rule("The unit name was incorrect", "custom", "getPersistenceUnitName"), new PersistenceXMLParsingTest.Rule("Unexpected SharedCacheMode", SharedCacheMode.ENABLE_SELECTIVE, "getSharedCacheMode"), new PersistenceXMLParsingTest.Rule("Unexpected ValidationMode", ValidationMode.CALLBACK, "getValidationMode") });
    }

    /**
     * Test parsing a persistence descriptor with several entries
     *
     * @throws Exception
     * 		
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testReallyBigFile() throws Exception {
        URL locationUrl = getClass().getClassLoader().getResource("apache-aries/file24/META-INF/persistence.xml");
        Collection<? extends PersistenceUnitInfo> parsedUnits = PersistenceXmlUtil.parse(locationUrl);
        Assert.assertNotNull("Persistence units shouldn't be null.", parsedUnits);
        Assert.assertEquals("An incorrect number of persistence units has been returned.", 33, parsedUnits.size());
        List<OJPAPersistenceUnitInfo> units = new ArrayList<OJPAPersistenceUnitInfo>();
        units.addAll(((Collection<OJPAPersistenceUnitInfo>) (parsedUnits)));
        Assert.assertEquals("An incorrect number of units has been returned.", 33, units.size());
        // prepare
        Collections.sort(units, new Comparator<OJPAPersistenceUnitInfo>() {
            @Override
            public int compare(OJPAPersistenceUnitInfo p1, OJPAPersistenceUnitInfo p2) {
                return Integer.valueOf(p1.getPersistenceUnitName()).compareTo(Integer.valueOf(p2.getPersistenceUnitName()));
            }
        });
        for (int counter = 1; counter < (units.size()); counter++) {
            PersistenceXMLParsingTest.assertPersistenceUnit(units.get((counter - 1)), new PersistenceXMLParsingTest.Rule[]{ // 
            new PersistenceXMLParsingTest.Rule("The schema version was incorrect", "1.0", "getPersistenceXMLSchemaVersion"), new PersistenceXMLParsingTest.Rule("The unit name was incorrect", Integer.toString(counter), "getPersistenceUnitName"), new PersistenceXMLParsingTest.Rule("The transaction type was incorrect", JTA, "getTransactionType"), new PersistenceXMLParsingTest.Rule("The provider class name was incorrect", ("provider." + counter), "getPersistenceProviderClassName"), new PersistenceXMLParsingTest.Rule("Incorrect mapping files were listed", Arrays.asList(("mappingFile." + counter)), "getMappingFileNames"), new PersistenceXMLParsingTest.Rule("Incorrect jar URLs were listed", PersistenceXMLParsingTest.asURLList(("jarFile." + counter)), "getJarFileUrls"), new PersistenceXMLParsingTest.Rule("Incorrect managed classes were listed", Arrays.asList(("class." + counter)), "getManagedClassNames"), new PersistenceXMLParsingTest.Rule("We should not exclude any classes", true, "excludeUnlistedClasses"), new PersistenceXMLParsingTest.Rule("The properties should never be null", PersistenceXMLParsingTest.asProperty(("some.prop." + counter), ("prop.value." + counter)), "getProperties") });
        }
    }

    // ---------------- helpers
    @Test
    public void elementsPrefixedWithPersistenceNameSpaceShouldBeAccepted() throws Exception {
        URL locationUrl = getClass().getClassLoader().getResource("apache-aries/file26/META-INF/persistence.xml");
        Collection<? extends PersistenceUnitInfo> parsedUnits = PersistenceXmlUtil.parse(locationUrl);
        Assert.assertNotNull("Persistence units shouldn't be null.", parsedUnits);
        Assert.assertEquals("An incorrect number of persistence units has been returned.", 1, parsedUnits.size());
    }

    @Test(expected = PersistenceException.class)
    public void elementsPrefixedWithWrongNameSpaceShouldBeRejected() throws Exception {
        URL locationUrl = getClass().getClassLoader().getResource("apache-aries/file27/META-INF/persistence.xml");
        PersistenceXmlUtil.parse(locationUrl);
        Assert.fail("should throw");
    }

    @Test(expected = PersistenceException.class)
    public void testConfigWithoutXMLSchemaVersion() throws Exception {
        URL locationUrl = getClass().getClassLoader().getResource("orient/file1/META-INF/persistence.xml");
        PersistenceXmlUtil.parse(locationUrl);
        Assert.fail("should throw");
    }

    class Rule {
        public String message;

        public Object expected;

        public String method;

        public Rule(String message, Object expected, String method) {
            this.message = message;
            this.expected = expected;
            this.method = method;
        }
    }
}

