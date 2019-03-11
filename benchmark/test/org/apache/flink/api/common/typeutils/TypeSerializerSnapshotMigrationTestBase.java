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
package org.apache.flink.api.common.typeutils;


import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.function.Supplier;
import org.apache.flink.testutils.migration.MigrationVersion;
import org.apache.flink.util.Preconditions;
import org.apache.flink.util.TestLogger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


/**
 * A test base for verifying {@link TypeSerializerSnapshot} migration.
 *
 * @param <ElementT>
 * 		the element being serialized.
 */
public abstract class TypeSerializerSnapshotMigrationTestBase<ElementT> extends TestLogger {
    private final TypeSerializerSnapshotMigrationTestBase.TestSpecification<ElementT> testSpecification;

    protected TypeSerializerSnapshotMigrationTestBase(TypeSerializerSnapshotMigrationTestBase.TestSpecification<ElementT> testSpecification) {
        this.testSpecification = TypeSerializerSnapshotMigrationTestBase.checkNotNull(testSpecification);
    }

    @Test
    public void serializerSnapshotIsSuccessfullyRead() {
        TypeSerializerSnapshot<ElementT> snapshot = snapshotUnderTest();
        MatcherAssert.assertThat(snapshot, CoreMatchers.allOf(CoreMatchers.notNullValue(), CoreMatchers.instanceOf(TypeSerializerSnapshot.class)));
    }

    @Test
    public void specifiedNewSerializerHasExpectedCompatibilityResultsWithSnapshot() {
        TypeSerializerSnapshot<ElementT> snapshot = snapshotUnderTest();
        TypeSerializerSchemaCompatibility<ElementT> result = snapshot.resolveSchemaCompatibility(testSpecification.createSerializer());
        MatcherAssert.assertThat(result, testSpecification.schemaCompatibilityMatcher);
    }

    @Test
    public void restoredSerializerIsAbleToDeserializePreviousData() throws IOException {
        TypeSerializerSnapshot<ElementT> snapshot = snapshotUnderTest();
        TypeSerializer<ElementT> serializer = snapshot.restoreSerializer();
        assertSerializerIsAbleToReadOldData(serializer);
    }

