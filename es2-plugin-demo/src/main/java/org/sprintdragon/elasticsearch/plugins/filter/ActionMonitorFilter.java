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

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.bulk.BulkAction;
import org.elasticsearch.action.get.GetAction;
import org.elasticsearch.action.index.IndexAction;
import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.action.support.ActionFilter;
import org.elasticsearch.action.support.ActionFilterChain;
import org.elasticsearch.action.update.UpdateAction;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.sprintdragon.elasticsearch.plugins.filter.wrapper.MonitorFilterWrapper;
import org.sprintdragon.elasticsearch.plugins.filter.wrapper.MonitorWrapper;

/**
 * Created by wangdi on 17-9-5.
 */
public class ActionMonitorFilter extends AbstractComponent implements ActionFilter {

    private static volatile String[] CARE_ABOUT_ACTION = {SearchAction.NAME, GetAction.NAME, BulkAction.NAME, IndexAction.NAME, UpdateAction.NAME};
    private static final String SETTING_PARAM_KEY_NODE_MASTER = "node.master";
    private static final String SETTING_PARAM_KEY_NODE_DATA = "node.data";
    protected static boolean isGatewayNode;
    protected static String clusterName;
    protected static String monitorType;
    private MonitorFilterWrapper monitorFilterWrapper;  // 监控模块实现，本例子不讨论

    @Inject
    public ActionMonitorFilter(Settings settings) {
        super(settings);
        clusterName = settings.get("cluster.name");
        monitorType = settings.get("my.plugins.monitor.type");
        //网关节点
        isGatewayNode = ifGatewayNode(settings);
        if (isGatewayNode) {
            //todo 根据 monitorType 去定义 monitorFilterWrapper
            logger.info("my monitor 网关节点启动监控 clusterName={},monitorType={}", clusterName, monitorType);
        }
    }

    private boolean ifGatewayNode(Settings settings) {
        //既不是master也不是data就是网关节点
        return !settings.getAsBoolean(SETTING_PARAM_KEY_NODE_MASTER, true)
                && !settings.getAsBoolean(SETTING_PARAM_KEY_NODE_DATA, true);
    }

    private boolean isCareAboutAction(String action) {
        for (String c : CARE_ABOUT_ACTION) {
            if (action.equals(c)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int order() {
        return 0;
    }

    //requestcontext
    @Override
    public void apply(String action, ActionRequest request, ActionListener listener, ActionFilterChain chain) {
        if (isGatewayNode && isCareAboutAction(action) && checkActionRequest(request)) {
            monitorApply(action, request, listener, chain);
        } else {
            //do nothing
            chain.proceed(action, request, listener);
        }
    }

    //防止子类Action重复filter
    private boolean checkActionRequest(ActionRequest request) {
        if (null != request.getContext().get("alreadyFiltered")) {
            return false;
        } else {
            request.putInContext("alreadyFiltered", true);
            return true;
        }
    }

    private void monitorApply(String action, ActionRequest request, ActionListener listener, ActionFilterChain chain) {
        MonitorWrapper monitorWrapper = monitorFilterWrapper.getMonitorWrapper(action, request, listener, chain);
        monitorWrapper.register(clusterName, action);
        chain.proceed(action, request, new ActionListenerWrapper(listener, action, request, System.currentTimeMillis(), monitorWrapper));
    }

    @Override
    public void apply(String action, ActionResponse response, ActionListener listener, ActionFilterChain chain) {
        chain.proceed(action, response, listener);
    }

    public class ActionListenerWrapper implements ActionListener {
        private ActionListener listener;
        private String action;
        private ActionRequest request;
        private Long startTime;
        private MonitorWrapper monitorWrapper;

        public ActionListenerWrapper(ActionListener listener, String action, ActionRequest request, Long startTime, MonitorWrapper monitorWrapper) {
            this.listener = listener;
            this.action = action;
            this.request = request;
            this.startTime = startTime;
            this.monitorWrapper = monitorWrapper;
        }

        @Override
        public void onResponse(Object object) {
            monitorWrapper.before();
            try {
                listener.onResponse(object);
                monitorWrapper.success();
            } catch (Exception e) {
                monitorWrapper.error();
                logger.error("monitor biz error!", e);
                throw e;
            } finally {
                monitorWrapper.end();
            }
        }

        public void onFailure(Throwable e) {
            monitorWrapper.before();
            try {
                listener.onFailure(e);
                monitorWrapper.error();
                logger.error("onFailure threadId={},response={},cost={}", Thread.currentThread().getId(), e, System.currentTimeMillis() - startTime);
            } finally {
                monitorWrapper.end();
            }
        }
    }

}
