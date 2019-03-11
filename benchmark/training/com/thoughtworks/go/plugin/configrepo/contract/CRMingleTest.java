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


import junit.framework.TestCase;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CRMingleTest extends CRBaseTest<CRMingle> {
    private final CRMingle mingle;

    private final CRMingle invalidNoUrl;

    private final CRMingle invalidNoId;

    public CRMingleTest() {
        mingle = new CRMingle("http://mingle.example.com", "my_project");
        invalidNoUrl = new CRMingle(null, "my_project");
        invalidNoId = new CRMingle("http://mingle.example.com", null);
    }

    @Test
    public void shouldDeserializeFromAPILikeObject() {
        String json = "{\n" + ((("    \"base_url\": \"https://mingle.example.com\",\n" + "    \"project_identifier\": \"foobar_widgets\",\n") + "    \"mql_grouping_conditions\": \"status > \'In Dev\'\"\n") + "  }");
        CRMingle deserializedValue = gson.fromJson(json, CRMingle.class);
        Assert.assertThat(deserializedValue.getBaseUrl(), Matchers.is("https://mingle.example.com"));
        Assert.assertThat(deserializedValue.getProjectIdentifier(), Matchers.is("foobar_widgets"));
        Assert.assertThat(deserializedValue.getMqlGroupingConditions(), Matchers.is("status > 'In Dev'"));
        ErrorCollection errors = deserializedValue.getErrors();
        TestCase.assertTrue(errors.isEmpty());
    }
}

