package com.morris.mms.mms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByLocation(String location);
    List<Item> findByCategory(String category);

    @Query("select distinct i.category from Item i where i.category is not null and i.category<>'' order by i.category")
    List<String> distinctCategories();

    @Query("select distinct i.room from Item i where i.room is not null and i.room<>'' order by i.room")
    List<String> distinctRooms();

    @Query("select distinct i.location from Item i where i.location is not null and i.location<>'' order by i.location")
    List<String> distinctLocations();
}
