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
package org.apache.flink.formats.avro.typeutils;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericRecord;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.TypeSerializerSnapshot;
import org.apache.flink.formats.avro.generated.User;
import org.apache.flink.formats.avro.utils.TestDataGenerator;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


/**
 * Test {@link AvroSerializerSnapshot}.
 */
public class AvroSerializerSnapshotTest {
    private static final Schema FIRST_NAME = SchemaBuilder.record("name").namespace("org.apache.flink").fields().requiredString("first").endRecord();

    private static final Schema FIRST_REQUIRED_LAST_OPTIONAL = SchemaBuilder.record("name").namespace("org.apache.flink").fields().requiredString("first").optionalString("last").endRecord();

    private static final Schema BOTH_REQUIRED = SchemaBuilder.record("name").namespace("org.apache.flink").fields().requiredString("first").requiredString("last").endRecord();

    @Test
    public void sameSchemaShouldBeCompatibleAsIs() {
        MatcherAssert.assertThat(AvroSerializerSnapshot.resolveSchemaCompatibility(AvroSerializerSnapshotTest.FIRST_NAME, AvroSerializerSnapshotTest.FIRST_NAME), isCompatibleAsIs());
    }

    @Test
    public void removingAnOptionalFieldsIsCompatibleAsIs() {
        MatcherAssert.assertThat(AvroSerializerSnapshot.resolveSchemaCompatibility(AvroSerializerSnapshotTest.FIRST_REQUIRED_LAST_OPTIONAL, AvroSerializerSnapshotTest.FIRST_NAME), isCompatibleAfterMigration());
    }

    @Test
    public void addingAnOptionalFieldsIsCompatibleAsIs() {
        MatcherAssert.assertThat(AvroSerializerSnapshot.resolveSchemaCompatibility(AvroSerializerSnapshotTest.FIRST_NAME, AvroSerializerSnapshotTest.FIRST_REQUIRED_LAST_OPTIONAL), isCompatibleAfterMigration());
    }

    @Test
    public void addingARequiredMakesSerializersIncompatible() {
        MatcherAssert.assertThat(AvroSerializerSnapshot.resolveSchemaCompatibility(AvroSerializerSnapshotTest.FIRST_REQUIRED_LAST_OPTIONAL, AvroSerializerSnapshotTest.BOTH_REQUIRED), isIncompatible());
    }

    @Test
    public void anAvroSnapshotIsCompatibleWithItsOriginatingSerializer() {
        AvroSerializer<GenericRecord> serializer = new AvroSerializer(GenericRecord.class, AvroSerializerSnapshotTest.FIRST_REQUIRED_LAST_OPTIONAL);
        TypeSerializerSnapshot<GenericRecord> snapshot = serializer.snapshotConfiguration();
        MatcherAssert.assertThat(snapshot.resolveSchemaCompatibility(serializer), isCompatibleAsIs());
    }

    @Test
    public void anAvroSnapshotIsCompatibleAfterARoundTrip() throws IOException {
        AvroSerializer<GenericRecord> serializer = new AvroSerializer(GenericRecord.class, AvroSerializerSnapshotTest.FIRST_REQUIRED_LAST_OPTIONAL);
        AvroSerializerSnapshot<GenericRecord> restored = AvroSerializerSnapshotTest.roundTrip(serializer.snapshotConfiguration());
        MatcherAssert.assertThat(restored.resolveSchemaCompatibility(serializer), isCompatibleAsIs());
    }

    @Test
    public void anAvroSpecificRecordIsCompatibleAfterARoundTrip() throws IOException {
        // user is an avro generated test object.
        AvroSerializer<User> serializer = new AvroSerializer(User.class);
        AvroSerializerSnapshot<User> restored = AvroSerializerSnapshotTest.roundTrip(serializer.snapshotConfiguration());
        MatcherAssert.assertThat(restored.resolveSchemaCompatibility(serializer), isCompatibleAsIs());
    }

    @Test
    public void aPojoIsCompatibleAfterARoundTrip() throws IOException {
        AvroSerializer<AvroSerializerSnapshotTest.Pojo> serializer = new AvroSerializer(AvroSerializerSnapshotTest.Pojo.class);
        AvroSerializerSnapshot<AvroSerializerSnapshotTest.Pojo> restored = AvroSerializerSnapshotTest.roundTrip(serializer.snapshotConfiguration());
        MatcherAssert.assertThat(restored.resolveSchemaCompatibility(serializer), isCompatibleAsIs());
    }

    @Test
    public void recordSerializedShouldBeDeserializeWithTheResortedSerializer() throws IOException {
        // user is an avro generated test object.
        final User user = TestDataGenerator.generateRandomUser(new Random());
        final AvroSerializer<User> originalSerializer = new AvroSerializer(User.class);
        // 
        // first serialize the record
        // 
        ByteBuffer serializedUser = AvroSerializerSnapshotTest.serialize(originalSerializer, user);
        // 
        // then restore a serializer from the snapshot
        // 
        TypeSerializer<User> restoredSerializer = originalSerializer.snapshotConfiguration().restoreSerializer();
        // 
        // now deserialize the user with the resorted serializer.
        // 
        User restoredUser = AvroSerializerSnapshotTest.deserialize(restoredSerializer, serializedUser);
        MatcherAssert.assertThat(user, CoreMatchers.is(restoredUser));
    }

    @Test
    public void validSchemaEvaluationShouldResultInCRequiresMigration() {
        final AvroSerializer<GenericRecord> originalSerializer = new AvroSerializer(GenericRecord.class, AvroSerializerSnapshotTest.FIRST_NAME);
        final AvroSerializer<GenericRecord> newSerializer = new AvroSerializer(GenericRecord.class, AvroSerializerSnapshotTest.FIRST_REQUIRED_LAST_OPTIONAL);
        TypeSerializerSnapshot<GenericRecord> originalSnapshot = originalSerializer.snapshotConfiguration();
        MatcherAssert.assertThat(originalSnapshot.resolveSchemaCompatibility(newSerializer), isCompatibleAfterMigration());
    }

    @Test
    public void nonValidSchemaEvaluationShouldResultInCompatibleSerializers() {
        final AvroSerializer<GenericRecord> originalSerializer = new AvroSerializer(GenericRecord.class, AvroSerializerSnapshotTest.FIRST_REQUIRED_LAST_OPTIONAL);
        final AvroSerializer<GenericRecord> newSerializer = new AvroSerializer(GenericRecord.class, AvroSerializerSnapshotTest.BOTH_REQUIRED);
        TypeSerializerSnapshot<GenericRecord> originalSnapshot = originalSerializer.snapshotConfiguration();
        MatcherAssert.assertThat(originalSnapshot.resolveSchemaCompatibility(newSerializer), isIncompatible());
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void changingFromGenericToSpecificWithCompatibleSchemaShouldResultInCompatibleSerializers() {
        // starting with a generic serializer
        AvroSerializer<Object> generic = new AvroSerializer(GenericRecord.class, User.SCHEMA$);
        TypeSerializerSnapshot<Object> genericSnapshot = generic.snapshotConfiguration();
        // then upgrading to a specific serializer
        AvroSerializer<Object> specificSerializer = new AvroSerializer(User.class);
        specificSerializer.snapshotConfiguration();
        MatcherAssert.assertThat(genericSnapshot.resolveSchemaCompatibility(specificSerializer), isCompatibleAsIs());
    }

    // ---------------------------------------------------------------------------------------------------------------
    // Test classes
    // ---------------------------------------------------------------------------------------------------------------
    private static class Pojo {
        private String foo;

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }
    }
}

