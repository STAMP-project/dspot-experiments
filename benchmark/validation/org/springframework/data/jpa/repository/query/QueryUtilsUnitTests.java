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
package org.springframework.data.jpa.repository.query;


import java.util.Set;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.JpaSort;


/**
 * Unit test for {@link QueryUtils}.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Komi Innocent
 * @author Christoph Strobl
 * @author Jens Schauder
 */
public class QueryUtilsUnitTests {
    static final String QUERY = "select u from User u";

    static final String FQ_QUERY = "select u from org.acme.domain.User$Foo_Bar u";

    static final String SIMPLE_QUERY = "from User u";

    static final String COUNT_QUERY = "select count(u) from User u";

    static final String QUERY_WITH_AS = "select u from User as u where u.username = ?";

    static final Matcher<String> IS_U = is("u");

    @Test
    public void createsCountQueryCorrectly() throws Exception {
        QueryUtilsUnitTests.assertCountQuery(QueryUtilsUnitTests.QUERY, QueryUtilsUnitTests.COUNT_QUERY);
    }

    @Test
    public void createsCountQueriesCorrectlyForCapitalLetterJPQL() {
        QueryUtilsUnitTests.assertCountQuery("FROM User u WHERE u.foo.bar = ?", "select count(u) FROM User u WHERE u.foo.bar = ?");
        QueryUtilsUnitTests.assertCountQuery("SELECT u FROM User u where u.foo.bar = ?", "select count(u) FROM User u where u.foo.bar = ?");
    }

    @Test
    public void createsCountQueryForDistinctQueries() throws Exception {
        QueryUtilsUnitTests.assertCountQuery("select distinct u from User u where u.foo = ?", "select count(distinct u) from User u where u.foo = ?");
    }

    @Test
    public void createsCountQueryForConstructorQueries() throws Exception {
        QueryUtilsUnitTests.assertCountQuery("select distinct new User(u.name) from User u where u.foo = ?", "select count(distinct u) from User u where u.foo = ?");
    }

    @Test
    public void createsCountQueryForJoins() throws Exception {
        QueryUtilsUnitTests.assertCountQuery("select distinct new User(u.name) from User u left outer join u.roles r WHERE r = ?", "select count(distinct u) from User u left outer join u.roles r WHERE r = ?");
    }

    @Test
    public void createsCountQueryForQueriesWithSubSelects() throws Exception {
        QueryUtilsUnitTests.assertCountQuery("select u from User u left outer join u.roles r where r in (select r from Role)", "select count(u) from User u left outer join u.roles r where r in (select r from Role)");
    }

    @Test
    public void createsCountQueryForAliasesCorrectly() throws Exception {
        QueryUtilsUnitTests.assertCountQuery("select u from User as u", "select count(u) from User as u");
    }

    @Test
    public void allowsShortJpaSyntax() throws Exception {
        QueryUtilsUnitTests.assertCountQuery(QueryUtilsUnitTests.SIMPLE_QUERY, QueryUtilsUnitTests.COUNT_QUERY);
    }

    @Test
    public void detectsAliasCorrectly() throws Exception {
        Assert.assertThat(detectAlias(QueryUtilsUnitTests.QUERY), QueryUtilsUnitTests.IS_U);
        Assert.assertThat(detectAlias(QueryUtilsUnitTests.SIMPLE_QUERY), QueryUtilsUnitTests.IS_U);
        Assert.assertThat(detectAlias(QueryUtilsUnitTests.COUNT_QUERY), QueryUtilsUnitTests.IS_U);
        Assert.assertThat(detectAlias(QueryUtilsUnitTests.QUERY_WITH_AS), QueryUtilsUnitTests.IS_U);
        Assert.assertThat(detectAlias("SELECT FROM USER U"), is("U"));
        Assert.assertThat(detectAlias("select u from  User u"), QueryUtilsUnitTests.IS_U);
        Assert.assertThat(detectAlias("select u from  com.acme.User u"), QueryUtilsUnitTests.IS_U);
        Assert.assertThat(detectAlias("select u from T05User u"), QueryUtilsUnitTests.IS_U);
    }

