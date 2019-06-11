package io.pivotal.pal.tracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    private Logger log = LoggerFactory.getLogger(getClass());

    HashMap<Long, TimeEntry> entities = new HashMap<>();
    Long lastMaxVal = 0l;

    @Override
    public TimeEntry create(TimeEntry toCreate) {
        Long maxVal;

        if (lastMaxVal > 0) {
            maxVal = lastMaxVal;
        } else {
            maxVal = 0l;
        }

        Long nextVal = maxVal + 1;
        this.lastMaxVal = nextVal;
        TimeEntry created = new TimeEntry(nextVal, toCreate.getProjectId(), toCreate.getUserId(), toCreate.getDate(), toCreate.getHours());
        this.entities.put(nextVal, created);

        log.info(created.toString());
        return created;
    }

    @Override
    public TimeEntry find(Long aId) {
        TimeEntry toFind = this.entities.get(aId);

        if (toFind == null) {
            return null;
            // TODO preferred way
            // throw new RuntimeException(String.format("Could not find TimeEntry with id %d", aId));
        } else {
            return toFind;
        }
    }

    @Override
    public List<TimeEntry> list() {

        List<TimeEntry> toReturn = new ArrayList<>(this.entities.values());

        return toReturn;
    }

    @Override
    public TimeEntry update(Long id, TimeEntry entry) {
        TimeEntry found = this.entities.get(id);

        if (found != null) {
            found.setDate(entry.getDate());
            found.setHours(entry.getHours());
            found.setProjectId(entry.getProjectId());
            found.setUserId(entry.getUserId());
            this.entities.put(found.getId(), found);
            return found;
        } else {
            return null;
        }
    }

    @Override
    public void delete(Long id) {
        TimeEntry found = this.entities.get(id);

        if ( found != null) {
            this.entities.remove(id);
        }
    }
}
