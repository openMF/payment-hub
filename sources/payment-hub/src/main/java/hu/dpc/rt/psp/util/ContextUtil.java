/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.util;

import hu.dpc.rt.psp.internal.FspId;
import hu.dpc.rt.psp.type.TransactionRole;
import org.apache.camel.Exchange;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import static hu.dpc.rt.psp.constant.ExchangeHeader.PAYEE_FSP_ID;
import static hu.dpc.rt.psp.constant.ExchangeHeader.PAYER_FSP_ID;
import static hu.dpc.rt.psp.type.TransactionRole.PAYER;

public class ContextUtil {

    public static final String EXTENSION_TRANSACTION_ID = "transactionId";

    private final static SimpleDateFormat LOCAL_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    private final static DecimalFormat AMOUNT_FORMAT = new DecimalFormat("#.####");


    public static FspId getFspId(Exchange exchange, TransactionRole role) {
        return exchange.getProperty((role == PAYER ? PAYER_FSP_ID : PAYEE_FSP_ID).getKey(), FspId.class);
    }

    public static void setFspId(Exchange exchange, TransactionRole role, FspId fspId) {
        exchange.setProperty((role == PAYER ? PAYER_FSP_ID : PAYEE_FSP_ID).getKey(), fspId);
    }

    public static FspId parseFspId(String fspId) {
        return fspId == null ? null : new FspId(fspId.substring(0, 4), fspId.substring(4, 8));
    }

    public static String parsePathParam(String pathInfo, int numberOfParams, int index) {
        //TODO: URL parameter handling (transactionId, channelRefId, etc.) should be rewritten
        int revIdx = numberOfParams - index;
        int from = 0;
        String parse = pathInfo;
        while (--revIdx >= 0) {
            int idx = parse.lastIndexOf('/');
            if (idx < 0)
                return null;

            parse = parse.substring(0, idx);
            from = idx + 1;
        }
        int to = pathInfo.indexOf('/', from);
        if (to < 0)
            to = pathInfo.indexOf('?', from);
        if (to < 0)
            to = pathInfo.length();
        return pathInfo.substring(from, to);
    }

