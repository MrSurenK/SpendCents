package com.MrSurenK.SpendCentsBackend.util_test;

import com.MrSurenK.SpendCentsBackend.util.RecurringDateCalculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Monthly recurring date calculations")
class RecurringDateCalculatorTest {

    @Test
    @DisplayName("First due date is one month after the start date")
    void firstDueDate_isOneMonthAfterStartDate() {
        assertEquals(LocalDate.of(2026, 2, 15),
                RecurringDateCalculator.firstDueDate(LocalDate.of(2026, 1, 15)));
        //The day is clamped when the target month is shorter
        assertEquals(LocalDate.of(2026, 2, 28),
                RecurringDateCalculator.firstDueDate(LocalDate.of(2026, 1, 31)));
    }

    @Test
    @DisplayName("The 31st is restored after a short month")
    void nextMonthlyDueDate_restoresThe31stAfterFebruary() {
        LocalDate anchor = LocalDate.of(2026, 1, 31);

        LocalDate february = RecurringDateCalculator.nextMonthlyDueDate(anchor, anchor);
        assertEquals(LocalDate.of(2026, 2, 28), february);

        LocalDate march = RecurringDateCalculator.nextMonthlyDueDate(anchor, february);
        assertEquals(LocalDate.of(2026, 3, 31), march);

        LocalDate april = RecurringDateCalculator.nextMonthlyDueDate(anchor, march);
        assertEquals(LocalDate.of(2026, 4, 30), april);

        LocalDate may = RecurringDateCalculator.nextMonthlyDueDate(anchor, april);
        assertEquals(LocalDate.of(2026, 5, 31), may);
    }

    @Test
    @DisplayName("The 30th is restored after February")
    void nextMonthlyDueDate_restoresThe30thAfterFebruary() {
        LocalDate anchor = LocalDate.of(2026, 1, 30);
        LocalDate february = RecurringDateCalculator.nextMonthlyDueDate(anchor, anchor);

        assertEquals(LocalDate.of(2026, 2, 28), february);
        assertEquals(LocalDate.of(2026, 3, 30),
                RecurringDateCalculator.nextMonthlyDueDate(anchor, february));
    }

    @Test
    @DisplayName("A leap-day anchor is restored in later years")
    void nextMonthlyDueDate_restoresLeapDay() {
        LocalDate anchor = LocalDate.of(2024, 2, 29);
        LocalDate february2025 = LocalDate.of(2025, 2, 28);

        assertEquals(LocalDate.of(2025, 3, 29),
                RecurringDateCalculator.nextMonthlyDueDate(anchor, february2025));
    }

    @Test
    @DisplayName("Every generated occurrence moves forward")
    void nextMonthlyDueDate_alwaysMovesForward() {
        LocalDate anchor = LocalDate.of(2026, 1, 31);
        LocalDate current = anchor;

        for (int i = 0; i < 24; i++) {
            LocalDate next = RecurringDateCalculator.nextMonthlyDueDate(anchor, current);
            assertTrue(next.isAfter(current), "next due date must move forward");
            current = next;
        }
    }
}
