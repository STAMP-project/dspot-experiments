/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.conf;


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Base class for comparing fields in one or more Configuration classes
 * against a corresponding .xml file.  Usage is intended as follows:
 * <p></p>
 * <ol>
 * <li> Create a subclass to TestConfigurationFieldsBase
 * <li> Define <code>initializeMemberVariables</code> method in the
 *      subclass.  In this class, do the following:
 * <p></p>
 *   <ol>
 *   <li> <b>Required</b> Set the variable <code>xmlFilename</code> to
 *        the appropriate xml definition file
 *   <li> <b>Required</b> Set the variable <code>configurationClasses</code>
 *        to an array of the classes which define the constants used by the
 *        code corresponding to the xml files
 *   <li> <b>Optional</b> Set <code>errorIfMissingConfigProps</code> if the
 *        subclass should throw an error in the method
 *        <code>testCompareXmlAgainstConfigurationClass</code>
 *   <li> <b>Optional</b> Set <code>errorIfMissingXmlProps</code> if the
 *        subclass should throw an error in the method
 *        <code>testCompareConfigurationClassAgainstXml</code>
 *   <li> <b>Optional</b> Instantiate and populate strings into one or
 *        more of the following variables:
 *        <br><code>configurationPropsToSkipCompare</code>
 *        <br><code>configurationPrefixToSkipCompare</code>
 *        <br><code>xmlPropsToSkipCompare</code>
 *        <br><code>xmlPrefixToSkipCompare</code>
 *        <br>
 *        in order to get comparisons clean
 *   </ol>
 * </ol>
 * <p></p>
 * The tests to do class-to-file and file-to-class should automatically
 * run.  This class (and its subclasses) are mostly not intended to be
 * overridden, but to do a very specific form of comparison testing.
 */
@Ignore
public abstract class TestConfigurationFieldsBase {
    private static final Logger LOG = LoggerFactory.getLogger(TestConfigurationFieldsBase.class);

    private static final Logger LOG_CONFIG = LoggerFactory.getLogger("org.apache.hadoop.conf.TestConfigurationFieldsBase.config");

    private static final Logger LOG_XML = LoggerFactory.getLogger("org.apache.hadoop.conf.TestConfigurationFieldsBase.xml");

    /**
     * Member variable for storing xml filename.
     */
    protected String xmlFilename = null;

    /**
     * Member variable for storing all related Configuration classes.
     */
    protected Class[] configurationClasses = null;

    /**
     * Throw error during comparison if missing configuration properties.
     * Intended to be set by subclass.
     */
    protected boolean errorIfMissingConfigProps = false;

    /**
     * Throw error during comparison if missing xml properties.  Intended
     * to be set by subclass.
     */
    protected boolean errorIfMissingXmlProps = false;

    /**
     * Set of properties to skip extracting (and thus comparing later) in
     * {@link #extractMemberVariablesFromConfigurationFields(Field[])}.
     */
    protected Set<String> configurationPropsToSkipCompare = new HashSet<>();

    /**
     * Set of property prefixes to skip extracting (and thus comparing later)
     * in * extractMemberVariablesFromConfigurationFields.
     */
    protected Set<String> configurationPrefixToSkipCompare = new HashSet<>();

    /**
     * Set of properties to skip extracting (and thus comparing later) in
     * extractPropertiesFromXml.
     */
    protected Set<String> xmlPropsToSkipCompare = new HashSet<>();

    /**
     * Set of property prefixes to skip extracting (and thus comparing later)
     * in extractPropertiesFromXml.
     */
    protected Set<String> xmlPrefixToSkipCompare = new HashSet<>();

    /**
     * Member variable to store Configuration variables for later comparison.
     */
    private Map<String, String> configurationMemberVariables = null;

    /**
     * Member variable to store Configuration variables for later reference.
     */
    private Map<String, String> configurationDefaultVariables = null;

    /**
     * Member variable to store XML properties for later comparison.
     */
    private Map<String, String> xmlKeyValueMap = null;

    /**
     * Member variable to store Configuration variables that are not in the
     * corresponding XML file.
     */
    private Set<String> configurationFieldsMissingInXmlFile = null;

    /**
     * Member variable to store XML variables that are not in the
     * corresponding Configuration class(es).
     */
    private Set<String> xmlFieldsMissingInConfiguration = null;

    /**
     * A set of strings used to check for collision of default values.
     * For each of the set's strings, the default values containing that string
     * in their name should not coincide.
     */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Set<String> filtersForDefaultValueCollisionCheck = new HashSet<>();

