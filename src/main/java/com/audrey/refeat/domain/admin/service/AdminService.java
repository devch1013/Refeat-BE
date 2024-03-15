package com.audrey.refeat.domain.admin.service;

import com.audrey.refeat.domain.project.entity.Project;
import com.audrey.refeat.domain.project.entity.dao.DocumentRepository;
import com.audrey.refeat.domain.project.entity.dao.ProjectRepository;
import com.audrey.refeat.domain.user.entity.UserInfo;
import com.audrey.refeat.domain.user.entity.dao.UserInfoRepository;
import com.audrey.refeat.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final DocumentRepository documentRepository;
    private final ProjectRepository projectRepository;
    private final UserInfoRepository userInfoRepository;

    public String userList() {
        List<UserInfo> userList = userInfoRepository.findAll();
        StringBuilder result = new StringBuilder();
        result.append(" <table>" +
                "    <tr>" +
                "      <th scope=\"col\">ID</td>" +
                "      <th scope=\"col\">email</td>" +
                "      <th scope=\"col\">username</td>" +
                "    </tr>");
        for (UserInfo user : userList) {
            result.append("<tr>")
                    .append("<td>").append(user.getId()).append("</td>")
                    .append("<td>").append(user.getEmail()).append("</td>")
                    .append("<td>").append(user.getNickname()).append("</td>")
                    .append("</tr>");
        }
        result.append("</table>");
        return result.toString();
    }

    public String projectList() {
        List<Project> projectList = projectRepository.findAll();
        StringBuilder result = new StringBuilder();
        result.append(" <table>" +
                "    <tr>" +
                "      <th scope=\"col\">ID</td>" +
                "      <th scope=\"col\">name</td>" +
                "      <th scope=\"col\">link</td>" +
                "      <th scope=\"col\">userId</td>" +
                "      <th scope=\"col\">userName</td>" +

                "    </tr>");
        for (Project project : projectList) {
            result.append("<tr>")
                    .append("<td>").append(project.getId()).append("</td>")
                    .append("<td>").append(project.getName()).append("</td>")
                    .append("<td>").append("<a href=").append("\"https://refeat.ai/en/project/").append(project.getId()).append("\">Link</a>").append("</td>")
                    .append("<td>").append(project.getUser().getId()).append("</td>")
                    .append("<td>").append(project.getUser().getNickname()).append("</td>")
                    .append("</tr>");
        }
        result.append("</table>");
        return result.toString();
    }

}
