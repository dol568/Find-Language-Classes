package com.springbootangularcourses.springbootbackend.resource;

import com.springbootangularcourses.springbootbackend.domain.LanguageClass;
import com.springbootangularcourses.springbootbackend.service.LanguageClassService;
import com.springbootangularcourses.springbootbackend.system.HttpResponse;
import com.springbootangularcourses.springbootbackend.utils.converter.LanguageClassToReturnLanguageClassConverter;
import com.springbootangularcourses.springbootbackend.utils.converter.LanguageClassesToReturnLanguageClassesConverter;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnLanguageClass;
import com.springbootangularcourses.springbootbackend.domain.dto.LanguageClassDTO;
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
@RequestMapping("/api/languageClasses")
public class LanguageClassController {

    private final LanguageClassService languageClassService;
    private final LanguageClassToReturnLanguageClassConverter LanguageClassToReturnLanguageClass;
    private final LanguageClassesToReturnLanguageClassesConverter LanguageClassesToReturnLanguageClassesConverter;

    @GetMapping("")
    public ResponseEntity<HttpResponse> getLanguageClasses() {
        List<LanguageClass> classes = this.languageClassService.getAllLanguageClasses();

        List<ReturnLanguageClass> returnClasses = this.LanguageClassesToReturnLanguageClassesConverter.convert(classes);

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(returnClasses)
                        .message("Language classes retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpResponse> getLanguageClass(@PathVariable Long id) {
        LanguageClass foundClass = this.languageClassService.getLanguageClass(id);

        ReturnLanguageClass returnLanguageClass = this.LanguageClassToReturnLanguageClass.convert(foundClass);

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(returnLanguageClass)
                        .message("Language class with id '" + returnLanguageClass.getId() + "' retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PostMapping("")
    public ResponseEntity<HttpResponse> addLanguageClass(@Valid @RequestBody LanguageClassDTO languageClassDTO,
                                                         Principal principal) {

        LanguageClass languageClass = this.languageClassService.saveLanguageClass(languageClassDTO, principal.getName());

        ReturnLanguageClass returnLanguageClass = this.LanguageClassToReturnLanguageClass.convert(languageClass);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(languageClass.getId()).toUri();

        return ResponseEntity.created(uri).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(returnLanguageClass)
                        .message("Language class created")
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .build());
    }

    @PutMapping("{id}")
    public ResponseEntity<HttpResponse> editLanguageClass(@Valid @RequestBody LanguageClassDTO languageClassDTO,
                                                          @PathVariable Long id) {

        LanguageClass languageClass
                = this.languageClassService.editLanguageClass(languageClassDTO, id);

        ReturnLanguageClass returnLanguageClass = this.LanguageClassToReturnLanguageClass.convert(languageClass);

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(returnLanguageClass)
                        .message("Language class updated")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpResponse> deleteLanguageClass(@PathVariable Long id) {

        this.languageClassService.deleteLanguageClass(id);

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .message("Language class deleted")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PostMapping("/{id}/attend")
    public ResponseEntity<HttpResponse> attendClass(@PathVariable Long id, Principal principal) {

        LanguageClass languageClass
                = this.languageClassService.attendClass(id, principal.getName());
        ReturnLanguageClass returnLanguageClass = this.LanguageClassToReturnLanguageClass.convert(languageClass);

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(returnLanguageClass)
                        .message("Language class attended")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @DeleteMapping("/{id}/abandon")
    public ResponseEntity<HttpResponse> abandonClass(@PathVariable Long id, Principal principal) {

        LanguageClass languageClass
                = this.languageClassService.abandonClass(id, principal.getName());
        ReturnLanguageClass returnLanguageClass = this.LanguageClassToReturnLanguageClass.convert(languageClass);

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(returnLanguageClass)
                        .message("Language class abandoned")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }
}
