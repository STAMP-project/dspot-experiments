/**
 * Copyright 2000-2006 DFKI GmbH.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of MARY TTS.
 *
 * MARY TTS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package marytts.unitselection.data;


import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import marytts.exceptions.MaryConfigurationException;
import marytts.util.Pair;
import marytts.util.data.Datagram;
import org.junit.Assert;
import org.junit.Test;


/**
 * Provides the actual timeline test case for the timeline reading/writing symmetry.
 */
public class TimelineTest {
    private static TestableTimelineReader tlr;

    private static String hdrContents;

    private static int NUMDATAGRAMS;

    private static int MAXDATAGRAMBYTESIZE;

    private static int MAXDATAGRAMDURATION;

    private static int sampleRate;

    private static Datagram[] origDatagrams;

    private static final String tlFileName = "timelineTest.bin";

    @Test
    public void procHeader() throws IOException {
        /* Check the procHeader */
        Assert.assertEquals("The procHeader is out of sync.", getProcHeaderContents(), TimelineTest.hdrContents);
    }

    @Test
    public void numDatagrams() throws IOException {
        /* Check the number of datagrams */
        Assert.assertEquals("numDatagrams is out of sync.", getNumDatagrams(), TimelineTest.NUMDATAGRAMS);
    }

    @Test
    public void testSkip() throws IOException {
        System.out.println("READ INDEX:");
        getIndex().print();
        /* Testing skip */
        System.out.println("Testing skip...");
        long timeNow = 0;
        long timeBefore = 0;
        Pair<ByteBuffer, Long> p = TimelineTest.tlr.getByteBufferAtTime(timeNow);
        ByteBuffer bb = p.getFirst();
        int byteNow = bb.position();
        int byteBefore = 0;
        for (int i = 0; i < (TimelineTest.NUMDATAGRAMS); i++) {
            timeBefore = timeNow;
            byteBefore = byteNow;
            try {
                long skippedDuration = TimelineTest.tlr.skipNextDatagram(bb);
                timeNow += skippedDuration;
            } catch (BufferUnderflowException e) {
                // reached end of byte buffer
                break;
            }
            byteNow = bb.position();
            Assert.assertEquals((("Skipping fails on datagram [" + i) + "]."), (((long) (TimelineTest.origDatagrams[i].getLength())) + 12L), (byteNow - byteBefore));
            Assert.assertEquals((("Time is out of sync after skipping datagram [" + i) + "]."), TimelineTest.origDatagrams[i].getDuration(), (timeNow - timeBefore));
        }
        /* Testing the EOF trap for skip */
        try {
            TimelineTest.tlr.skipNextDatagram(bb);
            Assert.fail("should have thrown BufferUnderflowException to indicate end of byte buffer");
        } catch (BufferUnderflowException e) {
            // OK, expected
        }
    }

    @Test
    public void testGet() throws IOException {
        /* Testing get */
        System.out.println("Testing get...");
        Datagram[] readDatagrams = new Datagram[TimelineTest.NUMDATAGRAMS];
        Pair<ByteBuffer, Long> p = TimelineTest.tlr.getByteBufferAtTime(0);
        ByteBuffer bb = p.getFirst();
        for (int i = 0; i < (TimelineTest.NUMDATAGRAMS); i++) {
            readDatagrams[i] = TimelineTest.tlr.getNextDatagram(bb);
            if ((readDatagrams[i]) == null) {
                System.err.println((("Could read " + i) + " datagrams"));
                break;
            }
            Assert.assertTrue((("Datagram [" + i) + "] is out of sync."), TimelineTest.areEqual(TimelineTest.origDatagrams[i].getData(), readDatagrams[i].getData()));
            Assert.assertEquals((("Time for datagram [" + i) + "] is out of sync."), TimelineTest.origDatagrams[i].getDuration(), readDatagrams[i].getDuration());
        }
        /* Testing the EOF trap for get */
        Assert.assertEquals(null, TimelineTest.tlr.getNextDatagram(bb));
    }

