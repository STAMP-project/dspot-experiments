package com.baeldung.apachecayenne;


import Author.NAME;
import com.baeldung.apachecayenne.persistent.Article;
import com.baeldung.apachecayenne.persistent.Author;
import java.util.List;
import junit.framework.Assert;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.junit.Test;

import static org.junit.Assert.assertNull;


public class CayenneOperationLiveTest {
    private static ObjectContext context = null;

    @Test
    public void givenAuthor_whenInsert_thenWeGetOneRecordInTheDatabase() {
        Author author = CayenneOperationLiveTest.context.newObject(Author.class);
        author.setName("Paul");
        CayenneOperationLiveTest.context.commitChanges();
        long records = ObjectSelect.dataRowQuery(Author.class).selectCount(CayenneOperationLiveTest.context);
        Assert.assertEquals(1, records);
    }

    @Test
    public void givenAuthor_whenInsert_andQueryByFirstName_thenWeGetTheAuthor() {
        Author author = CayenneOperationLiveTest.context.newObject(Author.class);
        author.setName("Paul");
        CayenneOperationLiveTest.context.commitChanges();
        Author expectedAuthor = ObjectSelect.query(Author.class).where(NAME.eq("Paul")).selectOne(CayenneOperationLiveTest.context);
        Assert.assertEquals("Paul", expectedAuthor.getName());
    }

    @Test
    public void givenTwoAuthor_whenInsert_andQueryAll_thenWeGetTwoAuthors() {
        Author firstAuthor = CayenneOperationLiveTest.context.newObject(Author.class);
        firstAuthor.setName("Paul");
        Author secondAuthor = CayenneOperationLiveTest.context.newObject(Author.class);
        secondAuthor.setName("Ludovic");
        CayenneOperationLiveTest.context.commitChanges();
        List<Author> authors = ObjectSelect.query(Author.class).select(CayenneOperationLiveTest.context);
        Assert.assertEquals(2, authors.size());
    }

    @Test
    public void givenAuthor_whenUpdating_thenWeGetAnUpatedeAuthor() {
        Author author = CayenneOperationLiveTest.context.newObject(Author.class);
        author.setName("Paul");
        CayenneOperationLiveTest.context.commitChanges();
        Author expectedAuthor = ObjectSelect.query(Author.class).where(NAME.eq("Paul")).selectOne(CayenneOperationLiveTest.context);
        expectedAuthor.setName("Garcia");
        CayenneOperationLiveTest.context.commitChanges();
        Assert.assertEquals(author.getName(), expectedAuthor.getName());
    }

    @Test
    public void givenAuthor_whenDeleting_thenWeLostHisDetails() {
        Author author = CayenneOperationLiveTest.context.newObject(Author.class);
        author.setName("Paul");
        CayenneOperationLiveTest.context.commitChanges();
        Author savedAuthor = ObjectSelect.query(Author.class).where(NAME.eq("Paul")).selectOne(CayenneOperationLiveTest.context);
        if (savedAuthor != null) {
            CayenneOperationLiveTest.context.deleteObjects(author);
            CayenneOperationLiveTest.context.commitChanges();
        }
        Author expectedAuthor = ObjectSelect.query(Author.class).where(NAME.eq("Paul")).selectOne(CayenneOperationLiveTest.context);
        assertNull(expectedAuthor);
    }

    @Test
    public void givenAuthor_whenAttachingToArticle_thenTheRelationIsMade() {
        Author author = CayenneOperationLiveTest.context.newObject(Author.class);
        author.setName("Paul");
        Article article = CayenneOperationLiveTest.context.newObject(Article.class);
        article.setTitle("My post title");
        article.setContent("The content");
        article.setAuthor(author);
        CayenneOperationLiveTest.context.commitChanges();
        Author expectedAuthor = ObjectSelect.query(Author.class).where(NAME.eq("Paul")).selectOne(CayenneOperationLiveTest.context);
        Article expectedArticle = expectedAuthor.getArticles().get(0);
        Assert.assertEquals(article.getTitle(), expectedArticle.getTitle());
    }
}

