package com.olvarey.spanner.iso20022.application;

import com.olvarey.spanner.iso20022.domain.Pacs008PayloadData;

/** Parses pacs.008 payload content into a normalized domain model. */
public interface Pacs008PayloadParser {

  /**
   * Parses a pacs.008 XML message into key payload fields.
   *
   * @param xmlMessage XML payload
   * @return parsed payload fields
   */
  Pacs008PayloadData parse(String xmlMessage);
}
