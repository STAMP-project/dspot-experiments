package cn.dreampie.example;


import User.dao;
import cn.dreampie.orm.Record;
import cn.dreampie.orm.TableSetting;
import cn.dreampie.orm.page.FullPage;
import cn.dreampie.resource.user.model.User;
import cn.dreampie.resource.user.model.UserInfo;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 * Created by ice on 15-1-4.
 */
@FixMethodOrder(MethodSorters.JVM)
public class SqlTest {
    private static final String tableName = "tst_" + (new Date().getTime());

    @Test
    public void testTable() {
        Record recordDAO = new Record();
        recordDAO.execute((("CREATE TABLE " + (SqlTest.tableName)) + "(id INT NOT NULL AUTO_INCREMENT,name VARCHAR(100) NOT NULL,PRIMARY KEY(id));"));
    }

    @Test
    public void testColumn() {
        Record tstDAO = new Record(new TableSetting(SqlTest.tableName));
        Iterator iterator = tstDAO.getTableMeta().getColumnMetadata().values().iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next().toString());
        } 
    }

    private TableSetting tableSetting = new TableSetting("sec_user", true);

    @Test
    public void testSql() {
        Record recordDAO = new Record();
        List<Record> records = recordDAO.find("select * from sec_user");
        System.out.println(records.size());
        recordDAO.execute("create table tb1 select * from sec_user");
        List<Record> records1 = recordDAO.find("select * from tb1");
        System.out.println(records1.size());
        recordDAO.execute("insert into tb1(sid,username,password,providername,created_at)values(1000,'xx','11','12','2014-10-00 10:00:00');");
    }

    @Test
    public void testSave() {
        User u = new User().set("sid", 1).set("username", "a").set("providername", "test").set("password", "123456").set("created_at", new Date());
        u.save();
        User u1 = new User().set("sid", 1).set("username", "a").set("providername", "test").set("password", "123456").set("created_at", new Date());
        User u2 = new User().set("sid", 1).set("username", "a").set("providername", "test").set("password", "123456").set("created_at", new Date());
        UserInfo userInfo = null;
        if ((u.get("user_info")) == null) {
            userInfo = new UserInfo().set("gender", 0);
        } else {
            userInfo = u.get("user_info");
        }
        if (dao.save(u1, u2)) {
            System.out.println((((u.get("id")) + "/") + (u1.get("id"))));
            userInfo.set("user_id", u.get("id"));
            userInfo.save();
        }
        Record recordDAO = new Record(tableSetting);
        recordDAO.reNew().set("sid", 2).set("username", "test").set("providername", "test").set("password", "123456").set("created_at", new Date()).save();
        Record r1 = recordDAO.reNew().set("sid", 2).set("username", "test").set("providername", "test").set("password", "123456").set("created_at", new Date());
        Record r2 = recordDAO.reNew().set("sid", 2).set("username", "test").set("providername", "test").set("password", "123456").set("created_at", new Date());
        recordDAO.save(r1, r2);
    }

    @Test
    public void testFind() {
        List<User> users = dao.findAll();
        Long id = 1L;
        Long sid = 1L;
        for (User user : users) {
            id = user.<Long>get("id");
            sid = user.<Long>get("sid");
            System.out.println(user.<String>get("username"));
        }
        Record recordDAO = new Record(tableSetting);
        List<Record> records = recordDAO.findAll();
        for (Record r : records) {
            System.out.println(r.<String>get("username"));
        }
        User us = dao.findByIds(id, sid);
        System.out.println(((("findByIds," + (us.get("id"))) + ",") + (us.get("sid"))));
    }

    @Test
    public void testPaginate() {
        FullPage<User> users = dao.unCache().fullPaginateAll(1, 10);
        for (User user : users.getList()) {
            System.out.println(user.<String>get("username"));
        }
        Record recordDAO = new Record(tableSetting);
        FullPage<Record> records = recordDAO.fullPaginate(1, 10, "SELECT * FROM sec_user");
        for (Record record : records.getList()) {
            System.out.println(record.<String>get("username"));
        }
        records = recordDAO.unCache().fullPaginate(1, 10, "SELECT * FROM sec_user");
    }

    @Test
    public void testUpdate() {
        List<User> users = dao.findAll();
        for (User user : users) {
            user.set("username", "testupdate").update();
        }
        dao.update("UPDATE sec_user SET username='c' WHERE username='a'");
        Record recordDAO = new Record(tableSetting);
        List<Record> records = recordDAO.findAll();
        int i = 0;
        for (Record record : records) {
            if ((i % 2) == 0)
                record.set("username", "testupdxx").update();

            i++;
        }
    }

    @Test
    public void testExcute() {
        // ????sql??
        dao.execute("UPDATE sec_user SET username='b' WHERE username='c'", "UPDATE sec_user SET username='x' WHERE username='test'");
    }

    @Test
    public void testDelete() {
        List<User> users = dao.findAll();
        Long id = 1L;
        Long sid = 1L;
        int i = 0;
        for (User user : users) {
            id = user.<Long>get("id");
            sid = user.<Long>get("sid");
            if (i == 0) {
                dao.deleteByIds(id, sid);
            }
            user.delete();
            i++;
        }
        Record recordDAO = new Record(tableSetting);
        recordDAO.deleteById("1");
    }
}

