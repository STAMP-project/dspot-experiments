/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.wire;


import alluxio.grpc.GrpcUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.Test;


public class FileInfoTest {
    @Test
    public void javaSerialization() throws Exception {
        FileInfo fileInfo = FileInfoTest.createRandom();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new ObjectOutputStream(byteArrayOutputStream).writeObject(fileInfo);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        FileInfo newFileInfo = ((FileInfo) (new ObjectInputStream(byteArrayInputStream).readObject()));
        checkEquality(fileInfo, newFileInfo);
    }

    @Test
    public void json() throws Exception {
        FileInfo fileInfo = FileInfoTest.createRandom();
        ObjectMapper mapper = new ObjectMapper();
        String s = mapper.writeValueAsString(fileInfo);
        FileInfo other = mapper.readValue(mapper.writeValueAsBytes(fileInfo), FileInfo.class);
        checkEquality(fileInfo, other);
    }

    @Test
    public void proto() {
        FileInfo fileInfo = FileInfoTest.createRandom();
        FileInfo other = GrpcUtils.fromProto(GrpcUtils.toProto(fileInfo));
        checkEquality(fileInfo, other);
    }
}

