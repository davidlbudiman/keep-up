package xyz.davidbudiman.services.records;

import java.util.List;

public record RandomSampleSortRecord(List<Integer> samples, List<Integer> result, Long elapsedTime) {
    public RandomSampleSortRecord updateDuration(Long elapsedTime) {
        return new RandomSampleSortRecord(samples(), result(), elapsedTime);
    }
}
