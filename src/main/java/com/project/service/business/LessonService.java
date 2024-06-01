package com.project.service.business;

import com.project.entity.concretes.business.Lesson;
import com.project.exception.ConflictException;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.LessonMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.LessonRequest;
import com.project.payload.response.business.LessonResponse;
import com.project.payload.response.business.ResponseMessage;
import com.project.repository.business.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;

    public ResponseMessage<LessonResponse> saveLesson(LessonRequest lessonRequest) {
        isLessonExistsByLessonName(lessonRequest.getLessonName());
        Lesson savedLesson = lessonRepository.save((lessonMapper.mapLessonRequestToLesson(lessonRequest)));
        return ResponseMessage.<LessonResponse>builder()
                .object(lessonMapper.mapLessonToLessonResponse(savedLesson))
                .message(SuccessMessages.LESSON_SAVE)
                .httpStatus(HttpStatus.CREATED)
                .build();

    }

    private boolean isLessonExistsByLessonName(String lessonName){
        boolean lessonExist = lessonRepository.existsByLessonNameEqualsIgnoreCase(lessonName);

        if(lessonExist){
            throw new ConflictException(ErrorMessages.LESSON_ALREADY_EXIST_WITH_LESSON_NAME);
        }else return false;
    }

    public ResponseMessage<LessonResponse> getLessonByLessonName(String lessonName) {

        if(lessonRepository.getLessonByLessonName(lessonName).isPresent()){
            return ResponseMessage.<LessonResponse>builder()
                    .message(SuccessMessages.LESSON_FOUND)
                    .object(lessonMapper.mapLessonToLessonResponse(lessonRepository.getLessonByLessonName(lessonName).get()))
                    .build();
        }else {
            return ResponseMessage.<LessonResponse>builder()
                    .message(String.format(ErrorMessages.NOT_FOUND_LESSON_MESSAGE, lessonName))
                    .build();
        }

    }

    public LessonResponse updateLessonById(Long lessonId, LessonRequest lessonRequest) {

        Lesson lesson = isLessonExistById(lessonId);
        if(
               !(lesson.getLessonName().equals(lessonRequest.getLessonName())) &&
                       (lessonRepository.existsByLessonNameEqualsIgnoreCase(lessonRequest.getLessonName()))
        ){
            throw new ConflictException(String.format(ErrorMessages.LESSON_ALREADY_EXIST_WITH_LESSON_NAME, lessonRequest));
        }

        Lesson updatedLesson = lessonMapper.mapLessonRequestToUpdatedLesson(lessonId, lessonRequest);

        updatedLesson.setLessonPrograms(lesson.getLessonPrograms());
        Lesson savedLesson = lessonRepository.save(updatedLesson);

        return lessonMapper.mapLessonToLessonResponse(savedLesson);

    }


    public Lesson isLessonExistById(Long id){
        return lessonRepository.findById(id).orElseThrow(()->new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_LESSON_MESSAGE)));
    }
}
