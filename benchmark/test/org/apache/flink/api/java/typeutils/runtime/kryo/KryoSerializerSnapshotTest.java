/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.api.java.typeutils.runtime.kryo;


import java.io.IOException;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.TypeSerializerMatchers;
import org.apache.flink.api.common.typeutils.TypeSerializerSnapshot;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


/**
 * Tests for {@link KryoSerializerSnapshot}.
 */
public class KryoSerializerSnapshotTest {
    private ExecutionConfig oldConfig;

    private ExecutionConfig newConfig;

    @Test
    public void sanityTest() {
        MatcherAssert.assertThat(KryoSerializerSnapshotTest.resolveKryoCompatibility(oldConfig, newConfig), TypeSerializerMatchers.isCompatibleAsIs());
    }

    @Test
    public void addingTypesIsCompatibleAfterReconfiguration() {
        oldConfig.registerKryoType(KryoPojosForMigrationTests.Animal.class);
        newConfig.registerKryoType(KryoPojosForMigrationTests.Animal.class);
        newConfig.registerTypeWithKryoSerializer(KryoPojosForMigrationTests.Dog.class, KryoPojosForMigrationTests.DogKryoSerializer.class);
        MatcherAssert.assertThat(KryoSerializerSnapshotTest.resolveKryoCompatibility(oldConfig, newConfig), TypeSerializerMatchers.isCompatibleWithReconfiguredSerializer());
    }

    @Test
    public void replacingKryoSerializersIsCompatibleAsIs() {
        oldConfig.registerKryoType(KryoPojosForMigrationTests.Animal.class);
        oldConfig.registerTypeWithKryoSerializer(KryoPojosForMigrationTests.Dog.class, KryoPojosForMigrationTests.DogKryoSerializer.class);
        newConfig.registerKryoType(KryoPojosForMigrationTests.Animal.class);
        newConfig.registerTypeWithKryoSerializer(KryoPojosForMigrationTests.Dog.class, KryoPojosForMigrationTests.DogV2KryoSerializer.class);
        // it is compatible as is, since Kryo does not expose compatibility API with KryoSerializers
        // so we can not know if DogKryoSerializer is compatible with DogV2KryoSerializer
        MatcherAssert.assertThat(KryoSerializerSnapshotTest.resolveKryoCompatibility(oldConfig, newConfig), TypeSerializerMatchers.isCompatibleAsIs());
    }

    @Test
    public void reorderingIsCompatibleAfterReconfiguration() {
        oldConfig.registerKryoType(KryoPojosForMigrationTests.Parrot.class);
        oldConfig.registerKryoType(KryoPojosForMigrationTests.Dog.class);
        newConfig.registerKryoType(KryoPojosForMigrationTests.Dog.class);
        newConfig.registerKryoType(KryoPojosForMigrationTests.Parrot.class);
        MatcherAssert.assertThat(KryoSerializerSnapshotTest.resolveKryoCompatibility(oldConfig, newConfig), TypeSerializerMatchers.isCompatibleWithReconfiguredSerializer());
    }

    @Test
    public void tryingToRestoreWithNonExistingClassShouldBeIncompatible() throws IOException {
        TypeSerializerSnapshot<KryoPojosForMigrationTests.Animal> restoredSnapshot = KryoSerializerSnapshotTest.kryoSnapshotWithMissingClass();
        TypeSerializer<KryoPojosForMigrationTests.Animal> currentSerializer = new KryoSerializer(KryoPojosForMigrationTests.Animal.class, new ExecutionConfig());
        MatcherAssert.assertThat(restoredSnapshot.resolveSchemaCompatibility(currentSerializer), TypeSerializerMatchers.isIncompatible());
    }
}

