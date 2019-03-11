/**
 * Copyright 2019 JanusGraph Authors
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package org.janusgraph.graphdb.configuration.validator;


import JanusGraphConstants.JANUSGRAPH_ID_STORE_NAME;
import JanusGraphConstants.TITAN_COMPATIBLE_VERSIONS;
import JanusGraphConstants.TITAN_ID_STORE_NAME;
import org.janusgraph.core.JanusGraphException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Validator tests for backward compatibility with Titan
 */
public class CompatibilityValidatorTest {
    @Test
    public void shouldThrowExceptionOnNullVersion() {
        IllegalArgumentException illegalArgumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            CompatibilityValidator.validateBackwardCompatibilityWithTitan(null, "");
        });
        Assertions.assertEquals("JanusGraph version nor Titan compatibility have not been initialized", illegalArgumentException.getMessage());
    }

    @Test
    public void shouldThrowExceptionOnTitanIncompatibleVersion() {
        Assertions.assertThrows(JanusGraphException.class, () -> {
            CompatibilityValidator.validateBackwardCompatibilityWithTitan("not_compatible", "");
        });
    }

    @Test
    public void shouldPassOnJanusGraphIdStoreName() {
        CompatibilityValidator.validateBackwardCompatibilityWithTitan(TITAN_COMPATIBLE_VERSIONS.get(0), JANUSGRAPH_ID_STORE_NAME);
    }

    @Test
    public void shouldPassOnTitanIdStoreName() {
        CompatibilityValidator.validateBackwardCompatibilityWithTitan(TITAN_COMPATIBLE_VERSIONS.get(0), TITAN_ID_STORE_NAME);
    }

    @Test
    public void shouldThrowExceptionOnIncompatibleIdStoreName() {
        expectIncompatibleIdStoreNameException(() -> {
            CompatibilityValidator.validateBackwardCompatibilityWithTitan(TITAN_COMPATIBLE_VERSIONS.get(0), "not_compatible");
        });
    }

    @Test
    public void shouldThrowExceptionOnNullIdStoreName() {
        expectIncompatibleIdStoreNameException(() -> {
            CompatibilityValidator.validateBackwardCompatibilityWithTitan(TITAN_COMPATIBLE_VERSIONS.get(0), null);
        });
    }
}

