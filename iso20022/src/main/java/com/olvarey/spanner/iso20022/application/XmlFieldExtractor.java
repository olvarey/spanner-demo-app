package com.olvarey.spanner.iso20022.application;

import java.util.Map;

/** Extracts all leaf fields and attributes from an XML message. */
public interface XmlFieldExtractor {

  /**
   * Extracts all XML leaf fields and attributes into a flattened key-value map.
   *
   * @param xmlMessage XML payload
   * @return flattened XML field map
   */
  Map<String, String> extractAllFields(String xmlMessage);
}
