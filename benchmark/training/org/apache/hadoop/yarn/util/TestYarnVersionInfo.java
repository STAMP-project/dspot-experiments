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
package org.apache.hadoop.yarn.util;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 * A JUnit test to test {@link YarnVersionInfo}
 */
public class TestYarnVersionInfo {
    /**
     * Test the yarn version info routines.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void versionInfoGenerated() throws IOException {
        // can't easily know what the correct values are going to be so just
        // make sure they aren't Unknown
        Assert.assertNotEquals("getVersion returned Unknown", "Unknown", YarnVersionInfo.getVersion());
        Assert.assertNotEquals("getUser returned Unknown", "Unknown", YarnVersionInfo.getUser());
        Assert.assertNotEquals("getSrcChecksum returned Unknown", "Unknown", YarnVersionInfo.getSrcChecksum());
        // these could be Unknown if the VersionInfo generated from code not in svn or git
        // so just check that they return something
        Assert.assertNotNull("getUrl returned null", YarnVersionInfo.getUrl());
        Assert.assertNotNull("getRevision returned null", YarnVersionInfo.getRevision());
        Assert.assertNotNull("getBranch returned null", YarnVersionInfo.getBranch());
        Assert.assertTrue("getBuildVersion check doesn't contain: source checksum", YarnVersionInfo.getBuildVersion().contains("source checksum"));
    }
}

