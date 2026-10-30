package com.MrSurenK.SpendCentsBackend.repository;

import com.MrSurenK.SpendCentsBackend.model.Transaction;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {

//    @Query(value="SELECT t FROM Transaction t WHERE(t.userAccount.id =:userId AND t.transactionDate >=:startDate AND t.transactionDate <:endDate AND")
//    Page<Transaction> findAllTransactionsWithFilters(@Param("userId") Integer userId,
//                                                     @Param("startDate") LocalDate startDate,
//                                                     @Param("endDate") LocalDate endDate,
//                                                     @Param("recurring") Boolean recurring,
//                                                     @Param("type") TransactionType type,
//                                                     @Param("category") Category cat,
//                                                     @Param("amount") Double amount,
//                                                     @Param("item") String item,
//                                                     @Param("description") String description,
//                                                     Pageable page); //Query to be used for all filtering logic

    Page<Transaction> findAllByUserAccountId(int userId, Pageable page);
    Page<Transaction>findAllByCategoryIdAndUserAccountId(Long catId, int userId, Pageable page);
    Page<Transaction> findAllByUserAccountIdAndTransactionDateBetween(int userId,LocalDate startDate, LocalDate endDate,
                                                                      Pageable page);

    /**
     * Recurring templates whose next occurrence is due today or earlier.
     * Using {@code <=} (rather than equality) lets the daily job catch up on
     * occurrences missed while the application was down. Rows with a null due
     * date are included so legacy rows created before the nextDueDate fix are
     * repaired on the next run.
     */
    @Query("SELECT t FROM Transaction t WHERE t.recurring = true " +
            "AND (t.nextDueDate IS NULL OR t.nextDueDate <= :date) ORDER BY t.nextDueDate ASC")
    List<Transaction> findRecurringTransactionsDueBy(@Param("date") LocalDate date);

    //More efficient than using MONTH and YEAR filters
    @Query(value ="SELECT t FROM Transaction t WHERE (t.userAccount.id = :userId AND t.transactionDate >=:startDate AND t.transactionDate < :endDate AND t.category.transactionType = com.MrSurenK.SpendCentsBackend.model.TransactionType.EXPENSE) ORDER BY t.amount DESC")
    List<Transaction> findTopFiveByAmount(@Param("userId") Integer userId, @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate, Pageable page);

    @Query(value="SELECT t FROM Transaction t WHERE (t.userAccount.id=:userId AND t.category.transactionType = com.MrSurenK.SpendCentsBackend.model.TransactionType.EXPENSE AND t.recurring=true AND t.category.name='Subscription')")
    List<Transaction>allRecurringSpend(@Param("userId") Integer userId, Pageable page);

    @Query("SELECT COALESCE(SUM(t.amount),0) FROM Transaction t WHERE t.userAccount.id=:userId AND t.category.transactionType = com.MrSurenK.SpendCentsBackend.model.TransactionType.INCOME AND t.category.name='Bonus' AND t.transactionDate >=:startDate AND t.transactionDate <=:endDate")
    BigDecimal sumSalaryBonusForMth(@Param("userId") Integer userId, @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(t.amount),0) FROM Transaction t WHERE t.userAccount.id=:userId AND t.category.transactionType = com.MrSurenK.SpendCentsBackend.model.TransactionType.INCOME AND t.category.name='Salary' AND t.transactionDate >=:startDate AND t.transactionDate <=:endDate")
    BigDecimal sumAllSalaryForMth(@Param("userId") Integer userId, @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(t.amount),0) FROM Transaction t WHERE t.userAccount.id=:userId AND t.category.transactionType=com.MrSurenK.SpendCentsBackend.model.TransactionType.EXPENSE AND t.transactionDate >=:startDate AND t.transactionDate <=:endDate")
    BigDecimal sumTotalSpend(@Param("userId") Integer userId, @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(t.amount),0) FROM Transaction t WHERE t.userAccount.id=:userId AND t.category.transactionType=com.MrSurenK.SpendCentsBackend.model.TransactionType.INCOME AND t.transactionDate >=:startDate AND t.transactionDate <=:endDate")
    BigDecimal sumTotalIncome(@Param("userId") Integer userId, @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate);

    //Join table and find expense amount, category for given data range
    @Query("SELECT t from Transaction t JOIN FETCH t.category c WHERE (t.userAccount.id=:userId AND MONTH(t.transactionDate)=:mth AND YEAR(t.transactionDate)=:year AND c.transactionType = com.MrSurenK.SpendCentsBackend.model.TransactionType.EXPENSE)")
    List<Transaction>findAllTransactionsInGivenTimePeriod(@Param("userId") Integer userId,
                                                          @Param("mth") Integer mth,
                                                          @Param("year") Integer year);


}
