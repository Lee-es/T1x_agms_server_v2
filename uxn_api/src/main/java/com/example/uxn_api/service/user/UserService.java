package com.example.uxn_api.service.user;

import com.example.uxn_api.service.login.LogService;
import com.example.uxn_api.service.login.LoginAttemptService;
import com.example.uxn_api.service.login.TokenService;
import com.example.uxn_api.web.device.dto.res.CheckDeviceResponse;
import com.example.uxn_api.web.device.dto.res.PercentageDto;
import com.example.uxn_api.web.device.dto.res.UserInfoResDto;
import com.example.uxn_api.web.error.ErrorCode;
import com.example.uxn_api.web.error.LoginException;
import com.example.uxn_common.global.domain.calibration.repository.CalibrationRepository;
import com.example.uxn_common.global.domain.device.Device;
import com.example.uxn_common.global.domain.device.repository.DeviceRepository;
import com.example.uxn_common.global.domain.note.Note;
import com.example.uxn_common.global.domain.note.repository.NoteRepository;
import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.staff.repository.StaffRepository;
import com.example.uxn_common.global.domain.user.*;
import com.example.uxn_common.global.domain.user.repository.*;
import com.example.uxn_common.global.utils.excel.ExcelUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final DeviceRepository deviceRepository;
    private final UserStaffMappingRepository userStaffMappingRepository;
    private final NoteRepository noteRepository;
    private final CalibrationRepository calibrationRepository;

    private final UserAuthorityRepository userAuthorityRepository;

    private final LoginAttemptService loginAttemptService;
    private final HttpServletRequest request;

    private final ChangePasswordTokenRepository changePasswordTokenRepository;



    private final TokenService tokenService;

    private final LogService logService;

    @Transactional
    public void deleteUser(User user){

        try{
            deviceRepository.deleteAllByUser(user);
            deviceRepository.flush();
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            userStaffMappingRepository.deleteAllByUserId(user.getIdx());
            userStaffMappingRepository.flush();
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            noteRepository.deleteAllByUser(user);
            noteRepository.flush();
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            calibrationRepository.deleteAllByUser(user);
            calibrationRepository.flush();
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            userAuthorityRepository.deleteAll(user.getAuthorities());
            userAuthorityRepository.flush();
        }catch (Exception e){
            e.printStackTrace();
        }

        userRepository.delete(user);
        userRepository.flush();
    }

    @Transactional
    public void addAuthority(Long id, String authority){
        userRepository.findById(id).ifPresent(user -> {
            UserAuthority newRole = new UserAuthority(user.getIdx(), authority);
            if(user.getAuthorities() == null){
                HashSet<UserAuthority> authorities = new HashSet<>();
                authorities.add(newRole);
                user.setAuthorities(authorities);
//                User.builder().authorities(user.getAuthorities());
                saveUser(user);
            }else if(!user.getAuthorities().contains(newRole)){
                HashSet<UserAuthority> authorities = new HashSet<>();
                authorities.addAll(user.getAuthorities());
                authorities.add(newRole);
                user.setAuthorities(authorities);
//                User.builder().authorities(user.getAuthorities());
                saveUser(user);
            }
        });
    }

    // C
    @Transactional
    public User saveUser(User user){
        return userRepository.save(user);
    }

    // R
    @Transactional(readOnly = true)
    public User findByUserIdAndPassword(String id, String password){

        return userRepository.findByEmailAndPassword(id, password);
    }

    @Transactional(readOnly = true)
    public UserInfoResDto findByIdx(Long idx){
        User user = userRepository.findById(idx).orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));
        return new UserInfoResDto(user);

    }

    @Transactional(readOnly = true)
    public UserInfoResDto findByUserId(String id){
        User user = userRepository.findByEmail(id);
        return new UserInfoResDto(user);
    }

    @Transactional(readOnly = true)
    public User findUser(Long idx){
        return userRepository.findByIdx(idx);
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }


    //사용하지 않아서 주석처리 - 22.12.29 ykw

