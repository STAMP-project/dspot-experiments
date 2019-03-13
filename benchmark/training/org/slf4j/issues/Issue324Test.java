package org.slf4j.issues;


import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Issue324Test extends TestCase {
    public void testLoggerCreationInPresenseOfSecurityManager() {
        String currentDir = System.getProperty("user.dir");
        System.out.println(("currentDir:" + currentDir));
        Logger logger = LoggerFactory.getLogger(Issue324Test.class);
        logger.debug("hello");
    }
}