    @Test
    public void gotoTime1() throws IOException {
        // setup
        final int testIdx = (TimelineTest.NUMDATAGRAMS) / 2;
        long onTime = getTimeOfIndex(testIdx);
        // exercise
        ByteBuffer bb = TimelineTest.tlr.getByteBufferAtTime(onTime).getFirst();
        Datagram d = TimelineTest.tlr.getNextDatagram(bb);
        // verify
        Assert.assertEquals(TimelineTest.origDatagrams[testIdx], d);
    }

    @Test
    public void gotoTime2() throws IOException {
        // setup
        final int testIdx = (TimelineTest.NUMDATAGRAMS) / 2;
        long onTime = getTimeOfIndex(testIdx);
        long afterTime = onTime + (TimelineTest.origDatagrams[testIdx].getDuration());
        long midTime = onTime + ((afterTime - onTime) / 2);
        // exercise
        ByteBuffer bb = TimelineTest.tlr.getByteBufferAtTime(midTime).getFirst();
        Datagram d = TimelineTest.tlr.getNextDatagram(bb);
        // verify
        Assert.assertEquals(TimelineTest.origDatagrams[testIdx], d);
    }

    @Test
    public void gotoTime3() throws IOException {
        // setup
        final int testIdx = (TimelineTest.NUMDATAGRAMS) / 2;
        long onTime = getTimeOfIndex(testIdx);
        long afterTime = onTime + (TimelineTest.origDatagrams[testIdx].getDuration());
        // exercise
        ByteBuffer bb = TimelineTest.tlr.getByteBufferAtTime(afterTime).getFirst();
        Datagram d = TimelineTest.tlr.getNextDatagram(bb);
        // verify
        Assert.assertEquals(TimelineTest.origDatagrams[(testIdx + 1)], d);
    }

    @Test
    public void getDatagrams1() throws IOException {
        // setup
        final int testIdx = (TimelineTest.NUMDATAGRAMS) / 2;
        long onTime = getTimeOfIndex(testIdx);
        Datagram[] D = null;
        long span = TimelineTest.origDatagrams[testIdx].getDuration();
        long[] offset = new long[1];
        // exercise
        D = TimelineTest.tlr.getDatagrams(onTime, span, TimelineTest.sampleRate, offset);
        // verify
        Assert.assertEquals(1, D.length);
        Assert.assertEquals(TimelineTest.origDatagrams[testIdx], D[0]);
        Assert.assertEquals(0L, offset[0]);
    }

    @Test
    public void getDatagrams2() throws IOException {
        // setup
        final int testIdx = (TimelineTest.NUMDATAGRAMS) / 2;
        long onTime = getTimeOfIndex(testIdx);
        Datagram[] D = null;
        long span = (TimelineTest.origDatagrams[testIdx].getDuration()) / 2;
        long[] offset = new long[1];
        // exercise
        D = TimelineTest.tlr.getDatagrams(onTime, span, TimelineTest.sampleRate, offset);
        // verify
        Assert.assertEquals(1, D.length);
        Assert.assertEquals(TimelineTest.origDatagrams[testIdx], D[0]);
        Assert.assertEquals(0L, offset[0]);
    }

    @Test
    public void getDatagrams3() throws IOException {
        // setup
        final int testIdx = (TimelineTest.NUMDATAGRAMS) / 2;
        long onTime = getTimeOfIndex(testIdx);
        long afterTime = onTime + (TimelineTest.origDatagrams[testIdx].getDuration());
        long midTime = onTime + ((afterTime - onTime) / 2);
        Datagram[] D = null;
        long span = (TimelineTest.origDatagrams[testIdx].getDuration()) / 2;
        // exercise
        D = TimelineTest.tlr.getDatagrams(midTime, span, TimelineTest.sampleRate);
        // verify
        Assert.assertEquals(1, D.length);
        Assert.assertEquals(TimelineTest.origDatagrams[testIdx], D[0]);
    }

