/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.engine.impl.webservice;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.flowable.common.engine.impl.util.ReflectUtil;
import org.flowable.engine.impl.bpmn.data.StructureDefinition;
import org.flowable.engine.impl.webservice.CxfWSDLImporter;
import org.flowable.engine.impl.webservice.WSOperation;
import org.flowable.engine.impl.webservice.WSService;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Esteban Robles Luna
 */
public class WSDLImporterTest {
    private CxfWSDLImporter importer;

    @Test
    public void testImportCounter() throws Exception {
        URL url = ReflectUtil.getResource("org/activiti/engine/impl/webservice/counter.wsdl");
        importer.importFrom(url.toString());
        List<WSService> services = new ArrayList<WSService>(importer.getServices().values());
        Assert.assertEquals(1, services.size());
        WSService service = services.get(0);
        Assert.assertEquals("Counter", service.getName());
        Assert.assertEquals("http://localhost:63081/counter", service.getLocation());
        List<StructureDefinition> structures = sortStructures();
        List<WSOperation> operations = sortOperations();
        Assert.assertEquals(5, operations.size());
        this.assertOperation(operations.get(0), "getCount", service);
        this.assertOperation(operations.get(1), "inc", service);
        this.assertOperation(operations.get(2), "prettyPrintCount", service);
        this.assertOperation(operations.get(3), "reset", service);
        this.assertOperation(operations.get(4), "setTo", service);
        Assert.assertEquals(10, structures.size());
        this.assertStructure(structures.get(0), "getCount", new String[]{  }, new Class<?>[]{  });
        this.assertStructure(structures.get(1), "getCountResponse", new String[]{ "count" }, new Class<?>[]{ Integer.class });
        this.assertStructure(structures.get(2), "inc", new String[]{  }, new Class<?>[]{  });
        this.assertStructure(structures.get(3), "incResponse", new String[]{  }, new Class<?>[]{  });
        this.assertStructure(structures.get(4), "prettyPrintCount", new String[]{ "prefix", "suffix" }, new Class<?>[]{ String.class, String.class });
        this.assertStructure(structures.get(5), "prettyPrintCountResponse", new String[]{ "prettyPrint" }, new Class<?>[]{ String.class });
        this.assertStructure(structures.get(6), "reset", new String[]{  }, new Class<?>[]{  });
        this.assertStructure(structures.get(7), "resetResponse", new String[]{  }, new Class<?>[]{  });
        this.assertStructure(structures.get(8), "setTo", new String[]{ "value" }, new Class<?>[]{ Integer.class });
        this.assertStructure(structures.get(9), "setToResponse", new String[]{  }, new Class<?>[]{  });
    }

    @Test
    public void testImportCounterWithImport() throws Exception {
        URL url = ReflectUtil.getResource("org/activiti/engine/impl/webservice/counterWithImport.wsdl");
        importer.importFrom(url.toString());
        List<WSService> services = new ArrayList<WSService>(importer.getServices().values());
        Assert.assertEquals(1, services.size());
        WSService service = services.get(0);
        Assert.assertEquals("Counter", service.getName());
        Assert.assertEquals("http://localhost:63081/counter", service.getLocation());
        List<StructureDefinition> structures = sortStructures();
        List<WSOperation> operations = sortOperations();
        Assert.assertEquals(5, operations.size());
        this.assertOperation(operations.get(0), "getCount", service);
        this.assertOperation(operations.get(1), "inc", service);
        this.assertOperation(operations.get(2), "prettyPrintCount", service);
        this.assertOperation(operations.get(3), "reset", service);
        this.assertOperation(operations.get(4), "setTo", service);
        Assert.assertEquals(10, structures.size());
        this.assertStructure(structures.get(0), "getCount", new String[]{  }, new Class<?>[]{  });
        this.assertStructure(structures.get(1), "getCountResponse", new String[]{ "count" }, new Class<?>[]{ Integer.class });
        this.assertStructure(structures.get(2), "inc", new String[]{  }, new Class<?>[]{  });
        this.assertStructure(structures.get(3), "incResponse", new String[]{  }, new Class<?>[]{  });
        this.assertStructure(structures.get(4), "prettyPrintCount", new String[]{ "prefix", "suffix" }, new Class<?>[]{ String.class, String.class });
        this.assertStructure(structures.get(5), "prettyPrintCountResponse", new String[]{ "prettyPrint" }, new Class<?>[]{ String.class });
        this.assertStructure(structures.get(6), "reset", new String[]{  }, new Class<?>[]{  });
        this.assertStructure(structures.get(7), "resetResponse", new String[]{  }, new Class<?>[]{  });
        this.assertStructure(structures.get(8), "setTo", new String[]{ "value" }, new Class<?>[]{ Integer.class });
        this.assertStructure(structures.get(9), "setToResponse", new String[]{  }, new Class<?>[]{  });
    }

    @Test
    public void testImportInheritedElement() throws Exception {
        URL url = ReflectUtil.getResource("org/activiti/engine/impl/webservice/inherited-elements-in-types.wsdl");
        Assert.assertNotNull(url);
        importer.importFrom(url.toString());
        List<StructureDefinition> structures = sortStructures();
        Assert.assertEquals(1, structures.size());
        final Object structureTypeInst = ReflectUtil.instantiate("org.activiti.webservice.counter.StructureType");
        final Class structureType = structureTypeInst.getClass();
        this.assertStructure(structures.get(0), "inheritedRequest", new String[]{ "rootElt", "inheritedElt", "newSimpleElt", "newStructuredElt" }, new Class<?>[]{ Short.class, Integer.class, String.class, structureType });
        Assert.assertEquals(2, structureType.getDeclaredFields().length);
        Assert.assertNotNull(structureType.getDeclaredField("booleanElt"));
        Assert.assertNotNull(structureType.getDeclaredField("dateElt"));
        Assert.assertEquals(1, structureType.getSuperclass().getDeclaredFields().length);
        Assert.assertNotNull(structureType.getSuperclass().getDeclaredField("rootElt"));
    }
}

