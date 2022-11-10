package com.example.demo.storageservice;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

public class StorageService {
  
  public StorageService(String sasToken) {
		blobServiceClient = new BlobServiceClientBuilder().endpoint(blobEndPoint).sasToken(sasToken).buildClient();
	}

	private static String blobEndPoint = "https://instartgram.blob.core.windows.net/";
	private static String clientName = "instartgram";
	private BlobServiceClient blobServiceClient;

	public void uploadFile(MultipartFile file, Long id) throws IOException {
		BlobContainerClient blobContainerClient = blobServiceClient.createBlobContainerIfNotExists(clientName);
		blobContainerClient = blobServiceClient.getBlobContainerClient(clientName);
		String fileName = id.toString() + ".jpg";
		BlobClient blobClient = blobContainerClient.getBlobClient(fileName);
		blobClient.upload(file.getInputStream(), file.getSize(), true);

	}
	
	public void deleteFile(Long id) throws IOException{
		BlobContainerClient blobContainerClient = blobServiceClient.createBlobContainerIfNotExists(clientName);
		blobContainerClient = blobServiceClient.getBlobContainerClient(clientName);
		BlobClient blobClient = blobContainerClient.getBlobClient(id.toString() + ".jpg");
		blobClient.deleteIfExists();
	}

}
