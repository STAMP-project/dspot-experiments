/**
 * Copyright 2011-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.lettuce.core.commands;


import io.lettuce.core.TestSupport;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.test.LettuceExtension;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;


/**
 *
 *
 * @author Will Glozer
 * @author Mark Paluch
 */
@ExtendWith(LettuceExtension.class)
@TestInstance(PER_CLASS)
public class KeyCommandIntegrationTests extends TestSupport {
    private final RedisCommands<String, String> redis;

    @Inject
    protected KeyCommandIntegrationTests(RedisCommands<String, String> redis) {
        this.redis = redis;
    }

    @Test
    public void move() {
        redis.set(TestSupport.key, TestSupport.value);
        redis.move(TestSupport.key, 1);
        assertThat(redis.get(TestSupport.key)).isNull();
        redis.select(1);
        assertThat(redis.get(TestSupport.key)).isEqualTo(TestSupport.value);
    }
}

