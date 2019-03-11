package water;


import org.junit.Test;
import water.api.StoreView;

import static Key.BUILT_IN_KEY;


// Weeny speed-test harness.  Not intended for use with any real testing.
public class KVSpeedTest extends TestUtil {
    // Inject a million system keys and a dozen user keys around the cluster.
    // Verify that StoreView and TypeAhead remain fast.
    @Test
    public void fastGlobalKeySearch() {
        final long t_start = System.currentTimeMillis();
        final int NUMKEYS = 100;// fast test for junits

        // final int NUMKEYS=1000000;  // a million keys
        invokeOnAllNodes();
        final long t_make = System.currentTimeMillis();
        // Skip 1st 10 keys of a StoreView.  Return the default of 20 more
        // user-mode keys.
        String json = new water.api.StoreView().setAndServe("10");
        // System.out.println(json);
        final long t_view = System.currentTimeMillis();
        invokeOnAllNodes();
        final long t_remove = System.currentTimeMillis();
        // System.out.print("Make: "+((t_make  -t_start)*1.0/NUMKEYS)+"\n"+
        // "View: "+((t_view  -t_make )       )+"ms"+"\n"+
        // "Remv: "+((t_remove-t_view )*1.0/NUMKEYS)+"\n"
        // );
    }

    // Bulk inject keys on the local node without any network traffic
    private static class DoKeys extends DRemoteTask<KVSpeedTest.DoKeys> {
        private final boolean _insert;

        private final int _sysnkeys;

        private final int _usernkeys;

        DoKeys(boolean insert, int sysnkeys, int usernkeys) {
            _insert = insert;
            _sysnkeys = sysnkeys;
            _usernkeys = usernkeys;
        }

        @Override
        public void lcompute() {
            long l = 0;
            for (int i = 0; i < ((_sysnkeys) + (_usernkeys)); i++) {
                byte[] kb = new byte[(2 + 4) + 8];
                kb[0] = (i < (_sysnkeys)) ? BUILT_IN_KEY : ((byte) ('_'));// System Key vs User Key

                kb[1] = 0;
                // No replicas
                kb[2] = 'A';
                kb[3] = 'B';
                kb[4] = 'C';
                kb[5] = 'D';
                while (true) {
                    UDP.set8(kb, 6, (l++));
                    Key k = Key.make(kb);
                    if (k.home()) {
                        if (_insert)
                            DKV.put(k, new Value(k, kb), _fs);
                        else
                            DKV.remove(k, _fs);

                        break;
                    }
                } 
            }
            tryComplete();
        }

        @Override
        public void reduce(KVSpeedTest.DoKeys ignore) {
        }
    }
}

