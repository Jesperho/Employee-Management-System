package dao;

import entity.ContractType;
import java.util.List;

public interface ContractTypeDao {

    void create(ContractType contractType);

    ContractType find(int id);

    void update(ContractType contractType);

    void delete(int id);

    List<ContractType> findAll();
}