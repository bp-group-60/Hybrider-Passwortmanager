#include <math.h>
#include <stdlib.h>
#include <time.h>
#include <stdio.h>
#include <emscripten.h> // note we added the emscripten header

void EMSCRIPTEN_KEEPALIVE GenerateRandomUsername(int *username, int username_size) {
    int randomizer = 0;

    // Seed the random-number generator
    // with current time so that the
    // numbers will be different every time
    srand((unsigned int) (time(NULL)));

    char allNumbers[] = "0123456789";
    char allLowerletter[] = "abcdefghijklmnopqrstuvwxyz";
    char allCapsLetter[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    char allSymbols[] = "!@#$^&*?";

    int i = 0;

    for (i = 0; i < username_size; i++) {

        randomizer = rand() % 2;

        if (randomizer == 1) {
            username[i] = (int) (allNumbers[rand() % (sizeof(allNumbers) - 1)]);
        } else if (randomizer == 2) {
            username[i] = (int) (allCapsLetter[rand() % (sizeof(allCapsLetter) - 1)]);
        } else {
            username[i] = (int) (allLowerletter[rand() % (sizeof(allLowerletter) - 1)]);
        }
    }
}

void EMSCRIPTEN_KEEPALIVE GenerateRandomPasswort(int *password, int password_size) {
    int randomizer = 0;

    // Seed the random-number generator
    // with current time so that the
    // numbers will be different every time
    srand((unsigned int) (time(NULL)));

    char allNumbers[] = "0123456789";
    char allLowerletter[] = "abcdefghijklmnopqrstuvwxyz";
    char allCapsLetter[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    char allSymbols[] = "!@#$^&*?";

    int i = 0;

    for (i = 0; i < password_size; i++) {

        randomizer = rand() % 4;

        if (randomizer == 1) {
            password[i] = (int) (allNumbers[rand() % (sizeof(allNumbers) - 1)]);
        } else if (randomizer == 2) {
            password[i] = (int) (allSymbols[rand() % (sizeof(allSymbols) - 1)]);
        } else if (randomizer == 3) {
            password[i] = (int) (allCapsLetter[rand() % (sizeof(allCapsLetter) - 1)]);
        } else {
            password[i] = (int) (allLowerletter[rand() % (sizeof(allLowerletter) - 1)]);
        }
    }
}

void EMSCRIPTEN_KEEPALIVE getReverseUsername(int *username) {

}

void EMSCRIPTEN_KEEPALIVE getReversePassword(int *password) {

}