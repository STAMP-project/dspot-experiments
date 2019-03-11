package com.commafeed.backend.opml;


import com.commafeed.backend.dao.FeedCategoryDAO;
import com.commafeed.backend.dao.FeedSubscriptionDAO;
import com.commafeed.backend.model.FeedCategory;
import com.commafeed.backend.model.FeedSubscription;
import com.commafeed.backend.model.User;
import com.rometools.opml.feed.opml.Opml;
import com.rometools.opml.feed.opml.Outline;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class OPMLExporterTest {
    @Mock
    private FeedCategoryDAO feedCategoryDAO;

    @Mock
    private FeedSubscriptionDAO feedSubscriptionDAO;

    private User user = new User();

    private FeedCategory cat1 = new FeedCategory();

    private FeedCategory cat2 = new FeedCategory();

    private FeedSubscription rootFeed = newFeedSubscription("rootFeed", "rootFeed.com");

    private FeedSubscription cat1Feed = newFeedSubscription("cat1Feed", "cat1Feed.com");

    private FeedSubscription cat2Feed = newFeedSubscription("cat2Feed", "cat2Feed.com");

    private List<FeedCategory> categories = new ArrayList<>();

    private List<FeedSubscription> subscriptions = new ArrayList<>();

    @Test
    public void generates_OPML_correctly() {
        Mockito.when(feedCategoryDAO.findAll(user)).thenReturn(categories);
        Mockito.when(feedSubscriptionDAO.findAll(user)).thenReturn(subscriptions);
        Opml opml = new OPMLExporter(feedCategoryDAO, feedSubscriptionDAO).export(user);
        List<Outline> rootOutlines = opml.getOutlines();
        Assert.assertEquals(2, rootOutlines.size());
        Assert.assertTrue(containsCategory(rootOutlines, "cat1"));
        Assert.assertTrue(containsFeed(rootOutlines, "rootFeed", "rootFeed.com"));
        Outline cat1Outline = getCategoryOutline(rootOutlines, "cat1");
        List<Outline> cat1Children = cat1Outline.getChildren();
        Assert.assertEquals(2, cat1Children.size());
        Assert.assertTrue(containsCategory(cat1Children, "cat2"));
        Assert.assertTrue(containsFeed(cat1Children, "cat1Feed", "cat1Feed.com"));
        Outline cat2Outline = getCategoryOutline(cat1Children, "cat2");
        List<Outline> cat2Children = cat2Outline.getChildren();
        Assert.assertEquals(1, cat2Children.size());
        Assert.assertTrue(containsFeed(cat2Children, "cat2Feed", "cat2Feed.com"));
    }
}

