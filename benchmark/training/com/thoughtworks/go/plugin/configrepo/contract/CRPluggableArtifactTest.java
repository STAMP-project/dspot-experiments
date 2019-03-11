/**
 * Copyright 2018 ThoughtWorks, Inc.
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
 */
package com.thoughtworks.go.plugin.configrepo.contract;


import CRArtifactType.external;
import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CRPluggableArtifactTest extends CRBaseTest<CRPluggableArtifact> {
    private CRPluggableArtifact validArtifactWithNoConfiguration;

    private CRPluggableArtifact validArtifactWithConfiguration;

    private CRPluggableArtifact invalidArtifactWithNoId;

    private CRPluggableArtifact invalidArtifactWithNoStoreId;

    private CRPluggableArtifact invalidArtifactWithInvalidConfiguration;

    public CRPluggableArtifactTest() {
        validArtifactWithNoConfiguration = new CRPluggableArtifact("id", "storeId");
        validArtifactWithConfiguration = new CRPluggableArtifact("id", "storeId", new CRConfigurationProperty("foo", "bar"));
        invalidArtifactWithNoId = new CRPluggableArtifact(null, "storeId");
        invalidArtifactWithNoStoreId = new CRPluggableArtifact("id", null);
        invalidArtifactWithInvalidConfiguration = new CRPluggableArtifact("id", "storeId", new CRConfigurationProperty("foo", "bar", "baz"));
    }

    @Test
    public void shouldDeserializeWhenConfigurationIsNull() {
        String json = "{\n" + ((("           \"id\" : \"id\",\n" + "           \"store_id\" : \"s3\",\n") + "           \"type\": \"external\"\n") + "            }");
        CRPluggableArtifact crPluggableArtifact = gson.fromJson(json, CRPluggableArtifact.class);
        Assert.assertThat(crPluggableArtifact.getId(), Matchers.is("id"));
        Assert.assertThat(crPluggableArtifact.getStoreId(), Matchers.is("s3"));
        Assert.assertThat(crPluggableArtifact.getType(), Matchers.is(external));
        Assert.assertNull(crPluggableArtifact.getConfiguration());
    }

    @Test
    public void shouldCheckForTypeWhileDeserializing() {
        String json = "{\n" + (("           \"id\" : \"id\",\n" + "           \"store_id\" : \"s3\"\n") + "            }");
        CRPluggableArtifact crPluggableArtifact = gson.fromJson(json, CRPluggableArtifact.class);
        Assert.assertThat(crPluggableArtifact.getId(), Matchers.is("id"));
        Assert.assertThat(crPluggableArtifact.getStoreId(), Matchers.is("s3"));
        Assert.assertNull(crPluggableArtifact.getType());
        Assert.assertNull(crPluggableArtifact.getConfiguration());
        Assert.assertFalse(crPluggableArtifact.getErrors().isEmpty());
    }

    @Test
    public void shouldDeserializePluggableArtifacts() {
        String json = "{\n" + (((("              \"id\" : \"id\",\n" + "              \"store_id\" : \"s3\",\n") + "              \"type\": \"external\",\n") + "              \"configuration\": [{\"key\":\"image\", \"value\": \"gocd-agent\"}]") + "            }");
        CRPluggableArtifact crPluggableArtifact = gson.fromJson(json, CRPluggableArtifact.class);
        Assert.assertThat(crPluggableArtifact.getId(), Matchers.is("id"));
        Assert.assertThat(crPluggableArtifact.getStoreId(), Matchers.is("s3"));
        Assert.assertThat(crPluggableArtifact.getConfiguration(), Matchers.is(Arrays.asList(new CRConfigurationProperty("image", "gocd-agent"))));
    }
}

