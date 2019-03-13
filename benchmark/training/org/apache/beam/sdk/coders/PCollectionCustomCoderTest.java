/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.coders;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.testing.NeedsRunner;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.DoFn.ProcessContext;
import org.apache.beam.sdk.transforms.DoFn.ProcessElement;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for coder exception handling in runners.
 */
@RunWith(JUnit4.class)
public class PCollectionCustomCoderTest {
    private static final Logger LOG = LoggerFactory.getLogger(PCollectionCustomCoderTest.class);

    /**
     * A custom test coder that can throw various exceptions during:
     *
     * <ul>
     *   <li>encoding or decoding of data.
     *   <li>serializing or deserializing the coder.
     * </ul>
     */
    static final String IO_EXCEPTION = "java.io.IOException";

    static final String NULL_POINTER_EXCEPTION = "java.lang.NullPointerException";

    static final String EXCEPTION_MESSAGE = "Super Unique Message!!!";

    @Rule
    public final transient ExpectedException thrown = ExpectedException.none();

    @Rule
    public final transient TestPipeline pipeline = TestPipeline.create();

    /**
     * Wrapper of StringUtf8Coder with customizable exception-throwing.
     */
    public static class CustomTestCoder extends CustomCoder<String> {
        private final String decodingException;

        private final String encodingException;

        private final String serializationException;

        private final String deserializationException;

        private final String exceptionMessage;

        /**
         *
         *
         * @param decodingException
         * 		The fully qualified class name of the exception to throw while
         * 		decoding data. e.g. (e.g. 'java.lang.NullPointerException')
         * @param encodingException
         * 		The fully qualified class name of the exception to throw while
         * 		encoding data. e.g. (e.g. 'java.lang.NullPointerException')
         * @param serializationException
         * 		The fully qualified class name of the exception to throw while
         * 		serializing the coder. (e.g. 'java.lang.NullPointerException')
         * @param deserializationException
         * 		The fully qualified class name of the exception to throw
         * 		while deserializing the coder. (e.g. 'java.lang.NullPointerException')
         * @param exceptionMessage
         * 		The message to place inside the body of the exception.
         */
        public CustomTestCoder(String decodingException, String encodingException, String serializationException, String deserializationException, String exceptionMessage) {
            this.decodingException = decodingException;
            this.encodingException = encodingException;
            this.serializationException = serializationException;
            this.deserializationException = deserializationException;
            this.exceptionMessage = exceptionMessage;
        }

        @Override
        public void encode(String value, OutputStream outStream) throws IOException, CoderException {
            throwIfPresent(encodingException);
            StringUtf8Coder.of().encode(value, outStream);
        }

        @Override
        public String decode(InputStream inStream) throws IOException, CoderException {
            throwIfPresent(decodingException);
            return StringUtf8Coder.of().decode(inStream);
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            throwIfPresent(serializationException);
            out.defaultWriteObject();
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            throwIfPresent(deserializationException);
        }

