package com.morris.mms.mms;

import java.util.List; // ✅ 1. 確保有匯入這個

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByNameAndLocation(String name, String location);



    List<Item> findByLocation(String location);
    List<Item> findByCategory(String category);

    @Query("select distinct i.category from Item i where i.category is not null and i.category<>'' order by i.category")
    List<String> distinctCategories();

    /*@Query("select distinct i.room from Item i where i.room is not null and i.room<>'' order by i.room")
    List<String> distinctRooms();*/

    @Query("select distinct i.location from Item i where i.location is not null and i.location<>'' order by i.location")
    List<String> distinctLocations();

    @Query("""
        SELECT TRIM(i.location), COUNT(i.id) AS c
        FROM Item i
        WHERE i.name IS NOT NULL
          AND LOWER(TRIM(i.name)) = LOWER(TRIM(:name))
          AND i.location IS NOT NULL
          AND TRIM(i.location) <> ''
        GROUP BY TRIM(i.location)
        ORDER BY c DESC
    """)
    List<Object[]> topLocationsByName(@Param("name") String name);

    @Query("""
        select i.category, count(i) as c
        from Item i
        where lower(i.name) = lower(:name) and i.category is not null and i.category <> ''
        group by i.category
        order by c desc
    """)
    List<Object[]> topCategoriesByName(@Param("name") String name);

    Item findTop1ByNameIgnoreCaseAndSkuIsNotNullOrderByIdDesc(String name);
}