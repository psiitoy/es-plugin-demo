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

package org.sprintdragon.elasticsearch.plugins.filter;

import org.elasticsearch.action.ActionModule;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.plugins.Plugin;

import java.util.Collection;
import java.util.Collections;

public class ActionMonitorPlugin extends Plugin {
    private final ESLogger log = Loggers.getLogger(ActionMonitorPlugin.class);


    public ActionMonitorPlugin() {
        log.info("Starting Action Monitor Plugin");
    }

    public void onModule(ActionModule module) {
        module.registerFilter(ActionMonitorFilter.class);
    }

    @Override
    public Collection<Module> nodeModules() {
        return Collections.<Module>singletonList(new ActionMonitorModule());
    }

    public String description() {
        return "description";
    }

    public String name() {
        return "name";
    }
}
