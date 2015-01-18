

/*
 * The ComputeSHA class computes the SHA-1 hash over the content of an input file
 * @author PrithvirajP
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class ComputeSHA {

	@SuppressWarnings("resource")
	static String computeSHA(String filePath) {

		String hash = "";
		byte[] inputByteArr = new byte[1024];
		try {

			// Initialize Message digest with SHA1 Algorithm
			FileInputStream fileStream = new FileInputStream(filePath);
			MessageDigest mdSHA = MessageDigest.getInstance("SHA1");

			// Update the digest using the specified array of bytes
			int readLength = 0;
			while ((readLength = fileStream.read(inputByteArr)) != -1) {
				mdSHA.update(inputByteArr, 0, readLength);
			}

			// Update the digest using the specified array of bytes
			byte[] messageByteArray = mdSHA.digest();

			// Convert the digest to Hex
			hash = DatatypeConverter.printHexBinary(messageByteArray);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return hash;
	}

	public static void main(String[] args) {

		if (args.length != 0) {
			// Compute SHA-1 hash over the contents of a file using computeSHA
			String outputHash = computeSHA(args[0]);
			System.out.println(outputHash.toLowerCase());
		} else {
			System.out.println("Execution Failed! Input Filename not provided.");
		}

	}

}
