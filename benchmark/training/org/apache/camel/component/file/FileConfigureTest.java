/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.file;


import java.io.File;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ResolveEndpointFailedException;
import org.junit.Assert;
import org.junit.Test;


public class FileConfigureTest extends ContextTestSupport {
    private static final String EXPECT_PATH = ((((("target" + (File.separator)) + "data") + (File.separator)) + "foo") + (File.separator)) + "bar";

    private static final String EXPECT_FILE = ((("some" + (File.separator)) + "nested") + (File.separator)) + "filename.txt";

    private static final Processor DUMMY_PROCESSOR = new Processor() {
        public void process(Exchange exchange) throws Exception {
            // Do nothing here
        }
    };

    @Test
    public void testUriConfigurations() throws Exception {
        assertFileEndpoint("file://target/data/foo/bar", FileConfigureTest.EXPECT_PATH, false);
        assertFileEndpoint("file://target/data/foo/bar?delete=true", FileConfigureTest.EXPECT_PATH, false);
        assertFileEndpoint("file:target/data/foo/bar?delete=true", FileConfigureTest.EXPECT_PATH, false);
        assertFileEndpoint("file:target/data/foo/bar", FileConfigureTest.EXPECT_PATH, false);
        assertFileEndpoint("file://target/data/foo/bar/", FileConfigureTest.EXPECT_PATH, false);
        assertFileEndpoint("file://target/data/foo/bar/?delete=true", FileConfigureTest.EXPECT_PATH, false);
        assertFileEndpoint("file:target/data/foo/bar/?delete=true", FileConfigureTest.EXPECT_PATH, false);
        assertFileEndpoint("file:target/data/foo/bar/", FileConfigureTest.EXPECT_PATH, false);
        assertFileEndpoint("file:/target/data/foo/bar/", ((((File.separator) + (FileConfigureTest.EXPECT_PATH)) + (File.separator)) + (FileConfigureTest.EXPECT_FILE)), true);
        assertFileEndpoint("file:/", File.separator, true);
        assertFileEndpoint("file:///", File.separator, true);
    }

    @Test
    public void testUriWithParameters() throws Exception {
        FileEndpoint endpoint = resolveMandatoryEndpoint(("file:///C:/camel/temp?delay=10&useFixedDelay=true&initialDelay=10&consumer.bridgeErrorHandler=true" + "&autoCreate=false&startingDirectoryMustExist=true&directoryMustExist=true&readLock=changed"), FileEndpoint.class);
        Assert.assertNotNull("Could not find file endpoint", endpoint);
        Assert.assertEquals("Get a wrong option of StartingDirectoryMustExist", true, endpoint.isStartingDirectoryMustExist());
        endpoint = resolveMandatoryEndpoint(("file:///C:/camel/temp?delay=10&useFixedDelay=true&initialDelay=10&startingDirectoryMustExist=true" + "&consumer.bridgeErrorHandler=true&autoCreate=false&directoryMustExist=true&readLock=changed"), FileEndpoint.class);
        Assert.assertNotNull("Could not find file endpoint", endpoint);
        Assert.assertEquals("Get a wrong option of StartingDirectoryMustExist", true, endpoint.isStartingDirectoryMustExist());
        endpoint = resolveMandatoryEndpoint(("file:///C:/camel/temp?delay=10&startingDirectoryMustExist=true&useFixedDelay=true&initialDelay=10" + "&consumer.bridgeErrorHandler=true&autoCreate=false&directoryMustExist=true&readLock=changed"), FileEndpoint.class);
        Assert.assertNotNull("Could not find file endpoint", endpoint);
        Assert.assertEquals("Get a wrong option of StartingDirectoryMustExist", true, endpoint.isStartingDirectoryMustExist());
        endpoint = resolveMandatoryEndpoint("file:///C:/camel/temp?delay=10&useFixedDelay=true&initialDelay=10", FileEndpoint.class);
        Assert.assertNotNull("Could not find file endpoint", endpoint);
        Assert.assertEquals("Get a wrong option of StartingDirectoryMustExist", false, endpoint.isStartingDirectoryMustExist());
    }

    @Test
    public void testUriWithCharset() throws Exception {
        FileEndpoint endpoint = resolveMandatoryEndpoint("file://target/data/foo/bar?charset=UTF-8", FileEndpoint.class);
        Assert.assertNotNull("Could not find endpoint: file://target/data/foo/bar?charset=UTF-8", endpoint);
        Assert.assertEquals("Get a wrong charset", "UTF-8", endpoint.getCharset());
        try {
            resolveMandatoryEndpoint("file://target/data/foo/bar?charset=ASSI", FileEndpoint.class);
            // The charset is wrong
            Assert.fail("Expect a configure exception here");
        } catch (Exception ex) {
            Assert.assertTrue("Get the wrong exception type here", (ex instanceof ResolveEndpointFailedException));
        }
    }

    @Test
    public void testConsumerConfigurations() throws Exception {
        FileConsumer consumer = createFileConsumer("file://target/data/foo/bar?recursive=true");
        Assert.assertNotNull(consumer);
        try {
            createFileConsumer("file://target/data/foo/bar?recursiv=true");
            Assert.fail("Expect a configure exception here");
        } catch (Exception ex) {
            Assert.assertTrue("Get the wrong exception type here", (ex instanceof ResolveEndpointFailedException));
        }
    }
}

