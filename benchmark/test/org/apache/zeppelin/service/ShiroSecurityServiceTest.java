/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.service;


import ZeppelinConfiguration.ConfVars.ZEPPELIN_USERNAME_FORCE_LOWERCASE;
import java.io.IOException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.zeppelin.conf.ZeppelinConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest(SecurityUtils.class)
public class ShiroSecurityServiceTest {
    @Mock
    Subject subject;

    ShiroSecurityService shiroSecurityService;

    ZeppelinConfiguration zeppelinConfiguration;

    @Test
    public void canGetPrincipalName() {
        String expectedName = "java.security.Principal.getName()";
        setupPrincipalName(expectedName);
        Assert.assertEquals(expectedName, shiroSecurityService.getPrincipal());
    }

    @Test
    public void testUsernameForceLowerCase() throws IOException, InterruptedException {
        String expectedName = "java.security.Principal.getName()";
        System.setProperty(ZEPPELIN_USERNAME_FORCE_LOWERCASE.getVarName(), String.valueOf(true));
        setupPrincipalName(expectedName);
        Assert.assertEquals(expectedName.toLowerCase(), shiroSecurityService.getPrincipal());
        System.setProperty(ZEPPELIN_USERNAME_FORCE_LOWERCASE.getVarName(), String.valueOf(false));
    }
}

