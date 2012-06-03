/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Margarita
 */
@Entity
@Table(name = "shelters")
@NamedQueries({
    @NamedQuery(name = "Shelters.findAll", query = "SELECT s FROM Shelters s"),
    @NamedQuery(name = "Shelters.findById", query = "SELECT s FROM Shelters s WHERE s.id = :id"),
    @NamedQuery(name = "Shelters.findByIsPrivate", query = "SELECT s FROM Shelters s WHERE s.isPrivate = :isPrivate"),
    @NamedQuery(name = "Shelters.findByIsFood", query = "SELECT s FROM Shelters s WHERE s.isFood = :isFood"),
    @NamedQuery(name = "Shelters.findByIsMedical", query = "SELECT s FROM Shelters s WHERE s.isMedical = :isMedical"),
    @NamedQuery(name = "Shelters.findByCityId", query = "SELECT s FROM Shelters s WHERE s.cityId = :cityId"),
    @NamedQuery(name = "Shelters.findByLocationId", query = "SELECT s FROM Shelters s WHERE s.locationId = :locationId"),
    @NamedQuery(name = "Shelters.findByHoursAvailableId", query = "SELECT s FROM Shelters s WHERE s.hoursAvailableId = :hoursAvailableId"),
    @NamedQuery(name = "Shelters.findByCapacity", query = "SELECT s FROM Shelters s WHERE s.capacity = :capacity"),
    @NamedQuery(name = "Shelters.findByName", query = "SELECT s FROM Shelters s WHERE s.name = :name"),
    @NamedQuery(name = "Shelters.findByIsShelter", query = "SELECT s FROM Shelters s WHERE s.isShelter = :isShelter"),
    @NamedQuery(name = "Shelters.findByAddress", query = "SELECT s FROM Shelters s WHERE s.address = :address")})
public class Shelters implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Column(name = "isPrivate")
    private Boolean isPrivate;
    @Column(name = "isFood")
    private Boolean isFood;
    @Column(name = "isMedical")
    private Boolean isMedical;
    @Lob
    @Size(max = 65535)
    @Column(name = "other_restrictions")
    private String otherRestrictions;
    @Lob
    @Size(max = 65535)
    @Column(name = "notes")
    private String notes;
    @Column(name = "city_id")
    private Integer cityId;
    @Column(name = "location_id")
    private Integer locationId;
    @Column(name = "hours_available_id")
    private Integer hoursAvailableId;
    @Column(name = "capacity")
    private Integer capacity;
    @Size(max = 45)
    @Column(name = "name")
    private String name;
    @Column(name = "isShelter")
    private Short isShelter;
    @Size(max = 100)
    @Column(name = "address")
    private String address;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "shelters")
    private SheltersResources sheltersResources;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "shelters")
    private Persons persons;

    public Shelters() {
    }

    public Shelters(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public Boolean getIsFood() {
        return isFood;
    }

    public void setIsFood(Boolean isFood) {
        this.isFood = isFood;
    }

    public Boolean getIsMedical() {
        return isMedical;
    }

    public void setIsMedical(Boolean isMedical) {
        this.isMedical = isMedical;
    }

    public String getOtherRestrictions() {
        return otherRestrictions;
    }

    public void setOtherRestrictions(String otherRestrictions) {
        this.otherRestrictions = otherRestrictions;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getHoursAvailableId() {
        return hoursAvailableId;
    }

    public void setHoursAvailableId(Integer hoursAvailableId) {
        this.hoursAvailableId = hoursAvailableId;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Short getIsShelter() {
        return isShelter;
    }

    public void setIsShelter(Short isShelter) {
        this.isShelter = isShelter;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public SheltersResources getSheltersResources() {
        return sheltersResources;
    }

    public void setSheltersResources(SheltersResources sheltersResources) {
        this.sheltersResources = sheltersResources;
    }

    public Persons getPersons() {
        return persons;
    }

    public void setPersons(Persons persons) {
        this.persons = persons;
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
        if (!(object instanceof Shelters)) {
            return false;
        }
        Shelters other = (Shelters) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Shelters[ id=" + id + " ]";
    }
    
}
