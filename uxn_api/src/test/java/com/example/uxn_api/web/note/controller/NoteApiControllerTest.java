package com.example.uxn_api.web.note.controller;

import com.example.uxn_api.service.calibration.CalibrationService;
import com.example.uxn_api.service.note.NoteService;
import com.example.uxn_api.web.calibration.dto.res.CalibrationDetailResDto;
import com.example.uxn_api.web.note.dto.req.NoteSaveReqDto;
import com.example.uxn_api.web.note.dto.res.NoteDetailResDto;
import com.example.uxn_common.global.domain.calibration.Calibration;
import com.example.uxn_common.global.domain.note.Note;
import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NoteApiController.class,
    excludeFilters = {
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcConfigurer.class),
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurerAdapter.class)})
class NoteApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    NoteService noteService;

    @Test
    @WithMockUser
    @DisplayName("note save test")
    void saveTest() throws Exception{
        long userID = 33;

        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        //실제 DB를 사용하지 않고 Test 용 Mock DB를 생성하는것을 의미한다.
        given(noteService.save(new NoteSaveReqDto("event", "lunch", "2022-11-03 09:28:59:000000", userID)))
                .willReturn(new NoteSaveReqDto("event", "lunch", "2022-11-03 09:28:59:000000", userID));

        NoteSaveReqDto saveReqDto = NoteSaveReqDto.builder()
                .title("event")
                .contents("lunch")
                .createData("2022-11-03 09:28:59:000000")
                .userid(userID)
                .build();

        Gson gson = new Gson();
        String content = gson.toJson(saveReqDto);
//        String content= objectMapper.writeValueAsString(saveReqDto);

        // andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/note/save")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
//                        .contentType(MediaType.TEXT_PLAIN)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.contents").exists())
                .andExpect(jsonPath("$.createData").exists())
                .andExpect(jsonPath("$.userid").exists())
                .andDo(print());


        // verify : 해당 객체의 메소드가 실행되었는지 체크해줌
        verify(noteService).save(new NoteSaveReqDto(
                "event",
                "lunch",
                "2022-11-03 09:28:59:000000",
                userID));
    }

    @Test
    @WithMockUser
    @DisplayName("note read test")
    void readTest() throws Exception{
        long userID = 33;
        Note note = Note
                .builder()
                .user(null)
                .contents("lunch")
                .title("event")
                .createDate(null)
                .build();

        String getUrl = String.format("/api/v1/note/read/%d", userID);

        //Mock 객체에서 특정 메소드가 실행되는 경우 실제 Return 을 줄 수 없기 때문에 아래와 같이 가정 사항을 만들어줌
        given(noteService.read(userID)).willReturn(new NoteDetailResDto(note));
        mockMvc.perform(MockMvcRequestBuilders.get(getUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.contents").exists())
                .andDo(print());

        ((NoteService) Mockito.verify(this.noteService)).read(userID);
    }
}