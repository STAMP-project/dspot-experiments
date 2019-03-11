package tk.mybatis.mapper.test.logic;


import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.mapper.MybatisHelper;
import tk.mybatis.mapper.mapper.TbUserLogicDeleteMapper;
import tk.mybatis.mapper.mapper.TbUserMapper;
import tk.mybatis.mapper.model.TbUser;
import tk.mybatis.mapper.model.TbUserLogicDelete;


public class TestLogicDelete {
    @Test
    public void testLogicDeleteByPrimaryKey() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserMapper mapper = sqlSession.getMapper(TbUserMapper.class);
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);
            deleteByPrimaryKey(3);
            Assert.assertFalse(existsWithPrimaryKey(3));
            Assert.assertTrue(existsWithPrimaryKey(3));
            // ???????????????????0
            Assert.assertEquals(0, deleteByPrimaryKey(9));
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    // ??????????????????????????????????
    @Test
    public void testLogicDelete() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserMapper mapper = sqlSession.getMapper(TbUserMapper.class);
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);
            // ?2?username?test??????1???????????
            TbUserLogicDelete tbUserLogicDelete = new TbUserLogicDelete();
            tbUserLogicDelete.setUsername("test");
            Assert.assertTrue(existsWithPrimaryKey(8));
            // ????????1?
            Assert.assertEquals(1, logicDeleteMapper.delete(tbUserLogicDelete));
            Assert.assertFalse(existsWithPrimaryKey(8));
            // ???????4?
            Assert.assertEquals(4, selectAll().size());
            TbUser tbUser = new TbUser();
            tbUser.setUsername("test");
            Assert.assertEquals(2, mapper.select(tbUser).size());
            // ????2?????????????
            Assert.assertEquals(2, delete(tbUser));
            // ????????4?
            Assert.assertEquals(4, selectAll().size());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testLogicDeleteByExample() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);
            TbUserMapper mapper = sqlSession.getMapper(TbUserMapper.class);
            Example example = new Example(TbUserLogicDelete.class);
            example.createCriteria().andEqualTo("id", 1);
            logicDeleteMapper.deleteByExample(example);
            Assert.assertFalse(existsWithPrimaryKey(1));
            Assert.assertTrue(existsWithPrimaryKey(1));
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    // ???????????????????????????
    @Test
    public void testSelectByPrimaryKey() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);
            Assert.assertNull(selectByPrimaryKey(9));
            TbUserMapper mapper = sqlSession.getMapper(TbUserMapper.class);
            Assert.assertEquals(0, ((int) (selectByPrimaryKey(9).getIsValid())));
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testExistsWithPrimaryKey() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);
            Assert.assertFalse(existsWithPrimaryKey(9));
            TbUserMapper mapper = sqlSession.getMapper(TbUserMapper.class);
            Assert.assertTrue(existsWithPrimaryKey(9));
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    // ?????????????????????????
    @Test
    public void testSelectAll() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);
            Assert.assertEquals(5, selectAll().size());
            TbUserMapper mapper = sqlSession.getMapper(TbUserMapper.class);
            Assert.assertEquals(9, selectAll().size());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    // ??????????????????????????????????
    @Test
    public void selectCount() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);
            TbUserLogicDelete tbUserLogicDelete = new TbUserLogicDelete();
            // ???5??????4???????????0?????????5?
            tbUserLogicDelete.setIsValid(0);
            Assert.assertEquals(5, logicDeleteMapper.selectCount(tbUserLogicDelete));
            // ??????????????????
            TbUserMapper mapper = sqlSession.getMapper(TbUserMapper.class);
            TbUser tbUser = new TbUser();
            Assert.assertEquals(9, mapper.selectCount(tbUser));
            tbUser.setIsValid(0);
            Assert.assertEquals(4, mapper.selectCount(tbUser));
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    // ????????????????????????????????????
    @Test
    public void testSelect() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);
            TbUserLogicDelete tbUserLogicDelete = new TbUserLogicDelete();
            // ???5??????4???????????0?????????5?
            tbUserLogicDelete.setIsValid(0);
            Assert.assertEquals(5, select(tbUserLogicDelete).size());
            tbUserLogicDelete.setUsername("test");
            Assert.assertEquals(1, select(tbUserLogicDelete).size());
            Assert.assertEquals(8, ((long) (select(tbUserLogicDelete).get(0).getId())));
            TbUserMapper mapper = sqlSession.getMapper(TbUserMapper.class);
            TbUser tbUser = new TbUser();
            // ??????????????????
            tbUser.setIsValid(0);
            Assert.assertEquals(4, mapper.select(tbUser).size());
            tbUser.setUsername("test");
            Assert.assertEquals(1, mapper.select(tbUser).size());
            Assert.assertEquals(9, ((long) (mapper.select(tbUser).get(0).getId())));
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testInsert() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);
            TbUserLogicDelete tbUserLogicDelete = new TbUserLogicDelete();
            tbUserLogicDelete.setUsername("test111");
            logicDeleteMapper.insert(tbUserLogicDelete);
            TbUserMapper mapper = sqlSession.getMapper(TbUserMapper.class);
            TbUser tbUser = new TbUser();
            tbUser.setUsername("test222");
            insert(tbUser);
            Assert.assertEquals(1, mapper.selectCount(tbUser));
            TbUserLogicDelete tbUserLogicDelete1 = new TbUserLogicDelete();
            tbUserLogicDelete1.setUsername("test222");
            Assert.assertEquals(0, logicDeleteMapper.selectCount(tbUserLogicDelete1));
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testInsertSelective() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);
            TbUserLogicDelete tbUserLogicDelete = new TbUserLogicDelete();
            tbUserLogicDelete.setUsername("test333");
            logicDeleteMapper.insertSelective(tbUserLogicDelete);
            Assert.assertEquals(1, logicDeleteMapper.selectCount(tbUserLogicDelete));
            TbUserLogicDelete tbUserLogicDelete1 = new TbUserLogicDelete();
            tbUserLogicDelete1.setUsername("test333");
            Assert.assertEquals(1, logicDeleteMapper.selectCount(tbUserLogicDelete1));
            TbUserMapper mapper = sqlSession.getMapper(TbUserMapper.class);
            TbUser tbUser = new TbUser();
            tbUser.setUsername("test333");
            insertSelective(tbUser);
            TbUser tbUser2 = new TbUser();
            tbUser2.setUsername("test333");
            Assert.assertEquals(2, mapper.selectCount(tbUser2));
            Assert.assertEquals(1, logicDeleteMapper.selectCount(tbUserLogicDelete1));
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testUpdate() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);
            TbUserLogicDelete tbUserLogicDelete = logicDeleteMapper.selectByPrimaryKey(1);
            tbUserLogicDelete.setPassword(null);
            updateByPrimaryKey(tbUserLogicDelete);
            Assert.assertNull(select(tbUserLogicDelete).get(0).getPassword());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testUpdateSelective() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);
            TbUserLogicDelete tbUserLogicDelete = logicDeleteMapper.selectByPrimaryKey(1);
            tbUserLogicDelete.setPassword(null);
            updateByPrimaryKeySelective(tbUserLogicDelete);
            Assert.assertEquals("12345678", select(tbUserLogicDelete).get(0).getPassword());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testSelectByExample() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);
            Example example = new Example(TbUserLogicDelete.class);
            example.createCriteria().andEqualTo("id", 9);
            Assert.assertEquals(0, logicDeleteMapper.selectByExample(example).size());
            example.or().andEqualTo("username", "test");
            Assert.assertEquals(1, logicDeleteMapper.selectByExample(example).size());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testSelectByExample2() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);
            // username?test????  ????????
            Example example = new Example(TbUserLogicDelete.class);
            example.createCriteria().andEqualTo("username", "test");
            Assert.assertEquals(1, logicDeleteMapper.selectByExample(example).size());
            // password?dddd????  username?test2????
            example.or().andEqualTo("password", "dddd").orEqualTo("username", "test2");
            Assert.assertEquals(2, logicDeleteMapper.selectByExample(example).size());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testUpdateByExample() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);
            // username?test????  ????????
            Example example = new Example(TbUserLogicDelete.class);
            example.createCriteria().andEqualTo("username", "test");
            TbUserLogicDelete tbUserLogicDelete = new TbUserLogicDelete();
            tbUserLogicDelete.setUsername("123");
            logicDeleteMapper.updateByExample(tbUserLogicDelete, example);
            example.clear();
            example.createCriteria().andEqualTo("username", "123");
            List<TbUserLogicDelete> list = logicDeleteMapper.selectByExample(example);
            Assert.assertEquals(1, list.size());
            Assert.assertNull(list.get(0).getPassword());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    @Test
    public void testUpdateByExampleSelective() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);
            Example example = new Example(TbUserLogicDelete.class);
            example.createCriteria().andEqualTo("username", "test");
            TbUserLogicDelete tbUserLogicDelete = new TbUserLogicDelete();
            tbUserLogicDelete.setUsername("123");
            logicDeleteMapper.updateByExampleSelective(tbUserLogicDelete, example);
            example.clear();
            example.createCriteria().andEqualTo("username", "123");
            List<TbUserLogicDelete> list = logicDeleteMapper.selectByExample(example);
            Assert.assertEquals(1, list.size());
            Assert.assertEquals("gggg", list.get(0).getPassword());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }

    // Example????????????where?????????????????
    @Test
    public void testExampleWithNoCriteria() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            TbUserLogicDeleteMapper logicDeleteMapper = sqlSession.getMapper(TbUserLogicDeleteMapper.class);
            Example example = new Example(TbUserLogicDelete.class);
            TbUserLogicDelete tbUserLogicDelete = new TbUserLogicDelete();
            tbUserLogicDelete.setUsername("123");
            Assert.assertEquals(5, logicDeleteMapper.updateByExample(tbUserLogicDelete, example));
            Assert.assertEquals(5, logicDeleteMapper.updateByExampleSelective(tbUserLogicDelete, example));
            List<TbUserLogicDelete> list = logicDeleteMapper.selectByExample(example);
            Assert.assertEquals(5, list.size());
        } finally {
            sqlSession.rollback();
            sqlSession.close();
        }
    }
}