    /**
     * Compares the properties that are in the Configuration class, but not
     * in the XML properties file.
     */
    @Test
    public void testCompareConfigurationClassAgainstXml() {
        // Error if subclass hasn't set class members
        Assert.assertNotNull(xmlFilename);
        Assert.assertNotNull(configurationClasses);
        final int missingXmlSize = configurationFieldsMissingInXmlFile.size();
        for (Class c : configurationClasses) {
            TestConfigurationFieldsBase.LOG.info(c.toString());
        }
        TestConfigurationFieldsBase.LOG.info("({} member variables)\n", configurationMemberVariables.size());
        StringBuilder xmlErrorMsg = new StringBuilder();
        for (Class c : configurationClasses) {
            xmlErrorMsg.append(c);
            xmlErrorMsg.append(" ");
        }
        xmlErrorMsg.append("has ");
        xmlErrorMsg.append(missingXmlSize);
        xmlErrorMsg.append(" variables missing in ");
        xmlErrorMsg.append(xmlFilename);
        TestConfigurationFieldsBase.LOG.error(xmlErrorMsg.toString());
        if (missingXmlSize == 0) {
            TestConfigurationFieldsBase.LOG.info("  (None)");
        } else {
            appendMissingEntries(xmlErrorMsg, configurationFieldsMissingInXmlFile);
        }
        TestConfigurationFieldsBase.LOG.info("\n=====\n");
        if (errorIfMissingXmlProps) {
            Assert.assertEquals(xmlErrorMsg.toString(), 0, missingXmlSize);
        }
    }

    /**
     * Compares the properties that are in the XML properties file, but not
     * in the Configuration class.
     */
    @Test
    public void testCompareXmlAgainstConfigurationClass() {
        // Error if subclass hasn't set class members
        Assert.assertNotNull(xmlFilename);
        Assert.assertNotNull(configurationClasses);
        final int missingConfigSize = xmlFieldsMissingInConfiguration.size();
        TestConfigurationFieldsBase.LOG.info("File {} ({} properties)", xmlFilename, xmlKeyValueMap.size());
        StringBuilder configErrorMsg = new StringBuilder();
        configErrorMsg.append(xmlFilename);
        configErrorMsg.append(" has ");
        configErrorMsg.append(missingConfigSize);
        configErrorMsg.append(" properties missing in");
        Arrays.stream(configurationClasses).forEach(( c) -> configErrorMsg.append("  ").append(c));
        TestConfigurationFieldsBase.LOG.info(configErrorMsg.toString());
        if (missingConfigSize == 0) {
            TestConfigurationFieldsBase.LOG.info("  (None)");
        } else {
            appendMissingEntries(configErrorMsg, xmlFieldsMissingInConfiguration);
        }
        TestConfigurationFieldsBase.LOG.info("\n=====\n");
        if (errorIfMissingConfigProps) {
            Assert.assertEquals(configErrorMsg.toString(), 0, missingConfigSize);
        }
    }

