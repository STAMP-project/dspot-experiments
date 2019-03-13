/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.sql;


import Template.TEMPLATE;
import java.util.Collections;
import org.hibernate.QueryException;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.function.SQLFunctionRegistry;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.entity.PropertyMapping;
import org.hibernate.sql.ordering.antlr.ColumnMapper;
import org.hibernate.sql.ordering.antlr.ColumnReference;
import org.hibernate.sql.ordering.antlr.SqlValueReference;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.type.Type;
import org.junit.Assert;
import org.junit.Test;

import static Template.TEMPLATE;


/**
 *
 *
 * @author Steve Ebersole
 */
public class TemplateTest extends BaseUnitTestCase {
    private static final PropertyMapping PROPERTY_MAPPING = new PropertyMapping() {
        public String[] toColumns(String propertyName) throws UnsupportedOperationException, QueryException {
            if ("sql".equals(propertyName)) {
                return new String[]{ "sql" };
            } else
                if ("component".equals(propertyName)) {
                    return new String[]{ "comp_1", "comp_2" };
                } else
                    if ("component.prop1".equals(propertyName)) {
                        return new String[]{ "comp_1" };
                    } else
                        if ("component.prop2".equals(propertyName)) {
                            return new String[]{ "comp_2" };
                        } else
                            if ("property".equals(propertyName)) {
                                return new String[]{ "prop" };
                            }




            throw new QueryException(("could not resolve property: " + propertyName));
        }

        public Type toType(String propertyName) throws QueryException {
            return null;
        }

        public String[] toColumns(String alias, String propertyName) throws QueryException {
            return new String[0];
        }

        public Type getType() {
            return null;
        }
    };

    private static final ColumnMapper MAPPER = new ColumnMapper() {
        public SqlValueReference[] map(String reference) {
            final String[] columnNames = TemplateTest.PROPERTY_MAPPING.toColumns(reference);
            final SqlValueReference[] result = new SqlValueReference[columnNames.length];
            int i = 0;
            for (final String columnName : columnNames) {
                result[i] = new ColumnReference() {
                    @Override
                    public String getColumnName() {
                        return columnName;
                    }
                };
                i++;
            }
            return result;
        }
    };

    private static final Dialect DIALECT = new HSQLDialect();

    private static final SQLFunctionRegistry FUNCTION_REGISTRY = new SQLFunctionRegistry(TemplateTest.DIALECT, Collections.EMPTY_MAP);

    private static SessionFactoryImplementor SESSION_FACTORY = null;// Required for ORDER BY rendering.


    @Test
    public void testSqlExtractFunction() {
        String fragment = "extract( year from col )";
        String template = Template.renderWhereStringTemplate(fragment, TEMPLATE, TemplateTest.DIALECT, TemplateTest.FUNCTION_REGISTRY);
        Assert.assertEquals((("extract(year from " + (TEMPLATE)) + ".col)"), template);
    }

    @Test
    public void testSqlTrimFunction() {
        String fragment = "trim( col )";
        String template = Template.renderWhereStringTemplate(fragment, TEMPLATE, TemplateTest.DIALECT, TemplateTest.FUNCTION_REGISTRY);
        Assert.assertEquals((("trim(" + (TEMPLATE)) + ".col)"), template);
        fragment = "trim( from col )";
        template = Template.renderWhereStringTemplate(fragment, TEMPLATE, TemplateTest.DIALECT, TemplateTest.FUNCTION_REGISTRY);
        Assert.assertEquals((("trim(from " + (TEMPLATE)) + ".col)"), template);
        fragment = "trim( both from col )";
        template = Template.renderWhereStringTemplate(fragment, TEMPLATE, TemplateTest.DIALECT, TemplateTest.FUNCTION_REGISTRY);
        Assert.assertEquals((("trim(both from " + (TEMPLATE)) + ".col)"), template);
        fragment = "trim( leading from col )";
        template = Template.renderWhereStringTemplate(fragment, TEMPLATE, TemplateTest.DIALECT, TemplateTest.FUNCTION_REGISTRY);
        Assert.assertEquals((("trim(leading from " + (TEMPLATE)) + ".col)"), template);
        fragment = "trim( TRAILING from col )";
        template = Template.renderWhereStringTemplate(fragment, TEMPLATE, TemplateTest.DIALECT, TemplateTest.FUNCTION_REGISTRY);
        Assert.assertEquals((("trim(TRAILING from " + (TEMPLATE)) + ".col)"), template);
        fragment = "trim( 'b' from col )";
        template = Template.renderWhereStringTemplate(fragment, TEMPLATE, TemplateTest.DIALECT, TemplateTest.FUNCTION_REGISTRY);
        Assert.assertEquals((("trim('b' from " + (TEMPLATE)) + ".col)"), template);
        fragment = "trim( both 'b' from col )";
        template = Template.renderWhereStringTemplate(fragment, TEMPLATE, TemplateTest.DIALECT, TemplateTest.FUNCTION_REGISTRY);
        Assert.assertEquals((("trim(both 'b' from " + (TEMPLATE)) + ".col)"), template);
    }

