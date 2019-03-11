/**
 * Copyright 2018 MovingBlocks
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
package org.terasology.persistence.internal;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class GamePreviewImageProviderTest {
    private static final String PREVIEWS = "previews";

    private static final String DEFAULT_IMAGE_NAME = "1.jpg";

    private static final Path TMP_FOLDER = Paths.get("out", "test", "engine-tests", "tmp", GamePreviewImageProviderTest.PREVIEWS).toAbsolutePath();

    private static final Path TMP_PREVIEWS_FOLDER = GamePreviewImageProviderTest.TMP_FOLDER.resolve(GamePreviewImageProviderTest.PREVIEWS);

    @Test
    public void getAllPreviewImagesEmptyTest() {
        final List<BufferedImage> result = GamePreviewImageProvider.getAllPreviewImages(GamePreviewImageProviderTest.TMP_FOLDER);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void getAllPreviewImagesNotEmptyFolderButEmptyFileTest() throws IOException {
        Files.createDirectories(GamePreviewImageProviderTest.TMP_PREVIEWS_FOLDER);
        Files.createFile(GamePreviewImageProviderTest.TMP_PREVIEWS_FOLDER.resolve(GamePreviewImageProviderTest.DEFAULT_IMAGE_NAME));
        final List<BufferedImage> result = GamePreviewImageProvider.getAllPreviewImages(GamePreviewImageProviderTest.TMP_FOLDER);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void getNextGamePreviewImagePathEmptyFolderTest() {
        final Path imagePath = GamePreviewImageProvider.getNextGamePreviewImagePath(GamePreviewImageProviderTest.TMP_FOLDER);
        Assert.assertNotNull(imagePath);
        Assert.assertEquals(GamePreviewImageProviderTest.TMP_PREVIEWS_FOLDER.resolve(GamePreviewImageProviderTest.DEFAULT_IMAGE_NAME), imagePath);
    }

    @Test
    public void getNextGamePreviewImagePathNotEmptyFolderTest() throws IOException {
        Files.createDirectories(GamePreviewImageProviderTest.TMP_PREVIEWS_FOLDER);
        Files.createFile(GamePreviewImageProviderTest.TMP_PREVIEWS_FOLDER.resolve(GamePreviewImageProviderTest.DEFAULT_IMAGE_NAME));
        final Path imagePath = GamePreviewImageProvider.getNextGamePreviewImagePath(GamePreviewImageProviderTest.TMP_FOLDER);
        Assert.assertNotNull(imagePath);
        Assert.assertEquals(GamePreviewImageProviderTest.TMP_PREVIEWS_FOLDER.resolve("2.jpg"), imagePath);
    }

    @Test
    public void getNextGamePreviewImagePathOldestFileTest() throws IOException, InterruptedException {
        Files.createDirectories(GamePreviewImageProviderTest.TMP_PREVIEWS_FOLDER);
        Files.createFile(GamePreviewImageProviderTest.TMP_PREVIEWS_FOLDER.resolve("1.jpg"));
        Files.createFile(GamePreviewImageProviderTest.TMP_PREVIEWS_FOLDER.resolve("2.jpg"));
        Files.createFile(GamePreviewImageProviderTest.TMP_PREVIEWS_FOLDER.resolve("3.jpg"));
        Files.createFile(GamePreviewImageProviderTest.TMP_PREVIEWS_FOLDER.resolve("4.jpg"));
        Files.createFile(GamePreviewImageProviderTest.TMP_PREVIEWS_FOLDER.resolve("5.jpg"));
        final Path expectedOldestFile = GamePreviewImageProviderTest.TMP_PREVIEWS_FOLDER.resolve("3.jpg");
        Files.setLastModifiedTime(expectedOldestFile, FileTime.fromMillis(0));
        final Path imagePath = GamePreviewImageProvider.getNextGamePreviewImagePath(GamePreviewImageProviderTest.TMP_FOLDER);
        Assert.assertNotNull(imagePath);
        Assert.assertEquals(expectedOldestFile, imagePath);
    }
}

