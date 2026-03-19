package SAP.Project.simple_vcs.service;

import SAP.Project.simple_vcs.entity.User;
import SAP.Project.simple_vcs.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(User user){
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        userRepository.save(user);
    }
}
