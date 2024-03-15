package com.audrey.refeat.domain.project.controller;

import com.audrey.refeat.common.response.RestResponse;
import com.audrey.refeat.common.response.RestResponseSimple;
import com.audrey.refeat.domain.project.dto.request.*;
import com.audrey.refeat.domain.project.dto.response.*;
import com.audrey.refeat.domain.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Project", description = "Project API")
@RestController
@AllArgsConstructor
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;
    @Operation(summary = "프로젝트 생성", description = "프로젝트 생성" +
            "<br>access token을 헤더에 담아 주셔야합니다." +
            "<br>프로젝트 생성 후 생성된 프로젝트 id가 반환됩니다." +
            "<br>프로젝트는 Untitled로 생성됩니다.")
    @PostMapping
    public ResponseEntity<RestResponse<CreateProjectResponseDto>> createProject() throws Exception {
        return ResponseEntity.ok(projectService.createProject());
    }

    @Operation(summary = "프로젝트 이름 수정", description = "프로젝트 이름 수정" +
            "<br>(400, \"PE001\", \"프로젝트가 존재하지 않습니다.\")")
    @PutMapping("/{project_id}")
    public ResponseEntity<RestResponseSimple> updateProjectName(@PathVariable(name = "project_id") Long projectId,
                                                                @RequestBody UpdateProjectNameRequestDto updateProjectNameRequestDto) throws Exception {
        return ResponseEntity.ok(projectService.updateProjectName(projectId, updateProjectNameRequestDto.name()));
    }

    @Operation(summary = "프로젝트 리스트 조회", description = "프로젝트 리스트 조회" +
            "<br>access token을 헤더에 담아 주셔야합니다." +
            "<br>메인 화면에서 표시될 프로젝트 리스트 입니다.")
    @GetMapping
    public ResponseEntity<RestResponse<ProjectListWrapperDto>> getUserProject(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {

        return ResponseEntity.ok(projectService.getProjectList(PageRequest.of(page, size)));
    }

    @Operation(summary = "프로젝트 상세 조회", description = "프로젝트 상세 조회" +
            "<br>프로젝트에 포함된 document 정보의 리스트를 반환합니다" +
            "<br>프로젝트에 추가된 column 정보도 함께 반환됩니다." +
            "<br>반환된 column name으로 표를 만드시고 각각 column의 칸에 대해 column id와 document id로 [POST] /project/{project_id}/column/content에 요청하여 값을 받아 채우시면 됩니다." +
            "<br>(400, \"PE001\", \"프로젝트가 존재하지 않습니다.\")")
    @GetMapping("/{project_id}")
    public ResponseEntity<RestResponse<ProjectDetailWrapperDto>> getProjectDetail(@PathVariable(name = "project_id") Long projectId,
                                                                                 @RequestParam(required = false, defaultValue = "0") int page,
                                                                                 @RequestParam(required = false, defaultValue = "10") int size) throws Exception {
        return ResponseEntity.ok(projectService.getProjectDetail(projectId, PageRequest.of(page, size)));
    }

    @Operation(summary = "프로젝트 삭제", description = "프로젝트 삭제" +
            "<br>프로젝트 삭제 후 삭제 성공 메시지가 반환됩니다." +
            "<br>(400, \"PE001\", \"프로젝트가 존재하지 않습니다.\")")
    @DeleteMapping("/{project_id}")
    public ResponseEntity<RestResponseSimple> deleteProject(@PathVariable(name = "project_id") Long projectId) throws Exception {
        return ResponseEntity.ok(projectService.deleteProject(projectId));
    }

    @Operation(summary = "임베딩 상태 확인", description = "임베딩 상태 확인" +
            "<br>임베딩이 완료되지 않은 document가 있는지 확인합니다." +
            "<br>임베딩이 완료되지 않은 document가 있으면 true, 없으면 false를 반환합니다." +
            "<br>(400, \"PE001\", \"프로젝트가 존재하지 않습니다.\")")
    @GetMapping("/{project_id}/embedding")
    public ResponseEntity<RestResponse<EmbeddingResponseDto>> checkEmbedding(@PathVariable(name = "project_id") Long projectId) throws Exception {
        return ResponseEntity.ok(projectService.checkEmbedding(projectId));
    }


    ////////////////// user

    @Operation(summary = "프로젝트에 참여해있는 유저 정보", description = "프로젝트에 참여해있는 유저 정보" +
            "<br>프로젝트에 참여해있는 유저 정보를 모두 반환합니다." +
            "<br>userId=1 인 Refeat 유저는 항상 포함되어있습니다." +
            "<br>MVP 개인 워크스페이스 버전에서는 항상 프로젝트를 만든 유저와 Refeat만 존재합니다.")
    @GetMapping("/{project_id}/user")
    public ResponseEntity<RestResponse<List<ProjectUserResponseDto>>> getProjectUser(@PathVariable(name = "project_id") Long projectId) throws Exception {
        return ResponseEntity.ok(projectService.getProjectUser(projectId));
    }
}
