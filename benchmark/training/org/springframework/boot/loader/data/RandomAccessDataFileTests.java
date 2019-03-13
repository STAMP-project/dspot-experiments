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
package org.springframework.boot.loader.data;


import java.io.EOFException;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for {@link RandomAccessDataFile}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class RandomAccessDataFileTests {
    private static final byte[] BYTES;

    static {
        BYTES = new byte[256];
        for (int i = 0; i < (RandomAccessDataFileTests.BYTES.length); i++) {
            RandomAccessDataFileTests.BYTES[i] = ((byte) (i));
        }
    }

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File tempFile;

    private RandomAccessDataFile file;

    private InputStream inputStream;

    @Test
    public void fileNotNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new RandomAccessDataFile(null)).withMessageContaining("File must not be null");
    }

    @Test
    public void fileExists() {
        File file = new File("/does/not/exist");
        assertThatIllegalArgumentException().isThrownBy(() -> new RandomAccessDataFile(file)).withMessageContaining(String.format("File %s must exist", file.getAbsolutePath()));
    }

    @Test
    public void readWithOffsetAndLengthShouldRead() throws Exception {
        byte[] read = this.file.read(2, 3);
        assertThat(read).isEqualTo(new byte[]{ 2, 3, 4 });
    }

    @Test
    public void readWhenOffsetIsBeyondEOFShouldThrowException() throws Exception {
        assertThatExceptionOfType(IndexOutOfBoundsException.class).isThrownBy(() -> this.file.read(257, 0));
    }

    @Test
    public void readWhenOffsetIsBeyondEndOfSubsectionShouldThrowException() throws Exception {
        RandomAccessData subsection = this.file.getSubsection(0, 10);
        assertThatExceptionOfType(IndexOutOfBoundsException.class).isThrownBy(() -> subsection.read(11, 0));
    }

    @Test
    public void readWhenOffsetPlusLengthGreaterThanEOFShouldThrowException() throws Exception {
        assertThatExceptionOfType(EOFException.class).isThrownBy(() -> this.file.read(256, 1));
    }

    @Test
    public void readWhenOffsetPlusLengthGreaterThanEndOfSubsectionShouldThrowException() throws Exception {
        RandomAccessData subsection = this.file.getSubsection(0, 10);
        assertThatExceptionOfType(EOFException.class).isThrownBy(() -> subsection.read(10, 1));
    }

    @Test
    public void inputStreamRead() throws Exception {
        for (int i = 0; i <= 255; i++) {
            assertThat(this.inputStream.read()).isEqualTo(i);
        }
    }

    @Test
    public void inputStreamReadNullBytes() throws Exception {
        assertThatNullPointerException().isThrownBy(() -> this.inputStream.read(null)).withMessage("Bytes must not be null");
    }

    @Test
    public void inputStreamReadNullBytesWithOffset() throws Exception {
        assertThatNullPointerException().isThrownBy(() -> this.inputStream.read(null, 0, 1)).withMessage("Bytes must not be null");
    }

    @Test
    public void inputStreamReadBytes() throws Exception {
        byte[] b = new byte[256];
        int amountRead = this.inputStream.read(b);
        assertThat(b).isEqualTo(RandomAccessDataFileTests.BYTES);
        assertThat(amountRead).isEqualTo(256);
    }

    @Test
    public void inputStreamReadOffsetBytes() throws Exception {
        byte[] b = new byte[7];
        this.inputStream.skip(1);
        int amountRead = this.inputStream.read(b, 2, 3);
        assertThat(b).isEqualTo(new byte[]{ 0, 0, 1, 2, 3, 0, 0 });
        assertThat(amountRead).isEqualTo(3);
    }

    @Test
    public void inputStreamReadMoreBytesThanAvailable() throws Exception {
        byte[] b = new byte[257];
        int amountRead = this.inputStream.read(b);
        assertThat(b).startsWith(RandomAccessDataFileTests.BYTES);
        assertThat(amountRead).isEqualTo(256);
    }

    @Test
    public void inputStreamReadPastEnd() throws Exception {
        this.inputStream.skip(255);
        assertThat(this.inputStream.read()).isEqualTo(255);
        assertThat(this.inputStream.read()).isEqualTo((-1));
        assertThat(this.inputStream.read()).isEqualTo((-1));
    }

    @Test
    public void inputStreamReadZeroLength() throws Exception {
        byte[] b = new byte[]{ 15 };
        int amountRead = this.inputStream.read(b, 0, 0);
        assertThat(b).isEqualTo(new byte[]{ 15 });
        assertThat(amountRead).isEqualTo(0);
        assertThat(this.inputStream.read()).isEqualTo(0);
    }

    @Test
    public void inputStreamSkip() throws Exception {
        long amountSkipped = this.inputStream.skip(4);
        assertThat(this.inputStream.read()).isEqualTo(4);
        assertThat(amountSkipped).isEqualTo(4L);
    }

    @Test
    public void inputStreamSkipMoreThanAvailable() throws Exception {
        long amountSkipped = this.inputStream.skip(257);
        assertThat(this.inputStream.read()).isEqualTo((-1));
        assertThat(amountSkipped).isEqualTo(256L);
    }

    @Test
    public void inputStreamSkipPastEnd() throws Exception {
        this.inputStream.skip(256);
        long amountSkipped = this.inputStream.skip(1);
        assertThat(amountSkipped).isEqualTo(0L);
    }

    @Test
    public void subsectionNegativeOffset() {
        assertThatExceptionOfType(IndexOutOfBoundsException.class).isThrownBy(() -> this.file.getSubsection((-1), 1));
    }

    @Test
    public void subsectionNegativeLength() {
        assertThatExceptionOfType(IndexOutOfBoundsException.class).isThrownBy(() -> this.file.getSubsection(0, (-1)));
    }

    @Test
    public void subsectionZeroLength() throws Exception {
        RandomAccessData subsection = this.file.getSubsection(0, 0);
        assertThat(subsection.getInputStream().read()).isEqualTo((-1));
    }

    @Test
    public void subsectionTooBig() {
        this.file.getSubsection(0, 256);
        assertThatExceptionOfType(IndexOutOfBoundsException.class).isThrownBy(() -> this.file.getSubsection(0, 257));
    }

    @Test
    public void subsectionTooBigWithOffset() {
        this.file.getSubsection(1, 255);
        assertThatExceptionOfType(IndexOutOfBoundsException.class).isThrownBy(() -> this.file.getSubsection(1, 256));
    }

    @Test
    public void subsection() throws Exception {
        RandomAccessData subsection = this.file.getSubsection(1, 1);
        assertThat(subsection.getInputStream().read()).isEqualTo(1);
    }

    @Test
    public void inputStreamReadPastSubsection() throws Exception {
        RandomAccessData subsection = this.file.getSubsection(1, 2);
        InputStream inputStream = subsection.getInputStream();
        assertThat(inputStream.read()).isEqualTo(1);
        assertThat(inputStream.read()).isEqualTo(2);
        assertThat(inputStream.read()).isEqualTo((-1));
    }

    @Test
    public void inputStreamReadBytesPastSubsection() throws Exception {
        RandomAccessData subsection = this.file.getSubsection(1, 2);
        InputStream inputStream = subsection.getInputStream();
        byte[] b = new byte[3];
        int amountRead = inputStream.read(b);
        assertThat(b).isEqualTo(new byte[]{ 1, 2, 0 });
        assertThat(amountRead).isEqualTo(2);
    }

    @Test
    public void inputStreamSkipPastSubsection() throws Exception {
        RandomAccessData subsection = this.file.getSubsection(1, 2);
        InputStream inputStream = subsection.getInputStream();
        assertThat(inputStream.skip(3)).isEqualTo(2L);
        assertThat(inputStream.read()).isEqualTo((-1));
    }

    @Test
    public void inputStreamSkipNegative() throws Exception {
        assertThat(this.inputStream.skip((-1))).isEqualTo(0L);
    }

    @Test
    public void getFile() {
        assertThat(this.file.getFile()).isEqualTo(this.tempFile);
    }

    @Test
    public void concurrentReads() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        List<Future<Boolean>> results = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            results.add(executorService.submit(() -> {
                InputStream subsectionInputStream = this.file.getSubsection(0, 256).getInputStream();
                byte[] b = new byte[256];
                subsectionInputStream.read(b);
                return Arrays.equals(b, RandomAccessDataFileTests.BYTES);
            }));
        }
        for (Future<Boolean> future : results) {
            assertThat(future.get()).isTrue();
        }
    }
}

