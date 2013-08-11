package tw.edu.ym.guid.querier.mybatis;

public interface MybatisBlock<E> {

  void yield(E example);

}
