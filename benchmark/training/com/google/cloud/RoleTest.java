/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud;


import org.junit.Test;


public class RoleTest {
    private static final Role VIEWER = Role.of("viewer");

    private static final Role EDITOR = Role.of("editor");

    private static final Role OWNER = Role.of("owner");

    @Test
    public void testOf() {
        assertThat(RoleTest.VIEWER.getValue()).isEqualTo("roles/viewer");
        assertThat(RoleTest.EDITOR.getValue()).isEqualTo("roles/editor");
        assertThat(RoleTest.OWNER.getValue()).isEqualTo("roles/owner");
        compareRoles(RoleTest.VIEWER, Role.of("roles/viewer"));
        compareRoles(RoleTest.EDITOR, Role.of("roles/editor"));
        compareRoles(RoleTest.OWNER, Role.of("roles/owner"));
        String customRole = "projects/foo/roles/bar";
        assertThat(Role.of(customRole).getValue()).isEqualTo(customRole);
    }

    @Test
    public void testViewer() {
        assertThat(Role.viewer().getValue()).isEqualTo("roles/viewer");
    }

    @Test
    public void testEditor() {
        assertThat(Role.editor().getValue()).isEqualTo("roles/editor");
    }

    @Test
    public void testOwner() {
        assertThat(Role.owner().getValue()).isEqualTo("roles/owner");
    }

    @Test(expected = NullPointerException.class)
    public void testOfNullValue() {
        Role.of(null);
    }
}

