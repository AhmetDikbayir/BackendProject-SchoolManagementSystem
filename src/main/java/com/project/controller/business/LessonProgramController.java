package com.project.controller.business;

import com.project.entity.concretes.business.Lesson;
import com.project.payload.request.business.LessonProgramRequest;
import com.project.payload.response.business.LessonProgramResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.service.business.LessonProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/lessonPrograms")
@RequiredArgsConstructor
public class LessonProgramController {

    private final LessonProgramService lessonProgramService;

    @PostMapping("/save") // http://localhost:8080/lessonPrograms/save + POST + JSON
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    public ResponseMessage<LessonProgramResponse> saveLessonProgram(@RequestBody @Valid
                                                             LessonProgramRequest lessonProgramRequest){
        return lessonProgramService.saveLessonProgram(lessonProgramRequest);
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    public List<LessonProgramResponse> getAllLessonProgram(){
        return lessonProgramService.getAllLessonProgram();
    }

    //Not : ODEV getById()  **********************

    @GetMapping("/getById")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    public ResponseMessage<LessonProgramResponse>getById(@PathVariable Long lessonProgramId){
        return lessonProgramService.getById(lessonProgramId);
    }

    //herhangi bir kullanıcı ataması yapılmamıs butun dersprogramlarını getireceğiz
    @GetMapping("/getAllUnAssigned")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    public List<LessonProgramResponse> getAllUnassigned(){
        return lessonProgramService.getAllUnassigned();
    }

    //Not : ODEV  getAllLessonProgramAssigned() ****************

    @GetMapping("/getAllLessonProgramAssigned")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public List<LessonProgramResponse> getAllLessonProgramAssigned(){
        return lessonProgramService.getAllLessonProgramAssigned();
    }

    //Not : Delete() *************************

    @DeleteMapping("/deleteLessonProgramById")
    @PreAuthorize("AnyAuthority('ADMIN','MANAGER','ASSISSTANT_MANAGER')")
    public ResponseMessage deleteLessonProgramById(@PathVariable Long lessonProgramId){
        return lessonProgramService.deleteLessonProgramById(lessonProgramId);
    }

    // Not : ( ODEV ) getAllWithPage() *************************************************
    @GetMapping("/getAllLessonProgramByPage") // http://localhost:8080/lessonPrograms/getAllLessonProgramByPage?page=0&size=1&sort=id&type=desc
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    public Page<LessonProgramResponse> getAllLessonProgramByPage (
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "type") String type){
        return lessonProgramService.getAllLessonProgramByPage(page,size,sort,type);
    }


    //bir öğretmen kendine ait lessonProgramları getiriyor

    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @GetMapping("/getAllLessonProgramByTeacher")
    public Set<LessonProgramResponse> getAllLessonProgramByTeacher(HttpServletRequest httpServletRequest){
        return lessonProgramService.getAllLessonProgramByUser(httpServletRequest);
    }

    //bir öğretmen kendine ait lessonProgramları getiriyor
    @PreAuthorize("hasAnyAuthority('STUDENT')")
    @GetMapping("/getAllLessonProgramByTeacher")
    public Set<LessonProgramResponse> getAllLessonProgramByStudent(HttpServletRequest httpServletRequest){
        return lessonProgramService.getAllLessonProgramByUser(httpServletRequest);
    }
}
