package com.example.authservice.mapper;

import com.example.authservice.domain.Role;
import com.example.authservice.domain.User;
import com.example.authservice.dto.UserListResponse;
import com.example.authservice.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserResponse userToResponse(User user);

    List<UserResponse> userListToResponseList(List<User> users);

    default UserListResponse userListToUserListResponse(Page<User> users) {
        List<UserResponse> userResponses = userListToResponseList(users.getContent());
        return new UserListResponse(users.getTotalElements(), userResponses);
    }

    default String map(Role role) {
        return role.getName();
    }
}
