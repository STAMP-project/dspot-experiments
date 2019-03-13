/**
 * (C) Copyright 2015-2016 the original author or authors.
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
 *
 * Contributors:
 *   ohun@live.cn (??)
 */
package com.mpush.tools.crypto;


import Constants.UTF_8;
import com.mpush.api.Constants;
import java.util.Random;
import org.junit.Test;


/**
 * Created by ohun on 2015/12/25.
 */
public class AESUtilsTest {
    @Test
    public void testEncryptDES() throws Exception {
        String data = "???????????????????";
        System.out.println(("\u539f\u6587\uff1a\n" + data));
        byte[] key = new byte[16];
        new Random().nextBytes(key);
        byte[] d1 = AESUtils.encrypt(data.getBytes(UTF_8), key, key);
        System.out.println(("\u52a0\u5bc6\u540e\uff1a\n" + (new String(d1))));
        byte[] d2 = AESUtils.decrypt(d1, key, key);
        System.out.println(("\u89e3\u5bc6\u540e\uff1a\n" + (new String(d2, Constants.UTF_8))));
    }
}

