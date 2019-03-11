package io.mycat.sequence;


import io.mycat.MycatServer;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import org.junit.Test;


/**
 * ???????
 *
 * @author Hash Zhang
 * @version 1.0
 * @unknown 00:12:05 2016/5/6
 */
public class SequenceTest {
    private Set<String> sequenceSet;

    private long startTime;

    private long endTime;

    // @Test
    // public void testIncrement(){
    // System.out.print("Increment ");
    // for (int i = 0; i < 1000000; i++) {
    // sequenceSet.add(i+"");
    // }
    // }
    // 
    @Test
    public void testUUID() {
        System.out.print("UUID ");
        for (int i = 0; i < 100; i++) {
            sequenceSet.add(UUID.randomUUID().toString());
        }
    }

    @Test
    public void testRandom() {
        TreeSet<String> treeSet = new TreeSet<>();
        System.out.println(Long.toBinaryString(Long.valueOf(((System.currentTimeMillis()) + ""))).length());
    }

    @Test
    public void testRandom2() {
        System.out.print("UUID ");
        for (int i = 0; i < 100; i++) {
            sequenceSet.add(("aaassscccddd" + i));
        }
    }

    @Test
    public void testXAXID() {
        String xid = MycatServer.getInstance().getXATXIDGLOBAL();
        System.out.println(xid);
    }
}

