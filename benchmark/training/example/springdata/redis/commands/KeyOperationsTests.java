/**
 * Copyright 2014-2018 the original author or authors.
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
package example.springdata.redis.commands;


import example.springdata.redis.test.util.RequiresRedisServer;
import java.util.Set;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Show usage of operations on redis keys using low level API provided by {@link RedisConnection}.
 *
 * @author Christoph Strobl
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class KeyOperationsTests {
    // we only want to run this tests when redis is up an running
    @ClassRule
    public static RequiresRedisServer requiresServer = RequiresRedisServer.onLocalhost();

    private static final String PREFIX = KeyOperationsTests.class.getSimpleName();

    private static final String KEY_PATTERN = (KeyOperationsTests.PREFIX) + "*";

    @Autowired
    RedisConnectionFactory connectionFactory;

    private RedisConnection connection;

    private RedisSerializer<String> serializer = new StringRedisSerializer();

    /**
     * Uses {@code KEYS} command for loading all matching keys. <br />
     * Note that {@code KEYS} is a blocking command that potentially might affect other operations execution time. <br />
     * All keys will be loaded within <strong>one single</strong> operation.
     */
    @Test
    public void iterateOverKeysMatchingPrefixUsingKeysCommand() {
        generateRandomKeys(1000);
        Set<byte[]> keys = this.connection.keys(serializer.serialize(KeyOperationsTests.KEY_PATTERN));
        printKeys(keys.iterator());
    }

    /**
     * Uses {@code SCAN} command for loading all matching keys. <br />
     * {@code SCAN} uses a cursor on server side returning only a subset of the available data with the possibility to
     * ripple load further elements using the cursors position. <br />
     * All keys will be loaded using <strong>multiple</strong> operations.
     */
    @Test
    public void iterateOverKeysMatchingPrefixUsingScanCommand() {
        generateRandomKeys(1000);
        Cursor<byte[]> cursor = this.connection.scan(ScanOptions.scanOptions().match(KeyOperationsTests.KEY_PATTERN).build());
        printKeys(cursor);
    }
}

