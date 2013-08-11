package tw.edu.ym.guid.querier.mybatis;

import java.util.List;

public interface MybatisCRUD<T, E> {

  void insert(T record);

  List<T> select(MybatisBlock<E> block);

  void update(T record, MybatisBlock<E> block);

  void delete(MybatisBlock<E> block);

  int count(MybatisBlock<E> block);

}
