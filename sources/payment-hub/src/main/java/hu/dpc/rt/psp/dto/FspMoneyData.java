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

public class FspMoneyData {

    private BigDecimal amount;
    private String currency;

    public FspMoneyData() {
    }

    public FspMoneyData(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Transient
    public Money toIlpMoney() {
        Money money = new Money();
        money.setAmount(ContextUtil.formatAmount(amount));
        money.setCurrency(currency);
        return money;
    }

    @Transient
    public static MoneyData toMoneyData(FspMoneyData moneyData) {
        return moneyData == null ? null : moneyData.toMoneyData();
    }

    @Transient
    public MoneyData toMoneyData() {
        return new MoneyData(amount, currency);
    }
}
