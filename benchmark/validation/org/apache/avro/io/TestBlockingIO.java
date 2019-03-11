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
package org.apache.avro.io;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Stack;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestBlockingIO {
    private static final String UTF_8 = "UTF-8";

    private final int iSize;

    private final int iDepth;

    private final String sInput;

    public TestBlockingIO(int sz, int dp, String inp) {
        this.iSize = sz;
        this.iDepth = dp;
        this.sInput = inp;
    }

    private static class Tests {
        private final JsonParser parser;

        private final Decoder input;

        private final int depth;

        public Tests(int bufferSize, int depth, String input) throws IOException {
            this.depth = depth;
            byte[] in = input.getBytes("UTF-8");
            JsonFactory f = new JsonFactory();
            JsonParser p = f.createJsonParser(new ByteArrayInputStream(input.getBytes("UTF-8")));
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            EncoderFactory factory = new EncoderFactory().configureBlockSize(bufferSize);
            Encoder cos = factory.blockingBinaryEncoder(os, null);
            TestBlockingIO.serialize(cos, p, os);
            cos.flush();
            byte[] bb = os.toByteArray();
            // dump(bb);
            this.input = DecoderFactory.get().binaryDecoder(bb, null);
            this.parser = f.createJsonParser(new ByteArrayInputStream(in));
        }

        public void scan() throws IOException {
            Stack<TestBlockingIO.S> countStack = new Stack<>();
            long count = 0;
            while ((parser.nextToken()) != null) {
                switch (parser.getCurrentToken()) {
                    case END_ARRAY :
                        Assert.assertEquals(0, count);
                        Assert.assertTrue(countStack.peek().isArray);
                        count = countStack.pop().count;
                        break;
                    case END_OBJECT :
                        Assert.assertEquals(0, count);
                        Assert.assertFalse(countStack.peek().isArray);
                        count = countStack.pop().count;
                        break;
                    case START_ARRAY :
                        countStack.push(new TestBlockingIO.S(count, true));
                        count = input.readArrayStart();
                        continue;
                    case VALUE_STRING :
                        {
                            String s = parser.getText();
                            int n = s.getBytes(TestBlockingIO.UTF_8).length;
                            TestBlockingIO.checkString(s, input, n);
                            break;
                        }
                    case FIELD_NAME :
                        {
                            String s = parser.getCurrentName();
                            int n = s.getBytes(TestBlockingIO.UTF_8).length;
                            TestBlockingIO.checkString(s, input, n);
                            continue;
                        }
                    case START_OBJECT :
                        countStack.push(new TestBlockingIO.S(count, false));
                        count = input.readMapStart();
                        if (count < 0) {
                            count = -count;
                            input.readLong();// byte count

                        }
                        continue;
                    default :
                        throw new RuntimeException(("Unsupported: " + (parser.getCurrentToken())));
                }
                count--;
                if (count == 0) {
                    count = (countStack.peek().isArray) ? input.arrayNext() : input.mapNext();
                }
            } 
        }

        public void skip(int skipLevel) throws IOException {
            Stack<TestBlockingIO.S> countStack = new Stack<>();
            long count = 0;
            while ((parser.nextToken()) != null) {
                switch (parser.getCurrentToken()) {
                    case END_ARRAY :
                        // assertEquals(0, count);
                        Assert.assertTrue(countStack.peek().isArray);
                        count = countStack.pop().count;
                        break;
                    case END_OBJECT :
                        // assertEquals(0, count);
                        Assert.assertFalse(countStack.peek().isArray);
                        count = countStack.pop().count;
                        break;
                    case START_ARRAY :
                        if ((countStack.size()) == skipLevel) {
                            TestBlockingIO.skipArray(parser, input, ((depth) - skipLevel));
                            break;
                        } else {
                            countStack.push(new TestBlockingIO.S(count, true));
                            count = input.readArrayStart();
                            continue;
                        }
                    case VALUE_STRING :
                        {
                            if ((countStack.size()) == skipLevel) {
                                input.skipBytes();
                            } else {
                                String s = parser.getText();
                                int n = s.getBytes(TestBlockingIO.UTF_8).length;
                                TestBlockingIO.checkString(s, input, n);
                            }
                            break;
                        }
                    case FIELD_NAME :
                        {
                            String s = parser.getCurrentName();
                            int n = s.getBytes(TestBlockingIO.UTF_8).length;
                            TestBlockingIO.checkString(s, input, n);
                            continue;
                        }
                    case START_OBJECT :
                        if ((countStack.size()) == skipLevel) {
                            TestBlockingIO.skipMap(parser, input, ((depth) - skipLevel));
                            break;
                        } else {
                            countStack.push(new TestBlockingIO.S(count, false));
                            count = input.readMapStart();
                            if (count < 0) {
                                count = -count;
                                input.readLong();// byte count

                            }
                            continue;
                        }
                    default :
                        throw new RuntimeException(("Unsupported: " + (parser.getCurrentToken())));
                }
                count--;
                if (count == 0) {
                    count = (countStack.peek().isArray) ? input.arrayNext() : input.mapNext();
                }
            } 
        }
    }

    private static class S {
        public final long count;

        public final boolean isArray;

        public S(long count, boolean isArray) {
            this.count = count;
            this.isArray = isArray;
        }
    }

    @Test
    public void testScan() throws IOException {
        TestBlockingIO.Tests t = new TestBlockingIO.Tests(iSize, iDepth, sInput);
        t.scan();
    }

    @Test
    public void testSkip1() throws IOException {
        testSkip(iSize, iDepth, sInput, 0);
    }

    @Test
    public void testSkip2() throws IOException {
        testSkip(iSize, iDepth, sInput, 1);
    }

    @Test
    public void testSkip3() throws IOException {
        testSkip(iSize, iDepth, sInput, 2);
    }
}

