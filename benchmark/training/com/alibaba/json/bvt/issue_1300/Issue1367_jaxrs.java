package com.alibaba.json.bvt.issue_1300;


import java.io.Serializable;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;


/**
 * <p>Title: Issue1367_jaxrs</p>
 * <p>Description: </p>
 *
 * @author Victor.Zxy
 * @version 1.0
 * @since 2017/8/7
 */
public class Issue1367_jaxrs extends JerseyTest {
    public static class AbstractController<ID extends Serializable, PO extends Issue1367.GenericEntity<ID>> {
        @POST
        @Path("/typeVariableBean")
        @Produces(MediaType.APPLICATION_JSON)
        @Consumes(MediaType.APPLICATION_JSON)
        public PO save(PO dto) {
            // do something
            return dto;
        }
    }

    @Path("beanController")
    public static class BeanController extends Issue1367_jaxrs.AbstractController<Long, Issue1367.TypeVariableBean> {
        @POST
        @Path("/parameterizedTypeBean")
        @Produces(MediaType.APPLICATION_JSON)
        @Consumes(MediaType.APPLICATION_JSON)
        public String parameterizedTypeBean(Issue1367.ParameterizedTypeBean<String> parameterizedTypeBean) {
            return parameterizedTypeBean.getT();
        }
    }

    @Test
    public void testParameterizedTypeBean() throws Exception {
        String request = "{\"t\": \"victor zeng\"}";
        Response response = target("beanController").path("parameterizedTypeBean").request().accept("application/json;charset=UTF-8").post(Entity.json(request));
        System.out.println(response.readEntity(String.class));
    }

    @Test
    public void testTypeVariableBean() throws Exception {
        String request = "{\"id\": 1}";
        Response response = target("beanController").path("typeVariableBean").request().accept("application/json;charset=UTF-8").post(Entity.json(request));
        System.out.println(response.readEntity(String.class));
    }
}

