package com.baeldung.apachecayenne;


import Author.NAME;
import com.baeldung.apachecayenne.persistent.Author;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.QueryResponse;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;


public class CayenneAdvancedOperationLiveTest {
    private static ObjectContext context = null;

    @Test
    public void givenAuthors_whenFindAllSQLTmplt_thenWeGetThreeAuthors() {
        SQLTemplate select = new SQLTemplate(Author.class, "select * from Author");
        List<Author> authors = CayenneAdvancedOperationLiveTest.context.performQuery(select);
        Assert.assertEquals(authors.size(), 3);
    }

    @Test
    public void givenAuthors_whenFindByNameSQLTmplt_thenWeGetOneAuthor() {
        SQLTemplate select = new SQLTemplate(Author.class, "select * from Author where name = 'Vicky Sarra'");
        List<Author> authors = CayenneAdvancedOperationLiveTest.context.performQuery(select);
        Author author = authors.get(0);
        Assert.assertEquals(authors.size(), 1);
        Assert.assertEquals(author.getName(), "Vicky Sarra");
    }

    @Test
    public void givenAuthors_whenLikeSltQry_thenWeGetOneAuthor() {
        Expression qualifier = ExpressionFactory.likeExp(NAME.getName(), "Paul%");
        SelectQuery query = new SelectQuery(Author.class, qualifier);
        List<Author> authorsTwo = CayenneAdvancedOperationLiveTest.context.performQuery(query);
        Assert.assertEquals(authorsTwo.size(), 1);
    }

    @Test
    public void givenAuthors_whenCtnsIgnorCaseSltQry_thenWeGetTwoAuthors() {
        Expression qualifier = ExpressionFactory.containsIgnoreCaseExp(NAME.getName(), "Paul");
        SelectQuery query = new SelectQuery(Author.class, qualifier);
        List<Author> authors = CayenneAdvancedOperationLiveTest.context.performQuery(query);
        Assert.assertEquals(authors.size(), 2);
    }

    @Test
    public void givenAuthors_whenCtnsIgnorCaseEndsWSltQry_thenWeGetTwoAuthors() {
        Expression qualifier = ExpressionFactory.containsIgnoreCaseExp(NAME.getName(), "Paul").andExp(ExpressionFactory.endsWithExp(NAME.getName(), "h"));
        SelectQuery query = new SelectQuery(Author.class, qualifier);
        List<Author> authors = CayenneAdvancedOperationLiveTest.context.performQuery(query);
        Author author = authors.get(0);
        Assert.assertEquals(authors.size(), 1);
        Assert.assertEquals(author.getName(), "pAuL Smith");
    }

    @Test
    public void givenAuthors_whenAscOrderingSltQry_thenWeGetOrderedAuthors() {
        SelectQuery query = new SelectQuery(Author.class);
        query.addOrdering(NAME.asc());
        List<Author> authors = query.select(CayenneAdvancedOperationLiveTest.context);
        Author firstAuthor = authors.get(0);
        Assert.assertEquals(authors.size(), 3);
        Assert.assertEquals(firstAuthor.getName(), "Paul Xavier");
    }

    @Test
    public void givenAuthors_whenDescOrderingSltQry_thenWeGetOrderedAuthors() {
        SelectQuery query = new SelectQuery(Author.class);
        query.addOrdering(NAME.desc());
        List<Author> authors = query.select(CayenneAdvancedOperationLiveTest.context);
        Author firstAuthor = authors.get(0);
        Assert.assertEquals(authors.size(), 3);
        Assert.assertEquals(firstAuthor.getName(), "pAuL Smith");
    }

    @Test
    public void givenAuthors_onContainsObjS_thenWeGetOneRecord() {
        List<Author> authors = ObjectSelect.query(Author.class).where(NAME.contains("Paul")).select(CayenneAdvancedOperationLiveTest.context);
        Assert.assertEquals(authors.size(), 1);
    }

    @Test
    public void givenAuthors_whenLikeObjS_thenWeGetTwoAuthors() {
        List<Author> authors = ObjectSelect.query(Author.class).where(NAME.likeIgnoreCase("Paul%")).select(CayenneAdvancedOperationLiveTest.context);
        Assert.assertEquals(authors.size(), 2);
    }

    @Test
    public void givenTwoAuthor_whenEndsWithObjS_thenWeGetOrderedAuthors() {
        List<Author> authors = ObjectSelect.query(Author.class).where(NAME.endsWith("Sarra")).select(CayenneAdvancedOperationLiveTest.context);
        Author firstAuthor = authors.get(0);
        Assert.assertEquals(authors.size(), 1);
        Assert.assertEquals(firstAuthor.getName(), "Vicky Sarra");
    }

