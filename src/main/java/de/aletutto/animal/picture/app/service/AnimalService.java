package de.aletutto.animal.picture.app.service;

import de.aletutto.animal.picture.app.persistence.Animal;
import de.aletutto.animal.picture.app.persistence.AnimalRepository;
import de.aletutto.animal.picture.app.persistence.AnimalType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.IntStream;


@Slf4j
@Service
public class AnimalService {

    @Autowired
    AnimalRepository animalRepository;

    private final RestClient restClient;

    public AnimalService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public void fetchAndSaveAnimalPictures(AnimalType animalType, int numberOfPictures, int width, int height) {
        List<Animal> animals = new ArrayList<>();
        IntStream.rangeClosed(1, numberOfPictures)
                .forEach(value -> animals.add(fetchAnimalPictureFromExternalApi(animalType, width, height)));
        animalRepository.saveAll(animals);
        log.info(numberOfPictures + " Animals created");
    }

    public byte[] getLatestPictureAsByte() {
        return animalRepository.findFirstByOrderByCreationDateDesc()
                .orElseThrow(NoAnimalFoundException::new)
                .getPictureData();

    }

    public InputStreamResource getLatestPictureAsImage() {
        return new InputStreamResource(new ByteArrayInputStream(
                animalRepository.findFirstByOrderByCreationDateDesc()
                        .orElseThrow(NoAnimalFoundException::new)
                        .getPictureData()));
    }

    private Animal fetchAnimalPictureFromExternalApi(AnimalType animalType, int width, int height) {
        ResponseEntity<byte[]> response = this.restClient.get().uri(getUriOfExternalApi(animalType), width, height).retrieve().toEntity(byte[].class);

        if (HttpStatus.OK != response.getStatusCode()) {
            throw new ExternalApiErrorException();
        }

        return Animal.builder().pictureData(response.getBody())
                .creationDate(Calendar.getInstance().getTime())
                .animalType(animalType)
                .build();
    }

    private String getUriOfExternalApi(AnimalType animalType) {
        return switch (animalType) {
            case BEAR -> "https://placebear.com/{width}/{height}";
            case CAT -> "https://placekitten.com/{width}/{height}";
            case DOG -> "https://place.dog/{width}/{height}";
        };
    }

}