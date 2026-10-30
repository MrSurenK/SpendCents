package com.MrSurenK.SpendCentsBackend.service_test;

import com.MrSurenK.SpendCentsBackend.dto.requestDto.EditTransactionDto;
import com.MrSurenK.SpendCentsBackend.dto.requestDto.TransactionRequestDto;
import com.MrSurenK.SpendCentsBackend.model.Category;
import com.MrSurenK.SpendCentsBackend.model.Transaction;
import com.MrSurenK.SpendCentsBackend.model.UserAccount;
import com.MrSurenK.SpendCentsBackend.repository.CategoryRepo;
import com.MrSurenK.SpendCentsBackend.repository.TransactionRepo;
import com.MrSurenK.SpendCentsBackend.repository.UserAccountRepo;
import com.MrSurenK.SpendCentsBackend.service.JwtService;
import com.MrSurenK.SpendCentsBackend.service.TransactionService;

import com.MrSurenK.SpendCentsBackend.util.DateTimeFormats;
import com.MrSurenK.SpendCentsBackend.util.RecurringDateCalculator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("Test funtionality of transactions features")
public class TransactionServiceTest {

    @Mock
    private TransactionRepo transactionRepo;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserAccountRepo userAccountRepo;

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private CategoryRepo categoryRepo;


    @Nested
    class TransactionCRUD {

         TransactionRequestDto transactionRequestDto;


        @Test
        @DisplayName("Test functionality to add transaction")
        void addTransaction(){
            transactionRequestDto = new TransactionRequestDto();

            BigDecimal amount = new BigDecimal("100.00");
            String remarks = "Test new transaction record added.";
            String title = "Test transaction";
            LocalDate date  = DateTimeFormats.formatStringDate("17-07-2025");
            Category catObject = new Category();
            catObject.setId(3L);
            catObject.setName("Test Category");

            transactionRequestDto.setAmount(amount);
            transactionRequestDto.setRemarks(remarks);
            transactionRequestDto.setTitle(title);
            transactionRequestDto.setRecurring(false);
            transactionRequestDto.setDate(date);
            transactionRequestDto.setCategoryId(3L);

            UserAccount dummyAccount = new UserAccount();
            dummyAccount.setId(1);
            dummyAccount.setUsername("testAccount");

            Category cat = new Category();
            cat.setId(3L);

            when(categoryRepo.getValidCat(1,3L)).thenReturn(Optional.of(cat));
            when(userAccountRepo.findById(1)).thenReturn(Optional.of(dummyAccount));

            transactionService.addItem(transactionRequestDto,dummyAccount.getId());

            ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

            verify(transactionRepo,times(1)).save(captor.capture());

            Transaction transaction = captor.getValue();

            assertEquals(transactionRequestDto.getAmount(),transaction.getAmount());
            assertEquals(transactionRequestDto.getCategoryId(),transaction.getCategory().getId());
            assertEquals(transactionRequestDto.getDate(),transaction.getTransactionDate());
            assertEquals(transactionRequestDto.getRecurring(),transaction.getRecurring());
            assertEquals(transactionRequestDto.getTitle(),transaction.getTitle());
            assertEquals(transactionRequestDto.getRemarks(),transaction.getRemarks());
            assertEquals(dummyAccount,transaction.getUserAccount());
        }

        @Test
        @DisplayName("Recurring transaction is created with its first due date")
        void addRecurringTransaction_setsFirstNextDueDate() {
            TransactionRequestDto dto = new TransactionRequestDto();
            dto.setAmount(new BigDecimal("100.00"));
            dto.setTitle("Monthly Rent");
            dto.setRecurring(true);
            dto.setDate(LocalDate.of(2026, 1, 31));
            dto.setCategoryId(3L);

            UserAccount user = new UserAccount();
            user.setId(1);
            Category category = new Category();
            category.setId(3L);

            when(categoryRepo.getValidCat(1, 3L)).thenReturn(Optional.of(category));
            when(userAccountRepo.findById(1)).thenReturn(Optional.of(user));

            Transaction transaction = transactionService.addItem(dto, 1);

            assertTrue(transaction.getRecurring());
            //31 Jan has no equivalent in February, so the first due date is clamped
            assertEquals(LocalDate.of(2026, 2, 28), transaction.getNextDueDate());
        }

