package pl.bsb.b2btester.model.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import pl.bsb.b2btester.type.ParamGroup;
import pl.bsb.b2btester.type.ParamKey;

/**
 *
 * @author aszatkowski
 */
@NamedQueries({
    @NamedQuery(name = "Parameter.findByKey", query = "SELECT e FROM Parameter e WHERE e.key = :key"),
    @NamedQuery(name = "Parameter.findByGroup", query = "SELECT e FROM Parameter e WHERE e.group = :group ORDER BY e.name" ),
    @NamedQuery(name = "Parameter.getCount", query = "SELECT count(e) FROM Parameter e")
})   
@Entity
@Table(name = "CONFIGURATION")
public class Parameter implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "PARAM_KEY", length = 64, nullable = false)
    @Enumerated(EnumType.STRING)
    private ParamKey key;
    @Column(name = "PARAM_GROUP", length = 64, nullable = false)
    @Enumerated(EnumType.STRING)
    private ParamGroup group;
    @Column(name = "PARAM_NAME", length = 512, nullable = false)
    private String name;
    @Column(name = "DESCRIPTION", length = 2048)
    private String description;
    @Column(name = "IS_EDITABLE", nullable = false)
    private short editable;
    @Column(name = "IS_HIDDEN", nullable = false)
    private short hidden;
    @Column(name = "PARAM_VALUE")
    private String value;
    @Column(name = "IS_BINARY", nullable = false)
    private short binary;

    /**
     *
     * @param key
     * @param group
     * @param name
     * @param description
     * @param editable
     * @param hidden
     * @param value
     */
    public Parameter(ParamKey key, ParamGroup group, String name, String description, short editable, short hidden, short binary, String value ) {
        this.key = key;
        this.group = group;
        this.name = name;
        this.description = description;
        this.editable = editable;
        this.hidden = hidden;
        this.value = value;
        this.binary = binary;
    }

    public Parameter() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParamKey getKey() {
        return key;
    }

    public void setKey(ParamKey key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public short getEditable() {
        return editable;
    }

    public void setEditable(short editable) {
        this.editable = editable;
    }

    public short getHidden() {
        return hidden;
    }

    public void setHidden(short hidden) {
        this.hidden = hidden;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public ParamGroup getGroup() {
        return group;
    }

    public void setGroup(ParamGroup group) {
        this.group = group;
    }

    public short getBinary() {
        return binary;
    }

    public void setBinary(short binary) {
        this.binary = binary;
    }
 
    public boolean getBooleanValue() {
        return Boolean.parseBoolean(value);
    }
}
