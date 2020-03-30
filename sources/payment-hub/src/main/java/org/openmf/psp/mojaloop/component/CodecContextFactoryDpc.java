/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.mojaloop.component;

import org.interledger.Condition;
import org.interledger.InterledgerAddress;
import org.interledger.codecs.CodecContext;
import org.interledger.codecs.oer.OerGeneralizedTimeCodec;
import org.interledger.codecs.oer.OerIA5StringCodec;
import org.interledger.codecs.oer.OerLengthPrefixCodec;
import org.interledger.codecs.oer.OerOctetStringCodec;
import org.interledger.codecs.oer.OerUint256Codec;
import org.interledger.codecs.oer.OerUint32Codec;
import org.interledger.codecs.oer.OerUint64Codec;
import org.interledger.codecs.oer.OerUint8Codec;
import org.interledger.codecs.oer.ilp.ConditionOerCodec;
import org.interledger.codecs.oer.ilp.InterledgerAddressOerCodec;
import org.interledger.codecs.oer.ilp.InterledgerPacketTypeOerCodec;
import org.interledger.codecs.oer.ilp.InterledgerPaymentOerCodec;
import org.interledger.codecs.oer.ilqp.QuoteByDestinationAmountRequestOerCodec;
import org.interledger.codecs.oer.ilqp.QuoteByDestinationAmountResponseOerCodec;
import org.interledger.codecs.oer.ilqp.QuoteBySourceAmountRequestOerCodec;
import org.interledger.codecs.oer.ilqp.QuoteBySourceAmountResponseOerCodec;
import org.interledger.codecs.oer.ilqp.QuoteLiquidityRequestOerCodec;
import org.interledger.codecs.oer.ilqp.QuoteLiquidityResponseOerCodec;
import org.interledger.codecs.oer.ipr.InterledgerPaymentRequestOerCodec;
import org.interledger.codecs.packettypes.InterledgerPacketType;
import org.interledger.codecs.psk.PskMessageBinaryCodec;
import org.interledger.ilp.InterledgerPayment;
import org.interledger.ilqp.QuoteByDestinationAmountRequest;
import org.interledger.ilqp.QuoteByDestinationAmountResponse;
import org.interledger.ilqp.QuoteBySourceAmountRequest;
import org.interledger.ilqp.QuoteBySourceAmountResponse;
import org.interledger.ilqp.QuoteLiquidityRequest;
import org.interledger.ilqp.QuoteLiquidityResponse;
import org.interledger.ipr.InterledgerPaymentRequest;
import org.interledger.psk.PskMessage;


public class CodecContextFactoryDpc {
    public CodecContextFactoryDpc() {
    }

    public static CodecContext interledger() {
        return (new CodecContext()).register(OerUint8Codec.OerUint8.class, new OerUint8Codec()).register(OerUint32Codec.OerUint32.class, new OerUint32Codec()).register(OerUint64Codec.OerUint64.class, new OerUint64Codec()).register(OerUint256Codec.OerUint256.class, new OerUint256Codec()).register(OerLengthPrefixCodec.OerLengthPrefix.class, new OerLengthPrefixCodec()).register(OerIA5StringCodec.OerIA5String.class, new OerIA5StringCodec()).register(OerOctetStringCodec.OerOctetString.class, new OerOctetStringCodec()).register(OerGeneralizedTimeCodec.OerGeneralizedTime.class, new OerGeneralizedTimeCodec()).register(InterledgerAddress.class, new InterledgerAddressOerCodec()).register(InterledgerPacketType.class, new InterledgerPacketTypeOerCodec()).register(InterledgerPayment.class, new InterledgerPaymentOerCodec()).register(InterledgerPaymentRequest.class, new InterledgerPaymentRequestOerCodec()).register(Condition.class, new ConditionOerCodec()).register(QuoteByDestinationAmountRequest.class, new QuoteByDestinationAmountRequestOerCodec()).register(QuoteByDestinationAmountResponse.class, new QuoteByDestinationAmountResponseOerCodec()).register(QuoteBySourceAmountRequest.class, new QuoteBySourceAmountRequestOerCodec()).register(QuoteBySourceAmountResponse.class, new QuoteBySourceAmountResponseOerCodec()).register(QuoteLiquidityRequest.class, new QuoteLiquidityRequestOerCodec()).register(QuoteLiquidityResponse.class, new QuoteLiquidityResponseOerCodec()).register(PskMessage.class, new PskMessageBinaryCodec()).register(InterledgerPaymentDpc.class, new InterledgerPaymentOerCodecDpc());
    }

    public static CodecContext interledgerJson() {
        throw new RuntimeException("Not yet implemented!");
    }

    public static CodecContext interledgerProtobuf() {
        throw new RuntimeException("Not yet implemented!");
    }
}
