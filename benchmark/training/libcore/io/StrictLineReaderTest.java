/**
 * Copyright (C) 2012 The Android Open Source Project
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
package libcore.io;


import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import junit.framework.TestCase;


public class StrictLineReaderTest extends TestCase {
    public void testLineReaderConsistencyWithReadAsciiLine() {
        try {
            // Testing with LineReader buffer capacity 32 to check some corner cases.
            StrictLineReader lineReader = new StrictLineReader(createTestInputStream(), 32, StandardCharsets.US_ASCII);
            InputStream refStream = createTestInputStream();
            while (true) {
                try {
                    String refLine = Streams.readAsciiLine(refStream);
                    try {
                        String line = lineReader.readLine();
                        if (!(refLine.equals(line))) {
                            TestCase.fail((((("line (\"" + line) + "\") differs from expected (\"") + refLine) + "\")."));
                        }
                    } catch (EOFException eof) {
                        TestCase.fail("line reader threw EOFException too early.");
                    }
                } catch (EOFException refEof) {
                    try {
                        lineReader.readLine();
                        TestCase.fail("line reader didn't throw the expected EOFException.");
                    } catch (EOFException eof) {
                        // OK
                        break;
                    }
                }
            } 
            refStream.close();
            lineReader.close();
        } catch (IOException ioe) {
            TestCase.fail(("Unexpected IOException " + (ioe.toString())));
        }
    }
}

