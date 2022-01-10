#include <jni.h>
#include "/include/openssl/aes.h"
#include "/include/openssl/evp.h"
#include "include/openssl/ossl_typ.h"
#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>
#include <string.h>


JNIEXPORT jbyteArray JNICALL
Java_tu_bp21_passwortmanager_Crypto_crypt(JNIEnv *env, jobject thiz, jbyteArray input,
                                          jbyteArray key, jbyteArray iv, jboolean should_encrypt) {
    int len_buffer, output_len;
    jsize input_len = (*env)->GetArrayLength(env, input);
    jbyte  *in = (*env)->GetByteArrayElements(env , input, 0);
    in[input_len] = '\0';
    jbyte *iv_ptr = (*env)->GetByteArrayElements(env , iv, 0);
    iv_ptr[16] = '\0';
    jbyte *key_ptr = (*env)->GetByteArrayElements(env , key, 0);
    key_ptr[32] = '\0';
    jbyte *output;

    EVP_CIPHER_CTX *ctx = EVP_CIPHER_CTX_new();
    EVP_CIPHER_CTX_init(ctx);

    if(should_encrypt) {
        output = calloc (input_len*2, sizeof (jbyte));
        EVP_EncryptInit(ctx, EVP_aes_256_cbc(), key_ptr, iv_ptr);
        EVP_EncryptUpdate(ctx, output, &len_buffer, in, input_len);
        output_len = len_buffer;
        EVP_EncryptFinal(ctx, output + len_buffer, &len_buffer);
        output_len += len_buffer;
        EVP_CIPHER_CTX_free(ctx);
    }
    else{
        output = calloc (input_len, sizeof (jbyte));
        EVP_DecryptInit(ctx, EVP_aes_256_cbc(), key_ptr, iv_ptr);
        EVP_DecryptUpdate(ctx, output, &len_buffer, in, input_len);
        output_len = len_buffer;
        EVP_DecryptFinal(ctx, output + len_buffer, &len_buffer);
        output_len += len_buffer;
        EVP_CIPHER_CTX_free(ctx);
    }

    jbyteArray array = (jbyteArray) (*env)->NewByteArray(env, output_len);

    (*env)->SetByteArrayRegion (env, array, 0, output_len, (jbyte *) output);

    //release memory
    free( output );
    free(iv_ptr);
    free(key_ptr);
    free(in);

    return array;
}