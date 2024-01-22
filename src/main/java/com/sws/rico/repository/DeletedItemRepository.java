package com.sws.rico.repository;

import com.sws.rico.entity.DeletedItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeletedItemRepository extends JpaRepository<DeletedItem, Long> {
}
