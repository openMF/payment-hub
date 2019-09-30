/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.internal;

import hu.dpc.rt.psp.constant.ExchangeHeader;
import hu.dpc.rt.psp.dto.Extension;
import hu.dpc.rt.psp.dto.FspMoneyData;
import hu.dpc.rt.psp.dto.GeoCode;
import hu.dpc.rt.psp.dto.MoneyData;
import hu.dpc.rt.psp.dto.PartyIdInfo;
import hu.dpc.rt.psp.dto.TransactionType;
import hu.dpc.rt.psp.dto.channel.TransactionChannelRequestDTO;
import hu.dpc.rt.psp.dto.fsp.PartyFspResponseDTO;
import hu.dpc.rt.psp.dto.fsp.QuoteFspResponseDTO;
import hu.dpc.rt.psp.dto.fsp.TransactionRequestFspResponseDTO;
import hu.dpc.rt.psp.dto.fsp.TransferFspResponseDTO;
import hu.dpc.rt.psp.dto.mojaloop.QuoteSwitchRequestDTO;
import hu.dpc.rt.psp.dto.mojaloop.QuoteSwitchResponseDTO;
import hu.dpc.rt.psp.dto.mojaloop.TransactionRequestSwitchRequestDTO;
import hu.dpc.rt.psp.dto.mojaloop.TransactionRequestSwitchResponseDTO;
import hu.dpc.rt.psp.dto.mojaloop.TransferSwitchRequestDTO;
import hu.dpc.rt.psp.dto.mojaloop.TransferSwitchResponseDTO;
import hu.dpc.rt.psp.type.AmountType;
import hu.dpc.rt.psp.type.AuthenticationType;
import hu.dpc.rt.psp.type.TransactionRole;
import hu.dpc.rt.psp.type.TransactionState;
import hu.dpc.rt.psp.type.TransferState;
import hu.dpc.rt.psp.util.ContextUtil;
import hu.dpc.rt.psp.util.UUIDUtil;
import org.apache.camel.Exchange;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionCacheContext {

    private String transactionId;
    private String channelClientRef;
    private String transactionRequestId;
    private String quoteId;
    private String transferId;
    private TransactionChannelRequestDTO paymentRequestDTO;
    private TransactionType transactionType;
    private String currency;
    private AmountType amountType;
    private BigDecimal transactionAmount;
    private BigDecimal prepareAmount;
    private BigDecimal transferAmount;
    private String note;
    private GeoCode geoCode;
    private AuthenticationType authenticationType;
    private Ilp ilp;
    private LocalDateTime expiration;
    private List<Extension> extensionList;

    private Map<TransactionRole, TransactionRoleContext> roleContexts = new HashMap<>(TransactionRole.VALUES.length);

    public TransactionCacheContext(String transactionId) {
        for (TransactionRole role : TransactionRole.VALUES) {
            roleContexts.put(role, new TransactionRoleContext(role));
        }
        this.transactionId = transactionId;
    }

    public TransactionCacheContext() {
        this(UUIDUtil.generateUUID());
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getOrCreateTransactionId() {
        return transactionId == null ? (transactionId = UUIDUtil.generateUUID()) : transactionId;
    }

    public String getChannelClientRef() {
        return channelClientRef;
    }

    public String getTransactionRequestId() {
        return transactionRequestId;
    }

    public String getOrCreateTransactionRequestId() {
        return transactionRequestId == null ? (transactionRequestId = UUIDUtil.generateUUID()) : transactionRequestId;
    }

    void setTransactionRequestId(String transactionRequestId) {
        this.transactionRequestId = transactionRequestId;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public String getOrCreateQuoteId() {
        return quoteId == null ? (quoteId = UUIDUtil.generateUUID()) : quoteId;
    }

    void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getTransferId() {
        return transferId;
    }

    public String getOrCreateTransferId() {
        return transferId == null ? (transferId = UUIDUtil.generateUUID()) : transferId;
    }

    void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    public TransactionChannelRequestDTO getPaymentRequestDTO() {
        return paymentRequestDTO;
    }

    private void updateAmounts(MoneyData amount) {
        if (transactionAmount == null)
            transactionAmount = amount.getAmountDecimal();
        if (currency == null)
            currency = amount.getCurrency();
        prepareAmount = transactionAmount;
        transferAmount = transactionAmount;
    }

    public AmountType getAmountType() {
        return amountType;
    }

    void setAmountType(AmountType amountType) {
        this.amountType = amountType;
    }

    public String getCurrency() {
        return currency;
    }

    void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public BigDecimal getPrepareAmount() {
        return prepareAmount;
    }

    public void setPrepareAmount(BigDecimal prepareAmount) {
        this.prepareAmount = prepareAmount;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getNote() {
        return note;
    }

    void setNote(String note) {
        this.note = note;
    }

    public GeoCode getGeoCode() {
        return geoCode;
    }

    void setGeoCode(GeoCode geoCode) {
        this.geoCode = geoCode;
    }

    public AuthenticationType getAuthenticationType() {
        return authenticationType;
    }

    void setAuthenticationType(AuthenticationType authenticationType) {
        this.authenticationType = authenticationType;
    }

    public LocalDateTime getExpiration() {
        return expiration;
    }

    void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }

    public Ilp getIlp() {
        return ilp;
    }

    public void setIlp(Ilp ilp) {
        this.ilp = ilp;
    }

    public LocalDateTime getCompletedStamp() {
        return getRoleContext(TransactionRole.PAYER).getCompletedStamp();
    }

    public TransactionState getTransactionState() {
        return getRoleContext(TransactionRole.PAYER).getTransactionState();
    }

    public TransferState getTransferState() {
        return getRoleContext(TransactionRole.PAYER).getTransferState();
    }

    public List<Extension> getExtensionList() {
        return extensionList;
    }

    void setExtensionList(List<Extension> extensionList) {
        this.extensionList = extensionList;
    }

    public Map<TransactionRole, TransactionRoleContext> getRoleContexts() {
        return roleContexts;
    }

    public TransactionRoleContext getRoleContext(TransactionRole role) {
        return roleContexts.get(role);
    }

    public TransactionRoleContext getRoleContext(FspId fspId) {
        if (fspId == null)
            return null;

        for (TransactionRoleContext roleContext : roleContexts.values()) {
            if (fspId.equals(roleContext.getFspId()))
                return roleContext;
        }
        return null;
    }

    public TransactionRoleContext getRoleContext(PartyIdInfo idInfo) {
        if (idInfo == null)
            return null;

        for (TransactionRoleContext roleContext : roleContexts.values()) {
            PartyContext partyContext = roleContext.getPartyContext();
            if (partyContext == null)
                continue;
            if (idInfo.equals(partyContext.getParty().getPartyIdInfo()))
                return roleContext;
        }
        return null;
    }

    void setRoleContexts(Map<TransactionRole, TransactionRoleContext> roleContexts) {
        this.roleContexts = roleContexts;
    }

    public void updateChannelPaymentRequest(TransactionChannelRequestDTO request) {
        this.paymentRequestDTO = request;
        channelClientRef = request.getClientRefId();
        setAmountType(request.getAmountType());
        updateAmounts(request.getAmount());
        transactionType = request.getTransactionType();
        note = request.getNote();
        geoCode = request.getGeoCode();
        LocalDateTime oExpiration = request.getExpirationDate();
        this.expiration = oExpiration == null ? LocalDateTime.now().plus(5, ChronoUnit.MINUTES) : oExpiration; // TODO: use property
        extensionList = request.getExtensionList();
        for (TransactionRoleContext roleContext : roleContexts.values()) {
            roleContext.updatePaymentRequest(request);
        }
    }

    public void updateFspParty(TransactionRole role, PartyFspResponseDTO response) {
        TransactionRoleContext roleContext = getRoleContext(role);
        roleContext.setFspPartyDTO(response);
    }

    public void updateFspTransactionRequest(TransactionRole role, TransactionRequestFspResponseDTO response) {
        getRoleContext(role).updateFspTransactionRequest(response);
    }

    public void updateFspQuote(TransactionRole role, QuoteFspResponseDTO response) {
        getRoleContext(role).updateFspQuote(response);
        FspMoneyData fspFee = response.getFspFee();
        FspMoneyData fspCommission = response.getFspCommission();
        updateQuote(role, fspFee == null ? null : fspFee.getAmount(), fspCommission == null ? null : fspCommission.getAmount());
        updateExpiration(response.getExpiration());
    }

    public void updateFspPrepareTransfer(TransactionRole role, TransferFspResponseDTO response) {
        getRoleContext(role).updateFspPrepareTransfer(response);
        updateExpiration(response.getExpiration());
    }

    public void updateFspCommitTransfer(TransactionRole role, TransferFspResponseDTO response) {
        getRoleContext(role).updateFspCommitTransfer(response);
        updateExpiration(response.getExpiration());
    }

    public void updateSwitchTransactionRequest(TransactionRole sourceRole, TransactionRequestSwitchRequestDTO request) {
        setTransactionRequestId(request.getTransactionRequestId());
        updateAmounts(request.getAmount());
        setTransactionType(request.getTransactionType());
        setNote(request.getNote());
        setGeoCode(request.getGeoCode());
        setExpiration(request.getExpiration());
        setExtensionList(request.getExtensionList());
        updateExpiration(request.getExpiration());

        getRoleContext(sourceRole).updateSwitchTransactionRequest(request);
        getRoleContext(TransactionRole.PAYER).updateParty(request.getPayer()); // source must be the PAYEE
    }

    public void updateSwitchTransactionRequest(TransactionRole sourceRole, TransactionRequestSwitchResponseDTO response) {
        getRoleContext(sourceRole).updateSwitchTransactionRequest(response);
    }

    public void updateSwitchQuoteRequest(TransactionRole sourceRole, QuoteSwitchRequestDTO request, FspId sourceFsp, FspId destFsp) {
        if (this.transactionId == null)
            setTransactionId(request.getTransactionId());

        setTransactionRequestId(request.getTransactionRequestId());
        setQuoteId(request.getQuoteId());
        updateAmounts(request.getAmount());
        setTransactionType(request.getTransactionType());
        setAmountType(request.getAmountType());
        setNote(request.getNote());
        setGeoCode(request.getGeoCode());
        setExpiration(request.getExpirationDate());
        setExtensionList(request.getExtensionList());
        updateExpiration(request.getExpirationDate());

        getRoleContext(sourceRole).updateSwitchQuoteRequest(request, sourceFsp, destFsp);
        // source must be the PAYER
        TransactionRoleContext payeeContext = getRoleContext(TransactionRole.PAYEE);
        payeeContext.updateParty(request.getPayee());
        payeeContext.setFspId(destFsp);
    }

    public void updateSwitchQuote(TransactionRole sourceRole, QuoteSwitchResponseDTO response, Ilp ilp) {
        MoneyData fspFee = response.getPayeeFspFee();
        MoneyData fspCommission = response.getPayeeFspCommission();
        updateQuote(sourceRole, fspFee == null ? null : fspFee.getAmountDecimal(), fspCommission == null ? null : fspCommission.getAmountDecimal());
        updateExpiration(response.getExpirationDate());
        updateIlp(ilp);

        getRoleContext(sourceRole).updateSwitchQuote(response, ilp);
    }

    public void updateSwitchTransferRequest(TransactionRole sourceRole, TransferSwitchRequestDTO request, Ilp ilp, FspId sourceFsp, FspId destFsp) {
        setTransferId(request.getTransferId());
        updateAmounts(request.getAmount());
        setExpiration(request.getExpirationDate());
        setExtensionList(request.getExtensionList());
        updateExpiration(request.getExpirationDate());
        updateIlp(ilp);

        getRoleContext(sourceRole).updateSwitchTransferRequest(request, ilp, sourceFsp, destFsp);
        // source must be the PAYER
        TransactionRoleContext payeeContext = getRoleContext(TransactionRole.PAYEE);
        payeeContext.setFspId(ContextUtil.parseFspId(request.getPayeeFsp()));
        payeeContext.setFspId(destFsp);
    }

    public void updateSwitchTransfer(TransactionRole sourceRole, TransferSwitchResponseDTO response) {
        getRoleContext(sourceRole).updateSwitchTransfer(response);
    }

    private void updateQuote(TransactionRole role, BigDecimal fspFee, BigDecimal fspCommission) {
        if (role == TransactionRole.PAYEE) {
            if (fspFee != null && getAmountType() == AmountType.RECEIVE) {
                prepareAmount = prepareAmount.add(fspFee);
                transferAmount = prepareAmount;
            }
        } else {
            if (fspFee != null) {
                prepareAmount = prepareAmount.add(fspFee);
            }
            if (fspCommission != null) {
                prepareAmount = prepareAmount.subtract(fspCommission);
            }
            if (fspFee != null && getAmountType() == AmountType.SEND) {
                transferAmount = transactionAmount.subtract(fspFee);
            }
        }
    }

    private void updateExpiration(LocalDateTime oExpiration) {
        if (oExpiration != null && oExpiration.isBefore(expiration))
            expiration = oExpiration;

        // Note: The new quoting service is sending back expiration in local time.
        if (expiration.isBefore(LocalDateTime.now())) //TODO change to dynamic timer and based on 'expiration' property setting
            throw new RuntimeException("Validity of request " + transactionId + " expired");
    }

    private void updateIlp(Ilp ilp) {
        if (this.ilp == null)
            setIlp(ilp);
        else
            this.ilp.update(ilp);
    }

    public boolean isFailed() {
        return getRoleContext(TransactionRole.PAYER).isFailed() || getRoleContext(TransactionRole.PAYEE).isFailed();
    }

    public void populateExchangeContext(TransactionRole currentRole, Exchange exchange) {
        exchange.setProperty(ExchangeHeader.TRANSACTION_ID.getKey(), transactionId);
        exchange.setProperty(ExchangeHeader.CURRENT_ROLE.getKey(), currentRole);
        exchange.setProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), getRoleContext(currentRole).getFspId());
        exchange.setProperty(ExchangeHeader.PAYER_FSP_ID.getKey(), getRoleContext(TransactionRole.PAYER).getFspId());
        exchange.setProperty(ExchangeHeader.PAYEE_FSP_ID.getKey(), getRoleContext(TransactionRole.PAYEE).getFspId());
        exchange.setProperty(ExchangeHeader.CALLER_FSP.getKey(), null);
    }

    public void populateExchangeContext(FspId currentFsp, Exchange exchange) {
        populateExchangeContext(getRoleContext(currentFsp).getRole(), exchange);
    }
}
