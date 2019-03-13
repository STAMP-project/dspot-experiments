/**
 * Copyright (c) 2016 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.common.truth.extensions.proto;


import com.google.auto.value.AutoValue;
import com.google.common.base.Optional;
import com.google.common.truth.Expect;
import com.google.common.truth.ExpectFailure;
import com.google.protobuf.MessageLite;
import java.util.Arrays;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Unit tests for {@link LiteProtoSubject}.
 */
@RunWith(Parameterized.class)
public class LiteProtoSubjectTest {
    /**
     * We run (almost) all the tests for both proto2 and proto3 implementations. This class organizes
     * the parameters much more cleanly than a raw Object[].
     */
    @AutoValue
    public abstract static class Config {
        abstract MessageLite nonEmptyMessage();

        abstract MessageLite equivalentNonEmptyMessage();

        abstract MessageLite nonEmptyMessageOfOtherValue();

        abstract MessageLite nonEmptyMessageOfOtherType();

        abstract MessageLite defaultInstance();

        abstract MessageLite defaultInstanceOfOtherType();

        abstract Optional<MessageLite> messageWithoutRequiredFields();

        public static LiteProtoSubjectTest.Config.Builder newBuilder() {
            return new AutoValue_LiteProtoSubjectTest_Config.Builder();
        }

        @AutoValue.Builder
        public abstract static class Builder {
            abstract LiteProtoSubjectTest.Config.Builder setNonEmptyMessage(MessageLite messageLite);

            abstract LiteProtoSubjectTest.Config.Builder setEquivalentNonEmptyMessage(MessageLite messageLite);

            abstract LiteProtoSubjectTest.Config.Builder setNonEmptyMessageOfOtherValue(MessageLite messageLite);

            abstract LiteProtoSubjectTest.Config.Builder setNonEmptyMessageOfOtherType(MessageLite messageLite);

            abstract LiteProtoSubjectTest.Config.Builder setDefaultInstance(MessageLite messageLite);

            abstract LiteProtoSubjectTest.Config.Builder setDefaultInstanceOfOtherType(MessageLite messageLite);

            abstract LiteProtoSubjectTest.Config.Builder setMessageWithoutRequiredFields(MessageLite messageLite);

            abstract LiteProtoSubjectTest.Config build();
        }
    }

    @Rule
    public final Expect expect = Expect.create();

    private final LiteProtoSubjectTest.Config config;

    public LiteProtoSubjectTest(@SuppressWarnings("unused")
    String name, LiteProtoSubjectTest.Config config) {
        this.config = config;
    }

    @Test
    public void testSubjectMethods() {
        expectThat(config.nonEmptyMessage()).isSameAs(config.nonEmptyMessage());
        expectThat(config.nonEmptyMessage().toBuilder()).isNotSameAs(config.nonEmptyMessage());
        expectThat(config.nonEmptyMessage()).isInstanceOf(MessageLite.class);
        expectThat(config.nonEmptyMessage().toBuilder()).isInstanceOf(MessageLite.Builder.class);
        expectThat(config.nonEmptyMessage()).isNotInstanceOf(MessageLite.Builder.class);
        expectThat(config.nonEmptyMessage().toBuilder()).isNotInstanceOf(MessageLite.class);
        expectThat(config.nonEmptyMessage()).isIn(Arrays.asList(config.nonEmptyMessage()));
        expectThat(config.nonEmptyMessage()).isNotIn(Arrays.asList(config.nonEmptyMessageOfOtherValue()));
        expectThat(config.nonEmptyMessage()).isAnyOf(config.nonEmptyMessage(), config.nonEmptyMessageOfOtherValue());
        expectThat(config.nonEmptyMessage()).isNoneOf(config.nonEmptyMessageOfOtherValue(), config.nonEmptyMessageOfOtherType());
    }

    @Test
    public void testIsEqualTo_success() {
        expectThat(null).isEqualTo(null);
        expectThat(null).isNull();
        expectThat(config.nonEmptyMessage()).isEqualTo(config.nonEmptyMessage());
        expectThat(config.nonEmptyMessage()).isEqualTo(config.equivalentNonEmptyMessage());
        expectThat(config.nonEmptyMessage()).isNotEqualTo(config.nonEmptyMessage().toBuilder());
        ExpectFailure.assertThat(config.defaultInstance()).isNotEqualTo(config.defaultInstanceOfOtherType());
        ExpectFailure.assertThat(config.nonEmptyMessage()).isNotEqualTo(config.nonEmptyMessageOfOtherType());
        ExpectFailure.assertThat(config.nonEmptyMessage()).isNotEqualTo(config.nonEmptyMessageOfOtherValue());
    }

