package com.moneymanager.repositories;

import com.moneymanager.entity.ExpenseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    // find the expenses for current logged user
    // select * from expenses where profile_id = ?1 order by date desc
    List<ExpenseEntity> findByProfileEntity_IdOrderByDateDesc(Long profileId);

    // select * from expenses where profile_id = ?1 order by date desc limit 5
    List<ExpenseEntity> findTop5ByProfileEntity_IdOrderByDateDesc(Long profileId);

    // sum of the total expenses
    // select * from tbl_expenses where profileId = ?1 and date between ?2 and ?3 and name like %?4%
    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e where e.profileEntity.id = :profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);

    List<ExpenseEntity> findByProfileEntity_IdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyWord,
            Sort sort
    );

    // select * from tbl_expenses where profile_id = ?1 and date between ?2 and ?3
    List<ExpenseEntity> findByProfileEntity_IdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);
}