    /**
     * For each property in the XML file, verify that the value matches
     * up to the default if one exists.
     */
    @Test
    public void testXmlAgainstDefaultValuesInConfigurationClass() {
        // Error if subclass hasn't set class members
        Assert.assertNotNull(xmlFilename);
        Assert.assertNotNull(configurationMemberVariables);
        Assert.assertNotNull(configurationDefaultVariables);
        TreeSet<String> xmlPropertiesWithEmptyValue = new TreeSet<>();
        TreeSet<String> configPropertiesWithNoDefaultConfig = new TreeSet<>();
        HashMap<String, String> xmlPropertiesMatchingConfigDefault = new HashMap<>();
        // Ugly solution.  Should have tuple-based solution.
        HashMap<HashMap<String, String>, HashMap<String, String>> mismatchingXmlConfig = new HashMap<>();
        for (Map.Entry<String, String> xEntry : xmlKeyValueMap.entrySet()) {
            String xmlProperty = xEntry.getKey();
            String xmlDefaultValue = xEntry.getValue();
            String configProperty = configurationMemberVariables.get(xmlProperty);
            if (configProperty != null) {
                String defaultConfigName = null;
                String defaultConfigValue = null;
                // Type 1: Prepend DEFAULT_
                String defaultNameCheck1 = "DEFAULT_" + configProperty;
                String defaultValueCheck1 = configurationDefaultVariables.get(defaultNameCheck1);
                // Type 2: Swap _KEY suffix with _DEFAULT suffix
                String defaultNameCheck2 = null;
                if (configProperty.endsWith("_KEY")) {
                    defaultNameCheck2 = (configProperty.substring(0, ((configProperty.length()) - 4))) + "_DEFAULT";
                }
                String defaultValueCheck2 = configurationDefaultVariables.get(defaultNameCheck2);
                // Type Last: Append _DEFAULT suffix
                String defaultNameCheck3 = configProperty + "_DEFAULT";
                String defaultValueCheck3 = configurationDefaultVariables.get(defaultNameCheck3);
                // Pick the default value that exists
                if (defaultValueCheck1 != null) {
                    defaultConfigName = defaultNameCheck1;
                    defaultConfigValue = defaultValueCheck1;
                } else
                    if (defaultValueCheck2 != null) {
                        defaultConfigName = defaultNameCheck2;
                        defaultConfigValue = defaultValueCheck2;
                    } else
                        if (defaultValueCheck3 != null) {
                            defaultConfigName = defaultNameCheck3;
                            defaultConfigValue = defaultValueCheck3;
                        }


                if (defaultConfigValue != null) {
                    if (xmlDefaultValue == null) {
                        xmlPropertiesWithEmptyValue.add(xmlProperty);
                    } else
                        if (!(xmlDefaultValue.equals(defaultConfigValue))) {
                            HashMap<String, String> xmlEntry = new HashMap<>();
                            xmlEntry.put(xmlProperty, xmlDefaultValue);
                            HashMap<String, String> configEntry = new HashMap<>();
                            configEntry.put(defaultConfigName, defaultConfigValue);
                            mismatchingXmlConfig.put(xmlEntry, configEntry);
                        } else {
                            xmlPropertiesMatchingConfigDefault.put(xmlProperty, defaultConfigName);
                        }

                } else {
                    configPropertiesWithNoDefaultConfig.add(configProperty);
                }
            }
        }
        // Print out any unknown mismatching XML value/Config default value
        TestConfigurationFieldsBase.LOG.info("{} has {} properties that do not match the default Config value", xmlFilename, mismatchingXmlConfig.size());
        if (mismatchingXmlConfig.isEmpty()) {
            TestConfigurationFieldsBase.LOG.info("  (None)");
        } else {
            for (Map.Entry<HashMap<String, String>, HashMap<String, String>> xcEntry : mismatchingXmlConfig.entrySet()) {
                xcEntry.getKey().forEach(( key, value) -> {
                    TestConfigurationFieldsBase.LOG.info("XML Property: {}", key);
                    TestConfigurationFieldsBase.LOG.info("XML Value:    {}", value);
                });
                xcEntry.getValue().forEach(( key, value) -> {
                    TestConfigurationFieldsBase.LOG.info("Config Name:  {}", key);
                    TestConfigurationFieldsBase.LOG.info("Config Value: {}", value);
                });
                TestConfigurationFieldsBase.LOG.info("");
            }
        }
        TestConfigurationFieldsBase.LOG.info("\n");
        // Print out Config properties that have no corresponding DEFAULT_*
        // variable and cannot do any XML comparison (i.e. probably needs to
        // be checked by hand)
        TestConfigurationFieldsBase.LOG.info(("Configuration(s) have {} " + (" properties with no corresponding default member variable.  These" + " will need to be verified manually.")), configPropertiesWithNoDefaultConfig.size());
        if (configPropertiesWithNoDefaultConfig.isEmpty()) {
            TestConfigurationFieldsBase.LOG.info("  (None)");
        } else {
            configPropertiesWithNoDefaultConfig.forEach(( c) -> TestConfigurationFieldsBase.LOG.info(" {}", c));
        }
        TestConfigurationFieldsBase.LOG.info("\n");
        // MAYBE TODO Print out any known mismatching XML value/Config default
        // Print out XML properties that have empty values (i.e. should result
        // in code-based default)
        TestConfigurationFieldsBase.LOG.info("{} has {} properties with empty values", xmlFilename, xmlPropertiesWithEmptyValue.size());
        if (xmlPropertiesWithEmptyValue.isEmpty()) {
            TestConfigurationFieldsBase.LOG.info("  (None)");
        } else {
            xmlPropertiesWithEmptyValue.forEach(( p) -> TestConfigurationFieldsBase.LOG.info("  {}", p));
        }
        TestConfigurationFieldsBase.LOG.info("\n");
        // Print out any matching XML value/Config default value
        TestConfigurationFieldsBase.LOG.info("{} has {} properties which match a corresponding Config variable", xmlFilename, xmlPropertiesMatchingConfigDefault.size());
        if (xmlPropertiesMatchingConfigDefault.isEmpty()) {
            TestConfigurationFieldsBase.LOG.info("  (None)");
        } else {
            xmlPropertiesMatchingConfigDefault.forEach(( key, value) -> TestConfigurationFieldsBase.LOG.info("  {} / {}", key, value));
        }
        TestConfigurationFieldsBase.LOG.info("\n=====\n");
    }

    /**
     * For each specified string, get the default parameter values whose names
     * contain the string. Then check whether any of these default values collide.
     * This is, for example, useful to make sure there is no collision of default
     * ports across different services.
     */
    @Test
    public void testDefaultValueCollision() {
        for (String filter : filtersForDefaultValueCollisionCheck) {
            TestConfigurationFieldsBase.LOG.info(("Checking if any of the default values whose name " + "contains string \"{}\" collide."), filter);
            // Map from filtered default value to name of the corresponding parameter.
            Map<String, String> filteredValues = new HashMap<>();
            int valuesChecked = 0;
            for (Map.Entry<String, String> ent : configurationDefaultVariables.entrySet()) {
                // Apply the name filter to the default parameters.
                if (ent.getKey().contains(filter)) {
                    // Check only for numerical values.
                    if (StringUtils.isNumeric(ent.getValue())) {
                        String crtValue = filteredValues.putIfAbsent(ent.getValue(), ent.getKey());
                        Assert.assertNull((((("Parameters " + (ent.getKey())) + " and ") + crtValue) + " are using the same default value!"), crtValue);
                    }
                    valuesChecked++;
                }
            }
            TestConfigurationFieldsBase.LOG.info("Checked {} default values for collision.", valuesChecked);
        }
    }
}

