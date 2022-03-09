EMCC = emcc
SOURCES = src/main/assets/src/js/app/ui/webAssembly/passwordGenerate.c
DESTIONATION = src/main/assets/src/js/app/ui/webAssembly/passwordGenerateEnvironment.js

EMFLAGS =  -s WASM=1 -s EXPORTED_FUNCTIONS=["malloc","free","_GenerateRandomPasswortbyC06"] -s EXTRA_EXPORTED_RUNTIME_METHODS= '["cwrap", "getValue", "setValue"]'

build:
	$(EMCC) $(SOURCES) -o $(DESTIONATION) $(EMFLAGS)