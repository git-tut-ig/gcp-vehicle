package org.vigor.delegates;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vigor.api.ScApi;
import org.vigor.model.ServiceCenter;

import javax.validation.Valid;
import java.io.IOException;
import java.lang.reflect.InaccessibleObjectException;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/v1/")
public class ScService implements ScApi {

    private Map<Long, ServiceCenter> storage = Collections.synchronizedMap(new HashMap<>());
    private AtomicLong generator = new AtomicLong(0);
    private HttpHeaders headers;
    private AtomicBoolean hcMig = new AtomicBoolean(true);
    private AtomicBoolean hcLb = new AtomicBoolean(true);

    public ScService() throws IOException {
        String hostname = InetAddress.getLocalHost().getHostName();
        headers = new HttpHeaders();
        headers.set("X-HOST", hostname);
    }

    @Override
    public ResponseEntity<ServiceCenter> addSc(@Valid ServiceCenter body) {
        long id = generator.incrementAndGet();
        body.setId(id);
        storage.put(id, body);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .headers(headers)
                .body(body);
    }

    @Override
    public ResponseEntity<Void> deleteSc(Long id) {
        if (storage.containsKey(id)) {
            storage.remove(id);
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .build();
        } else {
            return ResponseEntity
                    .notFound()
                    .headers(headers)
                    .build();
        }
    }

    @Override
    public ResponseEntity<ServiceCenter> getScById(Long id) {
        if (storage.containsKey(id)) {
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(storage.get(id));
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .headers(headers)
                    .build();
        }
    }

    @Override
    public ResponseEntity<List<ServiceCenter>> listSc() {
        ArrayList<ServiceCenter> list = new ArrayList<>(storage.values());
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(list);
    }

    @Override
    public ResponseEntity<ServiceCenter> updateSc(@Valid ServiceCenter body) {
        long id = body.getId();
        storage.put(id, body);
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(body);
    }

    @Override
    public ResponseEntity<Void> deleteAllSc() {
        storage.clear();
        return ResponseEntity
                .ok()
                .headers(headers)
                .build();
    }

    @Override
    public ResponseEntity<Void> hcLb() {
        HttpStatus status = hcLb.get() ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(status).headers(headers).build();
    }

    @Override
    public ResponseEntity<Void> hcMig() {
        HttpStatus status = hcMig.get() ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(status).headers(headers).build();
    }

    @Override
    public ResponseEntity<Void> triggerHcLb() {
        hcLb.set(false);
        return ResponseEntity.ok().headers(headers).build();
    }

    @Override
    public ResponseEntity<Void> triggerHcMig() {
        hcMig.set(false);
        return ResponseEntity.ok().headers(headers).build();
    }
}
