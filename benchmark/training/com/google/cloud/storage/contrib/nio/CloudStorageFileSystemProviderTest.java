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
package com.google.cloud.storage.contrib.nio;


import com.google.common.collect.ImmutableList;
import com.google.common.testing.NullPointerTester;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link CloudStorageFileSystemProvider}.
 */
@RunWith(JUnit4.class)
public class CloudStorageFileSystemProviderTest {
    private static final List<String> FILE_CONTENTS = ImmutableList.of("Fanatics have their dreams, wherewith they weave", "A paradise for a sect; the savage too", "From forth the loftiest fashion of his sleep", "Guesses at Heaven; pity these have not", "Trac'd upon vellum or wild Indian leaf", "The shadows of melodious utterance.", "But bare of laurel they live, dream, and die;", "For Poesy alone can tell her dreams,", "With the fine spell of words alone can save", "Imagination from the sable charm", "And dumb enchantment. Who alive can say,", "'Thou art no Poet may'st not tell thy dreams?'", "Since every man whose soul is not a clod", "Hath visions, and would speak, if he had loved", "And been well nurtured in his mother tongue.", "Whether the dream now purpos'd to rehearse", "Be poet's or fanatic's will be known", "When this warm scribe my hand is in the grave.");

    private static final String SINGULARITY = "A string";

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testSize() throws Exception {
        Path path = Paths.get(URI.create("gs://bucket/wat"));
        Files.write(path, CloudStorageFileSystemProviderTest.SINGULARITY.getBytes(StandardCharsets.UTF_8));
        assertThat(Files.size(path)).isEqualTo(CloudStorageFileSystemProviderTest.SINGULARITY.getBytes(StandardCharsets.UTF_8).length);
    }

    @Test
    public void testSize_trailingSlash_returnsFakePseudoDirectorySize() throws Exception {
        assertThat(Files.size(Paths.get(URI.create("gs://bucket/wat/")))).isEqualTo(1);
    }

    @Test
    public void testSize_trailingSlash_disablePseudoDirectories() throws Exception {
        try (CloudStorageFileSystem fs = CloudStorageFileSystem.forBucket("doodle", CloudStorageFileSystemProviderTest.usePseudoDirectories(false))) {
            Path path = fs.getPath("wat/");
            byte[] rapture = CloudStorageFileSystemProviderTest.SINGULARITY.getBytes(StandardCharsets.UTF_8);
            Files.write(path, rapture);
            assertThat(Files.size(path)).isEqualTo(rapture.length);
        }
    }

    @Test
    public void testReadAllBytes() throws Exception {
        Path path = Paths.get(URI.create("gs://bucket/wat"));
        Files.write(path, CloudStorageFileSystemProviderTest.SINGULARITY.getBytes(StandardCharsets.UTF_8));
        assertThat(new String(Files.readAllBytes(path), StandardCharsets.UTF_8)).isEqualTo(CloudStorageFileSystemProviderTest.SINGULARITY);
    }

    @Test
    public void testReadAllBytes_trailingSlash() throws Exception {
        thrown.expect(CloudStoragePseudoDirectoryException.class);
        Files.readAllBytes(Paths.get(URI.create("gs://bucket/wat/")));
    }

    @Test
    public void testNewByteChannelRead() throws Exception {
        Path path = Paths.get(URI.create("gs://bucket/wat"));
        byte[] data = CloudStorageFileSystemProviderTest.SINGULARITY.getBytes(StandardCharsets.UTF_8);
        Files.write(path, data);
        try (ReadableByteChannel input = Files.newByteChannel(path)) {
            ByteBuffer buffer = ByteBuffer.allocate(data.length);
            assertThat(input.read(buffer)).isEqualTo(data.length);
            assertThat(new String(buffer.array(), StandardCharsets.UTF_8)).isEqualTo(CloudStorageFileSystemProviderTest.SINGULARITY);
            buffer.rewind();
            assertThat(input.read(buffer)).isEqualTo((-1));
        }
    }

