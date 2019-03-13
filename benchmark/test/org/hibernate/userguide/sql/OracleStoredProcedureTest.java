package org.hibernate.userguide.sql;


import java.math.BigDecimal;
import javax.persistence.StoredProcedureQuery;
import org.hibernate.Session;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.result.Output;
import org.hibernate.result.ResultSetOutput;
import org.hibernate.testing.RequiresDialect;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialect(Oracle8iDialect.class)
public class OracleStoredProcedureTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testStoredProcedureOutParameter() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_count_phones");
            query.registerStoredProcedureParameter(1, .class, ParameterMode.IN);
            query.registerStoredProcedureParameter(2, .class, ParameterMode.OUT);
            query.setParameter(1, 1L);
            query.execute();
            Long phoneCount = ((Long) (query.getOutputParameterValue(2)));
            assertEquals(Long.valueOf(2), phoneCount);
        });
    }

    @Test
    public void testStoredProcedureRefCursor() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::sql-jpa-call-sp-ref-cursor-oracle-example[]
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_person_phones");
            query.registerStoredProcedureParameter(1, .class, ParameterMode.IN);
            query.registerStoredProcedureParameter(2, .class, ParameterMode.REF_CURSOR);
            query.setParameter(1, 1L);
            query.execute();
            List<Object[]> postComments = query.getResultList();
            // end::sql-jpa-call-sp-ref-cursor-oracle-example[]
            assertNotNull(postComments);
        });
    }

    @Test
    public void testStoredProcedureRefCursorUsingNamedQuery() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::sql-jpa-call-sp-ref-cursor-oracle-named-query-example[]
            List<Object[]> postComments = entityManager.createNamedStoredProcedureQuery("sp_person_phones").setParameter("personId", 1L).getResultList();
            // end::sql-jpa-call-sp-ref-cursor-oracle-named-query-example[]
            assertNotNull(postComments);
        });
    }

    @Test
    public void testHibernateProcedureCallRefCursor() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::sql-hibernate-call-sp-ref-cursor-oracle-example[]
            Session session = entityManager.unwrap(.class);
            ProcedureCall call = session.createStoredProcedureCall("sp_person_phones");
            call.registerParameter(1, .class, ParameterMode.IN).bindValue(1L);
            call.registerParameter(2, .class, ParameterMode.REF_CURSOR);
            Output output = call.getOutputs().getCurrent();
            List<Object[]> postComments = ((ResultSetOutput) (output)).getResultList();
            assertEquals(2, postComments.size());
            // end::sql-hibernate-call-sp-ref-cursor-oracle-example[]
        });
    }

    @Test
    public void testStoredProcedureReturnValue() {
        try {
            doInJPA(this::entityManagerFactory, ( entityManager) -> {
                BigDecimal phoneCount = ((BigDecimal) (entityManager.createNativeQuery("SELECT fn_count_phones(:personId) FROM DUAL").setParameter("personId", 1).getSingleResult()));
                assertEquals(BigDecimal.valueOf(2), phoneCount);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

