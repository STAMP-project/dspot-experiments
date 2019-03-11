/**
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package keywhiz.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.dropwizard.setup.Bootstrap;
import java.io.File;
import javax.validation.Validation;
import javax.validation.Validator;
import keywhiz.KeywhizConfig;
import keywhiz.auth.ldap.LdapLookupConfig;
import org.junit.Test;


public class LdapLookupConfigTest {
    Bootstrap<KeywhizConfig> bootstrap;

    @Test
    public void parsesLDAPLookupCorrectly() throws Exception {
        File yamlFile = new File(Resources.getResource("fixtures/keywhiz-ldap-lookup-test.yaml").getFile());
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        ObjectMapper objectMapper = bootstrap.getObjectMapper().copy();
        LdapLookupConfig lookupConfig = new io.dropwizard.configuration.YamlConfigurationFactory(LdapLookupConfig.class, validator, objectMapper, "dw").build(yamlFile);
        assertThat(lookupConfig.getRequiredRoles()).containsOnly("keywhizAdmins");
        assertThat(lookupConfig.getRoleBaseDN()).isEqualTo("ou=ApplicationAccess,dc=test,dc=com");
        assertThat(lookupConfig.getUserBaseDN()).isEqualTo("ou=people,dc=test,dc=com");
        assertThat(lookupConfig.getUserAttribute()).isEqualTo("uid");
    }
}

