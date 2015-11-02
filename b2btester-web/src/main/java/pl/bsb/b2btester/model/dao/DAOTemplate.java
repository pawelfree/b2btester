package pl.bsb.b2btester.model.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 *
 * @author bartoszk, paweld
 */
public class DAOTemplate<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    protected Class<T> entityClass;

    public DAOTemplate(Class<T> entity) {
        this.entityClass = entity;
    }

    protected <T> T executeTransactional(JpaCallback<T> action) {
        EntityManager em = createEntityManager();
        try {
            em.getTransaction().begin();
            T result = action.doInJpa(em);
            em.getTransaction().commit();
            return result;
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new DAOException("DAOTemplate.execute exception.", ex.getCause());
        } finally {
            DAOHelper.closeEntityManager(em);
        }
    }

    protected <T> T execute(JpaCallback<T> action) {
        EntityManager em = createEntityManager();

        try {
            return action.doInJpa(em);
        } finally {
            DAOHelper.closeEntityManager(em);
        }
    }

    private EntityManager createEntityManager() {
        return DAOHelper.getEntityManagerFactory().createEntityManager();
    }

    public <T> T find(final Class<T> entityClass, final Object id) {
        return execute(new JpaCallback<T>() {
            @Override
            public T doInJpa(EntityManager em) {
                return em.find(entityClass, id);
            }
        });
    }

    public <T> T getReference(final Class<T> entityClass, final Object id) {
        return execute(new JpaCallback<T>() {
            @Override
            public T doInJpa(EntityManager em) {
                return em.getReference(entityClass, id);
            }
        });
    }

    public boolean contains(final Object entity) {
        return execute(new JpaCallback<Boolean>() {
            @Override
            public Boolean doInJpa(EntityManager em) {
                return em.contains(entity);
            }
        }).booleanValue();
    }

    public void refresh(final Object entity) {
        executeTransactional(new JpaCallback<Void>() {
            @Override
            public Void doInJpa(EntityManager em) {
                em.refresh(entity);
                return null;
            }
        });
    }

    public void persist(final T entity) {
        executeTransactional(new JpaCallback<Void>() {
            @Override
            public Void doInJpa(EntityManager em) {
                em.persist(entity);
                return null;
            }
        });
    }

    public <T> T merge(final T entity) {
        return executeTransactional(new JpaCallback<T>() {
            @Override
            public T doInJpa(EntityManager em) {
                return em.merge(entity);
            }
        });
    }

    public void remove(final T entity) {
        executeTransactional(new JpaCallback<Void>() {
            @Override
            public Void doInJpa(EntityManager em) {
                T entityAttached = em.merge(entity);
                em.remove(entityAttached);
                return null;
            }
        });
    }

    public List<T> merge(final List<T> entityList) {
        return executeTransactional(new JpaCallback<List<T>>() {
            @Override
            public List<T> doInJpa(EntityManager em) {
                List<T> result = new ArrayList<>();
                for (T entity : entityList) {
                    entity = em.merge(entity);
                    result.add(entity);
                }
                return result;
            }
        });
    }

    public List<T> getAll() {
        return execute(new JpaCallback<List<T>>() {
            @Override
            public List<T> doInJpa(EntityManager em) {
                TypedQuery<T> q = em.createQuery("select e from " + entityClass.getSimpleName() + " e", entityClass);
                return q.getResultList();
            }
        });
    }
}