/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.component;

import org.eclipse.jetty.http.HttpHeader;
import org.openmf.psp.config.AuthEncodeType;
import org.openmf.psp.config.AuthProperties;
import org.openmf.psp.config.FspSettings;
import org.openmf.psp.config.HubSettings;
import org.openmf.psp.config.TenantProperties;
import org.openmf.psp.dto.fsp.LoginFspResponse;
import org.openmf.psp.dto.fsp.PartyFspResponseDTO;
import org.openmf.psp.dto.fsp.QuoteFspRequestDTO;
import org.openmf.psp.dto.fsp.QuoteFspResponseDTO;
import org.openmf.psp.dto.fsp.TransactionRequestFspRequestDTO;
import org.openmf.psp.dto.fsp.TransactionRequestFspResponseDTO;
import org.openmf.psp.dto.fsp.TransferFspRequestDTO;
import org.openmf.psp.dto.fsp.TransferFspResponseDTO;
import org.openmf.psp.internal.FspId;
import org.openmf.psp.mojaloop.type.TransactionActionType;
import org.openmf.psp.type.IdentifierType;
import org.openmf.psp.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class FspRestClient {

    private static Logger logger = LoggerFactory.getLogger(FspRestClient.class);

    private RestClient restClient;

    private HubSettings hubSettings;
    private FspSettings fspSettings;

    private Map<String, TenantAuth> tenantAuthDataCache = new HashMap<>();


    FspRestClient() {
    }

    @Autowired
    public FspRestClient(RestClient restClient, HubSettings hubSettings, FspSettings fspSettings) {
        this.restClient = restClient;
        this.hubSettings = hubSettings;
        this.fspSettings = fspSettings;
    }

    @PostConstruct
    public void postConstruct() {
        AuthEncodeType encode = fspSettings.getAuth().getEncode();
        hubSettings.getTenants().forEach(tenant -> {
            TenantAuth tenantAuthData = new TenantAuth();
            TenantProperties authOp = fspSettings.getOperation(FspSettings.FspOperation.AUTH, tenant);
            tenantAuthData.setUser(authOp.getUser());
            tenantAuthData.setPassword(encode.encode(authOp.getPassword()));
            tenantAuthData.setTenant(tenant);
            tenantAuthDataCache.put(tenant, tenantAuthData);
        });
    }


    public PartyFspResponseDTO callParties(IdentifierType identifierType, String idValue, String subIdOrType, FspId fspId) {
        logger.debug(String.format("Sending GET /parties. Type: %s, value: %s, subType: %s", identifierType, idValue, subIdOrType));

        String tenant = fspId.getTenant();
        TenantProperties operation = fspSettings.getOperation(FspSettings.FspOperation.PARTIES, tenant);
        String url = operation.getUrl() + '/' + identifierType + '/' + idValue;
        if (subIdOrType != null)
            url += '/' + subIdOrType;

        Map<String, String> headers = getHeaders(tenant);

        String responseJson = restClient.call(url, HttpMethod.GET, headers, null);

        logger.debug(String.format("GET /parties response. Type: %s, value: %s, subType: %s, payload: %s", identifierType, idValue, subIdOrType, responseJson));
        return JsonUtil.toPojo(responseJson, PartyFspResponseDTO.class);
    }

    public PartyFspResponseDTO callPartiesPost(String accountId, IdentifierType identifierType, String idValue, String tenant) {
        logger.debug(String.format("Sending POST request parties. Type: %s, value: %s, accountId: %s", identifierType, idValue, accountId));

        TenantProperties operation = fspSettings.getOperation(FspSettings.FspOperation.PARTIES, tenant);
        String url = operation.getUrl() + '/' + identifierType + '/' + idValue;

        Map<String, String> headers = getHeaders(tenant);

        PartyFspResponseDTO request = new PartyFspResponseDTO();
        request.setAccountId(accountId);

        String responseJson = restClient.call(url, HttpMethod.POST, headers, JsonUtil.toJson(request));

        logger.debug(String.format("Parties POST response. AccountId: %s, payload: %s", accountId, responseJson));
        return JsonUtil.toPojo(responseJson, PartyFspResponseDTO.class);
    }

    public TransactionRequestFspResponseDTO callTransactionRequest(TransactionRequestFspRequestDTO request, FspId fspId) {
        String tenant = fspId.getTenant();
        TenantProperties operation = fspSettings.getOperation(FspSettings.FspOperation.TRANSFERS, tenant);
        String url = operation.getUrl();

        Map<String, String> headers = getHeaders(tenant);

        String requestJson = JsonUtil.toJson(request);
        logger.debug(String.format("Sending transaction request, ayload: %s", requestJson));

        String responseJson = restClient.call(url, HttpMethod.POST, headers, requestJson);

        logger.debug(String.format("Transaction request response, payload: %s", responseJson));
        return JsonUtil.toPojo(responseJson, TransactionRequestFspResponseDTO.class);
    }

    public QuoteFspResponseDTO callQuotes(QuoteFspRequestDTO request, FspId fspId) {
        String tenant = fspId.getTenant();
        TenantProperties operation = fspSettings.getOperation(FspSettings.FspOperation.QUOTES, tenant);
        String url = operation.getUrl();

        Map<String, String> headers = getHeaders(tenant);

        String requestJson = JsonUtil.toJson(request);
        logger.debug(String.format("Sending quotes request. Transaction id: (%s), payload: %s", request.getTransactionCode(), requestJson));

        String responseJson = restClient.call(url, HttpMethod.POST, headers, requestJson);

        logger.debug(String.format("Quotes response. Transaction id: (%s), payload: %s", request.getTransactionCode(), responseJson));
        return JsonUtil.toPojo(responseJson, QuoteFspResponseDTO.class);
}

    public TransferFspResponseDTO callPrepareTransfer(TransferFspRequestDTO request, FspId fspId) {
        return sendTransfer(request, fspId, TransactionActionType.PREPARE);
    }

    public TransferFspResponseDTO callTransferCommit(TransferFspRequestDTO request, FspId fspId) {
        return sendTransfer(request, fspId, TransactionActionType.CREATE);
    }

    private TransferFspResponseDTO sendTransfer(TransferFspRequestDTO request, FspId fspId, TransactionActionType transactionActionType) {
        String tenant = fspId.getTenant();
        TenantProperties operation = fspSettings.getOperation(FspSettings.FspOperation.TRANSFERS, tenant);
        String url = operation.getUrl() + "?action=" + transactionActionType.getName();

        Map<String, String> headers = getHeaders(tenant);

        String requestJson = JsonUtil.toJson(request);
        logger.debug(String.format("Sending %s transfer request. Transaction id: (%s), payload: %s", transactionActionType.getName(), request.getTransactionCode(), requestJson));

        String responseJson = restClient.call(url, HttpMethod.POST, headers, requestJson);

        logger.debug(String.format("%s transaction response. Transaction id: (%s), payload: %s", transactionActionType.getName(), request.getTransactionCode(), responseJson));
        return JsonUtil.toPojo(responseJson, TransferFspResponseDTO.class);
    }

    private Map<String, String> getHeaders(String tenant) {
        Map<String, String> headers = new HashMap<>();
        TenantAuth tenantAuthData = getTenantAuthData(tenant);

        headers.put(HttpHeader.AUTHORIZATION.asString(), tenantAuthData.getCachedAuthHeader());
        headers.put(fspSettings.getHeader(FspSettings.FspHeader.TENANT).getKey(), tenant);
        headers.put(fspSettings.getHeader(FspSettings.FspHeader.USER).getKey(), tenantAuthData.getUser());

        return headers;
    }

    private TenantAuth getTenantAuthData(String tenant) {
        TenantAuth tenantAuthData = tenantAuthDataCache.get(tenant);
        if (tenantAuthData == null) {
            throw new RuntimeException(String.format("Could not call login on FSP, because the provided tenant is not configured! Tenant: %s", tenant));
        }
        if (StringUtils.isEmpty(tenantAuthData.getCachedAuthHeader()) || accessTokenExpired(tenantAuthData.getAccessTokenExpiration())) {
            login(tenantAuthData);
        }
        return tenantAuthData;
    }

    /**
     * Logins with the provided tenantAuthData and updates the accessToken and accessTokenExpiration entries in the passed tenantAuthData parameter
     *
     * @param tenantAuthData tenantAuthData that should be used for the login
     */
    private void login(TenantAuth tenantAuthData) {
        String tenant = tenantAuthData.getTenant();
        TenantProperties operation = fspSettings.getOperation(FspSettings.FspOperation.AUTH, tenant);
        String url = operation.getUrl() + "?grant_type=password&username=" + tenantAuthData.getUser() + "&password=" + tenantAuthData.getPassword();

        Map<String, String> headers = new HashMap<>();
        headers.put(fspSettings.getHeader(FspSettings.FspHeader.TENANT).getKey(), tenant);

        String responseJson = restClient.call(url, HttpMethod.POST, headers, null);

        AuthProperties auth = fspSettings.getAuth();
        LoginFspResponse loginResponseDTO = JsonUtil.toPojo(responseJson, auth.getLoginClass());
        tenantAuthData.setCachedAuthHeader(auth.getProfile().encode(loginResponseDTO.getAccessToken()));
        tenantAuthData.setAccessTokenExpiration(loginResponseDTO.getAccessTokenExpiration());
    }

    private boolean accessTokenExpired(Date accessTokenExpiration) {
        if (accessTokenExpiration == null)
            return false;

        Date fiveMinsFromNow = new Date(System.currentTimeMillis() + 300 * 1000);
        return accessTokenExpiration.before(fiveMinsFromNow);
    }

    public static class TenantAuth {
        private String tenant;
        private String cachedAuthHeader;
        private String user;
        private String password;
        private Date accessTokenExpiration;

        public String getTenant() {
            return tenant;
        }

        public void setTenant(String tenant) {
            this.tenant = tenant;
        }

        public String getCachedAuthHeader() {
            return cachedAuthHeader;
        }

        public void setCachedAuthHeader(String cachedAuthHeader) {
            this.cachedAuthHeader = cachedAuthHeader;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Date getAccessTokenExpiration() {
            return accessTokenExpiration;
        }

        public void setAccessTokenExpiration(Date accessTokenExpiration) {
            this.accessTokenExpiration = accessTokenExpiration;
        }
    }

}
