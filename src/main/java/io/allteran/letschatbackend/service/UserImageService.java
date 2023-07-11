package io.allteran.letschatbackend.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import io.allteran.letschatbackend.exception.InternalException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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

    public boolean uploadUserImage(String userId, MultipartFile multipartFile) {
        File file;
        try {
            file = convertMultipleFilesToFile(multipartFile);
            PutObjectRequest uploadRequest = new PutObjectRequest(BUCKET_NAME, userId, file);
            s3Client.putObject(uploadRequest);
            file.delete();
            return true;
        } catch (IOException e) {
            throw new InternalException(e.getMessage());
        }
    }

    private File convertMultipleFilesToFile(MultipartFile multipartFile) throws IOException {
        File result = new File(multipartFile.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(result)) {
;           fos.write(multipartFile.getBytes());
        } catch (IOException e) {
            throw new IOException(e);
        }
        return result;
    }
}
