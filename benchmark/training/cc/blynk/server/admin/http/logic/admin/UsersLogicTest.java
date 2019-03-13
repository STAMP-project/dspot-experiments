package cc.blynk.server.admin.http.logic.admin;


import cc.blynk.core.http.Response;
import cc.blynk.server.admin.http.logic.UsersLogic;
import cc.blynk.server.core.dao.ReportingDiskDao;
import cc.blynk.server.core.dao.SessionDao;
import cc.blynk.server.core.dao.UserDao;
import cc.blynk.server.core.model.auth.Session;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.db.DBManager;
import cc.blynk.utils.AppNameUtil;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class UsersLogicTest {
    @Mock
    private UserDao userDao;

    @Spy
    private SessionDao sessionDao;

    @Mock
    private DBManager dbManager;

    @Mock
    private ReportingDiskDao reportingDao;

    private User user;

    @Mock
    private Session session;

    private UsersLogic usersLogic;

    private static final String TEST_USER = "test_user";

    private static Path userFile;

    private static Path deletedUserFile;

    private static final String DELETED_DATA_DIR_NAME = "deleted";

    @Test
    public void deleteUserByName() throws Exception {
        Response response = usersLogic.deleteUserByName((((UsersLogicTest.TEST_USER) + "-") + (AppNameUtil.BLYNK)));
        Assert.assertEquals(HttpResponseStatus.OK, response.status());
        Assert.assertFalse(Files.exists(UsersLogicTest.userFile));
        Assert.assertTrue(Files.exists(UsersLogicTest.deletedUserFile));
    }

    @Test
    public void deleteFakeUserByName() throws Exception {
        Response response = usersLogic.deleteUserByName((("fake user" + "-") + (AppNameUtil.BLYNK)));
        Assert.assertEquals(HttpResponseStatus.NOT_FOUND, response.status());
    }
}

