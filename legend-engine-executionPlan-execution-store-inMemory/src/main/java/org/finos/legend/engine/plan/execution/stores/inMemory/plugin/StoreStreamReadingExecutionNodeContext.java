// Copyright 2020 Goldman Sachs
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.finos.legend.engine.plan.execution.stores.inMemory.plugin;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.finos.legend.engine.plan.dependencies.store.inMemory.IStoreStreamReader;
import org.finos.legend.engine.plan.dependencies.store.inMemory.IStoreStreamReadingExecutionNodeContext;
import org.finos.legend.engine.plan.execution.nodes.helpers.freemarker.FreeMarkerExecutor;
import org.finos.legend.engine.plan.execution.nodes.helpers.platform.DefaultExecutionNodeContext;
import org.finos.legend.engine.plan.execution.nodes.helpers.platform.ExecutionNodeJavaPlatformHelper;
import org.finos.legend.engine.plan.execution.nodes.state.ExecutionState;
import org.finos.legend.engine.plan.execution.result.Result;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.nodes.graphFetch.store.inMemory.StoreStreamReadingExecutionNode;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.modelToModel.connection.JsonModelConnection;
import org.finos.legend.engine.shared.core.url.UrlFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ServiceLoader;

public class StoreStreamReadingExecutionNodeContext extends DefaultExecutionNodeContext implements IStoreStreamReadingExecutionNodeContext
{

    private final ExecutionState state;
    public static ExecutionNodeJavaPlatformHelper.ExecutionNodeContextFactory factory(StoreStreamReadingExecutionNode node)
    {
        return (ExecutionState state, Result childResult) -> new StoreStreamReadingExecutionNodeContext(node, state, childResult);
    }

    private final StoreStreamReadingExecutionNode node;

    private StoreStreamReadingExecutionNodeContext(StoreStreamReadingExecutionNode node, ExecutionState state, Result childResult)
    {
        super(state, childResult);
        this.state = super.state;
        this.node = node;
    }

    @Override
    public IStoreStreamReader createReader(String s)
    {
        MutableList<StoreStreamReaderBuilder> builders = Lists.mutable.empty();
        for (StoreStreamReaderBuilder desc : ServiceLoader.load(StoreStreamReaderBuilder.class))
        {
            builders.add(desc);
        }
        return builders.isEmpty()
                ? null
                : builders.getFirst().newStoreStreamReader(s, node.store);
    }

    @Override
    public URL createUrl(String url) throws MalformedURLException
    {
        return UrlFactory.create(FreeMarkerExecutor.process(url,this.state));
    }
}
