/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.config;


import org.junit.Assert;
import org.junit.Test;


public class RoleConfigTest {
    @Test
    public void validate_presenceOfRoleName() {
        validatePresenceOfRoleName(new RoleConfigTest.Validator() {
            @Override
            public void validate(RoleConfig roleConfig, ValidationContext context) {
                roleConfig.validate(context);
            }
        });
    }

    @Test
    public void validate_nullNameInRole() {
        validateNullRoleName(new RoleConfigTest.Validator() {
            @Override
            public void validate(RoleConfig roleConfig, ValidationContext context) {
                roleConfig.validate(context);
            }
        });
    }

    @Test
    public void validate_uniquenessOfRoleName() throws Exception {
        validateUniquenessOfRoleName(new RoleConfigTest.Validator() {
            @Override
            public void validate(RoleConfig roleConfig, ValidationContext context) {
                roleConfig.validate(context);
            }
        });
    }

    @Test
    public void validateTree_presenceOfRoleName() {
        validatePresenceOfRoleName(new RoleConfigTest.Validator() {
            @Override
            public void validate(RoleConfig roleConfig, ValidationContext context) {
                Assert.assertFalse(roleConfig.validateTree(context));
            }
        });
    }

    @Test
    public void validateTree_nullNameInRole() {
        validateNullRoleName(new RoleConfigTest.Validator() {
            @Override
            public void validate(RoleConfig roleConfig, ValidationContext context) {
                roleConfig.validateTree(context);
            }
        });
    }

    @Test
    public void validateTree_uniquenessOfRoleName() throws Exception {
        validateUniquenessOfRoleName(new RoleConfigTest.Validator() {
            @Override
            public void validate(RoleConfig roleConfig, ValidationContext context) {
                Assert.assertFalse(roleConfig.validateTree(context));
            }
        });
    }

    interface Validator {
        void validate(RoleConfig roleConfig, ValidationContext context);
    }
}

