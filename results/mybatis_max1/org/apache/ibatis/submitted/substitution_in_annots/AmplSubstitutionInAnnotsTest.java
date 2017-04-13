

package org.apache.ibatis.submitted.substitution_in_annots;


public class AmplSubstitutionInAnnotsTest {
    protected static org.apache.ibatis.session.SqlSessionFactory sqlSessionFactory;

    @org.junit.BeforeClass
    public static void setUp() throws java.lang.Exception {
        java.lang.Class.forName("org.hsqldb.jdbcDriver");
        java.sql.Connection c = java.sql.DriverManager.getConnection("jdbc:hsqldb:mem:annots", "sa", "");
        java.io.Reader reader = org.apache.ibatis.io.Resources.getResourceAsReader("org/apache/ibatis/submitted/substitution_in_annots/CreateDB.sql");
        org.apache.ibatis.jdbc.ScriptRunner runner = new org.apache.ibatis.jdbc.ScriptRunner(c);
        runner.setLogWriter(null);
        runner.setErrorLogWriter(null);
        runner.runScript(reader);
        c.commit();
        reader.close();
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        org.apache.ibatis.mapping.Environment environment = new org.apache.ibatis.mapping.Environment("test", new org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory(), new org.apache.ibatis.datasource.unpooled.UnpooledDataSource("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:annots", null));
        configuration.setEnvironment(environment);
        configuration.addMapper(org.apache.ibatis.submitted.substitution_in_annots.SubstitutionInAnnotsMapper.class);
        org.apache.ibatis.submitted.substitution_in_annots.AmplSubstitutionInAnnotsTest.sqlSessionFactory = new org.apache.ibatis.session.SqlSessionFactoryBuilder().build(configuration);
    }

    @org.junit.Test
    public void testSubstitutionWithXml() {
        org.apache.ibatis.session.SqlSession sqlSession = org.apache.ibatis.submitted.substitution_in_annots.AmplSubstitutionInAnnotsTest.sqlSessionFactory.openSession();
        try {
            org.apache.ibatis.submitted.substitution_in_annots.SubstitutionInAnnotsMapper mapper = sqlSession.getMapper(org.apache.ibatis.submitted.substitution_in_annots.SubstitutionInAnnotsMapper.class);
            org.junit.Assert.assertEquals("Barney", mapper.getPersonNameByIdWithXml(4));
        } finally {
            sqlSession.close();
        }
    }

    @org.junit.Test
    public void testSubstitutionWithAnnotsValue() {
        org.apache.ibatis.session.SqlSession sqlSession = org.apache.ibatis.submitted.substitution_in_annots.AmplSubstitutionInAnnotsTest.sqlSessionFactory.openSession();
        try {
            org.apache.ibatis.submitted.substitution_in_annots.SubstitutionInAnnotsMapper mapper = sqlSession.getMapper(org.apache.ibatis.submitted.substitution_in_annots.SubstitutionInAnnotsMapper.class);
            org.junit.Assert.assertEquals("Barney", mapper.getPersonNameByIdWithAnnotsValue(4));
        } finally {
            sqlSession.close();
        }
    }

    @org.junit.Test
    public void testSubstitutionWithAnnotsParameter() {
        org.apache.ibatis.session.SqlSession sqlSession = org.apache.ibatis.submitted.substitution_in_annots.AmplSubstitutionInAnnotsTest.sqlSessionFactory.openSession();
        try {
            org.apache.ibatis.submitted.substitution_in_annots.SubstitutionInAnnotsMapper mapper = sqlSession.getMapper(org.apache.ibatis.submitted.substitution_in_annots.SubstitutionInAnnotsMapper.class);
            org.junit.Assert.assertEquals("Barney", mapper.getPersonNameByIdWithAnnotsParameter(4));
        } finally {
            sqlSession.close();
        }
    }

    @org.junit.Test
    public void testSubstitutionWithAnnotsParamAnnot() {
        org.apache.ibatis.session.SqlSession sqlSession = org.apache.ibatis.submitted.substitution_in_annots.AmplSubstitutionInAnnotsTest.sqlSessionFactory.openSession();
        try {
            org.apache.ibatis.submitted.substitution_in_annots.SubstitutionInAnnotsMapper mapper = sqlSession.getMapper(org.apache.ibatis.submitted.substitution_in_annots.SubstitutionInAnnotsMapper.class);
            org.junit.Assert.assertEquals("Barney", mapper.getPersonNameByIdWithAnnotsParamAnnot(4));
        } finally {
            sqlSession.close();
        }
    }
}

