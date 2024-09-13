package de.aletutto.animal.picture.app.controller;

import de.aletutto.animal.picture.app.persistence.AnimalType;
import de.aletutto.animal.picture.app.service.AnimalService;
import de.aletutto.animal.picture.app.service.ExternalApiErrorException;
import de.aletutto.animal.picture.app.service.NoAnimalFoundException;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/animals")
@Validated
public class AnimalController {

    @Autowired
    AnimalService animalService;

    @PostMapping
    public ResponseEntity<Object> fetchAndSaveAnimalPictures(@RequestParam AnimalType animalType,
                                                             @RequestParam(defaultValue = "1")
                                                                @Min(value = 1, message = "Minimum 1 picture need to be fetched")
                                                                @Max(value = 10, message = "Maximum 10 pictures can be fetched at once") int numberOfPictures,
                                                             @RequestParam(defaultValue = "300", required = false) int width,
                                                             @RequestParam(defaultValue = "300", required = false) int height) {
        try {
            animalService.fetchAndSaveAnimalPictures(animalType, numberOfPictures, width, height);
        } catch (ExternalApiErrorException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not fetch data from external API", e);
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/latest", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<Object> getLatestAnimalPicture(@RequestHeader(name = HttpHeaders.ACCEPT, required = false) @Parameter(hidden = true) String acceptHeader) {
        try {
            if (MediaType.IMAGE_JPEG_VALUE.equals(acceptHeader)) {
                return ResponseEntity.ok().body(animalService.getLatestPictureAsImage());
            }
            return ResponseEntity.ok().body(animalService.getLatestPictureAsByte());
        } catch (NoAnimalFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No animal found", e);
        }
    }
}
