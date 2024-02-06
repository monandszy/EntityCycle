package code.business.dao;

public interface DAO<T> {
   
   T create(T t);
   
   T get(Integer id);
   
   void delete(T t);
}