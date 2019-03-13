package org.springframework.web.context.support;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.test.MockServletContext;


/**
 *
 *
 * @author Chris Beams
 * @see org.springframework.core.io.ResourceTests
 */
public class ResourceTests {
    @Test
    public void testServletContextResource() throws IOException {
        MockServletContext sc = new MockServletContext();
        Resource resource = new ServletContextResource(sc, "org/springframework/core/io/Resource.class");
        doTestResource(resource);
        Assert.assertEquals(resource, new ServletContextResource(sc, "org/springframework/core/../core/io/./Resource.class"));
    }

    @Test
    public void testServletContextResourceWithRelativePath() throws IOException {
        MockServletContext sc = new MockServletContext();
        Resource resource = new ServletContextResource(sc, "dir/");
        Resource relative = resource.createRelative("subdir");
        Assert.assertEquals(new ServletContextResource(sc, "dir/subdir"), relative);
    }
}

