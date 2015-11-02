/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.bsb.b2btester.util;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author paweld
 */
public class HtmlUtil {

    public static String transcode(String input) {
        return input.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\\n", "<br/>").replaceAll(" ", "&nbsp;");
    }
    
    public static String transcodeReverse(String input) {
        return input.replaceAll("&lt;","<").replaceAll("&gt;",">").replaceAll("<br/>","\n").replaceAll("&nbsp;", " ");
    }
    
    public static String prettyFormat(String input, int indent) {
        try {
            Source xmlInput = new StreamSource(new StringReader(input));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", indent);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        } catch (TransformerFactoryConfigurationError | TransformerException e) {
            throw new RuntimeException(e); // simple exception handling, please review it 
        }
    }
}
