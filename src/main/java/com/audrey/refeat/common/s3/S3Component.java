package com.audrey.refeat.common.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.audrey.refeat.common.s3.exception.FileUploadFailException;
import com.audrey.refeat.domain.project.entity.enums.DocumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Component
public class S3Component {
    @Autowired
    private AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.s3.endpoint}")
    private String s3Endpoint;

    /**
     * 로컬 경로에 저장
     */
    public void uploadFileS3(String filePath, MultipartFile multipartFile) throws FileUploadFailException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, filePath, inputStream, objectMetadata));
        } catch (IOException e) {
            throw new FileUploadFailException();
        }
    }

    public void removeFileS3(String filePath) {
        try{
            amazonS3Client.deleteObject(bucket, filePath);
        } catch (Exception ignored) {
        }
    }

    public void uploadLocalPDFFile(String filePath, String localPath) throws Exception {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("application/pdf");
        objectMetadata.setContentLength(Files.size(Paths.get(localPath)));
        try {
            amazonS3Client.putObject(bucket, filePath, new File(localPath));
        } catch (Exception e) {
            throw new FileUploadFailException();
        }
        // temp 데이터 삭제
        new File(localPath).delete();
    }

    public String getEndpoint(String path) {
        return s3Endpoint + path;
    }

    public String getEndpoint() {
        return s3Endpoint;
    }

    public String getTextFromHtml(UUID documentId){
        return amazonS3Client.getObjectAsString(bucket, "html/" + documentId + ".html");
    }

    public String getDefaultFavicon(DocumentType type) {
        return switch (type) {
            case PDF -> "images/pdf_image.png";
            case WEB -> "images/web_image.png";
            default -> "";
        };
    }


    /**
     * S3로 업로드
     *
     * @param uploadFile : 업로드할 파일
     * @param fileName   : 업로드할 파일 이름
     * @return 업로드 경로
     */
    public String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(
                CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    /**
     * S3에 있는 파일 삭제
     * 영어 파일만 삭제 가능 -> 한글 이름 파일은 안됨
     */
    public void deleteS3(String filePath) throws Exception {
        try {
            String key = filePath.substring(56); // 폴더/파일.확장자

            try {
                amazonS3Client.deleteObject(bucket, key);
            } catch (AmazonServiceException e) {
//                log.info(e.getErrorMessage());
            }

        } catch (Exception exception) {
//            log.info(exception.getMessage());
        }
//        log.info("[S3Uploader] : S3에 있는 파일 삭제");
    }

    /**
     * 로컬에 저장된 파일 지우기
     *
     * @param targetFile : 저장된 파일
     */
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
//            log.info("[파일 업로드] : 파일 삭제 성공");
            return;
        }
//        log.info("[파일 업로드] : 파일 삭제 실패");
    }

    /**
     * 로컬에 파일 업로드 및 변환
     *
     * @param file : 업로드할 파일
     */
    private Optional<File> convert(MultipartFile file) throws IOException {
        // 로컬에서 저장할 파일 경로 : user.dir => 현재 디렉토리 기준
        String dirPath = System.getProperty("user.dir") + "/" + file.getOriginalFilename();
        File convertFile = new File(dirPath);

        if (convertFile.createNewFile()) {
            // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }
}
