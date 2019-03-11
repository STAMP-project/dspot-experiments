/**
 * Copyright 2012-2018 Chronicle Map Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eg;


import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.core.values.IntValue;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.values.Values;
import org.junit.Test;


public class WordCountTest {
    static String[] words;

    static int expectedSize;

    static {
        try {
            // english version of war and peace ->  ascii
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            InputStream zippedIS = cl.getResourceAsStream("war_and_peace.txt.gz");
            GZIPInputStream binaryIS = new GZIPInputStream(zippedIS);
            String fullText = new String(ByteStreams.toByteArray(binaryIS), StandardCharsets.UTF_8);
            WordCountTest.words = fullText.split("\\s+");
            WordCountTest.expectedSize = ((int) (Arrays.stream(WordCountTest.words).distinct().count()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void wordCountTest() throws IOException {
        try (ChronicleMap<CharSequence, IntValue> map = // average word is 7 ascii bytes long (text in english)
        ChronicleMap.of(CharSequence.class, IntValue.class).averageKeySize(7).entries(WordCountTest.expectedSize).create()) {
            IntValue v = Values.newNativeReference(IntValue.class);
            for (String word : WordCountTest.words) {
                try (Closeable ignored = map.acquireContext(word, v)) {
                    v.addValue(1);
                }
            }
        }
    }
}

