package xyz.davidbudiman.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.davidbudiman.controllers.records.Response;
import xyz.davidbudiman.services.RandomSampleSortService;
import xyz.davidbudiman.services.records.RandomSampleSortRecord;

@RestController
@RequestMapping("/random-sort")
public class RandomSampleSortController {

    @Autowired
    private RandomSampleSortService randomSampleSortService;

    @GetMapping()
    public Response<RandomSampleSortRecord> randomSampleSort() {
        RandomSampleSortRecord data = randomSampleSortService.randomSampleSort();
        return new Response<>(data, null);
    }
}
