/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.component;


import org.interledger.codecs.InterledgerPacketCodec;
import org.interledger.codecs.packettypes.InterledgerPacketType;
import org.interledger.codecs.packettypes.PaymentPacketType;

public interface InterledgerPaymentCodecDpc extends InterledgerPacketCodec<InterledgerPaymentDpc> {
    InterledgerPacketType TYPE = new PaymentPacketType();

    default InterledgerPacketType getTypeId() {
        return TYPE;
    }
}
