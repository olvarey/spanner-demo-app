package com.olvarey.spanner.iso20022.domain;

import java.util.Map;

/**
 * Parsed pacs.008 message data including high-level fields and flattened XML fields.
 *
 * @param transactionIdAttribute value of the {@code Transaction-Id} XML attribute, if present
 * @param messageId group header message ID
 * @param txId transaction ID
 * @param endToEndId end-to-end payment ID
 * @param amount interbank settlement amount value
 * @param currency interbank settlement amount currency
 * @param numberOfTransactions number of transactions in group header
 * @param parsedFields flattened XML leaf fields and attributes
 */
public record Pacs008MessageData(
    String transactionIdAttribute,
    String messageId,
    String txId,
    String endToEndId,
    String amount,
    String currency,
    String numberOfTransactions,
    Map<String, String> parsedFields) {}
