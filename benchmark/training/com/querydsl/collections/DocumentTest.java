package com.querydsl.collections;


import com.querydsl.core.types.Predicate;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static QDocument.document;


public class DocumentTest {
    private Document doc1;

    private Document doc2;

    private Document doc3;

    private QDocument qDoc = document;

    @Test
    public void test1() {
        Predicate crit = qDoc.id.eq(3L);
        List<Document> expResult = CollQueryFactory.from(qDoc, doc1, doc2, doc3).where(crit).fetch();
        Assert.assertTrue(expResult.contains(doc3));// ok

    }

    @Test
    public void test2() {
        Predicate crit = qDoc.meshThesaurusTerms.any().eq("x");
        List<Document> expResult = CollQueryFactory.from(qDoc, doc1, doc2, doc3).where(crit).fetch();
        Assert.assertTrue(expResult.contains(doc1));// ok

    }

    @Test
    public void test3() {
        Predicate crit = qDoc.meshThesaurusTerms.any().eq("x").or(qDoc.id.eq(3L));
        List<Document> expResult = CollQueryFactory.from(qDoc, doc1, doc2, doc3).where(crit).fetch();
        Assert.assertTrue(expResult.contains(doc1));
        Assert.assertTrue(expResult.contains(doc3));// fails, expResult contains only doc1, but should contain doc1 and doc3!

    }

    @Test
    public void test4() {
        Predicate crit = qDoc.id.eq(3L).or(qDoc.meshThesaurusTerms.any().eq("x"));
        List<Document> expResult = CollQueryFactory.from(qDoc, doc1, doc2, doc3).where(crit).fetch();
        Assert.assertTrue(expResult.contains(doc1));
        Assert.assertTrue(expResult.contains(doc3));// fails, expResult contains only doc1, but should contain doc1 and doc3!

    }
}