    @Test
    public void allowsFullyQualifiedEntityNamesInQuery() {
        Assert.assertThat(detectAlias(QueryUtilsUnitTests.FQ_QUERY), QueryUtilsUnitTests.IS_U);
        QueryUtilsUnitTests.assertCountQuery(QueryUtilsUnitTests.FQ_QUERY, "select count(u) from org.acme.domain.User$Foo_Bar u");
    }

    // DATAJPA-252
    @Test
    public void detectsJoinAliasesCorrectly() {
        Set<String> aliases = getOuterJoinAliases("select p from Person p left outer join x.foo b2_$ar where ?");
        Assert.assertThat(aliases, hasSize(1));
        Assert.assertThat(aliases, hasItems("b2_$ar"));
        aliases = getOuterJoinAliases("select p from Person p left join x.foo b2_$ar where ?");
        Assert.assertThat(aliases, hasSize(1));
        Assert.assertThat(aliases, hasItems("b2_$ar"));
        aliases = getOuterJoinAliases("select p from Person p left outer join x.foo as b2_$ar, left join x.bar as foo where ?");
        Assert.assertThat(aliases, hasSize(2));
        Assert.assertThat(aliases, hasItems("b2_$ar", "foo"));
        aliases = getOuterJoinAliases("select p from Person p left join x.foo as b2_$ar, left outer join x.bar foo where ?");
        Assert.assertThat(aliases, hasSize(2));
        Assert.assertThat(aliases, hasItems("b2_$ar", "foo"));
    }

    // DATAJPA-252
    @Test
    public void doesNotPrefixOrderReferenceIfOuterJoinAliasDetected() {
        String query = "select p from Person p left join p.address address";
        Assert.assertThat(applySorting(query, Sort.by("address.city")), endsWith("order by address.city asc"));
        Assert.assertThat(applySorting(query, Sort.by("address.city", "lastname"), "p"), endsWith("order by address.city asc, p.lastname asc"));
    }

    // DATAJPA-252
    @Test
    public void extendsExistingOrderByClausesCorrectly() {
        String query = "select p from Person p order by p.lastname asc";
        Assert.assertThat(applySorting(query, Sort.by("firstname"), "p"), endsWith("order by p.lastname asc, p.firstname asc"));
    }

    // DATAJPA-296
    @Test
    public void appliesIgnoreCaseOrderingCorrectly() {
        Sort sort = Sort.by(Order.by("firstname").ignoreCase());
        String query = "select p from Person p";
        Assert.assertThat(applySorting(query, sort, "p"), endsWith("order by lower(p.firstname) asc"));
    }

    // DATAJPA-296
    @Test
    public void appendsIgnoreCaseOrderingCorrectly() {
        Sort sort = Sort.by(Order.by("firstname").ignoreCase());
        String query = "select p from Person p order by p.lastname asc";
        Assert.assertThat(applySorting(query, sort, "p"), endsWith("order by p.lastname asc, lower(p.firstname) asc"));
    }

    // DATAJPA-342
    @Test
    public void usesReturnedVariableInCOuntProjectionIfSet() {
        QueryUtilsUnitTests.assertCountQuery("select distinct m.genre from Media m where m.user = ?1 order by m.genre asc", "select count(distinct m.genre) from Media m where m.user = ?1");
    }

    // DATAJPA-343
    @Test
    public void projectsCOuntQueriesForQueriesWithSubselects() {
        QueryUtilsUnitTests.assertCountQuery("select o from Foo o where cb.id in (select b from Bar b)", "select count(o) from Foo o where cb.id in (select b from Bar b)");
    }

    // DATAJPA-148
    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void doesNotPrefixSortsIfFunction() {
        Sort sort = Sort.by("sum(foo)");
        Assert.assertThat(applySorting("select p from Person p", sort, "p"), endsWith("order by sum(foo) asc"));
    }

    // DATAJPA-377
    @Test
    public void removesOrderByInGeneratedCountQueryFromOriginalQueryIfPresent() {
        QueryUtilsUnitTests.assertCountQuery("select distinct m.genre from Media m where m.user = ?1 OrDer  By   m.genre ASC", "select count(distinct m.genre) from Media m where m.user = ?1");
    }

