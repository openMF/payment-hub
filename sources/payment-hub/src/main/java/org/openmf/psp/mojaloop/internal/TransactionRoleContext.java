/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.mojaloop.internal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.openmf.psp.dto.FspMoneyData;
import org.openmf.psp.dto.MoneyData;
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
import org.openmf.psp.mojaloop.type.TransactionAction;
import org.openmf.psp.mojaloop.type.TransactionState;
import org.openmf.psp.mojaloop.type.TransferState;
import org.openmf.psp.type.TransactionRequestState;
import org.openmf.psp.type.TransactionRole;
import org.openmf.psp.util.ContextUtil;

public class TransactionRoleContext {

    private static Logger logger = LoggerFactory.getLogger(TransactionRoleContext.class);

    private TransactionRole role;
    private PartyContext partyContext;

    private MoneyData fee;
    private MoneyData commission;

    private List<TransactionAction> actions = new ArrayList<>(TransactionAction.VALUES.length);

    private LocalDateTime completedStamp;
    private TransactionState transactionState;
    private TransferState transferState;

    // for one role only fsp or switch responses are set
    private PartyFspResponseDTO fspPartyDTO;
    private TransactionRequestFspResponseDTO fspRequestDTO;
    private QuoteFspResponseDTO fspQuoteDTO;
    private TransferFspResponseDTO fspPrepareTransferDTO;
    private TransferFspResponseDTO fspTransferDTO;

    private PartySwitchResponseDTO switchPartyDTO;
    private TransactionRequestSwitchResponseDTO switchRequestDTO;
    private QuoteSwitchResponseDTO switchQuoteDTO;
    private TransferSwitchResponseDTO switchTransferDTO;

    TransactionRoleContext() {
    }

    public TransactionRoleContext(TransactionRole role, TransactionChannelRequestDTO paymentRequestDTO) {
        this.role = role;
        transactionState = TransactionState.RECEIVED;
    }

    public TransactionRoleContext(TransactionRole role) {
        this(role, null);
    }

    public TransactionRole getRole() {
        return role;
    }

    public void setRole(TransactionRole role) {
        this.role = role;
    }

    public PartyContext getPartyContext() {
        return partyContext;
    }

    public void setPartyContext(PartyContext partyContext) {
        this.partyContext = partyContext;
    }

    public FspId getFspId() {
        return partyContext == null ? null : partyContext.getFspId();
    }

    public void setFspId(FspId fspId) {
        partyContext.setFspId(fspId);
    }

    public MoneyData getFee() {
        return fee;
    }

    public void setFee(MoneyData fee) {
        this.fee = fee;
    }

    public void setFee(FspMoneyData fee) {
        this.fee = FspMoneyData.toMoneyData(fee);
    }

    public MoneyData getCommission() {
        return commission;
    }

    public void setCommission(MoneyData commission) {
        this.commission = commission;
    }

    public void setCommission(FspMoneyData commission) {
        this.commission = FspMoneyData.toMoneyData(commission);
    }

    List<TransactionAction> getActions() {
        return actions;
    }

    public TransactionAction getLastAction() {
        int size = actions.size();
        return size == 0 ? null : actions.get(size - 1);
    }

    TransactionAction addAction(TransactionAction action) {
        return addAction(action, true);
    }

    TransactionAction addAction(TransactionAction action, boolean strict) {
        logger.debug("Action " + action + " on " + this);
        TransactionAction lastAction = getLastAction();
        if (lastAction == action && !strict)
            return lastAction;

        TransactionAction newAction = handleTransition(lastAction, action);
        if (lastAction != newAction) {
            logger.info("New action " + action + " on " + this);
            TransactionState transactionState = TransactionState.forAction(newAction);
            if (transactionState != null && this.transactionState != transactionState)
                setTransactionState(transactionState);
            TransferState transferState = TransferState.forAction(newAction);
            if (transferState != null && this.transferState != transferState)
                setTransferState(transferState);
        }
        return newAction;
    }

