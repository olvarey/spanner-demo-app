package com.olvarey.spanner.common.xml;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/** Shared secure XML parser helpers for modules that parse untrusted XML. */
public final class SecureXmlParsers {

  private SecureXmlParsers() {}

  /**
   * Creates a secure DOM {@link DocumentBuilder} with strict error handling.
   *
   * @return secure document builder
   */
  public static DocumentBuilder newSecureDocumentBuilder() {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
      factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
      factory.setExpandEntityReferences(false);
      factory.setXIncludeAware(false);
      factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

      DocumentBuilder builder = factory.newDocumentBuilder();
      builder.setErrorHandler(new ThrowingErrorHandler());
      return builder;
    } catch (Exception ex) {
      throw new IllegalStateException("Failed to initialize secure XML parser", ex);
    }
  }

  /** Error handler that throws instead of printing parser diagnostics to stderr. */
  public static final class ThrowingErrorHandler implements ErrorHandler {

    @Override
    public void warning(SAXParseException exception) throws SAXException {
      throw exception;
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
      throw exception;
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
      throw exception;
    }
  }
}
