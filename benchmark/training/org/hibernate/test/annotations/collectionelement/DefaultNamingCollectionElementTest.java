/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.collectionelement;


import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.hibernate.Filter;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.test.annotations.Country;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests @ElementCollection using the default "legacy" NamingStrategyDelegator which does not
 * comply with JPA spec in some cases. See HHH-9387 and HHH-9389 for more information..
 *
 * @author Emmanuel Bernard
 * @author Hardy Ferentschik
 * @author Gail Badner
 */
@SuppressWarnings("unchecked")
public class DefaultNamingCollectionElementTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testSimpleElement() throws Exception {
        Assert.assertEquals("BoyFavoriteNumbers", metadata().getCollectionBinding((((Boy.class.getName()) + '.') + "favoriteNumbers")).getCollectionTable().getName());
        Session s = openSession();
        s.getTransaction().begin();
        Boy boy = new Boy();
        boy.setFirstName("John");
        boy.setLastName("Doe");
        boy.getNickNames().add("Johnny");
        boy.getNickNames().add("Thing");
        boy.getScorePerNickName().put("Johnny", 3);
        boy.getScorePerNickName().put("Thing", 5);
        int[] favNbrs = new int[4];
        for (int index = 0; index < ((favNbrs.length) - 1); index++) {
            favNbrs[index] = index * 3;
        }
        boy.setFavoriteNumbers(favNbrs);
        boy.getCharacters().add(Character.GENTLE);
        boy.getCharacters().add(Character.CRAFTY);
        HashMap<String, FavoriteFood> foods = new HashMap<String, FavoriteFood>();
        foods.put("breakfast", FavoriteFood.PIZZA);
        foods.put("lunch", FavoriteFood.KUNGPAOCHICKEN);
        foods.put("dinner", FavoriteFood.SUSHI);
        boy.setFavoriteFood(foods);
        s.persist(boy);
        s.getTransaction().commit();
        s.clear();
        Transaction tx = s.beginTransaction();
        boy = ((Boy) (s.get(Boy.class, boy.getId())));
        Assert.assertNotNull(boy.getNickNames());
        Assert.assertTrue(boy.getNickNames().contains("Thing"));
        Assert.assertNotNull(boy.getScorePerNickName());
        Assert.assertTrue(boy.getScorePerNickName().containsKey("Thing"));
        Assert.assertEquals(Integer.valueOf(5), boy.getScorePerNickName().get("Thing"));
        Assert.assertNotNull(boy.getFavoriteNumbers());
        Assert.assertEquals(3, boy.getFavoriteNumbers()[1]);
        Assert.assertTrue(boy.getCharacters().contains(Character.CRAFTY));
        Assert.assertTrue(boy.getFavoriteFood().get("dinner").equals(FavoriteFood.SUSHI));
        Assert.assertTrue(boy.getFavoriteFood().get("lunch").equals(FavoriteFood.KUNGPAOCHICKEN));
        Assert.assertTrue(boy.getFavoriteFood().get("breakfast").equals(FavoriteFood.PIZZA));
        List result = s.createQuery("select boy from Boy boy join boy.nickNames names where names = :name").setParameter("name", "Thing").list();
        Assert.assertEquals(1, result.size());
        s.delete(boy);
        tx.commit();
        s.close();
    }

    @Test
    public void testCompositeElement() throws Exception {
        Session s = openSession();
        s.getTransaction().begin();
        Boy boy = new Boy();
        boy.setFirstName("John");
        boy.setLastName("Doe");
        Toy toy = new Toy();
        toy.setName("Balloon");
        toy.setSerial("serial001");
        toy.setBrand(new Brand());
        toy.getBrand().setName("Bandai");
        boy.getFavoriteToys().add(toy);
        s.persist(boy);
        s.getTransaction().commit();
        s.clear();
        Transaction tx = s.beginTransaction();
        boy = ((Boy) (s.get(Boy.class, boy.getId())));
        Assert.assertNotNull(boy);
        Assert.assertNotNull(boy.getFavoriteToys());
        Assert.assertTrue(boy.getFavoriteToys().contains(toy));
        Assert.assertEquals("@Parent is failing", boy, boy.getFavoriteToys().iterator().next().getOwner());
        s.delete(boy);
        tx.commit();
        s.close();
    }

    @Test
    public void testAttributedJoin() throws Exception {
        Session s = openSession();
        s.getTransaction().begin();
        Country country = new Country();
        country.setName("Australia");
        s.persist(country);
        Boy boy = new Boy();
        boy.setFirstName("John");
        boy.setLastName("Doe");
        CountryAttitude attitude = new CountryAttitude();
        // TODO: doesn't work
        attitude.setBoy(boy);
        attitude.setCountry(country);
        attitude.setLikes(true);
        boy.getCountryAttitudes().add(attitude);
        s.persist(boy);
        s.getTransaction().commit();
        s.clear();
        Transaction tx = s.beginTransaction();
        boy = ((Boy) (s.get(Boy.class, boy.getId())));
        Assert.assertTrue(boy.getCountryAttitudes().contains(attitude));
        s.delete(boy);
        s.delete(s.get(Country.class, country.getId()));
        tx.commit();
        s.close();
    }

    @Test
    public void testLazyCollectionofElements() throws Exception {
        Assert.assertEquals("BoyFavoriteNumbers", metadata().getCollectionBinding((((Boy.class.getName()) + '.') + "favoriteNumbers")).getCollectionTable().getName());
        Session s = openSession();
        s.getTransaction().begin();
        Boy boy = new Boy();
        boy.setFirstName("John");
        boy.setLastName("Doe");
        boy.getNickNames().add("Johnny");
        boy.getNickNames().add("Thing");
        boy.getScorePerNickName().put("Johnny", 3);
        boy.getScorePerNickName().put("Thing", 5);
        int[] favNbrs = new int[4];
        for (int index = 0; index < ((favNbrs.length) - 1); index++) {
            favNbrs[index] = index * 3;
        }
        boy.setFavoriteNumbers(favNbrs);
        boy.getCharacters().add(Character.GENTLE);
        boy.getCharacters().add(Character.CRAFTY);
        s.persist(boy);
        s.getTransaction().commit();
        s.clear();
        Transaction tx = s.beginTransaction();
        boy = ((Boy) (s.get(Boy.class, boy.getId())));
        Assert.assertNotNull(boy.getNickNames());
        Assert.assertTrue(boy.getNickNames().contains("Thing"));
        Assert.assertNotNull(boy.getScorePerNickName());
        Assert.assertTrue(boy.getScorePerNickName().containsKey("Thing"));
        Assert.assertEquals(new Integer(5), boy.getScorePerNickName().get("Thing"));
        Assert.assertNotNull(boy.getFavoriteNumbers());
        Assert.assertEquals(3, boy.getFavoriteNumbers()[1]);
        Assert.assertTrue(boy.getCharacters().contains(Character.CRAFTY));
        List result = s.createQuery("select boy from Boy boy join boy.nickNames names where names = :name").setParameter("name", "Thing").list();
        Assert.assertEquals(1, result.size());
        s.delete(boy);
        tx.commit();
        s.close();
    }

    @Test
    public void testFetchEagerAndFilter() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        TestCourse test = new TestCourse();
        LocalizedString title = new LocalizedString("title in english");
        title.getVariations().put(Locale.FRENCH.getLanguage(), "title en francais");
        test.setTitle(title);
        s.save(test);
        s.flush();
        s.clear();
        Filter filter = s.enableFilter("selectedLocale");
        filter.setParameter("param", "fr");
        Query q = s.createQuery("from TestCourse t");
        List l = q.list();
        Assert.assertEquals(1, l.size());
        TestCourse t = ((TestCourse) (s.get(TestCourse.class, test.getTestCourseId())));
        Assert.assertEquals(1, t.getTitle().getVariations().size());
        tx.rollback();
        s.close();
    }

    @Test
    public void testMapKeyType() throws Exception {
        Matrix m = new Matrix();
        m.getMvalues().put(1, 1.1F);
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        s.persist(m);
        s.flush();
        s.clear();
        m = ((Matrix) (s.get(Matrix.class, m.getId())));
        Assert.assertEquals(1.1F, m.getMvalues().get(1), 0.01F);
        tx.rollback();
        s.close();
    }

    @Test
    public void testDefaultValueColumnForBasic() throws Exception {
        isDefaultValueCollectionColumnPresent(Boy.class.getName(), "hatedNames");
        isDefaultValueCollectionColumnPresent(Boy.class.getName(), "preferredNames");
        isCollectionColumnPresent(Boy.class.getName(), "nickNames", "nickNames");
        isDefaultValueCollectionColumnPresent(Boy.class.getName(), "scorePerPreferredName");
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9387")
    public void testDefaultTableNameNoOverrides() {
        // NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
        // to ensure that entity names/tables are not changed (which would invalidate these test cases).
        // Products has @Entity (no @Table)
        checkDefaultCollectionTableName(BugSystem.class, "bugs", "BugSystem_bugs");
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9387")
    public void testDefaultTableNameOwnerPrimaryTableOverride() {
        // NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
        // to ensure that entity names/tables are not changed (which would invalidate these test cases).
        // Boy has @Entity @Table(name="tbl_Boys")
        checkDefaultCollectionTableName(Boy.class, "hatedNames", "Boy_hatedNames");
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9387")
    public void testDefaultTableNameOwnerEntityNameAndPKColumnOverride() {
        // NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
        // to ensure that entity names/tables are not changed (which would invalidate these test cases).
        // Matrix has @Entity(name="Mtx"); entity table name defaults to "Mtx"; owner PK column is configured as "mId"
        // Legacy behavior used unqualified entity name (instead of JPA entity name) in generated collection table.
        checkDefaultCollectionTableName(Matrix.class, "mvalues", "Matrix_mvalues");
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9389")
    public void testDefaultJoinColumnOwnerPrimaryTableAndEntityNamesOverride() {
        // NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
        // to ensure that entity names/tables are not changed (which would invalidate these test cases).
        // Owner has @Entity( name="OWNER") @Table( name="OWNER_TABLE")
        // Legacy behavior used unqualified entity name (instead of JPA entity name) in generated join column.
        checkDefaultJoinColumnName(Owner.class, "elements", "Owner_id");
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9389")
    public void testDefaultJoinColumnNoOverrides() {
        // NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
        // to ensure that entity names/tables are not changed (which would invalidate these test cases).
        // Products has @Entity (no @Table)
        checkDefaultJoinColumnName(BugSystem.class, "bugs", "BugSystem_id");
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9389")
    public void testDefaultJoinColumnOwnerPrimaryTableOverride() {
        // NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
        // to ensure that entity names/tables are not changed (which would invalidate these test cases).
        // Boy has @Entity @Table(name="tbl_Boys")
        checkDefaultJoinColumnName(Boy.class, "hatedNames", "Boy_id");
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9389")
    public void testDefaultJoinColumnOwnerEntityNameAndPKColumnOverride() {
        // NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
        // to ensure that entity names/tables are not changed (which would invalidate these test cases).
        // Matrix has @Entity(name="Mtx"); entity table name defaults to "Mtx"; owner PK column is configured as "mId"
        // Legacy behavior used unqualified entity name (instead of JPA entity name) in generated join column.
        checkDefaultJoinColumnName(Matrix.class, "mvalues", "Matrix_mId");
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9387")
    public void testDefaultTableNameOwnerPrimaryTableAndEntityNamesOverride() {
        // NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
        // to ensure that entity names/tables are not changed (which would invalidate these test cases).
        // Owner has @Entity( name="OWNER") @Table( name="OWNER_TABLE")
        // Legacy behavior used unqualified entity name (instead of JPA entity name) in generated collection table.
        checkDefaultCollectionTableName(Owner.class, "elements", "Owner_elements");
    }
}

