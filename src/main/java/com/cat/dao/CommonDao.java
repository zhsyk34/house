package com.cat.dao;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface CommonDao<E, K extends Serializable> {

	EntityManager manager();

	void save(E e);

	void saves(Collection<E> es);

	int deleteById(K k);

	int deleteByIds(K[] ks);

	int deleteByIds(Collection<K> ks);

	int deleteByEntity(E e);

	int deleteByEntities(Collection<E> es);

	long deleteAll();

	void update(E e);

	void merge(E e);

	boolean contains(E e);

	E findById(K k);

	List<E> findList();

	long count();

}
