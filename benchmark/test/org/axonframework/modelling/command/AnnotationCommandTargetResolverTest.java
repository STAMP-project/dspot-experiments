/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.modelling.command;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Allard Buijze
 */
public class AnnotationCommandTargetResolverTest {
    private AnnotationCommandTargetResolver testSubject;

    @Test(expected = IllegalArgumentException.class)
    public void testResolveTarget_CommandWithoutAnnotations() {
        testSubject.resolveTarget(asCommandMessage("That won't work"));
    }

    @Test
    public void testResolveTarget_WithAnnotatedMethod() {
        final UUID aggregateIdentifier = UUID.randomUUID();
        VersionedAggregateIdentifier actual = testSubject.resolveTarget(asCommandMessage(new Object() {
            @TargetAggregateIdentifier
            private UUID getIdentifier() {
                return aggregateIdentifier;
            }
        }));
        Assert.assertEquals(aggregateIdentifier.toString(), actual.getIdentifier());
        Assert.assertNull(actual.getVersion());
    }

    @Test
    public void testResolveTarget_WithAnnotatedMethodAndVersion() {
        final UUID aggregateIdentifier = UUID.randomUUID();
        VersionedAggregateIdentifier actual = testSubject.resolveTarget(asCommandMessage(new Object() {
            @TargetAggregateIdentifier
            private UUID getIdentifier() {
                return aggregateIdentifier;
            }

            @TargetAggregateVersion
            private Long version() {
                return 1L;
            }
        }));
        Assert.assertEquals(aggregateIdentifier.toString(), actual.getIdentifier());
        Assert.assertEquals(((Long) (1L)), actual.getVersion());
    }

