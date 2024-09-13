package de.aletutto.animal.picture.app.controller;

import de.aletutto.animal.picture.app.persistence.AnimalType;
import de.aletutto.animal.picture.app.service.AnimalService;
import de.aletutto.animal.picture.app.service.ExternalApiErrorException;
import de.aletutto.animal.picture.app.service.NoAnimalFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class AnimalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnimalService animalService;

    private final String ANIMALS_PATH = "/animals";
    private final String ANIMALS_LATEST_PATH = "/animals/latest";
    private final String ANIMAL_TYPE_PARAM = "animalType";
    private final String NUMBER_OF_PICTURES_PARAM = "numberOfPictures";
    private final String NUMBER_OF_PICTURES = "1";

    @Test
    void givenAnimalTypeAndNumberOfPictures_whenPostAnimals_thenReturnNoContent() throws Exception {
        mockMvc.perform(post(ANIMALS_PATH)
                        .param(ANIMAL_TYPE_PARAM, AnimalType.CAT.name())
                        .param(NUMBER_OF_PICTURES_PARAM, NUMBER_OF_PICTURES))
                .andExpect(status().isNoContent());
    }

    @Test
    void givenNoAnimalTypeAndNumberOfPictures_whenPostAnimals_thenReturnBadRequest() throws Exception {
        mockMvc.perform(post(ANIMALS_PATH))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenExternalApiErrorException_whenPostAnimals_thenReturnInternalServerError() throws Exception {
        doThrow(ExternalApiErrorException.class).when(animalService).fetchAndSaveAnimalPictures(any(AnimalType.class), anyInt(), anyInt(), anyInt());
        mockMvc.perform(post(ANIMALS_PATH)
                        .param(ANIMAL_TYPE_PARAM, AnimalType.CAT.name())
                        .param(NUMBER_OF_PICTURES_PARAM, NUMBER_OF_PICTURES))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void givenExistingPicture_whenGetAnimalsLatestByOctetStream_ThenReturnOk() throws Exception {
        when(animalService.getLatestPictureAsByte()).thenReturn(new byte[1]);

        mockMvc.perform(get(ANIMALS_LATEST_PATH)
                        .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void givenNoAnimalFoundException_whenGetAnimalsLatestByOctetStream_ThenReturnNotFound() throws Exception {
        when(animalService.getLatestPictureAsByte()).thenThrow(NoAnimalFoundException.class);

        mockMvc.perform(get(ANIMALS_LATEST_PATH)
                        .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenExistingPicture_whenGetAnimalsLatestByJpeg_ThenReturnOk() throws Exception {
        when(animalService.getLatestPictureAsImage()).thenReturn(new InputStreamResource(ByteArrayInputStream.nullInputStream()));

        mockMvc.perform(get(ANIMALS_LATEST_PATH)
                        .accept(MediaType.IMAGE_JPEG_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    void givenNoAnimalFoundException_whenGetAnimalsLatestByJpeg_ThenReturnNotFound() throws Exception {
        when(animalService.getLatestPictureAsImage()).thenThrow(NoAnimalFoundException.class);

        mockMvc.perform(get(ANIMALS_LATEST_PATH)
                        .accept(MediaType.IMAGE_JPEG_VALUE))
                .andExpect(status().isNotFound());
    }

}
