/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.client.impl.protocol.codec;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.internal.ComparisonCriteria;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class StackTraceElementCodecTest extends HazelcastTestSupport {
    @Test
    public void testConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(StackTraceElementCodec.class);
    }

    @Test
    public void testEncodeDecodeStackTrace() {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        testEncodeDecodeStackTrace(stackTrace);
    }

    @Test
    public void testEncodeDecodeStackTraceWithNullFileName() {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        stackTrace = nullifyFileNames(stackTrace);
        testEncodeDecodeStackTrace(stackTrace);
    }

    /**
     * Helper class to assert 2 StackTraceElement arrays are equals, where the equality is only based on fields available in
     * Java 6 (and also 7, 8). The reason for using this helper class is the fact Java 9 adds several new fields to the
     * {@link StackTraceElement} class and Hazelcast encoder/decoder works just with the ones from Java 6.
     */
    public static class StackTraceElementComparisonCriteria extends ComparisonCriteria {
        @Override
        protected void assertElementsEqual(Object expected, Object actual) {
            if ((expected instanceof StackTraceElement) && (actual instanceof StackTraceElement)) {
                StackTraceElement st1 = ((StackTraceElement) (expected));
                StackTraceElement st2 = ((StackTraceElement) (actual));
                Assert.assertEquals("ClassNames have to be equal", st1.getClassName(), st2.getClassName());
                Assert.assertEquals("LineNumbers have to be equal", st1.getLineNumber(), st2.getLineNumber());
                Assert.assertEquals("MethodNames have to be equal", st1.getMethodName(), st2.getMethodName());
                Assert.assertEquals("FileNames have to be equal", st1.getFileName(), st2.getFileName());
            } else {
                Assert.assertEquals(expected, actual);
            }
        }
    }
}

