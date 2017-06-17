package com.devopsbuddy.backend.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.devopsbuddy.exceptions.S3Exception;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Service class to connect with the Amazon S3 Cloud
 *
 * Created by root on 16/06/17.
 */
@Service
public class S3Service {

    /** The application logger */
    private static final Logger LOG = LoggerFactory.getLogger(S3Service.class);

    private static final String PROFILE_PICTURE_FILE_NAME = "profilePicture";

    // Values from the application-common.properties file
    @Value("${aws.s3.root.bucket.name}")
    private String bucketName;

    // Values from the application-common.properties file
    @Value("${aws.s3.profile}")
    private String awsProfileName;

    // Values from the application-common.properties file
    @Value("${image.store.tmp.folder}")
    private String tempImageStore;

    // Spring instantiates the Amazon bean object we declared on the ApplicationConfig class
    @Autowired
    private AmazonS3Client s3Client;

    /**
     * - Method invoked by SignUp Controller class -
     *
     * It stores the given file name in S3 and returns the key under which the file has been stored
     *
     * @param uploadedFile The multipart file uploaded by user
     * @param username The username will be used to name the folder within Amazon S3 Bucket
     * @return The URL of the uploaded image file
     * @throws S3Exception If something goes wrong
     */
    public String storeProfileImage(MultipartFile uploadedFile, String username) {

        String profileImageUrl = null;

        try {

            if ((uploadedFile != null) && (!uploadedFile.isEmpty())) {

                byte[] bytes = uploadedFile.getBytes();

                // The root of our temporary assets. It will create if it doesn't exist
                File tmpImageStoredFolder =
                        new File(tempImageStore + File.separatorChar + username);

                // Verifies if the folder exist
                if (!tmpImageStoredFolder.exists()){
                    LOG.info("Creating the temporary root for the S3 assets");
                    tmpImageStoredFolder.mkdirs(); // Creates the folder
                }

                // Generates the file
                File tmpProfileImageFile =
                        new File(tmpImageStoredFolder.getAbsolutePath()
                                + File.separatorChar
                                + PROFILE_PICTURE_FILE_NAME
                                + "."
                                + FilenameUtils.getExtension(uploadedFile.getOriginalFilename()));// Gets the extension file

                LOG.info("Temporary file will be saved to {}", tmpProfileImageFile.getAbsolutePath());

                try {
                    // Creates the file using FileOutputStream and writes it in the folder
                    BufferedOutputStream stream = new BufferedOutputStream(
                            new FileOutputStream(
                                    new File(tmpProfileImageFile.getAbsolutePath())));

                    stream.write(bytes);

                } catch (Exception ex){
                    LOG.error("An error occurred while writing the file on BufferedOutputStream");
                }

                // Stores it on the Amazon S3 Cloud
                profileImageUrl = this.storeProfileImageToS3(tmpProfileImageFile, username);

                // Clean up the temporary folder
                tmpProfileImageFile.delete();

            }

        } catch (IOException e){
            throw new S3Exception(e);
        }

        return profileImageUrl;
    }

    /**
     * - Method invoked by storeProfileImage method -
     *
     * Returns the root URL where the bucket name is located.
     *
     * <p>Please note that the URL doesn't contain the bucket name</p>
     *
     * @param bucketName The bucket name
     * @return The root URL where the bucket name is located
     * @throws S3Exception If something goes wrong.
     */
    private String ensureBucketExists(String bucketName) {

        // Bucket URL to be returned
        String bucketUrl = null;

        try {
            // Checks on the S3 Cloud if the bucket exist
            if (!s3Client.doesBucketExist(bucketName)){
                LOG.info("Bucket {} doesn't exists... Creating one", bucketName);
                s3Client.createBucket(bucketName); // Creates the bucket if doesn't exist
                LOG.info("Created bucket: {}", bucketName);
            }

            // Gets the bucket URL where it's located on the Amazon S3 Cloud
            bucketUrl = s3Client.getResourceUrl(bucketName, null) + bucketName;

        } catch (AmazonClientException ace){
            LOG.error("An error occurred while connecting to S3. Will not execute action for bucket: {}", bucketName, ace);
            throw new S3Exception(ace);
        }

        LOG.debug("URL Bucket: {}", bucketUrl);

        return bucketUrl;
    }

    /**
     * - Method invoked by storeProfileImage method -
     *
     * It stores the given file name in S3 and returns the key under which the file has been stored.
     *
     * @param resource The file resource to upload to S3
     * @return The URL of the uploaded resource or null if a problem occurred
     *
     * @throws S3Exception If the resource file doesn't exist
     */
    private String storeProfileImageToS3(File resource, String username) {

        // URL to be returned
        String resourceUrl = null;

        // Checks if the file exist
        if (!resource.exists()){
            LOG.error("The file {} doesn't exist. Throwing an exception", resource.getAbsolutePath());
            throw new S3Exception("The file " + resource.getAbsolutePath() + " doesn't exist.");
        }

        // Ensures that the bucket exist on the Amazon S3 Cloud
        String rootBucketUrl = this.ensureBucketExists(bucketName);

        // Checks if any bucket exist and if it was created or not
        if (rootBucketUrl == null){
            LOG.error("The bucket {} doesn't exist and the application wasn't able to create it. " +
            "The image won't be stored with the profile", rootBucketUrl);

        } else {

            // If it does then creates an Access Control List Object and gets its permission for reading
            AccessControlList accessControlList = new AccessControlList();
            accessControlList.grantPermission(GroupGrantee.AllUsers, Permission.Read);

            // Creates a key value that will be the nae of the file saved on the cloud
            String key = username + "/" + PROFILE_PICTURE_FILE_NAME + "." + FilenameUtils.getExtension(resource.getName());

            try {
                // Save the file on the Amazon S3 Cloud bucket
                // (already created on-line or created by this class)
                // and gets its URL
                s3Client.putObject(new PutObjectRequest(bucketName, key, resource).withAccessControlList(accessControlList));
                resourceUrl = s3Client.getResourceUrl(bucketName, key);// Getting URL file

            } catch (AmazonClientException ex){
                LOG.error("A client exception occurred while trying to store the profile" +
                        " image {} on S3. The profile image won't be stored.", resource.getAbsolutePath(), ex);
                throw new S3Exception(ex);
            }

        }

        LOG.debug("URL Stored file: {}", resourceUrl);

        return resourceUrl; // Returns the full URL Amazon S3 Cloud bucket with the file saved
    }


}
