package com.olvarey.spanner.iso20022.infrastructure.prowide;

import com.olvarey.spanner.common.exception.MessageParsingException;
import com.olvarey.spanner.common.exception.MessageValidationException;
import com.olvarey.spanner.common.exception.UnsupportedMessageVersionException;
import com.olvarey.spanner.iso20022.application.Pacs008PayloadParser;
import com.olvarey.spanner.iso20022.domain.Pacs008PayloadData;
import com.prowidesoftware.swift.model.mx.AbstractMX;
import com.prowidesoftware.swift.model.mx.MxPacs00800107;
import com.prowidesoftware.swift.model.mx.MxPacs00800108;
import com.prowidesoftware.swift.model.mx.dic.ActiveCurrencyAndAmount;
import com.prowidesoftware.swift.model.mx.dic.CreditTransferTransaction30;
import com.prowidesoftware.swift.model.mx.dic.CreditTransferTransaction39;
import com.prowidesoftware.swift.model.mx.dic.FIToFICustomerCreditTransferV07;
import com.prowidesoftware.swift.model.mx.dic.FIToFICustomerCreditTransferV08;
import com.prowidesoftware.swift.model.mx.dic.GroupHeader70;
import com.prowidesoftware.swift.model.mx.dic.GroupHeader93;
import com.prowidesoftware.swift.model.mx.dic.PaymentIdentification3;
import com.prowidesoftware.swift.model.mx.dic.PaymentIdentification7;
import java.util.List;
import org.springframework.stereotype.Component;

/** Prowide-backed parser for pacs.008 payloads across supported versions. */
@Component
public class ProwidePacs008PayloadParser implements Pacs008PayloadParser {

  /**
   * Parses a pacs.008 XML payload using Prowide typed models.
   *
   * @param xmlMessage XML payload
   * @return normalized payload fields
   */
  @Override
  public Pacs008PayloadData parse(String xmlMessage) {
    if (xmlMessage == null || xmlMessage.isBlank()) {
      throw new MessageValidationException("Message payload is required");
    }

    AbstractMX mx;
    try {
      mx = AbstractMX.parse(xmlMessage);
    } catch (RuntimeException ex) {
      throw new MessageParsingException("Message is not a valid pacs.008 document", ex);
    }
    if (mx == null) {
      throw new MessageValidationException("Message is not a valid pacs.008 document");
    }

    if (mx instanceof MxPacs00800107 mxPacs00800107) {
      return parseV07(mxPacs00800107);
    }

    if (mx instanceof MxPacs00800108 mxPacs00800108) {
      return parseV08(mxPacs00800108);
    }

    if (!"pacs".equals(mx.getBusinessProcess()) || mx.getFunctionality() != 8) {
      throw new MessageValidationException("Message is not a valid pacs.008 document");
    }

    throw new UnsupportedMessageVersionException(
        "Unsupported pacs.008 version: " + mx.getVersion());
  }

  private Pacs008PayloadData parseV07(MxPacs00800107 mx) {
    if (mx.getFIToFICstmrCdtTrf() == null) {
      throw new MessageValidationException("Message is not a valid pacs.008.001.07 document");
    }

    FIToFICustomerCreditTransferV07 document = mx.getFIToFICstmrCdtTrf();
    List<CreditTransferTransaction30> transactions = document.getCdtTrfTxInf();
    if (transactions == null || transactions.isEmpty()) {
      throw new MessageValidationException(
          "pacs.008 message must contain at least one CdtTrfTxInf");
    }

    CreditTransferTransaction30 firstTransaction = transactions.get(0);
    PaymentIdentification3 paymentIdentification = firstTransaction.getPmtId();
    ActiveCurrencyAndAmount settlementAmount = firstTransaction.getIntrBkSttlmAmt();
    GroupHeader70 groupHeader = document.getGrpHdr();

    return new Pacs008PayloadData(
        groupHeader == null ? null : groupHeader.getMsgId(),
        paymentIdentification == null ? null : paymentIdentification.getTxId(),
        paymentIdentification == null ? null : paymentIdentification.getEndToEndId(),
        settlementAmount == null || settlementAmount.getValue() == null
            ? null
            : settlementAmount.getValue().toPlainString(),
        settlementAmount == null ? null : settlementAmount.getCcy(),
        groupHeader == null ? null : groupHeader.getNbOfTxs());
  }

  private Pacs008PayloadData parseV08(MxPacs00800108 mx) {
    if (mx.getFIToFICstmrCdtTrf() == null) {
      throw new MessageValidationException("Message is not a valid pacs.008.001.08 document");
    }

    FIToFICustomerCreditTransferV08 document = mx.getFIToFICstmrCdtTrf();
    List<CreditTransferTransaction39> transactions = document.getCdtTrfTxInf();
    if (transactions == null || transactions.isEmpty()) {
      throw new MessageValidationException(
          "pacs.008 message must contain at least one CdtTrfTxInf");
    }

    CreditTransferTransaction39 firstTransaction = transactions.get(0);
    PaymentIdentification7 paymentIdentification = firstTransaction.getPmtId();
    ActiveCurrencyAndAmount settlementAmount = firstTransaction.getIntrBkSttlmAmt();
    GroupHeader93 groupHeader = document.getGrpHdr();

    return new Pacs008PayloadData(
        groupHeader == null ? null : groupHeader.getMsgId(),
        paymentIdentification == null ? null : paymentIdentification.getTxId(),
        paymentIdentification == null ? null : paymentIdentification.getEndToEndId(),
        settlementAmount == null || settlementAmount.getValue() == null
            ? null
            : settlementAmount.getValue().toPlainString(),
        settlementAmount == null ? null : settlementAmount.getCcy(),
        groupHeader == null ? null : groupHeader.getNbOfTxs());
  }
}
