/**
 * Copyright 2017-2019 Crown Copyright
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
package uk.gov.gchq.gaffer;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 * Provides a common interface for testing classes that should be JSON serialisable.
 *
 * @param <T>
 * 		Object of type T that is to be tested
 */
public abstract class JSONSerialisationTest<T> {
    @Test
    public void shouldJsonSerialiseAndDeserialise() {
        // Given
        final T obj = getTestObject();
        // When
        final byte[] json = toJson(obj);
        final T deserialisedObj = fromJson(json);
        // Then
        Assert.assertNotNull(deserialisedObj);
    }

    @Test
    public void shouldHaveJsonPropertyAnnotation() throws Exception {
        // Given
        final T op = getTestObject();
        // When
        final JsonPropertyOrder annotation = op.getClass().getAnnotation(JsonPropertyOrder.class);
        // Then
        Assume.assumeTrue(("Missing JsonPropertyOrder annotation on class. It should de defined and set to alphabetical." + (op.getClass().getName())), ((null != annotation) && (annotation.alphabetic())));
    }
}

