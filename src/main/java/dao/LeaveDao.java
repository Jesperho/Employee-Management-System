package dao;

import entity.Leave;
import java.util.List;

public interface LeaveDao {

    void create(Leave leave);

    Leave find(int id);

    void update(Leave leave);

    void delete(int id);

    List<Leave> findAll();
}