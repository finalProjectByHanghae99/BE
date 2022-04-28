package com.hanghae99.finalprooject.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hanghae99.finalprooject.exception.ErrorCode;
import com.hanghae99.finalprooject.exception.PrivateException;
import com.hanghae99.finalprooject.model.Img;
import com.hanghae99.finalprooject.repository.ImgRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsS3UploadService {

    private final AmazonS3 s3Client;

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    private final ImgRepository imgRepository;

    @PostConstruct
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }

    public List<String> uploadImg(List<MultipartFile> multipartFile) {
        List<String> imgUrlList = new ArrayList<>();

        // forEach 구문을 통해 multipartFile로 넘어온 파일들 하나씩 fileNameList에 추가
        for (MultipartFile file : multipartFile) {
            String fileName = createFileName(file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try(InputStream inputStream = file.getInputStream()) {
                s3Client.putObject(new PutObjectRequest(bucket+"/post/image", fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                imgUrlList.add(s3Client.getUrl(bucket+"/post/image", fileName).toString());
            } catch(IOException e) {
                throw new PrivateException(ErrorCode.IMAGE_UPLOAD_ERROR);
            }
        }
        return imgUrlList;
    }

    // 이미지파일명 중복 방지
    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    // 파일 유효성 검사
    private String getFileExtension(String fileName) {
        if (fileName.length() == 0) {
            throw new PrivateException(ErrorCode.WRONG_INPUT_IMAGE);
        }
        ArrayList<String> fileValidate = new ArrayList<>();
        fileValidate.add(".jpg");
        fileValidate.add(".jpeg");
        fileValidate.add(".png");
        fileValidate.add(".JPG");
        fileValidate.add(".JPEG");
        fileValidate.add(".PNG");
        String idxFileName = fileName.substring(fileName.lastIndexOf("."));
        if (!fileValidate.contains(idxFileName)) {
            throw new PrivateException(ErrorCode.WRONG_IMAGE_FORMAT);
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    // 글 수정시 기존 S3 서버의 이미지 정보 삭제
    public List<String> updateImg(Long postId, List<MultipartFile> imgUrlList) {
        deleteImg(postId);
        return uploadImg(imgUrlList);
    }

//    public void deleteImg(Long postId) {
//        List<Img> lastImgList = imgRepository.findByPostId(postId);
//        log.info("수정/삭제 전 이미지 Url : " + lastImgList);
//
//        for (Img lastImg : lastImgList) {
//            if (!"".equals(lastImg.getImgUrl()) && lastImg.getImgUrl() != null) {
//                String lastImgUrl = lastImg.getImgUrl();
//                lastImgUrl = lastImgUrl.replace("https://s3.ap-northeast-2.amazonaws.com/hyemco-butket/post/image/", "");
//                boolean isExistObject = s3Client.doesObjectExist(bucket, lastImgUrl);
//                log.info("삭제한 이미지 Url : " + lastImgUrl);
//                log.info("삭제할 이미지 Url : " + lastImg.getImgUrl());
//                log.info("isExistObject : " + isExistObject);
//
//                if (isExistObject) {
//                    s3Client.deleteObject(bucket, lastImgUrl);
//                }
//            }
//            imgRepository.deleteById(lastImg.getId());
//        }
//    }

    // 이미지 삭제
    public void deleteImg(Long postId) {
        List<Img> lastImgList = imgRepository.findByPostId(postId);

        try {
            lastImgList.stream().forEach(i -> s3Client.deleteObject(new DeleteObjectRequest(bucket+"/post/image", i.getImgUrl().split("amazonaws.com/")[1])));
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
        }
    }
}