        @Test
        @DisplayName("Test functionality of editing existing transaction")
        void editTransaction(){
            //Arrange
            Transaction testTransaction = new Transaction();

            Category cat = new Category();
            cat.setId(2L);
            cat.setName("First Cat");

            Category newCat = new Category();
            newCat.setId(3L);
            newCat.setName("New Cat");

            UserAccount user = new UserAccount();
            user.setId(1);

            UUID id = UUID.randomUUID();
            testTransaction.setId(id);
            testTransaction.setTransactionDate(LocalDate.of(2025, 07, 29));
            testTransaction.setLastUpdated(LocalDateTime.of(2025, 07, 20,10,5));

            testTransaction.setCategory(cat);
            testTransaction.setAmount(new BigDecimal("123.45"));
            testTransaction.setRemarks("Test case");
            testTransaction.setRecurring(false);

            EditTransactionDto editForm = new EditTransactionDto();
            editForm.setRemarks("New remark");
            editForm.setRecurring(true);
            editForm.setAmount(new BigDecimal("555.00"));
            editForm.setCategoryId(3L);
            editForm.setDate(LocalDate.of(2025, 04, 03));

            //Act
            when(transactionRepo.findById(id)).thenReturn(Optional.of(testTransaction));
            when(categoryRepo.getValidCat(1, 3L)).thenReturn(Optional.of(newCat));
            transactionService.editTransaction(editForm, user.getId(), id);

            //Assert
            assertEquals(id,testTransaction.getId());
            assertEquals(editForm.getRemarks(),testTransaction.getRemarks());
            assertEquals(editForm.getAmount(),testTransaction.getAmount());
            assertEquals(editForm.getCategoryId(), testTransaction.getCategory().getId());
            assertEquals(editForm.getRecurring(),testTransaction.getRecurring());
            assertEquals(editForm.getDate(), testTransaction.getTransactionDate());
            assertEquals(RecurringDateCalculator.firstDueDate(editForm.getDate()),
                    testTransaction.getNextDueDate());
            assertNotEquals(testTransaction.getLastUpdated(),LocalDateTime
                    .of(2025, 07, 20,10,5));


        }

        @Test
        @DisplayName("Turning recurring on without changing the date initialises nextDueDate")
        void editTransaction_turningRecurringOnSetsNextDueDate() {
            LocalDate txnDate = LocalDate.of(2026, 1, 15);
            UUID id = UUID.randomUUID();

            Transaction txn = new Transaction();
            txn.setId(id);
            txn.setTransactionDate(txnDate);
            txn.setRecurring(false);
            txn.setNextDueDate(null);
            txn.setLastUpdated(LocalDateTime.now());

            EditTransactionDto dto = new EditTransactionDto();
            dto.setRecurring(true);

            when(transactionRepo.findById(id)).thenReturn(Optional.of(txn));

            transactionService.editTransaction(dto, 1, id);

            assertTrue(txn.getRecurring());
            assertEquals(RecurringDateCalculator.firstDueDate(txnDate), txn.getNextDueDate());
            verify(transactionRepo).save(txn);
        }

        @Test
        @DisplayName("Turning recurring off clears nextDueDate")
        void editTransaction_turningRecurringOffClearsNextDueDate() {
            UUID id = UUID.randomUUID();

            Transaction txn = new Transaction();
            txn.setId(id);
            txn.setTransactionDate(LocalDate.of(2026, 1, 15));
            txn.setRecurring(true);
            txn.setNextDueDate(LocalDate.of(2026, 2, 15));
            txn.setLastUpdated(LocalDateTime.now());

            EditTransactionDto dto = new EditTransactionDto();
            dto.setRecurring(false);

            when(transactionRepo.findById(id)).thenReturn(Optional.of(txn));

            transactionService.editTransaction(dto, 1, id);

            assertFalse(txn.getRecurring());
            assertNull(txn.getNextDueDate());
        }

