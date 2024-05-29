package com.project.payload.mappers;

import com.project.entity.concretes.user.User;
import com.project.payload.request.abstracts.AbstractUserRequest;
import com.project.payload.request.abstracts.BaseUserRequest;
import com.project.payload.response.user.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User mapUserRequestToUser(BaseUserRequest userRequest){

        return User.builder()
                .username(userRequest.getUsername())
                .name(userRequest.getName())
                .surname(userRequest.getSurname())
                .password(userRequest.getPassword())
                .ssn(userRequest.getSsn())
                .birthDay(userRequest.getBirthDay())
                .birthPlace(userRequest.getBirthPlace())
                .phoneNumber(userRequest.getPhoneNumber())
                .gender(userRequest.getGender())
                .email(userRequest.getEmail())
                .built_in(userRequest.getBuiltIn())
                .build();
    }

    public User mapUpdatedUserRequestToUser(BaseUserRequest updatedUserRequest){

        return User.builder()
                .username(updatedUserRequest.getUsername())
                .name(updatedUserRequest.getName())
                .surname(updatedUserRequest.getSurname())
                .password(updatedUserRequest.getPassword())
                .ssn(updatedUserRequest.getSsn())
                .birthDay(updatedUserRequest.getBirthDay())
                .birthPlace(updatedUserRequest.getBirthPlace())
                .phoneNumber(updatedUserRequest.getPhoneNumber())
                .gender(updatedUserRequest.getGender())
                .email(updatedUserRequest.getEmail())
                .built_in(updatedUserRequest.getBuiltIn())
                .build();
    }

    public UserResponse mapUserToUserResponse(User user){

        return UserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .surname(user.getSurname())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .birthDay(user.getBirthDay())
                .birthPlace(user.getBirthPlace())
                .ssn(user.getSsn())
                .email(user.getEmail())
                .userRole(user.getUserRole().getRoleType().name())
                .build();

    }

}