    private TransactionAction handleTransition(TransactionAction lastAction, TransactionAction action) {
        TransactionAction transactionAction = TransactionAction.handleTransition(lastAction, action);
        if (transactionAction != lastAction) {
            actions.add(transactionAction);
        }
        return transactionAction;
    }

    void setActions(List<TransactionAction> actions) {
        this.actions = actions;
    }

    public LocalDateTime getCompletedStamp() {
        return completedStamp;
    }

    void setCompletedStamp(LocalDateTime completedStamp) {
        this.completedStamp = completedStamp;
    }

    public TransactionState getTransactionState() {
        return transactionState;
    }

    void setTransactionState(TransactionState transactionState) {
        logger.info("Change transaction state to " + transactionState + " on " + this);
        this.transactionState = transactionState;
    }

    public TransferState getTransferState() {
        return transferState;
    }

    void setTransferState(TransferState transferState) {
        logger.info("Change transfer state to " + transferState + " on " + this);
        this.transferState = transferState;
    }

    public boolean isFailed() {
        return transactionState == TransactionState.REJECTED;
    }

    public PartyFspResponseDTO getFspPartyDTO() {
        return fspPartyDTO;
    }

    void setFspPartyDTO(PartyFspResponseDTO fspPartyDTO) {
        this.fspPartyDTO = fspPartyDTO;
        partyContext.update(fspPartyDTO); // ehcache and transaction contexes with the same party is share the same party context instance -> all updated
    }

    public TransactionRequestFspResponseDTO getFspRequestDTO() {
        return fspRequestDTO;
    }

    void setFspRequestDTO(TransactionRequestFspResponseDTO response) {
        this.fspRequestDTO = response;
    }

    public QuoteFspResponseDTO getFspQuoteDTO() {
        return fspQuoteDTO;
    }

    void setFspQuoteDTO(QuoteFspResponseDTO response) {
        this.fspQuoteDTO = response;
    }

    public TransferFspResponseDTO getFspPrepareTransferDTO() {
        return fspPrepareTransferDTO;
    }

    void setFspPrepareTransferDTO(TransferFspResponseDTO response) {
        this.fspPrepareTransferDTO = response;
    }

    public TransferFspResponseDTO getFspTransferDTO() {
        return fspTransferDTO;
    }

    void setFspTransferDTO(TransferFspResponseDTO response) {
        this.fspTransferDTO = response;
    }

    public PartySwitchResponseDTO getSwitchPartyDTO() {
        return switchPartyDTO;
    }

    void setSwitchPartyDTO(PartySwitchResponseDTO switchPartyDTO) {
        this.switchPartyDTO = switchPartyDTO;
        partyContext.update(fspPartyDTO); // maybe never get here
    }

    public TransactionRequestSwitchResponseDTO getSwitchRequestDTO() {
        return switchRequestDTO;
    }

    void setSwitchRequestDTO(TransactionRequestSwitchResponseDTO response) {
        this.switchRequestDTO = response;
    }

    public QuoteSwitchResponseDTO getSwitchQuoteDTO() {
        return switchQuoteDTO;
    }

    void setSwitchQuoteDTO(QuoteSwitchResponseDTO response) {
        this.switchQuoteDTO = response;
    }

    public TransferSwitchResponseDTO getSwitchTransferDTO() {
        return switchTransferDTO;
    }

    void setSwitchTransferDTO(TransferSwitchResponseDTO response) {
        this.switchTransferDTO = response;
    }

    void updateParty(PartySwitchResponseDTO partiesDTO) {
        if (partiesDTO == null)
            return;
        updateParty(partiesDTO.getParty());
    }

    void updateParty(PartyContext oContext) {
        if (oContext == null)
            return;
        partyContext.update(oContext);
    }

    void updateParty(Party oParty) {
        if (oParty == null)
            return;

        partyContext.update(oParty);
    }

