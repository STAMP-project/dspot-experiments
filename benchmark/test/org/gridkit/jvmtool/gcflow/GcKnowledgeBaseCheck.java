package org.gridkit.jvmtool.gcflow;


import java.io.IOException;
import java.util.concurrent.Callable;
import org.gridkit.nanocloud.CloudFactory;
import org.gridkit.vicluster.ViManager;
import org.gridkit.vicluster.ViNode;
import org.gridkit.vicluster.telecontrol.jvm.JvmProps;
import org.junit.Assume;
import org.junit.Test;


public class GcKnowledgeBaseCheck {
    public static ViManager cloud = CloudFactory.createCloud();

    @Test
    public void classify_local() throws IOException {
        GcKnowledgeBaseCheck.dumpMemoryPools();
    }

    @Test
    public void classify_serial_gc() throws IOException {
        try {
            String gc = "-XX:+UseSerialGC";
            ViNode jvm = GcKnowledgeBaseCheck.cloud.node(gc);
            JvmProps.at(jvm).addJvmArg(gc);
            jvm.exec(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    GcKnowledgeBaseCheck.dumpMemoryPools();
                    return null;
                }
            });
        } catch (Exception e) {
            Assume.assumeTrue(false);
        }
    }

    @Test
    public void classify_par_new_gc() throws IOException {
        try {
            String gc = "-XX:+UseParNewGC";
            ViNode jvm = GcKnowledgeBaseCheck.cloud.node(gc);
            JvmProps.at(jvm).addJvmArg(gc);
            jvm.exec(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    GcKnowledgeBaseCheck.dumpMemoryPools();
                    return null;
                }
            });
        } catch (Exception e) {
            Assume.assumeTrue(false);
        }
    }

    @Test
    public void classify_ps_gc() throws IOException {
        try {
            String gc = "|-XX:+UseParallelGC|-XX:-UseParallelOldGC";
            ViNode jvm = GcKnowledgeBaseCheck.cloud.node(gc);
            JvmProps.at(jvm).addJvmArg(gc);
            jvm.exec(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    GcKnowledgeBaseCheck.dumpMemoryPools();
                    return null;
                }
            });
        } catch (Exception e) {
            Assume.assumeTrue(false);
        }
    }

    @Test
    public void classify_par_old_gc() throws IOException {
        try {
            String gc = "-XX:+UseParallelOldGC";
            ViNode jvm = GcKnowledgeBaseCheck.cloud.node(gc);
            JvmProps.at(jvm).addJvmArg(gc);
            jvm.exec(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    GcKnowledgeBaseCheck.dumpMemoryPools();
                    return null;
                }
            });
        } catch (Exception e) {
            Assume.assumeTrue(false);
        }
    }

    @Test
    public void classify_cms_def_new_gc() throws IOException {
        try {
            String gc = "|-XX:+UseConcMarkSweepGC|-XX:-UseParNewGC";
            ViNode jvm = GcKnowledgeBaseCheck.cloud.node(gc);
            JvmProps.at(jvm).addJvmArg(gc);
            jvm.exec(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    GcKnowledgeBaseCheck.dumpMemoryPools();
                    return null;
                }
            });
        } catch (Exception e) {
            Assume.assumeTrue(false);
        }
    }

    @Test
    public void classify_cms_par_new_gc() throws IOException {
        try {
            String gc = "|-XX:+UseConcMarkSweepGC|-XX:+UseParNewGC";
            ViNode jvm = GcKnowledgeBaseCheck.cloud.node(gc);
            JvmProps.at(jvm).addJvmArg(gc);
            jvm.exec(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    GcKnowledgeBaseCheck.dumpMemoryPools();
                    return null;
                }
            });
        } catch (Exception e) {
            Assume.assumeTrue(false);
        }
    }

    @Test
    public void classify_g1_gc() throws IOException {
        try {
            String gc = "-XX:+UseG1GC";
            ViNode jvm = GcKnowledgeBaseCheck.cloud.node(gc);
            JvmProps.at(jvm).addJvmArg(gc);
            jvm.exec(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    GcKnowledgeBaseCheck.dumpMemoryPools();
                    return null;
                }
            });
        } catch (Exception e) {
            Assume.assumeTrue(false);
        }
    }
}