    // DATAJPA-375
    @Test
    public void findsExistingOrderByIndependentOfCase() {
        Sort sort = Sort.by("lastname");
        String query = applySorting("select p from Person p ORDER BY p.firstname", sort, "p");
        Assert.assertThat(query, endsWith("ORDER BY p.firstname, p.lastname asc"));
    }

    // DATAJPA-409
    @Test
    public void createsCountQueryForNestedReferenceCorrectly() {
        QueryUtilsUnitTests.assertCountQuery("select a.b from A a", "select count(a.b) from A a");
    }

    // DATAJPA-420
    @Test
    public void createsCountQueryForScalarSelects() {
        QueryUtilsUnitTests.assertCountQuery("select p.lastname,p.firstname from Person p", "select count(p) from Person p");
    }

    // DATAJPA-456
    @Test
    public void createCountQueryFromTheGivenCountProjection() {
        Assert.assertThat(createCountQueryFor("select p.lastname,p.firstname from Person p", "p.lastname"), is("select count(p.lastname) from Person p"));
    }

    // DATAJPA-726
    @Test
    public void detectsAliassesInPlainJoins() {
        String query = "select p from Customer c join c.productOrder p where p.delayed = true";
        Sort sort = Sort.by("p.lineItems");
        Assert.assertThat(applySorting(query, sort, "c"), endsWith("order by p.lineItems asc"));
    }

    // DATAJPA-736
    @Test
    public void supportsNonAsciiCharactersInEntityNames() {
        Assert.assertThat(createCountQueryFor("select u from Us?r u"), is("select count(u) from Us?r u"));
    }

    // DATAJPA-798
    @Test
    public void detectsAliasInQueryContainingLineBreaks() {
        Assert.assertThat(detectAlias("select \n u \n from \n User \nu"), is("u"));
    }

    // DATAJPA-815
    @Test
    public void doesPrefixPropertyWith() {
        String query = "from Cat c join Dog d";
        Sort sort = Sort.by("dPropertyStartingWithJoinAlias");
        Assert.assertThat(applySorting(query, sort, "c"), endsWith("order by c.dPropertyStartingWithJoinAlias asc"));
    }

    // DATAJPA-938
    @Test
    public void detectsConstructorExpressionInDistinctQuery() {
        Assert.assertThat(hasConstructorExpression("select distinct new Foo() from Bar b"), is(true));
    }

    // DATAJPA-938
    @Test
    public void detectsComplexConstructorExpression() {
        Assert.assertThat(hasConstructorExpression(("select new foo.bar.Foo(ip.id, ip.name, sum(lp.amount)) "// 
         + ((("from Bar lp join lp.investmentProduct ip "// 
         + "where (lp.toDate is null and lp.fromDate <= :now and lp.fromDate is not null) and lp.accountId = :accountId ")// 
         + "group by ip.id, ip.name, lp.accountId ")// 
         + "order by ip.name ASC"))), is(true));
    }

    // DATAJPA-938
    @Test
    public void detectsConstructorExpressionWithLineBreaks() {
        Assert.assertThat(hasConstructorExpression("select new foo.bar.FooBar(\na.id) from DtoA a "), is(true));
    }

    // DATAJPA-960
    @Test
    public void doesNotQualifySortIfNoAliasDetected() {
        Assert.assertThat(applySorting("from mytable where ?1 is null", Sort.by("firstname")), endsWith("order by firstname asc"));
    }

    // DATAJPA-965, DATAJPA-970
    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void doesNotAllowWhitespaceInSort() {
        Sort sort = Sort.by("case when foo then bar");
        applySorting("select p from Person p", sort, "p");
    }

    // DATAJPA-965, DATAJPA-970
    @Test
    public void doesNotPrefixUnsageJpaSortFunctionCalls() {
        JpaSort sort = JpaSort.unsafe("sum(foo)");
        Assert.assertThat(applySorting("select p from Person p", sort, "p"), endsWith("order by sum(foo) asc"));
    }

    // DATAJPA-965, DATAJPA-970
    @Test
    public void doesNotPrefixMultipleAliasedFunctionCalls() {
        String query = "SELECT AVG(m.price) AS avgPrice, SUM(m.stocks) AS sumStocks FROM Magazine m";
        Sort sort = Sort.by("avgPrice", "sumStocks");
        Assert.assertThat(applySorting(query, sort, "m"), endsWith("order by avgPrice asc, sumStocks asc"));
    }

