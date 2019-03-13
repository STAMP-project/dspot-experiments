/**
 * Copyright 2016 ThoughtWorks, Inc.
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
package com.thoughtworks.go.config.serialization;


import com.thoughtworks.go.config.CruiseConfig;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class RolesConfigTest {
    CruiseConfig config;

    @Test
    public void shouldSupportRoleWithNoUsers() throws Exception {
        addRole(role("test_role"));
    }

    @Test
    public void shouldNotSupportMultipleRolesWithTheSameName() throws Exception {
        addRole(role("test_role"));
        try {
            addRole(role("test_role"));
            Assert.fail("Role already exists");
        } catch (Exception expected) {
            Assert.assertThat(expected.getCause().getMessage(), Matchers.is("Role names should be unique. Duplicate names found."));
        }
    }

    @Test
    public void shouldNotSupportRoleWithTheMultipleUsersThatAreTheSame() throws Exception {
        Role role = role("test_role", user("chris"), user("chris"), user("bob"), user("john"));
        Assert.assertThat(role.getUsers(), Matchers.hasSize(3));
    }
}

