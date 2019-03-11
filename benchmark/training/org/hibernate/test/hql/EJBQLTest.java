/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hql;


import DialectChecks.SupportsNoColumnInsert;
import antlr.collections.AST;
import java.util.List;
import junit.framework.Assert;
import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
import org.hibernate.hql.internal.ast.QueryTranslatorImpl;
import org.hibernate.hql.internal.ast.util.ASTUtil;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 */
@RequiresDialectFeature(SupportsNoColumnInsert.class)
public class EJBQLTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testEjb3PositionalParameters() throws Exception {
        QueryTranslatorImpl qt = compile("from Animal a where a.bodyWeight = ?1");
        AST ast = ((AST) (qt.getSqlAST()));
        // make certain that the ejb3-positional param got recognized as a positional param
        List namedParams = ASTUtil.collectChildren(ast, new ASTUtil.FilterPredicate() {
            public boolean exclude(AST n) {
                return (n.getType()) != (HqlSqlTokenTypes.PARAM);
            }
        });
        Assert.assertTrue("ejb3 positional param not recognized as a named param", ((namedParams.size()) > 0));
    }

    @Test
    public void testSelectObjectClause() throws Exception {
        // parse("select object(m) from Model m");
        assertEjbqlEqualsHql("select object(m) from Model m", "from Model m");
    }

    @Test
    public void testCollectionMemberDeclaration() throws Exception {
        String hql = "select o from Animal a inner join a.offspring o";
        String ejbql = "select object(o) from Animal a, in(a.offspring) o";
        // parse(hql);
        // parse(ejbql);
        assertEjbqlEqualsHql(ejbql, hql);
    }

    @Test
    public void testIsEmpty() throws Exception {
        // String hql = "from Animal a where not exists (from a.offspring)";
        String hql = "from Animal a where not exists elements(a.offspring)";
        String ejbql = "select object(a) from Animal a where a.offspring is empty";
        // parse(hql);
        // parse(ejbql);
        assertEjbqlEqualsHql(ejbql, hql);
        hql = "from Animal a where exists (from a.mother.father.offspring)";
        ejbql = "select object(a) from Animal a where a.mother.father.offspring is not empty";
        assertEjbqlEqualsHql(ejbql, hql);
    }

    @Test
    public void testMemberOf() throws Exception {
        String hql = "from Animal a where a.mother in (from a.offspring)";
        // String hql = "from Animal a where a.mother in elements(a.offspring)";
        String ejbql = "select object(a) from Animal a where a.mother member of a.offspring";
        // parse(hql);
        // parse(ejbql);
        assertEjbqlEqualsHql(ejbql, hql);
        hql = "from Animal a where a.mother not in (from a.offspring)";
        // hql = "from Animal a where a.mother not in elements(a.offspring)";
        ejbql = "select object(a) from Animal a where a.mother not member of a.offspring";
        // parse(hql);
        // parse(ejbql);
        assertEjbqlEqualsHql(ejbql, hql);
    }

    @Test
    public void testEJBQLFunctions() throws Exception {
        String hql = "select object(a) from Animal a where a.description = concat('1', concat('2','3'), '4'||'5')||0";
        parse(hql, false);
        System.out.println(("sql: " + (toSql(hql))));
        hql = "from Animal a where substring(a.description, 1, 3) = :p1";
        parse(hql, false);
        System.out.println(("sql: " + (toSql(hql))));
        hql = "select substring(a.description, 1, 3) from Animal a";
        parse(hql, false);
        System.out.println(("sql: " + (toSql(hql))));
        hql = "from Animal a where lower(a.description) = :p1";
        parse(hql, false);
        System.out.println(("sql: " + (toSql(hql))));
        hql = "select lower(a.description) from Animal a";
        parse(hql, false);
        System.out.println(("sql: " + (toSql(hql))));
        hql = "from Animal a where upper(a.description) = :p1";
        parse(hql, false);
        System.out.println(("sql: " + (toSql(hql))));
        hql = "select upper(a.description) from Animal a";
        parse(hql, false);
        System.out.println(("sql: " + (toSql(hql))));
        hql = "from Animal a where length(a.description) = :p1";
        parse(hql, false);
        System.out.println(("sql: " + (toSql(hql))));
        hql = "select length(a.description) from Animal a";
        parse(hql, false);
        System.out.println(("sql: " + (toSql(hql))));
        hql = "from Animal a where locate(a.description, 'abc', 2) = :p1";
        parse(hql, false);
        System.out.println(("sql: " + (toSql(hql))));
        hql = "select locate(a.description, :p1, 2) from Animal a";
        parse(hql, false);
        System.out.println(("sql: " + (toSql(hql))));
        hql = "select object(a) from Animal a where trim(trailing '_' from a.description) = :p1";
        parse(hql, false);
        System.out.println(("sql: " + (toSql(hql))));
        hql = "select trim(trailing '_' from a.description) from Animal a";
        parse(hql, false);
        System.out.println(("sql: " + (toSql(hql))));
        hql = "select object(a) from Animal a where trim(leading '_' from a.description) = :p1";
        parse(hql, false);
        System.out.println(("sql: " + (toSql(hql))));
        hql = "select object(a) from Animal a where trim(both a.description) = :p1";
        parse(hql, false);
        System.out.println(("sql: " + (toSql(hql))));
        hql = "select object(a) from Animal a where trim(a.description) = :p1";
        parse(hql, false);
        System.out.println(("sql: " + (toSql(hql))));
        hql = "select object(a) from Animal a where abs(a.bodyWeight) = sqrt(a.bodyWeight)";
        parse(hql, false);
        System.out.println(("sql: " + (toSql(hql))));
        hql = "select object(a) from Animal a where mod(a.bodyWeight, a.mother.bodyWeight) = :p1";
        parse(hql, false);
        System.out.println(("sql: " + (toSql(hql))));
        hql = "select object(a) from Animal a where BIT_LENGTH(a.bodyWeight) = :p1";
        parse(hql, false);
        System.out.println(("sql: " + (toSql(hql))));
        hql = "select BIT_LENGTH(a.bodyWeight) from Animal a";
        parse(hql, false);
        System.out.println(("sql: " + (toSql(hql))));
        hql = "select object(a) from Animal a where CURRENT_DATE = :p1 or CURRENT_TIME = :p2 or CURRENT_TIMESTAMP = :p3";
        parse(hql, false);
        System.out.println(("sql: " + (toSql(hql))));
        // todo the following is not supported
        // hql = "select CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP from Animal a";
        // parse(hql, true);
        // System.out.println("sql: " + toSql(hql));
        hql = "select object(a) from Animal a where a.bodyWeight like '%a%'";
        parse(hql, false);
        System.out.println(("sql: " + (toSql(hql))));
        hql = "select object(a) from Animal a where a.bodyWeight not like '%a%'";
        parse(hql, false);
        System.out.println(("sql: " + (toSql(hql))));
        hql = "select object(a) from Animal a where a.bodyWeight like '%a%' escape '%'";
        parse(hql, false);
        System.out.println(("sql: " + (toSql(hql))));
    }

    @Test
    public void testTrueFalse() throws Exception {
        assertEjbqlEqualsHql("from Human h where h.pregnant is true", "from Human h where h.pregnant = true");
        assertEjbqlEqualsHql("from Human h where h.pregnant is false", "from Human h where h.pregnant = false");
        assertEjbqlEqualsHql("from Human h where not(h.pregnant is true)", "from Human h where not( h.pregnant=true )");
    }
}

