package it.unibz.digidojo.entitymanagerservice.user.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.unibz.digidojo.entitymanagerservice.user.domain.usecases.LoginUser;
import it.unibz.digidojo.entitymanagerservice.user.domain.usecases.ManageUser;
import it.unibz.digidojo.entitymanagerservice.user.domain.usecases.SearchUser;
import it.unibz.digidojo.entitymanagerservice.user.domain.User;
import it.unibz.digidojo.sharedmodel.request.UpdateUserRequest;
import it.unibz.digidojo.sharedmodel.request.UserRequest;

@RestController
@RequestMapping(path = "/v1/user")
public class UserController {
    private final ManageUser manageUser;
    private final SearchUser searchUser;
    private final LoginUser loginUser;

    @Autowired
    public UserController(ManageUser manageUser, SearchUser searchUser, LoginUser loginUser) {
        this.manageUser = manageUser;
        this.searchUser = searchUser;
        this.loginUser = loginUser;
    }

    @PostMapping
    public User createUser(@Validated @RequestBody UserRequest request) {
        return manageUser.createUser(
                request.name(),
                request.emailAddress(),
                loginUser.hashPassword(request.password(), request.emailAddress())
        );
    }

    @PostMapping("/login")
    public User loginUser(@RequestBody UserRequest request) {
        User user = searchUser.findByMailAddress(request.emailAddress());
        return loginUser.verifyPassword(user, request.password());
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable("id") Long id) {
        return searchUser.findById(id);
    }

    @GetMapping("/email/{emailAddress}")
    public User findByEMail(@PathVariable("emailAddress") String mailAddresses) {
        return searchUser.findByMailAddress(mailAddresses);
    }

    @PatchMapping("/{id}")
    public User updateUser(@PathVariable("id") Long id, @Validated @RequestBody UpdateUserRequest request) {
        User user = searchUser.findById(id), updatedUser = null;
        user = loginUser.verifyPassword(user, request.currentPassword());

        if (request.emailAddress() != null) {
            updatedUser = manageUser.updateUserMail(user.getEmailAddress(), request.emailAddress());
        }

        if (request.password() != null) {
            updatedUser = manageUser.updatePassword(user, loginUser.hashPassword(
                            request.password(),
                            updatedUser != null ? updatedUser.getEmailAddress() : user.getEmailAddress()
                    )
            );
        }

        if (updatedUser == null) {
            throw new IllegalArgumentException("The request must have at least one field to change");
        }

        return updatedUser;
    }

    @DeleteMapping("/{id}")
    public boolean deleteUser(@PathVariable("id") Long id) {
        User user = searchUser.findById(id);
        return manageUser.deleteUser(user.getEmailAddress());
    }
}