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
package org.sonar.ce.task.projectanalysis.filemove;


import Component.Type.DIRECTORY;
import Component.Type.FILE;
import Component.Type.PROJECT;
import Component.Type.PROJECT_VIEW;
import Component.Type.SUBVIEW;
import Component.Type.VIEW;
import MovedFilesRepository.OriginalFile;
import java.util.Random;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.component.ViewsComponent;


public class MutableMovedFilesRepositoryImplTest {
    private static final Component SOME_FILE = ReportComponent.builder(FILE, 1).build();

    private static final Component[] COMPONENTS_EXCEPT_FILE = new Component[]{ ReportComponent.builder(PROJECT, 1).build(), ReportComponent.builder(DIRECTORY, 1).build(), ViewsComponent.builder(VIEW, 1).build(), ViewsComponent.builder(SUBVIEW, 1).build(), ViewsComponent.builder(PROJECT_VIEW, 1).build() };

    private static final OriginalFile SOME_ORIGINAL_FILE = new MovedFilesRepository.OriginalFile(100, "uuid for 100", "key for 100");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private MutableMovedFilesRepositoryImpl underTest = new MutableMovedFilesRepositoryImpl();

    @Test
    public void setOriginalFile_throws_NPE_when_file_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("file can't be null");
        underTest.setOriginalFile(null, MutableMovedFilesRepositoryImplTest.SOME_ORIGINAL_FILE);
    }

    @Test
    public void setOriginalFile_throws_NPE_when_originalFile_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("originalFile can't be null");
        underTest.setOriginalFile(MutableMovedFilesRepositoryImplTest.SOME_FILE, null);
    }

    @Test
    public void setOriginalFile_throws_IAE_when_type_is_no_FILE() {
        for (Component component : MutableMovedFilesRepositoryImplTest.COMPONENTS_EXCEPT_FILE) {
            try {
                underTest.setOriginalFile(component, MutableMovedFilesRepositoryImplTest.SOME_ORIGINAL_FILE);
                fail("should have raised a NPE");
            } catch (IllegalArgumentException e) {
                assertThat(e).isInstanceOf(IllegalArgumentException.class).hasMessage("file must be of type FILE");
            }
        }
    }

    @Test
    public void setOriginalFile_throws_ISE_if_settings_another_originalFile() {
        underTest.setOriginalFile(MutableMovedFilesRepositoryImplTest.SOME_FILE, MutableMovedFilesRepositoryImplTest.SOME_ORIGINAL_FILE);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(("Original file OriginalFile{id=100, uuid='uuid for 100', key='key for 100'} " + "already registered for file ReportComponent{ref=1, key='key_1', type=FILE}"));
        underTest.setOriginalFile(MutableMovedFilesRepositoryImplTest.SOME_FILE, new MovedFilesRepository.OriginalFile(987, "uudi", "key"));
    }

    @Test
    public void setOriginalFile_does_not_fail_if_same_original_file_is_added_multiple_times_for_the_same_component() {
        underTest.setOriginalFile(MutableMovedFilesRepositoryImplTest.SOME_FILE, MutableMovedFilesRepositoryImplTest.SOME_ORIGINAL_FILE);
        for (int i = 0; i < (1 + (Math.abs(new Random().nextInt(10)))); i++) {
            underTest.setOriginalFile(MutableMovedFilesRepositoryImplTest.SOME_FILE, MutableMovedFilesRepositoryImplTest.SOME_ORIGINAL_FILE);
        }
    }

    @Test
    public void setOriginalFile_does_not_fail_when_originalFile_is_added_twice_for_different_files() {
        underTest.setOriginalFile(MutableMovedFilesRepositoryImplTest.SOME_FILE, MutableMovedFilesRepositoryImplTest.SOME_ORIGINAL_FILE);
        underTest.setOriginalFile(ReportComponent.builder(FILE, 2).build(), MutableMovedFilesRepositoryImplTest.SOME_ORIGINAL_FILE);
    }

    @Test
    public void getOriginalFile_throws_NPE_when_file_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("file can't be null");
        underTest.getOriginalFile(null);
    }

    @Test
    public void getOriginalFile_returns_absent_for_any_component_type_when_empty() {
        assertThat(underTest.getOriginalFile(MutableMovedFilesRepositoryImplTest.SOME_FILE)).isAbsent();
        for (Component component : MutableMovedFilesRepositoryImplTest.COMPONENTS_EXCEPT_FILE) {
            assertThat(underTest.getOriginalFile(component)).isAbsent();
        }
    }

    @Test
    public void getOriginalFile_returns_absent_for_any_type_of_Component_but_file_when_non_empty() {
        underTest.setOriginalFile(MutableMovedFilesRepositoryImplTest.SOME_FILE, MutableMovedFilesRepositoryImplTest.SOME_ORIGINAL_FILE);
        for (Component component : MutableMovedFilesRepositoryImplTest.COMPONENTS_EXCEPT_FILE) {
            assertThat(underTest.getOriginalFile(component)).isAbsent();
        }
        assertThat(underTest.getOriginalFile(MutableMovedFilesRepositoryImplTest.SOME_FILE)).contains(MutableMovedFilesRepositoryImplTest.SOME_ORIGINAL_FILE);
    }

    @Test
    public void getOriginalFile_returns_originalFile_base_on_file_key() {
        underTest.setOriginalFile(MutableMovedFilesRepositoryImplTest.SOME_FILE, MutableMovedFilesRepositoryImplTest.SOME_ORIGINAL_FILE);
        assertThat(underTest.getOriginalFile(MutableMovedFilesRepositoryImplTest.SOME_FILE)).contains(MutableMovedFilesRepositoryImplTest.SOME_ORIGINAL_FILE);
        assertThat(underTest.getOriginalFile(ReportComponent.builder(FILE, 1).setUuid("toto").build())).contains(MutableMovedFilesRepositoryImplTest.SOME_ORIGINAL_FILE);
    }
}

