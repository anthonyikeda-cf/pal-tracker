package io.pivotal.pal.tracker;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = "/time-entries")
public class TimeEntryController {

    private Logger log = LoggerFactory.getLogger(getClass());

    private final TimeEntryRepository repository;
    private final DistributionSummary timeEntrySummary;
    private final Counter actionCounter;

    @Autowired
    public TimeEntryController(TimeEntryRepository aRepository, MeterRegistry meterRegistry) {
        this.repository = aRepository;
        this.timeEntrySummary = meterRegistry.summary("timeEntry.summary");
        this.actionCounter = meterRegistry.counter("timeEntry.actionCounter");
    }

    @PostMapping
    public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry toCreate) {
        log.info("To Create: {}", toCreate.toString());
        TimeEntry created = repository.create(toCreate);
        actionCounter.increment();
        timeEntrySummary.record(repository.list().size());

        return ResponseEntity.created(URI.create(String.format("/time-entries/%d", created.getId()))).body(created);
    }

    @GetMapping("/{entry_id}")
    public ResponseEntity<TimeEntry> read(@PathVariable("entry_id") long entryId) {
        TimeEntry found = this.repository.find(entryId);
        if (found != null) {
            actionCounter.increment();
            return ResponseEntity.ok(found);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<TimeEntry>> list() {
        actionCounter.increment();
        return ResponseEntity.status(HttpStatus.OK).body(this.repository.list());
    }

    @PutMapping("/{entry_id}")
    public ResponseEntity<TimeEntry> update(@PathVariable("entry_id") long entryId, @RequestBody TimeEntry toUpdate) {
        TimeEntry updated = this.repository.update(entryId, toUpdate);

        if (updated != null) {
            actionCounter.increment();
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{entry_id}")
    public ResponseEntity<TimeEntry> delete(@PathVariable("entry_id") long entryId) {
        log.info("Deleting TimeEntry {}", entryId);
        this.repository.delete(entryId);
        actionCounter.increment();
        timeEntrySummary.record(repository.list().size());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
