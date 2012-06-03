/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bhn.controllers.jpa;

import bhn.controllers.jpa.exceptions.NonexistentEntityException;
import bhn.controllers.jpa.exceptions.RollbackFailureException;
import entities.Cities;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entities.States;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Margarita
 */
public class CitiesJpaController implements Serializable {

    public CitiesJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cities cities) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            States stateId = cities.getStateId();
            if (stateId != null) {
                stateId = em.getReference(stateId.getClass(), stateId.getId());
                cities.setStateId(stateId);
            }
            em.persist(cities);
            if (stateId != null) {
                stateId.getCitiesList().add(cities);
                stateId = em.merge(stateId);
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

    public void edit(Cities cities) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Cities persistentCities = em.find(Cities.class, cities.getId());
            States stateIdOld = persistentCities.getStateId();
            States stateIdNew = cities.getStateId();
            if (stateIdNew != null) {
                stateIdNew = em.getReference(stateIdNew.getClass(), stateIdNew.getId());
                cities.setStateId(stateIdNew);
            }
            cities = em.merge(cities);
            if (stateIdOld != null && !stateIdOld.equals(stateIdNew)) {
                stateIdOld.getCitiesList().remove(cities);
                stateIdOld = em.merge(stateIdOld);
            }
            if (stateIdNew != null && !stateIdNew.equals(stateIdOld)) {
                stateIdNew.getCitiesList().add(cities);
                stateIdNew = em.merge(stateIdNew);
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
                Integer id = cities.getId();
                if (findCities(id) == null) {
                    throw new NonexistentEntityException("The cities with id " + id + " no longer exists.");
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
            Cities cities;
            try {
                cities = em.getReference(Cities.class, id);
                cities.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cities with id " + id + " no longer exists.", enfe);
            }
            States stateId = cities.getStateId();
            if (stateId != null) {
                stateId.getCitiesList().remove(cities);
                stateId = em.merge(stateId);
            }
            em.remove(cities);
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

    public List<Cities> findCitiesEntities() {
        return findCitiesEntities(true, -1, -1);
    }

    public List<Cities> findCitiesEntities(int maxResults, int firstResult) {
        return findCitiesEntities(false, maxResults, firstResult);
    }

    private List<Cities> findCitiesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cities.class));
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

    public Cities findCities(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cities.class, id);
        } finally {
            em.close();
        }
    }

    public int getCitiesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cities> rt = cq.from(Cities.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
