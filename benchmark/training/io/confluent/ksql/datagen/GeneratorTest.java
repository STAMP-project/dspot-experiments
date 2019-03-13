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
package io.confluent.ksql.datagen;


import io.confluent.avro.random.generator.Generator;
import java.nio.file.Path;
import java.util.Random;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class GeneratorTest {
    private static final Random RNG = new Random();

    private final Path fileName;

    private final String content;

    public GeneratorTest(final Path fileName, final String content) {
        this.fileName = fileName;
        this.content = content;
    }

    @Test
    public void shouldHandleSchema() {
        final Generator generator = new Generator(content, GeneratorTest.RNG);
        final Object generated = generator.generate();
        System.out.println((((fileName) + ": ") + generated));
    }
}

