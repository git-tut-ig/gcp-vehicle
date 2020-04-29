package org.vigor.delegates;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.vigor.api.PetApi;
import org.vigor.model.ModelApiResponse;
import org.vigor.model.Pet;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Controller
public class PetDelegate implements PetApi {

    private AtomicLong idSequence = new AtomicLong();
    private Map<Long, Pet> pets = Collections.synchronizedMap(new HashMap<Long, Pet>());

    private HttpHeaders headers = new HttpHeaders();

    public PetDelegate() throws IOException {
        headers.set("X-HOSTNAME", InetAddress.getLocalHost().getHostName());
    }

    @Override
    public ResponseEntity<Void> addPet(Pet body) {
        Long id = idSequence.incrementAndGet();
        body.setId(id);
        pets.put(id, body);
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deletePet(Long petId, String apiKey) {
        Long id = petId;
        if (! pets.containsKey(id)) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }

        pets.remove(id);
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

/*    @Override
    public ResponseEntity<List<Pet>> findPetsByStatus(List<String> status) {
        return null;
    }

    @Override
    public ResponseEntity<List<Pet>> findPetsByTags(List<String> tags) {
        return null;
    }*/

    @Override
    public ResponseEntity<Pet> getPetById(Long petId) {
        Pet pet = pets.get(petId);
        if (pet != null) {
            return new ResponseEntity<>(pet,headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<List<Pet>> listPets() {
        List<Pet> result = new ArrayList(pets.values());
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updatePet(Pet body) {
        Long id = body.getId();
        Pet pet = pets.get(id);
        if (pet != null) {
            return new ResponseEntity<>(headers, HttpStatus.NOT_FOUND);
        }

        pets.put(id, body);
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

   /* @Override
    public ResponseEntity<Void> updatePetWithForm(Long petId, String name, String status) {
        return null;
    }

    @Override
    public ResponseEntity<ModelApiResponse> uploadFile(Long petId, String additionalMetadata, MultipartFile file) {
        return null;
    }*/
}
