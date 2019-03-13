package org.jboss.as.test.manualmode.jca.connectionlistener;


import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Simple connection listener {@link ConnectionListener} test case.
 *
 * @author <a href="mailto:hsvabek@redhat.com">Hynek Svabek</a>
 */
@RunWith(Arquillian.class)
@ServerSetup({ AbstractTestsuite.TestCaseSetup.class })
public class ConnectionListenerTestCase extends AbstractTestsuite {
    private static final Logger log = Logger.getLogger(ConnectionListenerTestCase.class);

    @ArquillianResource
    private ContainerController controller;

    @ArquillianResource
    private Deployer deployer;

    /**
     * Test: insert record in transaction and then rollback
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConnListenerWithRollback() throws Exception {
        testConnListenerTest(AbstractTestsuite.DEP_1, false);
    }

    /**
     * Test: insert record in transaction and then rollback
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConnListenerXaWithRollback() throws Exception {
        testConnListenerTest(AbstractTestsuite.DEP_1_XA, true);
    }

    /**
     * Test: insert record in transaction and then rollback
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConnListener2WithRollback() throws Exception {
        testConnListener2Test(AbstractTestsuite.DEP_2, false);
    }

    /**
     * Test: insert record in transaction and then rollback
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConnListener2XaWithRollback() throws Exception {
        testConnListener2Test(AbstractTestsuite.DEP_2_XA, true);
    }

    /**
     * Test: insert record in transaction
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConnListener3WithoutRollback() throws Exception {
        testConnListener3Test(AbstractTestsuite.DEP_3, false);
    }

    /**
     * Test: insert record in transaction
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConnListener3XaWithoutRollback() throws Exception {
        testConnListener3Test(AbstractTestsuite.DEP_3_XA, true);
    }
}