    @Test
    public void testNewByteChannelRead_seeking() throws Exception {
        Path path = Paths.get(URI.create("gs://lol/cat"));
        Files.write(path, "helloworld".getBytes(StandardCharsets.UTF_8));
        try (SeekableByteChannel input = Files.newByteChannel(path)) {
            ByteBuffer buffer = ByteBuffer.allocate(5);
            input.position(5);
            assertThat(input.position()).isEqualTo(5);
            assertThat(input.read(buffer)).isEqualTo(5);
            assertThat(input.position()).isEqualTo(10);
            assertThat(new String(buffer.array(), StandardCharsets.UTF_8)).isEqualTo("world");
            buffer.rewind();
            assertThat(input.read(buffer)).isEqualTo((-1));
            input.position(0);
            assertThat(input.position()).isEqualTo(0);
            assertThat(input.read(buffer)).isEqualTo(5);
            assertThat(input.position()).isEqualTo(5);
            assertThat(new String(buffer.array(), StandardCharsets.UTF_8)).isEqualTo("hello");
        }
    }

    @Test
    public void testNewByteChannelRead_seekBeyondSize_reportsEofOnNextRead() throws Exception {
        Path path = Paths.get(URI.create("gs://lol/cat"));
        Files.write(path, "hellocat".getBytes(StandardCharsets.UTF_8));
        try (SeekableByteChannel input = Files.newByteChannel(path)) {
            ByteBuffer buffer = ByteBuffer.allocate(5);
            input.position(10);
            assertThat(input.read(buffer)).isEqualTo((-1));
            input.position(11);
            assertThat(input.read(buffer)).isEqualTo((-1));
            assertThat(input.size()).isEqualTo(8);
        }
    }

    @Test
    public void testNewByteChannelRead_trailingSlash() throws Exception {
        Path path = Paths.get(URI.create("gs://bucket/wat/"));
        thrown.expect(CloudStoragePseudoDirectoryException.class);
        Files.newByteChannel(path);
    }

    @Test
    public void testNewByteChannelRead_notFound() throws Exception {
        Path path = Paths.get(URI.create("gs://bucket/wednesday"));
        thrown.expect(NoSuchFileException.class);
        Files.newByteChannel(path);
    }

    @Test
    public void testNewByteChannelWrite() throws Exception {
        Path path = Paths.get(URI.create("gs://bucket/tests"));
        try (SeekableByteChannel output = Files.newByteChannel(path, StandardOpenOption.WRITE)) {
            assertThat(output.position()).isEqualTo(0);
            assertThat(output.size()).isEqualTo(0);
            ByteBuffer buffer = ByteBuffer.wrap("filec".getBytes(StandardCharsets.UTF_8));
            assertThat(output.write(buffer)).isEqualTo(5);
            assertThat(output.position()).isEqualTo(5);
            assertThat(output.size()).isEqualTo(5);
            buffer = ByteBuffer.wrap("onten".getBytes(StandardCharsets.UTF_8));
            assertThat(output.write(buffer)).isEqualTo(5);
            assertThat(output.position()).isEqualTo(10);
            assertThat(output.size()).isEqualTo(10);
        }
        assertThat(new String(Files.readAllBytes(path), StandardCharsets.UTF_8)).isEqualTo("fileconten");
    }

    @Test
    public void testNewInputStream() throws Exception {
        Path path = Paths.get(URI.create("gs://bucket/wat"));
        Files.write(path, CloudStorageFileSystemProviderTest.SINGULARITY.getBytes(StandardCharsets.UTF_8));
        try (InputStream input = Files.newInputStream(path)) {
            byte[] data = new byte[CloudStorageFileSystemProviderTest.SINGULARITY.getBytes(StandardCharsets.UTF_8).length];
            input.read(data);
            assertThat(new String(data, StandardCharsets.UTF_8)).isEqualTo(CloudStorageFileSystemProviderTest.SINGULARITY);
        }
    }

    @Test
    public void testNewInputStream_trailingSlash() throws Exception {
        Path path = Paths.get(URI.create("gs://bucket/wat/"));
        thrown.expect(CloudStoragePseudoDirectoryException.class);
        try (InputStream input = Files.newInputStream(path)) {
            input.read();
        }
    }

    @Test
    public void testNewInputStream_notFound() throws Exception {
        Path path = Paths.get(URI.create("gs://cry/wednesday"));
        thrown.expect(NoSuchFileException.class);
        try (InputStream input = Files.newInputStream(path)) {
            input.read();
        }
    }

