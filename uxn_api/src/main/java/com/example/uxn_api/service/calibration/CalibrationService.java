package com.example.uxn_api.service.calibration;

import com.example.uxn_api.web.calibration.dto.req.CalibrationSaveReqDto;
import com.example.uxn_api.web.calibration.dto.res.CalibrationDetailResDto;
import com.example.uxn_api.web.calibration.dto.res.CalibrationListResDto;
import com.example.uxn_api.web.note.dto.req.NoteSaveReqDto;
import com.example.uxn_api.web.note.dto.res.NoteDetailResDto;
import com.example.uxn_api.web.note.dto.res.NoteListResDto;
import com.example.uxn_common.global.domain.calibration.Calibration;
import com.example.uxn_common.global.domain.calibration.repository.CalibrationRepository;
import com.example.uxn_common.global.domain.note.Note;
import com.example.uxn_common.global.domain.note.repository.NoteRepository;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalibrationService {

    private final CalibrationRepository calibrationRepository;
    private final UserRepository userRepository;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Transactional
    public CalibrationSaveReqDto save(CalibrationSaveReqDto reqDto){
        User user = userRepository.findByIdx(reqDto.getUserid());
        LocalDateTime time = null;
        if(reqDto.getCreateData()!=null){
            try{

                time = LocalDateTime.parse(reqDto.getCreateData(),formatter);
            }catch (Exception e){}

        }
        Calibration calibration = Calibration
                .builder()
                .user(user)
                .contents(reqDto.getContents())
                .title(reqDto.getTitle())
                .createdDate(time)
                .build();
        calibrationRepository.save(calibration);
        return reqDto;
    }

    @Transactional(readOnly = true)
    public CalibrationDetailResDto read(Long id){
        Calibration calibration = calibrationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 기록이 없습니다."));
        return new CalibrationDetailResDto(calibration);
    }

    @Transactional(readOnly = true)
    public List<CalibrationListResDto> noteList(Long userId){
        User user = userRepository.findByIdx(userId);
        List<Calibration> calibrationList = calibrationRepository.findByUser(user);
        return calibrationList.stream().map(CalibrationListResDto::new).collect(Collectors.toList());
    }

    @Transactional
    public Long update(Long id, CalibrationSaveReqDto reqDto){
        Calibration calibration = calibrationRepository.findById(id).orElseThrow();
        calibration.update(reqDto.getTitle(), reqDto.getContents());
        return id;
    }

    @Transactional
    public void delete(Long idx){
//        User user = userRepository.findByUserId(userId);
//        Note note = noteRepository.findByUserAndIdx(user, idx);
        // -> 조인이 돼 있어서 삭제가 안됨 -> 매핑되어 있는 부모 오브젝트에서 체크를 하게 되는데 여기에서는 변경사항이 없기 때문에 DB를 변경하지 않고,
        // 마찬가지로 Note 의 삭제도 이루어지지 않는다. -> delete 메소드가 아닌 deleteById 를 사용한다. or deleteInBatch 를 사용한다. -> 안지워진다 원인 ->
        // @OneToOne이나 @OneToMany에서 붙혀주는 영속성 전이 Cascade 때문에 일어난 문제였다.
        //필드에 cascade = CascadeType.ALL을 붙혀주면 그 필드와 연관된 엔티티를 persist 해주지 않아도 persist한 효과가 나면서 영속성이 된다.
        //하지만 Cascade를 사용하면 편리하긴하지만 주의해야할 점이 있다. 두가지 조건을 만족해야 사용할 수 있다.
        //1.등록 삭제 등 라이프 사이클이 똑같을 때
        //2.단일 엔티티에 완전히 종속적일때만 사용 가능하다.
        //ex)parent-child라는 연관관계를 맺고있을 때 child를 다른곳에서도 관계를 맺고있다면 사용하면 안된다.
//        noteRepository.delete(note);
        // ㅇㅇ
        calibrationRepository.deleteById(idx);

    }
}
