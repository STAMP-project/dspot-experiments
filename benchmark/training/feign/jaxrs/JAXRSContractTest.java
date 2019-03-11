/**
 * Copyright 2012-2019 The Feign Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package feign.jaxrs;


import feign.MethodMetadata;
import feign.Response;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests interfaces defined per {@link JAXRSContract} are interpreted into expected
 * {@link feign .RequestTemplate template} instances.
 */
public class JAXRSContractTest {
    private static final List<String> STRING_LIST = null;

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    JAXRSContract contract = createContract();

    @Test
    public void httpMethods() throws Exception {
        assertThat(parseAndValidateMetadata(JAXRSContractTest.Methods.class, "post").template()).hasMethod("POST");
        assertThat(parseAndValidateMetadata(JAXRSContractTest.Methods.class, "put").template()).hasMethod("PUT");
        assertThat(parseAndValidateMetadata(JAXRSContractTest.Methods.class, "get").template()).hasMethod("GET");
        assertThat(parseAndValidateMetadata(JAXRSContractTest.Methods.class, "delete").template()).hasMethod("DELETE");
    }

    @Test
    public void customMethodWithoutPath() throws Exception {
        assertThat(parseAndValidateMetadata(JAXRSContractTest.CustomMethod.class, "patch").template()).hasMethod("PATCH").hasUrl("/");
    }

    @Test
    public void queryParamsInPathExtract() throws Exception {
        assertThat(parseAndValidateMetadata(JAXRSContractTest.WithQueryParamsInPath.class, "none").template()).hasPath("/").hasQueries();
        assertThat(parseAndValidateMetadata(JAXRSContractTest.WithQueryParamsInPath.class, "one").template()).hasPath("/").hasQueries(entry("Action", Arrays.asList("GetUser")));
        assertThat(parseAndValidateMetadata(JAXRSContractTest.WithQueryParamsInPath.class, "two").template()).hasPath("/").hasQueries(entry("Action", Arrays.asList("GetUser")), entry("Version", Arrays.asList("2010-05-08")));
        assertThat(parseAndValidateMetadata(JAXRSContractTest.WithQueryParamsInPath.class, "three").template()).hasPath("/").hasQueries(entry("Action", Arrays.asList("GetUser")), entry("Version", Arrays.asList("2010-05-08")), entry("limit", Arrays.asList("1")));
        assertThat(parseAndValidateMetadata(JAXRSContractTest.WithQueryParamsInPath.class, "empty").template()).hasPath("/").hasQueries(entry("flag", new ArrayList()), entry("Action", Arrays.asList("GetUser")), entry("Version", Arrays.asList("2010-05-08")));
    }

