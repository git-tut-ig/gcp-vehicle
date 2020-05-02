package org.vigor.delegates;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vigor.api.ScApi;
import org.vigor.model.ServiceCenter;

import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/v1/")
public class ScService implements ScApi {

    private Map<Long, ServiceCenter> storage = Collections.synchronizedMap(new HashMap<>());
    private AtomicLong generator = new AtomicLong(0);

    @Override
    public ResponseEntity<ServiceCenter> addSc(@Valid ServiceCenter body) {
        long id = generator.incrementAndGet();
        body.setId(id);
        storage.put(id, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @Override
    public ResponseEntity<Void> deleteSc(Long id) {
        if (storage.containsKey(id)) {
            storage.remove(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<ServiceCenter> getScById(Long id) {
        if (storage.containsKey(id)) {
            return ResponseEntity.ok(storage.get(id));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Override
    public ResponseEntity<List<ServiceCenter>> listSc() {
        ArrayList<ServiceCenter> list = new ArrayList<>(storage.values());
        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<ServiceCenter> updateSc(@Valid ServiceCenter body) {
        long id = body.getId();
        storage.put(id, body);
        return ResponseEntity.ok(body);
    }
}
