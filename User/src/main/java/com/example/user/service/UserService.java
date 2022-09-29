package com.example.user.service;


import com.example.user.dto.LoginDTO;
import com.example.user.dto.UserDTO;
import com.example.user.email.EmailService;
import com.example.user.entity.User;
import com.example.user.exception.CustomException;
import com.example.user.repository.UserRepository;
import com.example.user.util.TokenUtility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;


@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    TokenUtility tokenUtility;

    @Autowired
    private RestTemplate restTemplate;

    private static final String createuser = "http://book-user-server/user/add";

    //add user data into table by using post method
    public  String addData(UserDTO userDTO) {
        User userData = new User(userDTO);
        userRepository.save(userData);
        String token = tokenUtility.createToken(userData.getUserid());
        emailService.sendEmail(userDTO.getEmail(), "User created", "Hello you have successfully sign-up " + token);
        return token;

//        User newUser = new User(userDTO);
//        restTemplate.postForEntity(createuser, newUser, User.class);
//        return  userRepository.save(newUser);
    }

    //login with id and pass
    public User loginUser(LoginDTO loginDTO) {
        Optional<User> login = userRepository.findByLoginid(loginDTO.getLoginid());
        if (loginDTO.getLoginid().equals(login.get().getLoginid()) && loginDTO.getPassword().equals(login.get().getPassword())) {
            //rabbitTemplate.convertAndSend(MessagingConfig.EXCHANGE,MessagingConfig.ROUTINGKEY);
            System.out.println("Login Successfull..!");
            return login.get();
        } else {
            throw new CustomException("Login ID and Password is wrong");
        }
    }

    public Optional<User> getBytId(int id) {
        if(userRepository.findById(id).isPresent()){
            return userRepository.findById(id);
        }else
            throw new CustomException("User Id not found..!");
    }

    //show all data in the DB post method using
    public List<User> listAll() {
        if(userRepository.findAll().isEmpty()){
            throw new CustomException("DB is Empty..!");
        }else
            return userRepository.findAll();
    }

    //delete by id from table
    public String deleteById(int id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
            return "User ID: " + id + " is Deleted Successfully!!";
        } else throw new CustomException("User ID not found..!");
    }

    //update by id
    public User editById(User userData, int id) {
        if (userRepository.findById(id).isPresent()) {
            User newData = new User(id, userData);
            return userRepository.save(newData);
        }else
            throw new CustomException("User id not found..!");
    }

}
