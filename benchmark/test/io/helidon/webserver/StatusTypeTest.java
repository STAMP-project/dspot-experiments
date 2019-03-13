/**
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.webserver;


import Http.ResponseStatus;
import Http.Status;
import Http.Status.OK_200;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * The StatusTypeTest.
 */
public class StatusTypeTest {
    @Test
    public void adHocStatusTypeObjectPropertiesTest() throws Exception {
        MatcherAssert.assertThat(((ResponseStatus.create(200)) == (Status.OK_200)), CoreMatchers.is(true));
        MatcherAssert.assertThat(((ResponseStatus.create(200).hashCode()) == (OK_200.hashCode())), CoreMatchers.is(true));
        MatcherAssert.assertThat(ResponseStatus.create(200).equals(OK_200), CoreMatchers.is(true));
        MatcherAssert.assertThat(ResponseStatus.create(999).equals(ResponseStatus.create(999)), CoreMatchers.is(true));
        // TODO if we were able to maintain even the '==' property, it would be awesome (cache the created ones?)
        MatcherAssert.assertThat(((ResponseStatus.create(999)) != (ResponseStatus.create(999))), CoreMatchers.is(true));
        MatcherAssert.assertThat(((ResponseStatus.create(999).hashCode()) == (ResponseStatus.create(999).hashCode())), CoreMatchers.is(true));
    }
}

