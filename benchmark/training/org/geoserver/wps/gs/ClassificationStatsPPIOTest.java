/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.gs;


import Statistic.MEAN;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import org.custommonkey.xmlunit.XMLAssert;
import org.geotools.process.classify.ClassificationStats;
import org.jaitools.numeric.Range;
import org.jaitools.numeric.StreamingSampleStats;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;


public class ClassificationStatsPPIOTest {
    @Test
    public void testSanity() throws Exception {
        List<Range<Double>> ranges = Arrays.asList(Range.create(0.0, true, 10.0, false), Range.create(10.0, true, 20.0, true));
        StreamingSampleStats s1 = new StreamingSampleStats();
        s1.setStatistic(MEAN);
        s1.addRange(ranges.get(0));
        s1.offer(10.0);
        StreamingSampleStats s2 = new StreamingSampleStats();
        s2.setStatistic(MEAN);
        s2.addRange(ranges.get(0));
        s2.offer(10.0);
        StreamingSampleStats[] stats = new StreamingSampleStats[]{ s1, s2 };
        ClassificationStats classStats = new org.geotools.process.vector.FeatureClassStats.Results(ranges, stats);
        ClassificationStatsPPIO ppio = new ClassificationStatsPPIO();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ppio.encode(classStats, bout);
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(bout.toByteArray()));
        Assert.assertEquals("Results", doc.getDocumentElement().getNodeName());
        XMLAssert.assertXpathExists("/Results/Class[@lowerBound='0.0']", doc);
        XMLAssert.assertXpathExists("/Results/Class[@lowerBound='10.0']", doc);
    }

    @Test
    public void testNamespacesNotNull() throws Exception {
        ContentHandler h = createNiceMock(ContentHandler.class);
        h.startElement(notNull(), notNull(), eq("Results"), anyObject());
        expectLastCall().times(1);
        replay(h);
        new ClassificationStatsPPIO().encode(newStats(), h);
        verify(h);
    }
}

