package com.olvarey.spanner.iso20022;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.olvarey.spanner.common.exception.MessageParsingException;
import com.olvarey.spanner.iso20022.application.Pacs008MessageService;
import com.olvarey.spanner.iso20022.domain.Pacs008MessageData;
import com.olvarey.spanner.iso20022.infrastructure.prowide.ProwidePacs008PayloadParser;
import com.olvarey.spanner.iso20022.infrastructure.xml.SecureXmlFieldExtractor;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

/** Tests {@link Pacs008MessageService} parsing behavior for valid and invalid payloads. */
class Pacs008MessageServiceTest {

  private static final String VALID_PACS008_V08_MESSAGE =
      """
      <?xml version="1.0" encoding="UTF-8"?>
      <Document xmlns="urn:iso:std:iso:20022:tech:xsd:pacs.008.001.08" Transaction-Id="attr-tx-123">
        <FIToFICstmrCdtTrf>
          <GrpHdr>
            <MsgId>MSG-001</MsgId>
            <CreDtTm>2026-02-21T12:00:00Z</CreDtTm>
            <NbOfTxs>1</NbOfTxs>
          </GrpHdr>
          <CdtTrfTxInf>
            <PmtId>
              <InstrId>INSTR-001</InstrId>
              <EndToEndId>E2E-001</EndToEndId>
              <TxId>TX-001</TxId>
            </PmtId>
            <IntrBkSttlmAmt Ccy="USD">1250.75</IntrBkSttlmAmt>
          </CdtTrfTxInf>
        </FIToFICstmrCdtTrf>
      </Document>
      """;

  private final Pacs008MessageService service =
      new Pacs008MessageService(new ProwidePacs008PayloadParser(), new SecureXmlFieldExtractor());

  /** Verifies parsing of the v07 mock data payload from test resources. */
  @Test
  void shouldParseMockDataPacs008V07() throws IOException {
    String xml = readResource("mockdata/pacs008test.txt");

    Pacs008MessageData data = service.parse(xml);

    assertThat(data.transactionIdAttribute()).isNull();
    assertThat(data.messageId()).isEqualTo("SCF230815170214000000031210");
    assertThat(data.txId()).isEqualTo("VPE510608100000000000000814026");
    assertThat(data.endToEndId()).isEqualTo("VPE510608100000000000000814026");
    assertThat(data.amount()).isEqualTo("120.0");
    assertThat(data.currency()).isEqualTo("USD");
    assertThat(data.numberOfTransactions()).isEqualTo("1");
    assertAllMockDataFieldsWereParsed(data.parsedFields());
  }

  /** Verifies parsing of v08 payloads and extraction of the Transaction-Id attribute. */
  @Test
  void shouldParsePacs008V08AndExtractTransactionIdAttribute() {
    Pacs008MessageData data = service.parse(VALID_PACS008_V08_MESSAGE);

    assertThat(data.transactionIdAttribute()).isEqualTo("attr-tx-123");
    assertThat(data.messageId()).isEqualTo("MSG-001");
    assertThat(data.txId()).isEqualTo("TX-001");
    assertThat(data.endToEndId()).isEqualTo("E2E-001");
    assertThat(data.amount()).isEqualTo("1250.75");
    assertThat(data.currency()).isEqualTo("USD");
    assertThat(data.numberOfTransactions()).isEqualTo("1");
    assertThat(data.parsedFields())
        .containsEntry("Document.@Transaction-Id", "attr-tx-123")
        .containsEntry("Document.FIToFICstmrCdtTrf.CdtTrfTxInf.PmtId.TxId", "TX-001");
  }

  /** Verifies invalid XML payloads are rejected. */
  @Test
  void shouldRejectInvalidXmlPayload() {
    assertThatThrownBy(() -> service.parse("not xml"))
        .isInstanceOf(MessageParsingException.class)
        .hasMessageContaining("valid XML");
  }

  /**
   * Reads a UTF-8 text resource from the classpath.
   *
   * @param path classpath resource path
   * @return resource content
   * @throws IOException if the resource cannot be read
   */
  private String readResource(String path) throws IOException {
    ClassPathResource resource = new ClassPathResource(path);
    return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
  }

