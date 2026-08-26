package com.moneymanager.repositories;

import com.moneymanager.entity.ExpenseEntity;
import com.moneymanager.entity.IncomeEntity;
import org.springdoc.core.converters.models.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<IncomeEntity, Long> {

    // find the income for current logged user
    // select * from income where profile_id = ?1 order by date desc
    List<IncomeEntity> findByProfileEntity_IdOrderByDateDesc(Long profileId);

    // select * from income where profile_id = ?1 order by date desc limit 5
    List<IncomeEntity> findTop5ByProfileEntity_IdOrderByDateDesc(Long profileId);

    // sum of the total income
    // select * from tbl_income where profileId = ?1 and date between ?2 and ?3 and name like %?4%
    @Query("SELECT SUM(e.amount) FROM IncomeEntity e where e.profileEntity.id = :profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);

    List<IncomeEntity> findByProfileEntity_IdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyWord,
            Sort sort
    );

    // select * from tbl_income where profile_id = ?1 and date between ?2 and ?3
    List<IncomeEntity> findByProfileEntity_IdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);
}
