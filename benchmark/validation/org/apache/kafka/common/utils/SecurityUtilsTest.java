/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.utils;


import KafkaPrincipal.USER_TYPE;
import org.apache.kafka.common.security.auth.KafkaPrincipal;
import org.junit.Assert;
import org.junit.Test;


public class SecurityUtilsTest {
    @Test
    public void testPrincipalNameCanContainSeparator() {
        String name = "name:with:separator:in:it";
        KafkaPrincipal principal = SecurityUtils.parseKafkaPrincipal((((KafkaPrincipal.USER_TYPE) + ":") + name));
        Assert.assertEquals(USER_TYPE, principal.getPrincipalType());
        Assert.assertEquals(name, principal.getName());
    }

    @Test
    public void testParseKafkaPrincipalWithNonUserPrincipalType() {
        String name = "foo";
        String principalType = "Group";
        KafkaPrincipal principal = SecurityUtils.parseKafkaPrincipal(((principalType + ":") + name));
        Assert.assertEquals(principalType, principal.getPrincipalType());
        Assert.assertEquals(name, principal.getName());
    }
}

