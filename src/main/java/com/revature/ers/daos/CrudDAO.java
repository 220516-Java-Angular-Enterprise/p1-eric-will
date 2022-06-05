package com.revature.ers.daos;

import java.util.List;

public interface CrudDAO<T> {

    void save(T obj);

    void update(T obj);

    void delete(String id);

    T getById(String id);

    List<T> getAll();

}
