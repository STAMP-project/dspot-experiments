package com.lyft.scoop;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;


public class ScreenScooperTest {
    private ScreenScooper screenScooper;

    private Scoop rootScoop;

    private ScreenScoopFactory screenScoopFactory;

    // [ ] - > [  ]
    @Test
    public void createScoopFromEmptyPathToEmptyPath() {
        Scoop scoop = screenScooper.create(rootScoop, null, Collections.<Screen>emptyList(), Collections.<Screen>emptyList());
        Assert.assertFalse(rootScoop.isDestroyed());
        Assert.assertNull(scoop);
    }

    // [ ] - > [ A ]
    @Test
    public void createScoopFromEmptyPathToAPath() {
        List<Screen> toPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA());
        Scoop aScoop = screenScooper.create(rootScoop, null, Collections.<Screen>emptyList(), toPath);
        Assert.assertFalse(rootScoop.isDestroyed());
        Assert.assertFalse(aScoop.isDestroyed());
        assertEquals(rootScoop, aScoop.getParent());
    }

    // [ ] - > [ A, B ]
    @Test
    public void createScoopFromEmptyPathToABPath() {
        List<Screen> toPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA(), new ScreenScooperTest.ScreenB());
        Scoop bScoop = screenScooper.create(rootScoop, null, Collections.<Screen>emptyList(), toPath);
        Assert.assertFalse(rootScoop.isDestroyed());
        Assert.assertFalse(bScoop.getParent().isDestroyed());
        Assert.assertFalse(bScoop.isDestroyed());
        assertEquals(rootScoop, bScoop.getParent().getParent());
    }

    // [ A ] - > [ A, B]
    @Test
    public void createScoopFromAPathToABPath() {
        List<Screen> fromPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA());
        List<Screen> toPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA(), new ScreenScooperTest.ScreenB());
        Scoop aScoop = screenScoopFactory.createScreenScoop(new ScreenScooperTest.ScreenA(), rootScoop);
        Scoop bScoop = screenScooper.create(rootScoop, aScoop, fromPath, toPath);
        Assert.assertFalse(rootScoop.isDestroyed());
        Assert.assertFalse(aScoop.isDestroyed());
        Assert.assertFalse(bScoop.isDestroyed());
        assertEquals(aScoop, bScoop.getParent());
    }

    // [ A ] - > [ A, B]
    @Test
    public void createScoopFromAPathToABPathWithNullCurrentScoop() {
        List<Screen> fromPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA());
        List<Screen> toPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA(), new ScreenScooperTest.ScreenB());
        Scoop bScoop = screenScooper.create(rootScoop, null, fromPath, toPath);
        Assert.assertFalse(rootScoop.isDestroyed());
        Assert.assertFalse(bScoop.isDestroyed());
        assertEquals(rootScoop, bScoop.getParent().getParent());
    }

    // [ A, B ] - > [ A ]
    @Test
    public void createScoopFromABPathToAPath() {
        List<Screen> fromPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA(), new ScreenScooperTest.ScreenB());
        List<Screen> toPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA());
        Scoop aScoop = screenScoopFactory.createScreenScoop(new ScreenScooperTest.ScreenA(), rootScoop);
        Scoop bScoop = screenScoopFactory.createScreenScoop(new ScreenScooperTest.ScreenB(), aScoop);
        Scoop scoop = screenScooper.create(rootScoop, bScoop, fromPath, toPath);
        Assert.assertFalse(rootScoop.isDestroyed());
        Assert.assertFalse(aScoop.isDestroyed());
        assertTrue(bScoop.isDestroyed());
        assertEquals(aScoop, scoop);
    }

    // [ A, B ] - > [ A ]
    @Test
    public void createScoopFromABPathToAPathNullCurrentScoop() {
        List<Screen> fromPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA(), new ScreenScooperTest.ScreenB());
        List<Screen> toPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA());
        Scoop scoop = screenScooper.create(rootScoop, null, fromPath, toPath);
        Assert.assertFalse(rootScoop.isDestroyed());
        assertEquals(rootScoop, scoop.getParent());
    }

    // [ A, B ] - > [  ]
    @Test
    public void createScoopFromABPathToEmptyPath() {
        List<Screen> fromPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA(), new ScreenScooperTest.ScreenB());
        List<Screen> toPath = Arrays.<Screen>asList();
        Scoop aScoop = screenScoopFactory.createScreenScoop(new ScreenScooperTest.ScreenA(), rootScoop);
        Scoop bScoop = screenScoopFactory.createScreenScoop(new ScreenScooperTest.ScreenB(), aScoop);
        Scoop scoop = screenScooper.create(rootScoop, bScoop, fromPath, toPath);
        Assert.assertFalse(rootScoop.isDestroyed());
        assertTrue(aScoop.isDestroyed());
        assertTrue(bScoop.isDestroyed());
        Assert.assertNull(scoop);
    }

    // [ A, B ] - > [  ]
    @Test
    public void createScoopFromABPathToEmptyPathNullCurrentScoop() {
        List<Screen> fromPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA(), new ScreenScooperTest.ScreenB());
        List<Screen> toPath = Arrays.<Screen>asList();
        Scoop scoop = screenScooper.create(rootScoop, null, fromPath, toPath);
        Assert.assertFalse(rootScoop.isDestroyed());
        Assert.assertNull(scoop);
    }

    // [ A, B, C ] - > [ A ]
    @Test
    public void createScoopFromABCPathToAPath() {
        List<Screen> fromPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA(), new ScreenScooperTest.ScreenB(), new ScreenScooperTest.ScreenC());
        List<Screen> toPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA());
        Scoop aScoop = screenScoopFactory.createScreenScoop(new ScreenScooperTest.ScreenA(), rootScoop);
        Scoop bScoop = screenScoopFactory.createScreenScoop(new ScreenScooperTest.ScreenB(), aScoop);
        Scoop cScoop = screenScoopFactory.createScreenScoop(new ScreenScooperTest.ScreenC(), bScoop);
        Scoop scoop = screenScooper.create(rootScoop, cScoop, fromPath, toPath);
        assertTrue(bScoop.isDestroyed());
        assertTrue(cScoop.isDestroyed());
        Assert.assertFalse(aScoop.isDestroyed());
        Assert.assertFalse(rootScoop.isDestroyed());
        assertEquals(aScoop, scoop);
    }

    // [ A ] - > [ B ] // [ B ] - > [ C ] // [ C ] - > [ C ]
    @Test
    public void createScoopFromABPathToBCPathToCCPath() {
        List<Screen> fromPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA());
        List<Screen> toPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenB());
        Scoop aScoop = screenScoopFactory.createScreenScoop(new ScreenScooperTest.ScreenA(), rootScoop);
        Scoop bScoop = screenScooper.create(rootScoop, aScoop, fromPath, toPath);
        assertNotEquals(aScoop, bScoop);
        fromPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenB());
        toPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenC());
        Scoop cScoop = screenScooper.create(rootScoop, bScoop, fromPath, toPath);
        assertNotEquals(bScoop, cScoop);
        fromPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenC());
        toPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenC());
        Scoop scoop = screenScooper.create(rootScoop, cScoop, fromPath, toPath);
        assertNotEquals(bScoop, scoop);
    }

    // [ A, B, C ] - > [ A ]
    @Test
    public void createScoopFromABCPathToAPathNullCurrentScoop() {
        List<Screen> fromPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA(), new ScreenScooperTest.ScreenB(), new ScreenScooperTest.ScreenC());
        List<Screen> toPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA());
        Scoop scoop = screenScooper.create(rootScoop, null, fromPath, toPath);
        Assert.assertFalse(scoop.isDestroyed());
        Assert.assertFalse(rootScoop.isDestroyed());
        assertEquals(rootScoop, scoop.getParent());
    }

    // [ A ] - > [ A, B, C ]
    @Test
    public void createScoopFromAPathToABCPath() {
        List<Screen> fromPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA());
        List<Screen> toPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA(), new ScreenScooperTest.ScreenB(), new ScreenScooperTest.ScreenC());
        Scoop aScoop = screenScoopFactory.createScreenScoop(new ScreenScooperTest.ScreenA(), rootScoop);
        Scoop cScoop = screenScooper.create(rootScoop, aScoop, fromPath, toPath);
        Assert.assertFalse(cScoop.getParent().isDestroyed());
        Assert.assertFalse(cScoop.isDestroyed());
        Assert.assertFalse(aScoop.isDestroyed());
        Assert.assertFalse(rootScoop.isDestroyed());
        assertEquals(aScoop, cScoop.getParent().getParent());
    }

    // [ A ] - > [ A, B, C ]
    @Test
    public void createScoopFromAPathToABCPathNullCurrentScoop() {
        List<Screen> fromPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA());
        List<Screen> toPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA(), new ScreenScooperTest.ScreenB(), new ScreenScooperTest.ScreenC());
        Scoop cScoop = screenScooper.create(rootScoop, null, fromPath, toPath);
        // A
        Assert.assertFalse(cScoop.getParent().getParent().isDestroyed());
        // B
        Assert.assertFalse(cScoop.getParent().isDestroyed());
        Assert.assertFalse(cScoop.isDestroyed());
        Assert.assertFalse(rootScoop.isDestroyed());
        assertEquals(rootScoop, cScoop.getParent().getParent().getParent());
    }

    // [ A ] - > [ B ]
    @Test
    public void createScoopFromAPathToBPath() {
        List<Screen> fromPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA());
        List<Screen> toPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenB());
        Scoop aScoop = screenScoopFactory.createScreenScoop(new ScreenScooperTest.ScreenA(), rootScoop);
        Scoop bScoop = screenScooper.create(rootScoop, aScoop, fromPath, toPath);
        assertTrue(aScoop.isDestroyed());
        Assert.assertFalse(bScoop.isDestroyed());
        Assert.assertFalse(rootScoop.isDestroyed());
        assertEquals(rootScoop, bScoop.getParent());
    }

    // [ A ] - > [ B ]
    @Test
    public void createScoopFromAPathToBPathNullCurrentScoop() {
        List<Screen> fromPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA());
        List<Screen> toPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenB());
        Scoop bScoop = screenScooper.create(rootScoop, null, fromPath, toPath);
        Assert.assertFalse(bScoop.isDestroyed());
        Assert.assertFalse(rootScoop.isDestroyed());
        assertEquals(rootScoop, bScoop.getParent());
    }

    // [ A, B ] - > [ A, C ]
    @Test
    public void createScoopFromABPathToACPath() {
        List<Screen> fromPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA(), new ScreenScooperTest.ScreenB());
        List<Screen> toPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA(), new ScreenScooperTest.ScreenC());
        Scoop aScoop = screenScoopFactory.createScreenScoop(new ScreenScooperTest.ScreenA(), rootScoop);
        Scoop bScoop = screenScoopFactory.createScreenScoop(new ScreenScooperTest.ScreenB(), aScoop);
        Scoop cScoop = screenScooper.create(rootScoop, bScoop, fromPath, toPath);
        Assert.assertFalse(rootScoop.isDestroyed());
        Assert.assertFalse(aScoop.isDestroyed());
        assertTrue(bScoop.isDestroyed());
        Assert.assertFalse(cScoop.isDestroyed());
        assertEquals(aScoop, cScoop.getParent());
    }

    // [ A, B ] - > [ A, C ]
    @Test
    public void createScoopFromABPathToACPathNullCurrentScoop() {
        List<Screen> fromPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA(), new ScreenScooperTest.ScreenB());
        List<Screen> toPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA(), new ScreenScooperTest.ScreenC());
        Scoop cScoop = screenScooper.create(rootScoop, null, fromPath, toPath);
        Assert.assertFalse(rootScoop.isDestroyed());
        // A
        Assert.assertFalse(cScoop.getParent().isDestroyed());
        Assert.assertFalse(cScoop.isDestroyed());
        assertEquals(rootScoop, cScoop.getParent().getParent());
    }

    // [ A, B ] - > [ C, D ]
    @Test
    public void createScoopFromABPathToCDPath() {
        List<Screen> fromPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA(), new ScreenScooperTest.ScreenB());
        List<Screen> toPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenC(), new ScreenScooperTest.ScreenD());
        Scoop aScoop = screenScoopFactory.createScreenScoop(new ScreenScooperTest.ScreenA(), rootScoop);
        Scoop bScoop = screenScoopFactory.createScreenScoop(new ScreenScooperTest.ScreenB(), aScoop);
        Scoop dScoop = screenScooper.create(rootScoop, bScoop, fromPath, toPath);
        Assert.assertFalse(rootScoop.isDestroyed());
        assertTrue(aScoop.isDestroyed());
        assertTrue(bScoop.isDestroyed());
        Assert.assertFalse(dScoop.getParent().isDestroyed());
        Assert.assertFalse(dScoop.isDestroyed());
        assertEquals(rootScoop, dScoop.getParent().getParent());
    }

    // [ A, B ] - > [ C, D ]
    @Test
    public void createScoopFromABPathToCDPathNullCurrentScoop() {
        List<Screen> fromPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA(), new ScreenScooperTest.ScreenB());
        List<Screen> toPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenC(), new ScreenScooperTest.ScreenD());
        Scoop dScoop = screenScooper.create(rootScoop, null, fromPath, toPath);
        Assert.assertFalse(rootScoop.isDestroyed());
        Assert.assertFalse(dScoop.getParent().isDestroyed());
        Assert.assertFalse(dScoop.isDestroyed());
        assertEquals(rootScoop, dScoop.getParent().getParent());
    }

    // [ A, B ] - > [ A, B, C ]
    @Test
    public void createScoopFromABPathToABCPath() {
        List<Screen> fromPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA(), new ScreenScooperTest.ScreenB());
        List<Screen> toPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA(), new ScreenScooperTest.ScreenB(), new ScreenScooperTest.ScreenC());
        Scoop aScoop = screenScoopFactory.createScreenScoop(new ScreenScooperTest.ScreenA(), rootScoop);
        Scoop bScoop = screenScoopFactory.createScreenScoop(new ScreenScooperTest.ScreenB(), aScoop);
        Scoop cScoop = screenScooper.create(rootScoop, bScoop, fromPath, toPath);
        Assert.assertFalse(rootScoop.isDestroyed());
        Assert.assertFalse(aScoop.isDestroyed());
        Assert.assertFalse(bScoop.isDestroyed());
        Assert.assertFalse(cScoop.isDestroyed());
        assertEquals(rootScoop, cScoop.getParent().getParent().getParent());
    }

    // [ A, B ] - > [ A, B, C ]
    @Test
    public void createScoopFromABPathToABCPathNullCurrentScoop() {
        List<Screen> fromPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA(), new ScreenScooperTest.ScreenB());
        List<Screen> toPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA(), new ScreenScooperTest.ScreenB(), new ScreenScooperTest.ScreenC());
        Scoop cScoop = screenScooper.create(rootScoop, null, fromPath, toPath);
        Assert.assertFalse(rootScoop.isDestroyed());
        Assert.assertFalse(cScoop.getParent().getParent().isDestroyed());
        Assert.assertFalse(cScoop.getParent().isDestroyed());
        Assert.assertFalse(cScoop.isDestroyed());
        assertEquals(rootScoop, cScoop.getParent().getParent().getParent());
    }

    // [ A, B, C ] - > [ A, B, D ]
    @Test
    public void createScoopFromABCPathToABDPath() {
        List<Screen> fromPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA(), new ScreenScooperTest.ScreenB(), new ScreenScooperTest.ScreenC());
        List<Screen> toPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA(), new ScreenScooperTest.ScreenB(), new ScreenScooperTest.ScreenD());
        Scoop aScoop = screenScoopFactory.createScreenScoop(new ScreenScooperTest.ScreenA(), rootScoop);
        Scoop bScoop = screenScoopFactory.createScreenScoop(new ScreenScooperTest.ScreenB(), aScoop);
        Scoop cScoop = screenScoopFactory.createScreenScoop(new ScreenScooperTest.ScreenC(), bScoop);
        Scoop dScoop = screenScooper.create(rootScoop, cScoop, fromPath, toPath);
        Assert.assertFalse(rootScoop.isDestroyed());
        Assert.assertFalse(aScoop.isDestroyed());
        Assert.assertFalse(bScoop.isDestroyed());
        Assert.assertFalse(dScoop.isDestroyed());
        assertTrue(cScoop.isDestroyed());
        assertEquals(rootScoop, dScoop.getParent().getParent().getParent());
    }

    // [ A, B, C ] - > [ A, B, D ]
    @Test
    public void createScoopFromABCPathToABDPathNullCurrentScoop() {
        List<Screen> fromPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA(), new ScreenScooperTest.ScreenB(), new ScreenScooperTest.ScreenC());
        List<Screen> toPath = Arrays.<Screen>asList(new ScreenScooperTest.ScreenA(), new ScreenScooperTest.ScreenB(), new ScreenScooperTest.ScreenD());
        Scoop dScoop = screenScooper.create(rootScoop, null, fromPath, toPath);
        Assert.assertFalse(rootScoop.isDestroyed());
        Assert.assertFalse(dScoop.getParent().getParent().isDestroyed());
        Assert.assertFalse(dScoop.getParent().isDestroyed());
        Assert.assertFalse(dScoop.isDestroyed());
        assertEquals(rootScoop, dScoop.getParent().getParent().getParent());
    }

    static class ScreenA extends Screen {}

    static class ScreenB extends Screen {}

    static class ScreenC extends Screen {}

    static class ScreenD extends Screen {}
}