        /**
         * Constructs and throws the specified exception.
         *
         * @param exceptionClassName
         * 		The fully qualified class name of the exception to throw. If null,
         * 		then no exception will be thrown.
         * @throws IOException
         * 		Is thrown if java.lang.IOException is passed in as exceptionClassName.
         */
        private void throwIfPresent(String exceptionClassName) throws IOException {
            if (exceptionClassName == null) {
                return;
            }
            try {
                Object throwable = Class.forName(exceptionClassName).getConstructor(String.class).newInstance(exceptionMessage);
                if (throwable instanceof IOException) {
                    throw ((IOException) (throwable));
                } else {
                    throw new RuntimeException(((Throwable) (throwable)));
                }
            } catch (InvocationTargetException | NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    @Category(NeedsRunner.class)
    public void testDecodingIOException() throws Exception {
        Pipeline p = pipelineWith(new PCollectionCustomCoderTest.CustomTestCoder(PCollectionCustomCoderTest.IO_EXCEPTION, null, null, null, PCollectionCustomCoderTest.EXCEPTION_MESSAGE));
        thrown.expect(Exception.class);
        thrown.expect(new PCollectionCustomCoderTest.ExceptionMatcher("java.io.IOException: Super Unique Message!!!"));
        p.run().waitUntilFinish();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testDecodingNPException() throws Exception {
        Pipeline p = pipelineWith(new PCollectionCustomCoderTest.CustomTestCoder(PCollectionCustomCoderTest.NULL_POINTER_EXCEPTION, null, null, null, PCollectionCustomCoderTest.EXCEPTION_MESSAGE));
        thrown.expect(Exception.class);
        thrown.expect(new PCollectionCustomCoderTest.ExceptionMatcher("java.lang.NullPointerException: Super Unique Message!!!"));
        p.run().waitUntilFinish();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testEncodingIOException() throws Exception {
        Pipeline p = pipelineWith(new PCollectionCustomCoderTest.CustomTestCoder(null, PCollectionCustomCoderTest.IO_EXCEPTION, null, null, PCollectionCustomCoderTest.EXCEPTION_MESSAGE));
        thrown.expect(Exception.class);
        thrown.expect(new PCollectionCustomCoderTest.ExceptionMatcher("java.io.IOException: Super Unique Message!!!"));
        p.run().waitUntilFinish();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testEncodingNPException() throws Exception {
        Pipeline p = pipelineWith(new PCollectionCustomCoderTest.CustomTestCoder(null, PCollectionCustomCoderTest.NULL_POINTER_EXCEPTION, null, null, PCollectionCustomCoderTest.EXCEPTION_MESSAGE));
        thrown.expect(Exception.class);
        thrown.expect(new PCollectionCustomCoderTest.ExceptionMatcher("java.lang.NullPointerException: Super Unique Message!!!"));
        p.run().waitUntilFinish();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testSerializationIOException() throws Exception {
        Pipeline p = pipelineWith(new PCollectionCustomCoderTest.CustomTestCoder(null, null, PCollectionCustomCoderTest.IO_EXCEPTION, null, PCollectionCustomCoderTest.EXCEPTION_MESSAGE));
        thrown.expect(Exception.class);
        thrown.expect(new PCollectionCustomCoderTest.ExceptionMatcher("java.io.IOException: Super Unique Message!!!"));
        p.run().waitUntilFinish();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testSerializationNPException() throws Exception {
        Pipeline p = pipelineWith(new PCollectionCustomCoderTest.CustomTestCoder(null, null, PCollectionCustomCoderTest.NULL_POINTER_EXCEPTION, null, PCollectionCustomCoderTest.EXCEPTION_MESSAGE));
        thrown.expect(Exception.class);
        thrown.expect(new PCollectionCustomCoderTest.ExceptionMatcher("java.lang.NullPointerException: Super Unique Message!!!"));
        p.run().waitUntilFinish();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testNoException() throws Exception {
        Pipeline p = pipelineWith(new PCollectionCustomCoderTest.CustomTestCoder(null, null, null, null, null));
        p.run().waitUntilFinish();
    }

    static class IdentityDoFn extends DoFn<String, String> {
        @ProcessElement
        public void process(ProcessContext c) {
            c.output(c.element());
        }
    }

    static class ContentReader implements SerializableFunction<Iterable<String>, Void> {
        private final String[] expected;

        public static PCollectionCustomCoderTest.ContentReader elementsEqual(Iterable<String> expected) {
            return new PCollectionCustomCoderTest.ContentReader(expected);
        }

        private ContentReader(Iterable<String> expected) {
            ArrayList<String> ret = new ArrayList<>();
            for (String t : expected) {
                ret.add(t);
            }
            this.expected = ret.toArray(new String[ret.size()]);
        }

        @Override
        public Void apply(Iterable<String> contents) {
            Assert.assertThat(contents, containsInAnyOrder(expected));
            return null;
        }
    }

    static class ExceptionMatcher extends TypeSafeMatcher<Throwable> {
        private String expectedError;

        public ExceptionMatcher(String expected) {
            this.expectedError = expected;
        }

        @Override
        public boolean matchesSafely(Throwable result) {
            if (result.toString().contains(expectedError)) {
                return true;
            }
            Throwable cause = result.getCause();
            while (null != cause) {
                String causeString = cause.toString();
                if (causeString.contains(expectedError)) {
                    return true;
                }
                cause = cause.getCause();
            } 
            return false;
        }

        @Override
        public void describeTo(Description descr) {
            descr.appendText(("exception with text matching: " + (expectedError)));
        }
    }
}

