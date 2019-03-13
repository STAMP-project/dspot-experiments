package io.dropwizard.testing.junit;


import io.dropwizard.testing.app.TestEntity;
import javax.validation.ConstraintViolationException;
import org.hibernate.SessionFactory;
import org.junit.Rule;
import org.junit.Test;


public class DAOTestRuleTest {
    @SuppressWarnings("deprecation")
    @Rule
    public final DAOTestRule daoTestRule = DAOTestRule.newBuilder().addEntityClass(TestEntity.class).build();

    @Test
    public void ruleCreatedSessionFactory() {
        final SessionFactory sessionFactory = daoTestRule.getSessionFactory();
        assertThat(sessionFactory).isNotNull();
    }

    @Test
    public void ruleCanOpenTransaction() {
        final Long id = daoTestRule.inTransaction(() -> persist(new TestEntity("description")).getId());
        assertThat(id).isNotNull();
    }

    @Test
    public void ruleCanRoundtrip() {
        final Long id = daoTestRule.inTransaction(() -> persist(new TestEntity("description")).getId());
        final TestEntity testEntity = get(id);
        assertThat(testEntity).isNotNull();
        assertThat(testEntity.getDescription()).isEqualTo("description");
    }

    @Test
    public void transactionThrowsExceptionAsExpected() {
        assertThatExceptionOfType(ConstraintViolationException.class).isThrownBy(() -> daoTestRule.inTransaction(() -> persist(new TestEntity(null))));
    }

    @Test
    public void rollsBackTransaction() {
        // given a successfully persisted entity
        final TestEntity testEntity = new TestEntity("description");
        daoTestRule.inTransaction(() -> persist(testEntity));
        // when we prepare an update of that entity
        testEntity.setDescription("newDescription");
        try {
            // ... but cause a constraint violation during the actual update
            daoTestRule.inTransaction(() -> {
                persist(testEntity);
                persist(new TestEntity(null));
            });
            failBecauseExceptionWasNotThrown(ConstraintViolationException.class);
        } catch (ConstraintViolationException ignoredException) {
            // keep calm and carry on
            // ... the entity has the original value
            final TestEntity sameTestEntity = get(testEntity.getId());
            assertThat(sameTestEntity.getDescription()).isEqualTo("description");
        }
    }
}

