/**
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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
package org.drools.core.rule;


import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.base.ClassObjectType;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.test.model.Cheese;
import org.junit.Assert;
import org.junit.Test;


public class DeclarationTest {
    ClassFieldAccessorStore store = new ClassFieldAccessorStore();

    @Test
    public void testDeclaration() {
        final InternalReadAccessor extractor = store.getReader(Cheese.class, "type");
        final Pattern pattern = new Pattern(5, new ClassObjectType(Cheese.class));
        // Bind the extractor to a decleration
        // Declarations know the pattern they derive their value from
        final Declaration declaration = new Declaration("typeOfCheese", extractor, pattern);
        Assert.assertEquals("typeOfCheese", declaration.getIdentifier());
        Assert.assertSame(String.class, declaration.getDeclarationClass());
        Assert.assertSame(extractor, declaration.getExtractor());
        Assert.assertEquals(5, declaration.getPattern().getOffset());
    }

    @Test
    public void testGetFieldValue() {
        final InternalReadAccessor extractor = store.getReader(Cheese.class, "type");
        final Pattern pattern = new Pattern(5, new ClassObjectType(Cheese.class));
        // Bind the extractor to a decleration
        // Declarations know the pattern they derive their value from
        final Declaration declaration = new Declaration("typeOfCheese", extractor, pattern);
        // Create some facts
        final Cheese cheddar = new Cheese("cheddar", 5);
        // Check we can extract Declarations correctly
        Assert.assertEquals("cheddar", declaration.getValue(null, cheddar));
    }
}

