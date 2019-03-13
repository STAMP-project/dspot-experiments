/**
 * Copyright 2002-2012 the original author or authors.
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
package org.springframework.scheduling.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link AnnotationAsyncExecutionInterceptor}.
 *
 * @author Chris Beams
 * @since 3.1.2
 */
public class AnnotationAsyncExecutionInterceptorTests {
    @Test
    @SuppressWarnings("unused")
    public void testGetExecutorQualifier() throws NoSuchMethodException, SecurityException {
        AnnotationAsyncExecutionInterceptor i = new AnnotationAsyncExecutionInterceptor(null);
        {
            // method level
            class C {
                @Async("qMethod")
                void m() {
                }
            }
            Assert.assertThat(i.getExecutorQualifier(C.class.getDeclaredMethod("m")), CoreMatchers.is("qMethod"));
        }
        {
            // class level
            @Async("qClass")
            class C {
                void m() {
                }
            }
            Assert.assertThat(i.getExecutorQualifier(C.class.getDeclaredMethod("m")), CoreMatchers.is("qClass"));
        }
        {
            // method and class level -> method value overrides
            @Async("qClass")
            class C {
                @Async("qMethod")
                void m() {
                }
            }
            Assert.assertThat(i.getExecutorQualifier(C.class.getDeclaredMethod("m")), CoreMatchers.is("qMethod"));
        }
        {
            // method and class level -> method value, even if empty, overrides
            @Async("qClass")
            class C {
                @Async
                void m() {
                }
            }
            Assert.assertThat(i.getExecutorQualifier(C.class.getDeclaredMethod("m")), CoreMatchers.is(""));
        }
        {
            // meta annotation with qualifier
            @AnnotationAsyncExecutionInterceptorTests.MyAsync
            class C {
                void m() {
                }
            }
            Assert.assertThat(i.getExecutorQualifier(C.class.getDeclaredMethod("m")), CoreMatchers.is("qMeta"));
        }
    }

    @Async("qMeta")
    @Retention(RetentionPolicy.RUNTIME)
    @interface MyAsync {}
}

