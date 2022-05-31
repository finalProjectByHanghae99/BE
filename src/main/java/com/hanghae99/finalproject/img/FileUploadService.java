package com.hanghae99.finalproject.img;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.hanghae99.finalproject.exception.CustomException;
import com.hanghae99.finalproject.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final AwsS3UploadService s3UploadService;

    // 원본 + 리사이징 이미지 업로드
    public ImgDto uploadImage(MultipartFile file, String dirName) throws IOException {

        // 파일명 생성(중복방지를 위해 UUID)
        String filename = dirName + "/" + createFileName(file.getOriginalFilename());

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        InputStream inputStream = file.getInputStream();

        // 원본 이미지 업로드
        s3UploadService.uploadFile(inputStream,objectMetadata,filename);

        if(Objects.equals(dirName,"post"))
        {
            // 리사이징 이미지 파일 이름
            String resizedFilename = filename.replace(dirName, dirName + "-resized");

            // 사진 크기 줄이기
            BufferedImage resizedImage = Thumbnails.of(file.getInputStream())
                    .outputQuality(1.0f)
                    .scale(0.75)
                    .asBufferedImage();

            // 사진 확장자 확인
            String extension = checkImageType(file);

            // 이미지 쓰기
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, extension, os);

            // 에러날 경우 확장자 "png"로 처리
            ByteArrayOutputStream uploadOs = new ByteArrayOutputStream();
            if (os.size() == 0) {
                ImageIO.write(resizedImage, "png", uploadOs);
            } else {
                uploadOs = os;
            }

            InputStream is = new ByteArrayInputStream(uploadOs.toByteArray());
            ObjectMetadata ob = new ObjectMetadata();
            ob.setContentType(extension);
            ob.setContentLength(uploadOs.size());

            // 리사이징 이미지 업로드
            s3UploadService.uploadFile(is,ob,resizedFilename);

        }
        // 원본 이미지 정보만 db에 저장
        return ImgDto.builder()
                .imgName(filename)
                .imgUrl(s3UploadService.getFileUrl(filename))
                .build();
    }

    // 이미지 파일명 중복 방지를 위해  UUID로 랜덤 생성
    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    // 파일 유효성 검사 - 확장자
    private String getFileExtension(String fileName) {
        ArrayList<String> fileValidate = new ArrayList<>();
        fileValidate.add(".jpg");
        fileValidate.add(".jpeg");
        fileValidate.add(".png");
        fileValidate.add(".JPG");
        fileValidate.add(".JPEG");
        fileValidate.add(".PNG");
        String idxFileName = fileName.substring(fileName.lastIndexOf("."));
        if (!fileValidate.contains(idxFileName)) {
            throw new CustomException(ErrorCode.WRONG_IMAGE_FORMAT);
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    // 이미지 확장자 분리 - 라사이징 이미지용
    private String checkImageType(MultipartFile file) throws IOException {
        Tika tika = new Tika();
        String mimeType = tika.detect(file.getInputStream());

        if (!mimeType.startsWith("image/")) {
            throw new CustomException(ErrorCode.WRONG_IMAGE_FORMAT);
        }

        return mimeType.substring(6);
    }
}