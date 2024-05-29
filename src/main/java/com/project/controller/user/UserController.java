package com.project.controller.user;

import com.project.contactmessage.dto.ContactMessageResponse;
import com.project.payload.request.user.UpdateUserRequest;
import com.project.payload.request.user.UserRequest;
import com.project.payload.response.business.ResponseMessage;
import com.project.payload.response.user.UserResponse;
import com.project.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/save/{userRole}") // http://localhost:8080/user/save/Admin  + JSON + POST
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseMessage<UserResponse>> saveUser(@RequestBody @Valid UserRequest userRequest,
                                                                  @PathVariable String userRole){
        return ResponseEntity.ok(userService.saveUser(userRequest, userRole));
    }

    // Not: getAllAdminOrDeanOrViceDeanByPage() ******************************************
    @GetMapping("/getAll/{userRole}") // http://localhost:8080/getAll/Admin?page=0&size=10&sort=dateTime&type=desc + GET
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Page<UserResponse> getAllAdminOrDeanOrViceDeanByPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "dateTime") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type,
            @PathVariable String userRole
    ) {
        return userService.getAllAdminOrDeanOrViceDeanByPage(page, size, sort, type, userRole);
    }

    // Not :  getUserById() *********************************************************
    @GetMapping("/getUser/{id}") // http://localhost:8080/user/getUser/1 + GET
    public ResponseEntity<UserResponse> getUserById(@PathVariable("id") Long id){
        UserResponse foundUser = userService.getUserById(id);
        return ResponseEntity.ok(foundUser);
    }

    // Not : deleteUser() **********************************************************
    @DeleteMapping("/deleteUser")
    @PreAuthorize("hasAnyAuthority('ADMIN')") // http://localhost:8080/user/deleteUser?userId=1 + DELETE
    public ResponseMessage<String> deleteUser(@RequestParam(value = "userId") Long id){
        return userService.deleteUser(id);
    }

    // Not: updateAdminOrDeanOrViceDean() ********************************************
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<UserResponse> updateAdminOrDeanOrViceDean(@RequestBody UpdateUserRequest updateUserRequest, @PathVariable("id") Long id){
            return ResponseEntity.ok(userService.updateAdminOrDeanOrViceDean(updateUserRequest, id));
    }

    // Not: updateUserForUser() **********************************************************

    // Not : getByName() ***************************************************************
    @GetMapping("/getByName")
    @PreAuthorize("hasAnyAuthority('ADMIN')") // http://localhost:8080/getByName/Mirac?page=0&size=10&sort=dateTime&type=desc + GET
    public Page<UserResponse> getByNameByPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "dateTime") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type,
            @PathVariable String username
    ) {
        return userService.getByNameByPage(page, size, sort, type, username);
    }
}
