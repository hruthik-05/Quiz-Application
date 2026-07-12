package com.projectquiz.demo.security.services;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.projectquiz.demo.models.Role;
import com.projectquiz.demo.models.User;
import com.projectquiz.demo.repositories.UserRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String sub = oAuth2User.getAttribute("sub");

        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from Google OAuth2 provider");
        }

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            // Create a new user
            user = new User();
            String username = (name != null) ? name : email.split("@")[0];
            if (userRepository.existsByUsername(username)) {
                username = email;
            }
            user.setUsername(username);
            user.setEmail(email);
            user.setProvider("google");
            user.setProviderId(sub);
            user.setRoles(Set.of(Role.USER));
            user = userRepository.save(user);
        } else {
            // Update provider info if not set
            if (user.getProvider() == null) {
                user.setProvider("google");
                user.setProviderId(sub);
                user = userRepository.save(user);
            }
        }

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        userDetails.setAttributes(oAuth2User.getAttributes());
        return userDetails;
    }
}
