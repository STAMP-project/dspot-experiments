package com.baeldung.dao.repositories;


import com.baeldung.dao.repositories.impl.PersonInsertRepository;
import com.baeldung.domain.Person;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@DataJpaTest
@Import(PersonInsertRepository.class)
public class PersonInsertRepositoryIntegrationTest {
    private static final Long ID = 1L;

    private static final String FIRST_NAME = "firstname";

    private static final String LAST_NAME = "lastname";

    private static final Person PERSON = new Person(PersonInsertRepositoryIntegrationTest.ID, PersonInsertRepositoryIntegrationTest.FIRST_NAME, PersonInsertRepositoryIntegrationTest.LAST_NAME);

    @Autowired
    private PersonInsertRepository personInsertRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void givenPersonEntity_whenInsertWithNativeQuery_ThenPersonIsPersisted() {
        insertWithQuery();
        assertPersonPersisted();
    }

    @Test
    public void givenPersonEntity_whenInsertedTwiceWithNativeQuery_thenPersistenceExceptionExceptionIsThrown() {
        assertThatExceptionOfType(PersistenceException.class).isThrownBy(() -> {
            insertWithQuery();
            insertWithQuery();
        });
    }

    @Test
    public void givenPersonEntity_whenInsertWithEntityManager_thenPersonIsPersisted() {
        insertPersonWithEntityManager();
        assertPersonPersisted();
    }

    @Test
    public void givenPersonEntity_whenInsertedTwiceWithEntityManager_thenEntityExistsExceptionIsThrown() {
        assertThatExceptionOfType(EntityExistsException.class).isThrownBy(() -> {
            insertPersonWithEntityManager();
            insertPersonWithEntityManager();
        });
    }
}

