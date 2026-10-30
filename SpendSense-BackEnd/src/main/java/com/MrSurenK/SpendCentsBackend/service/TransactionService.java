package com.MrSurenK.SpendCentsBackend.service;

import com.MrSurenK.SpendCentsBackend.JPASpecifications.TransactionSpecifications;
import com.MrSurenK.SpendCentsBackend.dto.requestDto.EditTransactionDto;
import com.MrSurenK.SpendCentsBackend.dto.requestDto.TransactionFiltersDto;
import com.MrSurenK.SpendCentsBackend.dto.requestDto.TransactionRequestDto;
import com.MrSurenK.SpendCentsBackend.model.Category;
import com.MrSurenK.SpendCentsBackend.model.Transaction;
import com.MrSurenK.SpendCentsBackend.repository.CategoryRepo;
import com.MrSurenK.SpendCentsBackend.repository.TransactionRepo;
import com.MrSurenK.SpendCentsBackend.repository.UserAccountRepo;
import com.MrSurenK.SpendCentsBackend.util.RecurringDateCalculator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class TransactionService {

    /**
     * Upper bound on catch-up rows per template. Prevents a corrupt/very old due
     * date from generating an unbounded number of transactions in one run.
     */
    private static final int MAX_CATCH_UP_TRANSACTIONS_PER_TEMPLATE = 120;

    private final TransactionRepo transactionRepo;

    private final UserAccountRepo userAccountRepo;

    private final CategoryRepo categoryRepo;

    public TransactionService(TransactionRepo transactionRepo, UserAccountRepo userAccountRepo, JwtService jwtService
    ,CategoryRepo categoryRepo){
        this.transactionRepo = transactionRepo;
        this.userAccountRepo = userAccountRepo;
        this.categoryRepo = categoryRepo;
    }

    //CRUD Functionality

    public Transaction addItem(TransactionRequestDto transactionRequestDto, Integer userId
                        ){
        Transaction newItem = new Transaction();

        newItem.setAmount(transactionRequestDto.getAmount().setScale(2, RoundingMode.HALF_UP));

        Category cat = categoryRepo.getValidCat(userId, transactionRequestDto.getCategoryId()).orElseThrow(()
                ->new EntityNotFoundException("Category not found"));
        newItem.setCategory(cat);
        LocalDate txnDate = transactionRequestDto.getDate();
        newItem.setTransactionDate(txnDate);

        boolean isRecurring = Boolean.TRUE.equals(transactionRequestDto.getRecurring());
        newItem.setRecurring(isRecurring);

        if(isRecurring){
            //First occurrence is due one month after the transaction date
            newItem.setNextDueDate(RecurringDateCalculator.firstDueDate(txnDate));
        }

        //Get userAccount from Security
        newItem.setUserAccount(userAccountRepo.findById(userId).orElseThrow());

        newItem.setTitle(transactionRequestDto.getTitle());

        newItem.setRemarks(transactionRequestDto.getRemarks());

        newItem.setLastUpdated(LocalDateTime.now());

        transactionRepo.save(newItem);

        return newItem;
    }

    //Getting and sorting transactions by specific filters

    //Pass JWT token to extract username and get User Id and also Pageable object with page details in controller
    public Page<Transaction> getTransactions(Integer userId,
   TransactionFiltersDto transactionFiltersDto){
        //Get the user id from user account
       //return transactionRepo.findAllByUserAccountId(userId,page);

        Pageable page = PageRequest.of(
                transactionFiltersDto.getPage(),
                transactionFiltersDto.getSize(),
          Sort.by(
                transactionFiltersDto.getSortDirection(),
                transactionFiltersDto.getSortField()
        )
);

        return transactionRepo.findAll(
                Specification.where(TransactionSpecifications.hasUser(userId))
                        .and(TransactionSpecifications.withinDateRange(transactionFiltersDto.getStartDate(),
                                transactionFiltersDto.getEndDate()))
                        .and(TransactionSpecifications.amountBetween(transactionFiltersDto.getMin(),
                                transactionFiltersDto.getMax()))
                        .and(TransactionSpecifications.hasCategory(transactionFiltersDto.getCatId()))
                        .and(TransactionSpecifications.hasExpenseOrIncome(transactionFiltersDto.getTransactionType()))
                        .and(TransactionSpecifications.isRecurringFilter(transactionFiltersDto.getIsRecurring()))
                        .and(TransactionSpecifications.hasTitle(transactionFiltersDto.getTitle()))
                        .and(TransactionSpecifications.globalSearch(transactionFiltersDto.getKeyword())),
                         page
        );
    }


    public Transaction editTransaction(EditTransactionDto dto, int userId, UUID transactionId){
        //Get transaction that is being edited
        Transaction currTransaction = transactionRepo.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));
        //Update new information in form
        if(dto.getAmount() != null){
            currTransaction.setAmount(dto.getAmount());
        }
        if(dto.getDate() != null){
            currTransaction.setTransactionDate(dto.getDate());
        }
        if(dto.getTitle() != null){
            currTransaction.setTitle(dto.getTitle());
        }
        if(dto.getRemarks() != null){
            currTransaction.setRemarks(dto.getRemarks());
        }
        if(dto.getRecurring() != null){
            currTransaction.setRecurring(dto.getRecurring());
        }
        if(dto.getCategoryId() != null){
            Category newCat = categoryRepo.getValidCat(userId, dto.getCategoryId()).orElseThrow(()
                    -> new EntityNotFoundException("Category not found"));
            currTransaction.setCategory(newCat);
        }

        //Keep nextDueDate consistent with the recurring flag. Initialise it when
        //recurring was just switched on (or when repairing a legacy row that has
        //no due date), but do not reset an existing schedule for unrelated edits.
        if(Boolean.TRUE.equals(currTransaction.getRecurring())){
            if(dto.getDate() != null || currTransaction.getNextDueDate() == null){
                currTransaction.setNextDueDate(
                        RecurringDateCalculator.firstDueDate(currTransaction.getTransactionDate()));
            }
        } else {
            currTransaction.setNextDueDate(null);
        }

        //Update lastUpdated field
        currTransaction.setLastUpdated(LocalDateTime.now());

        //Save and update transaction uin database
        transactionRepo.save(currTransaction);

        return currTransaction;

    }

    public void deleteTransaction(UUID transactionId){
        //Get transaction id
        transactionRepo.deleteById(transactionId);
        //Perform delete operation of transaction record from db
    }

    @Transactional
    public void generateNewRecurringTransactions(){
        LocalDate today = LocalDate.now();

        List<Transaction> dueTemplates = transactionRepo.findRecurringTransactionsDueBy(today);
        int generatedTotal = 0;

        for(Transaction template: dueTemplates){

            LocalDate anchorDate = template.getTransactionDate();
            LocalDate dueDate = template.getNextDueDate();

            if(dueDate == null){
                //Legacy row: recurring was switched on without a due date being set.
                dueDate = RecurringDateCalculator.firstDueDate(anchorDate);
                log.info("Initialised missing nextDueDate for recurring template {}", template.getId());
            }

            int generatedForTemplate = 0;

            //Catch up every missed month, not only the one that is due today.
            while(!dueDate.isAfter(today)
                    && generatedForTemplate < MAX_CATCH_UP_TRANSACTIONS_PER_TEMPLATE){

                Transaction copy = new Transaction();
                copy.setParentTransaction(template);
                copy.setUserAccount(template.getUserAccount());
                copy.setAmount(template.getAmount());
                copy.setCategory(template.getCategory());
                copy.setTitle(template.getTitle()); //title is NOT NULL in the database
                copy.setRemarks(template.getRemarks());
                copy.setTransactionDate(dueDate); //the date it was due, not the run date
                copy.setRecurring(false); //copies must never be recurring or they would cascade
                copy.setLastUpdated(LocalDateTime.now());

                transactionRepo.save(copy);
                generatedForTemplate++;
                generatedTotal++;

                dueDate = RecurringDateCalculator.nextMonthlyDueDate(anchorDate, dueDate);
            }

            //Safety valve for pathological due dates: stop creating rows and move
            //the template to its next future occurrence.
            if(!dueDate.isAfter(today)){
                log.warn("Recurring template {} is more than {} months overdue; " +
                                "skipping ahead without generating further copies",
                        template.getId(), MAX_CATCH_UP_TRANSACTIONS_PER_TEMPLATE);
                while(!dueDate.isAfter(today)){
                    dueDate = RecurringDateCalculator.nextMonthlyDueDate(anchorDate, dueDate);
                }
            }

            template.setNextDueDate(dueDate);
            transactionRepo.save(template);
        }

        if(generatedTotal > 0){
            log.info("Generated {} recurring transaction(s) from {} due template(s)",
                    generatedTotal, dueTemplates.size());
        }
    }

    public Transaction getTransaction(UUID id){
        log.info("Retrieving transaction : {}", id);
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));
        log.info("Retrieved transaction successfully");
        return transaction;
    }
}
