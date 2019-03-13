package azkaban.execapp.event;


import JobCallbackConstants.HTTP_POST;
import JobCallbackStatusEnum.STARTED;
import azkaban.utils.Props;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.mortbay.jetty.Server;


public class JobCallbackRequestMakerTest {
    private static final Logger logger = Logger.getLogger(JobCallbackRequestMakerTest.class);

    private static final String SLEEP_DURATION_PARAM = "sleepDuration";

    private static final String STATUS_CODE_PARAM = "returnedStatusCode";

    private static final String SERVER_NAME = "localhost:9999";

    private static final String PROJECT_NANE = "PROJECTX";

    private static final String FLOW_NANE = "FLOWX";

    private static final String JOB_NANE = "JOBX";

    private static final String EXECUTION_ID = "1234";

    private static final int PORT_NUMBER = 8989;

    private static JobCallbackRequestMaker jobCBMaker;

    private static Map<String, String> contextInfo;

    private static Server embeddedJettyServer;

    @Test(timeout = 4000)
    public void basicGetTest() {
        final Props props = new Props();
        final String url = buildUrlForDelay(1);
        props.put((("job.notification." + (STARTED.name().toLowerCase())) + ".1.url"), url);
        final List<HttpRequestBase> httpRequestList = JobCallbackUtil.parseJobCallbackProperties(props, STARTED, JobCallbackRequestMakerTest.contextInfo, 3);
        JobCallbackRequestMakerTest.jobCBMaker.makeHttpRequest(JobCallbackRequestMakerTest.JOB_NANE, JobCallbackRequestMakerTest.logger, httpRequestList);
    }

    @Test(timeout = 4000)
    public void simulateNotOKStatusCodeTest() {
        final Props props = new Props();
        final String url = buildUrlForStatusCode(404);
        props.put((("job.notification." + (STARTED.name().toLowerCase())) + ".1.url"), url);
        final List<HttpRequestBase> httpRequestList = JobCallbackUtil.parseJobCallbackProperties(props, STARTED, JobCallbackRequestMakerTest.contextInfo, 3);
        JobCallbackRequestMakerTest.jobCBMaker.makeHttpRequest(JobCallbackRequestMakerTest.JOB_NANE, JobCallbackRequestMakerTest.logger, httpRequestList);
    }

    @Test(timeout = 4000)
    public void unResponsiveGetTest() {
        final Props props = new Props();
        final String url = buildUrlForDelay(10);
        props.put((("job.notification." + (STARTED.name().toLowerCase())) + ".1.url"), url);
        final List<HttpRequestBase> httpRequestList = JobCallbackUtil.parseJobCallbackProperties(props, STARTED, JobCallbackRequestMakerTest.contextInfo, 3);
        JobCallbackRequestMakerTest.jobCBMaker.makeHttpRequest(JobCallbackRequestMakerTest.JOB_NANE, JobCallbackRequestMakerTest.logger, httpRequestList);
    }

    @Test(timeout = 4000)
    public void basicPostTest() {
        final Props props = new Props();
        final String url = buildUrlForDelay(1);
        props.put((("job.notification." + (STARTED.name().toLowerCase())) + ".1.url"), url);
        props.put((("job.notification." + (STARTED.name().toLowerCase())) + ".1.method"), HTTP_POST);
        props.put((("job.notification." + (STARTED.name().toLowerCase())) + ".1.body"), "This is it");
        final List<HttpRequestBase> httpRequestList = JobCallbackUtil.parseJobCallbackProperties(props, STARTED, JobCallbackRequestMakerTest.contextInfo, 3);
        JobCallbackRequestMakerTest.jobCBMaker.makeHttpRequest(JobCallbackRequestMakerTest.JOB_NANE, JobCallbackRequestMakerTest.logger, httpRequestList);
    }

    private static class DelayServlet extends HttpServlet {
        @Override
        public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException, ServletException {
            JobCallbackRequestMakerTest.logger.info(("Get get request: " + (req.getRequestURI())));
            JobCallbackRequestMakerTest.logger.info(("Get get request params: " + (req.getParameterMap())));
            final long start = System.currentTimeMillis();
            String responseMessage = handleDelay(req);
            JobCallbackRequestMakerTest.logger.info(("handleDelay elapse: " + ((System.currentTimeMillis()) - start)));
            responseMessage = handleSimulatedStatusCode(req, resp, responseMessage);
            final Writer writer = resp.getWriter();
            writer.write(responseMessage);
            writer.close();
        }

        private String handleSimulatedStatusCode(final HttpServletRequest req, final HttpServletResponse resp, String responseMessge) {
            final String returnedStatusCodeStr = req.getParameter(JobCallbackRequestMakerTest.STATUS_CODE_PARAM);
            if (returnedStatusCodeStr != null) {
                final int statusCode = Integer.parseInt(returnedStatusCodeStr);
                responseMessge = "Not good";
                resp.setStatus(statusCode);
            }
            return responseMessge;
        }

        private String handleDelay(final HttpServletRequest req) {
            final String sleepParamValue = req.getParameter(JobCallbackRequestMakerTest.SLEEP_DURATION_PARAM);
            if (sleepParamValue != null) {
                final long howLongMS = TimeUnit.MILLISECONDS.convert(Integer.parseInt(sleepParamValue), TimeUnit.SECONDS);
                JobCallbackRequestMakerTest.logger.info(("Delay for: " + howLongMS));
                try {
                    Thread.sleep(howLongMS);
                    return "Voila!!";
                } catch (final InterruptedException e) {
                    // don't care
                    return e.getMessage();
                }
            }
            return "";
        }

        @Override
        public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException, ServletException {
            JobCallbackRequestMakerTest.logger.info(("Get post request: " + (req.getRequestURI())));
            JobCallbackRequestMakerTest.logger.info(("Get post request params: " + (req.getParameterMap())));
            final BufferedReader reader = req.getReader();
            String line = null;
            while ((line = reader.readLine()) != null) {
                JobCallbackRequestMakerTest.logger.info(("post body: " + line));
            } 
            reader.close();
            String responseMessage = handleDelay(req);
            responseMessage = handleSimulatedStatusCode(req, resp, responseMessage);
            final Writer writer = resp.getWriter();
            writer.write(responseMessage);
            writer.close();
        }
    }
}

