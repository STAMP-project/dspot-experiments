/**
 * Copyright 2019 ThoughtWorks, Inc.
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
package com.thoughtworks.go.plugin.access.authorization.v1;


import SupportedAuthTypeDTO.Web;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CapabilitiesDTOTest {
    @Test
    public void shouldDeserializeFromJSON() throws Exception {
        String json = "" + (((("{\n" + "  \"supported_auth_type\": \"web\",\n") + "  \"can_search\": true,\n") + "  \"can_authorize\": true\n") + "}");
        CapabilitiesDTO capabilities = CapabilitiesDTO.fromJSON(json);
        Assert.assertThat(capabilities.getSupportedAuthType(), Matchers.is(Web));
        Assert.assertTrue(capabilities.canSearch());
        Assert.assertTrue(capabilities.canAuthorize());
    }
}

