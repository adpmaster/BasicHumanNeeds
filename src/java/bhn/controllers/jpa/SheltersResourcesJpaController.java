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
import entities.Resources;
import entities.Shelters;
import entities.SheltersResources;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Margarita
 */
public class SheltersResourcesJpaController implements Serializable {

    public SheltersResourcesJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(SheltersResources sheltersResources) throws IllegalOrphanException, RollbackFailureException, Exception {
        List<String> illegalOrphanMessages = null;
        Resources resourcesOrphanCheck = sheltersResources.getResources();
        if (resourcesOrphanCheck != null) {
            SheltersResources oldSheltersResourcesOfResources = resourcesOrphanCheck.getSheltersResources();
            if (oldSheltersResourcesOfResources != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Resources " + resourcesOrphanCheck + " already has an item of type SheltersResources whose resources column cannot be null. Please make another selection for the resources field.");
            }
        }
        Shelters sheltersOrphanCheck = sheltersResources.getShelters();
        if (sheltersOrphanCheck != null) {
            SheltersResources oldSheltersResourcesOfShelters = sheltersOrphanCheck.getSheltersResources();
            if (oldSheltersResourcesOfShelters != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Shelters " + sheltersOrphanCheck + " already has an item of type SheltersResources whose shelters column cannot be null. Please make another selection for the shelters field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Resources resources = sheltersResources.getResources();
            if (resources != null) {
                resources = em.getReference(resources.getClass(), resources.getId());
                sheltersResources.setResources(resources);
            }
            Shelters shelters = sheltersResources.getShelters();
            if (shelters != null) {
                shelters = em.getReference(shelters.getClass(), shelters.getId());
                sheltersResources.setShelters(shelters);
            }
            em.persist(sheltersResources);
            if (resources != null) {
                resources.setSheltersResources(sheltersResources);
                resources = em.merge(resources);
            }
            if (shelters != null) {
                shelters.setSheltersResources(sheltersResources);
                shelters = em.merge(shelters);
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

    public void edit(SheltersResources sheltersResources) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            SheltersResources persistentSheltersResources = em.find(SheltersResources.class, sheltersResources.getId());
            Resources resourcesOld = persistentSheltersResources.getResources();
            Resources resourcesNew = sheltersResources.getResources();
            Shelters sheltersOld = persistentSheltersResources.getShelters();
            Shelters sheltersNew = sheltersResources.getShelters();
            List<String> illegalOrphanMessages = null;
            if (resourcesNew != null && !resourcesNew.equals(resourcesOld)) {
                SheltersResources oldSheltersResourcesOfResources = resourcesNew.getSheltersResources();
                if (oldSheltersResourcesOfResources != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Resources " + resourcesNew + " already has an item of type SheltersResources whose resources column cannot be null. Please make another selection for the resources field.");
                }
            }
            if (sheltersNew != null && !sheltersNew.equals(sheltersOld)) {
                SheltersResources oldSheltersResourcesOfShelters = sheltersNew.getSheltersResources();
                if (oldSheltersResourcesOfShelters != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Shelters " + sheltersNew + " already has an item of type SheltersResources whose shelters column cannot be null. Please make another selection for the shelters field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (resourcesNew != null) {
                resourcesNew = em.getReference(resourcesNew.getClass(), resourcesNew.getId());
                sheltersResources.setResources(resourcesNew);
            }
            if (sheltersNew != null) {
                sheltersNew = em.getReference(sheltersNew.getClass(), sheltersNew.getId());
                sheltersResources.setShelters(sheltersNew);
            }
            sheltersResources = em.merge(sheltersResources);
            if (resourcesOld != null && !resourcesOld.equals(resourcesNew)) {
                resourcesOld.setSheltersResources(null);
                resourcesOld = em.merge(resourcesOld);
            }
            if (resourcesNew != null && !resourcesNew.equals(resourcesOld)) {
                resourcesNew.setSheltersResources(sheltersResources);
                resourcesNew = em.merge(resourcesNew);
            }
            if (sheltersOld != null && !sheltersOld.equals(sheltersNew)) {
                sheltersOld.setSheltersResources(null);
                sheltersOld = em.merge(sheltersOld);
            }
            if (sheltersNew != null && !sheltersNew.equals(sheltersOld)) {
                sheltersNew.setSheltersResources(sheltersResources);
                sheltersNew = em.merge(sheltersNew);
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
                Integer id = sheltersResources.getId();
                if (findSheltersResources(id) == null) {
                    throw new NonexistentEntityException("The sheltersResources with id " + id + " no longer exists.");
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
            SheltersResources sheltersResources;
            try {
                sheltersResources = em.getReference(SheltersResources.class, id);
                sheltersResources.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The sheltersResources with id " + id + " no longer exists.", enfe);
            }
            Resources resources = sheltersResources.getResources();
            if (resources != null) {
                resources.setSheltersResources(null);
                resources = em.merge(resources);
            }
            Shelters shelters = sheltersResources.getShelters();
            if (shelters != null) {
                shelters.setSheltersResources(null);
                shelters = em.merge(shelters);
            }
            em.remove(sheltersResources);
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

    public List<SheltersResources> findSheltersResourcesEntities() {
        return findSheltersResourcesEntities(true, -1, -1);
    }

    public List<SheltersResources> findSheltersResourcesEntities(int maxResults, int firstResult) {
        return findSheltersResourcesEntities(false, maxResults, firstResult);
    }

    private List<SheltersResources> findSheltersResourcesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(SheltersResources.class));
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

    public SheltersResources findSheltersResources(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(SheltersResources.class, id);
        } finally {
            em.close();
        }
    }

    public int getSheltersResourcesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<SheltersResources> rt = cq.from(SheltersResources.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
