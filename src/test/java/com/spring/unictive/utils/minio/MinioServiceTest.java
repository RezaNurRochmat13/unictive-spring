package com.spring.unictive.utils.minio;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MinioServiceTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private MinioService minioService;

    @BeforeEach
    void setUp() {
        // Inject value manually
        ReflectionTestUtils.setField(minioService, "bucketName", "test-bucket");
    }

    @Test
    void testUploadFile_shouldReturnShortenedUrl() throws Exception {
        // Mock file
        String fileName = "test.txt";
        String contentType = "text/plain";
        byte[] content = "Hello, World!".getBytes();
        InputStream inputStream = new ByteArrayInputStream(content);

        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getSize()).thenReturn((long) content.length);
        when(file.getContentType()).thenReturn(contentType);
        when(file.getInputStream()).thenReturn(inputStream);

        // Mock MinIO presigned URL
        String fakePresignedUrl = "https://example.com/fake-url";
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenReturn(fakePresignedUrl);

        // ⚠️ Override the shortenedPresignedUrl method to avoid real HTTP call
        MinioService spyService = Mockito.spy(minioService);
        doReturn("https://tinyurl.com/fake-short").when(spyService).shortenedPresignedUrl(fakePresignedUrl);

        // Call method
        String result = spyService.uploadFile(file);

        // Assert
        assertEquals("https://tinyurl.com/fake-short", result);
        verify(minioClient).putObject(any(PutObjectArgs.class));
    }
}

