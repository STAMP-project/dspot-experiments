package io.dropwizard.testing.junit;


import AvailableSettings.FORMAT_SQL;
import io.dropwizard.testing.app.TestEntity;
import org.h2.Driver;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Rule;
import org.junit.Test;


public class DAOTestRuleConfigTest {
    @SuppressWarnings("deprecation")
    @Rule
    public final DAOTestRule database = DAOTestRule.newBuilder().setUrl("jdbc:h2:mem:rule-config-test").setDriver(Driver.class).setUsername("username").useSqlComments(true).setHbm2DdlAuto("create").setShowSql(true).addEntityClass(TestEntity.class).setProperty(FORMAT_SQL, "true").customizeConfiguration(( c) -> c.setProperty("foobar", "baz")).build();

    @Test
    public void explicitConfigCreatesSessionFactory() {
        // it yields a valid SessionFactory instance
        final SessionFactory sessionFactory = database.getSessionFactory();
        assertThat(sessionFactory).isNotNull();
        assertThat(sessionFactory.getProperties()).containsEntry(FORMAT_SQL, "true").containsEntry("foobar", "baz");
        final Session currentSession = sessionFactory.getCurrentSession();
        // an instance of an entity contained in the package can be saved
        currentSession.saveOrUpdate(new TestEntity("foo"));
    }
}

