/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.api.resources;


import Qualifiers.APP;
import Qualifiers.DIRECTORY;
import Qualifiers.FILE;
import Qualifiers.PROJECT;
import Qualifiers.SUBVIEW;
import Qualifiers.VIEW;
import org.junit.Test;


public class ResourceTypesTest {
    private ResourceTypeTree viewsTree = ResourceTypeTree.builder().addType(ResourceType.builder(VIEW).setProperty("supportsMeasureFilters", "true").build()).addType(ResourceType.builder(SUBVIEW).build()).addRelations(VIEW, SUBVIEW).addRelations(SUBVIEW, PROJECT).build();

    private ResourceTypeTree applicationTree = ResourceTypeTree.builder().addType(ResourceType.builder(APP).setProperty("supportsMeasureFilters", "true").build()).addRelations(APP, PROJECT).build();

    private ResourceTypeTree defaultTree = ResourceTypeTree.builder().addType(ResourceType.builder(PROJECT).setProperty("supportsMeasureFilters", "true").build()).addType(ResourceType.builder(DIRECTORY).build()).addType(ResourceType.builder(FILE).build()).addRelations(PROJECT, DIRECTORY).addRelations(DIRECTORY, FILE).build();

    private ResourceTypes types = new ResourceTypes(new ResourceTypeTree[]{ defaultTree, viewsTree, applicationTree });

    @Test
    public void get() {
        assertThat(types.get(PROJECT).getQualifier()).isEqualTo(PROJECT);
        // does not return null
        assertThat(types.get("xxx").getQualifier()).isEqualTo("xxx");
    }

    @Test
    public void get_all() {
        assertThat(ResourceTypesTest.qualifiers(types.getAll())).containsExactly(PROJECT, DIRECTORY, FILE, VIEW, SUBVIEW, APP);
    }

    @Test
    public void get_roots() {
        assertThat(ResourceTypesTest.qualifiers(types.getRoots())).containsOnly(PROJECT, VIEW, APP);
    }

    @Test
    public void get_leaves_qualifiers() {
        assertThat(types.getLeavesQualifiers(PROJECT)).containsExactly(FILE);
        assertThat(types.getLeavesQualifiers(DIRECTORY)).containsExactly(FILE);
        assertThat(types.getLeavesQualifiers(VIEW)).containsExactly(PROJECT);
        assertThat(types.getLeavesQualifiers(APP)).containsExactly(PROJECT);
        assertThat(types.getLeavesQualifiers("xxx")).isEmpty();
    }

    @Test(expected = IllegalStateException.class)
    public void fail_on_duplicated_qualifier() {
        ResourceTypeTree tree1 = ResourceTypeTree.builder().addType(ResourceType.builder("foo").build()).build();
        ResourceTypeTree tree2 = ResourceTypeTree.builder().addType(ResourceType.builder("foo").build()).build();
        new ResourceTypes(new ResourceTypeTree[]{ tree1, tree2 });
    }

    @Test
    public void isQualifierPresent() {
        assertThat(types.isQualifierPresent(APP)).isTrue();
        assertThat(types.isQualifierPresent(VIEW)).isTrue();
        assertThat(types.isQualifierPresent("XXXX")).isFalse();
    }
}

