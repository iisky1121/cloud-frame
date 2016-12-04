package com.jfinal.ext.kit;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 基于jdk的xml解析的简单封装
 * @author L.cm
 */
public class XmlKit {
	
	private XmlKit() {}
	
	/**
	 * 读取xml文件转为 w3c Document
	 * @param xmlpath
	 * @return
	 */
	public static Document read(String xmlpath) {
		InputStream inputStream = null;
		try {
			DocumentBuilderFactory dbf = getDocumentBuilderFactory();
			DocumentBuilder db = dbf.newDocumentBuilder();
			inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(xmlpath);
			if (inputStream == null)
				throw new IllegalArgumentException("Properties file not found in classpath: " + xmlpath);
			return db.parse(inputStream); 
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			if (inputStream != null) try {inputStream.close();} catch (IOException e) {}
		}
	}
	
	/**
	 * 将xmlStr转为 w3c Document
	 * @param xmlStr
	 * @return
	 */
	public static Document parse(String xmlStr) {
		DocumentBuilderFactory dbf = getDocumentBuilderFactory();
		
		StringReader sr = null;
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			sr = new StringReader(xmlStr);
			InputSource is = new InputSource(sr);
			Document document = db.parse(is);
			
			return document;
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			IOKit.closeQuietly(sr);
		}
	}
	
	/**
	 * 获取xml节点中的文本
	 * @param element
	 * @param name
	 * @return
	 */
	public static String elementText(Element element, String name) {
		NodeList node = element.getElementsByTagName(name);
		if (node.getLength() == 0) {
			return null;
		}
		return node.item(0).getTextContent();
	}
	
	/**
	 * 获取xml文档中的文本
	 * @param element
	 * @param name
	 * @return
	 */
	public static String documentText(Document doc, String name) {
		NodeList node = doc.getElementsByTagName(name);
		if (node.getLength() == 0) {
			return null;
		}
		return node.item(0).getTextContent();
	}
	
	/**
	 * 获取节点下的xml文档
	 * @param element
	 * @param name
	 * @return
	 */
	public static Document element(Element element, String name) {
		NodeList list = element.getElementsByTagName(name);
		if (list.getLength() == 0) {
			return null;
		}
		return list.item(0).getOwnerDocument();
	}
	
	private static DocumentBuilderFactory getDocumentBuilderFactory(){
		return DocumentBuilderFactoryHolder.INSTANCE;
	}
	
	private static class DocumentBuilderFactoryHolder {
		private static DocumentBuilderFactory INSTANCE = DocumentBuilderFactory.newInstance();
	}
	
}
