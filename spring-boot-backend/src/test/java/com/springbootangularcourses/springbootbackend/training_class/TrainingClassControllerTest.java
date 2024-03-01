package com.springbootangularcourses.springbootbackend.training_class;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootangularcourses.springbootbackend.domain.TrainingClass;
import com.springbootangularcourses.springbootbackend.resource.TrainingClassController;
import com.springbootangularcourses.springbootbackend.service.TrainingClassService;
import com.springbootangularcourses.springbootbackend.system.exceptions.CustomResponseEntityExceptionHandler;
import com.springbootangularcourses.springbootbackend.system.exceptions.ObjectNotFoundException;
import com.springbootangularcourses.springbootbackend.utils.converter.TrainingClassToReturnTrainingClassConverter;
import com.springbootangularcourses.springbootbackend.utils.converter.TrainingClassesToReturnTrainingClassesConverter;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnTrainingClass;
import com.springbootangularcourses.springbootbackend.domain.dto.TrainingClassDTO;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = TrainingClassController.class)
@Import(CustomResponseEntityExceptionHandler.class)
class TrainingClassControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TrainingClassService trainingClassService;

    @MockBean
    TrainingClassToReturnTrainingClassConverter trainingClassToReturnTrainingClass;

    @MockBean
    TrainingClassesToReturnTrainingClassesConverter trainingClassesToReturnTrainingClassesConverter;

    @MockBean
    Principal mockPrincipal;

    @Autowired
    ObjectMapper objectMapper;

    String baseUrl = "/api/trainingClasses";

    ModelMapper modelMapper;

    List<TrainingClass> trainingClasses;
    List<ReturnTrainingClass> returnTrainingClasses;

    ReturnTrainingClass tcr1;
    ReturnTrainingClass tcr2;
    TrainingClass tc1;
    TrainingClass tc2;

    @BeforeEach
    void setUp() {
        this.tc1 = new TrainingClass();
        this.tc1.setId(1L);
        this.tc1.setTitle("Beginner English Class");
        this.tc1.setTime("11:00");
        this.tc1.setDescription("Class for beginners");
        this.tc1.setCategory("English");
        this.tc1.setDayOfWeek(3);
        this.tc1.setCity("Warsaw");
        this.tc1.setAddress("271 Street");
        this.tc1.setPostalCode("V1 712L");
        this.tc1.setProvince("MB");
        this.tc1.setCountry("Poland");
        this.tc1.setTotalSpots(20);

        this.tc2 = new TrainingClass();
        this.tc2.setId(2L);
        this.tc2.setTitle("Beginner French Class");
        this.tc2.setTime("14:00");
        this.tc2.setDescription("Class for advanced");
        this.tc2.setCategory("French");
        this.tc2.setDayOfWeek(5);
        this.tc2.setCity("Warsaw");
        this.tc2.setAddress("275 Street");
        this.tc2.setPostalCode("V1 722L");
        this.tc2.setProvince("MB");
        this.tc2.setCountry("Poland");
        this.tc2.setTotalSpots(15);

        modelMapper = new ModelMapper();
        this.trainingClasses = new ArrayList<>();
        this.returnTrainingClasses = new ArrayList<>();
        this.trainingClasses.add(tc1);
        this.trainingClasses.add(tc2);
        tcr1 = modelMapper.map(tc1, ReturnTrainingClass.class);
        tcr2 = modelMapper.map(tc2, ReturnTrainingClass.class);
        this.returnTrainingClasses.add(tcr1);
        this.returnTrainingClasses.add(tcr2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGetTrainingClassesSuccess() throws Exception {
        // Given
        given(this.trainingClassService.getAllTrainingClasses()).willReturn(this.trainingClasses);
        given(this.trainingClassesToReturnTrainingClassesConverter.convert(this.trainingClasses))
                .willReturn(this.returnTrainingClasses);

        // When and then
        this.mockMvc.perform(get(this.baseUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Training classes retrieved"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(this.trainingClasses.size())))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].title").value("Beginner English Class"))
                .andExpect(jsonPath("$.data[1].id").value(2L))
                .andExpect(jsonPath("$.data[1].title").value("Beginner French Class"));
    }

    @Test
    void testGetTrainingClassSuccess() throws Exception {

        // Given
        given(this.trainingClassService.getTrainingClass(1L)).willReturn(this.trainingClasses.get(0));
        given(this.trainingClassToReturnTrainingClass.convert(this.trainingClasses.get(0))).willReturn(tcr1);

        // When and then
        this.mockMvc.perform(get(this.baseUrl + "/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Training class with id '1' retrieved"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.title").value("Beginner English Class"));
    }

    @Test
    void testGetTrainingClassNotFound() throws Exception {
        // Given
        given(this.trainingClassService.getTrainingClass(1L))
                .willThrow(new ObjectNotFoundException("training class", 1L));

        // When and then
        this.mockMvc.perform(get(this.baseUrl + "/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Could not find training class with Id 1"));
    }

    @Test
    void testAddTrainingClassSuccess() throws Exception {
        // Given
        TrainingClassDTO tcDTO = modelMapper.map(tc1, TrainingClassDTO.class);

        String json = this.objectMapper.writeValueAsString(tcDTO);

        given(mockPrincipal.getName()).willReturn("me");

        given(this.trainingClassService
                .saveTrainingClass(Mockito.any(TrainingClassDTO.class), Mockito.any(String.class)))
                .willReturn(tc1);

        given(this.trainingClassToReturnTrainingClass.convert(tc1)).willReturn(tcr1);

        // When and then
        this.mockMvc.perform(post(this.baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.message").value("Training class created"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.title").value(tcDTO.getTitle()))
                .andExpect(jsonPath("$.data.description").value(tcDTO.getDescription()))
                .andExpect(jsonPath("$.data.category").value(tcDTO.getCategory()));
    }

    @Test
    void testEditTrainingClassSuccess() throws Exception {
        // Given
        TrainingClassDTO tcDTO = new TrainingClassDTO();
        tcDTO.setTitle("Beginner English Class Updated");
        tcDTO.setTime("15:00");
        tcDTO.setDescription("Class for beginners");
        tcDTO.setCategory("English");
        tcDTO.setDayOfWeek(3);
        tcDTO.setCity("Warsaw");
        tcDTO.setAddress("271 Street");
        tcDTO.setPostalCode("V1 712L");
        tcDTO.setProvince("MB");
        tcDTO.setCountry("Poland");
        tcDTO.setTotalSpots(20);

        TrainingClass update = modelMapper.map(tcDTO, TrainingClass.class);
        update.setId(1L);

        ReturnTrainingClass rtc1 = modelMapper.map(update, ReturnTrainingClass.class);

        String json = this.objectMapper.writeValueAsString(tcDTO);

        given(this.trainingClassService.editTrainingClass(Mockito.any(TrainingClassDTO.class), eq(1L))).willReturn(update);
        given(this.trainingClassToReturnTrainingClass.convert(Mockito.any(TrainingClass.class)))
                .willReturn(rtc1);

        // When and then
        this.mockMvc.perform(put(this.baseUrl + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Training class updated"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.title").value(tcDTO.getTitle()))
                .andExpect(jsonPath("$.data.description").value(tcDTO.getDescription()))
                .andExpect(jsonPath("$.data.category").value(tcDTO.getCategory()));
    }

    @Test
    void testEditTrainingClassErrorWithNonExistentId() throws Exception {
        // Given
        TrainingClassDTO tcDTO = new TrainingClassDTO();
        tcDTO.setTitle("Beginner English Class Updated");
        tcDTO.setTime("15:00");
        tcDTO.setDescription("Class for beginners");
        tcDTO.setCategory("English");
        tcDTO.setDayOfWeek(3);
        tcDTO.setCity("Warsaw");
        tcDTO.setAddress("271 Street");
        tcDTO.setPostalCode("V1 712L");
        tcDTO.setProvince("MB");
        tcDTO.setCountry("Poland");
        tcDTO.setTotalSpots(20);

        String json = this.objectMapper.writeValueAsString(tcDTO);

        given(this.trainingClassService.editTrainingClass(Mockito.any(TrainingClassDTO.class), eq(1L)))
                .willThrow(new ObjectNotFoundException("training class", 1L));

        // When and then
        this.mockMvc.perform(put(this.baseUrl + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Could not find training class with Id 1"));
    }

    @Test
    void testDeleteTrainingClassSuccess() throws Exception {
        // Given
        doNothing().when(this.trainingClassService).deleteTrainingClass(1L);

        // When and then
        this.mockMvc.perform(delete(this.baseUrl + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Training class deleted"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void testDeleteTrainingClassErrorWithNonExistentId() throws Exception {
        // Given
        doThrow(new ObjectNotFoundException("training class", 1L))
                .when(this.trainingClassService).deleteTrainingClass(1L);

        // When and then
        this.mockMvc.perform(delete(this.baseUrl + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Could not find training class with Id 1"));
    }

    @Test
    void testAttendClassSuccess() throws Exception {
        // Given
        given(mockPrincipal.getName()).willReturn("me");

        doNothing().when(this.trainingClassService).attendClass(eq(1L), Mockito.any(String.class));

        // When and then
        this.mockMvc.perform(post(this.baseUrl + "/1/attend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Training class attended"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void testAttendClassErrorWithNonExistentId() throws Exception {
        // Given
        given(mockPrincipal.getName()).willReturn("me");

        doThrow(new ObjectNotFoundException("training class", 1L))
                .when(this.trainingClassService).attendClass(eq(1L), Mockito.any(String.class));

        // When and then
        this.mockMvc.perform(post(this.baseUrl + "/1/attend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Could not find training class with Id 1"));
    }

    @Test
    void testAbandonClassSuccess() throws Exception {
        // Given
        given(mockPrincipal.getName()).willReturn("me");

        doNothing().when(this.trainingClassService)
                .abandonClass(eq(1L), Mockito.any(String.class));

        // When and then
        this.mockMvc.perform(delete(this.baseUrl + "/1/abandon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Training class abandoned"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void testAbandonClassErrorWithNonExistentId() throws Exception {
        // Given
        given(mockPrincipal.getName()).willReturn("me");

        doThrow(new ObjectNotFoundException("training class", 1L))
                .when(this.trainingClassService)
                .abandonClass(eq(1L), Mockito.any(String.class));

        // When and then
        this.mockMvc.perform(delete(this.baseUrl + "/1/abandon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Could not find training class with Id 1"));
    }
}