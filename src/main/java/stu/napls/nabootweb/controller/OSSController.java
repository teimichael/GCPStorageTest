package stu.napls.nabootweb.controller;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stu.napls.nabootweb.config.GlobalConstant;
import stu.napls.nabootweb.core.exception.Assert;
import stu.napls.nabootweb.core.response.Response;
import stu.napls.nabootweb.model.vo.BlobVO;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/oss")
public class OSSController {

    @PostMapping("/bucket/{bucketName}/object/{objectName}/update")
    public Response updateObject(@PathVariable("bucketName") String bucketName, @PathVariable("objectName") String objectName, @RequestParam String filePath) throws IOException {
        Storage storage = StorageOptions.newBuilder().setProjectId(GlobalConstant.GCP_STORAGE_PROJECT_ID).build().getService();
        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, Files.readAllBytes(Paths.get(filePath)));
        return Response.success("File " + filePath + " uploaded to bucket " + bucketName + " as " + objectName);
    }

    @PostMapping("/bucket/{bucketName}/object/upload")
    public Response uploadObject(@PathVariable("bucketName") String bucketName, @RequestParam MultipartFile file) throws IOException {
        Storage storage = StorageOptions.newBuilder().setProjectId(GlobalConstant.GCP_STORAGE_PROJECT_ID).build().getService();
        String objectName = file.getOriginalFilename();
        Assert.notNull(objectName, "File name cannot be null.");

        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        try (WriteChannel writer = storage.writer(blobInfo)) {
            byte[] buffer = new byte[1024 * 1024];
            try (InputStream inputStream = file.getInputStream()) {
                int length;
                while ((length = inputStream.read(buffer)) >= 0)
                    writer.write(ByteBuffer.wrap(buffer, 0, length));
            } catch (Exception ex) {
                // handle exception
            }
        }
        Blob blob = storage.get(blobId);
        return Response.success("File (" + objectName + ") uploaded to bucket " + bucketName, new BlobVO(blob.getName(), blob.getContentType(), blob.getSize(), blob.getMd5(), blob.getCreateTime(), blob.getUpdateTime()));
    }

    @GetMapping("/bucket/{bucketName}/object/{objectName}/get/url")
    public Response getObjectUrl(@PathVariable("bucketName") String bucketName, @PathVariable("objectName") String objectName) throws IOException {
        Storage storage = StorageOptions.newBuilder().setProjectId(GlobalConstant.GCP_STORAGE_PROJECT_ID).build().getService();
        String PATH_TO_JSON_KEY = "/web/test-gcp-credential.json";
        URL signedUrl = storage.signUrl(BlobInfo.newBuilder(bucketName, objectName).build(),
                1, TimeUnit.DAYS, Storage.SignUrlOption.signWith(ServiceAccountCredentials.fromStream(
                        new FileInputStream(PATH_TO_JSON_KEY))));
        Assert.notNull(signedUrl, "Failed getting download URL.");
        return Response.success("ok", signedUrl.toString());
    }

    @GetMapping("/bucket/{bucketName}/object/{objectName}/get/preview")
    public byte[] getObjectPreview(@PathVariable("bucketName") String bucketName, @PathVariable("objectName") String objectName) {
        Storage storage = StorageOptions.newBuilder().setProjectId(GlobalConstant.GCP_STORAGE_PROJECT_ID).build().getService();
        Blob blob = storage.get(BlobId.of(bucketName, objectName));
        return blob.getContent();
    }

    @GetMapping("/bucket/{bucketName}/object/get/list")
    public Response getList(@PathVariable("bucketName") String bucketName) {
        Storage storage = StorageOptions.newBuilder().setProjectId(GlobalConstant.GCP_STORAGE_PROJECT_ID).build().getService();
        Bucket bucket = storage.get(bucketName);
        Page<Blob> blobs = bucket.list();
        List<BlobVO> blobVOList = new ArrayList<>();
        for (Blob blob : blobs.iterateAll()) {
            blobVOList.add(new BlobVO(blob.getName(), blob.getContentType(), blob.getSize(), blob.getMd5(), blob.getCreateTime(), blob.getUpdateTime()));
        }
        return Response.success(blobVOList);
    }

    @PostMapping("/bucket/{bucketName}/object/{objectName}/delete")
    public Response deleteObject(@PathVariable("bucketName") String bucketName, @PathVariable("objectName") String objectName) {
        Storage storage = StorageOptions.newBuilder().setProjectId(GlobalConstant.GCP_STORAGE_PROJECT_ID).build().getService();
        Assert.isTrue(storage.delete(bucketName, objectName),"Failed deleting");

        return Response.success("Delete successfully.");
    }

}
