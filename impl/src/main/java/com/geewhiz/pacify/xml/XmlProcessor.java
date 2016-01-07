package com.geewhiz.pacify.xml;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.DefectException;
import com.geewhiz.pacify.defect.DefectMessage;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.model.PNamespace;
import com.geewhiz.pacify.model.PXPath;
import com.geewhiz.pacify.model.PXml;
import com.geewhiz.pacify.model.PXmlCreate;
import com.geewhiz.pacify.model.PXmlDelete;
import com.geewhiz.pacify.model.PXmlUpdate;

public class XmlProcessor {

	private Logger logger = LogManager.getLogger(XmlProcessor.class.getName());

	private PMarker pMarker;
	private PXml pXml;
	private Map<String, String> propertyValues;

	DocumentBuilderFactory documentBuilderFactory;
	XPath xpathSearch;

	public XmlProcessor(PMarker pMarker, PXml pXml, Map<String, String> propertyValues) {
		this.pMarker = pMarker;
		this.pXml = pXml;
		this.propertyValues = propertyValues;

		initializeDocument();
	}

	private void initializeDocument() {
		documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(pXml.isNamespaceAware());

		xpathSearch = XPathFactory.newInstance().newXPath();
		xpathSearch.setNamespaceContext(new NamespaceContext() {
			public String getNamespaceURI(String prefix) {
				if (prefix == null)
					throw new NullPointerException("Prefix is null.");

				for (PNamespace pNamespace : pXml.getNamespaces().getNamespaces()) {
					if (prefix.equals(pNamespace.getPrefix()))
						return pNamespace.getValue();
				}

				return XMLConstants.NULL_NS_URI;
			}

			// This method isn't necessary for XPath processing.
			public String getPrefix(String uri) {
				throw new UnsupportedOperationException();
			}

			// This method isn't necessary for XPath processing either.
			@SuppressWarnings("rawtypes")
			public Iterator getPrefixes(String uri) {
				throw new UnsupportedOperationException();
			}
		});
	}

	public LinkedHashSet<Defect> process() {
		LinkedHashSet<Defect> defects = new LinkedHashSet<Defect>();

		Document document = null;

		try {
			document = documentBuilderFactory.newDocumentBuilder().parse(pMarker.getAbsoluteFileFor(pXml));
		} catch (Exception e) {
			logger.debug(e);
			defects.add(new DefectMessage(
					"Error while reading xml file [" + pMarker.getAbsoluteFileFor(pXml) + "] into document."));
			return defects;
		}

		for (PXPath xPath : pXml.getXPaths()) {
			String select = xPath.getSelect();
			logger.debug("             Processing xpath [{}].", select);

			try {
				processXPath(document, xPath);
			} catch (DefectException de) {
				defects.add(de);
			}
		}

		try {
			writeDocument(document);
		} catch (DefectMessage e) {
			defects.add(e);
		}

		return defects;
	}

	private void writeDocument(Document document) throws DefectMessage {
		try {
			DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();

			DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");

			LSSerializer writer = impl.createLSSerializer();
			writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
			writer.getDomConfig().setParameter("xml-declaration", Boolean.TRUE);
			LSOutput output = impl.createLSOutput();
			output.setByteStream(System.out);
			writer.write(document, output);
		} catch (Exception e) {
			logger.debug(e);
			throw new DefectMessage("Error while writing the adjusted xml file.");
		}
	}

	private void processXPath(Document document, PXPath xPath) throws DefectException {
		if (xPath.getCreate() != null)
			processCreate(document, xPath.getSelect(), xPath.getCreate());
		if (xPath.getUpdate() != null)
			processUpdate(document, xPath.getSelect(), xPath.getUpdate());
		if (xPath.getDelete() != null)
			processDelete(document, xPath.getSelect(), xPath.getDelete());
	}

	private void processCreate(Document document, String xPath, PXmlCreate create) throws DefectException {
		String value = null;
		if (create.getFixedString() != null)
			value = create.getFixedString();
		else
			value = propertyValues.get(create.getProperty().getName());
		XMLUtils.addElementToParent(document, xpathSearch, xPath, value);
	}

	private void processUpdate(Document document, String xPath, PXmlUpdate update) throws DefectException {
		String value = null;
		if (update.getFixedString() != null)
			value = update.getFixedString();
		else
			value = propertyValues.get(update.getProperty().getName());

		XMLUtils.updateExistingElement(document, xpathSearch, xPath, value);
	}

	private void processDelete(Document document, String xPath, PXmlDelete delete) throws DefectException {
		XMLUtils.deleteExistingElement(document, xpathSearch, xPath);
	}

}
