package com.olvarey.spanner.iso20022.domain;

/**
 * Normalized core fields extracted from a pacs.008 payload.
 *
 * @param messageId group header message ID
 * @param txId transaction ID
 * @param endToEndId end-to-end payment ID
 * @param amount interbank settlement amount value
 * @param currency interbank settlement amount currency
 * @param numberOfTransactions number of transactions in group header
 */
public record Pacs008PayloadData(
    String messageId,
    String txId,
    String endToEndId,
    String amount,
    String currency,
    String numberOfTransactions) {}
