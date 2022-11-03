package com.miniProjet.projet.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.miniProjet.projet.models.User;
import com.miniProjet.projet.repo.UserRepo;
import com.miniProjet.projet.respo.UserRes;
import com.miniProjet.projet.respo.UsersSaveJson;
import com.miniProjet.projet.service.UserService;
import com.miniProjet.projet.tools.FileUploadUtil;
import com.miniProjet.projet.tools.Tools;
import com.miniProjet.projet.tools.UserJson;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.simple.parser.JSONParser;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.persistence.Embeddable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepo userRepo;

    JSONParser jsonParser = new JSONParser();


    @Operation(summary = "This is to generate and download a number of users in json",operationId = "/users/generate")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", content = {@Content(schema =  @Schema(implementation = UserJson.class))},description = "accessToken not required",useReturnTypeSchema = false)})
    @GetMapping("/users/generate")
    public ResponseEntity<byte[]>generate(@RequestParam int count){

        List<UserJson> u = new ArrayList<>();

        for(int i = 0; i < count ; i++){

            Faker fake = new Faker();
            UserJson o = new UserJson();
            o.setFirstName(fake.name().firstName());
            o.setLastName(fake.name().lastName());
            o.setBirthDate(fake.date().birthday());
            o.setCity(fake.address().city());
            o.setCountry(fake.address().country());
            o.setAvatar(fake.avatar().toString());
            o.setCompany(fake.company().name());
            o.setJobPosition(fake.job().position());
            o.setMobile(fake.phoneNumber().phoneNumber());
            o.setUsername(fake.name().username());
            o.setEmail(fake.internet().emailAddress());
            o.setPassword(new Tools().generateRandomPassword(new Tools().RandomFrom6to10()));
            o.setRoles(new Tools().RandomAdminAndUser());

            u.add(o);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;

        try {
            json = objectMapper.writeValueAsString(u);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        byte[] isr = json.getBytes();
        String fileCode = RandomStringUtils.randomAlphanumeric(16);
        String fileName = fileCode + "Users.json";
        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentLength(isr.length);
        respHeaders.setContentType(new MediaType("text", "json"));
        respHeaders.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        respHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        return new ResponseEntity<byte[]>(isr, respHeaders, HttpStatus.OK);


    }


    @Operation(summary = "From here can the {admin/user} access their profiles",operationId = "/users/me")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", content = {@Content(mediaType =  "application/json")},description = "accessToken required",useReturnTypeSchema = false)})
    @GetMapping("/users/me")
    public String myProfile(HttpServletRequest request, HttpServletResponse response){

        String authzHeader = request.getHeader(AUTHORIZATION);
        String token = authzHeader.substring("Bearer ".length());
        Algorithm algorithm = Algorithm.HMAC256("codefortest".getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);

        String username = decodedJWT.getSubject();

        return "The profile of user: " + username ;
    }


    @Operation(summary = "From here can just and only {admin} access all profiles including his",operationId = "/users/{User}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", content = {@Content(mediaType =  "application/json")},description = "accessToken required",useReturnTypeSchema = false)})
    @GetMapping("/users/{data}")
    public String adminProfile(@PathVariable(required=false,name="data") String data){

        return "The profile of user: " + data ;
    }


    @Operation(summary = "This to import a json file and stock it in the DB as users",operationId = "/users/batch")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", content = {@Content(mediaType =  "application/json")},description = "accessToken not required",useReturnTypeSchema = false)})
    @Async
    @PostMapping("/users/batch")
    public ResponseEntity<UsersSaveJson> uploadFile(@RequestParam("file") MultipartFile multipartFile) throws IOException, InterruptedException {


        UsersSaveJson response = new UsersSaveJson();
        response.setNotImported(0);
        response.setNumberOfRecords(0);
        response.setSuccessfullyImported(0);


        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<UserRes>> typeReference = new TypeReference<List<UserRes>>(){};

        InputStream inputStream = multipartFile.getInputStream();

        try {
            List<UserRes> users = mapper.readValue(inputStream,typeReference);
            response.setNumberOfRecords(users.size());

            users.forEach(userRes -> {

                User userRepoByUsername = userRepo.findByUsername(userRes.getUsername());
                User userRepoByEmail = userRepo.findUserByEmail(userRes.getEmail());

                if(userRepoByUsername == null && userRepoByEmail == null){


                    response.setSuccessfullyImported(response.getSuccessfullyImported()+1);

                    ArrayList<String> roles = new ArrayList<>();
                    roles.add(userRes.getRoles());

                    userService.saveUser( new User(
                            null,
                            userRes.getFirstName(),
                            userRes.getLastName(),
                            new Date(userRes.getBirthDate()),
                            userRes.getCity(),
                            userRes.getCountry(),
                            userRes.getAvatar(),
                            userRes.getCompany(),
                            userRes.getJobPosition(),
                            userRes.getMobile(),
                            userRes.getUsername(),
                            userRes.getEmail(),
                            userRes.getPassword(),
                            new ArrayList<>()));

                    userService.assignRoleToUser(userRes.getUsername(), userRes.getRoles());
                }else
                {
                    response.setNotImported(response.getNotImported()+1);
                }
            });

            System.out.println("Users Saved!");
        } catch (IOException e){
            System.out.println("Unable to save users: " + e.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }





}
