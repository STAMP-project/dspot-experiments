/**
 * Copyright 2013 MovingBlocks
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
package org.terasology.entitySystem.metadata.internal;


import org.junit.Assert;
import org.junit.Test;
import org.terasology.engine.SimpleUri;
import org.terasology.reflection.copy.CopyStrategyLibrary;
import org.terasology.reflection.metadata.DefaultClassMetadata;
import org.terasology.reflection.metadata.FieldMetadata;
import org.terasology.reflection.reflect.ReflectFactory;
import org.terasology.reflection.reflect.ReflectionReflectFactory;


/**
 *
 */
public class MetadataBuilderTest {
    private ReflectFactory factory = new ReflectionReflectFactory();

    private CopyStrategyLibrary copyStrategyLibrary = new CopyStrategyLibrary(factory);

    @Test
    public void testDetectsLackOfDefaultConstructor() throws Exception {
        DefaultClassMetadata<MetadataBuilderTest.NoDefaultConstructor> metadata = new DefaultClassMetadata(new SimpleUri(), MetadataBuilderTest.NoDefaultConstructor.class, factory, copyStrategyLibrary);
        Assert.assertFalse(metadata.isConstructable());
    }

    @Test
    public void testTrivialMetadata() throws Exception {
        DefaultClassMetadata<MetadataBuilderTest.Trivial> metadata = new DefaultClassMetadata(new SimpleUri(), MetadataBuilderTest.Trivial.class, factory, copyStrategyLibrary);
        Assert.assertNotNull(metadata);
        Assert.assertEquals(0, metadata.getFieldCount());
        Assert.assertTrue(metadata.isConstructable());
    }

    @Test
    public void testPrivateField() throws Exception {
        DefaultClassMetadata<MetadataBuilderTest.PrivateField> metadata = new DefaultClassMetadata(new SimpleUri(), MetadataBuilderTest.PrivateField.class, factory, copyStrategyLibrary);
        Assert.assertNotNull(metadata);
        Assert.assertEquals(1, metadata.getFieldCount());
        FieldMetadata fieldMetadata = metadata.getField("name");
        Assert.assertNotNull(fieldMetadata);
        Assert.assertEquals(String.class, fieldMetadata.getType());
        Assert.assertEquals("name", fieldMetadata.getName());
        Assert.assertNotNull(metadata.newInstance());
    }

    @Test
    public void testInheritsFields() throws Exception {
        DefaultClassMetadata<MetadataBuilderTest.Inheriting> metadata = new DefaultClassMetadata(new SimpleUri(), MetadataBuilderTest.Inheriting.class, factory, copyStrategyLibrary);
        Assert.assertNotNull(metadata);
        Assert.assertEquals(2, metadata.getFieldCount());
        Assert.assertNotNull(metadata.getField("name"));
        Assert.assertNotNull(metadata.getField("value"));
        Assert.assertNotNull(metadata.newInstance());
    }

    private static class NoDefaultConstructor {
        NoDefaultConstructor(String name) {
        }
    }

    public static class Trivial {}

    public static class PrivateField {
        private String name;

        private PrivateField() {
        }

        public PrivateField(String name) {
            this.name = name;
        }

        public String whatName() {
            return name;
        }
    }

    public static class Inheriting extends MetadataBuilderTest.PrivateField {
        private String value;

        private Inheriting() {
        }

        public Inheriting(String name, String value) {
            super(name);
            this.value = value;
        }

        public String whatValue() {
            return value;
        }
    }
}

