/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bhn.controllers;

import entities.Cities;
import entities.Locations;
import entities.Locations_;
import entities.Shelters;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author Margarita
 */
@ManagedBean
@ViewScoped
public class ManagerController {
    EntityManager em = Persistence.createEntityManagerFactory("BasicHumanNeedsPU").createEntityManager();
    


    /**
     * Creates a new instance of ManagerController
     */
    public ManagerController() {
    }
    
    public List<Cities> getListOfCities(){
        Query q = null;
        try{
            q = em.createNamedQuery("Cities.findAll");
            return q.getResultList();
        }catch(Exception e){e.printStackTrace();}
        return q.getResultList();
    }
    
    public List<Shelters> getListOfShelters(){
        Query q = null;
        try{
            q = em.createNamedQuery("Shelters.findAll");
            return q.getResultList();
        }catch(Exception e){e.printStackTrace();}
        return q.getResultList();
    }
    
    public void pasarLocation(Shelters shel){
        
        MapBean.setLatitudAndLongitud(em.find(Locations.class, shel.getLocationId()).getLatitude(),em.find(Locations.class, shel.getLocationId()).getLongitude());
               
    }
}