    @Test
    public void testResolveTarget_WithAnnotatedMethodAndStringVersion() {
        final UUID aggregateIdentifier = UUID.randomUUID();
        VersionedAggregateIdentifier actual = testSubject.resolveTarget(asCommandMessage(new Object() {
            @TargetAggregateIdentifier
            private UUID getIdentifier() {
                return aggregateIdentifier;
            }

            @TargetAggregateVersion
            private String version() {
                return "1000230";
            }
        }));
        Assert.assertEquals(aggregateIdentifier.toString(), actual.getIdentifier());
        Assert.assertEquals(((Long) (1000230L)), actual.getVersion());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResolveTarget_WithAnnotatedMethodAndVoidIdentifier() {
        testSubject.resolveTarget(asCommandMessage(new Object() {
            @TargetAggregateIdentifier
            private void getIdentifier() {
            }
        }));
    }

    @Test
    public void testResolveTarget_WithAnnotatedFields() {
        final UUID aggregateIdentifier = UUID.randomUUID();
        final Object version = 1L;
        VersionedAggregateIdentifier actual = testSubject.resolveTarget(asCommandMessage(new AnnotationCommandTargetResolverTest.FieldAnnotatedCommand(aggregateIdentifier, version)));
        Assert.assertEquals(aggregateIdentifier.toString(), actual.getIdentifier());
        Assert.assertEquals(version, actual.getVersion());
    }

    @Test
    public void testResolveTarget_WithAnnotatedFields_StringIdentifier() {
        final UUID aggregateIdentifier = UUID.randomUUID();
        final Object version = 1L;
        VersionedAggregateIdentifier actual = testSubject.resolveTarget(asCommandMessage(new AnnotationCommandTargetResolverTest.FieldAnnotatedCommand(aggregateIdentifier, version)));
        Assert.assertEquals(aggregateIdentifier.toString(), actual.getIdentifier());
        Assert.assertEquals(version, actual.getVersion());
    }

    @Test
    public void testResolveTarget_WithAnnotatedFields_ObjectIdentifier() {
        final Object aggregateIdentifier = new Object();
        final Object version = 1L;
        VersionedAggregateIdentifier actual = testSubject.resolveTarget(asCommandMessage(new AnnotationCommandTargetResolverTest.FieldAnnotatedCommand(aggregateIdentifier, version)));
        Assert.assertEquals(aggregateIdentifier.toString(), actual.getIdentifier());
        Assert.assertEquals(version, actual.getVersion());
    }

    @Test
    public void testResolveTarget_WithAnnotatedFields_ParsableVersion() {
        final UUID aggregateIdentifier = UUID.randomUUID();
        final Object version = "1";
        VersionedAggregateIdentifier actual = testSubject.resolveTarget(asCommandMessage(new AnnotationCommandTargetResolverTest.FieldAnnotatedCommand(aggregateIdentifier, version)));
        Assert.assertEquals(aggregateIdentifier.toString(), actual.getIdentifier());
        Assert.assertEquals(((Long) (1L)), actual.getVersion());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResolveTarget_WithAnnotatedFields_NonNumericVersion() {
        final UUID aggregateIdentifier = UUID.randomUUID();
        final Object version = "abc";
        testSubject.resolveTarget(asCommandMessage(new AnnotationCommandTargetResolverTest.FieldAnnotatedCommand(aggregateIdentifier, version)));
    }

    @Test
    public void testMetaAnnotationsOnMethods() {
        final UUID aggregateIdentifier = UUID.randomUUID();
        final Long version = Long.valueOf(98765432109L);
        VersionedAggregateIdentifier actual = testSubject.resolveTarget(asCommandMessage(new Object() {
            @AnnotationCommandTargetResolverTest.MetaTargetAggregateIdentifier
            private UUID getIdentifier() {
                return aggregateIdentifier;
            }

            @AnnotationCommandTargetResolverTest.MetaTargetAggregateVersion
            private Long version() {
                return version;
            }
        }));
        Assert.assertEquals(aggregateIdentifier.toString(), actual.getIdentifier());
        Assert.assertEquals(version, actual.getVersion());
    }

    @Test
    public void testMetaAnnotationsOnFields() {
        final UUID aggregateIdentifier = UUID.randomUUID();
        final Long version = Long.valueOf(98765432109L);
        VersionedAggregateIdentifier actual = testSubject.resolveTarget(asCommandMessage(new AnnotationCommandTargetResolverTest.FieldMetaAnnotatedCommand(aggregateIdentifier, version)));
        Assert.assertEquals(aggregateIdentifier.toString(), actual.getIdentifier());
        Assert.assertEquals(version, actual.getVersion());
    }

    @Test
    public void testCustomAnnotationsOnMethods() {
        testSubject = AnnotationCommandTargetResolver.builder().targetAggregateIdentifierAnnotation(AnnotationCommandTargetResolverTest.CustomTargetAggregateIdentifier.class).targetAggregateVersionAnnotation(AnnotationCommandTargetResolverTest.CustomTargetAggregateVersion.class).build();
        final UUID aggregateIdentifier = UUID.randomUUID();
        final Long version = Long.valueOf(98765432109L);
        VersionedAggregateIdentifier actual = testSubject.resolveTarget(asCommandMessage(new Object() {
            @AnnotationCommandTargetResolverTest.CustomTargetAggregateIdentifier
            private UUID getIdentifier() {
                return aggregateIdentifier;
            }

            @AnnotationCommandTargetResolverTest.CustomTargetAggregateVersion
            private Long version() {
                return version;
            }
        }));
        Assert.assertEquals(aggregateIdentifier.toString(), actual.getIdentifier());
        Assert.assertEquals(version, actual.getVersion());
    }

    @Test
    public void testCustomAnnotationsOnFields() {
        testSubject = AnnotationCommandTargetResolver.builder().targetAggregateIdentifierAnnotation(AnnotationCommandTargetResolverTest.CustomTargetAggregateIdentifier.class).targetAggregateVersionAnnotation(AnnotationCommandTargetResolverTest.CustomTargetAggregateVersion.class).build();
        final UUID aggregateIdentifier = UUID.randomUUID();
        final Long version = Long.valueOf(98765432109L);
        VersionedAggregateIdentifier actual = testSubject.resolveTarget(asCommandMessage(new AnnotationCommandTargetResolverTest.FieldCustomAnnotatedCommand(aggregateIdentifier, version)));
        Assert.assertEquals(aggregateIdentifier.toString(), actual.getIdentifier());
        Assert.assertEquals(version, actual.getVersion());
    }

    private static class FieldAnnotatedCommand {
        @TargetAggregateIdentifier
        private final Object aggregateIdentifier;

        @TargetAggregateVersion
        private final Object version;

        public FieldAnnotatedCommand(Object aggregateIdentifier, Object version) {
            this.aggregateIdentifier = aggregateIdentifier;
            this.version = version;
        }
    }

    private static class FieldMetaAnnotatedCommand {
        @AnnotationCommandTargetResolverTest.MetaTargetAggregateIdentifier
        private final Object aggregateIdentifier;

        @AnnotationCommandTargetResolverTest.MetaTargetAggregateVersion
        private final Object version;

        public FieldMetaAnnotatedCommand(Object aggregateIdentifier, Object version) {
            this.aggregateIdentifier = aggregateIdentifier;
            this.version = version;
        }
    }

    private static class FieldCustomAnnotatedCommand {
        @AnnotationCommandTargetResolverTest.CustomTargetAggregateIdentifier
        private final Object aggregateIdentifier;

        @AnnotationCommandTargetResolverTest.CustomTargetAggregateVersion
        private final Object version;

        public FieldCustomAnnotatedCommand(Object aggregateIdentifier, Object version) {
            this.aggregateIdentifier = aggregateIdentifier;
            this.version = version;
        }
    }

    @Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @TargetAggregateIdentifier
    public static @interface MetaTargetAggregateIdentifier {}

    @Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @TargetAggregateVersion
    public static @interface MetaTargetAggregateVersion {}

    @Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface CustomTargetAggregateIdentifier {}

    @Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface CustomTargetAggregateVersion {}
}

