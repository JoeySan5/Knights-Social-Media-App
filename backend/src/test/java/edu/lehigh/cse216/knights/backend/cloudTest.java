package edu.lehigh.cse216.knights.backend;

//https://github.com/googleapis/java-storage/tree/main
//Imports are from above^, collected to make authenticated requests to google cloud
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;

public class cloudTest {
    public static void main(String[] args) {

        // This authenticates service object to communicate w/ cloud via API calls (by
        // calling methods on Storage service object 'storage')
        Storage storage = StorageOptions.getDefaultInstance().getService();
        String bucketName = "knights-bucket-1";

        Page<Blob> blobs = storage.list(bucketName);
        System.out.println(blobs);

    }
}
