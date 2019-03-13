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
package org.apache.hadoop.yarn.appcatalog.application;


import java.util.List;
import org.apache.hadoop.yarn.appcatalog.model.AppStoreEntry;
import org.apache.hadoop.yarn.appcatalog.model.Application;
import org.apache.solr.client.solrj.SolrClient;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for AppCatalogSolrClient.
 */
public class TestAppCatalogSolrClient {
    static final String CONFIGSET_DIR = "src/test/resources/configsets";

    private static SolrClient solrClient;

    private static AppCatalogSolrClient spy;

    @Test
    public void testRegister() throws Exception {
        Application example = new Application();
        example.setOrganization("jenkins-ci.org");
        example.setName("jenkins");
        example.setDescription("World leading open source automation system.");
        example.setIcon("/css/img/feather.png");
        TestAppCatalogSolrClient.spy.register(example);
        List<AppStoreEntry> apps = TestAppCatalogSolrClient.spy.getRecommendedApps();
        Assert.assertEquals(1, apps.size());
    }

    @Test
    public void testSearch() throws Exception {
        Application example = new Application();
        example.setOrganization("jenkins-ci.org");
        example.setName("jenkins");
        example.setDescription("World leading open source automation system.");
        example.setIcon("/css/img/feather.png");
        TestAppCatalogSolrClient.spy.register(example);
        List<AppStoreEntry> results = TestAppCatalogSolrClient.spy.search("name_s:jenkins");
        int expected = 1;
        int actual = results.size();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNotFoundSearch() throws Exception {
        Application example = new Application();
        example.setOrganization("jenkins-ci.org");
        example.setName("jenkins");
        example.setDescription("World leading open source automation system.");
        example.setIcon("/css/img/feather.png");
        TestAppCatalogSolrClient.spy.register(example);
        List<AppStoreEntry> results = TestAppCatalogSolrClient.spy.search("name_s:abc");
        int expected = 0;
        int actual = results.size();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetRecommendedApps() throws Exception {
        List<AppStoreEntry> expected = TestAppCatalogSolrClient.spy.getRecommendedApps();
        List<AppStoreEntry> actual = TestAppCatalogSolrClient.spy.getRecommendedApps();
        Assert.assertEquals(expected, actual);
    }
}

