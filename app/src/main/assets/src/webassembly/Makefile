DESTINATION = generated/generateStringEnvironment.js
EMFLAGS = -s WASM=1 -s EXPORTED_FUNCTIONS=["_malloc","_free","_GenerateRandomString","_IdentityFunction"] -s EXPORTED_RUNTIME_METHODS=["cwrap","getValue","setValue"]

build:
	$(CC) generateString.c -o $(DESTINATION) $(EMFLAGS)
