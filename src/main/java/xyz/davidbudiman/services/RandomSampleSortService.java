package xyz.davidbudiman.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xyz.davidbudiman.services.records.RandomSampleSortRecord;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Service
public class RandomSampleSortService {

    private static final Map<SortMethod, Sort> sortMethods = new HashMap<>();
    static {
        sortMethods.put(SortMethod.DEFAULT, new Sort() {
            @Override
            RandomSampleSortRecord doSort(List<Integer> samples) {
                @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
                List<Integer> toSort = new ArrayList<>(samples);
                Collections.sort(toSort);
                return new RandomSampleSortRecord(samples, toSort, Long.MIN_VALUE);
            }
        });
        sortMethods.put(SortMethod.MERGE_SORT, new MergeSort());
        sortMethods.put(SortMethod.INSERTION_SORT, new InsertionSort());
        sortMethods.put(SortMethod.SELECTION_SORT, new SelectionSort());
        sortMethods.put(SortMethod.BUBBLE_SORT, new BubbleSort());
    }

    private final SortMethod sortMethod;

    @Autowired
    public RandomSampleSortService(@Value("${sort_method:default}") String sortMethod) {
        if (sortMethod == null) throw new IllegalArgumentException("sortMethod:null");
        this.sortMethod = SortMethod.valueOf(sortMethod);
    }

    public RandomSampleSortRecord randomSampleSort() {
        int sampleSize = ThreadLocalRandom.current().nextInt(1000, 10000);
        sampleSize = 5;
        List<Integer> samples = new ArrayList<>();
        for (int idx = 0; idx < sampleSize; idx++) {
            samples.add(ThreadLocalRandom.current().nextInt());
        }
        return sortMethods.get(sortMethod).sort(samples);
    }
}

abstract class Sort {
    RandomSampleSortRecord sort(List<Integer> samples) {
        long startTime = System.nanoTime();
        RandomSampleSortRecord result = doSort(samples);
        long endTime = System.nanoTime();
        return result.updateDuration(endTime - startTime);
    }

    abstract RandomSampleSortRecord doSort(List<Integer> samples);
}

class MergeSort extends Sort {
    List<Integer> merge(List<Integer> leftArr, List<Integer> rightArr) {
        int low = leftArr.size(); // size of the left sub array
        int high = rightArr.size(); // size of the right sub array
        int i = 0, j = 0; // reset loop variables before performing merge

        List<Integer> arr = new ArrayList<>();
        while (i < low && j < high) { // merge the left and right sub arrays
            if (leftArr.get(i) <= rightArr.get(j)) {
                arr.add(leftArr.get(i));
                i++;
            }
            else {
                arr.add(rightArr.get(j));
                j++;
            }
        }

        while (i < low) { // merge the remaining elements from the left sub array
            arr.add(leftArr.get(i));
            i++;
        }

        while (j < high) { // merge the remaining elements from right sub array
            arr.add(rightArr.get(j));
            j++;
        }

        return arr;
    }


    List<Integer> mergeSort(List<Integer> arr) { // helper function that creates the cases for sorting
        if (arr.size() > 1) { // sort only if the left index is lesser than the right index (meaning that sorting is done)
            int middle = (arr.size() / 2);

            List<Integer> leftArr = new ArrayList<>(arr.subList(0, middle));
            leftArr = mergeSort(leftArr); // left sub array
            List<Integer> rightArr = new ArrayList<>(arr.subList(middle, arr.size()));
            rightArr = mergeSort(rightArr); // right sub array

            arr = merge(leftArr, rightArr); // merge the two sub arrays
        }
        return arr;
    }

    RandomSampleSortRecord doSort(List<Integer> samples) {
        List<Integer> toSort = new ArrayList<>(samples);
        List<Integer> arr = mergeSort(toSort);
        return new RandomSampleSortRecord(samples, arr, Long.MIN_VALUE);
    }
}

class InsertionSort extends Sort {
    @Override
    RandomSampleSortRecord doSort(List<Integer> samples) {
        List<Integer> throwaway = new ArrayList<>(samples);
        List<Integer> toSort = new ArrayList<>();
        int size = samples.size();
        for (int i = 0; i < size; i++) {
            AtomicInteger MIN = new AtomicInteger(Integer.MAX_VALUE);
            AtomicInteger MIN_IDX = new AtomicInteger(0);
            IntStream.range(0, throwaway.size()).forEach(idx -> {
                if (throwaway.get(idx) < MIN.get()) {
                    MIN.set(throwaway.get(idx));
                    MIN_IDX.set(idx);
                }
            });
            toSort.add(MIN.get());
            throwaway.remove(MIN_IDX.get());
        }
        return new RandomSampleSortRecord(samples, toSort, Long.MIN_VALUE);
    }
}

class SelectionSort extends Sort {
    @Override
    RandomSampleSortRecord doSort(List<Integer> samples) {
        Integer[] toSort = samples.toArray(new Integer[0]);

        for (int i = 0; i < toSort.length; i++) {
            int pos = i;
            for (int j = i + 1; j < toSort.length; j++) {
                if (toSort[j] < toSort[pos]) {
                    pos = j;
                }
            }
            Integer temp = toSort[pos];
            toSort[pos] = toSort[i];
            toSort[i] = temp;
        }

        return new RandomSampleSortRecord(samples, Arrays.asList(toSort), Long.MIN_VALUE);
    }
}

class BubbleSort extends Sort {
    @Override
    RandomSampleSortRecord doSort(List<Integer> samples) {
        Integer[] toSort = samples.toArray(new Integer[0]);

        for (int i = 0; i < samples.size(); i++) {
            for (int j = i + 1; j < samples.size(); j++) {
                if (toSort[j] < toSort[i]) {
                    Integer temp = toSort[j];
                    toSort[j] = toSort[i];
                    toSort[i] = temp;
                }
            }
        }

        return new RandomSampleSortRecord(samples, Arrays.asList(toSort), Long.MIN_VALUE);
    }
}
