package com.hanghae99.finalprooject.security;

import com.hanghae99.finalprooject.model.User;
import com.hanghae99.finalprooject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  @Autowired
  public UserDetailsServiceImpl(UserRepository userRepository) {

    this.userRepository = userRepository;
  }

  public UserDetailsImpl loadUserByUsername(String email) throws UsernameNotFoundException {

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("Can't find " + email));

    return new UserDetailsImpl(user);
  }
}