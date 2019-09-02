/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.dto;

import com.ilp.conditions.models.pdp.Money;
import hu.dpc.rt.psp.util.ContextUtil;

import java.beans.Transient;
import java.math.BigDecimal;

public class MoneyData {

    private String amount;
    private String currency;

    public MoneyData() {
    }

    public MoneyData(String amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public MoneyData(BigDecimal amount, String currency) {
        this(ContextUtil.formatAmount(amount), currency);
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Transient
    public BigDecimal getAmountDecimal() {
        return ContextUtil.parseAmount(amount);
    }

    public void setAmount(BigDecimal amount) {
        this.amount = ContextUtil.formatAmount(amount);
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Transient
    public Money getIlpMoney() {
        Money money = new Money();
        money.setAmount(amount);
        money.setCurrency(currency);
        return money;
    }

    @Transient
    public static FspMoneyData toFspMoneyData(MoneyData moneyData) {
        return moneyData == null ? null : moneyData.toFspMoneyData();
    }

    @Transient
    public FspMoneyData toFspMoneyData() {
        return new FspMoneyData(getAmountDecimal(), currency);
    }
}
