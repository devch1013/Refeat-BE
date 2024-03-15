package com.audrey.refeat.domain.project.service;

import com.audrey.refeat.common.component.AuthComponent;
import com.audrey.refeat.common.response.RestResponse;
import com.audrey.refeat.common.response.RestResponseSimple;
import com.audrey.refeat.common.s3.S3Component;
import com.audrey.refeat.domain.project.dto.response.*;
import com.audrey.refeat.domain.project.entity.ColumnTitle;
import com.audrey.refeat.domain.project.entity.Document;
import com.audrey.refeat.domain.project.entity.Project;
import com.audrey.refeat.domain.project.entity.dao.ColumnTitleRepository;
import com.audrey.refeat.domain.project.entity.dao.DocumentRepository;
import com.audrey.refeat.domain.project.entity.dao.ProjectRepository;
import com.audrey.refeat.domain.project.entity.enums.DocumentStatus;
import com.audrey.refeat.domain.project.exception.ProjectNotExistException;
import com.audrey.refeat.domain.user.component.JwtComponent;
import com.audrey.refeat.domain.user.dto.JwtUser;
import com.audrey.refeat.domain.user.entity.UserInfo;
import com.audrey.refeat.domain.user.entity.dao.UserInfoRepository;
import com.audrey.refeat.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private final UserInfoRepository userInfoRepository;
    private final ProjectRepository projectRepository;
    private final DocumentRepository documentRepository;
    private final ColumnTitleRepository columnTitleRepository;
    private final AuthComponent authComponent;
    private final S3Component s3Component;


    ///////////// Project
    public RestResponse<CreateProjectResponseDto> createProject() throws Exception {
        JwtUser jwtUser = JwtComponent.getUserInfo();
        UserInfo userInfo = userInfoRepository.findById(jwtUser.id()).orElseThrow(UserNotFoundException::new);
        Project project = projectRepository.save(Project.builder().user(userInfo).build());
        return RestResponse.ok(new CreateProjectResponseDto(project.getId()));
    }

    public RestResponseSimple updateProjectName(Long projectId, String projectName) throws Exception {
        Project project = authComponent.getAuthProject(projectId);
        project.updateName(projectName);
        project.updateTime();
        projectRepository.save(project);
        return RestResponseSimple.success();
    }

    public RestResponse<ProjectListWrapperDto> getProjectList(Pageable pageable) {
        Page<Project> projectList = projectRepository.findByUser_IdAndIsDeletedFalseOrderByUpdatedAtDesc(JwtComponent.getUserInfo().id(), pageable);
        return RestResponse.ok( new ProjectListWrapperDto(projectList.hasNext(), UserProjectListResponseDto.getDtoFromList(projectList.getContent())));
    }

    public RestResponse<ProjectDetailWrapperDto> getProjectDetail(Long projectId, Pageable pageable) throws Exception {
        Project project = authComponent.getAuthProject(projectId);
        Page<Document> documentList = documentRepository.findByProjectOrderByCreatedAtDescPaging(project, pageable);
        List<ColumnTitle> columnTitleList = columnTitleRepository.findByProjectAndDeletedFalseOrderByCreatedAtAsc(project);
        columnTitleList = columnTitleList.reversed();
        return RestResponse.ok(new ProjectDetailWrapperDto(
                project.getId(),
                project.getName(),
                documentList.hasNext(),
                ProjectDetailDto.fromList(documentList.getContent(), s3Component.getEndpoint()),
                AddColumnResponseDto.fromList(columnTitleList)));
    }

    public RestResponseSimple deleteProject(Long projectId) throws  Exception {
        Project project = authComponent.getAuthProject(projectId);
        project.delete();
        projectRepository.save(project);
        return RestResponseSimple.success();
    }

    public RestResponse<EmbeddingResponseDto> checkEmbedding(Long projectId) throws Exception {
        Project project = authComponent.getAuthProject(projectId);
        List<Document> documentList = documentRepository.findByProject(project);
        for (Document document : documentList) {
            if (document.getEmbeddingDone() != DocumentStatus.SUCCESS) {
                return RestResponse.ok(new EmbeddingResponseDto(true));
            }
        }
        return RestResponse.ok(new EmbeddingResponseDto(false));
    }

    ///////////////// user

    public RestResponse<List<ProjectUserResponseDto>> getProjectUser(Long projectId) throws Exception {
        Project project = projectRepository.findById(projectId).orElseThrow(ProjectNotExistException::new);
        UserInfo refeatUser = userInfoRepository.findById(1L).orElseThrow();
        List<UserInfo> userInfoList = List.of(refeatUser, project.getUser());
        return RestResponse.ok(ProjectUserResponseDto.fromUserList(userInfoList));
    }




}
