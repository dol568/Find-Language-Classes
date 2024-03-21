package com.springbootangularcourses.springbootbackend.language_class;

import com.springbootangularcourses.springbootbackend.domain.LanguageClass;
import com.springbootangularcourses.springbootbackend.domain.UserLanguageClass;
import com.springbootangularcourses.springbootbackend.repository.LanguageClassRepository;
import com.springbootangularcourses.springbootbackend.service.LanguageClassServiceImpl;
import com.springbootangularcourses.springbootbackend.system.exceptions.ObjectNotFoundException;
import com.springbootangularcourses.springbootbackend.system.exceptions.UserNotFoundException;
import com.springbootangularcourses.springbootbackend.utils.converter.LanguageClassDTOToLanguageClassConverter;
import com.springbootangularcourses.springbootbackend.domain.dto.LanguageClassDTO;
import com.springbootangularcourses.springbootbackend.domain.User;
import com.springbootangularcourses.springbootbackend.repository.UserRepository;
import com.springbootangularcourses.springbootbackend.service.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LanguageClassServiceTest {

    @Mock
    LanguageClassRepository LanguageClassRepository;

    @Mock
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    LanguageClassDTOToLanguageClassConverter LanguageClassDTOToLanguageClassConverter;

    @InjectMocks
    LanguageClassServiceImpl LanguageClassService;

    List<LanguageClass> languageClasses;
    User user;
    LanguageClass tc1;
    LanguageClass tc2;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("1");
        user.setUserName("joetalbot568");
        user.setEmail("joe@gmail.com");
        user.setFullName("Joe Talbot");
        user.setPassword("!Jjoetalbot8");
        user.setBio("im joe");

        tc1 = new LanguageClass();
        tc1.setId(1L);
        tc1.setTitle("Beginner English Class");
        tc1.setTime("11:00");
        tc1.setDescription("Class for beginners");
        tc1.setCategory("English");
        tc1.setDayOfWeek(3);
        tc1.setCity("Warsaw");
        tc1.setAddress("271 Street");
        tc1.setPostalCode("V1 712L");
        tc1.setProvince("MB");
        tc1.setCountry("Poland");
        tc1.setTotalSpots(20);

        tc2 = new LanguageClass();
        tc2.setId(2L);
        tc2.setTitle("Beginner French Class");
        tc2.setTime("14:00");
        tc2.setDescription("Class for advanced");
        tc2.setCategory("French");
        tc2.setDayOfWeek(5);
        tc2.setCity("Warsaw");
        tc2.setAddress("275 Street");
        tc2.setPostalCode("V1 722L");
        tc2.setProvince("MB");
        tc2.setCountry("Poland");
        tc2.setTotalSpots(15);

        this.languageClasses = new ArrayList<>();
        this.languageClasses.add(tc1);
        this.languageClasses.add(tc2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllLanguageClassesSuccess() {
        // Given
        given(this.LanguageClassRepository.findByOrderByTimeAsc())
                .willReturn(this.languageClasses);

        // When
        List<LanguageClass> actualLanguageClasses = this.LanguageClassService.getAllLanguageClasses();

        //Then
        assertThat(actualLanguageClasses.size()).isEqualTo(this.languageClasses.size());
        verify(this.LanguageClassRepository, times(1)).findByOrderByTimeAsc();
    }

    @Test
    void getLanguageClassSuccess() {
        // Given
        LanguageClass tc = new LanguageClass();
        tc.setId(3L);
        tc.setTitle("Beginner English Class");
        tc.setTime("11:00");
        tc.setDescription("Class for beginners");
        tc.setCategory("English");
        tc.setDayOfWeek(3);
        tc.setCity("Warsaw");
        tc.setAddress("271 Street");
        tc.setPostalCode("V1 712L");
        tc.setProvince("MB");
        tc.setCountry("Poland");
        tc.setTotalSpots(20);

        given(this.LanguageClassRepository.findById(3L))
                .willReturn(Optional.of(tc));

        // When
        LanguageClass returnedLanguageClass = this.LanguageClassService.getLanguageClass(3L);

        // Then
        assertThat(returnedLanguageClass.getId()).isEqualTo(tc.getId());
        assertThat(returnedLanguageClass.getTitle()).isEqualTo(tc.getTitle());
        assertThat(returnedLanguageClass.getTime()).isEqualTo(tc.getTime());
        assertThat(returnedLanguageClass.getDescription()).isEqualTo(tc.getDescription());
        assertThat(returnedLanguageClass.getCategory()).isEqualTo(tc.getCategory());
        assertThat(returnedLanguageClass.getDayOfWeek()).isEqualTo(tc.getDayOfWeek());
        assertThat(returnedLanguageClass.getCity()).isEqualTo(tc.getCity());
        assertThat(returnedLanguageClass.getAddress()).isEqualTo(tc.getAddress());
        assertThat(returnedLanguageClass.getPostalCode()).isEqualTo(tc.getPostalCode());
        assertThat(returnedLanguageClass.getAddress()).isEqualTo(tc.getAddress());
        assertThat(returnedLanguageClass.getProvince()).isEqualTo(tc.getProvince());
        assertThat(returnedLanguageClass.getCountry()).isEqualTo(tc.getCountry());
        assertThat(returnedLanguageClass.getTotalSpots()).isEqualTo(tc.getTotalSpots());

        verify(this.LanguageClassRepository, times(1)).findById(3L);
    }

    @Test
    void getLanguageClassNotFound() {
        // Given
        given(this.LanguageClassRepository.findById(Mockito.any(Long.class))).willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> this.LanguageClassService.getLanguageClass(3L));

        // Then
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find Language class with Id 3");
        verify(this.LanguageClassRepository, times(1)).findById(3L);
    }

    @Test
    void saveLanguageClass() {
        // Given
        LanguageClassDTO tcDTO = new LanguageClassDTO();
        tcDTO.setTitle("Beginner English Class");
        tcDTO.setTime("11:00");
        tcDTO.setDescription("Class for beginners");
        tcDTO.setCategory("English");
        tcDTO.setDayOfWeek(3);
        tcDTO.setCity("Warsaw");
        tcDTO.setAddress("271 Street");
        tcDTO.setPostalCode("V1 712L");
        tcDTO.setProvince("MB");
        tcDTO.setCountry("Poland");
        tcDTO.setTotalSpots(20);

        given(this.userService.findByEmail("joe@gmail.com")).willReturn(user);
        given(this.LanguageClassDTOToLanguageClassConverter.convert(tcDTO)).willReturn(tc1);
        given(this.LanguageClassRepository.save(tc1)).willReturn(tc1);
        given(this.LanguageClassRepository.findById(tc1.getId())).willReturn(Optional.of(tc1));

        // When
        LanguageClass savedLanguageClass = this.LanguageClassService.saveLanguageClass(tcDTO, user.getEmail());

        // Then
        assertThat(savedLanguageClass.getUserLanguageClasses().size()).isEqualTo(1);
        assertThat(savedLanguageClass.getTitle()).isEqualTo(tcDTO.getTitle());
        verify(this.userService, times(1)).findByEmail("joe@gmail.com");
        verify(this.LanguageClassDTOToLanguageClassConverter, times(1)).convert(tcDTO);
        verify(this.LanguageClassRepository, times(1)).save(tc1);
        verify(this.LanguageClassRepository, times(1)).findById(tc1.getId());
    }

    @Test
    void editLanguageClassSuccess() {
        // Given
        LanguageClassDTO update = new LanguageClassDTO();
        update.setTitle("Updated title");
        update.setTime("18:00");
        update.setDescription("Updated description");
        update.setCategory("Spanish");
        update.setDayOfWeek(5);
        update.setCity("Warsaw");
        update.setAddress("275 Street");
        update.setPostalCode("V1 722L");
        update.setProvince("MB");
        update.setCountry("Poland");
        update.setTotalSpots(17);

        given(this.LanguageClassRepository.findById(1L)).willReturn(Optional.of(tc1));
        given(this.LanguageClassRepository.save(tc1)).willReturn(tc1);

        // When
        LanguageClass updatedLanguageClass = this.LanguageClassService.editLanguageClass(update, 1L);

        // Then
        assertThat(updatedLanguageClass.getId()).isEqualTo(1L);
        assertThat(updatedLanguageClass.getTitle()).isEqualTo("Updated title");
        assertThat(updatedLanguageClass.getDescription()).isEqualTo("Updated description");
        verify(this.LanguageClassRepository, times(1)).save(tc1);
        verify(this.LanguageClassRepository, times(2)).findById(1L);
    }

    @Test
    void editLanguageClassNotFound() {
        // Given
        LanguageClassDTO update = new LanguageClassDTO();
        update.setTitle("Beginner Spanish Class");
        update.setTime("18:00");
        update.setDescription("Class...");
        update.setCategory("Spanish");
        update.setDayOfWeek(5);
        update.setCity("Warsaw");
        update.setAddress("275 Street");
        update.setPostalCode("V1 722L");
        update.setProvince("MB");
        update.setCountry("Poland");
        update.setTotalSpots(17);

        given(this.LanguageClassRepository.findById(1L)).willReturn(Optional.empty());

        // When
        assertThrows(ObjectNotFoundException.class, () -> {
            this.LanguageClassService.editLanguageClass(update, 1L);
        });

        // Then
        verify(this.LanguageClassRepository, times(1)).findById(1L);
    }

    @Test
    void deleteLanguageClassSuccess() {
        // Given
        given(this.LanguageClassRepository.findById(2L)).willReturn(Optional.of(tc2));
        doNothing().when(this.LanguageClassRepository).delete(tc2);

        // When
        this.LanguageClassService.deleteLanguageClass(2L);

        // Then
        verify(this.LanguageClassRepository, times(1)).delete(tc2);
    }

    @Test
    void deleteLanguageClassNotFound() {
        // Given
        given(this.LanguageClassRepository.findById(2L)).willReturn(Optional.empty());

        // When
        assertThrows(ObjectNotFoundException.class, () -> {
            this.LanguageClassService.deleteLanguageClass(2L);
        });

        // Then
        verify(this.LanguageClassRepository, times(1)).findById(2L);
    }

    @Test
    void attendClassSuccess() {
        // Given
        given(this.userService.findByEmail("joe@gmail.com")).willReturn(user);
        given(this.LanguageClassRepository.findById(2L))
                .willReturn(Optional.of(tc2));

        // When
        this.LanguageClassService.attendClass(2L, "joe@gmail.com");

        // Then
        assertThat(tc2.getUserLanguageClasses().size()).isEqualTo(1);
        verify(this.userService, times(1)).findByEmail("joe@gmail.com");
        verify(this.LanguageClassRepository, times(1)).findById(tc2.getId());
        verify(this.LanguageClassRepository, times(1)).save(tc2);
        verify(this.userRepository, times(1)).save(user);
    }

    @Test
    void attendClassNotFound() {
        // Given
        given(this.userService.findByEmail("joe@gmail.com")).willReturn(user);
        given(this.LanguageClassRepository.findById(2L))
                .willReturn(Optional.empty());

        // When
        assertThrows(ObjectNotFoundException.class, () -> {
            this.LanguageClassService.attendClass(2L, "joe@gmail.com");
        });

        // Then
        verify(this.userService, times(1)).findByEmail("joe@gmail.com");
        verify(this.LanguageClassRepository, times(1)).findById(tc2.getId());
    }

    @Test
    void attendClassAlreadyAttended() {
        // Given
        UserLanguageClass userLanguageClass = new UserLanguageClass();
        userLanguageClass.setUser(user);
        userLanguageClass.setHost(false);

        tc2.addUserLanguageClass(userLanguageClass);

        given(this.userService.findByEmail("joe@gmail.com")).willReturn(user);
        given(this.LanguageClassRepository.findById(2L))
                .willReturn(Optional.of(tc2));

        // When
        assertThrows(UserNotFoundException.class, () -> {
            this.LanguageClassService.attendClass(2L, "joe@gmail.com");
        });

        // Then
        assertThat(tc2.getUserLanguageClasses().size()).isEqualTo(1);
        verify(this.userService, times(1)).findByEmail("joe@gmail.com");
        verify(this.LanguageClassRepository, times(1)).findById(tc2.getId());
    }

    @Test
    void abandonClassSuccess() {
        // Given

        given(this.userService.findByEmail("joe@gmail.com")).willReturn(user);
        given(this.LanguageClassRepository.findById(2L))
                .willReturn(Optional.of(tc2));

        UserLanguageClass userLanguageClass = new UserLanguageClass();
        userLanguageClass.setUser(user);
        userLanguageClass.setHost(false);

        tc2.addUserLanguageClass(userLanguageClass);
        // When
        this.LanguageClassService.abandonClass(2L, "joe@gmail.com");

        // Then
        assertThat(tc2.getUserLanguageClasses().size()).isEqualTo(0);
        verify(this.userService, times(1)).findByEmail("joe@gmail.com");
        verify(this.LanguageClassRepository, times(1)).findById(tc2.getId());
        verify(this.LanguageClassRepository, times(1)).save(tc2);
        verify(this.userRepository, times(1)).save(user);
    }

    @Test
    void abandonClassNotFound() {
        // Given
        given(this.userService.findByEmail("joe@gmail.com")).willReturn(user);
        given(this.LanguageClassRepository.findById(2L))
                .willReturn(Optional.empty());

        // When
        assertThrows(ObjectNotFoundException.class, () -> {
            this.LanguageClassService.abandonClass(2L, "joe@gmail.com");
        });

        // Then
        verify(this.userService, times(1)).findByEmail("joe@gmail.com");
        verify(this.LanguageClassRepository, times(1)).findById(tc2.getId());
    }

    @Test
    void abandonClassNotAttended() {
        // Given
        given(this.userService.findByEmail("joe@gmail.com")).willReturn(user);
        given(this.LanguageClassRepository.findById(2L))
                .willReturn(Optional.of(tc2));

        // When
        assertThrows(UserNotFoundException.class, () -> {
            this.LanguageClassService.abandonClass(2L, "joe@gmail.com");
        });

        // Then
        assertThat(tc2.getUserLanguageClasses().size()).isEqualTo(0);
        verify(this.userService, times(1)).findByEmail("joe@gmail.com");
        verify(this.LanguageClassRepository, times(1)).findById(tc2.getId());
    }

    @Test
    void abandonClassHosted() {
        // Given
        UserLanguageClass userLanguageClass = new UserLanguageClass();
        userLanguageClass.setUser(user);
        userLanguageClass.setHost(true);

        tc2.addUserLanguageClass(userLanguageClass);

        given(this.userService.findByEmail("joe@gmail.com")).willReturn(user);
        given(this.LanguageClassRepository.findById(2L))
                .willReturn(Optional.of(tc2));

        // When
        assertThrows(UserNotFoundException.class, () -> {
            this.LanguageClassService.abandonClass(2L, "joe@gmail.com");
        });

        // Then
        assertThat(tc2.getUserLanguageClasses().size()).isEqualTo(1);
        verify(this.userService, times(1)).findByEmail("joe@gmail.com");
        verify(this.LanguageClassRepository, times(1)).findById(tc2.getId());
    }
}