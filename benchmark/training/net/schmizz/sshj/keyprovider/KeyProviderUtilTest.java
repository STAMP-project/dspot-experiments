/**
 * Copyright (C)2009 - SSHJ Contributors
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
package net.schmizz.sshj.keyprovider;


import KeyFormat.OpenSSH;
import KeyFormat.PKCS5;
import KeyFormat.PKCS8;
import KeyFormat.PuTTY;
import java.io.File;
import java.io.IOException;
import net.schmizz.sshj.userauth.keyprovider.KeyFormat;
import net.schmizz.sshj.userauth.keyprovider.KeyProviderUtil;
import org.junit.Assert;
import org.junit.Test;


public class KeyProviderUtilTest {
    private static final File ROOT = new File("src/test/resources/keyformats");

    @Test
    public void testOpenSsh() throws IOException {
        KeyFormat format = KeyProviderUtil.detectKeyFileFormat(new File(KeyProviderUtilTest.ROOT, "openssh"));
        Assert.assertEquals(OpenSSH, format);
    }

    @Test
    public void testPkcs5() throws IOException {
        KeyFormat format = KeyProviderUtil.detectKeyFileFormat(new File(KeyProviderUtilTest.ROOT, "pkcs5"));
        Assert.assertEquals(PKCS5, format);
    }

    @Test
    public void testPkcs8() throws IOException {
        KeyFormat format = KeyProviderUtil.detectKeyFileFormat(new File(KeyProviderUtilTest.ROOT, "pkcs8"));
        Assert.assertEquals(PKCS8, format);
    }

    @Test
    public void testPutty() throws IOException {
        KeyFormat format = KeyProviderUtil.detectKeyFileFormat(new File(KeyProviderUtilTest.ROOT, "putty"));
        Assert.assertEquals(PuTTY, format);
    }

    @Test
    public void testSkipsBlankLines() throws IOException {
        KeyFormat format = KeyProviderUtil.detectKeyFileFormat(new File(KeyProviderUtilTest.ROOT, "pkcs8-blanks"));
        Assert.assertEquals(PKCS8, format);
    }

    @Test
    public void testOpenSshSigned() throws IOException {
        KeyFormat format = KeyProviderUtil.detectKeyFileFormat(new File(KeyProviderUtilTest.ROOT, "signed"));
        Assert.assertEquals(OpenSSH, format);
    }
}

