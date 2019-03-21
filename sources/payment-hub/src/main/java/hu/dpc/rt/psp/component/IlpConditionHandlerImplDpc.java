/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.component;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilp.conditions.models.pdp.Transaction;
import org.interledger.Condition;
import org.interledger.Fulfillment;
import org.interledger.InterledgerAddress;
import org.interledger.codecs.CodecContext;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.logging.Logger;


public class IlpConditionHandlerImplDpc {
    private Logger log = Logger.getLogger(this.getClass().getName());

    public IlpConditionHandlerImplDpc() {
    }

    public String getILPPacket(String ilpAddress, String amount, byte[] transaction) throws IOException {
        InterledgerAddress address = InterledgerAddress.builder().value(ilpAddress).build();
        InterledgerPaymentDpc payment = InterledgerPaymentDpc.builder().destinationAccount(address).destinationAmount(amount).data(transaction).build();
        CodecContext context = CodecContextFactoryDpc.interledger();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        context.write(InterledgerPaymentDpc.class, payment, outputStream);
        String encodedILPPacket = Base64.getUrlEncoder().encodeToString(outputStream.toByteArray());
        return encodedILPPacket;
    }

    public String getILPPacket(String ilpAddress, String amount, Transaction transaction) throws IOException {
        InterledgerAddress address = InterledgerAddress.builder().value(ilpAddress).build();
        InterledgerPaymentDpc.Builder paymentBuilder = InterledgerPaymentDpc.builder();
        paymentBuilder.destinationAccount(address);
        paymentBuilder.destinationAmount(amount);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        String notificationJson = mapper.writeValueAsString(transaction);
        byte[] serializedTransaction = Base64.getUrlEncoder().encode(notificationJson.getBytes());
        paymentBuilder.data(serializedTransaction);
        CodecContext context = CodecContextFactoryDpc.interledger();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        context.write(InterledgerPaymentDpc.class, paymentBuilder.build(), outputStream);
        String encodedILPPacket = Base64.getUrlEncoder().encodeToString(outputStream.toByteArray());
        return encodedILPPacket;
    }

    public Transaction getTransactionFromIlpPacket(String ilpPacket) throws IOException {
        byte[] decodedILPPacket = Base64.getUrlDecoder().decode(ilpPacket);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedILPPacket);
        CodecContext context = CodecContextFactoryDpc.interledger();
        InterledgerPaymentDpc ip = (InterledgerPaymentDpc)context.read(InterledgerPaymentDpc.class, inputStream);
        byte[] decodedTxn = Base64.getUrlDecoder().decode(ip.getData());
        ObjectMapper mapper = new ObjectMapper();
        Transaction retTransaction = (Transaction)mapper.readValue(decodedTxn, Transaction.class);
        return retTransaction;
    }

    public String generateFulfillment(String ilpPacket, byte[] secret) {
        byte[] bFulfillment = this.getFulfillmentBytes(ilpPacket, secret);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bFulfillment);
    }

    public String generateCondition(String ilpPacket, byte[] secret) {
        byte[] bFulfillment = this.getFulfillmentBytes(ilpPacket, secret);
        Fulfillment fulfillment = Fulfillment.builder().preimage(bFulfillment).build();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(fulfillment.getCondition().getHash());
    }

    public boolean validateFulfillmentAgainstCondition(String strFulfillment, String strCondition) {
        byte[] bFulfillment = Base64.getUrlDecoder().decode(strFulfillment);
        Fulfillment fulfillment = Fulfillment.of(bFulfillment);
        byte[] bCondition = Base64.getUrlDecoder().decode(strCondition);
        Condition condition = Condition.of(bCondition);
        return fulfillment.validate(condition);
    }

    public String generateIPR(String destAddr, long destAmnt, ZonedDateTime expAt, byte[] recSecret) {
        return null;
    }

    private byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    private byte[] getFulfillmentBytes(String ilpPacket, byte[] secret) {
        try {
            String HMAC_ALGORITHM = "HmacSHA256";
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
            return mac.doFinal(ilpPacket.getBytes());
        } catch (NoSuchAlgorithmException | IllegalStateException | InvalidKeyException var5) {
            throw new RuntimeException("Error getting HMAC", var5);
        }
    }
}
