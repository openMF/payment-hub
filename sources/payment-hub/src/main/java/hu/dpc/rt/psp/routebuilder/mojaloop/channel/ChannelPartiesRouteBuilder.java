/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.routebuilder.mojaloop.channel;

import hu.dpc.rt.psp.component.FspRestClient;
import hu.dpc.rt.psp.component.SwitchRestClient;
import hu.dpc.rt.psp.config.BindingProperties;
import hu.dpc.rt.psp.config.ChannelSettings;
import hu.dpc.rt.psp.config.HubSettings;
import hu.dpc.rt.psp.dto.mojaloop.channel.RegisterAliasRequestDTO;
import hu.dpc.rt.psp.dto.fsp.PartyFspResponseDTO;
import hu.dpc.rt.psp.internal.FspId;
import hu.dpc.rt.psp.type.IdentifierType;
import hu.dpc.rt.psp.util.UrlParamUtil;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Collection of /participants and /parties related endpoints called from the client channel.
 */
@Configuration
public class ChannelPartiesRouteBuilder extends RouteBuilder {

    private static Logger logger = LoggerFactory.getLogger(ChannelPartiesRouteBuilder.class);

    private ChannelSettings channelSettings;
    private HubSettings hubSettings;

    private FspRestClient fspRestClient;
    private SwitchRestClient switchRestClient;

    @Autowired
    public ChannelPartiesRouteBuilder(CamelContext camelContext, ChannelSettings channelSettings, HubSettings hubSettings,
                                      FspRestClient fspRestClient, SwitchRestClient switchRestClient) {
        super(camelContext);
        this.channelSettings = channelSettings;
        this.hubSettings = hubSettings;
        this.fspRestClient = fspRestClient;
        this.switchRestClient = switchRestClient;
    }

    @Override
    public void configure() throws Exception {
        getContext().getShutdownStrategy().setTimeout(1);

        BindingProperties binding = channelSettings.getBinding(ChannelSettings.ChannelBinding.PARTIES);
        boolean isCorsEnabled = channelSettings.isCorsEnabled();
        String url = binding.getUrl();

        buildRegisterAccountAliasRoute(url, isCorsEnabled);
        buildGetAccountAliasRoutes(url, isCorsEnabled);
    }

    public void buildRegisterAccountAliasRoute(String url, boolean isCorsEnabled) {
        String consumerEndpoint = "jetty:" + url + "?httpMethodRestrict=" + HttpMethod.POST + "&enableCORS=" + isCorsEnabled;

        from(consumerEndpoint)
                .id("channel-register-account-alias-post-consumer")
                .to("direct:register-account-alias-post");

        from("direct:register-account-alias-post")
                .id("register-account-alias-post")
                .unmarshal().json(JsonLibrary.Jackson, RegisterAliasRequestDTO.class)
                .process(exchange -> {
                    HttpServletRequest httpServletRequest = exchange.getIn().getBody(HttpServletRequest.class);

                    String tenant = httpServletRequest.getHeader(channelSettings.getHeader(ChannelSettings.ChannelHeader.TENANT).getKey());

                    RegisterAliasRequestDTO request = exchange.getIn().getBody(RegisterAliasRequestDTO.class);

                    fspRestClient.callPartiesPost(request.getAccountId(), request.getIdType(), request.getIdValue(), tenant);

                    switchRestClient.callParticipantsPost(request.getIdType(), request.getIdValue(), null, tenant);

                    //TODO: error handling, rollback in FSP when the switch responses


                    exchange.getIn().setBody(null);
                })
        ;
    }

    public void buildGetAccountAliasRoutes(String url, boolean isCorsEnabled) {
        HttpMethod httpMethod = HttpMethod.GET;
        String params = "?httpMethodRestrict=" + httpMethod + "&enableCORS=" + isCorsEnabled;

        String getPartiesEndpoint = "jetty:" + url + "/{idType}/{idValue}" + params;
        String getPartiesWithSubTypeEndpoint = "jetty:" + url + "/{idType}/{idValue}/{subIdOrType}" + params;


        String idTypePropertyName = "Id-Type";
        String idValuePropertyName = "Id-Value";
        String subIdOrTypePropertyName = "SubId-Or-Type";

        from(getPartiesEndpoint)
                .id("channel-parties-get-consumer")
                .to("direct:parties-get-extract-query-params");

        from(getPartiesWithSubTypeEndpoint)
                .id("channel-parties-with-subtype-get-consumer")
                .to("direct:parties-with-subtype-get-extract-query-params");

        from("direct:parties-get-extract-query-params")
                .id("parties-get-extract-query-params")
                .process(exchange -> {
                    HttpServletRequest httpServletRequest = exchange.getIn().getBody(HttpServletRequest.class);

                    String idType = UrlParamUtil.extractFromContextPath(httpServletRequest.getPathInfo(), 3);
                    String idValue = UrlParamUtil.extractFromContextPath(httpServletRequest.getPathInfo(), 4);

                    exchange.setProperty(idTypePropertyName, idType);
                    exchange.setProperty(idValuePropertyName, idValue);
                })
                .to("direct:parties-get");

        from("direct:parties-with-subtype-get-extract-query-params")
                .id("parties-with-subtype-get-extract-query-params")
                .process(exchange -> {
                    HttpServletRequest httpServletRequest = exchange.getIn().getBody(HttpServletRequest.class);

                    String idType = UrlParamUtil.extractFromContextPath(httpServletRequest.getPathInfo(), 3);
                    String idValue = UrlParamUtil.extractFromContextPath(httpServletRequest.getPathInfo(), 4);
                    String subIdOrType = UrlParamUtil.extractFromContextPath(httpServletRequest.getPathInfo(), 5);

                    exchange.setProperty(idTypePropertyName, idType);
                    exchange.setProperty(idValuePropertyName, idValue);
                    exchange.setProperty(subIdOrTypePropertyName, subIdOrType);
                })
                .to("direct:parties-get");



        from("direct:parties-get")
                .id("parties-get")
                .process(exchange -> {
                    HttpServletRequest httpServletRequest = exchange.getIn().getBody(HttpServletRequest.class);

                    String tenant = httpServletRequest.getHeader(channelSettings.getHeader(ChannelSettings.ChannelHeader.TENANT).getKey());

                    String idType = exchange.getProperty(idTypePropertyName, String.class);
                    String idValue = exchange.getProperty(idValuePropertyName, String.class);
                    String subIdOrType = exchange.getProperty(subIdOrTypePropertyName, String.class);

                    FspId fspId = new FspId(hubSettings.getInstance(), tenant);

                    PartyFspResponseDTO partiesResponseDTO = fspRestClient.callParties(IdentifierType.valueOf(idType.toUpperCase()), idValue, subIdOrType, fspId);

                    exchange.getIn().setBody(partiesResponseDTO);
                })
                .marshal().json(JsonLibrary.Jackson);
    }
}
