package com.baeldung.hibernate.jpacriteriabuilder;


import com.baeldung.hibernate.entities.DeptEmployee;
import com.baeldung.hibernate.jpacriteriabuilder.service.EmployeeSearchService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.hamcrest.MatcherAssert;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;


public class EmployeeSearchServiceIntegrationTest {
    private EntityManager entityManager;

    private EmployeeSearchService searchService;

    private Session session;

    @Test
    public final void givenCriteriaQuery_whenSearchedUsingCriteriaBuilderWithListofAuthors_thenResultIsFilteredByAuthorNames() {
        List<String> titles = new ArrayList<String>() {
            {
                add("Manager");
                add("Senior Manager");
                add("Director");
            }
        };
        List<DeptEmployee> result = searchService.filterbyTitleUsingCriteriaBuilder(titles);
        Assert.assertEquals("Number of Employees does not match with expected.", 6, result.size());
        MatcherAssert.assertThat(result.stream().map(DeptEmployee::getTitle).distinct().collect(Collectors.toList()), containsInAnyOrder(titles.toArray()));
    }

    @Test
    public final void givenCriteriaQuery_whenSearchedUsingExpressionWithListofAuthors_thenResultIsFilteredByAuthorNames() {
        List<String> titles = new ArrayList<String>() {
            {
                add("Manager");
                add("Senior Manager");
                add("Director");
            }
        };
        List<DeptEmployee> result = searchService.filterbyTitleUsingExpression(titles);
        Assert.assertEquals("Number of Employees does not match with expected.", 6, result.size());
        MatcherAssert.assertThat(result.stream().map(DeptEmployee::getTitle).distinct().collect(Collectors.toList()), containsInAnyOrder(titles.toArray()));
    }

    @Test
    public final void givenCriteriaQuery_whenSearchedDepartmentLike_thenResultIsFilteredByDepartment() {
        List<DeptEmployee> result = searchService.searchByDepartmentQuery("Sales");
        Assert.assertEquals("Number of Employees does not match with expected.", 7, result.size());
    }
}

