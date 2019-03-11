/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.beanvalidation;


import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test verifying that DDL constraints get applied when Bean Validation / Hibernate Validator are enabled.
 *
 * @author Emmanuel Bernard
 * @author Hardy Ferentschik
 */
public class DDLTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testBasicDDL() {
        PersistentClass classMapping = metadata().getEntityBinding(Address.class.getName());
        Column stateColumn = ((Column) (classMapping.getProperty("state").getColumnIterator().next()));
        Assert.assertEquals(stateColumn.getLength(), 3);
        Column zipColumn = ((Column) (classMapping.getProperty("zip").getColumnIterator().next()));
        Assert.assertEquals(zipColumn.getLength(), 5);
        Assert.assertFalse(zipColumn.isNullable());
    }

    @Test
    public void testApplyOnIdColumn() throws Exception {
        PersistentClass classMapping = metadata().getEntityBinding(Tv.class.getName());
        Column serialColumn = ((Column) (classMapping.getIdentifierProperty().getColumnIterator().next()));
        Assert.assertEquals("Validator annotation not applied on ids", 2, serialColumn.getLength());
    }

    @Test
    @TestForIssue(jiraKey = "HHH-5281")
    public void testLengthConstraint() throws Exception {
        PersistentClass classMapping = metadata().getEntityBinding(Tv.class.getName());
        Column modelColumn = ((Column) (classMapping.getProperty("model").getColumnIterator().next()));
        Assert.assertEquals(modelColumn.getLength(), 5);
    }

    @Test
    public void testApplyOnManyToOne() throws Exception {
        PersistentClass classMapping = metadata().getEntityBinding(TvOwner.class.getName());
        Column serialColumn = ((Column) (classMapping.getProperty("tv").getColumnIterator().next()));
        Assert.assertEquals("Validator annotations not applied on associations", false, serialColumn.isNullable());
    }

    @Test
    public void testSingleTableAvoidNotNull() throws Exception {
        PersistentClass classMapping = metadata().getEntityBinding(Rock.class.getName());
        Column serialColumn = ((Column) (classMapping.getProperty("bit").getColumnIterator().next()));
        Assert.assertTrue("Notnull should not be applied on single tables", serialColumn.isNullable());
    }

    @Test
    public void testNotNullOnlyAppliedIfEmbeddedIsNotNullItself() throws Exception {
        PersistentClass classMapping = metadata().getEntityBinding(Tv.class.getName());
        Property property = classMapping.getProperty("tuner.frequency");
        Column serialColumn = ((Column) (property.getColumnIterator().next()));
        Assert.assertEquals("Validator annotations are applied on tuner as it is @NotNull", false, serialColumn.isNullable());
        property = classMapping.getProperty("recorder.time");
        serialColumn = ((Column) (property.getColumnIterator().next()));
        Assert.assertEquals("Validator annotations are applied on tuner as it is @NotNull", true, serialColumn.isNullable());
    }
}

