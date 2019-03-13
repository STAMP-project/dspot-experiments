package com.kickstarter.models;


import com.kickstarter.mock.factories.CategoryFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;


public class CategoryTest extends TestCase {
    public void testCompareTo() {
        final List<Category> categories = Arrays.asList(CategoryFactory.bluesCategory(), CategoryFactory.ceramicsCategory(), CategoryFactory.worldMusicCategory(), CategoryFactory.musicCategory(), CategoryFactory.artCategory(), CategoryFactory.photographyCategory(), CategoryFactory.artCategory(), CategoryFactory.textilesCategory());
        final List<Category> sorted = new java.util.ArrayList(categories);
        Collections.sort(sorted);
        final List<Category> expected = Arrays.asList(CategoryFactory.artCategory(), CategoryFactory.artCategory(), CategoryFactory.ceramicsCategory(), CategoryFactory.textilesCategory(), CategoryFactory.musicCategory(), CategoryFactory.bluesCategory(), CategoryFactory.worldMusicCategory(), CategoryFactory.photographyCategory());
        TestCase.assertEquals(expected, sorted);
    }

    public void testComparableRootCategories() {
        final Category artCategory = CategoryFactory.artCategory();
        final Category musicCategory = CategoryFactory.musicCategory();
        TestCase.assertTrue(((artCategory.compareTo(musicCategory)) <= (-1)));
        TestCase.assertTrue(((musicCategory.compareTo(artCategory)) >= 1));
    }

    public void testComparableRootAndSelf() {
        final Category artCategory = CategoryFactory.artCategory();
        TestCase.assertTrue(((artCategory.compareTo(artCategory)) == 0));
    }

    public void testComparableChildAndSelf() {
        final Category bluesCategory = CategoryFactory.bluesCategory();
        TestCase.assertTrue(((bluesCategory.compareTo(bluesCategory)) == 0));
    }

    public void testComparableParentAndChildren() {
        final Category musicCategory = CategoryFactory.musicCategory();
        final Category bluesCategory = CategoryFactory.bluesCategory();
        final Category worldMusicCategory = CategoryFactory.worldMusicCategory();
        TestCase.assertTrue(((musicCategory.compareTo(bluesCategory)) <= (-1)));
        TestCase.assertTrue(((bluesCategory.compareTo(musicCategory)) >= 1));
        TestCase.assertTrue(((musicCategory.compareTo(worldMusicCategory)) <= (-1)));
        TestCase.assertTrue(((worldMusicCategory.compareTo(musicCategory)) >= (-1)));
    }

    public void testComparableChildrenAndOtherRoot() {
        final Category photographyCategory = CategoryFactory.photographyCategory();
        final Category bluesCategory = CategoryFactory.bluesCategory();
        final Category worldMusicCategory = CategoryFactory.worldMusicCategory();
        TestCase.assertTrue(((bluesCategory.compareTo(photographyCategory)) <= (-1)));
        TestCase.assertTrue(((worldMusicCategory.compareTo(photographyCategory)) <= (-1)));
        TestCase.assertTrue(((photographyCategory.compareTo(bluesCategory)) >= 1));
        TestCase.assertTrue(((photographyCategory.compareTo(worldMusicCategory)) >= 1));
    }

    public void testComparableChildrenDifferentRoots() {
        final Category bluesCategory = CategoryFactory.bluesCategory();
        final Category textilesCategory = CategoryFactory.textilesCategory();
        TestCase.assertTrue(((bluesCategory.compareTo(textilesCategory)) >= 1));
        TestCase.assertTrue(((textilesCategory.compareTo(bluesCategory)) <= (-1)));
        final Category ceramicsCategory = CategoryFactory.ceramicsCategory();
        final Category worldMusicCategory = CategoryFactory.worldMusicCategory();
        TestCase.assertTrue(((ceramicsCategory.compareTo(worldMusicCategory)) <= (-1)));
        TestCase.assertTrue(((worldMusicCategory.compareTo(ceramicsCategory)) >= 1));
    }
}

