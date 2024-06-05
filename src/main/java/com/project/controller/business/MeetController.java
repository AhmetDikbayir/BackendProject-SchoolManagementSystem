package com.project.controller.business;

import com.project.payload.request.business.MeetRequest;
import com.project.payload.response.business.MeetResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.service.business.MeetService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/meets")
@RequiredArgsConstructor
public class MeetController {

    private final MeetService meetService;

    // Not : ( ODEVV ) save *******************************************
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @PostMapping("/save")
    public ResponseMessage<MeetResponse> saveMeet(HttpServletRequest httpServletRequest,
                                                  @RequestBody @Valid MeetRequest meetRequest){
        return meetService.saveMeet(httpServletRequest, meetRequest);
    }
    // Not: ( ODEVV ) update ******************************************


    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/getAll") // http://localhost:8080/meet/getAll
    public List<MeetResponse> getAll(){
        return meetService.getAll();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/getMeetById/{meetId}") // http://localhost:8080/meet/getMeetById/1
    public ResponseMessage<MeetResponse> getMeetById(@PathVariable Long meetId){
        return meetService.getMeetById(meetId);
    }
}