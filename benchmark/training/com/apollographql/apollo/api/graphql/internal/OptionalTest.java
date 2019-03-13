/**
 * Copyright (C) 2011 The Guava Authors
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
package com.apollographql.apollo.api.graphql.internal;


import com.apollographql.apollo.api.internal.Functions;
import com.apollographql.apollo.api.internal.Optional;
import com.google.common.collect.FluentIterable;
import java.util.Collections;
import java.util.Set;
import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;


public final class OptionalTest {
    @Test
    public void testAbsent() {
        Optional<String> optionalName = Optional.absent();
        TestCase.assertFalse(optionalName.isPresent());
    }

    @Test
    public void testOf() {
        Assert.assertEquals("training", Optional.of("training").get());
    }

    @Test
    public void testOfNull() {
        try {
            Optional.of(null);
            Assert.fail();
        } catch (NullPointerException ignore) {
        }
    }

    @Test
    public void testFromNullable() {
        Optional<String> optionalName = Optional.fromNullable("bob");
        Assert.assertEquals("bob", optionalName.get());
    }

    @Test
    public void testFromNullableNull() {
        // not promised by spec, but easier to test
        TestCase.assertSame(Optional.absent(), Optional.fromNullable(null));
    }

    @Test
    public void testIsPresentNo() {
        TestCase.assertFalse(Optional.absent().isPresent());
    }

    @Test
    public void testIsPresentYes() {
        TestCase.assertTrue(Optional.of("training").isPresent());
    }

    @Test
    public void testGetAbsent() {
        Optional<String> optional = Optional.absent();
        try {
            optional.get();
            Assert.fail();
        } catch (IllegalStateException ignore) {
        }
    }

    @Test
    public void testGetPresent() {
        Assert.assertEquals("training", Optional.of("training").get());
    }

    @Test
    public void testOrTPresent() {
        Assert.assertEquals("a", Optional.of("a").or("default"));
    }

    @Test
    public void testOrTAbsent() {
        Assert.assertEquals("default", Optional.absent().or("default"));
    }

    @Test
    public void testOrOptionalPresent() {
        Assert.assertEquals(Optional.of("a"), Optional.of("a").or(Optional.of("fallback")));
    }

    @Test
    public void testOrOptionalAbsent() {
        Assert.assertEquals(Optional.of("fallback"), Optional.absent().or(Optional.of("fallback")));
    }

    @Test
    public void testOrNullPresent() {
        Assert.assertEquals("a", Optional.of("a").orNull());
    }

    @Test
    public void testOrNullAbsent() {
        Assert.assertNull(Optional.absent().orNull());
    }

    @Test
    public void testAsSetPresent() {
        Set<String> expected = Collections.singleton("a");
        Assert.assertEquals(expected, Optional.of("a").asSet());
    }

    @Test
    public void testAsSetAbsent() {
        TestCase.assertTrue("Returned set should be empty", Optional.absent().asSet().isEmpty());
    }

    @Test
    public void testAsSetPresentIsImmutable() {
        Set<String> presentAsSet = Optional.of("a").asSet();
        try {
            presentAsSet.add("b");
            Assert.fail();
        } catch (UnsupportedOperationException ignore) {
        }
    }

    @Test
    public void testAsSetAbsentIsImmutable() {
        Set<Object> absentAsSet = Optional.absent().asSet();
        try {
            absentAsSet.add("foo");
            Assert.fail();
        } catch (UnsupportedOperationException ignore) {
        }
    }

    @Test
    public void testTransformAbsent() {
        Assert.assertEquals(Optional.absent(), Optional.absent().transform(Functions.identity()));
        Assert.assertEquals(Optional.absent(), Optional.absent().transform(Functions.toStringFunction()));
    }

    @Test
    public void testTransformPresentIdentity() {
        Assert.assertEquals(Optional.of("a"), Optional.of("a").transform(Functions.identity()));
    }

    @Test
    public void testTransformPresentToString() {
        Assert.assertEquals(Optional.of("42"), Optional.of(42).transform(Functions.toStringFunction()));
    }

    @Test
    public void testTransformPresentFunctionReturnsNull() {
        try {
            Optional<String> unused = Optional.of("a").transform(new com.apollographql.apollo.api.internal.Function<String, String>() {
                @Override
                public String apply(String input) {
                    return null;
                }
            });
            Assert.fail("Should throw if Function returns null.");
        } catch (NullPointerException ignore) {
        }
    }

    @Test
    public void testTransformAbssentFunctionReturnsNull() {
        Assert.assertEquals(Optional.absent(), Optional.absent().transform(new com.apollographql.apollo.api.internal.Function<Object, Object>() {
            @Override
            public Object apply(Object input) {
                return null;
            }
        }));
    }

    @Test
    public void testEqualsAndHashCodeAbsent() {
        Assert.assertEquals(Optional.<String>absent(), Optional.<Integer>absent());
        Assert.assertEquals(Optional.absent().hashCode(), Optional.absent().hashCode());
        assertThat(Optional.absent().hashCode()).isNotEqualTo(Optional.of(0).hashCode());
    }

    @Test
    public void testEqualsAndHashCodePresent() {
        Assert.assertEquals(Optional.of("training"), Optional.of("training"));
        TestCase.assertFalse(Optional.of("a").equals(Optional.of("b")));
        TestCase.assertFalse(Optional.of("a").equals(Optional.absent()));
        Assert.assertEquals(Optional.of("training").hashCode(), Optional.of("training").hashCode());
    }

    @Test
    public void testToStringAbsent() {
        Assert.assertEquals("Optional.absent()", Optional.absent().toString());
    }

    @Test
    public void testToStringPresent() {
        Assert.assertEquals("Optional.of(training)", Optional.of("training").toString());
    }

    /* The following tests demonstrate the shortcomings of or() and test that the casting workaround
    mentioned in the method Javadoc does in fact compile.
     */
    // compilation test
    @SuppressWarnings("unused")
    @Test
    public void testSampleCodeError1() {
        Optional<Integer> optionalInt = OptionalTest.getSomeOptionalInt();
        // Number value = optionalInt.or(0.5); // error
    }

    // compilation test
    @SuppressWarnings("unused")
    @Test
    public void testSampleCodeFine1() {
        Optional<Number> optionalInt = Optional.of(((Number) (1)));
        Number value = optionalInt.or(0.5);// fine

    }

    // compilation test
    @SuppressWarnings("unused")
    @Test
    public void testSampleCodeFine2() {
        FluentIterable<? extends Number> numbers = OptionalTest.getSomeNumbers();
        // Sadly, the following is what users will have to do in some circumstances.
    }

    @Test
    public void testMapAbsent() {
        Assert.assertEquals(Optional.absent(), Optional.absent().map(Functions.identity()));
        Assert.assertEquals(Optional.absent(), Optional.absent().map(Functions.toStringFunction()));
    }

    @Test
    public void testMapPresentIdentity() {
        Assert.assertEquals(Optional.of("a"), Optional.of("a").map(Functions.identity()));
    }

    @Test
    public void testMapPresentToString() {
        Assert.assertEquals(Optional.of("42"), Optional.of(42).map(Functions.toStringFunction()));
    }

    @Test
    public void testMapPresentFunctionReturnsNull() {
        try {
            Optional<String> unused = Optional.of("a").map(new com.apollographql.apollo.api.internal.Function<String, String>() {
                @Override
                public String apply(String input) {
                    return null;
                }
            });
            Assert.fail("Should throw if Function returns null.");
        } catch (NullPointerException ignore) {
        }
    }

    @Test
    public void testMapAbssentFunctionReturnsNull() {
        Assert.assertEquals(Optional.absent(), Optional.absent().map(new com.apollographql.apollo.api.internal.Function<Object, Object>() {
            @Override
            public Object apply(Object input) {
                return null;
            }
        }));
    }

    @Test
    public void testFlatMapAbsent() {
        Assert.assertEquals(Optional.absent(), Optional.absent().flatMap(new com.apollographql.apollo.api.internal.Function<Object, Optional<String>>() {
            @NotNull
            @Override
            public Optional<String> apply(@NotNull
            Object o) {
                return Optional.of(o.toString());
            }
        }));
    }

    @Test
    public void testFlatMapMapPresentIdentity() {
        Assert.assertEquals(Optional.of("a"), Optional.of("a").flatMap(new com.apollographql.apollo.api.internal.Function<String, Optional<String>>() {
            @NotNull
            @Override
            public Optional<String> apply(@NotNull
            String s) {
                return Optional.of(s);
            }
        }));
    }

    @Test
    public void testFlatMapPresentToString() {
        Assert.assertEquals(Optional.of("42"), Optional.of(42).flatMap(new com.apollographql.apollo.api.internal.Function<Integer, Optional<String>>() {
            @NotNull
            @Override
            public Optional<String> apply(@NotNull
            Integer integer) {
                return Optional.of(integer.toString());
            }
        }));
    }

    @Test
    public void testFlatMapPresentFunctionReturnsNull() {
        try {
            Optional<String> unused = Optional.of("a").flatMap(new com.apollographql.apollo.api.internal.Function<String, Optional<String>>() {
                @NotNull
                @Override
                public Optional<String> apply(@NotNull
                String s) {
                    return null;
                }
            });
            Assert.fail("Should throw if Function returns null.");
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void testFlatMapAbssentFunctionReturnsNull() {
        Assert.assertEquals(Optional.absent(), Optional.absent().flatMap(new com.apollographql.apollo.api.internal.Function<Object, Optional<Object>>() {
            @NotNull
            @Override
            public Optional<Object> apply(@NotNull
            Object o) {
                return null;
            }
        }));
    }
}

