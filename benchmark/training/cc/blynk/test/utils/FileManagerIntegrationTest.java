package cc.blynk.test.utils;


import cc.blynk.server.core.dao.FileManager;
import cc.blynk.server.core.dao.UserKey;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.utils.AppNameUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * User: ddumanskiy
 * Date: 09.12.13
 * Time: 8:07
 */
public class FileManagerIntegrationTest {
    private final User user1 = new User("name1", "pass1", AppNameUtil.BLYNK, "local", "127.0.0.1", false, false);

    private final User user2 = new User("name2", "pass2", AppNameUtil.BLYNK, "local", "127.0.0.1", false, false);

    private FileManager fileManager;

    @Test
    public void testGenerateFileName() {
        Path file = fileManager.generateFileName(user1.email, user1.appName);
        Assert.assertEquals("name1.Blynk.user", file.getFileName().toString());
    }

    @Test
    public void testNotNullTokenManager() throws IOException {
        fileManager.overrideUserFile(user1);
        Map<UserKey, User> users = fileManager.deserializeUsers();
        Assert.assertNotNull(users);
        Assert.assertNotNull(users.get(new UserKey(user1.email, AppNameUtil.BLYNK)));
    }

    @Test
    public void testCreationTempFile() throws IOException {
        fileManager.overrideUserFile(user1);
        // file existence ignored
        fileManager.overrideUserFile(user1);
    }

    @Test
    public void testReadListOfFiles() throws IOException {
        fileManager.overrideUserFile(user1);
        fileManager.overrideUserFile(user2);
        Path fakeFile = Paths.get(fileManager.getDataDir().toString(), "123.txt");
        Files.deleteIfExists(fakeFile);
        Files.createFile(fakeFile);
        Map<UserKey, User> users = fileManager.deserializeUsers();
        Assert.assertNotNull(users);
        Assert.assertEquals(2, users.size());
        Assert.assertNotNull(users.get(new UserKey(user1.email, AppNameUtil.BLYNK)));
        Assert.assertNotNull(users.get(new UserKey(user2.email, AppNameUtil.BLYNK)));
    }

    @Test
    public void testOverrideFiles() throws IOException {
        fileManager.overrideUserFile(user1);
        fileManager.overrideUserFile(user1);
        Map<UserKey, User> users = fileManager.deserializeUsers();
        Assert.assertNotNull(users);
        Assert.assertNotNull(users.get(new UserKey(user1.email, AppNameUtil.BLYNK)));
    }
}

