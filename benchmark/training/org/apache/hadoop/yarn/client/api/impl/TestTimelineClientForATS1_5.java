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
package org.apache.hadoop.yarn.client.api.impl;


import YarnConfiguration.TIMELINE_SERVICE_ENTITYGROUP_FS_STORE_WITH_USER_DIR;
import java.io.File;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestTimelineClientForATS1_5 {
    private static final Logger LOG = LoggerFactory.getLogger(TestTimelineClientForATS1_5.class);

    private TimelineClientImpl client;

    private static FileContext localFS;

    private static File localActiveDir;

    private TimelineWriter spyTimelineWriter;

    private UserGroupInformation authUgi;

    @Test
    public void testPostEntities() throws Exception {
        client = createTimelineClient(getConfigurations());
        verifyForPostEntities(false);
    }

    @Test
    public void testPostEntitiesToKeepUnderUserDir() throws Exception {
        YarnConfiguration conf = getConfigurations();
        conf.setBoolean(TIMELINE_SERVICE_ENTITYGROUP_FS_STORE_WITH_USER_DIR, true);
        client = createTimelineClient(conf);
        verifyForPostEntities(true);
    }

    @Test
    public void testPutDomain() {
        client = createTimelineClient(getConfigurations());
        verifyForPutDomain(false);
    }

    @Test
    public void testPutDomainToKeepUnderUserDir() {
        YarnConfiguration conf = getConfigurations();
        conf.setBoolean(TIMELINE_SERVICE_ENTITYGROUP_FS_STORE_WITH_USER_DIR, true);
        client = createTimelineClient(conf);
        verifyForPutDomain(true);
    }
}

