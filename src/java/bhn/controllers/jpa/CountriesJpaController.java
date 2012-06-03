/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bhn.controllers.jpa;

import bhn.controllers.jpa.exceptions.IllegalOrphanException;
import bhn.controllers.jpa.exceptions.NonexistentEntityException;
import bhn.controllers.jpa.exceptions.RollbackFailureException;
import entities.Countries;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
public class CountriesJpaController implements Serializable {

    public CountriesJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Countries countries) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            States states = countries.getStates();
            if (states != null) {
                states = em.getReference(states.getClass(), states.getId());
                countries.setStates(states);
            }
            em.persist(countries);
            if (states != null) {
                Countries oldCountriesOfStates = states.getCountries();
                if (oldCountriesOfStates != null) {
                    oldCountriesOfStates.setStates(null);
                    oldCountriesOfStates = em.merge(oldCountriesOfStates);
                }
                states.setCountries(countries);
                states = em.merge(states);
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

    public void edit(Countries countries) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Countries persistentCountries = em.find(Countries.class, countries.getId());
            States statesOld = persistentCountries.getStates();
            States statesNew = countries.getStates();
            List<String> illegalOrphanMessages = null;
            if (statesOld != null && !statesOld.equals(statesNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain States " + statesOld + " since its countries field is not nullable.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (statesNew != null) {
                statesNew = em.getReference(statesNew.getClass(), statesNew.getId());
                countries.setStates(statesNew);
            }
            countries = em.merge(countries);
            if (statesNew != null && !statesNew.equals(statesOld)) {
                Countries oldCountriesOfStates = statesNew.getCountries();
                if (oldCountriesOfStates != null) {
                    oldCountriesOfStates.setStates(null);
                    oldCountriesOfStates = em.merge(oldCountriesOfStates);
                }
                statesNew.setCountries(countries);
                statesNew = em.merge(statesNew);
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
                Integer id = countries.getId();
                if (findCountries(id) == null) {
                    throw new NonexistentEntityException("The countries with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Countries countries;
            try {
                countries = em.getReference(Countries.class, id);
                countries.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The countries with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            States statesOrphanCheck = countries.getStates();
            if (statesOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Countries (" + countries + ") cannot be destroyed since the States " + statesOrphanCheck + " in its states field has a non-nullable countries field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(countries);
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

    public List<Countries> findCountriesEntities() {
        return findCountriesEntities(true, -1, -1);
    }

    public List<Countries> findCountriesEntities(int maxResults, int firstResult) {
        return findCountriesEntities(false, maxResults, firstResult);
    }

    private List<Countries> findCountriesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Countries.class));
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

    public Countries findCountries(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Countries.class, id);
        } finally {
            em.close();
        }
    }

    public int getCountriesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Countries> rt = cq.from(Countries.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
