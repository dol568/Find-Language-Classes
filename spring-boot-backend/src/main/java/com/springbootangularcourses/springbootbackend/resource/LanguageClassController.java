package com.springbootangularcourses.springbootbackend.resource;

import com.springbootangularcourses.springbootbackend.domain.Comment;
import com.springbootangularcourses.springbootbackend.domain.LanguageClass;
import com.springbootangularcourses.springbootbackend.domain.dto.CommentDTO;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnComment;
import com.springbootangularcourses.springbootbackend.service.LanguageClassService;
import com.springbootangularcourses.springbootbackend.system.HttpResponse;
import com.springbootangularcourses.springbootbackend.utils.converter.CommentToReturnCommentConverter;
import com.springbootangularcourses.springbootbackend.utils.converter.LanguageClassToReturnLanguageClassConverter;
import com.springbootangularcourses.springbootbackend.utils.converter.LanguageClassesToReturnLanguageClassesConverter;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnLanguageClass;
import com.springbootangularcourses.springbootbackend.domain.dto.LanguageClassDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Map.of;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/languageClasses")
public class LanguageClassController {


    private final LanguageClassService languageClassService;
    private final LanguageClassToReturnLanguageClassConverter languageClassToReturnLanguageClass;
    private final LanguageClassesToReturnLanguageClassesConverter languageClassesToReturnLanguageClassesConverter;
    private final CommentToReturnCommentConverter commentToReturnCommentConverter;

    @GetMapping("")
    public ResponseEntity<HttpResponse> getLanguageClasses() {
        List<LanguageClass> classes = this.languageClassService.getAllLanguageClasses();

        List<ReturnLanguageClass> returnClasses = this.languageClassesToReturnLanguageClassesConverter.convert(classes);

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

        ReturnLanguageClass returnLanguageClass = this.languageClassToReturnLanguageClass.convert(foundClass);

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

        ReturnLanguageClass returnLanguageClass = this.languageClassToReturnLanguageClass.convert(languageClass);

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

        ReturnLanguageClass returnLanguageClass = this.languageClassToReturnLanguageClass.convert(languageClass);

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
        ReturnLanguageClass returnLanguageClass = this.languageClassToReturnLanguageClass.convert(languageClass);

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
        ReturnLanguageClass returnLanguageClass = this.languageClassToReturnLanguageClass.convert(languageClass);

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(returnLanguageClass)
                        .message("Language class abandoned")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping("/{id}/comment")
    public ResponseEntity<HttpResponse> getComments(@PathVariable Long id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {

        Page<Comment> comments = this.languageClassService.getAllComments(id, page, size);

        List<ReturnComment> returnCommentList = comments.getContent().stream()
                .map(commentToReturnCommentConverter::convert)
                .collect(Collectors.toList());

        Page<ReturnComment> returnCommentPage = new PageImpl<>(returnCommentList, PageRequest.of(page, size), comments.getTotalElements());

        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(of("page", returnCommentPage))
                        .message("Comments for language class with id '" + id + "' retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<HttpResponse> postComment(@Valid @RequestBody CommentDTO commentDTO, @PathVariable Long id, Principal principal) {

        Comment comment = this.languageClassService.postComment(commentDTO, id, principal.getName());

        ReturnComment returnComment = this.commentToReturnCommentConverter.convert(comment);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(comment.getId()).toUri();

        return ResponseEntity.created(uri).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(returnComment)
                        .message("Comment has been posted")
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .build());
    }
}
