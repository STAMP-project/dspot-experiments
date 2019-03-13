/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.examples.use;


import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class ExampleTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private ArticleCalculator mockCalculator;

    @Mock
    private ArticleDatabase mockDatabase;

    @InjectMocks
    private ArticleManager articleManager;

    @Test
    public void managerCountsArticlesAndSavesThemInTheDatabase() {
        Mockito.when(mockCalculator.countArticles("Guardian")).thenReturn(12);
        Mockito.when(mockCalculator.countArticlesInPolish(ArgumentMatchers.anyString())).thenReturn(5);
        articleManager.updateArticleCounters("Guardian");
        Mockito.verify(mockDatabase).updateNumberOfArticles("Guardian", 12);
        Mockito.verify(mockDatabase).updateNumberOfPolishArticles("Guardian", 5);
        Mockito.verify(mockDatabase).updateNumberOfEnglishArticles("Guardian", 7);
    }

    @Test
    public void managerCountsArticlesUsingCalculator() {
        articleManager.updateArticleCounters("Guardian");
        Mockito.verify(mockCalculator).countArticles("Guardian");
        Mockito.verify(mockCalculator).countArticlesInPolish("Guardian");
    }

    @Test
    public void managerSavesArticlesInTheDatabase() {
        articleManager.updateArticleCounters("Guardian");
        Mockito.verify(mockDatabase).updateNumberOfArticles("Guardian", 0);
        Mockito.verify(mockDatabase).updateNumberOfPolishArticles("Guardian", 0);
        Mockito.verify(mockDatabase).updateNumberOfEnglishArticles("Guardian", 0);
    }

    @Test
    public void managerUpdatesNumberOfRelatedArticles() {
        Article articleOne = new Article();
        Article articleTwo = new Article();
        Article articleThree = new Article();
        Mockito.when(mockCalculator.countNumberOfRelatedArticles(articleOne)).thenReturn(1);
        Mockito.when(mockCalculator.countNumberOfRelatedArticles(articleTwo)).thenReturn(12);
        Mockito.when(mockCalculator.countNumberOfRelatedArticles(articleThree)).thenReturn(0);
        Mockito.when(mockDatabase.getArticlesFor("Guardian")).thenReturn(Arrays.asList(articleOne, articleTwo, articleThree));
        articleManager.updateRelatedArticlesCounters("Guardian");
        Mockito.verify(mockDatabase).save(articleOne);
        Mockito.verify(mockDatabase).save(articleTwo);
        Mockito.verify(mockDatabase).save(articleThree);
    }

    @Test
    public void shouldPersistRecalculatedArticle() {
        Article articleOne = new Article();
        Article articleTwo = new Article();
        Mockito.when(mockCalculator.countNumberOfRelatedArticles(articleOne)).thenReturn(1);
        Mockito.when(mockCalculator.countNumberOfRelatedArticles(articleTwo)).thenReturn(12);
        Mockito.when(mockDatabase.getArticlesFor("Guardian")).thenReturn(Arrays.asList(articleOne, articleTwo));
        articleManager.updateRelatedArticlesCounters("Guardian");
        InOrder inOrder = Mockito.inOrder(mockDatabase, mockCalculator);
        inOrder.verify(mockCalculator).countNumberOfRelatedArticles(((Article) (ArgumentMatchers.anyObject())));
        inOrder.verify(mockDatabase, Mockito.atLeastOnce()).save(((Article) (ArgumentMatchers.anyObject())));
    }
}

