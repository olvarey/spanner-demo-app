# spanner-demo-app

## Google Pub/Sub (consume pacs.008 XML)

This project now includes:
- A Spring Integration inbound adapter wired to `pubsubInputChannel`
- A `@ServiceActivator` consumer that listens on `pubsubInputChannel`
- A pacs.008 parser built with Prowide (`MxPacs00800108`)
- Logging of `Transaction-Id` XML attribute and key payment fields

### 1. Create Pub/Sub resources

```bash
gcloud pubsub topics create notes-topic
gcloud pubsub subscriptions create notes-sub --topic=notes-topic
```

### 2. Configure credentials and app env vars

```bash
export GOOGLE_APPLICATION_CREDENTIALS="/absolute/path/to/service-account.json"
export GCP_PROJECT_ID="your-gcp-project-id"
export GCP_PUBSUB_SUBSCRIPTION="notes-sub"
```

### 3. Enable Pub/Sub in the app

Set this in `app/src/main/resources/application.yaml` (or env/config override):

```yaml
spring:
  cloud:
    gcp:
      pubsub:
        enabled: true
```

Keep your topic/subscription settings under:
Keep your subscription settings under:

```yaml
app:
  pubsub:
    subscription-id: notes-sub
```

Or with env vars:

```bash
export SPRING_CLOUD_GCP_PUBSUB_ENABLED=true
export GCP_PUBSUB_SUBSCRIPTION="notes-sub"
```

### 4. Run the app

```bash
./gradlew bootRun
```

### 5. Publish a pacs.008 XML message to Pub/Sub

```bash
gcloud pubsub topics publish notes-topic --message='
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
</Document>'
```

When the subscription consumer receives the message, you will see a log like:

```text
Received pacs.008 message pubSubMessageId=... transactionIdAttribute=attr-tx-123 msgId=MSG-001 txId=TX-001 endToEndId=E2E-001 amount=1250.75 currency=USD numberOfTransactions=1 parsedFields={...all XML fields and attributes...}
```

### Components used

- `PubSubIntegrationConfig`: creates `pubsubInputChannel` and `PubSubInboundChannelAdapter`
- `PubSubSubscriberService`: channel listener method with `@ServiceActivator(inputChannel = "pubsubInputChannel")`
- `Pacs008MessageService`: application service that coordinates XML attribute extraction and Prowide parsing
- `ProwidePacs008PayloadParser`: infrastructure adapter for pacs.008 parsing (supports `pacs.008.001.07` and `pacs.008.001.08`)
