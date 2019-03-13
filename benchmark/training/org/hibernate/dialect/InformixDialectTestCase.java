/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.dialect;


import StandardBasicTypes.DATE;
import StandardBasicTypes.TIMESTAMP;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.type.Type;
import org.junit.Assert;
import org.junit.Test;


/**
 * Testing of patched support for Informix boolean type; see HHH-9894, HHH-10800
 *
 * @author Greg Jones
 */
public class InformixDialectTestCase extends BaseUnitTestCase {
    private final InformixDialect dialect = new InformixDialect();

    @Test
    @TestForIssue(jiraKey = "HHH-9894")
    public void testToBooleanValueStringTrue() {
        Assert.assertEquals("'t'", dialect.toBooleanValueString(true));
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9894")
    public void testToBooleanValueStringFalse() {
        Assert.assertEquals("'f'", dialect.toBooleanValueString(false));
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10800")
    public void testCurrentTimestampFunction() {
        Map<String, SQLFunction> functions = dialect.getFunctions();
        SQLFunction sqlFunction = functions.get("current_timestamp");
        Type firstArgumentType = null;
        Mapping mapping = null;
        Assert.assertEquals(TIMESTAMP, sqlFunction.getReturnType(firstArgumentType, mapping));
        firstArgumentType = null;
        List arguments = Collections.emptyList();
        SessionFactoryImplementor factory = null;
        Assert.assertEquals("current", sqlFunction.render(firstArgumentType, arguments, factory));
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10800")
    public void testCurrentDateFunction() {
        Map<String, SQLFunction> functions = dialect.getFunctions();
        SQLFunction sqlFunction = functions.get("current_date");
        Type firstArgumentType = null;
        Mapping mapping = null;
        Assert.assertEquals(DATE, sqlFunction.getReturnType(firstArgumentType, mapping));
        firstArgumentType = null;
        List arguments = Collections.emptyList();
        SessionFactoryImplementor factory = null;
        Assert.assertEquals("today", sqlFunction.render(firstArgumentType, arguments, factory));
    }
}