        @Test
        @DisplayName("Changing the date of a recurring transaction resets its schedule")
        void editTransaction_changingDateOfRecurringResetsNextDueDate() {
            UUID id = UUID.randomUUID();
            LocalDate newDate = LocalDate.of(2026, 3, 31);

            Transaction txn = new Transaction();
            txn.setId(id);
            txn.setTransactionDate(LocalDate.of(2026, 1, 31));
            txn.setRecurring(true);
            txn.setNextDueDate(LocalDate.of(2026, 2, 28));
            txn.setLastUpdated(LocalDateTime.now());

            EditTransactionDto dto = new EditTransactionDto();
            dto.setDate(newDate);

            when(transactionRepo.findById(id)).thenReturn(Optional.of(txn));

            transactionService.editTransaction(dto, 1, id);

            assertEquals(newDate, txn.getTransactionDate());
            assertEquals(RecurringDateCalculator.firstDueDate(newDate), txn.getNextDueDate());
        }

        @Test
        @DisplayName("Unrelated edits do not reset an existing recurring schedule")
        void editTransaction_unrelatedEditKeepsNextDueDate() {
            UUID id = UUID.randomUUID();
            LocalDate existingDueDate = LocalDate.of(2026, 5, 15);

            Transaction txn = new Transaction();
            txn.setId(id);
            txn.setTransactionDate(LocalDate.of(2026, 4, 15));
            txn.setRecurring(true);
            txn.setNextDueDate(existingDueDate);
            txn.setLastUpdated(LocalDateTime.now());

            EditTransactionDto dto = new EditTransactionDto();
            dto.setTitle("Updated title");

            when(transactionRepo.findById(id)).thenReturn(Optional.of(txn));

            transactionService.editTransaction(dto, 1, id);

            assertEquals("Updated title", txn.getTitle());
            assertEquals(existingDueDate, txn.getNextDueDate());
        }

        @Test
        @DisplayName("Recurring generation copies all fields and advances the due date")
        void generateNewRecurringTransactions_shouldCreateCopyAndUpdateNextDueDate() {
            // Arrange
            LocalDate today = LocalDate.now();
            LocalDate anchor = today.minusMonths(1);

            Transaction template = new Transaction();
            template.setId(UUID.randomUUID());
            template.setAmount(new BigDecimal("100.00"));
            template.setTitle("Monthly Rent");
            template.setCategory(new Category());
            template.setUserAccount(new UserAccount());
            template.setTransactionDate(anchor);
            template.setRecurring(true);
            template.setNextDueDate(today);
            template.setRemarks("Monthly Rent");
            template.setLastUpdated(LocalDateTime.now());

            when(transactionRepo.findRecurringTransactionsDueBy(today))
                    .thenReturn(List.of(template));

            // Act
            transactionService.generateNewRecurringTransactions();

            // Assert
            verify(transactionRepo).findRecurringTransactionsDueBy(today);

            ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
            verify(transactionRepo, times(2)).save(captor.capture());

            Transaction savedCopy = captor.getAllValues().stream()
                    .filter(t -> !t.getRecurring())
                    .findFirst()
                    .orElseThrow();

            Transaction savedTemplate = captor.getAllValues().stream()
                    .filter(Transaction::getRecurring)
                    .findFirst()
                    .orElseThrow();

            assertEquals(template, savedCopy.getParentTransaction());
            assertEquals(template.getTitle(), savedCopy.getTitle());
            assertEquals(template.getAmount(), savedCopy.getAmount());
            assertEquals(template.getCategory(), savedCopy.getCategory());
            assertEquals(template.getUserAccount(), savedCopy.getUserAccount());
            assertEquals(template.getRemarks(), savedCopy.getRemarks());
            assertEquals(today, savedCopy.getTransactionDate());
            assertFalse(savedCopy.getRecurring());
            assertEquals(RecurringDateCalculator.nextMonthlyDueDate(anchor, today),
                    savedTemplate.getNextDueDate());
            assertTrue(savedTemplate.getNextDueDate().isAfter(today));
        }

