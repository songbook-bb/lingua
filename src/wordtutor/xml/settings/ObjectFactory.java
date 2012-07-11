//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.06.01 at 08:36:17 PM MSD 
//


package wordtutor.xml.settings;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the wordtutor.xml.settings package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {
    private final static QName _Settings_QNAME = new QName("", "settings");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: wordtutor.xml.settings
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SettingsType }
     * 
     */
    public SettingsType createSettingsType() {
        return new SettingsType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SettingsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "settings")
    public JAXBElement<SettingsType> createSettings(SettingsType value) {
        return new JAXBElement<SettingsType>(_Settings_QNAME, SettingsType.class, null, value);
    }

}