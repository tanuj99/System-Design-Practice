package repository;

import model.User;

public interface UserRepository {

    void save(User user);

    User findById(String userId);

    void delete(String userId);
}