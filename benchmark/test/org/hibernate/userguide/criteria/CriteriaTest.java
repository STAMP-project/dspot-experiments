/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.criteria;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.userguide.model.Partner;
import org.hibernate.userguide.model.Person;
import org.hibernate.userguide.model.Phone;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class CriteriaTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test_criteria_typedquery_entity_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::criteria-typedquery-entity-example[]
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Person> criteria = builder.createQuery(.class);
            Root<Person> root = criteria.from(.class);
            criteria.select(root);
            criteria.where(builder.equal(root.get(Person_.name), "John Doe"));
            List<Person> persons = entityManager.createQuery(criteria).getResultList();
            // end::criteria-typedquery-entity-example[]
            assertEquals(1, persons.size());
        });
    }

    @Test
    public void test_criteria_typedquery_expression_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::criteria-typedquery-expression-example[]
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<String> criteria = builder.createQuery(.class);
            Root<Person> root = criteria.from(.class);
            criteria.select(root.get(Person_.nickName));
            criteria.where(builder.equal(root.get(Person_.name), "John Doe"));
            List<String> nickNames = entityManager.createQuery(criteria).getResultList();
            // end::criteria-typedquery-expression-example[]
            assertEquals(1, nickNames.size());
        });
    }

    @Test
    public void test_criteria_typedquery_multiselect_explicit_array_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::criteria-typedquery-multiselect-array-explicit-example[]
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Object[]> criteria = builder.createQuery(.class);
            Root<Person> root = criteria.from(.class);
            Path<Long> idPath = root.get(Person_.id);
            Path<String> nickNamePath = root.get(Person_.nickName);
            criteria.select(builder.array(idPath, nickNamePath));
            criteria.where(builder.equal(root.get(Person_.name), "John Doe"));
            List<Object[]> idAndNickNames = entityManager.createQuery(criteria).getResultList();
            // end::criteria-typedquery-multiselect-array-explicit-example[]
            assertEquals(1, idAndNickNames.size());
        });
    }

    @Test
    public void test_criteria_typedquery_multiselect_implicit_array_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::criteria-typedquery-multiselect-array-implicit-example[]
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Object[]> criteria = builder.createQuery(.class);
            Root<Person> root = criteria.from(.class);
            Path<Long> idPath = root.get(Person_.id);
            Path<String> nickNamePath = root.get(Person_.nickName);
            criteria.multiselect(idPath, nickNamePath);
            criteria.where(builder.equal(root.get(Person_.name), "John Doe"));
            List<Object[]> idAndNickNames = entityManager.createQuery(criteria).getResultList();
            // end::criteria-typedquery-multiselect-array-implicit-example[]
            assertEquals(1, idAndNickNames.size());
        });
    }

    @Test
    public void test_criteria_typedquery_wrapper_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::criteria-typedquery-wrapper-example[]
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<PersonWrapper> criteria = builder.createQuery(.class);
            Root<Person> root = criteria.from(.class);
            Path<Long> idPath = root.get(Person_.id);
            Path<String> nickNamePath = root.get(Person_.nickName);
            criteria.select(builder.construct(.class, idPath, nickNamePath));
            criteria.where(builder.equal(root.get(Person_.name), "John Doe"));
            List<PersonWrapper> wrappers = entityManager.createQuery(criteria).getResultList();
            // end::criteria-typedquery-wrapper-example[]
            assertEquals(1, wrappers.size());
        });
    }

    @Test
    public void test_criteria_tuple_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::criteria-tuple-example[]
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Tuple> criteria = builder.createQuery(.class);
            Root<Person> root = criteria.from(.class);
            Path<Long> idPath = root.get(Person_.id);
            Path<String> nickNamePath = root.get(Person_.nickName);
            criteria.multiselect(idPath, nickNamePath);
            criteria.where(builder.equal(root.get(Person_.name), "John Doe"));
            List<Tuple> tuples = entityManager.createQuery(criteria).getResultList();
            for (Tuple tuple : tuples) {
                Long id = tuple.get(idPath);
                String nickName = tuple.get(nickNamePath);
            }
            // or using indices
            for (Tuple tuple : tuples) {
                Long id = ((Long) (tuple.get(0)));
                String nickName = ((String) (tuple.get(1)));
            }
            // end::criteria-tuple-example[]
            assertEquals(1, tuples.size());
        });
    }

    @Test
    public void test_criteria_from_root_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::criteria-from-root-example[]
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Person> criteria = builder.createQuery(.class);
            Root<Person> root = criteria.from(.class);
            // end::criteria-from-root-example[]
        });
    }

    @Test
    public void test_criteria_from_multiple_root_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            String address = "Earth";
            String prefix = "J%";
            // tag::criteria-from-multiple-root-example[]
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Tuple> criteria = builder.createQuery(.class);
            Root<Person> personRoot = criteria.from(.class);
            Root<Partner> partnerRoot = criteria.from(.class);
            criteria.multiselect(personRoot, partnerRoot);
            Predicate personRestriction = builder.and(builder.equal(personRoot.get(Person_.address), address), builder.isNotEmpty(personRoot.get(Person_.phones)));
            Predicate partnerRestriction = builder.and(builder.like(partnerRoot.get(Partner_.name), prefix), builder.equal(partnerRoot.get(Partner_.version), 0));
            criteria.where(builder.and(personRestriction, partnerRestriction));
            List<Tuple> tuples = entityManager.createQuery(criteria).getResultList();
            // end::criteria-from-multiple-root-example[]
            assertEquals(2, tuples.size());
        });
    }

    @Test
    public void test_criteria_from_join_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::criteria-from-join-example[]
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Phone> criteria = builder.createQuery(.class);
            Root<Phone> root = criteria.from(.class);
            // Phone.person is a @ManyToOne
            Join<Phone, Person> personJoin = root.join(Phone_.person);
            // Person.addresses is an @ElementCollection
            Join<Person, String> addressesJoin = personJoin.join(Person_.addresses);
            criteria.where(builder.isNotEmpty(root.get(Phone_.calls)));
            List<Phone> phones = entityManager.createQuery(criteria).getResultList();
            // end::criteria-from-join-example[]
            assertEquals(2, phones.size());
        });
    }

    @Test
    public void test_criteria_from_fetch_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::criteria-from-fetch-example[]
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Phone> criteria = builder.createQuery(.class);
            Root<Phone> root = criteria.from(.class);
            // Phone.person is a @ManyToOne
            Fetch<Phone, Person> personFetch = root.fetch(Phone_.person);
            // Person.addresses is an @ElementCollection
            Fetch<Person, String> addressesJoin = personFetch.fetch(Person_.addresses);
            criteria.where(builder.isNotEmpty(root.get(Phone_.calls)));
            List<Phone> phones = entityManager.createQuery(criteria).getResultList();
            // end::criteria-from-fetch-example[]
            assertEquals(2, phones.size());
        });
    }

    @Test
    public void test_criteria_param_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::criteria-param-example[]
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Person> criteria = builder.createQuery(.class);
            Root<Person> root = criteria.from(.class);
            ParameterExpression<String> nickNameParameter = builder.parameter(.class);
            criteria.where(builder.equal(root.get(Person_.nickName), nickNameParameter));
            TypedQuery<Person> query = entityManager.createQuery(criteria);
            query.setParameter(nickNameParameter, "JD");
            List<Person> persons = query.getResultList();
            // end::criteria-param-example[]
            assertEquals(1, persons.size());
        });
    }

    @Test
    public void test_criteria_group_by_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::criteria-group-by-example[]
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Tuple> criteria = builder.createQuery(.class);
            Root<Person> root = criteria.from(.class);
            criteria.groupBy(root.get("address"));
            criteria.multiselect(root.get("address"), builder.count(root));
            List<Tuple> tuples = entityManager.createQuery(criteria).getResultList();
            for (Tuple tuple : tuples) {
                String name = ((String) (tuple.get(0)));
                Long count = ((Long) (tuple.get(1)));
            }
            // end::criteria-group-by-example[]
            assertEquals(2, tuples.size());
        });
    }

    @Test
    public void testLegacyCriteriaJpavsHibernateEntityName() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.criteria.Event event1 = new org.hibernate.userguide.criteria.Event();
            event1.id = 1L;
            event1.name = "E1";
            entityManager.persist(event1);
            org.hibernate.userguide.criteria.Event event2 = new org.hibernate.userguide.criteria.Event();
            event2.id = 2L;
            event2.name = "E2";
            entityManager.persist(event2);
            List<String> eventNames = entityManager.unwrap(.class).createQuery("select ae.name from ApplicationEvent ae").list();
            try {
                List<org.hibernate.userguide.criteria.Event> events = entityManager.unwrap(.class).createCriteria("ApplicationEvent").list();
            } catch ( expected) {
                assertEquals("Unknown entity: ApplicationEvent", expected.getMessage());
            }
            List<org.hibernate.userguide.criteria.Event> events = entityManager.unwrap(.class).createCriteria(.class.getName()).list();
        });
    }

    @Entity(name = "ApplicationEvent")
    public static class Event {
        @Id
        private Long id;

        private String name;
    }
}

