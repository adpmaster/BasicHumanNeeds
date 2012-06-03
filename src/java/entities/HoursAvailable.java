/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Margarita
 */
@Entity
@Table(name = "hours_available")
@NamedQueries({
    @NamedQuery(name = "HoursAvailable.findAll", query = "SELECT h FROM HoursAvailable h"),
    @NamedQuery(name = "HoursAvailable.findById", query = "SELECT h FROM HoursAvailable h WHERE h.id = :id"),
    @NamedQuery(name = "HoursAvailable.findByOpen", query = "SELECT h FROM HoursAvailable h WHERE h.open = :open"),
    @NamedQuery(name = "HoursAvailable.findByClose", query = "SELECT h FROM HoursAvailable h WHERE h.close = :close")})
public class HoursAvailable implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Column(name = "open")
    @Temporal(TemporalType.TIME)
    private Date open;
    @Column(name = "close")
    @Temporal(TemporalType.TIME)
    private Date close;

    public HoursAvailable() {
    }

    public HoursAvailable(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getOpen() {
        return open;
    }

    public void setOpen(Date open) {
        this.open = open;
    }

    public Date getClose() {
        return close;
    }

    public void setClose(Date close) {
        this.close = close;
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
        if (!(object instanceof HoursAvailable)) {
            return false;
        }
        HoursAvailable other = (HoursAvailable) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.HoursAvailable[ id=" + id + " ]";
    }
    
}
