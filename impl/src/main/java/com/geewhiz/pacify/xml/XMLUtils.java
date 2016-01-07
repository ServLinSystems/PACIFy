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

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.geewhiz.pacify.defect.DefectException;
import com.geewhiz.pacify.defect.DefectMessage;

public class XMLUtils {

	private static Logger logger = LogManager.getLogger(XMLUtils.class.getName());

	private static final String SLASH = "/";
	private static final String R_BRACKET = "]";
	private static final String L_BRACKET = "[";
	private static final String ATTRIBUTE = "@";

	/**
	 * Looks for the last '/' and returns the name of the node
	 * 
	 * @param xpath
	 * @return the child element name or null
	 */
	public static final String getChildNodeName(String xpath) {
		if (StringUtils.isEmpty(xpath)) {
			return null;
		}
		String childName = xpath.substring(xpath.lastIndexOf(SLASH) + 1);
		return stripIndex(childName);
	}

	/**
	 * returns the xpath if traversing up the tree one node i.e.
	 * /root/suspension_rec returns /root
	 * 
	 * @param xpath
	 * @return
	 */
	public static final String getParentXPath(String xpath) {
		if (StringUtils.isEmpty(xpath) || xpath.lastIndexOf(SLASH) <= 0) {
			return null;
		}
		return xpath.substring(0, xpath.lastIndexOf(SLASH));
	}

	/**
	 * returns the index of the child element xpath i.e. /suspension_rec[3]
	 * returns 3. /suspension_rec defaults to 1
	 * 
	 * @param xpath
	 * @return 1, the index, or null if the provided xpath is empty
	 */
	public static Integer getChildElementIndex(String xpath) {
		if (StringUtils.isEmpty(xpath)) {
			return null;
		}

		if (xpath.endsWith(R_BRACKET)) {
			String value = xpath.substring(xpath.lastIndexOf(L_BRACKET) + 1, xpath.lastIndexOf(R_BRACKET));
			if (StringUtils.isNumeric(value)) {
				return Integer.valueOf(value);
			}
		}
		return 1;
	}

	/**
	 * @param xpath
	 * @param childIndex
	 * @return
	 */
	public static String createPositionXpath(String xpath, Integer childIndex) {
		if (StringUtils.isEmpty(xpath)) {
			return null;
		}
		return stripIndex(xpath) + "[position()<" + childIndex + "]";
	}

	/**
	 * @param childName
	 * @return
	 */
	private static String stripIndex(String childName) {
		if (childName.endsWith(R_BRACKET)) {
			return childName.substring(0, childName.lastIndexOf(L_BRACKET));
		} else {
			return childName;
		}
	}

	public static void updateExistingElement(Document document, XPath xpathSearch, String xpath, String value)
			throws DefectException {
		try {
			Node node = (Node) xpathSearch.evaluate(xpath, document, XPathConstants.NODE);
			node.setTextContent(value);
		} catch (Exception e) {
			logger.debug(e);
			throw new DefectMessage("Error while trying to update xpath [" + xpath + "] expression.");
		}
	}

	public static void deleteExistingElement(Document document, XPath xpathSearch, String xpath)
			throws DefectException {
		try {
			String parentXPath = getParentXPath(xpath);

			Node parentNode = (Node) xpathSearch.evaluate(parentXPath, document, XPathConstants.NODE);
			Node childNode = (Node) xpathSearch.evaluate(xpath, document, XPathConstants.NODE);

			parentNode.removeChild(childNode);
		} catch (Exception e) {
			logger.debug(e);
			throw new DefectMessage("Error while trying to delete xpath [" + xpath + "] .");
		}
	}

	public static Node addElementToParent(Document document, XPath xpathSearch, String xpath, String value)
			throws DefectException {

		try {

			String nodeName = getChildNodeName(xpath);
			String parentXPath = getParentXPath(xpath);

			Node parentNode = (Node) xpathSearch.evaluate(parentXPath, document, XPathConstants.NODE);
			if (parentNode == null) {
				parentNode = addElementToParent(document, xpathSearch, parentXPath, null);
			}

			// create younger siblings if needed
			Integer childIndex = getChildElementIndex(xpath);
			if (childIndex > 1) {
				NodeList nodelist = (NodeList) xpathSearch.evaluate(createPositionXpath(xpath, childIndex), document,
						XPathConstants.NODESET);

				// how many to create = (index wanted - existing - 1 to account
				// for
				// the new element we will create)
				int nodesToCreate = childIndex - nodelist.getLength() - 1;
				for (int i = 0; i < nodesToCreate; i++) {
					Node node = document.createElement(nodeName);
					((Element) parentNode).appendChild(node);
				}
			}

			// create requested node
			Node node = null;
			Node created = null;
			if (nodeName.startsWith(ATTRIBUTE)) {
				node = document.createAttribute(nodeName.substring(1));
				((Element) parentNode).setAttribute(nodeName.substring(1), value);
			} else {
				node = document.createElement(nodeName);
				created = ((Node) parentNode).appendChild(node);
				if (null != value) {
					created.setTextContent(value);
				}
			}

			return created;
		} catch (Exception e) {
			logger.debug(e);
			throw new DefectMessage("Error while trying to add xpath [" + xpath + "].");
		}
	}

}
