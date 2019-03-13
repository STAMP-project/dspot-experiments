/**
 * Copyright 2014-2019 Real Logic Ltd.
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
package io.aeron.driver;


import MediaDriver.Context;
import ThreadingMode.SHARED;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class MediaDriverTest {
    @Test
    public void shouldPrintConfigOnStart() {
        final MediaDriver.Context context = new MediaDriver.Context().threadingMode(SHARED).dirDeleteOnStart(true).printConfigurationOnStart(true);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(os);
        final PrintStream out = System.out;
        System.setOut(printStream);
        try (MediaDriver ignore = MediaDriver.launch(context)) {
            final String result = os.toString();
            Assert.assertThat(result, Matchers.containsString("printConfigurationOnStart=true"));
        } finally {
            System.setOut(out);
            context.deleteAeronDirectory();
        }
    }
}

