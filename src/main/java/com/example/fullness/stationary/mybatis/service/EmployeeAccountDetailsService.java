package com.example.fullness.stationary.mybatis.service;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.security.core.GrantedAuthority; 
import org.springframework.security.core.authority.AuthorityUtils; 
import org.springframework.security.core.userdetails.UserDetails; 
import org.springframework.security.core.userdetails.UserDetailsService; 
import org.springframework.security.core.userdetails.UsernameNotFoundException; 
import org.springframework.stereotype.Service; 
import org.springframework.transaction.annotation.Transactional;
import com.example.fullness.stationary.mybatis.entity.Employee;
import com.example.fullness.stationary.mybatis.entity.EmployeeAccount;
import com.example.fullness.stationary.mybatis.repository.EmployeeAccountRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List; 
@Service 
@Transactional(readOnly=true) 
public class EmployeeAccountDetailsService implements UserDetailsService { 
    @Autowired 
    EmployeeAccountRepository employeeAcountRepository; 
    @Override 
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { 
        EmployeeAccount employeeAccount = employeeAcountRepository.findByName(username); 
        if (employeeAccount == null){ 
            throw new UsernameNotFoundException("user not found."); 
        } 
        Collection<GrantedAuthority> authorites = getAuthorities(employeeAccount); 
        return new EmployeeAccountDetails(employeeAccount,authorites); 

        
    } 
    
} 