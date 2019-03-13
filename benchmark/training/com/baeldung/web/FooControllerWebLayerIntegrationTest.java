package com.baeldung.web;


import com.baeldung.persistence.model.Foo;
import com.baeldung.persistence.service.IFooService;
import com.baeldung.web.controller.FooController;
import com.baeldung.web.hateoas.event.PaginatedResultsRetrievedEvent;
import java.util.Collections;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


/**
 * We'll start only the web layer.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(FooController.class)
public class FooControllerWebLayerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IFooService service;

    @MockBean
    private ApplicationEventPublisher publisher;

    @Test
    public void givenPresentFoo_whenFindPaginatedRequest_thenPageWithFooRetrieved() throws Exception {
        Page<Foo> page = new org.springframework.data.domain.PageImpl(Collections.singletonList(new Foo("fooName")));
        Mockito.when(service.findPaginated(0, 2)).thenReturn(page);
        Mockito.doNothing().when(publisher).publishEvent(ArgumentMatchers.any(PaginatedResultsRetrievedEvent.class));
        this.mockMvc.perform(get("/auth/foos").param("page", "0").param("size", "2")).andExpect(status().isOk()).andExpect(jsonPath("$", Matchers.hasSize(1)));
    }
}

