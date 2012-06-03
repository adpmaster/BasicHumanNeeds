/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bhn.controllers.jpa;

import bhn.controllers.jpa.exceptions.IllegalOrphanException;
import bhn.controllers.jpa.exceptions.NonexistentEntityException;
import bhn.controllers.jpa.exceptions.RollbackFailureException;
import entities.Resources;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
public class ResourcesJpaController implements Serializable {

    public ResourcesJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Resources resources) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            SheltersResources sheltersResources = resources.getSheltersResources();
            if (sheltersResources != null) {
                sheltersResources = em.getReference(sheltersResources.getClass(), sheltersResources.getId());
                resources.setSheltersResources(sheltersResources);
            }
            em.persist(resources);
            if (sheltersResources != null) {
                Resources oldResourcesOfSheltersResources = sheltersResources.getResources();
                if (oldResourcesOfSheltersResources != null) {
                    oldResourcesOfSheltersResources.setSheltersResources(null);
                    oldResourcesOfSheltersResources = em.merge(oldResourcesOfSheltersResources);
                }
                sheltersResources.setResources(resources);
                sheltersResources = em.merge(sheltersResources);
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

    public void edit(Resources resources) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Resources persistentResources = em.find(Resources.class, resources.getId());
            SheltersResources sheltersResourcesOld = persistentResources.getSheltersResources();
            SheltersResources sheltersResourcesNew = resources.getSheltersResources();
            List<String> illegalOrphanMessages = null;
            if (sheltersResourcesOld != null && !sheltersResourcesOld.equals(sheltersResourcesNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain SheltersResources " + sheltersResourcesOld + " since its resources field is not nullable.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (sheltersResourcesNew != null) {
                sheltersResourcesNew = em.getReference(sheltersResourcesNew.getClass(), sheltersResourcesNew.getId());
                resources.setSheltersResources(sheltersResourcesNew);
            }
            resources = em.merge(resources);
            if (sheltersResourcesNew != null && !sheltersResourcesNew.equals(sheltersResourcesOld)) {
                Resources oldResourcesOfSheltersResources = sheltersResourcesNew.getResources();
                if (oldResourcesOfSheltersResources != null) {
                    oldResourcesOfSheltersResources.setSheltersResources(null);
                    oldResourcesOfSheltersResources = em.merge(oldResourcesOfSheltersResources);
                }
                sheltersResourcesNew.setResources(resources);
                sheltersResourcesNew = em.merge(sheltersResourcesNew);
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
                Integer id = resources.getId();
                if (findResources(id) == null) {
                    throw new NonexistentEntityException("The resources with id " + id + " no longer exists.");
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
            Resources resources;
            try {
                resources = em.getReference(Resources.class, id);
                resources.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The resources with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            SheltersResources sheltersResourcesOrphanCheck = resources.getSheltersResources();
            if (sheltersResourcesOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Resources (" + resources + ") cannot be destroyed since the SheltersResources " + sheltersResourcesOrphanCheck + " in its sheltersResources field has a non-nullable resources field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(resources);
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

    public List<Resources> findResourcesEntities() {
        return findResourcesEntities(true, -1, -1);
    }

    public List<Resources> findResourcesEntities(int maxResults, int firstResult) {
        return findResourcesEntities(false, maxResults, firstResult);
    }

    private List<Resources> findResourcesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Resources.class));
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

    public Resources findResources(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Resources.class, id);
        } finally {
            em.close();
        }
    }

    public int getResourcesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Resources> rt = cq.from(Resources.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
