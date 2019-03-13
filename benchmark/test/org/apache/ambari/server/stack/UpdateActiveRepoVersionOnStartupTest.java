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
package org.apache.ambari.server.stack;


import org.apache.ambari.server.orm.dao.RepositoryVersionDAO;
import org.junit.Test;


/**
 * Unit test for {@link UpdateActiveRepoVersionOnStartup}
 */
public class UpdateActiveRepoVersionOnStartupTest {
    private static String CLUSTER_NAME = "c1";

    private static String ADD_ON_REPO_ID = "MSFT_R-8.0";

    private RepositoryVersionDAO repositoryVersionDao;

    private UpdateActiveRepoVersionOnStartup activeRepoUpdater;

    @Test
    public void addAServiceRepoToExistingRepoVersion() throws Exception {
        init(true);
        activeRepoUpdater.process();
        verifyRepoIsAdded();
    }

    @Test
    public void missingClusterVersionShouldNotCauseException() throws Exception {
        init(false);
        activeRepoUpdater.process();
    }
}

