/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.sql.hand.query;


import StringType.INSTANCE;
import Transformers.ALIAS_TO_ENTITY_MAP;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.persistence.PersistenceException;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.QueryException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
import org.hibernate.engine.spi.NamedSQLQueryDefinitionBuilder;
import org.hibernate.test.sql.hand.Dimension;
import org.hibernate.test.sql.hand.Employment;
import org.hibernate.test.sql.hand.Group;
import org.hibernate.test.sql.hand.ImageHolder;
import org.hibernate.test.sql.hand.Order;
import org.hibernate.test.sql.hand.Organization;
import org.hibernate.test.sql.hand.Person;
import org.hibernate.test.sql.hand.Product;
import org.hibernate.test.sql.hand.SpaceShip;
import org.hibernate.test.sql.hand.Speech;
import org.hibernate.test.sql.hand.TextHolder;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.hibernate.transform.BasicTransformerAdapter;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests of various features of native SQL queries.
 *
 * @author Steve Ebersole
 */
public class NativeSQLQueriesTest extends BaseCoreFunctionalTestCase {
    @Test
    @SkipForDialect(H2Dialect.class)
    public void testFailOnNoAddEntityOrScalar() {
        // Note: this passes, but for the wrong reason.
        // there is actually an exception thrown, but it is the database
        // throwing a sql exception because the SQL gets passed
        // "un-processed"...
        // 
        // Oddly, H2 accepts this query.
        Session s = openSession();
        s.beginTransaction();
        try {
            String sql = "select {org.*} " + "from organization org";
            s.createSQLQuery(sql).list();
            Assert.fail("Should throw an exception since no addEntity nor addScalar has been performed.");
        } catch (PersistenceException pe) {
            // expected behavior
        } finally {
            s.getTransaction().rollback();
            s.close();
        }
    }

    @Test
    public void testRegisteredNamedSQLQueryWithScalar() {
        final NamedSQLQueryDefinitionBuilder builder = new NamedSQLQueryDefinitionBuilder();
        builder.setName("namedQuery");
        builder.setQuery("select count(*) AS c from ORGANIZATION");
        builder.setQueryReturns(new NativeSQLQueryReturn[1]);
        sessionFactory().registerNamedSQLQueryDefinition("namedQuery", builder.createNamedQueryDefinition());
        final Session s = openSession();
        s.beginTransaction();
        final SQLQuery query = ((SQLQuery) (s.getNamedQuery("namedQuery")));
        query.addScalar("c");
        final Number result = ((Number) (query.uniqueResult()));
        s.getTransaction().commit();
        s.close();
        Assert.assertNotNull(result);
        Assert.assertTrue((0 == (result.intValue())));
    }

    @Test
    public void testManualSynchronization() {
        Session s = openSession();
        s.beginTransaction();
        sessionFactory().getStatistics().clear();
        // create an Organization...
        Organization jboss = new Organization("JBoss");
        s.persist(jboss);
        // now query on Employment, this should not cause an auto-flush
        s.createSQLQuery(getEmploymentSQL()).addSynchronizedQuerySpace("ABC").list();
        Assert.assertEquals(0, sessionFactory().getStatistics().getEntityInsertCount());
        // now try to query on Employment but this time add Organization as a synchronized query space...
        s.createSQLQuery(getEmploymentSQL()).addSynchronizedEntityClass(Organization.class).list();
        Assert.assertEquals(1, sessionFactory().getStatistics().getEntityInsertCount());
        // clean up
        s.delete(jboss);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testSQLQueryInterface() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Organization ifa = new Organization("IFA");
        Organization jboss = new Organization("JBoss");
        Person gavin = new Person("Gavin");
        Employment emp = new Employment(gavin, jboss, "AU");
        s.persist(ifa);
        s.persist(jboss);
        s.persist(gavin);
        s.persist(emp);
        List l = s.createSQLQuery(getOrgEmpRegionSQL()).addEntity("org", Organization.class).addJoin("emp", "org.employments").addScalar("regionCode", INSTANCE).list();
        Assert.assertEquals(2, l.size());
        l = s.createSQLQuery(getOrgEmpPersonSQL()).addEntity("org", Organization.class).addJoin("emp", "org.employments").addJoin("pers", "emp.employee").list();
        Assert.assertEquals(l.size(), 1);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        l = s.createSQLQuery(("select {org.*}, {emp.*} " + ("from ORGANIZATION org " + "     left outer join EMPLOYMENT emp on org.ORGID = emp.EMPLOYER, ORGANIZATION org2"))).addEntity("org", Organization.class).addJoin("emp", "org.employments").setResultTransformer(DistinctRootEntityResultTransformer.INSTANCE).list();
        Assert.assertEquals(l.size(), 2);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.delete(emp);
        s.delete(gavin);
        s.delete(ifa);
        s.delete(jboss);
        t.commit();
        s.close();
    }