  /**
   * Asserts that all expected fields from the pacs.008 mock message were extracted.
   *
   * @param fields flattened XML field map
   */
  private void assertAllMockDataFieldsWereParsed(Map<String, String> fields) {
    assertThat(fields)
        .containsEntry("Document.@xmlns", "urn:iso:std:iso:20022:tech:xsd:pacs.008.001.07")
        .containsEntry("Document.FIToFICstmrCdtTrf.GrpHdr.MsgId", "SCF230815170214000000031210")
        .containsEntry("Document.FIToFICstmrCdtTrf.GrpHdr.CreDtTm", "2023-08-15T17:02:14-06:00")
        .containsEntry("Document.FIToFICstmrCdtTrf.GrpHdr.NbOfTxs", "1")
        .containsEntry("Document.FIToFICstmrCdtTrf.GrpHdr.TtlIntrBkSttlmAmt.@Ccy", "USD")
        .containsEntry("Document.FIToFICstmrCdtTrf.GrpHdr.TtlIntrBkSttlmAmt", "120.00")
        .containsEntry("Document.FIToFICstmrCdtTrf.GrpHdr.IntrBkSttlmDt", "2023-08-15")
        .containsEntry("Document.FIToFICstmrCdtTrf.GrpHdr.SttlmInf.SttlmMtd", "CLRG")
        .containsEntry("Document.FIToFICstmrCdtTrf.GrpHdr.SttlmInf.ClrSys.Prtry", "ACH")
        .containsEntry("Document.FIToFICstmrCdtTrf.CdtTrfTxInf.PmtId.InstrId", "SALA")
        .containsEntry(
            "Document.FIToFICstmrCdtTrf.CdtTrfTxInf.PmtId.EndToEndId",
            "VPE510608100000000000000814026")
        .containsEntry(
            "Document.FIToFICstmrCdtTrf.CdtTrfTxInf.PmtId.TxId", "VPE510608100000000000000814026")
        .containsEntry("Document.FIToFICstmrCdtTrf.CdtTrfTxInf.IntrBkSttlmAmt.@Ccy", "USD")
        .containsEntry("Document.FIToFICstmrCdtTrf.CdtTrfTxInf.IntrBkSttlmAmt", "120.0")
        .containsEntry("Document.FIToFICstmrCdtTrf.CdtTrfTxInf.ChrgBr", "SLEV")
        .containsEntry(
            "Document.FIToFICstmrCdtTrf.CdtTrfTxInf.Dbtr.Nm", "ALMACENES SIMAN,S.A. DE C.V.")
        .containsEntry(
            "Document.FIToFICstmrCdtTrf.CdtTrfTxInf.Dbtr.Id.OrgId.Othr.Id", "06141702660013")
        .containsEntry(
            "Document.FIToFICstmrCdtTrf.CdtTrfTxInf.Dbtr.Id.OrgId.Othr.SchmeNm.Prtry", "02")
        .containsEntry("Document.FIToFICstmrCdtTrf.CdtTrfTxInf.Dbtr.CtctDtls.Othr", "J")
        .containsEntry(
            "Document.FIToFICstmrCdtTrf.CdtTrfTxInf.DbtrAcct.Id.Othr.Id",
            "SV70CAGR00000000005000600378")
        .containsEntry(
            "Document.FIToFICstmrCdtTrf.CdtTrfTxInf.DbtrAgt.FinInstnId.BICFI", "CAGRSVSS")
        .containsEntry(
            "Document.FIToFICstmrCdtTrf.CdtTrfTxInf.CdtrAgt.FinInstnId.BICFI", "BHSASVSS")
        .containsEntry("Document.FIToFICstmrCdtTrf.CdtTrfTxInf.Cdtr.Nm", "david acosta")
        .containsEntry("Document.FIToFICstmrCdtTrf.CdtTrfTxInf.Cdtr.Ln", "david acosta")
        .containsEntry("Document.FIToFICstmrCdtTrf.CdtTrfTxInf.Cdtr.CtctDtls.Othr", "N")
        .containsEntry(
            "Document.FIToFICstmrCdtTrf.CdtTrfTxInf.CdtrAcct.Id.Othr.Id",
            "SV06BHSA00000000000440600378")
        .containsEntry("Document.FIToFICstmrCdtTrf.CdtTrfTxInf.CdtrAcct.Tp.Prtry", "02")
        .containsEntry("Document.FIToFICstmrCdtTrf.CdtTrfTxInf.RmtInf.Ustrd", "prueba");
  }
}
