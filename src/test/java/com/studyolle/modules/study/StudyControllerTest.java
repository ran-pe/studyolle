package com.studyolle.modules.study;

import com.studyolle.infra.MockMvcTest;
import com.studyolle.modules.account.Account;
import com.studyolle.modules.account.AccountFactory;
import com.studyolle.modules.account.AccountRepository;
import com.studyolle.modules.account.WithAccount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
public class StudyControllerTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected StudyService studyService;
    @Autowired
    protected StudyRepository studyRepository;
    @Autowired
    protected AccountRepository accountRepository;
    @Autowired
    AccountFactory accountFactory;
    @Autowired
    StudyFactory studyFactory;

    @Test
    @WithAccount("youngran")
    @DisplayName("스터디 개설 폼 조회")
    void createStudyForm() throws Exception {
        mockMvc.perform(get("/new-study"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("studyForm"));
    }

    @Test
    @WithAccount("youngran")
    @DisplayName("스터디 개설 - 완료")
    void createStudy_success() throws Exception {
        mockMvc.perform(post("/new-study")
                .param("path", "test-path")
                .param("title", "study title")
                .param("shortDescription", "short description of a study")
                .param("fullDescription", "full description of a study")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/test-path"));
    }

    @Test
    @WithAccount("youngran")
    @DisplayName("스터디 개설 - 실패")
    void createStudy_fail() throws Exception {
        mockMvc.perform(post("/new-study")
                .param("path", "wrong path")
                .param("title", "study title")
                .param("shortDescription", "short description of a study")
                .param("fullDescription", "full description of a study")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("study/form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("studyForm"))
                .andExpect(model().attributeExists("account"));

        Study study = studyRepository.findByPath("test-path");
        assertNull(study);
    }

    @Test
    @WithAccount("youngran")
    @DisplayName("스터디 조회")
    void viewStudy() throws Exception {
        Study study = new Study();
        study.setPath("test-path");
        study.setTitle("test study");
        study.setShortDescription("short description");
        study.setFullDescription("<p>full description</p>");

        Account account = accountRepository.findByNickname("youngran");
        studyService.createNewStudy(study, account);

        mockMvc.perform(get("/study/test-path"))
                .andExpect(view().name("study/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));

    }

    @Test
    @WithAccount("youngran")
    @DisplayName("스터디 가입")
    void joinStudy() throws Exception {
        Account newAccount = accountFactory.createAccount("ranTest");
        Study study = studyFactory.createStudy("test-study", newAccount);

        mockMvc.perform(get("/study/" + study.getPath() + "/join"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/members"));

        Account account = accountRepository.findByNickname("youngran");
        assertTrue(study.getMembers().contains(account));
    }

    @Test
    @WithAccount("youngran")
    @DisplayName("스터디 탈퇴")
    void leaveStudy() throws Exception {
        Account newAccount = accountFactory.createAccount("ranTest");
        Study study = studyFactory.createStudy("test-study", newAccount);

        Account account = accountRepository.findByNickname("youngran");
        studyService.addMember(study, account);

        mockMvc.perform(get("/study/" + study.getPath() + "/leave"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/members"));
        assertFalse(study.getMembers().contains(account));
    }

}
