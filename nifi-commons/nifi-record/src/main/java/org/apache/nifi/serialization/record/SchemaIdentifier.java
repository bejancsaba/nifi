/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.nifi.serialization.record;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;

public interface SchemaIdentifier {

    /**
     * @return the name of the schema, if one has been defined.
     */
    Optional<String> getName();

    /**
     * @return the identifier of the schema, if one has been defined.
     */
    OptionalLong getIdentifier();

    /**
     * @return the version of the schema, if one has been defined.
     */
    OptionalInt getVersion();

    /**
     * @return the schema version ID of the schema, if one has been defined.
     */
    OptionalLong getSchemaVersionId();

    /**
     * @return the name of the branch where the schema is located, if one has been defined
     */
    Optional<String> getBranch();

    /**
     * @return the protocol used to get this schema identifier
     */
    Integer getProtocol();


    SchemaIdentifier EMPTY = new StandardSchemaIdentifier(null, null, null, null, null, -1);

    static Builder builder() {
        return new StandardSchemaIdentifier.Builder();
    }

    /**
     * Implementations should provide a builder to create instances of the SchemaIdentifier.
     */
    interface Builder {

        Builder name(String name);

        Builder id(Long id);

        Builder version(Integer version);

        Builder schemaVersionId(Long schemaVersionId);

        Builder branch(String branch);

        Builder protocol(Integer protocol);

        SchemaIdentifier build();

    }
}