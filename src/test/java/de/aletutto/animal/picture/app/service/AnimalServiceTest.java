package de.aletutto.animal.picture.app.service;

import de.aletutto.animal.picture.app.persistence.Animal;
import de.aletutto.animal.picture.app.persistence.AnimalRepository;
import de.aletutto.animal.picture.app.persistence.AnimalType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.InputStreamResource;

import java.io.IOException;
import java.util.Calendar;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
class AnimalServiceTest {
    @Autowired
    AnimalService animalService;

    @MockBean
    static AnimalRepository mockAnimalRepository;

    @Autowired
    AnimalRepository animalRepository;

    @Test
    void givenAnimalPictureData_whenGetLatestPictureAsByte_thenPictureIsFetched() {
        Animal animal = Animal.builder().pictureData(new byte[1])
                .creationDate(Calendar.getInstance().getTime())
                .animalType(AnimalType.BEAR)
                .build();

        when(mockAnimalRepository.findFirstByOrderByCreationDateDesc()).thenReturn(Optional.ofNullable(animal));

        byte[] pictureData = animalService.getLatestPictureAsByte();

        Assertions.assertThat(animal.getPictureData()).isEqualTo(pictureData);
    }

    @Test
    void givenNoAnimal_whenGetLatestPictureAsByte_thenNoAnimalFoundExceptionIsThrown() {
        Assertions.assertThatThrownBy(() -> {
            animalService.getLatestPictureAsByte();
        }).isInstanceOf(NoAnimalFoundException.class);
    }

    @Test
    void givenAnimalPictureData_whenGetLatestPictureAsImage_thenPictureIsFetched() throws IOException {
        Animal animal = Animal.builder().pictureData(new byte[1])
                .creationDate(Calendar.getInstance().getTime())
                .animalType(AnimalType.BEAR)
                .build();

        when(mockAnimalRepository.findFirstByOrderByCreationDateDesc()).thenReturn(Optional.ofNullable(animal));

        InputStreamResource picture = animalService.getLatestPictureAsImage();

        Assertions.assertThat(animal.getPictureData()).isEqualTo(picture.getInputStream().readAllBytes());
    }

    @Test
    void givenNoAnimal_whenGetLatestPictureAsImage_thenNoAnimalFoundExceptionIsThrown() {
        Assertions.assertThatThrownBy(() -> {
            animalService.getLatestPictureAsImage();
        }).isInstanceOf(NoAnimalFoundException.class);
    }

}
