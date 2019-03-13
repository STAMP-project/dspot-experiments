/**
 * Copyright 2019 Hippo Seven
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
package com.hippo.ehviewer.client;


import java.io.IOException;
import java.io.InputStream;
import okio.BufferedSource;
import okio.Okio;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class EhTagDatabaseTest {
    @Test
    public void readTheList() throws IOException {
        InputStream resource = EhTagDatabaseTest.class.getResourceAsStream("EhTagDatabaseTest");
        EhTagDatabase db;
        try (BufferedSource source = Okio.buffer(Okio.source(resource))) {
            db = new EhTagDatabase("EhTagDatabaseTest", source);
        }
        Assert.assertEquals("a", db.getTranslation("1"));
        Assert.assertEquals("ab", db.getTranslation("12"));
        Assert.assertEquals("abc", db.getTranslation("123"));
        Assert.assertEquals("abcd", db.getTranslation("1234"));
        Assert.assertEquals("1", db.getTranslation("a"));
        Assert.assertEquals("12", db.getTranslation("ab"));
        Assert.assertEquals("123", db.getTranslation("abc"));
        Assert.assertEquals("1234", db.getTranslation("abcd"));
        Assert.assertNull(db.getTranslation("21"));
    }
}

