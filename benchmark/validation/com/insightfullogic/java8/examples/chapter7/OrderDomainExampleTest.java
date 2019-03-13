package com.insightfullogic.java8.examples.chapter7;


import com.insightfullogic.java8.examples.chapter4.OrderDomain;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 * Sample code for chapter7 OrderDomain from the
 * previous chapter.
 */
public class OrderDomainExampleTest {
    // BEGIN can_count_albums
    @Test
    public void canCountFeatures() {
        OrderDomain order = new OrderDomain(Arrays.asList(newAlbum("Exile on Main St."), newAlbum("Beggars Banquet"), newAlbum("Aftermath"), newAlbum("Let it Bleed")));
        Assert.assertEquals(8, order.countFeature(( album) -> 2));
    }
}

