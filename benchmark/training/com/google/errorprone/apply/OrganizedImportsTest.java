/**
 * Copyright 2017 The Error Prone Authors.
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
package com.google.errorprone.apply;


import ImportOrganizer.OrganizedImports;
import com.google.common.primitives.Booleans;
import com.google.errorprone.apply.ImportOrganizer.Import;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 *
 */
@RunWith(JUnit4.class)
public class OrganizedImportsTest {
    private static final Comparator<Import> IMPORT_COMPARATOR = Comparator.comparing(Import::isStatic, Booleans.falseFirst()).thenComparing(Import::getType);

    @Test
    public void emptyList() {
        ImportOrganizer.OrganizedImports organizedImports = new ImportOrganizer.OrganizedImports();
        assertThat(organizedImports.asImportBlock()).isEmpty();
    }

    @Test
    public void singleGroup() {
        Map<String, Set<Import>> groups = new TreeMap<>();
        groups.put("first", buildSortedImportSet(Import.importOf("import first")));
        ImportOrganizer.OrganizedImports organizedImports = new ImportOrganizer.OrganizedImports().addGroups(groups, groups.keySet());
        assertThat(organizedImports.asImportBlock()).isEqualTo("import first;\n");
    }

    @Test
    public void multipleGroups() {
        Map<String, Set<Import>> groups = new TreeMap<>();
        groups.put("first", buildSortedImportSet(Import.importOf("import first")));
        groups.put("second", buildSortedImportSet(Import.importOf("import second")));
        ImportOrganizer.OrganizedImports organizedImports = new ImportOrganizer.OrganizedImports().addGroups(groups, groups.keySet());
        assertThat(organizedImports.asImportBlock()).isEqualTo("import first;\n\nimport second;\n");
    }

    @Test
    public void importCount() {
        Map<String, Set<Import>> groups = new TreeMap<>();
        groups.put("first", buildSortedImportSet(Import.importOf("import first")));
        groups.put("second", buildSortedImportSet(Import.importOf("import second")));
        ImportOrganizer.OrganizedImports organizedImports = new ImportOrganizer.OrganizedImports().addGroups(groups, groups.keySet());
        assertThat(organizedImports.getImportCount()).isEqualTo(2);
    }
}

