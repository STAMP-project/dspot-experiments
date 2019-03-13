package com.apache.camel.file.processor;


import com.baeldung.camel.file.FileProcessor;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class FileProcessorIntegrationTest {
    private static final long DURATION_MILIS = 10000;

    private static final String SOURCE_FOLDER = "src/test/source-folder";

    private static final String DESTINATION_FOLDER = "src/test/destination-folder";

    @Test
    public void moveFolderContentJavaDSLTest() throws Exception {
        final CamelContext camelContext = new DefaultCamelContext();
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from((("file://" + (FileProcessorIntegrationTest.SOURCE_FOLDER)) + "?delete=true")).process(new FileProcessor()).to(("file://" + (FileProcessorIntegrationTest.DESTINATION_FOLDER)));
            }
        });
        camelContext.start();
        Thread.sleep(FileProcessorIntegrationTest.DURATION_MILIS);
        camelContext.stop();
    }

    @Test
    public void moveFolderContentSpringDSLTest() throws InterruptedException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("camel-context-test.xml");
        Thread.sleep(FileProcessorIntegrationTest.DURATION_MILIS);
        applicationContext.close();
    }
}

