/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.sql;


import javax.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.loader.custom.NonUniqueDiscoveredSqlAliasException;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.transform.Transformers;
import org.hibernate.userguide.model.Call;
import org.hibernate.userguide.model.CreditCardPayment;
import org.hibernate.userguide.model.Person;
import org.hibernate.userguide.model.PersonNames;
import org.hibernate.userguide.model.PersonPhoneCount;
import org.hibernate.userguide.model.Phone;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class SQLTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test_sql_jpa_all_columns_scalar_query_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::sql-jpa-all-columns-scalar-query-example[]
            List<Object[]> persons = entityManager.createNativeQuery("SELECT * FROM Person").getResultList();
            // end::sql-jpa-all-columns-scalar-query-example[]
            assertEquals(3, persons.size());
        });
    }

    @Test
    public void test_sql_jpa_custom_column_selection_scalar_query_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::sql-jpa-custom-column-selection-scalar-query-example[]
            List<Object[]> persons = entityManager.createNativeQuery("SELECT id, name FROM Person").getResultList();
            for (Object[] person : persons) {
                Number id = ((Number) (person[0]));
                String name = ((String) (person[1]));
            }
            // end::sql-jpa-custom-column-selection-scalar-query-example[]
            assertEquals(3, persons.size());
        });
    }

    @Test
    public void test_sql_hibernate_query_scalar_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            // tag::sql-hibernate-all-columns-scalar-query-example[]
            List<Object[]> persons = session.createNativeQuery("SELECT * FROM Person").list();
            // end::sql-hibernate-all-columns-scalar-query-example[]
            assertEquals(3, persons.size());
        });
    }

    @Test
    public void test_sql_hibernate_custom_column_selection_scalar_query_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            // tag::sql-hibernate-custom-column-selection-scalar-query-example[]
            List<Object[]> persons = session.createNativeQuery("SELECT id, name FROM Person").list();
            for (Object[] person : persons) {
                Number id = ((Number) (person[0]));
                String name = ((String) (person[1]));
            }
            // end::sql-hibernate-custom-column-selection-scalar-query-example[]
            assertEquals(3, persons.size());
        });
    }

    @Test
    public void test_sql_hibernate_query_scalar_explicit_result_set_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            // tag::sql-hibernate-scalar-query-explicit-result-set-example[]
            List<Object[]> persons = session.createNativeQuery("SELECT * FROM Person").addScalar("id", LongType.INSTANCE).addScalar("name", StringType.INSTANCE).list();
            for (Object[] person : persons) {
                Long id = ((Long) (person[0]));
                String name = ((String) (person[1]));
            }
            // end::sql-hibernate-scalar-query-explicit-result-set-example[]
            assertEquals(3, persons.size());
        });
    }

    @Test
    public void test_sql_hibernate_query_scalar_partial_explicit_result_set_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            // tag::sql-hibernate-scalar-query-partial-explicit-result-set-example[]
            List<Object[]> persons = session.createNativeQuery("SELECT * FROM Person").addScalar("id", LongType.INSTANCE).addScalar("name").list();
            for (Object[] person : persons) {
                Long id = ((Long) (person[0]));
                String name = ((String) (person[1]));
            }
            // end::sql-hibernate-scalar-query-partial-explicit-result-set-example[]
            assertEquals(3, persons.size());
        });
    }

    @Test
    public void test_sql_jpa_entity_query_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::sql-jpa-entity-query-example[]
            List<Person> persons = entityManager.createNativeQuery("SELECT * FROM Person", .class).getResultList();
            // end::sql-jpa-entity-query-example[]
            assertEquals(3, persons.size());
        });
    }

    @Test
    public void test_sql_hibernate_entity_query_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            // tag::sql-hibernate-entity-query-example[]
            List<Person> persons = session.createNativeQuery("SELECT * FROM Person").addEntity(.class).list();
            // end::sql-hibernate-entity-query-example[]
            assertEquals(3, persons.size());
        });
    }

    @Test
    public void test_sql_jpa_entity_query_explicit_result_set_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::sql-jpa-entity-query-explicit-result-set-example[]
            List<Person> persons = entityManager.createNativeQuery(("SELECT id, name, nickName, address, createdOn, version " + "FROM Person"), .class).getResultList();
            // end::sql-jpa-entity-query-explicit-result-set-example[]
            assertEquals(3, persons.size());
        });
    }

    @Test
    public void test_sql_hibernate_entity_query_explicit_result_set_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            // tag::sql-hibernate-entity-query-explicit-result-set-example[]
            List<Person> persons = session.createNativeQuery(("SELECT id, name, nickName, address, createdOn, version " + "FROM Person")).addEntity(.class).list();
            // end::sql-hibernate-entity-query-explicit-result-set-example[]
            assertEquals(3, persons.size());
        });
    }

    @Test
    public void test_sql_jpa_entity_associations_query_many_to_one_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::sql-jpa-entity-associations-query-many-to-one-example[]
            List<Phone> phones = entityManager.createNativeQuery(("SELECT id, phone_number, phone_type, person_id " + "FROM Phone"), .class).getResultList();
            // end::sql-jpa-entity-associations-query-many-to-one-example[]
            assertEquals(3, phones.size());
        });
    }

    @Test
    public void test_sql_hibernate_entity_associations_query_many_to_one_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            // tag::sql-hibernate-entity-associations-query-many-to-one-example[]
            List<Phone> phones = session.createNativeQuery(("SELECT id, phone_number, phone_type, person_id " + "FROM Phone")).addEntity(.class).list();
            // end::sql-hibernate-entity-associations-query-many-to-one-example[]
            assertEquals(3, phones.size());
        });
    }

    @Test
    public void test_sql_jpa_entity_associations_query_many_to_one_join_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::sql-jpa-entity-associations-query-many-to-one-join-example[]
            List<Phone> phones = entityManager.createNativeQuery(("SELECT * " + ("FROM Phone ph " + "JOIN Person pr ON ph.person_id = pr.id")), .class).getResultList();
            for (Phone phone : phones) {
                assertNotNull(phone.getPerson().getName());
            }
            // end::sql-jpa-entity-associations-query-many-to-one-join-example[]
            assertEquals(3, phones.size());
        });
    }

    @Test
    public void test_sql_hibernate_entity_associations_query_many_to_one_join_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            // tag::sql-hibernate-entity-associations-query-many-to-one-join-example[]
            List<Object[]> tuples = session.createNativeQuery(("SELECT * " + ("FROM Phone ph " + "JOIN Person pr ON ph.person_id = pr.id"))).addEntity("phone", .class).addJoin("pr", "phone.person").list();
            for (Object[] tuple : tuples) {
                Phone phone = ((Phone) (tuple[0]));
                Person person = ((Person) (tuple[1]));
                assertNotNull(person.getName());
            }
            // end::sql-hibernate-entity-associations-query-many-to-one-join-example[]
            assertEquals(3, tuples.size());
        });
    }

    @Test
    public void test_sql_hibernate_entity_associations_query_many_to_one_join_result_transformer_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            // tag::sql-hibernate-entity-associations-query-many-to-one-join-result-transformer-example[]
            List<Person> persons = session.createNativeQuery(("SELECT * " + ("FROM Phone ph " + "JOIN Person pr ON ph.person_id = pr.id"))).addEntity("phone", .class).addJoin("pr", "phone.person").setResultTransformer(Criteria.ROOT_ENTITY).list();
            for (Person person : persons) {
                person.getPhones();
            }
            // end::sql-hibernate-entity-associations-query-many-to-one-join-result-transformer-example[]
            assertEquals(3, persons.size());
        });
    }

    @Test
    @RequiresDialect(H2Dialect.class)
    @RequiresDialect(Oracle8iDialect.class)
    @RequiresDialect(PostgreSQL82Dialect.class)
    public void test_sql_jpa_entity_associations_query_one_to_many_join_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::sql-jpa-entity-associations-query-one-to-many-join-example[]
            List<Phone> phones = entityManager.createNativeQuery(("SELECT * " + ("FROM Phone ph " + "JOIN phone_call c ON c.phone_id = ph.id")), .class).getResultList();
            for (Phone phone : phones) {
                List<Call> calls = phone.getCalls();
            }
            // end::sql-jpa-entity-associations-query-one-to-many-join-example[]
            assertEquals(2, phones.size());
        });
    }

    @Test
    public void test_sql_hibernate_entity_associations_query_one_to_many_join_example_1() {
        try {
            doInJPA(this::entityManagerFactory, ( entityManager) -> {
                Session session = entityManager.unwrap(.class);
                List<Phone> phones = session.createNativeQuery(("SELECT * " + ("FROM Phone ph " + "JOIN phone_call c ON c.phone_id = ph.id"))).addEntity("phone", .class).addJoin("c", "phone.calls").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
                for (Phone phone : phones) {
                    List<Call> calls = phone.getCalls();
                }
                assertEquals(2, phones.size());
            });
        } catch (Exception e) {
            log.error("HHH-10504", e);
            // See issue https://hibernate.atlassian.net/browse/HHH-10504
        }
    }

    @Test
    @RequiresDialect(H2Dialect.class)
    @RequiresDialect(Oracle8iDialect.class)
    @RequiresDialect(PostgreSQL82Dialect.class)
    public void test_sql_hibernate_entity_associations_query_one_to_many_join_example_2() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            // tag::sql-hibernate-entity-associations-query-one-to-many-join-example[]
            List<Object[]> tuples = session.createNativeQuery(("SELECT * " + ("FROM Phone ph " + "JOIN phone_call c ON c.phone_id = ph.id"))).addEntity("phone", .class).addJoin("c", "phone.calls").list();
            for (Object[] tuple : tuples) {
                Phone phone = ((Phone) (tuple[0]));
                Call call = ((Call) (tuple[1]));
            }
            // end::sql-hibernate-entity-associations-query-one-to-many-join-example[]
            assertEquals(2, tuples.size());
        });
    }

    @Test
    public void test_sql_jpa_multi_entity_query_example() {
        try {
            doInJPA(this::entityManagerFactory, ( entityManager) -> {
                // tag::sql-jpa-multi-entity-query-example[]
                List<Object> entities = entityManager.createNativeQuery(("SELECT * " + ("FROM Person pr, Partner pt " + "WHERE pr.name = pt.name"))).getResultList();
                // end::sql-jpa-multi-entity-query-example[]
                assertEquals(2, entities.size());
            });
            Assert.fail("Should throw NonUniqueDiscoveredSqlAliasException!");
        } catch (PersistenceException expected) {
            Assert.assertEquals(NonUniqueDiscoveredSqlAliasException.class, expected.getCause().getClass());
        }
    }

    @Test
    public void test_sql_hibernate_multi_entity_query_example() {
        try {
            doInJPA(this::entityManagerFactory, ( entityManager) -> {
                Session session = entityManager.unwrap(.class);
                // tag::sql-hibernate-multi-entity-query-example[]
                List<Object> entities = session.createNativeQuery(("SELECT * " + ("FROM Person pr, Partner pt " + "WHERE pr.name = pt.name"))).list();
                // end::sql-hibernate-multi-entity-query-example[]
                assertEquals(2, entities.size());
            });
            Assert.fail("Should throw NonUniqueDiscoveredSqlAliasException!");
        } catch (NonUniqueDiscoveredSqlAliasException e) {
            // expected
        } catch (PersistenceException e) {
            assertTyping(NonUniqueDiscoveredSqlAliasException.class, e.getCause());
        }
    }

    @Test
    public void test_sql_hibernate_multi_entity_query_alias_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            // tag::sql-hibernate-multi-entity-query-alias-example[]
            List<Object> entities = session.createNativeQuery(("SELECT {pr.*}, {pt.*} " + ("FROM Person pr, Partner pt " + "WHERE pr.name = pt.name"))).addEntity("pr", .class).addEntity("pt", .class).list();
            // end::sql-hibernate-multi-entity-query-alias-example[]
            assertEquals(1, entities.size());
        });
    }

    @Test
    public void test_sql_hibernate_dto_query_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            // tag::sql-hibernate-dto-query-example[]
            List<PersonSummaryDTO> dtos = session.createNativeQuery(("SELECT p.id as \"id\", p.name as \"name\" " + "FROM Person p")).setResultTransformer(Transformers.aliasToBean(.class)).list();
            // end::sql-hibernate-dto-query-example[]
            assertEquals(3, dtos.size());
        });
    }

    @Test
    public void test_sql_hibernate_inheritance_query_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            // tag::sql-hibernate-inheritance-query-example[]
            List<CreditCardPayment> payments = session.createNativeQuery(("SELECT * " + ("FROM Payment p " + "JOIN CreditCardPayment cp on cp.id = p.id"))).addEntity(.class).list();
            // end::sql-hibernate-inheritance-query-example[]
            assertEquals(1, payments.size());
        });
    }

    @Test
    public void test_sql_jpa_query_parameters_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::sql-jpa-query-parameters-example[]
            List<Person> persons = entityManager.createNativeQuery(("SELECT * " + ("FROM Person " + "WHERE name like :name")), .class).setParameter("name", "J%").getResultList();
            // end::sql-jpa-query-parameters-example[]
            assertEquals(1, persons.size());
        });
    }

    @Test
    public void test_sql_hibernate_query_parameters_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            // tag::sql-hibernate-query-parameters-example[]
            List<Person> persons = session.createNativeQuery(("SELECT * " + ("FROM Person " + "WHERE name like :name"))).addEntity(.class).setParameter("name", "J%").list();
            // end::sql-hibernate-query-parameters-example[]
            assertEquals(1, persons.size());
        });
    }

    @Test
    public void test_sql_jpa_scalar_named_query_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::sql-jpa-scalar-named-query-example[]
            List<String> names = entityManager.createNamedQuery("find_person_name").getResultList();
            // end::sql-jpa-scalar-named-query-example[]
            assertEquals(3, names.size());
        });
    }

    @Test
    public void test_sql_hibernate_scalar_named_query_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            // tag::sql-hibernate-scalar-named-query-example[]
            List<String> names = session.getNamedQuery("find_person_name").list();
            // end::sql-hibernate-scalar-named-query-example[]
            assertEquals(3, names.size());
        });
    }

    @Test
    public void test_sql_jpa_multiple_scalar_values_named_query_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::sql-jpa-multiple-scalar-values-named-query-example[]
            List<Object[]> tuples = entityManager.createNamedQuery("find_person_name_and_nickName").getResultList();
            for (Object[] tuple : tuples) {
                String name = ((String) (tuple[0]));
                String nickName = ((String) (tuple[1]));
            }
            // end::sql-jpa-multiple-scalar-values-named-query-example[]
            assertEquals(3, tuples.size());
        });
    }

    @Test
    public void test_sql_hibernate_multiple_scalar_values_named_query_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            // tag::sql-hibernate-multiple-scalar-values-named-query-example[]
            List<Object[]> tuples = session.getNamedQuery("find_person_name_and_nickName").list();
            for (Object[] tuple : tuples) {
                String name = ((String) (tuple[0]));
                String nickName = ((String) (tuple[1]));
            }
            // end::sql-hibernate-multiple-scalar-values-named-query-example[]
            assertEquals(3, tuples.size());
        });
    }

    @Test
    public void test_sql_jpa_multiple_scalar_values_dto_named_query_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::sql-jpa-multiple-scalar-values-dto-named-query-example[]
            List<PersonNames> personNames = entityManager.createNamedQuery("find_person_name_and_nickName_dto").getResultList();
            // end::sql-jpa-multiple-scalar-values-dto-named-query-example[]
            assertEquals(3, personNames.size());
        });
    }

    @Test
    public void test_sql_hibernate_multiple_scalar_values_dto_named_query_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            // tag::sql-hibernate-multiple-scalar-values-dto-named-query-example[]
            List<PersonNames> personNames = session.getNamedQuery("find_person_name_and_nickName_dto").list();
            // end::sql-hibernate-multiple-scalar-values-dto-named-query-example[]
            assertEquals(3, personNames.size());
        });
    }

    @Test
    public void test_sql_hibernate_multiple_scalar_values_dto_hibernate_named_query_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            // tag::sql-hibernate-multiple-scalar-values-dto-hibernate-named-query-example[]
            List<PersonPhoneCount> personNames = session.getNamedNativeQuery("get_person_phone_count").getResultList();
            // end::sql-hibernate-multiple-scalar-values-dto-hibernate-named-query-example[]
            assertEquals(2, personNames.size());
            assertEquals(1, personNames.stream().filter(( person) -> person.getName().equals("John Doe")).map(PersonPhoneCount::getPhoneCount).findAny().get().intValue());
            assertEquals(2, personNames.stream().filter(( person) -> person.getName().equals("Mrs. John Doe")).map(PersonPhoneCount::getPhoneCount).findAny().get().intValue());
        });
    }

    @Test
    public void test_sql_jpa_entity_named_query_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::sql-jpa-entity-named-query-example[]
            List<Person> persons = entityManager.createNamedQuery("find_person_by_name").setParameter("name", "J%").getResultList();
            // end::sql-jpa-entity-named-query-example[]
            assertEquals(1, persons.size());
        });
    }

    @Test
    public void test_sql_hibernate_entity_named_query_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            // tag::sql-hibernate-entity-named-query-example[]
            List<Person> persons = session.getNamedQuery("find_person_by_name").setParameter("name", "J%").list();
            // end::sql-hibernate-entity-named-query-example[]
            assertEquals(1, persons.size());
        });
    }

    @Test
    @RequiresDialect(H2Dialect.class)
    @RequiresDialect(Oracle8iDialect.class)
    @RequiresDialect(PostgreSQL82Dialect.class)
    public void test_sql_jpa_entity_associations_named_query_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::sql-jpa-entity-associations_named-query-example[]
            List<Object[]> tuples = entityManager.createNamedQuery("find_person_with_phones_by_name").setParameter("name", "J%").getResultList();
            for (Object[] tuple : tuples) {
                Person person = ((Person) (tuple[0]));
                Phone phone = ((Phone) (tuple[1]));
            }
            // end::sql-jpa-entity-associations_named-query-example[]
            assertEquals(1, tuples.size());
        });
    }

    @Test
    @RequiresDialect(H2Dialect.class)
    @RequiresDialect(Oracle8iDialect.class)
    @RequiresDialect(PostgreSQL82Dialect.class)
    public void test_sql_hibernate_entity_associations_named_query_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            // tag::sql-hibernate-entity-associations_named-query-example[]
            List<Object[]> tuples = session.getNamedQuery("find_person_with_phones_by_name").setParameter("name", "J%").list();
            for (Object[] tuple : tuples) {
                Person person = ((Person) (tuple[0]));
                Phone phone = ((Phone) (tuple[1]));
            }
            // end::sql-hibernate-entity-associations_named-query-example[]
            assertEquals(1, tuples.size());
        });
    }

    @Test
    public void test_sql_jpa_composite_key_entity_associations_named_query_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::sql-jpa-composite-key-entity-associations_named-query-example[]
            List<Object[]> tuples = entityManager.createNamedQuery("find_all_spaceships").getResultList();
            for (Object[] tuple : tuples) {
                SpaceShip spaceShip = ((SpaceShip) (tuple[0]));
                Number surface = ((Number) (tuple[1]));
                Number volume = ((Number) (tuple[2]));
            }
            // end::sql-jpa-composite-key-entity-associations_named-query-example[]
            assertEquals(1, tuples.size());
        });
    }

    @Test
    public void test_sql_hibernate_composite_key_entity_associations_named_query_example() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            // tag::sql-hibernate-composite-key-entity-associations_named-query-example[]
            List<Object[]> tuples = session.getNamedQuery("find_all_spaceships").list();
            for (Object[] tuple : tuples) {
                SpaceShip spaceShip = ((SpaceShip) (tuple[0]));
                Number surface = ((Number) (tuple[1]));
                Number volume = ((Number) (tuple[2]));
            }
            // end::sql-hibernate-composite-key-entity-associations_named-query-example[]
            assertEquals(1, tuples.size());
        });
    }
}

