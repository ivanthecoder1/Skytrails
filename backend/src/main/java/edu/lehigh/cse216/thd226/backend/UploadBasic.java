package edu.lehigh.cse216.thd226.backend;

// Imports reposonsible for interacting with google drive api
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.services.drive.model.Permission;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;

// Responsible for base64 encoding and decoding
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;

import org.apache.commons.io.IOUtils;

/* Class to demonstrate use of Drive insert file API */
// From google drive api documentation
public class UploadBasic {

    /**
     * Upload a file to google drive based on base64 string provided
     *
     * @return if file was successfully uploaded, return the file ID
     * @throws IOException if service account credentials file not found.
     */
    public static String uploadToGoogleDrive(String base64String) throws IOException {
        // Load pre-authorized user credentials from the environment.
        // Set the GOOGLE_APPLICATION_CREDENTIALS property
        // Absolute path only works, and relative path returns a file not found error
        InputStream inputStream = UploadBasic.class.getClassLoader().getResourceAsStream("credentials.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream)
                .createScoped(Arrays.asList(DriveScopes.DRIVE_FILE));
        System.out.println(credentials);
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        // Build a new authorized API client service.
        Drive service = new Drive.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Drive samples")
                .build();

        // Upload file photo.jpg on drive.
        File fileMetadata = new File();

        // Defines the name of the file
        fileMetadata.setName("test2.jpg");

        // Sends to skytrail folder on our team email
        fileMetadata.setParents(Collections.singletonList("1WKc9RD4pvbTTVublGFjZzvAe4CJWIXF2"));

        // Decode the Base64 string to binary data
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);

        // Upload the file to Google Drive
        ByteArrayContent mediaContent = new ByteArrayContent("image/jpeg", decodedBytes);

        // Send file to google drive
        try {
            // Creates the file and uploads it
            File file = service.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
            System.out.println("File successfully uploaded");
            System.out.println("File ID: " + file.getId());

            // Create a new permission to make the file accessible to anyone with the link
            Permission permission = new Permission()
                    .setType("anyone") // "anyone" represents anyone with the link
                    .setRole("reader"); // "reader" grants view access

            // Insert the permission
            Permission newPermission = service.permissions().create(file.getId(), permission).execute();
            System.out.println("File is now accessible to anyone with the link.");
            System.out.println("Permission ID: " + newPermission.getId());

            // Get the shareable link for the file
            String fileLink = "https://drive.google.com/file/d/" + file.getId() + "/view";
            System.out.println("File Link: " + fileLink);
            return fileLink;
        } catch (GoogleJsonResponseException e) {
            System.err.println("Unable to upload file: " + e.getDetails());
            throw e;
        }
    }

    // Old code that Imma save here
    // Get the content of the file
    // InputStream fileContent =
    // service.files().get(fileID).executeMediaAsInputStream();

    // Convert InputStream to Base64 string
    // String base64Content =
    // Base64.getEncoder().encodeToString(IOUtils.toByteArray(fileContent));
    // System.out.println("Base 64 String: " + base64Content);

    // Retrieve the file by ID from our drive folder
    // String fileId = "1zT4VPVtHigVDmzQIrVimjZGHsJdvovld";
    // File retrievedFile = service.files().get(fileId).execute();
    // System.out.println("File details:");
    // System.out.println("File Name: " + retrievedFile.getName());
    // System.out.println("File ID: " + retrievedFile.getId());

    // Convert base64 string into a file and send it to google drive
    // private static void convertToFile(String base64String) {
    // // Decode the Base64 string to binary data
    // byte[] decodedBytes = Base64.getDecoder().decode(base64String);

    // // Send to google drive
    // }

    // This method encodes a binary file to Base64 and saves the result to a text
    // file
    // private static void encodeToBase64(String inputFile, String outputFile) {
    // try {
    // byte[] fileBytes = Files.readAllBytes(Paths.get(inputFile));
    // String base64Encoded = Base64.getEncoder().encodeToString(fileBytes);

    // Files.write(Paths.get(outputFile), base64Encoded.getBytes());
    // System.out.println("File encoded to Base64 and saved to " + outputFile);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    // This method decodes a Base64-encoded text file back to a binary file
    // private static void decodeFromBase64(String inputFile, String outputFile) {
    // try {
    // String base64Encoded = new String(Files.readAllBytes(Paths.get(inputFile)));
    // byte[] fileBytes = Base64.getDecoder().decode(base64Encoded);

    // Files.write(Paths.get(outputFile), fileBytes);
    // System.out.println("Base64 decoded to file and saved to " + outputFile);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    // // Encode binary file to a text file using base64 and write to it
    // encodeToBase64("C:\\Users\\ivanz\\Downloads\\Lehigh\\Lehigh Junior Fall
    // 2023\\CSE 216\\n" + //
    // "ew
    // backend\\cse216-2023fa-team-7\\backend\\src\\main\\java\\edu\\lehigh\\cse216\\thd226\\backend\\cat.jpg",
    // "C:\\Users\\ivanz\\Downloads\\Lehigh\\Lehigh Junior Fall 2023\\CSE 216\\n" +
    // //
    // "ew
    // backend\\cse216-2023fa-team-7\\backend\\src\\main\\java\\edu\\lehigh\\cse216\\thd226\\backend\\cat.txt");

    // // Decode Base64 text file to binary and write to a new binary file
    // decodeFromBase64("C:\\Users\\ivanz\\Downloads\\Lehigh\\Lehigh Junior Fall
    // 2023\\CSE 216\\n" + //
    // "ew
    // backend\\cse216-2023fa-team-7\\backend\\src\\main\\java\\edu\\lehigh\\cse216\\thd226\\backend\\cat.txt",
    // "C:\\Users\\ivanz\\Downloads\\Lehigh\\Lehigh Junior Fall 2023\\CSE 216\\n" +
    // //
    // "ew
    // backend\\cse216-2023fa-team-7\\backend\\src\\main\\java\\edu\\lehigh\\cse216\\thd226\\backend\\decoded_cat.jpg");

}