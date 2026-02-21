package com.olvarey.spanner.iso20022.application;

import com.olvarey.spanner.iso20022.domain.Pacs008MessageData;
import com.olvarey.spanner.iso20022.domain.Pacs008PayloadData;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Coordinates pacs.008 parsing and full XML field extraction. */
@Service
@RequiredArgsConstructor
public class Pacs008MessageService {

  private final Pacs008PayloadParser payloadParser;
  private final XmlFieldExtractor xmlFieldExtractor;

  /**
   * Parses a pacs.008 XML message into structured fields and flattened XML fields.
   *
   * @param xmlMessage XML payload
   * @return parsed pacs.008 data
   */
  public Pacs008MessageData parse(String xmlMessage) {
    Map<String, String> parsedFields = xmlFieldExtractor.extractAllFields(xmlMessage);
    Pacs008PayloadData payloadData = payloadParser.parse(xmlMessage);
    String transactionIdAttribute = resolveTransactionIdAttribute(parsedFields);
    return new Pacs008MessageData(
        transactionIdAttribute,
        payloadData.messageId(),
        payloadData.txId(),
        payloadData.endToEndId(),
        payloadData.amount(),
        payloadData.currency(),
        payloadData.numberOfTransactions(),
        parsedFields);
  }

  private String resolveTransactionIdAttribute(Map<String, String> parsedFields) {
    for (Map.Entry<String, String> entry : parsedFields.entrySet()) {
      if (entry.getKey().endsWith(".@Transaction-Id")
          && entry.getValue() != null
          && !entry.getValue().isBlank()) {
        return entry.getValue();
      }
    }
    return null;
  }
}
