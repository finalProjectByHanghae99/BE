package com.hanghae99.finalprooject.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hanghae99.finalprooject.dto.ImgDto;
import com.hanghae99.finalprooject.exception.ErrorCode;
import com.hanghae99.finalprooject.exception.PrivateException;
import com.hanghae99.finalprooject.model.Img;
import com.hanghae99.finalprooject.repository.ImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AwsS3UploadService {

    private final S3Service s3Service;

//    @Value("${cloud.aws.credentials.accessKey}")
//    private String accessKey;
//
//    @Value("${cloud.aws.credentials.secretKey}")
//    private String secretKey;
//
//    @Value("${cloud.aws.s3.bucket}")
//    private String bucket;
//
//    @Value("${cloud.aws.region.static}")
//    private String region;
//
//    @PostConstruct
//    public AmazonS3Client amazonS3Client() {
//        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
//        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
//                .withRegion(region)
//                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
//                .build();
//    }

    public ImgDto uploadImage(MultipartFile file) throws IOException {
        String filename = createFileName(file.getOriginalFilename());

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        InputStream inputStream = file.getInputStream();

        s3Service.uploadFile(inputStream,objectMetadata,filename);

        return ImgDto.builder()
                .imgUrl(s3Service.getFileUrl(filename))
                .build();

    }

    // 이미지 파일명 중복 방지를 위해  UUID로 랜덤 생성
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
}