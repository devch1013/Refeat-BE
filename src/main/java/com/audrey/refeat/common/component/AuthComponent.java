package com.audrey.refeat.common.component;

import com.audrey.refeat.common.exception.CustomException;
import com.audrey.refeat.common.exception.ErrorCode;
import com.audrey.refeat.domain.project.entity.Project;
import com.audrey.refeat.domain.project.entity.dao.ProjectRepository;
import com.audrey.refeat.domain.project.exception.ProjectNotExistException;
import com.audrey.refeat.domain.user.component.JwtComponent;
import com.audrey.refeat.domain.user.dto.JwtUser;
import com.audrey.refeat.domain.user.entity.Authority;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthComponent {
    private final ProjectRepository projectRepository;
    public Project getAuthProject(Long projectId) throws Exception {
        Project project = projectRepository.findById(projectId).orElseThrow(ProjectNotExistException::new);
        JwtUser jwtUser = JwtComponent.getUserInfo();
        if (jwtUser.role() == Authority.ADMIN){
            return project;
        }
        if (!project.getUser().getId().equals(jwtUser.id())){
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        return project;
    }

    public void checkAuth(Project project) throws Exception {
        JwtUser jwtUser = JwtComponent.getUserInfo();
        if (jwtUser.role() == Authority.ADMIN){
            return;
        }
        if (!project.getUser().getId().equals(jwtUser.id())){
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }
}
