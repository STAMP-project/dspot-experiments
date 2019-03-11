/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.rest.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class EntityTest {
    private static final Set<Class<?>> BLACK_LIST = ImmutableSet.<Class<?>>builder().add(Versions.class).build();

    private final Class<?> entityClass;

    public EntityTest(final Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    @Test
    public void shouldBeAttributedWithIgnoreUnknownProperties() {
        final JsonIgnoreProperties annotation = entityClass.getAnnotation(JsonIgnoreProperties.class);
        MatcherAssert.assertThat(((entityClass) + ": @JsonIgnoreProperties annotation missing"), annotation, Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(((entityClass) + ": not ignoring unknown properties"), annotation.ignoreUnknown(), Matchers.is(true));
    }
}

