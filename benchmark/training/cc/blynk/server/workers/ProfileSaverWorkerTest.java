package cc.blynk.server.workers;


import cc.blynk.server.core.BlockingIOProcessor;
import cc.blynk.server.core.dao.FileManager;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.dao.UserKey;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.stats.GlobalStats;
import cc.blynk.utils.AppNameUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 3/3/2015.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class ProfileSaverWorkerTest {
    @Mock
    private UserDao userDao;

    @Mock
    private FileManager fileManager;

    @Mock
    private GlobalStats stats;

    private BlockingIOProcessor blockingIOProcessor = new BlockingIOProcessor(4, 1);

    @Test
    public void testCorrectProfilesAreSaved() throws IOException {
        ProfileSaverWorker profileSaverWorker = new ProfileSaverWorker(userDao, fileManager, new cc.blynk.server.db.DBManager(blockingIOProcessor, true));
        User user1 = new User("1", "", AppNameUtil.BLYNK, "local", "127.0.0.1", false, false);
        User user2 = new User("2", "", AppNameUtil.BLYNK, "local", "127.0.0.1", false, false);
        User user3 = new User("3", "", AppNameUtil.BLYNK, "local", "127.0.0.1", false, false);
        User user4 = new User("4", "", AppNameUtil.BLYNK, "local", "127.0.0.1", false, false);
        ConcurrentMap<UserKey, User> userMap = new ConcurrentHashMap<>();
        userMap.put(new UserKey(user1), user1);
        userMap.put(new UserKey(user2), user2);
        userMap.put(new UserKey(user3), user3);
        userMap.put(new UserKey(user4), user4);
        Mockito.when(userDao.getUsers()).thenReturn(userMap);
        profileSaverWorker.run();
        Mockito.verify(fileManager, Mockito.times(4)).overrideUserFile(ArgumentMatchers.any());
        Mockito.verify(fileManager).overrideUserFile(user1);
        Mockito.verify(fileManager).overrideUserFile(user2);
        Mockito.verify(fileManager).overrideUserFile(user3);
        Mockito.verify(fileManager).overrideUserFile(user4);
    }

    @Test
    public void testNoProfileChanges() throws Exception {
        User user1 = new User("1", "", AppNameUtil.BLYNK, "local", "127.0.0.1", false, false);
        User user2 = new User("2", "", AppNameUtil.BLYNK, "local", "127.0.0.1", false, false);
        User user3 = new User("3", "", AppNameUtil.BLYNK, "local", "127.0.0.1", false, false);
        User user4 = new User("4", "", AppNameUtil.BLYNK, "local", "127.0.0.1", false, false);
        Map<UserKey, User> userMap = new HashMap<>();
        userMap.put(new UserKey("1", AppNameUtil.BLYNK), user1);
        userMap.put(new UserKey("2", AppNameUtil.BLYNK), user2);
        userMap.put(new UserKey("3", AppNameUtil.BLYNK), user3);
        userMap.put(new UserKey("4", AppNameUtil.BLYNK), user4);
        Thread.sleep(1);
        ProfileSaverWorker profileSaverWorker = new ProfileSaverWorker(userDao, fileManager, new cc.blynk.server.db.DBManager(blockingIOProcessor, true));
        Mockito.when(userDao.getUsers()).thenReturn(userMap);
        profileSaverWorker.run();
        Mockito.verifyNoMoreInteractions(fileManager);
    }
}

