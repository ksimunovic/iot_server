package org.foi.nwtis.karsimuno.ejb.eb;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Administrator
 */
@Entity
@Table(name = "PORUKE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Poruke.findAll", query = "SELECT p FROM Poruke p")
    , @NamedQuery(name = "Poruke.findById", query = "SELECT p FROM Poruke p WHERE p.id = :id")
    , @NamedQuery(name = "Poruke.findByIot", query = "SELECT p FROM Poruke p WHERE p.iot = :iot")
    , @NamedQuery(name = "Poruke.findByVrijeme", query = "SELECT p FROM Poruke p WHERE p.vrijeme = :vrijeme")
    , @NamedQuery(name = "Poruke.findByTekst", query = "SELECT p FROM Poruke p WHERE p.tekst = :tekst")
    , @NamedQuery(name = "Poruke.findByStatus", query = "SELECT p FROM Poruke p WHERE p.status = :status")})
public class Poruke implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "IOT")
    private int iot;
    @Column(name = "VRIJEME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date vrijeme;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "TEKST")
    private String tekst;
    @Basic(optional = false)
    @NotNull
    @Column(name = "STATUS")
    private int status;

    public Poruke() {
    }

    public Poruke(Integer id) {
        this.id = id;
    }

    public Poruke(Integer id, int iot, String tekst, int status) {
        this.id = id;
        this.iot = iot;
        this.tekst = tekst;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getIot() {
        return iot;
    }

    public void setIot(int iot) {
        this.iot = iot;
    }

    public Date getVrijeme() {
        return vrijeme;
    }

    public void setVrijeme(Date vrijeme) {
        this.vrijeme = vrijeme;
    }

    public String getTekst() {
        return tekst;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Poruke)) {
            return false;
        }
        Poruke other = (Poruke) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.foi.nwtis.karsimuno.ejb.eb.Poruke[ id=" + id + " ]";
    }

}
