/**
 * Copyright 2016 MovingBlocks
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
package org.terasology.persistence;


import EntityData.GlobalStore;
import org.junit.Assert;
import org.junit.Test;
import org.terasology.TerasologyTestingEnvironment;
import org.terasology.entitySystem.entity.EntityBuilder;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.internal.EngineEntityManager;
import org.terasology.persistence.serializers.PrefabSerializer;
import org.terasology.persistence.serializers.WorldSerializer;
import org.terasology.protobuf.EntityData;


public class WorldSerializerTest extends TerasologyTestingEnvironment {
    @Test
    public void testNotPersistedIfFlagedOtherwise() throws Exception {
        EngineEntityManager entityManager = context.get(EngineEntityManager.class);
        EntityBuilder entityBuilder = entityManager.newBuilder();
        PrefabSerializer prefabSerializer = new PrefabSerializer(entityManager.getComponentLibrary(), entityManager.getTypeSerializerLibrary());
        WorldSerializer worldSerializer = new org.terasology.persistence.serializers.WorldSerializerImpl(entityManager, prefabSerializer);
        entityBuilder.setPersistent(false);
        // just used to express that an entity got created
        @SuppressWarnings("unused")
        EntityRef entity = entityBuilder.build();
        EntityData.GlobalStore worldData = worldSerializer.serializeWorld(false);
        Assert.assertEquals(0, worldData.getEntityCount());
    }
}