    @Test
    public void givenTwoAuthor_whenInObjS_thenWeGetAuthors() {
        List<String> names = Arrays.asList("Paul Xavier", "pAuL Smith", "Vicky Sarra");
        List<Author> authors = ObjectSelect.query(Author.class).where(NAME.in(names)).select(CayenneAdvancedOperationLiveTest.context);
        Assert.assertEquals(authors.size(), 3);
    }

    @Test
    public void givenTwoAuthor_whenNinObjS_thenWeGetAuthors() {
        List<String> names = Arrays.asList("Paul Xavier", "pAuL Smith");
        List<Author> authors = ObjectSelect.query(Author.class).where(NAME.nin(names)).select(CayenneAdvancedOperationLiveTest.context);
        Author author = authors.get(0);
        Assert.assertEquals(authors.size(), 1);
        Assert.assertEquals(author.getName(), "Vicky Sarra");
    }

    @Test
    public void givenTwoAuthor_whenIsNotNullObjS_thenWeGetAuthors() {
        List<Author> authors = ObjectSelect.query(Author.class).where(NAME.isNotNull()).select(CayenneAdvancedOperationLiveTest.context);
        Assert.assertEquals(authors.size(), 3);
    }

    @Test
    public void givenAuthors_whenFindAllEJBQL_thenWeGetThreeAuthors() {
        EJBQLQuery query = new EJBQLQuery("select a FROM Author a");
        List<Author> authors = CayenneAdvancedOperationLiveTest.context.performQuery(query);
        Assert.assertEquals(authors.size(), 3);
    }

    @Test
    public void givenAuthors_whenFindByNameEJBQL_thenWeGetOneAuthor() {
        EJBQLQuery query = new EJBQLQuery("select a FROM Author a WHERE a.name = 'Vicky Sarra'");
        List<Author> authors = CayenneAdvancedOperationLiveTest.context.performQuery(query);
        Author author = authors.get(0);
        Assert.assertEquals(authors.size(), 1);
        Assert.assertEquals(author.getName(), "Vicky Sarra");
    }

    @Test
    public void givenAuthors_whenUpdadingByNameEJBQL_thenWeGetTheUpdatedAuthor() {
        EJBQLQuery query = new EJBQLQuery("UPDATE Author AS a SET a.name = 'Vicky Edison' WHERE a.name = 'Vicky Sarra'");
        QueryResponse queryResponse = CayenneAdvancedOperationLiveTest.context.performGenericQuery(query);
        EJBQLQuery queryUpdatedAuthor = new EJBQLQuery("select a FROM Author a WHERE a.name = 'Vicky Edison'");
        List<Author> authors = CayenneAdvancedOperationLiveTest.context.performQuery(queryUpdatedAuthor);
        Author author = authors.get(0);
        assertNotNull(author);
    }

    @Test
    public void givenAuthors_whenSeletingNamesEJBQL_thenWeGetListWithSizeThree() {
        String[] args = new String[]{ "Paul Xavier", "pAuL Smith", "Vicky Sarra" };
        List<String> names = Arrays.asList(args);
        EJBQLQuery query = new EJBQLQuery("select a.name FROM Author a");
        List<String> nameList = CayenneAdvancedOperationLiveTest.context.performQuery(query);
        Collections.sort(names);
        Collections.sort(nameList);
        Assert.assertEquals(names.size(), 3);
        Assert.assertEquals(nameList.size(), 3);
        Assert.assertEquals(names, nameList);
    }

    @Test
    public void givenAuthors_whenDeletingAllWithEJB_thenWeGetNoAuthor() {
        EJBQLQuery deleteQuery = new EJBQLQuery("delete FROM Author");
        EJBQLQuery findAllQuery = new EJBQLQuery("select a FROM Author a");
        CayenneAdvancedOperationLiveTest.context.performQuery(deleteQuery);
        List<Author> objects = CayenneAdvancedOperationLiveTest.context.performQuery(findAllQuery);
        Assert.assertEquals(objects.size(), 0);
    }

    @Test
    public void givenAuthors_whenInsertingSQLExec_thenWeGetNewAuthor() {
        int inserted = SQLExec.query("INSERT INTO Author (name) VALUES ('Baeldung')").update(CayenneAdvancedOperationLiveTest.context);
        Assert.assertEquals(inserted, 1);
    }

    @Test
    public void givenAuthors_whenUpdatingSQLExec_thenItsUpdated() {
        int updated = SQLExec.query("UPDATE Author SET name = 'Baeldung' WHERE name = 'Vicky Sarra'").update(CayenneAdvancedOperationLiveTest.context);
        Assert.assertEquals(updated, 1);
    }
}

