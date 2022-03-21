export function generateRandomString(length){
    let RandomStringModule = Module.cwrap("GenerateRandomString", null, ["number","number"]);
    let byteOffset = 0;
    let outputArray = new Int32Array(Module.HEAP32.buffer, byteOffset, length);
    let bytesPerElement = outputArray.BYTES_PER_ELEMENT;

    let outputPointer = Module._malloc(length * bytesPerElement);

    Module.HEAP32.set(outputArray, (outputPointer / bytesPerElement));
    RandomStringModule(outputPointer, length);

    outputArray = new Int32Array(Module.HEAP32.buffer, outputPointer, length);
    let generatedPassword = String.fromCharCode.apply(null, outputArray);

    Module._free(outputPointer);

    return generatedPassword
}

export function identityFunction(inputString){
    let IdentityStringFunction = Module.cwrap("IdentityFunction", null, ["number","number","number"]);
    let length = inputString.length;
    let stringAsCharCodeArray = Array.from([...inputString], (x) => x.charCodeAt(0));
    let inputArray = Int32Array.from(stringAsCharCodeArray);
    let bytesPerElement = inputArray.BYTES_PER_ELEMENT;

    let inputPointer = Module._malloc(length * bytesPerElement);
    let outputPointer = Module._malloc(length * bytesPerElement);

    Module.HEAP32.set(inputArray, (inputPointer / bytesPerElement));
    IdentityStringFunction(inputPointer,outputPointer, length);

    let outputArray = new Int32Array(Module.HEAP32.buffer, outputPointer, length);
    let generatedString = String.fromCharCode.apply(null, outputArray);

    Module._free(inputPointer);
    Module._free(outputPointer);

    return generatedString;
}
