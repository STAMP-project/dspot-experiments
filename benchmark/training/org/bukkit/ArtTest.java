package org.bukkit;


import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ArtTest {
    @Test(expected = IllegalArgumentException.class)
    public void getByNullName() {
        Art.getByName(null);
    }

    @Test
    public void getById() {
        for (Art art : Art.values()) {
            Assert.assertThat(Art.getById(art.getId()), CoreMatchers.is(art));
        }
    }

    @Test
    public void getByName() {
        for (Art art : Art.values()) {
            Assert.assertThat(Art.getByName(art.toString()), CoreMatchers.is(art));
        }
    }

    @Test
    public void dimensionSanityCheck() {
        for (Art art : Art.values()) {
            Assert.assertThat(art.getBlockHeight(), CoreMatchers.is(Matchers.greaterThan(0)));
            Assert.assertThat(art.getBlockWidth(), CoreMatchers.is(Matchers.greaterThan(0)));
        }
    }

    @Test
    public void getByNameWithMixedCase() {
        Art subject = Art.values()[0];
        String name = subject.toString().replace('E', 'e');
        Assert.assertThat(Art.getByName(name), CoreMatchers.is(subject));
    }
}