    @Test
    public void testNewOutputStream() throws Exception {
        Path path = Paths.get(URI.create("gs://bucket/wat"));
        Files.write(path, CloudStorageFileSystemProviderTest.SINGULARITY.getBytes(StandardCharsets.UTF_8));
        try (OutputStream output = Files.newOutputStream(path)) {
            output.write(CloudStorageFileSystemProviderTest.SINGULARITY.getBytes(StandardCharsets.UTF_8));
        }
        assertThat(new String(Files.readAllBytes(path), StandardCharsets.UTF_8)).isEqualTo(CloudStorageFileSystemProviderTest.SINGULARITY);
    }

    @Test
    public void testNewOutputStream_truncateByDefault() throws Exception {
        Path path = Paths.get(URI.create("gs://bucket/wat"));
        Files.write(path, CloudStorageFileSystemProviderTest.SINGULARITY.getBytes(StandardCharsets.UTF_8));
        Files.write(path, "hello".getBytes(StandardCharsets.UTF_8));
        try (OutputStream output = Files.newOutputStream(path)) {
            output.write(CloudStorageFileSystemProviderTest.SINGULARITY.getBytes(StandardCharsets.UTF_8));
        }
        assertThat(new String(Files.readAllBytes(path), StandardCharsets.UTF_8)).isEqualTo(CloudStorageFileSystemProviderTest.SINGULARITY);
    }

    @Test
    public void testNewOutputStream_truncateExplicitly() throws Exception {
        Path path = Paths.get(URI.create("gs://bucket/wat"));
        Files.write(path, CloudStorageFileSystemProviderTest.SINGULARITY.getBytes(StandardCharsets.UTF_8));
        Files.write(path, "hello".getBytes(StandardCharsets.UTF_8));
        try (OutputStream output = Files.newOutputStream(path, StandardOpenOption.TRUNCATE_EXISTING)) {
            output.write(CloudStorageFileSystemProviderTest.SINGULARITY.getBytes(StandardCharsets.UTF_8));
        }
        assertThat(new String(Files.readAllBytes(path), StandardCharsets.UTF_8)).isEqualTo(CloudStorageFileSystemProviderTest.SINGULARITY);
    }

    @Test
    public void testNewOutputStream_trailingSlash() throws Exception {
        Path path = Paths.get(URI.create("gs://bucket/wat/"));
        thrown.expect(CloudStoragePseudoDirectoryException.class);
        Files.newOutputStream(path);
    }

    @Test
    public void testNewOutputStream_createNew() throws Exception {
        Path path = Paths.get(URI.create("gs://cry/wednesday"));
        Files.newOutputStream(path, StandardOpenOption.CREATE_NEW);
    }

    @Test
    public void testNewOutputStream_createNew_alreadyExists() throws Exception {
        Path path = Paths.get(URI.create("gs://cry/wednesday"));
        Files.write(path, CloudStorageFileSystemProviderTest.SINGULARITY.getBytes(StandardCharsets.UTF_8));
        thrown.expect(FileAlreadyExistsException.class);
        Files.newOutputStream(path, StandardOpenOption.CREATE_NEW);
    }

    @Test
    public void testWrite_objectNameWithExtraSlashes_throwsIae() throws Exception {
        Path path = Paths.get(URI.create("gs://double/slash//yep"));
        thrown.expect(IllegalArgumentException.class);
        Files.write(path, CloudStorageFileSystemProviderTest.FILE_CONTENTS, StandardCharsets.UTF_8);
    }

    @Test
    public void testWrite_objectNameWithExtraSlashes_canBeNormalized() throws Exception {
        try (CloudStorageFileSystem fs = CloudStorageFileSystem.forBucket("greenbean", CloudStorageFileSystemProviderTest.permitEmptyPathComponents(false))) {
            Path path = fs.getPath("adipose//yep").normalize();
            Files.write(path, CloudStorageFileSystemProviderTest.FILE_CONTENTS, StandardCharsets.UTF_8);
            assertThat(Files.readAllLines(path, StandardCharsets.UTF_8)).isEqualTo(CloudStorageFileSystemProviderTest.FILE_CONTENTS);
            assertThat(Files.exists(fs.getPath("adipose", "yep"))).isTrue();
        }
    }

