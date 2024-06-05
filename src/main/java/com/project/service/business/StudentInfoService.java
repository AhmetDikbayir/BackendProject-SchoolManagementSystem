package com.project.service.business;

import com.project.entity.concretes.business.EducationTerm;
import com.project.entity.concretes.business.Lesson;
import com.project.entity.concretes.business.StudentInfo;
import com.project.entity.concretes.user.User;
import com.project.entity.enums.Note;
import com.project.entity.enums.RoleType;
import com.project.exception.ResourceNotFoundException;
import com.project.payload.mappers.StudentInfoMapper;
import com.project.payload.messages.ErrorMessages;
import com.project.payload.messages.SuccessMessages;
import com.project.payload.request.business.StudentInfoRequest;
import com.project.payload.response.business.ResponseMessage;
import com.project.payload.response.business.StudentInfoResponse;
import com.project.repository.business.StudentInfoRepository;
import com.project.service.helper.MethodHelper;
import com.project.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class StudentInfoService {

    private final StudentInfoRepository studentInfoRepository;
    private final MethodHelper methodHelper;
    private final LessonService lessonService;
    private final EducationTermService educationTermService;
    private final StudentInfoMapper studentInfoMapper;
    private final PageableHelper pageableHelper;

    @Value("${midterm.exam.percentage}")
    private Double midtermExamPercentage;

    @Value("${final.exam.percentage}")
    private Double finalExamPercentage;

    public ResponseMessage<StudentInfoResponse> saveStudentInfo(HttpServletRequest httpServletRequest,
                                                                StudentInfoRequest studentInfoRequest) {

        String teacherUsername = (String) httpServletRequest.getAttribute("username");
        // !!! requestte gelen studentId ile studenti getirme
        User student = methodHelper.isUserExist(studentInfoRequest.getStudentId());
        // requestten gelen studentId gercekten bir Studenta mi ait
        methodHelper.checkRole(student, RoleType.STUDENT);

        // !!! username ile teacher getirme
        User teacher = methodHelper.isUserExistByUsername(teacherUsername);
        // !!! requestten gelen lessonId ile lesson getiriyoruz
        Lesson lesson = lessonService.isLessonExistById(studentInfoRequest.getLessonId());
        // !!! requestten gelen educationTermId ile educationTerm getiriyoruz
        EducationTerm educationTerm =
                educationTermService.findEducationTermById(studentInfoRequest.getEducationTermId());

        // letterGrade hesaplamasi :
        Note note = checkLetterGrade(calculateAverageExam(studentInfoRequest.getMidtermExam(),
                studentInfoRequest.getFinalExam()));
        // DTO --> POJO
        StudentInfo studentInfo =
                studentInfoMapper.mapStudentInfoRequestToStudentInfo(
                        studentInfoRequest,
                        note,
                        calculateAverageExam(studentInfoRequest.getMidtermExam(),
                                studentInfoRequest.getFinalExam()));

        studentInfo.setStudent(student);
        studentInfo.setTeacher(teacher);
        studentInfo.setEducationTerm(educationTerm);
        studentInfo.setLesson(lesson);

        StudentInfo savedStudentInfo = studentInfoRepository.save(studentInfo);

        return ResponseMessage.<StudentInfoResponse>builder()
                .message(SuccessMessages.STUDENT_INFO_SAVE)
                .object(studentInfoMapper.mapStudentInfoToStudentInfoResponse(savedStudentInfo))
                .httpStatus(HttpStatus.CREATED)
                .build();
    }

    private Double calculateAverageExam(Double midtermExam, Double finalExam){
        return ((midtermExam * midtermExamPercentage ) + (finalExam * finalExamPercentage));
    }

    private Note checkLetterGrade(Double average){

        //aşağıdaki kodlar hardcode olmadan yaz!!
        if(average<50.0){
            return Note.FF;
        } else if (average<60) {
            return Note.DD;
        } else if (average<65) {
            return Note.CC;
        } else if (average<70) {
            return Note.CB;
        } else if (average<75) {
            return Note.BB;
        } else if (average<80) {
            return Note.BA;
        } else   {
            return Note.AA;
        }
    }

    public Page<StudentInfoResponse> getAllForTeacher(HttpServletRequest httpServletRequest, int page, int size) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page,size);
        String username = (String) httpServletRequest.getAttribute("username");

        return studentInfoRepository.findByTeacherId_UsernameEquals(username, pageable)
                .map(studentInfoMapper::mapStudentInfoToStudentInfoResponse);
    }


    public Page<StudentInfoResponse> getAllForStudent(HttpServletRequest httpServletRequest, int page, int size) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page,size);
        String username = (String) httpServletRequest.getAttribute("username");

        return studentInfoRepository.findByStudentId_UsernameEquals(username, pageable)
                .map(studentInfoMapper::mapStudentInfoToStudentInfoResponse);

    }

    public ResponseMessage deleteStudentInfoById(Long studentInfoId) {
        isStudentInfoExistById(studentInfoId);
        studentInfoRepository.deleteById(studentInfoId);
        return ResponseMessage.builder()
                .message(SuccessMessages.STUDENT_INFO_DELETE)
                .httpStatus(HttpStatus.OK)
                .build();

    }

    private StudentInfo isStudentInfoExistById(Long studentInfoId){
        return studentInfoRepository.
                findById(studentInfoId).
                orElseThrow(()->new ResourceNotFoundException(
                        String.format (ErrorMessages.STUDENT_INFO_NOT_FOUND, studentInfoId)));
    }

    public Page<StudentInfoResponse> getAllWithPage(int page, int size) {

        Pageable pageable = pageableHelper.getPageableWithProperties(page,size);

        return studentInfoRepository.findAll(pageable)
                .map(studentInfoMapper::mapStudentInfoToStudentInfoResponse);
    }

    public ResponseMessage<StudentInfoResponse> updateStudentInfoById(
            Long studentInfoId,
            StudentInfoRequest studentInfoRequest) {
        isStudentInfoExistById(studentInfoId);
        Double updatedExamAverage = calculateAverageExam(studentInfoRequest.getMidtermExam(), studentInfoRequest.getFinalExam());
        Note updatedNote = checkLetterGrade(updatedExamAverage);
        StudentInfo updatedStudentInfo = studentInfoMapper.mapStudentInfoRequestToStudentInfo(studentInfoRequest, updatedNote, updatedExamAverage);
        studentInfoRepository.save(updatedStudentInfo);
        return ResponseMessage.<StudentInfoResponse>builder()
                .message(SuccessMessages.STUDENT_INFO_UPDATE)
                .object(studentInfoMapper.mapStudentInfoToStudentInfoResponse(updatedStudentInfo))
                .httpStatus(HttpStatus.OK)
                .build();

    }
}