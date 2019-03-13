package org.hibernate.userguide.fetching;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.graph.AbstractEntityGraphTest;
import org.hibernate.graph.EntityGraphs;
import org.hibernate.graph.GraphParser;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.userguide.fetching.GraphFetchingTest.Employee;
import org.junit.Assert;
import org.junit.Test;


@RequiresDialect(H2Dialect.class)
public class GraphParsingTest extends AbstractEntityGraphTest {
    @Test
    public void testParsingExample1() {
        EntityManager entityManager = getOrCreateEntityManager();
        // tag::fetching-strategies-dynamic-fetching-entity-graph-parsing-example-1[]
        final EntityGraph<GraphFetchingTest.Project> graph = GraphParser.parse(GraphFetchingTest.Project.class, "employees( department )", entityManager);
        // end::fetching-strategies-dynamic-fetching-entity-graph-parsing-example-1[]
        Assert.assertNotNull(graph);
    }

    @Test
    public void testParsingExample2() {
        EntityManager entityManager = getOrCreateEntityManager();
        // tag::fetching-strategies-dynamic-fetching-entity-graph-parsing-example-2[]
        final EntityGraph<GraphFetchingTest.Project> graph = GraphParser.parse(GraphFetchingTest.Project.class, "employees( username, password, accessLevel, department( employees( username ) ) )", entityManager);
        // end::fetching-strategies-dynamic-fetching-entity-graph-parsing-example-2[]
        Assert.assertNotNull(graph);
    }

    @Test
    public void testMapKeyParsing() {
        EntityManager entityManager = getOrCreateEntityManager();
        // tag::fetching-strategies-dynamic-fetching-entity-graph-parsing-key-example-1[]
        final EntityGraph<GraphParsingTest.Movie> graph = GraphParser.parse(GraphParsingTest.Movie.class, "cast.key( name )", entityManager);
        // end::fetching-strategies-dynamic-fetching-entity-graph-parsing-key-example-1[]
        Assert.assertNotNull(graph);
    }

    @Test
    public void testEntityKeyParsing() {
        EntityManager entityManager = getOrCreateEntityManager();
        // tag::fetching-strategies-dynamic-fetching-entity-graph-parsing-key-example-2[]
        final EntityGraph<GraphParsingTest.Ticket> graph = GraphParser.parse(GraphParsingTest.Ticket.class, "showing.key( movie( cast ) )", entityManager);
        // end::fetching-strategies-dynamic-fetching-entity-graph-parsing-key-example-2[]
        Assert.assertNotNull(graph);
    }

    @Test
    public void testMergingExample() {
        EntityManager entityManager = getOrCreateEntityManager();
        // tag::fetching-strategies-dynamic-fetching-entity-graph-merging-example[]
        final EntityGraph<GraphFetchingTest.Project> a = GraphParser.parse(GraphFetchingTest.Project.class, "employees( username )", entityManager);
        final EntityGraph<GraphFetchingTest.Project> b = GraphParser.parse(GraphFetchingTest.Project.class, "employees( password, accessLevel )", entityManager);
        final EntityGraph<GraphFetchingTest.Project> c = GraphParser.parse(GraphFetchingTest.Project.class, "employees( department( employees( username ) ) )", entityManager);
        final EntityGraph<GraphFetchingTest.Project> all = EntityGraphs.merge(entityManager, GraphFetchingTest.Project.class, a, b, c);
        // end::fetching-strategies-dynamic-fetching-entity-graph-merging-example[]
        final EntityGraph<GraphFetchingTest.Project> expected = GraphParser.parse(GraphFetchingTest.Project.class, "employees( username, password, accessLevel, department( employees( username ) ) )", entityManager);
        Assert.assertTrue(EntityGraphs.areEqual(expected, all));
    }

    @Test
    public void testFindExample() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Long userId = 1L;
            // tag::fetching-strategies-dynamic-fetching-entity-graph-apply-example-find[]
            entityManager.find(.class, userId, Collections.singletonMap(GraphSemantic.FETCH.getJpaHintName(), GraphParser.parse(.class, "username, accessLevel, department", entityManager)));
            // end::fetching-strategies-dynamic-fetching-entity-graph-apply-example-find[]
        });
    }

    @Test
    public void testQueryExample() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::fetching-strategies-dynamic-fetching-entity-graph-apply-example-query[]
            final String graphString = "username, accessLevel";
            final String queryString = "select e from Employee e where e.id = 1";
            final EntityGraph<Employee> graph = GraphParser.parse(.class, graphString, entityManager);
            TypedQuery<Employee> query1 = entityManager.createQuery(queryString, .class);
            query1.setHint(GraphSemantic.FETCH.getJpaHintName(), graph);
            // end::fetching-strategies-dynamic-fetching-entity-graph-apply-example-query[]
        });
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Some entities for illustrating key sub-graphs
    // 
    // NOTE : for the moment I do not add named (sub)graph annotations because
    // this is only used for discussing graph parsing
    @Entity(name = "Person")
    public static class Person {
        @Id
        private Integer id;

        String name;
    }

    @Entity(name = "Movie")
    public static class Movie {
        @Id
        private Integer id;

        String title;

        // @OneToMany
        @ElementCollection
        Map<GraphParsingTest.Person, String> cast;
    }

    @Entity(name = "Theater")
    public static class Theater {
        @Id
        private Integer id;

        int seatingCapacity;

        boolean foodService;
    }

    @Entity(name = "Showing")
    public static class Showing {
        @Embeddable
        public static class Id implements Serializable {
            @ManyToOne
            @JoinColumn
            private GraphParsingTest.Movie movie;

            @ManyToOne
            @JoinColumn
            private GraphParsingTest.Theater theater;
        }

        @EmbeddedId
        private GraphParsingTest.Showing.Id id;

        private LocalDateTime startTime;

        private LocalDateTime endTime;
    }

    @Entity(name = "Ticket")
    public static class Ticket {
        @Id
        private Integer id;

        @ManyToOne
        GraphParsingTest.Showing showing;
    }
}

