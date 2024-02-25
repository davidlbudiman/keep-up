package xyz.davidbudiman.services;

import com.google.common.collect.Ordering;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import xyz.davidbudiman.services.records.RandomSampleSortRecord;

import static org.junit.jupiter.api.Assertions.*;

public class RandomSampleSortServiceTests {
    @ParameterizedTest
    @EnumSource(SortMethod.class)
    public void randomSampleSort_ShouldReturnRecordForValidMethod(SortMethod sortMethod) {
        RandomSampleSortService service = new RandomSampleSortService(sortMethod.name());
        RandomSampleSortRecord record = service.randomSampleSort();
        assertNotNull(record);
        assertNotNull(record.samples());
        assertNotNull(record.elapsedTime());
        assertFalse(record.samples().isEmpty());
        assertTrue(Ordering.natural().isOrdered(record.result()));
        assertTrue(record.elapsedTime() > 0);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"UNKNOWN"})
    public void randomSampleSort_ShouldThrowExceptionForInvalidMethod(String method) {
        assertThrows(IllegalArgumentException.class, () -> new RandomSampleSortService(method));
    }
}
