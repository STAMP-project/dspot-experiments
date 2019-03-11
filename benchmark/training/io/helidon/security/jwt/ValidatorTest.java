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
package io.helidon.security.jwt;


import Errors.Collector;
import Errors.ErrorMessagesException;
import io.helidon.common.Errors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Unit test for {@link Validator}.
 */
public class ValidatorTest {
    @Test
    public void testValidatorExample() {
        // javadoc of Validator
        Validator<String> validator = ( str, collector) -> {
            if (null == str) {
                collector.fatal("String must not be null");
            }
        };
        Errors.Collector collector = Errors.collector();
        validator.validate("string", collector);
        try {
            collector.collect().checkValid();
        } catch (Errors e) {
            Assertions.fail(("Should not have failed: " + (e.getMessage())));
        }
    }
}

