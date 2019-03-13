package org.javaee7.jpa.extended.pc;


import java.util.List;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class ExtendedPersistenceContextTest {
    @PersistenceContext
    EntityManager em;

    @EJB
    CharactersBean bean;

    @Test
    @InSequence(1)
    public void should_not_persist_changes_without_transaction_flush() {
        List<Character> characters = em.createNamedQuery(FIND_ALL, Character.class).getResultList();
        Character raj = em.find(Character.class, 6);
        Assert.assertThat(characters, hasSize(7));
        Assert.assertThat(raj.getName(), CoreMatchers.is(CoreMatchers.equalTo("Raj")));
    }

    @Test
    @InSequence(2)
    public void should_update_characters_after_transaction_flush() {
        // when
        bean.commitChanges();
        // then
        List<Character> characters = em.createNamedQuery(FIND_ALL, Character.class).getResultList();
        Character rajesh = em.find(Character.class, 6);
        Character wil = em.find(Character.class, 8);
        Assert.assertThat(characters, hasSize(8));
        Assert.assertThat(rajesh.getName(), CoreMatchers.is(CoreMatchers.equalTo("Rajesh Ramayan")));
        Assert.assertThat(wil.getName(), CoreMatchers.is(CoreMatchers.equalTo("Wil Wheaton")));
    }
}

