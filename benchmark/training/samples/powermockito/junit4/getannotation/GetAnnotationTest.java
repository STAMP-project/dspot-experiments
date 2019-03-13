/**
 * Copyright 2010 the original author or authors.
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
package samples.powermockito.junit4.getannotation;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.MockGateway;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import samples.annotationbased.AnnotatedClassDemo;
import samples.annotationbased.testannotations.RuntimeAnnotation;


/**
 * Assert that "isAnnotationPresent" and "getAnnotation" works correctly when mockStatic is used
 *
 * @see <a href="https://github.com/jayway/powermock/issues/676">Issue 676</a>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(AnnotatedClassDemo.class)
public class GetAnnotationTest {
    @Test
    public void getClassAnnotationsReturnActualAnnotationsByDefault() throws Exception {
        Assert.assertTrue(AnnotatedClassDemo.class.isAnnotationPresent(RuntimeAnnotation.class));
        Assert.assertNotNull(AnnotatedClassDemo.class.getAnnotation(RuntimeAnnotation.class));
        Assert.assertTrue(AnnotatedClassDemo.staticMethod());
    }

    @Test
    public void nonExistingAnnotationsAreNotReturnedByDefault() throws Exception {
        Assert.assertFalse(AnnotatedClassDemo.class.isAnnotationPresent(Deprecated.class));
        Assert.assertNull(AnnotatedClassDemo.class.getAnnotation(Deprecated.class));
        Assert.assertTrue(AnnotatedClassDemo.staticMethod());
    }

    // behavior before the fix:
    @Test
    public void isAnnotationPresentReturnsFalseWhenMethodsAreMocked() throws Exception {
        MockGateway.MOCK_ANNOTATION_METHODS = true;
        try {
            Assert.assertFalse(AnnotatedClassDemo.class.isAnnotationPresent(RuntimeAnnotation.class));
            Assert.assertTrue(AnnotatedClassDemo.staticMethod());
        } finally {
            MockGateway.MOCK_ANNOTATION_METHODS = false;
        }
    }

    @Test
    public void getAnnotationReturnsNullWhenMethodsAreMocked() throws Exception {
        MockGateway.MOCK_ANNOTATION_METHODS = true;
        try {
            Assert.assertNull(AnnotatedClassDemo.class.getAnnotation(RuntimeAnnotation.class));
            Assert.assertTrue(AnnotatedClassDemo.staticMethod());
        } finally {
            MockGateway.MOCK_ANNOTATION_METHODS = false;
        }
    }
}