    @Test
    public void testSQLReferences() {
        String fragment = "sql asc, sql desc";
        String template = doStandardRendering(fragment);
        Assert.assertEquals(((((TEMPLATE) + ".sql asc, ") + (TEMPLATE)) + ".sql desc"), template);
    }

    @Test
    public void testQuotedSQLReferences() {
        String fragment = "`sql` asc, `sql` desc";
        String template = doStandardRendering(fragment);
        Assert.assertEquals(((((TEMPLATE) + ".\"sql\" asc, ") + (TEMPLATE)) + ".\"sql\" desc"), template);
    }

    @Test
    public void testPropertyReference() {
        String fragment = "property asc, property desc";
        String template = doStandardRendering(fragment);
        Assert.assertEquals(((((TEMPLATE) + ".prop asc, ") + (TEMPLATE)) + ".prop desc"), template);
    }

    @Test
    public void testFunctionReference() {
        String fragment = "upper(sql) asc, lower(sql) desc";
        String template = doStandardRendering(fragment);
        Assert.assertEquals((((("upper(" + (TEMPLATE)) + ".sql) asc, lower(") + (TEMPLATE)) + ".sql) desc"), template);
    }

    @Test
    public void testQualifiedFunctionReference() {
        String fragment = "qual.upper(property) asc, qual.lower(property) desc";
        String template = doStandardRendering(fragment);
        Assert.assertEquals((((("qual.upper(" + (TEMPLATE)) + ".prop) asc, qual.lower(") + (TEMPLATE)) + ".prop) desc"), template);
    }

    @Test
    public void testDoubleQualifiedFunctionReference() {
        String fragment = "qual1.qual2.upper(property) asc, qual1.qual2.lower(property) desc";
        String template = doStandardRendering(fragment);
        Assert.assertEquals((((("qual1.qual2.upper(" + (TEMPLATE)) + ".prop) asc, qual1.qual2.lower(") + (TEMPLATE)) + ".prop) desc"), template);
    }

    @Test
    public void testFunctionWithPropertyReferenceAsParam() {
        String fragment = "upper(property) asc, lower(property) desc";
        String template = doStandardRendering(fragment);
        Assert.assertEquals((((("upper(" + (TEMPLATE)) + ".prop) asc, lower(") + (TEMPLATE)) + ".prop) desc"), template);
    }

    @Test
    public void testNestedFunctionReferences() {
        String fragment = "upper(lower(sql)) asc, lower(upper(sql)) desc";
        String template = doStandardRendering(fragment);
        Assert.assertEquals((((("upper(lower(" + (TEMPLATE)) + ".sql)) asc, lower(upper(") + (TEMPLATE)) + ".sql)) desc"), template);
    }

    @Test
    public void testComplexNestedFunctionReferences() {
        String fragment = "mod(mod(sql,2),3) asc";
        String template = doStandardRendering(fragment);
        Assert.assertEquals((("mod(mod(" + (TEMPLATE)) + ".sql, 2), 3) asc"), template);
    }

    @Test
    public void testCollation() {
        String fragment = "`sql` COLLATE my_collation, `sql` COLLATE your_collation";
        String template = doStandardRendering(fragment);
        Assert.assertEquals(((((TEMPLATE) + ".\"sql\" collate my_collation, ") + (TEMPLATE)) + ".\"sql\" collate your_collation"), template);
    }

    @Test
    public void testCollationAndOrdering() {
        String fragment = "sql COLLATE my_collation, upper(prop) COLLATE your_collation asc, `sql` desc";
        String template = doStandardRendering(fragment);
        Assert.assertEquals(((((((TEMPLATE) + ".sql collate my_collation, upper(") + (TEMPLATE)) + ".prop) collate your_collation asc, ") + (TEMPLATE)) + ".\"sql\" desc"), template);
    }

    @Test
    public void testComponentReferences() {
        String fragment = "component asc";
        String template = doStandardRendering(fragment);
        Assert.assertEquals(((((TEMPLATE) + ".comp_1 asc, ") + (TEMPLATE)) + ".comp_2 asc"), template);
    }

    @Test
    public void testComponentDerefReferences() {
        String fragment = "component.prop1 asc";
        String template = doStandardRendering(fragment);
        Assert.assertEquals(((TEMPLATE) + ".comp_1 asc"), template);
    }
}

