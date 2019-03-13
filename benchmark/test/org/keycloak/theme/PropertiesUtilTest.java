/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
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
package org.keycloak.theme;


import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:wadahiro@gmail.com">Hiroyuki Wada</a>
 */
public class PropertiesUtilTest {
    @Test
    public void testDetectEncoding() throws Exception {
        Charset encoding = PropertiesUtil.detectEncoding(new ByteArrayInputStream("# encoding: utf-8\nkey=value".getBytes()));
        Assert.assertEquals(Charset.forName("utf-8"), encoding);
        encoding = PropertiesUtil.detectEncoding(new ByteArrayInputStream("# encoding: Shift_JIS\nkey=value".getBytes()));
        Assert.assertEquals(Charset.forName("Shift_JIS"), encoding);
    }

    @Test
    public void testDefaultEncoding() throws Exception {
        Charset encoding = PropertiesUtil.detectEncoding(new ByteArrayInputStream("key=value".getBytes()));
        Assert.assertEquals(Charset.forName("ISO-8859-1"), encoding);
        encoding = PropertiesUtil.detectEncoding(new ByteArrayInputStream("# encoding: unknown\nkey=value".getBytes()));
        Assert.assertEquals(Charset.forName("ISO-8859-1"), encoding);
        encoding = PropertiesUtil.detectEncoding(new ByteArrayInputStream("\n# encoding: utf-8\nkey=value".getBytes()));
        Assert.assertEquals(Charset.forName("ISO-8859-1"), encoding);
        encoding = PropertiesUtil.detectEncoding(new ByteArrayInputStream("".getBytes()));
        Assert.assertEquals(Charset.forName("ISO-8859-1"), encoding);
    }
}

