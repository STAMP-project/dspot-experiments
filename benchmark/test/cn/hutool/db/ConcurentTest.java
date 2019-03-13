package cn.hutool.db;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.db.handler.EntityListHandler;
import java.sql.SQLException;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;


/**
 * SqlRunner??????
 *
 * @author looly
 */
@Ignore
public class ConcurentTest {
    private Db db;

    @Test
    public void findTest() {
        for (int i = 0; i < 10000; i++) {
            ThreadUtil.execute(new Runnable() {
                @Override
                public void run() {
                    List<Entity> find = null;
                    try {
                        find = db.find(CollectionUtil.newArrayList("name AS name2"), Entity.create("user"), new EntityListHandler());
                    } catch (SQLException e) {
                        throw new DbRuntimeException(e);
                    }
                    Console.log(find);
                }
            });
        }
        // ??????????????sleep??????????
        ThreadUtil.sleep(5000);
    }
}

