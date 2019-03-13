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
package org.sonar.server.duplication.ws;


import DuplicationsParser.Block;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;


public class ShowResponseBuilderTest {
    @Rule
    public DbTester db = DbTester.create();

    private ShowResponseBuilder underTest = new ShowResponseBuilder(db.getDbClient().componentDao());

    @Test
    public void write_duplications() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto module = db.components().insertComponent(newModuleDto(project));
        ComponentDto file1 = db.components().insertComponent(newFileDto(module));
        ComponentDto file2 = db.components().insertComponent(newFileDto(module));
        List<DuplicationsParser.Block> blocks = Lists.newArrayList();
        blocks.add(new DuplicationsParser.Block(Lists.newArrayList(Duplication.newComponent(file1, 57, 12), Duplication.newComponent(file2, 73, 12))));
        test(blocks, null, null, ((((((((((((((((((((((((((((((((((((((((("{\n" + (((((((((((((("  \"duplications\": [\n" + "    {\n") + "      \"blocks\": [\n") + "        {\n") + "          \"from\": 57, \"size\": 12, \"_ref\": \"1\"\n") + "        },\n") + "        {\n") + "          \"from\": 73, \"size\": 12, \"_ref\": \"2\"\n") + "        }\n") + "      ]\n") + "    },") + "  ],\n") + "  \"files\": {\n") + "    \"1\": {\n") + "      \"key\": \"")) + (file1.getKey())) + "\",\n") + "      \"name\": \"") + (file1.longName())) + "\",\n") + "      \"project\": \"") + (project.getKey())) + "\",\n") + "      \"projectName\": \"") + (project.longName())) + "\",\n") + "      \"subProject\": \"") + (module.getKey())) + "\",\n") + "      \"subProjectName\": \"") + (module.longName())) + "\"\n") + "    },\n") + "    \"2\": {\n") + "      \"key\": \"") + (file2.getKey())) + "\",\n") + "      \"name\": \"") + (file2.longName())) + "\",\n") + "      \"project\": \"") + (project.getKey())) + "\",\n") + "      \"projectName\": \"") + (project.longName())) + "\",\n") + "      \"subProject\": \"") + (module.getKey())) + "\",\n") + "      \"subProjectName\": \"") + (module.longName())) + "\"\n") + "    }\n") + "  }") + "}"));
    }

    @Test
    public void write_duplications_without_sub_project() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto file1 = db.components().insertComponent(newFileDto(project));
        ComponentDto file2 = db.components().insertComponent(newFileDto(project));
        List<DuplicationsParser.Block> blocks = Lists.newArrayList();
        blocks.add(new DuplicationsParser.Block(Lists.newArrayList(Duplication.newComponent(file1, 57, 12), Duplication.newComponent(file2, 73, 12))));
        test(blocks, null, null, ((((((((((((((((((((((((((((("{\n" + (((((((((((((("  \"duplications\": [\n" + "    {\n") + "      \"blocks\": [\n") + "        {\n") + "          \"from\": 57, \"size\": 12, \"_ref\": \"1\"\n") + "        },\n") + "        {\n") + "          \"from\": 73, \"size\": 12, \"_ref\": \"2\"\n") + "        }\n") + "      ]\n") + "    },") + "  ],\n") + "  \"files\": {\n") + "    \"1\": {\n") + "      \"key\": \"")) + (file1.getKey())) + "\",\n") + "      \"name\": \"") + (file1.longName())) + "\",\n") + "      \"project\": \"") + (project.getKey())) + "\",\n") + "      \"projectName\": \"") + (project.longName())) + "\",\n") + "    },\n") + "    \"2\": {\n") + "      \"key\": \"") + (file2.getKey())) + "\",\n") + "      \"name\": \"") + (file2.longName())) + "\",\n") + "      \"project\": \"") + (project.getKey())) + "\",\n") + "      \"projectName\": \"") + (project.longName())) + "\",\n") + "    }\n") + "  }") + "}"));
    }

    @Test
    public void write_duplications_with_a_removed_component() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto file = db.components().insertComponent(newFileDto(project));
        List<DuplicationsParser.Block> blocks = Lists.newArrayList();
        blocks.add(new DuplicationsParser.Block(// Duplication on a removed file
        Lists.newArrayList(Duplication.newComponent(file, 57, 12), Duplication.newRemovedComponent("key", 73, 12))));
        test(blocks, null, null, ((((((((((((((("{\n" + (((((((((((((("  \"duplications\": [\n" + "    {\n") + "      \"blocks\": [\n") + "        {\n") + "          \"from\": 57, \"size\": 12, \"_ref\": \"1\"\n") + "        },\n") + "        {\n") + "          \"from\": 73, \"size\": 12\n") + "        }\n") + "      ]\n") + "    },") + "  ],\n") + "  \"files\": {\n") + "    \"1\": {\n") + "      \"key\": \"")) + (file.getKey())) + "\",\n") + "      \"name\": \"") + (file.longName())) + "\",\n") + "      \"project\": \"") + (project.getKey())) + "\",\n") + "      \"projectName\": \"") + (project.longName())) + "\",\n") + "    }\n") + "  }") + "}"));
    }

    @Test
    public void write_duplications_with_a_component_without_details() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto file = db.components().insertComponent(newFileDto(project));
        List<DuplicationsParser.Block> blocks = Lists.newArrayList();
        blocks.add(new DuplicationsParser.Block(// Duplication on a file without details
        Lists.newArrayList(Duplication.newComponent(file, 57, 12), Duplication.newTextComponent("project:path/to/file", 73, 12))));
        test(blocks, null, null, ((((((((((((((((((("{\n" + (((((((((((((("  \"duplications\": [\n" + "    {\n") + "      \"blocks\": [\n") + "        {\n") + "          \"from\": 57, \"size\": 12, \"_ref\": \"1\"\n") + "        },\n") + "        {\n") + "          \"from\": 73, \"size\": 12\n") + "        }\n") + "      ]\n") + "    },") + "  ],\n") + "  \"files\": {\n") + "    \"1\": {\n") + "      \"key\": \"")) + (file.getKey())) + "\",\n") + "      \"name\": \"") + (file.longName())) + "\",\n") + "      \"project\": \"") + (project.getKey())) + "\",\n") + "      \"projectName\": \"") + (project.longName())) + "\",\n") + "    }\n") + "    \"2\": {\n") + "      \"key\": \"project:path/to/file\",\n") + "      \"name\": \"path/to/file\",\n") + "    }\n") + "  }") + "}"));
    }

    @Test
    public void write_duplications_on_branch() {
        ComponentDto project = db.components().insertMainBranch();
        ComponentDto branch = db.components().insertProjectBranch(project);
        ComponentDto file1 = db.components().insertComponent(newFileDto(branch));
        ComponentDto file2 = db.components().insertComponent(newFileDto(branch));
        List<DuplicationsParser.Block> blocks = Lists.newArrayList();
        blocks.add(new DuplicationsParser.Block(Lists.newArrayList(Duplication.newComponent(file1, 57, 12), Duplication.newComponent(file2, 73, 12))));
        test(blocks, branch.getBranch(), null, ((((((((((((((((((((((((((((((((((("{\n" + (((((((((((((("  \"duplications\": [\n" + "    {\n") + "      \"blocks\": [\n") + "        {\n") + "          \"from\": 57, \"size\": 12, \"_ref\": \"1\"\n") + "        },\n") + "        {\n") + "          \"from\": 73, \"size\": 12, \"_ref\": \"2\"\n") + "        }\n") + "      ]\n") + "    },") + "  ],\n") + "  \"files\": {\n") + "    \"1\": {\n") + "      \"key\": \"")) + (file1.getKey())) + "\",\n") + "      \"name\": \"") + (file1.longName())) + "\",\n") + "      \"project\": \"") + (branch.getKey())) + "\",\n") + "      \"projectName\": \"") + (branch.longName())) + "\",\n") + "      \"branch\": \"") + (branch.getBranch())) + "\",\n") + "    },\n") + "    \"2\": {\n") + "      \"key\": \"") + (file2.getKey())) + "\",\n") + "      \"name\": \"") + (file2.longName())) + "\",\n") + "      \"project\": \"") + (branch.getKey())) + "\",\n") + "      \"projectName\": \"") + (branch.longName())) + "\",\n") + "      \"branch\": \"") + (branch.getBranch())) + "\",\n") + "    }\n") + "  }") + "}"));
    }

    @Test
    public void write_duplications_on_pull_request() {
        ComponentDto project = db.components().insertMainBranch();
        ComponentDto pullRequest = db.components().insertProjectBranch(project, ( b) -> b.setBranchType(PULL_REQUEST));
        ComponentDto file1 = db.components().insertComponent(newFileDto(pullRequest));
        ComponentDto file2 = db.components().insertComponent(newFileDto(pullRequest));
        List<DuplicationsParser.Block> blocks = Lists.newArrayList();
        blocks.add(new DuplicationsParser.Block(Lists.newArrayList(Duplication.newComponent(file1, 57, 12), Duplication.newComponent(file2, 73, 12))));
        test(blocks, null, pullRequest.getPullRequest(), ((((((((((((((((((((((((((((((((((("{\n" + (((((((((((((("  \"duplications\": [\n" + "    {\n") + "      \"blocks\": [\n") + "        {\n") + "          \"from\": 57, \"size\": 12, \"_ref\": \"1\"\n") + "        },\n") + "        {\n") + "          \"from\": 73, \"size\": 12, \"_ref\": \"2\"\n") + "        }\n") + "      ]\n") + "    },") + "  ],\n") + "  \"files\": {\n") + "    \"1\": {\n") + "      \"key\": \"")) + (file1.getKey())) + "\",\n") + "      \"name\": \"") + (file1.longName())) + "\",\n") + "      \"project\": \"") + (pullRequest.getKey())) + "\",\n") + "      \"projectName\": \"") + (pullRequest.longName())) + "\",\n") + "      \"pullRequest\": \"") + (pullRequest.getPullRequest())) + "\",\n") + "    },\n") + "    \"2\": {\n") + "      \"key\": \"") + (file2.getKey())) + "\",\n") + "      \"name\": \"") + (file2.longName())) + "\",\n") + "      \"project\": \"") + (pullRequest.getKey())) + "\",\n") + "      \"projectName\": \"") + (pullRequest.longName())) + "\",\n") + "      \"pullRequest\": \"") + (pullRequest.getPullRequest())) + "\",\n") + "    }\n") + "  }") + "}"));
    }

    @Test
    public void write_nothing_when_no_data() {
        test(Collections.emptyList(), null, null, "{\"duplications\": [], \"files\": {}}");
    }
}

