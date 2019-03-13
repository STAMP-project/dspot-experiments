/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.batchfetch;


import java.util.stream.IntStream;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


public class PaddedBatchFetchTestCase extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12835")
    public void paddedBatchFetchTest() throws Exception {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            // Having DEFAULT_BATCH_FETCH_SIZE=15
            // results in batchSizes = [15, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1]
            // Let's create 11 countries so batch size 15 will be used with padded values,
            // this causes to have to remove 4 elements from list
            int numberOfCountries = 11;
            IntStream.range(0, numberOfCountries).forEach(( i) -> {
                Country c = new Country(("Country " + i));
                session.save(c);
                session.save(new City(("City " + i), c));
            });
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            List<City> allCities = session.createQuery("from City", .class).list();
            // this triggers countries to be fetched in batch
            assertNotNull(allCities.get(0).getCountry().getName());
        });
    }
}

