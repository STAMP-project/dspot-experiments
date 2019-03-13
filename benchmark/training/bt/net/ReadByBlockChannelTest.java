/**
 * Copyright (c) 2016?2017 Andrei Tomashpolskiy and individual contributors.
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
package bt.net;


import java.io.IOException;
import org.junit.Test;


public class ReadByBlockChannelTest {
    @Test
    public void testChannel_NoPartialReads() throws IOException {
        int[] limits = new int[]{ 100, 200, 300 };
        int[] expectedReads = new int[]{ 100, 100, 100 };
        testChannel(limits, expectedReads);
    }

    @Test
    public void testChannel_PartialReads() throws IOException {
        int[] limits = new int[]{ 0, 75, 100, 150, 250, 250, 300 };
        int[] expectedReads = new int[]{ 0, 75, 25, 50, 50, 50, 50 };
        testChannel(limits, expectedReads);
    }
}

