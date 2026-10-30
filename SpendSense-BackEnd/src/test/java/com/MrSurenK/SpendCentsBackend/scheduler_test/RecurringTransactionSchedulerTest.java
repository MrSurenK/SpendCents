package com.MrSurenK.SpendCentsBackend.scheduler_test;

import com.MrSurenK.SpendCentsBackend.SpendCentsBackEndApplication;
import com.MrSurenK.SpendCentsBackend.scheduler.RecurringTransactionScheduler;
import com.MrSurenK.SpendCentsBackend.service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Recurring transaction scheduler")
class RecurringTransactionSchedulerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private RecurringTransactionScheduler scheduler;

    @Test
    @DisplayName("Scheduled run delegates to the recurring transaction generator")
    void run_delegatesToTransactionService() {
        scheduler.run();

        verify(transactionService).generateNewRecurringTransactions();
    }

    @Test
    @DisplayName("Application enables Spring scheduling")
    void applicationEnablesScheduling() {
        //Without @EnableScheduling the @Scheduled method is silently never registered
        assertNotNull(SpendCentsBackEndApplication.class.getAnnotation(EnableScheduling.class));
    }
}
