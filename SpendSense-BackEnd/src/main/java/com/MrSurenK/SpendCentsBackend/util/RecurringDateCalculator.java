package com.MrSurenK.SpendCentsBackend.util;

import java.time.LocalDate;

/**
 * Date arithmetic for monthly recurring transactions.
 *
 * <p>{@link LocalDate#plusMonths(long)} clamps the day-of-month when the target
 * month is shorter (31 Jan + 1 month = 28/29 Feb). Advancing from the previous
 * due date would then keep drifting downwards forever (31 Jan -> 28 Feb -> 28
 * Mar ...). The methods here use the original transaction date as the anchor so
 * the intended day-of-month is restored as soon as a month is long enough.</p>
 */
public final class RecurringDateCalculator {

    private RecurringDateCalculator() {
    }

    /**
     * First due date for a recurring transaction: one month after its start date.
     */
    public static LocalDate firstDueDate(LocalDate startDate) {
        return nextMonthlyDueDate(startDate, startDate);
    }

    /**
     * Calculates the monthly occurrence that follows {@code currentDueDate},
     * keeping the day-of-month of {@code anchorDate} whenever that date exists
     * in the target month.
     *
     * @param anchorDate     original transaction date, used for the day-of-month
     * @param currentDueDate the occurrence being advanced from
     * @return the next occurrence, always after {@code currentDueDate}
     */
    public static LocalDate nextMonthlyDueDate(LocalDate anchorDate, LocalDate currentDueDate) {
        LocalDate nextMonth = currentDueDate.plusMonths(1);
        int dayOfMonth = Math.min(anchorDate.getDayOfMonth(), nextMonth.lengthOfMonth());
        return nextMonth.withDayOfMonth(dayOfMonth);
    }
}