    @Test
    public void testResultSetMappingDefinition() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Organization ifa = new Organization("IFA");
        Organization jboss = new Organization("JBoss");
        Person gavin = new Person("Gavin");
        Employment emp = new Employment(gavin, jboss, "AU");
        s.persist(ifa);
        s.persist(jboss);
        s.persist(gavin);
        s.persist(emp);
        List l = s.createSQLQuery(getOrgEmpRegionSQL()).setResultSetMapping("org-emp-regionCode").list();
        Assert.assertEquals(l.size(), 2);
        l = s.createSQLQuery(getOrgEmpPersonSQL()).setResultSetMapping("org-emp-person").list();
        Assert.assertEquals(l.size(), 1);
        s.delete(emp);
        s.delete(gavin);
        s.delete(ifa);
        s.delete(jboss);
        t.commit();
        s.close();
    }

    @Test
    public void testScalarValues() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Organization ifa = new Organization("IFA");
        Organization jboss = new Organization("JBoss");
        Serializable idIfa = s.save(ifa);
        Serializable idJBoss = s.save(jboss);
        s.flush();
        List result = s.getNamedQuery("orgNamesOnly").list();
        Assert.assertTrue(result.contains("IFA"));
        Assert.assertTrue(result.contains("JBoss"));
        result = s.getNamedQuery("orgNamesOnly").setResultTransformer(ALIAS_TO_ENTITY_MAP).list();
        Map m = ((Map) (result.get(0)));
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(1, m.size());
        Assert.assertTrue(m.containsKey("NAME"));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        Iterator iter = s.getNamedQuery("orgNamesAndOrgs").list().iterator();
        Object[] o = ((Object[]) (iter.next()));
        Assert.assertEquals("expecting 2 values", 2, o.length);
        Assert.assertEquals(o[0], "IFA");
        Assert.assertEquals(((Organization) (o[1])).getName(), "IFA");
        o = ((Object[]) (iter.next()));
        Assert.assertEquals(o[0], "JBoss");
        Assert.assertEquals(((Organization) (o[1])).getName(), "JBoss");
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        // test that the ordering of the results is truly based on the order in which they were defined
        iter = s.getNamedQuery("orgsAndOrgNames").list().iterator();
        Object[] row = ((Object[]) (iter.next()));
        Assert.assertEquals("expecting 2 values", 2, row.length);
        Assert.assertEquals("expecting non-scalar result first", Organization.class, row[0].getClass());
        Assert.assertEquals("expecting scalar result second", String.class, row[1].getClass());
        Assert.assertEquals(((Organization) (row[0])).getName(), "IFA");
        Assert.assertEquals(row[1], "IFA");
        row = ((Object[]) (iter.next()));
        Assert.assertEquals("expecting non-scalar result first", Organization.class, row[0].getClass());
        Assert.assertEquals("expecting scalar result second", String.class, row[1].getClass());
        Assert.assertEquals(((Organization) (row[0])).getName(), "JBoss");
        Assert.assertEquals(row[1], "JBoss");
        Assert.assertFalse(iter.hasNext());
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        iter = s.getNamedQuery("orgIdsAndOrgNames").list().iterator();
        o = ((Object[]) (iter.next()));
        Assert.assertEquals(o[1], "IFA");
        Assert.assertEquals(o[0], idIfa);
        o = ((Object[]) (iter.next()));
        Assert.assertEquals(o[1], "JBoss");
        Assert.assertEquals(o[0], idJBoss);
        s.delete(ifa);
        s.delete(jboss);
        t.commit();
        s.close();
    }

    @Test
    @SuppressWarnings({ "deprecation", "UnusedDeclaration" })
    public void testMappedAliasStrategy() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Organization ifa = new Organization("IFA");
        Organization jboss = new Organization("JBoss");
        Person gavin = new Person("Gavin");
        Employment emp = new Employment(gavin, jboss, "AU");
        Serializable orgId = s.save(jboss);
        Serializable orgId2 = s.save(ifa);
        s.save(gavin);
        s.save(emp);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        Query namedQuery = s.getNamedQuery("AllEmploymentAsMapped");
        List list = namedQuery.list();
        Assert.assertEquals(1, list.size());
        Employment emp2 = ((Employment) (list.get(0)));
        Assert.assertEquals(emp2.getEmploymentId(), emp.getEmploymentId());
        Assert.assertEquals(emp2.getStartDate().getDate(), emp.getStartDate().getDate());
        Assert.assertEquals(emp2.getEndDate(), emp.getEndDate());
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        Query sqlQuery = s.getNamedQuery("EmploymentAndPerson");
        sqlQuery.setResultTransformer(ALIAS_TO_ENTITY_MAP);
        list = sqlQuery.list();
        Assert.assertEquals(1, list.size());
        Object res = list.get(0);
        ExtraAssertions.assertClassAssignability(Map.class, res.getClass());
        Map m = ((Map) (res));
        Assert.assertEquals(2, m.size());
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        sqlQuery = s.getNamedQuery("organizationreturnproperty");
        sqlQuery.setResultTransformer(ALIAS_TO_ENTITY_MAP);
        list = sqlQuery.list();
        Assert.assertEquals(2, list.size());
        m = ((Map) (list.get(0)));
        Assert.assertEquals(2, m.size());
        Assert.assertTrue(m.containsKey("org"));
        Assert.assertTrue(m.containsKey("emp"));
        ExtraAssertions.assertClassAssignability(m.get("org").getClass(), Organization.class);
        if ((jboss.getId()) == (((Organization) (m.get("org"))).getId())) {
            ExtraAssertions.assertClassAssignability(m.get("emp").getClass(), Employment.class);
        }
        Map m2 = ((Map) (list.get(1)));
        Assert.assertEquals(2, m.size());
        Assert.assertTrue(m2.containsKey("org"));
        Assert.assertTrue(m2.containsKey("emp"));
        ExtraAssertions.assertClassAssignability(m2.get("org").getClass(), Organization.class);
        if ((jboss.getId()) == (((Organization) (m2.get("org"))).getId())) {
            ExtraAssertions.assertClassAssignability(m2.get("emp").getClass(), Employment.class);
        }
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        namedQuery = s.getNamedQuery("EmploymentAndPerson");
        list = namedQuery.list();
        Assert.assertEquals(1, list.size());
        Object[] objs = ((Object[]) (list.get(0)));
        Assert.assertEquals(2, objs.length);
        emp2 = ((Employment) (objs[0]));
        gavin = ((Person) (objs[1]));
        s.delete(emp2);
        s.delete(jboss);
        s.delete(gavin);
        s.delete(ifa);
        t.commit();
        s.close();
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    @FailureExpected(jiraKey = "unknown")
    public void testCompositeIdJoins() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Person person = new Person();
        person.setName("Noob");
        Product product = new Product();
        product.setProductId(new Product.ProductId());
        product.getProductId().setOrgid("x");
        product.getProductId().setProductnumber("1234");
        product.setName("Hibernate 3");
        Order order = new Order();
        order.setOrderId(new Order.OrderId());
        order.getOrderId().setOrdernumber("1");
        order.getOrderId().setOrgid("y");
        product.getOrders().add(order);
        order.setProduct(product);
        order.setPerson(person);
        s.save(product);
        s.save(order);
        s.save(person);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        Product p = ((Product) (s.createQuery("from Product p join fetch p.orders").list().get(0)));
        Assert.assertTrue(Hibernate.isInitialized(p.getOrders()));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        Object[] o = ((Object[]) (s.createSQLQuery(("select\r\n" + ((((((((((("        product.orgid as {product.id.orgid}," + "        product.productnumber as {product.id.productnumber},") + "        {prod_orders}.orgid as orgid3_1_,\r\n") + "        {prod_orders}.ordernumber as ordernum2_3_1_,\r\n") + "        product.name as {product.name},") + "        {prod_orders.element.*}") + /* "        orders.PROD_NO as PROD4_3_1_,\r\n" +
        "        orders.person as person3_1_,\r\n" +
        "        orders.PROD_ORGID as PROD3_0__,\r\n" +
        "        orders.PROD_NO as PROD4_0__,\r\n" +
        "        orders.orgid as orgid0__,\r\n" +
        "        orders.ordernumber as ordernum2_0__ \r\n" +
         */
        "    from\r\n") + "        Product product \r\n") + "    inner join\r\n") + "        TBL_ORDER {prod_orders} \r\n") + "            on product.orgid={prod_orders}.PROD_ORGID \r\n") + "            and product.productnumber={prod_orders}.PROD_NO"))).addEntity("product", Product.class).addJoin("prod_orders", "product.orders").list().get(0)));
        p = ((Product) (o[0]));
        Assert.assertTrue(Hibernate.isInitialized(p.getOrders()));
        Assert.assertNotNull(p.getOrders().iterator().next());
        t.commit();
        s.close();
    }

    @Test
    @SuppressWarnings({ "UnusedDeclaration", "deprecation", "UnusedAssignment" })
    public void testAutoDetectAliasing() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Organization ifa = new Organization("IFA");
        Organization jboss = new Organization("JBoss");
        Person gavin = new Person("Gavin");
        Employment emp = new Employment(gavin, jboss, "AU");
        Serializable orgId = s.save(jboss);
        Serializable orgId2 = s.save(ifa);
        s.save(gavin);
        s.save(emp);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        List list = s.createSQLQuery(getEmploymentSQL()).addEntity(Employment.class.getName()).list();
        Assert.assertEquals(1, list.size());
        Employment emp2 = ((Employment) (list.get(0)));
        Assert.assertEquals(emp2.getEmploymentId(), emp.getEmploymentId());
        Assert.assertEquals(emp2.getStartDate().getDate(), emp.getStartDate().getDate());
        Assert.assertEquals(emp2.getEndDate(), emp.getEndDate());
        s.clear();
        list = s.createSQLQuery(getEmploymentSQL()).addEntity(Employment.class.getName()).setResultTransformer(ALIAS_TO_ENTITY_MAP).list();
        Assert.assertEquals(1, list.size());
        Map m = ((Map) (list.get(0)));
        Assert.assertTrue(m.containsKey("Employment"));
        Assert.assertEquals(1, m.size());
        list = s.createSQLQuery(getEmploymentSQL()).list();
        Assert.assertEquals(1, list.size());
        Object[] o = ((Object[]) (list.get(0)));
        Assert.assertEquals(8, o.length);
        list = s.createSQLQuery(getEmploymentSQL()).setResultTransformer(new NativeSQLQueriesTest.UpperCasedAliasToEntityMapResultTransformer()).list();
        Assert.assertEquals(1, list.size());
        m = ((Map) (list.get(0)));
        Assert.assertTrue(m.containsKey("EMPID"));
        Assert.assertTrue(m.containsKey("AMOUNT"));
        Assert.assertTrue(m.containsKey("ENDDATE"));
        Assert.assertEquals(8, m.size());
        list = s.createSQLQuery(getEmploymentSQLMixedScalarEntity()).addScalar("employerid").addEntity(Employment.class).list();
        Assert.assertEquals(1, list.size());
        o = ((Object[]) (list.get(0)));
        Assert.assertEquals(2, o.length);
        ExtraAssertions.assertClassAssignability(Number.class, o[0].getClass());
        ExtraAssertions.assertClassAssignability(Employment.class, o[1].getClass());
        Query queryWithCollection = s.getNamedQuery("organizationEmploymentsExplicitAliases");
        queryWithCollection.setLong("id", jboss.getId());
        list = queryWithCollection.list();
        Assert.assertEquals(list.size(), 1);
        s.clear();
        list = s.createSQLQuery(getOrganizationJoinEmploymentSQL()).addEntity("org", Organization.class).addJoin("emp", "org.employments").list();
        Assert.assertEquals(2, list.size());
        s.clear();
        list = s.createSQLQuery(getOrganizationFetchJoinEmploymentSQL()).addEntity("org", Organization.class).addJoin("emp", "org.employments").list();
        Assert.assertEquals(2, list.size());
        s.clear();
        // TODO : why twice?
        s.getNamedQuery("organizationreturnproperty").list();
        list = s.getNamedQuery("organizationreturnproperty").list();
        Assert.assertEquals(2, list.size());
        s.clear();
        list = s.getNamedQuery("organizationautodetect").list();
        Assert.assertEquals(2, list.size());
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.delete(emp2);
        s.delete(jboss);
        s.delete(gavin);
        s.delete(ifa);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        Dimension dim = new Dimension(3, 30);
        s.save(dim);
        list = s.createSQLQuery("select d_len * d_width as surface, d_len * d_width * 10 as volume from Dimension").list();
        s.delete(dim);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        SpaceShip enterprise = new SpaceShip();
        enterprise.setModel("USS");
        enterprise.setName("Entreprise");
        enterprise.setSpeed(50.0);
        Dimension d = new Dimension(45, 10);
        enterprise.setDimensions(d);
        s.save(enterprise);
        Object[] result = ((Object[]) (s.getNamedQuery("spaceship").uniqueResult()));
        Assert.assertEquals("expecting 3 result values", 3, result.length);
        enterprise = ((SpaceShip) (result[0]));
        Assert.assertTrue((50.0 == (enterprise.getSpeed())));
        Assert.assertTrue((450.0 == (extractDoubleValue(result[1]))));
        Assert.assertTrue((4500.0 == (extractDoubleValue(result[2]))));
        s.delete(enterprise);
        t.commit();
        s.close();
    }

    @Test
    @SuppressWarnings({ "UnusedDeclaration" })
    public void testExplicitReturnAPI() {
        Session s = openSession();
        s.beginTransaction();
        Organization jboss = new Organization("JBoss");
        Person me = new Person("Steve");
        Employment emp = new Employment(me, jboss, "US");
        Serializable jbossId = s.save(jboss);
        s.save(me);
        s.save(emp);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        String sql = "SELECT org.ORGID 		as orgid," + ((((((((((("       org.NAME 		as name," + "       emp.EMPLOYER 	as employer,") + "       emp.EMPID 		as empid,") + "       emp.EMPLOYEE 	as employee,") + "       emp.EMPLOYER 	as employer,") + "       emp.STARTDATE 	as startDate,") + "       emp.ENDDATE 	as endDate,") + "       emp.REGIONCODE 	as regionCode,") + "       emp.AMOUNT 		as AMOUNT,") + "       emp.CURRENCY 	as CURRENCY") + " FROM 	ORGANIZATION org") + "    LEFT OUTER JOIN EMPLOYMENT emp ON org.ORGID = emp.EMPLOYER");
        // as a control, lets apply an existing rs mapping
        SQLQuery sqlQuery = s.createSQLQuery(sql);
        sqlQuery.setResultSetMapping("org-description");
        sqlQuery.list();
        // next try a partial mapping def
        sqlQuery.addRoot("org", Organization.class);
        sqlQuery.addFetch("emp", "org", "employments");
        sqlQuery.list();
        // now try full explicit mappings
        sqlQuery.addRoot("org", Organization.class).addProperty("id", "orgid").addProperty("name").addColumnAlias("name");
        sqlQuery.addFetch("emp", "org", "employments").addProperty("key", "employer").addProperty("element", "empid").addProperty("element.employee", "employee").addProperty("element.employer", "employer").addProperty("element.startDate", "startDate").addProperty("element.endDate", "endDate").addProperty("element.regionCode", "regionCode").addProperty("element.employmentId", "empId").addProperty("element.salary").addColumnAlias("AMOUNT").addColumnAlias("CURRENCY");
        sqlQuery.list();
        // lets try a totally different approach now and pull back scalars, first with explicit types
        sqlQuery.addScalar("orgid", LongType.INSTANCE).addScalar("name", INSTANCE).addScalar("empid", LongType.INSTANCE).addScalar("employee", LongType.INSTANCE).addScalar("startDate", TimestampType.INSTANCE).addScalar("endDate", TimestampType.INSTANCE).addScalar("regionCode", INSTANCE).addScalar("empId", LongType.INSTANCE).addScalar("AMOUNT", FloatType.INSTANCE).addScalar("CURRENCY", INSTANCE);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.delete(emp);
        s.delete(jboss);
        s.delete(me);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testMixAndMatchEntityScalar() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Speech speech = new Speech();
        speech.setLength(new Double(23.0));
        speech.setName("Mine");
        s.persist(speech);
        s.flush();
        s.clear();
        List l = s.createSQLQuery("select name, id, flength, name as scalarName from Speech").setResultSetMapping("speech").list();
        Assert.assertEquals(l.size(), 1);
        t.rollback();
        s.close();
    }

    @Test
    @SuppressWarnings({ "unchecked", "UnusedDeclaration" })
    public void testAddJoinForManyToMany() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Person gavin = new Person("Gavin");
        Person max = new Person("Max");
        Person pete = new Person("Pete");
        Group hibernate = new Group("Hibernate");
        Group seam = new Group("Seam");
        s.persist(gavin);
        s.persist(max);
        s.persist(pete);
        s.persist(seam);
        s.persist(hibernate);
        hibernate.getPersons().add(gavin);
        hibernate.getPersons().add(max);
        seam.getPersons().add(gavin);
        seam.getPersons().add(pete);
        s.flush();
        s.clear();
        // todo : see http://opensource.atlassian.com/projects/hibernate/browse/HHH-3908
        // String sqlStr = "SELECT {groupp.*} , {gp.*} " +
        // "FROM GROUPP groupp, GROUP_PERSON gp, PERSON person WHERE groupp.ID = gp.GROUP_ID and person.PERID = gp.PERSON_ID";
        // 
        // List l = s.createSQLQuery( sqlStr )
        // .addEntity("groupp", Group.class)
        // .addJoin("gp","groupp.persons")
        // .list();
        List l = s.getNamedQuery("manyToManyFetch").list();
        // assertEquals( 2, l.size() );
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        seam.getPersons().remove(gavin);
        seam.getPersons().remove(pete);
        hibernate.getPersons().remove(gavin);
        hibernate.getPersons().remove(max);
        s.delete(seam);
        s.delete(hibernate);
        s.delete(gavin);
        s.delete(max);
        s.delete(pete);
        t.commit();
        s.close();
    }

    @SkipForDialect(value = AbstractHANADialect.class, comment = "On HANA, this returns an clob for the text column which doesn't get mapped to a String")
    @Test
    public void testTextTypeInSQLQuery() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        String description = buildLongString(15000, 'a');
        TextHolder holder = new TextHolder(description);
        s.persist(holder);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        String descriptionRead = ((String) (s.createSQLQuery(getDescriptionsSQL()).uniqueResult()));
        Assert.assertEquals(description, descriptionRead);
        s.delete(holder);
        t.commit();
        s.close();
    }

    @SkipForDialect(value = AbstractHANADialect.class, comment = "On HANA, this returns a blob for the image column which doesn't get mapped to a byte[]")
    @Test
    public void testImageTypeInSQLQuery() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        byte[] photo = buildLongByteArray(15000, true);
        ImageHolder holder = new ImageHolder(photo);
        s.persist(holder);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        byte[] photoRead = ((byte[]) (s.createSQLQuery(getPhotosSQL()).uniqueResult()));
        Assert.assertTrue(Arrays.equals(photo, photoRead));
        s.delete(holder);
        t.commit();
        s.close();
    }

    @Test
    @RequiresDialect(MySQL5Dialect.class)
    public void testEscapeColonInSQL() throws QueryException {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        SQLQuery query = s.createSQLQuery("SELECT @row \\:= 1");
        List list = query.list();
        Assert.assertTrue(list.get(0).toString().equals("1"));
        t.commit();
        s.close();
    }

    @SuppressWarnings({ "unchecked" })
    private static class UpperCasedAliasToEntityMapResultTransformer extends BasicTransformerAdapter implements Serializable {
        public Object transformTuple(Object[] tuple, String[] aliases) {
            Map result = new HashMap(tuple.length);
            for (int i = 0; i < (tuple.length); i++) {
                String alias = aliases[i];
                if (alias != null) {
                    result.put(alias.toUpperCase(Locale.ROOT), tuple[i]);
                }
            }
            return result;
        }
    }
}

