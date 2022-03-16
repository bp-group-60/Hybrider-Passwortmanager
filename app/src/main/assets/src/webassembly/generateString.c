#include <math.h>
#include <stdlib.h>
#include <time.h>
#include <stdio.h>
#include <emscripten.h>


void EMSCRIPTEN_KEEPALIVE GenerateRandomString(int *output, int outputSize) {
    int randomizer = 0;

    // Seed the random-number generator
    // with current time so that the
    // numbers will be different every time
    srand((unsigned int) (time(NULL)));

    char allNumbers[] = "0123456789";
    char allLowerletter[] = "abcdefghijklmnopqrstuvwxyz";
    char allCapsLetter[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    char allSymbols[] = "!@#$^&*?";

    int i;

    for ( i = 0; i < outputSize; i++) {

        randomizer = rand() % 4;

        if (randomizer == 1) {
            output[i] = (int) (allNumbers[rand() % (sizeof(allNumbers) - 1)]);
        } else if (randomizer == 2) {
            output[i] = (int) (allSymbols[rand() % (sizeof(allSymbols) - 1)]);
        } else if (randomizer == 3) {
            output[i] = (int) (allCapsLetter[rand() % (sizeof(allCapsLetter) - 1)]);
        } else {
            output[i] = (int) (allLowerletter[rand() % (sizeof(allLowerletter) - 1)]);
        }
    }
}

void EMSCRIPTEN_KEEPALIVE IdentityFunction(int *input,int *output, int outputSize) {
   int i;
   for (i = 0; i < outputSize; i++) {
    output[i] = input[i];
   }
}
