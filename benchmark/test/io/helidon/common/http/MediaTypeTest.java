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
package io.helidon.common.http;


import java.util.Optional;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;


/**
 * Unit test for {@link MediaType}.
 */
class MediaTypeTest {
    @Test
    public void parseUnknownType() {
        MediaType mediaType = MediaType.parse("unknown-type/unknown-subtype");
        MatcherAssert.assertThat(mediaType.type(), Is.is("unknown-type"));
        MatcherAssert.assertThat(mediaType.subtype(), Is.is("unknown-subtype"));
        MatcherAssert.assertThat(mediaType.charset(), Is.is(Optional.empty()));
        MatcherAssert.assertThat(mediaType.parameters().entrySet(), iterableWithSize(0));
    }
}

