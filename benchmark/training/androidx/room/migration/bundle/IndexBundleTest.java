/**
 * Copyright 2017 The Android Open Source Project
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


import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static IndexBundle.DEFAULT_PREFIX;


@RunWith(JUnit4.class)
public class IndexBundleTest {
    @Test
    public void schemaEquality_same_equal() {
        IndexBundle bundle = new IndexBundle("index1", false, Arrays.asList("col1", "col2"), "sql");
        IndexBundle other = new IndexBundle("index1", false, Arrays.asList("col1", "col2"), "sql");
        MatcherAssert.assertThat(bundle.isSchemaEqual(other), CoreMatchers.is(true));
    }

    @Test
    public void schemaEquality_diffName_notEqual() {
        IndexBundle bundle = new IndexBundle("index1", false, Arrays.asList("col1", "col2"), "sql");
        IndexBundle other = new IndexBundle("index3", false, Arrays.asList("col1", "col2"), "sql");
        MatcherAssert.assertThat(bundle.isSchemaEqual(other), CoreMatchers.is(false));
    }

    @Test
    public void schemaEquality_diffGenericName_equal() {
        IndexBundle bundle = new IndexBundle(((DEFAULT_PREFIX) + "x"), false, Arrays.asList("col1", "col2"), "sql");
        IndexBundle other = new IndexBundle(((DEFAULT_PREFIX) + "y"), false, Arrays.asList("col1", "col2"), "sql");
        MatcherAssert.assertThat(bundle.isSchemaEqual(other), CoreMatchers.is(true));
    }

    @Test
    public void schemaEquality_diffUnique_notEqual() {
        IndexBundle bundle = new IndexBundle("index1", false, Arrays.asList("col1", "col2"), "sql");
        IndexBundle other = new IndexBundle("index1", true, Arrays.asList("col1", "col2"), "sql");
        MatcherAssert.assertThat(bundle.isSchemaEqual(other), CoreMatchers.is(false));
    }

    @Test
    public void schemaEquality_diffColumns_notEqual() {
        IndexBundle bundle = new IndexBundle("index1", false, Arrays.asList("col1", "col2"), "sql");
        IndexBundle other = new IndexBundle("index1", false, Arrays.asList("col2", "col1"), "sql");
        MatcherAssert.assertThat(bundle.isSchemaEqual(other), CoreMatchers.is(false));
    }

    @Test
    public void schemaEquality_diffSql_equal() {
        IndexBundle bundle = new IndexBundle("index1", false, Arrays.asList("col1", "col2"), "sql");
        IndexBundle other = new IndexBundle("index1", false, Arrays.asList("col1", "col2"), "sql22");
        MatcherAssert.assertThat(bundle.isSchemaEqual(other), CoreMatchers.is(true));
    }
}

