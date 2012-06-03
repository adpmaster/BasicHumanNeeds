/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bhn.controllers.jpa;

import bhn.controllers.jpa.exceptions.IllegalOrphanException;
import bhn.controllers.jpa.exceptions.NonexistentEntityException;
import bhn.controllers.jpa.exceptions.RollbackFailureException;
import entities.Persons;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
public class PersonsJpaController implements Serializable {

    public PersonsJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Persons persons) throws IllegalOrphanException, RollbackFailureException, Exception {
        List<String> illegalOrphanMessages = null;
        Shelters sheltersOrphanCheck = persons.getShelters();
        if (sheltersOrphanCheck != null) {
            Persons oldPersonsOfShelters = sheltersOrphanCheck.getPersons();
            if (oldPersonsOfShelters != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Shelters " + sheltersOrphanCheck + " already has an item of type Persons whose shelters column cannot be null. Please make another selection for the shelters field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Shelters shelters = persons.getShelters();
            if (shelters != null) {
                shelters = em.getReference(shelters.getClass(), shelters.getId());
                persons.setShelters(shelters);
            }
            em.persist(persons);
            if (shelters != null) {
                shelters.setPersons(persons);
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

    public void edit(Persons persons) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Persons persistentPersons = em.find(Persons.class, persons.getId());
            Shelters sheltersOld = persistentPersons.getShelters();
            Shelters sheltersNew = persons.getShelters();
            List<String> illegalOrphanMessages = null;
            if (sheltersNew != null && !sheltersNew.equals(sheltersOld)) {
                Persons oldPersonsOfShelters = sheltersNew.getPersons();
                if (oldPersonsOfShelters != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Shelters " + sheltersNew + " already has an item of type Persons whose shelters column cannot be null. Please make another selection for the shelters field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (sheltersNew != null) {
                sheltersNew = em.getReference(sheltersNew.getClass(), sheltersNew.getId());
                persons.setShelters(sheltersNew);
            }
            persons = em.merge(persons);
            if (sheltersOld != null && !sheltersOld.equals(sheltersNew)) {
                sheltersOld.setPersons(null);
                sheltersOld = em.merge(sheltersOld);
            }
            if (sheltersNew != null && !sheltersNew.equals(sheltersOld)) {
                sheltersNew.setPersons(persons);
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
                Integer id = persons.getId();
                if (findPersons(id) == null) {
                    throw new NonexistentEntityException("The persons with id " + id + " no longer exists.");
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
            Persons persons;
            try {
                persons = em.getReference(Persons.class, id);
                persons.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The persons with id " + id + " no longer exists.", enfe);
            }
            Shelters shelters = persons.getShelters();
            if (shelters != null) {
                shelters.setPersons(null);
                shelters = em.merge(shelters);
            }
            em.remove(persons);
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

    public List<Persons> findPersonsEntities() {
        return findPersonsEntities(true, -1, -1);
    }

    public List<Persons> findPersonsEntities(int maxResults, int firstResult) {
        return findPersonsEntities(false, maxResults, firstResult);
    }

    private List<Persons> findPersonsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Persons.class));
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

    public Persons findPersons(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Persons.class, id);
        } finally {
            em.close();
        }
    }

    public int getPersonsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Persons> rt = cq.from(Persons.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
