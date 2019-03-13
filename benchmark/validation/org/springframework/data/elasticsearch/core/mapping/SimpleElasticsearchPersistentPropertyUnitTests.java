/**
 * Copyright 2018-2019 the original author or authors.
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
package org.springframework.data.elasticsearch.core.mapping;


import org.junit.Test;
import org.springframework.data.elasticsearch.annotations.Score;
import org.springframework.data.mapping.MappingException;


/**
 * Unit tests for {@link SimpleElasticsearchPersistentProperty}.
 *
 * @author Oliver Gierke
 */
public class SimpleElasticsearchPersistentPropertyUnitTests {
    // DATAES-462
    @Test
    public void rejectsScorePropertyOfTypeOtherthanFloat() {
        SimpleElasticsearchMappingContext context = new SimpleElasticsearchMappingContext();
        // 
        // 
        assertThatExceptionOfType(MappingException.class).isThrownBy(() -> context.getRequiredPersistentEntity(.class)).withMessageContaining("scoreProperty");
    }

    static class InvalidScoreProperty {
        @Score
        String scoreProperty;
    }
}

