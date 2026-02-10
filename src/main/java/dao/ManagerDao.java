package dao;

import entity.Manager;
import java.util.List;

public interface ManagerDao {

    void create(Manager manager);

    Manager find(int id);

    void update(Manager manager);

    void delete(int id);

    List<Manager> findAll();
}
