/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.devtools.filewatch;


import Type.ADD;
import Type.DELETE;
import Type.MODIFY;
import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.util.FileCopyUtils;


/**
 * Tests for {@link FolderSnapshot}.
 *
 * @author Phillip Webb
 */
public class FolderSnapshotTests {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File folder;

    private FolderSnapshot initialSnapshot;

    @Test
    public void folderMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new FolderSnapshot(null)).withMessageContaining("Folder must not be null");
    }

    @Test
    public void folderMustNotBeFile() throws Exception {
        File file = this.temporaryFolder.newFile();
        assertThatIllegalArgumentException().isThrownBy(() -> new FolderSnapshot(file)).withMessageContaining((("Folder '" + file) + "' must not be a file"));
    }

    @Test
    public void folderDoesNotHaveToExist() throws Exception {
        File file = new File(this.temporaryFolder.getRoot(), "does/not/exist");
        FolderSnapshot snapshot = new FolderSnapshot(file);
        assertThat(snapshot).isEqualTo(new FolderSnapshot(file));
    }

    @Test
    public void equalsWhenNothingHasChanged() {
        FolderSnapshot updatedSnapshot = new FolderSnapshot(this.folder);
        assertThat(this.initialSnapshot).isEqualTo(updatedSnapshot);
        assertThat(this.initialSnapshot.hashCode()).isEqualTo(updatedSnapshot.hashCode());
    }

    @Test
    public void notEqualsWhenAFileIsAdded() throws Exception {
        new File(new File(this.folder, "folder1"), "newfile").createNewFile();
        FolderSnapshot updatedSnapshot = new FolderSnapshot(this.folder);
        assertThat(this.initialSnapshot).isNotEqualTo(updatedSnapshot);
    }

    @Test
    public void notEqualsWhenAFileIsDeleted() {
        new File(new File(this.folder, "folder1"), "file1").delete();
        FolderSnapshot updatedSnapshot = new FolderSnapshot(this.folder);
        assertThat(this.initialSnapshot).isNotEqualTo(updatedSnapshot);
    }

    @Test
    public void notEqualsWhenAFileIsModified() throws Exception {
        File file1 = new File(new File(this.folder, "folder1"), "file1");
        FileCopyUtils.copy("updatedcontent".getBytes(), file1);
        FolderSnapshot updatedSnapshot = new FolderSnapshot(this.folder);
        assertThat(this.initialSnapshot).isNotEqualTo(updatedSnapshot);
    }

    @Test
    public void getChangedFilesSnapshotMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.initialSnapshot.getChangedFiles(null, null)).withMessageContaining("Snapshot must not be null");
    }

    @Test
    public void getChangedFilesSnapshotMustBeTheSameSourceFolder() throws Exception {
        assertThatIllegalArgumentException().isThrownBy(() -> this.initialSnapshot.getChangedFiles(new FolderSnapshot(createTestFolderStructure()), null)).withMessageContaining((("Snapshot source folder must be '" + (this.folder)) + "'"));
    }

    @Test
    public void getChangedFilesWhenNothingHasChanged() {
        FolderSnapshot updatedSnapshot = new FolderSnapshot(this.folder);
        this.initialSnapshot.getChangedFiles(updatedSnapshot, null);
    }

    @Test
    public void getChangedFilesWhenAFileIsAddedAndDeletedAndChanged() throws Exception {
        File folder1 = new File(this.folder, "folder1");
        File file1 = new File(folder1, "file1");
        File file2 = new File(folder1, "file2");
        File newFile = new File(folder1, "newfile");
        FileCopyUtils.copy("updatedcontent".getBytes(), file1);
        file2.delete();
        newFile.createNewFile();
        FolderSnapshot updatedSnapshot = new FolderSnapshot(this.folder);
        ChangedFiles changedFiles = this.initialSnapshot.getChangedFiles(updatedSnapshot, null);
        assertThat(changedFiles.getSourceFolder()).isEqualTo(this.folder);
        assertThat(getChangedFile(changedFiles, file1).getType()).isEqualTo(MODIFY);
        assertThat(getChangedFile(changedFiles, file2).getType()).isEqualTo(DELETE);
        assertThat(getChangedFile(changedFiles, newFile).getType()).isEqualTo(ADD);
    }
}

