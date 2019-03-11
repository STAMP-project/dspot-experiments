package io.github.hidroh.materialistic.data;


import HackerNewsClient.WEB_ITEM_PATH;
import HackerNewsItem.CREATOR;
import Item.POLL_TYPE;
import Item.STORY_TYPE;
import R.string.dead_prefix;
import RuntimeEnvironment.application;
import android.os.Parcel;
import io.github.hidroh.materialistic.test.TestItem;
import io.github.hidroh.materialistic.test.TestRunner;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(TestRunner.class)
public class HackerNewsItemTest {
    private HackerNewsItem item;

    @Test
    public void testId() {
        Assert.assertEquals("1", item.getId());
    }

    @Test
    public void testFavorite() {
        Assert.assertFalse(item.isFavorite());
        item.setFavorite(true);
        Assert.assertTrue(item.isFavorite());
    }

    @Test
    public void testViewed() {
        Assert.assertFalse(item.isViewed());
        item.setIsViewed(true);
        Assert.assertTrue(item.isViewed());
    }

    @Test
    public void testLocalRevision() {
        Assert.assertEquals((-1), item.getLocalRevision());
        item.setLocalRevision(0);
        Assert.assertEquals(0, item.getLocalRevision());
    }

    @Test
    public void testCollapsed() {
        Assert.assertFalse(item.isCollapsed());
        item.setCollapsed(true);
        Assert.assertTrue(item.isCollapsed());
    }

    @Test
    public void testPopulate() {
        item.populate(new TestItem() {
            @Override
            public String getTitle() {
                return "title";
            }

            @Override
            public String getRawType() {
                return "rawType";
            }

            @Override
            public String getRawUrl() {
                return "rawUrl";
            }

            @Override
            public long[] getKids() {
                return new long[]{ 1L };
            }

            @Override
            public String getBy() {
                return "by";
            }

            @Override
            public long getTime() {
                return 1234L;
            }

            @Override
            public String getText() {
                return "text";
            }

            @Override
            public String getParent() {
                return "1";
            }

            @Override
            public boolean isDead() {
                return true;
            }

            @Override
            public boolean isDeleted() {
                return true;
            }

            @Override
            public int getDescendants() {
                return 1;
            }

            @Override
            public int getScore() {
                return 5;
            }
        });
        Assert.assertEquals("title", item.getTitle());
        Assert.assertEquals("text", item.getText());
        Assert.assertEquals("rawType", item.getRawType());
        Assert.assertEquals("rawUrl", item.getRawUrl());
        Assert.assertEquals("by", item.getBy());
        Assert.assertEquals(1234L, item.getTime());
        Assert.assertEquals(1, item.getDescendants());
        Assert.assertEquals(1, item.getLastKidCount());
        Assert.assertEquals(5, item.getScore());
        Assert.assertTrue(item.isDead());
        Assert.assertTrue(item.isDeleted());
        assertThat(item.getDisplayedAuthor(application, true, 0)).contains(" - by");
        assertThat(item.getDisplayedTime(application)).isNotEmpty();
        assertThat(item.getKids()).hasSize(1);
        Assert.assertEquals(1, item.getKidItems()[0].getLevel());
    }

    @Test
    public void testGetTypeDefault() {
        item.populate(new TestItem() {
            @Override
            public String getRawType() {
                return null;
            }
        });
        Assert.assertEquals(STORY_TYPE, item.getType());
    }

    @Test
    public void testGetType() {
        item.populate(new TestItem() {
            @Override
            public String getRawType() {
                return "poll";
            }
        });
        Assert.assertEquals(POLL_TYPE, item.getType());
    }

    @Test
    public void testGetTypeInvalid() {
        item.populate(new TestItem() {
            @Override
            public String getRawType() {
                return "blah";
            }
        });
        Assert.assertEquals("blah", item.getType());
    }

    @Test
    public void testGetDisplayedTitleComment() {
        item.populate(new TestItem() {
            @Override
            public String getRawType() {
                return "comment";
            }

            @Override
            public String getText() {
                return "comment";
            }
        });
        Assert.assertEquals("comment", item.getDisplayedTitle());
    }

    @Test
    public void testGetDisplayedTitleNonComment() {
        item.populate(new TestItem() {
            @Override
            public String getRawType() {
                return "story";
            }

            @Override
            public String getTitle() {
                return "title";
            }
        });
        Assert.assertEquals("title", item.getDisplayedTitle());
    }

    @Test
    public void testGetDisplayedTime() {
        item.populate(new TestItem() {
            @Override
            public long getTime() {
                return 1429027200L;// Apr 15 2015

            }

            @Override
            public String getBy() {
                return "author";
            }
        });
        assertThat(item.getDisplayedAuthor(application, true, 0)).contains(" - author");
        assertThat(item.getDisplayedAuthor(application, false, 0)).contains(" - author");
        item.populate(new TestItem() {
            @Override
            public String getBy() {
                return "author";
            }

            @Override
            public boolean isDead() {
                return true;
            }
        });
        assertThat(item.getDisplayedTime(application)).contains(application.getString(dead_prefix));
    }

