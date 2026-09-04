package com.example.study.study;

import com.example.study.common.GlobalExceptionHandler;
import com.example.study.study.dto.StudyListResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StudyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StudyService studyService;

    @InjectMocks
    private StudyController studyController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(studyController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("TODO 12 · GET /api/studies 기본 요청 시 200 OK와 PageResponse 규격으로 반환한다 (EP-01)")
    void list_DefaultParameters_Returns200AndPageResponse() throws Exception {
        // given
        StudyListResponse item = new StudyListResponse(
                1L,
                "자바 스터디 모집",
                1L,
                "홍길동",
                5,
                2L,
                LocalDate.of(2026, 9, 30),
                "RECRUITING",
                LocalDateTime.of(2026, 9, 3, 10, 15, 30)
        );
        Page<StudyListResponse> mockPage = new PageImpl<>(List.of(item), Pageable.ofSize(10), 1);

        given(studyService.findAll(eq(null), eq(null), any(Pageable.class))).willReturn(mockPage);

        // when & then
        mockMvc.perform(get("/api/studies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("자바 스터디 모집"))
                .andExpect(jsonPath("$.content[0].writerNickname").value("홍길동"))
                .andExpect(jsonPath("$.content[0].capacity").value(5))
                .andExpect(jsonPath("$.content[0].acceptedCount").value(2))
                .andExpect(jsonPath("$.content[0].status").value("RECRUITING"));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(studyService).findAll(eq(null), eq(null), pageableCaptor.capture());

        Pageable captured = pageableCaptor.getValue();
        assertThat(captured.getPageNumber()).isEqualTo(0);
        assertThat(captured.getPageSize()).isEqualTo(10);
        assertThat(captured.getSort().getOrderFor("id")).isNotNull();
        assertThat(captured.getSort().getOrderFor("id").getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    @DisplayName("TODO 12 · GET /api/studies에 page, size, keyword, status 파라미터가 정확히 전달된다")
    void list_WithQueryParameters_PassesArgumentsToService() throws Exception {
        // given
        Page<StudyListResponse> emptyPage = new PageImpl<>(List.of(), Pageable.ofSize(5), 0);
        given(studyService.findAll(eq("자바"), eq(StudyStatus.RECRUITING), any(Pageable.class)))
                .willReturn(emptyPage);

        // when & then
        mockMvc.perform(get("/api/studies")
                        .param("page", "1")
                        .param("size", "5")
                        .param("keyword", "자바")
                        .param("status", "RECRUITING")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(studyService).findAll(eq("자바"), eq(StudyStatus.RECRUITING), pageableCaptor.capture());

        Pageable captured = pageableCaptor.getValue();
        assertThat(captured.getPageNumber()).isEqualTo(1);
        assertThat(captured.getPageSize()).isEqualTo(5);
    }
}
