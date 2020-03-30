/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.routebuilder;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.openmf.psp.config.ChannelSettings;
import org.openmf.psp.mojaloop.cache.TransactionContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * List of administration endpoints, like the one which queries and the other which empties all the caches.
 */
@Configuration
public class AdminRouteBuilder extends RouteBuilder {

    private static Logger logger = LoggerFactory.getLogger(AdminRouteBuilder.class);

    private ChannelSettings channelSettings;

    private TransactionContextHolder transactionContextHolder;

    @Autowired
    public AdminRouteBuilder(CamelContext context, ChannelSettings channelSettings, TransactionContextHolder transactionContextHolder) {
        super(context);
        this.channelSettings = channelSettings;
        this.transactionContextHolder = transactionContextHolder;
    }

    @Override
    public void configure() throws Exception {
//        from("jetty:http://" + channelSettings.getChannelRestBindHost() + ":" + channelSettings.getChannelRestBindPort()
//                + "/admin/contexts/{transactionId}?httpMethodRestrict=GET")
//                .id("admin-context-get-consumer")
//                .log("admin-context-get-consumer called!")
//                .process(exchange -> {
//                    HttpServletRequest httpServletRequest = exchange.getIn().getBody(HttpServletRequest.class);
//                    String transactionId = UrlParamUtil.extractFromContextPath(httpServletRequest.getPathInfo(), 3);
//                    exchange.getIn().setBody(transactionContextHolder.getTransactionContext(transactionId));
//                })
//                .marshal().json(JsonLibrary.Jackson)
//        ;
//
//        from("jetty:http://" + channelSettings.getChannelRestBindHost() + ":" + channelSettings.getChannelRestBindPort()
//                + "/admin/contexts/client/{channelClientRef}?httpMethodRestrict=GET")
//                .id("admin-context-get-by-channelclientref-consumer")
//                .log("admin-context-get-by-channeclientref-consumer called!")
//                .process(exchange -> {
//                    HttpServletRequest httpServletRequest = exchange.getIn().getBody(HttpServletRequest.class);
//                    String channelClientRef = UrlParamUtil.extractFromContextPath(httpServletRequest.getPathInfo(), 4);
//                    exchange.getIn().setBody(transactionContextHolder.getContextByChannelClientRef(channelClientRef));
//                })
//                .marshal().json(JsonLibrary.Jackson)
//        ;
    }
}