    @Test
    public void testWrite_objectNameWithExtraSlashes_permitEmptyPathComponents() throws Exception {
        try (CloudStorageFileSystem fs = CloudStorageFileSystem.forBucket("greenbean", CloudStorageFileSystemProviderTest.permitEmptyPathComponents(true))) {
            Path path = fs.getPath("adipose//yep");
            Files.write(path, CloudStorageFileSystemProviderTest.FILE_CONTENTS, StandardCharsets.UTF_8);
            assertThat(Files.readAllLines(path, StandardCharsets.UTF_8)).isEqualTo(CloudStorageFileSystemProviderTest.FILE_CONTENTS);
            assertThat(Files.exists(path)).isTrue();
        }
    }

    @Test
    public void testWrite_absoluteObjectName_prefixSlashGetsRemoved() throws Exception {
        Path path = Paths.get(URI.create("gs://greenbean/adipose/yep"));
        Files.write(path, CloudStorageFileSystemProviderTest.FILE_CONTENTS, StandardCharsets.UTF_8);
        assertThat(Files.readAllLines(path, StandardCharsets.UTF_8)).isEqualTo(CloudStorageFileSystemProviderTest.FILE_CONTENTS);
        assertThat(Files.exists(path)).isTrue();
    }

    @Test
    public void testWrite_absoluteObjectName_disableStrip_slashGetsPreserved() throws Exception {
        try (CloudStorageFileSystem fs = CloudStorageFileSystem.forBucket("greenbean", CloudStorageConfiguration.builder().stripPrefixSlash(false).build())) {
            Path path = fs.getPath("/adipose/yep");
            Files.write(path, CloudStorageFileSystemProviderTest.FILE_CONTENTS, StandardCharsets.UTF_8);
            assertThat(Files.readAllLines(path, StandardCharsets.UTF_8)).isEqualTo(CloudStorageFileSystemProviderTest.FILE_CONTENTS);
            assertThat(Files.exists(path)).isTrue();
        }
    }

    @Test
    public void testWrite() throws Exception {
        Path path = Paths.get(URI.create("gs://greenbean/adipose"));
        Files.write(path, CloudStorageFileSystemProviderTest.FILE_CONTENTS, StandardCharsets.UTF_8);
        assertThat(Files.readAllLines(path, StandardCharsets.UTF_8)).isEqualTo(CloudStorageFileSystemProviderTest.FILE_CONTENTS);
    }

    @Test
    public void testWriteOnClose() throws Exception {
        Path path = Paths.get(URI.create("gs://greenbean/adipose"));
        try (SeekableByteChannel chan = Files.newByteChannel(path, StandardOpenOption.WRITE)) {
            // writing lots of contents to defeat channel-internal buffering.
            for (int i = 0; i < 9999; i++) {
                for (String s : CloudStorageFileSystemProviderTest.FILE_CONTENTS) {
                    chan.write(ByteBuffer.wrap(s.getBytes(StandardCharsets.UTF_8)));
                }
            }
            try {
                Files.size(path);
                // we shouldn't make it to this line. Not using thrown.expect because
                // I still want to run a few lines after the exception.
                assertThat(false).isTrue();
            } catch (NoSuchFileException nsf) {
                // that's what we wanted, we're good.
            }
        }
        // channel now closed, the file should be there and with the new contents.
        assertThat(Files.exists(path)).isTrue();
        assertThat(Files.size(path)).isGreaterThan(100L);
    }

    @Test
    public void testWrite_trailingSlash() throws Exception {
        thrown.expect(CloudStoragePseudoDirectoryException.class);
        Files.write(Paths.get(URI.create("gs://greenbean/adipose/")), CloudStorageFileSystemProviderTest.FILE_CONTENTS, StandardCharsets.UTF_8);
    }

    @Test
    public void testExists() throws Exception {
        assertThat(Files.exists(Paths.get(URI.create("gs://military/fashion")))).isFalse();
        Files.write(Paths.get(URI.create("gs://military/fashion")), "(?? ?? )?".getBytes(StandardCharsets.UTF_8));
        assertThat(Files.exists(Paths.get(URI.create("gs://military/fashion")))).isTrue();
    }

    @Test
    public void testExists_trailingSlash() {
        assertThat(Files.exists(Paths.get(URI.create("gs://military/fashion/")))).isTrue();
        assertThat(Files.exists(Paths.get(URI.create("gs://military/fashion/.")))).isTrue();
        assertThat(Files.exists(Paths.get(URI.create("gs://military/fashion/..")))).isTrue();
    }

