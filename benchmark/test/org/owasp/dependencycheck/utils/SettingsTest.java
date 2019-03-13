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
package org.owasp.dependencycheck.utils;


import Settings.KEYS;
import Settings.KEYS.DB_CONNECTION_STRING;
import Settings.KEYS.DB_FILE_NAME;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Jeremy Long
 */
public class SettingsTest extends BaseTest {
    /**
     * Test of getString method, of class Settings.
     */
    @Test
    public void testGetString() {
        String key = KEYS.CVE_MODIFIED_VALID_FOR_DAYS;
        String expResult = "7";
        String result = getSettings().getString(key);
        Assert.assertTrue(result.endsWith(expResult));
    }

    /**
     * Test of getDataFile method, of class Settings.
     */
    @Test
    public void testGetDataFile() throws IOException {
        String key = KEYS.DATA_DIRECTORY;
        String expResult = "data";
        File result = getSettings().getDataFile(key);
        Assert.assertTrue(result.getAbsolutePath().endsWith(expResult));
    }

    /**
     * Test of mergeProperties method, of class Settings.
     *
     * @throws java.io.IOException
     * 		thrown when the test fails
     * @throws java.net.URISyntaxException
     * 		thrown when the test fails
     */
    @Test
    public void testMergeProperties_String() throws IOException, URISyntaxException {
        String key = KEYS.PROXY_PORT;
        String expResult = getSettings().getString(key);
        File f = new File(this.getClass().getClassLoader().getResource("test.properties").toURI());
        // InputStream in = this.getClass().getClassLoader().getResourceAsStream("test.properties");
        getSettings().mergeProperties(f.getAbsolutePath());
        String result = getSettings().getString(key);
        Assert.assertTrue("setting didn't change?", (((expResult == null) && (result != null)) || (!(expResult.equals(result)))));
    }

    /**
     * Test of setString method, of class Settings.
     */
    @Test
    public void testSetString() {
        String key = "newProperty";
        String value = "someValue";
        getSettings().setString(key, value);
        String expResults = getSettings().getString(key);
        Assert.assertEquals(expResults, value);
    }

    /**
     * Test of setStringIfNotNull method, of class Settings.
     */
    @Test
    public void testSetStringIfNotNull() {
        String key = "nullableProperty";
        String value = "someValue";
        getSettings().setString(key, value);
        getSettings().setStringIfNotNull(key, null);// NO-OP

        String expResults = getSettings().getString(key);
        Assert.assertEquals(expResults, value);
    }

    /**
     * Test of setStringIfNotNull method, of class Settings.
     */
    @Test
    public void testSetStringIfNotEmpty() {
        String key = "optionalProperty";
        String value = "someValue";
        getSettings().setString(key, value);
        getSettings().setStringIfNotEmpty(key, "");// NO-OP

        String expResults = getSettings().getString(key);
        Assert.assertEquals(expResults, value);
    }

