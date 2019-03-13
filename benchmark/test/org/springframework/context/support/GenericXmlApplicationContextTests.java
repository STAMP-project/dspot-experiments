package org.springframework.context.support;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;


/**
 * Unit tests for {@link GenericXmlApplicationContext}.
 *
 * See SPR-7530.
 *
 * @author Chris Beams
 */
public class GenericXmlApplicationContextTests {
    private static final Class<?> RELATIVE_CLASS = GenericXmlApplicationContextTests.class;

    private static final String RESOURCE_BASE_PATH = ClassUtils.classPackageAsResourcePath(GenericXmlApplicationContextTests.RELATIVE_CLASS);

    private static final String RESOURCE_NAME = (GenericXmlApplicationContextTests.class.getSimpleName()) + "-context.xml";

    private static final String FQ_RESOURCE_PATH = ((GenericXmlApplicationContextTests.RESOURCE_BASE_PATH) + '/') + (GenericXmlApplicationContextTests.RESOURCE_NAME);

    private static final String TEST_BEAN_NAME = "testBean";

    @Test
    public void classRelativeResourceLoading_ctor() {
        ApplicationContext ctx = new GenericXmlApplicationContext(GenericXmlApplicationContextTests.RELATIVE_CLASS, GenericXmlApplicationContextTests.RESOURCE_NAME);
        Assert.assertThat(ctx.containsBean(GenericXmlApplicationContextTests.TEST_BEAN_NAME), CoreMatchers.is(true));
    }

    @Test
    public void classRelativeResourceLoading_load() {
        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
        ctx.load(GenericXmlApplicationContextTests.RELATIVE_CLASS, GenericXmlApplicationContextTests.RESOURCE_NAME);
        ctx.refresh();
        Assert.assertThat(ctx.containsBean(GenericXmlApplicationContextTests.TEST_BEAN_NAME), CoreMatchers.is(true));
    }

    @Test
    public void fullyQualifiedResourceLoading_ctor() {
        ApplicationContext ctx = new GenericXmlApplicationContext(GenericXmlApplicationContextTests.FQ_RESOURCE_PATH);
        Assert.assertThat(ctx.containsBean(GenericXmlApplicationContextTests.TEST_BEAN_NAME), CoreMatchers.is(true));
    }

    @Test
    public void fullyQualifiedResourceLoading_load() {
        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
        ctx.load(GenericXmlApplicationContextTests.FQ_RESOURCE_PATH);
        ctx.refresh();
        Assert.assertThat(ctx.containsBean(GenericXmlApplicationContextTests.TEST_BEAN_NAME), CoreMatchers.is(true));
    }
}

