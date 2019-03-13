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


import java.net.URL;
import org.junit.Test;


/**
 * Created by ohun on 2015/12/25.
 *
 * @author ohun@live.cn
 */
public class RSAUtilsTest {
    String publicKey;

    String privateKey;

    @Test
    public void testGetKeys() throws Exception {
        String source = "??????RSA??????????";
        byte[] data = source.getBytes();
        byte[] encodedData = RSAUtils.encryptByPublicKey(data, publicKey);
        System.out.println(("\u52a0\u5bc6\u540e:\n" + (new String(encodedData))));
        byte[] decodedData = RSAUtils.decryptByPrivateKey(encodedData, privateKey);
        String target = new String(decodedData);
        System.out.println(("\u89e3\u5bc6\u540e:\n" + target));
    }

    @Test
    public void testGetPrivateKey() throws Exception {
        System.out.println(("\u516c\u94a5: \n\r" + (publicKey)));
        System.out.println(("\u79c1\u94a5\uff1a \n\r" + (privateKey)));
    }

    @Test
    public void testSign() throws Exception {
        String source = "??????RSA??????????";
        System.out.println("===========??????????=======================");
        System.out.println(("\u539f\u6587\u5b57:\n" + source));
        byte[] data = source.getBytes();
        byte[] encodedData = RSAUtils.encryptByPrivateKey(data, privateKey);
        System.out.println(("\u52a0\u5bc6\u540e:\n" + (new String(encodedData))));
        byte[] decodedData = RSAUtils.decryptByPublicKey(encodedData, publicKey);
        String target = new String(decodedData);
        System.out.println(("\u89e3\u5bc6\u540e:\n" + target));
        System.out.println("============????????????===================");
        String sign = RSAUtils.sign(encodedData, privateKey);
        System.out.println(("\u7b7e\u540d:\n" + sign));
        boolean status = RSAUtils.verify(encodedData, publicKey, sign);
        System.out.println(("\u9a8c\u8bc1\u7ed3\u679c:\n" + status));
    }

    @Test
    public void test1() throws Exception {
        System.err.println("??????????");
        String source = "???????????????????????????";
        System.out.println(("\r\u52a0\u5bc6\u524d\u6587\u5b57\uff1a\r\n" + source));
        byte[] data = source.getBytes();
        byte[] encodedData = RSAUtils.encryptByPublicKey(data, publicKey);
        System.out.println(("\u52a0\u5bc6\u540e\u6587\u5b57\uff1a\r\n" + (new String(encodedData))));
        byte[] decodedData = RSAUtils.decryptByPrivateKey(encodedData, privateKey);
        String target = new String(decodedData);
        System.out.println(((((("\u89e3\u5bc6\u540e\u6587\u5b57: \r\n" + target) + ", ????:") + (data.length)) + ", ????:") + (encodedData.length)));
    }

    @Test
    public void testGetPrivateKey1() throws Exception {
        URL url = this.getClass().getResource("/");
        System.out.println(url.getPath());
        System.out.println(url.getFile());
    }
}

