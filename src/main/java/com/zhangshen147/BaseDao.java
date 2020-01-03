package com.zhangshen147;


public interface BaseDao<T> {
    int add( T t);
    void delete( int id);
    int update(int id,T t);
    T queryByID(int id, Class<T> clazz, Object[] params);
    T[] queryAll( Class<T> clazz, Object[] params);
}
