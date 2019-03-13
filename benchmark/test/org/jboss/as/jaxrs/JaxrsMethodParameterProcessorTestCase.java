package org.jboss.as.jaxrs;


import java.util.Set;
import org.jboss.as.jaxrs.deployment.JaxrsMethodParameterProcessor;
import org.jboss.as.jaxrs.deployment.ResteasyDeploymentData;
import org.jboss.as.jaxrs.rsources.PrimitiveParamResource;
import org.jboss.as.jaxrs.rsources.SimpleClassLazyParamConverter;
import org.jboss.as.jaxrs.rsources.SimpleClassParamConverterProvider;
import org.jboss.as.jaxrs.rsources.SimpleClassParamConverterResource;
import org.jboss.as.jaxrs.rsources.SimpleClassParameterizedTypeResource;
import org.jboss.as.jaxrs.rsources.SimpleFromStringProvider;
import org.jboss.as.jaxrs.rsources.SimpleFromStringResource;
import org.jboss.as.jaxrs.rsources.SimpleFromValueProvider;
import org.jboss.as.jaxrs.rsources.SimpleFromValueResource;
import org.jboss.as.jaxrs.rsources.SimpleValueOfProvider;
import org.jboss.as.jaxrs.rsources.SimpleValueOfResource;
import org.junit.Assert;
import org.junit.Test;


public class JaxrsMethodParameterProcessorTestCase {
    private ResteasyDeploymentData resteasyDeploymentData;

    private Set<String> resources;

    private Set<String> providers;

    /**
     * Check that a custom datatype is process by the ParamConverterProvider.
     * The provider throws an exception by design.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void customParameterTest() throws Exception {
        providers.clear();
        resources.clear();
        providers.add(SimpleClassParamConverterProvider.class.getName());
        resources.add(SimpleClassParamConverterResource.class.getName());
        JaxrsMethodParameterProcessor jProcessor = new JaxrsMethodParameterProcessor();
        try {
            jProcessor.testProcessor(Thread.currentThread().getContextClassLoader(), resteasyDeploymentData);
        } catch (Exception e) {
            Assert.fail(("Test failed.  It should not have thrown an exception: " + e));
        }
    }

    @Test
    public void customParameterizedTypeTest() throws Exception {
        providers.clear();
        resources.clear();
        providers.add(SimpleClassParamConverterProvider.class.getName());
        resources.add(SimpleClassParameterizedTypeResource.class.getName());
        JaxrsMethodParameterProcessor jProcessor = new JaxrsMethodParameterProcessor();
        try {
            jProcessor.testProcessor(Thread.currentThread().getContextClassLoader(), resteasyDeploymentData);
        } catch (Exception e) {
            Assert.fail(("Test failed.  It should not have thrown an exception: " + e));
        }
    }

    /**
     * Check the primitive datatypes are not processed by any converter.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void primitiveParameterTest() throws Exception {
        providers.clear();
        resources.clear();
        providers.add(SimpleClassParamConverterProvider.class.getName());
        resources.add(PrimitiveParamResource.class.getName());
        JaxrsMethodParameterProcessor jProcessor = new JaxrsMethodParameterProcessor();
        try {
            jProcessor.testProcessor(Thread.currentThread().getContextClassLoader(), resteasyDeploymentData);
        } catch (Exception e) {
            Assert.fail("Test failed no exception should have been thrown");
        }
    }

    @Test
    public void fromValueTest() throws Exception {
        providers.clear();
        resources.clear();
        providers.add(SimpleFromValueProvider.class.getName());
        resources.add(SimpleFromValueResource.class.getName());
        JaxrsMethodParameterProcessor jProcessor = new JaxrsMethodParameterProcessor();
        try {
            jProcessor.testProcessor(Thread.currentThread().getContextClassLoader(), resteasyDeploymentData);
        } catch (Exception e) {
            Assert.fail(("Test failed.  It should not have thrown an exception: " + e));
        }
    }

    @Test
    public void fromStringTest() throws Exception {
        providers.clear();
        resources.clear();
        providers.add(SimpleFromStringProvider.class.getName());
        resources.add(SimpleFromStringResource.class.getName());
        JaxrsMethodParameterProcessor jProcessor = new JaxrsMethodParameterProcessor();
        try {
            jProcessor.testProcessor(Thread.currentThread().getContextClassLoader(), resteasyDeploymentData);
        } catch (Exception e) {
            Assert.fail(("Test failed.  It should not have thrown an exception: " + e));
        }
    }

    @Test
    public void valueOfTest() throws Exception {
        providers.clear();
        resources.clear();
        providers.add(SimpleValueOfProvider.class.getName());
        resources.add(SimpleValueOfResource.class.getName());
        JaxrsMethodParameterProcessor jProcessor = new JaxrsMethodParameterProcessor();
        try {
            jProcessor.testProcessor(Thread.currentThread().getContextClassLoader(), resteasyDeploymentData);
        } catch (Exception e) {
            Assert.fail(("Test failed.  It should not have thrown an exception: " + e));
        }
    }

    @Test
    public void lazyLoadAnnotationTest() throws Exception {
        providers.clear();
        resources.clear();
        providers.add(SimpleClassLazyParamConverter.class.getName());
        resources.add(SimpleClassParamConverterResource.class.getName());
        JaxrsMethodParameterProcessor jProcessor = new JaxrsMethodParameterProcessor();
        try {
            jProcessor.testProcessor(Thread.currentThread().getContextClassLoader(), resteasyDeploymentData);
        } catch (Exception e) {
            Assert.fail(("Test failed.  It should not have thrown an exception: " + e));
        }
    }
}

