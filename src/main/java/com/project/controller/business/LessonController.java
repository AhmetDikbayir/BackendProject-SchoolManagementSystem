package com.project.controller.business;

import com.project.payload.request.business.LessonRequest;
import com.project.payload.response.business.LessonResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.service.business.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping("/save") //http://localhost:8080/lessons/save + POST + JSON
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    public ResponseMessage<LessonResponse> saveLesson(@RequestBody @Valid LessonRequest lessonRequest){
        return lessonService.saveLesson(lessonRequest);
    }

    //Not : ODEVV : deleteById **********************************
    //Not : ODEVV : getAllWithPage ******************************

    @GetMapping("/getLessonByName") // http://localhost:8080/lessons/getLessonByName?lessonName=java
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    public ResponseMessage<LessonResponse>getLessonByLessonName(@RequestParam String lessonName){

        return lessonService.getLessonByLessonName(lessonName);
    }

    //Not : ODEVV : getLessonByIdList( ) ***********************
    //- 1 - 3 -5 di li lesson getir
    //requestPAram ile al
    //istetyen bir tane isteyen on tane

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    @PutMapping("/update/{lessonId}") //http://localhost:8080/lessons/update/1
    public ResponseEntity<LessonResponse> updateLessonById(@PathVariable Long lessonId,
                                                           @RequestBody LessonRequest lessonRequest){
        return ResponseEntity.ok(lessonService.updateLessonById(lessonId, lessonRequest));
    }
}