/*
 * Copyright 2008-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.reactivestreams.client

import com.mongodb.internal.async.client.AsyncMongoClients
import spock.lang.Specification

class MongoClientsSpecification extends Specification {


    def 'should have the same methods as the wrapped MongoClients'() {
        given:
        def wrapped = (AsyncMongoClients.methods*.name).sort()
        def local = MongoClients.methods*.name.sort()

        expect:
        wrapped == local
    }

    def 'should return the same default codec registry'() {
        expect:
        MongoClients.getDefaultCodecRegistry() == AsyncMongoClients.getDefaultCodecRegistry()
    }

}
