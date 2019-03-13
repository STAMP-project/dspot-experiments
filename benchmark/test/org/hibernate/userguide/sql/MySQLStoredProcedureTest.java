package org.hibernate.userguide.sql;


import java.sql.CallableStatement;
import java.util.regex.Pattern;
import javax.persistence.StoredProcedureQuery;
import org.hibernate.Session;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.result.Output;
import org.hibernate.result.ResultSetOutput;
import org.hibernate.testing.RequiresDialect;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialect(MySQL5Dialect.class)
public class MySQLStoredProcedureTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testStoredProcedureOutParameter() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::sql-jpa-call-sp-out-mysql-example[]
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_count_phones");
            query.registerStoredProcedureParameter("personId", .class, ParameterMode.IN);
            query.registerStoredProcedureParameter("phoneCount", .class, ParameterMode.OUT);
            query.setParameter("personId", 1L);
            query.execute();
            Long phoneCount = ((Long) (query.getOutputParameterValue("phoneCount")));
            // end::sql-jpa-call-sp-out-mysql-example[]
            assertEquals(Long.valueOf(2), phoneCount);
        });
    }

    @Test
    public void testHibernateProcedureCallOutParameter() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::sql-hibernate-call-sp-out-mysql-example[]
            Session session = entityManager.unwrap(.class);
            ProcedureCall call = session.createStoredProcedureCall("sp_count_phones");
            call.registerParameter("personId", .class, ParameterMode.IN).bindValue(1L);
            call.registerParameter("phoneCount", .class, ParameterMode.OUT);
            Long phoneCount = ((Long) (call.getOutputs().getOutputParameterValue("phoneCount")));
            assertEquals(Long.valueOf(2), phoneCount);
            // end::sql-hibernate-call-sp-out-mysql-example[]
        });
    }

    @Test
    public void testStoredProcedureRefCursor() {
        try {
            doInJPA(this::entityManagerFactory, ( entityManager) -> {
                StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_phones");
                query.registerStoredProcedureParameter(1, .class, ParameterMode.REF_CURSOR);
                query.registerStoredProcedureParameter(2, .class, ParameterMode.IN);
                query.setParameter(2, 1L);
                List<Object[]> personComments = query.getResultList();
                assertEquals(2, personComments.size());
            });
        } catch (Exception e) {
            Assert.assertTrue(Pattern.compile("Dialect .*? not known to support REF_CURSOR parameters").matcher(e.getCause().getMessage()).matches());
        }
    }

    @Test
    public void testStoredProcedureReturnValue() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::sql-jpa-call-sp-no-out-mysql-example[]
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_phones");
            query.registerStoredProcedureParameter(1, .class, ParameterMode.IN);
            query.setParameter(1, 1L);
            List<Object[]> personComments = query.getResultList();
            // end::sql-jpa-call-sp-no-out-mysql-example[]
            assertEquals(2, personComments.size());
        });
    }

    @Test
    public void testHibernateProcedureCallReturnValueParameter() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::sql-hibernate-call-sp-no-out-mysql-example[]
            Session session = entityManager.unwrap(.class);
            ProcedureCall call = session.createStoredProcedureCall("sp_phones");
            call.registerParameter(1, .class, ParameterMode.IN).bindValue(1L);
            Output output = call.getOutputs().getCurrent();
            List<Object[]> personComments = ((ResultSetOutput) (output)).getResultList();
            // end::sql-hibernate-call-sp-no-out-mysql-example[]
            assertEquals(2, personComments.size());
        });
    }

    @Test
    public void testFunctionWithJDBC() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::sql-call-function-mysql-example[]
            final AtomicReference<Integer> phoneCount = new AtomicReference<>();
            Session session = entityManager.unwrap(.class);
            session.doWork(( connection) -> {
                try (CallableStatement function = connection.prepareCall("{ ? = call fn_count_phones(?) }")) {
                    function.registerOutParameter(1, Types.INTEGER);
                    function.setInt(2, 1);
                    function.execute();
                    phoneCount.set(function.getInt(1));
                }
            });
            // end::sql-call-function-mysql-example[]
            assertEquals(Integer.valueOf(2), phoneCount.get());
        });
    }
}

