package com.kert;

import com.kert.config.SecurityConfig;
import com.kert.config.SecurityUser;
import com.kert.config.SecurityUserService;
import com.kert.model.Admin;
import com.kert.model.Password;
import com.kert.model.User;
import com.kert.service.AdminService;
import com.kert.service.UserService;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private UserService userService;

    @MockBean
    private SecurityUserService securityUserService;

    @MockBean
    private AdminService adminService;

    private final User testUser = new User();
    private final User testAdmin = new User();
    private String adminJwtToken;
    private String userJwtToken;

    @BeforeEach
    public void setMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @BeforeAll
    public void setUp() {
        testUser.setName("testUser");
        testUser.setEmail("testUser@gmail.com");
        testUser.setProfilePicture("test");
        testUser.setStudentId(1L);
        testUser.setGeneration("test");
        testUser.setMajor("test");

        testAdmin.setName("testAdmin");
        testAdmin.setEmail("testAdmin@gmail.com");
        testAdmin.setProfilePicture("test");
        testAdmin.setStudentId(0L);
        testAdmin.setGeneration("test");
        testAdmin.setMajor("test");

        //generate test jwt token

        long EXPIRATION_TIME = 900000;
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        SecretKey testKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(Dotenv.load().get("JWT_SECRET")));

        Claims userClaims =Jwts.claims().setSubject(Long.toString(testUser.getStudentId()));
        Set<GrantedAuthority> userAuthorities = new HashSet<>();
        userAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        userClaims.put("roles", userAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        userJwtToken = Jwts.builder().setClaims(userClaims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(testKey)
                .compact();

        Claims adminClaims = Jwts.claims().setSubject(Long.toString(testAdmin.getStudentId()));
        Set<GrantedAuthority> adminAuthorities = new HashSet<>();
        adminAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        adminClaims.put("roles", adminAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        adminJwtToken = Jwts.builder().setClaims(adminClaims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(testKey)
                .compact();
    }

    @Test
    @DisplayName("get all users with not_admin")
    public void getAllUsersWithNotAdmin() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(testUser));

        mockMvc.perform(get("/users")).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("get all users with admin")
    public void getAllUsersWithAdmin() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(testUser));
        when(securityUserService.loadUserById(testAdmin.getStudentId())).thenReturn(new SecurityUser(testAdmin, new Admin(), new Password(), Set.of()));
        when(adminService.getAdminByStudentId(testAdmin.getStudentId())).thenReturn(new Admin());

        mockMvc.perform(get("/users").header("Authorization", "Bearer " + adminJwtToken)).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].student_id").value(testUser.getStudentId()))
                .andExpect(jsonPath("$[0].name").value(testUser.getName()))
                .andExpect(jsonPath("$[0].email").value(testUser.getEmail()))
                .andExpect(jsonPath("$[0].profile_picture").value(testUser.getProfilePicture()))
                .andExpect(jsonPath("$[0].generation").value(testUser.getGeneration()))
                .andExpect(jsonPath("$[0].major").value(testUser.getMajor()));
    }

    @Test
    @DisplayName("get user by id with self")
    public void getUserByIdWithSelf() throws Exception {
        when(userService.getUserById(testUser.getStudentId())).thenReturn(testUser);

        mockMvc.perform(get("/users/{studentId}", testUser.getStudentId()).header("Authorization", "Bearer " + userJwtToken)).andExpect(status().isOk());
    }

    @Test
    @DisplayName("get user by id with another")
    public void getUserByIdWithAnother() throws Exception {
        when(userService.getUserById(2L)).thenReturn(new User());

        mockMvc.perform(get("/users/2").header("Authorization", "Bearer " + userJwtToken)).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("get user by id with admin")
    public void getUserByIdWithAdmin() throws Exception {
        when(userService.getUserById(testUser.getStudentId())).thenReturn(testUser);
        when(securityUserService.loadUserById(testAdmin.getStudentId())).thenReturn(new SecurityUser(testAdmin, new Admin(), new Password(), Set.of()));
        when(adminService.getAdminByStudentId(testAdmin.getStudentId())).thenReturn(new Admin());

        mockMvc.perform(get("/users/{studentId}", testUser.getStudentId()).header("Authorization", "Bearer " + adminJwtToken)).andExpect(status().isOk())
                .andExpect(jsonPath("$.student_id").value(testUser.getStudentId()))
                .andExpect(jsonPath("$.name").value(testUser.getName()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.profile_picture").value(testUser.getProfilePicture()))
                .andExpect(jsonPath("$.generation").value(testUser.getGeneration()))
                .andExpect(jsonPath("$.major").value(testUser.getMajor()));
    }

    @Test
    @DisplayName("update user with self")
    public void updateUserByIdWithSelf() throws Exception {
        when(userService.updateUser(testUser.getStudentId(), testUser)).thenReturn(testUser);

        //request body의 항목이 testUser와 정확히 일치해야 위의 mocking이 적용되어 여기선 student_id까지 body에 추가함
        String requestBody= """
                {
                    "name": "%s",
                    "email": "%s",
                    "profile_picture": "%s",
                    "student_id": "%s",
                    "generation": "%s",
                    "major": "%s"
                }
                """.formatted(testUser.getName(),testUser.getEmail(),testUser.getProfilePicture(),testUser.getStudentId(),testUser.getGeneration(),testUser.getMajor());

        mockMvc.perform(put("/users/{studentId}", testUser.getStudentId()).header("Authorization", "Bearer " + userJwtToken).contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().isOk());
    }

    @Test
    @DisplayName("update user with another")
    public void updateUserByIdWithAnother() throws Exception {
        //request body의 항목이 testUser와 정확히 일치해야 위의 mocking이 적용되어 여기선 student_id까지 body에 추가함
        String requestBody= """
                {
                    "name": "%s",
                    "email": "%s",
                    "profile_picture": "%s",
                    "student_id": "%s",
                    "generation": "%s",
                    "major": "%s"
                }
                """.formatted(testUser.getName(),testUser.getEmail(),testUser.getProfilePicture(),testUser.getStudentId(),testUser.getGeneration(),testUser.getMajor());

        mockMvc.perform(put("/users/2").header("Authorization", "Bearer " + userJwtToken).contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("update user with admin")
    public void updateUserByIdWithAdmin() throws Exception {
        when(userService.updateUser(testUser.getStudentId(), testUser)).thenReturn(testUser);
        when(securityUserService.loadUserById(testAdmin.getStudentId())).thenReturn(new SecurityUser(testAdmin, new Admin(), new Password(), Set.of()));
        when(adminService.getAdminByStudentId(testAdmin.getStudentId())).thenReturn(new Admin());
        
        //request body의 항목이 testUser와 정확히 일치해야 위의 mocking이 적용되어 여기선 student_id까지 body에 추가함
        String requestBody= """
                {
                    "name": "%s",
                    "email": "%s",
                    "profile_picture": "%s",
                    "student_id": "%s",
                    "generation": "%s",
                    "major": "%s"
                }
                """.formatted(testUser.getName(),testUser.getEmail(),testUser.getProfilePicture(),testUser.getStudentId(),testUser.getGeneration(),testUser.getMajor());

        mockMvc.perform(put("/users/{studentId}", testUser.getStudentId()).header("Authorization", "Bearer " + adminJwtToken).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("delete user with admin")
    public void deleteUserByIdWithAdmin() throws Exception {
        when(userService.getUserById(testUser.getStudentId())).thenReturn(testUser);
        when(securityUserService.loadUserById(testAdmin.getStudentId())).thenReturn(new SecurityUser(testAdmin, new Admin(), new Password(), Set.of()));
        when(adminService.getAdminByStudentId(testAdmin.getStudentId())).thenReturn(new Admin());

        mockMvc.perform(delete("/users/{studentId}", testUser.getStudentId()).header("Authorization", "Bearer " + adminJwtToken)).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("delete user with self")
    public void deleteUserByIdWithSelf() throws Exception {
        when(userService.getUserById(testUser.getStudentId())).thenReturn(testUser);

        mockMvc.perform(delete("/users/{studentId}", testUser.getStudentId()).header("Authorization", "Bearer " + userJwtToken)).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("delete user with another")
    public void deleteUserByIdWithAnother() throws Exception {
        when(userService.getUserById(2L)).thenReturn(new User());

        mockMvc.perform(delete("/users/2", testUser.getStudentId()).header("Authorization", "Bearer " + userJwtToken)).andExpect(status().isForbidden());
    }
}
