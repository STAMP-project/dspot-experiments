package org.jboss.as.test.integration.deployment.structure.war;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.ejb.EJB;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests parsing of jboss-deployment-structure.xml file in a deployment
 * <p/>
 * User: Jaikiran Pai
 */
@RunWith(Arquillian.class)
public class WarJBossDeploymentStructureTestCase {
    private static final Logger logger = Logger.getLogger(WarJBossDeploymentStructureTestCase.class);

    @EJB(mappedName = "java:module/ClassLoadingEJB")
    private ClassLoadingEJB ejb;

    public static final String TO_BE_FOUND_CLASS_NAME = "org.jboss.as.test.integration.deployment.structure.war.Available";

    public static final String TO_BE_MISSSING_CLASS_NAME = "org.jboss.as.test.integration.deployment.structure.war.ToBeIgnored";

    /**
     * Make sure the <filter> element in jboss-deployment-structure.xml is processed correctly and the
     * exclude/include is honoured
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDeploymentStructureFilters() throws Exception {
        this.ejb.loadClass(WarJBossDeploymentStructureTestCase.TO_BE_FOUND_CLASS_NAME);
        try {
            this.ejb.loadClass(WarJBossDeploymentStructureTestCase.TO_BE_MISSSING_CLASS_NAME);
            Assert.fail(("Unexpectedly found class " + (WarJBossDeploymentStructureTestCase.TO_BE_MISSSING_CLASS_NAME)));
        } catch (ClassNotFoundException cnfe) {
            // expected
        }
    }

    @Test
    public void testUsePhysicalCodeSource() throws ClassNotFoundException {
        Class<?> clazz = this.ejb.loadClass(WarJBossDeploymentStructureTestCase.TO_BE_FOUND_CLASS_NAME);
        Assert.assertTrue(clazz.getProtectionDomain().getCodeSource().getLocation().getProtocol().equals("jar"));
        Assert.assertTrue(ClassLoadingEJB.class.getProtectionDomain().getCodeSource().getLocation().getProtocol().equals("file"));
    }

    /**
     * EE.5.15, part of testsuite migration AS6->AS7 (jbas7556)
     */
    @Test
    public void testModuleName() throws Exception {
        String result = ejb.query("java:module/ModuleName");
        Assert.assertEquals("deployment-structure", result);
        result = ejb.getResourceModuleName();
        Assert.assertEquals("deployment-structure", result);
    }

    @Test
    public void testAppName() throws Exception {
        String result = ejb.query("java:app/AppName");
        Assert.assertEquals("deployment-structure", result);
        result = ejb.getResourceAppName();
        Assert.assertEquals("deployment-structure", result);
    }

    @Test
    public void testAddingRootResource() throws IOException, ClassNotFoundException {
        InputStream clazz = getClass().getClassLoader().getResourceAsStream("root-file.txt");
        try {
            byte[] data = new byte[100];
            int res;
            StringBuilder sb = new StringBuilder();
            while ((res = clazz.read(data)) > 0) {
                sb.append(new String(data, 0, res, StandardCharsets.UTF_8));
            } 
            Assert.assertEquals("Root file", sb.toString());
        } finally {
            clazz.close();
        }
    }
}

