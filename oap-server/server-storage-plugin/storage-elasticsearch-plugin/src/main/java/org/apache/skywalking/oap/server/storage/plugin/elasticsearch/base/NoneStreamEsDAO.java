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
 *
 */

package org.apache.skywalking.oap.server.storage.plugin.elasticsearch.base;

import java.io.IOException;
import java.util.Map;
import org.apache.skywalking.oap.server.core.analysis.config.NoneStream;
import org.apache.skywalking.oap.server.core.storage.INoneStreamDAO;
import org.apache.skywalking.oap.server.core.storage.model.Model;
import org.apache.skywalking.oap.server.core.storage.type.StorageBuilder;
import org.apache.skywalking.oap.server.library.client.elasticsearch.ElasticSearchClient;

/**
 * Synchronize storage Elasticsearch implements
 */
public class NoneStreamEsDAO extends EsDAO implements INoneStreamDAO {
    private final StorageBuilder<NoneStream> storageBuilder;

    public NoneStreamEsDAO(ElasticSearchClient client,
                           StorageBuilder<NoneStream> storageBuilder) {
        super(client);
        this.storageBuilder = storageBuilder;
    }

    @Override
    public void insert(Model model, NoneStream noneStream) throws IOException {
        final ElasticSearchConverter.ToStorage toStorage = new ElasticSearchConverter.ToStorage(model.getName());
        storageBuilder.entity2Storage(noneStream, toStorage);
        Map<String, Object> builder =
            IndexController.INSTANCE.appendTableColumn(model, toStorage.obtain());
        String modelName = TimeSeriesUtils.writeIndexName(model, noneStream.getTimeBucket());
        String id = IndexController.INSTANCE.generateDocId(model, noneStream.id().build());
        getClient().forceInsert(modelName, id, builder);
    }
}
