package azkaban.jobcallback;


import JobCallbackConstants.HTTP_POST;
import JobCallbackStatusEnum.COMPLETED;
import JobCallbackStatusEnum.FAILURE;
import JobCallbackStatusEnum.STARTED;
import azkaban.utils.Props;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class JobCallbackValidatorTest {
    private Props serverProps;

    @Test
    public void noJobCallbackProps() {
        final Props jobProps = new Props();
        final Set<String> errors = new HashSet<>();
        Assert.assertEquals(0, JobCallbackValidator.validate("bogusJob", this.serverProps, jobProps, errors));
        Assert.assertEquals(0, errors.size());
    }

    @Test
    public void sequenceStartWithZeroProps() {
        final Props jobProps = new Props();
        final Set<String> errors = new HashSet<>();
        jobProps.put((("job.notification." + (FAILURE.name().toLowerCase())) + ".0.url"), "http://www.linkedin.com");
        jobProps.put((("job.notification." + (COMPLETED.name().toLowerCase())) + ".1.url"), "http://www.linkedin.com");
        Assert.assertEquals(1, JobCallbackValidator.validate("bogusJob", this.serverProps, jobProps, errors));
        Assert.assertEquals(1, errors.size());
    }

    @Test
    public void oneGetJobCallback() {
        final Props jobProps = new Props();
        jobProps.put((("job.notification." + (FAILURE.name().toLowerCase())) + ".1.url"), "http://www.linkedin.com");
        final Set<String> errors = new HashSet<>();
        Assert.assertEquals(1, JobCallbackValidator.validate("bogusJob", this.serverProps, jobProps, errors));
        Assert.assertEquals(0, errors.size());
    }

    @Test
    public void onePostJobCallback() {
        final Props jobProps = new Props();
        jobProps.put((("job.notification." + (FAILURE.name().toLowerCase())) + ".1.url"), "http://www.linkedin.com");
        jobProps.put((("job.notification." + (FAILURE.name().toLowerCase())) + ".1.method"), HTTP_POST);
        jobProps.put((("job.notification." + (FAILURE.name().toLowerCase())) + ".1.body"), "doc:id");
        final Set<String> errors = new HashSet<>();
        Assert.assertEquals(1, JobCallbackValidator.validate("bogusJob", this.serverProps, jobProps, errors));
        Assert.assertEquals(0, errors.size());
    }

    @Test
    public void multiplePostJobCallbacks() {
        final Props jobProps = new Props();
        jobProps.put((("job.notification." + (FAILURE.name().toLowerCase())) + ".1.url"), "http://www.linkedin.com");
        jobProps.put((("job.notification." + (FAILURE.name().toLowerCase())) + ".1.method"), HTTP_POST);
        jobProps.put((("job.notification." + (FAILURE.name().toLowerCase())) + ".1.body"), "doc:id");
        jobProps.put((("job.notification." + (FAILURE.name().toLowerCase())) + ".2.url"), "http://www.linkedin2.com");
        jobProps.put((("job.notification." + (FAILURE.name().toLowerCase())) + ".2.method"), HTTP_POST);
        jobProps.put((("job.notification." + (FAILURE.name().toLowerCase())) + ".2.body"), "doc2:id");
        final Set<String> errors = new HashSet<>();
        Assert.assertEquals(2, JobCallbackValidator.validate("bogusJob", this.serverProps, jobProps, errors));
        Assert.assertEquals(0, errors.size());
    }

    @Test
    public void noPostBodyJobCallback() {
        final Props jobProps = new Props();
        jobProps.put((("job.notification." + (FAILURE.name().toLowerCase())) + ".1.url"), "http://www.linkedin.com");
        jobProps.put((("job.notification." + (FAILURE.name().toLowerCase())) + ".1.method"), HTTP_POST);
        final Set<String> errors = new HashSet<>();
        Assert.assertEquals(0, JobCallbackValidator.validate("bogusJob", this.serverProps, jobProps, errors));
        Assert.assertEquals(1, errors.size());
        System.out.println(errors);
    }

    @Test
    public void multipleGetJobCallbacks() {
        final Props jobProps = new Props();
        jobProps.put((("job.notification." + (FAILURE.name().toLowerCase())) + ".1.url"), "http://www.linkedin.com");
        jobProps.put((("job.notification." + (STARTED.name().toLowerCase())) + ".1.url"), "http://www.linkedin.com");
        final Set<String> errors = new HashSet<>();
        Assert.assertEquals(2, JobCallbackValidator.validate("bogusJob", this.serverProps, jobProps, errors));
        Assert.assertEquals(0, errors.size());
    }

    @Test
    public void multipleGetJobCallbackWithGap() {
        final Props jobProps = new Props();
        jobProps.put((("job.notification." + (FAILURE.name().toLowerCase())) + ".1.url"), "http://www.linkedin.com");
        jobProps.put((("job.notification." + (FAILURE.name().toLowerCase())) + ".2.url"), "http://www.linkedin.com");
        jobProps.put((("job.notification." + (STARTED.name().toLowerCase())) + ".2.url"), "http://www.linkedin.com");
        final Set<String> errors = new HashSet<>();
        Assert.assertEquals(2, JobCallbackValidator.validate("bogusJob", this.serverProps, jobProps, errors));
        Assert.assertEquals(0, errors.size());
    }

    @Test
    public void postBodyLengthTooLargeTest() {
        final Props jobProps = new Props();
        jobProps.put((("job.notification." + (FAILURE.name().toLowerCase())) + ".1.url"), "http://www.linkedin.com");
        jobProps.put((("job.notification." + (FAILURE.name().toLowerCase())) + ".1.method"), HTTP_POST);
        final String postBodyValue = "abcdefghijklmnopqrstuvwxyz";
        final int postBodyLength = 20;
        Assert.assertTrue(((postBodyValue.length()) > postBodyLength));
        jobProps.put((("job.notification." + (FAILURE.name().toLowerCase())) + ".1.body"), postBodyValue);
        final Props localServerProps = new Props();
        localServerProps.put(JobCallbackConstants.MAX_POST_BODY_LENGTH_PROPERTY_KEY, postBodyLength);
        final Set<String> errors = new HashSet<>();
        Assert.assertEquals(0, JobCallbackValidator.validate("bogusJob", localServerProps, jobProps, errors));
        System.out.println(errors);
        Assert.assertEquals(1, errors.size());
    }
}

