package bhn.controllers;


import java.util.ArrayList;  
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;

@ManagedBean(name="autoCompleteBean")
public class AutoCompleteBean {  
  
    private String searchLabel; 
    List<String> paises = new ArrayList<String>();
    
    private EntityManager em;
    

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }
    
    public void guardar(Object obj){
        try{
            em = Persistence.createEntityManagerFactory("BasicHumanNeedsPU").createEntityManager();
            em.getTransaction().begin();
            em.persist(obj);
            em.getTransaction().commit();
        }catch(Exception e){e.printStackTrace();}
        finally{
        em.close();}
    }
    
    public List<Object> listOfAdministers(){
        Query q = null;
        try{
            q = em.createQuery("SELECT a FROM Administer a");
        return (List<Object>) q.getResultList();
        }catch(Exception ex){ex.printStackTrace();}
        return q.getResultList();
//        List<String> countries = new ArrayList<String>();
//        countries.add("Santo Domingo");
//        countries.add("Venezuela");
//        countries.add("Chile");
//        countries.add("Argentina");
//        countries.add("Brazil");
//        return countries;
        
        
    }
    
      
    public List<String> complete(String query) {  
        List<String> results = new ArrayList<String>();  
          
        for (int i = 0; i < 10; i++) {  
            results.add(query + i);  
        }  
          
        return results;  
    }  
  
    public String getSearchLabel() {  
        return searchLabel;  
    }  
  
    public void setSearchLabel(String label) {  
        this.searchLabel = label;  
    }  
  
    
}  