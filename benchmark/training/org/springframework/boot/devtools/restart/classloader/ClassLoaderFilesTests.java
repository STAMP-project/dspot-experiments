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
package org.springframework.boot.devtools.restart.classloader;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.devtools.restart.classloader.ClassLoaderFile.Kind;
import org.springframework.boot.devtools.restart.classloader.ClassLoaderFiles.SourceFolder;


/**
 * Tests for {@link ClassLoaderFiles}.
 *
 * @author Phillip Webb
 */
public class ClassLoaderFilesTests {
    private ClassLoaderFiles files = new ClassLoaderFiles();

    @Test
    public void addFileNameMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.files.addFile(null, mock(.class))).withMessageContaining("Name must not be null");
    }

    @Test
    public void addFileFileMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.files.addFile("test", null)).withMessageContaining("File must not be null");
    }

    @Test
    public void getFileWithNullName() {
        assertThat(this.files.getFile(null)).isNull();
    }

    @Test
    public void addAndGet() {
        ClassLoaderFile file = new ClassLoaderFile(Kind.ADDED, new byte[10]);
        this.files.addFile("myfile", file);
        assertThat(this.files.getFile("myfile")).isEqualTo(file);
    }

    @Test
    public void getMissing() {
        assertThat(this.files.getFile("missing")).isNull();
    }

    @Test
    public void addTwice() {
        ClassLoaderFile file1 = new ClassLoaderFile(Kind.ADDED, new byte[10]);
        ClassLoaderFile file2 = new ClassLoaderFile(Kind.MODIFIED, new byte[10]);
        this.files.addFile("myfile", file1);
        this.files.addFile("myfile", file2);
        assertThat(this.files.getFile("myfile")).isEqualTo(file2);
    }

    @Test
    public void addTwiceInDifferentSourceFolders() {
        ClassLoaderFile file1 = new ClassLoaderFile(Kind.ADDED, new byte[10]);
        ClassLoaderFile file2 = new ClassLoaderFile(Kind.MODIFIED, new byte[10]);
        this.files.addFile("a", "myfile", file1);
        this.files.addFile("b", "myfile", file2);
        assertThat(this.files.getFile("myfile")).isEqualTo(file2);
        assertThat(this.files.getOrCreateSourceFolder("a").getFiles().size()).isEqualTo(0);
        assertThat(this.files.getOrCreateSourceFolder("b").getFiles().size()).isEqualTo(1);
    }

    @Test
    public void getSourceFolders() {
        ClassLoaderFile file1 = new ClassLoaderFile(Kind.ADDED, new byte[10]);
        ClassLoaderFile file2 = new ClassLoaderFile(Kind.MODIFIED, new byte[10]);
        ClassLoaderFile file3 = new ClassLoaderFile(Kind.MODIFIED, new byte[10]);
        ClassLoaderFile file4 = new ClassLoaderFile(Kind.MODIFIED, new byte[10]);
        this.files.addFile("a", "myfile1", file1);
        this.files.addFile("a", "myfile2", file2);
        this.files.addFile("b", "myfile3", file3);
        this.files.addFile("b", "myfile4", file4);
        Iterator<SourceFolder> sourceFolders = this.files.getSourceFolders().iterator();
        SourceFolder sourceFolder1 = sourceFolders.next();
        SourceFolder sourceFolder2 = sourceFolders.next();
        assertThat(sourceFolders.hasNext()).isFalse();
        assertThat(sourceFolder1.getName()).isEqualTo("a");
        assertThat(sourceFolder2.getName()).isEqualTo("b");
        assertThat(sourceFolder1.getFiles()).containsOnly(file1, file2);
        assertThat(sourceFolder2.getFiles()).containsOnly(file3, file4);
    }

    @Test
    public void serialize() throws Exception {
        ClassLoaderFile file = new ClassLoaderFile(Kind.ADDED, new byte[10]);
        this.files.addFile("myfile", file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this.files);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
        ClassLoaderFiles readObject = ((ClassLoaderFiles) (ois.readObject()));
        assertThat(readObject.getFile("myfile")).isNotNull();
    }

    @Test
    public void addAll() {
        ClassLoaderFile file1 = new ClassLoaderFile(Kind.ADDED, new byte[10]);
        this.files.addFile("a", "myfile1", file1);
        ClassLoaderFiles toAdd = new ClassLoaderFiles();
        ClassLoaderFile file2 = new ClassLoaderFile(Kind.MODIFIED, new byte[10]);
        ClassLoaderFile file3 = new ClassLoaderFile(Kind.MODIFIED, new byte[10]);
        toAdd.addFile("a", "myfile2", file2);
        toAdd.addFile("b", "myfile3", file3);
        this.files.addAll(toAdd);
        Iterator<SourceFolder> sourceFolders = this.files.getSourceFolders().iterator();
        SourceFolder sourceFolder1 = sourceFolders.next();
        SourceFolder sourceFolder2 = sourceFolders.next();
        assertThat(sourceFolders.hasNext()).isFalse();
        assertThat(sourceFolder1.getName()).isEqualTo("a");
        assertThat(sourceFolder2.getName()).isEqualTo("b");
        assertThat(sourceFolder1.getFiles()).containsOnly(file1, file2);
    }

    @Test
    public void getSize() {
        this.files.addFile("s1", "n1", Mockito.mock(ClassLoaderFile.class));
        this.files.addFile("s1", "n2", Mockito.mock(ClassLoaderFile.class));
        this.files.addFile("s2", "n3", Mockito.mock(ClassLoaderFile.class));
        this.files.addFile("s2", "n1", Mockito.mock(ClassLoaderFile.class));
        assertThat(this.files.size()).isEqualTo(3);
    }

    @Test
    public void classLoaderFilesMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new ClassLoaderFiles(null)).withMessageContaining("ClassLoaderFiles must not be null");
    }

    @Test
    public void constructFromExistingSet() {
        this.files.addFile("s1", "n1", Mockito.mock(ClassLoaderFile.class));
        this.files.addFile("s1", "n2", Mockito.mock(ClassLoaderFile.class));
        ClassLoaderFiles copy = new ClassLoaderFiles(this.files);
        this.files.addFile("s2", "n3", Mockito.mock(ClassLoaderFile.class));
        assertThat(this.files.size()).isEqualTo(3);
        assertThat(copy.size()).isEqualTo(2);
    }
}

