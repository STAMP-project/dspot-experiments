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
package io.helidon.config.tests.mappers2;


import io.helidon.config.Config;
import java.math.BigInteger;
import java.util.OptionalInt;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * Module {@code module-mappers-2-override} overrides built-in mappers for {@link Integer}, {@link OptionalInt}
 * and {@link BigInteger}.
 */
public abstract class AbstractDifferentIntMapperServicesTest {
    protected static final String KEY = "int-p";

    protected static final String CONFIGURED_VALUE = "2147483647";

    protected static final int CONFIGURED_INT = Integer.parseInt(AbstractDifferentIntMapperServicesTest.CONFIGURED_VALUE);

    @Test
    public void testDifferentInts() {
        Config config = configBuilder().build().get(AbstractDifferentIntMapperServicesTest.KEY);
        MatcherAssert.assertThat(config.asInt().get(), Matchers.is(expected()));
        MatcherAssert.assertThat(config.as(Integer.class).get(), Matchers.is(expected()));
        MatcherAssert.assertThat(config.as(BigInteger.class).get(), Matchers.is(BigInteger.valueOf(expected())));
        MatcherAssert.assertThat(config.as(OptionalInt.class).get(), Matchers.is(OptionalInt.of(expected())));
        MatcherAssert.assertThat(config.asInt().get(), Matchers.is(expected()));
        MatcherAssert.assertThat(config.as(Integer.class).get(), Matchers.is(expected()));
        MatcherAssert.assertThat(config.as(BigInteger.class).get(), Matchers.is(BigInteger.valueOf(expected())));
    }
}

