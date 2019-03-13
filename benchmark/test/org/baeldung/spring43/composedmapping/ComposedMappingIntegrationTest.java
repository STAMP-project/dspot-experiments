package org.baeldung.spring43.composedmapping;


import MediaType.ALL;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


@ContextConfiguration(classes = ComposedMappingConfiguration.class)
@WebAppConfiguration
public class ComposedMappingIntegrationTest extends AbstractJUnit4SpringContextTests {
    @Autowired
    private AppointmentService appointmentService;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Test
    public void whenRequestingMethodWithGetMapping_thenReceiving200Answer() throws Exception {
        this.mockMvc.perform(get("/appointments").accept(ALL)).andExpect(status().isOk());
        verify(appointmentService);
    }
}

