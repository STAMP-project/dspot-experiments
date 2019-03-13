/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.embedded;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.test.util.SchemaUtil;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;

import static org.hibernate.test.annotations.embedded.FloatLeg.RateIndex.LIBOR;
import static org.hibernate.test.annotations.embedded.FloatLeg.RateIndex.TIBOR;
import static org.hibernate.test.annotations.embedded.Leg.Frequency.MONTHLY;
import static org.hibernate.test.annotations.embedded.Leg.Frequency.QUARTERLY;
import static org.hibernate.test.annotations.embedded.Leg.Frequency.SEMIANNUALLY;
import static org.hibernate.test.annotations.embedded.VanillaSwap.Currency.EUR;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class EmbeddedTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testSimple() throws Exception {
        Person person = new Person();
        Address a = new Address();
        Country c = new Country();
        Country bornCountry = new Country();
        c.setIso2("DM");
        c.setName("Matt Damon Land");
        bornCountry.setIso2("US");
        bornCountry.setName("United States of America");
        a.address1 = "colorado street";
        a.city = "Springfield";
        a.country = c;
        person.address = a;
        person.bornIn = bornCountry;
        person.name = "Homer";
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.persist(person);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Person p = session.get(.class, person.id);
            assertNotNull(p);
            assertNotNull(p.address);
            assertEquals("Springfield", p.address.city);
            assertNotNull(p.address.country);
            assertEquals("DM", p.address.country.getIso2());
            assertNotNull(p.bornIn);
            assertEquals("US", p.bornIn.getIso2());
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-8172")
    public void testQueryWithEmbeddedIsNull() throws Exception {
        Person person = new Person();
        Address a = new Address();
        Country c = new Country();
        Country bornCountry = new Country();
        c.setIso2("DM");
        c.setName("Matt Damon Land");
        Assert.assertNull(bornCountry.getIso2());
        Assert.assertNull(bornCountry.getName());
        a.address1 = "colorado street";
        a.city = "Springfield";
        a.country = c;
        person.address = a;
        person.bornIn = bornCountry;
        person.name = "Homer";
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.persist(person);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Person p = ((Person) (session.createQuery("from Person p where p.bornIn is null").uniqueResult()));
            assertNotNull(p);
            assertNotNull(p.address);
            assertEquals("Springfield", p.address.city);
            assertNotNull(p.address.country);
            assertEquals("DM", p.address.country.getIso2());
            assertNull(p.bornIn);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-8172")
    @FailureExpected(jiraKey = "HHH-8172")
    public void testQueryWithEmbeddedParameterAllNull() throws Exception {
        Session s;
        Transaction tx;
        Person person = new Person();
        Address a = new Address();
        Country c = new Country();
        Country bornCountry = new Country();
        Assert.assertNull(bornCountry.getIso2());
        Assert.assertNull(bornCountry.getName());
        a.address1 = "colorado street";
        a.city = "Springfield";
        a.country = c;
        person.address = a;
        person.bornIn = bornCountry;
        person.name = "Homer";
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.persist(person);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Person p = ((Person) (session.createQuery("from Person p where p.bornIn = :b").setParameter("b", person.bornIn).uniqueResult()));
            assertNotNull(p);
            assertNotNull(p.address);
            assertEquals("Springfield", p.address.city);
            assertNotNull(p.address.country);
            assertEquals("DM", p.address.country.getIso2());
            assertNull(p.bornIn);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-8172")
    @FailureExpected(jiraKey = "HHH-8172")
    public void testQueryWithEmbeddedParameterOneNull() throws Exception {
        Person person = new Person();
        Address a = new Address();
        Country c = new Country();
        Country bornCountry = new Country();
        c.setIso2("DM");
        c.setName("Matt Damon Land");
        bornCountry.setIso2("US");
        Assert.assertNull(bornCountry.getName());
        a.address1 = "colorado street";
        a.city = "Springfield";
        a.country = c;
        person.address = a;
        person.bornIn = bornCountry;
        person.name = "Homer";
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.persist(person);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Person p = ((Person) (session.createQuery("from Person p where p.bornIn = :b").setParameter("b", person.bornIn).uniqueResult()));
            assertNotNull(p);
            assertNotNull(p.address);
            assertEquals("Springfield", p.address.city);
            assertNotNull(p.address.country);
            assertEquals("DM", p.address.country.getIso2());
            assertEquals("US", p.bornIn.getIso2());
            assertNull(p.bornIn.getName());
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-8172")
    public void testQueryWithEmbeddedWithNullUsingSubAttributes() throws Exception {
        Person person = new Person();
        Address a = new Address();
        Country c = new Country();
        Country bornCountry = new Country();
        c.setIso2("DM");
        c.setName("Matt Damon Land");
        bornCountry.setIso2("US");
        Assert.assertNull(bornCountry.getName());
        a.address1 = "colorado street";
        a.city = "Springfield";
        a.country = c;
        person.address = a;
        person.bornIn = bornCountry;
        person.name = "Homer";
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.persist(person);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Person p = ((Person) (session.createQuery(("from Person p " + ("where ( p.bornIn.iso2 is null or p.bornIn.iso2 = :i ) and " + "( p.bornIn.name is null or p.bornIn.name = :n )"))).setParameter("i", person.bornIn.getIso2()).setParameter("n", person.bornIn.getName()).uniqueResult()));
            assertNotNull(p);
            assertNotNull(p.address);
            assertEquals("Springfield", p.address.city);
            assertNotNull(p.address.country);
            assertEquals("DM", p.address.country.getIso2());
            assertEquals("US", p.bornIn.getIso2());
            assertNull(p.bornIn.getName());
        });
    }

    @Test
    public void testCompositeId() throws Exception {
        Session s;
        Transaction tx;
        RegionalArticlePk pk = new RegionalArticlePk();
        pk.iso2 = "FR";
        pk.localUniqueKey = "1234567890123";
        RegionalArticle reg = new RegionalArticle();
        reg.setName("Je ne veux pes rester sage - Dolly");
        reg.setPk(pk);
        s = openSession();
        tx = s.beginTransaction();
        s.persist(reg);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        reg = ((RegionalArticle) (s.get(RegionalArticle.class, reg.getPk())));
        Assert.assertNotNull(reg);
        Assert.assertNotNull(reg.getPk());
        Assert.assertEquals("Je ne veux pes rester sage - Dolly", reg.getName());
        Assert.assertEquals("FR", reg.getPk().iso2);
        tx.commit();
        s.close();
    }

    @Test
    public void testManyToOneInsideComponent() throws Exception {
        Session s;
        Transaction tx;
        s = openSession();
        tx = s.beginTransaction();
        Person p = new Person();
        Country bornIn = new Country();
        bornIn.setIso2("FR");
        bornIn.setName("France");
        p.bornIn = bornIn;
        p.name = "Emmanuel";
        AddressType type = new AddressType();
        type.setName("Primary Home");
        s.persist(type);
        Country currentCountry = new Country();
        currentCountry.setIso2("US");
        currentCountry.setName("USA");
        Address add = new Address();
        add.address1 = "4 square street";
        add.city = "San diego";
        add.country = currentCountry;
        add.type = type;
        p.address = add;
        s.persist(p);
        tx.commit();
        s = openSession();
        tx = s.beginTransaction();
        Query q = s.createQuery("select p from Person p where p.address.city = :city");
        q.setString("city", add.city);
        List result = q.list();
        Person samePerson = ((Person) (result.get(0)));
        Assert.assertNotNull(samePerson.address.type);
        Assert.assertEquals(type.getName(), samePerson.address.type.getName());
        tx.commit();
        s.close();
    }

    @Test
    public void testEmbeddedSuperclass() {
        Session s;
        Transaction tx;
        s = openSession();
        tx = s.beginTransaction();
        VanillaSwap swap = new VanillaSwap();
        swap.setInstrumentId("US345421");
        swap.setCurrency(EUR);
        FixedLeg fixed = new FixedLeg();
        fixed.setPaymentFrequency(SEMIANNUALLY);
        fixed.setRate(5.6);
        FloatLeg floating = new FloatLeg();
        floating.setPaymentFrequency(QUARTERLY);
        floating.setRateIndex(LIBOR);
        floating.setRateSpread(1.1);
        swap.setFixedLeg(fixed);
        swap.setFloatLeg(floating);
        s.persist(swap);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        swap = ((VanillaSwap) (s.get(VanillaSwap.class, swap.getInstrumentId())));
        // All fields must be filled with non-default values
        fixed = swap.getFixedLeg();
        Assert.assertNotNull("Fixed leg retrieved as null", fixed);
        floating = swap.getFloatLeg();
        Assert.assertNotNull("Floating leg retrieved as null", floating);
        Assert.assertEquals(SEMIANNUALLY, fixed.getPaymentFrequency());
        Assert.assertEquals(QUARTERLY, floating.getPaymentFrequency());
        s.delete(swap);
        tx.commit();
        s.close();
    }

    @Test
    public void testDottedProperty() {
        Session s;
        Transaction tx;
        s = openSession();
        tx = s.beginTransaction();
        // Create short swap
        Swap shortSwap = new Swap();
        shortSwap.setTenor(2);
        FixedLeg shortFixed = new FixedLeg();
        shortFixed.setPaymentFrequency(SEMIANNUALLY);
        shortFixed.setRate(5.6);
        FloatLeg shortFloating = new FloatLeg();
        shortFloating.setPaymentFrequency(QUARTERLY);
        shortFloating.setRateIndex(LIBOR);
        shortFloating.setRateSpread(1.1);
        shortSwap.setFixedLeg(shortFixed);
        shortSwap.setFloatLeg(shortFloating);
        // Create medium swap
        Swap swap = new Swap();
        swap.setTenor(7);
        FixedLeg fixed = new FixedLeg();
        fixed.setPaymentFrequency(MONTHLY);
        fixed.setRate(7.6);
        FloatLeg floating = new FloatLeg();
        floating.setPaymentFrequency(MONTHLY);
        floating.setRateIndex(TIBOR);
        floating.setRateSpread(0.8);
        swap.setFixedLeg(fixed);
        swap.setFloatLeg(floating);
        // Create long swap
        Swap longSwap = new Swap();
        longSwap.setTenor(7);
        FixedLeg longFixed = new FixedLeg();
        longFixed.setPaymentFrequency(MONTHLY);
        longFixed.setRate(7.6);
        FloatLeg longFloating = new FloatLeg();
        longFloating.setPaymentFrequency(MONTHLY);
        longFloating.setRateIndex(TIBOR);
        longFloating.setRateSpread(0.8);
        longSwap.setFixedLeg(longFixed);
        longSwap.setFloatLeg(longFloating);
        // Compose a curve spread deal
        SpreadDeal deal = new SpreadDeal();
        deal.setId("FX45632");
        deal.setNotional(450000.0);
        deal.setShortSwap(shortSwap);
        deal.setSwap(swap);
        deal.setLongSwap(longSwap);
        s.persist(deal);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        deal = ((SpreadDeal) (s.get(SpreadDeal.class, deal.getId())));
        // All fields must be filled with non-default values
        Assert.assertNotNull("Short swap is null.", deal.getShortSwap());
        Assert.assertNotNull("Swap is null.", deal.getSwap());
        Assert.assertNotNull("Long swap is null.", deal.getLongSwap());
        Assert.assertEquals(2, deal.getShortSwap().getTenor());
        Assert.assertEquals(7, deal.getSwap().getTenor());
        Assert.assertEquals(7, deal.getLongSwap().getTenor());
        Assert.assertNotNull("Short fixed leg is null.", deal.getShortSwap().getFixedLeg());
        Assert.assertNotNull("Short floating leg is null.", deal.getShortSwap().getFloatLeg());
        Assert.assertNotNull("Fixed leg is null.", deal.getSwap().getFixedLeg());
        Assert.assertNotNull("Floating leg is null.", deal.getSwap().getFloatLeg());
        Assert.assertNotNull("Long fixed leg is null.", deal.getLongSwap().getFixedLeg());
        Assert.assertNotNull("Long floating leg is null.", deal.getLongSwap().getFloatLeg());
        Assert.assertEquals(SEMIANNUALLY, deal.getShortSwap().getFixedLeg().getPaymentFrequency());
        Assert.assertEquals(QUARTERLY, deal.getShortSwap().getFloatLeg().getPaymentFrequency());
        Assert.assertEquals(MONTHLY, deal.getSwap().getFixedLeg().getPaymentFrequency());
        Assert.assertEquals(MONTHLY, deal.getSwap().getFloatLeg().getPaymentFrequency());
        Assert.assertEquals(MONTHLY, deal.getLongSwap().getFixedLeg().getPaymentFrequency());
        Assert.assertEquals(MONTHLY, deal.getLongSwap().getFloatLeg().getPaymentFrequency());
        Assert.assertEquals(5.6, deal.getShortSwap().getFixedLeg().getRate(), 0.01);
        Assert.assertEquals(7.6, deal.getSwap().getFixedLeg().getRate(), 0.01);
        Assert.assertEquals(7.6, deal.getLongSwap().getFixedLeg().getRate(), 0.01);
        Assert.assertEquals(LIBOR, deal.getShortSwap().getFloatLeg().getRateIndex());
        Assert.assertEquals(TIBOR, deal.getSwap().getFloatLeg().getRateIndex());
        Assert.assertEquals(TIBOR, deal.getLongSwap().getFloatLeg().getRateIndex());
        Assert.assertEquals(1.1, deal.getShortSwap().getFloatLeg().getRateSpread(), 0.01);
        Assert.assertEquals(0.8, deal.getSwap().getFloatLeg().getRateSpread(), 0.01);
        Assert.assertEquals(0.8, deal.getLongSwap().getFloatLeg().getRateSpread(), 0.01);
        s.delete(deal);
        tx.commit();
        s.close();
    }

    @Test
    public void testEmbeddedInSecondaryTable() throws Exception {
        Session s;
        s = openSession();
        s.getTransaction().begin();
        Book book = new Book();
        book.setIsbn("1234");
        book.setName("HiA Second Edition");
        Summary summary = new Summary();
        summary.setText("This is a HiA SE summary");
        summary.setSize(summary.getText().length());
        book.setSummary(summary);
        s.persist(book);
        s.getTransaction().commit();
        s.clear();
        Transaction tx = s.beginTransaction();
        Book loadedBook = ((Book) (s.get(Book.class, book.getIsbn())));
        Assert.assertNotNull(loadedBook.getSummary());
        Assert.assertEquals(book.getSummary().getText(), loadedBook.getSummary().getText());
        s.delete(loadedBook);
        tx.commit();
        s.close();
    }

    @Test
    public void testParent() throws Exception {
        Session s;
        s = openSession();
        s.getTransaction().begin();
        Book book = new Book();
        book.setIsbn("1234");
        book.setName("HiA Second Edition");
        Summary summary = new Summary();
        summary.setText("This is a HiA SE summary");
        summary.setSize(summary.getText().length());
        book.setSummary(summary);
        s.persist(book);
        s.getTransaction().commit();
        s.clear();
        Transaction tx = s.beginTransaction();
        Book loadedBook = ((Book) (s.get(Book.class, book.getIsbn())));
        Assert.assertNotNull(loadedBook.getSummary());
        Assert.assertEquals(loadedBook, loadedBook.getSummary().getSummarizedBook());
        s.delete(loadedBook);
        tx.commit();
        s.close();
    }

    @Test
    public void testEmbeddedAndMultipleManyToOne() throws Exception {
        Session s;
        s = openSession();
        Transaction tx = s.beginTransaction();
        CorpType type = new CorpType();
        type.setType("National");
        s.persist(type);
        Nationality nat = new Nationality();
        nat.setName("Canadian");
        s.persist(nat);
        InternetProvider provider = new InternetProvider();
        provider.setBrandName("Fido");
        LegalStructure structure = new LegalStructure();
        structure.setCorporationType(type);
        structure.setCountry("Canada");
        structure.setName("Rogers");
        provider.setOwner(structure);
        structure.setOrigin(nat);
        s.persist(provider);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        provider = ((InternetProvider) (s.get(InternetProvider.class, provider.getId())));
        Assert.assertNotNull(provider.getOwner());
        Assert.assertNotNull("Many to one not set", provider.getOwner().getCorporationType());
        Assert.assertEquals("Wrong link", type.getType(), provider.getOwner().getCorporationType().getType());
        Assert.assertNotNull("2nd Many to one not set", provider.getOwner().getOrigin());
        Assert.assertEquals("Wrong 2nd link", nat.getName(), provider.getOwner().getOrigin().getName());
        s.delete(provider);
        s.delete(provider.getOwner().getCorporationType());
        s.delete(provider.getOwner().getOrigin());
        tx.commit();
        s.close();
    }

    @Test
    public void testEmbeddedAndOneToMany() throws Exception {
        Session s;
        s = openSession();
        Transaction tx = s.beginTransaction();
        InternetProvider provider = new InternetProvider();
        provider.setBrandName("Fido");
        LegalStructure structure = new LegalStructure();
        structure.setCountry("Canada");
        structure.setName("Rogers");
        provider.setOwner(structure);
        s.persist(provider);
        Manager manager = new Manager();
        manager.setName("Bill");
        manager.setEmployer(provider);
        structure.getTopManagement().add(manager);
        s.persist(manager);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        provider = ((InternetProvider) (s.get(InternetProvider.class, provider.getId())));
        Assert.assertNotNull(provider.getOwner());
        Set<Manager> topManagement = provider.getOwner().getTopManagement();
        Assert.assertNotNull("OneToMany not set", topManagement);
        Assert.assertEquals("Wrong number of elements", 1, topManagement.size());
        manager = topManagement.iterator().next();
        Assert.assertEquals("Wrong element", "Bill", manager.getName());
        s.delete(manager);
        s.delete(provider);
        tx.commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9642")
    public void testEmbeddedAndOneToManyHql() throws Exception {
        Session s;
        s = openSession();
        Transaction tx = s.beginTransaction();
        InternetProvider provider = new InternetProvider();
        provider.setBrandName("Fido");
        LegalStructure structure = new LegalStructure();
        structure.setCountry("Canada");
        structure.setName("Rogers");
        provider.setOwner(structure);
        s.persist(provider);
        Manager manager = new Manager();
        manager.setName("Bill");
        manager.setEmployer(provider);
        structure.getTopManagement().add(manager);
        s.persist(manager);
        tx.commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        InternetProvider internetProviderQueried = ((InternetProvider) (s.createQuery("from InternetProvider").uniqueResult()));
        Assert.assertFalse(Hibernate.isInitialized(internetProviderQueried.getOwner().getTopManagement()));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        internetProviderQueried = ((InternetProvider) (s.createQuery("from InternetProvider i join fetch i.owner.topManagement").uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(internetProviderQueried.getOwner().getTopManagement()));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        internetProviderQueried = ((InternetProvider) (s.createQuery("from InternetProvider i join fetch i.owner o join fetch o.topManagement").uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(internetProviderQueried.getOwner().getTopManagement()));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        provider = ((InternetProvider) (s.get(InternetProvider.class, provider.getId())));
        manager = provider.getOwner().getTopManagement().iterator().next();
        s.delete(manager);
        s.delete(provider);
        tx.commit();
        s.close();
    }

    @Test
    public void testDefaultCollectionTable() throws Exception {
        // are the tables correct?
        Assert.assertTrue(SchemaUtil.isTablePresent("WealthyPerson_vacationHomes", metadata()));
        Assert.assertTrue(SchemaUtil.isTablePresent("WealthyPerson_legacyVacationHomes", metadata()));
        Assert.assertTrue(SchemaUtil.isTablePresent("WelPers_VacHomes", metadata()));
        // just to make sure, use the mapping
        Session s;
        Transaction tx;
        WealthyPerson p = new WealthyPerson();
        Address a = new Address();
        Address vacation = new Address();
        Country c = new Country();
        Country bornCountry = new Country();
        c.setIso2("DM");
        c.setName("Matt Damon Land");
        bornCountry.setIso2("US");
        bornCountry.setName("United States of America");
        a.address1 = "colorado street";
        a.city = "Springfield";
        a.country = c;
        vacation.address1 = "rock street";
        vacation.city = "Plymouth";
        vacation.country = c;
        p.vacationHomes.add(vacation);
        p.address = a;
        p.bornIn = bornCountry;
        p.name = "Homer";
        s = openSession();
        tx = s.beginTransaction();
        s.persist(p);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        p = ((WealthyPerson) (s.get(WealthyPerson.class, p.id)));
        Assert.assertNotNull(p);
        Assert.assertNotNull(p.address);
        Assert.assertEquals("Springfield", p.address.city);
        Assert.assertNotNull(p.address.country);
        Assert.assertEquals("DM", p.address.country.getIso2());
        Assert.assertNotNull(p.bornIn);
        Assert.assertEquals("US", p.bornIn.getIso2());
        tx.commit();
        s.close();
    }

    // make sure we support collection of embeddable objects inside embeddable objects
    @Test
    public void testEmbeddableInsideEmbeddable() throws Exception {
        Session s;
        Transaction tx;
        Collection<URLFavorite> urls = new ArrayList<URLFavorite>();
        URLFavorite urlFavorite = new URLFavorite();
        urlFavorite.setUrl("http://highscalability.com/");
        urls.add(urlFavorite);
        urlFavorite = new URLFavorite();
        urlFavorite.setUrl("http://www.jboss.org/");
        urls.add(urlFavorite);
        urlFavorite = new URLFavorite();
        urlFavorite.setUrl("http://www.hibernate.org/");
        urls.add(urlFavorite);
        urlFavorite = new URLFavorite();
        urlFavorite.setUrl("http://www.jgroups.org/");
        urls.add(urlFavorite);
        Collection<String> ideas = new ArrayList<String>();
        ideas.add("lionheart");
        ideas.add("xforms");
        ideas.add("dynamic content");
        ideas.add("http");
        InternetFavorites internetFavorites = new InternetFavorites();
        internetFavorites.setLinks(urls);
        internetFavorites.setIdeas(ideas);
        FavoriteThings favoriteThings = new FavoriteThings();
        favoriteThings.setWeb(internetFavorites);
        s = openSession();
        tx = s.beginTransaction();
        s.persist(favoriteThings);
        tx.commit();
        tx = s.beginTransaction();
        s.flush();
        favoriteThings = ((FavoriteThings) (s.get(FavoriteThings.class, favoriteThings.getId())));
        Assert.assertTrue("has web", ((favoriteThings.getWeb()) != null));
        Assert.assertTrue("has ideas", ((favoriteThings.getWeb().getIdeas()) != null));
        Assert.assertTrue("has favorite idea 'http'", favoriteThings.getWeb().getIdeas().contains("http"));
        Assert.assertTrue("has favorite idea 'http'", favoriteThings.getWeb().getIdeas().contains("dynamic content"));
        urls = favoriteThings.getWeb().getLinks();
        Assert.assertTrue("has urls", (urls != null));
        URLFavorite[] favs = new URLFavorite[4];
        urls.toArray(favs);
        Assert.assertTrue("has http://www.hibernate.org url favorite link", (((("http://www.hibernate.org/".equals(favs[0].getUrl())) || ("http://www.hibernate.org/".equals(favs[1].getUrl()))) || ("http://www.hibernate.org/".equals(favs[2].getUrl()))) || ("http://www.hibernate.org/".equals(favs[3].getUrl()))));
        tx.commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-3868")
    public void testTransientMergeComponentParent() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Book b = new Book();
        b.setIsbn(UUID.randomUUID().toString());
        b.setSummary(new Summary());
        b = ((Book) (s.merge(b)));
        tx.commit();
        s.close();
    }
}

