#include <jni.h>
#include <android/log.h>
#include <openssl/rand.h>
#include <openssl/sha.h>
#include <openssl/evp.h>
#include <openssl/kdf.h>
#include <math.h>
#include <string.h>



JNIEXPORT jbyteArray JNICALL
Java_tu_bp21_passwortmanager_Crypto_crypt(JNIEnv *env, jobject thiz, jbyteArray input, jbyteArray aad, jbyteArray iv,
                                          jbyteArray key) {
    int len_buffer, output_len;
    int tag_len = 16;
    jsize input_len = (*env)->GetArrayLength(env, input);
    jsize aad_len = (*env)->GetArrayLength(env, aad);
    jsize iv_len;
    jbyte  *input_ptr = (*env)->GetByteArrayElements(env , input, 0);
    jbyte  *aad_ptr = (*env)->GetByteArrayElements(env , aad, 0);
    jbyte  *iv_ptr;
    jbyte *key_ptr = (*env)->GetByteArrayElements(env , key, 0);
    jbyte *output;
    int decryptSuccess;

    EVP_CIPHER_CTX *ctx = EVP_CIPHER_CTX_new();
    EVP_CIPHER_CTX_init(ctx);

    //encrypt
    if(iv != NULL) {
        iv_len = (*env)->GetArrayLength(env, iv);
        iv_ptr = (*env)->GetByteArrayElements(env , iv, 0);
        //iv store in the first 16 elements of output
        output = calloc ( iv_len + input_len + tag_len, sizeof (jbyte));
        memcpy(output, iv_ptr, iv_len);
        //start encryption
        EVP_EncryptInit_ex(ctx, EVP_aes_256_gcm(), NULL, key_ptr, iv_ptr);
        //provide aad(additional associated data)
        EVP_EncryptUpdate(ctx, NULL, &len_buffer, aad_ptr, aad_len);
        //provide plaintext
        EVP_EncryptUpdate(ctx, &output[iv_len], &len_buffer, input_ptr, input_len);
        output_len = len_buffer;
        //finish encryption
        EVP_EncryptFinal_ex(ctx, &output[iv_len] + len_buffer, &len_buffer);
        output_len += len_buffer;
        output_len += iv_len;
        //append authentication Tag at the end of cipher
        EVP_CIPHER_CTX_ctrl(ctx, EVP_CTRL_GCM_GET_TAG, tag_len, &output[output_len]);
        output_len += tag_len;
        //free pointer
        EVP_CIPHER_CTX_free(ctx);
        free(iv_ptr);
    }
    //decrypt
    else{
        //iv receive from the first 16 elements of input and authentication tag at the last 16 elements
        iv_len = 12;
        input_len -= (iv_len + tag_len);
        output = calloc (input_len, sizeof (jbyte));
        //start decryption
        EVP_DecryptInit_ex(ctx, EVP_aes_256_gcm(), NULL, key_ptr, input_ptr);
        //provide aad(additional associated data)
        EVP_DecryptUpdate(ctx, NULL, &len_buffer, aad_ptr, aad_len);
        //provide ciphertext
        EVP_DecryptUpdate(ctx, output, &len_buffer, &input_ptr[iv_len], input_len);
        output_len = len_buffer;
        //set expected authentication tag
        EVP_CIPHER_CTX_ctrl(ctx, EVP_CTRL_GCM_SET_TAG, tag_len, &input_ptr[iv_len+input_len]);
        //finish decryption
        decryptSuccess = EVP_DecryptFinal_ex(ctx, output + len_buffer, &len_buffer);
        output_len += len_buffer;
        EVP_CIPHER_CTX_free(ctx);
        if(decryptSuccess<=0)
            return NULL;

    }

    //store in jbyteArray for return
    jbyteArray output_array = (jbyteArray) (*env)->NewByteArray(env, output_len);
    (*env)->SetByteArrayRegion (env, output_array, 0, output_len, (jbyte *) output);

    //release memory
    free( output );
    free(key_ptr);
    free(input_ptr);
    free(aad_ptr);

    return output_array;
}

JNIEXPORT jbyteArray JNICALL
Java_tu_bp21_passwortmanager_Crypto_generateKey(JNIEnv *env, jobject thiz, jbyteArray input,
                                                jint input_length, jbyteArray salt, jint salt_length) {
    jbyte  *input_ptr = (*env)->GetByteArrayElements(env , input, 0);
    jbyte  *salt_ptr = (*env)->GetByteArrayElements(env , salt, 0);
    int output_length = 32;
    int iterations = 1000000;
    jbyte *output = calloc (output_length, sizeof (jbyte));

    //generate key with sha3_256
    PKCS5_PBKDF2_HMAC(input_ptr, input_length, salt_ptr, salt_length, iterations, EVP_sha3_256(), output_length, output);

    //store in jbyteArray for return
    jbyteArray output_array = (jbyteArray) (*env)->NewByteArray(env, output_length);
    (*env)->SetByteArrayRegion (env, output_array, 0, output_length, (jbyte *) output);

    //release memory
    free( output );

    return output_array;
}

JNIEXPORT jbyteArray JNICALL
Java_tu_bp21_passwortmanager_Crypto_generateSecureByteArray(JNIEnv *env, jclass clazz, jint size) {
    // TODO: salt unique sicherstellen benötigt?
    //generate salt
    jbyte *output = calloc (size, sizeof (jbyte));
    RAND_bytes(output, size);

    //store in jbyteArray for return
    jbyteArray output_array = (jbyteArray) (*env)->NewByteArray(env, size);
    (*env)->SetByteArrayRegion (env, output_array, 0, size, (jbyte *) output);

    //release memory
    free( output );

    return output_array;
}

JNIEXPORT jbyteArray JNICALL
Java_tu_bp21_passwortmanager_Crypto_hash(JNIEnv *env, jclass clazz, jbyteArray input,
                                         jint input_length, jbyteArray salt, jint salt_length) {
    jbyte  *input_ptr = (*env)->GetByteArrayElements(env , input, 0);
    jbyte  *salt_ptr = (*env)->GetByteArrayElements(env , salt, 0);
    size_t output_length = 64;
    jbyte *output = calloc (output_length + salt_length, sizeof (jbyte));
    memcpy(output, salt_ptr, salt_length);
    int success;

    EVP_PKEY_CTX *ctx = EVP_PKEY_CTX_new_id(EVP_PKEY_SCRYPT, NULL);
    EVP_PKEY_derive_init(ctx);
    EVP_PKEY_CTX_set1_pbe_pass(ctx, input_ptr, input_length);
    EVP_PKEY_CTX_set1_scrypt_salt(ctx, salt_ptr, salt_length);

    EVP_PKEY_CTX_set_scrypt_N(ctx, (int) pow(2, 18));
    EVP_PKEY_CTX_set_scrypt_r(ctx, 8);
    EVP_PKEY_CTX_set_scrypt_p(ctx, 1);

    success = EVP_PKEY_derive(ctx, &output[salt_length], &output_length);
    //__android_log_print(ANDROID_LOG_INFO, "MyTag", "The value is %d", i);

    EVP_PKEY_CTX_free(ctx);

    if(success<=0)
        return NULL;

    jbyteArray output_array = (jbyteArray) (*env)->NewByteArray(env, output_length+salt_length);
    (*env)->SetByteArrayRegion(env, output_array, 0, output_length+salt_length, (jbyte *) output);

    //release memory
    free( output );
    free(input_ptr);
    free(salt_ptr);

    return output_array;

}