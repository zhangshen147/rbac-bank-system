package com.zhangshen147;


public interface BaseDao<T> {
    int add( T t);
    void delete( int id);
    int update(T t);
    T query(Object[] params,Class<T> clazz);
    T queryAll(Object[] params,Class<T> clazz);
}
