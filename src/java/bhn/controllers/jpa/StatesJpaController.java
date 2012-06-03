/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bhn.controllers.jpa;

import bhn.controllers.jpa.exceptions.IllegalOrphanException;
import bhn.controllers.jpa.exceptions.NonexistentEntityException;
import bhn.controllers.jpa.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entities.Countries;
import entities.Cities;
import entities.States;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Margarita
 */
public class StatesJpaController implements Serializable {

    public StatesJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(States states) throws IllegalOrphanException, RollbackFailureException, Exception {
        if (states.getCitiesList() == null) {
            states.setCitiesList(new ArrayList<Cities>());
        }
        List<String> illegalOrphanMessages = null;
        Countries countriesOrphanCheck = states.getCountries();
        if (countriesOrphanCheck != null) {
            States oldStatesOfCountries = countriesOrphanCheck.getStates();
            if (oldStatesOfCountries != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Countries " + countriesOrphanCheck + " already has an item of type States whose countries column cannot be null. Please make another selection for the countries field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Countries countries = states.getCountries();
            if (countries != null) {
                countries = em.getReference(countries.getClass(), countries.getId());
                states.setCountries(countries);
            }
            List<Cities> attachedCitiesList = new ArrayList<Cities>();
            for (Cities citiesListCitiesToAttach : states.getCitiesList()) {
                citiesListCitiesToAttach = em.getReference(citiesListCitiesToAttach.getClass(), citiesListCitiesToAttach.getId());
                attachedCitiesList.add(citiesListCitiesToAttach);
            }
            states.setCitiesList(attachedCitiesList);
            em.persist(states);
            if (countries != null) {
                countries.setStates(states);
                countries = em.merge(countries);
            }
            for (Cities citiesListCities : states.getCitiesList()) {
                States oldStateIdOfCitiesListCities = citiesListCities.getStateId();
                citiesListCities.setStateId(states);
                citiesListCities = em.merge(citiesListCities);
                if (oldStateIdOfCitiesListCities != null) {
                    oldStateIdOfCitiesListCities.getCitiesList().remove(citiesListCities);
                    oldStateIdOfCitiesListCities = em.merge(oldStateIdOfCitiesListCities);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(States states) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            States persistentStates = em.find(States.class, states.getId());
            Countries countriesOld = persistentStates.getCountries();
            Countries countriesNew = states.getCountries();
            List<Cities> citiesListOld = persistentStates.getCitiesList();
            List<Cities> citiesListNew = states.getCitiesList();
            List<String> illegalOrphanMessages = null;
            if (countriesNew != null && !countriesNew.equals(countriesOld)) {
                States oldStatesOfCountries = countriesNew.getStates();
                if (oldStatesOfCountries != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Countries " + countriesNew + " already has an item of type States whose countries column cannot be null. Please make another selection for the countries field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (countriesNew != null) {
                countriesNew = em.getReference(countriesNew.getClass(), countriesNew.getId());
                states.setCountries(countriesNew);
            }
            List<Cities> attachedCitiesListNew = new ArrayList<Cities>();
            for (Cities citiesListNewCitiesToAttach : citiesListNew) {
                citiesListNewCitiesToAttach = em.getReference(citiesListNewCitiesToAttach.getClass(), citiesListNewCitiesToAttach.getId());
                attachedCitiesListNew.add(citiesListNewCitiesToAttach);
            }
            citiesListNew = attachedCitiesListNew;
            states.setCitiesList(citiesListNew);
            states = em.merge(states);
            if (countriesOld != null && !countriesOld.equals(countriesNew)) {
                countriesOld.setStates(null);
                countriesOld = em.merge(countriesOld);
            }
            if (countriesNew != null && !countriesNew.equals(countriesOld)) {
                countriesNew.setStates(states);
                countriesNew = em.merge(countriesNew);
            }
            for (Cities citiesListOldCities : citiesListOld) {
                if (!citiesListNew.contains(citiesListOldCities)) {
                    citiesListOldCities.setStateId(null);
                    citiesListOldCities = em.merge(citiesListOldCities);
                }
            }
            for (Cities citiesListNewCities : citiesListNew) {
                if (!citiesListOld.contains(citiesListNewCities)) {
                    States oldStateIdOfCitiesListNewCities = citiesListNewCities.getStateId();
                    citiesListNewCities.setStateId(states);
                    citiesListNewCities = em.merge(citiesListNewCities);
                    if (oldStateIdOfCitiesListNewCities != null && !oldStateIdOfCitiesListNewCities.equals(states)) {
                        oldStateIdOfCitiesListNewCities.getCitiesList().remove(citiesListNewCities);
                        oldStateIdOfCitiesListNewCities = em.merge(oldStateIdOfCitiesListNewCities);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = states.getId();
                if (findStates(id) == null) {
                    throw new NonexistentEntityException("The states with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            States states;
            try {
                states = em.getReference(States.class, id);
                states.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The states with id " + id + " no longer exists.", enfe);
            }
            Countries countries = states.getCountries();
            if (countries != null) {
                countries.setStates(null);
                countries = em.merge(countries);
            }
            List<Cities> citiesList = states.getCitiesList();
            for (Cities citiesListCities : citiesList) {
                citiesListCities.setStateId(null);
                citiesListCities = em.merge(citiesListCities);
            }
            em.remove(states);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<States> findStatesEntities() {
        return findStatesEntities(true, -1, -1);
    }

    public List<States> findStatesEntities(int maxResults, int firstResult) {
        return findStatesEntities(false, maxResults, firstResult);
    }

    private List<States> findStatesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(States.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public States findStates(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(States.class, id);
        } finally {
            em.close();
        }
    }

    public int getStatesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<States> rt = cq.from(States.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
