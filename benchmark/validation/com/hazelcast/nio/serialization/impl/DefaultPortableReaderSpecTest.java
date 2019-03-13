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
package com.hazelcast.nio.serialization.impl;


import com.hazelcast.map.AbstractEntryProcessor;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.query.impl.getters.MultiResult;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.SlowTest;
import com.hazelcast.util.ExceptionUtil;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static com.hazelcast.nio.serialization.impl.DefaultPortableReaderTestStructure.PrimitivePortable.Init.FULL;


/**
 * Tests that verifies the behavior of the DefaultPortableReader.
 * All tests cases are generated, since there's a lot of possible cases due to the long lists of read* method on the reader.
 * <p>
 * The test is parametrised with 4 parameters
 * Each test execution runs one read operation on the reader.
 * <p>
 * The rationale behind these tests is to cover all possible combinations of reads using nested paths and quantifiers
 * (number or any) with all possible portable types. It's impossible to do it manually, since there's 20 supported
 * types and a read method for each one of them.
 * <p>
 * Each test case is documented, plus each test outputs it's scenario in a readable way, so you it's easy to follow
 * the test case while you run it. Also each test case shows in which method it is generated.
 * <p>
 * IF YOU SEE A FAILURE HERE:
 * - check the test output - analyse the test scenario
 * - check in which method the scenario is generated - narrow down the scope of the tests run
 */
@RunWith(Parameterized.class)
@Category({ SlowTest.class, ParallelTest.class })
public class DefaultPortableReaderSpecTest extends HazelcastTestSupport {
    private static final DefaultPortableReaderTestStructure.PrimitivePortable P_NON_EMPTY = new DefaultPortableReaderTestStructure.PrimitivePortable(0, FULL);

    private static final DefaultPortableReaderTestStructure.GroupPortable G_NON_EMPTY = DefaultPortableReaderTestStructure.group(FULL);

    private static final DefaultPortableReaderTestStructure.NestedGroupPortable N_NON_EMPTY = DefaultPortableReaderTestStructure.nested(new Portable[]{ DefaultPortableReaderSpecTest.G_NON_EMPTY, DefaultPortableReaderSpecTest.G_NON_EMPTY });

    @Rule
    public ExpectedException expected = ExpectedException.none();

    // input object
    private Portable inputObject;

    // object or exception
    private Object expectedResult;

    // e.g. 'readInt', or generic 'read'
    private String readMethodNameToInvoke;

    // e.g. body.brain.iq
    private String pathToRead;

    // parent method of this test to identify it in case of failures
    private String parent;

    public DefaultPortableReaderSpecTest(Portable inputObject, Object expectedResult, DefaultPortableReaderTestStructure.Method method, String pathToRead, String parent) {
        this.inputObject = inputObject;
        this.expectedResult = expectedResult;
        this.readMethodNameToInvoke = method.name().replace("Generic", "");
        this.pathToRead = pathToRead;
        this.parent = parent;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void executeTestScenario() throws Exception {
        // handle result
        Object resultToMatch = expectedResult;
        if ((expectedResult) instanceof Class) {
            // expected exception case
            expected.expect(Matchers.isA(((Class) (expectedResult))));
        } else
            if ((expectedResult) instanceof List) {
                // just convenience -> if result is a list if will be compared to an array, so it has to be converted
                resultToMatch = ((List) (resultToMatch)).toArray();
            }

        // print test scenario for debug purposes
        // it makes debugging easier since all scenarios are generated
        printlnScenarioDescription(resultToMatch);
        // assert the condition
        Object result = DefaultPortableReaderSpecTest.Invoker.invoke(reader(inputObject), readMethodNameToInvoke, pathToRead);
        if (result instanceof MultiResult) {
            MultiResult multiResult = ((MultiResult) (result));
            if ((((multiResult.getResults().size()) == 1) && ((multiResult.getResults().get(0)) == null)) && (multiResult.isNullEmptyTarget())) {
                // explode null in case of a single multi-result target result
                result = null;
            } else {
                // in case of multi result while invoking generic "read" method deal with the multi results
                result = getResults().toArray();
            }
            Assert.assertThat(result, Matchers.equalTo(resultToMatch));
        } else {
            Assert.assertThat(result, Matchers.equalTo(resultToMatch));
        }
    }

    /**
     * Steals a "real" serialised Data of a PortableObject to be as close as possible to real use-case.
     */
    public static class EntryStealingProcessor extends AbstractEntryProcessor {
        private final Object key;

        private Data stolenEntryData;

        EntryStealingProcessor(String key) {
            super(false);
            this.key = key;
        }

        @Override
        public Object process(Map.Entry entry) {
            // hack to get rid of de-serialization cost (assuming in-memory-format is BINARY, if it is OBJECT you can replace
            // the null check below with entry.getValue() != null), but works only for versions >= 3.6
            if (key.equals(entry.getKey())) {
                stolenEntryData = ((Data) (getValueData()));
            }
            return null;
        }
    }

    /**
     * Since the reader has a lot of methods and we want to parametrise the test to invoke each one of them in a generic
     * way, we invoke them using this Invoker that leverages reflection.
     */
    static class Invoker {
        public static <T> T invoke(PortableReader reader, String methodName, String path) {
            return DefaultPortableReaderSpecTest.Invoker.invokeMethod(reader, ("read" + methodName), path);
        }

        @SuppressWarnings("unchecked")
        static <T> T invokeMethod(Object object, String methodName, String arg) throws RuntimeException {
            try {
                Method method = object.getClass().getMethod(methodName, String.class);
                return ((T) (method.invoke(object, arg)));
            } catch (Exception e) {
                throw ExceptionUtil.rethrow(e);
            }
        }
    }
}

