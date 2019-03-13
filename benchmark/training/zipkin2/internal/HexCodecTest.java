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
package zipkin2.internal;


import org.junit.Test;


public class HexCodecTest {
    @Test
    public void lowerHexToUnsignedLong_downgrades128bitIdsByDroppingHighBits() {
        assertThat(HexCodec.lowerHexToUnsignedLong("463ac35c9f6413ad48485a3953bb6124")).isEqualTo(HexCodec.lowerHexToUnsignedLong("48485a3953bb6124"));
    }

    @Test
    public void lowerHexToUnsignedLongTest() {
        assertThat(HexCodec.lowerHexToUnsignedLong("ffffffffffffffff")).isEqualTo((-1));
        assertThat(HexCodec.lowerHexToUnsignedLong("0")).isEqualTo(0);
        assertThat(HexCodec.lowerHexToUnsignedLong(Long.toHexString(Long.MAX_VALUE))).isEqualTo(Long.MAX_VALUE);
        try {
            HexCodec.lowerHexToUnsignedLong("fffffffffffffffffffffffffffffffff");// too long

            failBecauseExceptionWasNotThrown(NumberFormatException.class);
        } catch (NumberFormatException e) {
        }
        try {
            HexCodec.lowerHexToUnsignedLong("");// too short

            failBecauseExceptionWasNotThrown(NumberFormatException.class);
        } catch (NumberFormatException e) {
        }
        try {
            HexCodec.lowerHexToUnsignedLong("rs");// bad charset

            failBecauseExceptionWasNotThrown(NumberFormatException.class);
        } catch (NumberFormatException e) {
        }
        try {
            HexCodec.lowerHexToUnsignedLong("48485A3953BB6124");// uppercase

            failBecauseExceptionWasNotThrown(NumberFormatException.class);
        } catch (NumberFormatException e) {
            assertThat(e).hasMessage("48485A3953BB6124 should be a 1 to 32 character lower-hex string with no prefix");
        }
    }
}

