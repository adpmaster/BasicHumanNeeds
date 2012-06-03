/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Margarita
 */
@Entity
@Table(name = "shelters_resources")
@NamedQueries({
    @NamedQuery(name = "SheltersResources.findAll", query = "SELECT s FROM SheltersResources s"),
    @NamedQuery(name = "SheltersResources.findById", query = "SELECT s FROM SheltersResources s WHERE s.id = :id"),
    @NamedQuery(name = "SheltersResources.findByShelterId", query = "SELECT s FROM SheltersResources s WHERE s.shelterId = :shelterId"),
    @NamedQuery(name = "SheltersResources.findByResourceId", query = "SELECT s FROM SheltersResources s WHERE s.resourceId = :resourceId"),
    @NamedQuery(name = "SheltersResources.findByWeekTimeExistence", query = "SELECT s FROM SheltersResources s WHERE s.weekTimeExistence = :weekTimeExistence")})
public class SheltersResources implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Column(name = "shelter_id")
    private Integer shelterId;
    @Column(name = "resource_id")
    private Integer resourceId;
    @Column(name = "week_time_existence")
    private Integer weekTimeExistence;
    @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Resources resources;
    @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Shelters shelters;

    public SheltersResources() {
    }

    public SheltersResources(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getShelterId() {
        return shelterId;
    }

    public void setShelterId(Integer shelterId) {
        this.shelterId = shelterId;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getWeekTimeExistence() {
        return weekTimeExistence;
    }

    public void setWeekTimeExistence(Integer weekTimeExistence) {
        this.weekTimeExistence = weekTimeExistence;
    }

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    public Shelters getShelters() {
        return shelters;
    }

    public void setShelters(Shelters shelters) {
        this.shelters = shelters;
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
        if (!(object instanceof SheltersResources)) {
            return false;
        }
        SheltersResources other = (SheltersResources) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.SheltersResources[ id=" + id + " ]";
    }
    
}
