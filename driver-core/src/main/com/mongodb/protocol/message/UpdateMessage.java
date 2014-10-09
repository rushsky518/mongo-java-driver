/*
 * Copyright (c) 2008-2014 MongoDB, Inc.
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

package com.mongodb.protocol.message;

import com.mongodb.operation.UpdateRequest;
import org.bson.io.BsonOutput;

import java.util.List;

import static com.mongodb.operation.WriteRequest.Type.REPLACE;

/**
 * An OP_UPDATE message.
 *
 * @mongodb.driver.manual ../meta-driver/latest/legacy/mongodb-wire-protocol/#op-update OP_UPDATE
 * @since 3.0
 */
public class UpdateMessage extends RequestMessage {
    private final List<UpdateRequest> updates;

    /**
     * Construct an instance.
     *
     * @param collectionName the collection name
     * @param updates the list of update requests
     * @param settings the message settings
     */
    public UpdateMessage(final String collectionName, final List<UpdateRequest> updates, final MessageSettings settings) {
        super(collectionName, OpCode.OP_UPDATE, settings);
        this.updates = updates;
    }

    @Override
    protected RequestMessage encodeMessageBody(final BsonOutput bsonOutput, final int messageStartPosition) {
        bsonOutput.writeInt32(0); // reserved
        bsonOutput.writeCString(getCollectionName());

        UpdateRequest updateRequest = updates.get(0);
        int flags = 0;
        if (updateRequest.isUpsert()) {
            flags |= 1;
        }
        if (updateRequest.isMulti()) {
            flags |= 2;
        }
        bsonOutput.writeInt32(flags);

        addDocument(updateRequest.getCriteria(), bsonOutput, new NoOpFieldNameValidator());
        if (updateRequest.getType() == REPLACE) {
            addCollectibleDocument(updateRequest.getUpdate(), bsonOutput, new CollectibleDocumentFieldNameValidator());
        } else {
            addDocument(updateRequest.getUpdate(), bsonOutput, new UpdateFieldNameValidator());
        }

        if (updates.size() == 1) {
            return null;
        } else {
            return new UpdateMessage(getCollectionName(), updates.subList(1, updates.size()), getSettings());
        }
    }

}