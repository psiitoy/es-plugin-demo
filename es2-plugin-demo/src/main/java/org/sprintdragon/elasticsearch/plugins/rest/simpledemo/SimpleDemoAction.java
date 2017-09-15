package org.sprintdragon.elasticsearch.plugins.rest.simpledemo;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wangdi on 17-9-15.
 */
public class SimpleDemoAction extends BaseRestHandler {

    @Inject
    protected SimpleDemoAction(final Settings settings, RestController controller, Client client) {
        super(settings, controller, client);
        controller.registerHandler(RestRequest.Method.GET, "/hello/", this);
        controller.registerHandler(RestRequest.Method.GET, "/hello/{name}", this);
        controller.registerFilter(new RestFilter() {
            @Override
            public void process(RestRequest restRequest, RestChannel restChannel, RestFilterChain restFilterChain) throws Exception {

                if (restRequest.method() == RestRequest.Method.DELETE) {
                    restChannel.sendResponse(new BytesRestResponse(RestStatus.FORBIDDEN, "forbidden delete method"));
                } else if (restRequest.uri().contains("_search")) {
                    List<String> indices = getIndices(restRequest);
                    String deny_indices = settings.get("deny_indices");
                    assert indices != null;
                    for (String index : indices) {
                        if (index.contains(deny_indices)) {
                            restChannel.sendResponse(new BytesRestResponse(RestStatus.FORBIDDEN, "forbidden to operate index:" + index));
                        }
                    }
                    restFilterChain.continueProcessing(restRequest, restChannel);
                } else {
                    restFilterChain.continueProcessing(restRequest, restChannel);
                }
            }
        });
    }

    @Override
    protected void handleRequest(RestRequest request, RestChannel channel, Client client) throws Exception {
        logger.debug("HelloWorldAction.handleRequest called");
        final String name = request.hasParam("name") ? request.param("name") : "world";

        String content = "{\"success\":true, \"message\":\"hello " + name + "\"}";

        RestResponse response = new BytesRestResponse(RestStatus.OK, BytesRestResponse.TEXT_CONTENT_TYPE, content);
        channel.sendResponse(response);
    }


    public static List<String> getIndices(final RestRequest request) {
        String[] indices = new String[0];
        final String path = request.path();
        System.out.println("Evaluate decoded path for indices'" + path + "'");

        if (!path.startsWith("/")) {
            return null;
        }

        if (path.length() > 1) {
            int endIndex;
            if ((path.indexOf('/', 1)) != -1) {
                endIndex = path.indexOf('/', 1);
            } else {
                endIndex = path.length();
            }

            if (!path.trim().startsWith("/_")) {
                indices = Strings.splitStringByCommaToArray(path.substring(1, endIndex));
            }
        }

        System.out.println("Indices: " + Arrays.toString(indices));
        return Arrays.asList(indices);

    }

}