    @Test
    public void testKidCount() {
        item.populate(new TestItem() {
            @Override
            public int getDescendants() {
                return 10;
            }
        });
        Assert.assertEquals(10, item.getKidCount());
    }

    @Test
    public void testLastKidCount() {
        item.populate(new TestItem() {
            @Override
            public int getDescendants() {
                return 0;
            }
        });
        Assert.assertEquals(0, item.getLastKidCount());
        item.setLastKidCount(1);
        Assert.assertEquals(1, item.getLastKidCount());
    }

    @Test
    public void testHasNewKids() {
        Assert.assertFalse(item.hasNewKids());
        item.populate(new TestItem() {
            @Override
            public int getDescendants() {
                return 0;
            }
        });
        Assert.assertFalse(item.hasNewKids());
        item.populate(new TestItem() {
            @Override
            public int getDescendants() {
                return 1;
            }
        });
        Assert.assertTrue(item.hasNewKids());
        item.populate(new TestItem() {
            @Override
            public int getDescendants() {
                return 1;
            }
        });
        Assert.assertFalse(item.hasNewKids());
    }

    @Test
    public void testKidCountNull() {
        item.populate(new TestItem() {
            @Override
            public long[] getKids() {
                return null;
            }
        });
        Assert.assertEquals(0, item.getKidCount());
    }

    @Test
    public void testKidCountNoDescendants() {
        item.populate(new TestItem() {
            @Override
            public long[] getKids() {
                return new long[]{ 1L, 2L };
            }
        });
        Assert.assertEquals(2, item.getKidCount());
    }

    @Test
    public void testGetUrlStory() {
        item.populate(new TestItem() {
            @Override
            public String getRawType() {
                return "story";
            }

            @Override
            public String getRawUrl() {
                return "http://example.com";
            }
        });
        Assert.assertEquals("http://example.com", item.getUrl());
    }

    @Test
    public void testGetUrlStoryNoUrl() {
        item.populate(new TestItem() {
            @Override
            public String getRawType() {
                return "story";
            }
        });
        Assert.assertEquals(String.format(WEB_ITEM_PATH, "1"), item.getUrl());
    }

    @Test
    public void testGetUrlNonStory() {
        item.populate(new TestItem() {
            @Override
            public String getRawType() {
                return "comment";
            }
        });
        Assert.assertEquals(String.format(WEB_ITEM_PATH, "1"), item.getUrl());
    }

    @Test
    public void testGetSource() {
        Assert.assertEquals("news.ycombinator.com", item.getSource());
        item.populate(new TestItem() {
            @Override
            public String getRawUrl() {
                return "http://example.com";
            }
        });
        Assert.assertEquals("example.com", item.getSource());
    }

    @Test
    public void testGetKidItems() {
        assertThat(item.getKidItems()).isEmpty();
        item.populate(new TestItem() {
            @Override
            public long[] getKids() {
                return new long[]{ 1L, 2L };
            }
        });
        assertThat(item.getKidItems()).hasSize(2);
        Assert.assertEquals(1, item.getKidItems()[0].getRank());
        Assert.assertEquals(2, item.getKidItems()[1].getRank());
    }

    @Test
    public void testIsShareable() {
        Assert.assertTrue(item.isStoryType());
        item.populate(new TestItem() {
            @Override
            public String getRawType() {
                return "comment";
            }
        });
        Assert.assertFalse(item.isStoryType());
        item.populate(new TestItem() {
            @Override
            public String getRawType() {
                return "poll";
            }
        });
        Assert.assertTrue(item.isStoryType());
    }

    @Test
    public void testParcelReadWrite() {
        Parcel parcel = Parcel.obtain();
        item.populate(new TestItem() {
            @Override
            public String getTitle() {
                return "title";
            }
        });
        item.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Item actual = CREATOR.createFromParcel(parcel);
        Assert.assertEquals("title", actual.getDisplayedTitle());
        Assert.assertFalse(actual.isFavorite());
    }

    @Test
    public void testParcelFavorite() {
        Parcel parcel = Parcel.obtain();
        item.populate(new TestItem() {});
        item.setFavorite(true);
        item.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Assert.assertTrue(CREATOR.createFromParcel(parcel).isFavorite());
    }

    @Test
    public void testParcelable() {
        assertThat(CREATOR.newArray(1)).hasSize(1);
        Assert.assertEquals(0, item.describeContents());
    }

    @Test
    public void testEquals() {
        Assert.assertFalse(item.equals(null));
        Assert.assertFalse(item.equals(new TestItem() {}));
        Assert.assertFalse(item.equals(new HackerNewsItem(2L)));
        Assert.assertTrue(item.equals(item));
        Assert.assertTrue(item.equals(new HackerNewsItem(1L)));
    }
}

