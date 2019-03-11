/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.distributed.internal;


import StartupMessageData.HOSTED_LOCATORS;
import java.io.DataInput;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.geode.DataSerializer;
import org.apache.geode.internal.ByteArrayData;
import org.apache.geode.test.junit.categories.MembershipTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static StartupMessageData.COMMA_DELIMITER;


/**
 * Tests {@link StartupMessageData}.
 *
 * @since GemFire 7.0
 */
@Category({ MembershipTest.class })
public class StartupMessageDataJUnitTest {
    @Test
    public void testWriteHostedLocatorsWithEmpty() throws Exception {
        Collection<String> hostedLocators = new ArrayList<String>();
        StartupMessageData data = new StartupMessageData();
        data.writeHostedLocators(hostedLocators);
        Assert.assertTrue(data.getOptionalFields().isEmpty());
    }

    @Test
    public void testWriteHostedLocatorsWithNull() throws Exception {
        Collection<String> hostedLocators = null;
        StartupMessageData data = new StartupMessageData();
        data.writeHostedLocators(hostedLocators);
        Assert.assertTrue(data.getOptionalFields().isEmpty());
    }

    @Test
    public void testWriteHostedLocatorsWithOne() throws Exception {
        String locatorString = createOneLocatorString();
        List<String> hostedLocators = new ArrayList<String>();
        hostedLocators.add(locatorString);
        StartupMessageData data = new StartupMessageData();
        data.writeHostedLocators(hostedLocators);
        Assert.assertEquals(1, data.getOptionalFields().size());
        Assert.assertEquals(locatorString, data.getOptionalFields().get(HOSTED_LOCATORS));
    }

    @Test
    public void testWriteHostedLocatorsWithThree() throws Exception {
        String[] locatorStrings = createManyLocatorStrings(3);
        List<String> hostedLocators = new ArrayList<String>();
        for (int i = 0; i < 3; i++) {
            hostedLocators.add(locatorStrings[i]);
        }
        StartupMessageData data = new StartupMessageData();
        data.writeHostedLocators(hostedLocators);
        Assert.assertEquals(1, data.getOptionalFields().size());
        String hostedLocatorsField = data.getOptionalFields().getProperty(HOSTED_LOCATORS);
        StringTokenizer st = new StringTokenizer(hostedLocatorsField, COMMA_DELIMITER);
        for (int i = 0; st.hasMoreTokens(); i++) {
            Assert.assertEquals(locatorStrings[i], st.nextToken());
        }
    }

    @Test
    public void testReadHostedLocatorsWithThree() throws Exception {
        // set up the data
        String[] locatorStrings = createManyLocatorStrings(3);
        List<String> hostedLocators = new ArrayList<String>();
        for (int i = 0; i < 3; i++) {
            hostedLocators.add(locatorStrings[i]);
        }
        StartupMessageData data = new StartupMessageData();
        data.writeHostedLocators(hostedLocators);
        Assert.assertEquals(1, data.getOptionalFields().size());
        // test readHostedLocators
        int i = 0;
        Collection<String> readLocatorStrings = data.readHostedLocators();
        Assert.assertEquals(3, readLocatorStrings.size());
        for (String readLocatorString : readLocatorStrings) {
            Assert.assertEquals(locatorStrings[i], readLocatorString);
            i++;
        }
    }

    @Test
    public void testToDataWithEmptyHostedLocators() throws Exception {
        Collection<String> hostedLocators = new ArrayList<String>();
        StartupMessageData data = new StartupMessageData();
        data.writeHostedLocators(hostedLocators);
        ByteArrayData testStream = new ByteArrayData();
        Assert.assertTrue(testStream.isEmpty());
        DataOutputStream out = testStream.getDataOutput();
        data.writeTo(out);
        Assert.assertTrue(((testStream.size()) > 0));
        DataInput in = testStream.getDataInput();
        Properties props = ((Properties) (DataSerializer.readObject(in)));
        Assert.assertNull(props);
    }

