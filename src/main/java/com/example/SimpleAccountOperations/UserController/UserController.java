package com.example.SimpleAccountOperations.UserController;


import com.example.SimpleAccountOperations.UserDTO.UserDTO;
import com.example.SimpleAccountOperations.UserInfo.Role;
import com.example.SimpleAccountOperations.UserInfo.UserInfo;
import com.example.SimpleAccountOperations.UserRepository.UserRepository;
import com.example.SimpleAccountOperations.UserService.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;


@RestController
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, UserService userService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserInfo newUser) {
        if(userRepository.existsByEmail(newUser.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        newUser.setStatus(true);
        newUser.setRole(Role.USER);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        userRepository.save(newUser);
        UserDTO userDTO = new UserDTO(newUser.getFirstName(),
                newUser.getLastName(),
                newUser.getEmail(), newUser.getPhone());

        return  ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @PostMapping("/change_password")
    public ResponseEntity<String> changePassword(@RequestParam String email,
                                                 @RequestParam(required = false) String oldPassword,
                                                 @RequestParam String newPassword) {
        boolean isAdmin = userService.isCurrentUserAdmin();
        String acting = userService.getCurrentUserEmail();

        if (!isAdmin && !acting.equalsIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can change only your own password.");
        }

        UserInfo user = userService.getUserInfoByEmail(email);

        if (!isAdmin) {
            if (oldPassword == null || !passwordEncoder.matches(oldPassword, user.getPassword())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password");
            }
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return ResponseEntity.ok("Your password has been changed");
    }

    @DeleteMapping("/delete_account")
    public String deleteAccount(@RequestParam String email) {
        userRepository.delete(userRepository.getUserInfosByEmail(email));
        return "account has been deleted";
    }

    @GetMapping("/users")
    public List<UserInfo> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/search")
    public ResponseEntity<?> getUser(@RequestParam(required = false) String email,
                                     @RequestParam(required = false) String lastName) {

        if (email != null){
            UserInfo userInfo = userService.getUserInfoByEmail(email);

            if(userInfo.getStatus().equals(true)) {
                return ResponseEntity.ok(new UserDTO(userInfo.getFirstName(),
                        userInfo.getLastName(), userInfo.getEmail(),userInfo.getPhone()));
            }
            else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found!");

        }
        else if (lastName != null) {
            List<UserInfo> userInfoList = userService.findUsersByLastName(lastName);
            List<UserDTO> userDTOList = userInfoList.stream().filter(user -> Boolean.TRUE.equals(user.getStatus())).map(user -> new UserDTO(user.getFirstName(),
                    user.getLastName(), user.getEmail(), user.getPhone())).toList();

            if (userDTOList.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Users not found!");
            }
            return ResponseEntity.ok(userDTOList);
        }
            else
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide email or lastName");
    }

    @PostMapping("/user/change-status")
    public ResponseEntity<?> changeStatus(@RequestParam String targetEmail,
                                          @RequestParam(required = false) String password) {

        String actingEmail = userService.getCurrentUserEmail();
        boolean isAdmin = userService.isCurrentUserAdmin();

        UserInfo targetUser = userService.getUserInfoByEmail(targetEmail);

        if(!isAdmin) {
            if(!actingEmail.equalsIgnoreCase(targetEmail)){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong email, you can only change your status");
            }
            if(!passwordEncoder.matches(password, targetUser.getPassword())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong password");
            }
        }

        targetUser.setStatus(!Boolean.TRUE.equals(targetUser.getStatus()));

        userRepository.save(targetUser);

        String statusCode = Boolean.TRUE.equals(targetUser.getStatus()) ? "active" : "disabled";

        return ResponseEntity.ok("User account status - " + statusCode);

        }

    @GetMapping("/user/status")
    public String getStatus(@RequestParam String email) {
        UserInfo userInfo = userService.getUserInfoByEmail(email);

        return "email: " + userInfo.getEmail() + " status " + userInfo.getStatus();
    }

}
