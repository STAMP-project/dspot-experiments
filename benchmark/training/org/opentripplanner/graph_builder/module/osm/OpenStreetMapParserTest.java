package org.opentripplanner.graph_builder.module.osm;


import java.io.File;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.zip.GZIPInputStream;
import org.junit.Test;
import org.opentripplanner.openstreetmap.impl.AnyFileBasedOpenStreetMapProviderImpl;
import org.opentripplanner.openstreetmap.impl.BinaryFileBasedOpenStreetMapProviderImpl;
import org.opentripplanner.openstreetmap.impl.FileBasedOpenStreetMapProviderImpl;
import org.opentripplanner.openstreetmap.impl.OpenStreetMapParser;
import org.opentripplanner.openstreetmap.impl.StreamedFileBasedOpenStreetMapProviderImpl;
import org.opentripplanner.openstreetmap.model.OSMMap;


public class OpenStreetMapParserTest {
    @Test
    public void testAFBinaryParser() throws Exception {
        AnyFileBasedOpenStreetMapProviderImpl pr = new AnyFileBasedOpenStreetMapProviderImpl();
        OSMMap map = new OSMMap();
        pr.setPath(new File(URLDecoder.decode(getClass().getResource("map.osm.pbf").getPath(), "UTF-8")));
        pr.readOSM(map);
        testParser(map);
    }

    @Test
    public void testAFXMLParser() throws Exception {
        AnyFileBasedOpenStreetMapProviderImpl pr = new AnyFileBasedOpenStreetMapProviderImpl();
        OSMMap map = new OSMMap();
        pr.setPath(new File(URLDecoder.decode(getClass().getResource("map.osm.gz").getPath(), "UTF-8")));
        pr.readOSM(map);
        testParser(map);
    }

    @Test
    public void testBinaryParser() throws Exception {
        BinaryFileBasedOpenStreetMapProviderImpl pr = new BinaryFileBasedOpenStreetMapProviderImpl();
        OSMMap map = new OSMMap();
        pr.setPath(new File(URLDecoder.decode(getClass().getResource("map.osm.pbf").getPath(), "UTF-8")));
        pr.readOSM(map);
        testParser(map);
    }

    @Test
    public void testXMLParser() throws Exception {
        FileBasedOpenStreetMapProviderImpl pr = new FileBasedOpenStreetMapProviderImpl();
        OSMMap map = new OSMMap();
        pr.setPath(new File(URLDecoder.decode(getClass().getResource("map.osm.gz").getPath(), "UTF-8")));
        pr.readOSM(map);
        testParser(map);
    }

    @Test
    public void testStreamedXMLParser() throws Exception {
        StreamedFileBasedOpenStreetMapProviderImpl pr = new StreamedFileBasedOpenStreetMapProviderImpl();
        OSMMap map = new OSMMap();
        pr.setPath(new File(URLDecoder.decode(getClass().getResource("map.osm.gz").getPath(), "UTF-8")));
        pr.readOSM(map);
        testParser(map);
    }

    @Test
    public void testBasicParser() throws Exception {
        InputStream in = new GZIPInputStream(getClass().getResourceAsStream("map.osm.gz"));
        OpenStreetMapParser parser = new OpenStreetMapParser();
        OSMMap map = new OSMMap();
        parser.parseMap(in, map);
        testParser(map);
    }
}

