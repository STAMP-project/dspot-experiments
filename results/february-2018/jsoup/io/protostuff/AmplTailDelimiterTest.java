/**
 * ========================================================================
 */
/**
 * Copyright 2007-2010 David Yu dyuproject@gmail.com
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 * ========================================================================
 */
package io.protostuff;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


/**
 * Test case for tail-delimited protostuff messages.
 *
 * @author David Yu
 * @unknown Oct 5, 2010
 */
public class AmplTailDelimiterTest extends AbstractTest {
    public <T> int writeListTo(OutputStream out, List<T> messages, Schema<T> schema) throws IOException {
        return ProtostuffIOUtil.writeListTo(out, messages, schema, new LinkedBuffer(LinkedBuffer.DEFAULT_BUFFER_SIZE));
    }

    public <T> List<T> parseListFrom(InputStream in, Schema<T> schema) throws IOException {
        return ProtostuffIOUtil.parseListFrom(in, schema);
    }
}

