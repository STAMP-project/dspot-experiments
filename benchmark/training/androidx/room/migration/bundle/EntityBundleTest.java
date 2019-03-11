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
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
@RunWith(JUnit4.class)
public class EntityBundleTest {
    @Test
    public void schemaEquality_same_equal() {
        EntityBundle bundle = new EntityBundle("foo", "sq", Arrays.asList(createFieldBundle("foo"), createFieldBundle("bar")), new PrimaryKeyBundle(false, Arrays.asList("foo")), Arrays.asList(createIndexBundle("foo")), Arrays.asList(createForeignKeyBundle("bar", "foo")));
        EntityBundle other = new EntityBundle("foo", "sq", Arrays.asList(createFieldBundle("foo"), createFieldBundle("bar")), new PrimaryKeyBundle(false, Arrays.asList("foo")), Arrays.asList(createIndexBundle("foo")), Arrays.asList(createForeignKeyBundle("bar", "foo")));
        MatcherAssert.assertThat(bundle.isSchemaEqual(other), CoreMatchers.is(true));
    }

    @Test
    public void schemaEquality_reorderedFields_equal() {
        EntityBundle bundle = new EntityBundle("foo", "sq", Arrays.asList(createFieldBundle("foo"), createFieldBundle("bar")), new PrimaryKeyBundle(false, Arrays.asList("foo")), Collections.<IndexBundle>emptyList(), Collections.<ForeignKeyBundle>emptyList());
        EntityBundle other = new EntityBundle("foo", "sq", Arrays.asList(createFieldBundle("bar"), createFieldBundle("foo")), new PrimaryKeyBundle(false, Arrays.asList("foo")), Collections.<IndexBundle>emptyList(), Collections.<ForeignKeyBundle>emptyList());
        MatcherAssert.assertThat(bundle.isSchemaEqual(other), CoreMatchers.is(true));
    }

    @Test
    public void schemaEquality_diffFields_notEqual() {
        EntityBundle bundle = new EntityBundle("foo", "sq", Arrays.asList(createFieldBundle("foo"), createFieldBundle("bar")), new PrimaryKeyBundle(false, Arrays.asList("foo")), Collections.<IndexBundle>emptyList(), Collections.<ForeignKeyBundle>emptyList());
        EntityBundle other = new EntityBundle("foo", "sq", Arrays.asList(createFieldBundle("foo2"), createFieldBundle("bar")), new PrimaryKeyBundle(false, Arrays.asList("foo")), Collections.<IndexBundle>emptyList(), Collections.<ForeignKeyBundle>emptyList());
        MatcherAssert.assertThat(bundle.isSchemaEqual(other), CoreMatchers.is(false));
    }

    @Test
    public void schemaEquality_reorderedForeignKeys_equal() {
        EntityBundle bundle = new EntityBundle("foo", "sq", Collections.<FieldBundle>emptyList(), new PrimaryKeyBundle(false, Arrays.asList("foo")), Collections.<IndexBundle>emptyList(), Arrays.asList(createForeignKeyBundle("x", "y"), createForeignKeyBundle("bar", "foo")));
        EntityBundle other = new EntityBundle("foo", "sq", Collections.<FieldBundle>emptyList(), new PrimaryKeyBundle(false, Arrays.asList("foo")), Collections.<IndexBundle>emptyList(), Arrays.asList(createForeignKeyBundle("bar", "foo"), createForeignKeyBundle("x", "y")));
        MatcherAssert.assertThat(bundle.isSchemaEqual(other), CoreMatchers.is(true));
    }

    @Test
    public void schemaEquality_diffForeignKeys_notEqual() {
        EntityBundle bundle = new EntityBundle("foo", "sq", Collections.<FieldBundle>emptyList(), new PrimaryKeyBundle(false, Arrays.asList("foo")), Collections.<IndexBundle>emptyList(), Arrays.asList(createForeignKeyBundle("bar", "foo")));
        EntityBundle other = new EntityBundle("foo", "sq", Collections.<FieldBundle>emptyList(), new PrimaryKeyBundle(false, Arrays.asList("foo")), Collections.<IndexBundle>emptyList(), Arrays.asList(createForeignKeyBundle("bar2", "foo")));
        MatcherAssert.assertThat(bundle.isSchemaEqual(other), CoreMatchers.is(false));
    }

    @Test
    public void schemaEquality_reorderedIndices_equal() {
        EntityBundle bundle = new EntityBundle("foo", "sq", Collections.<FieldBundle>emptyList(), new PrimaryKeyBundle(false, Arrays.asList("foo")), Arrays.asList(createIndexBundle("foo"), createIndexBundle("baz")), Collections.<ForeignKeyBundle>emptyList());
        EntityBundle other = new EntityBundle("foo", "sq", Collections.<FieldBundle>emptyList(), new PrimaryKeyBundle(false, Arrays.asList("foo")), Arrays.asList(createIndexBundle("baz"), createIndexBundle("foo")), Collections.<ForeignKeyBundle>emptyList());
        MatcherAssert.assertThat(bundle.isSchemaEqual(other), CoreMatchers.is(true));
    }

    @Test
    public void schemaEquality_diffIndices_notEqual() {
        EntityBundle bundle = new EntityBundle("foo", "sq", Collections.<FieldBundle>emptyList(), new PrimaryKeyBundle(false, Arrays.asList("foo")), Arrays.asList(createIndexBundle("foo")), Collections.<ForeignKeyBundle>emptyList());
        EntityBundle other = new EntityBundle("foo", "sq", Collections.<FieldBundle>emptyList(), new PrimaryKeyBundle(false, Arrays.asList("foo")), Arrays.asList(createIndexBundle("foo2")), Collections.<ForeignKeyBundle>emptyList());
        MatcherAssert.assertThat(bundle.isSchemaEqual(other), CoreMatchers.is(false));
    }
}