    @Test
    public void testIsEqualTo_failure() {
        try {
            ExpectFailure.assertThat(config.nonEmptyMessage()).isEqualTo(config.nonEmptyMessageOfOtherValue());
            Assert.fail("Should have failed.");
        } catch (AssertionError e) {
            expectRegex(e, ".*expected:.*\"foo\".*");
            expectNoRegex(e, ".*but was:.*\"foo\".*");
        }
        try {
            ExpectFailure.assertThat(config.nonEmptyMessage()).isEqualTo(config.nonEmptyMessageOfOtherType());
            Assert.fail("Should have failed.");
        } catch (AssertionError e) {
            expectRegex(e, ("Not true that \\(.*\\) proto is equal to the expected \\(.*\\) object\\.\\s*" + "They are not of the same class\\."));
        }
        try {
            ExpectFailure.assertThat(config.nonEmptyMessage()).isNotEqualTo(config.equivalentNonEmptyMessage());
            Assert.fail("Should have failed.");
        } catch (AssertionError e) {
            expectRegex(e, String.format("Not true that protos are different\\.\\s*Both are \\(%s\\) <.*optional_int: 3.*>\\.", Pattern.quote(config.nonEmptyMessage().getClass().getName())));
        }
    }

    @Test
    public void testHasAllRequiredFields_success() {
        expectThat(config.nonEmptyMessage()).hasAllRequiredFields();
    }

    @Test
    public void testHasAllRequiredFields_failures() {
        if (!(config.messageWithoutRequiredFields().isPresent())) {
            return;
        }
        try {
            ExpectFailure.assertThat(config.messageWithoutRequiredFields().get()).hasAllRequiredFields();
            Assert.fail("Should have failed.");
        } catch (AssertionError e) {
            expectRegex(e, ("Not true that <.*> has all required fields set\\.\\s*" + "\\(Lite runtime could not determine which fields were missing\\.\\)"));
        }
    }

    @Test
    public void testDefaultInstance_success() {
        expectThat(config.defaultInstance()).isEqualToDefaultInstance();
        expectThat(config.defaultInstanceOfOtherType()).isEqualToDefaultInstance();
        expectThat(config.nonEmptyMessage().getDefaultInstanceForType()).isEqualToDefaultInstance();
        expectThat(null).isNotEqualToDefaultInstance();
        expectThat(config.nonEmptyMessage()).isNotEqualToDefaultInstance();
    }

    @Test
    public void testDefaultInstance_failure() {
        try {
            ExpectFailure.assertThat(config.nonEmptyMessage()).isEqualToDefaultInstance();
            Assert.fail("Should have failed.");
        } catch (AssertionError e) {
            expectRegex(e, ("Not true that <.*optional_int:\\s*3.*> is a default proto instance\\.\\s*" + "It has set values\\."));
        }
        try {
            ExpectFailure.assertThat(config.defaultInstance()).isNotEqualToDefaultInstance();
            Assert.fail("Should have failed.");
        } catch (AssertionError e) {
            expectRegex(e, String.format(("Not true that \\(%s\\) <.*\\[empty proto\\].*> is not a default " + "proto instance\\.\\s*It has no set values\\."), Pattern.quote(config.defaultInstance().getClass().getName())));
        }
    }

    @Test
    public void testSerializedSize_success() {
        int size = config.nonEmptyMessage().getSerializedSize();
        expectThat(config.nonEmptyMessage()).serializedSize().isEqualTo(size);
        expectThat(config.defaultInstance()).serializedSize().isEqualTo(0);
    }

    @Test
    public void testSerializedSize_failure() {
        int size = config.nonEmptyMessage().getSerializedSize();
        try {
            ExpectFailure.assertThat(config.nonEmptyMessage()).serializedSize().isGreaterThan(size);
            Assert.fail("Should have failed.");
        } catch (AssertionError e) {
            ExpectFailure.assertThat(e).factValue("value of").isEqualTo("messageLite.getSerializedSize()");
            ExpectFailure.assertThat(e).factValue("messageLite was").containsMatch("optional_int:\\s*3");
        }
        try {
            ExpectFailure.assertThat(config.defaultInstance()).serializedSize().isGreaterThan(0);
            Assert.fail("Should have failed.");
        } catch (AssertionError e) {
            ExpectFailure.assertThat(e).factValue("value of").isEqualTo("messageLite.getSerializedSize()");
            ExpectFailure.assertThat(e).factValue("messageLite was").contains("[empty proto]");
        }
    }
}

