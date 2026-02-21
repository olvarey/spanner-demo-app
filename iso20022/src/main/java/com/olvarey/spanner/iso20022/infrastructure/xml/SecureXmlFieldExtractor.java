package com.olvarey.spanner.iso20022.infrastructure.xml;

import static com.olvarey.spanner.common.xml.SecureXmlParsers.newSecureDocumentBuilder;

import com.olvarey.spanner.common.exception.MessageParsingException;
import com.olvarey.spanner.common.exception.MessageValidationException;
import com.olvarey.spanner.iso20022.application.XmlFieldExtractor;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/** Secure DOM-based XML field extractor that flattens leaf values and attributes. */
@Component
public class SecureXmlFieldExtractor implements XmlFieldExtractor {

  /**
   * Extracts all XML leaf fields and attributes from the payload.
   *
   * @param xmlMessage XML payload
   * @return flattened XML field map
   */
  @Override
  public Map<String, String> extractAllFields(String xmlMessage) {
    if (xmlMessage == null || xmlMessage.isBlank()) {
      throw new MessageValidationException("Message payload is required");
    }

    try {
      Element root =
          newSecureDocumentBuilder()
              .parse(new InputSource(new StringReader(xmlMessage)))
              .getDocumentElement();

      Map<String, String> fields = new LinkedHashMap<>();
      collectFields(root, elementName(root), fields);
      return fields;
    } catch (Exception ex) {
      throw new MessageParsingException("Message is not valid XML", ex);
    }
  }

  private void collectFields(Element element, String path, Map<String, String> fields) {
    collectAttributes(element, path, fields);

    List<Element> childElements = childElements(element);
    if (childElements.isEmpty()) {
      String text = element.getTextContent();
      if (text != null && !text.isBlank()) {
        fields.put(path, text.trim());
      }
      return;
    }

    Map<String, Integer> counts = new LinkedHashMap<>();
    for (Element child : childElements) {
      String name = elementName(child);
      counts.put(name, counts.getOrDefault(name, 0) + 1);
    }

    Map<String, Integer> indexes = new LinkedHashMap<>();
    for (Element child : childElements) {
      String name = elementName(child);
      int index = indexes.getOrDefault(name, 0);
      indexes.put(name, index + 1);
      String childPath =
          counts.get(name) > 1 ? path + "." + name + "[" + index + "]" : path + "." + name;
      collectFields(child, childPath, fields);
    }
  }

  private void collectAttributes(Element element, String path, Map<String, String> fields) {
    NamedNodeMap attributes = element.getAttributes();
    for (int i = 0; i < attributes.getLength(); i++) {
      Node attribute = attributes.item(i);
      String name = attribute.getNodeName();
      String value = attribute.getNodeValue();
      if (value != null && !value.isBlank()) {
        fields.put(path + ".@" + name, value);
      }
    }
  }

  private List<Element> childElements(Element element) {
    List<Element> children = new ArrayList<>();
    NodeList nodes = element.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++) {
      Node node = nodes.item(i);
      if (node instanceof Element child) {
        children.add(child);
      }
    }
    return children;
  }

  private String elementName(Element element) {
    return element.getLocalName() != null ? element.getLocalName() : element.getTagName();
  }
}