    void updateParty(PartyIdInfo oInfo) {
        if (oInfo == null)
            return;

        partyContext.update(oInfo);
    }

    void updateFspParty(PartyFspResponseDTO response) {
        setFspPartyDTO(response);
    }

    void updatePaymentRequest(TransactionChannelRequestDTO request) {
        Party party = role == TransactionRole.PAYER ? request.getPayer() : request.getPayee();
        updateParty(party);
    }

    void updateFspTransactionRequest(TransactionRequestFspResponseDTO response) {
        setFspRequestDTO(response);
        addAction(response.getState() == TransactionRequestState.ACCEPTED ? TransactionAction.REQUEST : TransactionAction.REQUEST_FAILED);
    }

    void updateFspQuote(QuoteFspResponseDTO response) {
        setFspQuoteDTO(response);
        addAction(response.getState() == TransactionRequestState.ACCEPTED ? TransactionAction.QUOTE : TransactionAction.QUOTE_FAILED);
        setFee(response.getFspFee());
        setCommission(response.getFspCommission());
    }

    void updateFspPrepareTransfer(TransferFspResponseDTO response) {
        setFspPrepareTransferDTO(response);
        addAction(response.getState() == TransactionRequestState.ACCEPTED ? TransactionAction.PREPARE : TransactionAction.PREPARE_FAILED);
    }

    void updateFspCommitTransfer(TransferFspResponseDTO response) {
        setFspTransferDTO(response);
        addAction(response.getState() == TransactionRequestState.ACCEPTED ? TransactionAction.COMMIT : TransactionAction.COMMIT_FAILED);
        setCompletedStamp(completedStamp == null ? LocalDateTime.now() : completedStamp);
    }

    void updateSwitchTransactionRequest(TransactionRequestSwitchRequestDTO request) {
        if (role == TransactionRole.PAYER)
            updateParty(request.getPayer());
        else
            updateParty(request.getPayee());
    }

    void updateSwitchTransactionRequest(TransactionRequestSwitchResponseDTO response) {
        setSwitchRequestDTO(response);
        addAction(response.getTransactionRequestState() == TransactionRequestState.ACCEPTED ? TransactionAction.REQUEST : TransactionAction.REQUEST_FAILED, false);
    }

    void updateSwitchQuoteRequest(QuoteSwitchRequestDTO request, FspId sourceFsp, FspId destFsp) {
        updateParty(role == TransactionRole.PAYER ? request.getPayer() : request.getPayee());
        setFee(request.getFees());
        setFspId(role == TransactionRole.PAYER ? sourceFsp : destFsp);
    }

    void updateSwitchQuote(QuoteSwitchResponseDTO response, Ilp ilp) {
        setSwitchQuoteDTO(response);
        addAction(TransactionAction.QUOTE, false);
        setFee(response.getPayeeFspFee());
        setCommission(response.getPayeeFspCommission());
    }

    void updateSwitchTransferRequest(TransferSwitchRequestDTO request, Ilp ilp, FspId sourceFsp, FspId destFsp) {
        setFspId(ContextUtil.parseFspId(role == TransactionRole.PAYER ? request.getPayerFsp() : request.getPayeeFsp()));
        setFspId(role == TransactionRole.PAYER ? sourceFsp : destFsp);
    }

    void updateSwitchTransfer(TransferSwitchResponseDTO response) {
        setSwitchTransferDTO(response);
        addAction(response.getTransferState() == TransferState.ABORTED ? TransactionAction.COMMIT_FAILED : TransactionAction.COMMIT, false);
        setCompletedStamp(completedStamp == null ? LocalDateTime.now() : completedStamp);
    }

    @Override
    public String toString() {
        return "TransactionRoleContext{" +
                role +
                ", " + getFspId() +
                ", transaction:" + transactionState +
                ", transfer:" + transferState +
                '}';
    }
}
