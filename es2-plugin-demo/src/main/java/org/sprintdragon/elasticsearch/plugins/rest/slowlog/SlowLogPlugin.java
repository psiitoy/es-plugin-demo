/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.sprintdragon.elasticsearch.plugins.rest.slowlog;

import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestModule;

/**
 * Created by lihailong1 on 2017/6/27.
 */
public class SlowLogPlugin extends Plugin {

    private static final String NAME = "slow-log-query";


    @Override
    public String name() {
        return NAME;
    }

    @Override
    public String description() {
        return "Query slow log on data node";
    }

    public void onModule(RestModule restModule) {
        restModule.addRestAction(RestSlowLogQueryAction.class);
    }
}
