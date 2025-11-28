package com.spring.unictive.utils.minio;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@Service
public class MinioService {
    private final MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public String uploadFile(MultipartFile file) throws Exception {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }

        String presignedUrl = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .method(Method.GET)
                        .expiry(7, TimeUnit.DAYS) // URL berlaku 7 hari
                        .build()
        );

        return shortenedPresignedUrl(presignedUrl);
    }

    protected String shortenedPresignedUrl(String longUrl) throws Exception {
        String apiUrl = "https://tinyurl.com/api-create.php?url=" + longUrl;
        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
        connection.setRequestMethod("GET");

        try (Scanner scanner = new Scanner(connection.getInputStream())) {
            return scanner.hasNext() ? scanner.nextLine() : longUrl; // Return shortened URL or original if failed
        }
    }
}
