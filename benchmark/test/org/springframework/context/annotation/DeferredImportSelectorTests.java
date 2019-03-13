/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.context.annotation;


import Group.Entry;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.core.type.AnnotationMetadata;


/**
 * Tests for {@link DeferredImportSelector}.
 *
 * @author Stephane Nicoll
 */
public class DeferredImportSelectorTests {
    @Test
    public void entryEqualsSameInstance() {
        AnnotationMetadata metadata = Mockito.mock(AnnotationMetadata.class);
        Group.Entry entry = new Group.Entry(metadata, "com.example.Test");
        Assert.assertEquals(entry, entry);
    }

    @Test
    public void entryEqualsSameMetadataAndClassName() {
        AnnotationMetadata metadata = Mockito.mock(AnnotationMetadata.class);
        Assert.assertEquals(new Group.Entry(metadata, "com.example.Test"), new Group.Entry(metadata, "com.example.Test"));
    }

    @Test
    public void entryEqualDifferentMetadataAndSameClassName() {
        Assert.assertNotEquals(new Group.Entry(Mockito.mock(AnnotationMetadata.class), "com.example.Test"), new Group.Entry(Mockito.mock(AnnotationMetadata.class), "com.example.Test"));
    }

    @Test
    public void entryEqualSameMetadataAnDifferentClassName() {
        AnnotationMetadata metadata = Mockito.mock(AnnotationMetadata.class);
        Assert.assertNotEquals(new Group.Entry(metadata, "com.example.Test"), new Group.Entry(metadata, "com.example.AnotherTest"));
    }
}

