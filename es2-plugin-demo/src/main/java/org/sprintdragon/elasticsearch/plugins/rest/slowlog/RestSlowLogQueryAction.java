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

import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.*;

import static org.elasticsearch.rest.RestRequest.Method.GET;

public class RestSlowLogQueryAction extends BaseRestHandler {

    private ESLogger logger = Loggers.getLogger(RestSlowLogQueryAction.class);

    @Inject
    protected RestSlowLogQueryAction(Settings settings, RestController controller, Client client) {
        super(settings, controller, client);
        controller.registerHandler(GET, "/{index}/{type}/_slowlog", this);
    }

    @Override
    protected void handleRequest(RestRequest request, RestChannel channel, Client client) throws Exception {

//        String handle = request.param("type");
//        if(handle.equals("_delete")) {
//            this.doClear(channel);
//        } else if(handle.equals("_queryLog")){
//            this.queryLog(request,channel,client);
//        }
    }

//    private void doClear(RestChannel channel) throws Exception {
//
//        SLowLogDataSetHolder.instance().clear();
//
//        XContentBuilder builder = XContentFactory.jsonBuilder();
//
//        builder.startObject("result")
//                .field("msg","successful")
//                .endObject();
//        this.doResponse(RestStatus.OK,builder,channel);
//    }
//
//
//    private void queryLog(RestRequest request, RestChannel channel, Client client) throws Exception {
//        ConcurrentSkipListSet<SlowLogEntry> entrys = null;
//
//        switch (request.param("index")) {
//            case "fetch" :
//                entrys = SLowLogDataSetHolder.instance().getEntrys(SLowLogDataSetHolder.SlowLogPhaseType.FETCH);
//                break;
//            case "query" :
//                entrys = SLowLogDataSetHolder.instance().getEntrys(SLowLogDataSetHolder.SlowLogPhaseType.QUERY);
//                break;
//            default:
//                throw new IllegalArgumentException("Unknown phase " + request.param("phase"));
//        }
//
//
//        XContentBuilder builder = XContentFactory.jsonBuilder();
//
//        if(entrys != null && entrys.size() > 0) {
//            builder.startObject()
//                    .startArray("slowlogs");
//            for(SlowLogEntry entry : entrys) {
//                builder.startObject()
//                        .field("tookInNanos",entry.getTookInNanos())
//                        .field("slowlog",entry.getSlowlog())
//                        .endObject();
//            }
//            builder.endArray().endObject();
//        } else {
//            builder.startObject()
//                    .field("msg","no slow log found")
//                    .endObject();
//        }
//        this.doResponse(RestStatus.OK,builder,channel);
//    }

    private void doResponse(RestStatus restStatus, XContentBuilder builder, RestChannel channel) {
        BytesRestResponse restResponse = new BytesRestResponse(restStatus, builder);
        channel.sendResponse(restResponse);
    }
}
