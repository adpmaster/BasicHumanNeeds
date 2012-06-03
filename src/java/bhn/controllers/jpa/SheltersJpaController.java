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
import entities.SheltersResources;
import entities.Persons;
import entities.Shelters;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Margarita
 */
public class SheltersJpaController implements Serializable {

    public SheltersJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Shelters shelters) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            SheltersResources sheltersResources = shelters.getSheltersResources();
            if (sheltersResources != null) {
                sheltersResources = em.getReference(sheltersResources.getClass(), sheltersResources.getId());
                shelters.setSheltersResources(sheltersResources);
            }
            Persons persons = shelters.getPersons();
            if (persons != null) {
                persons = em.getReference(persons.getClass(), persons.getId());
                shelters.setPersons(persons);
            }
            em.persist(shelters);
            if (sheltersResources != null) {
                Shelters oldSheltersOfSheltersResources = sheltersResources.getShelters();
                if (oldSheltersOfSheltersResources != null) {
                    oldSheltersOfSheltersResources.setSheltersResources(null);
                    oldSheltersOfSheltersResources = em.merge(oldSheltersOfSheltersResources);
                }
                sheltersResources.setShelters(shelters);
                sheltersResources = em.merge(sheltersResources);
            }
            if (persons != null) {
                Shelters oldSheltersOfPersons = persons.getShelters();
                if (oldSheltersOfPersons != null) {
                    oldSheltersOfPersons.setPersons(null);
                    oldSheltersOfPersons = em.merge(oldSheltersOfPersons);
                }
                persons.setShelters(shelters);
                persons = em.merge(persons);
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

    public void edit(Shelters shelters) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Shelters persistentShelters = em.find(Shelters.class, shelters.getId());
            SheltersResources sheltersResourcesOld = persistentShelters.getSheltersResources();
            SheltersResources sheltersResourcesNew = shelters.getSheltersResources();
            Persons personsOld = persistentShelters.getPersons();
            Persons personsNew = shelters.getPersons();
            List<String> illegalOrphanMessages = null;
            if (sheltersResourcesOld != null && !sheltersResourcesOld.equals(sheltersResourcesNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain SheltersResources " + sheltersResourcesOld + " since its shelters field is not nullable.");
            }
            if (personsOld != null && !personsOld.equals(personsNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Persons " + personsOld + " since its shelters field is not nullable.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (sheltersResourcesNew != null) {
                sheltersResourcesNew = em.getReference(sheltersResourcesNew.getClass(), sheltersResourcesNew.getId());
                shelters.setSheltersResources(sheltersResourcesNew);
            }
            if (personsNew != null) {
                personsNew = em.getReference(personsNew.getClass(), personsNew.getId());
                shelters.setPersons(personsNew);
            }
            shelters = em.merge(shelters);
            if (sheltersResourcesNew != null && !sheltersResourcesNew.equals(sheltersResourcesOld)) {
                Shelters oldSheltersOfSheltersResources = sheltersResourcesNew.getShelters();
                if (oldSheltersOfSheltersResources != null) {
                    oldSheltersOfSheltersResources.setSheltersResources(null);
                    oldSheltersOfSheltersResources = em.merge(oldSheltersOfSheltersResources);
                }
                sheltersResourcesNew.setShelters(shelters);
                sheltersResourcesNew = em.merge(sheltersResourcesNew);
            }
            if (personsNew != null && !personsNew.equals(personsOld)) {
                Shelters oldSheltersOfPersons = personsNew.getShelters();
                if (oldSheltersOfPersons != null) {
                    oldSheltersOfPersons.setPersons(null);
                    oldSheltersOfPersons = em.merge(oldSheltersOfPersons);
                }
                personsNew.setShelters(shelters);
                personsNew = em.merge(personsNew);
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
                Integer id = shelters.getId();
                if (findShelters(id) == null) {
                    throw new NonexistentEntityException("The shelters with id " + id + " no longer exists.");
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
            Shelters shelters;
            try {
                shelters = em.getReference(Shelters.class, id);
                shelters.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The shelters with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            SheltersResources sheltersResourcesOrphanCheck = shelters.getSheltersResources();
            if (sheltersResourcesOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Shelters (" + shelters + ") cannot be destroyed since the SheltersResources " + sheltersResourcesOrphanCheck + " in its sheltersResources field has a non-nullable shelters field.");
            }
            Persons personsOrphanCheck = shelters.getPersons();
            if (personsOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Shelters (" + shelters + ") cannot be destroyed since the Persons " + personsOrphanCheck + " in its persons field has a non-nullable shelters field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(shelters);
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

    public List<Shelters> findSheltersEntities() {
        return findSheltersEntities(true, -1, -1);
    }

    public List<Shelters> findSheltersEntities(int maxResults, int firstResult) {
        return findSheltersEntities(false, maxResults, firstResult);
    }

    private List<Shelters> findSheltersEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Shelters.class));
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

    public Shelters findShelters(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Shelters.class, id);
        } finally {
            em.close();
        }
    }

    public int getSheltersCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Shelters> rt = cq.from(Shelters.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
