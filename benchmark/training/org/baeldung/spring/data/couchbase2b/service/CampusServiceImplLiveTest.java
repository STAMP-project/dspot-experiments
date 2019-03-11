package org.baeldung.spring.data.couchbase2b.service;


import Campus.Builder;
import java.util.Set;
import org.baeldung.spring.data.couchbase.model.Campus;
import org.baeldung.spring.data.couchbase2b.MultiBucketLiveTest;
import org.baeldung.spring.data.couchbase2b.repos.CampusRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;


public class CampusServiceImplLiveTest extends MultiBucketLiveTest {
    @Autowired
    private CampusServiceImpl campusService;

    @Autowired
    private CampusRepository campusRepo;

    private final Campus Brown = Builder.newInstance().id("campus:Brown").name("Brown").location(new Point(71.4025, 51.8268)).build();

    private final Campus Cornell = Builder.newInstance().id("campus:Cornell").name("Cornell").location(new Point(76.4833, 42.4459)).build();

    private final Campus Columbia = Builder.newInstance().id("campus:Columbia").name("Columbia").location(new Point(73.9626, 40.8075)).build();

    private final Campus Dartmouth = Builder.newInstance().id("campus:Dartmouth").name("Dartmouth").location(new Point(72.2887, 43.7044)).build();

    private final Campus Harvard = Builder.newInstance().id("campus:Harvard").name("Harvard").location(new Point(71.1167, 42.377)).build();

    private final Campus Penn = Builder.newInstance().id("campus:Penn").name("Penn").location(new Point(75.1932, 39.9522)).build();

    private final Campus Princeton = Builder.newInstance().id("campus:Princeton").name("Princeton").location(new Point(74.6514, 40.334)).build();

    private final Campus Yale = Builder.newInstance().id("campus:Yale").name("Yale").location(new Point(72.9223, 41.3163)).build();

    private final Point Boston = new Point(71.0589, 42.3601);

    private final Point NewYorkCity = new Point(74.0059, 40.7128);

    @Test
    public final void givenNameHarvard_whenFindByName_thenReturnsHarvard() throws Exception {
        Set<Campus> campuses = campusService.findByName(Harvard.getName());
        Assert.assertNotNull(campuses);
        Assert.assertFalse(campuses.isEmpty());
        Assert.assertTrue(((campuses.size()) == 1));
        Assert.assertTrue(campuses.contains(Harvard));
    }

    @Test
    public final void givenHarvardId_whenFind_thenReturnsHarvard() throws Exception {
        Campus actual = campusService.find(Harvard.getId());
        Assert.assertNotNull(actual);
        Assert.assertEquals(Harvard, actual);
    }

    @Test
    public final void whenFindAll_thenReturnsAll() throws Exception {
        Set<Campus> campuses = campusService.findAll();
        Assert.assertTrue(campuses.contains(Brown));
        Assert.assertTrue(campuses.contains(Columbia));
        Assert.assertTrue(campuses.contains(Cornell));
        Assert.assertTrue(campuses.contains(Dartmouth));
        Assert.assertTrue(campuses.contains(Harvard));
        Assert.assertTrue(campuses.contains(Penn));
        Assert.assertTrue(campuses.contains(Princeton));
        Assert.assertTrue(campuses.contains(Yale));
    }

    @Test
    public final void whenFindByLocationNearBoston_thenResultContainsHarvard() throws Exception {
        Set<Campus> campuses = campusService.findByLocationNear(Boston, new org.springframework.data.geo.Distance(1, Metrics.NEUTRAL));
        Assert.assertFalse(campuses.isEmpty());
        Assert.assertTrue(campuses.contains(Harvard));
        Assert.assertFalse(campuses.contains(Columbia));
    }

    @Test
    public final void whenFindByLocationNearNewYorkCity_thenResultContainsColumbia() throws Exception {
        Set<Campus> campuses = campusService.findByLocationNear(NewYorkCity, new org.springframework.data.geo.Distance(1, Metrics.NEUTRAL));
        Assert.assertFalse(campuses.isEmpty());
        Assert.assertTrue(campuses.contains(Columbia));
        Assert.assertFalse(campuses.contains(Harvard));
    }
}

