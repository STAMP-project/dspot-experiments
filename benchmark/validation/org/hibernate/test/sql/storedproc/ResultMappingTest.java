/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.sql.storedproc;


import org.hibernate.dialect.H2Dialect;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.procedure.ProcedureOutputs;
import org.hibernate.result.Output;
import org.hibernate.result.ResultSetOutput;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
@RequiresDialect(H2Dialect.class)
public class ResultMappingTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testResultClass() {
        inTransaction(( session) -> {
            final ProcedureCall call = session.createStoredProcedureCall("findOneUser", .class);
            final ProcedureOutputs procedureResult = call.getOutputs();
            final Output currentOutput = procedureResult.getCurrent();
            assertNotNull(currentOutput);
            final ResultSetOutput resultSetReturn = assertTyping(.class, currentOutput);
            final Object result = resultSetReturn.getSingleResult();
            assertTyping(.class, result);
            assertEquals("Steve", ((H2ProcTesting.MyEntity) (result)).name);
        });
    }

    @Test
    public void testMappingAllFields() {
        inTransaction(( session) -> {
            final ProcedureCall call = session.createStoredProcedureCall("findOneUser", "all-fields");
            final ProcedureOutputs procedureResult = call.getOutputs();
            final Output currentOutput = procedureResult.getCurrent();
            assertNotNull(currentOutput);
            final ResultSetOutput resultSetReturn = assertTyping(.class, currentOutput);
            final Object result = resultSetReturn.getSingleResult();
            assertTyping(.class, result);
            assertEquals("Steve", ((H2ProcTesting.MyEntity) (result)).name);
        });
    }

    @Test
    public void testMappingSomeFields() {
        inTransaction(( session) -> {
            final ProcedureCall call = session.createStoredProcedureCall("findOneUser", "some-fields");
            final ProcedureOutputs procedureResult = call.getOutputs();
            final Output currentOutput = procedureResult.getCurrent();
            assertNotNull(currentOutput);
            final ResultSetOutput resultSetReturn = assertTyping(.class, currentOutput);
            final Object result = resultSetReturn.getSingleResult();
            assertTyping(.class, result);
            assertEquals("Steve", ((H2ProcTesting.MyEntity) (result)).name);
        });
    }

    @Test
    public void testMappingNoFields() {
        inTransaction(( session) -> {
            final ProcedureCall call = session.createStoredProcedureCall("findOneUser", "no-fields");
            final ProcedureOutputs procedureResult = call.getOutputs();
            final Output currentOutput = procedureResult.getCurrent();
            assertNotNull(currentOutput);
            final ResultSetOutput resultSetReturn = assertTyping(.class, currentOutput);
            final Object result = resultSetReturn.getSingleResult();
            assertTyping(.class, result);
            assertEquals("Steve", ((H2ProcTesting.MyEntity) (result)).name);
        });
    }
}

