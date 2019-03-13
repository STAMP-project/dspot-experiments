package aima.test.core.unit.environment.map;


import aima.core.environment.map.ExtendableMap;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ciaran O'Reilly
 */
public class MapTest {
    ExtendableMap aMap;

    @Test
    public void testLocationsLinkedTo() {
        ArrayList<String> locations = new ArrayList<>();
        List<String> linkedTo;
        linkedTo = aMap.getPossibleNextLocations("A");
        locations.clear();
        locations.add("B");
        locations.add("C");
        Assert.assertTrue(((locations.containsAll(linkedTo)) && ((linkedTo.size()) == 2)));
        linkedTo = aMap.getPossibleNextLocations("B");
        locations.clear();
        locations.add("A");
        locations.add("C");
        locations.add("E");
        Assert.assertTrue(((locations.containsAll(linkedTo)) && ((linkedTo.size()) == 3)));
        linkedTo = aMap.getPossibleNextLocations("C");
        locations.clear();
        locations.add("A");
        locations.add("B");
        locations.add("D");
        Assert.assertTrue(((locations.containsAll(linkedTo)) && ((linkedTo.size()) == 3)));
        linkedTo = aMap.getPossibleNextLocations("D");
        locations.clear();
        locations.add("C");
        Assert.assertTrue(((locations.containsAll(linkedTo)) && ((linkedTo.size()) == 1)));
        linkedTo = aMap.getPossibleNextLocations("E");
        Assert.assertTrue(((linkedTo.size()) == 0));
    }

    @Test
    public void testDistances() {
        Assert.assertEquals(new Double(5), aMap.getDistance("A", "B"));
        Assert.assertEquals(new Double(6), aMap.getDistance("A", "C"));
        Assert.assertEquals(new Double(4), aMap.getDistance("B", "C"));
        Assert.assertEquals(new Double(7), aMap.getDistance("C", "D"));
        Assert.assertEquals(new Double(14), aMap.getDistance("B", "E"));
        // 
        Assert.assertEquals(new Double(5), aMap.getDistance("B", "A"));
        Assert.assertEquals(new Double(6), aMap.getDistance("C", "A"));
        Assert.assertEquals(new Double(4), aMap.getDistance("C", "B"));
        Assert.assertEquals(new Double(7), aMap.getDistance("D", "C"));
        // No distances should be returned if links not established or locations
        // do not exist
        Assert.assertNull(aMap.getDistance("X", "Z"));
        Assert.assertNull(aMap.getDistance("A", "Z"));
        Assert.assertNull(aMap.getDistance("A", "E"));
        // B->E is unidirectional so should not have opposite direction
        Assert.assertNull(aMap.getDistance("E", "B"));
    }

    @Test
    public void testRandomGeneration() {
        ArrayList<String> locations = new ArrayList<>();
        locations.add("A");
        locations.add("B");
        locations.add("C");
        locations.add("D");
        locations.add("E");
        for (int i = 0; i < (locations.size()); i++) {
            Assert.assertTrue(locations.contains(aMap.randomlyGenerateDestination()));
        }
    }
}