    // DATAJPA-965, DATAJPA-970
    @Test
    public void doesNotPrefixSingleAliasedFunctionCalls() {
        String query = "SELECT AVG(m.price) AS avgPrice FROM Magazine m";
        Sort sort = Sort.by("avgPrice");
        Assert.assertThat(applySorting(query, sort, "m"), endsWith("order by avgPrice asc"));
    }

    // DATAJPA-965, DATAJPA-970
    @Test
    public void prefixesSingleNonAliasedFunctionCallRelatedSortProperty() {
        String query = "SELECT AVG(m.price) AS avgPrice FROM Magazine m";
        Sort sort = Sort.by("someOtherProperty");
        Assert.assertThat(applySorting(query, sort, "m"), endsWith("order by m.someOtherProperty asc"));
    }

    // DATAJPA-965, DATAJPA-970
    @Test
    public void prefixesNonAliasedFunctionCallRelatedSortPropertyWhenSelectClauseContainesAliasedFunctionForDifferentProperty() {
        String query = "SELECT m.name, AVG(m.price) AS avgPrice FROM Magazine m";
        Sort sort = Sort.by("name", "avgPrice");
        Assert.assertThat(applySorting(query, sort, "m"), endsWith("order by m.name asc, avgPrice asc"));
    }

    // DATAJPA-965, DATAJPA-970
    @Test
    public void doesNotPrefixAliasedFunctionCallNameWithMultipleNumericParameters() {
        String query = "SELECT SUBSTRING(m.name, 2, 5) AS trimmedName FROM Magazine m";
        Sort sort = Sort.by("trimmedName");
        Assert.assertThat(applySorting(query, sort, "m"), endsWith("order by trimmedName asc"));
    }

    // DATAJPA-965, DATAJPA-970
    @Test
    public void doesNotPrefixAliasedFunctionCallNameWithMultipleStringParameters() {
        String query = "SELECT CONCAT(m.name, 'foo') AS extendedName FROM Magazine m";
        Sort sort = Sort.by("extendedName");
        Assert.assertThat(applySorting(query, sort, "m"), endsWith("order by extendedName asc"));
    }

    // DATAJPA-965, DATAJPA-970
    @Test
    public void doesNotPrefixAliasedFunctionCallNameWithUnderscores() {
        String query = "SELECT AVG(m.price) AS avg_price FROM Magazine m";
        Sort sort = Sort.by("avg_price");
        Assert.assertThat(applySorting(query, sort, "m"), endsWith("order by avg_price asc"));
    }

    // DATAJPA-965, DATAJPA-970
    @Test
    public void doesNotPrefixAliasedFunctionCallNameWithDots() {
        String query = "SELECT AVG(m.price) AS m.avg FROM Magazine m";
        Sort sort = Sort.by("m.avg");
        Assert.assertThat(applySorting(query, sort, "m"), endsWith("order by m.avg asc"));
    }

    // DATAJPA-965, DATAJPA-970
    @Test
    public void doesNotPrefixAliasedFunctionCallNameWhenQueryStringContainsMultipleWhiteSpaces() {
        String query = "SELECT  AVG(  m.price  )   AS   avgPrice   FROM Magazine   m";
        Sort sort = Sort.by("avgPrice");
        Assert.assertThat(applySorting(query, sort, "m"), endsWith("order by avgPrice asc"));
    }

    // DATAJPA-1000
    @Test
    public void discoversCorrectAliasForJoinFetch() {
        Set<String> aliases = QueryUtils.QueryUtils.getOuterJoinAliases("SELECT DISTINCT user FROM User user LEFT JOIN FETCH user.authorities AS authority");
        Assert.assertThat(aliases, contains("authority"));
    }

    // DATAJPA-1171
    @Test
    public void doesNotContainStaticClauseInExistsQuery() {
        // 
        endsWith("WHERE x.id = :id");
    }

    // DATAJPA-1363
    @Test
    public void discoversAliasWithComplexFunction() {
        // 
        contains("myAlias");
    }
}

