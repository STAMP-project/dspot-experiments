/**
 * Copyright (C) 2014 Markus Junginger, greenrobot (http://greenrobot.de)
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
package org.greenrobot.essentials;


import javax.xml.bind.DatatypeConverter;
import org.junit.Assert;
import org.junit.Test;


public class StringUtilsJvmTest {
    @Test
    public void testHexBig() {
        for (int i = 0; i < (256 * 256); i++) {
            byte[] bytes = new byte[]{ ((byte) (i >> 8)), ((byte) (i)) };
            String hexExpected = DatatypeConverter.printHexBinary(bytes);
            String hex = StringUtils.hex(bytes);
            Assert.assertEquals(hexExpected, hex);
            byte[] bytes2 = StringUtils.parseHex(hex);
            Assert.assertEquals(bytes[0], bytes2[0]);
            Assert.assertEquals(bytes[1], bytes2[1]);
        }
    }
}

