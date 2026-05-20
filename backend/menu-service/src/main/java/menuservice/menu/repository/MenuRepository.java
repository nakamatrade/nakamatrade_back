package menuservice.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import menuservice.menu.domain.Menu;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    @Query("SELECT MAX(m.folderSn) FROM Menu m WHERE m.parentId IS NULL")
    Optional<Integer> findMaxFolderSnByParentIdIsNull();

    @Query("SELECT MAX(m.itemSn) FROM Menu m WHERE m.parentId = :parentId")
    Optional<Integer> findMaxItemSnByParentId(@Param("parentId") Long parentId);
	
    boolean existsByPath(String path);
    
    boolean existsByParentId(Long parentId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Menu m SET m.folderSn = m.folderSn + 1 WHERE m.parentId IS NULL AND m.folderSn >= :targetFolderSn")
    void shiftUpFolderSnAfter(@Param("targetFolderSn") Integer targetFolderSn);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Menu m SET m.itemSn = m.itemSn + 1 WHERE m.parentId = :parentId AND m.itemSn >= :targetItemSn")
    void shiftUpItemSnAfter(@Param("parentId") Long parentId, @Param("targetItemSn") Integer targetItemSn);
    
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Menu m SET m.folderSn = m.folderSn + 1 WHERE m.parentId IS NULL AND m.folderSn >= :newItemSn AND m.folderSn < :oldItemSn")
    void shiftUpFolderSnBetween(@Param("newItemSn") Integer newItemSn, @Param("oldItemSn") Integer oldItemSn);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Menu m SET m.folderSn = m.folderSn - 1 WHERE m.parentId IS NULL AND m.folderSn > :oldItemSn AND m.folderSn <= :newItemSn")
    void shiftDownFolderSnBetween(@Param("oldItemSn") Integer oldItemSn, @Param("newItemSn") Integer newItemSn);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Menu m SET m.itemSn = m.itemSn + 1 WHERE m.parentId = :parentId AND m.itemSn >= :newItemSn AND m.itemSn < :oldItemSn")
    void shiftUpItemSnBetween(@Param("parentId") Long parentId, @Param("newItemSn") Integer newItemSn, @Param("oldItemSn") Integer oldItemSn);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Menu m SET m.itemSn = m.itemSn - 1 WHERE m.parentId = :parentId AND m.itemSn > :oldItemSn AND m.itemSn <= :newItemSn")
    void shiftDownItemSnBetween(@Param("parentId") Long parentId, @Param("oldItemSn") Integer oldItemSn, @Param("newItemSn") Integer newItemSn);
}