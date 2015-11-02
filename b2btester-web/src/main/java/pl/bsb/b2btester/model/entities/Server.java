package pl.bsb.b2btester.model.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author paweld
 */
@Entity
public class Server implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private String address;
    private String senderPath;
    private String statusPath;
    private String receiverPath;
    private String receiverListPath;
    private String wspingPath;
    private String dbAddress;
    private Integer dbPort;
    private String dbName;
    private String dbUser;
    private String dbPassword;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "server", orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();
    @Column(length = 4000)
    private String rootCertBase64;
    @Column(length = 4000)
    private String encodeCertBase64;
    @Column(length = 8000)
    private String decodeKeyBase64;
    @Column(length = 16000)
    private String crlBase64;
    private String decodeKeyPassword;

    public List<String> compare(Server server) {
        List<String> errors = new ArrayList<>();
        if (name.compareToIgnoreCase(server.getName()) == 0) {
            errors.add("error.server.name");
        }
        if (address.compareToIgnoreCase(server.getAddress()) == 0) {
            if ((senderPath.compareToIgnoreCase(server.getSenderPath()) == 0)
                    || (receiverPath.compareToIgnoreCase(server.getReceiverPath()) == 0)
                    || (receiverListPath.compareToIgnoreCase(server.getReceiverListPath()) == 0)) {
                errors.add("error.server.address");
            }
        }
        if (dbAddress.compareToIgnoreCase(server.getDbAddress()) == 0) {
            if (dbPort.equals(server.getDbPort())) {
                if (dbName.compareToIgnoreCase(server.getDbName()) == 0) {
                    errors.add("error.server.dbaddress");
                }
            }
        }
        return errors;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSenderPath() {
        return senderPath;
    }

    public void setSenderPath(String senderPath) {
        this.senderPath = senderPath;
    }

    public String getStatusPath() {
        return statusPath;
    }

    public void setStatusPath(String statusPath) {
        this.statusPath = statusPath;
    }

    public String getReceiverPath() {
        return receiverPath;
    }

    public void setReceiverPath(String receiverPath) {
        this.receiverPath = receiverPath;
    }

    public String getReceiverListPath() {
        return receiverListPath;
    }

    public void setReceiverListPath(String receiverListPath) {
        this.receiverListPath = receiverListPath;
    }

    public String getWspingPath() {
        return wspingPath;
    }

    public void setWspingPath(String wspingPath) {
        this.wspingPath = wspingPath;
    }

    @Override
    public String toString() {
        return this.getName().concat(" - ").concat(getAddress());
    }

    public String getDbAddress() {
        return dbAddress;
    }

    public void setDbAddress(String dbAddress) {
        this.dbAddress = dbAddress;
    }

    public Integer getDbPort() {
        return dbPort;
    }

    public void setDbPort(Integer dbPort) {
        this.dbPort = dbPort;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public String getRootCertBase64() {
        return rootCertBase64;
    }

    public void setRootCertBase64(String rootCertBase64) {
        this.rootCertBase64 = rootCertBase64;
    }

    public String getEncodeCertBase64() {
        return encodeCertBase64;
    }

    public void setEncodeCertBase64(String encodeCertBase64) {
        this.encodeCertBase64 = encodeCertBase64;
    }

    public String getDecodeKeyBase64() {
        return decodeKeyBase64;
    }

    public void setDecodeKeyBase64(String decodeKeyBase64) {
        this.decodeKeyBase64 = decodeKeyBase64;
    }

    public String getCrlBase64() {
        return crlBase64;
    }

    public void setCrlBase64(String crlBase64) {
        this.crlBase64 = crlBase64;
    }

    public String getDecodeKeyPassword() {
        return decodeKeyPassword;
    }

    public void setDecodeKeyPassword(String decodeKeyPassword) {
        this.decodeKeyPassword = decodeKeyPassword;
    }
}
