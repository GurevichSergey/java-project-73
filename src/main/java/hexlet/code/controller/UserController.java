package hexlet.code.controller;


import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;


@RestController
@AllArgsConstructor
@RequestMapping("${base-url}" + USER_CONTROLLER_PATH)
public class UserController {
    public static final String USER_CONTROLLER_PATH = "/users";

    public static final String FULL_USER_CONTROLLER_PATH = "/api/users";
    public static final String ID = "/{id}";

    private static final String ONLY_OWNER_BY_ID = """
            @userRepository.findById(#id).get().getEmail() == authentication.getName()
        """;

    private final UserService userService;

    @Operation(summary = "Get all User")
    @ApiResponses(@ApiResponse(responseCode = "200", content =
        @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))))
    @GetMapping
    public List<User> getUsers() {
        return userService.getAllUsers().stream().toList();
    }

    @Operation(summary = "Return User by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User is found", content =
                @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping(ID)
    public User getUserById(
            @Parameter(description = "Id of User to be found")
            @PathVariable final long id) {
        return userService.getUserById(id);
    }

    @Operation(summary = "Create new User")
    @ApiResponses(@ApiResponse(responseCode = "201", description = "User created", content =
        @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(
            @Parameter(description = "User data to save")
            @RequestBody @Valid final UserDto userDto) {
        return userService.createNewUser(userDto);
    }


    @Operation(summary = "Change data of User")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "User changed", content =
            @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "The user with this id is not found",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden to update",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Invalid request",
                    content = @Content)})
    @PreAuthorize(ONLY_OWNER_BY_ID)
    @PutMapping(ID)
    public User updateUser(
            @Parameter(description = "Id of User data to be changed")
            @PathVariable final long id,
            @RequestBody @Valid final UserDto userDto) {
        return userService.updateUser(id, userDto);
    }

    @Operation(summary = "Delete User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The user is deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden to delete",
                    content = @Content)
    })
    @PreAuthorize(ONLY_OWNER_BY_ID)
    @DeleteMapping(ID)
    public void deleteUser(
            @Parameter(description = "Id of user to be deleted")
            @PathVariable long id) {
        userService.deleteUserById(id);
    }
}
