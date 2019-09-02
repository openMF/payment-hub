/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.cache;

import hu.dpc.rt.psp.dto.Party;
import hu.dpc.rt.psp.dto.PartyIdInfo;
import hu.dpc.rt.psp.dto.mojaloop.channel.TransactionChannelRequestDTO;
import hu.dpc.rt.psp.dto.fsp.PartyFspResponseDTO;
import hu.dpc.rt.psp.dto.fsp.QuoteFspResponseDTO;
import hu.dpc.rt.psp.dto.fsp.TransactionRequestFspResponseDTO;
import hu.dpc.rt.psp.dto.fsp.TransferFspResponseDTO;
import hu.dpc.rt.psp.dto.mojaloop.PartySwitchResponseDTO;
import hu.dpc.rt.psp.dto.mojaloop.QuoteSwitchRequestDTO;
import hu.dpc.rt.psp.dto.mojaloop.QuoteSwitchResponseDTO;
import hu.dpc.rt.psp.dto.mojaloop.TransactionRequestSwitchRequestDTO;
import hu.dpc.rt.psp.dto.mojaloop.TransactionRequestSwitchResponseDTO;
import hu.dpc.rt.psp.dto.mojaloop.TransferSwitchRequestDTO;
import hu.dpc.rt.psp.dto.mojaloop.TransferSwitchResponseDTO;
import hu.dpc.rt.psp.internal.FspId;
import hu.dpc.rt.psp.internal.Ilp;
import hu.dpc.rt.psp.internal.PartyContext;
import hu.dpc.rt.psp.internal.TransactionCacheContext;
import hu.dpc.rt.psp.internal.TransactionRoleContext;
import hu.dpc.rt.psp.type.TransactionRole;
import hu.dpc.rt.psp.util.ContextUtil;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashSet;

@Component
public class TransactionContextHolder {

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
        String channelClientRef = transactionCacheContext.getChannelClientRef();
        if (channelClientRef != null) {
            channelClientRefCache.put(channelClientRef, transactionId);
        }
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