    /**
     * Test of getString method, of class Settings.
     */
    @Test
    public void testGetString_String_String() {
        String key = "key That Doesn't Exist";
        String defaultValue = "blue bunny";
        String expResult = "blue bunny";
        String result = getSettings().getString(key);
        Assert.assertTrue((result == null));
        result = getSettings().getString(key, defaultValue);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getString method, of class Settings.
     */
    @Test
    public void testGetString_String() {
        String key = KEYS.CONNECTION_TIMEOUT;
        String result = getSettings().getString(key);
        Assert.assertTrue((result == null));
    }

    /**
     * Test of getInt method, of class Settings.
     */
    @Test
    public void testGetInt() throws InvalidSettingException {
        String key = "SomeNumber";
        int expResult = 85;
        getSettings().setString(key, "85");
        int result = getSettings().getInt(key);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getInt method, of class Settings.
     */
    @Test
    public void testGetIntDefault() throws InvalidSettingException {
        String key = "SomeKey";
        int expResult = 85;
        getSettings().setString(key, "blue");
        int result = getSettings().getInt(key, expResult);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getLong method, of class Settings.
     */
    @Test
    public void testGetLong() throws InvalidSettingException {
        String key = "SomeNumber";
        long expResult = 300L;
        getSettings().setString(key, "300");
        long result = getSettings().getLong(key);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getBoolean method, of class Settings.
     */
    @Test
    public void testGetBoolean() throws InvalidSettingException {
        String key = "SomeBoolean";
        getSettings().setString(key, "false");
        boolean expResult = false;
        boolean result = getSettings().getBoolean(key);
        Assert.assertEquals(expResult, result);
        key = "something that does not exist";
        expResult = true;
        result = getSettings().getBoolean(key, true);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of removeProperty method, of class Settings.
     */
    @Test
    public void testRemoveProperty() {
        String key = "SomeKey";
        String value = "value";
        String dfault = "default";
        getSettings().setString(key, value);
        String ret = getSettings().getString(key);
        Assert.assertEquals(value, ret);
        getSettings().removeProperty(key);
        ret = getSettings().getString(key, dfault);
        Assert.assertEquals(dfault, ret);
    }

    /**
     * Test of getConnectionString.
     */
    @Test
    public void testGetConnectionString() throws Exception {
        String value = getSettings().getConnectionString(DB_CONNECTION_STRING, DB_FILE_NAME);
        Assert.assertNotNull(value);
        String msg = null;
        try {
            value = getSettings().getConnectionString("invalidKey", null);
        } catch (InvalidSettingException e) {
            msg = e.getMessage();
        }
        Assert.assertNotNull(msg);
    }

    /**
     * Test of getTempDirectory.
     */
    @Test
    public void testGetTempDirectory() throws Exception {
        File tmp = getSettings().getTempDirectory();
        Assert.assertTrue(tmp.exists());
    }

    /**
     * Assert {@link Settings#getArray(String)} from a delimited string returns
     * multiple values in an array.
     */
    @Test
    public void testGetArrayFromADelimitedString() {
        // GIVEN a delimited string
        final String delimitedString = "value1,value2";
        getSettings().setString("key", delimitedString);
        // WHEN getting the array
        final String[] array = getSettings().getArray("key");
        // THEN the split array is returned
        Assert.assertThat("Expected the array to be non-null", array, IsNull.notNullValue());
        Assert.assertThat("Expected the array to have two values", array.length, Is.is(2));
        Assert.assertThat("Expected the first array value to be value1", array[0], Is.is("value1"));
        Assert.assertThat("Expected the second array value to be value2", array[1], Is.is("value2"));
    }

    /**
     * Assert {@link Settings#getArray(String)} returns {@code null} if the
     * property is not set.
     */
    @Test
    public void testGetArrayWhereThePropertyIsNotSet() {
        // WHEN getting the array
        final String[] array = getSettings().getArray("key");
        // THEN null is returned
        Assert.assertThat("Expected the array to be null", array, IsNull.nullValue());
    }

    /**
     * Assert {@link Settings#setArrayIfNotEmpty(String, String[])} with an
     * empty array is ignored.
     */
    @Test
    public void testSetArrayNotEmptyIgnoresAnEmptyArray() {
        // GIVEN an empty array
        final String[] array = new String[]{  };
        // WHEN setting the array
        getSettings().setArrayIfNotEmpty("key", array);
        // THEN the property was not set
        Assert.assertThat("Expected the property to not be set", getSettings().getString("key"), IsNull.nullValue());
    }

    /**
     * Assert {@link Settings#setArrayIfNotEmpty(String, String[])} with a null
     * array is ignored.
     */
    @Test
    public void testSetArrayNotEmptyIgnoresAnNullArray() {
        // GIVEN a null array
        final String[] array = null;
        // WHEN setting the array
        getSettings().setArrayIfNotEmpty("key", array);
        // THEN the property was not set
        Assert.assertThat("Expected the property to not be set", getSettings().getString("key"), IsNull.nullValue());
    }

    /**
     * Assert {@link Settings#setArrayIfNotEmpty(String, List<String>)}
     * correctly stores the list as an array.
     */
    @Test
    public void testSetArrayNotEmptyWithList() {
        // GIVEN a null array
        final List<String> list = new ArrayList<>();
        list.add("one");
        list.add("two");
        // WHEN setting the array
        getSettings().setArrayIfNotEmpty("key", list);
        // THEN the property was not set
        Assert.assertThat("Expected the property to not be set", getSettings().getArray("key"), IsEqual.equalTo(new String[]{ "one", "two" }));
    }
}

