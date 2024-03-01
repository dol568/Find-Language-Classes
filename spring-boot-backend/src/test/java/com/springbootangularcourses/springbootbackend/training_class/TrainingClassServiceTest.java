package com.springbootangularcourses.springbootbackend.training_class;

import com.springbootangularcourses.springbootbackend.domain.TrainingClass;
import com.springbootangularcourses.springbootbackend.domain.UserTrainingClass;
import com.springbootangularcourses.springbootbackend.repository.TrainingClassRepository;
import com.springbootangularcourses.springbootbackend.service.TrainingClassServiceImpl;
import com.springbootangularcourses.springbootbackend.system.exceptions.ObjectNotFoundException;
import com.springbootangularcourses.springbootbackend.system.exceptions.UserNotFoundException;
import com.springbootangularcourses.springbootbackend.utils.converter.TrainingClassDTOToTrainingClassConverter;
import com.springbootangularcourses.springbootbackend.domain.dto.TrainingClassDTO;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingClassServiceTest {

    @Mock
    TrainingClassRepository trainingClassRepository;

    @Mock
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    TrainingClassDTOToTrainingClassConverter trainingClassDTOToTrainingClassConverter;

    @InjectMocks
    TrainingClassServiceImpl trainingClassService;

    List<TrainingClass> trainingClasses;
    User user;
    TrainingClass tc1;
    TrainingClass tc2;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("1");
        user.setUserName("joetalbot568");
        user.setEmail("joe@gmail.com");
        user.setFullName("Joe Talbot");
        user.setPassword("!Jjoetalbot8");
        user.setBio("im joe");

        tc1 = new TrainingClass();
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

        tc2 = new TrainingClass();
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

        this.trainingClasses = new ArrayList<>();
        this.trainingClasses.add(tc1);
        this.trainingClasses.add(tc2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllTrainingClassesSuccess() {
        // Given
        given(this.trainingClassRepository.findAll()).willReturn(this.trainingClasses);

        // When
        List<TrainingClass> actualTrainingClasses = this.trainingClassService.getAllTrainingClasses();

        //Then
        assertThat(actualTrainingClasses.size()).isEqualTo(this.trainingClasses.size());
        verify(this.trainingClassRepository, times(1)).findAll();
    }

    @Test
    void getTrainingClassSuccess() {
        // Given
        TrainingClass tc = new TrainingClass();
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

        given(this.trainingClassRepository.findById(3L))
                .willReturn(Optional.of(tc));

        // When
        TrainingClass returnedTrainingClass = this.trainingClassService.getTrainingClass(3L);

        // Then
        assertThat(returnedTrainingClass.getId()).isEqualTo(tc.getId());
        assertThat(returnedTrainingClass.getTitle()).isEqualTo(tc.getTitle());
        assertThat(returnedTrainingClass.getTime()).isEqualTo(tc.getTime());
        assertThat(returnedTrainingClass.getDescription()).isEqualTo(tc.getDescription());
        assertThat(returnedTrainingClass.getCategory()).isEqualTo(tc.getCategory());
        assertThat(returnedTrainingClass.getDayOfWeek()).isEqualTo(tc.getDayOfWeek());
        assertThat(returnedTrainingClass.getCity()).isEqualTo(tc.getCity());
        assertThat(returnedTrainingClass.getAddress()).isEqualTo(tc.getAddress());
        assertThat(returnedTrainingClass.getPostalCode()).isEqualTo(tc.getPostalCode());
        assertThat(returnedTrainingClass.getAddress()).isEqualTo(tc.getAddress());
        assertThat(returnedTrainingClass.getProvince()).isEqualTo(tc.getProvince());
        assertThat(returnedTrainingClass.getCountry()).isEqualTo(tc.getCountry());
        assertThat(returnedTrainingClass.getTotalSpots()).isEqualTo(tc.getTotalSpots());

        verify(this.trainingClassRepository, times(1)).findById(3L);
    }

    @Test
    void getTrainingClassNotFound() {
        // Given
        given(this.trainingClassRepository.findById(Mockito.any(Long.class))).willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> this.trainingClassService.getTrainingClass(3L));

        // Then
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find training class with Id 3");
        verify(this.trainingClassRepository, times(1)).findById(3L);
    }

    @Test
    void saveTrainingClass() {
        // Given
        TrainingClassDTO tcDTO = new TrainingClassDTO();
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
        given(this.trainingClassDTOToTrainingClassConverter.convert(tcDTO)).willReturn(tc1);
        given(this.trainingClassRepository.save(tc1)).willReturn(tc1);
        given(this.trainingClassRepository.findById(tc1.getId())).willReturn(Optional.of(tc1));

        // When
        TrainingClass savedTrainingClass = this.trainingClassService.saveTrainingClass(tcDTO, user.getEmail());

        // Then
        assertThat(savedTrainingClass.getUserTrainingClasses().size()).isEqualTo(1);
        assertThat(savedTrainingClass.getTitle()).isEqualTo(tcDTO.getTitle());
        verify(this.userService, times(1)).findByEmail("joe@gmail.com");
        verify(this.trainingClassDTOToTrainingClassConverter, times(1)).convert(tcDTO);
        verify(this.trainingClassRepository, times(1)).save(tc1);
        verify(this.trainingClassRepository, times(1)).findById(tc1.getId());
    }

    @Test
    void editTrainingClassSuccess() {
        // Given
        TrainingClassDTO update = new TrainingClassDTO();
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

        given(this.trainingClassRepository.findById(1L)).willReturn(Optional.of(tc1));
        given(this.trainingClassRepository.save(tc1)).willReturn(tc1);

        // When
        TrainingClass updatedTrainingClass = this.trainingClassService.editTrainingClass(update, 1L);

        // Then
        assertThat(updatedTrainingClass.getId()).isEqualTo(1L);
        assertThat(updatedTrainingClass.getTitle()).isEqualTo("Updated title");
        assertThat(updatedTrainingClass.getDescription()).isEqualTo("Updated description");
        verify(this.trainingClassRepository, times(1)).save(tc1);
        verify(this.trainingClassRepository, times(2)).findById(1L);
    }

    @Test
    void editTrainingClassNotFound() {
        // Given
        TrainingClassDTO update = new TrainingClassDTO();
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

        given(this.trainingClassRepository.findById(1L)).willReturn(Optional.empty());

        // When
        assertThrows(ObjectNotFoundException.class, () -> {
            this.trainingClassService.editTrainingClass(update, 1L);
        });

        // Then
        verify(this.trainingClassRepository, times(1)).findById(1L);
    }

    @Test
    void deleteTrainingClassSuccess() {
        // Given
        given(this.trainingClassRepository.findById(2L)).willReturn(Optional.of(tc2));
        doNothing().when(this.trainingClassRepository).delete(tc2);

        // When
        this.trainingClassService.deleteTrainingClass(2L);

        // Then
        verify(this.trainingClassRepository, times(1)).delete(tc2);
    }

    @Test
    void deleteTrainingClassNotFound() {
        // Given
        given(this.trainingClassRepository.findById(2L)).willReturn(Optional.empty());

        // When
        assertThrows(ObjectNotFoundException.class, () -> {
            this.trainingClassService.deleteTrainingClass(2L);
        });

        // Then
        verify(this.trainingClassRepository, times(1)).findById(2L);
    }

    @Test
    void attendClassSuccess() {
        // Given
        given(this.userService.findByEmail("joe@gmail.com")).willReturn(user);
        given(this.trainingClassRepository.findById(2L))
                .willReturn(Optional.of(tc2));

        // When
        this.trainingClassService.attendClass(2L, "joe@gmail.com");

        // Then
        assertThat(tc2.getUserTrainingClasses().size()).isEqualTo(1);
        verify(this.userService, times(1)).findByEmail("joe@gmail.com");
        verify(this.trainingClassRepository, times(1)).findById(tc2.getId());
        verify(this.trainingClassRepository, times(1)).save(tc2);
        verify(this.userRepository, times(1)).save(user);
    }

    @Test
    void attendClassNotFound() {
        // Given
        given(this.userService.findByEmail("joe@gmail.com")).willReturn(user);
        given(this.trainingClassRepository.findById(2L))
                .willReturn(Optional.empty());

        // When
        assertThrows(ObjectNotFoundException.class, () -> {
            this.trainingClassService.attendClass(2L, "joe@gmail.com");
        });

        // Then
        verify(this.userService, times(1)).findByEmail("joe@gmail.com");
        verify(this.trainingClassRepository, times(1)).findById(tc2.getId());
    }

    @Test
    void attendClassAlreadyAttended() {
        // Given
        UserTrainingClass userTrainingClass = new UserTrainingClass();
        userTrainingClass.setUser(user);
        userTrainingClass.setHost(false);

        tc2.addUserTrainingClass(userTrainingClass);

        given(this.userService.findByEmail("joe@gmail.com")).willReturn(user);
        given(this.trainingClassRepository.findById(2L))
                .willReturn(Optional.of(tc2));

        // When
        assertThrows(UserNotFoundException.class, () -> {
            this.trainingClassService.attendClass(2L, "joe@gmail.com");
        });

        // Then
        assertThat(tc2.getUserTrainingClasses().size()).isEqualTo(1);
        verify(this.userService, times(1)).findByEmail("joe@gmail.com");
        verify(this.trainingClassRepository, times(1)).findById(tc2.getId());
    }

    @Test
    void abandonClassSuccess() {
        // Given

        given(this.userService.findByEmail("joe@gmail.com")).willReturn(user);
        given(this.trainingClassRepository.findById(2L))
                .willReturn(Optional.of(tc2));

        UserTrainingClass userTrainingClass = new UserTrainingClass();
        userTrainingClass.setUser(user);
        userTrainingClass.setHost(false);

        tc2.addUserTrainingClass(userTrainingClass);
        // When
        this.trainingClassService.abandonClass(2L, "joe@gmail.com");

        // Then
        assertThat(tc2.getUserTrainingClasses().size()).isEqualTo(0);
        verify(this.userService, times(1)).findByEmail("joe@gmail.com");
        verify(this.trainingClassRepository, times(1)).findById(tc2.getId());
        verify(this.trainingClassRepository, times(1)).save(tc2);
        verify(this.userRepository, times(1)).save(user);
    }

    @Test
    void abandonClassNotFound() {
        // Given
        given(this.userService.findByEmail("joe@gmail.com")).willReturn(user);
        given(this.trainingClassRepository.findById(2L))
                .willReturn(Optional.empty());

        // When
        assertThrows(ObjectNotFoundException.class, () -> {
            this.trainingClassService.abandonClass(2L, "joe@gmail.com");
        });

        // Then
        verify(this.userService, times(1)).findByEmail("joe@gmail.com");
        verify(this.trainingClassRepository, times(1)).findById(tc2.getId());
    }

    @Test
    void abandonClassNotAttended() {
        // Given
        given(this.userService.findByEmail("joe@gmail.com")).willReturn(user);
        given(this.trainingClassRepository.findById(2L))
                .willReturn(Optional.of(tc2));

        // When
        assertThrows(UserNotFoundException.class, () -> {
            this.trainingClassService.abandonClass(2L, "joe@gmail.com");
        });

        // Then
        assertThat(tc2.getUserTrainingClasses().size()).isEqualTo(0);
        verify(this.userService, times(1)).findByEmail("joe@gmail.com");
        verify(this.trainingClassRepository, times(1)).findById(tc2.getId());
    }

    @Test
    void abandonClassHosted() {
        // Given
        UserTrainingClass userTrainingClass = new UserTrainingClass();
        userTrainingClass.setUser(user);
        userTrainingClass.setHost(true);

        tc2.addUserTrainingClass(userTrainingClass);

        given(this.userService.findByEmail("joe@gmail.com")).willReturn(user);
        given(this.trainingClassRepository.findById(2L))
                .willReturn(Optional.of(tc2));

        // When
        assertThrows(UserNotFoundException.class, () -> {
            this.trainingClassService.abandonClass(2L, "joe@gmail.com");
        });

        // Then
        assertThat(tc2.getUserTrainingClasses().size()).isEqualTo(1);
        verify(this.userService, times(1)).findByEmail("joe@gmail.com");
        verify(this.trainingClassRepository, times(1)).findById(tc2.getId());
    }
}