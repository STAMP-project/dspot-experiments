package com.baeldung.weakhashmap;


import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class WeakHashMapUnitTest {
    @Test
    public void givenWeakHashMap_whenCacheValueThatHasNoReferenceToIt_GCShouldReclaimThatObject() {
        // given
        WeakHashMap<WeakHashMapUnitTest.UniqueImageName, WeakHashMapUnitTest.BigImage> map = new WeakHashMap<>();
        WeakHashMapUnitTest.BigImage bigImage = new WeakHashMapUnitTest.BigImage("image_id");
        WeakHashMapUnitTest.UniqueImageName imageName = new WeakHashMapUnitTest.UniqueImageName("name_of_big_image");
        map.put(imageName, bigImage);
        Assert.assertTrue(map.containsKey(imageName));
        // when big image key is not reference anywhere
        imageName = null;
        System.gc();
        // then GC will finally reclaim that object
        await().atMost(10, TimeUnit.SECONDS).until(map::isEmpty);
    }

    @Test
    public void givenWeakHashMap_whenCacheValueThatHasNoReferenceToIt_GCShouldReclaimThatObjectButLeaveReferencedObject() {
        // given
        WeakHashMap<WeakHashMapUnitTest.UniqueImageName, WeakHashMapUnitTest.BigImage> map = new WeakHashMap<>();
        WeakHashMapUnitTest.BigImage bigImageFirst = new WeakHashMapUnitTest.BigImage("foo");
        WeakHashMapUnitTest.UniqueImageName imageNameFirst = new WeakHashMapUnitTest.UniqueImageName("name_of_big_image");
        WeakHashMapUnitTest.BigImage bigImageSecond = new WeakHashMapUnitTest.BigImage("foo_2");
        WeakHashMapUnitTest.UniqueImageName imageNameSecond = new WeakHashMapUnitTest.UniqueImageName("name_of_big_image_2");
        map.put(imageNameFirst, bigImageFirst);
        map.put(imageNameSecond, bigImageSecond);
        Assert.assertTrue(map.containsKey(imageNameFirst));
        Assert.assertTrue(map.containsKey(imageNameSecond));
        // when
        imageNameFirst = null;
        System.gc();
        // then
        await().atMost(10, TimeUnit.SECONDS).until(() -> (map.size()) == 1);
        await().atMost(10, TimeUnit.SECONDS).until(() -> map.containsKey(imageNameSecond));
    }

    class BigImage {
        public final String imageId;

        BigImage(String imageId) {
            this.imageId = imageId;
        }
    }

    class UniqueImageName {
        public final String imageName;

        UniqueImageName(String imageName) {
            this.imageName = imageName;
        }
    }
}

