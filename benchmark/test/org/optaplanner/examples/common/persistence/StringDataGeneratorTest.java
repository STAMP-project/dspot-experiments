/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.examples.common.persistence;


import org.junit.Assert;
import org.junit.Test;
import org.optaplanner.examples.common.persistence.generator.StringDataGenerator;


public class StringDataGeneratorTest {
    @Test
    public void with2Parts() {
        StringDataGenerator generator = new StringDataGenerator();
        generator.addPart("a", "b", "c", "d");
        generator.addPart("h", "i", "j", "k");
        Assert.assertEquals("a h", generator.generateNextValue());
        Assert.assertEquals("b i", generator.generateNextValue());
        Assert.assertEquals("c j", generator.generateNextValue());
        Assert.assertEquals("d k", generator.generateNextValue());
        Assert.assertEquals("a i", generator.generateNextValue());
        Assert.assertEquals("b j", generator.generateNextValue());
        Assert.assertEquals("c k", generator.generateNextValue());
        Assert.assertEquals("d h", generator.generateNextValue());
        Assert.assertEquals("a j", generator.generateNextValue());
        Assert.assertEquals("b k", generator.generateNextValue());
        Assert.assertEquals("c h", generator.generateNextValue());
        Assert.assertEquals("d i", generator.generateNextValue());
    }

    @Test
    public void with3Parts() {
        StringDataGenerator generator = new StringDataGenerator();
        generator.addPart("a", "b", "c", "d");
        generator.addPart("h", "i", "j", "k");
        generator.addPart("o", "p", "q", "r");
        Assert.assertEquals("a h o", generator.generateNextValue());
        Assert.assertEquals("b i p", generator.generateNextValue());
        Assert.assertEquals("c j q", generator.generateNextValue());
        Assert.assertEquals("d k r", generator.generateNextValue());
        Assert.assertEquals("a h p", generator.generateNextValue());
        Assert.assertEquals("b i q", generator.generateNextValue());
        Assert.assertEquals("c j r", generator.generateNextValue());
        Assert.assertEquals("d k o", generator.generateNextValue());
    }

    @Test
    public void with4Parts() {
        StringDataGenerator generator = new StringDataGenerator();
        generator.addPart("a", "b", "c", "d");
        generator.addPart("h", "i", "j", "k");
        generator.addPart("o", "p", "q", "r");
        generator.addPart("v", "w", "x", "y");
        Assert.assertEquals("a h o v", generator.generateNextValue());
        Assert.assertEquals("b i p w", generator.generateNextValue());
        Assert.assertEquals("c j q x", generator.generateNextValue());
        Assert.assertEquals("d k r y", generator.generateNextValue());
        Assert.assertEquals("a h p w", generator.generateNextValue());
        Assert.assertEquals("b i q x", generator.generateNextValue());
        Assert.assertEquals("c j r y", generator.generateNextValue());
        Assert.assertEquals("d k o v", generator.generateNextValue());
        Assert.assertEquals("a h q x", generator.generateNextValue());
        Assert.assertEquals("b i r y", generator.generateNextValue());
        Assert.assertEquals("c j o v", generator.generateNextValue());
        Assert.assertEquals("d k p w", generator.generateNextValue());
    }

    @Test
    public void with4PartsMaximumSizeFor2() {
        StringDataGenerator generator = new StringDataGenerator();
        generator.addPart("a", "b", "c", "d");
        generator.addPart("h", "i", "j", "k");
        generator.addPart("o", "p", "q", "r");
        generator.addPart("v", "w", "x", "y");
        generator.predictMaximumSizeAndReset(9);
        Assert.assertEquals("a v", generator.generateNextValue());
        Assert.assertEquals("b w", generator.generateNextValue());
        Assert.assertEquals("c x", generator.generateNextValue());
        Assert.assertEquals("d y", generator.generateNextValue());
        Assert.assertEquals("a w", generator.generateNextValue());
        Assert.assertEquals("b x", generator.generateNextValue());
        Assert.assertEquals("c y", generator.generateNextValue());
        Assert.assertEquals("d v", generator.generateNextValue());
    }

    @Test
    public void with4PartsMaximumSizeFor3() {
        StringDataGenerator generator = new StringDataGenerator();
        generator.addPart("a", "b", "c", "d");
        generator.addPart("h", "i", "j", "k");
        generator.addPart("o", "p", "q", "r");
        generator.addPart("v", "w", "x", "y");
        generator.predictMaximumSizeAndReset(((4 * 4) + 3));
        Assert.assertEquals("a o v", generator.generateNextValue());
        Assert.assertEquals("b p w", generator.generateNextValue());
        Assert.assertEquals("c q x", generator.generateNextValue());
        Assert.assertEquals("d r y", generator.generateNextValue());
        Assert.assertEquals("a o w", generator.generateNextValue());
        Assert.assertEquals("b p x", generator.generateNextValue());
        Assert.assertEquals("c q y", generator.generateNextValue());
        Assert.assertEquals("d r v", generator.generateNextValue());
    }
}

