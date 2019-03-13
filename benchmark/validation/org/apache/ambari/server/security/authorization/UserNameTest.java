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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.security.authorization;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class UserNameTest {
    private final String name;

    private final boolean valid;

    public UserNameTest(String name, boolean valid) {
        this.name = name;
        this.valid = valid;
    }

    @Test
    public void testRejectsForbiddenCharacters() throws Exception {
        try {
            Assert.assertEquals(name, UserName.fromString(name).toString());
            if (!(valid)) {
                Assert.fail((("Expected user " + (name)) + " to be invalid."));
            }
        } catch (IllegalArgumentException e) {
            if (valid) {
                Assert.fail(((("Expected user " + (name)) + " to be valid. But was: ") + (e.getMessage())));
            }
        }
    }
}

