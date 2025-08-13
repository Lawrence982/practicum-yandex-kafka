//package ru.yandex.practicum.database.repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.history.RevisionRepository;
//import org.springframework.data.repository.query.Param;
//import ru.yandex.practicum.database.entity.User;
//
//import java.util.List;
//
//public interface UserRepository extends JpaRepository<User, Long>, RevisionRepository<User, Long, Integer> {
//
//    @Query(value = "SELECT u FROM User u WHERE u.name like %:name%")
//    List<User> findAllByNameContaining(@Param(value = "name") String name);
//
//}
