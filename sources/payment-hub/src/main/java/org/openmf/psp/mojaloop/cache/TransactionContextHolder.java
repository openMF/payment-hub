/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.mojaloop.cache;

import java.util.HashSet;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.openmf.psp.dto.Party;
import org.openmf.psp.dto.PartyIdInfo;
import org.openmf.psp.dto.channel.TransactionChannelRequestDTO;
import org.openmf.psp.dto.fsp.PartyFspResponseDTO;
import org.openmf.psp.dto.fsp.QuoteFspResponseDTO;
import org.openmf.psp.dto.fsp.TransactionRequestFspResponseDTO;
import org.openmf.psp.dto.fsp.TransferFspResponseDTO;
import org.openmf.psp.internal.FspId;
import org.openmf.psp.mojaloop.dto.mojaloop.PartySwitchResponseDTO;
import org.openmf.psp.mojaloop.dto.mojaloop.QuoteSwitchRequestDTO;
import org.openmf.psp.mojaloop.dto.mojaloop.QuoteSwitchResponseDTO;
import org.openmf.psp.mojaloop.dto.mojaloop.TransactionRequestSwitchRequestDTO;
import org.openmf.psp.mojaloop.dto.mojaloop.TransactionRequestSwitchResponseDTO;
import org.openmf.psp.mojaloop.dto.mojaloop.TransferSwitchRequestDTO;
import org.openmf.psp.mojaloop.dto.mojaloop.TransferSwitchResponseDTO;
import org.openmf.psp.mojaloop.internal.Ilp;
import org.openmf.psp.mojaloop.internal.PartyContext;
import org.openmf.psp.mojaloop.internal.TransactionCacheContext;
import org.openmf.psp.mojaloop.internal.TransactionRoleContext;
import org.openmf.psp.type.TransactionRole;
import org.openmf.psp.util.ContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TransactionContextHolder {

    private static Logger logger = LoggerFactory.getLogger(TransactionContextHolder.class);

    private CacheManager cacheManager;
    private Cache<String, TransactionCacheContext> transactionContextCache; // transactionId -> transaction context
    private Cache<Integer, PartyContext> partyCache; // hashCode(party idType, id, subTypeOrId) -> party context
    private Cache<Integer, HashSet> partyTransactionCache; // WAITING transactions only! - hashCode(party idType, id, subTypeOrId) -> list of waiting transactionIds
    private Cache<String, String> channelClientRefCache; // clientRef -> transactionId
    private Cache<String, String> transferCache; // transferId -> transactionId


    @PostConstruct
    public void postConstruct() {
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("transactionContext",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, TransactionCacheContext.class, ResourcePoolsBuilder.heap(10000)))
                .withCache("partyCache",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(Integer.class, PartyContext.class, ResourcePoolsBuilder.heap(10000)))
                .withCache("partyTransactionCache",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(Integer.class, HashSet.class, ResourcePoolsBuilder.heap(10000)))
                .withCache("channelClientRefCache",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(10000)))
                .withCache("transferCache",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(10000)))
                .build();
        cacheManager.init();

        transactionContextCache = cacheManager.getCache("transactionContext", String.class, TransactionCacheContext.class);
        partyCache = cacheManager.getCache("partyCache", Integer.class, PartyContext.class);
        partyTransactionCache = cacheManager.getCache("partyTransactionCache", Integer.class, HashSet.class);
        channelClientRefCache = cacheManager.getCache("channelClientRefCache", String.class, String.class);
        transferCache = cacheManager.getCache("transferCache", String.class, String.class);
    }

    @PreDestroy
    public void preDestroy() {
        cacheManager.removeCache("transactionContext");
        cacheManager.removeCache("partyCache");
        cacheManager.removeCache("partyTransactionCache");
        cacheManager.removeCache("channelClientRef");
        cacheManager.removeCache("transferCache");
        cacheManager.close();
    }

    private void addTransactionContext(TransactionCacheContext transactionCacheContext) {
        String transactionId = transactionCacheContext.getTransactionId();
        transactionContextCache.put(transactionId, transactionCacheContext);
        for (TransactionRoleContext roleContext : transactionCacheContext.getRoleContexts().values()) {
            PartyContext cacheContext = putOrUpdateParty(roleContext.getPartyContext());
            roleContext.setPartyContext(cacheContext);
        }
        updateChannelClientRefCache(transactionCacheContext.getChannelClientRef(), transactionId);
        updateTransferCache(transactionCacheContext.getTransferId(), transactionId);
    }

    public TransactionCacheContext createTransactionCacheContext() {
        TransactionCacheContext transactionCacheContext = new TransactionCacheContext();
        addTransactionContext(transactionCacheContext);
        return transactionCacheContext;
    }

    private TransactionCacheContext getOrCreateTransactionCacheContext(String transactionId) {
        TransactionCacheContext transactionCacheContext = transactionContextCache.get(transactionId);
        if (transactionCacheContext == null) {
            transactionCacheContext = new TransactionCacheContext(transactionId);
            addTransactionContext(transactionCacheContext);
        }
        return transactionCacheContext;
    }

    public TransactionCacheContext getTransactionContext(String transactionId) {
        return transactionContextCache.get(transactionId);
    }

    private void updateChannelClientRefCache(String channelClientRef, String transactionId) {
        if (channelClientRef != null) {
            logger.info("Register channelClientRef " + channelClientRef + " for transaction " + transactionId);
            channelClientRefCache.put(channelClientRef, transactionId);
        }
    }

    public TransactionCacheContext getContextByChannelClientRef(String channelClientRef) {
        String transactionId = channelClientRefCache.get(channelClientRef);
        return transactionId == null ? null : transactionContextCache.get(transactionId);
    }

    public String getTransactionIdByChannelClientRef(String channelClientRef) {
        return channelClientRefCache.get(channelClientRef);
    }

    public PartyContext getPartyContext(PartyIdInfo idInfo) {
        return partyCache.get(idInfo.hashCode());
    }

    public void updateChannelPaymentRequest(String transactionId, TransactionChannelRequestDTO request) {
        updateChannelClientRefCache(request.getClientRefId(), transactionId);
        TransactionCacheContext transactionCacheContext = transactionContextCache.get(transactionId);

        Party payer = request.getPayer();
        updatePartyCache(transactionCacheContext, TransactionRole.PAYER, payer == null ? null : payer.getPartyIdInfo());
        Party payee = request.getPayee();
        updatePartyCache(transactionCacheContext, TransactionRole.PAYEE, payee == null ? null : payee.getPartyIdInfo());

        transactionCacheContext.updateChannelPaymentRequest(request);
    }

    public String[] getWaitingPartyTransactions(PartyIdInfo idInfo) {
        HashSet<String> transactions = partyTransactionCache.get(idInfo.hashCode());
        return transactions == null ? new String[0] : transactions.toArray(new String[transactions.size()]);
    }

    public void addWaitingPartyTransaction(PartyIdInfo idInfo, String transactionId) {
        if (idInfo == null || transactionId == null)
            return;

        HashSet<String> toUpdate = new HashSet<>(1);
        HashSet<String> transactions = partyTransactionCache.putIfAbsent(idInfo.hashCode(), toUpdate);
        if (transactions != null)
            toUpdate = transactions;

        toUpdate.add(transactionId);
    }

    public boolean removeWaitingPartyTransaction(PartyIdInfo idInfo, String transactionId) {
        HashSet transactions = partyTransactionCache.get(idInfo.hashCode());
        return transactions != null && transactions.remove(transactionId);
    }

    public String popWaitingPartyTransaction(PartyIdInfo idInfo) {
        String[] transactions = getWaitingPartyTransactions(idInfo);
        if (transactions == null || transactions.length == 0)
            return null;
        String transaction = transactions[0];
        removeWaitingPartyTransaction(idInfo, transaction);
        return transaction;
    }

    public String getTransactionByTransfer(String transferId) {
        return transferCache.get(transferId);
    }

    public String getOrCreateTransferId(String transactionId) {
        TransactionCacheContext transactionCacheContext = transactionContextCache.get(transactionId);
        String transferId = transactionCacheContext.getOrCreateTransferId();
        updateTransferCache(transferId, transactionId);
        return transferId;
    }

    private void updateTransferCache(String transferId, String transactionId) {
        if (transferId != null)
            transferCache.putIfAbsent(transferId, transactionId);
    }

    public void registerIlp(String transactionId, Ilp ilp) {
        TransactionCacheContext transactionCacheContext = transactionContextCache.get(transactionId);
        transactionCacheContext.setIlp(ilp);
    }

    public void updateFspParty(String transactionId, TransactionRole role, PartyFspResponseDTO response) {
        TransactionCacheContext transactionCacheContext = transactionContextCache.get(transactionId);
        transactionCacheContext.updateFspParty(role, response); // must be in the cache
    }

    public PartyContext updateFspParty(PartyIdInfo idInfo, PartyFspResponseDTO response) {
        return updatePartyCache(idInfo, response.getAccountId());
    }

    public void updateFspTransactionRequest(String transactionId, TransactionRole role, TransactionRequestFspResponseDTO response) {
        TransactionCacheContext transactionCacheContext = transactionContextCache.get(transactionId);
        transactionCacheContext.updateFspTransactionRequest(role, response);
    }

    public void updateFspQuote(String transactionId, TransactionRole role, QuoteFspResponseDTO response) {
        TransactionCacheContext transactionCacheContext = transactionContextCache.get(transactionId);
        transactionCacheContext.updateFspQuote(role, response);
    }

    public void updateFspPrepareTransfer(String transactionId, TransactionRole role, TransferFspResponseDTO response) {
        TransactionCacheContext transactionCacheContext = transactionContextCache.get(transactionId);
        transactionCacheContext.updateFspPrepareTransfer(role, response);
    }

    public void updateFspCommitTransfer(String transactionId, TransactionRole role, TransferFspResponseDTO response) {
        TransactionCacheContext transactionCacheContext = transactionContextCache.get(transactionId);
        transactionCacheContext.updateFspCommitTransfer(role, response);
    }

    public void updateSwitchParty(PartySwitchResponseDTO partiesDTO) {
        if (partiesDTO == null)
            return;
        putOrUpdateParty(new PartyContext(partiesDTO.getParty(), null)); // same partyContext instances on every transactionContexts are updated if cache is updated
    }

    public void updateSwitchTransactionRequest(String transactionId, TransactionRole sourceRole, TransactionRequestSwitchRequestDTO request) {
        TransactionCacheContext transactionCacheContext = getOrCreateTransactionCacheContext(transactionId);
        updateChannelClientRefCache(request.getExtensionValue(ContextUtil.EXTENSION_KEY_CHANNEL_CLIENT_REF), transactionId);

        updatePartyCache(transactionCacheContext, TransactionRole.PAYER, request.getPayer());
        Party payee = request.getPayee();
        updatePartyCache(transactionCacheContext, TransactionRole.PAYEE, payee == null ? null : payee.getPartyIdInfo());

        transactionCacheContext.updateSwitchTransactionRequest(sourceRole, request);
    }

    public void updateSwitchTransactionRequest(String transactionId, TransactionRole sourceRole, TransactionRequestSwitchResponseDTO response) {
        TransactionCacheContext transactionCacheContext = transactionContextCache.get(transactionId);
        transactionCacheContext.updateSwitchTransactionRequest(sourceRole, response);
    }

    public void updateSwitchQuoteRequest(String transactionId, TransactionRole sourceRole, QuoteSwitchRequestDTO request, FspId sourceFsp, FspId destFsp) {
        TransactionCacheContext transactionCacheContext = getOrCreateTransactionCacheContext(transactionId);
        updateChannelClientRefCache(request.getExtensionValue(ContextUtil.EXTENSION_KEY_CHANNEL_CLIENT_REF), transactionId);

        Party payer = request.getPayer();
        updatePartyCache(transactionCacheContext, TransactionRole.PAYER, payer == null ? null : payer.getPartyIdInfo());
        Party payee = request.getPayee();
        updatePartyCache(transactionCacheContext, TransactionRole.PAYEE, payee == null ? null : payee.getPartyIdInfo());

        transactionCacheContext.updateSwitchQuoteRequest(sourceRole, request, sourceFsp, destFsp);
    }

    public void updateSwitchQuote(String transactionId, TransactionRole sourceRole, QuoteSwitchResponseDTO response, Ilp ilp) {
        TransactionCacheContext transactionCacheContext = transactionContextCache.get(transactionId);
        transactionCacheContext.updateSwitchQuote(sourceRole, response, ilp);
    }

    public void updateSwitchTransferRequest(String transactionId, TransactionRole sourceRole, TransferSwitchRequestDTO request, Ilp ilp, FspId sourceFsp, FspId destFsp) {
        TransactionCacheContext transactionCacheContext = getOrCreateTransactionCacheContext(transactionId);
        updateChannelClientRefCache(request.getExtensionValue(ContextUtil.EXTENSION_KEY_CHANNEL_CLIENT_REF), transactionId);

        String payer = request.getPayerFsp();
        updatePartyCache(transactionCacheContext, TransactionRole.PAYER, payer);
        String payee = request.getPayeeFsp();
        updatePartyCache(transactionCacheContext, TransactionRole.PAYEE, payee);

        transactionCacheContext.updateSwitchTransferRequest(sourceRole, request, ilp, sourceFsp, destFsp);
        updateTransferCache(request.getTransferId(), transactionId);
    }

    public void updateSwitchTransfer(String transactionId, TransactionRole sourceRole, TransferSwitchResponseDTO response) {
        TransactionCacheContext transactionCacheContext = transactionContextCache.get(transactionId);
        transactionCacheContext.updateSwitchTransfer(sourceRole, response);
    }

    private PartyContext putOrUpdateParty(PartyContext oContext) {
        if (oContext == null)
            return null;

        int key = oContext.getParty().getPartyIdInfo().hashCode();
        PartyContext context = partyCache.putIfAbsent(key, oContext);
        if (context != null) {
            context.update(oContext);
        }
        return context == null ? oContext : context;
    }

    private PartyContext updatePartyCache(PartyIdInfo idInfo, String acountId) {
        return putOrUpdateParty(new PartyContext(new Party(idInfo), acountId));
    }

    private PartyContext updatePartyCache(PartyIdInfo idInfo) {
        return updatePartyCache(idInfo, null);
    }

    private void updatePartyCache(TransactionCacheContext transactionCacheContext, TransactionRole role, PartyIdInfo partyIdInfo) {
        if (partyIdInfo == null)
            return;

        TransactionRoleContext roleContext = transactionCacheContext.getRoleContext(role);
        if (roleContext.getPartyContext() == null)
            roleContext.setPartyContext(updatePartyCache(partyIdInfo));
    }

    private void updatePartyCache(TransactionCacheContext transactionCacheContext, TransactionRole role, String fspId) {
        if (fspId == null)
            return;

        TransactionRoleContext roleContext = transactionCacheContext.getRoleContext(role);
        if (roleContext.getPartyContext() == null) // can not update cache without PartyIdInfo
            roleContext.setPartyContext(new PartyContext(ContextUtil.parseFspId(fspId)));
    }
}
