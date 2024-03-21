package com.springbootangularcourses.springbootbackend.language_class;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootangularcourses.springbootbackend.domain.LanguageClass;
import com.springbootangularcourses.springbootbackend.resource.LanguageClassController;
import com.springbootangularcourses.springbootbackend.service.LanguageClassService;
import com.springbootangularcourses.springbootbackend.system.exceptions.CustomResponseEntityExceptionHandler;
import com.springbootangularcourses.springbootbackend.system.exceptions.ObjectNotFoundException;
import com.springbootangularcourses.springbootbackend.utils.converter.LanguageClassToReturnLanguageClassConverter;
import com.springbootangularcourses.springbootbackend.utils.converter.LanguageClassesToReturnLanguageClassesConverter;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnLanguageClass;
import com.springbootangularcourses.springbootbackend.domain.dto.LanguageClassDTO;
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
@ContextConfiguration(classes = LanguageClassController.class)
@Import(CustomResponseEntityExceptionHandler.class)
class LanguageClassControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    LanguageClassService languageClassService;

    @MockBean
    LanguageClassToReturnLanguageClassConverter LanguageClassToReturnLanguageClass;

    @MockBean
    LanguageClassesToReturnLanguageClassesConverter LanguageClassesToReturnLanguageClassesConverter;

    @MockBean
    Principal mockPrincipal;

    @Autowired
    ObjectMapper objectMapper;

    String baseUrl = "/api/languageClasses";

    ModelMapper modelMapper;

    List<LanguageClass> languageClasses;
    List<ReturnLanguageClass> returnLanguageClasses;

    ReturnLanguageClass tcr1;
    ReturnLanguageClass tcr2;
    LanguageClass tc1;
    LanguageClass tc2;

    @BeforeEach
    void setUp() {
        this.tc1 = new LanguageClass();
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

        this.tc2 = new LanguageClass();
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
        this.languageClasses = new ArrayList<>();
        this.returnLanguageClasses = new ArrayList<>();
        this.languageClasses.add(tc1);
        this.languageClasses.add(tc2);
        tcr1 = modelMapper.map(tc1, ReturnLanguageClass.class);
        tcr2 = modelMapper.map(tc2, ReturnLanguageClass.class);
        this.returnLanguageClasses.add(tcr1);
        this.returnLanguageClasses.add(tcr2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGetLanguageClassesSuccess() throws Exception {
        // Given
        given(this.languageClassService.getAllLanguageClasses()).willReturn(this.languageClasses);
        given(this.LanguageClassesToReturnLanguageClassesConverter.convert(this.languageClasses))
                .willReturn(this.returnLanguageClasses);

        // When and then
        this.mockMvc.perform(get(this.baseUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Language classes retrieved"))
                .andExpect(jsonPath("$.data", Matchers.hasSize(this.languageClasses.size())))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].title").value("Beginner English Class"))
                .andExpect(jsonPath("$.data[1].id").value(2L))
                .andExpect(jsonPath("$.data[1].title").value("Beginner French Class"));
    }

    @Test
    void testGetLanguageClassSuccess() throws Exception {

        // Given
        given(this.languageClassService.getLanguageClass(1L)).willReturn(this.languageClasses.get(0));
        given(this.LanguageClassToReturnLanguageClass.convert(this.languageClasses.get(0))).willReturn(tcr1);

        // When and then
        this.mockMvc.perform(get(this.baseUrl + "/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Language class with id '1' retrieved"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.title").value("Beginner English Class"));
    }

    @Test
    void testGetLanguageClassNotFound() throws Exception {
        // Given
        given(this.languageClassService.getLanguageClass(1L))
                .willThrow(new ObjectNotFoundException("Language class", 1L));

        // When and then
        this.mockMvc.perform(get(this.baseUrl + "/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Could not find Language class with Id 1"));
    }

    @Test
    void testAddLanguageClassSuccess() throws Exception {
        // Given
        LanguageClassDTO tcDTO = modelMapper.map(tc1, LanguageClassDTO.class);

        String json = this.objectMapper.writeValueAsString(tcDTO);

        given(mockPrincipal.getName()).willReturn("me");

        given(this.languageClassService
                .saveLanguageClass(Mockito.any(LanguageClassDTO.class), Mockito.any(String.class)))
                .willReturn(tc1);

        given(this.LanguageClassToReturnLanguageClass.convert(tc1)).willReturn(tcr1);

        // When and then
        this.mockMvc.perform(post(this.baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.message").value("Language class created"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.title").value(tcDTO.getTitle()))
                .andExpect(jsonPath("$.data.description").value(tcDTO.getDescription()))
                .andExpect(jsonPath("$.data.category").value(tcDTO.getCategory()));
    }

    @Test
    void testEditLanguageClassSuccess() throws Exception {
        // Given
        LanguageClassDTO tcDTO = new LanguageClassDTO();
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

        LanguageClass update = modelMapper.map(tcDTO, LanguageClass.class);
        update.setId(1L);

        ReturnLanguageClass rtc1 = modelMapper.map(update, ReturnLanguageClass.class);

        String json = this.objectMapper.writeValueAsString(tcDTO);

        given(this.languageClassService.editLanguageClass(Mockito.any(LanguageClassDTO.class), eq(1L))).willReturn(update);
        given(this.LanguageClassToReturnLanguageClass.convert(Mockito.any(LanguageClass.class)))
                .willReturn(rtc1);

        // When and then
        this.mockMvc.perform(put(this.baseUrl + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Language class updated"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.title").value(tcDTO.getTitle()))
                .andExpect(jsonPath("$.data.description").value(tcDTO.getDescription()))
                .andExpect(jsonPath("$.data.category").value(tcDTO.getCategory()));
    }

    @Test
    void testEditLanguageClassErrorWithNonExistentId() throws Exception {
        // Given
        LanguageClassDTO tcDTO = new LanguageClassDTO();
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

        given(this.languageClassService.editLanguageClass(Mockito.any(LanguageClassDTO.class), eq(1L)))
                .willThrow(new ObjectNotFoundException("Language class", 1L));

        // When and then
        this.mockMvc.perform(put(this.baseUrl + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Could not find Language class with Id 1"));
    }

    @Test
    void testDeleteLanguageClassSuccess() throws Exception {
        // Given
        doNothing().when(this.languageClassService).deleteLanguageClass(1L);

        // When and then
        this.mockMvc.perform(delete(this.baseUrl + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Language class deleted"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void testDeleteLanguageClassErrorWithNonExistentId() throws Exception {
        // Given
        doThrow(new ObjectNotFoundException("Language class", 1L))
                .when(this.languageClassService).deleteLanguageClass(1L);

        // When and then
        this.mockMvc.perform(delete(this.baseUrl + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Could not find Language class with Id 1"));
    }

    @Test
    void testAttendClassSuccess() throws Exception {
        // Given
        given(mockPrincipal.getName()).willReturn("me");

        doNothing().when(this.languageClassService).attendClass(eq(1L), Mockito.any(String.class));

        // When and then
        this.mockMvc.perform(post(this.baseUrl + "/1/attend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Language class attended"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void testAttendClassErrorWithNonExistentId() throws Exception {
        // Given
        given(mockPrincipal.getName()).willReturn("me");

        doThrow(new ObjectNotFoundException("Language class", 1L))
                .when(this.languageClassService).attendClass(eq(1L), Mockito.any(String.class));

        // When and then
        this.mockMvc.perform(post(this.baseUrl + "/1/attend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Could not find Language class with Id 1"));
    }

    @Test
    void testAbandonClassSuccess() throws Exception {
        // Given
        given(mockPrincipal.getName()).willReturn("me");

        doNothing().when(this.languageClassService)
                .abandonClass(eq(1L), Mockito.any(String.class));

        // When and then
        this.mockMvc.perform(delete(this.baseUrl + "/1/abandon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Language class abandoned"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void testAbandonClassErrorWithNonExistentId() throws Exception {
        // Given
        given(mockPrincipal.getName()).willReturn("me");

        doThrow(new ObjectNotFoundException("Language class", 1L))
                .when(this.languageClassService)
                .abandonClass(eq(1L), Mockito.any(String.class));

        // When and then
        this.mockMvc.perform(delete(this.baseUrl + "/1/abandon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Could not find Language class with Id 1"));
    }
}