package dao;

import entity.Position;
import java.util.List;

public interface PositionDao {

    void create(Position position);

    Position find(int id);

    void update(Position position);

    void delete(int id);

    List<Position> findAll();
}