    @Test
    public void testExists_trailingSlash_disablePseudoDirectories() throws Exception {
        try (CloudStorageFileSystem fs = CloudStorageFileSystem.forBucket("military", CloudStorageFileSystemProviderTest.usePseudoDirectories(false))) {
            assertThat(Files.exists(fs.getPath("fashion/"))).isFalse();
        }
    }

    @Test
    public void testFakeDirectories() throws IOException {
        try (FileSystem fs = CloudStorageFileSystem.forBucket("military")) {
            List<Path> paths = new ArrayList<>();
            paths.add(fs.getPath("dir/angel"));
            paths.add(fs.getPath("dir/deepera"));
            paths.add(fs.getPath("dir/deeperb"));
            paths.add(fs.getPath("dir/deeper_"));
            paths.add(fs.getPath("dir/deeper.sea/hasfish"));
            paths.add(fs.getPath("dir/deeper/fish"));
            for (Path path : paths) {
                Files.createFile(path);
            }
            // ends with slash, must be a directory
            assertThat(Files.isDirectory(fs.getPath("dir/"))).isTrue();
            // files are not directories
            assertThat(Files.exists(fs.getPath("dir/angel"))).isTrue();
            assertThat(Files.isDirectory(fs.getPath("dir/angel"))).isFalse();
            // directories are recognized even without the trailing "/"
            assertThat(Files.isDirectory(fs.getPath("dir"))).isTrue();
            // also works for absolute paths
            assertThat(Files.isDirectory(fs.getPath("/dir"))).isTrue();
            // non-existent files are not directories (but they don't make us crash)
            assertThat(Files.isDirectory(fs.getPath("di"))).isFalse();
            assertThat(Files.isDirectory(fs.getPath("dirs"))).isFalse();
            assertThat(Files.isDirectory(fs.getPath("dir/deep"))).isFalse();
            assertThat(Files.isDirectory(fs.getPath("dir/deeper/fi"))).isFalse();
            assertThat(Files.isDirectory(fs.getPath("/dir/deeper/fi"))).isFalse();
            // also works for subdirectories
            assertThat(Files.isDirectory(fs.getPath("dir/deeper/"))).isTrue();
            assertThat(Files.isDirectory(fs.getPath("dir/deeper"))).isTrue();
            assertThat(Files.isDirectory(fs.getPath("/dir/deeper/"))).isTrue();
            assertThat(Files.isDirectory(fs.getPath("/dir/deeper"))).isTrue();
            // dot and .. folders are directories
            assertThat(Files.isDirectory(fs.getPath("dir/deeper/."))).isTrue();
            assertThat(Files.isDirectory(fs.getPath("dir/deeper/.."))).isTrue();
            // dots in the name are fine
            assertThat(Files.isDirectory(fs.getPath("dir/deeper.sea/"))).isTrue();
            assertThat(Files.isDirectory(fs.getPath("dir/deeper.sea"))).isTrue();
            assertThat(Files.isDirectory(fs.getPath("dir/deeper.seax"))).isFalse();
            // the root folder is a directory
            assertThat(Files.isDirectory(fs.getPath("/"))).isTrue();
            assertThat(Files.isDirectory(fs.getPath(""))).isTrue();
        }
    }

    @Test
    public void testDelete() throws Exception {
        Files.write(Paths.get(URI.create("gs://love/fashion")), "(?? ?? )?".getBytes(StandardCharsets.UTF_8));
        assertThat(Files.exists(Paths.get(URI.create("gs://love/fashion")))).isTrue();
        Files.delete(Paths.get(URI.create("gs://love/fashion")));
        assertThat(Files.exists(Paths.get(URI.create("gs://love/fashion")))).isFalse();
    }

