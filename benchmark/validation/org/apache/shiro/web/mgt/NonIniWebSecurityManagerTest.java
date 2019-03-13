/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shiro.web.mgt;


import Subject.Builder;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.junit.Test;


public class NonIniWebSecurityManagerTest extends AbstractWebSecurityManagerTest {
    private DefaultWebSecurityManager sm;

    /**
     * Test for SHIRO-646: Unable to login a DelegatingSubject on a DefaultWebSecurityManager.
     */
    @Test
    public void testLoginNonWebSubject() {
        Subject.Builder builder = new Subject.Builder(sm);
        Subject subject = builder.buildSubject();
        subject.login(new UsernamePasswordToken("lonestarr", "vespa"));
    }
}

