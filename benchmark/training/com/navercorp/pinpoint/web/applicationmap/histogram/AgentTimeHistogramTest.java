/**
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.web.applicationmap.histogram;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.web.view.AgentResponseTimeViewModel;
import com.navercorp.pinpoint.web.vo.Application;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.ResponseTime;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author emeroad
 */
public class AgentTimeHistogramTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testViewModel() throws IOException {
        Application app = new Application("test", ServiceType.STAND_ALONE);
        AgentTimeHistogramBuilder builder = new AgentTimeHistogramBuilder(app, new Range(0, (1000 * 60)));
        List<ResponseTime> responseHistogramList = createResponseTime(app, "test1", "test2");
        AgentTimeHistogram histogram = builder.build(responseHistogramList);
        List<AgentResponseTimeViewModel> viewModel = histogram.createViewModel();
        logger.debug("{}", viewModel);
        JsonFactory jsonFactory = mapper.getFactory();
        StringWriter stringWriter = new StringWriter();
        JsonGenerator jsonGenerator = jsonFactory.createGenerator(stringWriter);
        jsonGenerator.writeStartObject();
        for (AgentResponseTimeViewModel agentResponseTimeViewModel : viewModel) {
            jsonGenerator.writeObject(agentResponseTimeViewModel);
        }
        jsonGenerator.writeEndObject();
        jsonGenerator.flush();
        jsonGenerator.close();
        logger.debug(stringWriter.toString());
    }
}

