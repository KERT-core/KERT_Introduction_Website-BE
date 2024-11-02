package com.kert;

import com.kert.config.SecurityConfig;
import com.kert.model.Admin;
import com.kert.model.User;
import com.kert.service.AdminService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private AdminService adminService;

    private final Admin testAdmin = new Admin();
    private String testRequestBody;

    @BeforeAll
    public void setUp() {
        testAdmin.setStudentId(1L);
        testAdmin.setGeneration("test");
        testAdmin.setRole("test");
        testAdmin.setDescription("test");

        testRequestBody= """
                {
                    "student_id" : %d,
                    "generation" : "%s",
                    "role" : "%s",
                    "description": "%s"
                }
                """.formatted(testAdmin.getStudentId(), testAdmin.getGeneration(), testAdmin.getRole(), testAdmin.getDescription());
    }

    @BeforeEach
    public void setMockMvc() {
        this.mockMvc= MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @Test
    @DisplayName("get all admins with not_admin")
    public void getAllAdminsWithNotAdmin() throws Exception {
        mockMvc.perform(get("/admin")).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("get all admins with admin")
    @WithMockUser(roles = "ADMIN")
    public void getAllAdminsWithAdmin() throws Exception {
        when(adminService.getAllAdmins()).thenReturn(List.of(testAdmin));

        mockMvc.perform(get("/admin")).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].student_id").value(testAdmin.getStudentId()))
                .andExpect(jsonPath("$[0].generation").value(testAdmin.getGeneration()))
                .andExpect(jsonPath("$[0].role").value(testAdmin.getRole()))
                .andExpect(jsonPath("$[0].description").value(testAdmin.getDescription()));
    }

    @Test
    @DisplayName("get admin by id with not_admin")
    public void getAdminByIdWithNotAdmin() throws Exception {
        when(adminService.getAdminByStudentId(1L)).thenReturn(testAdmin);

        mockMvc.perform(get("/admin/1")).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("get admin by id with not_admin")
    @WithMockUser(roles = "ADMIN")
    public void getAdminByIdWithAdmin() throws Exception {
        when(adminService.getAdminByStudentId(1L)).thenReturn(testAdmin);

        mockMvc.perform(get("/admin/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.student_id").value(testAdmin.getStudentId()))
                .andExpect(jsonPath("$.generation").value(testAdmin.getGeneration()))
                .andExpect(jsonPath("$.role").value(testAdmin.getRole()))
                .andExpect(jsonPath("$.description").value(testAdmin.getDescription()));
    }

    @Test
    @DisplayName("create admin with not_admin")
    public void createAdminWithNotAdmin() throws Exception {
        when(adminService.createAdmin(testAdmin)).thenReturn(testAdmin);

        mockMvc.perform(post("/admin").contentType(MediaType.APPLICATION_JSON).content(testRequestBody)).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("create admin with admin")
    @WithMockUser(roles = "ADMIN")
    public void createAdminWithAdmin() throws Exception {
        when(adminService.createAdmin(testAdmin)).thenReturn(testAdmin);

        mockMvc.perform(post("/admin").contentType(MediaType.APPLICATION_JSON).content(testRequestBody)).andExpect(status().isOk())
                .andExpect(jsonPath("$.student_id").value(testAdmin.getStudentId()))
                .andExpect(jsonPath("$.generation").value(testAdmin.getGeneration()))
                .andExpect(jsonPath("$.role").value(testAdmin.getRole()))
                .andExpect(jsonPath("$.description").value(testAdmin.getDescription()));
    }

    @Test
    @DisplayName("update admin with not_admin")
    public void updateAdminWithNotAdmin() throws Exception {
        when(adminService.updateAdmin(1L, testAdmin)).thenReturn(testAdmin);

        mockMvc.perform(put("/admin/1").contentType(MediaType.APPLICATION_JSON).content(testRequestBody)).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("update admin with admin")
    @WithMockUser(roles = "ADMIN")
    public void updateAdminWithAdmin() throws Exception {
        when(adminService.updateAdmin(1L, testAdmin)).thenReturn(testAdmin);

        mockMvc.perform(put("/admin/1").contentType(MediaType.APPLICATION_JSON).content(testRequestBody)).andExpect(status().isOk())
                .andExpect(jsonPath("$.student_id").value(testAdmin.getStudentId()))
                .andExpect(jsonPath("$.generation").value(testAdmin.getGeneration()))
                .andExpect(jsonPath("$.role").value(testAdmin.getRole()))
                .andExpect(jsonPath("$.description").value(testAdmin.getDescription()));
    }

    @Test
    @DisplayName("delete admin with not_admin")
    public void deleteAdminWithNotAdmin() throws Exception {
        mockMvc.perform(delete("/admin/1")).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("delete admin with not_admin")
    @WithMockUser(roles = "ADMIN")
    public void deleteAdminWithAdmin() throws Exception {
        mockMvc.perform(delete("/admin/1")).andExpect(status().isNoContent());
    }
}
