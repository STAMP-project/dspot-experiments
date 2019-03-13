package org.opentripplanner.analyst.batch;


import com.google.common.io.Files;
import java.io.File;
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class CSVPopulationTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /**
     * Test that coordinate transforms are applied correctly
     */
    @Test
    public void testCoordinateTransform() throws Exception {
        File csvFile = temporaryFolder.newFile("coordinateTransform.csv");
        // coordinates via OpenStreetMap
        Files.write("Santa Barbara Botanical Gardens,1,6046688.23,1992920.46\n", csvFile, Charset.forName("utf-8"));
        CSVPopulation pop = new CSVPopulation();
        pop.sourceFilename = csvFile.getAbsolutePath();
        pop.skipHeaders = false;
        pop.xCol = 2;
        pop.yCol = 3;
        pop.inputCol = 1;
        pop.labelCol = 0;
        pop.crs = "EPSG:2229";// State Plane CA Zone 5, US Survey Feet

        pop.createIndividuals();
        Individual sbbg = pop.individuals.get(0);
        Assert.assertEquals(sbbg.lat, 34.45659, 1.0E-5);
        Assert.assertEquals(sbbg.lon, (-119.70843), 1.0E-5);
    }

    /**
     * Test that untransformed coordinate systems work
     */
    @Test
    public void testNoCoordinateTransform() throws Exception {
        File csvFile = temporaryFolder.newFile("noCoordinateTransform.csv");
        // coordinates via OpenStreetMap
        Files.write("Marine Science,1,-119.84330,34.40783\n", csvFile, Charset.forName("utf-8"));
        CSVPopulation pop = new CSVPopulation();
        pop.sourceFilename = csvFile.getAbsolutePath();
        pop.skipHeaders = false;
        pop.setLonCol(2);
        pop.setLatCol(3);
        pop.inputCol = 1;
        pop.labelCol = 0;
        pop.createIndividuals();
        Individual marsci = pop.individuals.get(0);
        Assert.assertEquals(marsci.lat, 34.40783, 1.0E-5);
        Assert.assertEquals(marsci.lon, (-119.8433), 1.0E-5);
        pop = new CSVPopulation();
        pop.sourceFilename = csvFile.getAbsolutePath();
        pop.skipHeaders = false;
        pop.setLonCol(2);
        pop.setLatCol(3);
        pop.inputCol = 1;
        pop.labelCol = 0;
        pop.crs = "EPSG:4326";
        pop.createIndividuals();
        marsci = pop.individuals.get(0);
        Assert.assertEquals(marsci.lat, 34.40783, 1.0E-5);
        Assert.assertEquals(marsci.lon, (-119.8433), 1.0E-5);
    }
}

