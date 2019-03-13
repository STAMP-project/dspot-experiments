/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.collectionelement.ordered;


import java.util.Arrays;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
 */
public class ElementCollectionSortingTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testSortingElementCollectionSyntax() {
        Session session = openSession();
        session.beginTransaction();
        session.createQuery("from Person p join fetch p.nickNamesAscendingNaturalSort").list();
        session.createQuery("from Person p join fetch p.nickNamesDescendingNaturalSort").list();
        session.createQuery("from Person p join fetch p.addressesAscendingNaturalSort").list();
        session.createQuery("from Person p join fetch p.addressesDescendingNaturalSort").list();
        session.createQuery("from Person p join fetch p.addressesCityAscendingSort").list();
        session.createQuery("from Person p join fetch p.addressesCityDescendingSort").list();
        session.getTransaction().commit();
        session.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-6875")
    public void testSortingEmbeddableCollectionOfPrimitives() {
        final Session session = openSession();
        session.beginTransaction();
        final Person steve = new Person();
        steve.setName("Steve");
        steve.getNickNamesAscendingNaturalSort().add("sebersole");
        steve.getNickNamesAscendingNaturalSort().add("ebersole");
        steve.getNickNamesDescendingNaturalSort().add("ebersole");
        steve.getNickNamesDescendingNaturalSort().add("sebersole");
        final Person lukasz = new Person();
        lukasz.setName("Lukasz");
        lukasz.getNickNamesAscendingNaturalSort().add("antoniak");
        lukasz.getNickNamesAscendingNaturalSort().add("lantoniak");
        lukasz.getNickNamesDescendingNaturalSort().add("lantoniak");
        lukasz.getNickNamesDescendingNaturalSort().add("antoniak");
        session.save(steve);
        session.save(lukasz);
        session.flush();
        session.clear();
        final List<String> lukaszNamesAsc = Arrays.asList("antoniak", "lantoniak");
        final List<String> lukaszNamesDesc = Arrays.asList("lantoniak", "antoniak");
        final List<String> steveNamesAsc = Arrays.asList("ebersole", "sebersole");
        final List<String> steveNamesDesc = Arrays.asList("sebersole", "ebersole");
        // Testing object graph navigation. Lazy loading collections.
        checkPersonNickNames(lukaszNamesAsc, lukaszNamesDesc, ((Person) (session.get(Person.class, lukasz.getId()))));
        checkPersonNickNames(steveNamesAsc, steveNamesDesc, ((Person) (session.get(Person.class, steve.getId()))));
        session.clear();
        // Testing HQL query. Eagerly fetching nicknames.
        final List<Person> result = session.createQuery("select distinct p from Person p join fetch p.nickNamesAscendingNaturalSort join fetch p.nickNamesDescendingNaturalSort order by p.name").list();
        Assert.assertEquals(2, result.size());
        checkPersonNickNames(lukaszNamesAsc, lukaszNamesDesc, result.get(0));
        checkPersonNickNames(steveNamesAsc, steveNamesDesc, result.get(1));
        // Metadata verification.
        checkSQLOrderBy(session, Person.class.getName(), "nickNamesAscendingNaturalSort", "asc");
        checkSQLOrderBy(session, Person.class.getName(), "nickNamesDescendingNaturalSort", "desc");
        session.getTransaction().rollback();
        session.close();
    }
}

