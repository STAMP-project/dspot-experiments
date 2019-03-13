/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.security;


import AuditEvent.AuditParam;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * AuditEvent Junit test.
 */
public class AuditEventTest {
    @Test
    public void testSensitiveParam() {
        String name = "paramName";
        String value = "sensitiveValue";
        AuditEvent.AuditParam param = AuditParam.sensitive(name, value);
        MatcherAssert.assertThat(param.name(), CoreMatchers.is(name));
        MatcherAssert.assertThat(param.value().get(), CoreMatchers.is(value));
        MatcherAssert.assertThat(param.isSensitive(), CoreMatchers.is(true));
        MatcherAssert.assertThat(param.toString(), CoreMatchers.containsString(name));
        MatcherAssert.assertThat(param.toString(), CoreMatchers.not(CoreMatchers.containsString(value)));
    }
}