    @Test
    public void testDelete_dotDirNotNormalized_throwsIae() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        Files.delete(Paths.get(URI.create("gs://love/fly/../passion")));
    }

    @Test
    public void testDelete_trailingSlash() throws Exception {
        Files.delete(Paths.get(URI.create("gs://love/passion/")));
    }

    @Test
    public void testDelete_trailingSlash_disablePseudoDirectories() throws Exception {
        try (CloudStorageFileSystem fs = CloudStorageFileSystem.forBucket("pumpkin", CloudStorageFileSystemProviderTest.usePseudoDirectories(false))) {
            Path path = fs.getPath("wat/");
            Files.write(path, CloudStorageFileSystemProviderTest.FILE_CONTENTS, StandardCharsets.UTF_8);
            assertThat(Files.exists(path));
            Files.delete(path);
            assertThat((!(Files.exists(path))));
        }
    }

    @Test
    public void testDelete_notFound() throws Exception {
        thrown.expect(NoSuchFileException.class);
        Files.delete(Paths.get(URI.create("gs://loveh/passionehu")));
    }

    @Test
    public void testDeleteIfExists() throws Exception {
        Files.write(Paths.get(URI.create("gs://love/passionz")), "(?? ?? )?".getBytes(StandardCharsets.UTF_8));
        assertThat(Files.deleteIfExists(Paths.get(URI.create("gs://love/passionz")))).isTrue();
        // call does not fail, the folder just doesn't exist
        Files.deleteIfExists(Paths.get(URI.create("gs://love/passion/")));
    }

    @Test
    public void testDeleteIfExists_trailingSlash_disablePseudoDirectories() throws Exception {
        try (CloudStorageFileSystem fs = CloudStorageFileSystem.forBucket("doodle", CloudStorageFileSystemProviderTest.usePseudoDirectories(false))) {
            // Doesn't exist, no error
            Files.deleteIfExists(Paths.get(URI.create("gs://love/passion/")));
        }
    }

    @Test
    public void testCopy() throws Exception {
        Path source = Paths.get(URI.create("gs://military/fashion.show"));
        Path target = Paths.get(URI.create("gs://greenbean/adipose"));
        Files.write(source, "(?? ?? )?".getBytes(StandardCharsets.UTF_8));
        Files.copy(source, target);
        assertThat(new String(Files.readAllBytes(target), StandardCharsets.UTF_8)).isEqualTo("(?? ?? )?");
        assertThat(Files.exists(source)).isTrue();
        assertThat(Files.exists(target)).isTrue();
    }

    @Test
    public void testCopy_sourceMissing_throwsNoSuchFileException() throws Exception {
        thrown.expect(NoSuchFileException.class);
        Files.copy(Paths.get(URI.create("gs://military/fashion.show")), Paths.get(URI.create("gs://greenbean/adipose")));
    }

    @Test
    public void testCopy_targetExists_throwsFileAlreadyExistsException() throws Exception {
        Path source = Paths.get(URI.create("gs://military/fashion.show"));
        Path target = Paths.get(URI.create("gs://greenbean/adipose"));
        Files.write(source, "(?? ?? )?".getBytes(StandardCharsets.UTF_8));
        Files.write(target, "(?? ?? )?".getBytes(StandardCharsets.UTF_8));
        thrown.expect(FileAlreadyExistsException.class);
        Files.copy(source, target);
    }

    @Test
    public void testCopyReplace_targetExists_works() throws Exception {
        Path source = Paths.get(URI.create("gs://military/fashion.show"));
        Path target = Paths.get(URI.create("gs://greenbean/adipose"));
        Files.write(source, "(?? ?? )?".getBytes(StandardCharsets.UTF_8));
        Files.write(target, "(?? ?? )?".getBytes(StandardCharsets.UTF_8));
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    public void testCopy_directory_doesNothing() throws Exception {
        Path source = Paths.get(URI.create("gs://military/fundir/"));
        Path target = Paths.get(URI.create("gs://greenbean/loldir/"));
        Files.copy(source, target);
    }

    @Test
    public void testCopy_atomic_throwsUnsupported() throws Exception {
        Path source = Paths.get(URI.create("gs://military/fashion.show"));
        Path target = Paths.get(URI.create("gs://greenbean/adipose"));
        Files.write(source, "(?? ?? )?".getBytes(StandardCharsets.UTF_8));
        thrown.expect(UnsupportedOperationException.class);
        Files.copy(source, target, StandardCopyOption.ATOMIC_MOVE);
    }

    @Test
    public void testMove() throws Exception {
        Path source = Paths.get(URI.create("gs://military/fashion.show"));
        Path target = Paths.get(URI.create("gs://greenbean/adipose"));
        Files.write(source, "(?? ?? )?".getBytes(StandardCharsets.UTF_8));
        Files.move(source, target);
        assertThat(new String(Files.readAllBytes(target), StandardCharsets.UTF_8)).isEqualTo("(?? ?? )?");
        assertThat(Files.exists(source)).isFalse();
        assertThat(Files.exists(target)).isTrue();
    }

    @Test
    public void testCreateDirectory() throws Exception {
        Path path = Paths.get(URI.create("gs://greenbean/dir/"));
        Files.createDirectory(path);
        assertThat(Files.exists(path)).isTrue();
    }

    @Test
    public void testMove_atomicMove_notSupported() throws Exception {
        Path source = Paths.get(URI.create("gs://military/fashion.show"));
        Path target = Paths.get(URI.create("gs://greenbean/adipose"));
        Files.write(source, "(?? ?? )?".getBytes(StandardCharsets.UTF_8));
        thrown.expect(AtomicMoveNotSupportedException.class);
        Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
    }

    @Test
    public void testIsDirectory() throws Exception {
        try (FileSystem fs = FileSystems.getFileSystem(URI.create("gs://doodle"))) {
            assertThat(Files.isDirectory(fs.getPath(""))).isTrue();
            assertThat(Files.isDirectory(fs.getPath("/"))).isTrue();
            assertThat(Files.isDirectory(fs.getPath("."))).isTrue();
            assertThat(Files.isDirectory(fs.getPath("./"))).isTrue();
            assertThat(Files.isDirectory(fs.getPath("cat/.."))).isTrue();
            assertThat(Files.isDirectory(fs.getPath("hello/cat/.."))).isTrue();
            assertThat(Files.isDirectory(fs.getPath("cat/../"))).isTrue();
            assertThat(Files.isDirectory(fs.getPath("hello/cat/../"))).isTrue();
        }
    }

    @Test
    public void testIsDirectory_trailingSlash_alwaysTrue() {
        assertThat(Files.isDirectory(Paths.get(URI.create("gs://military/fundir/")))).isTrue();
    }

    @Test
    public void testIsDirectory_trailingSlash_pseudoDirectoriesDisabled_false() throws Exception {
        try (CloudStorageFileSystem fs = CloudStorageFileSystem.forBucket("doodle", CloudStorageFileSystemProviderTest.usePseudoDirectories(false))) {
            assertThat(Files.isDirectory(fs.getPath("fundir/"))).isFalse();
        }
    }

    @Test
    public void testCopy_withCopyAttributes_preservesAttributes() throws Exception {
        Path source = Paths.get(URI.create("gs://military/fashion.show"));
        Path target = Paths.get(URI.create("gs://greenbean/adipose"));
        Files.write(source, "(?? ?? )?".getBytes(StandardCharsets.UTF_8), CloudStorageOptions.withMimeType("text/lolcat"), CloudStorageOptions.withCacheControl("public; max-age=666"), CloudStorageOptions.withContentEncoding("foobar"), CloudStorageOptions.withContentDisposition("my-content-disposition"), CloudStorageOptions.withUserMetadata("answer", "42"));
        Files.copy(source, target, StandardCopyOption.COPY_ATTRIBUTES);
        CloudStorageFileAttributes attributes = Files.readAttributes(target, CloudStorageFileAttributes.class);
        assertThat(attributes.mimeType()).hasValue("text/lolcat");
        assertThat(attributes.cacheControl()).hasValue("public; max-age=666");
        assertThat(attributes.contentEncoding()).hasValue("foobar");
        assertThat(attributes.contentDisposition()).hasValue("my-content-disposition");
        assertThat(attributes.userMetadata().containsKey("answer")).isTrue();
        assertThat(attributes.userMetadata().get("answer")).isEqualTo("42");
    }

    @Test
    public void testCopy_withoutOptions_doesntPreservesAttributes() throws Exception {
        Path source = Paths.get(URI.create("gs://military/fashion.show"));
        Path target = Paths.get(URI.create("gs://greenbean/adipose"));
        Files.write(source, "(?? ?? )?".getBytes(StandardCharsets.UTF_8), CloudStorageOptions.withMimeType("text/lolcat"), CloudStorageOptions.withCacheControl("public; max-age=666"), CloudStorageOptions.withUserMetadata("answer", "42"));
        Files.copy(source, target);
        CloudStorageFileAttributes attributes = Files.readAttributes(target, CloudStorageFileAttributes.class);
        String mimeType = attributes.mimeType().orNull();
        String cacheControl = attributes.cacheControl().orNull();
        assertThat(mimeType).isNotEqualTo("text/lolcat");
        assertThat(cacheControl).isNull();
        assertThat(attributes.userMetadata().containsKey("answer")).isFalse();
    }

    @Test
    public void testCopy_overwriteAttributes() throws Exception {
        Path source = Paths.get(URI.create("gs://military/fashion.show"));
        Path target1 = Paths.get(URI.create("gs://greenbean/adipose"));
        Path target2 = Paths.get(URI.create("gs://greenbean/round"));
        Files.write(source, "(?? ?? )?".getBytes(StandardCharsets.UTF_8), CloudStorageOptions.withMimeType("text/lolcat"), CloudStorageOptions.withCacheControl("public; max-age=666"));
        Files.copy(source, target1, StandardCopyOption.COPY_ATTRIBUTES);
        Files.copy(source, target2, StandardCopyOption.COPY_ATTRIBUTES, CloudStorageOptions.withMimeType("text/palfun"));
        CloudStorageFileAttributes attributes = Files.readAttributes(target1, CloudStorageFileAttributes.class);
        assertThat(attributes.mimeType()).hasValue("text/lolcat");
        assertThat(attributes.cacheControl()).hasValue("public; max-age=666");
        attributes = Files.readAttributes(target2, CloudStorageFileAttributes.class);
        assertThat(attributes.mimeType()).hasValue("text/palfun");
        assertThat(attributes.cacheControl()).hasValue("public; max-age=666");
    }

    @Test
    public void testNullness() throws Exception {
        try (FileSystem fs = FileSystems.getFileSystem(URI.create("gs://blood"))) {
            NullPointerTester tester = new NullPointerTester();
            tester.ignore(CloudStorageFileSystemProvider.class.getMethod("equals", Object.class));
            tester.setDefault(URI.class, URI.create("gs://blood"));
            tester.setDefault(Path.class, fs.getPath("and/one"));
            tester.setDefault(OpenOption.class, StandardOpenOption.CREATE);
            tester.setDefault(CopyOption.class, StandardCopyOption.COPY_ATTRIBUTES);
            tester.testAllPublicStaticMethods(CloudStorageFileSystemProvider.class);
            tester.testAllPublicInstanceMethods(new CloudStorageFileSystemProvider());
        }
    }

    @Test
    public void testProviderEquals() {
        Path path1 = Paths.get(URI.create("gs://bucket/tuesday"));
        Path path2 = Paths.get(URI.create("gs://blood/wednesday"));
        Path path3 = Paths.get("tmp");
        assertThat(path1.getFileSystem().provider()).isEqualTo(path2.getFileSystem().provider());
        assertThat(path1.getFileSystem().provider()).isNotEqualTo(path3.getFileSystem().provider());
    }

    @Test
    public void testNewFileSystem() throws Exception {
        Map<String, String> env = new HashMap<>();
        FileSystems.newFileSystem(URI.create("gs://bucket/path/to/file"), env);
    }

    @Test
    public void testFromSpace() throws Exception {
        // User should be able to create paths to files whose name contains a space.
        // Traditional way 1: manually escape the spaces
        Path path1 = Paths.get(URI.create("gs://bucket/with/a%20space"));
        CloudStorageFileSystemProvider provider = ((CloudStorageFileSystemProvider) (path1.getFileSystem().provider()));
        // Traditional way 2: use UrlEscapers.urlFragmentEscaper().escape
        // to escape the string for you.
        // (Not tested because UrlEscapers isn't the unit under test).
        // Non-traditional way: use our convenience method to work around URIs not being allowed to
        // contain spaces.
        Path path3 = provider.getPath("gs://bucket/with/a space");
        // Both approaches should be equivalent
        assertThat(path1.getFileSystem().provider()).isEqualTo(path3.getFileSystem().provider());
        assertThat(path1.toUri()).isEqualTo(path3.toUri());
        // getPath does not interpret the string at all.
        Path path4 = provider.getPath("gs://bucket/with/a%20percent");
        assertThat(path4.toString()).isEqualTo("/with/a%20percent");
    }
}