    public static LocalDateTime parseDate(String date) {
        if (date == null)
            return null;
        try {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(LOCAL_DATE_TIME_FORMAT.parse(date).getTime()), ZoneOffset.UTC);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String formatDate(LocalDateTime date) {
        return date == null ? null : LOCAL_DATE_TIME_FORMAT.format(Date.from(date.toInstant(ZoneOffset.UTC)));
    }

    public static BigDecimal parseAmount(String amount) {
        return amount == null ? null : new BigDecimal(amount);
    }

    public static String formatAmount(BigDecimal amount) {
        return amount == null ? null : AMOUNT_FORMAT.format(amount);
    }

    public static void main(String[] args) throws ParseException {
        final String ISO8601_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
//        LocalDateTimeDeserializer deserializer = LocalDateTimeDeserializer.INSTANCE;
//        LocalDateTimeSerializer serializer = LocalDateTimeSerializer.INSTANCE;
//        String dateS = "2019-12-31T11:00:00.000+02:00";
//        LocalDateTime date = LocalDateTime.parseDate(dateS);
//
//        LocalDateTime localDateTime = LocalDateTime.parseDate(dateS, DateTimeFormatter.ISO_DATE_TIME);
//
//        System.out.println("ZonedDateTime formatDate OFFSET: " + localDateTime.atZone(ZoneId.of("UTC")).formatDate(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
//        System.out.println("ZonedDateTime formatDate ZONED: " + localDateTime.atZone(ZoneId.of("UTC")).formatDate(DateTimeFormatter.ISO_ZONED_DATE_TIME));
//        System.out.println("ZonedDateTime formatDate LOCAL: " + localDateTime.atZone(ZoneId.of("UTC")).formatDate(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
//        System.out.println("ZonedDateTime formatDate DATE TIME: " + localDateTime.atZone(ZoneId.of("UTC")).formatDate(DateTimeFormatter.ISO_DATE_TIME));
//        System.out.println("ZonedDateTime formatDate DATE: " + localDateTime.atZone(ZoneId.of("UTC")).formatDate(DateTimeFormatter.ISO_DATE));
//
//        System.out.println("\nlocalDateTime formatDate LOCAL: " + localDateTime.formatDate(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
//        System.out.println("localDateTime formatDate DATE TIME: " + localDateTime.formatDate(DateTimeFormatter.ISO_DATE_TIME));
//        System.out.println("localDateTime formatDate DATE: " + localDateTime.formatDate(DateTimeFormatter.ISO_DATE));
//
//        SimpleDateFormat zoneOrZuluFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSXXX");
//        SimpleDateFormat isoDateFormat = new SimpleDateFormat(ISO8601_DATE_TIME_PATTERN);
//        SimpleDateFormat nozoneFormat =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS");
//
//        "yyyy-mm-dd hh:mm:ss[.fffffffff]"
//        Timestamp timestamp = Timestamp.valueOf("2019-12-31 11:00:00.001"); // no zone allowed
//        System.out.println("\ntimestamp formatDate: " + zoneOrZuluFormat.formatDate(timestamp));
//
//        Date date = zoneOrZuluFormat.parseDate(dateS);
//
//        Instant instant = Instant.ofEpochMilli(date.getTime());
//        localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
//
//        System.out.println("\ndate formatDate zoneOrZulu: " + zoneOrZuluFormat.formatDate(date));
//        System.out.println("date formatDate isoDateFormat: " + isoDateFormat.formatDate(date));
//        System.out.println("date formatDate nozone: " + nozoneFormat.formatDate(date));
//
//
//        Date dateFromLocal = Date.from(localDateTime.toInstant(ZoneOffset.UTC));
//        System.out.println("\ndateFromLocal formatDate zoneOrZulu: " + zoneOrZuluFormat.formatDate(dateFromLocal));
//        System.out.println("dateFromLocal formatDate isoDateFormat: " + isoDateFormat.formatDate(dateFromLocal));
//        System.out.println("dateFromLocal formatDate nozone: " + nozoneFormat.formatDate(dateFromLocal));
//
//        zoneOrZuluFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
//        isoDateFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
//        nozoneFormat.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
//
//        System.out.println("\ndate formatDate zoneOrZulu timezone UTC: " + zoneOrZuluFormat.formatDate(date)); //OK
//        System.out.println("date formatDate isoDateFormat UTC: " + isoDateFormat.formatDate(date)); //OK
//        System.out.println("date formatDate nozone UTC: " + nozoneFormat.formatDate(date));
//
//        System.out.println("\ndateFromLocal formatDate zoneOrZulu timezone UTC: " + zoneOrZuluFormat.formatDate(dateFromLocal)); //OK
//        System.out.println("dateFromLocal formatDate isoDateFormat UTC: " + isoDateFormat.formatDate(dateFromLocal)); //OK
//        System.out.println("dateFromLocal formatDate nozone UTC: " + nozoneFormat.formatDate(dateFromLocal));
//        BigDecimal amount;
//        System.out.println("123.45: " + AMOUNT_FORMAT.format(new BigDecimal("123.45")));
//        System.out.println("123.00: " + AMOUNT_FORMAT.format(new BigDecimal("123.00")));
//        System.out.println("123.40: " + AMOUNT_FORMAT.format(new BigDecimal("123.40")));
//        System.out.println("123.040: " + AMOUNT_FORMAT.format(new BigDecimal("123.040")));
//        System.out.println("0.00: " + AMOUNT_FORMAT.format(new BigDecimal("0.00")));
//        System.out.println("23456.70: " + AMOUNT_FORMAT.format(new BigDecimal("23456.70")));
//        System.out.println("23456789.10: " + AMOUNT_FORMAT.format(new BigDecimal("23456789.10")));
//        System.out.println("123456789.123456789: " + AMOUNT_FORMAT.format(new BigDecimal("123456789.123456789")));
//        System.out.println("0.123456789: " + AMOUNT_FORMAT.format(new BigDecimal("0.123456789")));
//        System.out.println("1234567890123456789.023456089: " + AMOUNT_FORMAT.format(new BigDecimal("1234567890123456789.023456089")));
    }
}
