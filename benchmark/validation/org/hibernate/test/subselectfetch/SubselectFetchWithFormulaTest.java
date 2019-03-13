/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.subselectfetch;


import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hibernate.Session;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.dialect.SybaseDialect;
import org.hibernate.mapping.Collection;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


@SkipForDialect({ SQLServerDialect.class, SybaseDialect.class })
public class SubselectFetchWithFormulaTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void checkSubselectWithFormula() throws Exception {
        // as a pre-condition make sure that subselect fetching is enabled for the collection...
        Collection collectionBinding = metadata().getCollectionBinding(((Name.class.getName()) + ".values"));
        Assert.assertThat(collectionBinding.isSubselectLoadable(), CoreMatchers.is(true));
        // Now force the subselect fetch and make sure we do not get SQL errors
        Session session = openSession();
        session.getTransaction().begin();
        List results = session.createCriteria(Name.class).list();
        for (Object result : results) {
            Name name = ((Name) (result));
            name.getValues().size();
        }
        session.getTransaction().commit();
        session.close();
    }
}

