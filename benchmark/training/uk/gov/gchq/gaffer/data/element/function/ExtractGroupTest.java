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
package uk.gov.gchq.gaffer.data.element.function;


import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.koryphe.function.FunctionTest;


public class ExtractGroupTest extends FunctionTest {
    @Test
    public void shouldReturnGroupFromEdge() {
        // Given
        final ExtractGroup function = new ExtractGroup();
        final String group = "testGroup";
        final Edge edge = new Edge.Builder().source("src").dest("dest").directed(true).group(group).build();
        // When
        final String result = function.apply(edge);
        // Then
        Assert.assertEquals(group, result);
    }

    @Test
    public void shouldReturnGroupFromEntity() {
        // Given
        final ExtractGroup function = new ExtractGroup();
        final String group = "testGroup_2";
        final Entity entity = new Entity.Builder().vertex("1").group(group).build();
        // When
        final String result = function.apply(entity);
        // Then
        Assert.assertEquals(group, result);
    }

    @Test
    public void shouldReturnNullForNullElement() {
        // Given
        final ExtractGroup function = new ExtractGroup();
        // When
        final String result = function.apply(null);
        // Then
        Assert.assertNull(result);
    }
}

