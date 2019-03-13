package hex.example;


import Vec.VectorGroup.VG_LEN1;
import org.joda.time.MutableDateTime;
import org.junit.Ignore;
import org.junit.Test;
import water.Frame;
import water.util.ArrayUtils;

import static Vec.T_NUM;
import static water.TestUtil.<init>;


@Ignore("Test DS end-to-end workflow; not intended as a junit yet")
public class WorkFlowTest extends TestUtil {
    // Test DS end-to-end workflow on a small dataset
    @Test
    public void testWorkFlowSmall() {
        String[] files = new String[]{ "bigdata/laptop/citibike-nyc/2013-10.csv" };
        testWorkFlow(files);
    }

    // Split out Days, Month, DayOfWeek and HourOfDay from Unix Epoch msec
    class TimeSplit extends MRTask<WorkFlowTest.TimeSplit> {
        public Frame doIt(Vec time) {
            return doAll(new byte[]{ T_NUM }, time).outputFrame(new String[]{ "Days" }, null);
        }

        @Override
        public void map(Chunk msec, NewChunk day) {
            for (int i = 0; i < (msec._len); i++) {
                day.addNum(((msec.at8(i)) / (((1000 * 60) * 60) * 24)));// Days since the Epoch

            }
        }
    }

    // Monster Group-By.  Count bike starts per-station per-hour per-month.
    class CountBikes extends MRTask<WorkFlowTest.CountBikes> {
        /* days */
        /* station */
        int[] _bikes;

        final int _day0;

        final int _last_day;

        int _num_sid;

        private int idx(long day, long sid) {
            return ((int) (((day - (_day0)) * (_num_sid)) + sid));
        }

        CountBikes(Vec vday) {
            _day0 = ((int) (vday.at8(0)));
            _last_day = ((int) (vday.at8((((int) (vday.length())) - 1)))) + 1;
        }

        @Override
        public void map(Chunk[] chk) {
            Chunk day = chk[0];
            Chunk sid = chk[1];
            _num_sid = sid.vec().cardinality();
            int len = chk[0]._len;
            _bikes = new int[idx(_last_day, 0)];
            for (int i = 0; i < len; i++)
                (_bikes[idx(day.at8(i), sid.at8(i))])++;

        }

        @Override
        public void reduce(WorkFlowTest.CountBikes cb) {
            ArrayUtils.add(_bikes, cb._bikes);
        }

        Frame makeFrame(Key key) {
            final int ncols = 4;
            AppendableVec[] avecs = new AppendableVec[ncols];
            NewChunk[] ncs = new NewChunk[ncols];
            Key[] keys = VG_LEN1.addVecs(ncols);
            for (int c = 0; c < (avecs.length); c++)
                avecs[c] = new AppendableVec(keys[c], Vec.T_NUM);

            Futures fs = new Futures();
            int chunknum = 0;
            MutableDateTime mdt = new MutableDateTime();// Recycle same MDT

            for (int day = _day0; day < (_last_day); day++) {
                for (int sid = 0; sid < (_num_sid); sid++) {
                    int bikecnt = _bikes[idx(day, sid)];
                    if (bikecnt == 0)
                        continue;

                    if ((ncs[0]) == null)
                        for (int c = 0; c < ncols; c++)
                            ncs[c] = new NewChunk(avecs[c], chunknum);


                    ncs[0].addNum(sid);
                    ncs[1].addNum(bikecnt);
                    long msec = day * (((1000L * 60) * 60) * 24);// msec since the Epoch

                    mdt.setMillis(msec);
                    // Set time in msec of unix epoch
                    ncs[2].addNum(((mdt.getMonthOfYear()) - 1));// Convert to 0-based from 1-based

                    ncs[3].addNum(((mdt.getDayOfWeek()) - 1));// Convert to 0-based from 1-based

                }
                if ((ncs[0]) != null) {
                    for (int c = 0; c < ncols; c++)
                        ncs[c].close(chunknum, fs);

                    chunknum++;
                    ncs[0] = null;
                }
            }
            Vec[] vecs = new Vec[ncols];
            final int rowLayout = avecs[0].compute_rowLayout();
            for (int c = 0; c < (avecs.length); c++)
                vecs[c] = avecs[c].close(rowLayout, fs);

            vecs[0].setDomain(_fr.vec(1).domain());
            vecs[1].setDomain(null);
            vecs[2].setDomain(new String[]{ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" });
            vecs[3].setDomain(new String[]{ "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" });// Order comes from Joda

            fs.blockForPending();
            Frame fr = new Frame(key, new String[]{ "Station", "bikes", "Month", "DayOfWeek" }, vecs);
            DKV.put(fr);
            return fr;
        }
    }
}

