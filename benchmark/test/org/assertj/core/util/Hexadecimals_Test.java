/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.util;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Mariusz Smykula
 */
public class Hexadecimals_Test {
    @Test
    public void should_return_hexadecimal_representation_of_byte() {
        Assertions.assertThat(Hexadecimals.byteToHexString(((byte) (0)))).isEqualTo("00");
        Assertions.assertThat(Hexadecimals.byteToHexString(((byte) (255)))).isEqualTo("FF");
        Assertions.assertThat(Hexadecimals.byteToHexString(((byte) (162)))).isEqualTo("A2");
    }
}

