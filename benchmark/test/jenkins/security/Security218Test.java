package jenkins.security;


import hudson.slaves.DumbSlave;
import java.io.Serializable;
import java.util.logging.Level;
import org.codehaus.groovy.runtime.MethodClosure;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.LoggerRule;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
@Issue("SECURITY-218")
public class Security218Test implements Serializable {
    @Rule
    public transient JenkinsRule j = new JenkinsRule();

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Rule
    public LoggerRule logging = new LoggerRule().record(ClassFilterImpl.class, Level.FINE);

    /**
     * JNLP slave.
     */
    private transient Process jnlp;

    /**
     * Makes sure SECURITY-218 fix also applies to slaves.
     *
     * This test is for regular dumb slave
     */
    @Test
    public void dumbSlave() throws Exception {
        check(j.createOnlineSlave());
    }

    /**
     * Makes sure SECURITY-218 fix also applies to slaves.
     *
     * This test is for JNLP slave
     */
    @Test
    public void jnlpSlave() throws Exception {
        DumbSlave s = createJnlpSlave("test");
        launchJnlpSlave(s);
        check(s);
    }

    private static class EvilReturnValue extends MasterToSlaveCallable<Object, RuntimeException> {
        @Override
        public Object call() {
            return new MethodClosure("oops", "trim");
        }
    }
}

