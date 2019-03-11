package com.baeldung.restdocs;


import MediaTypes.HAL_JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


@ExtendWith({ RestDocumentationExtension.class, SpringExtension.class })
@SpringBootTest(classes = SpringRestDocsApplication.class)
public class ApiDocumentationJUnit5IntegrationTest {
    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Test
    public void indexExample() throws Exception {
        this.mockMvc.perform(get("/")).andExpect(status().isOk()).andDo(document("index-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()), links(linkWithRel("crud").description("The CRUD resource")), responseFields(subsectionWithPath("_links").description("Links to other resources")), responseHeaders(headerWithName("Content-Type").description("The Content-Type of the payload, e.g. `application/hal+json`"))));
    }

    @Test
    public void crudGetExample() throws Exception {
        Map<String, Object> crud = new HashMap<>();
        crud.put("id", 1L);
        crud.put("title", "Sample Model");
        crud.put("body", "http://www.baeldung.com/");
        String tagLocation = this.mockMvc.perform(get("/crud").contentType(HAL_JSON).content(this.objectMapper.writeValueAsString(crud))).andExpect(status().isOk()).andReturn().getResponse().getHeader("Location");
        crud.put("tags", Collections.singletonList(tagLocation));
        ConstraintDescriptions desc = new ConstraintDescriptions(CrudInput.class);
        this.mockMvc.perform(get("/crud").contentType(HAL_JSON).content(this.objectMapper.writeValueAsString(crud))).andExpect(status().isOk()).andDo(document("crud-get-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()), requestFields(fieldWithPath("id").description(("The id of the input" + (collectionToDelimitedString(desc.descriptionsForProperty("id"), ". ")))), fieldWithPath("title").description("The title of the input"), fieldWithPath("body").description("The body of the input"), fieldWithPath("tags").description("An array of tag resource URIs"))));
    }

    @Test
    public void crudCreateExample() throws Exception {
        Map<String, Object> crud = new HashMap<>();
        crud.put("id", 2L);
        crud.put("title", "Sample Model");
        crud.put("body", "http://www.baeldung.com/");
        String tagLocation = this.mockMvc.perform(post("/crud").contentType(HAL_JSON).content(this.objectMapper.writeValueAsString(crud))).andExpect(status().isCreated()).andReturn().getResponse().getHeader("Location");
        crud.put("tags", Collections.singletonList(tagLocation));
        this.mockMvc.perform(post("/crud").contentType(HAL_JSON).content(this.objectMapper.writeValueAsString(crud))).andExpect(status().isCreated()).andDo(document("crud-create-example", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()), requestFields(fieldWithPath("id").description("The id of the input"), fieldWithPath("title").description("The title of the input"), fieldWithPath("body").description("The body of the input"), fieldWithPath("tags").description("An array of tag resource URIs"))));
    }

    @Test
    public void crudDeleteExample() throws Exception {
        this.mockMvc.perform(delete("/crud/{id}", 10)).andExpect(status().isOk()).andDo(document("crud-delete-example", pathParameters(parameterWithName("id").description("The id of the input to delete"))));
    }

    @Test
    public void crudPatchExample() throws Exception {
        Map<String, String> tag = new HashMap<>();
        tag.put("name", "PATCH");
        String tagLocation = this.mockMvc.perform(patch("/crud/{id}", 10).contentType(HAL_JSON).content(this.objectMapper.writeValueAsString(tag))).andExpect(status().isOk()).andReturn().getResponse().getHeader("Location");
        Map<String, Object> crud = new HashMap<>();
        crud.put("title", "Sample Model Patch");
        crud.put("body", "http://www.baeldung.com/");
        crud.put("tags", Collections.singletonList(tagLocation));
        this.mockMvc.perform(patch("/crud/{id}", 10).contentType(HAL_JSON).content(this.objectMapper.writeValueAsString(crud))).andExpect(status().isOk());
    }

    @Test
    public void crudPutExample() throws Exception {
        Map<String, String> tag = new HashMap<>();
        tag.put("name", "PUT");
        String tagLocation = this.mockMvc.perform(put("/crud/{id}", 10).contentType(HAL_JSON).content(this.objectMapper.writeValueAsString(tag))).andExpect(status().isAccepted()).andReturn().getResponse().getHeader("Location");
        Map<String, Object> crud = new HashMap<>();
        crud.put("title", "Sample Model");
        crud.put("body", "http://www.baeldung.com/");
        crud.put("tags", Collections.singletonList(tagLocation));
        this.mockMvc.perform(put("/crud/{id}", 10).contentType(HAL_JSON).content(this.objectMapper.writeValueAsString(crud))).andExpect(status().isAccepted());
    }
}

