/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.component;

import org.interledger.InterledgerAddress;
import org.interledger.InterledgerPacket;

import java.util.Arrays;
import java.util.Objects;

public interface InterledgerPaymentDpc extends InterledgerPacket {
    static InterledgerPaymentDpc.Builder builder() {
        return new InterledgerPaymentDpc.Builder();
    }

    InterledgerAddress getDestinationAccount();

    String getDestinationAmount();

    byte[] getData();

    public static class Builder {
        private InterledgerAddress destinationAccount;
        private String destinationAmount;
        private byte[] data;

        public Builder() {
        }

        public InterledgerPaymentDpc.Builder destinationAccount(InterledgerAddress destinationAccount) {
            this.destinationAccount = (InterledgerAddress)Objects.requireNonNull(destinationAccount);
            return this;
        }

        public InterledgerPaymentDpc.Builder destinationAmount(String destinationAmount) {
            this.destinationAmount = (String)Objects.requireNonNull(destinationAmount);
            return this;
        }

        public InterledgerPaymentDpc.Builder data(byte[] data) {
            this.data = (byte[])Objects.requireNonNull(data);
            return this;
        }

        public InterledgerPaymentDpc build() {
            return new InterledgerPaymentDpc.Builder.Impl(this);
        }

        private static final class Impl implements InterledgerPaymentDpc {
            private final InterledgerAddress destinationAccount;
            private final String destinationAmount;
            private final byte[] data;

            private Impl(InterledgerPaymentDpc.Builder builder) {
                Objects.requireNonNull(builder);
                this.destinationAccount = (InterledgerAddress)Objects.requireNonNull(builder.destinationAccount, "destinationAccount must not be null!");
                this.destinationAmount = (String)Objects.requireNonNull(builder.destinationAmount, "destinationAmount must not be null!");
                this.data = (byte[])Objects.requireNonNull(builder.data, "data must not be null!");
            }

            public InterledgerAddress getDestinationAccount() {
                return this.destinationAccount;
            }

            public String getDestinationAmount() {
                return this.destinationAmount;
            }

            public byte[] getData() {
                return Arrays.copyOf(this.data, this.data.length);
            }

            public boolean equals(Object obj) {
                if (this == obj) {
                    return true;
                } else if (obj != null && this.getClass() == obj.getClass()) {
                    InterledgerPaymentDpc.Builder.Impl impl = (InterledgerPaymentDpc.Builder.Impl)obj;
                    return this.destinationAccount.equals(impl.destinationAccount) && this.destinationAmount.equals(impl.destinationAmount) && Arrays.equals(this.data, impl.data);
                } else {
                    return false;
                }
            }

            public int hashCode() {
                int result = this.destinationAccount.hashCode();
                result = 31 * result + this.destinationAmount.hashCode();
                result = 31 * result + Arrays.hashCode(this.data);
                return result;
            }

            public String toString() {
                return "InterledgerPayment.Impl{destinationAccount=" + this.destinationAccount + ", destinationAmount=" + this.destinationAmount + ", data=" + Arrays.toString(this.data) + '}';
            }
        }
    }
}
