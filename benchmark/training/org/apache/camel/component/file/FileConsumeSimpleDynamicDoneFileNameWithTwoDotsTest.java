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


import Exchange.FILE_NAME;
import java.io.File;
import java.lang.invoke.MethodHandles;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.builder.NotifyBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests an issue where an input file is not picked up due to a
 * dynamic doneFileName using the simple syntax and containing two dots.
 */
public class FileConsumeSimpleDynamicDoneFileNameWithTwoDotsTest extends ContextTestSupport {
    private static final String TARGET_DIR_NAME = "target/data/" + (MethodHandles.lookup().lookupClass().getSimpleName());

    @Test
    public void testSimpleDynamicDoneFileNameContainingTwoDots() throws Exception {
        NotifyBuilder notify = whenDone(1).create();
        getMockEndpoint("mock:result").expectedBodiesReceivedInAnyOrder("input-body");
        template.sendBodyAndHeader(("file:" + (FileConsumeSimpleDynamicDoneFileNameWithTwoDotsTest.TARGET_DIR_NAME)), "input-body", FILE_NAME, "test.twodot.txt");
        template.sendBodyAndHeader(("file:" + (FileConsumeSimpleDynamicDoneFileNameWithTwoDotsTest.TARGET_DIR_NAME)), "done-body", FILE_NAME, "test.twodot.done");
        assertMockEndpointsSatisfied();
        Assert.assertTrue(notify.matchesMockWaitTime());
        Assert.assertFalse("Input file should be deleted", new File(FileConsumeSimpleDynamicDoneFileNameWithTwoDotsTest.TARGET_DIR_NAME, "test.twodot.txt").exists());
        Assert.assertFalse("Done file should be deleted", new File(FileConsumeSimpleDynamicDoneFileNameWithTwoDotsTest.TARGET_DIR_NAME, "test.twodot.done").exists());
    }
}

