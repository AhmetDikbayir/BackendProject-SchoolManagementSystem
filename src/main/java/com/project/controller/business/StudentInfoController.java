package com.project.controller.business;

import com.project.entity.concretes.business.StudentInfo;
import com.project.payload.request.business.StudentInfoRequest;
import com.project.payload.response.business.ResponseMessage;
import com.project.payload.response.business.StudentInfoResponse;
import com.project.service.business.StudentInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/studentInfo")
@RequiredArgsConstructor
public class StudentInfoController {

    private final StudentInfoService studentInfoService;

    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @PostMapping("/save") // http://localhost:8080/studentInfo/save   + POST  + JSON
    public ResponseMessage<StudentInfoResponse> saveStudentInfo(HttpServletRequest httpServletRequest,
                                                                @RequestBody @Valid StudentInfoRequest studentInfoRequest){

        return studentInfoService.saveStudentInfo(httpServletRequest, studentInfoRequest);
    }

    // Not : ( ODEV )  Delete() ************************************************************
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @DeleteMapping("/delete/{studentInfoId}") // http://localhost:8080/studentInfo/delete/1
    public ResponseMessage deleteStudentInfoById(@PathVariable Long studentInfoId){
        return studentInfoService.deleteStudentInfoById(studentInfoId);
    }

    // Not: ( ODEV ) getAllWithPage ********************************************************
    @PreAuthorize("hasAnyAuthority('ADMIN, MANAGER,ASSISTANT_MANAGER')")
    @GetMapping("/getAllWithPage")   // http://localhost:8080/studentInfo/getAllWithPage
    public ResponseEntity<Page<StudentInfoResponse>> getAllWithPage(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size
    ){
        return new ResponseEntity<>(studentInfoService.getAllWithPage(page,size), HttpStatus.OK);
    }

    // Not: ( ODEV ) Update() *************************************************************
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @PutMapping("/update/{studentInfoId}") // http://localhost:8080/studentInfo/update/1
    public ResponseMessage<StudentInfoResponse> updateStudentInfoById(
            @PathVariable Long studentInfoId,
            @RequestBody @Valid StudentInfoRequest studentInfoRequest){
        return studentInfoService.updateStudentInfoById(studentInfoId, studentInfoRequest);
    }

    // !!! -> Bir ogretmen kendi ogrencilerinin bilgilerini almak isterse :
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @GetMapping("/getAllForTeacher")   // http://localhost:8080/studentInfo/getAllForTeacher
    public ResponseEntity<Page<StudentInfoResponse>> getAllForTeacher(
            HttpServletRequest httpServletRequest,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size
    ){
        return new ResponseEntity<>(studentInfoService.getAllForTeacher(httpServletRequest,page,size), HttpStatus.OK);
    }

    //!!! --> bir öğrenci kendi bilgilerini almak isterse
    @PreAuthorize("hasAnyAuthority('STUDENT')")
    @GetMapping("/getAllForStudent")
    public ResponseEntity<Page<StudentInfoResponse>> getAllForStudent(HttpServletRequest httpServletRequest,
                                                                      @RequestParam(value = "page") int page,
                                                                      @RequestParam(value = "size") int size){
        return new ResponseEntity<>(studentInfoService.getAllForStudent(httpServletRequest,page,size), HttpStatus.OK);
    }

}
