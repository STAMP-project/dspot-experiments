/**
 * Copyright 2016-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.data.element;


import TestGroups.ENTITY;
import TestPropertyNames.STRING;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.gchq.gaffer.commonutil.TestGroups;


@RunWith(MockitoJUnitRunner.class)
public class EntityTest extends ElementTest {
    @Test
    public void shouldBuildEntity() {
        // Given
        final String vertex = "vertex1";
        final String propValue = "propValue";
        // When
        final Entity entity = new Entity.Builder().group(ENTITY).vertex(vertex).property(STRING, propValue).build();
        // Then
        Assert.assertEquals(ENTITY, entity.getGroup());
        Assert.assertEquals(vertex, entity.getVertex());
        Assert.assertEquals(propValue, entity.getProperty(STRING));
    }

    @Test
    public void shouldConstructEntity() {
        // Given
        final String vertex = "vertex1";
        final String propValue = "propValue";
        // When
        final Entity entity = new Entity(TestGroups.ENTITY, vertex);
        entity.putProperty(STRING, propValue);
        // Then
        Assert.assertEquals(ENTITY, entity.getGroup());
        Assert.assertEquals(vertex, entity.getVertex());
        Assert.assertEquals(propValue, entity.getProperty(STRING));
    }

    @Test
    public void shouldCloneEntity() {
        // Given
        final String vertex = "vertex1";
        final String propValue = "propValue";
        // When
        final Entity entity = new Entity(TestGroups.ENTITY, vertex);
        final Entity clone = entity.emptyClone();
        // Then
        Assert.assertEquals(clone, entity);
    }

    @Test
    public void shouldReturnFalseForEqualsWhenPropertyIsDifferent() {
        // Given
        final Entity entity1 = new Entity("group");
        entity1.setVertex("identifier");
        entity1.putProperty("some property", "some value");
        final Entity entity2 = cloneCoreFields(entity1);
        entity2.putProperty("some property", "some other value");
        // When
        boolean isEqual = entity1.equals(((Object) (entity2)));
        // Then
        Assert.assertFalse(isEqual);
        Assert.assertNotEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    public void shouldReturnFalseForEqualsWhenIdentifierIsDifferent() {
        // Given
        final Entity entity1 = new Entity("group");
        entity1.setVertex("vertex");
        final Entity entity2 = cloneCoreFields(entity1);
        entity2.setVertex("different vertex");
        // When
        boolean isEqual = entity1.equals(((Object) (entity2)));
        // Then
        Assert.assertFalse(isEqual);
        Assert.assertFalse(((entity1.hashCode()) == (entity2.hashCode())));
    }
}