    @Test
    public void testToDataWithNullHostedLocators() throws Exception {
        Collection<String> hostedLocators = null;
        StartupMessageData data = new StartupMessageData();
        data.writeHostedLocators(hostedLocators);
        ByteArrayData testStream = new ByteArrayData();
        Assert.assertTrue(testStream.isEmpty());
        DataOutputStream out = testStream.getDataOutput();
        data.writeTo(out);
        Assert.assertTrue(((testStream.size()) > 0));
        DataInput in = testStream.getDataInput();
        Properties props = ((Properties) (DataSerializer.readObject(in)));
        Assert.assertNull(props);
    }

    @Test
    public void testToDataWithOneHostedLocator() throws Exception {
        String locatorString = createOneLocatorString();
        List<String> hostedLocators = new ArrayList<String>();
        hostedLocators.add(locatorString);
        StartupMessageData data = new StartupMessageData();
        data.writeHostedLocators(hostedLocators);
        ByteArrayData testStream = new ByteArrayData();
        Assert.assertTrue(testStream.isEmpty());
        DataOutputStream out = testStream.getDataOutput();
        data.writeTo(out);
        Assert.assertTrue(((testStream.size()) > 0));
        DataInput in = testStream.getDataInput();
        Properties props = ((Properties) (DataSerializer.readObject(in)));
        Assert.assertNotNull(props);
        String hostedLocatorsString = props.getProperty(HOSTED_LOCATORS);
        Assert.assertNotNull(hostedLocatorsString);
        Assert.assertEquals(locatorString, hostedLocatorsString);
    }

    @Test
    public void testToDataWithThreeHostedLocators() throws Exception {
        String[] locatorStrings = createManyLocatorStrings(3);
        List<String> hostedLocators = new ArrayList<String>();
        for (int i = 0; i < 3; i++) {
            hostedLocators.add(locatorStrings[i]);
        }
        StartupMessageData data = new StartupMessageData();
        data.writeHostedLocators(hostedLocators);
        ByteArrayData testStream = new ByteArrayData();
        Assert.assertTrue(testStream.isEmpty());
        DataOutputStream out = testStream.getDataOutput();
        data.writeTo(out);
        Assert.assertTrue(((testStream.size()) > 0));
        DataInput in = testStream.getDataInput();
        Properties props = ((Properties) (DataSerializer.readObject(in)));
        Assert.assertNotNull(props);
        String hostedLocatorsString = props.getProperty(HOSTED_LOCATORS);
        Assert.assertNotNull(hostedLocatorsString);
        Collection<String> actualLocatorStrings = new ArrayList<String>(1);
        StringTokenizer st = new StringTokenizer(hostedLocatorsString, COMMA_DELIMITER);
        while (st.hasMoreTokens()) {
            actualLocatorStrings.add(st.nextToken());
        } 
        Assert.assertEquals(3, actualLocatorStrings.size());
        int i = 0;
        for (String actualLocatorString : actualLocatorStrings) {
            Assert.assertEquals(locatorStrings[i], actualLocatorString);
            i++;
        }
    }

    @Test
    public void testNullHostedLocator() throws Exception {
        String locatorString = null;
        DataInput in = getDataInputWithOneHostedLocator(locatorString);
        StartupMessageData dataToRead = new StartupMessageData();
        dataToRead.readFrom(in);
        Collection<String> readHostedLocators = dataToRead.readHostedLocators();
        Assert.assertNull(readHostedLocators);
    }

    @Test
    public void testEmptyHostedLocator() throws Exception {
        String locatorString = "";
        DataInput in = getDataInputWithOneHostedLocator(locatorString);
        StartupMessageData dataToRead = new StartupMessageData();
        dataToRead.readFrom(in);
        Collection<String> readHostedLocators = dataToRead.readHostedLocators();
        Assert.assertNull(readHostedLocators);
    }

    @Test
    public void testOneHostedLocator() throws Exception {
        String locatorString = createOneLocatorString();
        DataInput in = getDataInputWithOneHostedLocator(locatorString);
        StartupMessageData dataToRead = new StartupMessageData();
        dataToRead.readFrom(in);
        Collection<String> readHostedLocators = dataToRead.readHostedLocators();
        Assert.assertNotNull(readHostedLocators);
        Assert.assertEquals(1, readHostedLocators.size());
        Assert.assertEquals(locatorString, readHostedLocators.iterator().next());
    }
}

