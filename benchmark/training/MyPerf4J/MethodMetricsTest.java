package MyPerf4J;


import cn.myperf4j.base.MethodTag;
import cn.myperf4j.core.recorder.AccurateRecorder;
import cn.myperf4j.core.recorder.Recorders;
import cn.myperf4j.core.recorder.RoughRecorder;
import java.util.concurrent.atomic.AtomicReferenceArray;
import org.junit.Test;


/**
 * Created by LinShunkang on 2018/10/17
 */
public class MethodMetricsTest {
    @Test
    public void test() {
        Recorders recorders = new Recorders(new AtomicReferenceArray<cn.myperf4j.core.recorder.Recorder>(10));
        MethodTagMaintainer methodTagMaintainer = MethodTagMaintainer.getInstance();
        int methodId1 = methodTagMaintainer.addMethodTag(MethodTag.getGeneralInstance("Test", "test1", ""));
        recorders.setRecorder(methodId1, AccurateRecorder.getInstance(0, 100000, 50));
        int methodId2 = methodTagMaintainer.addMethodTag(MethodTag.getGeneralInstance("Test", "test1", ""));
        recorders.setRecorder(methodId2, RoughRecorder.getInstance(0, 100000));
        testRecorder(recorders, methodTagMaintainer, methodId1);
        testRecorder(recorders, methodTagMaintainer, methodId2);
    }
}

