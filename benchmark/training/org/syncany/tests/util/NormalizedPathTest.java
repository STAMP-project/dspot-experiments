/**
 * Syncany, www.syncany.org
 * Copyright (C) 2011-2016 Philipp C. Heckel <philipp.heckel@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.syncany.tests.util;


import OperatingSystem.UNIX_LIKE;
import OperatingSystem.WINDOWS;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.syncany.util.EnvironmentUtil;
import org.syncany.util.EnvironmentUtil.OperatingSystem;
import org.syncany.util.FileUtil;
import org.syncany.util.NormalizedPath;


public class NormalizedPathTest {
    private OperatingSystem originalOperatingSystem;

    @Test
    public void testGetRelativeFilePath() {
        String expectedResult = "somefile";
        File[] rootFolders = new File[]{ new File("/home/user/Syncany"), new File("/home/user/Syncany/"), new File("/home/user/Syncany//"), new File("/home/user//Syncany"), new File("/home/user//Syncany/"), new File("/home/user//Syncany//") };
        File[] files = new File[]{ new File("/home/user/Syncany/somefile"), new File("/home/user/Syncany/somefile/"), new File("/home/user/Syncany/somefile//"), new File("/home/user/Syncany//somefile"), new File("/home/user/Syncany//somefile/"), new File("/home/user/Syncany//somefile//") };
        for (File rootFolder : rootFolders) {
            for (File file : files) {
                String actualResult = FileUtil.getRelativePath(rootFolder, file);
                Assert.assertEquals((((((("Expected '" + expectedResult) + "' for root folder '") + rootFolder) + "' and file '") + file) + "'"), expectedResult, actualResult);
            }
        }
    }

    @Test
    public void testGetRelativeFilePathSpecialCases() {
        Assert.assertEquals("", FileUtil.getRelativePath(new File("/home/user/"), new File("/home/user")));
        Assert.assertEquals("", FileUtil.getRelativePath(new File("/home/user/"), new File("/home/user/")));
        Assert.assertEquals("", FileUtil.getRelativePath(new File("/home/user/"), new File("/home/user//")));
    }

    @Test
    public void testNameAndParentPathForNormalizedPathsOnWindows() {
        testNameAndParentPathForNormalizedPaths(WINDOWS);
    }

    @Test
    public void testNameAndParentPathForNormalizedPathsOnUnixLikeSystems() {
        testNameAndParentPathForNormalizedPaths(UNIX_LIKE);
    }

    @Test
    public void testNameAndParentPathForNormalizedPathsMoreTests() {
        // Does not depend on OS
        Assert.assertEquals("", new NormalizedPath(null, "Philipp").getParent().toString());
    }

    @Test
    public void testCreatablizationOnWindows() throws Exception {
        EnvironmentUtil.setOperatingSystem(WINDOWS);
        File root = new File("C:\\Philipp");
        Assert.assertEquals("Philipp", new NormalizedPath(root, "Philipp").toCreatable("filename conflict", true).toString());
        Assert.assertEquals("Philipp", new NormalizedPath(root, "Philipp").toCreatable("filename conflict", true).toString());
        Assert.assertEquals("Philipp/image.jpg", new NormalizedPath(root, "Philipp/image.jpg").toCreatable("filename conflict", true).toString());
        Assert.assertEquals("Philipp/image", new NormalizedPath(root, "Philipp/image").toCreatable("filename conflict", true).toString());
        Assert.assertEquals("Philipp/filewithcolons (filename conflict).txt", new NormalizedPath(root, "Philipp/file:with:colons.txt").toCreatable("filename conflict", true).toString());// Cannot happen on Windows

        Assert.assertEquals("Philipp/filewithbackslashes (filename conflict).txt", new NormalizedPath(root, "Philipp/file\\with\\backslashes.txt").toCreatable("filename conflict", true).toString());
        Assert.assertEquals("Philipp/folderwithbackslashes (filename conflict)", new NormalizedPath(root, "Philipp/folder\\with\\backslashes").toCreatable("filename conflict", true).toString());
    }
}

