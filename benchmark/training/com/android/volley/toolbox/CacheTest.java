/**
 * Copyright (C) 2015 The Android Open Source Project
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
package com.android.volley.toolbox;


import Cache.Entry;
import com.android.volley.Cache;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class CacheTest {
    @Test
    public void publicMethods() throws Exception {
        // Catch-all test to find API-breaking changes.
        Assert.assertNotNull(Cache.class.getMethod("get", String.class));
        Assert.assertNotNull(Cache.class.getMethod("put", String.class, Entry.class));
        Assert.assertNotNull(Cache.class.getMethod("initialize"));
        Assert.assertNotNull(Cache.class.getMethod("invalidate", String.class, boolean.class));
        Assert.assertNotNull(Cache.class.getMethod("remove", String.class));
        Assert.assertNotNull(Cache.class.getMethod("clear"));
    }
}

