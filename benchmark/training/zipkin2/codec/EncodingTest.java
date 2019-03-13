/**
 * Copyright 2015-2018 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin2.codec;


import Encoding.JSON;
import Encoding.PROTO3;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;


public class EncodingTest {
    @Test
    public void emptyList_json() {
        List<byte[]> encoded = Arrays.asList();
        /* [] */
        assertThat(JSON.listSizeInBytes(encoded)).isEqualTo(2);
    }

    @Test
    public void singletonList_json() {
        List<byte[]> encoded = Arrays.asList(new byte[10]);
        assertThat(JSON.listSizeInBytes(encoded.get(0).length)).isEqualTo((2/* [] */
         + 10));
        assertThat(JSON.listSizeInBytes(encoded)).isEqualTo((2/* [] */
         + 10));
    }

    @Test
    public void multiItemList_json() {
        List<byte[]> encoded = Arrays.asList(new byte[3], new byte[4], new byte[128]);
        assertThat(JSON.listSizeInBytes(encoded)).isEqualTo((((((2/* [] */
         + 3) + 1)/* , */
         + 4) + 1)/* , */
         + 128));
    }

    @Test
    public void emptyList_proto3() {
        List<byte[]> encoded = Arrays.asList();
        assertThat(PROTO3.listSizeInBytes(encoded)).isEqualTo(0);
    }

    // an entry in a list is a repeated field
    @Test
    public void singletonList_proto3() {
        List<byte[]> encoded = Arrays.asList(new byte[10]);
        assertThat(PROTO3.listSizeInBytes(encoded.get(0).length)).isEqualTo(10);
        assertThat(PROTO3.listSizeInBytes(encoded)).isEqualTo(10);
    }

    // per ListOfSpans in zipkin2.proto
    @Test
    public void multiItemList_proto3() {
        List<byte[]> encoded = Arrays.asList(new byte[3], new byte[4], new byte[128]);
        assertThat(PROTO3.listSizeInBytes(encoded)).isEqualTo(((3 + 4) + 128));
    }
}

