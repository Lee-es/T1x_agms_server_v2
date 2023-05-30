package com.example.uxn_api.service.user;

import com.example.uxn_api.web.error.ErrorCode;
import com.example.uxn_api.web.error.LoginException;
import com.example.uxn_common.global.domain.device.repository.DeviceValueRepository;
import com.example.uxn_common.global.domain.note.repository.CalibrationNoteRepository;
import com.example.uxn_common.global.domain.note.repository.EventNoteRepository;
import com.example.uxn_common.global.domain.patient.Patient;
import com.example.uxn_common.global.domain.patient.PatientDevice;
import com.example.uxn_common.global.domain.patient.repository.PatientDeviceRepository;
import com.example.uxn_common.global.domain.patient.repository.PatientRepository;
import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.repository.PatientStaffRepository;
import com.example.uxn_common.global.domain.user.repository.UserRepository;
import com.example.uxn_common.global.domain.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final StaffRepository staffRepository;
    private final PatientStaffRepository patientStaffRepository;
    private final DeviceValueRepository deviceValueRepository;
    private final PatientDeviceRepository patientDeviceRepository;

    private final EventNoteRepository eventNoteRepository;
    private final CalibrationNoteRepository calibrationNoteRepository;


    @Transactional
    public void deletePatient(Patient patient){

        try{
            int userId = patient.getUserId();

            //patientStaff 삭제
            patientStaffRepository.deleteAllByStaffId(userId);
            patientStaffRepository.flush();

            //deviceID 연관 value 삭제
            List<PatientDevice> patientDeviceList = patientDeviceRepository.findAllByPatientId(userId);
            if(patientDeviceList!=null) {
                for (PatientDevice mapping : patientDeviceList) {
                    deviceValueRepository.deleteAllByDeviceId(mapping.getDeviceId());
                    deviceValueRepository.flush();

                    //deviceValue2

                    eventNoteRepository.deleteAllByDeviceID(mapping.getDeviceId());
                    eventNoteRepository.flush();

                    calibrationNoteRepository.deleteAllByDeviceID(mapping.getDeviceId());
                    calibrationNoteRepository.flush();

                    //patient device
                    patientDeviceRepository.delete(mapping);
                    patientDeviceRepository.flush();
                }//for
            }


            patientRepository.delete(patient);
            patientRepository.flush();

            userRepository.deleteAllByUserId(userId);
            userRepository.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public User findByIdx(Integer id){
        return userRepository.findById(id).orElseThrow();
    }

    @Transactional
    public void emailVerifyCode(String code, String email){
        User user = userRepository.findByEmail(email);
        user.setEmailVerifyCode(code);
//        user.setEmailVerifyStartTime(LocalDateTime.now());
    }

    @Transactional
    public void emailCheck(String email){
        User user = userRepository.findByEmail(email);
        user.emailCheck(true);

    }

}
