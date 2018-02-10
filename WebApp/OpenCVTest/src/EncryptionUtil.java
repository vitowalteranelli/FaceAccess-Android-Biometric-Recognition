import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.crypto.Cipher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author JavaDigest
 * 
 */
public class EncryptionUtil extends HttpServlet {

  /**
   * String to hold name of the encryption algorithm.
   */
  public static final String ALGORITHM = "RSA";

  /**
   * String to hold the name of the private key file.
   */
  public static final String PRIVATE_KEY_FILE = "C:/keys/private.key";

  /**
   * String to hold name of the public key file.
   */
  public static final String PUBLIC_KEY_FILE = "C:/keys/public.key";

  /**
   * Generate key which contains a pair of private and public key using 1024
   * bytes. Store the set of keys in Prvate.key and Public.key files.
   * 
   * @throws NoSuchAlgorithmException
   * @throws IOException
   * @throws FileNotFoundException
   */
  public static void generateKey() {
    try {
      final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
      keyGen.initialize(2048);
      final KeyPair key = keyGen.generateKeyPair();

      File privateKeyFile = new File(PRIVATE_KEY_FILE);
      File publicKeyFile = new File(PUBLIC_KEY_FILE);

      // Create files to store public and private key
      if (privateKeyFile.getParentFile() != null) {
        privateKeyFile.getParentFile().mkdirs();
      }
      privateKeyFile.createNewFile();

      if (publicKeyFile.getParentFile() != null) {
        publicKeyFile.getParentFile().mkdirs();
      }
      publicKeyFile.createNewFile();

      // Saving the Public key in a file
      ObjectOutputStream publicKeyOS = new ObjectOutputStream(
          new FileOutputStream(publicKeyFile));
      publicKeyOS.writeObject(key.getPublic());
      publicKeyOS.close();

      // Saving the Private key in a file
      ObjectOutputStream privateKeyOS = new ObjectOutputStream(
          new FileOutputStream(privateKeyFile));
      privateKeyOS.writeObject(key.getPrivate());
      privateKeyOS.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * The method checks if the pair of public and private key has been generated.
   * 
   * @return flag indicating if the pair of keys were generated.
   */
  public static boolean areKeysPresent() {

    File privateKey = new File(PRIVATE_KEY_FILE);
    File publicKey = new File(PUBLIC_KEY_FILE);

    if (privateKey.exists() && publicKey.exists()) {
      return true;
    }
    return false;
  }

  /**
   * Encrypt the plain text using public key.
   * 
   * @param text
   *          : original plain text
   * @param key
   *          :The public key
   * @return Encrypted text
   * @throws java.lang.Exception
   */
  public static byte[] encrypt(String text, PublicKey key) {
    byte[] cipherText = null;
    try {
      // get an RSA cipher object and print the provider
      final Cipher cipher = Cipher.getInstance(ALGORITHM);
      // encrypt the plain text using the public key
      cipher.init(Cipher.ENCRYPT_MODE, key);
      cipherText = cipher.doFinal(text.getBytes("UTF-8"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return cipherText;
  }

  /**
   * Decrypt text using private key.
   * 
   * @param text
   *          :encrypted text
   * @param key
   *          :The private key
   * @return plain text
   * @throws java.lang.Exception
   */
  public static String decrypt(byte[] text, PrivateKey key) {
    byte[] dectyptedText = null;
    try {
      // get an RSA cipher object and print the provider
      final Cipher cipher = Cipher.getInstance(ALGORITHM);

      // decrypt the text using the private key
      cipher.init(Cipher.DECRYPT_MODE, key);
      dectyptedText = cipher.doFinal(text);

    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return new String(dectyptedText);
  }

  /**
   * Test the EncryptionUtil
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response) 
	    	throws IOException {
	  response.setContentType("text/html");
  	PrintWriter out = response.getWriter();
  	
  	 out.write("<html><body>");

       out.write("<h1>Esempio di encrypting</h1>");
    try {

      // Check if the pair of keys are present else generate those.
      if (!areKeysPresent()) {
        // Method generates a pair of keys using the RSA algorithm and stores it
        // in their respective files
        generateKey();
      }

      final String originalText = "Text to be encrypted ";
      ObjectInputStream inputStream = null;

      // Encrypt the string using the public key
      inputStream = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
      final PublicKey publicKey = (PublicKey) inputStream.readObject();
      final byte[] cipherText = encrypt(originalText, publicKey);

      // Decrypt the cipher text using the private key.
      inputStream = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
      final PrivateKey privateKey = (PrivateKey) inputStream.readObject();
      final String plainText = decrypt(cipherText, privateKey);

      // Printing the Original, Encrypted and Decrypted Text
      out.write("Original Text: " + originalText+"<br />");
      out.write("Encrypted Text: " +cipherText.toString()+"<br />");
      out.write("Decrypted Text: " + plainText+"<br />");
      
      //db job
      
      
      
      
      
      
      
      
   // This is needed to use Connector/J. It basically creates a new instance
   // of the Connector/J jdbc driver.
   Class.forName("com.mysql.jdbc.Driver").newInstance();
   out.write("instanzio l'oggetto di classe<br />");
   // Open new connection.
   java.sql.Connection conn;

   out.write("la connessione<br />");
   conn = DriverManager.getConnection("jdbc:mysql://localhost/FaceAccessDB?user=root");
   //Statement sqlStatement = conn.createStatement();
   out.write("creo lo statement<br />");
	// Define variables
	String uId;
	String fName;
	String lName;
	String publicrsa;
	String privatersa;
   // Generate the SQL query.
	byte[] publicarray= publicKey.getEncoded();
	byte[] privatearray=privateKey.getEncoded();
	out.write("trasformo in byte array le chiavi<br />");
	PreparedStatement sqlStatement = conn.prepareStatement("UPDATE users SET public=?,private=? WHERE username='general';");
	//sqlStatement.setString(1, "aaa");
	//sqlStatement.setString(2, "bbb");
	sqlStatement.setBytes(1, publicarray);
	sqlStatement.setBytes(2, privatearray);

	out.write("preparato statement: "+publicarray.toString()+"<br />");
   // Get the query results and display them.
   sqlStatement.executeUpdate();
   out.write("query eseguita<br />");
   
   
   
   // Generate the SQL query.
   String query = "SELECT id, firstname, lastname, public, private FROM users";
	
   // Get the query results and display them.
   ResultSet sqlResult = sqlStatement.executeQuery(query);
   while(sqlResult.next()) {
	   uId = sqlResult.getString("id");
	   fName = sqlResult.getString("firstname");
	   lName = sqlResult.getString("lastname");
	   publicrsa = sqlResult.getString("public");
	   byte[] privateKeyBytes = sqlResult.getBytes("private");
	   byte[] publicKeyBytes = sqlResult.getBytes("public");
	   KeyFactory kf = KeyFactory.getInstance("RSA"); // or "EC" or whatever
	   PrivateKey private_ = kf.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
	   PublicKey public_ = kf.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
	   privatersa = sqlResult.getString("private");
	   out.write("<b>" + uId + "</b>, " + fName + ", " + lName + "<br />" + publicrsa + "<br />" + privatersa + "<br />");
	   
	   final String plainText_ = decrypt(cipherText, private_);
	   out.write("Decrypting: "+cipherText+"  --->  "+plainText_+"<br />");
   }
   // Close the connection.
   sqlResult.close();
   sqlStatement.close();
   conn.close();
      
      
      

      
      
      
      
      
      
      
      
      
      
      
      //db job end
      
      

    } catch (Exception e) {
      e.printStackTrace();
    }
    
    
    
    
    
    
    
    
    
    
    
    out.write("</body></html>");
  }
}