    @Test
    public void getDatagrams4() throws IOException {
        // setup
        final int testIdx = (TimelineTest.NUMDATAGRAMS) / 2;
        long onTime = getTimeOfIndex(testIdx);
        Datagram[] D = null;
        long span = (TimelineTest.origDatagrams[testIdx].getDuration()) + 1;
        long[] offset = new long[1];
        // exercise
        D = TimelineTest.tlr.getDatagrams(onTime, span, TimelineTest.sampleRate, offset);
        // verify
        Assert.assertEquals(2, D.length);
        Assert.assertEquals(TimelineTest.origDatagrams[testIdx], D[0]);
        Assert.assertEquals(TimelineTest.origDatagrams[(testIdx + 1)], D[1]);
        Assert.assertEquals(0L, offset[0]);
    }

    @Test
    public void getDatagrams5() throws IOException {
        // setup
        final int testIdx = (TimelineTest.NUMDATAGRAMS) / 2;
        long onTime = getTimeOfIndex(testIdx);
        Datagram[] D = null;
        long span = (TimelineTest.origDatagrams[testIdx].getDuration()) + (TimelineTest.origDatagrams[(testIdx + 1)].getDuration());
        long[] offset = new long[1];
        // exercise
        D = TimelineTest.tlr.getDatagrams(onTime, span, TimelineTest.sampleRate, offset);
        // verify
        Assert.assertEquals(2, D.length);
        Assert.assertEquals(TimelineTest.origDatagrams[testIdx], D[0]);
        Assert.assertEquals(TimelineTest.origDatagrams[(testIdx + 1)], D[1]);
        Assert.assertEquals(0L, offset[0]);
    }

    @Test
    public void getDatagrams6() throws IOException {
        // setup
        final int testIdx = (TimelineTest.NUMDATAGRAMS) / 2;
        long onTime = getTimeOfIndex(testIdx);
        Datagram[] D = null;
        long span = (TimelineTest.origDatagrams[testIdx].getDuration()) + (TimelineTest.origDatagrams[(testIdx + 1)].getDuration());
        long[] offset = new long[1];
        // exercise
        D = TimelineTest.tlr.getDatagrams((onTime + 1), span, TimelineTest.sampleRate, offset);
        // verify
        Assert.assertEquals(((((((((((((("textIdx=" + testIdx) + ", span=") + span) + ", dur[") + testIdx) + "]=") + (TimelineTest.origDatagrams[testIdx].getDuration())) + ", dur[") + (testIdx + 1)) + "]=") + (TimelineTest.origDatagrams[(testIdx + 1)].getDuration())) + ", offset=") + (offset[0])), 3, D.length);
        Assert.assertEquals(TimelineTest.origDatagrams[testIdx], D[0]);
        Assert.assertEquals(TimelineTest.origDatagrams[(testIdx + 1)], D[1]);
        Assert.assertEquals(TimelineTest.origDatagrams[(testIdx + 2)], D[2]);
        Assert.assertEquals(1L, offset[0]);
    }

    @Test
    public void getDatagrams7() throws IOException {
        // setup
        final int testIdx = (TimelineTest.NUMDATAGRAMS) / 2;
        long onTime = getTimeOfIndex(testIdx);
        long afterTime = onTime + (TimelineTest.origDatagrams[testIdx].getDuration());
        long midTime = onTime + ((afterTime - onTime) / 2);
        Datagram[] D = null;
        long dur = TimelineTest.origDatagrams[testIdx].getDuration();
        long span = (dur - (dur / 2)) + 1;
        long[] offset = new long[1];
        // exercise
        D = TimelineTest.tlr.getDatagrams(midTime, span, TimelineTest.sampleRate, offset);
        // verify
        Assert.assertEquals(2, D.length);
        Assert.assertEquals(TimelineTest.origDatagrams[testIdx], D[0]);
        Assert.assertEquals(TimelineTest.origDatagrams[(testIdx + 1)], D[1]);
        Assert.assertEquals((dur / 2), offset[0]);
    }

