package com.cat.dao.impl;

import com.cat.dao.CommonDao;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommonDaoImpl<E, K extends Serializable> implements CommonDao<E, K> {

	protected static String id;
	protected final Class<E> clazz;
	protected final Class<E> key;
	@PersistenceContext
//    @PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	protected CommonDaoImpl() {
		Type type = this.getClass().getGenericSuperclass();
		if (type != null && type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			clazz = (Class<E>) parameterizedType.getActualTypeArguments()[0];
			key = (Class<E>) parameterizedType.getActualTypeArguments()[1];
		} else {
			throw new RuntimeException();
		}
	}

	@Override
	public final EntityManager manager() {
//		return entityManagerFactory.createEntityManager();
		return entityManager;
	}

	@PostConstruct
	protected final void id() {
		Metamodel metamodel = entityManager.getMetamodel();

		EntityType<E> entityType = metamodel.entity(clazz);

		assert entityType.getIdType().getJavaType() == key;

		id = entityType.getId(key).getName();
		assert id != null && !id.isEmpty();
	}

	@Override
	public void save(E e) {
		assert e != null;
		manager().persist(e);
	}

	@Override
	public void saves(Collection<E> es) {
		assert es != null && !es.isEmpty();
		es.forEach(this::save);
	}

	@Override
	public int deleteById(K k) {
		assert k != null;
		EntityManager manager = manager();

		CriteriaBuilder builder = manager.getCriteriaBuilder();

		CriteriaDelete<E> query = builder.createCriteriaDelete(clazz);
		query.where(builder.equal(query.from(clazz).get(id), k));

		return manager.createQuery(query).executeUpdate();
	}

	@Override
	public int deleteByIds(K[] ks) {
		if (ks == null || ks.length == 0) {
			return 0;
		}
		EntityManager manager = manager();
		CriteriaDelete<E> query = manager.getCriteriaBuilder().createCriteriaDelete(clazz);
		query.where(query.from(clazz).get(id).in(ks));
		return manager.createQuery(query).executeUpdate();
	}

	@Override
	public int deleteByIds(Collection<K> ks) {
		if (ks == null || ks.isEmpty()) {
			return 0;
		}
		EntityManager manager = manager();
		CriteriaDelete<E> query = manager.getCriteriaBuilder().createCriteriaDelete(clazz);
		query.where(query.from(clazz).get(id).in(ks));
		return manager.createQuery(query).executeUpdate();
	}

	@Override
	public int deleteByEntity(E e) {
		assert e != null;
		EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();
		@SuppressWarnings("unchecked")
		K id = (K) entityManagerFactory.getPersistenceUnitUtil().getIdentifier(e);
		return this.deleteById(id);
	}

	@Override
	public int deleteByEntities(Collection<E> es) {
		assert es != null && !es.isEmpty();
		EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();
		Collection<K> ks = new ArrayList<>();
		es.forEach(e -> {
			@SuppressWarnings("unchecked")
			K id = (K) entityManagerFactory.getPersistenceUnitUtil().getIdentifier(e);
			ks.add(id);
		});
		return this.deleteByIds(ks);
	}

	@Override
	public long deleteAll() {
		EntityManager manager = manager();
		CriteriaDelete<E> query = manager.getCriteriaBuilder().createCriteriaDelete(clazz);
		query.from(clazz);
		return manager.createQuery(query).executeUpdate();
	}

	@Override
	public void update(E e) {
		assert e != null;
		this.merge(e);
	}

	@Override
	public void merge(E e) {
		assert e != null;
		manager().merge(e);
	}

	@Override
	public boolean contains(E e) {
		return e != null && manager().contains(e);
	}

	@Override
	public E findById(K k) {
		assert k != null;
		return manager().find(clazz, k);
	}

	@Override
	public List<E> findList() {
		EntityManager manager = manager();

		CriteriaQuery<E> query = manager.getCriteriaBuilder().createQuery(clazz);
		query.from(clazz);

		return manager.createQuery(query).getResultList();
	}

	@Override
	public long count() {
		EntityManager manager = manager();

		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<E> root = query.from(clazz);
		query.select(builder.count(root));

		return manager.createQuery(query).getSingleResult();
	}
}
