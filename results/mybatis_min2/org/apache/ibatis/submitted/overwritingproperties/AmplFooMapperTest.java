

package org.apache.ibatis.submitted.overwritingproperties;


public class AmplFooMapperTest {
    private static final java.lang.String SQL_MAP_CONFIG = "org/apache/ibatis/submitted/overwritingproperties/sqlmap.xml";

    private static org.apache.ibatis.session.SqlSession session;

    @org.junit.BeforeClass
    public static void setUpBeforeClass() {
        try {
            final org.apache.ibatis.session.SqlSessionFactory factory = new org.apache.ibatis.session.SqlSessionFactoryBuilder().build(org.apache.ibatis.io.Resources.getResourceAsReader(org.apache.ibatis.submitted.overwritingproperties.AmplFooMapperTest.SQL_MAP_CONFIG));
            org.apache.ibatis.submitted.overwritingproperties.AmplFooMapperTest.session = factory.openSession();
            java.sql.Connection conn = org.apache.ibatis.submitted.overwritingproperties.AmplFooMapperTest.session.getConnection();
            org.apache.ibatis.jdbc.ScriptRunner runner = new org.apache.ibatis.jdbc.ScriptRunner(conn);
            runner.setLogWriter(null);
            runner.setErrorLogWriter(null);
            java.io.Reader reader = org.apache.ibatis.io.Resources.getResourceAsReader("org/apache/ibatis/submitted/overwritingproperties/create-schema-mysql.sql");
            runner.runScript(reader);
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
        }
    }

    @org.junit.Before
    public void setUp() {
        final org.apache.ibatis.submitted.overwritingproperties.FooMapper mapper = org.apache.ibatis.submitted.overwritingproperties.AmplFooMapperTest.session.getMapper(org.apache.ibatis.submitted.overwritingproperties.FooMapper.class);
        mapper.deleteAllFoo();
        org.apache.ibatis.submitted.overwritingproperties.AmplFooMapperTest.session.commit();
    }

    @org.junit.Test
    public void testOverwriteWithDefault() {
        final org.apache.ibatis.submitted.overwritingproperties.FooMapper mapper = org.apache.ibatis.submitted.overwritingproperties.AmplFooMapperTest.session.getMapper(org.apache.ibatis.submitted.overwritingproperties.FooMapper.class);
        final org.apache.ibatis.submitted.overwritingproperties.Bar bar = new org.apache.ibatis.submitted.overwritingproperties.Bar(2L);
        final org.apache.ibatis.submitted.overwritingproperties.Foo inserted = new org.apache.ibatis.submitted.overwritingproperties.Foo(1L, bar, 3, 4);
        mapper.insertFoo(inserted);
        final org.apache.ibatis.submitted.overwritingproperties.Foo selected = mapper.selectFoo();
        org.junit.Assert.assertEquals(inserted.getField1(), selected.getField1());
        org.junit.Assert.assertEquals(inserted.getField3(), selected.getField4());
        org.junit.Assert.assertEquals(inserted.getField4(), selected.getField3());
        org.junit.Assert.assertEquals(inserted.getField2().getField1(), selected.getField2().getField1());
    }

    @org.junit.AfterClass
    public static void tearDownAfterClass() {
        org.apache.ibatis.submitted.overwritingproperties.AmplFooMapperTest.session.close();
    }
}

