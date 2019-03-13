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
package com.mpush.core.security;


import CipherBox.I;
import java.util.Arrays;
import org.junit.Test;


/**
 * Created by ohun on 2015/12/25.
 */
public class CipherBoxTest {
    @Test
    public void testGetPublicKey() throws Exception {
        for (int i = 0; i < 1000; i++) {
            byte[] clientKey = I.randomAESKey();
            byte[] serverKey = I.randomAESKey();
            byte[] sessionKey = I.mixKey(clientKey, serverKey);
            // System.out.println("clientKey:" + Arrays.toString(clientKey));
            // System.out.println("serverKey:" + Arrays.toString(serverKey));
            System.out.println(("sessionKey:" + (Arrays.toString(sessionKey))));
        }
    }
}

