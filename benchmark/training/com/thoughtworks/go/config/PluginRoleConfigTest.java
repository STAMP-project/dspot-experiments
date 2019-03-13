/**
 * Copyright 2017 ThoughtWorks, Inc.
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
package com.thoughtworks.go.config;


import com.thoughtworks.go.config.helper.ValidationContextMother;
import com.thoughtworks.go.domain.config.ConfigurationKey;
import com.thoughtworks.go.domain.config.ConfigurationProperty;
import com.thoughtworks.go.domain.config.ConfigurationValue;
import org.junit.Assert;
import org.junit.Test;


public class PluginRoleConfigTest {
    @Test
    public void validate_shouldValidatePresenceOfRoleName() {
        validatePresenceOfRoleName(new PluginRoleConfigTest.Validator() {
            @Override
            public void validate(PluginRoleConfig pluginRoleConfig, ValidationContext context) {
                pluginRoleConfig.validate(context);
            }
        });
    }

    @Test
    public void validate_shouldValidateNullRoleName() {
        validateNullRoleName(new PluginRoleConfigTest.Validator() {
            @Override
            public void validate(PluginRoleConfig pluginRoleConfig, ValidationContext context) {
                pluginRoleConfig.validate(context);
            }
        });
    }

    @Test
    public void validate_presenceAuthConfigId() {
        validatePresenceAuthConfigId(new PluginRoleConfigTest.Validator() {
            @Override
            public void validate(PluginRoleConfig pluginRoleConfig, ValidationContext context) {
                pluginRoleConfig.validate(context);
            }
        });
    }

    @Test
    public void validate_presenceOfAuthConfigIdInSecurityConfig() throws Exception {
        validatePresenceOfAuthConfigIdInSecurityConfig(new PluginRoleConfigTest.Validator() {
            @Override
            public void validate(PluginRoleConfig pluginRoleConfig, ValidationContext context) {
                pluginRoleConfig.validate(context);
            }
        });
    }

    @Test
    public void validate_uniquenessOfRoleName() throws Exception {
        validateUniquenessOfRoleName(new PluginRoleConfigTest.Validator() {
            @Override
            public void validate(PluginRoleConfig pluginRoleConfig, ValidationContext context) {
                pluginRoleConfig.validate(context);
            }
        });
    }

    @Test
    public void validateTree_shouldValidatePresenceOfRoleName() {
        validatePresenceOfRoleName(new PluginRoleConfigTest.Validator() {
            @Override
            public void validate(PluginRoleConfig pluginRoleConfig, ValidationContext context) {
                Assert.assertFalse(pluginRoleConfig.validateTree(context));
            }
        });
    }

    @Test
    public void validateTree_shouldValidateNullRoleName() {
        validateNullRoleName(new PluginRoleConfigTest.Validator() {
            @Override
            public void validate(PluginRoleConfig pluginRoleConfig, ValidationContext context) {
                pluginRoleConfig.validateTree(context);
            }
        });
    }

    @Test
    public void validateTree_presenceAuthConfigId() {
        validatePresenceAuthConfigId(new PluginRoleConfigTest.Validator() {
            @Override
            public void validate(PluginRoleConfig pluginRoleConfig, ValidationContext context) {
                Assert.assertFalse(pluginRoleConfig.validateTree(context));
            }
        });
    }

    @Test
    public void validateTree_presenceOfAuthConfigIdInSecurityConfig() throws Exception {
        validatePresenceOfAuthConfigIdInSecurityConfig(new PluginRoleConfigTest.Validator() {
            @Override
            public void validate(PluginRoleConfig pluginRoleConfig, ValidationContext context) {
                Assert.assertFalse(pluginRoleConfig.validateTree(context));
            }
        });
    }

    @Test
    public void validateTree_uniquenessOfRoleName() throws Exception {
        validateUniquenessOfRoleName(new PluginRoleConfigTest.Validator() {
            @Override
            public void validate(PluginRoleConfig pluginRoleConfig, ValidationContext context) {
                Assert.assertFalse(pluginRoleConfig.validateTree(context));
            }
        });
    }

    @Test
    public void hasErrors_shouldBeTrueIfRoleHasErrors() throws Exception {
        Role role = new PluginRoleConfig("", "auth_config_id");
        SecurityConfig securityConfig = new SecurityConfig();
        securityConfig.securityAuthConfigs().add(new SecurityAuthConfig("auth_config_id", "plugin_id"));
        role.validate(ValidationContextMother.validationContext(securityConfig));
        Assert.assertTrue(role.hasErrors());
    }

    @Test
    public void hasErrors_shouldBeTrueIfConfigurationPropertiesHasErrors() throws Exception {
        ConfigurationProperty property = new ConfigurationProperty(new ConfigurationKey("username"), new ConfigurationValue("view"));
        PluginRoleConfig roleConfig = new PluginRoleConfig("admin", "auth_id", property);
        property.addError("username", "username format is incorrect");
        Assert.assertTrue(roleConfig.hasErrors());
        Assert.assertTrue(roleConfig.errors().isEmpty());
    }

    interface Validator {
        void validate(PluginRoleConfig pluginRoleConfig, ValidationContext context);
    }
}

