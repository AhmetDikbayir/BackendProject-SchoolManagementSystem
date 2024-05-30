package com.project.service.user;

import com.project.entity.concretes.user.User;
import com.project.entity.enums.RoleType;
import com.project.exception.ConflictException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.UserMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.user.UserRequest;
import com.project.payload.request.user.UserRequestWithoutPassword;
import com.project.payload.response.abstracts.BaseUserResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.payload.response.user.UserResponse;
import com.project.repository.user.UserRepository;
import com.project.service.helper.MethodHelper;
import com.project.service.helper.PageableHelper;
import com.project.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UniquePropertyValidator uniquePropertyValidator;
    private final UserMapper userMapper;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final PageableHelper pageableHelper;
    private final MethodHelper methodHelper;

    public ResponseMessage<UserResponse> saveUser(UserRequest userRequest, String userRole) {

        //!!! username - ssn- phoneNumber unique mi kontrolu ??
        uniquePropertyValidator.checkDuplicate(userRequest.getUsername(),userRequest.getSsn(),
                userRequest.getPhoneNumber(),userRequest.getEmail());
        //!!! DTO --> POJO
        User user = userMapper.mapUserRequestToUser(userRequest);
        // !!! Rol bilgisi setleniyor
        if(userRole.equalsIgnoreCase(RoleType.ADMIN.name())){
            if(Objects.equals(userRequest.getUsername(),"Admin")){ // Mirac
                user.setBuilt_in(true);
            }
            user.setUserRole(userRoleService.getUserRole(RoleType.ADMIN));
        } else if (userRole.equalsIgnoreCase("Dean")) {
            user.setUserRole(userRoleService.getUserRole(RoleType.MANAGER));
        } else if (userRole.equalsIgnoreCase("ViceDean")) {
            user.setUserRole(userRoleService.getUserRole(RoleType.ASSISTANT_MANAGER));
        }
        // !!! password encode ediliyor
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Advisor degil
        user.setIsAdvisor(Boolean.FALSE);

        User savedUser = userRepository.save(user);

        return ResponseMessage.<UserResponse>builder()
                .message(SuccessMessages.USER_CREATE)
                .object(userMapper.mapUserToUserResponse(savedUser))
                .build();
    }

    public Page<UserResponse> getAllAdminOrDeanOrViceDeanByPage(int page, int size, String sort, String type, String userRole) {

        Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending());
        if(Objects.equals(type, "desc")){
            pageable = PageRequest.of(page,size, Sort.by(sort).descending());
        }
        return userRepository.findByUserRoleEquals(userRole, pageable).
                map(userMapper::mapUserToUserResponse);
    }

    public UserResponse getUserById(Long id) {
        return userRepository.findById(id).
                map(userMapper::mapUserToUserResponse).
                orElseThrow(()-> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND));
    }

    public ResponseMessage<String> deleteUser(Long id) {
        getUserById(id);
        userRepository.deleteById(id);
        return ResponseMessage.<String>builder()
                .object(null)
                .message(SuccessMessages.USER_DELETE)
                .httpStatus(HttpStatus.NO_CONTENT)
                .build();
    }

    public Page<UserResponse> getByNameByPage(int page, int size, String sort, String type, String username) {

        Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending());
        if(Objects.equals(type, "desc")){
            pageable = PageRequest.of(page,size, Sort.by(sort).descending());
        }
        return userRepository.findByUsername(username, pageable).
                map(userMapper::mapUserToUserResponse);

    }
/*
    public UserResponse updateAdminOrDeanOrViceDean(UpdateUserRequest updateUserRequest, Long id) {
        //!!! id'li user var mi kontrolu :
        UserResponse foundUser = getUserById(id);
        // !!! email exist mi ? ve eger email degisecek ise DB de mevcutta olan emaillerden olmamasi gerekiyor
        boolean isEmailExist = userRepository.existsByEmail(updateUserRequest.getEmail());
        boolean isUserNameExist = userRepository.existsByUsername(updateUserRequest.getUsername());
        boolean isPhoneNumberExist = userRepository.existsByPhoneNumber(updateUserRequest.getPhoneNumber());
        */
        /*
        POJO CLASS , DTO
        1) kendi email : mrc , mrc --> TRUE && FALSE (UPDATE OLUR)
        2) kendi email : mrc, ahmet ve DB'de zaten ahmet diye biri var --> TRUE && TRUE (EXCEPTION ATAR)
        3) kendi email : mrc, mehmet ama DB'de mehmet diye biri yok --> FALSE &&  TRUE (UPDATE OLUR)
        */
/*
        if(isEmailExist && !updateUserRequest.getEmail().equals(foundUser.getEmail())){
            throw new ConflictException(ErrorMessages.ALREADY_REGISTER_MESSAGE_EMAIL);
        }

        if(isUserNameExist && !updateUserRequest.getUsername().equals(foundUser.getUsername())){
            throw new ConflictException(ErrorMessages.ALREADY_REGISTER_MESSAGE_USERNAME);
        }

        if(isPhoneNumberExist && !updateUserRequest.getPhoneNumber().equals(foundUser.getPhoneNumber())){
            throw new ConflictException(ErrorMessages.ALREADY_REGISTER_MESSAGE_PHONE);
        }

        User updatedUser = userMapper.mapUpdatedUserRequestToUser(updateUserRequest);
        userRepository.save(updatedUser);
        return userMapper.mapUserToUserResponse(updatedUser);
    }
*/
public ResponseMessage<BaseUserResponse> updateUser(UserRequest userRequest, Long userId) {

    User user = methodHelper.isUserExist(userId);
    // !!! bulit_in kontrolu
    methodHelper.checkBuiltIn(user);
    //!!! update isleminde gelen request de unique olmasi gereken eski datalar hic degismedi ise
    // dublicate kontrolu yapmaya gerek yok :
    uniquePropertyValidator.checkUniqueProperties(user, userRequest);
    //!!! DTO --> POJO
    User updatedUser = userMapper.mapUserRequestToUpdatedUser(userRequest, userId);
    // !!! Password Encode
    updatedUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
    updatedUser.setUserRole(user.getUserRole());
    User savedUser = userRepository.save(updatedUser);

    return ResponseMessage.<BaseUserResponse>builder()
            .message(SuccessMessages.USER_UPDATE_MESSAGE)
            .httpStatus(HttpStatus.OK)
            .object(userMapper.mapUserToUserResponse(savedUser))
            .build();
}

    // Not: updateUserForUser() **********************************************************
    public ResponseEntity<String> updateUserForUsers(UserRequestWithoutPassword userRequest,
                                                     HttpServletRequest request) {
        String userName = (String) request.getAttribute("username");
        User user = userRepository.findByUsernameEquals(userName);

        // !!! bulit_in kontrolu
        methodHelper.checkBuiltIn(user);

        // !!! unique kontrolu
        uniquePropertyValidator.checkUniqueProperties(user, userRequest);
        // !!! DTO --> pojo donusumu burada yazildi
        user.setUsername(userRequest.getUsername());
        user.setBirthDay(userRequest.getBirthDay());
        user.setEmail(userRequest.getEmail());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setBirthPlace(userRequest.getBirthPlace());
        user.setGender(userRequest.getGender());
        user.setName(userRequest.getName());
        user.setSurname(userRequest.getSurname());
        user.setSsn(userRequest.getSsn());

        userRepository.save(user);

        String message = SuccessMessages.USER_UPDATE;

        return ResponseEntity.ok(message) ;
    }

    public long countAllAdmins(){
        return userRepository.countAdmin(RoleType.ADMIN);
    }
}
