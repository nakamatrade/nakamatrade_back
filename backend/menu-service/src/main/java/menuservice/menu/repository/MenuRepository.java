package menuservice.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import menuservice.menu.domain.Menu;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    @Query("SELECT m FROM Menu m LEFT JOIN FETCH m.parent ORDER BY \n" +
            "        CASE WHEN m.parent IS NULL THEN 0 ELSE 1 END ASC, \n" +
            "                  m.orderSn ASC NULLS LAST")
    List<Menu> findAllOrderedByParentAndOrderSn();

    @Query("SELECT MAX(m.orderSn) FROM Menu m WHERE m.parent.id IS NULL")
    Optional<Integer> findMaxOrderSnByParentIdIsNull();

    @Query("SELECT MAX(m.orderSn) FROM Menu m WHERE m.parent.id = :parentId")
    Optional<Integer> findMaxOrderSnByParentId(@Param("parentId") Long parentId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Menu m SET m.orderSn = m.orderSn + 1 WHERE m.parent.id IS NULL AND m.orderSn >= :targetOrderSn")
    void shiftUpOrderSnAfterRoot(@Param("targetOrderSn") Integer targetOrderSn);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Menu m SET m.orderSn = m.orderSn + 1 WHERE m.parent.id = :parentId AND m.orderSn >= :targetOrderSn")
    void shiftUpOrderSnAfter(@Param("parentId") Long parentId, @Param("targetOrderSn") Integer targetOrderSn);

    boolean existsByPath(String path);
    
    boolean existsByParentId(Long parentId);
}