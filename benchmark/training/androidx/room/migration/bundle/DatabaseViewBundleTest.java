/**
 * Copyright 2018 The Android Open Source Project
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
package androidx.room.migration.bundle;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class DatabaseViewBundleTest {
    @Test
    public void basic() {
        DatabaseViewBundle bundle = new DatabaseViewBundle("abc", "def");
        DatabaseViewBundle other = new DatabaseViewBundle("abc", "def");
        Assert.assertThat(bundle.isSchemaEqual(other), CoreMatchers.is(true));
        Assert.assertThat(bundle.getViewName(), CoreMatchers.is(CoreMatchers.equalTo("abc")));
        Assert.assertThat(bundle.getCreateSql(), CoreMatchers.is(CoreMatchers.equalTo("def")));
    }
}

