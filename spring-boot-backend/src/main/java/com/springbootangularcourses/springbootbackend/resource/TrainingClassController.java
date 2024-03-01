package com.springbootangularcourses.springbootbackend.resource;

import com.springbootangularcourses.springbootbackend.domain.TrainingClass;
import com.springbootangularcourses.springbootbackend.service.TrainingClassService;
import com.springbootangularcourses.springbootbackend.system.HttpResponse;
import com.springbootangularcourses.springbootbackend.utils.converter.TrainingClassToReturnTrainingClassConverter;
import com.springbootangularcourses.springbootbackend.utils.converter.TrainingClassesToReturnTrainingClassesConverter;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnTrainingClass;
import com.springbootangularcourses.springbootbackend.domain.dto.TrainingClassDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trainingClasses")
public class TrainingClassController {

    private final TrainingClassService trainingClassService;
    private final TrainingClassToReturnTrainingClassConverter trainingClassToReturnTrainingClass;
    private final TrainingClassesToReturnTrainingClassesConverter trainingClassesToReturnTrainingClassesConverter;

    @GetMapping("")
    public ResponseEntity<HttpResponse> getTrainingClasses() {
        List<TrainingClass> classes = this.trainingClassService.getAllTrainingClasses();

        List<ReturnTrainingClass> returnClasses = this.trainingClassesToReturnTrainingClassesConverter.convert(classes);

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(returnClasses)
                        .message("Training classes retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpResponse> getTrainingClass(@PathVariable Long id) {
        TrainingClass foundClass = this.trainingClassService.getTrainingClass(id);

        ReturnTrainingClass returnTrainingClass = this.trainingClassToReturnTrainingClass.convert(foundClass);

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(returnTrainingClass)
                        .message("Training class with id '" + returnTrainingClass.getId() + "' retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PostMapping("")
    public ResponseEntity<HttpResponse> addTrainingClass(@Valid @RequestBody TrainingClassDTO trainingClassDTO,
                                                         Principal principal) {

        TrainingClass trainingClass = this.trainingClassService.saveTrainingClass(trainingClassDTO, principal.getName());

        ReturnTrainingClass returnTrainingClass = this.trainingClassToReturnTrainingClass.convert(trainingClass);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(trainingClass.getId()).toUri();

        return ResponseEntity.created(uri).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(returnTrainingClass)
                        .message("Training class created")
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .build());
    }

    @PutMapping("{id}")
    public ResponseEntity<HttpResponse> editTrainingClass(@Valid @RequestBody TrainingClassDTO trainingClassDTO,
                                                          @PathVariable Long id) {

        TrainingClass trainingClass
                = this.trainingClassService.editTrainingClass(trainingClassDTO, id);

        ReturnTrainingClass returnTrainingClass = this.trainingClassToReturnTrainingClass.convert(trainingClass);

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(returnTrainingClass)
                        .message("Training class updated")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpResponse> deleteTrainingClass(@PathVariable Long id) {

        this.trainingClassService.deleteTrainingClass(id);

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("Training class deleted")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PostMapping("/{id}/attend")
    public ResponseEntity<HttpResponse> attendClass(@PathVariable Long id, Principal principal) {

        this.trainingClassService.attendClass(id, principal.getName());

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("Training class attended")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @DeleteMapping("/{id}/abandon")
    public ResponseEntity<HttpResponse> abandonClass(@PathVariable Long id, Principal principal) {

        this.trainingClassService.abandonClass(id, principal.getName());

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("Training class abandoned")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }
}
