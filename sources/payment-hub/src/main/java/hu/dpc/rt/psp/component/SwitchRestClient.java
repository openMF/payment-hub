/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.component;

import hu.dpc.rt.psp.config.HubSettings;
import hu.dpc.rt.psp.config.SwitchSettings;
import hu.dpc.rt.psp.config.TenantProperties;
import hu.dpc.rt.psp.dto.PartyIdInfo;
import hu.dpc.rt.psp.dto.mojaloop.ParticipantSwitchRequestDTO;
import hu.dpc.rt.psp.dto.mojaloop.PartySwitchResponseDTO;
import hu.dpc.rt.psp.dto.mojaloop.QuoteSwitchRequestDTO;
import hu.dpc.rt.psp.dto.mojaloop.QuoteSwitchResponseDTO;
import hu.dpc.rt.psp.dto.mojaloop.TransferSwitchRequestDTO;
import hu.dpc.rt.psp.dto.mojaloop.TransferSwitchResponseDTO;
import hu.dpc.rt.psp.internal.FspId;
import hu.dpc.rt.psp.type.IdentifierType;
import hu.dpc.rt.psp.util.JsonUtil;
import org.apache.logging.log4j.util.Strings;
import org.eclipse.jetty.http.HttpHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SwitchRestClient {

    private RestClient restClient;

    private HubSettings hubSettings;
    private SwitchSettings switchSettings;


    @Autowired
    public SwitchRestClient(RestClient restClient, HubSettings hubSettings, SwitchSettings switchSettings) {
        this.restClient = restClient;
        this.hubSettings = hubSettings;
        this.switchSettings = switchSettings;
    }

    public void callParticipantsPost(IdentifierType idType, String idValue, String subIdOrType, String tenant) {
        TenantProperties operation = switchSettings.getOperation(SwitchSettings.SwitchOperation.PARTICIPANTS, tenant);
        String url = operation.getUrl() + '/' + idType + '/' + idValue + (Strings.isEmpty(subIdOrType) ? "" : ('/' + subIdOrType));

        FspId sourceFspId = new FspId(hubSettings.getInstance(), tenant);
        Map<String, String> headers = getHeaders(sourceFspId, null);

        ParticipantSwitchRequestDTO request = new ParticipantSwitchRequestDTO();
        request.setFspId(sourceFspId.getId());

        restClient.call(url, HttpMethod.POST, headers, JsonUtil.toJson(request));
    }

    public void callGetParties(IdentifierType idType, String idValue, String subIdOrType, FspId sourceFspId) {
        String tenant = sourceFspId.getTenant();
        TenantProperties operation = switchSettings.getOperation(SwitchSettings.SwitchOperation.PARTIES, tenant);
        String url = operation.getUrl() + '/' + idType + '/' + idValue + (Strings.isEmpty(subIdOrType) ? "" : ('/' + subIdOrType));

        Map<String, String> headers = getHeaders(sourceFspId, null);
        headers.put(HttpHeader.CONTENT_TYPE.asString(), "application/vnd.interoperability.parties+json;version=1.0");
        headers.put(HttpHeader.ACCEPT.asString(), "application/vnd.interoperability.parties+json;version=1.0");

        restClient.call(url, HttpMethod.GET, headers, null);
    }

    public void callPutParties(PartySwitchResponseDTO response, FspId sourceFspId, FspId destFspId) {
        String tenant = sourceFspId.getTenant();
        TenantProperties operation = switchSettings.getOperation(SwitchSettings.SwitchOperation.PARTIES, tenant);

        Map<String, String> headers = getHeaders(sourceFspId, destFspId);
        headers.put(HttpHeader.CONTENT_TYPE.asString(), "application/vnd.interoperability.parties+json;version=1.0");
        headers.put(HttpHeader.ACCEPT.asString(), "application/vnd.interoperability.parties+json;version=1.0");

        PartyIdInfo idInfo = response.getParty().getPartyIdInfo();
        IdentifierType idType = idInfo.getPartyIdType();
        String idValue = idInfo.getPartyIdentifier();
        String subIdOrType = idInfo.getPartySubIdOrType();

        String url = operation.getUrl() + '/' + idType + '/' + idValue + (Strings.isEmpty(subIdOrType) ? "" : ('/' + subIdOrType));

        restClient.call(url, HttpMethod.PUT, headers, JsonUtil.toJson(response));
    }

    public void callPostQuotes(QuoteSwitchRequestDTO request, FspId sourceFspId, FspId destFspId) {
        String tenant = sourceFspId.getTenant();
        TenantProperties operation = switchSettings.getOperation(SwitchSettings.SwitchOperation.QUOTES, tenant);
        String url = operation.getUrl();

        Map<String, String> headers = getHeaders(sourceFspId, destFspId);
        headers.put(HttpHeader.CONTENT_TYPE.asString(), "application/vnd.interoperability.quotes+json;version=1.0");
        headers.put(HttpHeader.ACCEPT.asString(), "application/vnd.interoperability.quotes+json;version=1.0");

        restClient.call(url, HttpMethod.POST, headers, JsonUtil.toJson(request));
    }

    public void callPutQuotes(QuoteSwitchResponseDTO response, String quoteId, FspId sourceFspId, FspId destFspId) {
        String tenant = sourceFspId.getTenant();
        TenantProperties operation = switchSettings.getOperation(SwitchSettings.SwitchOperation.QUOTES, tenant);
        String url = operation.getUrl() + '/' + quoteId;

        Map<String, String> headers = getHeaders(sourceFspId, destFspId);
        headers.put(HttpHeader.CONTENT_TYPE.asString(), "application/vnd.interoperability.quotes+json;version=1.0");
        headers.put(HttpHeader.ACCEPT.asString(), "application/vnd.interoperability.quotes+json;version=1.0");

        restClient.call(url, HttpMethod.PUT, headers, JsonUtil.toJson(response));
    }

    public void callPostTransferCommit(TransferSwitchRequestDTO request, FspId sourceFspId, FspId destFspId) {
        String tenant = sourceFspId.getTenant();
        TenantProperties operation = switchSettings.getOperation(SwitchSettings.SwitchOperation.TRANSFERS, tenant);
        String url = operation.getUrl();

        Map<String, String> headers = getHeaders(sourceFspId, destFspId);
        headers.put(HttpHeader.ACCEPT.asString(), "application/vnd.interoperability.parties+json;version=1");
        headers.put(HttpHeader.CONTENT_TYPE.asString(), "application/vnd.interoperability.parties+json;version=1.0");

        restClient.call(url, HttpMethod.POST, headers, JsonUtil.toJson(request));
    }

    public void callPutTransferCommit(TransferSwitchResponseDTO response, String transferId, FspId sourceFspId, FspId destFspId) {
        String tenant = sourceFspId.getTenant();
        TenantProperties operation = switchSettings.getOperation(SwitchSettings.SwitchOperation.TRANSFERS, tenant);
        String url = operation.getUrl() + '/' + transferId;

        Map<String, String> headers = getHeaders(sourceFspId, destFspId);
        headers.put(HttpHeader.CONTENT_TYPE.asString(), "application/vnd.interoperability.parties+json;version=1.0");

        restClient.call(url, HttpMethod.PUT, headers, JsonUtil.toJson(response));
    }

    private Map<String, String> getHeaders(FspId sourceFspId, FspId destFspId) {
        Map<String, String> headers = new HashMap<>();

        headers.put(HttpHeader.DATE.asString(), RestClient.formatServerHeaderDate());
        if (sourceFspId != null)
            headers.put(switchSettings.getHeader(SwitchSettings.SwitchHeader.SOURCE).getKey(), sourceFspId.getId());
        if (destFspId != null)
            headers.put(switchSettings.getHeader(SwitchSettings.SwitchHeader.DESTINATION).getKey(), destFspId.getId());

        return headers;
    }
}
