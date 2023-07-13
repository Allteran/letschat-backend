package io.allteran.letschatbackend.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import io.allteran.letschatbackend.exception.InternalException;
import io.allteran.letschatbackend.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserImageService {
    @Value("${aws.s3.bucket.name}")
    private String BUCKET_NAME;

    private final AmazonS3 s3Client;
    private final UserService userService;

    public boolean saveUserImage(String userId, MultipartFile multipartFile) {
        File file;
        try {
            String fileFormat = "." + StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
            file = convertMultipleFilesToFile(multipartFile);
            PutObjectRequest uploadRequest = new PutObjectRequest(BUCKET_NAME, userId + fileFormat, file);
            s3Client.putObject(uploadRequest);
            file.delete();
            userService.saveUserImage(userId, userId + fileFormat);
            return true;
        } catch (IOException e) {
            throw new InternalException(e.getMessage());
        }
    }

    private File convertMultipleFilesToFile(MultipartFile multipartFile) throws IOException {
        File result = new File(multipartFile.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(result)) {
            fos.write(multipartFile.getBytes());
        } catch (IOException e) {
            throw new IOException(e);
        }
        return result;
    }

    public byte[] getUserImage(String userId) {
        GetObjectRequest fileRequest = new GetObjectRequest(BUCKET_NAME, userId);
        S3Object fileFromBucket = s3Client.getObject(fileRequest);
        if(fileFromBucket == null) {
            throw new NotFoundException("User does not have an image [UserID=" + userId + "]");
        }
        S3ObjectInputStream s3inputStream = fileFromBucket.getObjectContent();
        try {
            return IOUtils.toByteArray(s3inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalException(e.getMessage());
        }
    }
}