    @Test
    public void producesAddsAcceptHeader() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(JAXRSContractTest.ProducesAndConsumes.class, "produces");
        /* multiple @Produces annotations should be additive */
        assertThat(md.template()).hasHeaders(entry("Content-Type", Arrays.asList("application/json")), entry("Accept", Arrays.asList("application/xml")));
    }

    @Test
    public void producesMultipleAddsAcceptHeader() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(JAXRSContractTest.ProducesAndConsumes.class, "producesMultiple");
        assertThat(md.template()).hasHeaders(entry("Content-Type", Collections.singletonList("application/json")), entry("Accept", Arrays.asList("application/xml", "text/plain")));
    }

    @Test
    public void producesNada() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Produces.value() was empty on method producesNada");
        parseAndValidateMetadata(JAXRSContractTest.ProducesAndConsumes.class, "producesNada");
    }

    @Test
    public void producesEmpty() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Produces.value() was empty on method producesEmpty");
        parseAndValidateMetadata(JAXRSContractTest.ProducesAndConsumes.class, "producesEmpty");
    }

    @Test
    public void consumesAddsContentTypeHeader() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(JAXRSContractTest.ProducesAndConsumes.class, "consumes");
        /* multiple @Consumes annotations are additive */
        assertThat(md.template()).hasHeaders(entry("Content-Type", Arrays.asList("application/xml")), entry("Accept", Arrays.asList("text/html")));
    }

    @Test
    public void consumesMultipleAddsContentTypeHeader() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(JAXRSContractTest.ProducesAndConsumes.class, "consumesMultiple");
        assertThat(md.template()).hasHeaders(entry("Content-Type", Arrays.asList("application/xml")), entry("Accept", Collections.singletonList("text/html")));
    }

    @Test
    public void consumesNada() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Consumes.value() was empty on method consumesNada");
        parseAndValidateMetadata(JAXRSContractTest.ProducesAndConsumes.class, "consumesNada");
    }

    @Test
    public void consumesEmpty() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Consumes.value() was empty on method consumesEmpty");
        parseAndValidateMetadata(JAXRSContractTest.ProducesAndConsumes.class, "consumesEmpty");
    }

    @Test
    public void producesAndConsumesOnClassAddsHeader() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(JAXRSContractTest.ProducesAndConsumes.class, "producesAndConsumes");
        assertThat(md.template()).hasHeaders(entry("Content-Type", Arrays.asList("application/json")), entry("Accept", Arrays.asList("text/html")));
    }

    @Test
    public void bodyParamIsGeneric() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(JAXRSContractTest.BodyParams.class, "post", List.class);
        assertThat(md.bodyIndex()).isEqualTo(0);
        assertThat(md.bodyType()).isEqualTo(JAXRSContractTest.class.getDeclaredField("STRING_LIST").getGenericType());
    }

    @Test
    public void tooManyBodies() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Method has too many Body");
        parseAndValidateMetadata(JAXRSContractTest.BodyParams.class, "tooMany", List.class, List.class);
    }

    @Test
    public void emptyPathOnType() throws Exception {
        assertThat(parseAndValidateMetadata(JAXRSContractTest.EmptyPathOnType.class, "base").template()).hasUrl("/");
    }

    @Test
    public void emptyPathOnTypeSpecific() throws Exception {
        assertThat(parseAndValidateMetadata(JAXRSContractTest.EmptyPathOnType.class, "get").template()).hasUrl("/specific");
    }

    @Test
    public void parsePathMethod() throws Exception {
        assertThat(parseAndValidateMetadata(JAXRSContractTest.PathOnType.class, "base").template()).hasUrl("/base");
        assertThat(parseAndValidateMetadata(JAXRSContractTest.PathOnType.class, "get").template()).hasUrl("/base/specific");
    }

    @Test
    public void emptyPathOnMethod() throws Exception {
        assertThat(parseAndValidateMetadata(JAXRSContractTest.PathOnType.class, "emptyPath").template()).hasUrl("/base");
    }

    @Test
    public void emptyPathParam() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("PathParam.value() was empty on parameter 0");
        parseAndValidateMetadata(JAXRSContractTest.PathOnType.class, "emptyPathParam", String.class);
    }

    @Test
    public void pathParamWithSpaces() throws Exception {
        assertThat(parseAndValidateMetadata(JAXRSContractTest.PathOnType.class, "pathParamWithSpaces", String.class).template()).hasUrl("/base/{param}");
    }

    @Test
    public void regexPathOnMethodOrType() throws Exception {
        assertThat(parseAndValidateMetadata(JAXRSContractTest.PathOnType.class, "pathParamWithRegex", String.class).template()).hasUrl("/base/regex/{param}");
        assertThat(parseAndValidateMetadata(JAXRSContractTest.PathOnType.class, "pathParamWithMultipleRegex", String.class, String.class).template()).hasUrl("/base/regex/{param1}/{param2}");
        assertThat(parseAndValidateMetadata(JAXRSContractTest.ComplexPathOnType.class, "pathParamWithMultipleRegex", String.class, String.class).template()).hasUrl("/{baseparam}/regex/{param1}/{param2}");
    }

    @Test
    public void withPathAndURIParams() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(JAXRSContractTest.WithURIParam.class, "uriParam", String.class, URI.class, String.class);
        // Skips 1 as it is a url index!
        assertThat(md.indexToName()).containsExactly(entry(0, Arrays.asList("1")), entry(2, Arrays.asList("2")));
        assertThat(md.urlIndex()).isEqualTo(1);
    }

    @Test
    public void pathAndQueryParams() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(JAXRSContractTest.WithPathAndQueryParams.class, "recordsByNameAndType", int.class, String.class, String.class);
        assertThat(md.template()).hasQueries(entry("name", Arrays.asList("{name}")), entry("type", Arrays.asList("{type}")));
        assertThat(md.indexToName()).containsExactly(entry(0, Arrays.asList("domainId")), entry(1, Arrays.asList("name")), entry(2, Arrays.asList("type")));
    }

    @Test
    public void emptyQueryParam() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("QueryParam.value() was empty on parameter 0");
        parseAndValidateMetadata(JAXRSContractTest.WithPathAndQueryParams.class, "empty", String.class);
    }

    @Test
    public void formParamsParseIntoIndexToName() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(JAXRSContractTest.FormParams.class, "login", String.class, String.class, String.class);
        assertThat(md.formParams()).containsExactly("customer_name", "user_name", "password");
        assertThat(md.indexToName()).containsExactly(entry(0, Arrays.asList("customer_name")), entry(1, Arrays.asList("user_name")), entry(2, Arrays.asList("password")));
    }

    /**
     * Body type is only for the body param.
     */
    @Test
    public void formParamsDoesNotSetBodyType() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(JAXRSContractTest.FormParams.class, "login", String.class, String.class, String.class);
        assertThat(md.bodyType()).isNull();
    }

    @Test
    public void emptyFormParam() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("FormParam.value() was empty on parameter 0");
        parseAndValidateMetadata(JAXRSContractTest.FormParams.class, "emptyFormParam", String.class);
    }

    @Test
    public void headerParamsParseIntoIndexToName() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(JAXRSContractTest.HeaderParams.class, "logout", String.class);
        assertThat(md.template()).hasHeaders(entry("Auth-Token", Arrays.asList("{Auth-Token}")));
        assertThat(md.indexToName()).containsExactly(entry(0, Arrays.asList("Auth-Token")));
    }

    @Test
    public void emptyHeaderParam() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("HeaderParam.value() was empty on parameter 0");
        parseAndValidateMetadata(JAXRSContractTest.HeaderParams.class, "emptyHeaderParam", String.class);
    }

    @Test
    public void pathsWithoutSlashesParseCorrectly() throws Exception {
        assertThat(parseAndValidateMetadata(JAXRSContractTest.PathsWithoutAnySlashes.class, "get").template()).hasUrl("/base/specific");
    }

    @Test
    public void pathsWithSomeSlashesParseCorrectly() throws Exception {
        assertThat(parseAndValidateMetadata(JAXRSContractTest.PathsWithSomeSlashes.class, "get").template()).hasUrl("/base/specific");
    }

    @Test
    public void pathsWithSomeOtherSlashesParseCorrectly() throws Exception {
        assertThat(parseAndValidateMetadata(JAXRSContractTest.PathsWithSomeOtherSlashes.class, "get").template()).hasUrl("/base/specific");
    }

    @Test
    public void classWithRootPathParsesCorrectly() throws Exception {
        assertThat(parseAndValidateMetadata(JAXRSContractTest.ClassRootPath.class, "get").template()).hasUrl("/specific");
    }

    @Test
    public void classPathWithTrailingSlashParsesCorrectly() throws Exception {
        assertThat(parseAndValidateMetadata(JAXRSContractTest.ClassPathWithTrailingSlash.class, "get").template()).hasUrl("/base/specific");
    }

    @Test
    public void methodPathWithoutLeadingSlashParsesCorrectly() throws Exception {
        assertThat(parseAndValidateMetadata(JAXRSContractTest.MethodWithFirstPathThenGetWithoutLeadingSlash.class, "get").template()).hasUrl("/base/specific");
    }

    @Test
    public void producesWithHeaderParamContainAllHeaders() throws Exception {
        assertThat(parseAndValidateMetadata(JAXRSContractTest.MixedAnnotations.class, "getWithHeaders", String.class, String.class, String.class).template()).hasHeaders(entry("Accept", Arrays.asList("{Accept}", "application/json"))).hasQueries(entry("multiple", Arrays.asList("stuff", "{multiple}")), entry("another", Collections.singletonList("{another}")));
    }

    interface Methods {
        @POST
        void post();

        @PUT
        void put();

        @GET
        void get();

        @DELETE
        void delete();
    }

    interface CustomMethod {
        @JAXRSContractTest.CustomMethod.PATCH
        Response patch();

        @Target({ ElementType.METHOD })
        @Retention(RetentionPolicy.RUNTIME)
        @HttpMethod("PATCH")
        public @interface PATCH {}
    }

    interface WithQueryParamsInPath {
        @GET
        @Path("/")
        Response none();

        @GET
        @Path("/?Action=GetUser")
        Response one();

        @GET
        @Path("/?Action=GetUser&Version=2010-05-08")
        Response two();

        @GET
        @Path("/?Action=GetUser&Version=2010-05-08&limit=1")
        Response three();

        @GET
        @Path("/?flag&Action=GetUser&Version=2010-05-08")
        Response empty();
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_HTML)
    interface ProducesAndConsumes {
        @GET
        @Produces("application/xml")
        Response produces();

        @GET
        @Produces({ "application/xml", "text/plain" })
        Response producesMultiple();

        @GET
        @Produces({  })
        Response producesNada();

        @GET
        @Produces({ "" })
        Response producesEmpty();

        @POST
        @Consumes("application/xml")
        Response consumes();

        @POST
        @Consumes({ "application/xml", "application/json" })
        Response consumesMultiple();

        @POST
        @Consumes({  })
        Response consumesNada();

        @POST
        @Consumes({ "" })
        Response consumesEmpty();

        @POST
        Response producesAndConsumes();
    }

    interface BodyParams {
        @POST
        Response post(List<String> body);

        @POST
        Response tooMany(List<String> body, List<String> body2);
    }

    @Path("")
    interface EmptyPathOnType {
        @GET
        Response base();

        @GET
        @Path("/specific")
        Response get();
    }

    @Path("/base")
    interface PathOnType {
        @GET
        Response base();

        @GET
        @Path("/specific")
        Response get();

        @GET
        @Path("")
        Response emptyPath();

        @GET
        @Path("/{param}")
        Response emptyPathParam(@PathParam("")
        String empty);

        @GET
        @Path("/{   param   }")
        Response pathParamWithSpaces(@PathParam("param")
        String path);

        @GET
        @Path("regex/{param:.+}")
        Response pathParamWithRegex(@PathParam("param")
        String path);

        @GET
        @Path("regex/{param1:[0-9]*}/{  param2 : .+}")
        Response pathParamWithMultipleRegex(@PathParam("param1")
        String param1, @PathParam("param2")
        String param2);
    }

    @Path("/{baseparam: [0-9]+}")
    interface ComplexPathOnType {
        @GET
        @Path("regex/{param1:[0-9]*}/{  param2 : .+}")
        Response pathParamWithMultipleRegex(@PathParam("param1")
        String param1, @PathParam("param2")
        String param2);
    }

    interface WithURIParam {
        @GET
        @Path("/{1}/{2}")
        Response uriParam(@PathParam("1")
        String one, URI endpoint, @PathParam("2")
        String two);
    }

    interface WithPathAndQueryParams {
        @GET
        @Path("/domains/{domainId}/records")
        Response recordsByNameAndType(@PathParam("domainId")
        int id, @QueryParam("name")
        String nameFilter, @QueryParam("type")
        String typeFilter);

        @GET
        Response empty(@QueryParam("")
        String empty);
    }

    interface FormParams {
        @POST
        void login(@FormParam("customer_name")
        String customer, @FormParam("user_name")
        String user, @FormParam("password")
        String password);

        @GET
        Response emptyFormParam(@FormParam("")
        String empty);
    }

    interface HeaderParams {
        @POST
        void logout(@HeaderParam("Auth-Token")
        String token);

        @GET
        Response emptyHeaderParam(@HeaderParam("")
        String empty);
    }

    @Path("base")
    interface PathsWithoutAnySlashes {
        @GET
        @Path("specific")
        Response get();
    }

    @Path("/base")
    interface PathsWithSomeSlashes {
        @GET
        @Path("specific")
        Response get();
    }

    @Path("base")
    interface PathsWithSomeOtherSlashes {
        @GET
        @Path("/specific")
        Response get();
    }

    @Path("/")
    interface ClassRootPath {
        @GET
        @Path("/specific")
        Response get();
    }

    @Path("/base/")
    interface ClassPathWithTrailingSlash {
        @GET
        @Path("/specific")
        Response get();
    }

    @Path("/base/")
    interface MethodWithFirstPathThenGetWithoutLeadingSlash {
        @Path("specific")
        @GET
        Response get();
    }

    interface MixedAnnotations {
        @GET
        @Path("/api/stuff?multiple=stuff")
        @Produces("application/json")
        Response getWithHeaders(@HeaderParam("Accept")
        String accept, @QueryParam("multiple")
        String multiple, @QueryParam("another")
        String another);
    }
}

