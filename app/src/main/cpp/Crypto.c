#include <jni.h>
#include "/include/openssl/aes.h"
#include "/include/openssl/evp.h"
#include "include/openssl/ossl_typ.h"
#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>
#include <string.h>
#include <openssl/rand.h>
#include <openssl/crypto.h>
#include <openssl/sha.h>


JNIEXPORT jbyteArray JNICALL
Java_tu_bp21_passwortmanager_Crypto_crypt(JNIEnv *env, jobject thiz, jbyteArray input,
                                          jbyteArray key, jboolean should_encrypt) {
    //TODO: iv unique sicherstellen benötigt?
    int len_buffer, output_len;
    int iv_length = 16;
    jsize input_len = (*env)->GetArrayLength(env, input);
    jbyte  *input_ptr = (*env)->GetByteArrayElements(env , input, 0);
    jbyte *key_ptr = (*env)->GetByteArrayElements(env , key, 0);
    jbyte *output;

    EVP_CIPHER_CTX *ctx = EVP_CIPHER_CTX_new();
    EVP_CIPHER_CTX_init(ctx);

    //encrypt
    if(should_encrypt) {
        //iv generate and store in the first 16 elements of output
        output = calloc (input_len*2 + iv_length, sizeof (jbyte));
        RAND_bytes(output, iv_length);
        //start encryption
        EVP_EncryptInit(ctx, EVP_aes_256_cbc(), key_ptr, output);
        EVP_EncryptUpdate(ctx, &output[iv_length], &len_buffer, input_ptr, input_len);
        output_len = len_buffer;
        EVP_EncryptFinal(ctx, &output[iv_length] + len_buffer, &len_buffer);
        output_len += len_buffer+iv_length;
        EVP_CIPHER_CTX_free(ctx);
    }
    //decrypt
    else{
        //iv receive from the first 16 elements of input
        input_len -= iv_length;
        output = calloc (input_len, sizeof (jbyte));
        //start decryption
        EVP_DecryptInit(ctx, EVP_aes_256_cbc(), key_ptr, input_ptr);
        EVP_DecryptUpdate(ctx, output, &len_buffer, &input_ptr[iv_length], input_len);
        output_len = len_buffer;
        EVP_DecryptFinal(ctx, output + len_buffer, &len_buffer);
        output_len += len_buffer;
        EVP_CIPHER_CTX_free(ctx);
    }

    //store in jbyteArray for return
    jbyteArray output_array = (jbyteArray) (*env)->NewByteArray(env, output_len);
    (*env)->SetByteArrayRegion (env, output_array, 0, output_len, (jbyte *) output);

    //release memory
    free( output );
    free(key_ptr);
    free(input_ptr);

    return output_array;
}

JNIEXPORT jbyteArray JNICALL
Java_tu_bp21_passwortmanager_Crypto_generateKey(JNIEnv *env, jobject thiz, jbyteArray input,
                                                jint input_length, jbyteArray salt, jint salt_length, jint iterations) {
    int output_length = 32;
    jbyte *output = calloc (output_length, sizeof (jbyte));

    //generate key with sha3_256
    PKCS5_PBKDF2_HMAC(input, input_length, salt, salt_length, iterations, EVP_sha3_256(), output_length, output);

    //store in jbyteArray for return
    jbyteArray output_array = (jbyteArray) (*env)->NewByteArray(env, output_length);
    (*env)->SetByteArrayRegion (env, output_array, 0, output_length, (jbyte *) output);

    //release memory
    free( output );

    return output_array;
}

JNIEXPORT jbyteArray JNICALL
Java_tu_bp21_passwortmanager_Crypto_generateSalt(JNIEnv *env, jclass clazz, jint size) {
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