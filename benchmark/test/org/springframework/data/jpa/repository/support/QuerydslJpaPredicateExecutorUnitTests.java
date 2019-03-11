/**
 * Copyright 2008-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.jpa.repository.support;


import Direction.ASC;
import Direction.DESC;
import QUser.user.address.streetName;
import QUser.user.dateOfBirth;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.sample.Address;
import org.springframework.data.jpa.domain.sample.QUser;
import org.springframework.data.jpa.domain.sample.Role;
import org.springframework.data.jpa.domain.sample.User;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


/**
 * Integration test for {@link QuerydslJpaPredicateExecutor}.
 *
 * @author Jens Schauder
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Mark Paluch
 * @author Christoph Strobl
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:infrastructure.xml" })
@Transactional
public class QuerydslJpaPredicateExecutorUnitTests {
    @PersistenceContext
    EntityManager em;

    QuerydslJpaPredicateExecutor<User> predicateExecutor;

    QUser user = new QUser("user");

    User dave;

    User carter;

    User oliver;

    Role adminRole;

    @Test
    public void executesPredicatesCorrectly() throws Exception {
        BooleanExpression isCalledDave = user.firstname.eq("Dave");
        BooleanExpression isBeauford = user.lastname.eq("Beauford");
        List<User> result = predicateExecutor.findAll(isCalledDave.or(isBeauford));
        assertThat(result).containsExactlyInAnyOrder(carter, dave);
    }

    @Test
    public void executesStringBasedPredicatesCorrectly() throws Exception {
        PathBuilder<User> builder = new PathBuilderFactory().create(User.class);
        BooleanExpression isCalledDave = builder.getString("firstname").eq("Dave");
        BooleanExpression isBeauford = builder.getString("lastname").eq("Beauford");
        List<User> result = predicateExecutor.findAll(isCalledDave.or(isBeauford));
        assertThat(result).containsExactlyInAnyOrder(carter, dave);
    }

    // DATAJPA-243
    @Test
    public void considersSortingProvidedThroughPageable() {
        Predicate lastnameContainsE = user.lastname.contains("e");
        Page<User> result = predicateExecutor.findAll(lastnameContainsE, PageRequest.of(0, 1, ASC, "lastname"));
        assertThat(result).containsExactly(carter);
        result = predicateExecutor.findAll(lastnameContainsE, PageRequest.of(0, 2, DESC, "lastname"));
        assertThat(result).containsExactly(oliver, dave);
    }

    // DATAJPA-296
    @Test
    public void appliesIgnoreCaseOrdering() {
        Sort sort = Sort.by(ignoreCase(), new org.springframework.data.domain.Sort.Order(Direction.ASC, "firstname"));
        Page<User> result = predicateExecutor.findAll(user.lastname.contains("e"), PageRequest.of(0, 2, sort));
        assertThat(result.getContent()).containsExactly(dave, oliver);
    }

    // DATAJPA-427
    @Test
    public void findBySpecificationWithSortByPluralAssociationPropertyInPageableShouldUseSortNullValuesLast() {
        oliver.getColleagues().add(dave);
        dave.getColleagues().add(oliver);
        QUser user = QUser.user;
        Page<User> page = predicateExecutor.findAll(user.firstname.isNotNull(), PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "colleagues.firstname")));
        assertThat(page.getContent()).hasSize(3).contains(oliver, dave, carter);
    }

    // DATAJPA-427
    @Test
    public void findBySpecificationWithSortBySingularAssociationPropertyInPageableShouldUseSortNullValuesLast() {
        oliver.setManager(dave);
        dave.setManager(carter);
        QUser user = QUser.user;
        Page<User> page = predicateExecutor.findAll(user.firstname.isNotNull(), PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "manager.firstname")));
        assertThat(page.getContent()).hasSize(3).contains(dave, oliver, carter);
    }

    // DATAJPA-427
    @Test
    public void findBySpecificationWithSortBySingularPropertyInPageableShouldUseSortNullValuesFirst() {
        QUser user = QUser.user;
        Page<User> page = predicateExecutor.findAll(user.firstname.isNotNull(), PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "firstname")));
        assertThat(page.getContent()).containsExactly(carter, dave, oliver);
    }

    // DATAJPA-427
    @Test
    public void findBySpecificationWithSortByOrderIgnoreCaseBySingularPropertyInPageableShouldUseSortNullValuesFirst() {
        QUser user = QUser.user;
        Page<User> page = predicateExecutor.findAll(user.firstname.isNotNull(), PageRequest.of(0, 10, Sort.by(ignoreCase())));
        assertThat(page.getContent()).containsExactly(carter, dave, oliver);
    }

    // DATAJPA-427
    @Test
    public void findBySpecificationWithSortByNestedEmbeddedPropertyInPageableShouldUseSortNullValuesFirst() {
        oliver.setAddress(new Address("Germany", "Saarbr?cken", "HaveItYourWay", "123"));
        QUser user = QUser.user;
        Page<User> page = predicateExecutor.findAll(user.firstname.isNotNull(), PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "address.streetName")));
        assertThat(page.getContent()).containsExactly(dave, carter, oliver);
    }

    // DATAJPA-12
    @Test
    public void findBySpecificationWithSortByQueryDslOrderSpecifierWithQPageRequestAndQSort() {
        QUser user = QUser.user;
        Page<User> page = predicateExecutor.findAll(user.firstname.isNotNull(), new QPageRequest(0, 10, new org.springframework.data.querydsl.QSort(user.firstname.asc())));
        assertThat(page.getContent()).containsExactly(carter, dave, oliver);
    }

    // DATAJPA-12
    @Test
    public void findBySpecificationWithSortByQueryDslOrderSpecifierWithQPageRequest() {
        QUser user = QUser.user;
        Page<User> page = predicateExecutor.findAll(user.firstname.isNotNull(), new QPageRequest(0, 10, user.firstname.asc()));
        assertThat(page.getContent()).containsExactly(carter, dave, oliver);
    }

    // DATAJPA-12
    @Test
    public void findBySpecificationWithSortByQueryDslOrderSpecifierForAssociationShouldGenerateLeftJoinWithQPageRequest() {
        oliver.setManager(dave);
        dave.setManager(carter);
        QUser user = QUser.user;
        Page<User> page = predicateExecutor.findAll(user.firstname.isNotNull(), new QPageRequest(0, 10, user.manager.firstname.asc()));
        assertThat(page.getContent()).containsExactly(carter, dave, oliver);
    }

    // DATAJPA-500, DATAJPA-635
    @Test
    public void sortByNestedEmbeddedAttribute() {
        carter.setAddress(new Address("U", "Z", "Y", "41"));
        dave.setAddress(new Address("U", "A", "Y", "41"));
        oliver.setAddress(new Address("G", "D", "X", "42"));
        List<User> users = predicateExecutor.findAll(streetName.asc());
        assertThat(users).hasSize(3).contains(dave, oliver, carter);
    }

    // DATAJPA-566, DATAJPA-635
    @Test
    public void shouldSupportSortByOperatorWithDateExpressions() {
        carter.setDateOfBirth(new LocalDate(2000, 2, 1).toDate());
        dave.setDateOfBirth(new LocalDate(2000, 1, 1).toDate());
        oliver.setDateOfBirth(new LocalDate(2003, 5, 1).toDate());
        List<User> users = predicateExecutor.findAll(dateOfBirth.yearMonth().asc());
        assertThat(users).containsExactly(dave, carter, oliver);
    }

    // DATAJPA-665
    @Test
    public void shouldSupportExistsWithPredicate() throws Exception {
        assertThat(predicateExecutor.exists(user.firstname.eq("Dave"))).isEqualTo(true);
        assertThat(predicateExecutor.exists(user.firstname.eq("Unknown"))).isEqualTo(false);
        assertThat(predicateExecutor.exists(((Predicate) (null)))).isEqualTo(true);
    }

    // DATAJPA-679
    @Test
    public void shouldSupportFindAllWithPredicateAndSort() {
        List<User> users = predicateExecutor.findAll(user.dateOfBirth.isNull(), Sort.by(ASC, "firstname"));
        assertThat(users).contains(carter, dave, oliver);
    }

    // DATAJPA-585
    @Test
    public void worksWithUnpagedPageable() {
        assertThat(predicateExecutor.findAll(user.dateOfBirth.isNull(), Pageable.unpaged()).getContent()).hasSize(3);
    }

    // DATAJPA-912
    @Test
    public void pageableQueryReportsTotalFromResult() {
        Page<User> firstPage = predicateExecutor.findAll(user.dateOfBirth.isNull(), PageRequest.of(0, 10));
        assertThat(firstPage.getContent()).hasSize(3);
        assertThat(firstPage.getTotalElements()).isEqualTo(3L);
        Page<User> secondPage = predicateExecutor.findAll(user.dateOfBirth.isNull(), PageRequest.of(1, 2));
        assertThat(secondPage.getContent()).hasSize(1);
        assertThat(secondPage.getTotalElements()).isEqualTo(3L);
    }

    // DATAJPA-912
    @Test
    public void pageableQueryReportsTotalFromCount() {
        Page<User> firstPage = predicateExecutor.findAll(user.dateOfBirth.isNull(), PageRequest.of(0, 3));
        assertThat(firstPage.getContent()).hasSize(3);
        assertThat(firstPage.getTotalElements()).isEqualTo(3L);
        Page<User> secondPage = predicateExecutor.findAll(user.dateOfBirth.isNull(), PageRequest.of(10, 10));
        assertThat(secondPage.getContent()).hasSize(0);
        assertThat(secondPage.getTotalElements()).isEqualTo(3L);
    }

    // DATAJPA-1115
    @Test
    public void findOneWithPredicateReturnsResultCorrectly() {
        assertThat(predicateExecutor.findOne(user.eq(dave))).contains(dave);
    }

    // DATAJPA-1115
    @Test
    public void findOneWithPredicateReturnsOptionalEmptyWhenNoDataFound() {
        assertThat(predicateExecutor.findOne(user.firstname.eq("batman"))).isNotPresent();
    }

    // DATAJPA-1115
    @Test(expected = IncorrectResultSizeDataAccessException.class)
    public void findOneWithPredicateThrowsExceptionForNonUniqueResults() {
        predicateExecutor.findOne(user.emailAddress.contains("com"));
    }
}

