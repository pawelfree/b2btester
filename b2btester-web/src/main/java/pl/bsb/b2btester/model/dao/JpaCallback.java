package pl.bsb.b2btester.model.dao;

import javax.persistence.EntityManager;

/**
 *
 * @author paweld
 * @param <T>
 */
public interface JpaCallback<T> {

    T doInJpa(EntityManager em);

}
