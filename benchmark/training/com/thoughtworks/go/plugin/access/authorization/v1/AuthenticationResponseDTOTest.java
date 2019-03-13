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


import org.junit.Assert;
import org.junit.Test;


public class AuthenticationResponseDTOTest {
    @Test
    public void shouldAbleToDeserializeJSON() throws Exception {
        String json = "{\n" + (((((("  \"user\": {\n" + "      \"username\":\"gocd\",\n") + "      \"display_name\": \"GoCD Admin\",\n") + "      \"email\": \"gocd@go.cd\"\n") + "  },\n") + "  \"roles\": [\"admin\",\"blackbird\"]\n") + "}");
        AuthenticationResponseDTO authenticationResponse = AuthenticationResponseDTO.fromJSON(json);
        Assert.assertThat(authenticationResponse.getUser(), is(new UserDTO("gocd", "GoCD Admin", "gocd@go.cd")));
        Assert.assertThat(authenticationResponse.getRoles(), hasSize(2));
        Assert.assertThat(authenticationResponse.getRoles(), containsInAnyOrder("admin", "blackbird"));
    }
}

