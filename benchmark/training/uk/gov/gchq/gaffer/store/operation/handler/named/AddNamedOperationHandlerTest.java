/**
 * Copyright 2016-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.store.operation.handler.named;


import com.google.common.collect.Maps;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.named.operation.AddNamedOperation;
import uk.gov.gchq.gaffer.named.operation.NamedOperation;
import uk.gov.gchq.gaffer.named.operation.NamedOperationDetail;
import uk.gov.gchq.gaffer.named.operation.ParameterDetail;
import uk.gov.gchq.gaffer.named.operation.cache.exception.CacheOperationFailedException;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.store.operation.handler.named.cache.NamedOperationCache;
import uk.gov.gchq.gaffer.user.User;


public class AddNamedOperationHandlerTest {
    private static final String EMPTY_ADMIN_AUTH = "";

    private final NamedOperationCache mockCache = Mockito.mock(NamedOperationCache.class);

    private final AddNamedOperationHandler handler = new AddNamedOperationHandler(mockCache);

    private Context context = new Context(new User.Builder().userId("test user").build());

    private Store store = Mockito.mock(Store.class);

    private AddNamedOperation addNamedOperation = new AddNamedOperation.Builder().overwrite(false).build();

    private static final String OPERATION_NAME = "test";

    private HashMap<String, NamedOperationDetail> storedOperations = new HashMap<>();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldNotAllowForNonRecursiveNamedOperationsToBeNested() throws OperationException {
        OperationChain child = new OperationChain.Builder().first(new AddElements()).build();
        addNamedOperation.setOperationChain(child);
        addNamedOperation.setOperationName("child");
        handler.doOperation(addNamedOperation, context, store);
        OperationChain parent = new OperationChain.Builder().first(new NamedOperation.Builder().name("child").build()).then(new GetElements()).build();
        addNamedOperation.setOperationChain(parent);
        addNamedOperation.setOperationName("parent");
        exception.expect(OperationException.class);
        handler.doOperation(addNamedOperation, context, store);
    }

    @Test
    public void shouldAllowForOperationChainJSONWithParameter() {
        try {
            final String opChainJSON = "{ \"operations\": [ { \"class\":\"uk.gov.gchq.gaffer.operation.impl.get.GetAllElements\" }, { \"class\":\"uk.gov.gchq.gaffer.operation.impl.Limit\", \"resultLimit\": \"${param1}\" } ] }";
            addNamedOperation.setOperationChain(opChainJSON);
            addNamedOperation.setOperationName("namedop");
            ParameterDetail param = new ParameterDetail.Builder().defaultValue(1L).description("Limit param").valueClass(Long.class).build();
            Map<String, ParameterDetail> paramMap = Maps.newHashMap();
            paramMap.put("param1", param);
            addNamedOperation.setParameters(paramMap);
            handler.doOperation(addNamedOperation, context, store);
            assert cacheContains("namedop");
        } catch (final Exception e) {
            Assert.fail(("Expected test to pass without error. Exception " + (e.getMessage())));
        }
    }

    @Test
    public void shouldNotAllowForOperationChainWithParameterNotInOperationString() throws OperationException {
        final String opChainJSON = "{ \"operations\": [ { \"class\":\"uk.gov.gchq.gaffer.operation.impl.get.GetAllElements\" }, { \"class\":\"uk.gov.gchq.gaffer.operation.impl.export.set.ExportToSet\", \"key\": \"${param1}\" } ] }";
        addNamedOperation.setOperationChain(opChainJSON);
        addNamedOperation.setOperationName("namedop");
        // Note the param is String class to get past type checking which will also catch a param
        // with an unknown name if its not a string.
        ParameterDetail param = new ParameterDetail.Builder().defaultValue("setKey").description("key param").valueClass(String.class).build();
        Map<String, ParameterDetail> paramMap = Maps.newHashMap();
        paramMap.put("param2", param);
        addNamedOperation.setParameters(paramMap);
        exception.expect(OperationException.class);
        handler.doOperation(addNamedOperation, context, store);
    }

    @Test
    public void shouldNotAllowForOperationChainJSONWithInvalidParameter() throws UnsupportedEncodingException, SerialisationException {
        String opChainJSON = "{" + ((((((((((((((((((((((((("  \"operations\": [" + "      {") + "          \"class\": \"uk.gov.gchq.gaffer.named.operation.AddNamedOperation\",") + "          \"operationName\": \"testInputParam\",") + "          \"overwriteFlag\": true,") + "          \"operationChain\": {") + "              \"operations\": [") + "                  {") + "                      \"class\": \"uk.gov.gchq.gaffer.operation.impl.get.GetAllElements\"") + "                  },") + "                  {") + "                     \"class\": \"uk.gov.gchq.gaffer.operation.impl.Limit\",") + "                     \"resultLimit\": \"${param1}\"") + "                  }") + "              ]") + "           },") + "           \"parameters\": {") + "               \"param1\" : { \"description\" : \"Test Long parameter\",") + "                              \"defaultValue\" : [ \"bad arg type\" ],") + "                              \"requiredArg\" : false,") + "                              \"valueClass\": \"java.lang.Long\"") + "                          }") + "           }") + "       }") + "   ]") + "}");
        exception.expect(SerialisationException.class);
        JSONSerialiser.deserialise(opChainJSON.getBytes("UTF-8"), OperationChain.class);
    }

    @Test
    public void shouldAddNamedOperationWithScoreCorrectly() throws CacheOperationFailedException, OperationException {
        OperationChain opChain = new OperationChain.Builder().first(new AddElements()).build();
        addNamedOperation.setOperationChain(opChain);
        addNamedOperation.setScore(2);
        addNamedOperation.setOperationName("testOp");
        handler.doOperation(addNamedOperation, context, store);
        final NamedOperationDetail result = mockCache.getNamedOperation("testOp", new User(), AddNamedOperationHandlerTest.EMPTY_ADMIN_AUTH);
        assert cacheContains("testOp");
        Assert.assertEquals(addNamedOperation.getScore(), result.getScore());
    }
}