    @Test
    public void reconfiguredSerializerIsAbleToDeserializePreviousData() throws IOException {
        TypeSerializerSnapshot<ElementT> snapshot = snapshotUnderTest();
        TypeSerializerSchemaCompatibility<ElementT> compatibility = snapshot.resolveSchemaCompatibility(testSpecification.createSerializer());
        if (!(compatibility.isCompatibleWithReconfiguredSerializer())) {
            // this test only applies for reconfigured serializers.
            return;
        }
        TypeSerializer<ElementT> serializer = compatibility.getReconfiguredSerializer();
        assertSerializerIsAbleToReadOldData(serializer);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void movingForward() throws IOException {
        TypeSerializerSnapshot<ElementT> previousSnapshot = snapshotUnderTest();
        TypeSerializer<ElementT> restoredSerializer = previousSnapshot.restoreSerializer();
        TypeSerializerSnapshot<ElementT> nextSnapshot = restoredSerializer.snapshotConfiguration();
        MatcherAssert.assertThat(nextSnapshot, CoreMatchers.instanceOf(testSpecification.snapshotClass));
        TypeSerializerSnapshot<ElementT> nextSnapshotDeserialized = writeAndThenReadTheSnapshot(restoredSerializer, nextSnapshot);
        MatcherAssert.assertThat(nextSnapshotDeserialized, CoreMatchers.allOf(CoreMatchers.notNullValue(), CoreMatchers.not(CoreMatchers.instanceOf(TypeSerializerConfigSnapshot.class))));
    }

    @Test
    public void restoreSerializerFromNewSerializerSnapshotIsAbleToDeserializePreviousData() throws IOException {
        TypeSerializer<ElementT> newSerializer = testSpecification.createSerializer();
        TypeSerializerSchemaCompatibility<ElementT> compatibility = snapshotUnderTest().resolveSchemaCompatibility(newSerializer);
        final TypeSerializer<ElementT> nextSerializer;
        if (compatibility.isCompatibleWithReconfiguredSerializer()) {
            nextSerializer = compatibility.getReconfiguredSerializer();
        } else
            if (compatibility.isCompatibleAsIs()) {
                nextSerializer = newSerializer;
            } else {
                // this test does not apply.
                return;
            }

        TypeSerializerSnapshot<ElementT> nextSnapshot = nextSerializer.snapshotConfiguration();
        assertSerializerIsAbleToReadOldData(nextSnapshot.restoreSerializer());
    }

    // --------------------------------------------------------------------------------------------------------------
    // Test Specification
    // --------------------------------------------------------------------------------------------------------------
    /**
     * Test Specification.
     */
    @SuppressWarnings("WeakerAccess")
    public static final class TestSpecification<T> {
        private final Class<? extends TypeSerializer<T>> serializerType;

        private final Class<? extends TypeSerializerSnapshot<T>> snapshotClass;

        private final String name;

        private final MigrationVersion testMigrationVersion;

        private Supplier<? extends TypeSerializer<T>> serializerProvider;

        private Matcher<TypeSerializerSchemaCompatibility<T>> schemaCompatibilityMatcher;

        private String snapshotDataLocation;

        private String testDataLocation;

        private int testDataCount;

        @SuppressWarnings("unchecked")
        private Matcher<T> testDataElementMatcher = ((Matcher<T>) (CoreMatchers.notNullValue()));

        @SuppressWarnings("unchecked")
        public static <T> TypeSerializerSnapshotMigrationTestBase.TestSpecification<T> builder(String name, Class<? extends TypeSerializer> serializerClass, Class<? extends TypeSerializerSnapshot> snapshotClass, MigrationVersion testMigrationVersion) {
            return new TypeSerializerSnapshotMigrationTestBase.TestSpecification(name, ((Class<? extends TypeSerializer<T>>) (serializerClass)), ((Class<? extends TypeSerializerSnapshot<T>>) (snapshotClass)), testMigrationVersion);
        }

        private TestSpecification(String name, Class<? extends TypeSerializer<T>> serializerType, Class<? extends TypeSerializerSnapshot<T>> snapshotClass, MigrationVersion testMigrationVersion) {
            this.name = name;
            this.serializerType = serializerType;
            this.snapshotClass = snapshotClass;
            this.testMigrationVersion = testMigrationVersion;
        }

        public TypeSerializerSnapshotMigrationTestBase.TestSpecification<T> withNewSerializerProvider(Supplier<? extends TypeSerializer<T>> serializerProvider) {
            return withNewSerializerProvider(serializerProvider, TypeSerializerSchemaCompatibility.compatibleAsIs());
        }

        public TypeSerializerSnapshotMigrationTestBase.TestSpecification<T> withNewSerializerProvider(Supplier<? extends TypeSerializer<T>> serializerProvider, TypeSerializerSchemaCompatibility<T> expectedCompatibilityResult) {
            this.serializerProvider = serializerProvider;
            this.schemaCompatibilityMatcher = TypeSerializerMatchers.hasSameCompatibilityAs(expectedCompatibilityResult);
            return this;
        }

        public TypeSerializerSnapshotMigrationTestBase.TestSpecification<T> withSchemaCompatibilityMatcher(Matcher<TypeSerializerSchemaCompatibility<T>> schemaCompatibilityMatcher) {
            this.schemaCompatibilityMatcher = schemaCompatibilityMatcher;
            return this;
        }

        public TypeSerializerSnapshotMigrationTestBase.TestSpecification<T> withSnapshotDataLocation(String snapshotDataLocation) {
            this.snapshotDataLocation = snapshotDataLocation;
            return this;
        }

        public TypeSerializerSnapshotMigrationTestBase.TestSpecification<T> withTestData(String testDataLocation, int testDataCount) {
            this.testDataLocation = testDataLocation;
            this.testDataCount = testDataCount;
            return this;
        }

        public TypeSerializerSnapshotMigrationTestBase.TestSpecification<T> withTestDataMatcher(Matcher<T> matcher) {
            testDataElementMatcher = matcher;
            return this;
        }

        public TypeSerializerSnapshotMigrationTestBase.TestSpecification<T> withTestDataCount(int expectedDataItmes) {
            this.testDataCount = expectedDataItmes;
            return this;
        }

        private TypeSerializer<T> createSerializer() {
            try {
                return (serializerProvider) == null ? serializerType.newInstance() : serializerProvider.get();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("serializer provider was not set, and creating the serializer reflectively failed.", e);
            }
        }

        private Path getTestDataLocation() {
            return TypeSerializerSnapshotMigrationTestBase.resourcePath(this.testDataLocation);
        }

        private Path getSnapshotDataLocation() {
            return TypeSerializerSnapshotMigrationTestBase.resourcePath(this.snapshotDataLocation);
        }

        private MigrationVersion getTestMigrationVersion() {
            return testMigrationVersion;
        }

        @Override
        public String toString() {
            return String.format("%s , %s, %s", name, serializerType.getSimpleName(), snapshotClass.getSimpleName());
        }
    }

    /**
     * Utility class to help build a collection of {@link TestSpecification} for
     * multiple test migration versions. For each test specification added,
     * an entry will be added for each specified migration version.
     */
    public static final class TestSpecifications {
        private static final int DEFAULT_TEST_DATA_COUNT = 10;

        private static final String DEFAULT_SNAPSHOT_FILENAME_FORMAT = "flink-%s-%s-snapshot";

        private static final String DEFAULT_TEST_DATA_FILENAME_FORMAT = "flink-%s-%s-data";

        private final Collection<TypeSerializerSnapshotMigrationTestBase.TestSpecification<?>> testSpecifications = new LinkedList<>();

        private final MigrationVersion[] testVersions;

        public TestSpecifications(MigrationVersion... testVersions) {
            Preconditions.checkArgument(((testVersions.length) > 0), "At least one test migration version should be specified.");
            this.testVersions = testVersions;
        }

        /**
         * Adds a test specification to be tested for all specified test versions.
         *
         * <p>This method adds the specification with pre-defined snapshot and data filenames,
         * with the format "flink-&lt;testVersion&gt;-&lt;specName&gt;-&lt;data/snapshot&gt;",
         * and each specification's test data count is assumed to always be 10.
         *
         * @param name
         * 		test specification name.
         * @param serializerClass
         * 		class of the current serializer.
         * @param snapshotClass
         * 		class of the current serializer snapshot class.
         * @param serializerProvider
         * 		provider for an instance of the current serializer.
         * @param <T>
         * 		type of the test data.
         */
        public <T> void add(String name, Class<? extends TypeSerializer> serializerClass, Class<? extends TypeSerializerSnapshot> snapshotClass, Supplier<? extends TypeSerializer<T>> serializerProvider) {
            for (MigrationVersion testVersion : testVersions) {
                testSpecifications.add(TypeSerializerSnapshotMigrationTestBase.TestSpecification.<T>builder(TypeSerializerSnapshotMigrationTestBase.TestSpecifications.getSpecNameForVersion(name, testVersion), serializerClass, snapshotClass, testVersion).withNewSerializerProvider(serializerProvider).withSnapshotDataLocation(String.format(TypeSerializerSnapshotMigrationTestBase.TestSpecifications.DEFAULT_SNAPSHOT_FILENAME_FORMAT, testVersion, name)).withTestData(String.format(TypeSerializerSnapshotMigrationTestBase.TestSpecifications.DEFAULT_TEST_DATA_FILENAME_FORMAT, testVersion, name), TypeSerializerSnapshotMigrationTestBase.TestSpecifications.DEFAULT_TEST_DATA_COUNT));
            }
        }

        /**
         * Adds a test specification to be tested for all specified test versions.
         *
         * <p>This method adds the specification with pre-defined snapshot and data filenames,
         * with the format "flink-&lt;testVersion&gt;-&lt;specName&gt;-&lt;data/snapshot&gt;",
         * and each specification's test data count is assumed to always be 10.
         *
         * @param <T>
         * 		type of the test data.
         */
        public <T> void addWithCompatibilityMatcher(String name, Class<? extends TypeSerializer> serializerClass, Class<? extends TypeSerializerSnapshot> snapshotClass, Supplier<? extends TypeSerializer<T>> serializerProvider, Matcher<TypeSerializerSchemaCompatibility<T>> schemaCompatibilityMatcher) {
            for (MigrationVersion testVersion : testVersions) {
                testSpecifications.add(TypeSerializerSnapshotMigrationTestBase.TestSpecification.<T>builder(TypeSerializerSnapshotMigrationTestBase.TestSpecifications.getSpecNameForVersion(name, testVersion), serializerClass, snapshotClass, testVersion).withNewSerializerProvider(serializerProvider).withSchemaCompatibilityMatcher(schemaCompatibilityMatcher).withSnapshotDataLocation(String.format(TypeSerializerSnapshotMigrationTestBase.TestSpecifications.DEFAULT_SNAPSHOT_FILENAME_FORMAT, testVersion, name)).withTestData(String.format(TypeSerializerSnapshotMigrationTestBase.TestSpecifications.DEFAULT_TEST_DATA_FILENAME_FORMAT, testVersion, name), TypeSerializerSnapshotMigrationTestBase.TestSpecifications.DEFAULT_TEST_DATA_COUNT));
            }
        }

        /**
         * Adds a test specification to be tested for all specified test versions.
         *
         * <p>This method adds the specification with pre-defined snapshot and data filenames,
         * with the format "flink-&lt;testVersion&gt;-&lt;specName&gt;-&lt;data/snapshot&gt;",
         * and each specification's test data count is assumed to always be 10.
         *
         * @param name
         * 		test specification name.
         * @param serializerClass
         * 		class of the current serializer.
         * @param snapshotClass
         * 		class of the current serializer snapshot class.
         * @param serializerProvider
         * 		provider for an instance of the current serializer.
         * @param elementMatcher
         * 		an {@code hamcrest} matcher to match test data.
         * @param <T>
         * 		type of the test data.
         */
        public <T> void add(String name, Class<? extends TypeSerializer> serializerClass, Class<? extends TypeSerializerSnapshot> snapshotClass, Supplier<? extends TypeSerializer<T>> serializerProvider, Matcher<T> elementMatcher) {
            for (MigrationVersion testVersion : testVersions) {
                testSpecifications.add(TypeSerializerSnapshotMigrationTestBase.TestSpecification.<T>builder(TypeSerializerSnapshotMigrationTestBase.TestSpecifications.getSpecNameForVersion(name, testVersion), serializerClass, snapshotClass, testVersion).withNewSerializerProvider(serializerProvider).withSnapshotDataLocation(String.format(TypeSerializerSnapshotMigrationTestBase.TestSpecifications.DEFAULT_SNAPSHOT_FILENAME_FORMAT, testVersion, name)).withTestData(String.format(TypeSerializerSnapshotMigrationTestBase.TestSpecifications.DEFAULT_TEST_DATA_FILENAME_FORMAT, testVersion, name), TypeSerializerSnapshotMigrationTestBase.TestSpecifications.DEFAULT_TEST_DATA_COUNT).withTestDataMatcher(elementMatcher));
            }
        }

        /**
         * Adds a test specification to be tested for all specified test versions.
         *
         * @param name
         * 		test specification name.
         * @param serializerClass
         * 		class of the current serializer.
         * @param snapshotClass
         * 		class of the current serializer snapshot class.
         * @param serializerProvider
         * 		provider for an instance of the current serializer.
         * @param testSnapshotFilenameProvider
         * 		provider for the filename of the test snapshot.
         * @param testDataFilenameProvider
         * 		provider for the filename of the test data.
         * @param testDataCount
         * 		expected number of records to be read in the test data files.
         * @param <T>
         * 		type of the test data.
         */
        public <T> void add(String name, Class<? extends TypeSerializer> serializerClass, Class<? extends TypeSerializerSnapshot> snapshotClass, Supplier<? extends TypeSerializer<T>> serializerProvider, TypeSerializerSnapshotMigrationTestBase.TestResourceFilenameSupplier testSnapshotFilenameProvider, TypeSerializerSnapshotMigrationTestBase.TestResourceFilenameSupplier testDataFilenameProvider, int testDataCount) {
            for (MigrationVersion testVersion : testVersions) {
                testSpecifications.add(TypeSerializerSnapshotMigrationTestBase.TestSpecification.<T>builder(TypeSerializerSnapshotMigrationTestBase.TestSpecifications.getSpecNameForVersion(name, testVersion), serializerClass, snapshotClass, testVersion).withNewSerializerProvider(serializerProvider).withSnapshotDataLocation(testSnapshotFilenameProvider.get(testVersion)).withTestData(testDataFilenameProvider.get(testVersion), testDataCount));
            }
        }

        public Collection<TypeSerializerSnapshotMigrationTestBase.TestSpecification<?>> get() {
            return Collections.unmodifiableCollection(testSpecifications);
        }

        private static String getSpecNameForVersion(String baseName, MigrationVersion testVersion) {
            return (testVersion + "-") + baseName;
        }
    }

    /**
     * Supplier of paths based on {@link MigrationVersion}.
     */
    protected interface TestResourceFilenameSupplier {
        String get(MigrationVersion testVersion);
    }
}