//    /**
//     * 14일치의 데이터 리스트에서 00시 00분은 00시 00분 끼리 / 00시02분은 00시 02분 끼리 묶여야함.
//     * */
//    public List<PercentageDto> percentage(Long id) { //
//        User user = userRepository.findByIdx(id);
//        Set<Device> deviceList = user.getDevices();
//        ArrayList<Double> diabetesList = new ArrayList<>();
//        ArrayList<PercentageDto> percentageDtos = new ArrayList<>();
//        ExcelUtils excelUtils = new ExcelUtils();
//
//        for(Device list : deviceList) {
//            diabetesList.add(list.getDiabetesLevel()); // 00 시 00분의 당뇨 수치 리스트 , 00시 02 분의 당뇨 수치 리스트 .....
//
//            double five = diabetesList.size() * 0.005; // 5% -> 0.7(14일 기준 -> 14 x 0.005 -> 0.7)
//            double twentyFive = diabetesList.size() * 0.25; //25% -> 3.5
//            double fifty = diabetesList.size() * 0.5; // 50% -> 7
//            double seventyFive = diabetesList.size() * 0.75; // 75% -> 10.5
//            double ninetyFive = diabetesList.size() * 0.95; // 95% -> 13.3
//
//            // 배열값 하나가 들어올 경우 여기서 0이 됨. -> smallListFunction 에서 out of index 발생 -> 핸들링 코드 필요. -> 현재 배열의 인자가 하나일 경우 그 하나를 리턴. -> 엑셀 술식대로 라면 해당 데이터 자체가 리턴 돼야함.
//            int first = excelUtils.intFunction(five);
//            int second = excelUtils.intFunction(twentyFive);
//            int third = excelUtils.intFunction(fifty);
//            int fourth = excelUtils.intFunction(seventyFive);
//            int fifth = excelUtils.intFunction(ninetyFive);
//
//            int[] percentile1 = new int[] {first+1};
//            int[] percentile2 = new int[] {second+1};
//            int[] percentile3 = new int[] {third};
//            int[] percentile3_1 = new int[] {third+1};
//            int[] percentile4 = new int[] {fourth+1};
//            int[] percentile5 = new int[] {fifth+1};
//
//            ArrayList<Double> list1 = excelUtils.smallListFunction(diabetesList,percentile3);
//            ArrayList<Double> list2 = excelUtils.smallListFunction(diabetesList,percentile3_1);
//
//            double fiftyRound = excelUtils.roundFunction((list1.get(0)+list2.get(0))/2.0 , 0); // 나눗셈시 분자 분모가 둘다 정수면 정수 나누기가 되므로 소숫점은 자동 내림됨.
//
//            PercentageDto dto = PercentageDto
//                    .builder()
//                    .five(excelUtils.smallListFunction(diabetesList, percentile1))
//                    .twentyFive(excelUtils.smallListFunction(diabetesList, percentile2))
//                    .fifty(excelUtils.returnListRoundFunction(fiftyRound, 0))
//                    .seventyFive(excelUtils.smallListFunction(diabetesList, percentile4))
//                    .ninetyFive(excelUtils.smallListFunction(diabetesList, percentile5))
//                    .build();
//            percentageDtos.add(dto);
//        }
//
//        return percentageDtos;
//    }

    // U
    @Transactional
    public void emailVerifyCode(String code, String email){
        User user = userRepository.findByEmail(email);
        user.setEmailVerifyCode(code);
        user.setEmailVerifyStartTime(LocalDateTime.now());
    }

    @Transactional
    public void emailCheck(String email){
        User user = userRepository.findByEmail(email);
        user.emailCheck(true);

    }
    //사용하지 않아서 주석처리 - 22.12.29 ykw
//    @Transactional
//    public void confirmEmail(String email){
//        User user = userRepository.findByEmail(email);
//        user.emailCheck(true);
//    }
//
//    @Transactional
//    public void updatePassword(String email, String password){
//        User user = userRepository.findByEmail(email);
//        user.passwordUpdate(password);
//        loginAttemptService.updateBlocked(getClientIP(),null);
//    }

    @Transactional
    public void updatePassword(String email, String password, String token){
        ChangePasswordToken changePasswordToken = changePasswordTokenRepository.findTopByTokenOrderByCreateTimeDesc(token);
        if(changePasswordToken==null){
            logService.log(false, ActivityKind.CHANGE_PASSWORD,"비밀번호 변경 실패, token not found", email,null,null,null);
            throw new LoginException("CHANGE_PASSWORD_TOKEN_ERROR",ErrorCode.CHANGE_PASSWORD_TOKEN_ERROR);
        }
        if(changePasswordToken.getEmail()==null || !changePasswordToken.getEmail().equalsIgnoreCase(email)){
            logService.log(false, ActivityKind.CHANGE_PASSWORD,"비밀번호 변경 실패, empty email", email,null,null,null);
            throw new LoginException("CHANGE_PASSWORD_EMAIL_ERROR",ErrorCode.CHANGE_PASSWORD_EMAIL_ERROR);
        }
        User user = userRepository.findByEmail(email);
        if(user!=null){
            user.passwordUpdate(password);
            loginAttemptService.updateBlocked(getClientIP(),email);
            tokenService.deleteAllToken(email);
            logService.log(true, ActivityKind.CHANGE_PASSWORD,"비밀번호 변경 성공", email,null,null,null);
        } else {
            Staff staff = staffRepository.findByEmail(email);
            if(staff!=null){
                staff.passwordUpdate(password);
                loginAttemptService.updateBlocked(getClientIP(),email);
                tokenService.deleteAllToken(email);
                logService.log(false, ActivityKind.CHANGE_PASSWORD,"비밀번호 변경 성공", email,null,null,null);
            } else {
                logService.log(true, ActivityKind.CHANGE_PASSWORD,"비밀번호 변경, but user not found", email,null,null,null);
            }
        }


    }

    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null){
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    @Transactional
    public void withdrawal(String email){
        User user = userRepository.findByEmail(email);
        deleteUser(user);
    }

    public void appendUserToStaff(Long userId, Long staffId, boolean recognize){
        User user = userRepository.findByIdx(userId);
        if(user==null){
            throw new LoginException("user not found", ErrorCode.NOT_FOUND);
        }
        List<UserStaffMapping> list=  userStaffMappingRepository.findAllByUserId(user.getIdx());
        if(list!=null && list.size()>=5){
            throw new LoginException("user have more than 5 staff", ErrorCode.OVER_5_STAFF);
        }
        UserStaffMapping mapping = UserStaffMapping.builder()
                .staffId(staffId)
                .userId(userId)
                .recognize(recognize)
                .build();
        userStaffMappingRepository.save(mapping);
    }

    //사용하지 않아서 주석처리 - 22.12.29 ykw
//    public void removeUserFromStaff(long userId, long target){
//        UserStaffMapping mapping = userStaffMappingRepository.findByUserIdAndStaffId(userId, target);
//        if(mapping != null){
//            userStaffMappingRepository.delete(mapping);
//        }
//    }

}