    @Test
    public void otherSampleRate() throws IOException {
        // setup
        final int testIdx = (TimelineTest.NUMDATAGRAMS) / 2;
        long onTime = getTimeOfIndex(testIdx);
        Datagram[] D = null;
        long dur = TimelineTest.origDatagrams[testIdx].getDuration();
        long span = dur;
        // exercise
        D = TimelineTest.tlr.getDatagrams((onTime * 2), (span * 2), ((TimelineTest.sampleRate) / 2));
        // verify
        Assert.assertEquals(1, D.length);
        Assert.assertTrue(TimelineTest.areEqual(D[0].getData(), TimelineTest.origDatagrams[testIdx].getData()));
        Assert.assertTrue(((D[0].getDuration()) != (TimelineTest.origDatagrams[testIdx].getDuration())));
    }

    @Test
    public void getLastDatagram() throws IOException, MaryConfigurationException {
        long totalDur = getTotalDuration();
        Assert.assertTrue((totalDur > 0));
        Datagram d = getDatagram((totalDur - 1));
        Assert.assertTrue((d != null));
        long dur = d.getDuration();
        d = TimelineTest.tlr.getDatagram((totalDur - dur));
        Assert.assertTrue((d != null));
    }

    @Test
    public void getLastDatagrams() throws IOException, MaryConfigurationException {
        long totalDur = getTotalDuration();
        Assert.assertTrue((totalDur > 0));
        Datagram[] ds = getDatagrams((totalDur - 1), 1);
        Assert.assertTrue((ds != null));
        Assert.assertTrue(((ds.length) == 1));
        ds = TimelineTest.tlr.getDatagrams((totalDur - 1), 2);
        Assert.assertTrue((ds != null));
        Assert.assertTrue(((ds.length) == 1));
    }

    @Test
    public void cannotGetAfterLastDatagram() throws IOException, MaryConfigurationException {
        long totalDur = getTotalDuration();
        Assert.assertTrue((totalDur > 0));
        try {
            Datagram d = getDatagram(totalDur);
            Assert.fail("Should have thrown a BufferUnderflowException");
        } catch (BufferUnderflowException e) {
            // OK, expected
        }
    }

    @Test
    public void cannotGetAfterLastDatagrams() throws IOException, MaryConfigurationException {
        long totalDur = getTotalDuration();
        Assert.assertTrue((totalDur > 0));
        try {
            Datagram[] ds = getDatagrams(totalDur, 1);
            Assert.fail("Should have thrown a BufferUnderflowException");
        } catch (BufferUnderflowException e) {
            // OK, expected
        }
    }

    @Test
    public void canReadLongDatagram() throws IOException, MaryConfigurationException {
        // setup custom fixture for this method
        TimelineReader timeline = new TimelineReader(TimelineTest.tlFileName, false);// do not try memory mapping

        // exercise
        Datagram d = timeline.getDatagram(0);
        // verify
        Assert.assertEquals(TimelineTest.origDatagrams[0].getLength(), d.getLength());
    }

    @Test
    public void canReadLongDatagrams1() throws IOException, MaryConfigurationException {
        // setup custom fixture for this method
        TimelineReader timeline = new TimelineReader(TimelineTest.tlFileName, false);// do not try memory mapping

        // exercise
        Datagram[] ds = timeline.getDatagrams(0, ((TimelineTest.origDatagrams[0].getDuration()) + 1));
        // verify
        Assert.assertEquals(2, ds.length);
        Assert.assertEquals(TimelineTest.origDatagrams[0].getLength(), ds[0].getLength());
    }

    @Test
    public void canReadLongDatagrams2() throws IOException, MaryConfigurationException {
        // setup custom fixture for this method
        TimelineReader timeline = new TimelineReader(TimelineTest.tlFileName, false);// do not try memory mapping

        // exercise
        Datagram[] ds = timeline.getDatagrams(TimelineTest.origDatagrams[0].getDuration(), ((TimelineTest.origDatagrams[1].getDuration()) + 1));
        // verify
        Assert.assertEquals(2, ds.length);
        Assert.assertEquals(TimelineTest.origDatagrams[1].getLength(), ds[0].getLength());
    }
}

