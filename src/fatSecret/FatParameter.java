package fatSecret;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SignatureException;
import java.util.*;

public class FatParameter
{
   //This parameter object is not associated with a specific user profile
   //This object will be used to call generic functions like getRecipe
   public FatParameter(boolean isGet, Hashtable<String, String> params, int key_count) throws UnsupportedEncodingException, SignatureException
   {
       this(isGet, params, "", key_count);
   }

   //This parameter object is associated with a specific user profile
   //This object will be used to call user-specific functions like getWeight
   public FatParameter(boolean isGet, Hashtable<String, String> params, String accessSharedSecret, int key_count) throws UnsupportedEncodingException, SignatureException
   {
       oauthTimestamp = Calendar.getInstance().getTimeInMillis() / 1000L;
       oauthNounce = UUID.randomUUID().toString();
       this.accessSharedSecret = accessSharedSecret;
       BuildSignature(isGet, params, key_count);
   }


   //      oauth_consumer_key      String  Your API key when you registered as a developer
   private static String[] oauthConsumerKey = {"your_key_here"};
   //      oauth_signature_method  String  The method used to generate the signature (only HMAC-SHA1 is supported)
   private static String oauthSignatureMethod = "HMAC-SHA1";
   //      oauth_timestamp Int     The date and time, expressed in the number of seconds since January 1, 1970 00:00:00 GMT. The timestamp value must be a positive integer and must be equal or greater than the timestamp used in previous requests
   private long oauthTimestamp;
   //      oauth_nonce     String  A randomly generated string for a request that can be combined with the timestamp to produce a unique value
   private String oauthNounce;
   //      oauth_version   String  MUST be "1.0"
   private static String oauthVersion = "1.0";
   //oauth_signature       String  The signature, a consistent reproducible concatenation of the request elements into a single string. The string is used as an input in hashing or signing algorithms.
   private String oauthSignature = "";

   Hashtable<String, String> allParams = null;
   private static String[] consumerSharedSecret = {"your_secret_here"};
   private String accessSharedSecret = "";

   private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
   /**
    * Computes RFC 2104-compliant HMAC signature.
    * * @param data
    * The data to be signed.
    * @param key
    * The signing key.
    * @return
    * The Base64-encoded RFC 2104-compliant HMAC signature.
    * @throws
    * SignatureException when signature generation fails
    */
   private static String calculateRFC2104HMAC(String data, String key)
           throws SignatureException
   {
       String result;
       try {

           // get an hmac_sha1 key from the raw key bytes
           SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);

           // get an hmac_sha1 Mac instance and initialize with the signing key
           Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
           mac.init(signingKey);

           // compute the hmac on input data bytes
           byte[] rawHmac = mac.doFinal(data.getBytes());

           // base64-encode the hmac
           result = EncodeBase64(rawHmac);

       } catch (Exception e) {
           throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
       }
       return result;
   }

   /**
    * Performs base64-encoding of input bytes.
    *
    * @param rawData
    *      Array of bytes to be encoded.
    * @return
    *      The base64-encoded string representation of rawData.
    */
   private static String EncodeBase64(byte[] rawData) {
       return Base64.encodeBytes(rawData);
   }

   private String BuildSignature(boolean isGet, Hashtable<String, String> params, int key_count) throws UnsupportedEncodingException, SignatureException
   {
       String httpMethod = isGet ? "GET" : "POST";
       String encodedURL = Encode("http://platform.fatsecret.com/rest/server.api");

       PrepareParameters(params, key_count);

       String normalizedParameter = Encode(GetNormalizedParamString());
       String signatureBaseString = httpMethod + "&" + encodedURL + "&" + normalizedParameter;

       oauthSignature = calculateRFC2104HMAC(signatureBaseString, consumerSharedSecret[key_count] + "&" + accessSharedSecret);

       return oauthSignature;
   }

   private void PrepareParameters(Hashtable<String, String> params, int key_count) {

       //Normalized Parameters
       //                      Please refer to the FatSecret REST API documentation for the full parameter list for each method, but for OAuth authentication the following parameters are required for every request:
       //                              oauth_consumer_key
       //                              Your consumer key (you can obtain one by registering here)
       //                              oauth_signature_method
       //                              We only support "HMAC-SHA1"
       //                              oauth_nonce
       //                              A randomly generated string for a request that can be combined with the timestamp to produce a unique value
       //                              oauth_timestamp
       //                              The date and time, expressed in the number of seconds since January 1, 1970 00:00:00 GMT. The timestamp value must be a positive integer and must be equal or greater than the timestamp used in previous requests
       //                              oauth_version
       //                              Must be "1.0"
       //                              Parameters are written in the format "name=value" and sorted using lexicographical byte value ordering, first by name and then by value. Finally the parameters are concatenated in their sorted order into a single string, each name-value pair separated by an '&' character (ASCII code 38).

       allParams = (Hashtable<String, String>)params.clone();
       allParams.put("oauth_consumer_key", oauthConsumerKey[key_count]);
       allParams.put("oauth_signature_method", oauthSignatureMethod);
       allParams.put("oauth_nonce", oauthNounce);
       allParams.put("oauth_timestamp", Long.toString(oauthTimestamp));
       allParams.put("oauth_version", oauthVersion);
   }

   private String GetNormalizedParamString()
   {
       String normalized = "";
       Vector<String> keys = new Vector<String>(allParams.keySet());
       Collections.sort(keys);

       for (int i = 0; i < keys.size(); i++)
       {
           if (i != 0)
               normalized += "&";
           String key = keys.get(i);
           normalized += key + "=" + allParams.get(key);
       }

       return normalized;
   }

   public String GetAllParamString() throws UnsupportedEncodingException
   {
       return GetNormalizedParamString() + "&oauth_signature=" + Encode(this.oauthSignature);
   }

   private static String Encode(String target) throws UnsupportedEncodingException
   {
       return URLEncoder.encode(target, "UTF-8");
   }
}
