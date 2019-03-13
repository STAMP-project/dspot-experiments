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
package org.axonframework.serialization;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Allard Buijze
 */
public class AnnotationRevisionResolverTest {
    private AnnotationRevisionResolver testSubject;

    @Test
    public void testRevisionOfAnnotatedClass() {
        Assert.assertEquals("2.3-TEST", testSubject.revisionOf(AnnotationRevisionResolverTest.WithAnnotation.class));
    }

    @Test
    public void testRevisionOfNonAnnotatedClass() {
        Assert.assertEquals(null, testSubject.revisionOf(AnnotationRevisionResolverTest.WithoutAnnotation.class));
    }

    @Revision("2.3-TEST")
    private class WithAnnotation {}

    private class WithoutAnnotation {}
}

