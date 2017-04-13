

package org.apache.ibatis.submitted.ognl_enum;


public class AmplEnumWithOgnlTest {
    private static org.apache.ibatis.session.SqlSessionFactory sqlSessionFactory;

    @org.junit.BeforeClass
    public static void initDatabase() throws java.lang.Exception {
        java.sql.Connection conn = null;
        try {
            java.lang.Class.forName("org.hsqldb.jdbcDriver");
            conn = java.sql.DriverManager.getConnection("jdbc:hsqldb:mem:ognl_enum", "sa", "");
            java.io.Reader reader = org.apache.ibatis.io.Resources.getResourceAsReader("org/apache/ibatis/submitted/ognl_enum/CreateDB.sql");
            org.apache.ibatis.jdbc.ScriptRunner runner = new org.apache.ibatis.jdbc.ScriptRunner(conn);
            runner.setLogWriter(null);
            runner.setErrorLogWriter(null);
            runner.runScript(reader);
            conn.commit();
            reader.close();
            reader = org.apache.ibatis.io.Resources.getResourceAsReader("org/apache/ibatis/submitted/ognl_enum/ibatisConfig.xml");
            org.apache.ibatis.submitted.ognl_enum.AmplEnumWithOgnlTest.sqlSessionFactory = new org.apache.ibatis.session.SqlSessionFactoryBuilder().build(reader);
            reader.close();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    @org.junit.Test
    public void testEnumWithOgnl() {
        org.apache.ibatis.session.SqlSession sqlSession = org.apache.ibatis.submitted.ognl_enum.AmplEnumWithOgnlTest.sqlSessionFactory.openSession();
        org.apache.ibatis.submitted.ognl_enum.PersonMapper personMapper = sqlSession.getMapper(org.apache.ibatis.submitted.ognl_enum.PersonMapper.class);
        java.util.List<org.apache.ibatis.submitted.ognl_enum.Person> persons = personMapper.selectAllByType(null);
        org.junit.Assert.assertEquals("Persons must contain 3 persons", 3, persons.size());
        sqlSession.close();
    }

    @org.junit.Test
    public void testEnumWithOgnlDirector() {
        org.apache.ibatis.session.SqlSession sqlSession = org.apache.ibatis.submitted.ognl_enum.AmplEnumWithOgnlTest.sqlSessionFactory.openSession();
        org.apache.ibatis.submitted.ognl_enum.PersonMapper personMapper = sqlSession.getMapper(org.apache.ibatis.submitted.ognl_enum.PersonMapper.class);
        java.util.List<org.apache.ibatis.submitted.ognl_enum.Person> persons = personMapper.selectAllByType(org.apache.ibatis.submitted.ognl_enum.Person.Type.DIRECTOR);
        org.junit.Assert.assertEquals("Persons must contain 1 persons", 1, persons.size());
        sqlSession.close();
    }

    @org.junit.Test
    public void testEnumWithOgnlDirectorNameAttribute() {
        org.apache.ibatis.session.SqlSession sqlSession = org.apache.ibatis.submitted.ognl_enum.AmplEnumWithOgnlTest.sqlSessionFactory.openSession();
        org.apache.ibatis.submitted.ognl_enum.PersonMapper personMapper = sqlSession.getMapper(org.apache.ibatis.submitted.ognl_enum.PersonMapper.class);
        java.util.List<org.apache.ibatis.submitted.ognl_enum.Person> persons = personMapper.selectAllByTypeNameAttribute(org.apache.ibatis.submitted.ognl_enum.Person.Type.DIRECTOR);
        org.junit.Assert.assertEquals("Persons must contain 1 persons", 1, persons.size());
        sqlSession.close();
    }

    @org.junit.Test
    public void testEnumWithOgnlDirectorWithInterface() {
        org.apache.ibatis.session.SqlSession sqlSession = org.apache.ibatis.submitted.ognl_enum.AmplEnumWithOgnlTest.sqlSessionFactory.openSession();
        org.apache.ibatis.submitted.ognl_enum.PersonMapper personMapper = sqlSession.getMapper(org.apache.ibatis.submitted.ognl_enum.PersonMapper.class);
        java.util.List<org.apache.ibatis.submitted.ognl_enum.Person> persons = personMapper.selectAllByTypeWithInterface(new org.apache.ibatis.submitted.ognl_enum.PersonMapper.PersonType() {
            @java.lang.Override
            public org.apache.ibatis.submitted.ognl_enum.Person.Type getType() {
                return org.apache.ibatis.submitted.ognl_enum.Person.Type;
            }
        });
        org.junit.Assert.assertEquals("Persons must contain 1 persons", 1, persons.size());
        sqlSession.close();
    }

    @org.junit.Test
    public void testEnumWithOgnlDirectorNameAttributeWithInterface() {
        org.apache.ibatis.session.SqlSession sqlSession = org.apache.ibatis.submitted.ognl_enum.AmplEnumWithOgnlTest.sqlSessionFactory.openSession();
        org.apache.ibatis.submitted.ognl_enum.PersonMapper personMapper = sqlSession.getMapper(org.apache.ibatis.submitted.ognl_enum.PersonMapper.class);
        java.util.List<org.apache.ibatis.submitted.ognl_enum.Person> persons = personMapper.selectAllByTypeNameAttributeWithInterface(new org.apache.ibatis.submitted.ognl_enum.PersonMapper.PersonType() {
            @java.lang.Override
            public org.apache.ibatis.submitted.ognl_enum.Person.Type getType() {
                return org.apache.ibatis.submitted.ognl_enum.Person.Type;
            }
        });
        org.junit.Assert.assertEquals("Persons must contain 1 persons", 1, persons.size());
        sqlSession.close();
    }
}

