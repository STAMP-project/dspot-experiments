package com.baeldung.hibernate.lifecycle;


import Status.DELETED;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Test;


public class HibernateLifecycleUnitTest {
    @Test
    public void whenEntityLoaded_thenEntityManaged() throws Exception {
        SessionFactory sessionFactory = HibernateLifecycleUtil.HibernateLifecycleUtil.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = startTransaction(session);
            assertThat(getManagedEntities(session)).isEmpty();
            List<FootballPlayer> players = session.createQuery("from FootballPlayer").getResultList();
            assertThat(getManagedEntities(session)).size().isEqualTo(3);
            assertThat(DirtyDataInspector.getDirtyEntities()).isEmpty();
            FootballPlayer gigiBuffon = players.stream().filter(( p) -> (p.getId()) == 3).findFirst().get();
            gigiBuffon.setName("Gianluigi Buffon");
            transaction.commit();
            assertThat(DirtyDataInspector.getDirtyEntities()).size().isEqualTo(1);
            assertThat(DirtyDataInspector.getDirtyEntities().get(0).getId()).isEqualTo(3);
            assertThat(DirtyDataInspector.getDirtyEntities().get(0).getName()).isEqualTo("Gianluigi Buffon");
        }
    }

    @Test
    public void whenDetached_thenNotTracked() throws Exception {
        SessionFactory sessionFactory = HibernateLifecycleUtil.HibernateLifecycleUtil.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = startTransaction(session);
            FootballPlayer cr7 = session.get(FootballPlayer.class, 1L);
            assertThat(getManagedEntities(session)).size().isEqualTo(1);
            assertThat(getManagedEntities(session).get(0).getId()).isEqualTo(cr7.getId());
            session.evict(cr7);
            assertThat(getManagedEntities(session)).size().isEqualTo(0);
            cr7.setName("CR7");
            transaction.commit();
            assertThat(DirtyDataInspector.getDirtyEntities()).isEmpty();
        }
    }

    @Test
    public void whenReattached_thenTrackedAgain() throws Exception {
        SessionFactory sessionFactory = HibernateLifecycleUtil.HibernateLifecycleUtil.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = startTransaction(session);
            FootballPlayer messi = session.get(FootballPlayer.class, 2L);
            session.evict(messi);
            messi.setName("Leo Messi");
            transaction.commit();
            assertThat(DirtyDataInspector.getDirtyEntities()).isEmpty();
            transaction = startTransaction(session);
            session.update(messi);
            transaction.commit();
            assertThat(DirtyDataInspector.getDirtyEntities()).size().isEqualTo(1);
            assertThat(DirtyDataInspector.getDirtyEntities().get(0).getName()).isEqualTo("Leo Messi");
        }
    }

    @Test
    public void givenNewEntityWithID_whenReattached_thenManaged() throws Exception {
        SessionFactory sessionFactory = HibernateLifecycleUtil.HibernateLifecycleUtil.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = startTransaction(session);
            FootballPlayer gigi = new FootballPlayer();
            gigi.setId(3);
            gigi.setName("Gigi the Legend");
            session.update(gigi);
            assertThat(getManagedEntities(session)).size().isEqualTo(1);
            transaction.commit();
            assertThat(DirtyDataInspector.getDirtyEntities()).size().isEqualTo(1);
        }
    }

    @Test
    public void givenTransientEntity_whenSave_thenManaged() throws Exception {
        SessionFactory sessionFactory = HibernateLifecycleUtil.HibernateLifecycleUtil.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = startTransaction(session);
            FootballPlayer neymar = new FootballPlayer();
            neymar.setName("Neymar");
            session.save(neymar);
            assertThat(getManagedEntities(session)).size().isEqualTo(1);
            assertThat(neymar.getId()).isNotNull();
            int count = queryCount("select count(*) from Football_Player where name='Neymar'");
            assertThat(count).isEqualTo(0);
            transaction.commit();
            count = queryCount("select count(*) from Football_Player where name='Neymar'");
            assertThat(count).isEqualTo(1);
            transaction = startTransaction(session);
            session.delete(neymar);
            transaction.commit();
        }
    }

    @Test
    public void whenDelete_thenMarkDeleted() throws Exception {
        SessionFactory sessionFactory = HibernateLifecycleUtil.HibernateLifecycleUtil.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = startTransaction(session);
            FootballPlayer neymar = new FootballPlayer();
            neymar.setName("Neymar");
            session.save(neymar);
            transaction.commit();
            transaction = startTransaction(session);
            session.delete(neymar);
            assertThat(getManagedEntities(session).get(0).getStatus()).isEqualTo(DELETED);
            transaction.commit();
        }
    }
}

