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
package org.apache.beam.sdk.transforms.display;


import DisplayData.Identifier;
import DisplayData.Path;
import DisplayData.Type;
import DisplayData.Type.BOOLEAN;
import DisplayData.Type.DURATION;
import DisplayData.Type.FLOAT;
import DisplayData.Type.INTEGER;
import DisplayData.Type.JAVA_CLASS;
import DisplayData.Type.STRING;
import DisplayData.Type.TIMESTAMP;
import Duration.ZERO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auto.value.AutoValue;
import com.google.common.testing.EqualsTester;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.beam.sdk.options.ValueProvider.StaticValueProvider;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.display.DisplayData.Builder;
import org.apache.beam.sdk.transforms.display.DisplayData.Item;
import org.apache.beam.sdk.util.SerializableUtils;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMultimap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Multimap;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link DisplayData} class.
 */
@RunWith(JUnit4.class)
public class DisplayDataTest implements Serializable {
    @Rule
    public transient ExpectedException thrown = ExpectedException.none();

    private static final DateTimeFormatter ISO_FORMATTER = ISODateTimeFormat.dateTime();

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testTypicalUsage() {
        final HasDisplayData subComponent1 = new HasDisplayData() {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("ExpectedAnswer", 42));
            }
        };
        final HasDisplayData subComponent2 = new HasDisplayData() {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("Location", "Seattle")).add(DisplayData.item("Forecast", "Rain"));
            }
        };
        PTransform<?, ?> transform = new PTransform<PCollection<String>, PCollection<String>>() {
            final Instant defaultStartTime = new Instant(0);

            Instant startTime = defaultStartTime;

            @Override
            public PCollection<String> expand(PCollection<String> begin) {
                throw new IllegalArgumentException("Should never be applied");
            }

            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.include("p1", subComponent1).include("p2", subComponent2).add(DisplayData.item("minSproggles", 200).withLabel("Minimum Required Sproggles")).add(DisplayData.item("fireLasers", true)).addIfNotDefault(DisplayData.item("startTime", startTime), defaultStartTime).add(DisplayData.item("timeBomb", Instant.now().plus(Duration.standardDays(1)))).add(DisplayData.item("filterLogic", subComponent1.getClass())).add(DisplayData.item("serviceUrl", "google.com/fizzbang").withLinkUrl("http://www.google.com/fizzbang"));
            }
        };
        DisplayData data = DisplayData.from(transform);
        Assert.assertThat(data.items(), Matchers.not(Matchers.empty()));
        Assert.assertThat(data.items(), Matchers.everyItem(Matchers.allOf(DisplayDataMatchers.hasKey(Matchers.not(Matchers.isEmptyOrNullString())), DisplayDataMatchers.hasNamespace(Matchers.<Class<?>>isOneOf(transform.getClass(), subComponent1.getClass(), subComponent2.getClass())), DisplayDataMatchers.hasType(Matchers.notNullValue(Type.class)), DisplayDataMatchers.hasValue(Matchers.not(Matchers.isEmptyOrNullString())))));
    }

    @Test
    public void testDefaultInstance() {
        DisplayData none = DisplayData.none();
        Assert.assertThat(none.items(), Matchers.empty());
    }

    @Test
    public void testCanBuildDisplayData() {
        DisplayData data = DisplayData.from(new HasDisplayData() {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("foo", "bar"));
            }
        });
        Assert.assertThat(data.items(), Matchers.hasSize(1));
        Assert.assertThat(data, DisplayDataMatchers.hasDisplayItem("foo", "bar"));
    }

    @Test
    public void testStaticValueProviderDate() {
        final Instant value = Instant.now();
        DisplayData data = DisplayData.from(new HasDisplayData() {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("foo", StaticValueProvider.of(value)));
            }
        });
        @SuppressWarnings("unchecked")
        DisplayData.Item item = ((DisplayData.Item) (data.items().toArray()[0]));
        @SuppressWarnings("unchecked")
        Matcher<Item> matchesAllOf = Matchers.allOf(DisplayDataMatchers.hasKey("foo"), DisplayDataMatchers.hasType(TIMESTAMP), DisplayDataMatchers.hasValue(DisplayDataTest.ISO_FORMATTER.print(value)));
        Assert.assertThat(item, matchesAllOf);
    }

    @Test
    public void testStaticValueProviderString() {
        DisplayData data = DisplayData.from(new HasDisplayData() {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("foo", StaticValueProvider.of("bar")));
            }
        });
        Assert.assertThat(data.items(), Matchers.hasSize(1));
        Assert.assertThat(data, DisplayDataMatchers.hasDisplayItem("foo", "bar"));
    }

    @Test
    public void testStaticValueProviderInt() {
        DisplayData data = DisplayData.from(new HasDisplayData() {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("foo", StaticValueProvider.of(1)));
            }
        });
        Assert.assertThat(data.items(), Matchers.hasSize(1));
        Assert.assertThat(data, DisplayDataMatchers.hasDisplayItem("foo", 1));
    }

    @Test
    public void testInaccessibleValueProvider() {
        DisplayData data = DisplayData.from(new HasDisplayData() {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("foo", new org.apache.beam.sdk.options.ValueProvider<String>() {
                    @Override
                    public boolean isAccessible() {
                        return false;
                    }

                    @Override
                    public String get() {
                        return "bar";
                    }

                    @Override
                    public String toString() {
                        return "toString";
                    }
                }));
            }
        });
        Assert.assertThat(data.items(), Matchers.hasSize(1));
        Assert.assertThat(data, DisplayDataMatchers.hasDisplayItem("foo", "toString"));
    }

    @Test
    public void testAsMap() {
        DisplayData data = DisplayData.from(new HasDisplayData() {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("foo", "bar"));
            }
        });
        Map<DisplayData.Identifier, DisplayData.Item> map = data.asMap();
        Assert.assertEquals(1, map.size());
        Assert.assertThat(data, DisplayDataMatchers.hasDisplayItem("foo", "bar"));
        Assert.assertEquals(map.values(), data.items());
    }

    @Test
    public void testItemProperties() {
        final Instant value = Instant.now();
        DisplayData data = DisplayData.from(new HasDisplayData() {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("now", value).withLabel("the current instant").withLinkUrl("http://time.gov").withNamespace(DisplayDataTest.class));
            }
        });
        @SuppressWarnings("unchecked")
        DisplayData.Item item = ((DisplayData.Item) (data.items().toArray()[0]));
        @SuppressWarnings("unchecked")
        Matcher<Item> matchesAllOf = Matchers.allOf(DisplayDataMatchers.hasNamespace(DisplayDataTest.class), DisplayDataMatchers.hasKey("now"), DisplayDataMatchers.hasType(TIMESTAMP), DisplayDataMatchers.hasValue(DisplayDataTest.ISO_FORMATTER.print(value)), DisplayDataTest.hasShortValue(Matchers.nullValue(String.class)), DisplayDataMatchers.hasLabel("the current instant"), DisplayDataTest.hasUrl(Matchers.is("http://time.gov")));
        Assert.assertThat(item, matchesAllOf);
    }

    @Test
    public void testUnspecifiedOptionalProperties() {
        DisplayData data = DisplayData.from(new HasDisplayData() {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("foo", "bar"));
            }
        });
        Assert.assertThat(data, DisplayDataMatchers.hasDisplayItem(Matchers.allOf(DisplayDataMatchers.hasLabel(Matchers.nullValue(String.class)), DisplayDataTest.hasUrl(Matchers.nullValue(String.class)))));
    }

    @Test
    public void testAddIfNotDefault() {
        DisplayData data = DisplayData.from(new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                builder.addIfNotDefault(DisplayData.item("defaultString", "foo"), "foo").addIfNotDefault(DisplayData.item("notDefaultString", "foo"), "notFoo").addIfNotDefault(DisplayData.item("defaultInteger", 1), 1).addIfNotDefault(DisplayData.item("notDefaultInteger", 1), 2).addIfNotDefault(DisplayData.item("defaultDouble", 123.4), 123.4).addIfNotDefault(DisplayData.item("notDefaultDouble", 123.4), 234.5).addIfNotDefault(DisplayData.item("defaultBoolean", true), true).addIfNotDefault(DisplayData.item("notDefaultBoolean", true), false).addIfNotDefault(DisplayData.item("defaultInstant", new Instant(0)), new Instant(0)).addIfNotDefault(DisplayData.item("notDefaultInstant", new Instant(0)), Instant.now()).addIfNotDefault(DisplayData.item("defaultDuration", ZERO), ZERO).addIfNotDefault(DisplayData.item("notDefaultDuration", Duration.millis(1234)), ZERO).addIfNotDefault(DisplayData.item("defaultClass", DisplayDataTest.class), DisplayDataTest.class).addIfNotDefault(DisplayData.item("notDefaultClass", DisplayDataTest.class), null);
            }
        });
        Assert.assertThat(data.items(), Matchers.hasSize(7));
        Assert.assertThat(data.items(), Matchers.everyItem(DisplayDataMatchers.hasKey(Matchers.startsWith("notDefault"))));
    }

    @Test
    @SuppressWarnings("UnnecessaryBoxing")
    public void testInterpolatedTypeDefaults() {
        DisplayData data = DisplayData.from(new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                builder.addIfNotDefault(DisplayData.item("integer", 123), 123).addIfNotDefault(DisplayData.item("Integer", Integer.valueOf(123)), Integer.valueOf(123)).addIfNotDefault(DisplayData.item("long", 123L), 123L).addIfNotDefault(DisplayData.item("Long", Long.valueOf(123)), Long.valueOf(123)).addIfNotDefault(DisplayData.item("float", 1.23F), 1.23F).addIfNotDefault(DisplayData.item("Float", Float.valueOf(1.23F)), Float.valueOf(1.23F)).addIfNotDefault(DisplayData.item("double", 1.23), 1.23).addIfNotDefault(DisplayData.item("Double", Double.valueOf(1.23)), Double.valueOf(1.23)).addIfNotDefault(DisplayData.item("boolean", true), true).addIfNotDefault(DisplayData.item("Boolean", Boolean.TRUE), Boolean.TRUE);
            }
        });
        Assert.assertThat(data.items(), Matchers.empty());
    }

    @Test
    public void testAddIfNotNull() {
        DisplayData data = DisplayData.from(new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                builder.addIfNotNull(DisplayData.item("nullString", ((String) (null)))).addIfNotNull(DisplayData.item("nullVPString", ((org.apache.beam.sdk.options.ValueProvider<String>) (null)))).addIfNotNull(DisplayData.item("nullierVPString", StaticValueProvider.of(null))).addIfNotNull(DisplayData.item("notNullString", "foo")).addIfNotNull(DisplayData.item("nullLong", ((Long) (null)))).addIfNotNull(DisplayData.item("notNullLong", 1234L)).addIfNotNull(DisplayData.item("nullDouble", ((Double) (null)))).addIfNotNull(DisplayData.item("notNullDouble", 123.4)).addIfNotNull(DisplayData.item("nullBoolean", ((Boolean) (null)))).addIfNotNull(DisplayData.item("notNullBoolean", true)).addIfNotNull(DisplayData.item("nullInstant", ((Instant) (null)))).addIfNotNull(DisplayData.item("notNullInstant", Instant.now())).addIfNotNull(DisplayData.item("nullDuration", ((Duration) (null)))).addIfNotNull(DisplayData.item("notNullDuration", ZERO)).addIfNotNull(DisplayData.item("nullClass", ((Class<?>) (null)))).addIfNotNull(DisplayData.item("notNullClass", DisplayDataTest.class));
            }
        });
        Assert.assertThat(data.items(), Matchers.hasSize(7));
        Assert.assertThat(data.items(), Matchers.everyItem(DisplayDataMatchers.hasKey(Matchers.startsWith("notNull"))));
    }

    @Test
    public void testModifyingConditionalItemIsSafe() {
        HasDisplayData component = new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                builder.addIfNotNull(DisplayData.item("nullItem", ((Class<?>) (null))).withLinkUrl("http://abc").withNamespace(DisplayDataTest.class).withLabel("Null item should be safe"));
            }
        };
        DisplayData.from(component);// should not throw

    }

    @Test
    public void testRootPath() {
        DisplayData.Path root = Path.root();
        Assert.assertThat(root.getComponents(), Matchers.empty());
    }

    @Test
    public void testExtendPath() {
        DisplayData.Path a = Path.root().extend("a");
        Assert.assertThat(a.getComponents(), Matchers.hasItems("a"));
        DisplayData.Path b = a.extend("b");
        Assert.assertThat(b.getComponents(), Matchers.hasItems("a", "b"));
    }

    @Test
    public void testExtendNullPathValidation() {
        DisplayData.Path root = Path.root();
        thrown.expect(NullPointerException.class);
        root.extend(null);
    }

    @Test
    public void testExtendEmptyPathValidation() {
        DisplayData.Path root = Path.root();
        thrown.expect(IllegalArgumentException.class);
        root.extend("");
    }

    @Test
    public void testAbsolute() {
        DisplayData.Path path = Path.absolute("a", "b", "c");
        Assert.assertThat(path.getComponents(), Matchers.hasItems("a", "b", "c"));
    }

    @Test
    public void testAbsoluteValidationNullFirstPath() {
        thrown.expect(NullPointerException.class);
        Path.absolute(null, "foo", "bar");
    }

    @Test
    public void testAbsoluteValidationEmptyFirstPath() {
        thrown.expect(IllegalArgumentException.class);
        Path.absolute("", "foo", "bar");
    }

    @Test
    public void testAbsoluteValidationNullSubsequentPath() {
        thrown.expect(NullPointerException.class);
        Path.absolute("a", "b", null, "c");
    }

    @Test
    public void testAbsoluteValidationEmptySubsequentPath() {
        thrown.expect(IllegalArgumentException.class);
        Path.absolute("a", "b", "", "c");
    }

    @Test
    public void testPathToString() {
        Assert.assertEquals("root string", "[]", Path.root().toString());
        Assert.assertEquals("single component", "[a]", Path.absolute("a").toString());
        Assert.assertEquals("hierarchy", "[a/b/c]", Path.absolute("a", "b", "c").toString());
    }

    @Test
    public void testPathEquality() {
        new EqualsTester().addEqualityGroup(Path.root(), Path.root()).addEqualityGroup(Path.root().extend("a"), Path.absolute("a")).addEqualityGroup(Path.root().extend("a").extend("b"), Path.absolute("a", "b")).testEquals();
    }

    @Test
    public void testIncludes() {
        final HasDisplayData subComponent = new HasDisplayData() {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("foo", "bar"));
            }
        };
        DisplayData data = DisplayData.from(new HasDisplayData() {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.include("p", subComponent);
            }
        });
        Assert.assertThat(data, DisplayDataMatchers.includesDisplayDataFor("p", subComponent));
    }

    @Test
    public void testIncludeSameComponentAtDifferentPaths() {
        final HasDisplayData subComponent1 = new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                builder.add(DisplayData.item("foo", "bar"));
            }
        };
        final HasDisplayData subComponent2 = new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                builder.add(DisplayData.item("foo2", "bar2"));
            }
        };
        HasDisplayData component = new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                builder.include("p1", subComponent1).include("p2", subComponent2);
            }
        };
        DisplayData data = DisplayData.from(component);
        Assert.assertThat(data, DisplayDataMatchers.includesDisplayDataFor("p1", subComponent1));
        Assert.assertThat(data, DisplayDataMatchers.includesDisplayDataFor("p2", subComponent2));
    }

    @Test
    public void testIncludesComponentsAtSamePath() {
        HasDisplayData component = new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                builder.include("p", new DisplayDataTest.NoopDisplayData()).include("p", new DisplayDataTest.NoopDisplayData());
            }
        };
        thrown.expectCause(Matchers.isA(IllegalArgumentException.class));
        DisplayData.from(component);
    }

    @Test
    public void testNullNamespaceOverride() {
        thrown.expectCause(Matchers.isA(NullPointerException.class));
        DisplayData.from(new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                builder.add(DisplayData.item("foo", "bar").withNamespace(null));
            }
        });
    }

    @Test
    public void testIdentifierEquality() {
        new EqualsTester().addEqualityGroup(Identifier.of(Path.absolute("a"), DisplayDataTest.class, "1"), Identifier.of(Path.absolute("a"), DisplayDataTest.class, "1")).addEqualityGroup(Identifier.of(Path.absolute("b"), DisplayDataTest.class, "1")).addEqualityGroup(Identifier.of(Path.absolute("a"), Object.class, "1")).addEqualityGroup(Identifier.of(Path.absolute("a"), DisplayDataTest.class, "2")).testEquals();
    }

    @Test
    public void testItemEquality() {
        new EqualsTester().addEqualityGroup(DisplayData.item("foo", "bar"), DisplayData.item("foo", "bar")).addEqualityGroup(DisplayData.item("foo", "barz")).testEquals();
    }

    @Test
    public void testDisplayDataEquality() {
        HasDisplayData component1 = new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                builder.add(DisplayData.item("foo", "bar"));
            }
        };
        HasDisplayData component2 = new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                builder.add(DisplayData.item("foo", "bar"));
            }
        };
        DisplayData component1DisplayData1 = DisplayData.from(component1);
        DisplayData component1DisplayData2 = DisplayData.from(component1);
        DisplayData component2DisplayData = DisplayData.from(component2);
        new EqualsTester().addEqualityGroup(component1DisplayData1, component1DisplayData2).addEqualityGroup(component2DisplayData).testEquals();
    }

    @Test
    public void testAcceptsKeysWithDifferentNamespaces() {
        DisplayData data = DisplayData.from(new HasDisplayData() {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("foo", "bar")).include("p", new HasDisplayData() {
                    @Override
                    public void populateDisplayData(DisplayData.Builder builder) {
                        builder.add(DisplayData.item("foo", "bar"));
                    }
                });
            }
        });
        Assert.assertThat(data.items(), Matchers.hasSize(2));
    }

    @Test
    public void testDuplicateKeyThrowsException() {
        thrown.expectCause(Matchers.isA(IllegalArgumentException.class));
        DisplayData.from(new HasDisplayData() {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("foo", "bar")).add(DisplayData.item("foo", "baz"));
            }
        });
    }

    @Test
    public void testDuplicateKeyWithNamespaceOverrideDoesntThrow() {
        DisplayData displayData = DisplayData.from(new HasDisplayData() {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("foo", "bar")).add(DisplayData.item("foo", "baz").withNamespace(DisplayDataTest.class));
            }
        });
        Assert.assertThat(displayData.items(), Matchers.hasSize(2));
    }

    @Test
    public void testToString() {
        HasDisplayData component = new HasDisplayData() {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("foo", "bar"));
            }
        };
        DisplayData data = DisplayData.from(component);
        Assert.assertEquals(String.format("[]%s:foo=bar", component.getClass().getName()), data.toString());
    }

    @Test
    public void testHandlesIncludeCycles() {
        final DisplayDataTest.IncludeSubComponent componentA = new DisplayDataTest.IncludeSubComponent() {
            @Override
            String getId() {
                return "componentA";
            }
        };
        final DisplayDataTest.IncludeSubComponent componentB = new DisplayDataTest.IncludeSubComponent() {
            @Override
            String getId() {
                return "componentB";
            }
        };
        HasDisplayData component = new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                builder.include("p", componentA);
            }
        };
        componentA.subComponent = componentB;
        componentB.subComponent = componentA;
        DisplayData data = DisplayData.from(component);
        Assert.assertThat(data.items(), Matchers.hasSize(2));
    }

    @Test
    public void testHandlesIncludeCyclesDifferentInstances() {
        HasDisplayData component = new DisplayDataTest.DelegatingDisplayData(new DisplayDataTest.DelegatingDisplayData(new DisplayDataTest.NoopDisplayData()));
        DisplayData data = DisplayData.from(component);
        Assert.assertThat(data.items(), Matchers.hasSize(2));
    }

    private static class DelegatingDisplayData implements HasDisplayData {
        private final HasDisplayData subComponent;

        public DelegatingDisplayData(HasDisplayData subComponent) {
            this.subComponent = subComponent;
        }

        @Override
        public void populateDisplayData(Builder builder) {
            builder.add(DisplayData.item("subComponent", subComponent.getClass())).include("p", subComponent);
        }
    }

    @Test
    public void testIncludesSubcomponentsWithObjectEquality() {
        DisplayData data = DisplayData.from(new HasDisplayData() {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.include("p1", new DisplayDataTest.EqualsEverything("foo1", "bar1")).include("p2", new DisplayDataTest.EqualsEverything("foo2", "bar2"));
            }
        });
        Assert.assertThat(data.items(), Matchers.hasSize(2));
    }

    private static class EqualsEverything implements HasDisplayData {
        private final String value;

        private final String key;

        EqualsEverything(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public void populateDisplayData(DisplayData.Builder builder) {
            builder.add(DisplayData.item(key, value));
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        public boolean equals(Object obj) {
            return true;
        }
    }

    @Test
    public void testDelegate() {
        final HasDisplayData subcomponent = new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                builder.add(DisplayData.item("subCompKey", "foo"));
            }
        };
        final HasDisplayData wrapped = new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                builder.add(DisplayData.item("wrappedKey", "bar")).include("p", subcomponent);
            }
        };
        HasDisplayData wrapper = new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                builder.delegate(wrapped);
            }
        };
        DisplayData data = DisplayData.from(wrapper);
        Assert.assertThat(data, DisplayDataMatchers.hasDisplayItem(Matchers.allOf(DisplayDataMatchers.hasKey("wrappedKey"), DisplayDataMatchers.hasNamespace(wrapped.getClass()), /* root */
        DisplayDataMatchers.hasPath())));
        Assert.assertThat(data, DisplayDataMatchers.hasDisplayItem(Matchers.allOf(DisplayDataMatchers.hasKey("subCompKey"), DisplayDataMatchers.hasNamespace(subcomponent.getClass()), DisplayDataMatchers.hasPath("p"))));
    }

    abstract static class IncludeSubComponent implements HasDisplayData {
        HasDisplayData subComponent;

        @Override
        public void populateDisplayData(DisplayData.Builder builder) {
            builder.add(DisplayData.item("id", getId())).include(getId(), subComponent);
        }

        abstract String getId();
    }

    @Test
    public void testTypeMappings() {
        DisplayData data = DisplayData.from(new HasDisplayData() {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("string", "foobar")).add(DisplayData.item("integer", 123)).add(DisplayData.item("float", 2.34)).add(DisplayData.item("boolean", true)).add(DisplayData.item("java_class", DisplayDataTest.class)).add(DisplayData.item("timestamp", Instant.now())).add(DisplayData.item("duration", Duration.standardHours(1)));
            }
        });
        Collection<Item> items = data.items();
        Assert.assertThat(items, Matchers.hasItem(Matchers.allOf(DisplayDataMatchers.hasKey("string"), DisplayDataMatchers.hasType(STRING))));
        Assert.assertThat(items, Matchers.hasItem(Matchers.allOf(DisplayDataMatchers.hasKey("integer"), DisplayDataMatchers.hasType(INTEGER))));
        Assert.assertThat(items, Matchers.hasItem(Matchers.allOf(DisplayDataMatchers.hasKey("float"), DisplayDataMatchers.hasType(FLOAT))));
        Assert.assertThat(items, Matchers.hasItem(Matchers.allOf(DisplayDataMatchers.hasKey("boolean"), DisplayDataMatchers.hasType(BOOLEAN))));
        Assert.assertThat(items, Matchers.hasItem(Matchers.allOf(DisplayDataMatchers.hasKey("java_class"), DisplayDataMatchers.hasType(JAVA_CLASS))));
        Assert.assertThat(items, Matchers.hasItem(Matchers.allOf(DisplayDataMatchers.hasKey("timestamp"), DisplayDataMatchers.hasType(TIMESTAMP))));
        Assert.assertThat(items, Matchers.hasItem(Matchers.allOf(DisplayDataMatchers.hasKey("duration"), DisplayDataMatchers.hasType(DURATION))));
    }

    @Test
    public void testExplicitItemType() {
        DisplayData data = DisplayData.from(new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                builder.add(DisplayData.item("integer", INTEGER, 1234L)).add(DisplayData.item("string", STRING, "foobar"));
            }
        });
        Assert.assertThat(data, DisplayDataMatchers.hasDisplayItem("integer", 1234L));
        Assert.assertThat(data, DisplayDataMatchers.hasDisplayItem("string", "foobar"));
    }

    @Test
    public void testFormatIncompatibleTypes() {
        Map<DisplayData.Type, Object> invalidPairs = ImmutableMap.<DisplayData.Type, Object>builder().put(STRING, 1234).put(INTEGER, "string value").put(FLOAT, "string value").put(BOOLEAN, "string value").put(TIMESTAMP, "string value").put(DURATION, "string value").put(JAVA_CLASS, "string value").build();
        for (Map.Entry<DisplayData.Type, Object> pair : invalidPairs.entrySet()) {
            try {
                DisplayData.Type type = pair.getKey();
                Object invalidValue = pair.getValue();
                type.format(invalidValue);
                Assert.fail(String.format("Expected exception not thrown for invalid %s value: %s", type, invalidValue));
            } catch (ClassCastException e) {
                // Expected
            }
        }
    }

    @Test
    public void testFormatCompatibleTypes() {
        Multimap<DisplayData.Type, Object> validPairs = ImmutableMultimap.<DisplayData.Type, Object>builder().put(INTEGER, 1234).put(INTEGER, 1234L).put(FLOAT, 123.4F).put(FLOAT, 123.4).put(FLOAT, 1234).put(FLOAT, 1234L).build();
        for (Map.Entry<DisplayData.Type, Object> pair : validPairs.entries()) {
            DisplayData.Type type = pair.getKey();
            Object value = pair.getValue();
            try {
                type.format(value);
            } catch (ClassCastException e) {
                throw new AssertionError(String.format("Failed to format %s for DisplayData.%s", value.getClass().getSimpleName(), type), e);
            }
        }
    }

    @Test
    public void testInvalidExplicitItemType() {
        HasDisplayData component = new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                builder.add(DisplayData.item("integer", INTEGER, "foobar"));
            }
        };
        thrown.expectCause(Matchers.isA(ClassCastException.class));
        DisplayData.from(component);
    }

    @Test
    public void testKnownTypeInference() {
        Assert.assertEquals(INTEGER, DisplayData.inferType(1234));
        Assert.assertEquals(INTEGER, DisplayData.inferType(1234L));
        Assert.assertEquals(FLOAT, DisplayData.inferType(12.3));
        Assert.assertEquals(FLOAT, DisplayData.inferType(12.3F));
        Assert.assertEquals(BOOLEAN, DisplayData.inferType(true));
        Assert.assertEquals(TIMESTAMP, DisplayData.inferType(Instant.now()));
        Assert.assertEquals(DURATION, DisplayData.inferType(Duration.millis(1234)));
        Assert.assertEquals(JAVA_CLASS, DisplayData.inferType(DisplayDataTest.class));
        Assert.assertEquals(STRING, DisplayData.inferType("hello world"));
        Assert.assertEquals(null, DisplayData.inferType(null));
        Assert.assertEquals(null, DisplayData.inferType(new Object() {}));
    }

    @Test
    public void testStringFormatting() throws IOException {
        final Instant now = Instant.now();
        final Duration oneHour = Duration.standardHours(1);
        HasDisplayData component = new HasDisplayData() {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("string", "foobar")).add(DisplayData.item("integer", 123)).add(DisplayData.item("float", 2.34)).add(DisplayData.item("boolean", true)).add(DisplayData.item("java_class", DisplayDataTest.class)).add(DisplayData.item("timestamp", now)).add(DisplayData.item("duration", oneHour));
            }
        };
        DisplayData data = DisplayData.from(component);
        Assert.assertThat(data, DisplayDataMatchers.hasDisplayItem("string", "foobar"));
        Assert.assertThat(data, DisplayDataMatchers.hasDisplayItem("integer", 123));
        Assert.assertThat(data, DisplayDataMatchers.hasDisplayItem("float", 2.34));
        Assert.assertThat(data, DisplayDataMatchers.hasDisplayItem("boolean", true));
        Assert.assertThat(data, DisplayDataMatchers.hasDisplayItem("java_class", DisplayDataTest.class));
        Assert.assertThat(data, DisplayDataMatchers.hasDisplayItem("timestamp", now));
        Assert.assertThat(data, DisplayDataMatchers.hasDisplayItem("duration", oneHour));
    }

    @Test
    public void testContextProperlyReset() {
        final HasDisplayData subComponent = new HasDisplayData() {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("foo", "bar"));
            }
        };
        HasDisplayData component = new HasDisplayData() {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.include("p", subComponent).add(DisplayData.item("alpha", "bravo"));
            }
        };
        DisplayData data = DisplayData.from(component);
        Assert.assertThat(data.items(), Matchers.hasItem(Matchers.allOf(DisplayDataMatchers.hasKey("alpha"), DisplayDataMatchers.hasNamespace(component.getClass()))));
    }

    @Test
    public void testFromNull() {
        thrown.expect(NullPointerException.class);
        DisplayData.from(null);
    }

    @Test
    public void testIncludeNull() {
        thrown.expectCause(Matchers.isA(NullPointerException.class));
        DisplayData.from(new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                builder.include("p", null);
            }
        });
    }

    @Test
    public void testIncludeNullPath() {
        thrown.expectCause(Matchers.isA(NullPointerException.class));
        DisplayData.from(new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                builder.include(null, new DisplayDataTest.NoopDisplayData());
            }
        });
    }

    @Test
    public void testIncludeEmptyPath() {
        thrown.expectCause(Matchers.isA(IllegalArgumentException.class));
        DisplayData.from(new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                builder.include("", new DisplayDataTest.NoopDisplayData());
            }
        });
    }

    @Test
    public void testNullKey() {
        thrown.expectCause(Matchers.isA(NullPointerException.class));
        DisplayData.from(new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                builder.add(DisplayData.item(null, "foo"));
            }
        });
    }

    @Test
    public void testRejectsNullValues() {
        DisplayData.from(new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                try {
                    builder.add(DisplayData.item("key", ((String) (null))));
                    throw new RuntimeException("Should throw on null string value");
                } catch (NullPointerException ex) {
                    // Expected
                }
                try {
                    builder.add(DisplayData.item("key", ((Class<?>) (null))));
                    throw new RuntimeException("Should throw on null class value");
                } catch (NullPointerException ex) {
                    // Expected
                }
                try {
                    builder.add(DisplayData.item("key", ((Duration) (null))));
                    throw new RuntimeException("Should throw on null duration value");
                } catch (NullPointerException ex) {
                    // Expected
                }
                try {
                    builder.add(DisplayData.item("key", ((Instant) (null))));
                    throw new RuntimeException("Should throw on null instant value");
                } catch (NullPointerException ex) {
                    // Expected
                }
            }
        });
    }

    @Test
    public void testAcceptsNullOptionalValues() {
        DisplayData.from(new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                builder.add(DisplayData.item("key", "value").withLabel(null).withLinkUrl(null));
            }
        });
        // Should not throw
    }

    @Test
    public void testJsonSerialization() throws IOException {
        final String stringValue = "foobar";
        final int intValue = 1234;
        final double floatValue = 123.4;
        final boolean boolValue = true;
        final int durationMillis = 1234;
        HasDisplayData component = new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                builder.add(DisplayData.item("string", stringValue)).add(DisplayData.item("long", intValue)).add(DisplayData.item("double", floatValue)).add(DisplayData.item("boolean", boolValue)).add(DisplayData.item("instant", new Instant(0))).add(DisplayData.item("duration", Duration.millis(durationMillis))).add(DisplayData.item("class", DisplayDataTest.class).withLinkUrl("http://abc").withLabel("baz"));
            }
        };
        DisplayData data = DisplayData.from(component);
        JsonNode json = DisplayDataTest.MAPPER.readTree(DisplayDataTest.MAPPER.writeValueAsBytes(data));
        Assert.assertThat(json, hasExpectedJson(component, "STRING", "string", quoted(stringValue)));
        Assert.assertThat(json, hasExpectedJson(component, "INTEGER", "long", intValue));
        Assert.assertThat(json, hasExpectedJson(component, "FLOAT", "double", floatValue));
        Assert.assertThat(json, hasExpectedJson(component, "BOOLEAN", "boolean", boolValue));
        Assert.assertThat(json, hasExpectedJson(component, "DURATION", "duration", durationMillis));
        Assert.assertThat(json, hasExpectedJson(component, "TIMESTAMP", "instant", quoted("1970-01-01T00:00:00.000Z")));
        Assert.assertThat(json, hasExpectedJson(component, "JAVA_CLASS", "class", quoted(DisplayDataTest.class.getName()), quoted("DisplayDataTest"), "baz", "http://abc"));
    }

    @Test
    public void testJsonSerializationAnonymousClassNamespace() throws IOException {
        HasDisplayData component = new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                builder.add(DisplayData.item("foo", "bar"));
            }
        };
        DisplayData data = DisplayData.from(component);
        JsonNode json = DisplayDataTest.MAPPER.readTree(DisplayDataTest.MAPPER.writeValueAsBytes(data));
        String namespace = json.elements().next().get("namespace").asText();
        final Pattern anonClassRegex = Pattern.compile(((Pattern.quote(DisplayDataTest.class.getName())) + "\\$\\d+$"));
        Assert.assertThat(namespace, new CustomTypeSafeMatcher<String>(("anonymous class regex: " + anonClassRegex)) {
            @Override
            protected boolean matchesSafely(String item) {
                java.util.regex.Matcher m = anonClassRegex.matcher(item);
                return m.matches();
            }
        });
    }

    @Test
    public void testCanSerializeItemSpecReference() {
        DisplayData.ItemSpec<?> spec = DisplayData.item("clazz", DisplayDataTest.class);
        SerializableUtils.ensureSerializable(new DisplayDataTest.HoldsItemSpecReference(spec));
    }

    private static class HoldsItemSpecReference implements Serializable {
        private final DisplayData.ItemSpec<?> spec;

        public HoldsItemSpecReference(DisplayData.ItemSpec<?> spec) {
            this.spec = spec;
        }
    }

    @Test
    public void testSerializable() {
        DisplayData data = DisplayData.from(new HasDisplayData() {
            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("foo", "bar"));
            }
        });
        DisplayData serData = SerializableUtils.clone(data);
        Assert.assertEquals(data, serData);
    }

    /**
     * Verify that {@link DisplayData.Builder} can recover from exceptions thrown in user code. This
     * is not used within the Beam SDK since we want all code to produce valid DisplayData. This test
     * just ensures it is possible to write custom code that does recover.
     */
    @Test
    public void testCanRecoverFromBuildException() {
        final HasDisplayData safeComponent = new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                builder.add(DisplayData.item("a", "a"));
            }
        };
        final HasDisplayData failingComponent = new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                throw new RuntimeException("oh noes!");
            }
        };
        DisplayData displayData = DisplayData.from(new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                builder.add(DisplayData.item("b", "b")).add(DisplayData.item("c", "c"));
                try {
                    builder.include("p", failingComponent);
                    Assert.fail("Expected exception not thrown");
                } catch (RuntimeException e) {
                    // Expected
                }
                builder.include("p", safeComponent).add(DisplayData.item("d", "d"));
            }
        });
        Assert.assertThat(displayData, DisplayDataMatchers.hasDisplayItem("a"));
        Assert.assertThat(displayData, DisplayDataMatchers.hasDisplayItem("b"));
        Assert.assertThat(displayData, DisplayDataMatchers.hasDisplayItem("c"));
        Assert.assertThat(displayData, DisplayDataMatchers.hasDisplayItem("d"));
    }

    @Test
    public void testExceptionMessage() {
        final RuntimeException cause = new RuntimeException("oh noes!");
        HasDisplayData component = new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                throw cause;
            }
        };
        thrown.expectMessage(component.getClass().getName());
        thrown.expectCause(Matchers.is(cause));
        DisplayData.from(component);
    }

    @Test
    public void testExceptionsNotWrappedRecursively() {
        final RuntimeException cause = new RuntimeException("oh noes!");
        HasDisplayData component = new HasDisplayData() {
            @Override
            public void populateDisplayData(Builder builder) {
                builder.include("p", new HasDisplayData() {
                    @Override
                    public void populateDisplayData(Builder builder) {
                        throw cause;
                    }
                });
            }
        };
        thrown.expectCause(Matchers.is(cause));
        DisplayData.from(component);
    }

    @AutoValue
    abstract static class Foo implements HasDisplayData {
        @Override
        public void populateDisplayData(Builder builder) {
            builder.add(DisplayData.item("someKey", "someValue"));
        }
    }

    @Test
    public void testAutoValue() {
        DisplayData data = DisplayData.from(new AutoValue_DisplayDataTest_Foo());
        Item item = Iterables.getOnlyElement(data.asMap().values());
        Assert.assertEquals(DisplayDataTest.Foo.class, item.getNamespace());
    }

    private static class NoopDisplayData implements HasDisplayData {
        @Override
        public void populateDisplayData(Builder builder) {
        }
    }
}

