/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.common.status;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static Level.OK;


public class StatusTest {
    @Test
    public void testConstructor1() throws Exception {
        Status status = new Status(OK, "message", "description");
        MatcherAssert.assertThat(status.getLevel(), Matchers.is(Level.OK));
        MatcherAssert.assertThat(status.getMessage(), Matchers.equalTo("message"));
        MatcherAssert.assertThat(status.getDescription(), Matchers.equalTo("description"));
    }

    @Test
    public void testConstructor2() throws Exception {
        Status status = new Status(OK, "message");
        MatcherAssert.assertThat(status.getLevel(), Matchers.is(Level.OK));
        MatcherAssert.assertThat(status.getMessage(), Matchers.equalTo("message"));
        MatcherAssert.assertThat(status.getDescription(), Matchers.isEmptyOrNullString());
    }

    @Test
    public void testConstructor3() throws Exception {
        Status status = new Status(OK);
        MatcherAssert.assertThat(status.getLevel(), Matchers.is(Level.OK));
        MatcherAssert.assertThat(status.getMessage(), Matchers.isEmptyOrNullString());
        MatcherAssert.assertThat(status.getDescription(), Matchers.isEmptyOrNullString());
    }
}

