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
package org.terasology.rendering.nui.layers.mainMenu.savedGames;


import GameManifest.DEFAULT_FILE_NAME;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class GameProviderTest {
    private static final String GAME_1 = "Game 1";

    private static final Path TMP_SAVES_FOLDER_PATH = Paths.get("out", "test", "engine-tests", "tmp", "saves").toAbsolutePath();

    private static final Path TMP_RECORDS_FOLDER_PATH = Paths.get("out", "test", "engine-tests", "tmp", "records").toAbsolutePath();

    private static final Path TMP_SAVE_GAME_PATH = GameProviderTest.TMP_SAVES_FOLDER_PATH.resolve(GameProviderTest.GAME_1);

    private static final Path TMP_RECORD_GAME_PATH = GameProviderTest.TMP_RECORDS_FOLDER_PATH.resolve(GameProviderTest.GAME_1);

    private static final String GAME_MANIFEST_JSON = "gameManifest.json";

    private static String MANIFEST_EXAMPLE;

    @Test
    public void emptySavedGameManifestTest() throws IOException {
        Files.createDirectory(GameProviderTest.TMP_SAVE_GAME_PATH);
        Files.createFile(GameProviderTest.TMP_SAVE_GAME_PATH.resolve(DEFAULT_FILE_NAME));
        final List<GameInfo> result = GameProvider.getSavedGames();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void noSavedGames() {
        final List<GameInfo> result = GameProvider.getSavedGames();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void noSavedRecordings() {
        final List<GameInfo> result = GameProvider.getSavedRecordings();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void emptyRecordingGameManifestTest() throws IOException {
        Files.createDirectory(GameProviderTest.TMP_RECORD_GAME_PATH);
        Files.createFile(GameProviderTest.TMP_RECORD_GAME_PATH.resolve(DEFAULT_FILE_NAME));
        final List<GameInfo> result = GameProvider.getSavedRecordings();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void successTest() throws IOException {
        Files.createDirectories(GameProviderTest.TMP_SAVE_GAME_PATH);
        Path manifestFilePath = GameProviderTest.TMP_SAVE_GAME_PATH.resolve(DEFAULT_FILE_NAME);
        writeToFile(manifestFilePath, GameProviderTest.MANIFEST_EXAMPLE);
        final List<GameInfo> result = GameProvider.getSavedGames();
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        final GameInfo gameInfo = result.get(0);
        Assert.assertNotNull(gameInfo);
        Assert.assertNotNull(gameInfo.getManifest());
        Assert.assertNotNull(gameInfo.getTimestamp());
        Assert.assertNotNull(gameInfo.getSavePath());
        Assert.assertEquals(GameProviderTest.GAME_1, gameInfo.getManifest().getTitle());
        Assert.assertEquals(GameProviderTest.TMP_SAVE_GAME_PATH, gameInfo.getSavePath());
    }

    @Test
    public void emptySavesGameFolderTest() {
        final boolean res = GameProvider.isSavesFolderEmpty();
        Assert.assertTrue(res);
    }

    @Test
    public void notEmptySavesGameFolderTest() throws IOException {
        Files.createDirectories(GameProviderTest.TMP_SAVE_GAME_PATH);
        Files.createFile(GameProviderTest.TMP_SAVE_GAME_PATH.resolve(DEFAULT_FILE_NAME));
        final boolean res = GameProvider.isSavesFolderEmpty();
        Assert.assertFalse(res);
    }

    @Test
    public void getNextGameNameDefaultNoSavesTest() {
        final String name = GameProvider.getNextGameName();
        Assert.assertNotNull(name);
        Assert.assertEquals(GameProviderTest.GAME_1, name);
    }

    @Test
    public void getNextGameNameNumberTest() throws IOException {
        Files.createDirectories(GameProviderTest.TMP_SAVE_GAME_PATH);
        Path manifestFilePath = GameProviderTest.TMP_SAVE_GAME_PATH.resolve(DEFAULT_FILE_NAME);
        writeToFile(manifestFilePath, GameProviderTest.MANIFEST_EXAMPLE);
        final String name = GameProvider.getNextGameName();
        Assert.assertNotNull(name);
        Assert.assertEquals("Game 2", name);
    }

    @Test
    public void getNextGameNameDefaultExceptionTest() throws IOException {
        Files.createDirectories(GameProviderTest.TMP_SAVE_GAME_PATH);
        Path manifestFilePath = GameProviderTest.TMP_SAVE_GAME_PATH.resolve(DEFAULT_FILE_NAME);
        writeToFile(manifestFilePath, GameProviderTest.MANIFEST_EXAMPLE.replace(GameProviderTest.GAME_1, "bad"));
        final String name = GameProvider.getNextGameName();
        Assert.assertNotNull(name);
        Assert.assertEquals(GameProviderTest.GAME_1, name);
    }
}

