package com.baeldung.java8;


import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.stream.StreamSupport;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import static com.google.common.io.Files.fileTreeTraverser;


public class JavaFolderSizeUnitTest {
    private final long EXPECTED_SIZE = 24;

    private String path;

    @Test
    public void whenGetFolderSizeRecursive_thenCorrect() {
        final File folder = new File(path);
        final long size = getFolderSize(folder);
        Assert.assertEquals(EXPECTED_SIZE, size);
    }

    @Test
    public void whenGetFolderSizeUsingJava7_thenCorrect() throws IOException {
        final AtomicLong size = new AtomicLong(0);
        final Path folder = Paths.get(path);
        Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                size.addAndGet(attrs.size());
                return FileVisitResult.CONTINUE;
            }
        });
        Assert.assertEquals(EXPECTED_SIZE, size.longValue());
    }

    @Test
    public void whenGetFolderSizeUsingJava8_thenCorrect() throws IOException {
        final Path folder = Paths.get(path);
        final long size = Files.walk(folder).filter(( p) -> p.toFile().isFile()).mapToLong(( p) -> p.toFile().length()).sum();
        Assert.assertEquals(EXPECTED_SIZE, size);
    }

    @Test
    public void whenGetFolderSizeUsingApacheCommonsIO_thenCorrect() {
        final File folder = new File(path);
        final long size = FileUtils.sizeOfDirectory(folder);
        Assert.assertEquals(EXPECTED_SIZE, size);
    }

    @Test
    public void whenGetFolderSizeUsingGuava_thenCorrect() {
        final File folder = new File(path);
        final Iterable<File> files = fileTreeTraverser().breadthFirstTraversal(folder);
        final long size = StreamSupport.stream(files.spliterator(), false).filter(File::isFile).mapToLong(File::length).sum();
        Assert.assertEquals(EXPECTED_SIZE, size);
    }

    @Test
    public void whenGetReadableSize_thenCorrect() {
        final File folder = new File(path);
        final long size = getFolderSize(folder);
        final String[] units = new String[]{ "B", "KB", "MB", "GB", "TB" };
        final int unitIndex = ((int) ((Math.log10(size)) / 3));
        final double unitValue = 1 << (unitIndex * 10);
        final String readableSize = ((new DecimalFormat("#,##0.#").format((size / unitValue))) + " ") + (units[unitIndex]);
        Assert.assertEquals(((EXPECTED_SIZE) + " B"), readableSize);
    }
}

