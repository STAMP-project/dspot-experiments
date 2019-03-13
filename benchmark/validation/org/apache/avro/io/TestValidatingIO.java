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


import java.io.IOException;
import org.apache.avro.Schema;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class TestValidatingIO {
    enum Encoding {

        BINARY,
        BLOCKING_BINARY,
        JSON;}

    private static final Logger LOG = LoggerFactory.getLogger(TestValidatingIO.class);

    private TestValidatingIO.Encoding eEnc;

    private int iSkipL;

    private String sJsSch;

    private String sCl;

    public TestValidatingIO(TestValidatingIO.Encoding enc, int skip, String js, String cls) {
        this.eEnc = enc;
        this.iSkipL = skip;
        this.sJsSch = js;
        this.sCl = cls;
    }

    private static final int COUNT = 1;

    @Test
    public void testMain() throws IOException {
        for (int i = 0; i < (TestValidatingIO.COUNT); i++) {
            testOnce(new Schema.Parser().parse(sJsSch), sCl, iSkipL, eEnc);
        }
    }

    public static class InputScanner {
        private final char[] chars;

        private int cpos = 0;

        public InputScanner(char[] chars) {
            this.chars = chars;
        }

        public boolean next() {
            if ((cpos) < (chars.length)) {
                (cpos)++;
            }
            return (cpos) != (chars.length);
        }

        public char cur() {
            return chars[cpos];
        }

        public boolean isDone() {
            return (cpos) == (chars.length);
        }
    }

    private static Object[][] encodings = new Object[][]{ new Object[]{ TestValidatingIO.Encoding.BINARY }, new Object[]{ TestValidatingIO.Encoding.BLOCKING_BINARY }, new Object[]{ TestValidatingIO.Encoding.JSON } };

    private static Object[][] skipLevels = new Object[][]{ new Object[]{ -1 }, new Object[]{ 0 }, new Object[]{ 1 }, new Object[]{ 2 } };
}

