package hex.purelogic;


import java.net.InetAddress;
import java.util.ArrayList;
import org.junit.Test;
import water.TestUtil;
import water.util.UserSpecifiedNetwork;


public class UserSpecifiedNetworkTest extends TestUtil {
    @Test
    public void test1() {
        // Check basic stuff.
        System.out.println("test1");
        ArrayList<UserSpecifiedNetwork> l;
        l = UserSpecifiedNetwork.calcArrayList("");
        UserSpecifiedNetworkTest.check((l == null));
        l = UserSpecifiedNetwork.calcArrayList("1.2.3.4");
        UserSpecifiedNetworkTest.check((l == null));
        l = UserSpecifiedNetwork.calcArrayList("1.2.3.4/");
        UserSpecifiedNetworkTest.check((l == null));
        l = UserSpecifiedNetwork.calcArrayList(" 1.2.3.4/5");
        UserSpecifiedNetworkTest.check((l == null));
        l = UserSpecifiedNetwork.calcArrayList("1.2.3.4 /5");
        UserSpecifiedNetworkTest.check((l == null));
        l = UserSpecifiedNetwork.calcArrayList("1.2.3.4/5 ");
        UserSpecifiedNetworkTest.check((l == null));
        l = UserSpecifiedNetwork.calcArrayList("1.2.3.4/a");
        UserSpecifiedNetworkTest.check((l == null));
        l = UserSpecifiedNetwork.calcArrayList("a.2.3.4/5");
        UserSpecifiedNetworkTest.check((l == null));
        l = UserSpecifiedNetwork.calcArrayList("1.a.3.4/5");
        UserSpecifiedNetworkTest.check((l == null));
        l = UserSpecifiedNetwork.calcArrayList("1.2.a.4/5");
        UserSpecifiedNetworkTest.check((l == null));
        l = UserSpecifiedNetwork.calcArrayList("1.2.3.a/5");
        UserSpecifiedNetworkTest.check((l == null));
        l = UserSpecifiedNetwork.calcArrayList("1.2.3.4/5,a.7.8.9/10");
        UserSpecifiedNetworkTest.check((l == null));
    }

    @Test
    public void test2() {
        System.out.println("test2");
        ArrayList<UserSpecifiedNetwork> l;
        l = UserSpecifiedNetwork.calcArrayList(null);
        UserSpecifiedNetworkTest.check((l != null));
        UserSpecifiedNetworkTest.check(((l.size()) == 0));
        l = UserSpecifiedNetwork.calcArrayList("1.2.3.4/5");
        UserSpecifiedNetworkTest.check((l != null));
        UserSpecifiedNetworkTest.check(((l.size()) == 1));
        l = UserSpecifiedNetwork.calcArrayList("1.2.3.4/5,10.11.12.13/14");
        UserSpecifiedNetworkTest.check((l != null));
        UserSpecifiedNetworkTest.check(((l.size()) == 2));
        l = UserSpecifiedNetwork.calcArrayList("1.2.3.4/5,10.11.12.13/14,15.16.17.18/19");
        UserSpecifiedNetworkTest.check((l != null));
        UserSpecifiedNetworkTest.check(((l.size()) == 3));
    }

    @Test
    public void test3() throws Exception {
        System.out.println("test3");
        ArrayList<UserSpecifiedNetwork> l;
        UserSpecifiedNetwork usn;
        l = UserSpecifiedNetwork.calcArrayList("0.0.0.0/0");
        UserSpecifiedNetworkTest.check((l != null));
        UserSpecifiedNetworkTest.check(((l.size()) == 1));
        usn = l.get(0);
        UserSpecifiedNetworkTest.check(usn.inetAddressOnNetwork(InetAddress.getByName("192.168.1.0")));
        UserSpecifiedNetworkTest.check(usn.inetAddressOnNetwork(InetAddress.getByName("192.168.1.100")));
        UserSpecifiedNetworkTest.check(usn.inetAddressOnNetwork(InetAddress.getByName("192.168.1.255")));
        l = UserSpecifiedNetwork.calcArrayList("10.20.30.40/32");
        UserSpecifiedNetworkTest.check((l != null));
        UserSpecifiedNetworkTest.check(((l.size()) == 1));
        usn = l.get(0);
        UserSpecifiedNetworkTest.check((!(usn.inetAddressOnNetwork(InetAddress.getByName("192.168.1.0")))));
        UserSpecifiedNetworkTest.check((!(usn.inetAddressOnNetwork(InetAddress.getByName("10.20.30.41")))));
        UserSpecifiedNetworkTest.check(usn.inetAddressOnNetwork(InetAddress.getByName("10.20.30.40")));
        l = UserSpecifiedNetwork.calcArrayList("192.168.1.0/24");
        UserSpecifiedNetworkTest.check((l != null));
        UserSpecifiedNetworkTest.check(((l.size()) == 1));
        usn = l.get(0);
        UserSpecifiedNetworkTest.check(usn.inetAddressOnNetwork(InetAddress.getByName("192.168.1.0")));
        UserSpecifiedNetworkTest.check(usn.inetAddressOnNetwork(InetAddress.getByName("192.168.1.100")));
        UserSpecifiedNetworkTest.check(usn.inetAddressOnNetwork(InetAddress.getByName("192.168.1.255")));
        UserSpecifiedNetworkTest.check((!(usn.inetAddressOnNetwork(InetAddress.getByName("192.168.2.0")))));
        UserSpecifiedNetworkTest.check((!(usn.inetAddressOnNetwork(InetAddress.getByName("191.168.1.0")))));
        l = UserSpecifiedNetwork.calcArrayList("10.255.0.0/16,192.168.1.0/24");
        UserSpecifiedNetworkTest.check((l != null));
        UserSpecifiedNetworkTest.check(((l.size()) == 2));
        usn = l.get(1);
        UserSpecifiedNetworkTest.check(usn.inetAddressOnNetwork(InetAddress.getByName("192.168.1.0")));
        UserSpecifiedNetworkTest.check(usn.inetAddressOnNetwork(InetAddress.getByName("192.168.1.100")));
        UserSpecifiedNetworkTest.check(usn.inetAddressOnNetwork(InetAddress.getByName("192.168.1.255")));
        UserSpecifiedNetworkTest.check((!(usn.inetAddressOnNetwork(InetAddress.getByName("192.168.2.0")))));
        UserSpecifiedNetworkTest.check((!(usn.inetAddressOnNetwork(InetAddress.getByName("191.168.1.0")))));
        usn = l.get(0);
        UserSpecifiedNetworkTest.check(usn.inetAddressOnNetwork(InetAddress.getByName("10.255.1.2")));
    }
}