        @Test
        @DisplayName("Recurring generation catches up every missed month")
        void generateNewRecurringTransactions_shouldCatchUpMissedMonths() {
            // Arrange
            LocalDate today = LocalDate.now();
            LocalDate anchor = today.minusMonths(3);
            LocalDate firstDueDate = RecurringDateCalculator.firstDueDate(anchor);

            List<LocalDate> expectedDates = new ArrayList<>();
            LocalDate dueDate = firstDueDate;
            while (!dueDate.isAfter(today)) {
                expectedDates.add(dueDate);
                dueDate = RecurringDateCalculator.nextMonthlyDueDate(anchor, dueDate);
            }

            Transaction template = new Transaction();
            template.setId(UUID.randomUUID());
            template.setAmount(new BigDecimal("42.00"));
            template.setTitle("Subscription");
            template.setCategory(new Category());
            template.setUserAccount(new UserAccount());
            template.setTransactionDate(anchor);
            template.setRecurring(true);
            template.setNextDueDate(firstDueDate);
            template.setLastUpdated(LocalDateTime.now());

            when(transactionRepo.findRecurringTransactionsDueBy(today))
                    .thenReturn(List.of(template));

            // Act
            transactionService.generateNewRecurringTransactions();

            // Assert
            ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
            verify(transactionRepo, times(expectedDates.size() + 1)).save(captor.capture());

            List<Transaction> copies = captor.getAllValues().stream()
                    .filter(t -> !t.getRecurring())
                    .toList();

            assertEquals(expectedDates.size(), copies.size());
            assertEquals(expectedDates, copies.stream()
                    .map(Transaction::getTransactionDate)
                    .toList());

            Transaction savedTemplate = captor.getAllValues().stream()
                    .filter(Transaction::getRecurring)
                    .findFirst()
                    .orElseThrow();
            assertEquals(dueDate, savedTemplate.getNextDueDate());
            assertTrue(savedTemplate.getNextDueDate().isAfter(today));
        }

        @Test
        @DisplayName("Recurring generation repairs a legacy row with no nextDueDate")
        void generateNewRecurringTransactions_shouldRepairMissingNextDueDate() {
            LocalDate today = LocalDate.now();
            LocalDate anchor = today.minusMonths(1);
            LocalDate firstDueDate = RecurringDateCalculator.firstDueDate(anchor);

            Transaction template = new Transaction();
            template.setId(UUID.randomUUID());
            template.setAmount(new BigDecimal("15.00"));
            template.setTitle("Legacy recurring");
            template.setCategory(new Category());
            template.setUserAccount(new UserAccount());
            template.setTransactionDate(anchor);
            template.setRecurring(true);
            template.setNextDueDate(null);
            template.setLastUpdated(LocalDateTime.now());

            when(transactionRepo.findRecurringTransactionsDueBy(today))
                    .thenReturn(List.of(template));

            transactionService.generateNewRecurringTransactions();

            ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
            verify(transactionRepo, atLeast(2)).save(captor.capture());

            List<Transaction> copies = captor.getAllValues().stream()
                    .filter(t -> !t.getRecurring())
                    .toList();

            assertFalse(copies.isEmpty());
            assertEquals(firstDueDate, copies.get(0).getTransactionDate());
            assertEquals(template.getTitle(), copies.get(0).getTitle());

            Transaction savedTemplate = captor.getAllValues().stream()
                    .filter(Transaction::getRecurring)
                    .findFirst()
                    .orElseThrow();
            assertTrue(savedTemplate.getNextDueDate().isAfter(today));
        }

        @Test
        @DisplayName("Recurring generation does nothing when no template is due")
        void generateNewRecurringTransactions_shouldDoNothingWhenNothingIsDue() {
            LocalDate today = LocalDate.now();
            when(transactionRepo.findRecurringTransactionsDueBy(today)).thenReturn(List.of());

            transactionService.generateNewRecurringTransactions();

            verify(transactionRepo, never()).save(any(Transaction.class));
        }



    }

}
