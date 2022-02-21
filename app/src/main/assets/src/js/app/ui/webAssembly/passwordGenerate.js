var GenPwd = Module.cwrap("GenerateRandomPasswortbyC06", null, ["number","number"]);

    function GenerateRandomPasswort(len){

        var ouput_array = new Int32Array(Module.HEAP32.buffer, 0, len);	// array of 32-bit signed int to pass
        var bytes_per_element = ouput_array.BYTES_PER_ELEMENT;   // 4 bytes each element
            
        // alloc memory, in this case 5*4 bytes
        var output_ptr = Module._malloc(len * bytes_per_element);

        Module.HEAP32.set(ouput_array, output_ptr / bytes_per_element); // write WASM memory calling the set method of the Int32Array
        GenPwd(output_ptr, len);   	                       // call the WASM function
        ouput_array = new Int32Array(Module.HEAP32.buffer, output_ptr, len); // extract data 
           
        reconstituted = String.fromCharCode.apply(null, ouput_array); // Traslation from ascii array to string
        document.querySelector("#erg").value = reconstituted ;
            
        // dealloc memory
        Module._free(output_ptr);
    }

    function savePassword(){
        var newPassword = document.querySelector("#erg").value;
        document.querySelector("#savedPassword").innerHTML ="saved Password is : " + newPassword;
    }        