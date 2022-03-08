DESTIONATION = generated/passwordGenerateEnvironment.js
EMFLAGS = -s WASM=1 -s EXPORTED_FUNCTIONS=["_malloc","_free","_GenerateRandomPasswort"] -s EXPORTED_RUNTIME_METHODS=["cwrap","getValue","setValue"]

build:
	$(CC) passwordGenerate.c -o $(DESTIONATION) $(EMFLAGS)
